import React from 'react';
import styled from 'styled-components';
import ReactTable, { CellInfo } from 'react-table';
import 'react-table/react-table.css';

import { colors } from '../../../utils/colors';
import { Records } from '../../../types';
import { isOverflown } from '../../../utils';
import TableCell from '../subComponents/TableCell.component';

export default ({
  records,
  nothing,
  queriesPage
}: {
  records: Records;
  nothing: boolean;
  queriesPage: boolean;
}) => {
  const data =
    records.results.length > 0 && records.headers.length > 0
      ? records.results.map(item => {
          const results = records.headers.map(head => ({
            [head]: item[head] !== undefined ? item[head].value : ''
          }));
          return { ...results.reduce((a, b) => ({ ...a, ...b }), {}) };
        })
      : [];

  const checkResults = records.results.length > 0 && records.results[0] !== {};

  const tableBodyId = document.getElementById('TableBody');

  const isBodyOverflown = !!tableBodyId && isOverflown(tableBodyId);

  return (
    <Container
      data-testid="TestingResults"
      queriesPage={queriesPage}
      recordsLength={records.results.length}
    >
      {checkResults && records.headers.length > 0 && (
        <ReactTable
          pageSize={data.length}
          showPagination={false}
          collapseOnDataChange={false}
          collapseOnSortingChange={false}
          filterable={false}
          resizable={true}
          sortable={false}
          multiSort={false}
          data={data}
          columns={records.headers.map(head => ({
            Header: () => <TableCell value={head} />,
            accessor: head,
            Cell: ({ value }: CellInfo) => <TableCell value={value} />
          }))}
          minRows={1}
          getTheadProps={() => ({
            style: {
              backgroundColor: colors.black,
              paddingRight: isBodyOverflown ? '16px' : '1px'
            }
          })}
          getTheadTrProps={() => ({
            style: {
              backgroundColor: colors.black,
              color: colors.yellow,
              marginLeft: '1px'
            }
          })}
          getProps={() => ({
            style: { border: 0, width: '100%', height: '100%' },
            'data-testid': 'TableContent'
          })}
          getTrGroupProps={() => ({
            style: {
              flex: 'unset',
              border: `1px solid ${colors.tableColor}`
            }
          })}
          getTdProps={() => ({
            style: {
              borderColor: colors.tableColor,
              padding: '5px'
            }
          })}
          getTbodyProps={() => ({
            style: {
              overflowX: 'hidden',
              position: 'relative',
              textAlign: 'center'
            },
            id: 'TableBody'
          })}
          getTheadThProps={() => ({
            style: { fontWeight: 800 }
          })}
          noDataText="No data found"
        />
      )}
      {checkResults && (
        <ResultText data-testid="Result">
          Result: Query returned {(records && records.results.length) || 0}{' '}
          records.
        </ResultText>
      )}
      {nothing && (
        <ResultText data-testid="ResultNothingFound">
          Result: Query returned no data.
        </ResultText>
      )}
    </Container>
  );
};

const ResultText = styled.span`
  font-family: Roboto, Helvetica, Arial, sans-serif;
  font-weight: 500;
  position: absolute;
  bottom: 5px;
  font-size: 1rem;
`;

const Container = styled.div<{ queriesPage: boolean; recordsLength: number }>`
  padding: 10px;
  display: flex;
  flex-direction: column;
  overflow: auto;
  height: ${p =>
    p.queriesPage ? (p.recordsLength < 6 ? 'auto' : '250px') : '100%'};
  font-size: small;
  margin-top: 10px;
  margin-bottom: 20px;
`;
