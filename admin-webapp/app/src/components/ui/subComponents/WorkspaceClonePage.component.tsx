import React, { useDispatch, useGlobal } from 'reactn';
import { useState, useEffect } from 'react';
import TextField from '@material-ui/core/TextField';
import { toast } from 'react-toastify';

import { WorkspaceOrJsonApi } from '../../../types';
import { BigWrapper } from '../../../utils/styles';
import { globalTextFieldProps, checkError } from '../../../utils';
import AlertDialog from './AlertDialog.component';
import PageHeader from './PageHeader.component';
import { itemTypes } from '../../../utils/itemTypes';
import constants from '../../../constants';
import CustomSpinner from './CustomSpinner.component';

export default ({ item }: { item: WorkspaceOrJsonApi }) => {
  const cloneWorkspaceApi = useDispatch('cloneItem');
  const [, setCloneItem] = useGlobal('cloneItem');
  const [workspaces] = useGlobal('workspaces');
  const [errorClone, setCloneError] = useGlobal('errorClone');

  const getCloneName = (): string => {
    const workspaceNames = workspaces
      .filter(x => x && item && x.environmentId === item.environmentId)
      .map(y => y.name);

    let newName = item.name + '_clone';
    let increment = 1;
    while (workspaceNames.includes(newName)) {
      newName = item.name + '_clone_' + increment.toString();
      increment = increment + 1;
    }

    return newName;
  };

  const [cloneWorkspaceName, setName] = useState(() => getCloneName());
  const [cloneWorkspaceId, setCloneWorkspaceId] = useState('');
  const [showAlert, setShowAlert] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    //If there is an error while cloning
    if (errorClone && errorClone.message) {
      const isTimeOut = errorClone.name === constants.cloningInProgress;
      toast(errorClone.message, {
        type: isTimeOut ? toast.TYPE.INFO : toast.TYPE.ERROR
      });
      setCloneError({ name: '', message: '' });
      isTimeOut && setCloneItem({ isCloning: false, item: {}, type: '' });
      return;
    }
  }, [setCloneError, errorClone, setCloneItem]);

  const PageHeaderProps = {
    pageTitle: `Clone Workspace`,
    cancelAction: () => setCloneItem({ isCloning: false, item: {}, type: '' }),
    createPage: true
  };

  const alertProps = {
    description: `Clone the workspace ${item.name}`,
    confirmationAction: () => {
      setLoading(true);
      cloneWorkspaceApi(
        {
          id: item.id,
          cloneWorkspaceId: cloneWorkspaceId,
          cloneWorkspaceName: cloneWorkspaceName
        },
        itemTypes.workspaces,
        () => setLoading(false)
      );
      setShowAlert(false);
    }
  };

  return (
    <BigWrapper>
      <AlertDialog
        open={showAlert}
        dialogOnClose={() => setShowAlert(false)}
        cancelAction={() => {
          setShowAlert(false);
        }}
        {...alertProps}
      />
      <PageHeader
        showFilters={false}
        type={itemTypes.workspaces}
        confirmationAction={() => {
          if (!cloneWorkspaceName.trim() || !cloneWorkspaceId.trim()) {
            toast('Workspace name and id are mandatory', {
              type: toast.TYPE.ERROR
            });
            return;
          }
          setShowAlert(true);
        }}
        {...PageHeaderProps}
      />
      <TextField
        error={checkError(cloneWorkspaceName)}
        label="Name"
        id="name"
        value={cloneWorkspaceName}
        onChange={e => setName(e.currentTarget.value)}
        {...globalTextFieldProps(true, true)}
      />
      <TextField
        error={checkError(cloneWorkspaceId)}
        label="WID"
        id="WID"
        value={cloneWorkspaceId}
        onChange={e => setCloneWorkspaceId(e.currentTarget.value)}
        {...globalTextFieldProps(true, true)}
      />
      {loading && <CustomSpinner />}
    </BigWrapper>
  );
};
