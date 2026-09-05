import '../global.css';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ThemeProvider } from '@react-navigation/native';
import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { PortalHost } from '@rn-primitives/portal';
import 'react-native-reanimated';
import * as React from 'react';
import { Text } from '@/src/shared/ui/primitives/text';

import { NAV_THEME } from '@/src/shared/lib/theme';
import { useColorScheme } from '@/src/shared/hooks/use-color-scheme';
import { useAuthStore } from '@/src/features/auth/store/auth-store';
import { useThemePreferenceStore } from '@/src/shared/lib/theme-preference-store';
import { View } from 'react-native';

const queryClient = new QueryClient();

export const unstable_settings = {};

let hasBootstrappedSession = false;
let hasBootstrappedThemePreference = false;

export default function RootLayout() {
  const isHydrating = useAuthStore((state) => state.isHydrating);
  const colorScheme = useColorScheme() ?? 'light';
  const navTheme = NAV_THEME[colorScheme];

  React.useEffect(() => {
    if (hasBootstrappedSession) return;
    hasBootstrappedSession = true;
    useAuthStore.getState().hydrateSession();
  }, []);

  React.useEffect(() => {
    if (hasBootstrappedThemePreference) return;
    hasBootstrappedThemePreference = true;
    useThemePreferenceStore.getState().hydrate();
  }, []);

  if (isHydrating) {
    return (
      <QueryClientProvider client={queryClient}>
        <ThemeProvider value={navTheme}>
          <View className="flex-1 items-center justify-center bg-background px-6">
            <Text className="text-center text-base text-muted-foreground">
              Chargement de la session...
            </Text>
          </View>
          <StatusBar style="auto" />
        </ThemeProvider>
      </QueryClientProvider>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider value={navTheme}>
        <Stack>
          <Stack.Screen name="index" options={{ headerShown: false }} />
          <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
          <Stack.Screen name="(auth)" options={{ headerShown: false }} />
          <Stack.Screen name="profile/information" options={{ title: 'Informations personnelles' }} />
          <Stack.Screen name="profile/security" options={{ title: 'Sécurité' }} />
          <Stack.Screen name="modal" options={{ presentation: 'modal', title: 'Modal' }} />
        </Stack>
        <PortalHost />
        <StatusBar style="auto" />
      </ThemeProvider>
    </QueryClientProvider>
  );
}