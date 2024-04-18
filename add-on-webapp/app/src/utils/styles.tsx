import constants from '../constants';
import { colors } from './colors';

const borderProp = 'solid 1px rgba(0,0,0,0.05)';

// helper object for the style of column header
export const columnHeader = {
  color: colors.white,
  textTransform: 'capitalize',
  fontWeight: 'bold',
  textAlign: 'left',
  fontSize: 'medium',
  boxShadow: 'unset',
  backgroundColor: colors.darkGrey,
  paddingLeft: 'unset'
};

// helper function for the style of clicked table row
export const tableRow = (checkClickedRow: boolean) => ({
  color: colors.black,
  cursor: 'pointer',
  borderLeft: borderProp,
  ...(checkClickedRow
    ? { fontWeight: 'bold', backgroundColor: colors.silver }
    : {})
});

// helper object for the style of table cells
export const tableCells = (columnId: string | number) => ({
  textAlign: 'left',
  border: borderProp,
  borderLeft: '0',
  ...(columnId === constants.collapse ? { padding: '0px' } : {})
});

// helper object for the style of button
export const buttonStyle = (disabled: boolean) => ({
  color: colors.blackOpacity,
  opacity: disabled ? 0.5 : 1,
  width: '52.8px',
  height: '45.8px'
});

// helper object for the style of icon
export const iconStyle = {
  marginLeft: 10
};

// helper object for the style of paper
export const paperStyle = {
  padding: '2px 4px',
  display: 'flex',
  alignItems: 'center',
  width: 300,
  marginTop: 5,
  marginLeft: 5,
  height: 33
};

// helper object for having no default overflow
export const noOverflow = {
  overflow: 'unset'
};

// helper object for having no default border
export const noBorder = {
  border: 0
};

// helper object for the style of the sort direction
export const arrowStyle = {
  marginTop: '3px'
};

// helper object for the style of the fold direction
export const foldIcon = {
  fontSize: 'x-large'
};

// helper function for the style of the action buttons in the alerts
export const alertIcons = (isHovered: boolean) => ({
  color: colors.white,
  fontSize: isHovered ? '20px' : 'medium'
});

// helper function for the style of the pagination buttons
export const paginationButton = (disabled: boolean) => ({
  marginLeft: '10px',
  width: '30px',
  height: '30px',
  opacity: disabled ? '1' : '0.3',
  color: colors.blackOpacity,
  padding: 'unset'
});

// helper object for the style of the resize icon in the column headers
export const resizerStyle = { width: '20px', right: '-10px' };
