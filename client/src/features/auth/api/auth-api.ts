import { httpClient } from "@/src/shared/api/http-client";
import { LoginApiResponse, LoginPayload } from "../model/types";

export async function login(payload: LoginPayload): Promise<LoginApiResponse> {
  const response = await httpClient.post<LoginApiResponse>('/api/v1/auth/login', payload);
  return response.data;
}