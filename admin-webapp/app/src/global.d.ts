import 'reactn';
import {
  Configuration,
  Endpoint,
  Filter,
  Query,
  TargetSystemOrEndpoint,
  IUnknownObject,
  WorkspaceOrJsonApi,
  Environment
} from './types';
import { initialState } from '.';

export type GlobalState = typeof initialState;

declare module 'reactn/default' {
  export interface Reducers {
    addItem: (
      global: State,
      dispatch: Dispatch,
      item: { [key: string]: unknown },
      type: dataKeysType
    ) => GlobalState;

    deleteItem: (
      global: State,
      dispatch: Dispatch,
      item: { [key: string]: string },
      type: dataKeysType
    ) => GlobalState;

    updateItem: (
      global: State,
      dispatch: Dispatch,
      item: { [key: string]: unknown },
      type: dataKeysType
    ) => GlobalState;

    cloneItem: (
      global: State,
      dispatch: Dispatch,
      item: { [key: string]: unknown },
      type: dataKeysType,
      updateLoading: () => void
    ) => GlobalState;

    fetchData: (
      global: State,
      dispatch: Dispatch,
      type: dataKeysType
    ) => GlobalState;

    fetchDataConfigPage: (global: State, dispatch: Dispatch) => GlobalState;
    fetchDataRelatics: (global: State, dispatch: Dispatch) => GlobalState;

    logout: (
      global: State,
      dispatch: Dispatch,
      userName: string,
      password: string
    ) => GlobalState;
  }

  export interface State {
    currentQueryEditor: string;
    showMenu: boolean;
    openFilterDialog: boolean;
    configurations: Configuration[];
    globalFilters: {
      [key: string]: Filter[];
      configurations: Filter[];
      sparqlendpoints: Filter[];
      sparqlqueries: Filter[];
      workspaces: Filter[];
      targetsystems: Filter[];
      environments: Filter[];
      jsonapis: Filter[];
    };
    errorDelete: Error;
    errorClone: Error;
    sparqlendpoints: Endpoint[];
    sparqlqueries: Query[];
    editItem: {
      isEditing: boolean;
      item: IUnknownObject;
      type: string;
    };
    cloneItem: {
      isCloning: boolean;
      item: IUnknownObject;
      type: string;
    };
    removeItem: {
      isDeleting: boolean;
      item: IUnknownObject;
      type: string;
    };
    createItem: {
      isCreating: boolean;
      item: IUnknownObject;
      type: string;
      configType?: string;
    };
    workspaces: WorkspaceOrJsonApi[];
    jsonapis: WorkspaceOrJsonApi[];
    targetsystems: TargetSystemOrEndpoint[];
    environments: Environment[];
  }
}

export type dataKeysType = keyof GlobalState['globalFilters'];
