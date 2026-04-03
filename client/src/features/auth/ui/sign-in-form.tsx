import { SocialConnections } from '@/src/features/auth/ui/social-connections';
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
import { Pressable, type TextInput, View } from 'react-native';
import { useAuthStore } from '../store/auth-store';
import axios from 'axios';
import { login } from '../api/auth-api';
import { ErrorAlert } from '@/src/shared/ui/feedback/error-alert';
 
export function SignInForm() {
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [errorMessage, setErrorMessage] = React.useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = React.useState(false);
  const [isPasswordVisible, setIsPasswordVisible] = React.useState(false);


  const passwordInputRef = React.useRef<TextInput>(null);
 
  function onEmailSubmitEditing() {
    passwordInputRef.current?.focus();
  }
 
  async function onSubmit() {
    if (isSubmitting) {
      return;
    }

    setErrorMessage(null);
    setIsSubmitting(true);

    try {
      const response = await login ({
        email,
        password
      });
      await useAuthStore.getState().setSession({
        accessToken: response.accessToken,
        user: {
          userId: response.userId,
          email: response.email,
          firstName: response.firstName
        }
      });
      router.replace('/(tabs)');
    } catch(error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;

        if (status === 401) {
          setErrorMessage('Email ou mot de passe invalide');
        } else if (status === 403) {
          setErrorMessage('Veuillez verifier votre email avant de vous connecter');
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
          <CardTitle className="text-center text-xl sm:text-left">Connexion</CardTitle>
          <CardDescription className="text-center sm:text-left">
            Bon retour! Connectez-vous pour continuer
          </CardDescription>
        </CardHeader>
        <CardContent className="gap-6">
          <View className="gap-6">
            <View className="gap-1.5">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                value={email}
                onChangeText={setEmail}
                placeholder="m@example.com"
                keyboardType="email-address"
                autoComplete="email"
                autoCapitalize="none"
                onSubmitEditing={onEmailSubmitEditing}
                returnKeyType="next"
                submitBehavior="submit"
              />
            </View>
            <View className="gap-1.5">
              <View className="flex-row items-center">
                <Label htmlFor="password">Mot de passe</Label>
                <Button
                  variant="link"
                  size="sm"
                  className="web:h-fit ml-auto h-4 px-1 py-0 sm:h-4"
                  onPress={() => {
                    router.push('/forgot-password')
                  }}>
                  <Text className="font-normal leading-4">Mot de passe oublié?</Text>
                </Button>
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
            <Button className="w-full" onPress={onSubmit} disabled={isSubmitting}>
              <Text>{isSubmitting? 'Connexion...' : 'Se connecter'}</Text>
            </Button>
          </View>
          {errorMessage ? <ErrorAlert title={errorMessage} /> : null}

          <Text className="text-center text-sm">
            Pas encore de compte?{' '}
            <Pressable
              className="items-center justify-center"
              onPress={() => {
                router.push('/sign-up');
              }}>
              <Text className="text-sm underline underline-offset-4">Inscription</Text>
            </Pressable>
          </Text>
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
