/**
 * WHAT BACKEND IS WAITING FOR
 */
export interface LoginPayload {
  email: string,
  password: string
};

export interface RegisterPayload {
  firstName: string;
  lastName: string;
  birthDate: string;
  email: string;
  password: string;
  country: string;
}

/**
 * BACKEND RESPONSES
 */

export interface LoginApiResponse {
  accessToken: string,
  tokenType: string,
  userId: string,
  email: string,
  firstName: string
};

export interface RegisterApiResponse {
  userId: string,
  email: string,
  emailVerified: boolean;
  message: string
};


/**
 * SESSION
 */
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
