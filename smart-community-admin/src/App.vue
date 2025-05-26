<script setup>
import { ref, onMounted, computed, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import Sidebar1 from './components/SuperAdmin_Sidebar.vue';
import Sidebar2 from './components/Admin_Sidebar.vue';

const route = useRoute();
const showSidebar = computed(() => route.path !== '/login');
// 定义 userRole 为响应式变量
const userRole = ref(null);

// 更新用户角色的方法
const updateUserRole = () => {
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    userRole.value = user.role;
  } catch (error) {
    console.error('解析用户信息出错:', error);
    userRole.value = null;
  }
};

onMounted(() => {
  updateUserRole();
  // 监听localStorage的变化
  window.addEventListener('storage', updateUserRole);
});

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('storage', updateUserRole);
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
/* 全局样式 */
:root {
  --bg-color: #f8f9fa;
  --text-color: #2c3e50;
  --border-color: #e9ecef;
  --header-bg: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  --card-bg: #ffffff;
  --shadow-color: rgba(0, 0, 0, 0.08);
  --hover-bg: #f8f9fa;
  --sidebar-bg: #ffffff;
  --sidebar-text: #2c3e50;
  --sidebar-active: #3498db;
}

/* 暗色主题 */
.dark-theme {
  --bg-color: #1a1a1a;
  --text-color: #e9ecef;
  --border-color: #2c3e50;
  --header-bg: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
  --card-bg: #2c3e50;
  --shadow-color: rgba(0, 0, 0, 0.2);
  --hover-bg: #34495e;
  --sidebar-bg: #2c3e50;
  --sidebar-text: #e9ecef;
  --sidebar-active: #3498db;
}

body {
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: all 0.3s ease;
}

/* 暗色主题下的表格样式 */
.dark-theme .common-table {
  background: var(--header-bg);
  border-color: var(--border-color);
}

.dark-theme .table th {
  background: var(--header-bg);
  color: var(--text-color);
  border-bottom-color: var(--border-color);
}

.dark-theme .table td {
  color: var(--text-color);
  border-bottom-color: var(--border-color);
}

.dark-theme .table-row:hover td {
  background: var(--hover-bg);
}

/* 暗色主题下的搜索框样式 */
.dark-theme .common-search {
  background: var(--header-bg);
  border-color: var(--border-color);
}

.dark-theme .el-input__wrapper {
  background: var(--card-bg) !important;
  border-color: var(--border-color) !important;
}

.dark-theme .el-input__inner {
  color: var(--text-color) !important;
}

/* 暗色主题下的按钮样式 */
.dark-theme .el-button {
  background: var(--card-bg);
  border-color: var(--border-color);
  color: var(--text-color);
}

.dark-theme .el-button--primary {
  background: linear-gradient(135deg, #3498db, #2980b9);
  border-color: transparent;
  color: white;
}

.dark-theme .el-button--danger {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  border-color: transparent;
  color: white;
}

/* 暗色主题下的滚动条样式 */
.dark-theme ::-webkit-scrollbar-track {
  background: var(--bg-color);
}

.dark-theme ::-webkit-scrollbar-thumb {
  background: #3498db;
}

.dark-theme ::-webkit-scrollbar-thumb:hover {
  background: #2980b9;
}

/* 暗色主题下的侧边栏样式 */
.dark-theme .el-menu {
  background-color: var(--sidebar-bg) !important;
  border-right-color: var(--border-color) !important;
}

.dark-theme .el-menu-item {
  color: var(--sidebar-text) !important;
}

.dark-theme .el-menu-item:hover {
  background-color: var(--hover-bg) !important;
}

.dark-theme .el-menu-item.is-active {
  color: var(--sidebar-active) !important;
  background-color: var(--hover-bg) !important;
}

/* 暗色主题下的对话框样式 */
.dark-theme .el-dialog {
  background: var(--card-bg);
  border: 1px solid var(--border-color);
}

.dark-theme .el-dialog__title {
  color: var(--text-color);
}

.dark-theme .el-dialog__body {
  color: var(--text-color);
}

/* 暗色主题下的表单样式 */
.dark-theme .el-form-item__label {
  color: var(--text-color);
}

.dark-theme .el-form-item__content {
  color: var(--text-color);
}

/* 暗色主题下的选择器样式 */
.dark-theme .el-select-dropdown {
  background: var(--card-bg);
  border-color: var(--border-color);
}

.dark-theme .el-select-dropdown__item {
  color: var(--text-color);
}

.dark-theme .el-select-dropdown__item.hover,
.dark-theme .el-select-dropdown__item:hover {
  background-color: var(--hover-bg);
}

/* 暗色主题下的分页样式 */
.dark-theme .el-pagination {
  background: var(--card-bg);
  color: var(--text-color);
}

.dark-theme .el-pagination .el-pagination__total,
.dark-theme .el-pagination .el-pagination__jump {
  color: var(--text-color);
}

.dark-theme .el-pagination .btn-prev,
.dark-theme .el-pagination .btn-next {
  background: var(--card-bg);
  color: var(--text-color);
}

.dark-theme .el-pagination .el-pager li {
  background: var(--card-bg);
  color: var(--text-color);
}

.dark-theme .el-pagination .el-pager li.active {
  color: var(--sidebar-active);
}

/* 登录页面样式 */
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: var(--bg-color);
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Arial, sans-serif;
}

.app {
  min-height: 100vh;
  display: flex;
  background-color: var(--bg-color);
}

.main-content {
  flex: 1;
  margin-left: 250px;
  padding: 30px;
  background-color: var(--bg-color);
  transition: all 0.3s ease;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 表格容器样式 */
.table-container {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-top: 20px;
}

/* 页面标题样式 */
.page-title {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}
</style>