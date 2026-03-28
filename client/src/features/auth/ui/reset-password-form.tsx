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
import { router } from 'expo-router';
import * as React from 'react';
import { TextInput, View } from 'react-native';

type ResetPasswordFormProps = {
  email: string;
};

 
export function ResetPasswordForm({ email }: ResetPasswordFormProps) {
  const codeInputRef = React.useRef<TextInput>(null);
 
  function onPasswordSubmitEditing() {
    codeInputRef.current?.focus();
  }
 
  function onSubmit() {
    router.push('/sign-in');
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
                secureTextEntry
                returnKeyType="next"
                submitBehavior="submit"
                onSubmitEditing={onPasswordSubmitEditing}
              />
            </View>
            <View className="gap-1.5">
              <Label htmlFor="code">Verification code</Label>
              <Input
                id="code"
                autoCapitalize="none"
                returnKeyType="send"
                keyboardType="numeric"
                autoComplete="sms-otp"
                textContentType="oneTimeCode"
                onSubmitEditing={onSubmit}
              />
            </View>
            <Button className="w-full" onPress={onSubmit}>
              <Text>Reset Password</Text>
            </Button>
          </View>
        </CardContent>
      </Card>
    </View>
  );
}