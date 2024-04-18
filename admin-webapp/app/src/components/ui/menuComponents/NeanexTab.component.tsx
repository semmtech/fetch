import React, { useGlobal, useDispatch } from 'reactn';
import { useEffect } from 'react';

import AlertDialog from '../subComponents/AlertDialog.component';
import FilterDialog from '../subComponents/FilterDialog.component';
import AllJsonApis from '../subComponents/AllJsonApis.component';
import WorkspaceOrJsonAPIPage from '../subComponents/WorkspaceOrJsonAPIPage.component';
import { ContainerTab } from '../../../utils/styles';
import { itemTypes } from '../../../utils/itemTypes';

export default () => {
  const [editItem] = useGlobal('editItem');
  const itemToDelete = useDispatch('deleteItem');
  const [jsonapis] = useGlobal('jsonapis');
  const [deleteItem, setDeleteItem] = useGlobal('removeItem');
  const [createItem] = useGlobal('createItem');
  const remove = () => setDeleteItem({ isDeleting: false, item: {}, type: '' });
  const fetchData = useDispatch('fetchData');

  useEffect(() => {
    fetchData(itemTypes.jsonapis);
  }, [fetchData]);

  return (
    <ContainerTab>
      <FilterDialog instance={jsonapis[0]} type={itemTypes.jsonapis} />
      <AlertDialog
        description="Delete the selected json api"
        item={deleteItem.item.name}
        open={deleteItem.isDeleting && deleteItem.type === itemTypes.jsonapis}
        confirmationAction={() => {
          itemToDelete(deleteItem.item, itemTypes.jsonapis);
        }}
        dialogOnClose={() => remove()}
        cancelAction={() => remove()}
      />

      {(editItem.isEditing && editItem.type === itemTypes.jsonapis) ||
      (createItem.isCreating && createItem.type === itemTypes.jsonapis) ? (
        <WorkspaceOrJsonAPIPage
          item={editItem.item}
          type={itemTypes.jsonapis}
          create={
            createItem.isCreating && createItem.type === itemTypes.jsonapis
          }
        />
      ) : (
        <AllJsonApis data={jsonapis} type={itemTypes.jsonapis} />
      )}
    </ContainerTab>
  );
};
