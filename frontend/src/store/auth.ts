import { Module } from "vuex";
import type { RootState } from "../store";
import type { UserResponse } from "../types/auth";
import { login, register, logout, refreshToken } from "../services/authService";
import { getUserProfile } from "../services/userService";
import user from "./user";

export interface AuthState {
  user: UserResponse | null;
  token: string | null;
}

const auth: Module<AuthState, RootState> = {
  namespaced: true,

  state: (): AuthState => {
    const token = localStorage.getItem("token");
    const user = token ? JSON.parse(localStorage.getItem("user") || "null") : null;
    return { token, user };
  },

  mutations: {
    setToken(state, token: string) {
      state.token = token;
      localStorage.setItem("token", token);
    },
    setUser(state, user: UserResponse) {
      state.user = user;
      localStorage.setItem("user", JSON.stringify(user));
    },
    logout(state) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      state.token = null;
      state.user = null;
    },
  },

  actions: {
    async login({ commit }, credentials: { email: string; password: string }) {
      const res = await login(credentials.email, credentials.password);
      commit("setToken", res.token);
      commit("setUser", res.user);
    },

    async register({ commit }, userData: Omit<UserResponse, "id"> & { password: string }) {
      const res = await register(userData);
      commit("setToken", res.token);
      commit("setUser", res.user);
    },

    async logout({ commit }) {
      await logout();
      commit("logout");
    },

    async refresh({ commit }) {
      const token = await refreshToken();
      commit("setToken", token);
    },

    async getUserProfile({ state, commit }) {
      if (!state.token || !state.user) return;
      const user = await getUserProfile(state.user.id);
      commit("setUser", user);
    },
  },


  getters: {
    isAuthenticated: (state) => !!state.token,
    token: (state) => state.token,
    user: (state) => state.user,
    userRole: (state) => state.user?.role || null,
    isAdmin: (state) => state.user?.role === "ADMIN",
  },
};

export default auth;
