<!-- src/App.vue -->
<template>
  <div id="app">
    <NavigationBar v-if="showLayout" />
 
    <div v-if="showLayout" class="main-container">
      <router-view />
    </div>
 
    <router-view v-else />
  </div>
</template>
 
<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import NavigationBar from './components/NavigationBar.vue'
 
const route = useRoute()
 
const showLayout = computed(() => {
  return !['/', '/login','/register'].includes(route.path)
})
</script>
 
<style>
.main-container {
  max-width: 1180px;
  margin: 28px auto;
  padding: 0 12px;
  font-family: "Inter", system-ui, Arial;
}
</style>
 
 