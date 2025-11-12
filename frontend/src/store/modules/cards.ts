import axios from 'axios'
import type { Module } from 'vuex'

// Types
interface Card {
  id: number
  cardType: string
  lastFour: string
  cardName: string
  status?: string
}

interface CardsState {
  cards: Card[]
  loading: boolean
  error: string | null
}

interface RootState {

}

const state: CardsState = {
  cards: [
    {
      id: 1,
      cardType: 'VISA',
      lastFour: '1234',
      cardName: 'Platinum',
      status: 'ACTIVE'
    },
    {
      id: 2,
      cardType: 'Mastercard',
      lastFour: '5678',
      cardName: 'Gold',
      status: 'ACTIVE'
    }
  ],
  loading: false,
  error: null
}

const mutations = {
  SET_LOADING(state: CardsState, loading: boolean) {
    state.loading = loading
  },
  SET_CARDS(state: CardsState, cards: Card[]) {
    state.cards = cards
  },
  SET_ERROR(state: CardsState, error: string | null) {
    state.error = error
  }
}

const actions = {
  async fetchCards({ commit }: any) {
    commit('SET_LOADING', true)
    try {
      const token = localStorage.getItem('token')
      const headers = token ? { Authorization: `Bearer ${token}` } : {}
      
      const response = await axios.get(`http://localhost:8080/cards`, { headers })
      commit('SET_CARDS', response.data)
      return response.data
    } catch (error: any) {
      commit('SET_ERROR', error.response?.data?.message || 'Failed to fetch cards')
      console.error('Error fetching cards:', error)
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  }
}

const getters = {
  getActiveCards: (state: CardsState) => {
    return state.cards.filter(card => card.status === 'ACTIVE')
  },
  getCardById: (state: CardsState) => (id: number) => {
    return state.cards.find(card => card.id === id)
  }
}

const cardsModule: Module<CardsState, RootState> = {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

export default cardsModule
export type { Card, CardsState }
