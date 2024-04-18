var sendMessage = message => {
  window.parent.postMessage(message, '*');
};

const getUrlDecodedConfigurationId = name => {
  name = name.replace(/[[]/, '\\[').replace(/[\]]/, '\\]');
  const regex = new RegExp('[\\?&#]' + name + '=([^&#]*)');
  const results = regex.exec(window.location.search || window.location.hash);
  return results === null
    ? ''
    : decodeURIComponent(results[1].replace(/\+/g, ' '));
};

document.getElementById('root').addEventListener('mouseup', () => {
  var configId = getUrlDecodedConfigurationId('configurationId');
  var tableBody = document.getElementById(`${configId}_TableBody`);
  var pagination = document.getElementById(`${configId}_PaginationFooter`);

  const myObserver = new ResizeObserver(entries => {
    entries.forEach(entry => {
      var height = entry.target.scrollHeight;
      var msg = JSON.stringify({
        messageType: `${configId}_resizing`,
        height,
        pagination: Boolean(pagination)
      });
      sendMessage(msg);
    });
  });
  myObserver.observe(tableBody);
});
