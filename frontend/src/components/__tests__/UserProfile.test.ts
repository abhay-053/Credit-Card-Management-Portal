import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import UserProfile from '../UserProfile.vue'

// Mock vue3-toastify
vi.mock('vue3-toastify', async () => {
  const actual = await vi.importActual<typeof import('vue3-toastify')>('vue3-toastify')
  return {
    ...actual,
    toast: {
      success: vi.fn(),
      error: vi.fn()
    }
  }
})

//Mock Vuex
const mockDispatch = vi.fn().mockResolvedValue({
  name: 'John Doe',
  phoneNumber: '9876543210',
  address: '123 Main St',
  annualIncome: 120000,
  email: 'john@example.com',
  id: 42
})

const mockGetters = {
  'user/profile': {
    email: 'john@example.com',
    id: 42
  }
}

vi.mock('vuex', () => ({
  useStore: () => ({
    dispatch: mockDispatch,
    getters: mockGetters
  })
}))

//Mock localStorage
Object.defineProperty(window, 'localStorage', {
  value: {
    getItem: vi.fn(() =>
      'Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.' +
      btoa(JSON.stringify({ userId: 42, sub: 'john@example.com', role: 'USER' })) +
      '.signature'
    )
  },
  writable: true
})

describe('UserProfile.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  // Test 1: Renders all profile sections
  it('renders all profile sections', () => {
    const wrapper = mount(UserProfile)
    expect(wrapper.text()).toContain('My Profile')
    expect(wrapper.text()).toContain('Personal Information')
    expect(wrapper.text()).toContain('Financial Information')
    expect(wrapper.text()).toContain('Account Information')
  })

  // Test 2: Loads profile data on mount
  it('loads profile data from store on mount', async () => {
    const wrapper = mount(UserProfile)
    await new Promise(resolve => setTimeout(resolve, 0))

    expect(mockDispatch).toHaveBeenCalledWith('user/fetchProfile', 42)

    const nameInput = wrapper.find('input#name').element as HTMLInputElement
    const phoneInput = wrapper.find('input#phone').element as HTMLInputElement
    const addressInput = wrapper.find('input#address').element as HTMLInputElement
    const incomeInput = wrapper.find('input#income').element as HTMLInputElement

    expect(nameInput.value).toBe('John Doe')
    expect(phoneInput.value).toBe('9876543210')
    expect(addressInput.value).toBe('123 Main St')
    expect(incomeInput.value).toBe('120000')
  })

  // Test 3: Shows BNPL eligibility badge
  it('shows BNPL eligibility when income is above threshold', async () => {
    const wrapper = mount(UserProfile)
    await new Promise(resolve => setTimeout(resolve, 0))
    expect(wrapper.text()).toContain('Eligible')
  })

  // Test 4: Dispatches updateProfile on form submit
  it('dispatches updateProfile when form is submitted', async () => {
    const wrapper = mount(UserProfile)
    await new Promise(resolve => setTimeout(resolve, 0))
    await wrapper.find('form').trigger('submit.prevent')

    expect(mockDispatch).toHaveBeenCalledWith('user/updateProfile', {
      userId: 42,
      payload: {
        name: 'John Doe',
        phoneNumber: '9876543210',
        address: '123 Main St',
        annualIncome: 120000
      }
    })
  })

  // Test 5: Displays account info from store getters
  it('displays account info from store getters', async () => {
    const wrapper = mount(UserProfile)
    await new Promise(resolve => setTimeout(resolve, 0))
    expect(wrapper.text()).toContain('john@example.com')
    expect(wrapper.text()).toContain('#42')
  })
})
