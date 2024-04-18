import React from 'react';

// This is for getting the parameters of the URL
const urlParams = new URLSearchParams(window.location.search);

// This is for giving a feedback message (logged out and login with error)
export const feedbackMessage = (param: string, text: string) =>
  urlParams.has(param) ? (
    <span data-testid="feedbackMessage">{text}</span>
  ) : (
    undefined
  );
