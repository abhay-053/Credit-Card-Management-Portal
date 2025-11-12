import axios from "axios";
import { toast } from "vue3-toastify";
import router from "../router";

export const api = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL || "http://localhost:8080/api",
  withCredentials: true, // needed for cookies
});

// Request interceptor: attach access token
api.interceptors.request.use(config => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor: handle token expiration and errors
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    // Handle 401 Unauthorized (token expired)
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        // Try to refresh token
        const refreshResponse = await axios.post(
          `${import.meta.env.VITE_BASE_URL || "http://localhost:8080/api"}/auth/refresh`,
          {},
          { withCredentials: true }
        );
        
        const { token } = refreshResponse.data;
        localStorage.setItem("token", token);
        localStorage.setItem("user", JSON.stringify(refreshResponse.data.user));
        
        // Retry original request with new token
        originalRequest.headers["Authorization"] = `Bearer ${token}`;
        return api(originalRequest);
        
      } catch (refreshError) {
        // Refresh failed, logout user
        console.error("Token refresh failed:", refreshError);
        handleLogout();
        return Promise.reject(error);
      }
    }
    
    // Handle 403 Forbidden
    if (error.response?.status === 403) {
      toast.error("Access denied. You don't have permission to perform this action.");
    }
    
    return Promise.reject(error);
  }
);

// Centralized logout function
const handleLogout = () => {
  // Clear local storage
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  
  // Show notification
  toast.warning("Your session has expired. Please log in again.");
  
  // Redirect to login
  router.push("/login");
  
  // Reload page to clear any cached state
  setTimeout(() => {
    window.location.reload();
  }, 1000);
};
