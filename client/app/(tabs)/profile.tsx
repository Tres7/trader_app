import { router } from 'expo-router';
import { View, Pressable, Platform } from 'react-native';
import React from 'react';
import axios from 'axios';
import DateTimePicker from '@react-native-community/datetimepicker';
import { User } from 'lucide-react-native';

import { getCurrentUser, updateCurrentUserProfile } from '@/src/features/auth/api/auth-api';
import { GetCurrentUserApiResponse } from '@/src/features/auth/model/types';
import { useAuthStore } from '@/src/features/auth/store/auth-store';
import { ErrorAlert } from '@/src/shared/ui/feedback/error-alert';
import { Button } from '@/src/shared/ui/primitives/button';
import { Input } from '@/src/shared/ui/primitives/input';
import { Text } from '@/src/shared/ui/primitives/text';
import { formatDateForApi, formatDateForDisplay } from '@/src/shared/lib/date';
import { LabeledValue } from '@/src/shared/ui/data-display/labeled-value';

export default function ProfileScreen() {
  const logout = useAuthStore((state) => state.logout);
  const refreshUser = useAuthStore((state) => state.refreshUser);

  const [profile, setProfile] = React.useState<GetCurrentUserApiResponse | null>(null);
  const [firstName, setFirstName] = React.useState('');
  const [lastName, setLastName] = React.useState('');
  const [country, setCountry] = React.useState('');
  const [birthDate, setBirthDate] = React.useState<Date | undefined>(undefined);

  const [isLoading, setIsLoading] = React.useState(true);
  const [isSubmitting, setIsSubmitting] = React.useState(false);
  const [showDatePicker, setShowDatePicker] = React.useState(false);
  const [errorMessage, setErrorMessage] = React.useState<string | null>(null);
  const [successMessage, setSuccessMessage] = React.useState<string | null>(null);

  React.useEffect(() => {
    async function loadProfile() {
      setErrorMessage(null);
      setSuccessMessage(null);
      setIsLoading(true);

      try {
        const response = await getCurrentUser();

        setProfile(response);
        setFirstName(response.firstName);
        setLastName(response.lastName);
        setCountry(response.country);
        setBirthDate(response.birthDate ? new Date(response.birthDate) : undefined);
      } catch (error) {
        if (axios.isAxiosError(error)) {
          const status = error.response?.status;

          if (status === 401 || status === 403) {
            setErrorMessage("Votre session n'est plus valide.");
          } else {
            setErrorMessage('Impossible de charger le profil.');
          }
        } else {
          setErrorMessage('Impossible de charger le profil.');
        }
      } finally {
        setIsLoading(false);
      }
    }

    loadProfile();
  }, []);

  function openBirthDatePicker() {
    setShowDatePicker(true);
  }

  function onBirthDateChange(_event: unknown, selectedDate?: Date) {
    if (Platform.OS !== 'ios') {
      setShowDatePicker(false);
    }

    if (selectedDate) {
      setBirthDate(selectedDate);
    }
  }

  async function onSubmit() {
    if (isSubmitting) {
      return;
    }

    setErrorMessage(null);
    setSuccessMessage(null);

    if (!firstName.trim() || !lastName.trim() || !country.trim()) {
      setErrorMessage('Veuillez remplir tous les champs modifiables.');
      return;
    }

    if (!birthDate) {
      setErrorMessage('La date de naissance est obligatoire.');
      return;
    }

    setIsSubmitting(true);

    try {
      const updatedProfile = await updateCurrentUserProfile({
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        birthDate: formatDateForApi(birthDate),
        country: country.trim().toUpperCase(),
      });

      setProfile(updatedProfile);
      setFirstName(updatedProfile.firstName);
      setLastName(updatedProfile.lastName);
      setCountry(updatedProfile.country);
      setBirthDate(updatedProfile.birthDate ? new Date(updatedProfile.birthDate) : undefined);
      setSuccessMessage('Profil mis à jour avec succès.');

      refreshUser({
        userId: updatedProfile.userId,
        email: updatedProfile.email,
        firstName: updatedProfile.firstName,
      });
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        const data = error.response?.data as { message?: string } | undefined;
        const backendMessage = data?.message;

        if (status === 400) {
          setErrorMessage(backendMessage ?? 'Veuillez vérifier les informations du formulaire.');
        } else if (status === 401 || status === 403) {
          setErrorMessage("Votre session n'est plus valide.");
        } else if (status === 404) {
          setErrorMessage('Profil introuvable.');
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

        {successMessage ? (
          <Text className="text-center text-sm text-green-500">{successMessage}</Text>
        ) : null}

        {!isLoading && !errorMessage && profile ? (
          <>
            <View className="flex-row gap-4">
              <View className="flex-1 rounded-3xl border border-border bg-card px-4 py-4">
                <Text className="text-sm text-muted-foreground">Prénom</Text>
                <Input
                  value={firstName}
                  onChangeText={setFirstName}
                  autoCapitalize="words"
                  className="mt-2 border-0 bg-transparent px-0 py-0 text-2xl shadow-none"
                />
              </View>

              <View className="flex-1 rounded-3xl border border-border bg-card px-4 py-4">
                <Text className="text-sm text-muted-foreground">Nom</Text>
                <Input
                  value={lastName}
                  onChangeText={setLastName}
                  autoCapitalize="words"
                  className="mt-2 border-0 bg-transparent px-0 py-0 text-2xl shadow-none"
                />
              </View>
            </View>

            <LabeledValue label="Email" value={profile.email} />
            <LabeledValue label="Email vérifié" value={profile.emailVerified ? 'Oui' : 'Non'} />

            <View className="rounded-3xl border border-border bg-card px-4 py-4">
              <Text className="text-sm text-muted-foreground">Pays</Text>
              <Input
                value={country}
                onChangeText={setCountry}
                autoCapitalize="characters"
                className="mt-2 border-0 bg-transparent px-0 py-0 text-2xl shadow-none"
              />
            </View>

            <View className="rounded-3xl border border-border bg-card px-4 py-4">
              <Text className="text-sm text-muted-foreground">Date de naissance</Text>

              <Pressable onPress={openBirthDatePicker}>
                <Input
                  value={formatDateForDisplay(birthDate)}
                  editable={false}
                  pointerEvents="none"
                  className="mt-2 border-0 bg-transparent px-0 py-0 text-2xl opacity-100 shadow-none"
                />
              </Pressable>

              {showDatePicker ? (
                <DateTimePicker
                  value={birthDate ?? new Date(2000, 0, 1)}
                  mode="date"
                  display="default"
                  maximumDate={new Date()}
                  onChange={onBirthDateChange}
                />
              ) : null}
            </View>

            <Button className="items-center pt-2" onPress={onSubmit} disabled={isSubmitting}>
              <Text>{isSubmitting ? 'Enregistrement...' : 'Enregistrer les modifications'}</Text>
            </Button>
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