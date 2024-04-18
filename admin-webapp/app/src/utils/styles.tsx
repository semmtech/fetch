import { CSSProperties } from 'react';
import styled from 'styled-components';

import { colors } from './colors';
import { GridList } from '../types';

// helper object for the style of all the textfields
export const inputStyling: CSSProperties = {
  overflow: 'hidden',
  textOverflow: 'ellipsis'
};

// helper object for the style of the selects in the importStep component
export const stepSelects: CSSProperties = {
  width: '100%',
  display: 'flex',
  margin: '15px'
};

// helper object for the style of the datepickers
export const datePickerInputStyle: CSSProperties = {
  paddingTop: '10.5px',
  paddingBottom: '10.5px'
};

// helper object for the style of the appbar
export const appBarStyle = (showMenu: boolean): CSSProperties => ({
  zIndex: 1299, // needs to be < 1300 -> SelectField = 1300
  backgroundColor: colors.black,
  color: colors.yellow,
  width: '100%',
  transitionProperty: 'width, margin',
  transitionDuration: showMenu ? '195ms, 195ms' : '225ms, 225ms',
  transitionTimingFunction:
    'cubic-bezier(0.4, 0, 0.6, 1), cubic-bezier(0.4, 0, 0.6, 1)',
  transitionDelay: '0ms, 0ms'
});


// helper object for the style of the input icon
export const inputIcon: CSSProperties = {
  color: colors.black,
  cursor: 'pointer'
};

// helper object for the style of the grid tile
export const tileStyle: CSSProperties = {
  height: 'auto'
};

// helper object for the style of paper
export const paperStyle: CSSProperties = {
  padding: '2px 4px',
  display: 'flex',
  alignItems: 'center',
  width: 300,
  marginTop: 5,
  marginLeft: 5,
  height: 33
};

// helper object for the style of the grid list
export const gridListStyle: CSSProperties = {
  margin: 0,
  marginRight: '5px',
  padding: '5px'
};

// helper object for the style of the card
export const cardStyle: CSSProperties = {
  border: '1px solid rgb(76, 76, 76, 0.3)',
  display: 'flex',
  flexDirection: 'column',
  borderRadius: '15px',
  boxShadow: 'unset'
};

// helper object for the style of the 3 dots
export const threeDots: CSSProperties = {
  width: '30px',
  height: '30px',
  backgroundColor: 'unset',
  boxShadow: 'unset',
  marginRight: '-10px'
};

// helper object for icon color black
export const iconColor: CSSProperties = {
  color: colors.black
};

// helper object for the style of the main menu item
export const mainMenuItem: CSSProperties = {
  textDecoration: 'none',
  ...iconColor
};

// helper object for the style of the selected item in the main menu
export const selectedItem = (selected: boolean): CSSProperties => ({
  // backgroundColor: selected ? colors.black : undefined,
  backgroundColor: selected ? colors.yellowOpacity : undefined
});

// helper object for the margin top
export const marginTopStyle = (value: number): CSSProperties => ({
  marginTop: value
});

export const chipStyle: CSSProperties = {
  backgroundColor: colors.black,
  color: colors.yellow,
  margin: '5px'
};

// helper object for the style of the finish and cancel buttons
export const editButtons: CSSProperties = {
  color: colors.yellow,
  backgroundColor: colors.black,
  marginRight: '10px'
};

// helper object for the style of the buttons in the overview pages
export const overviewButtons = (filter: boolean): CSSProperties => ({
  color: filter ? colors.green : colors.black,
  width: '20px',
  height: '20px'
});

export const ContainerTab = styled.div`
  font-size: 20px;
  display: flex;
  flex-direction: column;
  padding: 0 10px 20px 10px;
  align-items: center;
  height: 100%;
`;

export const ContainerOverview = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  padding: 0 10px 20px 10px;
  overflow-y: auto;
`;

export const HeaderTitle = styled.span`
  font-family: Roboto, Helvetica, Arial, sans-serif;
  font-weight: 500;
  display: flex;
  flex-shrink: 0;
  margin-right: 10px;
`;

export const Row = styled.div`
  width: 100%;
  display: flex;
  flex-direction: row;
`;

export const Column = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
`;

export const Text = styled.span`
  font-family: Roboto, Helvetica, Arial, sans-serif;
  font-weight: 500;
  align-self: center;
`;

export const Wrapper = styled.div<{ column?: boolean }>`
  display: flex;
  flex-direction: ${p => (p.column ? 'column' : 'row')};
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
`;

export const BigWrapper = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  padding: 10px;
  height: 100%;
`;

export const TestingZoneWrapper = styled.div`
  padding: 10px;
  flex-grow: 1;
  position: relative;
  display: flex;
  flex-direction: column;
  border: ${p => `1px solid ${p.theme.colors.blackOpacity}`};
  border-radius: 4px;
  margin-top: 10px;
`;

export const ConfigurationsPageWrapper = styled.div`
  width: 100%;
  padding: 10px;
  flex-grow: 1;
`;

export const ConfigurationColumn = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
`;

export const ConfigVisualize = styled.div`
  display: flex;
  flex-direction: row;
`;

export const ConfigRow = styled.div<{ grow: number }>`
  display: flex;
  flex-direction: column;
  margin-right: 10px;
  flex-grow: ${p => p.grow};
`;

export const ConfigPage = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
`;

export const ConfigContent = styled.div`
  display: flex;
  flex-direction: column;
  padding: 5px;
  flex-grow: 1;
`;

export const StepWrapper = styled.form<{ index: number }>`
  width: 100%;
  display: flex;
  padding-right: 15px;
  flex-direction: row;
  background-color: ${p =>
    p.index % 2 === 0 ? p.theme.colors.greyOpacity : p.theme.colors.white};
`;

export const FormWrapper = styled.form`
  display: flex;
  flex-direction: row;
  width: 100%;
`;

export const CustomGridList = styled.div<GridList>`
  display: grid;
  padding: 10px;
  grid-auto-flow: column;
  overflow-y: auto;
  grid-gap: 10px;
  grid-template-rows: ${p =>
    `repeat(${Math.ceil((p.length < 5 ? 5 : p.length) / p.columns)}, 1fr)`};
  grid-template-columns: ${p => `repeat(${p.columns}, 1fr)`};
  margin: 0px 5px 0px 0px;
`;
