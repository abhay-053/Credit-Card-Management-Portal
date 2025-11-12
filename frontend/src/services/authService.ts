import { api } from "./api";
import type { AuthResponse } from "../types/auth";
 
export const login = async (email: string, password: string): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>("/auth/login", { email, password });
  return response.data;
};
 
export const register = async (user: Omit<AuthResponse["user"], "id"> & { password: string }): Promise<AuthResponse> => {
  const response = await api.post<AuthResponse>("/auth/register", user);
  return response.data;
};
 
export const refreshToken = async (): Promise<string> => {
  const response = await api.post<AuthResponse>("/auth/refresh",{},{ withCredentials: true });
  const { token, user } = response.data;

  // Store tokens in localStorage
  localStorage.setItem("token", token);
  localStorage.setItem("user", JSON.stringify(user));

  return token;
};
 
export const logout = async (): Promise<void> => {
  try {
    await api.post("/auth/logout", {}, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token") || ""}`,
      },
      withCredentials: true,
    });
  } finally {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  }
};