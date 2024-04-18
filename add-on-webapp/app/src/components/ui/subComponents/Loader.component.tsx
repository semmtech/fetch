import React from 'reactn';
import styled from 'styled-components';
import { PulseLoader } from 'react-spinners';
import { colors } from '../../../utils/colors';

// This is the component of the loader
export default ({
  loading,
  color = colors.blackOpacity
}: {
  loading: boolean;
  color?: string;
}) => (
  <LoaderContainer data-testid="LoaderIcon">
    {loading ? (
      <PulseLoader color={color} size={15} margin={'2px'} loading={loading} />
    ) : undefined}
  </LoaderContainer>
);
const LoaderContainer = styled.div`
  display: flex;
  justify-content: center;
`;
