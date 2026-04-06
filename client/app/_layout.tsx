import '../global.css';

import { ThemeProvider } from '@react-navigation/native';
import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import { PortalHost } from '@rn-primitives/portal';
import 'react-native-reanimated';
import * as React from 'react';
import { Text } from '@/src/shared/ui/primitives/text';

import { NAV_THEME } from '@/src/shared/lib/theme';
import { useAuthStore } from '@/src/features/auth/store/auth-store';
import { View } from 'react-native';

export const unstable_settings = {
};

let hasBootstrappedSession = false;
export default function RootLayout() {
  const isHydrating = useAuthStore((state) => state.isHydrating);

    React.useEffect(() => {
      if (hasBootstrappedSession) {
        return;
      }
      hasBootstrappedSession = true;
      useAuthStore.getState().hydrateSession();
    }, []);

    if (isHydrating) {
    return (
      <ThemeProvider value={NAV_THEME.dark}>
        <View className="flex-1 items-center justify-center bg-background px-6">
          <Text className="text-center text-base text-muted-foreground">
            Chargement de la session...
          </Text>
        </View>
        <StatusBar style="dark" />
      </ThemeProvider>
    );
  }

  return (
    <ThemeProvider value={NAV_THEME.dark}>
      <Stack>
        <Stack.Screen name="index" options={{ headerShown: false }} />
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="(auth)" options={{ headerShown: false }} />
        <Stack.Screen name="profile/information" options={{ title: 'Informations personnelles' }}/>
        <Stack.Screen name="profile/security" options={{ title: 'Sécurité' }}/>
        <Stack.Screen name="modal" options={{ presentation: 'modal', title: 'Modal' }} />
      </Stack>
      <PortalHost />
      <StatusBar style="dark" />
    </ThemeProvider>
  );
}
