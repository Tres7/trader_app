import { httpClient } from "@/src/shared/api/http-client";
import { ForgotPasswordApiResponse, ForgotPasswordPayload, LoginApiResponse, LoginPayload, RegisterApiResponse, RegisterPayload, ResendVerificationCodeApiResponse, ResendVerificationCodePayload, ResetPasswordApiResponse, ResetPasswordPayload, VerifyEmailApiResponse, VerifyEmailPayload } from "../model/types";

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