import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import Login from '../Login.vue'

//Mock vue3-toastify safely (ES module workaround)
vi.mock('vue3-toastify', async () => {
  const actual = await vi.importActual<typeof import('vue3-toastify')>('vue3-toastify')
  return {
    ...actual,
    toast: {
      success: vi.fn(),
      error: vi.fn(),
      warn: vi.fn()
    }
  }
})

//Mock vuex
const mockDispatch = vi.fn().mockResolvedValue(true)
vi.mock('vuex', () => ({
  useStore: () => ({
    dispatch: mockDispatch
  })
}))

//Mock vue-router
const mockPush = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockPush
  })
}))

//Import mocked hooks
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'

describe('Login.vue', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders login form initially', () => {
    const wrapper = mount(Login)
    expect(wrapper.text()).toContain('Sign In')
  })

  it('shows validation errors when login form is submitted empty', async () => {
    const wrapper = mount(Login)
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.text()).toContain('Email is required')
    expect(wrapper.text()).toContain('Password is required')
    expect(toast.warn).toHaveBeenCalledWith('Please fix the highlighted errors.')
  })

  it('dispatches login and navigates to dashboard on success', async () => {
    const wrapper = mount(Login)
    await wrapper.find('input[type="email"]').setValue('user@example.com')
    await wrapper.find('input[type="password"]').setValue('securepass')
    await wrapper.find('form').trigger('submit.prevent')

    expect(useStore().dispatch).toHaveBeenCalledWith('auth/login', {
      email: 'user@example.com',
      password: 'securepass'
    })
    expect(useRouter().push).toHaveBeenCalledWith('/dashboard')
    expect(toast.success).toHaveBeenCalledWith('Login successful! 🎉')
  })

  it('shows invalid email error in register form', async () => {
    const wrapper = mount(Login)
    await wrapper.findAll('button')[1].trigger('click') // Switch to register
    await wrapper.find('input[placeholder="Enter your email"]').setValue('invalid-email')
    await wrapper.find('form').trigger('submit.prevent')

    expect(wrapper.text()).toContain('Invalid email')
    expect(toast.warn).toHaveBeenCalledWith('Please fix the highlighted errors.')
  })
})
