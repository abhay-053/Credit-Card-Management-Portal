import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import AdminDashboard from '../AdminDashboard.vue'

// Mock the store
const mockStore = createStore({
  modules: {
    applications: {
      namespaced: true,
      getters: {
        applications: () => [],
        loading: () => false
      },
      actions: {
        fetchApplications: vi.fn()
      }
    },
    auth: {
      namespaced: true,
      getters: {
        user: () => ({ name: 'nikil', email: 'nikil@admin.com' })
      }
    }
  }
})

describe('AdminDashboard Component', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  // Check if the main title is displayed
  it('shows the dashboard title', () => {
    const wrapper = mount(AdminDashboard, {
      global: {
        plugins: [mockStore]
      }
    })
    expect(wrapper.text()).toContain('Admin Dashboard')
  })

  // Check if welcome message is shown
  it('shows welcome message with admin name', () => {
    const wrapper = mount(AdminDashboard, {
      global: {
        plugins: [mockStore]
      }
    })
   expect(wrapper.text()).toContain("Welcome back")
  })

  // Check if loading state is shown initially
  it('shows loading state initially', () => {
    const wrapper = mount(AdminDashboard, {
      global: {
        plugins: [mockStore]
      }
    })
    expect(wrapper.text()).toContain('Loading dashboard data')
  })

  // Check if component loads without errors
  it('loads without crashing', () => {
    const wrapper = mount(AdminDashboard, {
      global: {
        plugins: [mockStore]
      }
    })
    expect(wrapper.exists()).toBe(true)
  })

  // Check if admin name is displayed in welcome message
  it('displays admin name in welcome message', () => {
    const wrapper = mount(AdminDashboard, {
      global: {
        plugins: [mockStore]
      }
    })
    expect(wrapper.text()).toContain("Welcome back, nikil!")
  })
}) 