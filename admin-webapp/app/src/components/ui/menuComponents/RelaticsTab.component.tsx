import React, { useDispatch, useGlobal } from 'reactn';
import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';

import { ContainerTab } from '../../../utils/styles';
import Relatics from '../subComponents/Relatics.component';
import AlertDialog from '../subComponents/AlertDialog.component';
import WorkspaceOrJsonAPIPage from '../subComponents/WorkspaceOrJsonAPIPage.component';
import EnvironmentsPage from '../subComponents/EnvironmentsPage.component';
import { dataKeysType } from '../../../global';
import { itemTypes } from '../../../utils/itemTypes';
import constants from '../../../constants';
import WorkspaceClonePageComponent from '../subComponents/WorkspaceClonePage.component';

export default () => {
  const [editItem] = useGlobal('editItem');
  const [cloneItem] = useGlobal('cloneItem');
  const [createItem] = useGlobal('createItem');
  const [error, setError] = useGlobal('errorDelete');
  const itemToDelete = useDispatch('deleteItem');
  const fetchDataRelatics = useDispatch('fetchDataRelatics');
  const [workspaces] = useGlobal('workspaces');
  const [envs] = useGlobal('environments');
  const [deleteItem, setDeleteItem] = useGlobal('removeItem');
  const remove = () =>
    setDeleteItem({
      isDeleting: false,
      item: {},
      type: ''
    });
  const [activeType, setActiveType] = useState<dataKeysType>(
    itemTypes.workspaces
  );
  const [allGlobalFilters, setAllGlobalFilters] = useGlobal('globalFilters');
  const checkError = error && error.message && error.message.length > 0;

  useEffect(() => {
    fetchDataRelatics();
  }, [fetchDataRelatics]);

  useEffect(() => {
    if (checkError) {
      setDeleteItem({
        isDeleting: false,
        item: {},
        type: ''
      });
      toast(error.message, {
        type: toast.TYPE.ERROR
      });
      setError({ name: '', message: '' });
    }
  }, [checkError, setError, setDeleteItem, error.message]);

  const checkTypeWorkspaces = [
    editItem.type,
    createItem.type,
    cloneItem.type
  ].includes(itemTypes.workspaces);
  const checkTypeEnvironments = [editItem.type, createItem.type].includes(
    itemTypes.environments
  );
  const check =
    (editItem.isEditing || createItem.isCreating || cloneItem.isCloning) &&
    (checkTypeWorkspaces || checkTypeEnvironments);

  const envToDelete = async () => {
    const checkDeletedExist =
      deleteItem.type === itemTypes.environments
        ? allGlobalFilters[itemTypes.workspaces].find(
            ({ value }) => value === deleteItem.item.id
          )
        : undefined;

    await itemToDelete(deleteItem.item, itemTypes.environments);
    await setAllGlobalFilters({
      ...allGlobalFilters,
      [itemTypes.workspaces]: allGlobalFilters.workspaces.filter(
        filter => filter !== checkDeletedExist
      )
    });
  };

  const AlertProps = (workspace: boolean) => ({
    item: deleteItem.item.name,
    dialogOnClose: () => remove(),
    cancelAction: () => remove(),
    description: `Delete the selected ${
      workspace ? constants.workspace : 'environment'
    }`,
    confirmationAction: () =>
      workspace
        ? itemToDelete(deleteItem.item, itemTypes.workspaces)
        : envToDelete(),
    open:
      deleteItem.isDeleting &&
      (workspace
        ? deleteItem.type === itemTypes.workspaces
        : deleteItem.type === itemTypes.environments)
  });

  return (
    <ContainerTab>
      <AlertDialog {...AlertProps(true)} />

      <AlertDialog {...AlertProps(false)} />

      {check ? (
        checkTypeWorkspaces ? (
          cloneItem.isCloning ? (
            <WorkspaceClonePageComponent item={cloneItem.item} />
          ) : (
            <WorkspaceOrJsonAPIPage
              item={editItem.item}
              type={itemTypes.workspaces}
              create={
                createItem.isCreating &&
                createItem.type === itemTypes.workspaces
              }
            />
          )
        ) : (
          <EnvironmentsPage
            environment={editItem.item}
            type={itemTypes.environments}
            create={
              createItem.isCreating &&
              createItem.type === itemTypes.environments
            }
          />
        )
      ) : (
        <Relatics
          changeActiveType={(type: dataKeysType) => setActiveType(type)}
          envType={itemTypes.environments}
          type={itemTypes.workspaces}
          envs={envs}
          data={workspaces}
          activeType={activeType}
        />
      )}
    </ContainerTab>
  );
};
