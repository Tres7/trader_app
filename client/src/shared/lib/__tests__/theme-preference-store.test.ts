import { describe, expect, it, jest, beforeEach } from '@jest/globals';

jest.mock('../theme-preference-storage', () => ({
  getThemePreference: jest.fn(),
  saveThemePreference: jest.fn(),
}));

jest.mock('nativewind', () => ({
  colorScheme: { set: jest.fn() },
}));

import { useThemePreferenceStore } from '../theme-preference-store';
import * as themePreferenceStorage from '../theme-preference-storage';
import { colorScheme } from 'nativewind';

const mockGetThemePreference = jest.mocked(themePreferenceStorage.getThemePreference);
const mockSaveThemePreference = jest.mocked(themePreferenceStorage.saveThemePreference);
const mockSetColorScheme = jest.mocked(colorScheme.set);

describe('useThemePreferenceStore', () => {
  beforeEach(() => {
    useThemePreferenceStore.setState({ preference: 'system' });
    jest.clearAllMocks();
  });

  describe('hydrate', () => {
    it('defaults to system when nothing is stored', async () => {
      mockGetThemePreference.mockResolvedValue(null);

      await useThemePreferenceStore.getState().hydrate();

      expect(useThemePreferenceStore.getState().preference).toBe('system');
      expect(mockSetColorScheme).toHaveBeenCalledWith('system');
    });

    it('applies the stored preference', async () => {
      mockGetThemePreference.mockResolvedValue('dark');

      await useThemePreferenceStore.getState().hydrate();

      expect(useThemePreferenceStore.getState().preference).toBe('dark');
      expect(mockSetColorScheme).toHaveBeenCalledWith('dark');
    });
  });

  describe('setPreference', () => {
    it('updates state, applies and persists the new preference', async () => {
      mockSaveThemePreference.mockResolvedValue(undefined);

      await useThemePreferenceStore.getState().setPreference('light');

      expect(useThemePreferenceStore.getState().preference).toBe('light');
      expect(mockSetColorScheme).toHaveBeenCalledWith('light');
      expect(mockSaveThemePreference).toHaveBeenCalledWith('light');
    });
  });
});
