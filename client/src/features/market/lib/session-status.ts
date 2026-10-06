import { MarketSession, MarketSessionStatus } from '../model/sessions';
import { toZonedWallClock, zonedTimeToUtc } from './time-zones';

const MINUTE_MS = 60_000;
const FOREX_WEEKLY_HOUR = 17;

function isWeekend(weekday: number): boolean {
  return weekday === 0 || weekday === 6;
}

export function getSessionStatus(session: MarketSession, now: Date): MarketSessionStatus {
  const nowMs = now.getTime();
  const wallClock = toZonedWallClock(session.timeZone, nowMs);
  const year = wallClock.getUTCFullYear();
  const month = wallClock.getUTCMonth();
  const day = wallClock.getUTCDate();

  // The first weekday occurrence that has not closed yet is either the current or the next one.
  for (let dayOffset = 0; dayOffset <= 7; dayOffset++) {
    const weekday = new Date(Date.UTC(year, month, day + dayOffset)).getUTCDay();
    if (isWeekend(weekday)) continue;

    const opensAt = zonedTimeToUtc(session.timeZone, year, month, day + dayOffset, session.openHour);
    const closesAt = zonedTimeToUtc(session.timeZone, year, month, day + dayOffset, session.closeHour);
    if (nowMs >= closesAt) continue;

    const isOpen = nowMs >= opensAt;
    return {
      session,
      isOpen,
      opensAt: new Date(opensAt),
      closesAt: new Date(closesAt),
      nextChangeInMs: (isOpen ? closesAt : opensAt) - nowMs,
    };
  }

  throw new Error(`No upcoming occurrence found for session ${session.id}`);
}

/**
 * Forex trades continuously from Sunday 17:00 to Friday 17:00, New York time.
 */
export function isForexOpen(now: Date): boolean {
  const wallClock = toZonedWallClock('America/New_York', now.getTime());
  const weekday = wallClock.getUTCDay();
  const hour = wallClock.getUTCHours();

  if (weekday === 6) return false;
  if (weekday === 5) return hour < FOREX_WEEKLY_HOUR;
  if (weekday === 0) return hour >= FOREX_WEEKLY_HOUR;
  return true;
}

/**
 * Compact French duration: "2j 3h", "5h 46m", "12m". Rounded up to the next minute.
 */
export function formatDuration(ms: number): string {
  const totalMinutes = Math.max(1, Math.ceil(ms / MINUTE_MS));
  const days = Math.floor(totalMinutes / (24 * 60));
  const hours = Math.floor((totalMinutes % (24 * 60)) / 60);
  const minutes = totalMinutes % 60;

  if (days > 0) return `${days}j ${hours}h`;
  if (hours > 0) return `${hours}h ${minutes}m`;
  return `${minutes}m`;
}

/**
 * "HH:mm" in the device's time zone.
 */
export function formatClock(date: Date): string {
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  return `${hours}:${minutes}`;
}
