import React from 'react';
import { GoGear, GoDatabase } from 'react-icons/go';
import { TextFieldProps } from '@material-ui/core/TextField';
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';
import ShareIcon from '@material-ui/icons/Share';
import _flatten from 'lodash/flatten';
import _groupBy from 'lodash/groupBy';
import _filter from 'lodash/filter';
import _unionBy from 'lodash/unionBy';
import copy from 'clipboard-copy';
import produce from 'immer';

import Relatics from '../img/Relatics.png';
import logoNeanex from '../img/logoNeanex.png';
import {
  Filter,
  IUnknownObject,
  Item,
  ReorderedTypes,
  Step,
  Overflown,
  TargetSystemOrEndpoint,
  Environment
} from '../types';
import { dataKeysType } from '../global';
import { colors } from '../utils/colors';
import { APIendpoints } from '../utils/endpoints';
import { headerContent } from '../reducers';
import { itemTypes } from './itemTypes';
import constants from '../constants';

export const globalTextFieldProps = (
  fullWidth: boolean,
  required?: boolean,
  columnsList?: boolean
): TextFieldProps => ({
  ...(columnsList && {
    style: {
      marginTop: 'unset',
      padding: '5px',
      backgroundColor: colors.tableHeader
    }
  }),
  inputProps: {
    style: {
      overflow: 'hidden',
      textOverflow: 'ellipsis',
      whiteSpace: 'nowrap',
      ...(columnsList && {
        fontSize: '13px',
        textAlign: 'center',
        color: colors.white
      })
    }
  },
  margin: 'dense',
  fullWidth,
  variant: 'outlined',
  required
});

export const defaultDates = {
  start: 0,
  end: 0
};
export type ValueDate = typeof defaultDates;

export const defaultStateError = {
  mainId: false,
  subId: false,
  value: false
};

export const booleantype = 'boolean';
export const objectType = 'object';

export const menuItems = [
  {
    icon: <GoGear />,
    title: 'Configurations'
  },
  {
    icon: <GoDatabase />,
    title: 'SPARQL endpoints'
  },
  {
    icon: <ShareIcon style={{ marginLeft: '-3px' }} />,
    title: 'SPARQL queries'
  },
  {
    icon: <img alt="Relatics logo" src={Relatics} height="20" width="20" />,
    title: 'Relatics'
  },
  {
    icon: (
      <img
        alt="Neanex logo"
        style={{ marginTop: '4px' }}
        src={logoNeanex}
        height="20"
        width="20"
      />
    ),
    title: 'Neanex'
  }
];

const dateCheck = (from: number, to: number, check: number) => {
  const fDate = from;
  const tDate = to;
  const cDate = check;

  if (cDate <= tDate && cDate >= fDate) {
    return true;
  }
  return false;
};

export const filteredData = (
  filter: Filter,
  data: IUnknownObject[],
  environments: Environment[]
) =>
  filter.mainId && filter.value
    ? filter.mainId.includes('Date')
      ? data.filter(item =>
          typeof filter.value !== 'string'
            ? dateCheck(
                filter.value.start,
                filter.value.end,
                item[filter.mainId]
              )
            : undefined
        )
      : filteredNonDateData(filter, data, environments)
    : data;

export const filteredNonDateData = (
  filter: Filter,
  data: IUnknownObject[],
  environments: Environment[]
) => {
  let filterValueToSearch = filter.value;
  if (
    (environments.length > 0 && filter.mainId === 'environmentId') ||
    (filter.subId && filter.subId === 'environmentId')
  ) {
    const environment = environments.find(env => env.environmentId === filter.value);
    if (environment) {
      filterValueToSearch = environment.id;
    }
  }

  return data.filter(
    item =>
      filterValueToSearch &&
      (filter.subId && !!item[filter.mainId]
        ? String(item[filter.mainId][filter.subId])
        : String(item[filter.mainId])
      )
        .toLowerCase()
        .includes(
          typeof filterValueToSearch === 'string'
            ? filterValueToSearch.toLowerCase()
            : ''
        ) &&
      String(item[filter.mainId]).trim().length !== 0
  );
};

const collator = new Intl.Collator(undefined, {
  numeric: true,
  sensitivity: 'base'
});

const nullSort = (a: string, b: string) => {
  if (a === b) {
    return 0;
  }
  if (a === null) {
    return 1;
  }
  if (b === null) {
    return -1;
  }

  let ret;
  ret = collator.compare(a, b);
  return ret;
};

