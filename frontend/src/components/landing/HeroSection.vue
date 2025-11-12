<template>
  <section
    class="relative overflow-hidden py-20 md:py-32 bg-gradient-to-br from-gray-50 via-white to-gray-100"
  >
    <!-- Background Pattern -->
    <div class="absolute inset-0 opacity-5">
      <div
        class="absolute top-20 left-10 w-72 h-72 rounded-full"
        style="background: #ffd60a;"
      ></div>
      <div
        class="absolute bottom-20 right-10 w-96 h-96 rounded-full"
        style="background: #0b2540;"
      ></div>
    </div>

    <div class="container mx-auto px-4 text-center relative z-10">
      <div class="max-w-4xl mx-auto">
        <h2
          class="text-4xl md:text-6xl lg:text-7xl font-bold mb-6 leading-tight"
          style="color: #0b2540;"
        >
          <span class="animate-fade-in-up block">Smart Credit Card</span>
          <span
            class="block animate-fade-in-up"
            style="animation-delay: 0.2s; color: #ffd60a;"
            >{{ currentTagline }}</span
          >
        </h2>
        <p
          class="text-lg md:text-xl mb-10 max-w-2xl mx-auto text-gray-600 leading-relaxed animate-fade-in-up"
          style="animation-delay: 0.4s;"
        >
          The ultimate platform for modern credit card management. Track
          transactions, manage BNPL payments, analyze spending patterns, and take
          control of your financial future with SmartSwipe.
        </p>
        <div
          class="flex flex-col sm:flex-row gap-4 justify-center items-center animate-fade-in-up"
          style="animation-delay: 0.6s;"
        >
          <router-link to="/login">
            <button
              class="px-8 py-4 rounded-xl text-lg font-semibold transition-all duration-300 hover:shadow-xl hover:scale-105 focus:outline-none focus:ring-4 focus:ring-yellow-400/50 animate-pulse"
              style="background: #ffd60a; color: #0b2540;"
            >
              Start Smart Banking
              <svg
                class="w-5 h-5 ml-2 inline"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M13 7l5 5m0 0l-5 5m5-5H6"
                ></path>
              </svg>
            </button>
          </router-link>
          <button
            class="px-8 py-4 rounded-xl text-lg font-semibold border-2 transition-all duration-300 hover:shadow-lg hover:scale-105 focus:outline-none focus:ring-4 focus:ring-gray-400/50"
            style="border-color: #0b2540; color: #0b2540;"
          >
            Watch Demo
            <svg
              class="w-5 h-5 ml-2 inline"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M14.828 14.828a4 4 0 01-5.656 0M9 10h1m4 0h1m-6 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              ></path>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted } from "vue";

export default defineComponent({
  name: "HeroSection",
  setup() {
    const taglines = ["Finances", "Spending", "Budget", "Future", "Wealth"];
    const currentTagline = ref(taglines[0]);
    let index = 0;
    let intervalId: ReturnType<typeof setInterval> | null = null;

    onMounted(() => {
      intervalId = setInterval(() => {
        index = (index + 1) % taglines.length;
        currentTagline.value = taglines[index];
      }, 2500);
    });

    onUnmounted(() => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    });

    return {
      currentTagline,
    };
  },
});
</script>

<style scoped>
/* Custom animations */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.animate-fade-in-up {
  animation: fadeInUp 0.8s ease-out;
}

.animate-pulse {
  animation: pulse 2s ease-in-out infinite;
}

/* Button animations */
button:hover {
  transform: translateY(-2px);
}
</style>
