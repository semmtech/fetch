import React from 'reactn';
import {
  DatePicker as MDatePicker,
  DatePickerProps as MDatePickerProps
} from '@material-ui/pickers';
import styled from 'styled-components';
import _noop from 'lodash/noop';

import { Row, datePickerInputStyle } from '../../../utils/styles';
import { ParsableDate } from '@material-ui/pickers/constants/prop-types';

interface Props {
  testIds: { start: string; end: string };
  labels: { start: string; end: string };
  values: { start: ParsableDate; end: ParsableDate };
  onChangeEnd: (date: Date | null) => void;
  onChangeStart: (date: Date | null) => void;
  extraStyle: boolean;
  configPage?: boolean;
  keyPress?: (e: React.KeyboardEvent<HTMLDivElement>) => void;
}

export default ({
  testIds,
  values,
  onChangeEnd,
  onChangeStart,
  extraStyle,
  configPage,
  labels,
  keyPress
}: Props) => (
  <Row
    style={{
      marginTop: '10px',
      ...(extraStyle && { justifyContent: 'space-between', marginTop: '15px' }),
      ...(configPage && { marginTop: '4px', marginLeft: '10px' })
    }}
  >
    <Wrapper>
      <DatePicker
        data-testid={testIds.start}
        label={labels.start}
        value={values.start}
        onChange={date => {
          onChangeStart(date);
        }}
        onKeyPress={e => {
          if (keyPress && values.end) {
            keyPress(e);
          }
        }}
      />
    </Wrapper>
    <DatePicker
      data-testid={testIds.end}
      label={labels.end}
      value={values.end}
      onChange={date => {
        onChangeEnd(date);
      }}
      onKeyPress={e => {
        if (keyPress) {
          keyPress(e);
        }
      }}
    />
  </Row>
);

const Wrapper = styled.div`
  margin-right: 10px;
`;

type DatePickerProps = Pick<
  MDatePickerProps,
  | 'children'
  | 'style'
  | 'value'
  | 'onChange'
  | 'label'
  | 'onKeyPress'
  | 'inputProps'
>;

const DatePicker = ({
  onChange = _noop,
  onKeyPress = _noop,
  ...props
}: DatePickerProps) => (
  <MDatePicker
    {...props}
    variant="inline"
    openTo="year"
    clearable={true}
    inputVariant="outlined"
    format="dd/MM/yyyy"
    views={['year', 'month', 'date']}
    label={props.label}
    style={props.style}
    value={props.value}
    inputProps={{
      style: datePickerInputStyle
    }}
    onKeyPress={onKeyPress}
    onChange={onChange}
  >
    {props.children}
  </MDatePicker>
);
