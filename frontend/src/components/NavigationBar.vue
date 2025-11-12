<template>
  <header class="topbar">
    <div class="topbar-inner">
      <div class="brand">
        <div class="brand-logo" aria-hidden="true">
          <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="5" width="20" height="14" rx="2" ry="2" />
            <line x1="2" y1="10" x2="22" y2="10" />
            <line x1="6" y1="15" x2="6.01" y2="15" />
            <line x1="10" y1="15" x2="14" y2="15" />
          </svg>
        </div>
        <div class="brand-title">SwipeSmart</div>
      </div>

      <nav class="topnav desktop-nav" role="navigation" aria-label="Primary" v-for="link in navLinks" :key="link.label">
        <RouterLink class="nav-item" v-if="link.to" :to="link.to" active-class="nav-active"
          exact-active-class="nav-exact-active" >{{ link.label }}</RouterLink>
        <a class="nav-item" v-else-if="link.action === 'logout'" @click.prevent="handleLogout">{{ link.label }}</a>
      </nav>
      <button
        class="hamburger"
        @click="toggleMobileNav"
        :aria-expanded="isMobileNavOpen.toString()"
        aria-controls="mobile-navigation"
        aria-label="Toggle navigation"
        type="button"
      >
        <span class="hamburger-bar" :class="{ open: isMobileNavOpen }"></span>
        <span class="hamburger-bar" :class="{ open: isMobileNavOpen }"></span>
        <span class="hamburger-bar" :class="{ open: isMobileNavOpen }"></span>
      </button>

      <div class="avatar desktop-avatar">
        <RouterLink>{{ userInitials }}</RouterLink>
      </div>
    </div>

    <div
      id="mobile-navigation"
      v-if="isMobileNavOpen"
      class="mobile-nav"
      @click.self="closeMobileNav"
      role="dialog"
      aria-modal="true"
      aria-label="Mobile navigation overlay"
    >
      <aside class="mobile-panel" role="menu" aria-label="Mobile Primary">
        <div class="mobile-panel-top">
          <div class="brand-mini">
            <div class="brand-logo-mini" aria-hidden="true">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="2" y="5" width="20" height="14" rx="2" ry="2" />
                <line x1="2" y1="10" x2="22" y2="10" />
              </svg>
            </div>
            <div class="brand-title-mini">SwipeSmart</div>
          </div>

          <button class="close-button" @click="closeMobileNav" aria-label="Close menu" type="button">✕</button>
        </div>

        <nav class="mobile-links">
    <ul>
      <li v-for="link in navLinks" :key="link.label">
        <RouterLink class="mobile-link" v-if="link.to" :to="link.to">{{ link.label }}</RouterLink>
        <a class="mobile-link" v-else-if="link.action === 'logout'" @click.prevent="handleLogout">{{ link.label }}</a>
      </li>
    </ul>
  </nav>


        <footer class="mobile-footer">
          <div class="avatar mobile-avatar">{{ userInitials }}</div>
          <div class="mobile-user">
            <div class="mobile-username">Hello, {{ userName }}</div>
            <RouterLink to="/profile" class="mobile-profile" @click="closeMobileNav">View profile</RouterLink>
          </div>
        </footer>
      </aside>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";
import { useRoute } from "vue-router";
import { useStore } from 'vuex';
import router from "../router";
import { logout } from "../services/authService";
import { toast } from "vue3-toastify"; 


const store = useStore();
const route = useRoute();
const isMobileNavOpen = ref(false);

const isAdmin = computed(() => store.getters["auth/isAdmin"]);

const navLinks = computed(() => {
  if (isAdmin.value) {
    return [
      { to: "/admin-dashboard", label: "Dashboard" },
      { to: "/admin-review", label: "Applications" },
      { action: "logout", label: "Logout" }
    ];
  } else {
    return [
      { to: "/dashboard", label: "Dashboard" },
      { to: "/cards", label: "My Cards" },
      { to: "/apply-card", label: "Apply Card" },
      { to: "/transactions", label: "Transactions" },
      { to: "/profile", label: "Profile" },
      { action: "logout", label: "Logout" }
    ];
  }
});

const toggleMobileNav = () => {
  isMobileNavOpen.value = !isMobileNavOpen.value;
  setBodyScrollLocked(isMobileNavOpen.value);
};

const handleLogout = async () => {
  try {
    await logout();
    toast.success("Logged out successfully! 🎉");
    router.push('/');
    window.location.reload();
  } catch (err) {
    console.error('Logout failed:', err);
    alert('logout failed');
    toast.error("Error logging out. 🎉");

 
  }
};

const closeMobileNav = () => {
  isMobileNavOpen.value = false;
  setBodyScrollLocked(false);
};

