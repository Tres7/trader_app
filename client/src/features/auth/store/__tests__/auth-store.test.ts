import { describe, expect, it, jest, beforeEach } from '@jest/globals';
import type { GetCurrentUserApiResponse } from '../../model/types';

jest.mock('../auth-storage', () => ({
  getAccessToken: jest.fn(),
  saveAccessToken: jest.fn(),
  deleteAccessToken: jest.fn(),
}));

jest.mock('../../api/auth-api', () => ({
  getCurrentUser: jest.fn(),
}));

jest.mock('axios', () => ({
  __esModule: true,
  default: { isAxiosError: jest.fn() },
}));

import { useAuthStore } from '../auth-store';
import * as authStorage from '../auth-storage';
import * as authApi from '../../api/auth-api';
import axios from 'axios';

const mockGetAccessToken = jest.mocked(authStorage.getAccessToken);
const mockSaveAccessToken = jest.mocked(authStorage.saveAccessToken);
const mockDeleteAccessToken = jest.mocked(authStorage.deleteAccessToken);
const mockGetCurrentUser = jest.mocked(authApi.getCurrentUser);
const mockIsAxiosError = jest.mocked(axios.isAxiosError);

const MOCK_TOKEN = 'test-access-token';
const MOCK_USER = {
  userId: 'user-1',
  email: 'user@test.com',
  firstName: 'Alice',
  lastName: 'Doe',
  country: 'FR',
  birthDate: '1990-01-01',
  emailVerified: true,
} satisfies GetCurrentUserApiResponse;

describe('useAuthStore', () => {
  beforeEach(() => {
    useAuthStore.setState({
      accessToken: null,
      user: null,
      isAuthenticated: false,
      isHydrating: false,
    });
    jest.clearAllMocks();
  });

  describe('setSession', () => {
    it('saves token, sets user and marks authenticated', async () => {
      mockSaveAccessToken.mockResolvedValue(undefined);

      await useAuthStore.getState().setSession({
        accessToken: MOCK_TOKEN,
        user: { userId: MOCK_USER.userId, email: MOCK_USER.email, firstName: MOCK_USER.firstName },
      });

      const { accessToken, user, isAuthenticated } = useAuthStore.getState();
      expect(accessToken).toBe(MOCK_TOKEN);
      expect(user?.email).toBe(MOCK_USER.email);
      expect(isAuthenticated).toBe(true);
      expect(mockSaveAccessToken).toHaveBeenCalledWith(MOCK_TOKEN);
    });
  });

  describe('refreshUser', () => {
    it('updates user without affecting token or auth status', () => {
      useAuthStore.setState({ isAuthenticated: true, accessToken: MOCK_TOKEN });

      useAuthStore.getState().refreshUser({
        userId: 'user-2',
        email: 'bob@test.com',
        firstName: 'Bob',
      });

      const { user, isAuthenticated, accessToken } = useAuthStore.getState();
      expect(user?.email).toBe('bob@test.com');
      expect(isAuthenticated).toBe(true);
      expect(accessToken).toBe(MOCK_TOKEN);
    });
  });

  describe('logout', () => {
    it('clears state and deletes the stored token', async () => {
      useAuthStore.setState({ accessToken: MOCK_TOKEN, isAuthenticated: true });
      mockDeleteAccessToken.mockResolvedValue(undefined);

      await useAuthStore.getState().logout();

      const { accessToken, user, isAuthenticated } = useAuthStore.getState();
      expect(accessToken).toBeNull();
      expect(user).toBeNull();
      expect(isAuthenticated).toBe(false);
      expect(mockDeleteAccessToken).toHaveBeenCalledTimes(1);
    });
  });

  describe('hydrateSession', () => {
    it('clears state when no token in storage', async () => {
      mockGetAccessToken.mockResolvedValue(null);

      await useAuthStore.getState().hydrateSession();

      const { isAuthenticated, accessToken, isHydrating } = useAuthStore.getState();
      expect(isAuthenticated).toBe(false);
      expect(accessToken).toBeNull();
      expect(isHydrating).toBe(false);
    });

    it('sets authenticated state on valid token and user', async () => {
      mockGetAccessToken.mockResolvedValue(MOCK_TOKEN);
      mockGetCurrentUser.mockResolvedValue(MOCK_USER);

      await useAuthStore.getState().hydrateSession();

      const { isAuthenticated, accessToken, user, isHydrating } = useAuthStore.getState();
      expect(isAuthenticated).toBe(true);
      expect(accessToken).toBe(MOCK_TOKEN);
      expect(user?.email).toBe(MOCK_USER.email);
      expect(isHydrating).toBe(false);
    });

    it('deletes token and clears state on 401', async () => {
      mockGetAccessToken.mockResolvedValue(MOCK_TOKEN);
      mockGetCurrentUser.mockRejectedValue({ response: { status: 401 } });
      mockIsAxiosError.mockReturnValue(true);
      mockDeleteAccessToken.mockResolvedValue(undefined);

      await useAuthStore.getState().hydrateSession();

      expect(mockDeleteAccessToken).toHaveBeenCalledTimes(1);
      expect(useAuthStore.getState().isAuthenticated).toBe(false);
      expect(useAuthStore.getState().isHydrating).toBe(false);
    });

    it('deletes token and clears state on 403', async () => {
      mockGetAccessToken.mockResolvedValue(MOCK_TOKEN);
      mockGetCurrentUser.mockRejectedValue({ response: { status: 403 } });
      mockIsAxiosError.mockReturnValue(true);
      mockDeleteAccessToken.mockResolvedValue(undefined);

      await useAuthStore.getState().hydrateSession();

      expect(mockDeleteAccessToken).toHaveBeenCalledTimes(1);
    });

    it('does not delete token on non-auth axios error (500)', async () => {
      mockGetAccessToken.mockResolvedValue(MOCK_TOKEN);
      mockGetCurrentUser.mockRejectedValue({ response: { status: 500 } });
      mockIsAxiosError.mockReturnValue(true);

      await useAuthStore.getState().hydrateSession();

      expect(mockDeleteAccessToken).not.toHaveBeenCalled();
      expect(useAuthStore.getState().isAuthenticated).toBe(false);
    });

    it('deletes token on non-axios error', async () => {
      mockGetAccessToken.mockResolvedValue(MOCK_TOKEN);
      mockGetCurrentUser.mockRejectedValue(new Error('network failure'));
      mockIsAxiosError.mockReturnValue(false);
      mockDeleteAccessToken.mockResolvedValue(undefined);

      await useAuthStore.getState().hydrateSession();

      expect(mockDeleteAccessToken).toHaveBeenCalledTimes(1);
      expect(useAuthStore.getState().isAuthenticated).toBe(false);
    });

    it('always sets isHydrating to false after completion', async () => {
      mockGetAccessToken.mockRejectedValue(new Error('storage error'));
      mockIsAxiosError.mockReturnValue(false);
      mockDeleteAccessToken.mockResolvedValue(undefined);

      await useAuthStore.getState().hydrateSession();

      expect(useAuthStore.getState().isHydrating).toBe(false);
    });
  });
});
