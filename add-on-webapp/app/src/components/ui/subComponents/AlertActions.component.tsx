import React from 'reactn';
import styled from 'styled-components';
import copy from 'clipboard-copy';
import { toast } from 'react-toastify';
import FileCopy from '@material-ui/icons/FileCopy';
import Clear from '@material-ui/icons/Clear';

import { notify } from '../../../utils';
import { alertIcons } from '../../../utils/styles';
import { useHover } from '../../../hooks';

/**
 * Copies the content of the import messages.
 * Makes it easier to track in case of failure and the user can mail the admin with the necessary info.
 */

const copiedAlertContent = async ({
  errors,
  warnings
}: {
  errors: string[];
  warnings: string[];
}) => {
  const feedbackMessage =
    errors.length > 0
      ? `Errors:
${errors.join('\n')} 
      \nWarnings:
${warnings.join('\n')}`
      : `Warnings:
${warnings.join('\n')}`;

  return copy(feedbackMessage)
    .then(() =>
      notify('Copied to clipboard!', undefined, {
        type: toast.TYPE.SUCCESS,
        position: toast.POSITION.TOP_RIGHT,
        autoClose: 3000
      })
    )
    .catch(() =>
      notify(
        'Copied failed, try again or manually copy the text by clicking on it',
        undefined,
        {
          type: toast.TYPE.ERROR,
          position: toast.POSITION.TOP_RIGHT,
          autoClose: 3000
        }
      )
    );
};

// This is the component for the alert actions
export default ({
  closeToast,
  warnings,
  errors
}: {
  closeToast?: () => void;
  warnings?: string[];
  errors?: string[];
}) => {
  const [hoverRefCopy, isHoveredCopy] = useHover();
  const [hoverRef, isHovered] = useHover();

  return (
    <>
      {((errors && warnings) || warnings) && (
        <IconWrapper
          data-testid="CopyToClipBoard"
          ref={hoverRefCopy}
          onClick={async () =>
            copiedAlertContent({ errors: errors || [], warnings })
          }
        >
          <FileCopy style={alertIcons(isHoveredCopy)} />
        </IconWrapper>
      )}
      <IconWrapper ref={hoverRef} onClick={closeToast}>
        <Clear style={alertIcons(isHovered)} data-testid="CloseToast" />
      </IconWrapper>
    </>
  );
};

const IconWrapper = styled.div`
  width: 30px;
  height: 30px;
`;
