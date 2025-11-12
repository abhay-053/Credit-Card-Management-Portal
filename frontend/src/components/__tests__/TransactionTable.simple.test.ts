import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import TransactionTable from '../TransactionTable.vue'

// Simple test that just checks if the component can be mounted
describe('TransactionTable - Simple Tests', () => {
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

  const mockTransactions = [
    {
      id: 1,
      merchantName: 'Test Merchant',
      amount: 1000,
      status: 'COMPLETED',
      transactionDate: '2025-09-28',
      category: 'shopping',
      isBnpl: false
    }
  ]

  it('can mount TransactionTable component', () => {
    const wrapper = mount(TransactionTable, {
      props: {
        transactions: mockTransactions,
        loading: false
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('renders with empty transactions', () => {
    const wrapper = mount(TransactionTable, {
      props: {
        transactions: [],
        loading: false
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('renders with loading state', () => {
    const wrapper = mount(TransactionTable, {
      props: {
        transactions: [],
        loading: true
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('accepts transaction props correctly', () => {
    const wrapper = mount(TransactionTable, {
      props: {
        transactions: mockTransactions,
        loading: false
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.props('transactions')).toEqual(mockTransactions)
    expect(wrapper.props('loading')).toBe(false)
  })
})
