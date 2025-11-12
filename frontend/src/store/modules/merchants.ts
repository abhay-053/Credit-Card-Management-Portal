import axios from 'axios'
import type { Module } from 'vuex'

// Types
interface Merchant {
  id: number
  name: string
}

interface MerchantsState {
  merchants: Merchant[]
  loading: boolean
  error: string | null
}

interface RootState {
  // Add other module states here if needed
}

const state: MerchantsState = {
  merchants: [
    { id: 1, name: 'Amazon' },
    { id: 2, name: 'Zomato' },
    { id: 3, name: 'Uber' },
    { id: 4, name: 'Netflix' },
    { id: 5, name: 'Flipkart' },
    { id: 6, name: 'Swiggy' }
  ],
  loading: false,
  error: null
}

const mutations = {
  SET_LOADING(state: MerchantsState, loading: boolean) {
    state.loading = loading
  },
  SET_MERCHANTS(state: MerchantsState, merchants: Merchant[]) {
    state.merchants = merchants
  },
  SET_ERROR(state: MerchantsState, error: string | null) {
    state.error = error
  }
}

const actions = {
  async fetchMerchants({ commit }: any) {
    commit('SET_LOADING', true)
    try {
      const response = await axios.get('http://localhost:8080/api/merchants')
      commit('SET_MERCHANTS', response.data)
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to fetch merchants')
      console.error('Error fetching merchants:', error)
    } finally {
      commit('SET_LOADING', false)
    }
  }
}

const getters = {
  getMerchantById: (state: MerchantsState) => (id: number) => {
    return state.merchants.find(merchant => merchant.id === id)
  }
}

const merchantsModule: Module<MerchantsState, RootState> = {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

export default merchantsModule
export type { Merchant, MerchantsState }
