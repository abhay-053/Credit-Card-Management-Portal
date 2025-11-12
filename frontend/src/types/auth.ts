export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterCredentials {
  email: string;
  password: string;
  name: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}

export interface UserResponse {
  id: number;            
  email: string;
  name: string;
  phoneNumber: string;
  address: string;
  isEligibleForBNPL: boolean;
  role?: string;           
}

export interface User {
  id: number;            
  email: string;
  name: string;
  role: 'user' | 'admin';
}

export interface AuthResponse {
  token: string;            // access token
  refreshToken: string;
  user: UserResponse;
}

export interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
}
