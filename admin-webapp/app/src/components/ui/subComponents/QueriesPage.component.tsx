import { useState, useEffect } from 'react';
import React, { useGlobal, useDispatch } from 'reactn';
import styled from 'styled-components';
import TextField from '@material-ui/core/TextField';
import ErrorIcon from '@material-ui/icons/Error';
import ArrowRight from '@material-ui/icons/ArrowRightAlt';
import Autocomplete from '@material-ui/lab/Autocomplete';
// @ts-ignore
import YASQE from 'yasgui-yasqe';
import QueryFilterFields from './QueryFilterFields.component';
import produce from 'immer';

import {
  QueryTypes,
  QueryPicklists,
  Query,
  Records,
  YasguiError,
  YasguiEditorError,
  YasguiEditorSuccess,
  DefaultGraphs
} from '../../../types';
import TestingResults from './TestingResults.component';
import AlertDialog from './AlertDialog.component';
import PageHeader from './PageHeader.component';
import { autoProps, useStyles } from '../../../utils/autoCompleteProps';
import CustomSpinner from './CustomSpinner.component';
import { dataKeysType } from '../../../global';
import constants from '../../../constants';
import {
  Text,
  Wrapper,
  BigWrapper,
  TestingZoneWrapper,
  Row
} from '../../../utils/styles';
import {
  globalTextFieldProps,
  checkError,
  defineAutocompleteOptions,
  includesEmptyString,
  menuOptions
} from '../../../utils';
import { APIendpoints } from '../../../utils/endpoints';
import { itemTypes } from '../../../utils/itemTypes';

