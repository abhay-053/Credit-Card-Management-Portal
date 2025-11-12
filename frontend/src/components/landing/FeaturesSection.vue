<template>
  <section id="features" class="py-20 md:py-24 bg-white">
    <div class="container mx-auto px-4">
      <div class="text-center mb-16">
        <h2
          class="text-3xl md:text-4xl font-bold mb-4 animate-fade-in-up"
          style="color: #0b2540;"
        >
          Smart Features for Modern Banking
        </h2>
        <p
          class="text-lg text-gray-600 max-w-2xl mx-auto animate-fade-in-up"
          style="animation-delay: 0.2s;"
        >
          Everything you need to manage your credit cards effectively and make
          smarter financial decisions
        </p>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        <div
          v-for="(feature, index) in features"
          :key="feature.id"
          class="group bg-white p-8 rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-500 hover:-translate-y-3 border border-gray-100 animate-fade-in-up"
          :style="`animation-delay: ${0.1 * (index + 1)}s`"
        >
          <div
            class="w-16 h-16 mb-6 mx-auto rounded-xl flex items-center justify-center group-hover:scale-110 group-hover:rotate-6 transition-all duration-300 animate-float"
            style="background: #ffd60a; animation-delay: 0.5s;"
          >
            <img :src="feature.icon" :alt="feature.title" class="w-10 h-10" />
          </div>
          <h3
            class="text-xl font-semibold mb-4 text-center group-hover:text-yellow-600 transition-colors duration-300"
            style="color: #0b2540;"
          >
            {{ feature.title }}
          </h3>
          <p
            class="text-gray-600 text-center leading-relaxed group-hover:text-gray-800 transition-colors duration-300"
          >
            {{ feature.description }}
          </p>
          <div class="mt-4 text-center">
            <span
              class="inline-block px-3 py-1 rounded-full text-xs font-semibold transition-all duration-300 group-hover:scale-105"
              style="background: #ffd60a; color: #0b2540;"
            >
              Learn More →
            </span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";

export default defineComponent({
  name: "FeaturesSection",
  setup() {
    const features = ref([
      {
        id: 1,
        title: "Real-time Tracking",
        description:
          "Get instant notifications for every transaction and stay on top of your spending with real-time updates and smart alerts.",
        icon: "https://img.icons8.com/emoji/96/000000/credit-card-emoji.png",
      },
      {
        id: 2,
        title: "Smart Analytics",
        description:
          "Visual charts and AI-powered insights to track your expenses, understand spending patterns, and make informed financial decisions.",
        icon: "https://img.icons8.com/emoji/96/000000/bar-chart-emoji.png",
      },
      {
        id: 3,
        title: "BNPL Management",
        description:
          "Split your purchases into easy installments with our Buy Now, Pay Later feature—no extra stress, just smart spending with flexible payment options.",
        icon: "https://img.icons8.com/?size=100&id=bpb1NWfrmmHv&format=png&color=000000",
      },
    ]);

    return {
      features,
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

@keyframes float {
  0%,
  100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.animate-fade-in-up {
  animation: fadeInUp 0.8s ease-out;
}

.animate-float {
  animation: float 3s ease-in-out infinite;
}

/* Card hover effects */
.group:hover {
  transform: translateY(-8px) scale(1.02);
}

.group:hover .group-hover\:text-yellow-600 {
  color: #ffd60a;
}

.group:hover .group-hover\:text-gray-800 {
  color: #1f2937;
}

.group:hover .group-hover\:scale-110 {
  transform: scale(1.1);
}

.group:hover .group-hover\:rotate-6 {
  transform: rotate(6deg);
}
</style>
