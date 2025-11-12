import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createStore } from 'vuex'
import StatusBadge from '../StatusBadge.vue'

// Simple test that just checks if the component can be mounted
describe('StatusBadge - Simple Tests', () => {
  const store = createStore({
    modules: {}
  })

  it('can mount StatusBadge component', () => {
    const wrapper = mount(StatusBadge, {
      props: {
        status: 'COMPLETED'
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })

  it('renders with different statuses', () => {
    const statuses = ['COMPLETED', 'PENDING', 'FAILED', 'BNPL_ACTIVE']
    
    statuses.forEach(status => {
      const wrapper = mount(StatusBadge, {
        props: { status },
        global: {
          plugins: [store]
        }
      })
      
      expect(wrapper.exists()).toBe(true)
    })
  })

  it('accepts status prop correctly', () => {
    const wrapper = mount(StatusBadge, {
      props: {
        status: 'COMPLETED'
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.props('status')).toBe('COMPLETED')
  })

  it('handles empty status gracefully', () => {
    const wrapper = mount(StatusBadge, {
      props: {
        status: ''
      },
      global: {
        plugins: [store]
      }
    })

    expect(wrapper.exists()).toBe(true)
  })
})
