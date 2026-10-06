import { router } from 'expo-router';
import { ArrowRight, ChevronRight, ClipboardCheck } from 'lucide-react-native';
import * as React from 'react';
import { ActivityIndicator, Pressable, View } from 'react-native';

import { Card, CardContent, CardFooter, CardHeader } from '@/src/shared/ui/primitives/card';
import { Icon } from '@/src/shared/ui/primitives/icon';
import { Separator } from '@/src/shared/ui/primitives/separator';
import { Text } from '@/src/shared/ui/primitives/text';
import { usePlanSummary } from '../hooks/use-plan-summary';

export function PlanSummaryCard() {
  const { items, isLoading, isEmpty, isError } = usePlanSummary();

  function openPlan() {
    router.navigate('/plan');
  }

  return (
    <Pressable testID="home-plan-summary" accessibilityRole="button" onPress={openPlan}>
      <Card className="gap-4 rounded-3xl py-5">
        <CardHeader className="flex-row items-center justify-between px-5">
          <View className="flex-row items-center gap-2">
            <Icon as={ClipboardCheck} size={22} />
            <Text className="text-lg font-semibold">Mon Plan de Trading</Text>
          </View>
          <Icon as={ChevronRight} size={22} className="text-muted-foreground" />
        </CardHeader>

        <CardContent className="gap-3 px-5">
          {isLoading && <ActivityIndicator />}

          {isError && (
            <Text className="text-muted-foreground">Impossible de charger votre plan pour le moment.</Text>
          )}

          {isEmpty && (
            <Text className="text-muted-foreground">
              Votre plan est encore vide. Renseignez vos actifs, votre style et votre risk/reward pour les
              retrouver ici.
            </Text>
          )}

          {items.map((item, index) => (
            <React.Fragment key={item.key}>
              {index > 0 && <Separator />}
              <View>
                <Text className="text-sm text-muted-foreground">{item.label}</Text>
                <Text className="mt-1 text-base font-semibold" numberOfLines={2}>
                  {item.value}
                </Text>
              </View>
            </React.Fragment>
          ))}
        </CardContent>

        <CardFooter className="justify-center gap-1.5 px-5">
          <Text className="font-medium text-primary">
            {isEmpty ? 'Compléter mon plan' : 'Voir le plan complet'}
          </Text>
          <Icon as={ArrowRight} size={16} className="text-primary" />
        </CardFooter>
      </Card>
    </Pressable>
  );
}
