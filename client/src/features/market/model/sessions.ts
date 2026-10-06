import { MarketTimeZone } from '../lib/time-zones';

export type MarketSessionId = 'tokyo' | 'london' | 'new-york';

/**
 * A trading session, defined in the local time of its market.
 * Sessions only run on the market's weekdays (Monday to Friday).
 */
export interface MarketSession {
  id: MarketSessionId;
  label: string;
  timeZone: MarketTimeZone;
  openHour: number;
  closeHour: number;
}

export interface MarketSessionStatus {
  session: MarketSession;
  isOpen: boolean;
  /** Current occurrence when open, next one when closed. */
  opensAt: Date;
  closesAt: Date;
  /** Time until the session closes (when open) or opens (when closed). */
  nextChangeInMs: number;
}

export const MARKET_SESSIONS: MarketSession[] = [
  { id: 'tokyo', label: 'Tokyo', timeZone: 'Asia/Tokyo', openHour: 9, closeHour: 18 },
  { id: 'london', label: 'Londres', timeZone: 'Europe/London', openHour: 8, closeHour: 17 },
  { id: 'new-york', label: 'New York', timeZone: 'America/New_York', openHour: 8, closeHour: 17 },
];
