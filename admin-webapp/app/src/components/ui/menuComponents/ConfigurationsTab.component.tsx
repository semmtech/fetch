import React, { useGlobal, useDispatch } from 'reactn';
import { useEffect } from 'react';

import ConfigurationsPage from '../subComponents/ConfigurationsPage.component';
import AllConfigurations from '../subComponents/AllConfigurations.component';
import FilterDialog from '../subComponents/FilterDialog.component';
import AlertDialog from '../subComponents/AlertDialog.component';
import { ContainerTab } from '../../../utils/styles';
import { itemTypes } from '../../../utils/itemTypes';

export default () => {
  const [editItem] = useGlobal('editItem');
  const [configs] = useGlobal('configurations');
  const itemToDelete = useDispatch('deleteItem');
  const [deleteItem, setDeleteItem] = useGlobal('removeItem');
  const [createItem] = useGlobal('createItem');
  const remove = () => setDeleteItem({ isDeleting: false, item: {}, type: '' });

  const fetchData = useDispatch('fetchData');
  useEffect(() => {
    fetchData(itemTypes.configurations); // sets the data for the configurations
  }, [fetchData]);

  return (
    <ContainerTab>
      <FilterDialog instance={configs[0]} type={itemTypes.configurations} />
      <AlertDialog
        description="Delete the selected configuration"
        item={deleteItem.item.name}
        open={
          deleteItem.isDeleting && deleteItem.type === itemTypes.configurations
        }
        confirmationAction={() => {
          itemToDelete(deleteItem.item, itemTypes.configurations);
        }}
        dialogOnClose={() => remove()}
        cancelAction={() => remove()}
      />

      {(editItem.isEditing && editItem.type === itemTypes.configurations) ||
      (createItem.isCreating &&
        createItem.type === itemTypes.configurations &&
        createItem.configType) ? (
        <ConfigurationsPage
          configType={createItem.configType || editItem.item.targetType}
          configuration={editItem.item}
          type={itemTypes.configurations}
          create={
            createItem.isCreating &&
            createItem.type === itemTypes.configurations
          }
        />
      ) : (
        <AllConfigurations data={configs} type={itemTypes.configurations} />
      )}
    </ContainerTab>
  );
};
