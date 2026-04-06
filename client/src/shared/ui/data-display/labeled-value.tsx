import { Text } from '@/src/shared/ui/primitives/text';
import { View } from 'react-native';

type LabeledValueProps = {
  label: string;
  value: string;
  className?: string;
};

export function LabeledValue({ label, value, className }: LabeledValueProps) {
  return (
    <View className={`rounded-3xl border border-border bg-card px-4 py-4 ${className ?? ''}`}>
      <Text className="text-sm text-muted-foreground">{label}</Text>
      <Text className="mt-2 text-2xl">{value}</Text>
    </View>
  );
}