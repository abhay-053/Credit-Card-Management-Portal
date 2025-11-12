import { Module } from 'vuex';
import { getCardApplications, updateApplicationStatus } from '../../services/cards-service';
import type { CardApplication } from '../../types/cardApplication';
import { RootState } from '../index';

/**
 * State interface for the Applications Vuex module
 */
export interface ApplicationsState {
  /** List of all card applications */
  applications: CardApplication[];
  /** Loading indicator for async actions */
  loading: boolean;
  /** Error message, if any */
  error: string | null;
}

/**
 * Vuex module for managing card applications.
 * Namespaced to allow modular store structure.
 */
const applications: Module<ApplicationsState, RootState> = {
  namespaced: true,

  /**
   * Initial state for the module
   */
  state: (): ApplicationsState => ({
    applications: [],
    loading: false,
    error: null,
  }),

  /**
   * Mutations to update the state
   */
  mutations: {
    /**
     * Replace the applications list with a new array
     * @param state - module state
     * @param applications - new array of CardApplication
     */
    setApplications(state, applications: CardApplication[]) {
      state.applications = applications;
    },

    /**
     * Set the loading state
     * @param state - module state
     * @param loading - true/false
     */
    setLoading(state, loading: boolean) {
      state.loading = loading;
    },

    /**
     * Set the error message
     * @param state - module state
     * @param error - error string or null
     */
    setError(state, error: string | null) {
      state.error = error;
    },

    /**
     * Update a single application in the state
     * @param state - module state
     * @param updatedApplication - updated CardApplication object
     */
    updateApplication(state, updatedApplication: CardApplication) {
      const index = state.applications.findIndex(app => app.id === updatedApplication.id);
      if (index !== -1) {
        state.applications[index] = updatedApplication;
      }
    },
  },

  /**
   * Actions to perform async operations and commit mutations
   */
  actions: {
    /**
     * Fetch all card applications from API
     * Commits setApplications, setLoading, and setError mutations
     */
    async fetchApplications({ state, commit }) {
      if (state.applications.length > 0) {
        /**
         * Data already in store, no need to fetch
         */
        return state.applications
      }
      try {
        commit('setLoading', true);
        commit('setError', null);
        const applications = await getCardApplications();
        commit('setApplications', applications);
      } catch (error) {
        commit('setError', 'Failed to fetch applications');
        console.error('Error fetching applications:', error);
      } finally {
        commit('setLoading', false);
      }
    },

    /**
     * Update status of a specific card application
     * Commits updateApplication mutation after successful API call
     * @param param0 - Vuex context object containing commit
     * @param param1 - object with applicationId and new status
     */
    async updateApplicationStatus(
      { commit },
      { applicationId, status }: { applicationId: number; status: 'ACCEPTED' | 'REJECTED' }
    ) {
      try {
        commit('setLoading', true);
        commit('setError', null);
        const updatedApplication = await updateApplicationStatus(applicationId, status);
        if (updatedApplication) {
          commit('updateApplication', updatedApplication);
        }
        return updatedApplication;
      } catch (error) {
        commit('setError', 'Failed to update application status');
        console.error('Error updating application status:', error);
        throw error;
      } finally {
        commit('setLoading', false);
      }
    },
  },

  /**
   * Getters to access derived or raw state
   */
  getters: {
    /** Get full applications array */
    applications: (state) => state.applications,

    /** Get loading state */
    loading: (state) => state.loading,

    /** Get error message */
    error: (state) => state.error,

    /** Filter applications with status PENDING */
    pendingApplications: (state) => state.applications.filter(app => app.status === 'PENDING'),

    /** Filter applications with status ACCEPTED */
    acceptedApplications: (state) => state.applications.filter(app => app.status === 'ACCEPTED'),

    /** Filter applications with status REJECTED */
    rejectedApplications: (state) => state.applications.filter(app => app.status === 'REJECTED'),
  },
};

export default applications;
