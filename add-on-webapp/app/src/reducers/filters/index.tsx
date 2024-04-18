import { addReducer } from 'reactn';
import { State } from 'reactn/default';
import { Dispatch } from 'react';
import { produce } from 'immer';

import { FilterActions, FilterActionsTypes } from '../../types';
import {
  handleFetchError,
  handleFetchResponse,
  headerContent,
  notify
} from '../../utils';
import { baseUrlFilterValues } from '../../utils/endpoints';
import constants from '../../constants';

addReducer(
  'handleFilter',
  async (
    global: State,
    dispatch: Dispatch<any>,
    action: FilterActionsTypes
  ) => {
    // This object is gonna look like this (if there are 3 roots for example)
    // {0: false, 1: false, 2: false, 3: false}
    // It is for resetting the expanded state of the roots so it is back to its default state with only the roots available
    // When a filter is set active or not active anymore the roots data is gonna change, so the expanded needs to be too
    const resetExpandedStateForRoots = Object.values(global.data)
      .filter(item => item.level === 0)
      .reduce(
        (obj, item, index) => ({
          ...obj,
          [index]: false
        }),
        {}
      );

    switch (action.type) {
      case FilterActions.clearActive:
        return produce(global, draftState => {
          draftState.filters.isFilterDialogOpen = false;
          draftState.filters.areFiltersActive = false;
          draftState.expandedRows = {
            [constants.highestTreeLevel]: resetExpandedStateForRoots
          };
          draftState.filters.rootFilters = Object.values(
            draftState.filters.rootFilters
          ).reduce((obj, item) => {
            item = {
              ...item,
              value: undefined
            };
            // @ts-ignore
            obj[item.id] = item;
            return obj;
          }, {});
        });

      case FilterActions.setActive:
        const entries = Object.entries(action.payload);

        return produce(global, draftState => {
          draftState.filters.areFiltersActive = true;
          draftState.expandedRows = {
            [constants.highestTreeLevel]: resetExpandedStateForRoots
          };
          draftState.filters.isFilterDialogOpen = false;
          entries.forEach(item => {
            draftState.filters.rootFilters[item[0]].value = item[1];
          });
        });
      case FilterActions.open:
        return produce(global, draftState => {
          draftState.expandedRows = {
            [constants.highestTreeLevel]: resetExpandedStateForRoots
          };
          draftState.filters.isFilterDialogOpen = true;
          draftState.filters.areFiltersActive = false;
        });
      case FilterActions.close:
        const areFilterValuesFilledIn = !Object.values(
          global.filters.rootFilters
        ).every(x => !x.value);

        return produce(global, draftState => {
          draftState.filters.isFilterDialogOpen = false;
          if (areFilterValuesFilledIn) {
            draftState.filters.areFiltersActive = true;
          }
        });
      case FilterActions.picklistValues:
        dispatch((global: State) =>
          produce(global, draftState => {
            draftState.filters.loadingPicklist = true;
          })
        );

        const result = await fetch(
          `${baseUrlFilterValues}?configurationId=${global.configurationId}&queryId=${action.payload.queryId}`,
          {
            method: 'get',
            headers: headerContent
          }
        )
          .then(handleFetchResponse)
          .then((response: { [key: string]: string }[]) =>
            produce(global, draftState => {
              // sort filter values by value
              const picklist = response
                .sort((a, b) => {
                  const itemA = a.value;
                  const itemB = b.value;
                  return itemA < itemB ? -1 : itemA > itemB ? 1 : 0;
                })
                .map(item => ({
                  value: item.key,
                  display: item.value
                }));
              draftState.filters.picklist = picklist;
              draftState.filters.loadingPicklist = false;
            })
          )
          .catch((err: Error | Response) => {
            handleFetchError(
              err,
              (error: Error) => notify(`${error.message}, please try again.`),
              (message: string) => notify(`${message}, please try again.`)
            );

            return produce(global, draftState => {
              draftState.filters.loadingPicklist = false;
            });
          });
        return result;
      default:
        return global;
    }
  }
);
