import { create } from 'zustand';
import {
  deleteAccessToken,
  getAccessToken,
  saveAccessToken,
} from './auth-storage';
import { AuthSession, AuthState } from '../model/types';
import { getCurrentUser } from '../api/auth-api';
import axios from 'axios';

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  user: null,
  isAuthenticated: false,
  isHydrating: false,

  setSession: async (session: AuthSession) => {
    await saveAccessToken(session.accessToken);

    set({
      accessToken: session.accessToken,
      user: session.user,
      isAuthenticated: true,
      isHydrating: false,
    });
  },

  refreshUser: (user) => {
    set((state) => ({
      ...state,
      user,
    }));
  },


  hydrateSession: async () => {
    set({ isHydrating: true });

    try {
      const accessToken = await getAccessToken();

      if (!accessToken) {
        set({
          accessToken: null,
          user: null,
          isAuthenticated: false,
        });
        return;
      }

      const currentUser = await getCurrentUser();

      set({
        accessToken,
        user: {
          userId: currentUser.userId,
          email: currentUser.email,
          firstName: currentUser.firstName,
        },
        isAuthenticated: true,
      });
    } catch (error) {
      if (axios.isAxiosError(error)) {
        const status = error.response?.status;

        if (status === 401 || status === 403 || status === 404) {
          await deleteAccessToken();
        }
      } else {
        await deleteAccessToken();
      }

      set({
        accessToken: null,
        user: null,
        isAuthenticated: false,
      });
    } finally {
      set({ isHydrating: false });
    }
  },

  logout: async () => {
    await deleteAccessToken();

    set({
      accessToken: null,
      user: null,
      isAuthenticated: false,
      isHydrating: false,
    });
  },
}));
