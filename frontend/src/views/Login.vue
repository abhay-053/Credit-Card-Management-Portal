<template>
  <div class="min-h-screen flex items-center justify-center px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-yellow-50 via-blue-50 to-blue-100 relative overflow-hidden">
    <!-- Background Pattern -->
    <div class="absolute inset-0 opacity-40" style="background-image: url('data:image/svg+xml,%3Csvg width=%2260%22 height=%2260%22 viewBox=%220 0 60 60%22 xmlns=%22http://www.w3.org/2000/svg%22%3E%3Cg fill=%22none%22 fill-rule=%22evenodd%22%3E%3Cg fill=%22%23ffd60a%22 fill-opacity=%220.08%22%3E%3Ccircle cx=%2230%22 cy=%2230%22 r=%224%22/%3E%3C/g%3E%3C/g%3E%3C/svg%3E');"></div>
    
    <!-- Floating Elements -->
    <div class="absolute top-20 left-10 w-20 h-20 bg-yellow-200 rounded-full opacity-20 animate-pulse"></div>
    <div class="absolute top-40 right-16 w-16 h-16 bg-blue-200 rounded-full opacity-30 animate-bounce" style="animation-delay: 1s;"></div>
    <div class="absolute bottom-32 left-20 w-12 h-12 bg-yellow-300 rounded-full opacity-25 animate-pulse" style="animation-delay: 2s;"></div>
    
    <div class="login-container relative w-full max-w-sm sm:max-w-md lg:max-w-md z-10" style="max-width: 420px;">
      <!-- Header -->
      <div class="text-center mb-8 animate-fade-in">
        <div class="mx-auto h-20 w-20 bg-gradient-to-r from-blue-900 to-blue-700 rounded-full flex items-center justify-center mb-6 shadow-xl transform hover:scale-105 transition-all duration-300">
          <svg class="h-10 w-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
          </svg>
        </div>
        <h1 class="text-4xl font-bold bg-gradient-to-r from-blue-900 to-blue-700 bg-clip-text text-transparent mb-3">
          SmartSwipe
        </h1>
        <p class="text-gray-600 text-lg">
          {{ !isRegister ? "Welcome back! Sign in to your account" : "Create your account and get started" }}
        </p>
        <div class="mt-4 h-1 w-20 bg-gradient-to-r from-yellow-400 to-yellow-500 rounded-full mx-auto"></div>
      </div>

      <!-- Main Card -->
      <div class="bg-white rounded-2xl shadow-2xl border border-gray-100 overflow-hidden transform hover:scale-[1.02] transition-all duration-300 backdrop-blur-sm bg-white/95">
        <!-- Tab Navigation -->
        <div class="flex bg-gray-50">
          <button @click="isRegister = false"
                  :class="[
                    'flex-1 py-4 px-6 text-sm font-medium transition-all duration-200',
                    !isRegister 
                      ? 'bg-white text-blue-900 border-b-2 border-yellow-400 shadow-sm' 
                      : 'text-gray-500 hover:text-blue-900 hover:bg-yellow-50'
                  ]">
            <div class="flex items-center justify-center space-x-2">
              <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"></path>
              </svg>
              <span>Sign In</span>
            </div>
          </button>
          <button @click="isRegister = true"
                  :class="[
                    'flex-1 py-4 px-6 text-sm font-medium transition-all duration-200',
                    isRegister 
                      ? 'bg-white text-blue-900 border-b-2 border-yellow-400 shadow-sm' 
                      : 'text-gray-500 hover:text-blue-900 hover:bg-yellow-50'
                  ]">
            <div class="flex items-center justify-center space-x-2">
              <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
              </svg>
              <span>Sign Up</span>
            </div>
          </button>
        </div>

        <!-- Form Content -->
        <div class="p-6 sm:p-8">
          <!-- Login Form -->
          <form v-if="!isRegister" @submit.prevent="handleLogin" class="space-y-6">
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Email Address</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207"></path>
                    </svg>
                  </div>
                  <input v-model="loginForm.email" 
                         type="email" 
                         placeholder="Enter your email"
                         class="block w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-300 hover:border-yellow-300 focus:shadow-lg focus:scale-[1.02]"
                         :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.email }" />
                </div>
                <p v-if="errors.email" class="mt-2 text-sm text-red-600">{{ errors.email }}</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Password</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                    </svg>
                  </div>
                  <input v-model="loginForm.password" 
                         type="password" 
                         placeholder="Enter your password"
                         class="block w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-300 hover:border-yellow-300 focus:shadow-lg focus:scale-[1.02]"
                         :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.password }" />
                </div>
                <p v-if="errors.password" class="mt-2 text-sm text-red-600">{{ errors.password }}</p>
              </div>
            </div>

            <button type="submit"
                    :disabled="isLoading"
                    class="w-full flex justify-center py-4 px-4 border border-transparent rounded-lg shadow-lg text-sm font-medium text-white bg-gradient-to-r from-blue-900 to-blue-700 hover:from-blue-800 hover:to-blue-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-400 transition-all duration-300 transform hover:scale-[1.02] hover:shadow-xl active:scale-[0.98] relative overflow-hidden group disabled:opacity-60 disabled:cursor-not-allowed disabled:transform-none !box-border">
              <span class="absolute inset-0 bg-gradient-to-r from-white/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 !box-border"></span>
              <span class="relative flex items-center">
                <svg v-if="isLoading" class="animate-spin h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <svg v-else class="h-5 w-5 mr-2 transition-transform duration-300 group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"></path>
                </svg>
                {{ isLoading ? 'Signing In...' : 'Sign In' }}
              </span>
            </button>
          </form>

          <!-- Register Form -->
          <form v-else @submit.prevent="handleRegister" class="space-y-6">
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Full Name</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                    </svg>
                  </div>
                  <input v-model="registerForm.name" 
                         type="text" 
                         placeholder="Your full name"
                         class="block w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-300 hover:border-yellow-300 focus:shadow-lg focus:scale-[1.02]"
                         :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.name }" />
                </div>
                <p v-if="errors.name" class="mt-2 text-sm text-red-600">{{ errors.name }}</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Email Address</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207"></path>
                    </svg>
                  </div>
                  <input v-model="registerForm.email" 
                         type="email" 
                         placeholder="Enter your email"
                         class="block w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-300 hover:border-yellow-300 focus:shadow-lg focus:scale-[1.02]"
                         :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.email }" />
                </div>
                <p v-if="errors.email" class="mt-2 text-sm text-red-600">{{ errors.email }}</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Password</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                    </svg>
                  </div>
                  <input v-model="registerForm.password" 
                         type="password" 
                         placeholder="Create a strong password"
                         class="block w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-300 hover:border-yellow-300 focus:shadow-lg focus:scale-[1.02]"
                         :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.password }" />
                </div>
                <p v-if="errors.password" class="mt-2 text-sm text-red-600">{{ errors.password }}</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Phone Number</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
                    </svg>
                  </div>
                  <input v-model="registerForm.phoneNumber" 
                         type="tel" 
                         placeholder="10-digit phone number"
                         class="block w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-300 hover:border-yellow-300 focus:shadow-lg focus:scale-[1.02]"
                         :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.phoneNumber }" />
                </div>
                <p v-if="errors.phoneNumber" class="mt-2 text-sm text-red-600">{{ errors.phoneNumber }}</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Address</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
                    </svg>
                  </div>
                  <input v-model="registerForm.address" 
                         type="text" 
                         placeholder="Your complete address"
                         class="block w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-300 hover:border-yellow-300 focus:shadow-lg focus:scale-[1.02]"
                         :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.address }" />
                </div>
                <p v-if="errors.address" class="mt-2 text-sm text-red-600">{{ errors.address }}</p>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Annual Income (₹)</label>
                <div class="relative">
                  <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <svg class="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1"></path>
                    </svg>
                  </div>
                  <input v-model="registerForm.annualIncome" 
                         type="number" 
                         placeholder="Enter your annual income"
                         class="block w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-400 focus:border-yellow-400 transition-all duration-300 hover:border-yellow-300 focus:shadow-lg focus:scale-[1.02]"
                         :class="{ 'border-red-300 focus:ring-red-500 focus:border-red-500': errors.annualIncome }" />
                </div>
                <p v-if="errors.annualIncome" class="mt-2 text-sm text-red-600">{{ errors.annualIncome }}</p>
              </div>
            </div>


            <button type="submit"
                    :disabled="isLoading"
                    class="w-full flex justify-center py-4 px-4 border border-transparent rounded-lg shadow-lg text-sm font-medium text-white bg-gradient-to-r from-yellow-500 to-yellow-600 hover:from-yellow-600 hover:to-yellow-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-yellow-400 transition-all duration-300 transform hover:scale-[1.02] hover:shadow-xl active:scale-[0.98] relative overflow-hidden group disabled:opacity-60 disabled:cursor-not-allowed disabled:transform-none !box-border">
              <span class="absolute inset-0 bg-gradient-to-r from-white/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 !box-border"></span>
              <span class="relative flex items-center">
                <svg v-if="isLoading" class="animate-spin h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                <svg v-else class="h-5 w-5 mr-2 transition-transform duration-300 group-hover:translate-x-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
                </svg>
                {{ isLoading ? 'Creating Account...' : 'Create Account' }}
              </span>
            </button>
          </form>
        </div>
      </div>

      <!-- Footer -->
      <div class="mt-8 text-center">
        <p class="text-sm text-gray-600">
          {{ !isRegister ? "Don't have an account?" : "Already have an account?" }}
          <button @click="isRegister = !isRegister" class="font-medium text-blue-900 hover:text-yellow-600 transition-colors duration-200">
            {{ !isRegister ? "Sign up" : "Sign in" }}
          </button>
        </p>
      </div>
    </div>
  </div>
