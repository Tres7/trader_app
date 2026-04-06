import { router } from 'expo-router';
import { View } from 'react-native';

import { useAuthStore } from '@/src/features/auth/store/auth-store';
import { Button } from '@/src/shared/ui/primitives/button';
import { Text } from '@/src/shared/ui/primitives/text';
import React from 'react';
import { GetCurrentUserApiResponse } from '@/src/features/auth/model/types';
import { getCurrentUser } from '@/src/features/auth/api/auth-api';
import axios from 'axios';
import { Card, CardContent, CardHeader, CardTitle } from '@/src/shared/ui/primitives/card';
import { LabeledValue } from '@/src/shared/ui/data-display/labeled-value';
import { ErrorAlert } from '@/src/shared/ui/feedback/error-alert';
import { User } from 'lucide-react-native';

export default function ProfileScreen() {
  const logout = useAuthStore((state) => state.logout);
  const [profile, setProfile] = React.useState<GetCurrentUserApiResponse | null>(null);
  const [isLoading, setIsLoading] = React.useState(true);
  const [errorMessage, setErrorMessage] = React.useState<string | null>(null);

    React.useEffect(() => {
    async function loadProfile() {
      setErrorMessage(null);
      setIsLoading(true);

      try {
        const response = await getCurrentUser();
        setProfile(response);
      } catch (error) {
        if (axios.isAxiosError(error)) {
          const status = error.response?.status;

          if (status === 401 || status === 403) {
            setErrorMessage("Votre session n'est plus valide.");
          } else {
            setErrorMessage("Impossible de charger le profil.");
          }
        } else {
          setErrorMessage("Impossible de charger le profil.");
        }
      } finally {
        setIsLoading(false);
      }
    }

    loadProfile();
  }, []);

  async function onLogout() {
    await logout();
    router.replace('/');
  }

  return (
    <View className="flex-1 bg-background px-5 pt-8 pb-6">
      <Text className="text-center text-3xl font-bold">Informations personnelles</Text>

      <View className="mt-8 flex-1 gap-4">
        {isLoading ? (
          <Text className="text-center text-muted-foreground">Chargement du profil...</Text>
        ) : null}

        {errorMessage ? <ErrorAlert title={errorMessage} /> : null}

        {!isLoading && !errorMessage && profile ? (
          <>
            <View className="flex-row gap-4">
              <LabeledValue label="Prénom" value={profile.firstName} className="flex-1" />
              <LabeledValue label="Nom" value={profile.lastName} className="flex-1" />
            </View>

            <LabeledValue label="Email" value={profile.email} />
            <LabeledValue label="Pays" value={profile.country} />
            <LabeledValue label="Date de naissance" value={profile.birthDate} />
            <LabeledValue
              label="Email vérifié"
              value={profile.emailVerified ? 'Oui' : 'Non'}
            />
          </>
        ) : null}
      </View>

      <View className="items-center pt-6">
        <Button variant="destructive" className="self-center" onPress={onLogout}>
          <Text>Se déconnecter</Text>
        </Button>
      </View>
    </View>
  );
}
