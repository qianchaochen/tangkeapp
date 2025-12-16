<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  console.log('App Launch')
})
</script>

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
@import './static/styles/common.css';

#app {
  width: 100%;
  min-height: 100vh;
}
</style>
