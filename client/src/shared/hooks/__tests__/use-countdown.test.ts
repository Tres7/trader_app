import { describe, expect, it, jest, beforeEach, afterEach } from '@jest/globals';
import { renderHook, act } from '@testing-library/react-native';
import { useCountdown } from '../use-countdown';

describe('useCountdown', () => {
  beforeEach(() => {
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('initializes countdown to the given value', () => {
    const { result } = renderHook(() => useCountdown(10));
    expect(result.current.countdown).toBe(10);
  });

  it('defaults to 30 seconds', () => {
    const { result } = renderHook(() => useCountdown());
    expect(result.current.countdown).toBe(30);
  });

  it('decrements by 1 each second', () => {
    const { result } = renderHook(() => useCountdown(10));

    act(() => { jest.advanceTimersByTime(3000); });

    expect(result.current.countdown).toBe(7);
  });

  it('stops at 0 and does not go negative', () => {
    const { result } = renderHook(() => useCountdown(3));

    act(() => { jest.advanceTimersByTime(10000); });

    expect(result.current.countdown).toBe(0);
  });

  it('restartCountdown resets to initial value', () => {
    const { result } = renderHook(() => useCountdown(10));

    act(() => { jest.advanceTimersByTime(5000); });
    expect(result.current.countdown).toBe(5);

    act(() => { result.current.restartCountdown(); });
    expect(result.current.countdown).toBe(10);
  });

  it('continues decrementing after restart', () => {
    const { result } = renderHook(() => useCountdown(10));

    act(() => {
      result.current.restartCountdown();
      jest.advanceTimersByTime(3000);
    });

    expect(result.current.countdown).toBe(7);
  });

  it('clears the interval on unmount', () => {
    const spy = jest.spyOn(global, 'clearInterval');
    const { unmount } = renderHook(() => useCountdown(10));

    unmount();

    expect(spy).toHaveBeenCalled();
    spy.mockRestore();
  });
});