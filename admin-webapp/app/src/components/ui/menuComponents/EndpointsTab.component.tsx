import React, { useGlobal, useDispatch } from 'reactn';
import { useEffect } from 'react';

import AllEndpoints from '../subComponents/AllEndpoints.component';
import EndpointsPage from '../subComponents/EndpointsPage.component';
import AlertDialog from '../subComponents/AlertDialog.component';
import FilterDialog from '../subComponents/FilterDialog.component';
import { ContainerTab } from '../../../utils/styles';
import { itemTypes } from '../../../utils/itemTypes';

export default () => {
  const [editItem] = useGlobal('editItem');
  const [endpoints] = useGlobal('sparqlendpoints');
  const itemToDelete = useDispatch('deleteItem');
  const [deleteItem, setDeleteItem] = useGlobal('removeItem');
  const [createItem] = useGlobal('createItem');
  const remove = () => setDeleteItem({ isDeleting: false, item: {}, type: '' });

  const fetchData = useDispatch('fetchData');
  useEffect(() => {
    fetchData(itemTypes.endpoints); // sets the data for the endpoints
  }, [fetchData]);

  return (
    <ContainerTab>
      <FilterDialog instance={endpoints[0]} type={itemTypes.endpoints} />
      <AlertDialog
        description="Delete the selected endpoint"
        item={deleteItem.item.name}
        open={deleteItem.isDeleting && deleteItem.type === itemTypes.endpoints}
        confirmationAction={() => {
          itemToDelete(deleteItem.item, itemTypes.endpoints);
        }}
        dialogOnClose={() => remove()}
        cancelAction={() => remove()}
      />

      {(editItem.isEditing && editItem.type === itemTypes.endpoints) ||
      (createItem.isCreating && createItem.type === itemTypes.endpoints) ? (
        <EndpointsPage
          endpoint={editItem.item}
          type={itemTypes.endpoints}
          create={
            createItem.isCreating && createItem.type === itemTypes.endpoints
          }
        />
      ) : (
        <AllEndpoints data={endpoints} type={itemTypes.endpoints} />
      )}
    </ContainerTab>
  );
};