const checkCompare = (a: { name: string }, b: { name: string }) =>
  nullSort(a.name, b.name);

export const dataAllFilters = (
  data: IUnknownObject[],
  allFilters: Filter[],
  environments?: Environment[]
) => {
  switch (allFilters.length) {
    case 0:
      return data.sort((a, b) => checkCompare(a, b));
    case 1:
      return filteredData(allFilters[0], data, environments || []).sort(
        (a, b) => checkCompare(a, b)
      );
    default:
      const oneFilterData = _flatten(
        allFilters.map(filter => filteredData(filter, data, environments || []))
      );
      const grouppedData = _groupBy(oneFilterData, item => item.id);
      const multipleFiltersData = _flatten(
        _filter(grouppedData, items => items.length === allFilters.length)
      );

      return _unionBy(multipleFiltersData, 'id').sort((a, b) =>
        checkCompare(a, b)
      );
  }
};

export const checkUndefinedOrNull = (
  value: null | { [key: string]: unknown } | undefined
) => typeof value === 'undefined' || value === null;

export const itemTypeErrors = (type: dataKeysType) => {
  switch (type) {
    case itemTypes.configurations:
      return 'configuration';
    case itemTypes.endpoints:
      return 'endpoint';
    case itemTypes.queries:
      return 'query';
    case itemTypes.workspaces:
      return 'workspace';
    case itemTypes.environments:
      return 'environment';
    case itemTypes.jsonapis:
      return 'jsonapi';
    default:
      return '';
  }
};

export const getNullValues = (obj: Item, path: string) => {
  const props: string[] = [];
  for (const key in obj) {
    if (obj !== undefined) {
      const itemToTest = obj[key] as Item;
      if (itemToTest === null) {
        props.push(path + '.' + key);
      }
      if (itemToTest !== null && itemToTest.length === 0) {
        props.push(path + '.' + key);
      }
      if (itemToTest instanceof Object) {
        props.push.apply(props, getNullValues(itemToTest, path + '.' + key));
      }
    }
  }

  // This array is for the fields that are not required -> so they don't show these
  const filterKeys = [
    'description',
    'importTarget.xpathExpression',
    'targetsystem.xpathExpression',
    'childrenQuery.query',
    'endDate',
    'startDate',
    'namespace',
    'defaultGraphs',
    'targetDataSystems',
    'endpoints',
    'filterFields',
    'titleQuery',
    'additionalInputs'
  ];
  const filteredProps = props.filter(prop =>
    filterKeys.every(key => !prop.includes(key))
  );

  return filteredProps;
};

export const assignValues = (
  obj: { [key: string]: unknown },
  keyPath: string[],
  value: unknown
) => {
  const lastKeyIndex = keyPath.length - 1;
  keyPath.forEach((key, i: number) => {
    if (i === lastKeyIndex) {
      return;
    }
    if (!(key in obj)) {
      obj[key] = {};
    }
    obj = obj[key] as Item;
  });
  return (obj[keyPath[lastKeyIndex]] = value);
};

export const importStepDefaultValues = (items: Step[]) => {
  const trimmedImportSteps = items.map(item => {
    if (item.sparqlQuery.defaultGraphs !== null) {
      const trimmedGraphs = item.sparqlQuery.defaultGraphs
        ? item.sparqlQuery.defaultGraphs.map(graph => graph.trim())
        : [];

      return {
        ...item,
        sparqlQuery: {
          ...item.sparqlQuery,
          defaultGraphs: includesEmptyString(trimmedGraphs) ? [] : trimmedGraphs
        }
      };
    }
    return item;
  });

  return trimmedImportSteps.filter(
    item =>
      item.name !== '' ||
      item.importTarget !== null ||
      item.sparqlQuery.query !== null
  );
};

export const includesEmptyString = (items: string[]) =>
  items.every(item => item === '');

/**
 * This is the reorder function, it returns an array with the updated order
 * You give the destination index you want your item to go to, the source index from where it starts and then the items to reorder.
 * The function itself will handle the rest
 */
export const reorder = (
  itemsToReorder: ReorderedTypes,
  sourceIndex: number,
  destinationIndex?: number
) => {
  // No destination found or destination is same as source
  if (
    typeof destinationIndex === 'undefined' ||
    destinationIndex === sourceIndex
  ) {
    return itemsToReorder;
  }

  return produce(itemsToReorder, draft => {
    const [removed] = draft.splice(sourceIndex, 1);
    draft.splice(destinationIndex, 0, removed);
  });
};

