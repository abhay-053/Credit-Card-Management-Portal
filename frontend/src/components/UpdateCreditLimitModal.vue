<template>
  <transition name="fade">
    <div v-if="localVisible" class="backdrop-modal" @click.self="cancel">
      <div class="dialog-modal">
        <div class="dialog-header">
          <h2 class="dialog-title">Update Credit Limit</h2>
          <button class="close-button" @click="cancel">✕</button>
        </div>

        <div class="dialog-body">
          <div class="info-row">
            <div class="label-text">Current limit:</div>
            <div class="value-text">₹{{ formatINR(displayCurrentLimit) }}</div>
          </div>

          <div class="info-row">
            <div class="label-text">Outstanding balance:</div>
            <div class="value-text">₹{{ formatINR(currentOutstanding) }}</div>
          </div>

          <div class="input-field">
            <label class="label-text">New Credit Limit</label>
            <input
              type="number"
              v-model.number="proposedLimit"
              :min="currentOutstanding + props.minLimit"
              :max="props.maxLimit"
              class="input-control"
            />
            <div class="hint-text">
              Range: ₹{{ formatINR(currentOutstanding + props.minLimit) }} – ₹{{ formatINR(props.maxLimit) }}
            </div>

          </div>
        </div>

        <div class="dialog-footer">
          <button class="button button-ghost" @click="cancel">Cancel</button>
          <button class="button button-primary" :disabled="!isSubmittable" @click="updateLimit">
            Update Limit
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>


<script setup lang="ts">
import { ref, watch, computed } from 'vue'

const props = withDefaults(defineProps<{
  modelValue?: boolean
  currentLimit?: number
  outstanding?: number
  minLimit?: number
  maxLimit?: number
}>(), {
  modelValue: false,
  currentLimit: 0,
  outstanding: 0,
  minLimit: 0,
  maxLimit: 0,
})

const emit = defineEmits<{
  (event: 'update:modelValue', isVisible: boolean): void
  (event: 'next', payload: { newLimit: number }): void
}>()

const localVisible = ref<boolean>(props.modelValue ?? false)
const proposedLimit = ref<number>(props.currentLimit ?? 0)

watch(() => props.modelValue, (newVal) => {
  localVisible.value = !!newVal
  emit('update:modelValue', newVal)
})

const displayCurrentLimit = props.currentLimit ?? 0
const currentOutstanding = props.outstanding ?? 0

const isSubmittable = computed(() =>
  proposedLimit.value >= (currentOutstanding + (props.minLimit ?? 0)) &&
  proposedLimit.value <= (props.maxLimit ?? 0)
)

function cancel() {
  localVisible.value = false
  emit('update:modelValue', false) 
}

function updateLimit() {
  if (!isSubmittable.value) return
  emit('next', { newLimit: Number(proposedLimit.value) })
  localVisible.value = false
}

function formatINR(value?: number) {
  return Number(value || 0).toLocaleString('en-IN')
}
</script>




<style scoped>
.backdrop-modal {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex; align-items: center; justify-content: center;
  z-index: 2000;
  padding: 20px;
}
.dialog-modal {
  width: 500px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.15);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  border-bottom: 1px solid #f1f1f1;
}
.dialog-title {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: #0b2540;
}
.close-button {
  border: none;
  background: transparent;
  font-size: 18px;
  cursor: pointer;
  color: #6b7280;
}
.dialog-body {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.info-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}
.label-text { color: #6b7280; }
.value-text { font-weight: 700; }
.input-field { display: flex; flex-direction: column; gap: 6px; }
.input-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 15px;
}
.hint-text {
  font-size: 12px;
  color: #6b7280;
}
.info-note {
  background: #f9fafb;
  border-radius: 8px;
  padding: 10px;
  font-size: 13px;
  color: #374151;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 16px 20px;
  border-top: 1px solid #f1f1f1;
}
.button {
  padding: 10px 14px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
}
.button-ghost {
  background: #fff;
  border: 1px solid #d1d5db;
  color: #111827;
}
.button-primary {
  background: #0b2540;
  color: #fff;
  border: none;
}
.button-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.fade-enter-active, .fade-leave-active { transition: opacity .2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
