import 'reactn';
import {
  IImport,
  IRowProps,
  IColumnProps,
  IFilter,
  FilterParameters,
  FilterActionsTypes,
  MenuActions,
  ExpandActionsTypes
} from './types';
import { SortingRule } from 'react-table';
import { filterDialog } from './reducers';

declare module 'reactn/default' {
  export interface Reducers {
    import: (
      global: State,
      dispatch: Dispatch,
      treeNode?: IRowProps['treeNodeId']
    ) => State;

    handleFilter: (
      global: State,
      dispatch: Dispatch,
      action: FilterActionsTypes
    ) => State;

    handleExpand: (
      global: State,
      dispatch: Dispatch,
      action: ExpandActionsTypes
    ) => State;

    handleSelect: (
      global: State,
      dispatch: Dispatch,
      action: MenuActionsTypes
    ) => State;

    fetchChildren: (
      global: State,
      dispatch: Dispatch,
      notImporting: boolean,
      origin: IRowProps,
      level: number
    ) => State;

    fetchRoots: (global: State, dispatch: Dispatch) => State;
  }

  export interface State {
    columns: IColumnProps[];
    error: Error;
    selectedIds: string[];
    loadingRoots: boolean;
    configurationId: string;
    data: {
      [key: string]: IRowProps;
    };
    search: string;
    importSteps: IImport[];
    loading: boolean;
    newSorted: SortingRule[];
    parameters: { [key: string]: string }[];
    enablePagination: boolean;
    title: string;
    subtitle: string;
    filters: {
      areFiltersActive: boolean;
      rootFilters: {
        [key: string]: FilterParameters;
      };
      loadingPicklist: boolean;
      picklist: { value: string; display: string }[];
      isFilterDialogOpen: boolean;
      hasEmptyRootsFilters: boolean;
    };
    foldedColumn: { id?: string; folded: boolean };
    defaultColumnWidths: { id?: string; width: number }[];
    expandedRows: { [key: string]: { [key: number]: boolean } };
  }
}