export const checkError = (value: string | null | undefined) =>
  value === null || value === undefined || value.length === 0;

export const defineAutocompleteOptions = (
  data: IUnknownObject[],
  type: string,
  targetsystem?: boolean
) =>
  data
    .map(item => {
      const errors = getNullValues(item, type);
      if (errors.length > 0) {
        return undefined;
      }

      if (
        [constants.workspace, constants.jsonapi].includes(type) &&
        !targetsystem
      ) {
        return {
          value: item.id,
          display: item.name,
          envType: type
        };
      }

      return {
        value: item.id,
        display: targetsystem ? item.operationName : item.name
      };
    })
    .filter(Boolean);

//const defineUrl = ({ origin }: { origin: string }) => {
//  const staging = 'staging';
//  const development = 'development';
//
//  if (origin.includes(staging)) {
//    return `.${staging}.`;
//  }
//  if (origin.includes(development)) {
//    return `.${development}.`;
//  }
//  return '.';
//};

export const uniqueId = () => {
  return (
    Date.now().toString(36) +
    Math.random()
      .toString(36)
      .substring(2)
  );
};

// NOTE (MHE 2020-08-19): Apparently baseUrl and src of script were calculated by using the origin and checking for
// staging vs. development vs. (none); resulting in a baseUrl 'https://laces-fetch${defineInstance}neanex.com/add-on/'.
// This was removed since it will not allow for deploying in other instances - keep in mind that if the front-end is run
// in a different instance this might break.
export const generateJSForIframe = async ({
  configId,
  origin
}: {
  configId: string;
  origin: string;
}) => {
  const containerId = 'container-' + uniqueId();
  const instanceId = 'iframe-' + uniqueId();
  const generatedConfig = `
<div id="${containerId}"></div>
<script type="text/javascript" src="${origin}/add-on/integration.js"></script>
<script type="text/javascript">
    settings = {
        configurationId: "${configId}",
        containerId: "${containerId}",
        instanceId: "${instanceId}",
        urlParameters: new Array(
            { id: 'wid', type: 'literal' },
            { id: 'username', type: 'literal' },
            { id: 'subjectId', type: 'literal' }
        ),
        baseUrl: "${origin}/add-on/",
        height: {
            max: '800px',
            min: '400px'
        }
    };

    createAddonIframe(settings.baseUrl,
        settings.configurationId,
        settings.containerId,
        settings.instanceId,
        settings.urlParameters,
        settings.height);
</script>`;

  return copy(generatedConfig)
    .then(() => true)
    .catch(() => false);
};

export const CustomTooltip = withStyles(() => ({
  tooltip: {
    color: colors.yellow,
    fontSize: '13px',
    maxWidth: 220,
    border: `1px solid ${colors.white}`,
    backgroundColor: colors.black
  }
}))(Tooltip);

export const isOverflown = ({
  clientWidth,
  clientHeight,
  scrollWidth,
  scrollHeight
}: Overflown) => scrollHeight > clientHeight || scrollWidth > clientWidth;

export const resultTargetOrEndpoints = async ({
  workspace,
  id,
  updateError,
  updateLoading
}: {
  workspace: boolean;
  id: string;
  updateError(): void;
  updateLoading(): void;
}) =>
  fetch(
    `${
      workspace
        ? APIendpoints.targetsystemsWorkspace
        : APIendpoints.jsonapisEndpoints
    }${id}`,
    {
      method: 'GET',
      headers: headerContent
    }
  )
    .then(async res => {
      if (res.redirected) {
        window.location.href = res.url;
      }

      if (res.ok) {
        updateLoading();
        return res.json();
      }
      updateError();
    })
    .catch((err: Error) => {
      updateLoading();
      updateError();
    });

export const emptyTargetSystem = ({
  index
}: {
  index: string;
}): TargetSystemOrEndpoint => ({
  index,
  operationName: null,
  hidden: 'true',
  type: null,
  entryCode: null,
  xpathExpression: null
});

export const emptyEndpoint = ({
  index
}: {
  index: string;
}): TargetSystemOrEndpoint => ({
  index,
  name: null,
  hidden: 'true',
  type: null,
  path: null
});

export const menuOptions = (items: string[]) =>
  items.map(item => ({
    value: item,
    display: item
  }));
