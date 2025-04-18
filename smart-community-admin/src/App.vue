<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import Sidebar from './components/Sidebar.vue';

const route = useRoute();
const showSidebar = computed(() => route.path !== '/login');
</script>

<template>
  <div class="app">
    <Sidebar v-if="showSidebar" />
    <div :class="{ 'main-content': showSidebar }">
      <router-view></router-view>
    </div>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Arial, sans-serif;
  background-color: #f5f6fa;
}

.app {
  min-height: 100vh;
  display: flex;
}

.main-content {
  flex: 1;
  margin-left: 200px;
  padding: 20px;
}

/* 登录页面样式 */
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f6fa;
}
</style>