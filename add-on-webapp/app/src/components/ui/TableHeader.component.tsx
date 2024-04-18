import React, { useGlobal, useDispatch } from 'reactn';
import styled from 'styled-components';
import CloudDownload from '@material-ui/icons/CloudDownload';
import { FaFilter } from 'react-icons/fa';
import { GoX } from 'react-icons/go';
// The backdrop is gonna let the user know that the app is in a loading state, it is also for making sure the user waits for this to finish before he do something else
import Backdrop from '@material-ui/core/Backdrop';

import {
  IconButton,
  classes
} from './subComponents/CustomComponents.components';
import Loader from '../ui/subComponents/Loader.component';
import { FilterActions } from '../../types';
import { colors } from '../../utils/colors';

// This is the component of the Table header
export default () => {
  const customStyle = classes();
  const startImport = useDispatch('import');
  const [selectedIds] = useGlobal('selectedIds');
  const [loading] = useGlobal('loading');
  const [title] = useGlobal('title');
  const [subtitle] = useGlobal('subtitle');
  const [{ hasEmptyRootsFilters, areFiltersActive, rootFilters }] = useGlobal(
    'filters'
  );
  const hasSelectedIds = selectedIds.length === 0;
  const dispatch = useDispatch('handleFilter');

  return (
    <Container>
      <LeftContainer>
        <Title data-testid="title">{title}</Title>
        {subtitle && <Subtitle data-testid="subtitle">{subtitle}</Subtitle>}
      </LeftContainer>
      <Backdrop className={customStyle.backdrop} open={loading}>
        <Loader loading={loading} color={colors.white} />
      </Backdrop>
      <RightContainer>
        <IconButtonWrapper
          data-tip={
            hasSelectedIds
              ? 'Please select an item by clicking in a row.'
              : 'Import selected items'
          }
        >
          <IconButton
            onClick={() => {
              startImport();
            }}
            disabled={hasSelectedIds || loading}
            data-testid="ImportButton"
          >
            <CloudDownload
              style={{
                fontSize: 'larger',
                marginTop: '-4px'
              }}
            />
          </IconButton>
        </IconButtonWrapper>
        <IconButtonWrapper
          data-tip={
            hasEmptyRootsFilters
              ? 'There are no filters provided'
              : 'Open filter screen'
          }
        >
          <IconButton
            onClick={() => {
              dispatch({ type: FilterActions.open });
            }}
            disabled={hasEmptyRootsFilters}
            data-testid="FilterButton"
          >
            <FaFilter style={{ fontSize: 'large' }} />
          </IconButton>
        </IconButtonWrapper>
        <IconButtonWrapper
          data-tip={
            !areFiltersActive
              ? 'There are no active filters'
              : 'Clear active filters'
          }
        >
          <IconButton
            onClick={() => {
              dispatch({ type: FilterActions.clearActive });
            }}
            disabled={
              hasEmptyRootsFilters
                ? hasEmptyRootsFilters
                : Object.values(rootFilters).every(x => !x.value)
            }
            data-testid="ClearActiveFiltersButton"
          >
            <FaFilter style={{ fontSize: 'large', marginRight: '5px' }} />
            <GoX
              style={{
                marginTop: '4px',
                marginLeft: '8px',
                fontSize: 'small',
                position: 'absolute'
              }}
            />
          </IconButton>
        </IconButtonWrapper>
      </RightContainer>
    </Container>
  );
};

const Container = styled.header`
  display: flex;
  flex-direction: row;
  padding: 15px;
  justify-content: space-between;
  position: relative;
  background-color: ${p => p.theme.colors.greyOpacity};
  border-radius: 10px 10px 0 0;
  height: 80px;
`;

const Title = styled.h3`
  color: ${p => p.theme.colors.blackOpacity};
  text-transform: uppercase;
  font-size: initial;
  margin-block-start: 0.5em;
  margin-block-end: 0.5em;
`;

const Subtitle = styled.div`
  color: ${p => p.theme.colors.blackOpacity};
`;

const IconButtonWrapper = styled.div`
  margin-left: 10px;
`;

const LeftContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-self: center;
  text-align: left;
`;

const RightContainer = styled.div`
  display: flex;
  align-self: center;
  text-align: left;
`;
