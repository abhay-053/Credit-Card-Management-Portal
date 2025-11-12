import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import CreateTransaction from '../CreateTransaction.vue'

// Simple test that just checks if the component can be mounted
describe('CreateTransaction - Simple Tests', () => {
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

  it('can mount CreateTransaction component', () => {
    const wrapper = mount(CreateTransaction, {
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('renders without crashing', () => {
    const wrapper = mount(CreateTransaction, {
      global: {
        plugins: [store]
      }
    })

    // Just check that the component renders
    expect(wrapper.find('div').exists()).toBe(true)
  })

  it('has create transaction title', () => {
    const wrapper = mount(CreateTransaction, {
      global: {
        plugins: [store]
      }
    })

    // Check if title exists (this should be safe to test)
    expect(wrapper.text()).toContain('Transaction')
  })
})
