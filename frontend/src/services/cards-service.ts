import axios from 'axios';
import type { CreditCard, CardType } from '../model/credit-card';
import type { CardApplication, UpdateApplicationStatusRequest } from '../types/cardApplication';
import { getUserIdFromToken } from '../utils/getTokenData';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';


const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    (config.headers as any).Authorization = `Bearer ${token}`;
  }
  return config;
});

export const getCards = async (): Promise<CreditCard[]> => {
  const token = localStorage.getItem("token");
  const userId = getUserIdFromToken();
  try {
    const res = await api.get<CreditCard[]>('/cards');
    return Array.isArray(res.data) ? res.data : [];
  } catch (err) {
    console.error('card-service.getCards failed', err);
    return [];
  }
};

export const getCardTypes = async (): Promise<CardType[]> => {
  const token = localStorage.getItem("token");
  const userId = getUserIdFromToken();
  try {
    const res = await axios.get<CardType[]>(`${BASE_URL}/cards/type`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return res.data;
  } catch (err) {
    return [];
  }
};
export const getCardById = async (
  cardId: string | number
): Promise<{ card: CreditCard; transactions: any[] } | null> => {
  try {
    const res = await api.get(`/cards/${cardId}`);
    if (res.data && res.data.card) {
      return {
        card: res.data.card,
        transactions: res.data.transactions || []
      };
    }
    return null;
  } catch (err) {
    console.error('card-service.getCardById failed', err);
    return null;
  }
};

export const getCardPan = async (cardId: string | number): Promise<string | null> => {
  try {
    const res = await api.get(`/cards/${cardId}/pan`);
    if (!res || res.status !== 200) return null;
    if (typeof res.data === 'string') return res.data;
    if (res.data && (res.data.cardNumber || res.data.pan)) return res.data.cardNumber || res.data.pan;
    return null;
  } catch (err) {
    console.error('card-service.getCardPan failed', err);
    return null;
  }
};

export const updateCardStatus = async (
  cardId: any,
  newStatus: string
): Promise<CreditCard | null> => {
  try {
    const token = localStorage.getItem("token");
    const res = await api.put<CreditCard>(`/cards/${cardId}/status`, { cardStatus: newStatus });
    return res.data ?? null;
  } catch (err) {
    console.error('card-service.updateCardStatus failed', err);
    return null;
  }
};

export const getCardApplications = async (): Promise<CardApplication[]> => {
  try {
    const token = localStorage.getItem("token");
    
    const res = await axios.get<CardApplication[]>(`${BASE_URL}/card/application`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return res.data;
  } catch (err) {
    console.error('Error fetching card applications:', err);
    return [];
  }
};

export const updateApplicationStatus = async (
  applicationId: number,
  status: 'ACCEPTED' | 'REJECTED'
): Promise<CardApplication | null> => {
  try { 
    const token = localStorage.getItem("token");
    const res = await axios.patch<CardApplication>(
      `${BASE_URL}/card/application/${applicationId}`,
      { status },
      {
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      }
    );
    return res.data;
  } catch (err) {
    console.error('Error updating application status:', err);
    return null;
  }
};

export const updateCardLimit = async (cardId: string | number, newLimit: number): Promise<any> => {
  try {
    const res = await api.put(`/cards/${cardId}/limit`, { newCreditLimit: newLimit });
    return res.data;
  } catch (err) {
    console.error('card-service.updateCardLimit failed', err);
    throw err;
  }
};
