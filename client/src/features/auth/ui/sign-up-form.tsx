import { SocialConnections } from '@/src/features/auth/ui/social-connections';
import DateTimePicker from '@react-native-community/datetimepicker';
import { Button } from '@/src/shared/ui/primitives/button';
import { router } from 'expo-router';
import { Eye, EyeOff } from 'lucide-react-native';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/src/shared/ui/primitives/card';
import { Input } from '@/src/shared/ui/primitives/input';
import { Label } from '@/src/shared/ui/primitives/label';
import { Separator } from '@/src/shared/ui/primitives/separator';
import { Text } from '@/src/shared/ui/primitives/text';
import * as React from 'react';
import { Platform, Pressable, TextInput, View } from 'react-native';
import axios from 'axios';
import { formatDateForApi, formatDateForDisplay } from '@/src/shared/lib/date';
import { register } from '../api/auth-api';
import { ErrorAlert } from '@/src/shared/ui/feedback/error-alert';

export function SignUpForm() {
  const passwordInputRef = React.useRef<TextInput>(null);
  const firstNameInputRef = React.useRef<TextInput>(null);
  const lastNameInputRef = React.useRef<TextInput>(null);
  const birthDateInputRef = React.useRef<TextInput>(null);
  const countryInputRef = React.useRef<TextInput>(null);

  const [firstName, setFirstName] = React.useState('');
  const [lastName, setLastName] = React.useState('');
  const [email, setEmail] = React.useState('');
  const [country, setCountry] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [errorMessage, setErrorMessage] = React.useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = React.useState(false);
  const [isPasswordVisible, setIsPasswordVisible] = React.useState(false);


  const [birthDate, setBirthDate] = React.useState<Date | undefined>(undefined);
  const [showDatePicker, setShowDatePicker] = React.useState(false);

  function onFirstNameSubmitEditing() {
    firstNameInputRef.current?.focus;
  }

  function onLastNameSubmitEditing() {
    birthDateInputRef.current?.focus();
  }

  function onBirthDateSubmitEditing() {
    countryInputRef.current?.focus();
  }

  function onCountrySubmitEditing() {
    passwordInputRef.current?.focus();
  }

  function onEmailSubmitEditing() {
    passwordInputRef.current?.focus();
  }

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

  function formatBirthDate(date?: Date) {
    if (!date) return '';

    return date.toLocaleDateString('fr-FR');
  }


  async function onSubmit() {
    if (isSubmitting) {
      return;
    }

    setErrorMessage(null);
    setIsSubmitting(true);

    try {
      const response = await register({
        firstName,
        lastName,
        birthDate: formatDateForApi(birthDate),
        email,
        password,
        country,
      });

      router.push({
        pathname: '/verify-email',
        params: {
          email: response.email ?? email,
        },
      });
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;

        if (status === 409) {
          setErrorMessage('Un compte existe deja avec cet email');
        } else if (status === 400) {
          setErrorMessage('Veuillez verifier les informations du formulaire');
        } else {
          setErrorMessage('Une erreur est survenue. Veuillez reessayer.');
        }
      } else {
        setErrorMessage('Une erreur est survenue. Veuillez reessayer.');
      }
    } finally {
      setIsSubmitting(false);
    }
  }


  return (
    <View className="gap-6">
      <Card className="border-border/0 sm:border-border shadow-none sm:shadow-sm sm:shadow-black/5">
        <CardHeader>
          <CardTitle className="text-center text-xl sm:text-left">Créez votre compte</CardTitle>
          <CardDescription className="text-center sm:text-left">
            Bienvenue sur notre application.
          </CardDescription>
        </CardHeader>
        <CardContent className="gap-6">
          <View className="gap-6">
            <View className="gap-1.5">
              <Label htmlFor="firstName">Prénom</Label>
              <Input
                ref={firstNameInputRef}
                id="firstName"
                value= {firstName}
                onChangeText={setFirstName}
                placeholder="Amah"
                autoCapitalize="words"
                onSubmitEditing={onFirstNameSubmitEditing}
                returnKeyType="next"
              />
            </View>
            <View className="gap-1.5">
              <Label htmlFor="lastName">Nom</Label>
              <Input
                ref={lastNameInputRef}
                id="lastName"
                value= {lastName}
                onChangeText={setLastName}
                placeholder="KWACTCHAH"
                autoCapitalize="words"
                onSubmitEditing={onLastNameSubmitEditing}
                returnKeyType="next"
              />
            </View>
            <View className="gap-1.5">
              <Label htmlFor="birthDate">Date de naissance</Label>
              <Pressable onPress={openBirthDatePicker}>
                <Input
                  id="birthDate"
                  placeholder="JJ/MM/AAAA"
                  value={formatDateForDisplay(birthDate)}
                  editable={false}
                  pointerEvents="none"
                  className="opacity-100"
                />
              </Pressable>

              {showDatePicker && (
                <DateTimePicker
                  value={birthDate ?? new Date(2000, 0, 1)}
                  mode="date"
                  display="default"
                  maximumDate={new Date()}
                  onChange={onBirthDateChange}
                />
              )}
            </View>
            <View className="gap-1.5">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                placeholder="m@example.com"
                keyboardType="email-address"
                autoComplete="email"
                value= {email}
                onChangeText={setEmail}
                autoCapitalize="none"
                onSubmitEditing={onEmailSubmitEditing}
                returnKeyType="next"
                submitBehavior="submit"
              />
            </View>
            <View className="gap-1.5">
              <Label htmlFor="country">Pays</Label>
              <Input
                ref={countryInputRef}
                id="country"
                value={country}
                onChangeText={setCountry}
                placeholder="TG"
                autoCapitalize="characters"
                onSubmitEditing={onCountrySubmitEditing}
                returnKeyType="next"
              />
            </View>
            <View className="gap-1.5">
              <View className="flex-row items-center">
                <Label htmlFor="password">Mot de passe</Label>
              </View>
              <Input
                ref={passwordInputRef}
                id="password"
                value={password}
                onChangeText={setPassword}
                secureTextEntry = {isPasswordVisible}
                returnKeyType="send"
                onSubmitEditing={onSubmit}
              />
                <Pressable
                  className="absolute right-3 top-7 bottom-0 justify-center"
                  onPress={() => setIsPasswordVisible((prev) => !prev)}
                  hitSlop={8}>
                  {isPasswordVisible ? (
                    <EyeOff size={18} color="#71717a" />
                  ) : (
                    <Eye size={18} color="#71717a" />
                  )}
                </Pressable>
            </View>
            {errorMessage ? <ErrorAlert title={errorMessage} /> : null}
            <Button className="w-full" onPress={onSubmit} disabled={isSubmitting}>
              <Text>{isSubmitting ? 'Creation...' : 'Continue'}</Text>
            </Button>
          </View>
          <View className="flex-row items-center justify-center gap-1">
            <Text className="text-sm">Vous avez déjà un compte?</Text>
                <Pressable
                    onPress={() => {
                       router.push('/sign-in');
                    }}>
                    <Text className="text-sm underline underline-offset-4">Se connecter</Text>
                </Pressable>
            </View>
          <View className="flex-row items-center">
            <Separator className="flex-1" />
            <Text className="text-muted-foreground px-4 text-sm">ou</Text>
            <Separator className="flex-1" />
          </View>
          <SocialConnections />
        </CardContent>
      </Card>
    </View>
  );
}
