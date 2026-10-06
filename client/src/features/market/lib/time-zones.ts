/**
 * Daylight saving rules for the market time zones, computed by hand so that session
 * hours do not depend on the Intl time zone support of the JS engine (Hermes).
 * - UK (since 1996): BST from the last Sunday of March to the last Sunday of October, at 01:00 UTC.
 * - US (since 2007): EDT from the second Sunday of March 02:00 EST to the first Sunday of November 02:00 EDT.
 * - Japan: no daylight saving time.
 */
export type MarketTimeZone = 'Asia/Tokyo' | 'Europe/London' | 'America/New_York';

const MINUTE_MS = 60_000;
const HOUR_MS = 60 * MINUTE_MS;

function nthSundayOfMonthUtc(year: number, month: number, n: number): number {
  const firstWeekday = new Date(Date.UTC(year, month, 1)).getUTCDay();
  const firstSunday = 1 + ((7 - firstWeekday) % 7);
  return Date.UTC(year, month, firstSunday + (n - 1) * 7);
}

function lastSundayOfMonthUtc(year: number, month: number): number {
  const lastDay = new Date(Date.UTC(year, month + 1, 0));
  return Date.UTC(year, month, lastDay.getUTCDate() - lastDay.getUTCDay());
}

/**
 * Offset from UTC, in minutes, of the given time zone at the given instant.
 */
export function getUtcOffsetMinutes(timeZone: MarketTimeZone, instant: number): number {
  const year = new Date(instant).getUTCFullYear();

  switch (timeZone) {
    case 'Asia/Tokyo':
      return 9 * 60;
    case 'Europe/London': {
      const start = lastSundayOfMonthUtc(year, 2) + HOUR_MS;
      const end = lastSundayOfMonthUtc(year, 9) + HOUR_MS;
      return instant >= start && instant < end ? 60 : 0;
    }
    case 'America/New_York': {
      const start = nthSundayOfMonthUtc(year, 2, 2) + 7 * HOUR_MS;
      const end = nthSundayOfMonthUtc(year, 10, 1) + 6 * HOUR_MS;
      return instant >= start && instant < end ? -4 * 60 : -5 * 60;
    }
  }
}

/**
 * Wall-clock time of the time zone, as a Date whose UTC fields hold the local values.
 */
export function toZonedWallClock(timeZone: MarketTimeZone, instant: number): Date {
  return new Date(instant + getUtcOffsetMinutes(timeZone, instant) * MINUTE_MS);
}

/**
 * Instant (ms since epoch) of a wall-clock time in the time zone.
 * Day overflow is allowed (e.g. day 32 of January is February 1st).
 */
export function zonedTimeToUtc(
  timeZone: MarketTimeZone,
  year: number,
  month: number,
  day: number,
  hour: number,
  minute = 0
): number {
  const wallClockAsUtc = Date.UTC(year, month, day, hour, minute);
  const estimatedInstant = wallClockAsUtc - getUtcOffsetMinutes(timeZone, wallClockAsUtc) * MINUTE_MS;
  return wallClockAsUtc - getUtcOffsetMinutes(timeZone, estimatedInstant) * MINUTE_MS;
}
