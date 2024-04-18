import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import CircularProgress, {
  CircularProgressProps
} from '@material-ui/core/CircularProgress';
import styled from 'styled-components';

import { colors } from '../../../utils/colors';

const useStyles = makeStyles({
  top: {
    color: colors.lightBlue
  },
  bottom: {
    color: colors.blackOpacity,
    animationDuration: '550ms',
    position: 'absolute',
    left: 0
  }
});

const CustomProgress = (props: CircularProgressProps) => {
  const classes = useStyles();

  return (
    <Wrapper>
      <CircularProgress
        variant="determinate"
        value={100}
        className={classes.top}
        size={50}
        thickness={10}
        {...props}
      />
      <CircularProgress
        variant="indeterminate"
        disableShrink={true}
        className={classes.bottom}
        size={50}
        thickness={10}
        {...props}
      />
    </Wrapper>
  );
};

export default () => (
  <Progress>
    <CustomProgress />
  </Progress>
);

export const Wrapper = styled.div`
  position: relative;
`;

export const Progress = styled.div`
  flex-grow: 1;
  margin: auto;
  display: flex;
  align-items: center;
`;
