import React, { useGlobal } from 'reactn';
import { useState } from 'react';
import styled from 'styled-components';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';

import { autoProps, useStyles } from '../../../utils/autoCompleteProps';
import { IUnknownObject } from '../../../types';
import {
  booleantype,
  objectType,
  defaultStateError,
  defaultDates,
  globalTextFieldProps
} from '../../../utils';
import { dataKeysType } from '../../../global';
import constants from '../../../constants';
import DateFields from './DateFields.component';
import { itemTypes } from '../../../utils/itemTypes';

export default ({
  instance,
  type
}: {
  instance: IUnknownObject;
  type: dataKeysType;
}) => {
  const [filterId, setFilterId] = useState('');
  const [subFilterId, setSubFilterId] = useState('');
  const [filterValue, setFilterValue] = useState('');
  const [filterValueDate, setFilterValueDate] = useState(defaultDates);
  const [error, setError] = useState(defaultStateError);
  const [showDateError, setShowDateError] = useState(false);

  const [dialog, setDialog] = useGlobal('openFilterDialog');
  const [allGlobalFilters, setAllGlobalFilters] = useGlobal('globalFilters');

  const jsonApi = 'jsonApi'; // prop key of config
  const sparqlquery = 'sparqlQuery'; 

  // Gonna remove the workspace, sparqlqueries and jsonApi prop of the configuration and add them to the configFilterIds
  // -> so it is possible to filter on the workspace or the jsonApi key when filtering configurations
  const filterIds = instance
    ? Object.keys(instance).filter(
        item =>
          ![
            'filterFields',
            'password',
            'privateKey',
            'visualization',
            'importSteps',
            'authenticationMethod',
            'targetDataSystems',
            'endpoints',
            jsonApi,
            constants.workspace,
            sparqlquery
          ].includes(item) // These props of the item are filtered out so it is not possible to filter the configurations on these keys
      )
    : [];

  const configFilterIds = filterIds.concat([sparqlquery, jsonApi, constants.workspace]);

  const subFilterIds = (item: string) => {
    if (typeof instance[item] === objectType || item === jsonApi || item === sparqlquery) {
      switch (item) {
        case constants.workspace:
          return ['id', 'name', 'environmentId', 'workspaceId'];
        case jsonApi:
          return ['id', 'name', 'serviceUrl'];
        case 'sparqlEndpoint':
          return ['id', 'name', 'url'];
          case sparqlquery:
          return ['name','description', 'type'];
        default:
          return [];
      }
    }
    return [];
  };

  const clearFilter = () => {
    setFilterId('');
    setFilterValue('');
    setSubFilterId('');
    setFilterValueDate({ start: 0, end: 0 });
    setDialog(false);
  };

  const checkFilterDate = filterId.includes('Date');

  const enterKey = 'Enter';

  const addFilter = () => {
    const onlyOneActive = [
      'id',
      'environmentId',
      'applicationId',
      'workspaceId'
    ];
    const checkFilterIdExist = onlyOneActive.includes(filterId);
    const filter = allGlobalFilters[type].find(
      filter => filter.mainId === filterId
    );
    const defaultFilter = () => {
      clearFilter();
      setError(defaultStateError);
      setShowDateError(false);
    };

    if (filterId && !checkFilterDate && filterValue.trim().length === 0) {
      setFilterValue('');
      setError({
        ...error,
        value: true
      });
      setShowDateError(false);
    } else if (
      (filterId && (filterValueDate.start && filterValueDate.end)) ||
      filterValue
    ) {
      if (checkFilterIdExist && !!filter) {
        const updatedFilters = allGlobalFilters[type].filter(item => {
          if (onlyOneActive.includes(item.mainId) && filterId === item.mainId) {
            item.value = filterValue.replace(/\s+/g, ' ').trim();
          }
          return item;
        });

        setAllGlobalFilters({
          ...allGlobalFilters,
          [type]: updatedFilters
        });
        defaultFilter();
      } else {
        setAllGlobalFilters({
          ...allGlobalFilters,
          [type]: allGlobalFilters[type].concat({
            mainId: filterId,
            subId: subFilterId,
            value: checkFilterDate
              ? filterValueDate
              : filterValue.replace(/\s+/g, ' ').trim()
          })
        });
        defaultFilter();
      }
    } else if (
      (filterValueDate.start === 0 || filterValueDate.end === 0) &&
      checkFilterDate
    ) {
      setShowDateError(true);
    } else {
      setError({
        mainId: filterId ? false : true,
        subId:
          [constants.workspace, 'sparqlEndpoint'].includes(filterId) &&
          subFilterId
            ? false
            : true,
        value: !checkFilterDate ? (filterValue ? false : true) : false
      });
      setShowDateError(false);
    }
  };

  const classes = useStyles();

  return (
    <Dialog
      disableBackdropClick={true}
      disableEscapeKeyDown={true}
      fullWidth={true}
      open={dialog}
      onClose={() => setDialog(false)}
    >
      <DialogTitle data-testid="FilterTitle">Filter options</DialogTitle>
      <DialogContent>
        <DialogForm>
          {/* This displays main filter dropdown */}
          <Autocomplete
            {...autoProps({
              label: 'Main filter id',
              error: error.mainId,
              disableDelete: true,
              classes
            })}
            options={
              type === itemTypes.configurations
                ? configFilterIds.map(item => ({
                    value: item,
                    display: item
                  }))
                : filterIds.map(item => ({ value: item, display: item }))
            }
            onChange={(event, value) => {
              const id = value ? value.value : value;
              const checkId = filterId === id;
              setFilterId(id);
              setError({ ...error, mainId: false });
              setFilterValueDate({
                start: checkId ? filterValueDate.start : 0,
                end: checkId ? filterValueDate.end : 0
              });
              setFilterValue(checkId ? filterValue : '');
              setSubFilterId(checkId ? subFilterId : '');
            }}
          />
          {/* This displays sub filter dropdown */}
          {filterId &&
          [
            constants.workspace,
            jsonApi,
            'sparqlEndpoint',
            'importSteps',
            sparqlquery
          ].includes(filterId) ? (
            <Autocomplete
              {...autoProps({
                label: 'Sub filter id',
                error: error.subId,
                disableDelete: true,
                classes
              })}
              options={subFilterIds(filterId).map(item => ({
                value: item,
                display: item
              }))}
              onChange={(event, value) => {
                const pickedValue = value ? value.value : value;
                setSubFilterId(pickedValue);
                setError({ ...error, subId: false });
                setFilterValueDate({
                  start: 0,
                  end: 0
                });
                setFilterValue(subFilterId === pickedValue ? filterValue : '');
              }}
            />
          ) : (
            undefined
          )}
           {/* This displays filter value dropdown for boolean/date or text type */}
          {(filterId && typeof instance[filterId] === booleantype) ||
          (subFilterId &&
            filterId &&
            !!instance[filterId] &&
            typeof instance[filterId][subFilterId] === booleantype) ? (
            <Autocomplete
              {...autoProps({
                label: 'Filter value',
                error: error.value,
                disableDelete: true,
                classes
              })}
              options={[
                { value: 'false', display: 'Disabled' },
                { value: 'true', display: 'Enabled' }
              ]}
              onChange={(event, value) => {
                const pickedValue = value ? value.value : value;
                setFilterValue(pickedValue);
                setError({ ...error, value: false });
              }}
            />
          ) : checkFilterDate ? 
          (          
            <DateFields
              testIds={{ start: 'FilterStart', end: 'FilterEnd' }}
              labels={{ start: 'Start', end: 'End' }}
              values={{
                start:
                  filterValueDate.start === 0 ? null : filterValueDate.start,
                end: filterValueDate.end === 0 ? null : filterValueDate.end
              }}
              onChangeStart={date => {
                setFilterValueDate({
                  ...filterValueDate,
                  start: Date.parse(date !== null ? date.toString() : '0')
                });

                setShowDateError(
                  date !== null ? isNaN(Date.parse(date.toString())) : true
                );
              }}
              onChangeEnd={date => {
                setFilterValueDate({
                  ...filterValueDate,
                  end: Date.parse(date !== null ? date.toString() : '0')
                });

                setShowDateError(
                  date !== null ? isNaN(Date.parse(date.toString())) : true
                );
              }}
              extraStyle={true}
              configPage={false}
              keyPress={e => {
                if (e.key === enterKey) {
                  e.preventDefault();
                  addFilter();
                }
              }}
            />
          ) : (            
            <TextField
              id="value"
              label="Filter value"
              error={error.value}
              onChange={e => {
                setFilterValue(e.target.value);
                setError({ ...error, value: false });
              }}
              onKeyPress={e => {
                if (e.key === enterKey) {
                  e.preventDefault();
                  addFilter();
                }
              }}
              value={filterValue}
              {...globalTextFieldProps(true, false)}
            />
          )}
          {showDateError ? (
            <ResultText>Please pick some dates</ResultText>
          ) : (
            undefined
          )}
        </DialogForm>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={() => {
            clearFilter();
            setError(defaultStateError);
          }}
          data-testid="FilterCancelButton"
        >
          Cancel
        </Button>
        <Button data-testid="FilterAddButton" onClick={() => addFilter()}>
          Add
        </Button>
      </DialogActions>
    </Dialog>
  );
};

const DialogForm = styled.form`
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
`;

const ResultText = styled.span`
  font-family: Roboto, Helvetica, Arial, sans-serif;
  font-weight: 500;
  color: ${p => p.theme.colors.red};
  margin-top: 10px;
  align-self: center;
`;
