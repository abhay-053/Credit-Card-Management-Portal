<template>
  <div class="min-h-screen bg-white">
    <!-- Header with Mobile Menu -->
    <Header />

    <!-- Hero Section -->
    <HeroSection />

    <!-- Features Section -->
    <FeaturesSection />

    <!-- Benefits Section -->
    <BenefitsSection />

    <!-- Footer -->
    <Footer />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Header from "@/components/landing/Header.vue";
import HeroSection from "@/components/landing/HeroSection.vue";
import FeaturesSection from "@/components/landing/FeaturesSection.vue";
import BenefitsSection from "@/components/landing/BenefitsSection.vue";
import Footer from "@/components/landing/Footer.vue";

export default defineComponent({
  name: "LandingPage",
  components: {
    Header,
    HeroSection,
    FeaturesSection,
    BenefitsSection,
    Footer,
  },
});
</script>

<style scoped>
/* Global styles for the landing page */
html {
  scroll-behavior: smooth;
}

/* Ensure proper spacing and layout */
.min-h-screen {
  min-height: 100vh;
}
</style>