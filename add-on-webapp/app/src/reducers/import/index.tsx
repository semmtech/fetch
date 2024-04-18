import React, { Dispatch } from 'react';
import { addReducer } from 'reactn';

import { IResponseRoots, IRowProps } from '../../types';
import {
  cloning,
  handleFetchError,
  handleFetchResponse,
  headerContent,
  notify,
  valueHasTreeNode
} from '../../utils';
import { baseUrlImport, baseUrlVisualization } from '../../utils/endpoints';
import { State } from 'reactn/default';
import { produce } from 'immer';
import prop from 'lodash/fp/prop';
import { toast } from 'react-toastify';

import AlertActions from '../../components/ui/subComponents/AlertActions.component';

type Roots = Pick<IRowProps, 'uri' | 'uuid' | 'treeNodeId' | 'uniqId'>;

addReducer(
  'import',
  async (
    global: State,
    dispatch: Dispatch<any>,
    treeNode?: IRowProps['treeNodeId']
  ) => {
    const importValues = JSON.stringify({
      values: global.selectedIds
        .filter(id => valueHasTreeNode(id, treeNode))
        .map(id => cloning({ ...global.data[id], isImported: false })),
      commonParameters: global.parameters
    });

    dispatch((global: State) => ({
      ...global,
      loading: true
    }));

    return fetch(`${baseUrlImport}?configurationId=${global.configurationId}`, {
      method: 'post',
      headers: headerContent,
      body: importValues
    })
      .then(handleFetchResponse)
      .then(async response => {
        const successSteps = response.map(prop('success'));
        const error = successSteps.includes(false);

        // this call is for retrieving the roots again so we can update the old uuids with the new ones -> so you can import them again
        // and when you import them again it does not gonna add them to already imported items -> this was a bug
        const roots = await fetch(
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
          .then(({ values }: { values: IResponseRoots['values'] }) =>
            values.map((item: Roots, index) => ({
              uuid: item.uuid,
              uri: item.uri,
              treeNodeId: `${index + 1}`,
              uniqId: `${item.uri}_${index + 1}`
            }))
          )
          .catch((): [] => []);

        if (error) {
          return {
            ...global,
            loading: false,
            importSteps: response
          };
        }

        if (roots.length === 0) {
          notify(
            `During the import, something wrong happened with the retrieval of the roots, please try again`
          );
          return {
            ...global,
            loading: false
          };
        }

        // here we gonna update the store with the importSteps repsonse of what was successfull and update the data with the newly uuids
        // The treeNode is added here for the import all functionality, it is for making sure only the selected items with its children is gonna be unselected and not all of them, same wise for the isImported key -> this was a bug
        return produce(global, draftState => {
          draftState.loading = false;
          draftState.importSteps = response;
          roots.forEach(item => {
            draftState.data[item.uniqId].uuid = item.uuid;
            draftState.data[item.uniqId].children.forEach(id => {
              draftState.data[id].parentUuid = item.uuid;
            });
          });

          if (treeNode) {
            draftState.selectedIds = global.selectedIds.filter(
              id => !valueHasTreeNode(id, treeNode)
            );

            global.selectedIds
              .filter(id => valueHasTreeNode(id, treeNode))
              .forEach(id => {
                draftState.data[id].isImported = true;
              });
            return;
          }
          draftState.selectedIds = [];
          global.selectedIds.forEach(id => {
            draftState.data[id].isImported = true;
          });
        });
      })
      .catch((e: Error | Response) => {
        handleFetchError(
          e,
          (error: Error) => {
            notify(
              `The import could not be started, please try again. Error message: ${error.message}`
            );
          },
          (message: string) => {
            notify(
              'Query Execution Failed. Click Copy for details.',
              undefined,
              {
                type: toast.TYPE.ERROR,
                autoClose: false,
                closeButton: <AlertActions errors={[message]} warnings={[]} />
              }
            );
          }
        );

        return {
          loading: false
        };
      });
  }
);
