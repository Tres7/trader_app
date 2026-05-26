import { router } from 'expo-router';
import { ChevronRight, Lock, UserRound } from 'lucide-react-native';
import { View, Pressable } from 'react-native';

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
    <View className="flex-1 bg-background px-5 pt-8 pb-6">
      <View className="gap-4">
        <ProfileMenuItem
          icon={<UserRound size={24} color="#f4f4f5" />}
          label="Informations personnelles"
          onPress={() => router.push('/profile/information')}
        />

        <ProfileMenuItem
          icon={<Lock size={24} color="#f4f4f5" />}
          label="Sécurité"
          onPress={() => router.push('/profile/security')}
        />
      </View>

      <View className="items-center pt-6 mt-auto">
        <Button testID="profile-logout-button" variant="destructive" className="self-center" onPress={onLogout}>
          <Text>Se déconnecter</Text>
        </Button>
      </View>
    </View>
  );
}

type ProfileMenuItemProps = {
  icon: React.ReactNode;
  label: string;
  onPress: () => void;
};

function ProfileMenuItem({ icon, label, onPress }: ProfileMenuItemProps) {
  return (
    <Pressable
      onPress={onPress}
      className="flex-row items-center rounded-3xl border border-border bg-card px-5 py-5">
      <View className="mr-4">{icon}</View>

      <Text className="flex-1 text-xl font-semibold">{label}</Text>

      <ChevronRight size={26} color="#a1a1aa" />
    </Pressable>
  );
}
