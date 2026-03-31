export interface LoginPayload {
  email: string;
  password: string;
};

export interface LoginApiResponse {
  accessToken: string;
  tokenType: string;
  userId: string;
  email: string;
  firstName: string;
};
