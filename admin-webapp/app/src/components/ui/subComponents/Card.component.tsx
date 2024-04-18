import React, { useGlobal } from 'reactn';
import { useState } from 'react';
import styled from 'styled-components';
import Card from '@material-ui/core/Card';
import CardContent from '@material-ui/core/CardContent';
import Avatar from '@material-ui/core/Avatar';
import Fab from '@material-ui/core/Fab';
import Error from '@material-ui/icons/Error';
import MoreVert from '@material-ui/icons/MoreVert';
import Typography from '@material-ui/core/Typography';
import { FaFilter } from 'react-icons/fa';
import MenuComponent from '../subComponents/Menu.component';

import {
  cardStyle,
  threeDots,
  iconColor,
  Column,
  Row
} from '../../../utils/styles';
import { Card as CardType } from '../../../types';
import { itemTypeErrors, getNullValues, assignValues } from '../../../utils';
import { colors } from '../../../utils/colors';
import { useHover } from '../../../hooks';
import { itemTypes } from '../../../utils/itemTypes';
import logoRelatics from '../../../img/Relatics.png';
import logoNeanex from '../../../img/logoNeanex.png';

export default ({
  item,
  showAvatar,
  headerTitle,
  content,
  type,
  isRelaticsConfigType
}: CardType) => {
  const environmentId = 'environmentId';
  const [deleteItem] = useGlobal('removeItem');
  const [allGlobalFilters, setAllGlobalFilters] = useGlobal('globalFilters');

  const [anchorEl, setAnchorEl] = useState<HTMLElement | undefined>(undefined);
  const [showErrors, setShowErrors] = useState(false);
  const testId = item.id;
  const [hoverRef, isHovered] = useHover(true);

  const checkFilterExists = allGlobalFilters.workspaces.some(
    filter => filter.value === item.id
  );

  const checkFilterExistsOnIds = allGlobalFilters.workspaces.find(
    filter => filter.mainId === environmentId
  );

  const typeCheck = type === itemTypes.environments;

  return (
    <Card
      key={item.name || item.id}
      style={{
        ...cardStyle,
        ...(checkFilterExists && {
          border: `2px solid ${colors.green}`
        })
      }}
      data-testid={`Card_${type}_${item.id}`}
    >
      <Wrapper
        ref={hoverRef}
        deleting={deleteItem.isDeleting && deleteItem.item.id === testId}
        data-testid={`CardHeader_${item.id}`}
      >
        {showAvatar ? (
          <Avatar
            aria-label="Recipe"
            style={{
              backgroundColor: colors.white,
              marginRight: '10px'
            }}
          >
            {isRelaticsConfigType ? (
              <img
                alt="Relatics logo"
                src={logoRelatics}
                height="20"
                width="20"
                data-testid="RelaticsLogo"
              />
            ) : (
              <img
                alt="Neanex logo"
                src={logoNeanex}
                height="20"
                width="20"
                data-testid="NeanexLogo"
              />
            )}
          </Avatar>
        ) : (
          undefined
        )}
        <Column>
          <CardHeaderTitle data-testid={`CardTitle_${testId}`}>
            {headerTitle}
          </CardHeaderTitle>
        </Column>
        {typeCheck && (
          <Fab
            style={{
              ...threeDots,
              marginRight: 'unset',
              paddingTop: '2px',
              visibility: isHovered ? 'visible' : 'hidden'
            }}
            onClick={() => {
              if (!checkFilterExists && !checkFilterExistsOnIds) {
                setAllGlobalFilters({
                  ...allGlobalFilters,
                  workspaces: allGlobalFilters.workspaces.concat({
                    mainId: environmentId,
                    value: item.id || ''
                  })
                });
              } else {
                setAllGlobalFilters({
                  ...allGlobalFilters,
                  workspaces: allGlobalFilters.workspaces.map(filter => {
                    if (filter.mainId === environmentId) {
                      return {
                        ...filter,
                        value: item.id || ''
                      };
                    }
                    return filter;
                  })
                });
              }
            }}
          >
            <FaFilter
              style={{
                width: '20px',
                height: '20px',
                color: colors.black
              }}
              data-testid={`FilterOnEnvId_${item.id}`}
            />
          </Fab>
        )}
        <Fab
          style={{
            ...threeDots,
            marginRight: 'unset',
            visibility:
              getNullValues(item, itemTypeErrors(type)).length > 0
                ? 'visible'
                : 'hidden'
          }}
          onClick={e => {
            e.stopPropagation();
            setShowErrors(!showErrors);
          }}
        >
          <Error style={{ color: colors.red }} />
        </Fab>
        <Fab
          style={threeDots}
          aria-controls="simple-menu"
          aria-haspopup="true"
          onClick={e => {
            e.stopPropagation();
            setAnchorEl(e.currentTarget);
          }}
        >
          <MoreVert style={iconColor} data-testid={`CardThreeDots_${testId}`} />
        </Fab>
      </Wrapper>
      {content.length > 0 ? (
        <CardContent style={{ padding: '10px' }}>
          {content.map(item => (
            <Row key={item.value}>
              <Typography
                style={{
                  color: colors.blackOpacity,
                  fontWeight: 1000,
                  width: '120px',
                  flexShrink: 0
                }}
                variant="caption"
              >
                {item.pre}
              </Typography>
              <Typography
                style={{
                  color: colors.blackOpacity,
                  fontWeight: 500,
                  wordBreak: 'break-word'
                }}
                variant="caption"
                data-testid={`TextField_${item.value}`}
              >
                {item.value}
              </Typography>
            </Row>
          ))}
        </CardContent>
      ) : (
        undefined
      )}
      {showErrors ? (
        <ErrorsWrapper>
          {getNullValues(item, itemTypeErrors(type)).map(path => {
            const values = {};
            assignValues(values, path.split('.'), null);

            return (
              <ErrorPre key={path}>{JSON.stringify(values, null, 4)}</ErrorPre>
            );
          })}
        </ErrorsWrapper>
      ) : (
        undefined
      )}
      <MenuComponent
        id="simple-menu"
        anchor={anchorEl}
        item={item}
        type={type}
        closeMenu={() => setAnchorEl(undefined)}
        editItem={true}
        deleteItem={true}
        cloneItem={type === "workspaces"}
      />
    </Card>
  );
};

const CardHeaderTitle = styled.h3`
  font-size: small;
  font-weight: 800;
  text-transform: uppercase;
  color: ${p => p.theme.colors.blackOpacity};
  font-family: Roboto, Helvetica, Arial, sans-serif;
  line-height: 1.71429em;
  margin: unset;
  display: unset;
  word-break: break-word;
`;

const Wrapper = styled.div<{ deleting: boolean }>`
  display: flex;
  flex-direction: row;
  padding: 10px;
  background-color: ${p =>
    p.deleting ? p.theme.colors.grey : p.theme.colors.greyOpacity};
  align-items: center;
`;

const ErrorsWrapper = styled.div`
  max-height: 200px;
  overflow: auto;
`;

const ErrorPre = styled.pre`
  font-size: x-small;
  white-space: pre-wrap;
  padding: 10px;
  margin: 5px;
  border: ${p => `1px solid ${p.theme.colors.grey}`};
`;
