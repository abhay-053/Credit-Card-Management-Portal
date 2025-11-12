import { Module } from 'vuex'
import transactionService, { CreateTransactionRequest, Transaction } from '../../services/transactionService'

interface PaymentState {
  loading: boolean
  error: string | null
  recentTransactions: Transaction[]
}

const payments: Module<PaymentState, any> = {
  namespaced: true,
  
  state: {
    loading: false,
    error: null,
    recentTransactions: []
  },

  mutations: {
    SET_LOADING(state, loading: boolean) {
      state.loading = loading
    },
    SET_ERROR(state, error: string | null) {
      state.error = error
    },
    SET_RECENT_TRANSACTIONS(state, transactions: Transaction[]) {
      state.recentTransactions = transactions
    },
    ADD_RECENT_TRANSACTION(state, transaction: Transaction) {
      state.recentTransactions.unshift(transaction)
    }
  },

  actions: {
    // Create a new transaction (replaces old payment methods)
    async createTransaction({ commit }, transactionData: CreateTransactionRequest) {
      commit('SET_LOADING', true)
      commit('SET_ERROR', null)

      try {
        const transaction = await transactionService.createTransaction(transactionData)
        commit('ADD_RECENT_TRANSACTION', transaction)
        return transaction
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Transaction failed'
        commit('SET_ERROR', errorMessage)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // Process BNPL payment (creates a BNPL transaction)
    async processBnplPayment({ commit }, { cardId, merchantName, amount, category, cardType, lastFour, tenureMonths }: {
      cardId: number
      merchantName: string
      amount: number
      category: string
      cardType: string
      lastFour: string
      tenureMonths?: number
    }) {
      commit('SET_LOADING', true)
      commit('SET_ERROR', null)

      try {
        const transactionData: CreateTransactionRequest = {
          cardId,
          merchantName,
          amount,
          transactionDate: new Date().toISOString().split('T')[0],
          category,
          isBnpl: true,
          cardType,
          lastFour,
          tenureMonths
        }

        const transaction = await transactionService.createTransaction(transactionData)
        commit('ADD_RECENT_TRANSACTION', transaction)
        return transaction
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'BNPL payment failed'
        commit('SET_ERROR', errorMessage)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // Process regular credit card payment
    async processCreditCardPayment({ commit }, { cardId, merchantName, amount, category, cardType, lastFour }: {
      cardId: number
      merchantName: string
      amount: number
      category: string
      cardType: string
      lastFour: string
    }) {
      commit('SET_LOADING', true)
      commit('SET_ERROR', null)

      try {
        const transactionData: CreateTransactionRequest = {
          cardId,
          merchantName,
          amount,
          transactionDate: new Date().toISOString().split('T')[0],
          category,
          isBnpl: false,
          cardType,
          lastFour
        }

        const transaction = await transactionService.createTransaction(transactionData)
        commit('ADD_RECENT_TRANSACTION', transaction)
        return transaction
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Credit card payment failed'
        commit('SET_ERROR', errorMessage)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // Get recent transactions for a card
    async getRecentTransactions({ commit }, cardId: number) {
      commit('SET_LOADING', true)
      commit('SET_ERROR', null)

      try {
        const response = await transactionService.getTransactionHistory(cardId, { page: 0, size: 5 })
        commit('SET_RECENT_TRANSACTIONS', response.transactions)
        return response.transactions
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Failed to get recent transactions'
        commit('SET_ERROR', errorMessage)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // Get BNPL transactions for a card
    async getBnplTransactions({ commit }, cardId: number) {
      commit('SET_LOADING', true)
      commit('SET_ERROR', null)

      try {
        const transactions = await transactionService.getBnplTransactions(cardId)
        return transactions
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Failed to get BNPL transactions'
        commit('SET_ERROR', errorMessage)
        throw error
      } finally {
        commit('SET_LOADING', false)
      }
    },

    clearError({ commit }) {
      commit('SET_ERROR', null)
    }
  }
}

export default payments
