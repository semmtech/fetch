import React, { useGlobal } from 'reactn';
import _get from 'lodash/get';

import { dataAllFilters } from '../../../utils';
import { ContainerOverview, CustomGridList } from '../../../utils/styles';
import PageHeader from './PageHeader.component';
import Card from './Card.component';
import { dataKeysType, GlobalState } from '../../../global';

export default ({
  data,
  type
}: {
  data: GlobalState['sparqlendpoints'];
  type: dataKeysType;
}) => {
  const [, setCreateItem] = useGlobal('createItem');
  const [, setDialog] = useGlobal('openFilterDialog');

  const [allGlobalFilters] = useGlobal('globalFilters');

  const allData: GlobalState['sparqlendpoints'] = dataAllFilters(
    data,
    allGlobalFilters[type]
  );

  const filteredData = allData.filter(item => !!item);

  return (
    <ContainerOverview>
      <PageHeader
        pageTitle="SPARQL Endpoints"
        showFilters={true}
        type={type}
        editPage={false}
        filterAction={() => setDialog(true)}
        createAction={() => setCreateItem({ isCreating: true, item: {}, type })}
        allItemsLength={filteredData.length}
      />

      <CustomGridList columns={3} length={filteredData.length}>
        {filteredData.map(endpoint => (
          <div key={endpoint.id}>
            <Card
              item={endpoint}
              showAvatar={false}
              headerTitle={endpoint.name || ''}
              content={[
                {
                  pre: 'Auth type',
                  value: _get(
                    endpoint,
                    'authenticationMethod.type',
                    ''
                  ) as string
                }
              ]}
              type={type}
            />
          </div>
        ))}
      </CustomGridList>
    </ContainerOverview>
  );
};
