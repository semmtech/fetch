import React from 'react';
import { addReducer } from 'reactn';
import { toast } from 'react-toastify';

import { dataKeysType, GlobalState } from '../global';
import {
  Item,
  Environment,
  WorkspaceOrJsonApi,
  ConflictMessage
} from '../types';
import { APIendpoints } from '../utils/endpoints';
import constants from '../constants';
import Alert from '../components/ui/subComponents/Alert.component';

export const showAlert = ({
  type = toast.TYPE.ERROR,
  ...props
}: {
  title: string;
  feedback: string;
  errMessage?: string;
  type?: 'error' | 'warning';
}) =>
  toast(
    <Alert
      title={props.title}
      feedback={props.feedback}
      type={type}
      {...(props.errMessage && { errMessage: props.errMessage })}
    />,
    {
      type
    }
  );

interface UnknownObject {
  [key: string]: unknown;
}

export const headerContent = {
  'Content-Type': 'application/json',
  credentials: 'include'
};

const resolvedData = (global: GlobalState, type: dataKeysType) =>
  global[type] as (Item)[];

addReducer('logout', (global, dispatch) =>
  fetch(APIendpoints.logout, {
    method: 'POST'
  })
    .then(res => {
      if (res.ok) {
        window.location.href = res.url;
      }
    })
    .catch((err: Error) => {
      showAlert({
        title: constants.errorTitles.logout,
        feedback: constants.errorMessages.logout,
        errMessage: err.message
      });

      return global;
    })
);

addReducer('fetchData', async (global, dispatch, type: dataKeysType) =>
  fetch(`${APIendpoints.api}${type}/`, {
    method: 'GET',
    headers: headerContent
  })
    .then(async res => {
      if (res.redirected) {
        window.location.href = res.url;
      }
      if (res.ok) {
        return res.json();
      }
    })
    .then(res => ({
      ...global,
      [type]: res.sort((a: Item, b: Item) =>
        a.name && b.name && a.name > b.name ? 1 : -1
      )
    }))
    .catch((err: Error) => {
      showAlert({
        title: `Fetching data for the ${type} failed!`,
        feedback: constants.errorMessages.fetchingData,
        errMessage: err.message
      });

      return global;
    })
);

addReducer('fetchDataConfigPage', async (global, dispatch) => {
  const headerHelper = {
    method: 'GET',
    headers: headerContent
  };
  return Promise.all([
    fetch(APIendpoints.jsonapis, headerHelper),
    fetch(APIendpoints.workspaces, headerHelper),
    fetch(APIendpoints.sparqlendpoints, headerHelper),
    fetch(APIendpoints.sparqlqueries, headerHelper)
  ])
    .then(async res =>
      Promise.all(
        res.map(async value => {
          if (value.redirected) {
            window.location.href = value.url;
          }
          return value.json();
        })
      )
    )
    .then(res => ({
      ...global,
      jsonapis: res[0].sort((a: WorkspaceOrJsonApi, b: WorkspaceOrJsonApi) =>
        a.name && b.name && a.name > b.name ? 1 : -1
      ),
      workspaces: res[1].sort((a: WorkspaceOrJsonApi, b: WorkspaceOrJsonApi) =>
        a.name && b.name && a.name > b.name ? 1 : -1
      ),
      sparqlendpoints: res[2].sort((a: Item, b: Item) =>
        a.name && b.name && a.name > b.name ? 1 : -1
      ),
      sparqlqueries: res[3].sort((a: Item, b: Item) =>
        a.name && b.name && a.name > b.name ? 1 : -1
      )
    }))
    .catch((err: Error) => {
      showAlert({
        title: constants.errorTitles.configPage,
        feedback: constants.errorMessages.fetchingData,
        errMessage: err.message
      });

      return global;
    });
});

addReducer('fetchDataRelatics', async (global, dispatch) => {
  const headerHelper = {
    method: 'GET',
    headers: headerContent
  };
  return Promise.all([
    fetch(APIendpoints.workspaces, headerHelper),
    fetch(APIendpoints.environments, headerHelper)
  ])
    .then(async res =>
      Promise.all(
        res.map(async value => {
          if (value.redirected) {
            window.location.href = value.url;
          }
          return value.json();
        })
      )
    )
    .then(res => ({
      ...global,
      workspaces: res[0].sort((a: WorkspaceOrJsonApi, b: WorkspaceOrJsonApi) =>
        a.name && b.name && a.name > b.name ? 1 : -1
      ),
      environments: res[1].sort((a: Environment, b: Environment) =>
        a.name && b.name && a.name > b.name ? 1 : -1
      )
    }))
    .catch((err: Error) => {
      showAlert({
        title: constants.errorTitles.relaticsPage,
        feedback: constants.errorMessages.fetchingData,
        errMessage: err.message
      });

      return global;
    });
});