export default ({
  query,
  type,
  create
}: {
  query: Query;
  type: dataKeysType;
  create: boolean;
}) => {
  const fetchData = useDispatch('fetchData');
  const [endpoints] = useGlobal('sparqlendpoints');
  const [sparqlqueries] = useGlobal('sparqlqueries');
  const [, setEditItem] = useGlobal('editItem');
  const [, setCreateItem] = useGlobal('createItem');
  const updateQuery = useDispatch('updateItem');
  const addQuery = useDispatch('addItem');

  const [showAlert, setShowAlert] = useState(false);
  const [loading, setLoading] = useState(false);
  const [queryType, setQueryType] = useState<QueryTypes>(query.type);
  const [name, setName] = useState(query.name);
  const [description, setDescription] = useState(query.description || '');
  const [queryText, setQueryText] = useState(query.query || '');
  const [yasguiError, setYasguiError] = useState<YasguiError>({
    status: undefined,
    statusText: '',
    responseText: '',
    error: true
  });
  const [records, setRecords] = useState<Records>({
    headers: [],
    results: []
  });
  const [endpointId, setEndpointId] = useState('');
  const [defaultGraphs, setDefaultGraphs] = useState<DefaultGraphs>([]);
  const [currentQueryEditor, setCurrentQueryEditor] = useState('');
  const [nothingFound, setNothingFound] = useState(false);
  const [filterFields, setFilterFields] = useState<QueryPicklists[]>(
    create ? [] : query.filterFields
  );

  const isQueryTypeRoots = queryType === QueryTypes.roots;

  const queryObject = {
    name,
    description,
    query: queryText,
    filterFields: isQueryTypeRoots ? filterFields : [],
    type: queryType
  };

  const alertProps = {
    description: create
      ? 'Add the newly created query'
      : `Update the following query: ${query.name || '...'} -> ${name ||
          '...'}`,
    confirmationAction: create
      ? () => {
          addQuery(queryObject, itemTypes.queries);
          setCreateItem({ isCreating: false, item: {}, type: '' });
          setShowAlert(false);
        }
      : () => {
          updateQuery(
            {
              id: query.id,
              ...queryObject
            },
            itemTypes.queries
          );
          setEditItem({ isEditing: false, item: {}, type: '' });
          setShowAlert(false);
        }
  };

  const PageHeaderProps = {
    pageTitle: create ? 'New Query' : 'Details Query',
    cancelAction: create
      ? () => setCreateItem({ isCreating: false, item: {}, type: '' })
      : () => setEditItem({ isEditing: false, item: {}, type: '' }),
    editPage: create ? false : true,
    ...(create && { createPage: true })
  };

  const classes = useStyles();

  useEffect(() => {
    fetchData(itemTypes.endpoints);
  }, [fetchData]);

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    setYasguiError({
      error: false,
      status: undefined,
      statusText: '',
      responseText: ''
    });
    setNothingFound(false);
    setRecords({ results: [], headers: [] });
    setLoading(false);

    const el = document.querySelector('.yasqe');
    if (el !== null) {
      el.remove();
    }

    const yasqe = YASQE(document.getElementById('yasqe'), {
      sparql: {
        endpoint: APIendpoints.testQueries,
        getQueryForAjax: (doc: {
          [key: string]: unknown;
          getValue: () => string;
        }) => {
          setLoading(true);
          const query = doc.getValue();
          return query.replace('?additional_values', '(<urn:nothing>)');
        },
        showQueryButton: true,
        callbacks: {
          beforeSend: (e: {
            [key: string]: unknown;
            setRequestHeader: (header: string, id: string) => void;
          }) => {
            const trimmedGraphs = defaultGraphs.map(graph => graph.trim());

            e.setRequestHeader('X-LACES-ENDPOINTID', endpointId);
            if (!includesEmptyString(trimmedGraphs)) {
              e.setRequestHeader('X-LACES-DEFAULTGRAPHS', trimmedGraphs.join());
            }
          },
          error: (res: YasguiEditorError) => {
            console.log('yasgui error', res);
            setYasguiError({
              error: true,
              status: res.status,
              statusText: res.statusText,
              responseText: res.responseText
            });
            setRecords({ results: [], headers: [] });
            setNothingFound(false);
            setLoading(false);
          },
          success: (res: YasguiEditorSuccess) => {
            setLoading(false);
            setNothingFound(res.results.bindings.length === 0);
            setRecords({
              results: res.results.bindings,
              headers: res.head.vars
            });
            setYasguiError({
              error: false,
              status: undefined,
              statusText: '',
              responseText: ''
            });
            setQueryText(yasqe.getValue());
          }
        }
      },
      createShareLink: null
    });

    if (create) {
      yasqe.setValue(currentQueryEditor);
    } else {
      yasqe.setValue(queryText);
    }

    yasqe.on('change', () => {
      setRecords({ results: [], headers: [] });
      setCurrentQueryEditor(yasqe.getValue());
      setQueryText(yasqe.getValue());
    });
  }, [endpointId, defaultGraphs]);

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
          setYasguiError({ ...yasguiError, error: queryText === '' });
        }}
        {...PageHeaderProps}
      />

      <Wrapper column={true}>
        <Row>
          <TextField
            error={checkError(name)}
            label="Name"
            id="queryName"
            value={name || ''}
            onChange={e => {
              setName(e.target.value);
            }}
            {...globalTextFieldProps(true, true)}
          />
          <Autocomplete
            {...autoProps({
              label: 'Type',
              style: { marginLeft: '15px' },
              required: true,
              classes
            })}
            defaultValue={
              create || !queryType
                ? null
                : { value: queryType, display: queryType }
            }
            options={menuOptions([
              QueryTypes.children,
              QueryTypes.filter,
              QueryTypes.import,
              QueryTypes.roots,
              QueryTypes.title
            ])}
            onChange={(event, value) => {
              setQueryType(value ? value.value : value);
            }}
          />
        </Row>
        <TextField
          label="Description"
          id="queryDescription"
          value={description || ''}
          onChange={e => {
            setDescription(e.target.value);
          }}
          style={{ margin: '10px' }}
          multiline={true}
          rowsMax={3}
          {...globalTextFieldProps(true, false)}
        />
      </Wrapper>
      <TestingZoneWrapper data-testid="TestingZone">
        <Text data-testid="TestingZoneTitle">Test the query: </Text>
        <Row>
          <Autocomplete
            {...autoProps({
              label: 'Endpoint',
              classes
            })}
            defaultValue={endpointId}
            options={defineAutocompleteOptions(endpoints, 'endpoint')}
            onChange={(event, value) => {
              setEndpointId(value ? value.value : value);
              setRecords({ results: [], headers: [] });
              setYasguiError({ ...yasguiError, error: true });
            }}
          />
          <TextField
            label="Default Graphs"
            id="defaultGraphs"
            value={defaultGraphs.join()}
            onChange={e => {
              const graphs = e.target.value.split(',');
              setDefaultGraphs(graphs);
              setRecords({ results: [], headers: [] });
              setYasguiError({ ...yasguiError, error: true });
            }}
            placeholder={constants.placeHolderGraphs}
            style={{ marginLeft: '15px' }}
            {...globalTextFieldProps(true, false)}
          />
        </Row>
        <YasguiEditor
          id="yasqe"
          data-testid="yasgui editor"
          error={yasguiError.error}
        />
        {yasguiError.error && yasguiError.status !== undefined ? (
          <YasguiErrorMessage>
            <ErrorIcon />
            <span>
              {yasguiError.status} {yasguiError.statusText}
            </span>
            <ArrowRight style={{ width: '5%' }} />
            <ResponseText>{yasguiError.responseText}</ResponseText>
          </YasguiErrorMessage>
        ) : (
          undefined
        )}
        {loading ? (
          <CustomSpinner />
        ) : (
          <TestingResults
            nothing={nothingFound}
            records={records}
            queriesPage={true}
          />
        )}
      </TestingZoneWrapper>
      {isQueryTypeRoots && (
        <QueryFilterFields
          queries={[...sparqlqueries].filter(
            query => query.type === QueryTypes.filter
          )}
          setFilters={setFilterFields}
          filters={filterFields}
          addNewEmptyRow={() =>
            setFilterFields(previousState =>
              produce(previousState, draftState =>
                draftState.concat({
                  name: '',
                  type: constants.queryFilterTypes.text,
                  variable: '',
                  query: null
                })
              )
            )
          }
          updateRowData={(event, index, item, value) => {
            event.persist();
            setFilterFields(previousState =>
              produce(previousState, draftState => {
                draftState[index][item] = value;
                if (
                  item === 'type' &&
                  value === constants.queryFilterTypes.text
                ) {
                  draftState[index].query = null;
                }
              })
            );
          }}
          deleteRowData={item =>
            setFilterFields(filterFields.filter(filter => filter !== item))
          }
        />
      )}
    </BigWrapper>
  );
};

const YasguiEditor = styled.div<{ error: boolean }>`
  width: 100%;
  font-size: initial;
  border: ${p => (p.error ? `1px solid ${p.theme.colors.red}` : 'unset')};
  margin-top: 10px;
`;

const YasguiErrorMessage = styled.div`
  display: flex;
  flex-direction: row;
  width: 100%;
  color: ${p => p.theme.colors.red};
  font-size: medium;
  justify-content: space-between;
`;

const ResponseText = styled.span`
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 75%;
`;
