import { useGlobal, useDispatch } from 'reactn';
import React, { useState, useEffect } from 'react';
// you can find more information about react table in here: https://www.npmjs.com/package/react-table/v/6.10.0
import ReactTable, { Column, SortingRule, TableProps } from 'react-table';
import 'react-table/react-table.css';
import styled from 'styled-components';
import ReactTooltip from 'react-tooltip';
import './table.css';

import {
  tableRow,
  tableCells,
  noBorder,
  noOverflow,
  resizerStyle
} from '../../../utils/styles';
import { hasElementOverflown, customId } from '../../../utils';
import {
  mouseEvent,
  IRowProps,
  ExpandActions,
  CellProps
} from '../../../types';
import constants from '../../../constants';
import Loader from '../subComponents/Loader.component';
import Pagination from '../subComponents/Pagination.component';
import CustomNoDataComponent from '../subComponents/CustomNoData.component';
import { colors } from '../../../utils/colors';
import { useWindowSize } from '../../../hooks';

// This is the component of the table body (react-table)
const TableBody = ({
  data,
  isRoot,
  treeNode
}: {
  data: IRowProps[];
  isRoot: boolean;
  treeNode: string;
}) => {
  const [foldedColumn] = useGlobal('foldedColumn');
  const [, setDefaultWidths] = useGlobal('defaultColumnWidths');
  const [sortedColumn, setSortedColumn] = useGlobal('newSorted');
  const [columns, setColumns] = useGlobal('columns');
  const [allData] = useGlobal('data');
  const [selectedIds, setSelectedIds] = useGlobal('selectedIds');
  const [pagination] = useGlobal('enablePagination');
  const [configurationId] = useGlobal('configurationId');

  const dataTestId = 'data-testid';
  const tableBodyId =
    document.getElementById(customId('TableBody', configurationId)) ||
    undefined;

  const [newColumns, setNewColumns] = useState<{ id: string; width: number }[]>(
    []
  );
  const [resize, setResize] = useState(false);
  const [checking, setChecking] = useState(!hasElementOverflown(tableBodyId));
  const [pageChanged, setPageChanged] = useState(false);

  const [expandedRows] = useGlobal('expandedRows');
  const dispatchExpand = useDispatch('handleExpand');
  const [, height] = useWindowSize();

  // This useEffect is for resizing the columns
  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    // This is for setting the default widths of the columns -> so when you cancel the folding of the column, it knows which width it needs to set back
    if (!foldedColumn.folded) {
      const defaultColumnWidths = columns.map(column => {
        const headerId = document.getElementById(column.id || '');
        const defaultColumn = headerId && headerId.getBoundingClientRect();

        if (defaultColumn) {
          return {
            id: column.id,
            width: defaultColumn.width
          };
        }
        return {
          id: column.id,
          width: 250
        };
      });
      setDefaultWidths(defaultColumnWidths);
    }

    setColumns([
      ...columns
        // @ts-ignore
        .concat(newColumns)
        .reduce(
          (a: any, b: any) => a.set(b.id, Object.assign(a.get(b.id) || {}, b)),
          new Map()
        )
        .values()
    ]);
    setResize(false);
  }, [resize]);

  // This is a function to handle the row click or handle the original
  const cellProps = (rowInfo: CellProps, column: Column) => ({
    onClick: (e: mouseEvent, handleOriginal: () => void) => {
      // These are for checking which element is clicked
      const { id } = e.target as HTMLButtonElement;
      const hasExpandIconClicked = [
        constants.containerExpander,
        constants.iconExpand,
        constants.iconExpandWrapper
      ].includes(id);

      if (id.length === 0) {
        return;
      }

      // When a user clicks on the expand icon, it is gonna expand so you can see the children -> that is what the handleOriginal is gonna do
      if (hasExpandIconClicked) {
        handleOriginal();
        return;
      }

      selectedIds.some(id => id === rowInfo.original.uniqId)
        ? setSelectedIds(
            selectedIds.filter(id => id !== rowInfo.original.uniqId)
          )
        : setSelectedIds(selectedIds.concat(rowInfo.original.uniqId));
    },
    style: tableCells(column.id || ''),
    [dataTestId]: constants.tableCell,
    id:
      column.id === constants.collapse
        ? constants.expandCell
        : constants.tableCell
  });

  const sortingMethod = (newSorted: SortingRule[]) => {
    const sorted = newSorted.length > 0 ? newSorted[0] : undefined;
    const accessor = columns[0].accessor;
    if (!!sorted && sorted.id === constants.collapse && !!accessor) {
      data.sort((a, b) => {
        const itemA = a[accessor];
        const itemB = b[accessor];
        return itemA < itemB ? -1 : itemA > itemB ? 1 : 0;
      });
    }
    setSortedColumn(newSorted);
    dispatchExpand({ type: ExpandActions.resetExpand });
  };

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    setChecking(hasElementOverflown(tableBodyId));
    setPageChanged(false);
    ReactTooltip.rebuild();
  }, [tableBodyId, pageChanged, height, foldedColumn]);

  const getNoDataProps = (props: TableProps) => ({
    loading: props.loading
  });

  return (
    <Wrapper data-testid="TableBody" root={isRoot}>
      <ReactTooltip
        multiline={true}
        className="extraClass"
        overridePosition={(
          { left, top },
          currentEvent,
          currentTarget,
          node
        ) => {
          const d = document.documentElement;

          left = Math.min(d.clientWidth - (node ? node.clientWidth : 0), left);
          top = Math.min(d.clientHeight - (node ? node.clientHeight : 0), top);

          left = Math.max(10, left);
          top = Math.max(10, top);

          return { top, left };
        }}
      />
      <ReactTable
        {...(isRoot
          ? {
              ...(pagination && { defaultPageSize: 10 }),
              ...(!pagination && { pageSize: data.length }),
              getTbodyProps: () => ({
                style: {
                  maxHeight: pagination
                    ? `${height - 195}px` // (rounded on 185 -> 40 (padding) + 68 (header) + 43 (pagination) + 31 (for the column headers) + 10 (extra) = 192)
                    : `${height - 155}px`, // (rounded on 145 -> 40 (padding) + 68 (header) + 31 (for the column headers) + 10 (extra) = 149)
                  minHeight: '38px', // +- the height of one row
                  overflowX: 'hidden'
                },
                id: customId('TableBody', configurationId)
              })
            }
          : {
              TheadComponent: () => null,
              defaultPageSize: 1000,
              getTbodyProps: () => ({
                style: {
                  minHeight: '30px', // +- the height of one row
                  overflow: 'unset'
                }
              })
            })} // This is for having a children table with no headers / There is a default page size for the roots
        showPagination={pagination && isRoot}
        getNoDataProps={getNoDataProps}
        PaginationComponent={Pagination}
        collapseOnDataChange={false}
        collapseOnSortingChange={true}
        pageSizeOptions={[1, 5, 10, 20, 50, 100, 1000]}
        filterable={false}
        resizable={true}
        sortable={true}
        multiSort={false}
        SubComponent={({
          // children component
          original
        }: {
          original: IRowProps;
        }) =>
          original.children.length > 0 ? (
            <TableBody
              data={original.children.map(uri => allData[uri])}
              isRoot={false}
              treeNode={original.treeNodeId}
            />
          ) : original.hasChildren ? (
            <Loader loading={true} />
          ) : undefined
        }
        className="-highlight"
        data={data}
        columns={columns}
        NoDataComponent={CustomNoDataComponent}
        minRows={1} // controls the minimum number of rows to display - default will be `pageSize`
        getResizerProps={() => ({
          style: resizerStyle
        })}
        getTrProps={(state: any, rowInfo: any) => ({
          // This is for making it visible when a row is clicked
          style: tableRow(
            selectedIds.some(id =>
              rowInfo && rowInfo.original
                ? id === rowInfo.original.uniqId
                : false
            )
          ),
          [dataTestId]: `TableRow_${
            rowInfo && rowInfo.original.uri.replace(/.*\//, '')
          }`
        })}
        getTheadThProps={(state, rowInfo, column) => ({
          [dataTestId]: `TableHeader_${(column && column.id) || ''}`,
          id: (column && column.id) || ''
        })}
        getTheadTrProps={() => ({
          style: {
            backgroundColor: colors.darkGrey,
            ...(checking && {
              paddingRight: '10px'
            })
          }
        })}
        getTdProps={(state: any, rowInfo: any, column: any) =>
          cellProps(rowInfo, column)
        }
        getTableProps={() => (isRoot ? {} : { style: noOverflow })}
        getProps={() => ({ style: noBorder })}
        getTrGroupProps={() => ({ style: noBorder })}
        expanderDefaults={{
          sortable: true,
          resizable: true,
          filterable: false
        }}
        resized={
          foldedColumn.folded
            ? []
            : newColumns.map(({ width: value, ...rest }) => ({
                value,
                ...rest
              })) // I need to add the empty array if a column is folded, because there was a bug with the resizing that it takes not the previous width
        }
        onResizedChange={(newResized, event) => {
          setNewColumns(
            newResized.map(({ value: width, ...rest }) => ({
              width,
              ...rest
            }))
          );
          setResize(true);
        }}
        sorted={sortedColumn}
        expanded={expandedRows[`${treeNode}`]}
        onSortedChange={sortingMethod}
        onExpandedChange={(newExpanded, index) => {
          dispatchExpand({
            type: ExpandActions.handleExpand,
            payload: {
              treeNode,
              newExpanded,
              index: index[0]
            }
          });
        }}
        onPageSizeChange={() => setPageChanged(true)}
      />
    </Wrapper>
  );
};

export default TableBody;

const Wrapper = styled.div<{ root: boolean }>`
  text-align: center;
  border-radius: 0 0 10px 10px;
  box-shadow: ${p =>
    p.root
      ? '0px 1px 5px 0px rgba(0, 0, 0, 0.2), 0px 2px 2px 0px rgba(0, 0, 0, 0.14), 0px 3px 1px -2px rgba(0, 0, 0, 0.12)'
      : 'unset'};
`;
