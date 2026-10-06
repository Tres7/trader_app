import * as React from 'react';
import { AppState } from 'react-native';

/**
 * Current date, refreshed every `intervalMs` and whenever the app comes back to the foreground.
 */
export function useNow(intervalMs = 30_000): Date {
  const [now, setNow] = React.useState(() => new Date());

  React.useEffect(() => {
    const tick = () => setNow(new Date());
    const interval = setInterval(tick, intervalMs);
    const subscription = AppState.addEventListener('change', (state) => {
      if (state === 'active') tick();
    });

    return () => {
      clearInterval(interval);
      subscription.remove();
    };
  }, [intervalMs]);

  return now;
}
