<template>
  <div class="sidebar">
    <div class="logo">
      <h2>管理系统</h2>
    </div>
    <nav>
      <ul>
        <li :class="{ active: currentPath === '/property' }" @click="navigate('/property')">
          <i class="fa-solid fa-house"></i> 主页
        </li>
        <li :class="{ active: currentPath === '/property/user-management' }" @click="navigate('/property/user-management')">
          <i class="fa-solid fa-users"></i> 用户信息管理
        </li>
        <!-- 可以根据需要添加更多功能模块 -->
      </ul>
    </nav>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentPath = ref('/property');

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