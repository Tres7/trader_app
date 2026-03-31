export interface LoginPayload {
  email: string,
  password: string
};

export interface LoginApiResponse {
  accessToken: string,
  tokenType: string,
  userId: string,
  email: string,
  firstName: string
};

export interface AuthUser {
  userId: string;
  email: string;
  firstName: string;
}

export interface AuthSession {
  accessToken: string;
  user: AuthUser;
}

export interface AuthState {
  accessToken: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;
  isHydrating: boolean;
  setSession: (session: AuthSession) => Promise<void>;
  hydrateSession: () => Promise<void>;
  logout: () => Promise<void>;
}
