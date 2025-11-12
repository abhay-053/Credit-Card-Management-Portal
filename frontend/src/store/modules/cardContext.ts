import { Module } from 'vuex'

interface CardContextState {
  selectedCardId: number | null
  selectedCard: any | null
  availableCards: any[]
  loading: boolean
  error: string | null
}

interface RootState {
  // Add other module states here if needed
}

const state: CardContextState = {
  selectedCardId: null,
  selectedCard: null,
  availableCards: [],
  loading: false,
  error: null
}

const mutations = {
  SET_SELECTED_CARD_ID(state: CardContextState, cardId: number | null) {
    state.selectedCardId = cardId
    // Update selected card when ID changes
    if (cardId && state.availableCards.length > 0) {
      state.selectedCard = state.availableCards.find(card => card.id === cardId) || null
    } else {
      state.selectedCard = null
    }
  },
  
  SET_SELECTED_CARD(state: CardContextState, card: any) {
    state.selectedCard = card
    state.selectedCardId = card ? card.id : null
  },
  
  SET_AVAILABLE_CARDS(state: CardContextState, cards: any[]) {
    state.availableCards = cards
    // If no card is selected but cards are available, select the first one
    if (!state.selectedCardId && cards.length > 0) {
      state.selectedCardId = cards[0].id
      state.selectedCard = cards[0]
    }
  },
  
  SET_LOADING(state: CardContextState, loading: boolean) {
    state.loading = loading
  },
  
  SET_ERROR(state: CardContextState, error: string | null) {
    state.error = error
  },
  
  CLEAR_CARD_CONTEXT(state: CardContextState) {
    state.selectedCardId = null
    state.selectedCard = null
    state.availableCards = []
    state.loading = false
    state.error = null
  }
}

const actions = {
  async initializeCardContext({ commit, dispatch }: any) {
    commit('SET_LOADING', true)
    try {
      // Fetch available cards
      const cards = await dispatch('cards/fetchCards', null, { root: true })
      commit('SET_AVAILABLE_CARDS', cards || [])
      
      // Try to restore selected card from localStorage
      const savedCardId = localStorage.getItem('selectedCardId')
      if (savedCardId && cards && cards.length > 0) {
        const cardId = parseInt(savedCardId)
        const cardExists = cards.some((card: any) => card.id === cardId)
        if (cardExists) {
          commit('SET_SELECTED_CARD_ID', cardId)
        } else {
          // If saved card doesn't exist, select first available card
          commit('SET_SELECTED_CARD_ID', cards[0].id)
        }
      } else if (cards && cards.length > 0) {
        // No saved card, select first available
        commit('SET_SELECTED_CARD_ID', cards[0].id)
      }
      
      return cards
    } catch (error: any) {
      commit('SET_ERROR', error.message || 'Failed to initialize card context')
      throw error
    } finally {
      commit('SET_LOADING', false)
    }
  },
  
  selectCard({ commit }: any, cardId: number) {
    commit('SET_SELECTED_CARD_ID', cardId)
    // Persist selection to localStorage
    localStorage.setItem('selectedCardId', cardId.toString())
  },
  
  selectCardByObject({ commit }: any, card: any) {
    commit('SET_SELECTED_CARD', card)
    // Persist selection to localStorage
    if (card) {
      localStorage.setItem('selectedCardId', card.id.toString())
    } else {
      localStorage.removeItem('selectedCardId')
    }
  },
  
  clearCardContext({ commit }: any) {
    commit('CLEAR_CARD_CONTEXT')
    localStorage.removeItem('selectedCardId')
  }
}

const getters = {
  getSelectedCard: (state: CardContextState) => state.selectedCard,
  getSelectedCardId: (state: CardContextState) => state.selectedCardId,
  getAvailableCards: (state: CardContextState) => state.availableCards,
  getActiveCards: (state: CardContextState) => state.availableCards.filter(card => card.status === 'ACTIVE'),
  getCardById: (state: CardContextState) => (id: number) => state.availableCards.find(card => card.id === id),
  hasMultipleCards: (state: CardContextState) => state.availableCards.length > 1,
  isLoading: (state: CardContextState) => state.loading,
  getError: (state: CardContextState) => state.error
}

const cardContextModule: Module<CardContextState, RootState> = {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}

export default cardContextModule
export type { CardContextState }
