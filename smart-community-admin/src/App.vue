<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import Sidebar1 from './components/SuperAdmin_Sidebar.vue';
import Sidebar2 from './components/Admin_Sidebar.vue';

const route = useRoute();
const showSidebar = computed(() => route.path !== '/login');
// 定义 userRole 为响应式变量
const userRole = ref(null);

onMounted(() => {
  try {
    // 从本地存储获取用户角色
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    userRole.value = user.role;
  } catch (error) {
    console.error('解析用户信息出错:', error);
  }
});

</script>

<template>
  <div class="app">
    <!-- 根据用户角色动态显示侧边栏 -->
    <Sidebar1 v-if="showSidebar && userRole === 3" />
    <Sidebar2 v-if="showSidebar && userRole === 2" />
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