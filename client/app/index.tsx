import { router } from 'expo-router';
import { ScrollView, View } from 'react-native';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/src/shared/ui/primitives/card';
import { Button } from '@/src/shared/ui/primitives/button';
import { Text } from '@/src/shared/ui/primitives/text';

export default function PublicHomeScreen() {
  return (
    <ScrollView
      contentContainerClassName="flex-1 items-center justify-center p-4 py-8 sm:p-6"
      keyboardDismissMode="interactive">
      <View className="w-full max-w-md">
        <Card className="border-border/0 sm:border-border shadow-none sm:shadow-sm sm:shadow-black/5">
          <CardHeader className="gap-3">
            <CardTitle className="text-center text-3xl sm:text-left">
              TraderApp
            </CardTitle>
            <CardDescription className="text-center text-base sm:text-left">
              Ayez votre plan de trading à portée de main! Et soyez à l'affût de toute
              l'actualité
            </CardDescription>
          </CardHeader>

          <CardContent className="gap-4">
            <Button className="w-full" onPress={() => router.push('/sign-in')}>
              <Text>Connexion</Text>
            </Button>

            <Button variant="outline" className="w-full" onPress={() => router.push('/sign-up')}>
              <Text>Inscription</Text>
            </Button>
          </CardContent>
        </Card>
      </View>
    </ScrollView>
  );
}