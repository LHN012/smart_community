<template>
  <el-button
    class="theme-switch"
    :class="{ 'is-dark': isDark }"
    @click="toggleTheme"
  >
    <el-icon v-if="isDark"><Sunny /></el-icon>
    <el-icon v-else><Moon /></el-icon>
  </el-button>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { Sunny, Moon } from '@element-plus/icons-vue';

const isDark = ref(false);

// 切换主题
const toggleTheme = () => {
  isDark.value = !isDark.value;
  document.documentElement.classList.toggle('dark-theme', isDark.value);
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light');
};

// 初始化主题
onMounted(() => {
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme === 'dark') {
    isDark.value = true;
    document.documentElement.classList.add('dark-theme');
  }
});
</script>

<style scoped>
.theme-switch {
  padding: 8px;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border: none;
  color: #2c3e50;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.theme-switch::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, 
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.2)
  );
  transform: translateX(-100%);
  transition: transform 0.3s ease;
}

.theme-switch:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.theme-switch:hover::before {
  transform: translateX(100%);
}

.theme-switch.is-dark {
  background: linear-gradient(135deg, #2c3e50, #34495e);
  color: #ffffff;
}

.theme-switch :deep(.el-icon) {
  font-size: 1.2em;
  transition: transform 0.3s ease;
}

.theme-switch:hover :deep(.el-icon) {
  transform: rotate(180deg);
}
</style> 