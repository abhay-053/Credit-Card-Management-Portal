// services/dashboardService.ts
import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export type Summary = {
  activeCards: number;
  totalCards: number;
  totalLimit: number;
  availableCredit: number;
  outstanding: number;
};

export type Card = {
  id: string;
  name?: string;
  number?: string;
  cardNumber?: string;
  totalLimit?: number;
  availableLimit?: number;
  status?: string;
  creditLimit?: number;
  cardHolderName?: string;
  cardType?: { name?: string };
  [prop: string]: any;
};

export type Transaction = {
  id: string;
  cardId?: string;
  amount: number;
  merchant?: string;
  category?: string;
  mode?: string;
  date?: string;
  [prop: string]: any;
};

export type DashboardResponse = {
  userName?: string;
  summary?: Summary;
  cards?: Card[];
  transactions?: Transaction[];
  lastUpdated?: string;
};

// axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token && config.headers) {
    (config.headers as any).Authorization = `Bearer ${token}`;
  }
  return config;
});

/**
 * Normalizes various backend shapes into DashboardResponse
 */
function normalizeDashboardPayload(payload: any): DashboardResponse {
  if (!payload) return {};
  const topKeys = ["userName", "summary", "cards", "transactions", "lastUpdated"];
  const hasTopKeys = topKeys.some((k) => k in payload);
  if (hasTopKeys) {
    return {
      userName: payload.userName,
      summary: payload.summary,
      cards: Array.isArray(payload.cards) ? payload.cards : [],
      transactions: Array.isArray(payload.transactions) ? payload.transactions : [],
      lastUpdated: payload.lastUpdated,
    };
  }

  const possibleWrappers = ["dashboard", "data", "payload"];
  for (const w of possibleWrappers) {
    if (payload[w]) {
      return normalizeDashboardPayload(payload[w]);
    }
  }

  const cards = Array.isArray(payload.cards) ? payload.cards : payload.items || [];
  const summary =
    payload.summary ||
    ({
      activeCards: cards.length,
      totalCards: cards.length,
      totalLimit: cards.reduce((s: number, c: any) => s + (Number(c.totalLimit) || Number(c.creditLimit) || 0), 0),
      availableCredit: payload.availableCredit ?? 0,
      outstanding: payload.outstanding ?? 0,
    } as Summary);

  return {
    userName: payload.userName || (payload.user && payload.user.name) || undefined,
    summary,
    cards,
    transactions: Array.isArray(payload.transactions) ? payload.transactions : [],
    lastUpdated: payload.lastUpdated,
  };
}

/**
 * Attempt a GET to a list of candidate endpoints sequentially until one succeeds.
 */
async function tryGetFromCandidates<T = any>(candidates: string[]): Promise<T> {
  let lastError: any = null;
  for (const path of candidates) {
    try {
      const res = await api.get<T>(path);
      return res.data;
    } catch (err) {
      lastError = err;
      console.warn(`dashboardService: request to ${path} failed:`, (err as any)?.message || err);
    }
  }
  throw lastError;
}

/** Fetch dashboard aggregated data (summary, cards, recent transactions). */
export const getDashboard = async (): Promise<DashboardResponse> => {
  const candidatePaths = ["/api/dashboard"];

  try {
    const raw = await tryGetFromCandidates<any>(candidatePaths);//api.get('/api/dashboard')
    const normalized = normalizeDashboardPayload(raw);
    normalized.cards = Array.isArray(normalized.cards) ? normalized.cards : [];
    normalized.transactions = Array.isArray(normalized.transactions) ? normalized.transactions : [];

    if (!normalized.summary) {
      normalized.summary = {
        activeCards: normalized.cards.filter((c: any) => c.status !== "CLOSED" && c.status !== "BLOCKED").length,
        totalCards: normalized.cards.length,
        totalLimit: normalized.cards.reduce((s: number, c: any) => s + (Number(c.totalLimit) || Number(c.creditLimit) || 0), 0),
        availableCredit: normalized.cards.reduce((s: number, c: any) => s + (Number(c.availableLimit) || 0), 0),
        outstanding: 0,
      };
    }
    return normalized;
  } catch (err) {
    console.error("getDashboard failed after trying multiple endpoints:", err);
    throw err;
  }
};
