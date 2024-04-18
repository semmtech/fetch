import React, { useGlobal } from 'reactn';
import { get } from 'lodash';

import Card from './Card.component';
import { ContainerOverview, CustomGridList } from '../../../utils/styles';
import { dataAllFilters } from '../../../utils';
import PageHeader from './PageHeader.component';
import { dataKeysType, GlobalState } from '../../../global';
import { configTypes } from '../../../utils/configTypes';
import { Configuration, Filter, Query } from '../../../types';

export default ({
  data,
  type
}: {
  data: GlobalState['configurations'];
  type: dataKeysType;
}) => {
  const [envs] = useGlobal('environments');
  const [, setDialog] = useGlobal('openFilterDialog');
  const [allGlobalFilters] = useGlobal('globalFilters');

  //There is a custom filter for SPARQL Queries in configurations
  const filterConfigurations = (
    data: Configuration[],
    allFilters: Filter[]
  ) => {

    if(allFilters.length === 1 
      &&  allFilters[0].mainId === "sparqlQuery" 
      && allFilters[0].subId 
      && allFilters[0].value 
      && typeof allFilters[0].value === "string"){

      const queryFilterField = allFilters[0].subId;
      const queryFilterValue = allFilters[0].value.toLowerCase();
      
      return data.filter(
        conf =>
        {
          const allQueries:Query[] = [];
          conf.visualization.rootsQuery.query && allQueries.push(conf.visualization.rootsQuery.query);
          conf.visualization.childrenQuery.query && allQueries.push(conf.visualization.childrenQuery.query);
          conf.visualization.titleQuery && allQueries.push(conf.visualization.titleQuery);
          for(const step of conf.importSteps){
            step.sparqlQuery.query && allQueries.push(step.sparqlQuery.query);
          }

          return allQueries.some(query => 
            String(query[queryFilterField]).toLowerCase().includes(queryFilterValue));
        });
    }

    //use default filter
    return dataAllFilters(data,  allGlobalFilters[type], envs);
  };

  const allData: GlobalState['configurations'] = filterConfigurations(
    data,
    allGlobalFilters[type]
  );
  const filteredData = allData.filter(item => !!item);

  return (
    <ContainerOverview>
      <PageHeader
        pageTitle="Configurations"
        showFilters={true}
        type={type}
        editPage={false}
        filterAction={() => setDialog(true)}
        allItemsLength={filteredData.length}
      />

      <CustomGridList columns={2} length={filteredData.length}>
        {filteredData.map(config => {
          const isRelaticsConfigType =
            config.targetType === configTypes.relatics;

          const content = [
            {
              pre: 'Pagination',
              value: get(config, 'visualization.enablePagination')
                ? 'Yes'
                : 'No'
            },
            {
              pre: 'Description',
              value: config.description || ''
            },
            {
              pre: 'Endpoint',
              value: get(config, 'sparqlEndpoint.name', '') as string
            },
            {
              ...(isRelaticsConfigType
                ? {
                    pre: 'Workspace',
                    value: get(config, 'workspace.name', '') as string
                  }
                : {
                    pre: 'Json API',
                    value: get(config, 'jsonApi.name', '') as string
                  })
            }
          ];

          return (
            <div key={config.id}>
              <Card
                item={config}
                showAvatar={true}
                headerTitle={config.name || ''}
                content={content}
                type={type}
                isRelaticsConfigType={isRelaticsConfigType}
              />
            </div>
          );
        })}
      </CustomGridList>
    </ContainerOverview>
  );
};
