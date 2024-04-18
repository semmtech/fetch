import { addReducer } from 'reactn';
import { State } from 'reactn/default';
import { Dispatch } from 'react';
import { produce } from 'immer';

import {
  ExpandActionsTypes,
  ExpandActions,
  IResponseRoots,
  IRowProps
} from '../../types';
import { getChildren, updateTreeDataWithAllChildren } from '../../utils';
import constants from '../../constants';

// this function is for determining the last number in a provided value
const getLastNumber = (value: string) =>
  Number(value.substring(value.lastIndexOf('.') + 1)) - 1;

// this function is to decide on which tree level the provided treeNode is located
// level 0 (roots) = Root
const getTreeLevel = (treeNode: IRowProps['treeNodeId']) =>
  treeNode.includes('.')
    ? `${treeNode.substring(0, treeNode.lastIndexOf('.'))}`
    : constants.highestTreeLevel;

// this function is gonna update the expanded state
// It is also a recursive one for expanding the children of children and so on ...
// This is necessary for the expand all functionality
// the expanded state looks like this for example
// {
// Root: {0: true, 1: false, 2: true},      -> expanded state of the roots (tree level 0)
// 1: { 0: false, 1: true },                -> expanded state of the children of the 1st root (tree level 1)
// 1.2: { 0: true },                        -> expanded state of the children of level 1 the 2th item (tree level 2)
// 1.2.1: { 0: true, 1: false, 2: false },  -> expanded state of the children of level 2 the 1st item (tree level 3)
// 1.2.1.1: { 0: false, 1: true },          -> expanded state of the children of level 3 the 1st item (tree level 4)
// 3: { 0: false }                          -> expanded state of the children of the 3th root (tree level 1)
// }

// In react table, the expanded state look like this but it is only for one level deep
// expanded={{ // The nested row indexes on the current page that should appear expanded
//     1: true,
//     4: true,
//     5: {
//       2: true,
//       3: true
//     }
//   }}
// this is the callback that is called when a change occurs for expanding
// onExpandedChange={(newExpanded, index, event) => {...}} // Called when an expander is clicked
// newExpanded is for example:
// { 0: { } } -> for indicating that the first row is expanded
// { 0: false } -> for indicating that the first row is collapsed
// index is an array with only one item inside of it, it includes the index of the clicked row

/**
 * Copies the content of the import messages.
 * Makes it easier to track in case of failure. And the user can mail the admin with the necessary info.
 */

const calcExpanded = (
  children: IResponseRoots['values'],
  expandedState: State['expandedRows'],
  parentId?: string
) => {
  children.forEach((item, index) => {
    if (item.children.length > 0) {
      calcExpanded(item.children, expandedState);
    }

    // this is gonna update the expanded rows for each child
    // Just to be sure there is a double check done that only rows can expanded who have children available
    expandedState[getTreeLevel(item.treeNodeId)] = {
      ...expandedState[getTreeLevel(item.treeNodeId)],
      [index]: item.children.length > 0 && item.hasChildren
    };

    // this is gonna update the expanded rows for the parent
    // First it is gonna determine on which level we are, because we can expand all on a nested child as well and than turn the expanded state to true for that row
    // It is only gonna execute 1 time
    if (parentId) {
      expandedState[getTreeLevel(parentId)] = {
        [parentId.length > 1
          ? getLastNumber(parentId)
          : Number(parentId) - 1]: true
      };
    }
  });
  return expandedState;
};

addReducer(
  'handleExpand',
  async (
    global: State,
    dispatch: Dispatch<any>,
    action: ExpandActionsTypes
  ) => {
    switch (action.type) {
      case ExpandActions.expandAll:
        const {
          payload: { item }
        } = action;

        dispatch((global: State) => ({
          ...global,
          loading: true
        }));

        // the children is an array here that includes all the nested children for the provided row
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
        // the updatedExpandedRows is a representative of the store expanded rows
        const updatedExpandedRows = calcExpanded(
          children,
          { ...global.expandedRows },
          item.treeNodeId
        );

        // here we gonna update the store with the newly update data and the expanded rows
        return produce(global, draftState => {
          draftState.loading = false;
          draftState.data = updatedData;
          draftState.expandedRows = updatedExpandedRows;
        });
      case ExpandActions.handleExpand:
        const {
          payload: { treeNode, index, newExpanded }
        } = action;

        return produce(global, draftState => {
          const expandedTreeLevel = global.expandedRows[treeNode];

          // This is gonna collapse the siblings of the clicked row -> for performance issues
          // Is for when all children are expanded and you wanna collapse a row -> is gonna make sure the other ones dont collapse too
          if (
            Object.keys(expandedTreeLevel || {}).includes(`${index}`) &&
            expandedTreeLevel[index]
          ) {
            draftState.expandedRows[treeNode] = newExpanded;
            return;
          }

          draftState.expandedRows[treeNode] = {
            [index]: Boolean(newExpanded[index]) // for the true state, react table returns an empty object so I wrapped it with Boolean
          };
        });
      case ExpandActions.resetExpand:
        return produce(global, draftState => {
          draftState.expandedRows = {};
        });
      default:
        return global;
    }
  }
);
