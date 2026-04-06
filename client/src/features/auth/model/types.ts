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

export interface ForgotPasswordPayload {
  email: string;
}

export interface ResetPasswordPayload {
  email: string;
  code: string;
  newPassword: string;
}

export interface UpdateCurrentUserProfilePayload {
  firstName: string;
  lastName: string;
  birthDate: string;
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

export interface VerifyEmailApiResponse {
  email: string;
  emailVerified: boolean;
  message: string;
}


export interface ResendVerificationCodeApiResponse {
  email: string;
  message: string;
}


export interface ForgotPasswordApiResponse {
  email: string;
  message: string;
}

export interface ResetPasswordApiResponse {
  email: string;
  message: string;
}

export interface GetCurrentUserApiResponse {
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  country: string;
  birthDate: string;
  emailVerified: boolean;
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
  refreshUser: (user: AuthUser) => void;
}
