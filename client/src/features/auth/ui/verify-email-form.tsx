import { Button } from '@/src/shared/ui/primitives/button';
import { useCountdown } from '@/src/shared/hooks/use-countdown';
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
import * as React from 'react';
import { type TextStyle, View } from 'react-native';
import { resendVerificationCode, verifyEmail } from '../api/auth-api';
import { router } from 'expo-router';
import { ErrorAlert } from '@/src/shared/ui/feedback/error-alert';
 
const RESEND_CODE_INTERVAL_SECONDS = 30;
 
const TABULAR_NUMBERS_STYLE: TextStyle = { fontVariant: ['tabular-nums'] };

type VerifyEmailFormProps = {
  email: string;
};

 
export function VerifyEmailForm({ email }: VerifyEmailFormProps) {
  const [code, setCode] = React.useState('');
  const [errorMessage, setErrorMessage] = React.useState<string | null>(null);
  const [infoMessage, setInfoMessage] = React.useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = React.useState(false);
  const [isResending, setIsResending] = React.useState(false);

  const { countdown, restartCountdown } = useCountdown(RESEND_CODE_INTERVAL_SECONDS);
 
  async function onSubmit() {
    if (isSubmitting) {
      return;
    }

    setErrorMessage(null);
    setInfoMessage(null);

    if (!email) {
      setErrorMessage("Email manquant. Recommencez l'inscription.");
      return;
    }

    if (!code.trim()) {
      setErrorMessage('Veuillez entrer le code de verification.');
      return;
    }

    setIsSubmitting(true);

    try {
      await verifyEmail({
        email,
        code: code.trim(),
      });

      router.replace('/sign-in');
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;
        const message = error.response?.data?.message;

        if (status === 409) {
          setErrorMessage('Cet email est deja verifie. Vous pouvez vous connecter.');
        } else if (status === 400) {
          if (message === 'Verification code has expired') {
            setErrorMessage('Le code de verification a expire.');
          } else if (message === 'Verification code has already been used') {
            setErrorMessage('Ce code de verification a deja ete utilise.');
          } else {
            setErrorMessage('Code de verification invalide.');
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

  async function onResendCode() {
    if (isResending || countdown > 0) {
      return;
    }

    setErrorMessage(null);
    setInfoMessage(null);

    if (!email) {
      setErrorMessage("Email manquant. Recommencez l'inscription.");
      return;
    }

    setIsResending(true);

    try {
      const response = await resendVerificationCode({ email });

      setInfoMessage(response.message || 'Code renvoye avec succes.');
      restartCountdown();
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;

        if (status === 409) {
          setErrorMessage('Cet email est deja verifie. Vous pouvez vous connecter.');
        } else {
          setErrorMessage('Impossible de renvoyer le code pour le moment.');
        }
      } else {
        setErrorMessage('Impossible de renvoyer le code pour le moment.');
      }
    } finally {
      setIsResending(false);
    }
  }
 
  return (
    <View className="gap-6">
      <Card className="border-border/0 sm:border-border pb-4 shadow-none sm:shadow-sm sm:shadow-black/5">
        <CardHeader>
          <CardTitle className="text-center text-xl sm:text-left">Vérifier votre email</CardTitle>
          <CardDescription className="text-center sm:text-left">
            Entrer le code de vérification envoyé à {email}
          </CardDescription>
        </CardHeader>
        <CardContent className="gap-6">
          <View className="gap-6">
            <View className="gap-1.5">
              <Label htmlFor="code">Code de vérification</Label>
              <Input
                id="code"
                autoCapitalize="none"
                value={code}
                onChangeText={setCode}
                returnKeyType="send"
                keyboardType="numeric"
                autoComplete="sms-otp"
                textContentType="oneTimeCode"
                onSubmitEditing={onSubmit}
              />
              <Button
                variant="link"
                size="sm"
                disabled={countdown > 0 || isResending}
                onPress={onResendCode}>
                <Text className="text-center text-xs">
                  {isResending ? 'Renvoi...' : 'Rien recu? Demander un renvoi'}{' '}
                  {countdown > 0 ? (
                    <Text className="text-xs" style={TABULAR_NUMBERS_STYLE}>
                      ({countdown})
                    </Text>
                  ) : null}
                </Text>
              </Button>
            </View>
            {errorMessage ? <ErrorAlert title={errorMessage} /> : null}
            {infoMessage ? (
              <Text className="text-center text-sm text-muted-foreground">{infoMessage}</Text>
            ) : null}
            <View className="gap-3">
              <Button className="w-full" onPress={onSubmit} disabled={isSubmitting}>
                <Text>{isSubmitting ? 'Verification...' : 'Continue'}</Text>
              </Button>
              <Button
                variant="link"
                className="mx-auto"
                onPress={() => {
                  router.replace('/sign-up')
                }}>
                <Text>Annuler</Text>
              </Button>
            </View>
          </View>
        </CardContent>
      </Card>
    </View>
  );
}