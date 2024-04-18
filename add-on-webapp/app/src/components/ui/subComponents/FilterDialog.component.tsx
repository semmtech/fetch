import { useGlobal, useDispatch } from 'reactn';
import React from 'react';
import styled from 'styled-components';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';

import { FilterActions, FilterTypes } from '../../../types';
import { TextField, Autocomplete, Button } from './CustomComponents.components';

export default () => {
  const [filters] = useGlobal('filters');
  const dispatch = useDispatch('handleFilter');

  const [updatedFilterValues, setUpdatedFilterValues] = React.useState({});

  const handleSubmit = (e: React.FormEvent | React.KeyboardEvent) => {
    e.preventDefault();
    dispatch({
      type: FilterActions.setActive,
      payload: updatedFilterValues
    });
  };

  return (
    <Dialog
      fullWidth={true}
      open={filters.isFilterDialogOpen}
      onClose={() => {
        dispatch({ type: FilterActions.close });
      }}
    >
      <DialogTitle data-testid="FilterTitle">Filter options</DialogTitle>
      <DialogContent
        style={{
          overflowY: 'unset'
        }}
      >
        <DialogForm id="filter-add-on" onSubmit={handleSubmit}>
          {Object.values(filters.rootFilters).map(filter => {
            const pickedFilterQuery =
              filters.picklist.find(
                queryFilter => queryFilter.value === filter.value
              ) || undefined;

            if (filter.type === FilterTypes.literal) {
              return (
                <TextField
                  key={filter.id}
                  label={filter.name}
                  id={`Filter_${filter.id}`}
                  defaultValue={filter.value}
                  onChange={e => {
                    setUpdatedFilterValues({
                      ...updatedFilterValues,
                      [filter.id]: e.target.value
                    });
                  }}
                  onKeyPress={e => {
                    if (e.key === 'Enter') {
                      handleSubmit(e);
                    }
                  }}
                />
              );
            }
            return (
              <Autocomplete
                key={filter.id}
                id="SPARQL filter"
                label={filter.name}
                loading={filters.loadingPicklist}
                defaultValue={pickedFilterQuery}
                onOpen={() => {
                  dispatch({
                    type: FilterActions.picklistValues,
                    payload: { queryId: filter.query }
                  });
                }}
                options={filters.picklist}
                onChange={(event, value) => {
                  setUpdatedFilterValues({
                    ...updatedFilterValues,
                    [filter.id]: value?.value
                  });
                }}
              />
            );
          })}
        </DialogForm>
      </DialogContent>
      <DialogActions
        style={{
          justifyContent: 'center',
          paddingBottom: '16px'
        }}
      >
        <Button
          data-testid="AddFilterButton"
          type="submit"
          form="filter-add-on"
          id="FilterDialog_Apply"
        >
          Apply Filters
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
