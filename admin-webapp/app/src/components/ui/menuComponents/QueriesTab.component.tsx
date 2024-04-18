import React, { useGlobal, useDispatch } from 'reactn';
import { useEffect } from 'react';

import AllQueries from '../subComponents/AllQueries.component';
import QueriesPage from '../subComponents/QueriesPage.component';
import AlertDialog from '../subComponents/AlertDialog.component';
import FilterDialog from '../subComponents/FilterDialog.component';
import { ContainerTab } from '../../../utils/styles';
import { itemTypes } from '../../../utils/itemTypes';

export default () => {
  const [editItem] = useGlobal('editItem');
  const [queries] = useGlobal('sparqlqueries');
  const itemToDelete = useDispatch('deleteItem');
  const [deleteItem, setDeleteItem] = useGlobal('removeItem');
  const [createItem] = useGlobal('createItem');
  const remove = () => setDeleteItem({ isDeleting: false, item: {}, type: '' });

  const fetchData = useDispatch('fetchData');
  useEffect(() => {
    fetchData(itemTypes.queries); // sets the data for the queries
  }, [fetchData]);

  return (
    <ContainerTab>
      <FilterDialog instance={queries[0]} type={itemTypes.queries} />
      <AlertDialog
        description="Delete the selected query"
        item={deleteItem.item.name}
        open={deleteItem.isDeleting && deleteItem.type === itemTypes.queries}
        confirmationAction={() => {
          itemToDelete(deleteItem.item, itemTypes.queries);
        }}
        dialogOnClose={() => remove()}
        cancelAction={() => remove()}
      />

      {(editItem.isEditing && editItem.type === itemTypes.queries) ||
      (createItem.isCreating && createItem.type === itemTypes.queries) ? (
        <QueriesPage
          query={editItem.item}
          type={itemTypes.queries}
          create={
            createItem.isCreating && createItem.type === itemTypes.queries
          }
        />
      ) : (
        <AllQueries data={queries} type={itemTypes.queries} />
      )}
    </ContainerTab>
  );
};
