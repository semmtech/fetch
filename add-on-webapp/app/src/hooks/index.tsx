import React, { useRef, useState, useEffect, useLayoutEffect } from 'react';

// Hook for knowing when you hover over element
export const useHover = (
  children?: boolean
): [React.RefObject<HTMLDivElement>, boolean] => {
  const [value, setValue] = useState(false);

  const ref = useRef<HTMLDivElement>(null);

  const handleMouseEnter = () => setValue(true);
  const handleMouseLeave = () => setValue(false);

  const eventOne = children ? 'mouseenter' : 'mouseover';
  const eventTwo = children ? 'mouseleave' : 'mouseout';

  /* eslint-disable react-hooks/exhaustive-deps */
  useEffect(() => {
    const node = ref.current;
    if (node) {
      node.addEventListener(eventOne, handleMouseEnter);
      node.addEventListener(eventTwo, handleMouseLeave);

      return () => {
        node.removeEventListener(eventOne, handleMouseEnter);
        node.removeEventListener(eventTwo, handleMouseLeave);
      };
    }
  }, [ref.current]); // Recall only if ref changes

  return [ref, value];
};

// Hook for getting the window size
export const useWindowSize = () => {
  const [windowSize, setWindowSize] = useState([0, 0]);
  useLayoutEffect(() => {
    const updateSize = () => {
      setWindowSize([window.innerWidth, window.innerHeight]);
    };
    window.addEventListener('resize', updateSize);
    updateSize();
    return () => window.removeEventListener('resize', updateSize);
  }, []);
  return windowSize;
};
