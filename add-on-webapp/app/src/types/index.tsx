import React from 'react';
import {
  PageChangeFunction,
  ReactTableFunction,
  Column,
  RowInfo
} from 'react-table';

// TODO: remove the null value -> use undefined instead (only use null for returning stuff of the API)

export interface IImport {
  success: boolean;
  warnings: string[];
  successMessage: string;
  importStep: string | undefined;
  errors: string[];
}

// type for properties of original (comes from row information)
export interface IOriginalProps {
  original: IRowProps;
}

// interface for row properties
export interface IRowProps {
  hasChildren: boolean;
  name?: string;
  type?: string;
  uri: string;
  usedConfig?: number;
  isImported: boolean;
  children: string[];
  isExpanded?: boolean;
  level: number;
  treeNodeId: string;
  uuid: string;
  uniqId: string;
  [key: string]: any;
}

// interface for column properties
export interface IColumnProps {
  columns?: Column[];
  expander?: boolean;
  Header?: string | ((original: any) => JSX.Element);
  accessor?: string;
  show?: boolean;
  headerStyle?: object;
  width?: number;
  sortable?: boolean;
  filterable?: boolean;
  resizable?: boolean;
  display?: string;
  name?: string;
  id?: string;
  style?: object;
  getProps?: ReactTableFunction;
  minWidth?: number;
  minResizeWidth?: number;
  maxWidth?: number;
  Expander?(original: IOriginalProps): JSX.Element | string;
  Cell?({ original }: IOriginalProps): JSX.Element | string;
}

export type mouseEvent = React.MouseEvent<HTMLButtonElement>;

export interface ICACProps {
  readonly uniqId: string;
  count: number;
  readonly allData: {
    [key: string]: any;
  };
  readonly selectedIds: string[];
  readonly parentId: string;
}

export interface IPagination {
  showPageSizeOptions: boolean;
  pageSizeOptions: number[];
  data: { [key: string]: any }[];
  canPrevious: boolean;
  canNextFromData: any;
  className: string;
  onPageChange: PageChangeFunction;
  style: { [key: string]: any };
  PreviousComponent: React.ReactType;
  NextComponent: React.ReactType;
  onPageSizeChange(newPageSize: number): void;
  [key: string]: any;
}

export interface ITableColumn {
  name: string | null;
  display: string | null;
  show: boolean;
}

export interface IResponseRoots {
  columns: ITableColumn[];
  values: any[];
  visualizationMetadata: {
    enablePagination: boolean;
    title: string;
    subtitle?: string;
  };
  filters: {
    name: string;
    type: FilterTypes.picklist | FilterTypes.text;
    variable: string;
    query: string | null;
  }[];
}

export interface FilterParameters {
  name: string;
  id: string;
  type: FilterTypes.literal | FilterTypes.uri;
  value?: string;
  query: string;
}

export enum FilterTypes {
  text = 'Text',
  picklist = 'SPARQL Picklist',
  literal = 'literal',
  uri = 'uri'
}

// Actions

export enum FilterActions {
  open = 'OPEN_FILTER_DIALOG',
  close = 'CLOSE_FILTER_DIALOG',
  picklistValues = 'FETCH_PICKLIST_VALUES',
  setActive = 'SET_ACTIVE_FILTERS',
  clearActive = 'CLEAR_ACTIVE_FILTERS'
}

export type FilterActionsTypes =
  | {
      type:
        | FilterActions.clearActive
        | FilterActions.open
        | FilterActions.close;
    }
  | { type: FilterActions.picklistValues; payload: { queryId: string } }
  | {
      type: FilterActions.setActive;
      payload: {
        [key: string]: FilterParameters['value'];
      };
    };

export enum SelectActions {
  selectAll = 'SELECT_ALL',
  unselectAll = 'UNSELECT_ALL'
}

export type SelectActionsTypes = {
  type: SelectActions;
  payload: { item: IRowProps };
};

export type ExpandActionsTypes =
  | {
      type: ExpandActions.resetExpand;
    }
  | {
      type: ExpandActions.handleExpand;
      payload: { newExpanded: any; treeNode: string; index: number };
    }
  | {
      type: ExpandActions.expandAll;
      payload: { item: IRowProps };
    };

export enum ExpandActions {
  handleExpand = 'HANDLE_EXPAND',
  resetExpand = 'RESET_EXPAND',
  expandAll = 'HANDLE_ALL_CHILDREN'
}

export type CellProps = {
  original: IRowProps;
} & Omit<RowInfo, 'original'>;
