import { addReducer } from 'reactn';
import { State } from 'reactn/default';
import { produce } from 'immer';

import { IRowProps, IResponseRoots, FilterTypes } from '../../types';
import {
  cloning,
  handleFetchError,
  handleFetchResponse,
  headerContent,
  isFilterTypeText,
  manipulateStringValuesToBooleans
} from '../../utils';
import { baseUrlVisualization } from '../../utils/endpoints';
import { calcColumns } from '../../components/ui/ExtraColumns';
import { Dispatch } from 'react';

// reducer to fetch the roots (makes use of a normalized state)
addReducer('fetchRoots', async (global: State, dispatch: Dispatch<any>) => {
  dispatch((global: State) => ({
    ...global,
    loading: true
  }));

  return fetch(
    `${baseUrlVisualization}roots?configurationId=${global.configurationId}`,
    {
      method: 'post',
      headers: headerContent,
      body: JSON.stringify({
        commonParameters: global.parameters,
        filterParameters: Object.values(global.filters.rootFilters)
      })
    }
  )
    .then(handleFetchResponse)
    .then(
      ({
        columns,
        values,
        visualizationMetadata,
        filters
      }: IResponseRoots) => ({
        loadingRoots: false,
        loading: false,
        filters: {
          ...global.filters,
          hasEmptyRootsFilters: filters.length === 0,
          rootFilters: filters.reduce(
            (toggleColumnsConfig, column) => ({
              ...toggleColumnsConfig,
              [column.variable]: {
                name: column.name,
                query: column.query,
                value:
                  global.filters.rootFilters[column.variable]?.value ||
                  undefined,
                id: column.variable,
                type: isFilterTypeText(column.type)
                  ? FilterTypes.literal
                  : FilterTypes.uri
              }
            }),
            {}
          )
        },
        columns:
          Object.values(global.filters.rootFilters).length > 0
            ? global.columns
            : calcColumns(columns), // The columns only needs te be calculated the first time we load the app
        enablePagination: visualizationMetadata.enablePagination,
        title: visualizationMetadata.title,
        subtitle: visualizationMetadata.subtitle,
        data: manipulateStringValuesToBooleans(values).reduce(
          (value, item: IRowProps, index) => {
            const uniqId = `${item.uri}_${index + 1}`;
            item = {
              ...item,
              children: [],
              level: 0,
              treeNodeId: `${index + 1}`,
              uniqId
            };
            value[uniqId] = item;
            return value;
          },
          {}
        )
      })
    )
    .catch((err: Error | Response) =>
      handleFetchError(
        err,
        (error: Error) => ({
          error,
          loadingRoots: false,
          loading: false
        }),
        (message: string) => ({
          error: new Error(message),
          loadingRoots: false,
          loading: false
        })
      )
    );
});

// reducer to fetch the children (makes also use of a normalized state)
addReducer(
  'fetchChildren',
  (
    global: State,
    dispatch: Dispatch<any>,
    notImporting: boolean,
    origin: IRowProps,
    level: number
  ) =>
    fetch(
      `${baseUrlVisualization}children?configurationId=${global.configurationId}`,
      {
        method: 'post',
        headers: headerContent,
        body: JSON.stringify({
          values: [cloning(origin)],
          commonParameters: global.parameters
        })
      }
    )
      .then(handleFetchResponse)
      .then(({ values }: { values: IResponseRoots['values'] }) => {
        const updatedvalues = manipulateStringValuesToBooleans(values);

        if (notImporting) {
          return {
            data: {
              ...global.data,
              ...updatedvalues.reduce(
                (value, item: IRowProps, index: number) => {
                  const uniqId = `${item.uri}_${origin.treeNodeId}.${
                    index + 1
                  }`;
                  item = {
                    ...item,
                    children: []
                  };
                  value[uniqId] = item;
                  item.level = level + 1;
                  item.treeNodeId = `${origin.treeNodeId}.${index + 1}`;
                  item.uniqId = uniqId;
                  return value;
                },
                {}
              ),
              ...{
                [origin.uniqId]: {
                  ...global.data[origin.uniqId],
                  children: updatedvalues.map(
                    (item, index) =>
                      `${item.uri}_${origin.treeNodeId}.${index + 1}`
                  )
                }
              }
            }
          };
        }

        // After an import is done, this is gonna get called for updating all the uuids and parentuuids of the children
        const allData = global.data;
        updatedvalues.forEach(
          (item, index) =>
            (allData[`${item.uri}_${origin.treeNodeId}.${index + 1}`].uuid =
              item.uuid)
        );
        updatedvalues.forEach(
          (item, index) =>
            (allData[
              `${item.uri}_${origin.treeNodeId}.${index + 1}`
            ].parentUuid = origin.uuid)
        );
        return produce(global, draftState => {
          draftState.data = allData;
        });
      })
      .catch((err: Error | Response) =>
        handleFetchError(
          err,
          (error: Error) => ({ error }),
          (message: string) => ({ error: new Error(message) })
        )
      )
);
