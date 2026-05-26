import { View } from 'react-native';
import { Text } from '@/src/shared/ui/primitives/text';
import { useAuthStore } from '@/src/features/auth/store/auth-store';

export default function HomeScreen() {
  const user = useAuthStore((state) => state.user);

  return (
    <View className="flex-1 items-center justify-center px-6">
      <Text testID="home-greeting" className="text-center text-3xl font-bold">
        {user?.firstName ? `Bonjour, ${user.firstName}` : 'Bonjour'}
      </Text>
      <Text className="text-muted-foreground mt-3 text-center text-base">
        Bienvenue dans votre espace TraderApp.
      </Text>
    </View>
  );
}