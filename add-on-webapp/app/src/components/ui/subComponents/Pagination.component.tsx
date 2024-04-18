import React, { ReactNode, ReactPortal, useEffect } from 'react';
import { useGlobal } from 'reactn';
import classnames from 'classnames';
import styled from 'styled-components';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import IconButton from '@material-ui/core/IconButton';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import Select from '@material-ui/core/Select';
import InputBase from '@material-ui/core/InputBase';

import { paginationButton } from '../../../utils/styles';
import { IPagination } from '../../../types';
import { updateHeight, customId } from '../../../utils';

const defaultButton = (props: {
  children: ReactNode | ReactPortal;
  disabled: boolean;
  [key: string]: unknown;
}) => (
  <IconButton type="button" {...props} disabled={props.disabled}>
    {props.children}
  </IconButton>
);

export default (props: IPagination) => {
  const {
    showPageSizeOptions,
    pageSizeOptions,
    pageSize,
    data,
    canPrevious,
    canNextFromData,
    onPageSizeChange,
    className,
    onPageChange,
    style,
    PreviousComponent = defaultButton,
    NextComponent = defaultButton
  } = props;
  const [configurationId] = useGlobal('configurationId');

  const getSafePage = (page: number) => {
    if (isNaN(page)) {
      page = props.page;
    }
    if (props.canNextFromData) {
      return page;
    }

    return Math.min(Math.max(page, 0), props.pages - 1);
  };

  const changePage = (page: number) => {
    page = getSafePage(page);
    if (props.page !== page) {
      onPageChange(page);
    }
  };

  const canNext = canNextFromData
    ? data && data.length === pageSize
    : props.canNext;

  const startAmount = props.page * pageSize + 1;
  const showAmount =
    props.page + 1 === props.pages ? data.length : (props.page + 1) * pageSize;
  const total = data.length;

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    updateHeight(true, configurationId);
  }, [pageSize]);

  return (
    <div
      className={classnames(className, '-pagination')}
      data-testid="PaginationFooter"
      id={customId('PaginationFooter', configurationId)}
      style={{
        ...style,
        borderRadius: '0 0 15px 15px',
        borderTop: 'unset'
      }}
    >
      <WrapperPageSize>
        {showPageSizeOptions && (
          <PageSizeOptions className="select-wrap -pageSizeOptions">
            <TextPageSize>Rows per page</TextPageSize>
            <FormControl>
              <Select
                value={pageSize}
                onChange={e => onPageSizeChange(Number(e.target.value))}
                input={<InputBase id="pageSize-select" />}
                style={{
                  fontSize: 'small',
                  paddingTop: '3px'
                }}
              >
                {pageSizeOptions.map(option => (
                  <MenuItem key={option} value={option}>
                    {option}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </PageSizeOptions>
        )}
        <TextTotalRoots data-testid="TotalRoots">{`${startAmount}-${showAmount} of ${total}`}</TextTotalRoots>
      </WrapperPageSize>
      <Wrapper disabled={!canPrevious}>
        <PreviousComponent
          onClick={() => {
            if (!canPrevious) {
              return;
            }
            changePage(0);
          }}
          disabled={!canPrevious}
          style={paginationButton(canPrevious)}
          data-testid="GoFirstPage"
        >
          <FirstPage />
        </PreviousComponent>
        <PreviousComponent
          onClick={() => {
            if (!canPrevious) {
              return;
            }
            changePage(props.page - 1);
          }}
          disabled={!canPrevious}
          style={paginationButton(canPrevious)}
          data-testid="GoPreviousPage"
        >
          <ChevronLeft />
        </PreviousComponent>
      </Wrapper>
      <Wrapper disabled={!canNext}>
        <NextComponent
          onClick={() => {
            if (!canNext) {
              return;
            }
            changePage(props.page + 1);
          }}
          disabled={!canNext}
          style={paginationButton(canNext)}
          data-testid="GoNextPage"
        >
          <ChevronRight />
        </NextComponent>
        <NextComponent
          onClick={() => {
            if (!canNext) {
              return;
            }
            changePage(props.pages);
          }}
          disabled={!canNext}
          style={paginationButton(canNext)}
          data-testid="GoLastPage"
        >
          <LastPage />
        </NextComponent>
      </Wrapper>
    </div>
  );
};

const Wrapper = styled.div<{ disabled: boolean }>`
  display: flex;
  justify-content: center;
  flex: 1;
  align-items: center;
  margin-right: 10px;
  cursor: ${p => (p.disabled ? 'not-allowed' : '')};
`;

const TextTotalRoots = styled.span`
  margin-left: 15px;
`;

const TextPageSize = styled.span`
  margin-right: 10px;
`;

const WrapperPageSize = styled.div`
  justify-content: flex-end;
  margin-right: 15px;
  flex: 15 1;
  display: flex;
  align-items: center;
`;

const PageSizeOptions = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
`;
