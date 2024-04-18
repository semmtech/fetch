import { addReducer } from 'reactn';

// TODO: add encyption for sending the username and password with an basic authorization header
addReducer('login', async (global, userName: string, password: string) =>
  fetch(`/authenticate?username=${userName}&password=${password}`, {
    credentials: 'include',
    method: 'POST'
  })
    .then(response => (window.location.href = response.url))
    .catch((err: Error) => ({ error: err }))
);
