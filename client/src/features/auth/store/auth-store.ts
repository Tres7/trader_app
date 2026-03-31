import { create } from 'zustand';
import {
  deleteAccessToken,
  deleteUser,
  getAccessToken,
  getUser,
  saveAccessToken,
  saveUser,
} from './auth-storage';
import { AuthSession, AuthState } from '../model/types';

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  user: null,
  isAuthenticated: false,
  isHydrating: false,

  setSession: async (session: AuthSession) => {
    await saveAccessToken(session.accessToken);
    await saveUser(JSON.stringify(session.user));

    set({
      accessToken: session.accessToken,
      user: session.user,
      isAuthenticated: true,
    });
  },

  hydrateSession: async () => {
    set({ isHydrating: true });

    try {
      const accessToken = await getAccessToken();
      const rawUser = await getUser();

      if (!accessToken || !rawUser) {
        set({
          accessToken: null,
          user: null,
          isAuthenticated: false,
          isHydrating: false,
        });
        return;
      }

      const user = JSON.parse(rawUser);

      set({
        accessToken,
        user,
        isAuthenticated: true,
        isHydrating: false,
      });
    } catch {
      set({
        accessToken: null,
        user: null,
        isAuthenticated: false,
        isHydrating: false,
      });
    }
  },

  logout: async () => {
    await deleteAccessToken();
    await deleteUser();

    set({
      accessToken: null,
      user: null,
      isAuthenticated: false,
    });
  },
}));