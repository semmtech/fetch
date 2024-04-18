import React, { useDispatch, useGlobal } from 'reactn';
import { useState } from 'react';
import TextField from '@material-ui/core/TextField';

import { Environment } from '../../../types';
import { dataKeysType } from '../../../global';
import { BigWrapper } from '../../../utils/styles';
import { globalTextFieldProps, checkError } from '../../../utils';
import AlertDialog from './AlertDialog.component';
import PageHeader from './PageHeader.component';
import { itemTypes } from '../../../utils/itemTypes';

export default ({
  environment,
  type,
  create
}: {
  environment: Environment;
  type: dataKeysType;
  create: boolean;
}) => {
  const updateEnvironment = useDispatch('updateItem');
  const addEnvironment = useDispatch('addItem');

  const [, setEditItem] = useGlobal('editItem');
  const [, setCreateItem] = useGlobal('createItem');

  const [envName, setEnvName] = useState(environment.name || null);
  const [serviceUrl, setServiceUrl] = useState(environment.serviceUrl || '');
  const [namespace, setNamespace] = useState(environment.namespace || '');
  const [environmentId, setEnvironmentId] = useState(
    environment.environmentId || ''
  );
  const [showAlert, setShowAlert] = useState(false);

  const environmentObject = {
    name: envName,
    serviceUrl,
    namespace,
    environmentId
  };

  const PageHeaderProps = {
    pageTitle: create ? 'New Environment' : 'Details Environment',
    cancelAction: create
      ? () => {
          setCreateItem({ isCreating: false, item: {}, type: '' });
        }
      : () => {
          setEditItem({ isEditing: false, item: {}, type: '' });
        },
    editPage: create ? false : true,
    ...(create && { createPage: true })
  };

  const alertProps = {
    description: create
      ? 'Add the newly created environment'
      : `Update the following environment: ${environment.name ||
          '...'} -> ${envName || '...'}`,
    confirmationAction: create
      ? () => {
          addEnvironment(environmentObject, itemTypes.environments);
          setCreateItem({ isCreating: false, item: {}, type: '' });
          setShowAlert(false);
        }
      : () => {
          updateEnvironment(
            {
              id: environment.id,
              ...environmentObject
            },
            itemTypes.environments
          );
          setEditItem({ isEditing: false, item: {}, type: '' });
          setShowAlert(false);
        }
  };

  return (
    <BigWrapper>
      <AlertDialog
        open={showAlert}
        dialogOnClose={() => setShowAlert(false)}
        cancelAction={() => setShowAlert(false)}
        {...alertProps}
      />

      <PageHeader
        showFilters={false}
        type={type}
        confirmationAction={() => setShowAlert(true)}
        {...PageHeaderProps}
      />

      <TextField
        error={checkError(envName)}
        label="Name"
        id="envName"
        value={envName || ''}
        onChange={e => setEnvName(e.target.value)}
        {...globalTextFieldProps(true, true)}
      />
      <TextField
        error={checkError(serviceUrl)}
        label="Service URL"
        id="serviceURL"
        value={serviceUrl || ''}
        onChange={e => setServiceUrl(e.target.value)}
        {...globalTextFieldProps(true, true)}
      />
      <TextField
        error={checkError(environmentId)}
        label="Environment id"
        id="EnvironmentId"
        value={environmentId || ''}
        onChange={e => setEnvironmentId(e.target.value)}
        {...globalTextFieldProps(true, true)}
      />
      <TextField
        label="Namespace"
        id="name"
        value={namespace || ''}
        onChange={e => setNamespace(e.target.value)}
        {...globalTextFieldProps(true, false)}
      />
    </BigWrapper>
  );
};
