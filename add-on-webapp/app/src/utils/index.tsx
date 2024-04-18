import React from 'react';
import { toast } from 'react-toastify';
import { css } from 'glamor';

import Alert from '../components/ui/subComponents/Alert.component';
import AlertActions from '../components/ui/subComponents/AlertActions.component';

import {
  IImport,
  IRowProps,
  ICACProps,
  FilterTypes,
  IResponseRoots
} from '../types';
import { colors } from './colors';
import { State } from 'reactn/default';
import { baseUrlVisualization } from './endpoints';
import prop from 'lodash/fp/prop';

// This function is for showing alerts in the app with the right information
export const notify = (
  message: string,
  importStep?: string,
  options?: object | undefined,
  warnings?: string[],
  errors?: string[]
) =>
  toast(
    <Alert
      title={message}
      warnings={warnings}
      errors={errors}
      importStep={importStep}
    />,
    options || {
      type: toast.TYPE.ERROR
    }
  );

// This function is for showing the response of the import alerts, based on its props, it shows if it was successfull or not
export const importAlerts = (importSteps: IImport[]) => {
  importSteps.forEach(
    ({ success, warnings, successMessage, importStep, errors }) => {
      // successfull step
      if (success && warnings.length === 0) {
        notify(successMessage, importStep, {
          type: toast.TYPE.SUCCESS,
          autoClose: 4000
        });
      }

      // successfull step with warnings
      if (success && warnings.length > 0) {
        notify(
          successMessage,
          importStep,
          {
            type: toast.TYPE.WARNING,
            autoClose: false,
            closeButton: <AlertActions warnings={warnings} />,
            className: css({ background: `${colors.orange} !important` })
          },
          warnings
        );
      }

      // unsuccessfull step with errors
      if (!success && (errors.length > 0 || warnings.length > 0)) {
        // In case of simple feedback there will only be one error message
        if (
          errors.length === 1 &&
          warnings.length === 0 &&
          errors[0].trim().toLowerCase() ===
            'error in import. contact your administrator'
        ) {
          notify(errors[0], importStep, {
            type: toast.TYPE.ERROR,
            autoClose: false,
            closeButton: <AlertActions warnings={warnings} errors={errors} />
          });
        } else {
          notify(
            'This step has failed',
            importStep,
            {
              type: toast.TYPE.ERROR,
              autoClose: false,
              closeButton: <AlertActions warnings={warnings} errors={errors} />
            },
            warnings || undefined,
            errors || undefined
          );
        }
      }
    }
  );
};

export const getUrlDecodedConfigurationId = (name: string) => {
  name = name.replace(/[[]/, '\\[').replace(/[\]]/, '\\]');
  const regex = new RegExp('[\\?&#]' + name + '=([^&#]*)');
  const results = regex.exec(window.location.search || window.location.hash);
  return results === null
    ? ''
    : decodeURIComponent(results[1].replace(/\+/g, ' '));
};

export const cloning = (item: IRowProps) =>
  (({ level, children, ...others }) => ({ ...others }))(item); // remove level and children array

export const headerContent = {
  'Content-Type': 'application/json'
};

// This function is for calculating how many children there are selected, it also counts the children of different levels deep
export const countAllChildren = ({
  uniqId,
  count,
  allData,
  selectedIds,
  parentId
}: ICACProps) => {
  const children = allData[uniqId].children;
  if (children.length > 0) {
    children.forEach((child: string) => {
      count = countAllChildren({
        uniqId: child,
        count,
        allData,
        selectedIds,
        parentId
      });
      return count;
    });
  }

  return selectedIds.includes(uniqId) && uniqId !== parentId
    ? (count += 1)
    : count;
};

export const hasElementOverflown = (element?: HTMLElement) => {
  if (!element) {
    return false;
  }
  return (
    element.scrollHeight > element.clientHeight ||
    element.scrollWidth > element.clientWidth
  );
};

export const isFilterTypeText = (type: FilterTypes) =>
  type === FilterTypes.text;

