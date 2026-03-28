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
import * as React from 'react';
import { type TextStyle, View } from 'react-native';
 
const RESEND_CODE_INTERVAL_SECONDS = 30;
 
const TABULAR_NUMBERS_STYLE: TextStyle = { fontVariant: ['tabular-nums'] };

type VerifyEmailFormProps = {
  email: string;
};

 
export function VerifyEmailForm({ email }: VerifyEmailFormProps) {
  const { countdown, restartCountdown } = useCountdown(RESEND_CODE_INTERVAL_SECONDS);
 
  function onSubmit() {
    // TODO: Submit form and navigate to protected screen if successful
  }
 
  return (
    <View className="gap-6">
      <Card className="border-border/0 sm:border-border pb-4 shadow-none sm:shadow-sm sm:shadow-black/5">
        <CardHeader>
          <CardTitle className="text-center text-xl sm:text-left">Vérifier votre email</CardTitle>
          <CardDescription className="text-center sm:text-left">
            Entrer le code de vérification envoyé au vraiMailBienDispo@example.com
          </CardDescription>
        </CardHeader>
        <CardContent className="gap-6">
          <View className="gap-6">
            <View className="gap-1.5">
              <Label htmlFor="code">Code de vérification</Label>
              <Input
                id="code"
                autoCapitalize="none"
                returnKeyType="send"
                keyboardType="numeric"
                autoComplete="sms-otp"
                textContentType="oneTimeCode"
                onSubmitEditing={onSubmit}
              />
              <Button
                variant="link"
                size="sm"
                disabled={countdown > 0}
                onPress={() => {
                  // TODO: Resend code
                  restartCountdown();
                }}>
                <Text className="text-center text-xs">
                  Rien reçu? Demander un renvoi{' '}
                  {countdown > 0 ? (
                    <Text className="text-xs" style={TABULAR_NUMBERS_STYLE}>
                      ({countdown})
                    </Text>
                  ) : null}
                </Text>
              </Button>
            </View>
            <View className="gap-3">
              <Button className="w-full" onPress={onSubmit}>
                <Text>Continue</Text>
              </Button>
              <Button
                variant="link"
                className="mx-auto"
                onPress={() => {
                  // TODO: Navigate to sign up screen
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
 
function useCountdown(seconds = 30) {
  const [countdown, setCountdown] = React.useState(seconds);
  const intervalRef = React.useRef<ReturnType<typeof setInterval> | null>(null);
 
  const startCountdown = React.useCallback(() => {
    setCountdown(seconds);
 
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
    }
 
    intervalRef.current = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          if (intervalRef.current) {
            clearInterval(intervalRef.current);
            intervalRef.current = null;
          }
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  }, [seconds]);
 
  React.useEffect(() => {
    startCountdown();
 
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [startCountdown]);
 
  return { countdown, restartCountdown: startCountdown };
}