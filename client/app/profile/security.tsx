import * as React from 'react';
import axios from 'axios';
import { Eye, EyeOff } from 'lucide-react-native';
import { Pressable, View } from 'react-native';

import { updateCurrentUserPassword } from '@/src/features/auth/api/auth-api';
import { ErrorAlert } from '@/src/shared/ui/feedback/error-alert';
import { Button } from '@/src/shared/ui/primitives/button';
import { Input } from '@/src/shared/ui/primitives/input';
import { Text } from '@/src/shared/ui/primitives/text';

export default function ProfileSecurityScreen() {
  const [currentPassword, setCurrentPassword] = React.useState('');
  const [newPassword, setNewPassword] = React.useState('');
  const [errorMessage, setErrorMessage] = React.useState<string | null>(null);
  const [successMessage, setSuccessMessage] = React.useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = React.useState(false);

  const [isCurrentPasswordVisible, setIsCurrentPasswordVisible] = React.useState(false);
  const [isNewPasswordVisible, setIsNewPasswordVisible] = React.useState(false);

  const hasChanges = currentPassword.trim().length > 0 || newPassword.trim().length > 0;

  async function onSubmit() {
    if (isSubmitting) {
      return;
    }

    setErrorMessage(null);
    setSuccessMessage(null);

    if (!currentPassword.trim() || !newPassword.trim()) {
      setErrorMessage('Veuillez remplir les deux champs.');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await updateCurrentUserPassword({
        currentPassword,
        newPassword,
      });

      setCurrentPassword('');
      setNewPassword('');
      setSuccessMessage(response.message || 'Mot de passe mis à jour avec succès.');
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        const data = error.response?.data as { message?: string } | undefined;
        const backendMessage = data?.message;

        if (status === 401) {
          setErrorMessage('Le mot de passe actuel est incorrect.');
        } else if (status === 400) {
          setErrorMessage(backendMessage ?? 'Veuillez vérifier les informations saisies.');
        } else if (status === 404) {
          setErrorMessage('Utilisateur introuvable.');
        } else {
          setErrorMessage('Une erreur est survenue. Veuillez réessayer.');
        }
      } else {
        setErrorMessage('Une erreur est survenue. Veuillez réessayer.');
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <View className="flex-1 bg-background px-5 pt-8 pb-6">
      <Text className="text-center text-3xl font-bold">Sécurité</Text>

      <View className="mt-8 gap-4">
        <View className="rounded-3xl border border-border bg-card px-4 py-4">
          <Text className="text-sm text-muted-foreground">Mot de passe actuel</Text>

          <View className="relative mt-2">
            <Input
              value={currentPassword}
              onChangeText={setCurrentPassword}
              secureTextEntry={!isCurrentPasswordVisible}
              className="border-0 bg-transparent px-0 py-0 pr-12 text-2xl shadow-none"
            />

            <Pressable
              className="absolute right-0 top-0 bottom-0 justify-center"
              onPress={() => setIsCurrentPasswordVisible((prev) => !prev)}
              hitSlop={8}>
              {isCurrentPasswordVisible ? (
                <EyeOff size={18} color="#71717a" />
              ) : (
                <Eye size={18} color="#71717a" />
              )}
            </Pressable>
          </View>
        </View>

        <View className="rounded-3xl border border-border bg-card px-4 py-4">
          <Text className="text-sm text-muted-foreground">Nouveau mot de passe</Text>

          <View className="relative mt-2">
            <Input
              value={newPassword}
              onChangeText={setNewPassword}
              secureTextEntry={!isNewPasswordVisible}
              className="border-0 bg-transparent px-0 py-0 pr-12 text-2xl shadow-none"
            />

            <Pressable
              className="absolute right-0 top-0 bottom-0 justify-center"
              onPress={() => setIsNewPasswordVisible((prev) => !prev)}
              hitSlop={8}>
              {isNewPasswordVisible ? (
                <EyeOff size={18} color="#71717a" />
              ) : (
                <Eye size={18} color="#71717a" />
              )}
            </Pressable>
          </View>
        </View>

        {errorMessage ? <ErrorAlert title={errorMessage} /> : null}

        {successMessage ? (
          <Text className="text-center text-sm text-green-500">{successMessage}</Text>
        ) : null}

        <View className="items-center pt-2">
          <Button
            className="self-center"
            onPress={onSubmit}
            disabled={isSubmitting || !hasChanges}>
            <Text>{isSubmitting ? 'Enregistrement...' : 'Mettre à jour le mot de passe'}</Text>
          </Button>
        </View>
      </View>
    </View>
  );
}