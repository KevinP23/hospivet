<script setup>
import AppHeader from '../components/AppHeader.vue'
</script>

<template>
  <div class="shell">
    <AppHeader authenticated />
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

