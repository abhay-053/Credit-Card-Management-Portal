import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// Types
export interface Transaction {
  id: number
  cardId: number
  merchantName: string
  amount: number
  transactionDate: string
  category: string
  isBnpl: boolean
  cardType: string
  lastFour: string
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED'
  createdAt: string
}

export interface CreateTransactionRequest {
  cardId: number
  merchantName: string
  amount: number
  transactionDate: string
  category: string
  isBnpl: boolean
  cardType: string
  lastFour: string
  tenureMonths?: number
}

export interface TransactionHistoryResponse {
  transactions: Transaction[]
  currentPage: number
  totalPages: number
  totalItems: number
}

export interface AnalyticsResponse {
  totalSpent: number
  transactionCount: number
  categoryBreakdown: CategorySpending[]
  monthlyTrends: MonthlySpending[]
  averageTransactionAmount: number
  bnplTransactionCount: number
}

export interface CategorySpending {
  category: string
  amount: number
  percentage: number
}

export interface MonthlySpending {
  year: number
  month: number
  total: number
  monthName: string
}

export interface TransactionFilters {
  page?: number
  size?: number
  category?: string
  isBnpl?: boolean
  merchantName?: string
}

class TransactionService {
  private baseURL: string

  constructor() {
    this.baseURL = API_BASE_URL
  }

  private getAuthHeaders() {
    const token = localStorage.getItem('token')
    return {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` })
    }
  }

  // Get transaction history with filters
  async getTransactionHistory(
    cardId: number, 
    filters: TransactionFilters = {}
  ): Promise<TransactionHistoryResponse> {
    const params = new URLSearchParams({
      page: (filters.page || 0).toString(),
      size: (filters.size || 10).toString()
    })
    
    if (filters.category) params.append('category', filters.category)
    if (filters.isBnpl !== undefined) params.append('isBnpl', filters.isBnpl.toString())
    if (filters.merchantName) params.append('merchantName', filters.merchantName)

    const response = await axios.get(
      `${this.baseURL}/api/transactions/${cardId}?${params}`,
      { headers: this.getAuthHeaders() }
    )
    return response.data
  }

  // Create a new transaction
  async createTransaction(transactionData: CreateTransactionRequest): Promise<Transaction> {
    const response = await axios.post(
      `${this.baseURL}/api/transactions`,
      transactionData,
      { headers: this.getAuthHeaders() }
    )
    return response.data
  }

  // Get transaction by ID
  async getTransactionById(transactionId: number): Promise<Transaction> {
    const response = await axios.get(
      `${this.baseURL}/api/transactions/details/${transactionId}`,
      { headers: this.getAuthHeaders() }
    )
    return response.data
  }

  // Get analytics for a card
  async getAnalytics(cardId: number): Promise<AnalyticsResponse> {
    const response = await axios.get(
      `${this.baseURL}/api/transactions/${cardId}/analytics`,
      { headers: this.getAuthHeaders() }
    )
    return response.data
  }

  // Get monthly spending trends
  async getMonthlyTrends(cardId: number, startDate?: string): Promise<MonthlySpending[]> {
    const params = startDate ? `?startDate=${startDate}` : ''
    const response = await axios.get(
      `${this.baseURL}/api/transactions/${cardId}/analytics/trends${params}`,
      { headers: this.getAuthHeaders() }
    )
    return response.data
  }

  // Get BNPL transactions
  async getBnplTransactions(cardId: number): Promise<Transaction[]> {
    const response = await axios.get(
      `${this.baseURL}/api/transactions/${cardId}/bnpl`,
      { headers: this.getAuthHeaders() }
    )
    return response.data
  }

  // Get spending by category
  async getSpendingByCategory(cardId: number): Promise<CategorySpending[]> {
    const response = await axios.get(
      `${this.baseURL}/api/transactions/${cardId}/analytics/category`,
      { headers: this.getAuthHeaders() }
    )
    return response.data
  }

  // Get total spent
  async getTotalSpent(cardId: number): Promise<{ totalSpent: number; transactionCount: number }> {
    const response = await axios.get(
      `${this.baseURL}/api/transactions/${cardId}/total`,
      { headers: this.getAuthHeaders() }
    )
    return response.data
  }
}

export default new TransactionService()

