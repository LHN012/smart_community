<template>
  <div class="sidebar">
    <div class="logo">
      <h2>管理系统</h2>
    </div>
    <nav>
      <ul>
        <li :class="{ active: currentPath === '/user-management' }" @click="navigate('/user-management')">
          <i class="fa-solid fa-users"></i>  用户信息管理模块
        </li>
        <li :class="{ active: currentPath === '/user-house-management' }" @click="navigate('/user-house-management')">
          <i class="fa-solid fa-price"></i> 用户房屋绑定模块
        </li>
        <li :class="{ active: currentPath === '/house-info' }" @click="navigate('/house-info')">
          <i class="fa-solid fa-price"></i> 房屋信息模块
        </li>
        <!-- transaction-records -->
        <li :class="{ active: currentPath === '' }" @click="navigate('')">
          <i class="fa-solid fa-price"></i> 缴费/充值记录
        </li>
        <li :class="{ active: currentPath === '/notifications' }" @click="navigate('/notifications')">
          <i class="fa-solid fa-notice"></i> 通知管理模块
        </li>
        <li :class="{ active: currentPath === '' }" @click="navigate('')">
          <i class="fa-solid fa-repair"></i> 报修管理模块
        </li>

      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentPath = ref('/user-management');

const navigate = (path) => {
  currentPath.value = path;
  router.push(path);
};

onMounted(() => {
  currentPath.value = router.currentRoute.value.path;
});
</script>

<style scoped>
.sidebar {
  width: 200px;
  height: 100vh;
  background-color: #2c3e50;
  color: white;
  position: fixed;
  left: 0;
  top: 0;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #34495e;
}

.logo h2 {
  margin: 0;
  font-size: 1.5em;
}

nav ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

nav li {
  padding: 15px 20px;
  cursor: pointer;
  transition: background-color 0.3s;
}

nav li:hover {
  background-color: #34495e;
}

nav li.active {
  background-color: #3498db;
}

nav li i {
  margin-right: 10px;
}
</style>