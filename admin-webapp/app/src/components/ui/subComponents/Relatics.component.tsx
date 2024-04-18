import React, { useGlobal } from 'reactn';
import styled from 'styled-components';

import FilterDialog from '../subComponents/FilterDialog.component';
import { dataAllFilters } from '../../../utils';
import { ContainerOverview, CustomGridList } from '../../../utils/styles';
import PageHeader from './PageHeader.component';
import Card from './Card.component';
import { dataKeysType, GlobalState } from '../../../global';
import { itemTypes } from '../../../utils/itemTypes';

export default ({
  data,
  envs,
  type,
  envType,
  changeActiveType,
  activeType
}: {
  data: GlobalState['workspaces'];
  envs: GlobalState['environments'];
  type: dataKeysType;
  envType: dataKeysType;
  changeActiveType: (type: dataKeysType) => void;
  activeType: dataKeysType;
}) => {
  const [, setCreateItem] = useGlobal('createItem');
  const [, setDialog] = useGlobal('openFilterDialog');

  const [allGlobalFilters] = useGlobal('globalFilters');

  const allWorkspaces: GlobalState['workspaces'] = dataAllFilters(
    data,
    allGlobalFilters[type],
    envs
  );
  const allEnvs: GlobalState['environments'] = dataAllFilters(
    envs,
    allGlobalFilters[envType]
  );

  const filteredWorkspaces = allWorkspaces.filter(item => !!item);
  const filteredEnvs = allEnvs.filter(item => !!item);

  const pageHeaderProps = (env: boolean) => ({
    pageTitle: env ? 'Workspaces' : 'Environments',
    showFilters: true,
    editPage: false,
    filterAction: () => {
      setDialog(true);
      env
        ? changeActiveType(itemTypes.workspaces)
        : changeActiveType(itemTypes.environments);
    }
  });

  return (
    <ContainerOverview style={{ height: '100%' }}>
      <FilterDialog
        instance={activeType === itemTypes.workspaces ? data[0] : envs[0]}
        type={
          activeType === itemTypes.workspaces
            ? itemTypes.workspaces
            : itemTypes.environments
        }
      />

      <RowWrapper envs={false}>
        <PageHeader
          type={itemTypes.environments}
          createAction={() =>
            setCreateItem({
              isCreating: true,
              item: {},
              type: itemTypes.environments
            })
          }
          allItemsLength={filteredEnvs.length}
          {...pageHeaderProps(false)}
        />
        <CustomGridList
          length={filteredEnvs.length}
          data-testid="Environments"
          columns={3}
        >
          {filteredEnvs.map(env => (
            <div key={env.id}>
              <Card
                item={env}
                showAvatar={false}
                headerTitle={env.name || ''}
                content={[
                  { pre: 'Namespace', value: env.namespace || '' },
                  { pre: 'Service URL', value: env.serviceUrl || '' }
                ]}
                type={envType}
              />
            </div>
          ))}
        </CustomGridList>
      </RowWrapper>

      <RowWrapper envs={true}>
        <PageHeader
          type={itemTypes.workspaces}
          createAction={() =>
            setCreateItem({
              isCreating: true,
              item: {},
              type: itemTypes.workspaces
            })
          }
          allItemsLength={filteredWorkspaces.length}
          {...pageHeaderProps(true)}
        />
        <CustomGridList
          columns={3}
          data-testid="Workspaces"
          length={filteredWorkspaces.length}
        >
          {filteredWorkspaces.map(workspace => (
            <div key={workspace.id}>
              <Card
                item={workspace}
                showAvatar={false}
                headerTitle={workspace.name || ''}
                content={[{ pre: 'WID', value: workspace.workspaceId || '' }]}
                type={type}
              />
            </div>
          ))}
        </CustomGridList>
      </RowWrapper>
    </ContainerOverview>
  );
};

const RowWrapper = styled.div<{ envs: boolean }>`
  height: ${p => (p.envs ? '60%' : '40%')};
  display: flex;
  flex-direction: column;
`;
