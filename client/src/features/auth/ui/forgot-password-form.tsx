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
import { View } from 'react-native';
import { router } from 'expo-router';
import * as React from 'react';
import axios from 'axios';
import { forgotPassword } from '../api/auth-api';
import { ErrorAlert } from '@/src/shared/ui/feedback/error-alert';


export function ForgotPasswordForm() {
    const [errorMessage, setErrorMessage] = React.useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = React.useState(false);

    const [email, setEmail] = React.useState('');
      async function onSubmit() {
    if (isSubmitting) {
      return;
    }

    setErrorMessage(null);

    if (!email.trim()) {
      setErrorMessage("Veuillez entrer votre email.");
      return;
    }

    setIsSubmitting(true);

    try {
        const response = await forgotPassword({
        email: email.trim(),
      });

      router.push({
        pathname: '/reset-password',
        params: {
          email: response.email ?? email.trim(),
        },
      });
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        const data = error.response?.data as { message?: string } | undefined;
        const backendMessage = data?.message;

        if (status === 404) {
          setErrorMessage("Aucun compte n'existe avec cet email.");
        } else if (status === 400) {
          setErrorMessage(backendMessage ?? "Veuillez verifier votre email.");
        } else {
          setErrorMessage("Une erreur est survenue. Veuillez reessayer.");
        }
      } else {
        setErrorMessage("Une erreur est survenue. Veuillez reessayer.");
      }
    } finally {
      setIsSubmitting(false);
    }
  }
    
    return (
        <View className="gap-6">
        <Card className="border-border/0 sm:border-border shadow-none sm:shadow-sm sm:shadow-black/5">
            <CardHeader>
            <CardTitle className="text-center text-xl sm:text-left">Mot de passe oublié?</CardTitle>
            <CardDescription className="text-center sm:text-left">
                Entrez votre email pour réinitialiser votre mot de passe
            </CardDescription>
            </CardHeader>
            <CardContent className="gap-6">
            <View className="gap-6">
                <View className="gap-1.5">
                <Label htmlFor="email">Email</Label>
                <Input
                    id="email"
                    placeholder="m@example.com"
                    keyboardType="email-address"
                    autoComplete="email"
                    value={email}
                    onChangeText={setEmail}
                    autoCapitalize="none"
                    returnKeyType="send"
                    onSubmitEditing={onSubmit}
                />
                </View>
                {errorMessage ? <ErrorAlert title={errorMessage} /> : null}

                <Button className="w-full" onPress={onSubmit} disabled={isSubmitting}>
                <Text>{isSubmitting ? 'Envoi...' : 'Reinitialiser votre mot de passe'}</Text>
                </Button>
            </View>
            </CardContent>
        </Card>
        </View>
    );
}