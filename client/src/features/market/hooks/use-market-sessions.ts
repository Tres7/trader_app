import * as React from 'react';

import { useNow } from '@/src/shared/hooks/use-now';
import { getSessionStatus, isForexOpen } from '../lib/session-status';
import { MARKET_SESSIONS } from '../model/sessions';

export function useMarketSessions() {
  const now = useNow();

  return React.useMemo(
    () => ({
      sessions: MARKET_SESSIONS.map((session) => getSessionStatus(session, now)),
      isForexOpen: isForexOpen(now),
    }),
    [now]
  );
}
