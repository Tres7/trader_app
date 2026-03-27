import { ScrollView, View } from 'react-native';
import { SignUpForm } from '@/src/features/auth/ui/sign-up-form';

export default function SignUpScreen() {
  return (
    <ScrollView
      keyboardShouldPersistTaps="handled"
      contentContainerClassName="flex-1 items-center justify-center p-4 py-8"
      keyboardDismissMode="interactive">
      <View className="w-full max-w-sm">
        <SignUpForm />
      </View>
    </ScrollView>
  );
}