import { setGlobal } from 'reactn';
import { render as foo } from 'react-testing-library';
import '../../reducers';
import { defaultInitialState } from '../..';

export const render = (ui, { initialState = defaultInitialState } = {}) => {
  setGlobal(initialState);
  return {
    ...foo(ui)
  };
};
