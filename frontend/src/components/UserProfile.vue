<template>
  <div class="bg-gray-50 min-h-screen py-8 px-4 sm:px-6 lg:px-8">
    <div class="max-w-3xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-900 text-center mb-2">My Profile</h1>
      <p class="text-base text-gray-600 text-center mb-8">Manage your personal information and preferences.</p>

      <div class="bg-white rounded-lg p-6 mb-6 shadow-sm">
        <h2 class="text-xl font-semibold text-gray-900 mb-2">Personal Information</h2>
        <p class="text-sm text-gray-500 mb-4">Your basic personal details and contact information.</p>
        <form @submit.prevent="handleUpdate" class="space-y-4">
          <div class="space-y-1">
            <label for="name" class="font-medium text-gray-700">Full Name</label>
            <input id="name" v-model="form.name" placeholder="Enter full name" required 
                   class="w-full p-2.5 border border-gray-300 rounded-md text-base focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200" />
          </div>
          <div class="space-y-1">
            <label for="phone" class="font-medium text-gray-700">Phone Number</label>
            <input id="phone" v-model="form.phoneNumber" placeholder="Enter phone number" required 
                   class="w-full p-2.5 border border-gray-300 rounded-md text-base focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200" />
          </div>
          <div class="space-y-1">
            <label for="address" class="font-medium text-gray-700">Address</label>
            <input id="address" v-model="form.address" placeholder="Enter address" required 
                   class="w-full p-2.5 border border-gray-300 rounded-md text-base focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200" />
          </div>
          <button type="submit" 
                  class="w-full py-3 bg-yellow-400 text-gray-900 font-medium rounded-md hover:bg-gray-800 hover:text-yellow-400 transition-colors">
            Update Profile
          </button>
        </form>
      </div>

      <div class="bg-white rounded-lg p-6 mb-6 shadow-sm">
        <h2 class="text-xl font-semibold text-gray-900 mb-2">Financial Information</h2>
        <p class="text-sm text-gray-500 mb-4">Your income details for credit assessment.</p>
        <div class="flex justify-between items-center py-2 border-b border-gray-200">
          <label for="income" class="font-medium text-gray-700">Annual Income</label>
          <input id="income" type="number" v-model.number="form.annualIncome" placeholder="Enter annual income" required 
                 class="w-1/2 p-2.5 border border-gray-300 rounded-md text-base focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200" />
        </div>
        <div class="flex justify-between items-center py-2 border-b border-gray-200">
          <label class="font-medium text-gray-700">BNPL Eligibility</label>
          <span :class="{ 'bg-green-500 text-white': isEligible, 'bg-gray-200 text-gray-700': !isEligible }" 
                class="px-3 py-1 rounded-full text-sm font-semibold">
            {{ isEligible ? 'Eligible' : 'Not Eligible' }}
          </span>
        </div>
        <p class="text-xs text-gray-500 mt-1">You can use Buy Now, Pay Later services.</p>
      </div>

   
      <div class="bg-white rounded-lg p-6 shadow-sm">
        <h2 class="text-xl font-semibold text-gray-900 mb-2">Account Information</h2>
        <p class="text-sm text-gray-500 mb-4">Your account details and membership information.</p>
        <div v-for="(value, label) in accountInfo" :key="label" 
             class="flex justify-between items-center py-2 border-b border-gray-200 last:border-b-0">
          <label class="font-medium text-gray-700">{{ label }}</label>
          <span class="text-gray-600">{{ value }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive, onMounted, computed } from 'vue';
import { useStore } from 'vuex';
import type { User } from '../types/User';
import { toast } from "vue3-toastify"; 

function decodeToken(token: string): { id: number; email: string; role: string } | null {
  try {
    const jwt = token.replace(/^Bearer\s+/i, '');
    const payload = JSON.parse(atob(jwt.split('.')[1]));
    return {
      id: Number(payload.userId),
      email: payload.sub,
      role: payload.role
    };
  } catch {
    return null;
  }
}

interface ProfileForm {
  name: string;
  phoneNumber: string;
  address: string;
  annualIncome: number;
}

export default defineComponent({
  setup() {
    const store = useStore();

    const form = reactive<ProfileForm>({
      name: '',
      phoneNumber: '',
      address: '',
      annualIncome: 0
    });

    const token = localStorage.getItem('token');
    const decoded = token ? decodeToken(token) : null;

    onMounted(async () => {
      if (!decoded?.id) {
        console.warn('Invalid or missing token');
        return;
      }

      try {
        const profileData: User = await store.dispatch('user/fetchProfile', decoded.id);
        Object.assign(form, {
          name: profileData.name,
          phoneNumber: profileData.phoneNumber,
          address: profileData.address,
          annualIncome: profileData.annualIncome
        });
      } catch (error) {
        console.error('Failed to load profile:', error);
      }
    });

    const handleUpdate = async () => {
      if (!decoded?.id) {
        alert('User ID not found in token');
        return;
      }

      try {
        await store.dispatch('user/updateProfile', {
          userId: decoded.id,
          payload: form
        });
        toast.success("Profile updated successfully!🎉");
      } catch (error) {
        console.error('Update failed:', error);
        toast.error("Error updating profile");
      }
    };

    const isEligible = computed(() => form.annualIncome > 100000);

    const accountInfo = computed(() => {
      const user: User | null = store.getters['user/profile'];
      return {
        Email: user?.email || '-',
        'User ID': user?.id ? `#${user.id}` : '-',
      };
    });

    return { form, handleUpdate, isEligible, accountInfo };
  }
});
</script>