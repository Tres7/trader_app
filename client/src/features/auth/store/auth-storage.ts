import * as SecureStore from 'expo-secure-store'

const ACCESS_TOKEN_KEY = 'auth_access_token';
const USER_KEY = 'auth_user';

export async function saveAccessToken(token: string): Promise<void> {
  await SecureStore.setItemAsync(ACCESS_TOKEN_KEY, token);
}

export async function getAccessToken(): Promise<string | null> {
  return SecureStore.getItemAsync(ACCESS_TOKEN_KEY);
}

export async function deleteAccessToken(): Promise<void> {
  await SecureStore.deleteItemAsync(ACCESS_TOKEN_KEY);
}

export async function saveUser(user: string): Promise<void> {
  await SecureStore.setItemAsync(USER_KEY, user);
}

export async function getUser(): Promise<string | null> {
  return SecureStore.getItemAsync(USER_KEY);
}

export async function deleteUser(): Promise<void> {
  await SecureStore.deleteItemAsync(USER_KEY);
}