import React, { useDispatch } from 'reactn';
import { useState } from 'react';
import styled from 'styled-components';
import Fab from '@material-ui/core/Fab';
import MoreVert from '@material-ui/icons/MoreVert';

import Expander from './Expander.component';
import Menu from './Menu.component';
import { useHover } from '../../../hooks';
import {
  IRowProps,
  ITableColumn,
  SelectActions,
  ExpandActions
} from '../../../types';
import constants from '../../../constants';

export default ({
  original,
  isExpanded,
  column,
  count
}: {
  isExpanded: boolean;
  original: IRowProps;
  column: ITableColumn;
  count?: number;
}) => {
  const [hoverRef, isHovered] = useHover();
  const checkWidth =
    hoverRef.current &&
    hoverRef.current.offsetWidth < hoverRef.current.scrollWidth;
  const textValue = column.name && original[column.name];
  const dispatchSelect = useDispatch('handleSelect');
  const dispatchExpand = useDispatch('handleExpand');
  const startImport = useDispatch('import');
  const [anchorEl, setAnchorEl] = useState<HTMLElement | undefined>(undefined);

  const menuItems = [
    {
      title: 'Expand all',
      action: () => {
        dispatchExpand({
          type: ExpandActions.expandAll,
          payload: { item: original }
        });
      }
    },
    {
      title: 'Select all',
      action: () => {
        dispatchSelect({
          type: SelectActions.selectAll,
          payload: { item: original }
        });
      }
    },
    {
      title: 'Unselect all',
      action: () => {
        dispatchSelect({
          type: SelectActions.unselectAll,
          payload: { item: original }
        });
      }
    },
    {
      title: 'Import all',
      action: async () => {
        await dispatchSelect({
          type: SelectActions.selectAll,
          payload: { item: original }
        });
        await startImport(original.treeNodeId);
      }
    }
  ];

  return (
    <Wrapper
      level={original.level}
      {...(original.hasChildren && { id: constants.containerExpander })}
      data-tip={checkWidth && isHovered ? textValue || '' : ''}
    >
      {original.hasChildren ? (
        <Expander
          isExpanded={isExpanded}
          origin={original}
          level={original.level}
        />
      ) : undefined}
      <Text
        ref={hoverRef}
        id={constants.expanderText}
        checkChildren={original.hasChildren}
      >
        {textValue || ''}
        <AmountChildren
          data-testid={`AmountChildren_${textValue}`}
          id={constants.countChildren}
        >
          {!isExpanded && original.children.length > 0 && count !== 0
            ? `  (+${count} children)`
            : ''}
        </AmountChildren>
      </Text>
      {original.hasChildren ? (
        <Fab
          id={constants.menuButton}
          data-testid={`${original.treeNodeId}_${constants.menuButton}`}
          aria-controls={constants.cellMenu}
          style={{
            width: '30px',
            height: '30px',
            backgroundColor: 'unset',
            boxShadow: 'unset'
          }}
          aria-haspopup="true"
          onClick={e => {
            e.stopPropagation();
            setAnchorEl(e.currentTarget);
          }}
        >
          <MoreVert style={{ color: '#4c4c4c', fontSize: '20px' }} />
        </Fab>
      ) : undefined}
      <Menu
        id={constants.cellMenu}
        anchorEl={anchorEl}
        closeMenu={() => setAnchorEl(undefined)}
        menuItems={menuItems}
      />
    </Wrapper>
  );
};

const Wrapper = styled.div<{ level: number }>`
  margin-left: ${p => `${p.level * 20}px`};
  display: flex;
  flex-direction: row;
  height: 100%;
`;

const Text = styled.span<{ checkChildren: boolean }>`
  margin-left: ${p => (p.checkChildren ? '0px' : '23px')};
  width: 100%;
  padding: 10px;
  height: 100%;
  white-space: nowrap;
  overflow: hidden !important;
  text-overflow: ellipsis;
`;

const AmountChildren = styled.span`
  font-weight: bold;
  color: ${p => p.theme.colors.black};
  padding-left: 10px;
`;
