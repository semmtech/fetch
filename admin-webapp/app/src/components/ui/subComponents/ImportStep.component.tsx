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
import DeleteIcon from '@material-ui/icons/Delete';
import DragIndicator from '@material-ui/icons/DragIndicator';
import IconButton from '@material-ui/core/IconButton';
import _get from 'lodash/get';
import Autocomplete from '@material-ui/lab/Autocomplete';

import { autoProps, useStyles } from '../../../utils/autoCompleteProps';
import { ImportStep } from '../../../types';
import {
  Row,
  HeaderTitle,
  editButtons,
  Column,
  StepWrapper,
  inputStyling
} from '../../../utils/styles';
import {
  checkError,
  defineAutocompleteOptions,
  globalTextFieldProps,
  reorder
} from '../../../utils';
import { colors } from '../../../utils/colors';
import constants from '../../../constants';

export default ({
  steps,
  addNewEmptyRow,
  deleteRowData,
  queries,
  targetSystems,
  isRelatics,
  updateRowData,
  setImportSteps
}: ImportStep) => {
  const classes = useStyles();

  return (
    <Wrapper>
      <Row style={{ justifyContent: 'space-between', marginBottom: '10px' }}>
        <HeaderTitle
          {...(steps.length === 0 && { style: { color: colors.errorRed } })}
        >
          Import steps
        </HeaderTitle>
        <Button
          data-testid="Add_Step"
          size={'small'}
          style={editButtons}
          onClick={addNewEmptyRow}
        >
          Add
        </Button>
      </Row>

      <Column
        style={{
          overflow: 'auto',
          border: steps.length > 0 ? `1px solid ${colors.blackOpacityDND}` : 0
        }}
      >
        <DragDropContext
          onDragEnd={({ source, destination }: DropResult) => {
            const draggedResult = reorder(
              [...steps],
              source.index,
              destination && destination.index
            );
            setImportSteps(draggedResult);
          }}
        >
          <Droppable droppableId="droppable">
            {provided => (
              <div ref={provided.innerRef} {...provided.droppableProps}>
                {[steps].length > 0 &&
                  steps.map((item, index) => (
                    <Draggable
                      draggableId={index.toString()}
                      key={index}
                      index={index}
                    >
                      {provided => (
                        <StepWrapper
                          index={index}
                          ref={provided.innerRef}
                          {...provided.draggableProps}
                          {...provided.dragHandleProps}
                          data-testid={`DND-Step${index}`}
                        >
                          <DragIndicator
                            style={{ margin: 'auto', marginLeft: '10px' }}
                          />
                          <TextField
                            label="Name"
                            value={item.name || ''}
                            style={{ margin: '15px' }}
                            onChange={e =>
                              updateRowData(e, index, 'name', e.target.value)
                            }
                            id={`StepName_${index}`}
                            inputProps={{ style: inputStyling }}
                            error={checkError(item.name)}
                            {...globalTextFieldProps(true, false)}
                          />
                          <Autocomplete
                            {...autoProps({
                              label: 'Query',
                              style: { margin: '7px 15px' },
                              error:
                                item.sparqlQuery &&
                                item.sparqlQuery.query === null,
                              classes
                            })}
                            value={
                              _get(item, 'sparqlQuery.query.id', '') === ''
                                ? null
                                : {
                                    value: _get(
                                      item,
                                      'sparqlQuery.query.id',
                                      ''
                                    ),
                                    display: _get(
                                      item,
                                      'sparqlQuery.query.name',
                                      ''
                                    )
                                  }
                            }
                            options={defineAutocompleteOptions(
                              queries,
                              'query'
                            )}
                            onChange={(event, value) => {
                              const pickedValue = value ? value.value : value;
                              const updatedQuery =
                                queries.find(
                                  query => query.id === pickedValue
                                ) || null;
                              updateRowData(
                                event,
                                index,
                                'sparqlQuery',
                                updatedQuery,
                                'query'
                              );
                            }}
                          />
                          <TextField
                            label="Default Graphs"
                            value={
                              item.sparqlQuery
                                ? (item.sparqlQuery.defaultGraphs || []).join()
                                : ''
                            }
                            style={{ margin: '15px' }}
                            onChange={e =>
                              updateRowData(
                                e,
                                index,
                                'sparqlQuery',
                                e.target.value,
                                'defaultGraphs'
                              )
                            }
                            id={`DefaultGraphs_${index}`}
                            inputProps={{ style: inputStyling }}
                            placeholder={constants.placeHolderGraphs}
                            {...globalTextFieldProps(true, false)}
                          />
                          <Autocomplete
                            {...autoProps({
                              label: 'Target',
                              style: { margin: '7px 15px' },
                              error: item.importTarget === null,
                              classes
                            })}
                            value={
                              _get(item, 'importTarget', null) === null
                                ? null
                                : {
                                    value: _get(item, 'importTarget.id', ''),
                                    display: _get(
                                      item,
                                      isRelatics
                                        ? 'importTarget.operationName'
                                        : 'importTarget.name',
                                      ''
                                    )
                                  }
                            }
                            options={defineAutocompleteOptions(
                              targetSystems,
                              'importTarget',
                              isRelatics
                            )}
                            onChange={(event, value) => {
                              const pickedValue = value ? value.value : value;
                              const importTarget =
                                targetSystems.find(
                                  target => target.id === pickedValue
                                ) || null;

                              updateRowData(
                                event,
                                index,
                                'importTarget',
                                importTarget
                              );
                            }}
                          />
                          <IconButton
                            edge="end"
                            onClick={() => deleteRowData(item)}
                            aria-label="Delete"
                            style={{ margin: 'auto', marginRight: '-12px' }}
                          >
                            <DeleteIcon />
                          </IconButton>
                        </StepWrapper>
                      )}
                    </Draggable>
                  ))}
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
`;
