import { api } from "./api";
import type { User } from "../types/User";

export async function getUserProfile(userId: number) {
  return api.get<User>(`/profile/${userId}`);
}

export async function updateUserProfile(
  userId: number,
  payload: Omit<User, "id" | "email" | "password">
) {
  return api.put(`/profile/${userId}`, payload);
}
