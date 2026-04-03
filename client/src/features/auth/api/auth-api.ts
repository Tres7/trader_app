import { httpClient } from "@/src/shared/api/http-client";
import { LoginApiResponse, LoginPayload, RegisterApiResponse, RegisterPayload } from "../model/types";

export async function login(payload: LoginPayload): Promise<LoginApiResponse> {
  const response = await httpClient.post<LoginApiResponse>('/api/v1/auth/login', payload);
  return response.data;
}

export async function register(payload: RegisterPayload): Promise<RegisterApiResponse> {
  const response = await httpClient.post<RegisterApiResponse>('/api/v1/auth/register', payload);
  return response.data;
}