export const manipulateStringValuesToBooleans = (
  values: IResponseRoots['values']
) =>
  values.map(item => ({
    ...item,
    hasChildren: ['1', 'true'].includes(item.hasChildren),
    isImported: ['1', 'true'].includes(item.isImported)
  }));

// This function is a recursive one, it fetches the children for the provided root.
// When 1 of the children has children of itself it is gonna call the function again and so on ...
// This is necessary for the expand all, select all and import all functionality
// We are gonna make many different calls to fetch the data
export const getChildren = async (
  original: IRowProps,
  global: State
): Promise<IResponseRoots['values']> =>
  fetch(
    `${baseUrlVisualization}children?configurationId=${global.configurationId}`,
    {
      method: 'post',
      headers: headerContent,
      body: JSON.stringify({
        values: [cloning(original)],
        commonParameters: global.parameters
      })
    }
  )
    .then(handleFetchResponse)
    .then(async ({ values }: { values: IResponseRoots['values'] }) =>
      Promise.all(
        manipulateStringValuesToBooleans(values).map(async (item, index) => {
          const updatedItemWithLevelAndTreeNode = {
            ...item,
            level: original.level + 1,
            treeNodeId: `${original.treeNodeId}.${index + 1}`
          };
          return {
            ...updatedItemWithLevelAndTreeNode,
            uniqId: `${item.uri}_${original.treeNodeId}.${index + 1}`,
            children: item.hasChildren
              ? await getChildren(updatedItemWithLevelAndTreeNode, global)
              : []
          };
        })
      )
    )
    .catch((err: Error | Response) => {
      notify(
        'There was a problem retrieving all the children, please try again.',
        undefined,
        {
          type: toast.TYPE.ERROR,
          autoClose: 5000
        }
      );
      return [];
    });

// this function is gonna update the data, this data is a normalized list
// It is also a recursive one for updating the children of children and so on ...
// This is necessary for the expand all, select all and import all functionality
export const updateTreeDataWithAllChildren = (
  children: IResponseRoots['values'],
  dataToChange: State['data'],
  parentLevel: number,
  parentId?: string
) => {
  children.forEach(item => {
    if (item.children.length > 0) {
      updateTreeDataWithAllChildren(item.children, dataToChange, item.level);
    }

    // this is gonna update the children for the provided item in the children array
    dataToChange[item.uniqId] = {
      ...item,
      children: item.children.map(prop('uniqId'))
    };

    // this is gonna update the children for the parent
    // It is only gonna execute 1 time
    if (parentId) {
      dataToChange[parentId].children = Object.values(children).reduce(
        (result, { level, uniqId }) => [
          ...result,
          ...(level === parentLevel + 1 ? [uniqId] : [])
        ],
        []
      );
    }
  });
  return dataToChange;
};

// this function is for checking a provided value has the provided treeNode inside of it
// Is used in filtering the right items during select/unselect all
export const valueHasTreeNode = (id: string, treeNode = '') =>
  id.substring(id.lastIndexOf('_') + 1).startsWith(treeNode);

// Send message to parent window to adjust the iframe's height
export const updateHeight = (pagination: boolean, configId: string) => {
  const tableBody = document.getElementById(customId('TableBody', configId));
  window.parent.postMessage(
    JSON.stringify({
      messageType: customId('resizing', configId),
      height: tableBody?.scrollHeight,
      pagination
    }),
    '*'
  );
};

export const customId = (id: string, configId: string) =>
  configId ? `${configId}_${id}` : id;

export const handleFetchResponse = async (response: Response) => {
  if (response.ok) {
    return response.json();
  }
  return Promise.reject(response);
};

export const handleFetchError = async (
  errorOrResponse: Response | Error,
  errorCallback: (error: Error) => void,
  responseCallback: (message: string) => void
) => {
  if (errorOrResponse instanceof Response) {
    errorOrResponse.json().then((json: any) => {
      responseCallback(json.message);
    });
  } else {
    errorCallback(errorOrResponse);
  }
};
