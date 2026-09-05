import { Moon, Smartphone, Sun } from 'lucide-react-native';
import { Pressable, View } from 'react-native';
import Animated, { useAnimatedStyle, useSharedValue, withTiming } from 'react-native-reanimated';
import * as React from 'react';

import { useThemePreferenceStore } from '@/src/shared/lib/theme-preference-store';
import { ThemePreference } from '@/src/shared/lib/theme-preference-storage';
import { Text } from '@/src/shared/ui/primitives/text';

const TRACK_WIDTH = 216;
const OPTIONS: { value: ThemePreference; Icon: typeof Moon; label: string }[] = [
  { value: 'dark', Icon: Moon, label: 'Thème sombre' },
  { value: 'system', Icon: Smartphone, label: 'Thème système' },
  { value: 'light', Icon: Sun, label: 'Thème clair' },
];
const OPTION_WIDTH = TRACK_WIDTH / OPTIONS.length;
const MUTED_ICON_COLOR = '#a1a1aa';
const ACTIVE_ICON_COLOR = '#ffffff';
const THUMB_COLOR = '#3b82f6';

export function ThemeToggle() {
  const preference = useThemePreferenceStore((state) => state.preference);
  const setPreference = useThemePreferenceStore((state) => state.setPreference);
  const activeIndex = OPTIONS.findIndex((option) => option.value === preference);
  const translateX = useSharedValue(activeIndex * OPTION_WIDTH);

  React.useEffect(() => {
    translateX.value = withTiming(activeIndex * OPTION_WIDTH, { duration: 200 });
  }, [activeIndex, translateX]);

  const thumbStyle = useAnimatedStyle(() => ({
    transform: [{ translateX: translateX.value }],
  }));

  return (
    <View className="items-center gap-2">
      <View
        style={{ width: TRACK_WIDTH }}
        className="flex-row rounded-full border border-border bg-card p-1">
        <Animated.View
          style={[{ width: OPTION_WIDTH }, thumbStyle]}
          className="absolute bottom-1 top-1 rounded-full"
          pointerEvents="none">
          <View className="flex-1 rounded-full" style={{ backgroundColor: THUMB_COLOR }} />
        </Animated.View>

        {OPTIONS.map(({ value, Icon, label }) => (
          <Pressable
            key={value}
            accessibilityRole="button"
            accessibilityLabel={label}
            accessibilityState={{ selected: preference === value }}
            onPress={() => setPreference(value)}
            style={{ width: OPTION_WIDTH }}
            className="items-center justify-center py-2">
            <Icon size={20} color={preference === value ? ACTIVE_ICON_COLOR : MUTED_ICON_COLOR} />
          </Pressable>
        ))}
      </View>

      <Text className="text-sm text-muted-foreground">Thème</Text>
    </View>
  );
}
