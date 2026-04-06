import { httpClient } from "@/src/shared/api/http-client";
import { ForgotPasswordApiResponse, GetCurrentUserApiResponse, ForgotPasswordPayload, LoginApiResponse, LoginPayload, RegisterApiResponse, RegisterPayload, ResendVerificationCodeApiResponse, ResendVerificationCodePayload, ResetPasswordApiResponse, ResetPasswordPayload, VerifyEmailApiResponse, VerifyEmailPayload, UpdateCurrentUserProfilePayload, UpdateCurrentUserPasswordPayload, UpdateCurrentUserPasswordApiResponse } from "../model/types";

export async function login(payload: LoginPayload): Promise<LoginApiResponse> {
  const response = await httpClient.post<LoginApiResponse>('/api/v1/auth/login', payload);
  return response.data;
}

export async function register(payload: RegisterPayload): Promise<RegisterApiResponse> {
  const response = await httpClient.post<RegisterApiResponse>('/api/v1/auth/register', payload);
  return response.data;
}

export async function verifyEmail(payload: VerifyEmailPayload): Promise<VerifyEmailApiResponse> {
  const response = await httpClient.post<VerifyEmailApiResponse>('/api/v1/auth/verify-email', payload);
  return response.data;
}

export async function resendVerificationCode(
  payload: ResendVerificationCodePayload
): Promise<ResendVerificationCodeApiResponse> {
  const response = await httpClient.post<ResendVerificationCodeApiResponse>(
    '/api/v1/auth/resend-verification-code',
    payload
  );
  return response.data;
}

export async function forgotPassword(
  payload: ForgotPasswordPayload
): Promise<ForgotPasswordApiResponse> {
  const response = await httpClient.post<ForgotPasswordApiResponse>(
    '/api/v1/auth/forgot-password',
    payload
  );
  return response.data;
}

export async function resetPassword(
  payload: ResetPasswordPayload
): Promise<ResetPasswordApiResponse> {
  const response = await httpClient.post<ResetPasswordApiResponse>(
    '/api/v1/auth/reset-password',
    payload
  );
  return response.data;
}

export async function getCurrentUser(): Promise<GetCurrentUserApiResponse> {
  const response = await httpClient.get<GetCurrentUserApiResponse>('/api/v1/auth/me');
  return response.data;
}

export async function updateCurrentUserProfile(
  payload: UpdateCurrentUserProfilePayload
): Promise<GetCurrentUserApiResponse> {
  const response = await httpClient.patch<GetCurrentUserApiResponse>('/api/v1/auth/me', payload);
  return response.data;
}

export async function updateCurrentUserPassword(
  payload: UpdateCurrentUserPasswordPayload
): Promise<UpdateCurrentUserPasswordApiResponse> {
  const response = await httpClient.patch<UpdateCurrentUserPasswordApiResponse>(
    '/api/v1/auth/me/password',
    payload
  );
  return response.data;
}
