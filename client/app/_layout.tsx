import '../global.css';

import { ThemeProvider } from '@react-navigation/native';
import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { PortalHost } from '@rn-primitives/portal';
import 'react-native-reanimated';
import * as React from 'react';

import { NAV_THEME } from '@/src/shared/lib/theme';
import { useAuthStore } from '@/src/features/auth/store/auth-store';

export const unstable_settings = {
};

export default function RootLayout() {
  const hydrateSession = useAuthStore((state) => state.hydrateSession);

    const isHydrating = useAuthStore((state) => state.isHydrating);

  React.useEffect(() => {
    hydrateSession();
  }, [hydrateSession]);

  if (isHydrating) {
    return null;
  }

  return (
    <ThemeProvider value={NAV_THEME.dark}>
      <Stack initialRouteName="index">
        <Stack.Screen name="index" options={{ headerShown: false }} />
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="(auth)" options={{ headerShown: false }} />
        <Stack.Screen name="modal" options={{ presentation: 'modal', title: 'Modal' }} />
      </Stack>
      <PortalHost />
      <StatusBar style="dark" />
    </ThemeProvider>
  );
}