</template>
 
<script setup lang="ts">
import { reactive, ref } from "vue";
import { useStore } from "vuex";
import { useRouter } from "vue-router";
import { toast } from "vue3-toastify";
 
const store = useStore();
const router = useRouter();
 
const isRegister = ref(false);
const isLoading = ref(false);

const loginForm = reactive({ email: "", password: "" });
const registerForm = reactive({
  name: "",
  email: "",
  password: "",
  phoneNumber: "",
  address: "",
  annualIncome: null as number | null,
});

const errors = reactive<Record<string, string>>({});
 
// Login Validation
const validateLogin = () => {
  errors.email = !loginForm.email ? "Email is required" : "";
  errors.password = !loginForm.password ? "Password is required" : "";
  return !errors.email && !errors.password;
};
 
// Register Validation
const validateRegister = () => {
   errors.name = !registerForm.name ? "Name is required" : "";
  errors.email = !registerForm.email
    ? "Email is required"
    : !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)
    ? "Invalid email"
    : "";
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
  errors.password = !passwordRegex.test(registerForm.password)
    ? "Password must be min 6 chars, include uppercase, lowercase, number & special char"
    : "";
  errors.phoneNumber = !/^\d{10}$/.test(registerForm.phoneNumber)
    ? "Phone must be 10 digits"
    : "";
  errors.address = !registerForm.address ? "Address is required" : "";
  errors.annualIncome =
    !registerForm.annualIncome || registerForm.annualIncome <= 0
      ? "Annual income must be positive"
      : "";
  return Object.values(errors).every((v) => !v);
};
 
