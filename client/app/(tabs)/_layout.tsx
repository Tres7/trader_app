import { Redirect, Tabs } from 'expo-router';
import React from 'react';

import { HapticTab } from '@/src/shared/ui/haptic-tab';
import { IconSymbol } from '@/src/shared/ui/icons/icon-symbol';
import { THEME } from '@/src/shared/lib/theme';
import { useColorScheme } from '@/src/shared/hooks/use-color-scheme';
import { useAuthStore } from '@/src/features/auth/store/auth-store';

export default function TabLayout() {
  const colorScheme = useColorScheme();
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const isHydrating = useAuthStore((state) => state.isHydrating);

  if (!isHydrating && !isAuthenticated) {
    return <Redirect href="/sign-in" />;
  }

  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor:
          colorScheme === 'dark' ? THEME.dark.primary : THEME.light.primary,
        headerShown: false,
        tabBarButton: HapticTab,
      }}>
      <Tabs.Screen
        name="index"
        options={{
          title: 'Accueil',
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="house.fill" color={color} />,
        }}
      />
      <Tabs.Screen
        name="plan"
        options={{
          title: 'Plan',
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="doc.text.fill" color={color} />,
        }}
      />
      <Tabs.Screen
        name="profile"
        options={{
          title: 'Profil',
          tabBarIcon: ({ color }) => <IconSymbol size={28} name="person.fill" color={color} />,
        }}
      />
    </Tabs>
  );
}