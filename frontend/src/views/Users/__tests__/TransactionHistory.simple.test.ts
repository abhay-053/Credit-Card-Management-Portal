import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import TransactionHistory from '../TransactionHistory.vue'

// Simple test that just checks if the component can be mounted
describe('TransactionHistory - Simple Tests', () => {
  const store = createStore({
    modules: {
      transactions: {
        namespaced: true,
        state: {},
        actions: {},
        mutations: {},
        getters: {}
      }
    }
  })

  it('can mount TransactionHistory component', () => {
    const wrapper = mount(TransactionHistory, {
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('renders without crashing', () => {
    const wrapper = mount(TransactionHistory, {
      global: {
        plugins: [store]
      }
    })

    // Just check that the component renders
    expect(wrapper.find('div').exists()).toBe(true)
  })

  it('has transaction history title', () => {
    const wrapper = mount(TransactionHistory, {
      global: {
        plugins: [store]
      }
    })

    // Check if title exists (this should be safe to test)
    expect(wrapper.text()).toContain('Transaction')
  })
})
