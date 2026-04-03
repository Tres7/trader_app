import { Button } from '@/src/shared/ui/primitives/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/src/shared/ui/primitives/card';
import { Input } from '@/src/shared/ui/primitives/input';
import { Label } from '@/src/shared/ui/primitives/label';
import { Text } from '@/src/shared/ui/primitives/text';
import axios from 'axios';
import { router } from 'expo-router';
import * as React from 'react';
import { Pressable, TextInput, View } from 'react-native';
import { resetPassword } from '../api/auth-api';
import { ErrorAlert } from '@/src/shared/ui/feedback/error-alert';
import { Eye, EyeOff } from 'lucide-react-native';

type ResetPasswordFormProps = {
  email: string;
};

 
export function ResetPasswordForm({ email }: ResetPasswordFormProps) {
  const codeInputRef = React.useRef<TextInput>(null);

  const [password, setPassword] = React.useState('');
  const [code, setCode] = React.useState('');
  const [errorMessage, setErrorMessage] = React.useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = React.useState(false);
  const [isPasswordVisible, setIsPasswordVisible] = React.useState(false);
 
  function onPasswordSubmitEditing() {
    codeInputRef.current?.focus();
  }
 
    async function onSubmit() {
    if (isSubmitting) {
      return;
    }

    setErrorMessage(null);

    if (!email) {
      setErrorMessage("Email manquant. Recommencez la procedure.");
      return;
    }

    if (!password.trim()) {
      setErrorMessage("Veuillez entrer un nouveau mot de passe.");
      return;
    }

    if (!code.trim()) {
      setErrorMessage("Veuillez entrer le code de reinitialisation.");
      return;
    }

    setIsSubmitting(true);

    try {
      await resetPassword({
        email,
        code: code.trim(),
        newPassword: password,
      });

      router.replace('/sign-in');
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        const data = error.response?.data as { message?: string } | undefined;
        const backendMessage = data?.message;

        if (status === 404) {
          setErrorMessage("Aucun compte n'existe avec cet email.");
        } else if (status === 400) {
          if (backendMessage === 'Password reset code has expired') {
            setErrorMessage('Le code de reinitialisation a expire.');
          } else if (backendMessage === 'Password reset code has already been used') {
            setErrorMessage('Ce code de reinitialisation a deja ete utilise.');
          } else if (backendMessage === 'Invalid password reset code') {
            setErrorMessage('Code de reinitialisation invalide.');
          } else {
            setErrorMessage(backendMessage ?? 'Veuillez verifier les informations saisies.');
          }
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
          <CardTitle className="text-center text-xl sm:text-left">Réinitialiser le mot de passe</CardTitle>
          <CardDescription className="text-center sm:text-left">
            Entrez le code qui vous a été envoyé par mail au {email}
          </CardDescription>
        </CardHeader>
        <CardContent className="gap-6">
          <View className="gap-6">
            <View className="gap-1.5">
              <View className="flex-row items-center">
                <Label htmlFor="password">Nouveau mot de passe</Label>
              </View>
              <Input
                id="password"
                secureTextEntry={isPasswordVisible}
                value= {password}
                onChangeText={setPassword}
                returnKeyType="next"
                submitBehavior="submit"
                onSubmitEditing={onPasswordSubmitEditing}
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
            <View className="gap-1.5">
              <Label htmlFor="code">Verification code</Label>
              <Input
                id="code"
                autoCapitalize="none"
                returnKeyType="send"
                value={code}
                onChangeText={setCode}
                keyboardType="numeric"
                autoComplete="sms-otp"
                textContentType="oneTimeCode"
                onSubmitEditing={onSubmit}
              />
            </View>
            {errorMessage ? <ErrorAlert title={errorMessage} /> : null}
            <Button className="w-full" onPress={onSubmit} disabled={isSubmitting}>
              <Text>{isSubmitting ? 'Reinitialisation...' : 'Reset Password'}</Text>
            </Button>
          </View>
        </CardContent>
      </Card>
    </View>
  );
}