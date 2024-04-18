import React, { useGlobal, useDispatch } from 'reactn';
import { useState, useEffect } from 'react';
import TextField from '@material-ui/core/TextField';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';
import produce from 'immer';
import _get from 'lodash/get';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { ToggleButton, ToggleButtonGroup } from '@material-ui/lab';

import { autoProps, useStyles } from '../../../utils/autoCompleteProps';
import { itemTypes } from '../../../utils/itemTypes';
import constants from '../../../constants';
import { colors } from '../../../utils/colors';
import {
  Configuration,
  TargetSystemOrEndpoint,
  ConfigTypes,
  Columns,
  DefaultGraphs,
  QueryTypes,
  ConflictMessage
} from '../../../types';
import PageHeader from './PageHeader.component';
import AlertDialog from './AlertDialog.component';
import { dataKeysType } from '../../../global';
import ColumnsList from './ColumnsList.component';
import ImportStep from './ImportStep.component';
import DateFields from './DateFields.component';
import {
  HeaderTitle,
  Row,
  ConfigurationColumn,
  ConfigVisualize,
  ConfigRow,
  ConfigurationsPageWrapper,
  ConfigPage,
  ConfigContent
} from '../../../utils/styles';
import {
  checkUndefinedOrNull,
  globalTextFieldProps,
  importStepDefaultValues,
  checkError,
  defineAutocompleteOptions,
  resultTargetOrEndpoints,
  includesEmptyString
} from '../../../utils';
import { configTypes } from '../../../utils/configTypes';
import { APIendpoints } from '../../../utils/endpoints';
import { headerContent, showAlert as errorAlert } from '../../../reducers';
import { toast } from 'react-toastify';
import { FormControl, Typography } from '@material-ui/core';

