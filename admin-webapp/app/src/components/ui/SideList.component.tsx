import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import List from '@material-ui/core/List';
import Divider from '@material-ui/core/Divider';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';

import { menuItems } from '../../utils';
import { colors } from '../../utils/colors';
import { mainMenuItem, selectedItem } from '../../utils/styles';

export default () => {
  const [route, setRoute] = useState('');

  useEffect(() => {
    const startRoute = `${window.location.hash
      .replace(/L/g, 'L ')
      .replace('/', '')
      .replace('#', '')}`;

    setRoute(startRoute);
  }, []);

  return (
    <>
      <List
        style={{
          paddingTop: 'unset'
        }}
      >
        {menuItems.map(({ title, icon }) => {
          const listItemColor = colors.black;
          return (
            <Link
              style={mainMenuItem}
              to={`/${title.replace(/ /g, '')}`}
              key={title}
            >
              <ListItem
                button={true}
                onClick={() => setRoute(title)}
                style={selectedItem(title === route)}
                data-testid={`sideList_${title}`}
              >
                <ListItemIcon
                  style={{
                    color: listItemColor
                  }}
                  data-testid={`listItemIcon_${title}`}
                >
                  {icon}
                </ListItemIcon>
                <ListItemText
                  style={{
                    padding: 0,
                    color: listItemColor
                  }}
                  primary={title}
                  data-testid={`listItemText_${title}`}
                />
              </ListItem>
            </Link>
          );
        })}
      </List>
      <Divider />
    </>
  );
};