const setBodyScrollLocked = (shouldLock) => {
  document.documentElement.style.overflow = shouldLock ? "hidden" : "";
  document.body.style.overflow = shouldLock ? "hidden" : "";
};

const handleKeydown = (keyboardEvent) => {
  if (keyboardEvent.key === "Escape" && isMobileNavOpen.value) {
    closeMobileNav();
  }
};

onMounted(() => {
  window.addEventListener("keydown", handleKeydown);

  watch(
    () => route.fullPath,
    () => {
      closeMobileNav();
    }
  );
});

onBeforeUnmount(() => {
  window.removeEventListener("keydown", handleKeydown);
  setBodyScrollLocked(false);
});
</script>

<style scoped>
.topbar {
  background: #fff;
  border-bottom: 1px solid #ececec;
  position: relative;
  z-index: 60;
}
.topbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.brand {
  display: flex;
  gap: 12px;
  align-items: center;
}
.brand-logo {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  background: var(--accent, #0b2540);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 18px rgba(11, 37, 64, 0.12);
}
.brand-logo svg {
  width: 22px;
  height: 22px;
}
.brand-title {
  font-weight: 700;
  color: var(--accent, #0b2540);
  font-size: 18px;
}
.topnav {
  display: flex;
  gap: 20px;
  align-items: center;
  color: var(--muted, #6b7280);
}
.topnav .nav-item {
  text-decoration: none;
  color: inherit;
  padding: 6px 10px;
  border-radius: 8px;
  font-weight: 600;
  transition: background 0.15s, color 0.15s, transform 0.06s;
}
.topnav .nav-item:hover {
  background: #e0c126;
  color: #0b2540;
}
.topnav .nav-item.nav-active {
  background: #ffd60a;
  color: #0b2540;
  box-shadow: 0 4px 12px rgba(255, 214, 10, 0.25);
}
.hamburger {
  display: none;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 5px;
  background: transparent;
  border: none;
  order: 2; 
  margin-left: auto;
  padding: 8px;
  width: 44px;
  height: 44px;
  cursor: pointer;
  z-index: 200;
  -webkit-tap-highlight-color: transparent;
}
.hamburger-bar {
  display: block;
  width: 22px;
  height: 2px;
  border-radius: 2px;
  background: var(--muted, #6b7280);
  transition: transform 0.18s ease, opacity 0.18s ease;
  transform-origin: center;
}
.hamburger-bar.open:nth-child(1) {
  transform: translateY(6px) rotate(45deg);
}
.hamburger-bar.open:nth-child(2) {
  opacity: 0;
  transform: scaleX(0.1);
}
.hamburger-bar.open:nth-child(3) {
  transform: translateY(-6px) rotate(-45deg);
}
.hamburger:focus {
  outline: 2px solid rgba(31,111,235,0.16);
  outline-offset: 3px;
}
.mobile-nav {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: stretch;
  justify-content: flex-start;
  background: rgba(0, 0, 0, 0.38);
}
.mobile-panel {
  width: 300px;
  max-width: 86%;
  height: 100%;
  background: #fff;
  padding: 18px;
  box-shadow: 6px 0 28px rgba(10, 20, 30, 0.18);
  display: flex;
  flex-direction: column;
  gap: 12px;
  transform: translateX(0);
  animation: slideIn 220ms ease forwards;
  pointer-events: auto;
}
.brand-mini {
  display: flex;
  align-items: center;
  gap: 10px;
}
.brand-logo-mini {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  background: var(--accent, #0b2540);
  display: flex;
  align-items: center;
  justify-content: center;
}
.brand-title-mini {
  font-weight: 800;
  color: var(--accent, #0b2540);
}
.mobile-panel-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.close-button {
  background: transparent;
  border: none;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
}
.mobile-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 6px;
}
.mobile-link {
  display: block;
  padding: 12px 10px;
  border-radius: 8px;
  text-decoration: none;
  color: #222;
  font-weight: 700;
}
.mobile-link.active {
  background: #ffd60a;
  color: #0b2540;
  box-shadow: 0 6px 18px rgba(255, 214, 10, 0.12);
}
.mobile-footer {
  margin-top: auto;
  display: flex;
  gap: 12px;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #eee;
}
.mobile-avatar {
  width: 46px;
  height: 46px;
  border-radius: 50%;
  background: var(--accent, #0b2540);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}
.mobile-user .mobile-username {
  font-weight: 700;
}
.mobile-profile {
  display: inline-block;
  margin-top: 4px;
  font-size: 13px;
  color: var(--muted, #6b7280);
  text-decoration: none;
}
@keyframes slideIn {
  from {
    transform: translateX(-12px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
@media (max-width: 768px) {
  .desktop-nav {
    display: none;
  }
  .hamburger {
    display: inline-flex;
  }
}
html,
body {
  max-width: 100%;
  overflow-x: hidden;
}
</style>