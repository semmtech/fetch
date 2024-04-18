import React from 'react';
import styled from 'styled-components';
import {
  DragDropContext,
  Droppable,
  Draggable,
  DropResult
} from 'react-beautiful-dnd';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import DragIndicator from '@material-ui/icons/DragIndicator';
import Autocomplete from '@material-ui/lab/Autocomplete';
import IconButton from '@material-ui/core/IconButton';
import DeleteIcon from '@material-ui/icons/Delete';
import _get from 'lodash/get';
import constants from '../../../constants';

import { autoProps, useStyles } from '../../../utils/autoCompleteProps';
import {
  Row,
  HeaderTitle,
  editButtons,
  Column,
  StepWrapper,
  inputStyling
} from '../../../utils/styles';
import {
  globalTextFieldProps,
  menuOptions,
  defineAutocompleteOptions,
  reorder,
  checkError
} from '../../../utils';
import { QueryFilterFields, QueryPicklists } from '../../../types';

export default ({
  filters,
  setFilters,
  addNewEmptyRow,
  updateRowData,
  deleteRowData,
  queries
}: QueryFilterFields) => {
  const classes = useStyles();

  const defaultQueryValue = (filter: QueryPicklists) =>
    _get(filter, 'query', '');

  return (
    <Wrapper>
      <Row style={{ justifyContent: 'space-between', marginBottom: '10px' }}>
        <HeaderTitle data-testid="FilterFieldsHeader">
          Filter Fields
        </HeaderTitle>
        <Button
          data-testid="Add-NewFilter"
          size={'small'}
          style={editButtons}
          onClick={addNewEmptyRow}
        >
          Add Filter Field
        </Button>
      </Row>

      <Column
        style={{
          overflow: 'auto',
          border: filters.length > 0 ? '1px solid rgba(0, 0, 0, 0.54)' : 0
        }}
      >
        <DragDropContext
          onDragEnd={({ source, destination }: DropResult) => {
            const draggedResult = reorder(
              [...filters],
              source.index,
              destination && destination.index
            );
            setFilters(draggedResult);
          }}
        >
          <Droppable droppableId="droppable">
            {provided => (
              <div ref={provided.innerRef} {...provided.droppableProps}>
                {filters.length > 0 &&
                  [...filters].map((filter, index) => {
                    const displayQueryName = queries.find(
                      query => query.id === filter.query
                    );

                    return (
                      <Draggable
                        draggableId={filter.variable}
                        key={index}
                        index={index}
                      >
                        {provided => (
                          <StepWrapper
                            index={index}
                            ref={provided.innerRef}
                            {...provided.draggableProps}
                            {...provided.dragHandleProps}
                            data-testid={`DND-QueryFilter${index}`}
                          >
                            <DragIndicator
                              data-testid="DragIcon"
                              style={{ margin: 'auto', marginLeft: '10px' }}
                            />
                            <Autocomplete
                              {...autoProps({
                                label: 'Type',
                                style: { margin: '7px 15px' },
                                required: true,
                                disableDelete: true,
                                error: false,
                                classes
                              })}
                              value={{
                                value: filter.type,
                                display: filter.type
                              }}
                              options={menuOptions([
                                constants.queryFilterTypes.text,
                                constants.queryFilterTypes.sparql
                              ])}
                              onChange={(event, value) =>
                                updateRowData(event, index, 'type', value.value)
                              }
                            />
                            <TextField
                              label="Variable"
                              id="variable"
                              error={checkError(filter.variable)}
                              value={filter.variable}
                              style={{ margin: '15px' }}
                              onChange={e =>
                                updateRowData(
                                  e,
                                  index,
                                  'variable',
                                  e.target.value
                                )
                              }
                              inputProps={{ style: inputStyling }}
                              {...globalTextFieldProps(true, true)}
                            />
                            <TextField
                              label="Label"
                              id="label"
                              error={checkError(filter.name)}
                              value={filter.name}
                              style={{ margin: '15px' }}
                              onChange={e =>
                                updateRowData(e, index, 'name', e.target.value)
                              }
                              inputProps={{ style: inputStyling }}
                              {...globalTextFieldProps(true, true)}
                            />
                            <Autocomplete
                              {...autoProps({
                                label: 'Query',
                                style: { margin: '7px 15px' },
                                classes,
                                required:
                                  filter.type ===
                                  constants.queryFilterTypes.sparql
                              })}
                              value={
                                defaultQueryValue(filter) === ''
                                  ? null
                                  : {
                                      value: defaultQueryValue(filter),
                                      display:
                                        displayQueryName !== undefined &&
                                        displayQueryName.name
                                    }
                              }
                              disabled={
                                filter.type !==
                                constants.queryFilterTypes.sparql
                              }
                              options={defineAutocompleteOptions(
                                queries,
                                'query'
                              )}
                              onChange={(event, value) =>
                                updateRowData(
                                  event,
                                  index,
                                  'query',
                                  value.value
                                )
                              }
                            />
                            <IconButton
                              edge="end"
                              onClick={() => deleteRowData(filter)}
                              aria-label="Delete"
                              style={{ margin: 'auto', marginRight: '-12px' }}
                              data-testid={`DeleteFilter-${index}`}
                            >
                              <DeleteIcon />
                            </IconButton>
                          </StepWrapper>
                        )}
                      </Draggable>
                    );
                  })}
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </DragDropContext>
      </Column>
    </Wrapper>
  );
};

const Wrapper = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-top: 10px;
  padding-bottom: 20px;
`;
