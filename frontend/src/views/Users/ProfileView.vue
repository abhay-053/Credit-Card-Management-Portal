<!-- ProfileView.vue -->
<template>
  <UserProfile />
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import UserProfile from '../../components/UserProfile.vue';

export default defineComponent({
  components: { UserProfile }
});
</script>
