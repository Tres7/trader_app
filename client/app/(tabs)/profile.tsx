import { router } from 'expo-router';
import { View } from 'react-native';

import { useAuthStore } from '@/src/features/auth/store/auth-store';
import { Button } from '@/src/shared/ui/primitives/button';
import { Text } from '@/src/shared/ui/primitives/text';

export default function ProfileScreen() {
  const logout = useAuthStore((state) => state.logout);

  async function onLogout() {
    await logout();
    router.replace('/');
  }

  return (
    <View className="flex-1 justify-end px-6 pb-10">
      <Button variant="destructive" className="self-center" onPress={onLogout}>
        <Text>Se déconnecter</Text>
      </Button>
    </View>
  );
}
