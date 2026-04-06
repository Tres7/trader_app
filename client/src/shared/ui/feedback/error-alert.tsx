import { Alert, AlertDescription, AlertTitle } from '@/src/shared/ui/primitives/alert';
import { Text } from '@/src/shared/ui/primitives/text';
import { AlertCircleIcon } from 'lucide-react-native';
import { View } from 'react-native';

interface ErrorAlertProps {
  title?: string;
  description?: string;
  items?: string[];
}

export function ErrorAlert({ title, description, items }: ErrorAlertProps) {
  if (!title && !description && (!items || items.length === 0)) {
    return null;
  }

  return (
    <Alert variant="destructive" icon={AlertCircleIcon}>
      {title ? <AlertTitle>{title}</AlertTitle> : null}
      {description ? <AlertDescription>{description}</AlertDescription> : null}

      {items?.length ? (
        <View role="list" className="ml-0.5 pb-2 pl-6">
          {items.map((item) => (
            <Text key={item} role="listitem" className="text-sm">
              <Text className="web:pr-2">•</Text>
              {item}
            </Text>
          ))}
        </View>
      ) : null}
    </Alert>
  );
}