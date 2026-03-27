import { SocialConnections } from '@/src/features/auth/ui/social-connections';
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
import { Separator } from '@/src/shared/ui/primitives/separator';
import { Text } from '@/src/shared/ui/primitives/text';
import * as React from 'react';
import { Pressable, TextInput, View } from 'react-native';

export function SignUpForm() {
  const passwordInputRef = React.useRef<TextInput>(null);

  function onEmailSubmitEditing() {
    passwordInputRef.current?.focus();
  }

  function onSubmit() {
    // TODO: Submit form and navigate to protected screen if successful
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
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
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
              </View>
              <Input
                ref={passwordInputRef}
                id="password"
                secureTextEntry
                returnKeyType="send"
                onSubmitEditing={onSubmit}
              />
            </View>
            <Button className="w-full" onPress={onSubmit}>
              <Text>Continue</Text>
            </Button>
          </View>
          <View className="flex-row items-center justify-center gap-1">
            <Text className="text-sm">Vous avez déjà un compte?</Text>
                <Pressable
                    onPress={() => {
                       // TODO: Navigate to sign in screen
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
