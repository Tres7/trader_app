import { View } from 'react-native';

import { cn } from '@/src/shared/lib/utils';
import { Text } from '@/src/shared/ui/primitives/text';
import { formatClock, formatDuration } from '../lib/session-status';
import { MarketSessionStatus } from '../model/sessions';

type SessionRowProps = {
  status: MarketSessionStatus;
};

export function SessionRow({ status }: SessionRowProps) {
  const { session, isOpen, opensAt, closesAt, nextChangeInMs } = status;

  return (
    <View testID={`market-session-${session.id}`} className="flex-row items-center justify-between py-3">
      <View>
        <Text className="text-lg font-semibold">{session.label}</Text>
        <Text className="text-sm text-muted-foreground">
          {formatClock(opensAt)} → {formatClock(closesAt)}
        </Text>
      </View>

      <View className="items-end">
        <View className="flex-row items-center gap-2">
          <View className={cn('h-2.5 w-2.5 rounded-full', isOpen ? 'bg-green-500' : 'bg-red-500')} />
          <Text className={cn('font-semibold', isOpen ? 'text-green-500' : 'text-red-500')}>
            {isOpen ? 'Ouvert' : 'Fermé'}
          </Text>
        </View>
        <Text className="text-sm text-muted-foreground">
          {isOpen ? 'Ferme' : 'Ouvre'} dans {formatDuration(nextChangeInMs)}
        </Text>
      </View>
    </View>
  );
}
