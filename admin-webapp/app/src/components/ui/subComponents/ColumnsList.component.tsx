import React from 'reactn';
import styled from 'styled-components';
import TextField from '@material-ui/core/TextField';
import Visibility from '@material-ui/icons/Visibility';
import VisibilityOff from '@material-ui/icons/VisibilityOff';
import IconButton from '@material-ui/core/IconButton';
import Chip from '@material-ui/core/Chip';
import DragIndicator from '@material-ui/icons/DragIndicator';
import {
  DragDropContext,
  Droppable,
  Draggable,
  DropResult
} from 'react-beautiful-dnd';
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';

import { colors } from '../../../utils/colors';
import { Row, Column } from '../../../utils/styles';
import { Columns, ColumnsList } from '../../../types';
import { globalTextFieldProps, reorder } from '../../../utils';
import { CSSProperties } from '@material-ui/core/styles/withStyles';

const marginAuto: CSSProperties = {
  margin: 'auto'
};

const CssTextField = withStyles({
  root: {
    '& .MuiOutlinedInput-root': {
      '& fieldset': {
        borderColor: colors.white
      },
      '&:hover fieldset': {
        borderColor: colors.lightGrey
      },
      '&.Mui-focused fieldset': {
        borderColor: colors.white
      }
    }
  }
})(TextField);

export default ({
  columns,
  updateDisplayName,
  updateVisibleStatus,
  setColumns
}: ColumnsList) => {
  const visibleColumns: Columns[] = Object.values(columns || {});
  return (
    <Wrapper amountOfColumns={visibleColumns.length}>
      <DragDropContext
        onDragEnd={({ source, destination }: DropResult) => {
          const draggedResult = reorder(
            Object.values(columns),
            source.index,
            destination && destination.index
          );

          const dataToUpdate = draggedResult.reduce((value, item) => {
            value[item['bindingName']] = item;
            return value;
          }, {});
          setColumns(dataToUpdate);
        }}
      >
        <Droppable direction="horizontal" droppableId="droppable">
          {droppableProvided => (
            <Row
              ref={droppableProvided.innerRef}
              {...droppableProvided.droppableProps}
            >
              {visibleColumns.map((column, index) => (
                <Draggable
                  draggableId={index.toString()}
                  key={column.bindingName}
                  index={index}
                >
                  {draggableProvided => (
                    <ColumnWrapper
                      index={index}
                      key={column.bindingName}
                      ref={draggableProvided.innerRef}
                      {...draggableProvided.dragHandleProps}
                      {...draggableProvided.draggableProps}
                    >
                      <Column
                        data-testid={`Column_${column.bindingName}_${index}`}
                      >
                        <CssTextField
                          value={column.displayName}
                          onChange={e =>
                            updateDisplayName(e, column.bindingName)
                          }
                          id={column.bindingName}
                          {...globalTextFieldProps(true, false, true)}
                        />
                        <Row
                          style={{
                            padding: '5px 0',
                            justifyContent: 'space-between'
                          }}
                        >
                          <DragIndicator
                            data-testid="DragIcon"
                            style={marginAuto}
                          />
                          <Chip
                            label={column.bindingName}
                            disabled={true}
                            clickable={false}
                            variant="outlined"
                            size="small"
                            style={{ margin: 'auto' }}
                            data-testid="Chip"
                          />
                          {column.visible ? (
                            <Tooltip title="Column is visible">
                              <IconButton
                                onClick={() =>
                                  updateVisibleStatus(
                                    !column.visible,
                                    column.bindingName
                                  )
                                }
                                size="small"
                                style={marginAuto}
                                data-testid="Visible"
                              >
                                <Visibility
                                  style={{
                                    color: colors.blackOpacity
                                  }}
                                />
                              </IconButton>
                            </Tooltip>
                          ) : (
                            <Tooltip title="Column is hidden">
                              <IconButton
                                onClick={() =>
                                  updateVisibleStatus(
                                    !column.visible,
                                    column.bindingName
                                  )
                                }
                                size="small"
                                style={marginAuto}
                                data-testid="VisibleOff"
                              >
                                <VisibilityOff
                                  style={{
                                    color: colors.blackOpacity
                                  }}
                                />
                              </IconButton>
                            </Tooltip>
                          )}
                        </Row>
                      </Column>
                    </ColumnWrapper>
                  )}
                </Draggable>
              ))}
              {droppableProvided.placeholder}
            </Row>
          )}
        </Droppable>
      </DragDropContext>
    </Wrapper>
  );
};

const Wrapper = styled.div<{ amountOfColumns: number }>`
  display: flex;
  flex-direction: row;
  height: fit-content;
  border: ${p =>
    p.amountOfColumns > 0 ? `1px solid ${p.theme.colors.blackOpacityDND}` : 0};
  margin: 10px 0;
`;

const ColumnWrapper = styled.div<{ index: number }>`
  flex-grow: 1;
  background-color: ${p =>
    p.index % 2 === 0 ? p.theme.colors.greyOpacity : p.theme.colors.white};
  display: flex;
  flex-direction: column;
`;
