import { refreshToken } from "../services/authService";

export const initializeAuth = async () => {
  const token = localStorage.getItem("token");
  const user = localStorage.getItem("user");

  // If no token, try to refresh using refresh cookie
  if (!token) {
    try {
      const newToken = await refreshToken();
      return {
        accessToken: newToken,
        user: JSON.parse(localStorage.getItem("user") || "{}"),
      };
    } catch (err) {
      console.error("Session restore failed:", err);
      return null;
    }
  }

  // If token exists, check if it's expired
  try {
    const tokenPayload = JSON.parse(atob(token.split('.')[1]));
    const currentTime = Date.now() / 1000;
    
    // If token is expired, try to refresh
    if (tokenPayload.exp < currentTime) {
      try {
        const newToken = await refreshToken();
        return {
          accessToken: newToken,
          user: JSON.parse(localStorage.getItem("user") || "{}"),
        };
      } catch (err) {
        console.error("Token refresh failed:", err);
        // Clear expired token
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        return null;
      }
    }
  } catch (err) {
    console.error("Invalid token format:", err);
    // Clear invalid token
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    return null;
  }

  return {
    accessToken: token,
    user: JSON.parse(user || "{}"),
  };
};
