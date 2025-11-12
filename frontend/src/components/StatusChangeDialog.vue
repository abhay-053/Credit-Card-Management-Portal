<template>
  <transition name="fade">
    <div
      v-if="localVisible"
      class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50"
      @click.self="cancel"
    >
      <div
        class="bg-white rounded-xl w-full max-w-md p-6 shadow-xl transform transition-all width-350"
      >
        <!-- Header -->
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-semibold text-gray-900">Change Card Status</h2>
          <button
            class="text-gray-500 hover:text-gray-800 text-lg"
            @click="cancel"
          >
            ✕
          </button>
        </div>

        <!-- Body -->
        <div class="text-sm text-gray-700 mb-6 space-y-2">
          <p>
            Current status:
            <strong>{{ currentStatus }}</strong>
          </p>
          <p>
            Requested status:
            <strong>{{ requestedStatus }}</strong>
          </p>
          <p class="text-gray-500 text-sm mt-3">
            Are you sure you want to update this card’s status
            for card ending <strong>{{ cardLast4 }}</strong>?
          </p>
        </div>

        <!-- Footer -->
        <div class="flex justify-end gap-3">
          <button
            class="flex items-center gap-2 bg-gray-200 text-gray-800 px-5 py-2.5 rounded-xl font-medium hover:bg-gray-300 transition"
            @click="cancel"
          >
            Cancel
          </button>

          <button
            class="flex items-center gap-2 bg-[#ffd60a] text-black px-5 py-2.5 rounded-xl font-medium hover:bg-[#e5cd56] transition"
            @click="confirm"
          >
            Confirm
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "StatusChangeDialog",
  props: {
    modelValue: { type: Boolean, required: true },
    currentStatus: { type: String, required: true },
    requestedStatus: { type: String, required: true },
    cardLast4: { type: String, required: true },
  },
  emits: ["update:modelValue", "confirm"],
  data() {
    return {
      localVisible: this.modelValue,
    };
  },
  watch: {
    modelValue(newVal) {
      this.localVisible = newVal;
    },
    localVisible(newVal) {
      this.$emit("update:modelValue", newVal);
    },
  },
  methods: {
    cancel() {
      this.localVisible = false;
    },
    confirm() {
      this.$emit("confirm", { newStatus: this.requestedStatus });
      this.localVisible = false;
    },
  },
});
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.width-350{
  width: 350px;
}
</style>
