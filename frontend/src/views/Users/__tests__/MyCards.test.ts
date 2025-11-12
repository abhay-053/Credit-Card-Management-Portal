import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import MyCards from '../MyCards.vue'

// Mock the service
vi.mock('../../../services/cards-service', () => ({
  getCards: vi.fn(),
  updateCardStatus: vi.fn()
}))

describe('MyCards Component', () => {
  beforeEach(() => {
    // Reset mocks before each test
    vi.clearAllMocks()
  })

  // Check if the page title is correct
  it('shows the page title', async () => {
    const { getCards } = await import('../../../services/cards-service')
    vi.mocked(getCards).mockResolvedValue([])
    
    const wrapper = mount(MyCards)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('My Credit Cards')
  })

  // Check if the apply button exists
  it('has an apply for new card button', async () => {
    const { getCards } = await import('../../../services/cards-service')
    vi.mocked(getCards).mockResolvedValue([])
    
    const wrapper = mount(MyCards)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('Apply for New Card')
  })

  // Check if the description text is there
  it('shows the description text', async () => {
    const { getCards } = await import('../../../services/cards-service')
    vi.mocked(getCards).mockResolvedValue([])
    
    const wrapper = mount(MyCards)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('Manage your credit cards')
  })

  // Check if component loads properly
  it('loads without errors', async () => {
    const { getCards } = await import('../../../services/cards-service')
    vi.mocked(getCards).mockResolvedValue([])
    
    const wrapper = mount(MyCards)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.exists()).toBe(true)
  })

  // Check if it shows empty state when no cards
  it('shows empty state when no cards', async () => {
    const { getCards } = await import('../../../services/cards-service')
    vi.mocked(getCards).mockResolvedValue([])
    
    const wrapper = mount(MyCards)
    await wrapper.vm.$nextTick()
    
    expect(wrapper.text()).toContain('No Credit Cards Found')
  })
})
