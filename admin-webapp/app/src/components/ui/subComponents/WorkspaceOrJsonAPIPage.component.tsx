import React, { useDispatch, useGlobal } from 'reactn';
import { useState, useEffect } from 'react';
import TextField from '@material-ui/core/TextField';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import InputAdornment from '@material-ui/core/InputAdornment';
import Autocomplete from '@material-ui/lab/Autocomplete';
import DeleteIcon from '@material-ui/icons/Delete';
import IconButton from '@material-ui/core/IconButton';
import Button from '@material-ui/core/Button';
import produce from 'immer';
import _sortBy from 'lodash/sortBy';
import uniqId from 'uniqid';

import CustomSpinner from './CustomSpinner.component';
import { autoProps, useStyles } from '../../../utils/autoCompleteProps';
import { WorkspaceOrJsonApi, TargetSystemOrEndpoint } from '../../../types';
import { dataKeysType } from '../../../global';
import {
  BigWrapper,
  Column,
  Row,
  HeaderTitle,
  editButtons,
  StepWrapper,
  inputIcon
} from '../../../utils/styles';
import {
  globalTextFieldProps,
  checkError,
  defineAutocompleteOptions,
  resultTargetOrEndpoints,
  emptyTargetSystem,
  emptyEndpoint,
  menuOptions
} from '../../../utils';
import AlertDialog from './AlertDialog.component';
import PageHeader from './PageHeader.component';
import { colors } from '../../../utils/colors';
import { itemTypes } from '../../../utils/itemTypes';
import constants from '../../../constants';

