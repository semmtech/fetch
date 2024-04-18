import { useState, useEffect } from 'react';
import React, { useGlobal, useDispatch } from 'reactn';
import TextField from '@material-ui/core/TextField';
import styled from 'styled-components';
import Button from '@material-ui/core/Button';
import { get } from 'lodash';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { autoProps, useStyles } from '../../../utils/autoCompleteProps';

import { itemTypes } from '../../../utils/itemTypes';
import { Endpoint, Records } from '../../../types';
import constants from '../../../constants';
import TestingResults from './TestingResults.component';
import AlertDialog from './AlertDialog.component';
import PageHeader from './PageHeader.component';
import CustomSpinner from './CustomSpinner.component';
import AuthenticationFields from './AuthenticationFields.component';
import { dataKeysType } from '../../../global';
import {
  globalTextFieldProps,
  checkError,
  defineAutocompleteOptions,
  checkUndefinedOrNull,
  menuOptions
} from '../../../utils';
import {
  Text,
  Wrapper,
  BigWrapper,
  TestingZoneWrapper,
  Row,
  editButtons
} from '../../../utils/styles';
import { APIendpoints } from '../../../utils/endpoints';

export default ({
  endpoint,
  type,
  create
}: {
  endpoint: Endpoint;
  type: dataKeysType;
  create: boolean;
}) => {
  const fetchData = useDispatch('fetchData');
  const [queries] = useGlobal('sparqlqueries');
  const [, setEditItem] = useGlobal('editItem');
  const updateEndpoint = useDispatch('updateItem');
  const [, setCreateItem] = useGlobal('createItem');
  const addEndpoint = useDispatch('addItem');

  const classes = useStyles();

  const defaultAuthorizationValues = {
    userName: get(endpoint, 'authenticationMethod.userName', '') as string,
    password: get(endpoint, 'authenticationMethod.password', '') as string,
    privateKey: get(endpoint, 'authenticationMethod.privateKey', '') as string,
    applicationId: get(
      endpoint,
      'authenticationMethod.applicationId',
      ''
    ) as string
  };
  const [authenticationType, setAuthenticationType] = useState(get(
    endpoint,
    'authenticationMethod.type',
    constants.menuItems.none
  ) as string);
  const [name, setName] = useState(endpoint.name || null);
  const [url, setURL] = useState(endpoint.url || '');
  const [show, setShow] = useState(true);
  const [loading, setLoading] = useState(false);
  const [authorizationValues, setAuthorizationValues] = useState(
    defaultAuthorizationValues
  );
  const [showAlert, setShowAlert] = useState(false);
  const [queryId, setQueryId] = useState('');
  const [play, setPlay] = useState(false);
  const [testResults, setTestResults] = useState<Records>({
    results: [],
    headers: []
  });
  const [errorMessage, setErrorMessage] = useState<string | undefined>(
    undefined
  );
  const [nothingFound, setNothingFound] = useState(false);

  const endpointObject = {
    name,
    url,
    authenticationMethod: {
      type: authenticationType,
      userName: authorizationValues.userName,
      password: authorizationValues.password,
      privateKey: authorizationValues.privateKey,
      applicationId: authorizationValues.applicationId
    }
  };

  const alertProps = {
    description: create
      ? 'Add the newly created endpoint'
      : `Update the following endpoint: ${endpoint.name || '...'} -> ${name ||
          '...'}`,
    confirmationAction: create
      ? () => {
          addEndpoint(endpointObject, itemTypes.endpoints);
          setCreateItem({ isCreating: false, item: {}, type: '' });
          setShowAlert(false);
        }
      : () => {
          updateEndpoint(
            {
              id: endpoint.id,
              ...endpointObject
            },
            itemTypes.endpoints
          );
          setEditItem({ isEditing: false, item: {}, type: '' });
          setShowAlert(false);
        }
  };

  const PageHeaderProps = {
    pageTitle: create ? 'New Endpoint' : 'Details Endpoint',
    cancelAction: create
      ? () => setCreateItem({ isCreating: false, item: {}, type: '' })
      : () => setEditItem({ isEditing: false, item: {}, type: '' }),
    editPage: create ? false : true,
    ...(create && { createPage: true })
  };

  const marginStyle = { margin: '10px' };

  useEffect(() => {
    fetchData(itemTypes.queries);
  }, [fetchData]);

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    setErrorMessage(undefined);
    setNothingFound(false);

    const testEndpoint = async () => {
      setLoading(true);
      const result = await fetch(APIendpoints.testEndpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          credentials: 'include'
        },
        body: JSON.stringify({
          selectedQuery: queryId,
          sparqlEndpoint: endpointObject
        })
      })
        .then(async res => {
          if (res.redirected) {
            window.location.href = res.url;
          }

          if (res.ok) {
            setLoading(false);
            return res.json();
          }
          setTestResults({
            results: [],
            headers: []
          });
          setErrorMessage(res.status.toString());
          setLoading(false);
        })
        .catch((err: Error) => {
          setLoading(false);
          setTestResults({
            results: [],
            headers: []
          });
          setErrorMessage(err.message);
          setNothingFound(false);
        });

      if (result !== undefined) {
        setLoading(false);
        setTestResults({
          results: result.results.bindings,
          headers: result.head.vars
        });
        setErrorMessage(undefined);
        setNothingFound(result.results.bindings.length === 0);
      }
    };

    if (queryId !== '' && play) {
      setPlay(false);
      testEndpoint();
    }
  }, [queryId, play]);

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
      <Wrapper column={true}>
        <TextField
          error={checkError(name)}
          label="Name"
          id="endpointName"
          value={name || ''}
          style={marginStyle}
          onChange={e => {
            setName(e.target.value);
          }}
          {...globalTextFieldProps(true, true)}
        />
        <TextField
          error={checkError(url)}
          label="URL"
          id="endpointURLName"
          value={url || ''}
          style={marginStyle}
          onChange={e => {
            setURL(e.target.value);
          }}
          {...globalTextFieldProps(true, true)}
        />
        <Authentication>
          <Autocomplete
            {...autoProps({
              label: 'Authentication type',
              required: true,
              disableDelete: true,
              classes
            })}
            defaultValue={
              create && checkUndefinedOrNull(endpoint.authenticationMethod)
                ? {
                    value: constants.menuItems.none,
                    display: constants.menuItems.none
                  }
                : {
                    value: authenticationType,
                    display:
                      endpoint.authenticationMethod.type ===
                      constants.menuItems.tokenUnderscore
                        ? constants.menuItems.token
                        : endpoint.authenticationMethod.type
                  }
            }
            options={menuOptions([
              constants.menuItems.none,
              constants.menuItems.basic,
              constants.menuItems.tokenUnderscore
            ])}
            onChange={(event, value) => {
              setAuthenticationType(value ? value.value : value);
            }}
          />
          <AuthenticationFields
            authenticationType={authenticationType}
            show={show}
            values={authorizationValues}
            onShow={() => setShow(!show)}
            onChange={(e, id) => {
              setAuthorizationValues({
                ...authorizationValues,
                [id]: e.target.value
              });
            }}
          />
        </Authentication>
      </Wrapper>
      <TestingZoneWrapper data-testid="TestingZone">
        <Row style={{ justifyContent: 'space-between' }}>
          <Text data-testid="TestingZoneTitle">Test the endpoint: </Text>
          <Button
            onClick={() => {
              setPlay(true);
            }}
            size={'small'}
            style={editButtons}
          >
            PLAY
          </Button>
        </Row>
        <Autocomplete
          {...autoProps({
            label: 'Query',
            classes
          })}
          defaultValue={queryId}
          options={defineAutocompleteOptions(queries, 'query')}
          onChange={(event, value) => {
            setQueryId(value ? value.value : value);
            setTestResults({
              results: [],
              headers: []
            });
            setErrorMessage(undefined);
          }}
        />
        {errorMessage !== undefined ? (
          <ResultText>{errorMessage}</ResultText>
        ) : (
          undefined
        )}
        {loading ? (
          <CustomSpinner />
        ) : (
          <TestingResults
            nothing={nothingFound}
            records={testResults}
            queriesPage={false}
          />
        )}
      </TestingZoneWrapper>
    </BigWrapper>
  );
};

export const Authentication = styled.div`
  width: 100%;
`;

const ResultText = styled.span`
  font-family: Roboto, Helvetica, Arial, sans-serif;
  font-weight: 500;
  color: ${p => p.theme.colors.red};
`;