// tslint:disable:no-console
// tslint:disable-next-line: cyclomatic-complexity
export default ({
  configuration,
  type,
  create,
  configType
}: {
  configuration: Configuration;
  type: dataKeysType;
  create: boolean;
  configType: ConfigTypes;
}) => {
  const fetchDataConfigPage = useDispatch('fetchDataConfigPage');
  const updateConfig = useDispatch('updateItem');
  const addConfiguration = useDispatch('addItem');
  const [endpoints] = useGlobal('sparqlendpoints');
  const [queries] = useGlobal('sparqlqueries');
  const [workspaces] = useGlobal('workspaces');
  const [jsonapis] = useGlobal('jsonapis');
  const [, setEditItem] = useGlobal('editItem');
  const [, setCreateItem] = useGlobal('createItem');

  const [pagination, setPagination] = useState(_get(
    configuration,
    'visualization.enablePagination',
    false
  ) as boolean);
  const [status, setStatus] = useState(configuration.isActive || false);
  const [simpleFeedback, setSimpleFeedback] = useState(
    configuration.isSimpleFeedback || false
  );
  const [name, setName] = useState(configuration.name || undefined);
  const [description, setDescription] = useState(
    configuration.description || ''
  );
  const [displayName, setDisplayName] = useState(
    configuration.displayName || ''
  );
  const [startDate, setStartDate] = useState<Configuration['startDate']>(
    create
      ? Date.parse(new Date(Date.now()).toString())
      : configuration.startDate
  );
  const [endDate, setEndDate] = useState<Configuration['endDate']>(
    create ? null : configuration.endDate
  );
  const [showAlert, setShowAlert] = useState(false);
  const [importSteps, setImportSteps] = useState(
    create ? [] : configuration.importSteps || []
  );
  const [endpointId, setEndpointId] = useState(_get(
    configuration,
    'sparqlEndpoint.id',
    ''
  ) as string);
  const [workspaceId, setWorkspaceId] = useState(_get(
    configuration,
    'workspace.id',
    ''
  ) as string);
  const [jsonApiId, setJsonApiId] = useState(_get(
    configuration,
    'jsonApi.id',
    ''
  ) as string);
  const [rootsQueryId, setRootsQueryId] = useState(_get(
    configuration,
    'visualization.rootsQuery.query.id',
    ''
  ) as string);
  const [oldRootsQueryId, setOldRootsQueryId] = useState(
    create
      ? ''
      : (_get(configuration, 'visualization.rootsQuery.query.id', '') as string)
  );
  const [rootsGraphs, setRootsGraphs] = useState(_get(
    configuration,
    'visualization.rootsQuery.defaultGraphs',
    []
  ) as string[]);
  const [childrenQueryId, setChildrenQueryId] = useState(_get(
    configuration,
    'visualization.childrenQuery.query.id',
    ''
  ) as string);
  const [childrenGraphs, setChildrenGraphs] = useState(_get(
    configuration,
    'visualization.childrenQuery.defaultGraphs',
    []
  ) as string[]);
  const [targetSystemId, setTargetSystemId] = useState<string | null>(_get(
    configuration,
    'visualization.additionalInputs.id',
    ''
  ) as string);
  const [titleQueryId, setTitleQueryId] = useState<string | null>(_get(
    configuration,
    'visualization.titleQuery.id',
    ''
  ) as string);
  const [columns, setColumns] = useState(
    checkUndefinedOrNull(configuration.visualization)
      ? {}
      : configuration.visualization.columns
  );
  const [variablesError] = useState(false);
  const [targetSystemsWorkspace, setTargetSystemsWorkspace] = useState<
    TargetSystemOrEndpoint[]
  >([]);
  const [endpointsJsonApi, setEndpointsJsonApi] = useState<
    TargetSystemOrEndpoint[]
  >([]);

  const isRelaticsConfigType = configType === configTypes.relatics;

  const pickedRootsQuery = queries.find(item => item.id === rootsQueryId);
  const pickedEndpoint = endpoints.find(item => item.id === endpointId);

  const pickedWorkspace = workspaces.find(item => item.id === workspaceId);
  const pickedJsonApi = jsonapis.find(item => item.id === jsonApiId);
  const pickedAdditionalInput = isRelaticsConfigType
    ? targetSystemsWorkspace.find(item => item.id === targetSystemId)
    : endpointsJsonApi.find(item => item.id === targetSystemId);

  const PageHeaderProps = {
    pageTitle: create
      ? `New ${configType} Configuration`
      : `Details ${configType} Configuration`,
    cancelAction: create
      ? () =>
          setCreateItem({
            isCreating: false,
            item: {},
            type: '',
            configType: undefined
          })
      : () =>
          setEditItem({
            isEditing: false,
            item: {},
            type: ''
          }),
    editPage: create ? false : true,
    ...(create && { createPage: true })
  };

  const configurationObject = (): Configuration => {
    const trimmedRootsGraphs = rootsGraphs
      ? rootsGraphs.map(graph => graph.trim())
      : [];
    const trimmedChildrenGraphs = childrenGraphs
      ? childrenGraphs.map(graph => graph.trim())
      : [];

    return {
      isActive: status,
      isSimpleFeedback: simpleFeedback,
      name,
      description,
      startDate,
      endDate,
      displayName,
      targetType: configType,
      sparqlEndpoint: pickedEndpoint,
      ...(isRelaticsConfigType
        ? { workspace: pickedWorkspace }
        : { jsonApi: pickedJsonApi }),
      visualization: {
        enablePagination: pagination,
        rootsQuery: {
          defaultGraphs: includesEmptyString(trimmedRootsGraphs)
            ? []
            : trimmedRootsGraphs,
          query: pickedRootsQuery
        },
        childrenQuery: {
          defaultGraphs: includesEmptyString(trimmedChildrenGraphs)
            ? []
            : trimmedChildrenGraphs,
          query: queries.find(item => item.id === childrenQueryId)
        },
        columns,
        additionalInputs: pickedAdditionalInput,
        titleQuery: queries.find(item => item.id === titleQueryId)
      },
      importSteps: importStepDefaultValues(importSteps)
    };
  };

  const alertProps = {
    description: create
      ? `Add the newly created ${configType} configuration`
      : `Update the following ${configType} configuration: ${configuration.name ||
          '...'} -> ${name || '...'}`,
    confirmationAction: create
      ? () => {
          addConfiguration(configurationObject(), itemTypes.configurations);
          setCreateItem({
            isCreating: false,
            item: {},
            type: '',
            configType: undefined
          });
          setShowAlert(false);
        }
      : () => {
          updateConfig(
            {
              id: configuration.id,
              ...configurationObject()
            },
            itemTypes.configurations
          );
          setEditItem({
            isEditing: false,
            item: {},
            type: ''
          });
          setShowAlert(false);
        }
  };

  useEffect(() => {
    fetchDataConfigPage();
  }, [fetchDataConfigPage]);

  useEffect(() => {
    const fetchLinkedData = async () => {
      const result = await resultTargetOrEndpoints(
        isRelaticsConfigType
          ? {
              workspace: true,
              id: workspaceId,
              updateError: () => setTargetSystemsWorkspace([]),
              updateLoading: () => undefined
            }
          : {
              workspace: false,
              id: jsonApiId,
              updateError: () => setEndpointsJsonApi([]),
              updateLoading: () => undefined
            }
      );
      if (result !== undefined) {
        isRelaticsConfigType
          ? setTargetSystemsWorkspace(result)
          : setEndpointsJsonApi(result);
      }
    };

    if (workspaceId || jsonApiId) {
      fetchLinkedData();
    }
  }, [workspaceId, jsonApiId, isRelaticsConfigType]);

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    if (rootsQueryId !== null && rootsQueryId !== '') {
      fetch(APIendpoints.sparqlSelection, {
        method: 'PUT',
        headers: headerContent,
        body: JSON.stringify({
          configuredColumns: columns,
          previousSelectedRootsQueryId: oldRootsQueryId,
          selectedRootsQueryId: rootsQueryId
        })
      })
        .then(async (res: Response) => {
          if (res.ok) {
            return res.json();
          }
          if (res.status === 409) {
            const conflictMessage: ConflictMessage = await res.json();

            errorAlert({
              title: conflictMessage.code,
              feedback:
                'The update of the columns in the config failed, because there is an error in the query. Please choose another query or update this one.',
              type: toast.TYPE.WARNING
            });

            return columns;
          }
        })
        .then((res: { [key: string]: Columns }) => {
          setColumns(res);
        })
        .catch((err: Error) =>
          errorAlert({
            title: 'Updating the columns',
            feedback: constants.errorMessages.crud,
            errMessage: err.message
          })
        );
    }
    setColumns({});
  }, [rootsQueryId]);

  const classes = useStyles();

  return (
    <ConfigurationsPageWrapper>
      <AlertDialog
        open={showAlert}
        dialogOnClose={() => setShowAlert(false)}
        cancelAction={() => setShowAlert(false)}
        {...alertProps}
      />

      <ConfigPage>
        <PageHeader
          showFilters={false}
          type={type}
          confirmationAction={() => {
            setShowAlert(true);
          }}
          configId={configuration.id}
          {...PageHeaderProps}
        />
        <ConfigContent>
          <ConfigRow grow={1}>
            <Row
              style={{ alignItems: 'center', justifyContent: 'space-between' }}
            >
              <Row
                style={{
                  width: '50%'
                }}
              >
                <HeaderTitle>General</HeaderTitle>
              </Row>
              <Row style={{ width: '50%', justifyContent: 'space-between' }}>
                <FormControlLabel
                  label="Active"
                  labelPlacement="start"
                  control={
                    <Checkbox
                      id="isActiveCheckbox"
                      checked={status}
                      onChange={() => setStatus(!status)}
                      style={{
                        color: status ? 'lightGreen' : colors.black
                      }}
                    />
                  }
                />
                <FormControl>
                  <ToggleButtonGroup
                    size={'small'}
                    value={simpleFeedback}
                    exclusive={true}
                    onChange={(event, newFeedback) => {
                      setSimpleFeedback(newFeedback);
                    }}
                    aria-label="feedback"
                  >
                    <ToggleButton
                      value={false}
                      aria-label="advanced feedback"
                      style={{
                        backgroundColor: !simpleFeedback
                          ? 'rgba(246, 229, 36, 0.75)'
                          : 'initial',
                        color: !simpleFeedback ? 'black' : 'initial'
                      }}
                    >
                      <Typography variant="subtitle2">
                        Advanced Feedback
                      </Typography>
                    </ToggleButton>
                    <ToggleButton
                      value={true}
                      aria-label="simple feedback"
                      style={{
                        backgroundColor: simpleFeedback
                          ? 'rgba(246, 229, 36, 0.75)'
                          : 'initial',
                        color: simpleFeedback ? 'black' : 'initial'
                      }}
                    >
                      <Typography variant="subtitle2">
                        Simple feedback
                      </Typography>
                    </ToggleButton>
                  </ToggleButtonGroup>
                </FormControl>
              </Row>
            </Row>
            <Row>
              <TextField
                error={checkError(name)}
                label="Configuration name"
                id="inputName"
                value={name || ''}
                onChange={e => {
                  setName(e.target.value);
                }}
                {...globalTextFieldProps(true, true)}
              />
              <TextField
                error={checkError(displayName)}
                style={{ marginLeft: '10px' }}
                value={displayName || ''}
                label="Display name"
                id="inputDisplayName"
                onChange={e => {
                  setDisplayName(e.target.value);
                }}
                {...globalTextFieldProps(true, true)}
              />
            </Row>

            <Row style={{ alignItems: 'center' }}>
              <TextField
                label="Configuration description"
                value={description || ''}
                id="inputDescription"
                multiline={true}
                rowsMax={3}
                onChange={e => {
                  setDescription(e.target.value);
                }}
                {...globalTextFieldProps(true, false)}
              />
              <DateFields
                testIds={{
                  start: 'ConfigDateStart',
                  end: 'ConfigDateEnd'
                }}
                labels={{ start: 'Start', end: 'End' }}
                values={{ start: startDate, end: endDate }}
                onChangeStart={date => {
                  setStartDate(
                    date !== null ? Date.parse(date.toString()) : null
                  );
                }}
                onChangeEnd={date => {
                  setEndDate(
                    date !== null ? Date.parse(date.toString()) : null
                  );
                }}
                extraStyle={false}
                configPage={true}
              />
            </Row>
          </ConfigRow>
          <ConfigRow grow={1}>
            <HeaderTitle>Environment</HeaderTitle>
            <Row
              style={{
                marginBottom: '10px'
              }}
            >
              <Autocomplete
                {...autoProps({
                  label: 'SPARQL Endpoint',
                  required: true,
                  error:
                    checkError(endpointId) ||
                    ((pickedEndpoint &&
                      (checkError(pickedEndpoint.name) ||
                        checkError(pickedEndpoint.url))) ||
                      false),
                  classes
                })}
                defaultValue={
                  create || !endpointId
                    ? null
                    : {
                        value: endpointId,
                        display: _get(configuration, 'sparqlEndpoint.name', '')
                      }
                }
                options={defineAutocompleteOptions(endpoints, 'endpoint')}
                onChange={(event, value) => {
                  setEndpointId(value ? value.value : value);
                }}
              />
              <Autocomplete
                {...autoProps({
                  subItem: true,
                  configType: isRelaticsConfigType,
                  typeSubItem: isRelaticsConfigType
                    ? !!workspaceId && workspaceId.length > 0
                    : !!jsonApiId && jsonApiId.length > 0,
                  label: isRelaticsConfigType ? 'Workspace' : 'Json Api',
                  required: true,
                  style: { marginLeft: '10px' },
                  ...(isRelaticsConfigType
                    ? {
                        error:
                          checkError(workspaceId) ||
                          (pickedWorkspace &&
                            (checkError(pickedWorkspace.name) ||
                              checkError(pickedWorkspace.workspaceId) ||
                              checkError(pickedWorkspace.environmentId)))
                      }
                    : {
                        error:
                          checkError(jsonApiId) ||
                          (pickedJsonApi &&
                            (checkError(pickedJsonApi.name) ||
                              checkError(pickedJsonApi.serviceUrl)))
                      }),
                  classes
                })}
                defaultValue={
                  isRelaticsConfigType
                    ? create || !workspaceId
                      ? null
                      : {
                          value: workspaceId,
                          display: _get(configuration, 'workspace.name', ''),
                          envType: constants.workspace
                        }
                    : create || !jsonApiId
                    ? null
                    : {
                        value: jsonApiId,
                        display: _get(configuration, 'jsonApi.name', ''),
                        envType: constants.jsonapi
                      }
                }
                options={
                  isRelaticsConfigType
                    ? defineAutocompleteOptions(workspaces, constants.workspace)
                    : defineAutocompleteOptions(jsonapis, constants.jsonapi)
                }
                onChange={(event, value) => {
                  const pickedValue = value ? value.value : value;
                  if (
                    isRelaticsConfigType
                      ? pickedValue !== workspaceId
                      : pickedValue !== jsonApiId
                  ) {
                    setTargetSystemsWorkspace([]);
                    setEndpointsJsonApi([]);
                  }

                  setImportSteps(
                    importSteps.map(step => ({
                      ...step,
                      importTarget: null
                    }))
                  );
                  setTargetSystemId(null);
                  setWorkspaceId(pickedValue);
                  setJsonApiId(pickedValue);
                }}
              />
            </Row>
          </ConfigRow>
          <ConfigRow grow={1}>
            <Row style={{ alignItems: 'center' }}>
              <HeaderTitle>Visualize</HeaderTitle>
              <FormControlLabel
                style={{ paddingTop: '5px' }}
                label="Enable Pagination"
                labelPlacement="start"
                control={
                  <Checkbox
                    id="EnablePagination"
                    checked={pagination}
                    onChange={() => setPagination(!pagination)}
                    style={{
                      color: pagination ? 'lightGreen' : colors.black
                    }}
                  />
                }
              />
            </Row>

            <ConfigVisualize>
              <ConfigurationColumn>
                <Row>
                  <Autocomplete
                    {...autoProps({
                      label: 'SPARQL roots query',
                      required: true,
                      error:
                        checkError(rootsQueryId) ||
                        (!!pickedRootsQuery &&
                          (checkError(pickedRootsQuery.name) ||
                            checkError(pickedRootsQuery.query))) ||
                        variablesError,
                      classes
                    })}
                    defaultValue={
                      create || !rootsQueryId
                        ? null
                        : {
                            value: rootsQueryId,
                            display: _get(
                              configuration,
                              'visualization.rootsQuery.query.name',
                              ''
                            )
                          }
                    }
                    options={defineAutocompleteOptions(
                      [...queries].filter(
                        filter => filter.type === QueryTypes.roots
                      ),
                      'query'
                    )}
                    onChange={(event, value) => {
                      setOldRootsQueryId(rootsQueryId);
                      setRootsQueryId(value ? value.value : value);
                    }}
                  />
                  <TextField
                    label="Default Graphs SPARQL roots query"
                    value={rootsGraphs && rootsGraphs.join()}
                    onChange={e => {
                      const graphs = e.target.value.split(',');
                      setRootsGraphs(graphs);
                    }}
                    id="DefaultGraphsRoot"
                    placeholder={constants.placeHolderGraphs}
                    style={{ marginLeft: '10px' }}
                    {...globalTextFieldProps(true, false)}
                  />
                </Row>
                <Row>
                  <Autocomplete
                    {...autoProps({
                      label: 'SPARQL children query',
                      classes
                    })}
                    defaultValue={
                      create || !childrenQueryId
                        ? null
                        : {
                            value: childrenQueryId,
                            display: _get(
                              configuration,
                              'visualization.childrenQuery.query.name',
                              ''
                            )
                          }
                    }
                    options={defineAutocompleteOptions(
                      [...queries].filter(
                        filter => filter.type === QueryTypes.children
                      ),
                      'query'
                    )}
                    onChange={(event, value) => {
                      setChildrenQueryId(value ? value.value : value);
                    }}
                  />
                  <TextField
                    label="Default Graphs SPARQL children query"
                    value={childrenGraphs && childrenGraphs.join()}
                    onChange={e => {
                      const graphs = e.target.value.split(',');
                      setChildrenGraphs(graphs);
                    }}
                    id="DefaultGraphsChildren"
                    placeholder={constants.placeHolderGraphs}
                    style={{ marginLeft: '10px' }}
                    {...globalTextFieldProps(true, false)}
                  />
                </Row>
                <Row>
                  <Autocomplete
                    {...autoProps({
                      label: 'Additional input',
                      classes
                    })}
                    value={
                      !!targetSystemId && targetSystemId.length > 0
                        ? {
                            value: targetSystemId,
                            display: isRelaticsConfigType
                              ? (_get(
                                  pickedAdditionalInput,
                                  'operationName',
                                  ''
                                ) as string)
                              : (_get(
                                  pickedAdditionalInput,
                                  'name',
                                  ''
                                ) as string)
                          }
                        : null
                    }
                    options={defineAutocompleteOptions(
                      isRelaticsConfigType
                        ? targetSystemsWorkspace.filter(
                            ({ type }) => type === constants.targetTypes.sending
                          )
                        : endpointsJsonApi.filter(
                            ({ type }) => type === constants.targetTypes.sending
                          ),
                      'targetsystem',
                      isRelaticsConfigType
                    )}
                    onChange={(event, value) => {
                      setTargetSystemId(value ? value.value : value);
                    }}
                  />
                </Row>
                {isRelaticsConfigType && (
                  <Row>
                    <Autocomplete
                      {...autoProps({
                        label: 'SPARQL title query',
                        classes
                      })}
                      defaultValue={
                        create || !titleQueryId
                          ? null
                          : {
                              value: titleQueryId,
                              display: _get(
                                configuration,
                                'visualization.titleQuery.name',
                                ''
                              )
                            }
                      }
                      options={defineAutocompleteOptions(
                        [...queries].filter(
                          filter => filter.type === QueryTypes.title
                        ),
                        'query'
                      )}
                      onChange={(event, value) => {
                        setTitleQueryId(value ? value.value : value);
                      }}
                    />
                  </Row>
                )}
              </ConfigurationColumn>
            </ConfigVisualize>
            <ColumnsList
              setColumns={setColumns}
              columns={columns}
              updateVisibleStatus={(status, index) => {
                setColumns(previousState =>
                  produce(previousState, draftState => {
                    draftState[index].visible = status;
                  })
                );
              }}
              updateDisplayName={(event, index) => {
                event.persist();
                setColumns(previousState =>
                  produce(previousState, draftState => {
                    draftState[index].displayName = event.target.value;
                  })
                );
              }}
            />
          </ConfigRow>
          <ConfigRow grow={1}>
            <ImportStep
              setImportSteps={setImportSteps}
              isRelatics={isRelaticsConfigType}
              queries={[...queries].filter(
                filter => filter.type === QueryTypes.import
              )}
              targetSystems={(isRelaticsConfigType
                ? targetSystemsWorkspace
                : endpointsJsonApi
              ).filter(({ type }) => type === constants.targetTypes.receiving)}
              steps={importSteps}
              addNewEmptyRow={() =>
                setImportSteps(previousState =>
                  produce(previousState, draftState =>
                    draftState.concat({
                      name: '',
                      sparqlQuery: {
                        query: null,
                        defaultGraphs: []
                      },
                      importTarget: null
                    })
                  )
                )
              }
              updateRowData={(event, index, item, value, subItem) => {
                event.persist();
                setImportSteps(previousState =>
                  produce(previousState, draftState => {
                    if (!subItem) {
                      draftState[index][item] = value;
                    } else {
                      // @ts-ignore
                      draftState[index][item][subItem] = value;
                      if (subItem === 'defaultGraphs') {
                        const graphs: DefaultGraphs = value.split(',');
                        // @ts-ignore
                        draftState[index][item][subItem] = graphs;
                      }
                    }
                  })
                );
              }}
              deleteRowData={item =>
                setImportSteps(
                  importSteps.filter(importStep => importStep !== item)
                )
              }
            />
          </ConfigRow>
        </ConfigContent>
      </ConfigPage>
    </ConfigurationsPageWrapper>
  );
};
