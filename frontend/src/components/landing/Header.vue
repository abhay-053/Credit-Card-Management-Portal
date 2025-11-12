<template>
  <header
    class="bg-white/95 backdrop-blur-sm text-gray-900 py-4 sticky top-0 z-50 shadow-lg border-b border-gray-100 mobile-menu-container relative"
  >
    <div class="container mx-auto px-4 flex justify-between items-center">
      <div class="flex items-center space-x-2">
        <div
          class="w-10 h-10 rounded-lg flex items-center justify-center"
          style="background: #ffd60a;"
        >
          <svg
            class="w-6 h-6"
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
        <h1 class="text-2xl font-bold" style="color: #0b2540;">SmartSwipe</h1>
      </div>
      <nav
        class="hidden md:flex space-x-8"
        role="navigation"
        aria-label="Main navigation"
      >
        <a
          href="#features"
          class="nav-link group relative font-medium focus:outline-none focus:ring-2 focus:ring-yellow-400 rounded-lg px-4 py-2 transition-all duration-300 hover:scale-105"
          aria-label="Features section"
        >
          <span class="relative z-10">Features</span>
          <div class="nav-link-bg absolute inset-0 rounded-lg opacity-0 group-hover:opacity-100 transition-all duration-300"></div>
        </a>
        <a
          href="#advantages"
          class="nav-link group relative font-medium focus:outline-none focus:ring-2 focus:ring-yellow-400 rounded-lg px-4 py-2 transition-all duration-300 hover:scale-105"
          aria-label="Advantages section"
        >
          <span class="relative z-10">Benefits</span>
          <div class="nav-link-bg absolute inset-0 rounded-lg opacity-0 group-hover:opacity-100 transition-all duration-300"></div>
        </a>
        <a
          href="#contact"
          class="nav-link group relative font-medium focus:outline-none focus:ring-2 focus:ring-yellow-400 rounded-lg px-4 py-2 transition-all duration-300 hover:scale-105"
          aria-label="Contact section"
        >
          <span class="relative z-10">Contact</span>
          <div class="nav-link-bg absolute inset-0 rounded-lg opacity-0 group-hover:opacity-100 transition-all duration-300"></div>
        </a>
        <router-link
          to="/login"
          class="px-6 py-2 rounded-lg font-semibold transition-all duration-300 hover:shadow-lg"
          style="background: #ffd60a; color: #0b2540;"
        >
          Sign In
        </router-link>
      </nav>
      <!-- Mobile menu button -->
      <button
        @click="toggleMobileMenu"
        class="md:hidden p-3 rounded-lg hover:bg-gray-100 active:bg-gray-200 transition-colors mobile-menu-btn touch-manipulation"
        style="min-width: 44px; min-height: 44px;"
        aria-label="Toggle mobile menu"
      >
        <span v-if="!isMobileMenuOpen" class="text-xl">☰</span>
        <span v-else class="text-xl">✕</span>
      </button>
    </div>

    <!-- Mobile Menu Dropdown -->
    <div v-if="isMobileMenuOpen" class="md:hidden mobile-menu-overlay">
      <!-- Backdrop -->
      <div class="mobile-menu-backdrop" @click="closeMobileMenu"></div>

      <!-- Menu Panel -->
      <div class="mobile-menu-panel">
        <!-- Menu Header -->
        <div class="mobile-menu-header">
          <div class="flex items-center space-x-3">
            <div
              class="w-8 h-8 rounded-lg flex items-center justify-center"
              style="background: #ffd60a;"
            >
              <svg
                class="w-5 h-5"
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
            <h3 class="text-lg font-bold" style="color: #0b2540;">
              SmartSwipe
            </h3>
          </div>
          <button
            @click="closeMobileMenu"
            class="mobile-menu-close-btn"
            aria-label="Close mobile menu"
          >
            <span class="text-lg">✕</span>
          </button>
        </div>

        <!-- Menu Items -->
        <div class="mobile-menu-content">
          <a
            href="#features"
            @click="closeMobileMenu"
            class="mobile-menu-item"
          >
            <span class="text-lg">📊</span>
            <span>Features</span>
            <span class="text-sm ml-auto">→</span>
          </a>

          <a
            href="#advantages"
            @click="closeMobileMenu"
            class="mobile-menu-item"
          >
            <span class="text-lg">💰</span>
            <span>Benefits</span>
            <span class="text-sm ml-auto">→</span>
          </a>

          <a
            href="#contact"
            @click="closeMobileMenu"
            class="mobile-menu-item"
          >
            <span class="text-lg">📧</span>
            <span>Contact</span>
            <span class="text-sm ml-auto">→</span>
          </a>
        </div>

        <!-- Menu Footer -->
        <div class="mobile-menu-footer">
          <router-link
            to="/login"
            @click="closeMobileMenu"
            class="mobile-menu-cta"
          >
            <span class="text-lg">👤</span>
            <span>Sign In</span>
          </router-link>
        </div>
      </div>
    </div>
  </header>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onUnmounted } from "vue";

