import { Module } from "vuex";
import { RootState } from "../index";
import { getCardTypes } from "../../services/cards-service";

export interface CardType {
  id: number;
  name: string;
  networkType: string;        
  minCardLimit: number;       
  maxCardLimit: number;      
  description: string;
}

// 2️⃣ Define module's state
export interface CardTypesState {
  cardTypes: CardType[];
  loading: boolean;
  error: string | null;
}

// 3️⃣ Vuex module
const cardTypes: Module<CardTypesState, RootState> = {
  namespaced: true,

  state: (): CardTypesState => ({
    cardTypes: [],
    loading: false,
    error: null,
  }),

  getters: {
    allCardTypes: (state) => state.cardTypes,
    isLoading: (state) => state.loading,
    error: (state) => state.error,
  },

  mutations: {
    SET_LOADING(state, payload: boolean) {
      state.loading = payload;
    },
    SET_CARD_TYPES(state, payload: CardType[]) {
      state.cardTypes = payload;       // directly set data from API
    },
    SET_ERROR(state, payload: string | null) {
      state.error = payload;
    },
  },

  actions: {
    async fetchCardTypes({ commit }) {
      commit("SET_LOADING", true);
      commit("SET_ERROR", null);
      try {
        const data = await getCardTypes();  // API call returns array of CardType
        commit("SET_CARD_TYPES", data);
      } catch (error: any) {
        commit("SET_ERROR", error.message || "Failed to fetch card types");
      } finally {
        commit("SET_LOADING", false);
      }
    },
  },
};

export default cardTypes;
