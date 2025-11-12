import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import UpdateCreditLimitModal from '../../src/components/UpdateCreditLimitModal.vue'

function formatINR(value?: number) {
  return Number(value || 0).toLocaleString('en-IN')
}

describe('UpdateCreditLimitModal (ManageLimit.vue) — Unit Tests', () => {
  let wrapper: ReturnType<typeof mount>

  beforeEach(() => {
    wrapper = mount(UpdateCreditLimitModal, {
      props: {
        modelValue: true,
        currentLimit: 5000,
        outstanding: 1000,
        maxLimit: 10000
      }
    })
  })

  it('renders current limit, outstanding, and range hint', () => {
    const hint = wrapper.find('.hint-text')
    expect(hint.exists()).toBe(true)
    expect(hint.text()).toContain(formatINR(1000))
    expect(hint.text()).toContain(formatINR(10000))
  })

  it('disables Update button when proposed limit < outstanding', async () => {
    const input = wrapper.get('input[type="number"]')
    const updateBtn = wrapper.get('.button-primary')

    await input.setValue('900') 
    expect((updateBtn.element as HTMLButtonElement).disabled).toBe(true)
  })

  it('enables Update button when proposed limit >= outstanding and <= maxLimit', async () => {
    const input = wrapper.get('input[type="number"]')
    const updateBtn = wrapper.get('.button-primary')

    await input.setValue('2000') // valid
    expect((updateBtn.element as HTMLButtonElement).disabled).toBe(false)
  })

  it('emits "next" with newLimit when Update clicked', async () => {
    const input = wrapper.get('input[type="number"]')
    const updateBtn = wrapper.get('.button-primary')

    await input.setValue('3000')
    await updateBtn.trigger('click')

    const emitted = wrapper.emitted('next')
    expect(emitted).toBeTruthy()
    const payload = emitted![0][0] as { newLimit: number }
    expect(payload.newLimit).toBe(3000)
  })

  it('emits update:modelValue=false when close button clicked', async () => {
    const closeBtn = wrapper.get('.close-button')
    await closeBtn.trigger('click')

    const emitted = wrapper.emitted('update:modelValue')
    expect(emitted).toBeTruthy()
    const last = emitted!.pop()
    expect(last![0]).toBe(false)
  })

  it('input min/max attributes are bound correctly', () => {
    const input = wrapper.get('input[type="number"]')
    expect(input.attributes('min')).toBe('1000')
    expect(input.attributes('max')).toBe('10000')
  })

  it('clicking backdrop (self) closes modal (update:modelValue=false)', async () => {
    const backdrop = wrapper.get('.backdrop-modal')
    await backdrop.trigger('click')
    const emitted = wrapper.emitted('update:modelValue')
    expect(emitted).toBeTruthy()
    const last = emitted!.pop()
    expect(last![0]).toBe(false)
  })
})