// Login Handler
const handleLogin = async () => {
  if (validateLogin()) {
    isLoading.value = true;
    try {
      await store.dispatch("auth/login", loginForm);
      router.push("/dashboard");
      toast.success("Login successful! 🎉");
    } catch (err) {
      toast.error("Login failed. Please try again.");
      console.error("Login failed:", err);
    } finally {
      isLoading.value = false;
    }
  } else {
    toast.warn("Please fix the highlighted errors.");
  }
};
 
// Register Handler
const handleRegister = async () => {
  if (validateRegister()) {
    isLoading.value = true;
    try {
      await store.dispatch("auth/register", registerForm);
       Object.assign(registerForm, {
        name: "",
        email: "",
        password: "",
        phoneNumber: "",
        address: "",
        annualIncome: null
      });
      router.push("/login");
      toast.success("Registration successful! 🎉");
      toast.success("Login with your credentials now!");
    } catch (err) {
      toast.error("Registration failed. Please try again." + err);
      console.error("Registration failed:", err);
    } finally {
      isLoading.value = false;
    }
  } else {
    toast.warn("Please fix the highlighted errors.");
  }
};
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out;
}

.animate-slide-in {
  animation: slideIn 0.4s ease-out;
}

/* Custom scrollbar for better UX */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Enhanced focus styles */
input:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* Smooth transitions for all interactive elements */
button, input, a {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* Loading state for buttons */
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

/* Enhanced hover effects */
.hover-lift:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}
</style>
 