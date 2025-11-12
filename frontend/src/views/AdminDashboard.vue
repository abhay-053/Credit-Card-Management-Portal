<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8 font-['Inter',system-ui,-apple-system,sans-serif]">
    <!-- Header Section -->
    <div class="mb-8 sm:mb-12">
      <h1 class="text-2xl sm:text-3xl lg:text-4xl font-bold text-gray-900 mb-2 sm:mb-4">Admin Dashboard </h1>
      <p class="text-base sm:text-lg text-gray-600">Welcome back, {{adminName}}! Manage credit card applications and user accounts.</p>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex flex-col items-center justify-center py-12">
      <div class="w-8 h-8 border-4 border-gray-200 border-t-blue-600 rounded-full animate-spin mb-4"></div>
      <p class="text-gray-600">Loading dashboard data...</p>
    </div>

    <!-- Dashboard Content -->
    <div v-else class="space-y-8">
      <!-- Summary Cards Row -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
        <div class="bg-white rounded-lg border border-gray-200 p-4 sm:p-6 transition-all duration-200 hover:shadow-md">
          <div class="flex justify-between items-start mb-4">
            <h3 class="text-sm font-medium text-gray-600">Total Applications</h3>
            <div class="w-6 h-6 text-gray-400">
              <svg class="w-full h-full" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
              </svg>
            </div>
          </div>
          <div class="text-2xl sm:text-3xl font-bold text-gray-900 mb-1">{{ totalApplications }}</div>
          <div class="text-sm text-gray-500">All applications</div>
        </div>

        <div class="bg-white rounded-lg border border-gray-200 p-4 sm:p-6 transition-all duration-200 hover:shadow-md">
          <div class="flex justify-between items-start mb-4">
            <h3 class="text-sm font-medium text-gray-600">Pending Applications</h3>
            <div class="w-6 h-6 text-gray-400">
              <svg class="w-full h-full" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
          </div>
          <div class="text-2xl sm:text-3xl font-bold text-gray-900 mb-1">{{ pendingApplications }}</div>
          <div class="text-sm text-gray-500">Require review</div>
        </div>
        <!-- Rejected Applications -->
        <div class="bg-white rounded-lg border border-gray-200 p-4 sm:p-6 transition-all duration-200 hover:shadow-md">
          <div class="flex justify-between items-start mb-4">
            <h3 class="text-sm font-medium text-gray-600">Rejected Applications</h3>
            <div class="w-6 h-6 text-gray-400">
              <svg class="w-full h-full" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </div>
          </div>
          <div class="text-2xl sm:text-3xl font-bold text-gray-900 mb-1">{{ rejectedApplications }}</div>
          <div class="text-sm text-gray-500">Declined applications</div>
        </div>
        <!-- Accepted Applications -->
        <div class="bg-white rounded-lg border border-gray-200 p-4 sm:p-6 transition-all duration-200 hover:shadow-md">
          <div class="flex justify-between items-start mb-4">
            <h3 class="text-sm font-medium text-gray-600">Accepted Applications</h3>
            <div class="w-6 h-6 text-gray-400">
              <svg class="w-full h-full" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>
          </div>
          <div class="text-2xl sm:text-3xl font-bold text-gray-900 mb-1">{{ acceptedApplications }}</div>
          <div class="text-sm text-gray-500">Approved applications</div>
        </div>
      </div>

      <!-- Recent Applications Section -->
      <div class="grid grid-cols-1 lg:grid-cols-1 gap-6">
        <div class="bg-white rounded-lg border border-gray-200 p-4 sm:p-6">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">Recent Applications</h2>
          <div class="space-y-4">
            <div 
              v-for="application in recentApplications" 
              :key="application.id"
              class="flex justify-between items-center py-3 border-b border-gray-100 last:border-b-0"
            >
              <!-- Application Details -->
              <div class="flex-1">
                <h4 class="text-sm font-medium text-gray-900">{{ application.cardNetworkType }} Application</h4>
                <p class="text-sm text-gray-500">₹{{ application.requestedLimit.toLocaleString() }} limit</p>
              </div>
              <!-- Application Status -->
              <span
                class="px-2 py-1 rounded-full text-xs font-medium uppercase tracking-wide"
                :class="{
                  'bg-gray-800 text-white':
                    application.status === APPLICATION_STATUS.PENDING,
                  'bg-green-100 text-green-800':
                    application.status === APPLICATION_STATUS.ACCEPTED,
                  'bg-red-100 text-red-800':
                    application.status === APPLICATION_STATUS.REJECTED
                }"
              >
                {{
                  APPLICATION_STATUS_LABELS[application.status] ||
                  application.status
                }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { mapGetters } from 'vuex'
import { APPLICATION_STATUS, APPLICATION_STATUS_LABELS, USER_ROLE } from '../constants/constants'
import type { CardApplication } from '../types/cardApplication'

export default {
  name: 'AdminDashboard',

  /* Component State */
  data() {
    return {
      /** Loading indicator for dashboard API calls */
      loading: true as boolean,

      /** Status constants for applications */
      APPLICATION_STATUS,

      /** Human-readable labels for statuses */
      APPLICATION_STATUS_LABELS,
    }
  },

  /* Vuex Getters (State Mapping)*/
  computed: {
    ...mapGetters({
      applications: 'applications/applications',
      user: 'auth/user',
    }),

    /**
     * Get admin name if available, otherwise fall back
     * to role detection (Admin/User).
     */
    adminName(): string {
      if (this.user && this.user.name) {
        return this.user.name
      }
      return this.$store.getters['auth/userRole'] ? USER_ROLE.ADMIN : USER_ROLE.USER
    },

    /** Total number of applications */
    totalApplications(): number {
      return (this.applications as CardApplication[]).length
    },

    /** Applications in pending status */
    pendingApplications(): number {
      return (this.applications as CardApplication[]).filter(
        (app) => app.status === APPLICATION_STATUS.PENDING
      ).length
    },

    /** Applications that have been rejected */
    rejectedApplications(): number {
      return (this.applications as CardApplication[]).filter(
        (app) => app.status === APPLICATION_STATUS.REJECTED
      ).length
    },

    /** Applications that have been accepted */
    acceptedApplications(): number {
      return (this.applications as CardApplication[]).filter(
        (app) => app.status === APPLICATION_STATUS.ACCEPTED
      ).length
    },

    /**
     * Recent applications sorted by applicationDate
     * Limited to latest 2 records.
     */
    recentApplications(): CardApplication[] {
      return [...(this.applications as CardApplication[])]
        .sort(
          (a, b) =>
            new Date(b.applicationDate).getTime() -
            new Date(a.applicationDate).getTime()
        )
        .slice(0, 2)
    },
  },

  /* Methods */
  methods: {
    /**
     * Fetches applications from backend and
     * updates local state.
     */
    async loadApplications(): Promise<void> {
      try {
        this.loading = true
        await this.$store.dispatch('applications/fetchApplications')
      } catch (error) {
        console.error('Error loading applications:', error)
      } finally {
        this.loading = false
      }
    },
  },

  /* Lifecycle Hook */
  mounted() {
    this.loadApplications()
  },
}
</script>
