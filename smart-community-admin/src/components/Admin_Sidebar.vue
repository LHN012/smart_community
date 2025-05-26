<template>
  <div class="sidebar">
    <div class="logo">
      <h2>智能社区管理系统</h2>
      <p class="subtitle">Smart Community</p>
    </div>
    <nav>
      <ul>
        <li :class="{ active: currentPath === '/user-management' }" @click="navigate('/user-management')">
          <i class="fas fa-users"></i>
          <span>用户信息管理</span>
        </li>
        <li :class="{ active: currentPath === '/user-house-management' }" @click="navigate('/user-house-management')">
          <i class="fas fa-home"></i>
          <span>用户房屋绑定</span>
        </li>
        <li :class="{ active: currentPath === '/house-info' }" @click="navigate('/house-info')">
          <i class="fas fa-building"></i>
          <span>房屋信息管理</span>
        </li>
        <li :class="{ active: currentPath === '/transaction-records' }" @click="navigate('/transaction-records')">
          <i class="fas fa-money-bill-wave"></i>
          <span>缴费/充值记录</span>
        </li>
        <li :class="{ active: currentPath === '/notifications' }" @click="navigate('/notifications')">
          <i class="fas fa-bell"></i>
          <span>通知管理</span>
        </li>
        <li :class="{ active: currentPath === '/maintenance' }" @click="navigate('/maintenance')">
          <i class="fas fa-tools"></i>
          <span>故障报修管理</span>
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
  width: 250px;
  height: 100vh;
  background: linear-gradient(180deg, #1a1f3c 0%, #2c3e50 100%);
  color: white;
  position: fixed;
  left: 0;
  top: 0;
  box-shadow: 4px 0 10px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.logo {
  padding: 25px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(255, 255, 255, 0.05);
}

.logo h2 {
  margin: 0;
  font-size: 1.4em;
  font-weight: 600;
  background: linear-gradient(45deg, #fff, #a8c0ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  margin: 5px 0 0;
  font-size: 0.8em;
  color: #a8c0ff;
  opacity: 0.8;
}

nav ul {
  list-style: none;
  padding: 15px 0;
  margin: 0;
}

nav li {
  padding: 12px 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  margin: 5px 0;
  border-radius: 0 25px 25px 0;
  margin-right: 15px;
}

nav li:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

nav li.active {
  background: linear-gradient(45deg, #3498db, #2980b9);
  box-shadow: 0 2px 10px rgba(52, 152, 219, 0.3);
}

nav li i {
  margin-right: 12px;
  font-size: 1.1em;
  width: 20px;
  text-align: center;
  color: #a8c0ff;
}

nav li.active i {
  color: white;
}

nav li span {
  font-size: 0.95em;
  font-weight: 500;
}
</style>