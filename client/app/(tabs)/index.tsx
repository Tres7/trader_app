import { ScrollView, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { Text } from '@/src/shared/ui/primitives/text';
import { useAuthStore } from '@/src/features/auth/store/auth-store';
import { MarketSessionsCard } from '@/src/features/market/ui/market-sessions-card';
import { PlanSummaryCard } from '@/src/features/trading-plan/ui/plan-summary-card';

export default function HomeScreen() {
  const user = useAuthStore((state) => state.user);

  return (
    <SafeAreaView edges={['top']} className="flex-1 bg-background">
      <ScrollView contentContainerClassName="gap-5 px-5 pb-8 pt-6">
        <View className="items-center">
          <Text testID="home-greeting" className="text-center text-3xl font-bold">
            {user?.firstName ? `Bonjour, ${user.firstName}` : 'Bonjour'}
          </Text>
          <Text className="text-muted-foreground mt-2 text-center text-base">
            Bienvenue dans votre espace TraderApp.
          </Text>
        </View>

        <PlanSummaryCard />
        <MarketSessionsCard />
      </ScrollView>
    </SafeAreaView>
  );
}
