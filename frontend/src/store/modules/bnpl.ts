import axios from 'axios'
import type { Module } from 'vuex'

// Types
interface BnplPlan {
  id: string
  merchant: string
  status: string
  tenureMonths: number
  totalAmount: number
  paidAmount: number
  remainingAmount: number
  monthlyEmi: number
  paidInstallments: number
  startDate: string
  nextDueDate: string
}

interface BnplState {
  plans: BnplPlan[]
  loading: boolean
  error: string | null
}

interface RootState {
  // Add other module states here if needed
}

const state: BnplState = {
  plans: [
    {
      id: '1',
      merchant: 'Amazon',
      status: 'ACTIVE',
      tenureMonths: 6,
      totalAmount: 2499,
      paidAmount: 1250,
      remainingAmount: 1249,
      monthlyEmi: 416.5,
      paidInstallments: 3,
      startDate: '2023-11-15',
      nextDueDate: '2024-02-15'
    },
    {
      id: '2',
      merchant: 'Flipkart',
      status: 'ACTIVE',
      tenureMonths: 12,
      totalAmount: 899,
      paidAmount: 180,
      remainingAmount: 719,
      monthlyEmi: 75,
      paidInstallments: 2,
      startDate: '2023-11-20',
      nextDueDate: '2024-01-20'
    }
  ],
  loading: false,
  error: null
}

const mutations = {
  SET_LOADING(state: BnplState, loading: boolean) {
    state.loading = loading
  },
  SET_PLANS(state: BnplState, plans: BnplPlan[]) {
    state.plans = plans
  },
  SET_ERROR(state: BnplState, error: string | null) {
    state.error = error
  }
}

const actions = {
  async fetchActivePlans({ commit }: any) {
    commit('SET_LOADING', true)
    try {
      const response = await axios.get('http://localhost:8080/api/bnpl/active')
      commit('SET_PLANS', response.data)
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to fetch BNPL plans')
      console.error('Error fetching BNPL plans:', error)
    } finally {
      commit('SET_LOADING', false)
    }
  },

  async processEmiPayment({ commit, dispatch }: any, { planId, amount }: { planId: string, amount: number }) {
    commit('SET_LOADING', true)
    try {
      await axios.post(`http://localhost:8080/api/bnpl/${planId}/payment`, { amount })
      // Refresh plans after payment
      await dispatch('bnpl/fetchActivePlans')
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to process EMI payment')
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  }
}

const getters = {
  getActivePlans: (state: BnplState) => {
    return state.plans.filter(plan => plan.status === 'ACTIVE')
  },
  getOverduePlans: (state: BnplState) => {
    const today = new Date()
    return state.plans.filter(plan => 
      plan.status === 'ACTIVE' && new Date(plan.nextDueDate) < today
    )
  }
}

const bnplModule: Module<BnplState, RootState> = {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

export default bnplModule
export type { BnplPlan, BnplState }
