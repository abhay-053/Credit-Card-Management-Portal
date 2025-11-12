<template>
  <footer
    id="contact"
    class="py-16 bg-gradient-to-r from-gray-900 to-gray-800 text-white"
  >
    <div class="container mx-auto px-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8 mb-12">
        <!-- Brand Section -->
        <div class="md:col-span-2">
          <div class="flex items-center space-x-3 mb-6">
            <div
              class="w-12 h-12 rounded-xl flex items-center justify-center"
              style="background: #ffd60a;"
            >
              <svg
                class="w-7 h-7"
                style="color: #0b2540;"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"
                ></path>
              </svg>
            </div>
            <h3 class="text-2xl font-bold" style="color: #ffd60a;">
              SmartSwipe
            </h3>
          </div>
          <p class="text-gray-300 mb-6 max-w-md leading-relaxed">
            Revolutionizing credit card management with smart technology, real-time
            insights, and seamless user experience. Your financial future starts
            here.
          </p>
          <div class="flex space-x-4">
            <a
              href="#"
              class="w-10 h-10 rounded-lg flex items-center justify-center transition-all duration-300 hover:scale-110 hover:shadow-lg"
              style="background: #ffd60a; color: #0b2540;"
              aria-label="Facebook"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path
                  d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"
                ></path>
              </svg>
            </a>
            <a
              href="#"
              class="w-10 h-10 rounded-lg flex items-center justify-center transition-all duration-300 hover:scale-110 hover:shadow-lg"
              style="background: #ffd60a; color: #0b2540;"
              aria-label="Twitter"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path
                  d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"
                ></path>
              </svg>
            </a>
            <a
              href="#"
              class="w-10 h-10 rounded-lg flex items-center justify-center transition-all duration-300 hover:scale-110 hover:shadow-lg"
              style="background: #ffd60a; color: #0b2540;"
              aria-label="LinkedIn"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                <path
                  d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"
                ></path>
              </svg>
            </a>
          </div>
        </div>

        <!-- Quick Links -->
        <div>
          <h4 class="text-lg font-semibold mb-6" style="color: #ffd60a;">
            Quick Links
          </h4>
          <ul class="space-y-3">
            <li>
              <a
                href="#features"
                class="text-gray-300 hover:text-yellow-400 transition-colors duration-300"
                >Features</a
              >
            </li>
            <li>
              <a
                href="#advantages"
                class="text-gray-300 hover:text-yellow-400 transition-colors duration-300"
                >Benefits</a
              >
            </li>
            <li>
              <a
                href="#contact"
                class="text-gray-300 hover:text-yellow-400 transition-colors duration-300"
                >Contact</a
              >
            </li>
            <li>
              <router-link
                to="/login"
                class="text-gray-300 hover:text-yellow-400 transition-colors duration-300"
                >Sign In</router-link
              >
            </li>
          </ul>
        </div>

        <!-- Contact Info -->
        <div>
          <h4 class="text-lg font-semibold mb-6" style="color: #ffd60a;">
            Contact Info
          </h4>
          <div class="space-y-4">
            <div class="flex items-center space-x-3">
              <svg
                class="w-5 h-5"
                style="color: #ffd60a;"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                ></path>
              </svg>
              <span class="text-gray-300">support@smartswipe.com</span>
            </div>
            <div class="flex items-center space-x-3">
              <svg
                class="w-5 h-5"
                style="color: #ffd60a;"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"
                ></path>
              </svg>
              <span class="text-gray-300">+1 (555) 123-4567</span>
            </div>
            <div class="flex items-center space-x-3">
              <svg
                class="w-5 h-5"
                style="color: #ffd60a;"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"
                ></path>
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"
                ></path>
              </svg>
              <span class="text-gray-300">New York, NY 10001</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Bottom Bar -->
      <div
        class="pt-8 border-t border-gray-700 flex flex-col md:flex-row justify-between items-center"
      >
        <p class="text-gray-400 text-sm mb-4 md:mb-0">
          © 2024 SmartSwipe. All rights reserved. Built with ❤️ for smart
          banking.
        </p>
        <div class="flex space-x-6">
          <a
            href="#"
            class="text-gray-400 hover:text-yellow-400 transition-colors duration-300 text-sm"
            >Privacy Policy</a
          >
          <a
            href="#"
            class="text-gray-400 hover:text-yellow-400 transition-colors duration-300 text-sm"
            >Terms of Service</a
          >
          <a
            href="#"
            class="text-gray-400 hover:text-yellow-400 transition-colors duration-300 text-sm"
            >Cookie Policy</a
          >
        </div>
      </div>
    </div>

    <!-- Back to Top Button -->
    <button
      @click="scrollToTop"
      class="fixed bottom-8 right-8 w-12 h-12 rounded-full flex items-center justify-center transition-all duration-300 hover:scale-110 hover:shadow-lg focus:outline-none focus:ring-4 focus:ring-yellow-400/50 z-50"
      style="background: #ffd60a; color: #0b2540;"
      aria-label="Back to top"
    >
      <svg
        class="w-6 h-6"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M5 10l7-7m0 0l7 7m-7-7v18"
        ></path>
      </svg>
    </button>
  </footer>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "Footer",
  setup() {
    const scrollToTop = () => {
      window.scrollTo({ top: 0, behavior: "smooth" });
    };

    return {
      scrollToTop,
    };
  },
});
</script>

<style scoped>
/* Smooth scroll behavior */
html {
  scroll-behavior: smooth;
}

/* Back to top button animation */
button:hover {
  transform: translateY(-2px) scale(1.1);
}

/* Social media hover effects */
a:hover {
  transform: translateY(-2px);
}
</style>
