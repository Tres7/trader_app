import { router } from 'expo-router';
import { ChevronRight, Lock, UserRound } from 'lucide-react-native';
import { View, Pressable } from 'react-native';

import { useAuthStore } from '@/src/features/auth/store/auth-store';
import { Button } from '@/src/shared/ui/primitives/button';
import { Separator } from '@/src/shared/ui/primitives/separator';
import { Text } from '@/src/shared/ui/primitives/text';
import { ThemeToggle } from '@/src/shared/ui/theme-toggle';

export default function ProfileScreen() {
  const logout = useAuthStore((state) => state.logout);

  async function onLogout() {
    await logout();
    router.replace('/');
  }

  return (
    <View className="flex-1 bg-background px-5 pt-12 pb-6">
      <Text className="mb-6 text-3xl font-bold">Mon Profil</Text>

      <View className="overflow-hidden rounded-3xl border border-border bg-card">
        <ProfileMenuItem
          icon={<UserRound size={24} color="#f4f4f5" />}
          label="Informations personnelles"
          onPress={() => router.push('/profile/information')}
        />

        <Separator />

        <ProfileMenuItem
          icon={<Lock size={24} color="#f4f4f5" />}
          label="Sécurité"
          onPress={() => router.push('/profile/security')}
        />
      </View>

      <View className="items-center pt-10">
        <ThemeToggle />
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
    <Pressable onPress={onPress} className="flex-row items-center px-5 py-5">
      <View className="mr-4">{icon}</View>

      <Text className="flex-1 text-xl font-semibold">{label}</Text>

      <ChevronRight size={26} color="#a1a1aa" />
    </Pressable>
  );
}
