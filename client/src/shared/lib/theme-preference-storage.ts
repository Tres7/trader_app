import * as SecureStore from 'expo-secure-store';

const THEME_PREFERENCE_KEY = 'theme_preference';

export type ThemePreference = 'light' | 'dark' | 'system';

export async function saveThemePreference(preference: ThemePreference): Promise<void> {
  await SecureStore.setItemAsync(THEME_PREFERENCE_KEY, preference);
}

export async function getThemePreference(): Promise<ThemePreference | null> {
  const value = await SecureStore.getItemAsync(THEME_PREFERENCE_KEY);
  return value === 'light' || value === 'dark' || value === 'system' ? value : null;
}
