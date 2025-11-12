import { jwtDecode } from 'jwt-decode';

interface DecodedToken {
  exp?: number;
  iat?: number;
  userId ?: number,
  email ?: string,
  role ?: string,
}

export const getUserIdFromToken = (): number | null => {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const decoded = jwtDecode<DecodedToken>(token);
    return decoded?.userId ?? null;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
};