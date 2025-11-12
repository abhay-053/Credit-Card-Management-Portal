import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Card from '../Card.vue'

describe('Card Component', () => {
  // Mock card data
  const mockCard = {
    id: 1,
    cardNumber: '1234567890123456',
    cardHolderName: 'John Doe',
    cardStatus: 'ACTIVE',
    creditLimit: 50000,
    availableLimit: 45000,
    expiryDate: '2025-12-31',
    cvv: '123',
    cardType: {
      networkType: 'VISA'
    }
  }

  // Test 1: Check if card renders with basic info
  it('shows card holder name', () => {
    const wrapper = mount(Card, {
      props: {
        card: mockCard
      }
    })
    expect(wrapper.text()).toContain('John Doe')
  })

  // Test 2: Check if card status is displayed
  it('shows card status', () => {
    const wrapper = mount(Card, {
      props: {
        card: mockCard
      }
    })
    expect(wrapper.text()).toContain('ACTIVE')
  })

  // Test 3: Check if toggle appears when showToggle is true
  it('shows toggle when showToggle prop is true', () => {
    const wrapper = mount(Card, {
      props: {
        card: mockCard,
        showToggle: true
      }
    })
    expect(wrapper.find('.toggle-checkbox').exists()).toBe(true)
  })

  // Test 4: Check if menu appears when showMenu is true
  it('shows menu button when showMenu prop is true', () => {
    const wrapper = mount(Card, {
      props: {
        card: mockCard,
        showMenu: true
      }
    })
    expect(wrapper.find('.menu-btn').exists()).toBe(true)
  })

  // Test 5: Check if card shows no data message when no card provided
  it('shows no data message when no card provided', () => {
    const wrapper = mount(Card, {
      props: {
        card: null
      }
    })
    expect(wrapper.text()).toContain('No card data available')
  })
}) 