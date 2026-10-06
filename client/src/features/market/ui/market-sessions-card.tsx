import { Clock } from 'lucide-react-native';
import * as React from 'react';
import { View } from 'react-native';

import { cn } from '@/src/shared/lib/utils';
import { Card, CardContent, CardHeader } from '@/src/shared/ui/primitives/card';
import { Icon } from '@/src/shared/ui/primitives/icon';
import { Separator } from '@/src/shared/ui/primitives/separator';
import { Text } from '@/src/shared/ui/primitives/text';
import { useMarketSessions } from '../hooks/use-market-sessions';
import { SessionRow } from './session-row';

export function MarketSessionsCard() {
  const { sessions, isForexOpen } = useMarketSessions();

  return (
    <Card className="gap-2 rounded-3xl py-5">
      <CardHeader className="flex-row flex-wrap items-center justify-between gap-2 px-5">
        <View className="flex-row items-center gap-2">
          <Icon as={Clock} size={22} />
          <Text className="text-lg font-semibold">Sessions de Marché</Text>
        </View>

        <View
          className={cn(
            'flex-row items-center gap-1.5 rounded-full px-3 py-1',
            isForexOpen ? 'bg-green-500/15' : 'bg-red-500/15'
          )}>
          <View className={cn('h-2 w-2 rounded-full', isForexOpen ? 'bg-green-500' : 'bg-red-500')} />
          <Text className={cn('text-xs font-medium', isForexOpen ? 'text-green-500' : 'text-red-500')}>
            {isForexOpen ? 'Marché Ouvert (Forex)' : 'Marché Fermé (Forex)'}
          </Text>
        </View>
      </CardHeader>

      <CardContent className="px-5">
        {sessions.map((status, index) => (
          <React.Fragment key={status.session.id}>
            {index > 0 && <Separator />}
            <SessionRow status={status} />
          </React.Fragment>
        ))}
      </CardContent>
    </Card>
  );
}
