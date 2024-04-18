import { ChangeEvent, CSSProperties, Dispatch, SetStateAction } from 'react';

import { ValueDate } from '../utils';
import { dataKeysType } from '../global';

export interface ConflictMessage {
  code: string;
  message: string;
}

interface Main {
  id: string;
  name: string;
}

export interface Authorization {
  userName?: string;
  password?: string;
  privateKey?: string;
  applicationId?: string;
}

type NumberOrNull = number | null | undefined;

export type DefaultGraphs = string[];

export type Event = (
  e: ChangeEvent<{
    name?: string;
    value: string;
  }>,
  index: number
) => void;

export type Item =
  | Endpoint
  | Query
  | Configuration
  | WorkspaceOrJsonApi
  | TargetSystemOrEndpoint
  | Environment;

export type IUnknownObject = any;

export interface Filter {
  mainId: string;
  subId?: string;
  value: string | ValueDate;
}

export interface AuthenticationFields {
  values: Authorization;
  authenticationType: string;
  show: boolean;
  onChange(
    e: ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
    id: string
  ): void;
  onShow(): void;
}

export enum ConfigTypes {
  Relatics = 'Relatics',
  JsonApi = 'JSON_API'
}

export interface Columns {
  bindingName: string;
  displayName: string;
  visible: boolean;
}

export interface Configuration {
  [key: string]: unknown;
  isActive?: boolean;
  isSimpleFeedback?: boolean;
  workspace?: WorkspaceOrJsonApi | null;
  jsonApi?: WorkspaceOrJsonApi | null;
  id?: string;
  startDate: NumberOrNull;
  endDate: NumberOrNull;
  name?: string;
  targetType: ConfigTypes;
  description?: string;
  displayName?: string;
  visualization: {
    enablePagination: boolean;
    rootsQuery: {
      defaultGraphs: DefaultGraphs;
      query?: Query | null;
    };
    childrenQuery: {
      defaultGraphs: DefaultGraphs;
      query?: Query | null;
    };
    columns: {
      [key: string]: Columns;
    };
    additionalInputs?: TargetSystemOrEndpoint;
    titleQuery?: Query;
  };
  sparqlEndpoint?: Endpoint | null;
  importSteps: Step[];
}

export interface AlertDialog {
  description: string;
  item?: string;
  open: boolean;
  confirmationAction(): void;
  dialogOnClose(): void;
  cancelAction(): void;
}

export interface Endpoint extends Main {
  [key: string]: unknown;
  url: string;
  authenticationMethod: {
    type: string;
    userName: Authorization['userName'];
    password: Authorization['password'];
    privateKey: Authorization['privateKey'];
    applicationId: Authorization['applicationId'];
  };
}

export enum QueryTypes {
  filter = 'filter',
  roots = 'roots',
  children = 'children',
  import = 'import',
  title = 'title'
}

export interface QueryPicklists {
  [key: string]: unknown;
  name: Query['name'];
  type: string;
  variable: string;
  query: string | null;
}

export interface Step {
  [key: string]: unknown;
  name: string;
  sparqlQuery: { query: Query | null; defaultGraphs: string[] };
  importTarget?: TargetSystemOrEndpoint | null;
}

export type ReorderedTypes = any[];

export interface Query extends Main {
  [key: string]: unknown;
  description?: string;
  query?: string;
  filterFields: QueryPicklists[];
  type: QueryTypes;
}

export interface WorkspaceOrJsonApi extends Main {
  [key: string]: unknown;
  // workspace
  environmentId?: string;
  workspaceId?: string;
  targetDataSystems?: TargetSystemOrEndpoint[];
  // json api
  serviceUrl?: string;
  endpoints?: TargetSystemOrEndpoint[];
}

export interface TargetSystemOrEndpoint {
  [key: string]: string | undefined | null;
  // both
  id?: string;
  type: string | null;
  // target system
  operationName?: string | null;
  entryCode?: string | null;
  xpathExpression?: string | null;
  workspaceId?: string;
  // endpoint
  name?: string | null;
  path?: string | null;
  apiId?: string;
}

export interface Card {
  item: Item;
  showAvatar: boolean;
  headerTitle?: string;
  content: { pre: string; value: string }[];
  type: dataKeysType;
  isRelaticsConfigType?: boolean;
}

export interface PageHeader {
  pageTitle: string;
  type: dataKeysType;
  showFilters?: boolean;
  editPage?: boolean;
  createPage?: boolean;
  allItemsLength?: number;
  confirmationAction?: () => void;
  cancelAction?: () => void;
  filterAction?: () => void;
  createAction?: () => void;
  configId?: string;
}

export interface Records {
  headers: string[];
  results: { [key: string]: { type: string; value: string } }[];
}

export interface YasguiEditorError {
  status?: number;
  statusText: string;
  responseText: string;
}

export interface YasguiError extends YasguiEditorError {
  error: boolean;
}

export interface YasguiEditorSuccess {
  head: {
    vars: string[];
  };
  results: {
    bindings: { [key: string]: { type: string; value: string } }[];
  };
}

export interface Environment extends Main {
  [key: string]: string;
  serviceUrl: string;
  namespace: string;
  environmentId: string;
}

interface DragAndDrop {
  queries: Query[];
  deleteRowData: (item: Step | QueryPicklists) => void;
  addNewEmptyRow(): void;
  updateRowData(
    event:
      | ChangeEvent<HTMLTextAreaElement | HTMLInputElement>
      | ChangeEvent<{}>,
    index: number,
    item: string,
    value?: any,
    subItem?: string
  ): void;
}

export interface ImportStep extends DragAndDrop {
  steps: Step[];
  setImportSteps: Dispatch<SetStateAction<Step[]>>;
  targetSystems: TargetSystemOrEndpoint[];
  isRelatics: boolean;
}

export interface QueryFilterFields extends DragAndDrop {
  filters: QueryPicklists[];
  setFilters: Dispatch<SetStateAction<QueryPicklists[]>>;
}

export interface GridList {
  length: number;
  columns: number;
}

export interface Overflown {
  clientWidth: number;
  clientHeight: number;
  scrollWidth: number;
  scrollHeight: number;
}

export interface Menu {
  editItem?: boolean;
  cloneItem?: boolean;
  deleteItem?: boolean;
  relaticsItem?: boolean;
  jsonAPIItem?: boolean;
  id: Main['id'];
  anchor?: HTMLElement;
  item?: Item;
  type: dataKeysType;
  closeMenu(): void;
}

export interface AutoCompleteOption {
  value: string;
  display: string;
  envType?: string;
}

export interface AutoProps {
  label: string;
  required?: boolean;
  error?: boolean;
  classes: Record<'option' | 'listbox' | 'paper', string>;
  style?: CSSProperties;
  disableDelete?: boolean;
  subItem?: boolean;
  typeSubItem?: boolean;
  configType?: boolean;
}

export interface ColumnsList {
  setColumns: Dispatch<
    SetStateAction<{
      [key: string]: Columns;
    }>
  >;
  columns: {
    [key: string]: Columns;
  };
  updateDisplayName: (
    e: ChangeEvent<HTMLTextAreaElement | HTMLInputElement>,
    index: string
  ) => void;
  updateVisibleStatus: (value: boolean, index: string) => void;
}
