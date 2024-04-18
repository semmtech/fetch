import React, { useGlobal } from 'reactn';
import { useState } from 'react';
import styled, { keyframes, css } from 'styled-components';
import IconButton from '@material-ui/core/IconButton';
import { FaFilter, FaPlusCircle } from 'react-icons/fa';
import { GoX } from 'react-icons/go';
import Chip from '@material-ui/core/Chip';
import CancelIcon from '@material-ui/icons/Cancel';
import Button from '@material-ui/core/Button';
import Tooltip from '@material-ui/core/Tooltip';
import { toast } from 'react-toastify';
import MenuComponent from '../subComponents/Menu.component';

import {
  chipStyle,
  editButtons,
  overviewButtons,
  HeaderTitle
} from '../../../utils/styles';
import { Filter, PageHeader } from '../../../types';
import { colors } from '../../../utils/colors';
import { generateJSForIframe } from '../../../utils';
import { itemTypes } from '../../../utils/itemTypes';

export default ({
  pageTitle,
  showFilters,
  editPage,
  createPage,
  confirmationAction,
  cancelAction,
  filterAction,
  createAction,
  type,
  configId,
  allItemsLength
}: PageHeader) => {
  const iconButtonStyle = { marginRight: '2px' };
  const [allGlobalFilters, setAllGlobalFilters] = useGlobal('globalFilters');
  const [anchorEl, setAnchorEl] = useState<HTMLElement | undefined>(undefined);

  const filterDisabled = allItemsLength === 0;

  const handleDelete = (filterToDelete: Filter) => {
    setAllGlobalFilters({
      ...allGlobalFilters,
      [type]: allGlobalFilters[type].filter(
        (filter: Filter) => filter.value !== filterToDelete.value
      )
    });
  };

  const formattedDate = (d: Date) =>
    [d.getDate(), d.getMonth() + 1, d.getFullYear()]
      .map(n => (n < 10 ? `0${n}` : `${n}`))
      .join('/');

  const displayValue = (filter: Filter) => {
    const startDate = new Date(
      typeof filter.value === 'object' ? filter.value.start : 0
    );
    const endDate = new Date(
      typeof filter.value === 'object' ? filter.value.end : 0
    );
    const a = formattedDate(startDate);
    const b = formattedDate(endDate);

    return filter && typeof filter.value === 'object'
      ? `${a} - ${b}`
      : filter.value;
  };

  return (
    <Header>
      <Container>
        <HeaderTitle data-testid="PageHeaderTitle">{pageTitle}</HeaderTitle>
        {type === itemTypes.configurations && editPage && !!configId && (
          <Button
            onClick={async () => {
              const origin = window.location.origin;

              const copiedIframe = await generateJSForIframe({
                configId,
                origin
              });
              if (copiedIframe) {
                toast('Copied to clipboard!', {
                  type: toast.TYPE.SUCCESS
                });
              } else {
                toast('Copied failed!', {
                  type: toast.TYPE.ERROR
                });
              }
            }}
            size={'small'}
            style={editButtons}
            data-testid="GenerateButton"
          >
            Generate
          </Button>
        )}
      </Container>
      {showFilters ? (
        <Container>
          <div>
            {allGlobalFilters[type].map((filter, index) =>
              filter.mainId && filter.value ? (
                <Chip
                  key={index}
                  style={chipStyle}
                  label={
                    filter.subId
                      ? `${filter.mainId} (${filter.subId}): ${filter.value}`
                      : `${filter.mainId}: ${displayValue(filter)}`
                  }
                  deleteIcon={
                    <CancelIcon
                      data-testid={`delete_${index}`}
                      style={{ color: colors.yellow }}
                    />
                  }
                  onDelete={() => handleDelete(filter)}
                  data-testid={`${filter.mainId}_${index}`}
                />
              ) : (
                undefined
              )
            )}
          </div>

          {allGlobalFilters[type][0] ? (
            <Tooltip title="Clear all filters">
              <IconButton
                style={{
                  marginRight: '5px',
                  width: '44px',
                  height: '44px',
                  display: 'flex',
                  alignSelf: 'center'
                }}
                onClick={() =>
                  setAllGlobalFilters({
                    ...allGlobalFilters,
                    [type]: []
                  })
                }
              >
                <FilterWrapper>
                  <FaFilter
                    style={{
                      height: '20px',
                      width: '20px',
                      marginLeft: '-5px'
                    }}
                    data-testid="RemoveFilterButton"
                  />
                  <GoX
                    style={{
                      height: '15px',
                      width: '15px',
                      position: 'absolute',
                      marginTop: '8px',
                      marginLeft: '-5px'
                    }}
                  />
                </FilterWrapper>
              </IconButton>
            </Tooltip>
          ) : (
            undefined
          )}
        </Container>
      ) : (
        undefined
      )}

      {editPage || createPage ? (
        <Container>
          <Button
            onClick={() => cancelAction && cancelAction()}
            size={'small'}
            data-testid="CancelButton"
            style={editButtons}
          >
            CANCEL
          </Button>
          <Button
            onClick={() => confirmationAction && confirmationAction()}
            size={'small'}
            data-testid="FinishAddButton"
            style={editButtons}
          >
            {createPage ? 'ADD' : 'FINISH'}
          </Button>
        </Container>
      ) : (
        <Container>
          <IconButton
            style={iconButtonStyle}
            onClick={e => {
              if (createAction) {
                createAction();
              }

              if (type === itemTypes.configurations && !createAction) {
                setAnchorEl(e.currentTarget);
              }
            }}
            aria-controls="configType-menu"
            aria-haspopup="true"
          >
            <FaPlusCircle
              style={overviewButtons(false)}
              data-testid="PlusButton"
            />
          </IconButton>
          <IconButton
            style={{
              ...iconButtonStyle,
              ...(filterDisabled && { opacity: 0.5 })
            }}
            onClick={() => filterAction && filterAction()}
            disabled={filterDisabled}
          >
            <IconAnimation
              status={
                allGlobalFilters[type][0] && allGlobalFilters[type][0].value
                  ? true
                  : false
              }
            >
              <FaFilter
                style={overviewButtons(
                  allGlobalFilters[type][0] && allGlobalFilters[type][0].value
                    ? true
                    : false
                )}
                data-testid="AddFilterButton"
              />
            </IconAnimation>
          </IconButton>
          {type === itemTypes.configurations && (
            <MenuComponent
              id="configType-menu"
              anchor={anchorEl}
              type={type}
              closeMenu={() => setAnchorEl(undefined)}
              relaticsItem={true}
              jsonAPIItem={true}
            />
          )}
        </Container>
      )}
    </Header>
  );
};

const FilterWrapper = styled.div`
  height: 20px;
  color: ${p => p.theme.colors.red};
`;

const Header = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
  padding: 10px 10px;
  flex-shrink: 0;
`;

const Container = styled.div`
  display: flex;
  flex-direction: row;
`;

const fadeIn = keyframes`
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
`;

const IconAnimation = styled.div<{ status?: boolean }>`
  height: 20px;
  animation: ${p =>
    p.status
      ? css`
          ${fadeIn} 1s ease-in infinite
        `
      : 'unset'};
`;
