import { describe, expect, it } from '@jest/globals';

import { MARKET_SESSIONS, MarketSessionId } from '../../model/sessions';
import { formatDuration, getSessionStatus, isForexOpen } from '../session-status';

const HOUR_MS = 3_600_000;

function session(id: MarketSessionId) {
  const found = MARKET_SESSIONS.find((s) => s.id === id);
  if (!found) throw new Error(`Unknown session ${id}`);
  return found;
}

function statusAt(id: MarketSessionId, iso: string) {
  return getSessionStatus(session(id), new Date(iso));
}

describe('getSessionStatus', () => {
  it('is open during the London session in summer (BST)', () => {
    const status = statusAt('london', '2026-07-14T10:00:00Z');

    expect(status.isOpen).toBe(true);
    expect(status.opensAt.toISOString()).toBe('2026-07-14T07:00:00.000Z');
    expect(status.closesAt.toISOString()).toBe('2026-07-14T16:00:00.000Z');
    expect(status.nextChangeInMs).toBe(6 * HOUR_MS);
  });

  it('follows winter time for London (GMT)', () => {
    const status = statusAt('london', '2026-01-13T07:30:00Z');

    expect(status.isOpen).toBe(false);
    expect(status.opensAt.toISOString()).toBe('2026-01-13T08:00:00.000Z');
    expect(status.nextChangeInMs).toBe(0.5 * HOUR_MS);
  });

  it('uses New York daylight time while London is still on winter time', () => {
    const status = statusAt('new-york', '2026-03-10T12:30:00Z');

    expect(status.isOpen).toBe(true);
    expect(status.opensAt.toISOString()).toBe('2026-03-10T12:00:00.000Z');
  });

  it('points to the next day once the session has closed', () => {
    const status = statusAt('new-york', '2026-07-14T21:30:00Z');

    expect(status.isOpen).toBe(false);
    expect(status.opensAt.toISOString()).toBe('2026-07-15T12:00:00.000Z');
    expect(status.nextChangeInMs).toBe(14.5 * HOUR_MS);
  });

  it('skips the weekend', () => {
    const status = statusAt('london', '2026-07-17T21:30:00Z');

    expect(status.isOpen).toBe(false);
    expect(status.opensAt.toISOString()).toBe('2026-07-20T07:00:00.000Z');
  });

  it('opens Tokyo on Monday morning Tokyo time, which is Sunday in UTC', () => {
    const beforeOpen = statusAt('tokyo', '2026-07-18T10:00:00Z');
    expect(beforeOpen.isOpen).toBe(false);
    expect(beforeOpen.opensAt.toISOString()).toBe('2026-07-20T00:00:00.000Z');
    expect(beforeOpen.nextChangeInMs).toBe(38 * HOUR_MS);

    const atOpen = statusAt('tokyo', '2026-07-20T00:00:00Z');
    expect(atOpen.isOpen).toBe(true);
    expect(atOpen.closesAt.toISOString()).toBe('2026-07-20T09:00:00.000Z');
  });

  it('is closed exactly at closing time', () => {
    const status = statusAt('london', '2026-07-14T16:00:00Z');

    expect(status.isOpen).toBe(false);
    expect(status.opensAt.toISOString()).toBe('2026-07-15T07:00:00.000Z');
  });
});

describe('isForexOpen', () => {
  it.each<[string, string, boolean]>([
    ['Wednesday', '2026-07-15T03:00:00Z', true],
    ['Friday before 17:00 New York', '2026-07-17T20:59:00Z', true],
    ['Friday at 17:00 New York', '2026-07-17T21:00:00Z', false],
    ['Saturday', '2026-07-18T12:00:00Z', false],
    ['Sunday before 17:00 New York', '2026-07-19T20:59:00Z', false],
    ['Sunday at 17:00 New York', '2026-07-19T21:00:00Z', true],
    ['Sunday at 17:00 New York in winter', '2026-01-18T22:00:00Z', true],
    ['Sunday before 17:00 New York in winter', '2026-01-18T21:30:00Z', false],
  ])('%s (%s) → %s', (_label, iso, expected) => {
    expect(isForexOpen(new Date(iso))).toBe(expected);
  });
});

describe('formatDuration', () => {
  it.each<[number, string]>([
    [(5 * 60 + 46) * 60_000, '5h 46m'],
    [46 * 60_000, '46m'],
    [30_000, '1m'],
    [(5 * 60 + 45) * 60_000 + 1, '5h 46m'],
    [(2 * 24 + 3) * HOUR_MS, '2j 3h'],
    [HOUR_MS, '1h 0m'],
  ])('%i ms → %s', (ms, expected) => {
    expect(formatDuration(ms)).toBe(expected);
  });
});
