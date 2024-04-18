import { addReducer } from 'reactn';
import { Dispatch } from 'react';
import { State } from 'reactn/default';
import { produce } from 'immer';
import _union from 'lodash/union';

import { SelectActions, SelectActionsTypes } from '../../types';
import {
  getChildren,
  updateTreeDataWithAllChildren,
  valueHasTreeNode
} from '../../utils';

addReducer(
  'handleSelect',
  async (
    global: State,
    dispatch: Dispatch<any>,
    { type, payload: { item } }: SelectActionsTypes
  ) => {
    switch (type) {
      case SelectActions.selectAll: {
        dispatch((global: State) => ({
          ...global,
          loading: true
        }));

        // the children is an array that includes all the nested children for the provided row
        // is't a deeply nested array
        const children = await getChildren(item, global);
        if (children.length === 0) {
          dispatch((global: State) => ({
            ...global,
            loading: false
          }));
          return;
        }
        // the updatedData is a representative of the store data, this includes all the fetched children for correctly selecting the right items
        const updatedData = updateTreeDataWithAllChildren(
          children,
          { ...global.data },
          item.level,
          item.uniqId
        );

        // here we gonna update the store with the newly update data and the newly selected items
        // for the selected items, we gonna make sure the previous selected siblings stay selected
        return produce(global, draftState => {
          draftState.loading = false;
          draftState.data = updatedData;
          draftState.selectedIds = _union(
            global.selectedIds,
            Object.values(updatedData)
              .filter(({ treeNodeId }) =>
                treeNodeId.startsWith(item.treeNodeId)
              )
              .map(({ uniqId }) => uniqId)
          );
        });
      }
      case SelectActions.unselectAll:
        // here we gonna update the store so that the selected items gonna be unselected for the provided parent row
        return produce(global, draftState => {
          draftState.selectedIds = global.selectedIds.filter(
            id => !valueHasTreeNode(id, item.treeNodeId)
          );
        });
      default:
        return global;
    }
  }
);