addReducer(
  'deleteItem',
  async (
    global: GlobalState,
    dispatch,
    item: UnknownObject,
    type: dataKeysType
  ) =>
    fetch(`${APIendpoints.api}${type}/`, {
      method: 'DELETE',
      headers: headerContent,
      body: JSON.stringify([item])
    })
      .then(async res => {
        if (res.redirected) {
          window.location.href = res.url;
        }
        return res.json();
      })
      .then(res =>
        Array.isArray(res)
          ? {
              ...global,
              [type]: resolvedData(global, type).filter(
                (instance: Item) => instance.id !== res[0]
              ),
              removeItem: { isDeleting: false, item: {}, type: '' }
            }
          : {
              ...global,
              errorDelete: { message: res.message, name: res.code }
            }
      )
      .catch((err: Error) => {
        showAlert({
          title: `The deletion of the ${type} has failed!`,
          feedback: constants.errorMessages.crud,
          errMessage: err.message
        });

        return global;
      })
);

addReducer(
  'updateItem',
  async (
    global: GlobalState,
    dispatch,
    item: UnknownObject,
    type: dataKeysType
  ) =>
    fetch(`${APIendpoints.api}${type}/`, {
      method: 'PUT',
      headers: headerContent,
      body: JSON.stringify(item)
    })
      .then(async res => {
        if (res.redirected) {
          window.location.href = res.url;
        }
        if (res.ok) {
          return res.json();
        }
        if (res.status === 409 && type === 'sparqlqueries') {
          const conflictMessage: ConflictMessage = await res.json();

          showAlert({
            title: conflictMessage.code,
            feedback: constants.errorMessages.crud,
            errMessage:
              'The update of the query was successful, but the update of the columns in the configs failed.',
            type: toast.TYPE.WARNING
          });

          return item;
        }
      })
      .then(res => ({
        ...global,
        [type]: resolvedData(global, type).map((instance: Item) =>
          instance.id === res.id ? res : instance
        ),
        editItem: {
          isEditing: false,
          item: {},
          type: ''
        }
      }))
      .catch((err: Error) => {
        showAlert({
          title: `The update for the ${type} has failed!`,
          feedback: constants.errorMessages.crud,
          errMessage: err.message
        });

        return global;
      })
);

addReducer(
  'addItem',
  async (
    global: GlobalState,
    dispatch,
    item: UnknownObject,
    type: dataKeysType
  ) =>
    fetch(`${APIendpoints.api}${type}/`, {
      method: 'POST',
      headers: headerContent,
      body: JSON.stringify(item)
    })
      .then(async res => {
        if (res.redirected) {
          window.location.href = res.url;
        }
        if (res.ok) {
          return res.json();
        }
      })
      .then(res => ({
        ...global,
        [type]: resolvedData(global, type).concat(res),
        createItem: {
          isCreating: false,
          item: {},
          type: '',
          configType: undefined
        }
      }))
      .catch((err: Error) => {
        showAlert({
          title: `The creation of the ${type} has failed!`,
          feedback: constants.errorMessages.crud,
          errMessage: err.message
        });

        return global;
      })
);

addReducer(
  'cloneItem',
  async (
    global: GlobalState,
    dispatch,
    item: UnknownObject,
    type: dataKeysType,
    updateLoading: () => void
  ) =>
    fetch(
      `${APIendpoints.api}${type}/${item.id}/clone?cloneWorkspaceId=${item.cloneWorkspaceId}&cloneWorkspaceName=${item.cloneWorkspaceName}`,
      {
        method: 'POST',
        headers: headerContent
      }
    )
      .then(async res => {
        if (res.redirected) {
          window.location.href = res.url;
        }

        //Request timeout and gateway timeout
        if (res.status === 504 || res.status === 408) {
          return {
            message:
              'Seems like it will take a while to clone the the workspace. Please refresh the page after few minutes.',
            code: constants.cloningInProgress
          };
        }

        return res.json();
      })
      .then(res => {
        updateLoading();
        return res.workspaceId && res.id
          ? {
              ...global,
              [type]: resolvedData(global, type).concat(res),
              cloneItem: {
                isCloning: false,
                item: {},
                type: ''
              }
            }
          : {
              ...global,
              errorClone: { message: res.message, name: res.code }
            };
      })
      .catch((err: Error) => {
        updateLoading();
        showAlert({
          title: `The cloning for the ${type} has failed!`,
          feedback: constants.errorMessages.crud,
          errMessage: err.message
        });

        return global;
      })
);
