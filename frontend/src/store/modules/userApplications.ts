// src/store/modules/applications.ts
import { Module } from "vuex";
import { RootState } from "../index";
import {
  fetchApplications,
  applyForCard,
  updateApplicationStatus,
  deleteApplication,
} from "../../services/cardApplicationService";

export interface Application {
  id: number;
  cardTypeName: string;
  cardNetworkType: string;
  requestedLimit: number;
  status: string;
  statusText: string;
  appliedDate: string;
  reviewerName?: string | null;
  reviewDate?: string | null;
}

export interface ApplicationsState {
  applications: Application[];
  loading: boolean;
  error: string | null;
}

const STATUS_TEXT_MAP: Record<string, string> = {
  PENDING: "Under Review",
  ACCEPTED: "Approved",
  REJECTED: "Rejected",
};

const applications: Module<ApplicationsState, RootState> = {
  namespaced: true,

  state: (): ApplicationsState => ({
    applications: [],
    loading: false,
    error: null,
  }),

  getters: {
    allApplications: (state) => state.applications,
    isLoading: (state) => state.loading,
    error: (state) => state.error,
  },

  mutations: {
    SET_LOADING(state, payload: boolean) {
      state.loading = payload;
    },
    SET_APPLICATIONS(state, payload: Application[]) {
      state.applications = payload;
    },
    ADD_APPLICATION(state, payload: Application) {
      state.applications.unshift(payload); // add newest first
    },
    SET_ERROR(state, payload: string | null) {
      state.error = payload;
    },
  },

  actions: {
    async fetchAll({ commit }) {
      commit("SET_LOADING", true);
      commit("SET_ERROR", null);
      try {
        const data = await fetchApplications();
        const mapped = data.map((app: any) => ({
          id: app.id,
          cardTypeName: app.cardTypeName,
          cardNetworkType: app.cardNetworkType,
          requestedLimit: app.requestedLimit,
          status: app.status,
          statusText: STATUS_TEXT_MAP[app.status] || "Unknown",
          appliedDate: new Date(app.applicationDate).toLocaleDateString("en-GB", {
            day: "2-digit",
            month: "long",
            year: "numeric",
          }),
          reviewerName: app.reviewerName || null,
          reviewDate: app.reviewDate
            ? new Date(app.reviewDate).toLocaleDateString("en-GB", {
                day: "2-digit",
                month: "long",
                year: "numeric",
              })
            : null,
        }));
        commit("SET_APPLICATIONS", mapped);
      } catch (error: any) {
        commit("SET_ERROR", error.message || "Failed to fetch applications");
      } finally {
        commit("SET_LOADING", false);
      }
    },

    async applyCard({ commit, dispatch }, payload) {
      try {
        await applyForCard(payload);
        // Refresh applications list after new application
        await dispatch("fetchAll");
      } catch (error: any) {
        commit("SET_ERROR", error.message || "Failed to apply for card");
      }
    },

    async updateStatus({ dispatch }, { id, status }) {
      await updateApplicationStatus(id, status);
      await dispatch("fetchAll");
    },

    async deleteApp({ dispatch }, id: number) {
      await deleteApplication(id);
      await dispatch("fetchAll");
    },
  },
};

export default applications;
