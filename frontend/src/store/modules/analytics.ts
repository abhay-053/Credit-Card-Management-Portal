import type { Module } from 'vuex'

interface CategoryBreakdown {
  name: string
  percentage: number
  amount: number
}

interface TopMerchant {
  name: string
  count: number
  amount: number
}

interface AnalyticsData {
  thisMonth: number
  lastMonth: number
  change: number
  categoryBreakdown: CategoryBreakdown[]
  topMerchants: TopMerchant[]
}

interface AnalyticsState {
  data: AnalyticsData
  loading: boolean
  error: string | null
}

interface RootState {
  
}

const state: AnalyticsState = {
  data: {
    thisMonth: 0,
    lastMonth: 0,
    change: 0,
    categoryBreakdown: [],
    topMerchants: []
  },
  loading: false,
  error: null
}

const mutations = {
  SET_LOADING(state: AnalyticsState, loading: boolean) {
    state.loading = loading
  },
  SET_ANALYTICS(state: AnalyticsState, analytics: AnalyticsData) {
    state.data = analytics
  },
  SET_ERROR(state: AnalyticsState, error: string | null) {
    state.error = error
  }
}

const actions = {
  async fetchAnalytics({ commit }: any) {
    commit('SET_LOADING', true)
    try {
      // Mock data for now
      const mockAnalytics: AnalyticsData = {
        thisMonth: 45000,
        lastMonth: 38000,
        change: 18.4,
        categoryBreakdown: [
          { name: 'Shopping', percentage: 45, amount: 20250 },
          { name: 'Food', percentage: 30, amount: 13500 },
          { name: 'Travel', percentage: 25, amount: 11250 }
        ],
        topMerchants: [
          { name: 'Amazon', count: 12, amount: 15000 },
          { name: 'Zomato', count: 8, amount: 8000 },
          { name: 'Uber', count: 6, amount: 5000 }
        ]
      }
      commit('SET_ANALYTICS', mockAnalytics)
    } catch (error: any) {
      commit('SET_ERROR', 'Failed to fetch analytics')
      console.error('Error fetching analytics:', error)
    } finally {
      commit('SET_LOADING', false)
    }
  }
}

const getters = {
  getSpendingTrend: (state: AnalyticsState) => {
    return {
      current: state.data.thisMonth,
      previous: state.data.lastMonth,
      change: state.data.change
    }
  }
}

const analyticsModule: Module<AnalyticsState, RootState> = {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

export default analyticsModule
export type { AnalyticsData, AnalyticsState, CategoryBreakdown, TopMerchant }
