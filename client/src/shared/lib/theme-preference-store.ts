import { create } from 'zustand';
import { colorScheme } from 'nativewind';

import {
  getThemePreference,
  saveThemePreference,
  ThemePreference,
} from './theme-preference-storage';

type ThemePreferenceState = {
  preference: ThemePreference;
  hydrate: () => Promise<void>;
  setPreference: (preference: ThemePreference) => Promise<void>;
};

export const useThemePreferenceStore = create<ThemePreferenceState>((set) => ({
  preference: 'system',

  hydrate: async () => {
    const preference = (await getThemePreference()) ?? 'system';
    colorScheme.set(preference);
    set({ preference });
  },

  setPreference: async (preference) => {
    colorScheme.set(preference);
    set({ preference });
    await saveThemePreference(preference);
  },
}));
