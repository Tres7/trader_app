import { describe, expect, it } from '@jest/globals';

import { getUtcOffsetMinutes, MarketTimeZone, zonedTimeToUtc } from '../time-zones';

const ZONES: MarketTimeZone[] = ['Asia/Tokyo', 'Europe/London', 'America/New_York'];

const formatters = new Map<MarketTimeZone, Intl.DateTimeFormat>();

/** Reference offset from Node's full ICU data. */
function intlOffsetMinutes(timeZone: MarketTimeZone, instant: number): number {
  let formatter = formatters.get(timeZone);
  if (!formatter) {
    formatter = new Intl.DateTimeFormat('en-US', {
      timeZone,
      hourCycle: 'h23',
      year: 'numeric',
      month: 'numeric',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
    });
    formatters.set(timeZone, formatter);
  }
  const parts = formatter.formatToParts(new Date(instant));
  const value = (type: string) => Number(parts.find((part) => part.type === type)?.value);
  const wallClockAsUtc = Date.UTC(value('year'), value('month') - 1, value('day'), value('hour'), value('minute'));
  return (wallClockAsUtc - instant) / 60_000;
}

describe('getUtcOffsetMinutes', () => {
  it.each<[MarketTimeZone, string, number]>([
    ['Europe/London', '2026-03-29T00:59:00Z', 0],
    ['Europe/London', '2026-03-29T01:00:00Z', 60],
    ['Europe/London', '2026-10-25T00:59:00Z', 60],
    ['Europe/London', '2026-10-25T01:00:00Z', 0],
    ['America/New_York', '2026-03-08T06:59:00Z', -300],
    ['America/New_York', '2026-03-08T07:00:00Z', -240],
    ['America/New_York', '2026-11-01T05:59:00Z', -240],
    ['America/New_York', '2026-11-01T06:00:00Z', -300],
    ['Asia/Tokyo', '2026-07-01T12:00:00Z', 540],
  ])('%s at %s is %i minutes', (zone, iso, expected) => {
    expect(getUtcOffsetMinutes(zone, Date.parse(iso))).toBe(expected);
  });

  it('matches the ICU time zone database every 6 hours from 2024 to 2032', () => {
    const start = Date.UTC(2024, 0, 1);
    const end = Date.UTC(2032, 0, 1);
    const step = 6 * 3_600_000;

    const mismatches: string[] = [];
    for (const zone of ZONES) {
      for (let instant = start; instant < end; instant += step) {
        const actual = getUtcOffsetMinutes(zone, instant);
        const expected = intlOffsetMinutes(zone, instant);
        if (actual !== expected) {
          mismatches.push(`${zone} ${new Date(instant).toISOString()}: ${actual} != ${expected}`);
        }
      }
    }

    expect(mismatches).toEqual([]);
  });
});

describe('zonedTimeToUtc', () => {
  it('converts a summer wall-clock time', () => {
    expect(new Date(zonedTimeToUtc('Europe/London', 2026, 6, 14, 8)).toISOString()).toBe(
      '2026-07-14T07:00:00.000Z'
    );
    expect(new Date(zonedTimeToUtc('America/New_York', 2026, 6, 14, 8)).toISOString()).toBe(
      '2026-07-14T12:00:00.000Z'
    );
  });

  it('converts a winter wall-clock time', () => {
    expect(new Date(zonedTimeToUtc('Europe/London', 2026, 0, 13, 8)).toISOString()).toBe(
      '2026-01-13T08:00:00.000Z'
    );
    expect(new Date(zonedTimeToUtc('America/New_York', 2026, 0, 13, 8)).toISOString()).toBe(
      '2026-01-13T13:00:00.000Z'
    );
  });

  it('converts a Tokyo time falling on the previous UTC day', () => {
    expect(new Date(zonedTimeToUtc('Asia/Tokyo', 2026, 6, 20, 8)).toISOString()).toBe(
      '2026-07-19T23:00:00.000Z'
    );
  });
});
