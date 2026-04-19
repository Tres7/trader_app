import React from 'react';
import {
  Modal,
  KeyboardAvoidingView,
  Platform,
  TouchableWithoutFeedback,
  TouchableOpacity,
  View,
  TextInput,
} from 'react-native';
import { X } from 'lucide-react-native';
import { Text } from '@/src/shared/ui/primitives/text';
import { Button } from '@/src/shared/ui/primitives/button';
import { Icon } from '@/src/shared/ui/primitives/icon';

interface PlanBottomSheetProps {
  visible: boolean;
  title: string;
  value: string;
  onClose: () => void;
  readOnly?: boolean;
  onChangeValue?: (text: string) => void;
  onConfirm?: () => void;
  confirmLabel?: string;
  placeholder?: string;
  fieldName?: string;
  onChangeFieldName?: (text: string) => void;
}

export function PlanBottomSheet({
  visible,
  title,
  value,
  onClose,
  readOnly = false,
  onChangeValue,
  onConfirm,
  confirmLabel = 'Valider',
  placeholder = 'Saisir le contenu...',
  fieldName,
  onChangeFieldName,
}: PlanBottomSheetProps) {
  const isCustomField = fieldName !== undefined && onChangeFieldName !== undefined;

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onRequestClose={onClose}
    >
      <TouchableWithoutFeedback onPress={onClose}>
        <View className="flex-1 bg-black/50" />
      </TouchableWithoutFeedback>

      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
        <View className="bg-card rounded-t-2xl px-4 pt-4 pb-8">

          {/* Header */}
          <View className="flex-row items-center justify-between mb-4">
            <Text className="font-semibold text-base text-foreground">{title}</Text>
            <TouchableOpacity onPress={onClose}>
              <Icon as={X} size={20} className="text-muted-foreground" />
            </TouchableOpacity>
          </View>

          {/* Nom du champ custom */}
          {isCustomField && (
            <View className="mb-3">
              <Text className="text-xs text-muted-foreground mb-1">Nom de la section</Text>
              <TextInput
                value={fieldName}
                onChangeText={onChangeFieldName}
                editable={!readOnly}
                placeholder="Nom de la section..."
                placeholderTextColor="#666"
                style={{
                  padding: 12,
                  borderRadius: 8,
                  backgroundColor: '#1a1a1a',
                  color: '#fff',
                  fontSize: 14,
                }}
              />
            </View>
          )}

          {/* Valeur / Contenu */}
          <View>
            {isCustomField && (
              <Text className="text-xs text-muted-foreground mb-1">Valeur</Text>
            )}
            <TextInput
              value={value}
              onChangeText={onChangeValue}
              editable={!readOnly}
              multiline
              autoFocus={!isCustomField}
              placeholder={readOnly ? undefined : placeholder}
              placeholderTextColor="#666"
              style={{
                textAlignVertical: 'top',
                minHeight: 100,
                padding: 12,
                borderRadius: 8,
                backgroundColor: readOnly ? '#111' : '#1a1a1a',
                color: readOnly ? '#aaa' : '#fff',
                fontSize: 14,
              }}
            />
          </View>

          {/* Bouton confirm */}
          {!readOnly && onConfirm && (
            <Button onPress={onConfirm} className="mt-4">
              <Text>{confirmLabel}</Text>
            </Button>
          )}

        </View>
      </KeyboardAvoidingView>
    </Modal>
  );
}