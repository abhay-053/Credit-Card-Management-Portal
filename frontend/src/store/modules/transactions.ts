import axios from 'axios'
import type { Module } from 'vuex'

// Types
interface Transaction {
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

interface CreateTransactionRequest {
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

interface TransactionHistoryResponse {
  transactions: Transaction[]
  currentPage: number
  totalPages: number
  totalItems: number
}

interface AnalyticsResponse {
  totalSpent: number
  transactionCount: number
  categoryBreakdown: CategorySpending[]
  monthlyTrends: MonthlySpending[]
  averageTransactionAmount: number
  bnplTransactionCount: number
}

interface CategorySpending {
  category: string
  amount: number
  percentage: number
}

interface MonthlySpending {
  year: number
  month: number
  total: number
  monthName: string
}

interface TransactionsState {
  transactions: Transaction[]
  analytics: AnalyticsResponse | null
  monthlyTrends: MonthlySpending[]
  loading: boolean
  error: string | null
  pagination: {
    currentPage: number
    totalPages: number
    totalItems: number
  }
}

interface RootState {
  // Add other module states here if needed
}

const state: TransactionsState = {
  transactions: [],
  analytics: null,
  monthlyTrends: [],
  loading: false,
  error: null,
  pagination: {
    currentPage: 0,
    totalPages: 0,
    totalItems: 0
  }
}

const mutations = {
  SET_LOADING(state: TransactionsState, loading: boolean) {
    state.loading = loading
  },
  SET_TRANSACTIONS(state: TransactionsState, data: TransactionHistoryResponse) {
    state.transactions = data.transactions
    state.pagination = {
      currentPage: data.currentPage,
      totalPages: data.totalPages,
      totalItems: data.totalItems
    }
  },
  SET_ANALYTICS(state: TransactionsState, analytics: AnalyticsResponse) {
    state.analytics = analytics
  },
  SET_MONTHLY_TRENDS(state: TransactionsState, trends: MonthlySpending[]) {
    state.monthlyTrends = trends
  },
  SET_ERROR(state: TransactionsState, error: string | null) {
    state.error = error
  },
  ADD_TRANSACTION(state: TransactionsState, transaction: Transaction) {
    state.transactions.unshift(transaction)
    state.pagination.totalItems += 1
  },
  CLEAR_ERROR(state: TransactionsState) {
    state.error = null
  }
}

const actions = {
  async fetchTransactions({ commit }: any, { cardId, page = 0, size = 10, category, isBnpl, merchantName }: {
    cardId: number
    page?: number
    size?: number
    category?: string
    isBnpl?: boolean
    merchantName?: string
  }) {
    commit('SET_LOADING', true)
    commit('CLEAR_ERROR')
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString()
      })
      
      if (category) params.append('category', category)
      if (isBnpl !== undefined) params.append('isBnpl', isBnpl.toString())
      if (merchantName) params.append('merchantName', merchantName)

      const token = localStorage.getItem('token')
      const headers = token ? { Authorization: `Bearer ${token}` } : {}

      const response = await axios.get(`http://localhost:8080/api/transactions/${cardId}?${params}`, { headers })
      commit('SET_TRANSACTIONS', response.data)
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to fetch transactions')
      console.error('Error fetching transactions:', error)
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async createTransaction({ commit }: any, transactionData: CreateTransactionRequest) {
    commit('SET_LOADING', true)
    commit('CLEAR_ERROR')
    try {
      const token = localStorage.getItem('token')
      const headers = token ? { Authorization: `Bearer ${token}` } : {}
      
      const response = await axios.post('http://localhost:8080/api/transactions', transactionData, { headers })
      commit('ADD_TRANSACTION', response.data)
      return response.data
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to create transaction')
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchTransactionById({ commit }: any, transactionId: number) {
    commit('SET_LOADING', true)
    commit('CLEAR_ERROR')
    try {
      const token = localStorage.getItem('token')
      const headers = token ? { Authorization: `Bearer ${token}` } : {}
      
      const response = await axios.get(`http://localhost:8080/api/transactions/details/${transactionId}`, { headers })
      return response.data
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to fetch transaction')
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchAnalytics({ commit }: any, cardId: number) {
    commit('SET_LOADING', true)
    commit('CLEAR_ERROR')
    try {
      const token = localStorage.getItem('token')
      const headers = token ? { Authorization: `Bearer ${token}` } : {}
      
      const response = await axios.get(`http://localhost:8080/api/transactions/${cardId}/analytics`, { headers })
      commit('SET_ANALYTICS', response.data)
      return response.data
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to fetch analytics')
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchMonthlyTrends({ commit }: any, { cardId, startDate }: { cardId: number, startDate?: string }) {
    commit('SET_LOADING', true)
    commit('CLEAR_ERROR')
    try {
      const token = localStorage.getItem('token')
      const headers = token ? { Authorization: `Bearer ${token}` } : {}
      
      const params = startDate ? `?startDate=${startDate}` : ''
      const response = await axios.get(`http://localhost:8080/api/transactions/${cardId}/analytics/trends${params}`, { headers })
      commit('SET_MONTHLY_TRENDS', response.data)
      return response.data
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to fetch monthly trends')
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async fetchBnplTransactions({ commit }: any, cardId: number) {
    commit('SET_LOADING', true)
    commit('CLEAR_ERROR')
    try {
      const token = localStorage.getItem('token')
      const headers = token ? { Authorization: `Bearer ${token}` } : {}
      
      const response = await axios.get(`http://localhost:8080/api/transactions/${cardId}/bnpl`, { headers })
      return response.data
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to fetch BNPL transactions')
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },

  clearError({ commit }: any) {
    commit('CLEAR_ERROR')
  }
}

const getters = {
  getTransactionsByCard: (state: TransactionsState) => (cardId: number) => {
    return state.transactions.filter(transaction => transaction.cardId === cardId)
  },
  getBnplTransactions: (state: TransactionsState) => {
    return state.transactions.filter(transaction => transaction.isBnpl)
  },
  getTransactionsByStatus: (state: TransactionsState) => (status: string) => {
    return state.transactions.filter(transaction => transaction.status === status)
  },
  getTransactionsByCategory: (state: TransactionsState) => (category: string) => {
    return state.transactions.filter(transaction => transaction.category === category)
  },
  getTotalSpent: (state: TransactionsState) => {
    return state.analytics?.totalSpent || 0
  },
  getCategoryBreakdown: (state: TransactionsState) => {
    return state.analytics?.categoryBreakdown || []
  },
  getMonthlyTrends: (state: TransactionsState) => {
    return state.monthlyTrends
  },
  getPagination: (state: TransactionsState) => {
    return state.pagination
  }
}

const transactionsModule: Module<TransactionsState, RootState> = {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

export default transactionsModule
export type { 
  Transaction, 
  TransactionsState, 
  CreateTransactionRequest, 
  TransactionHistoryResponse, 
  AnalyticsResponse, 
  CategorySpending, 
  MonthlySpending 
}