export default defineComponent({
  name: "Header",
  setup() {
    const isMobileMenuOpen = ref(false);

    const toggleMobileMenu = () => {
      console.log('Before toggle:', isMobileMenuOpen.value);
      isMobileMenuOpen.value = !isMobileMenuOpen.value;
      console.log('After toggle:', isMobileMenuOpen.value);
    };

    const closeMobileMenu = () => {
      isMobileMenuOpen.value = false;
    };

    // Close mobile menu when switching to desktop
    const handleResize = () => {
      if (window.innerWidth >= 768 && isMobileMenuOpen.value) {
        isMobileMenuOpen.value = false;
      }
    };

    onMounted(() => {
      window.addEventListener('resize', handleResize);
    });

    onUnmounted(() => {
      window.removeEventListener('resize', handleResize);
    });

    return {
      isMobileMenuOpen,
      toggleMobileMenu,
      closeMobileMenu,
    };
  },
});
</script>

<style scoped>
/* Mobile menu button animation */
.mobile-menu-btn {
  transition: transform 0.2s ease-in-out;
  -webkit-tap-highlight-color: transparent;
  user-select: none;
}

.mobile-menu-btn:hover {
  transform: scale(1.1);
}

.mobile-menu-btn:active {
  transform: scale(0.95);
}

/* Mobile touch improvements */
.touch-manipulation {
  touch-action: manipulation;
  -webkit-tap-highlight-color: transparent;
  user-select: none;
}

/* Mobile Menu Overlay */
.mobile-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  display: flex;
  align-items: flex-end;
}

.mobile-menu-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
}

.mobile-menu-panel {
  position: relative;
  width: 100%;
  max-height: 80vh;
  background: white;
  border-radius: 20px 20px 0 0;
  box-shadow: 0 -10px 25px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.mobile-menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f3f4f6;
  background: #fafafa;
}

.mobile-menu-close-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  color: #6b7280;
}

.mobile-menu-close-btn:hover {
  background: #e5e7eb;
  transform: scale(1.05);
}

.mobile-menu-content {
  padding: 16px 0;
  max-height: 50vh;
  overflow-y: auto;
}

.mobile-menu-item {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  color: #1f2937;
  text-decoration: none;
  transition: all 0.2s ease;
  border-left: 4px solid transparent;
}

.mobile-menu-item:hover {
  background: #f9fafb;
  border-left-color: #ffd60a;
  color: #0b2540;
}

.mobile-menu-item svg:first-child {
  margin-right: 16px;
  color: #6b7280;
}

.mobile-menu-item:hover svg:first-child {
  color: #ffd60a;
}

.mobile-menu-footer {
  padding: 20px 24px;
  border-top: 1px solid #f3f4f6;
  background: #fafafa;
}

.mobile-menu-cta {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 16px 24px;
  background: #ffd60a;
  color: #0b2540;
  border-radius: 12px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(255, 214, 10, 0.3);
}

.mobile-menu-cta:hover {
  background: #f4c430;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 214, 10, 0.4);
}

.mobile-menu-cta svg {
  margin-right: 8px;
}

/* Mobile menu links */
.mobile-menu-item,
.mobile-menu-cta {
  -webkit-tap-highlight-color: transparent;
  user-select: none;
  cursor: pointer;
}

.mobile-menu-item:active,
.mobile-menu-cta:active {
  background-color: #f3f4f6;
}

/* Navigation link hover effects */
.nav-link {
  color: #0b2540;
  position: relative;
  overflow: hidden;
}

.nav-link span {
  position: relative;
  z-index: 10;
}

.nav-link:hover {
  color: #ffd60a;
  transform: translateY(-2px);
}

.nav-link-bg {
  background: linear-gradient(135deg, rgba(255, 214, 10, 0.1), rgba(244, 196, 48, 0.1));
  transform: scale(0.8);
  transition: all 0.3s ease;
  z-index: 1;
}

.nav-link:hover .nav-link-bg {
  transform: scale(1);
}

.nav-link::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: #ffd60a;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.nav-link:hover::before {
  width: 80%;
}

/* Sign In button hover effects */
.router-link-active {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 214, 10, 0.4);
}

/* Smooth transitions for all interactive elements */
a, button {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

a:hover, button:hover {
  transform: translateY(-1px);
}
</style>
