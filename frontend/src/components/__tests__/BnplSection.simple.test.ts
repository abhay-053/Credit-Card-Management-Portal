import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import BnplSection from '../BnplSection.vue'

// Simple test that just checks if the component can be mounted
describe('BnplSection - Simple Tests', () => {
  const store = createStore({
    modules: {
      bnpl: {
        namespaced: true,
        state: {},
        actions: {},
        mutations: {},
        getters: {}
      }
    }
  })

  const mockBnplPlans = []

  it('can mount BnplSection component', () => {
    const wrapper = mount(BnplSection, {
      props: {
        bnplPlans: mockBnplPlans
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('renders with empty BNPL plans', () => {
    const wrapper = mount(BnplSection, {
      props: {
        bnplPlans: []
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('renders with BNPL plans data', () => {
    const plans = [
      {
        transactionId: 1,
        merchantName: 'Test Merchant',
        totalAmount: 6000,
        paidAmount: 1000,
        remainingAmount: 5000,
        monthlyEMI: 1000,
        tenureMonths: 6,
        status: 'ACTIVE'
      }
    ]

    const wrapper = mount(BnplSection, {
      props: {
        bnplPlans: plans
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('accepts BNPL props correctly', () => {
    const wrapper = mount(BnplSection, {
      props: {
        bnplPlans: mockBnplPlans
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.props('bnplPlans')).toEqual(mockBnplPlans)
  })
})
