import React, { useGlobal } from 'reactn';

import { dataAllFilters } from '../../../utils';
import { ContainerOverview, CustomGridList } from '../../../utils/styles';
import PageHeader from './PageHeader.component';
import Card from './Card.component';
import { dataKeysType, GlobalState } from '../../../global';

export default ({
  data,
  type
}: {
  data: GlobalState['sparqlqueries'];
  type: dataKeysType;
}) => {
  const [, setCreateItem] = useGlobal('createItem');
  const [, setDialog] = useGlobal('openFilterDialog');

  const [allGlobalFilters] = useGlobal('globalFilters');

  const allData: GlobalState['sparqlqueries'] = dataAllFilters(
    data,
    allGlobalFilters[type]
  );

  const filteredData = allData.filter(item => !!item);

  return (
    <ContainerOverview>
      <PageHeader
        pageTitle="SPARQL Queries"
        showFilters={true}
        type={type}
        editPage={false}
        filterAction={() => setDialog(true)}
        createAction={() => {
          setCreateItem({ isCreating: true, item: {}, type });
        }}
        allItemsLength={filteredData.length}
      />

      <CustomGridList columns={2} length={filteredData.length}>
        {filteredData.map(query => (
          <div key={query.id}>
            <Card
              item={query}
              showAvatar={false}
              headerTitle={query.name || ''}
              content={[{ pre: 'Description', value: query.description || '' }]}
              type={type}
            />
          </div>
        ))}
      </CustomGridList>
    </ContainerOverview>
  );
};
