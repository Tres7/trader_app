import React from 'react';
import { View } from 'react-native';
import { Text } from '@/src/shared/ui/primitives/text';
import {
  AccordionContent, AccordionItem, AccordionTrigger,
} from '@/src/shared/ui/primitives/accordion';

type Props = {
  value: string;
  title: string;
  preview?: string | null;
  content?: string | null;
  actions: React.ReactNode;
};

export function PlanAccordionItem({ value, title, preview, content, actions }: Props) {
  return (
    <AccordionItem value={value}>
      <AccordionTrigger>
        <View className="flex-1">
          <Text className="font-semibold text-foreground">{title}</Text>
          {preview ? (
            <Text className="text-xs text-muted-foreground mt-0.5">{preview}</Text>
          ) : null}
        </View>
      </AccordionTrigger>
      <AccordionContent>
        <View className="flex-row items-start justify-between gap-3">
          <Text className="flex-1 text-sm text-foreground">
            {content || <Text className="text-muted-foreground">Non renseigné</Text>}
          </Text>
          <View className="flex-row gap-3">
            {actions}
          </View>
        </View>
      </AccordionContent>
    </AccordionItem>
  );
}