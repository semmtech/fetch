function getWorkspaceId() {
  var windowAny = window;
  if (!windowAny.getWID) {
    throw new Error(
      'Error getting workspace Id. Function getWID is not defined'
    );
  }
  var wid = windowAny.getWID();
  if (!wid) {
    throw new Error(
      'Error getting workspace Id. Function getWID returned nothing'
    );
  }
  return wid;
}

function getSubjectId() {
  var windowRelaticsAPI = window.RelaticsAPI;
  if (windowRelaticsAPI) {
    var elt = windowRelaticsAPI.GetInstanceElement('@ID');
    if (elt && elt.Element) {
      return elt.Element.ID;
    }
  }
  return undefined;
}

async function getUserName() {
  if (window.RELATICS_SIMULATION) {
    await setTimeout(() => 'Simulated user', 1000);
  } else {
    var result = await fetch('../portal/GetCompositeItemViewObject', {
      method: 'get'
    })
      .then(response => response.json())
      .then(response => response.PersonalItem.UserName);
    return await result;
  }
}

function calculateIframeHeight(rootsLength, pagination, creatingIframe) {
  /**
   * 38 -> height of table header
   * 80 -> height of header
   * 30 -> 15 padding top, 15 padding bottom
   * 10 -> extra height
   * 43 -> height of pagination
   * rootsLength * 38 -> 38 height of row, rootslength is amount of root rows -> is only necessary when creating the iframe
   */

  return (
    (creatingIframe ? rootsLength * 38 : rootsLength) +
    38 +
    80 +
    30 +
    10 +
    (pagination && +43)
  );
}

async function createAddonIframe(
  baseUrl,
  configurationId,
  containerId,
  instanceId,
  urlParameters,
  height
) {
  var mainContainer = document.getElementById(containerId);
  if (!mainContainer) {
    throw new Error('Container element is not found by id: ' + containerId);
  }

  var spanElement = document.createElement('SPAN');
  var loadingText = document.createTextNode(
    'Retrieving iframe information ...'
  );

  spanElement.style = 'display: flex; justify-content: center;';
  var iframe = document.createElement('iframe');
  iframe.width = '100%';
  iframe.style = `min-height: ${
    (height && height.min) || '400px'
  }; max-height: ${(height && height.max) || '600px'};`;
  iframe.frameBorder = '0';
  iframe.id = instanceId;
  iframe.referrerPolicy = "no-referrer-when-downgrade";
  iframe.setAttribute("allow", "clipboard-read; clipboard-write");

  var parameterValues = item => ({
    id: item.id,
    type: item.type
  });

  if (configurationId) {
    spanElement.appendChild(loadingText);
    mainContainer.appendChild(spanElement);

    var parameters = urlParameters
      ? await Promise.all(
          urlParameters.map(async item => {
            switch (item.id) {
              case 'username':
                return {
                  value: await getUserName(),
                  ...parameterValues(item)
                };
              case 'wid':
                return {
                  value: getWorkspaceId(),
                  ...parameterValues(item)
                };
              case 'subjectId':
                return {
                  value: getSubjectId(),
                  ...parameterValues(item)
                };
              default:
                return {
                  value: item.value,
                  id: item.id,
                  type: item.type
                };
            }
          })
        )
      : [];

    const encodedParameters = btoa(JSON.stringify(parameters));

    iframe.setAttribute('height', '200px'); // loading height
    iframe.setAttribute(
      'src',
      `${baseUrl}index.html?configurationId=${configurationId}&params=${encodedParameters}`
    );
    iframe.onload = function () {
      mainContainer.removeChild(spanElement);
    };
  } else {
    iframe.setAttribute('src', `${baseUrl}index.html?configurationId=`);
  }

  mainContainer.appendChild(iframe);

  bindEvent(window, 'message', function (e) {
      if (typeof e.data !== 'string') {
        return;
      }
      var message = e.data ? JSON.parse(e.data) : { messageType: '', height: undefined, pagination: undefined };
      var iframeEl = document.getElementById(`${instanceId}`);
      if (iframeEl && message.messageType === `${configurationId}_resizing`) {
        var rowsHeight = calculateIframeHeight(message.height, message.pagination, false);
        iframeEl.height = `${rowsHeight}px`;
      }
  });
}
// MHE (2020-11-10): Removed this as using window.lacesFetch will cause problems if multiple instances are used
if (window.lacesFetch) {
    createAddonIframe(
      window.lacesFetch.baseUrl,
      window.lacesFetch.configurationId,
      window.lacesFetch.containerId,
      window.lacesFetch.instanceId,
      window.lacesFetch.urlParameters,
      window.lacesFetch.height
    );

    // MHE (2020-11-10): Removed from here and added to end of createAddonIframe function
    // This prevents the use of (again the problematic) window.lacesFetch variable!
    bindEvent(window, 'message', function (e) {
      if (typeof e.data !== 'string') {
        return;
      }

      var message = e.data
        ? JSON.parse(e.data)
        : {
            messageType: '',
            height: undefined,
            pagination: undefined
          };

      var iframeEl = document.getElementById(window.lacesFetch.instanceId);

      if (
        iframeEl &&
        message.messageType === `${window.lacesFetch.configurationId}_resizing`
      ) {
        var rowsHeight = calculateIframeHeight(
          message.height,
          message.pagination,
          false
        );

        iframeEl.height = `${rowsHeight}px`;
      }
    });
}

function bindEvent(element, eventName, eventHandler) {
  if (element && element.addEventListener) {
    element.addEventListener(eventName, eventHandler, false);
  } else if (element.attachEvent) {
    element.attachEvent('on' + eventName, eventHandler);
  } else {
    return;
  }
}
