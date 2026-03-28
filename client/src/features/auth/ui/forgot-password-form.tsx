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


export function ForgotPasswordForm() {

    const [email, setEmail] = React.useState('');
    function onSubmit() {
        router.push({
            pathname: '/reset-password',
            params: {
            email: email,
            },
        });
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
                <Button className="w-full" onPress={onSubmit}>
                <Text>Réinitialiser votre mot de passe</Text>
                </Button>
            </View>
            </CardContent>
        </Card>
        </View>
    );
}