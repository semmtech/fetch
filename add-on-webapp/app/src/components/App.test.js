import React from 'react';
import { render } from './testUtils';
import { waitForElement } from 'react-testing-library';

import App from './App.component';

describe('add-on', () => {
  it('renders without crashing', async () => {
    const { getByTestId } = render(<App />);
    await waitForElement(() => getByTestId('TableError'));
    expect(getByTestId('TableError')).toBeTruthy();
  });
});
