import {
  formatDateForApi,
  parseDateFromApi,
  formatDateForDisplay,
} from '../date';
import { describe, expect, it } from '@jest/globals';

describe('formatDateForApi', () => {
  it('returns empty string for undefined', () => {
    expect(formatDateForApi(undefined)).toBe('');
  });

  it('formats date as YYYY-MM-DD without UTC shift', () => {
    expect(formatDateForApi(new Date(2024, 0, 15))).toBe('2024-01-15');
  });

  it('pads single-digit month and day', () => {
    expect(formatDateForApi(new Date(2024, 2, 5))).toBe('2024-03-05');
  });

  it('handles last day of year', () => {
    expect(formatDateForApi(new Date(2024, 11, 31))).toBe('2024-12-31');
  });
});

describe('parseDateFromApi', () => {
  it('returns undefined for undefined', () => {
    expect(parseDateFromApi(undefined)).toBeUndefined();
  });

  it('returns undefined for empty string', () => {
    expect(parseDateFromApi('')).toBeUndefined();
  });

  it('returns undefined for non-date string', () => {
    expect(parseDateFromApi('not-a-date')).toBeUndefined();
  });

  it('parses a valid YYYY-MM-DD string to local Date', () => {
    const result = parseDateFromApi('2024-01-15');
    expect(result).toBeInstanceOf(Date);
    expect(result?.getFullYear()).toBe(2024);
    expect(result?.getMonth()).toBe(0);
    expect(result?.getDate()).toBe(15);
  });

  it('roundtrips with formatDateForApi', () => {
    const date = new Date(2024, 5, 20);
    const parsed = parseDateFromApi(formatDateForApi(date));
    expect(parsed?.getFullYear()).toBe(date.getFullYear());
    expect(parsed?.getMonth()).toBe(date.getMonth());
    expect(parsed?.getDate()).toBe(date.getDate());
  });
});

describe('formatDateForDisplay', () => {
  it('returns empty string for undefined', () => {
    expect(formatDateForDisplay(undefined)).toBe('');
  });

  it('returns a non-empty string for a valid date', () => {
    expect(formatDateForDisplay(new Date(2024, 0, 15))).toBeTruthy();
  });

  it('includes the day, month and year', () => {
    const result = formatDateForDisplay(new Date(2024, 0, 15));
    expect(result).toMatch(/15/);
    expect(result).toMatch(/2024/);
  });
});
