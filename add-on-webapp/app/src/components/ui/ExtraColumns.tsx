import React from 'react';
import { useGlobal } from 'reactn';
import { FaCheck } from 'react-icons/fa';

import { columnHeader } from '../../utils/styles';
import { countAllChildren } from '../../utils';
import { IColumnProps, IRowProps, ITableColumn } from '../../types';
import ExpanderCell from './subComponents/ExpanderCell.component';
import ColumnTitle from './subComponents/ColumnTitle.component';
import Cell from './subComponents/Cell.component';
import constants from '../../constants';
import { Column, CellInfo } from 'react-table';

// these are the props for the expand column
const collapseColumn = (column: ITableColumn) => ({
  expander: true,
  sortable: true,
  filterable: false,
  accessor: column.name || '',
  headerStyle: { ...columnHeader, marginLeft: 1 },
  Header: (props: { column: Column }) => (
    <ColumnTitle
      title={column.display || column.name || ''}
      columnProps={props.column}
    />
  ),
  show: column.show,
  id: constants.collapse,
  minWidth: 250,
  minResizeWidth: 250,
  Expander: ({
    isExpanded,
    original
  }: {
    isExpanded: boolean;
    original: IRowProps;
  }) => {
    const [selectedIds] = useGlobal('selectedIds');
    const [allData] = useGlobal('data');

    return (
      <ExpanderCell
        original={original}
        isExpanded={isExpanded}
        column={column}
        count={countAllChildren({
          uniqId: original.uniqId,
          parentId: original.uniqId,
          count: 0,
          allData,
          selectedIds
        })}
      />
    );
  }
});

const isImported = 'isImported';

// this is a function to turn the columns array into an array with the right props for the columns of react table
export const calcColumns = (columns: ITableColumn[]) => {
  const filteredColumns = columns.filter(column => column.show);

  const showColumns = filteredColumns.map(
    (column, index): IColumnProps => {
      // this is the first column in the table
      if (index === 0) {
        return collapseColumn(column);
      }
      // These are the normal column props
      return {
        Header: (props: { column: Column }) => (
          <ColumnTitle
            title={column.display || column.name || ''}
            columnProps={props.column}
          />
        ),
        accessor: column.name || '',
        show: column.show,
        minResizeWidth: 100,
        headerStyle: columnHeader,
        id: column.name || undefined,
        sortable: true,
        filterable: false,
        minWidth: 100,
        Cell: ({ value, original }: CellInfo) =>
          column.name === isImported ? (
            value && <FaCheck data-testid={`Import_${original.label}`} /> // this is the special cell for the import column
          ) : (
            <Cell value={value} />
          )
      };
    }
  );
  return showColumns;
};
