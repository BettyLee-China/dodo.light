// store/authStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

import type { StateCreator } from 'zustand';

interface User {
  username: string;
  token: string;
  role: 'customer' | 'photographer' | 'common';
}

interface AuthState {
  user: User | null;
  isLogin: boolean;
  login: (userData: User) => void;
  logout: () => void;
  getUsername: () => string | null;
}

type AuthStore = StateCreator<
  AuthState,
  [],
  [['zustand/persist', unknown]]
>;

const createAuthStore: AuthStore = (set, get) => ({
  user: null,
  isLogin: false,

  login: (userData: User) => set({ user: userData, isLogin: true }),

  logout: () => set({ user: null, isLogin: false }),

  getUsername: () => get().user?.username || null,
});

export const useAuthStore = create<AuthState>()(
  persist(createAuthStore, {
    name: 'auth-storage',
  })
);