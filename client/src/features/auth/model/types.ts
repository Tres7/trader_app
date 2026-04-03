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

export interface VerifyEmailPayload {
  email: string;
  code: string
}

export interface ResendVerificationCodePayload {
  email: string;
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

export interface VerifyEmailApiResponse {
  email: string;
  emailVerified: boolean;
  message: string;
}


export interface ResendVerificationCodeApiResponse {
  email: string;
  message: string;
}


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
