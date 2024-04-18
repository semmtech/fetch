import React, { useEffect } from 'react';
import { useGlobal, useDispatch } from 'reactn';
import 'react-table/react-table.css';
import styled from 'styled-components';

import TableBody from './subComponents/TableBody.component';
import TableHeader from './TableHeader.component';
import { importAlerts, updateHeight } from '../../utils';
import { IRowProps } from '../../types';
import constants from '../../constants';
import prop from 'lodash/fp/prop';

// This is the component of the Table
export default ({ data }: { data: { [key: string]: IRowProps } }) => {
  const fetchChildren = useDispatch('fetchChildren');
  const [error] = useGlobal('error');
  const [importSteps] = useGlobal('importSteps');
  const [allData] = useGlobal('data');
  const [pagination] = useGlobal('enablePagination');
  const [configurationId] = useGlobal('configurationId');

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    // This is just for the initialization to adjust the iframe's height
    updateHeight(pagination, configurationId);

    importAlerts(importSteps);
    const successSteps = importSteps.map(prop('success'));
    const hasImportFailed =
      !successSteps.includes(false) && importSteps.length !== 0;
    if (hasImportFailed) {
      Object.keys(allData).forEach(item => {
        const rowData = allData[item];
        if (rowData.children.length > 0) {
          fetchChildren(false, rowData, 0);
        }
      });
    }
    return;
  }, [importSteps]); // Update uuids en parentuuids of the data

  const initialData = React.useMemo(
    () => Object.values(data).filter(item => item.level === 0),
    [data, importSteps]
  );

  return (
    <Container>
      {error.name !== '' && error.message !== '' ? (
        <div>
            <h4 data-testid="TableError">
              Something went wrong with fetching the data, please reload and try
              again!
            </h4>
            <p>
                Should this error persist, please make sure the configuration is valid and active, and configured to be used by the current host application.
            </p>
        </div>
      ) : (
        <React.Fragment>
          <TableHeader />
          <TableBody
            data={initialData}
            isRoot={true}
            treeNode={constants.highestTreeLevel}
          />
        </React.Fragment>
      )}
    </Container>
  );
};

const Container = styled.div`
  text-align: center;
  margin: auto;
  border-radius: 10px;
  border: ${p => `1px solid ${p.theme.colors.blackOpacityBorder}`};
`;