export default ({
  item,
  type,
  create
}: {
  item: WorkspaceOrJsonApi;
  type: dataKeysType;
  create: boolean;
}) => {
  const updateWorkspaceOrJsonApi = useDispatch('updateItem');
  const addWorkspaceOrJsonApi = useDispatch('addItem');
  const [environments] = useGlobal('environments');
  const [, setEditItem] = useGlobal('editItem');
  const [, setCreateItem] = useGlobal('createItem');

  const [name, setName] = useState(item.name || '');
  const [id] = useState(item.id || '');
  const environmentId =
    environments.length > 0
      ? environments.find(env => env.id === item.environmentId)
      : undefined;
  const [envId, setEnvId] = useState((environmentId && environmentId.id) || '');
  const [workspaceId, setWorkspaceId] = useState(
    (item.workspaceId as string) || ''
  );
  const [targetSystemsOrEndpoints, setTargetSystemsOrEndpoints] = useState<
    TargetSystemOrEndpoint[]
  >([]);
  const [serviceUrl, setServiceUrl] = useState(
    (item.serviceUrl as string) || ''
  );
  const [loading, setLoading] = useState(false);
  const [showAlert, setShowAlert] = useState(false);
  const [specialId, setSpecialId] = useState('');

  const checkJsonApisType = type !== itemTypes.jsonapis;

  const workspaceObject = {
    environmentId: envId,
    workspaceId,
    name
  };

  const jsonApiObject = {
    serviceUrl,
    name
  };

  const PageHeaderProps = {
    pageTitle: create
      ? `New ${checkJsonApisType ? 'Workspace' : 'Json Api'}`
      : `Details ${checkJsonApisType ? 'Workspace' : 'Json Api'}`,
    cancelAction: create
      ? () => setCreateItem({ isCreating: false, item: {}, type: '' })
      : () => setEditItem({ isEditing: false, item: {}, type: '' }),
    editPage: create ? false : true,
    ...(create && { createPage: true })
  };

  const checkForErrors = targetSystemsOrEndpoints.map(item => {
    const itemKeys = Object.keys(item).filter(
      valueKey => !['hidden', 'index', 'id'].includes(valueKey)
    );
    if (checkJsonApisType) {
      const nullValues = itemKeys.map(keyIndex => item[keyIndex] === null);
      if (nullValues.every(value => value)) {
        return null;
      }
      return item;
    }
    const nullValues = itemKeys.map(keyIndex => item[keyIndex] === null);
    if (nullValues.every(value => value)) {
      return null;
    }
    return item;
  });

  const alertProps = {
    description: create
      ? `Add the newly created ${
          checkJsonApisType ? constants.workspace : 'json api'
        }`
      : `Updating the following ${
          checkJsonApisType ? constants.workspace : 'json api'
        }: ${item.name || '...'} -> ${name || '...'}`,
    confirmationAction: create
      ? () => {
          checkJsonApisType
            ? addWorkspaceOrJsonApi(
                {
                  targetDataSystems: checkForErrors.filter(item => !!item),
                  ...workspaceObject
                },
                itemTypes.workspaces
              )
            : addWorkspaceOrJsonApi(
                {
                  endpoints: checkForErrors.filter(item => !!item),
                  ...jsonApiObject
                },
                itemTypes.jsonapis
              );
          setCreateItem({ isCreating: false, item: {}, type: '' });
          setShowAlert(false);
        }
      : () => {
          checkJsonApisType
            ? updateWorkspaceOrJsonApi(
                {
                  id,
                  targetDataSystems: checkForErrors.filter(item => !!item),
                  ...workspaceObject
                },
                itemTypes.workspaces
              )
            : updateWorkspaceOrJsonApi(
                {
                  id,
                  endpoints: checkForErrors.filter(item => !!item),
                  ...jsonApiObject
                },
                itemTypes.jsonapis
              );
          setEditItem({ isEditing: false, item: {}, type: '' });
          setShowAlert(false);
        }
  };

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    const fetchTargetSystemsOrEndpoints = async () => {
      setLoading(true);
      const result = await resultTargetOrEndpoints({
        workspace: checkJsonApisType,
        id,
        updateError: () => {
          setTargetSystemsOrEndpoints([]);
        },
        updateLoading: () => setLoading(false)
      });

      if (result !== undefined) {
        const updatedResult: TargetSystemOrEndpoint[] = result.map(
          (item: TargetSystemOrEndpoint[]) => ({
            ...item,
            hidden: 'true'
          })
        );
        // (first: sort on type / second: sort on operationName or name)
        const sortedResult = _sortBy(updatedResult, [
          'type',
          checkJsonApisType ? 'operationName' : 'name'
        ]);

        setLoading(false);
        setTargetSystemsOrEndpoints(sortedResult);
      }
    };
    if (id) {
      fetchTargetSystemsOrEndpoints();
    }
  }, [id]);

  useEffect(() => {
    setSpecialId(uniqId());
  }, [targetSystemsOrEndpoints.length]);

  const classes = useStyles();

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
        type={type}
        confirmationAction={() => {
          setShowAlert(true);
        }}
        {...PageHeaderProps}
      />
      <TextField
        error={checkError(name)}
        label="Name"
        id="name"
        value={name}
        onChange={e => setName(e.currentTarget.value)}
        {...globalTextFieldProps(true, true)}
      />
      {checkJsonApisType && (
        <TextField
          error={checkError(workspaceId)}
          label="WID"
          id="WID"
          value={workspaceId}
          onChange={e => setWorkspaceId(e.currentTarget.value)}
          {...globalTextFieldProps(true, true)}
        />
      )}
      {!checkJsonApisType && (
        <TextField
          error={checkError(serviceUrl)}
          label="Service URL"
          id="serviceURL"
          value={serviceUrl || ''}
          onChange={e => {
            setServiceUrl(e.target.value);
          }}
          {...globalTextFieldProps(true, true)}
        />
      )}
      {checkJsonApisType && (
        <Autocomplete
          {...autoProps({
            label: 'Environment',
            required: true,
            error: checkError(envId),
            classes
          })}
          defaultValue={
            create || !environmentId
              ? null
              : {
                  value: envId,
                  display: environmentId.name
                }
          }
          options={defineAutocompleteOptions(environments, 'environment')}
          onChange={(event, value) => {
            setEnvId(value ? value.value : value);
          }}
        />
      )}
      <Row
        style={{
          justifyContent: 'space-between',
          marginBottom: '10px',
          marginTop: '8px'
        }}
      >
        <HeaderTitle>
          {checkJsonApisType ? 'Target Data Systems' : 'Endpoints'}
        </HeaderTitle>
        <Button
          data-testid={`Add_${checkJsonApisType ? 'TargetSystem' : 'Endpoint'}`}
          size={'small'}
          style={editButtons}
          onClick={() => {
            setTargetSystemsOrEndpoints(
              targetSystemsOrEndpoints.concat(
                checkJsonApisType
                  ? emptyTargetSystem({ index: specialId })
                  : emptyEndpoint({ index: specialId })
              )
            );
          }}
        >
          Add
        </Button>
      </Row>

      <Column
        style={{
          overflow: 'auto',
          border:
            targetSystemsOrEndpoints.length > 0
              ? `1px solid ${colors.blackOpacity}`
              : 0
        }}
      >
        {loading ? (
          <CustomSpinner />
        ) : (
          targetSystemsOrEndpoints.length > 0 &&
          targetSystemsOrEndpoints.map((item, index) => (
            <StepWrapper
              index={index}
              data-testid={
                checkJsonApisType
                  ? `TargetsystemsWorkspace_${index}`
                  : `EndpointsJsonApi_${index}`
              }
              key={item.id}
              autoComplete="none"
            >
              {checkJsonApisType ? (
                // Fields for the targetsystems of the workspaces
                <>
                  <TextField
                    label="Operation name"
                    value={item.operationName || ''}
                    style={{ margin: '15px' }}
                    onChange={e => {
                      e.persist();
                      setTargetSystemsOrEndpoints(previousState =>
                        produce(previousState, draftState => {
                          draftState[index].operationName = e.target.value;
                        })
                      );
                    }}
                    id={`OperationName_${index}`}
                    error={checkError(item.operationName)}
                    {...globalTextFieldProps(true, false)}
                  />
                  <TextField
                    label="Xpath Expression"
                    value={item.xpathExpression || ''}
                    style={{ margin: '15px' }}
                    onChange={e => {
                      e.persist();
                      setTargetSystemsOrEndpoints(previousState =>
                        produce(previousState, draftState => {
                          draftState[index].xpathExpression = e.target.value;
                        })
                      );
                    }}
                    id={`xpathExpression_${index}`}
                    error={checkError(item.xpathExpression)}
                    autoComplete="new-password"
                    {...globalTextFieldProps(true, false)}
                  />
                  <TextField
                    label="Entry Code"
                    value={item.entryCode || ''}
                    style={{
                      margin: '15px',
                      flexBasis: '50%',
                      maxWidth: '9em',
                      flexShrink: 0.75
                    }}
                    type={item.hidden === 'true' ? 'password' : 'text'}
                    onChange={e => {
                      e.persist();
                      setTargetSystemsOrEndpoints(previousState =>
                        produce(previousState, draftState => {
                          draftState[index].entryCode = e.target.value;
                        })
                      );
                    }}
                    id={`EntryCode_${index}`}
                    error={checkError(item.entryCode)}
                    InputProps={{
                      endAdornment: (
                        <InputAdornment position="end">
                          {item.hidden === 'true' ? (
                            <Visibility
                              onClick={() => {
                                setTargetSystemsOrEndpoints(previousState =>
                                  produce(previousState, draftState => {
                                    draftState[index].hidden = 'false';
                                  })
                                );
                              }}
                              style={inputIcon}
                            />
                          ) : (
                            <VisibilityOff
                              onClick={() => {
                                setTargetSystemsOrEndpoints(previousState =>
                                  produce(previousState, draftState => {
                                    draftState[index].hidden = 'true';
                                  })
                                );
                              }}
                              style={inputIcon}
                            />
                          )}
                        </InputAdornment>
                      )
                    }}
                    autoComplete="new-password"
                    {...globalTextFieldProps(true, false)}
                  />
                </>
              ) : (
                // Fields for the endpoints of the json api's
                <>
                  <TextField
                    label="Name"
                    value={item.name || ''}
                    style={{ margin: '15px' }}
                    onChange={e => {
                      e.persist();
                      setTargetSystemsOrEndpoints(previousState =>
                        produce(previousState, draftState => {
                          draftState[index].name = e.target.value;
                        })
                      );
                    }}
                    id={`Name_${index}`}
                    error={checkError(item.name)}
                    {...globalTextFieldProps(true, false)}
                  />
                  <TextField
                    label="Path"
                    value={item.path || ''}
                    style={{ margin: '15px' }}
                    onChange={e => {
                      e.persist();
                      setTargetSystemsOrEndpoints(previousState =>
                        produce(previousState, draftState => {
                          draftState[index].path = e.target.value;
                        })
                      );
                    }}
                    id={`Path_${index}`}
                    error={checkError(item.path)}
                    {...globalTextFieldProps(true, false)}
                  />
                </>
              )}
              {/* Type field is used for both */}
              <Autocomplete
                {...autoProps({
                  label: 'Type',
                  error: checkError(item.type),
                  classes,
                  style: {
                    margin: '7px 15px',
                    flexBasis: '50%',
                    maxWidth: '9em',
                    flexShrink: 0.75
                  }
                })}
                defaultValue={
                  create || !item.type
                    ? null
                    : {
                        value: item.type,
                        display: item.type
                      }
                }
                options={menuOptions([
                  constants.targetTypes.sending,
                  constants.targetTypes.receiving
                ])}
                onChange={(event, value) => {
                  setTargetSystemsOrEndpoints(previousState =>
                    produce(previousState, draftState => {
                      draftState[index].type = value ? value.value : value;
                    })
                  );
                }}
              />
              <IconButton
                edge="end"
                onClick={() => {
                  setTargetSystemsOrEndpoints(
                    targetSystemsOrEndpoints.filter(targetSystem =>
                      item.index !== undefined
                        ? item.index !== targetSystem.index
                        : item.id !== targetSystem.id
                    )
                  );
                }}
                aria-label="Delete"
                style={{ margin: 'auto', marginRight: '-12px' }}
              >
                <DeleteIcon data-testid={`Delete_${index}`} />
              </IconButton>
            </StepWrapper>
          ))
        )}
      </Column>
    </BigWrapper>
  );
};
