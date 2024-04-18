import React, { useState, useEffect } from 'react';
import { useGlobal } from 'reactn';
import { Column } from 'react-table';
import styled from 'styled-components';
import { FaArrowUp, FaArrowDown } from 'react-icons/fa';
import ArrowRight from '@material-ui/icons/ArrowRight';
import ArrowLeft from '@material-ui/icons/ArrowLeft';

import { useHover } from '../../../hooks';
import { arrowStyle, foldIcon } from '../../../utils/styles';
import { IColumnProps } from '../../../types';

export default ({
  title,
  columnProps
}: {
  title: string;
  columnProps: Column;
}) => {
  const [columns, setColumns] = useGlobal('columns');
  const [, setFoldedColumn] = useGlobal('foldedColumn');
  const [defaultWidths] = useGlobal('defaultColumnWidths');
  const [sorted] = useGlobal('newSorted');
  const [hoverRef, isHovered] = useHover(true);
  const [hoverRefText, isHoveredText] = useHover();
  const checkWidth =
    hoverRefText.current &&
    hoverRefText.current.offsetWidth < hoverRefText.current.scrollWidth;
  const sortedColumn = sorted[0];
  const checkId = sortedColumn && sortedColumn.id === columnProps.id;
  const [folded, setFolded] = useState(false);
  const [configColumn, setConfigColumn] = useState<IColumnProps>();

  useEffect(() => {
    if (!folded) {
      const foldedColumn = columns.find(column => column.id === columnProps.id);
      if (foldedColumn) {
        setConfigColumn(foldedColumn);
      }
    }
  }, [folded, columnProps.id, columns]);

  return (
    <Container ref={hoverRef}>
      <FoldWrapper
        folded={folded}
        data-testid={`Fold_${columnProps.id}`}
        onClick={e => {
          const foldedId = { id: columnProps.id, folded: !folded };
          e.stopPropagation();
          setFolded(!folded);
          setFoldedColumn(foldedId);

          const foldedColumnsConfig = columns.map((column, index) => {
            const defaultWidth = defaultWidths.find(
              item => item.id === column.id
            );
            if (columnProps.id === column.id) {
              // This if statement is for checking if column is clicked or not, if not just return the column
              if (folded) {
                // This if statement is for setting the fold props to the right column, otherwise the default config and its width is set back
                return {
                  ...configColumn,
                  width: defaultWidth ? defaultWidth.width + 2 : 200 // the purpose of the +2 => it takes the full width of the table
                };
              }
              return {
                ...column,
                width: 30,
                sortable: false,
                resizable: false,
                ...(index === 0
                  ? {
                      Expander: () => <FoldedCell>...</FoldedCell>
                    }
                  : { Cell: () => '...' })
              };
            }
            return column;
          });
          setColumns(foldedColumnsConfig);
        }}
      >
        {folded ? (
          <ArrowRight style={foldIcon} />
        ) : (
          <ArrowLeft style={foldIcon} />
        )}
      </FoldWrapper>
      <TitleWrapper
        ref={hoverRefText}
        data-tip={checkWidth && isHoveredText ? title : ''}
        sort={columnProps.sortable}
      >
        <Title>{title}</Title>
      </TitleWrapper>
      <SortDirection>
        {checkId &&
          (sortedColumn.desc ? (
            <FaArrowDown style={arrowStyle} />
          ) : (
            <FaArrowUp style={arrowStyle} />
          ))}
        {isHovered && !checkId && <FaArrowUp style={arrowStyle} />}
      </SortDirection>
    </Container>
  );
};

const Container = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
`;

const TitleWrapper = styled.span<{ sort?: boolean }>`
  width: ${p => (p.sort ? 'calc(100% - 50px)' : '100%')};
  overflow: hidden;
  text-overflow: ellipsis;
`;

const FoldWrapper = styled.div<{ folded: boolean }>`
  margin: ${p => (p.folded ? '0 5px' : '0 10px')};
  margin-right: unset;
  cursor: pointer;
`;

const Title = styled.span`
  display: inline;
`;

const FoldedCell = styled.div`
  padding: 7px 5px;
`;

const SortDirection = styled.div`
  width: 20px;
  height: 20px;
`;
