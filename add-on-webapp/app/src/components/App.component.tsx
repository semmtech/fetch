import React, { useGlobal, useDispatch } from 'reactn';
import { useEffect } from 'react';
import styled from 'styled-components';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { css } from 'glamor';

import Table from './ui/Table.component';
import Loader from '../components/ui/subComponents/Loader.component';
import { getUrlDecodedConfigurationId } from '../utils';
import AlertActions from '../components/ui/subComponents/AlertActions.component';
import FilterDialog from '../components/ui/subComponents/FilterDialog.component';

// This is the main component
export default () => {
  const fetchRoots = useDispatch('fetchRoots');
  const [loadingRoots] = useGlobal('loadingRoots');
  const [, setConfigurationId] = useGlobal('configurationId');
  const [data] = useGlobal('data');
  const [, setParameters] = useGlobal('parameters');
  const [{ areFiltersActive }] = useGlobal('filters');

  const areParamsAvailable = window.location.href.includes('params');
  const decodedParameters = atob(getUrlDecodedConfigurationId('params'));

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    setConfigurationId(getUrlDecodedConfigurationId('configurationId'));
    setParameters(areParamsAvailable ? JSON.parse(decodedParameters) : []);
  }, []); // set the configurationId

  useEffect(() => {
    fetchRoots();
  }, [fetchRoots, areFiltersActive]); // set the root data

  return (
    <Container>
      {loadingRoots ? <Loader loading={loadingRoots} /> : <Table data={data} />}
      <ToastContainer
        hideProgressBar={true}
        draggable={false}
        pauseOnHover={false}
        pauseOnFocusLoss={false}
        position={toast.POSITION.TOP_LEFT}
        closeOnClick={false}
        bodyClassName={css({ width: '90%' })}
        closeButton={<AlertActions />}
      />
      <FilterDialog />
    </Container>
  );
};

const Container = styled.div`
  user-select: auto;
  padding: 15px;
  color: ${p => p.theme.colors.black};
  margin: auto;
  font-size: smaller;
  font-family: 'Roboto', 'Helvetica', 'Arial', sans-serif;
`;
