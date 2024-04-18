import React, { useGlobal } from 'reactn';

import { dataAllFilters } from '../../../utils';
import { ContainerOverview, CustomGridList } from '../../../utils/styles';
import PageHeader from './PageHeader.component';
import Card from './Card.component';
import { dataKeysType, GlobalState } from '../../../global';
import { itemTypes } from '../../../utils/itemTypes';

export default ({
  data,
  type
}: {
  data: GlobalState['jsonapis'];
  type: dataKeysType;
}) => {
  const [, setCreateItem] = useGlobal('createItem');
  const [, setDialog] = useGlobal('openFilterDialog');
  const [allGlobalFilters] = useGlobal('globalFilters');

  const allData: GlobalState['jsonapis'] = dataAllFilters(
    data,
    allGlobalFilters[itemTypes.jsonapis]
  );

  const filteredData = allData.filter(item => !!item);

  return (
    <ContainerOverview>
      <PageHeader
        pageTitle="JSON API's"
        showFilters={true}
        type={type}
        editPage={false}
        filterAction={() => setDialog(true)}
        createAction={() => {
          setCreateItem({ isCreating: true, item: {}, type });
        }}
        allItemsLength={filteredData.length}
      />

      <CustomGridList
        columns={3}
        data-testid="JsonApis"
        length={filteredData.length}
      >
        {filteredData.map(jsonapi => (
          <div key={jsonapi.id}>
            <Card
              item={jsonapi}
              showAvatar={false}
              headerTitle={jsonapi.name || ''}
              content={[
                { pre: 'Service URL', value: jsonapi.serviceUrl || '' }
              ]}
              type={type}
            />
          </div>
        ))}
      </CustomGridList>
    </ContainerOverview>
  );
};
