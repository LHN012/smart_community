<template>
  <div class="common-header">
    <div class="header-left">
      <h2 class="title">{{ title }}</h2>
      <p v-if="description" class="description">{{ description }}</p>
    </div>
    <div class="header-buttons">
      <ThemeSwitch class="theme-switch-btn" />
      <slot name="extra-buttons"></slot>
      <el-button type="danger" class="logout-btn" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { SwitchButton } from '@element-plus/icons-vue';
import ThemeSwitch from './ThemeSwitch.vue';

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  }
});

const router = useRouter();

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.dispatchEvent(new Event('storage'));
    router.push('/login');
  }
};
</script>

<style scoped>
.common-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  background: var(--header-bg);
  padding: 24px 30px;
  border-radius: 16px;
  box-shadow: 0 6px 24px var(--shadow-color),
              0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
  transition: all 0.4s ease;
}

.common-header::before {
  display: none;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.title {
  margin: 0;
  color: var(--text-color);
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: 1px;
  font-family: 'Montserrat', sans-serif;
  text-shadow: 0 2px 4px var(--shadow-color);
  position: relative;
  display: inline-block;
  transition: all 0.4s ease;
}

.title::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #3498db, #2ecc71);
  border-radius: 2px;
}

.description {
  margin: 0;
  color: var(--text-color);
  opacity: 0.7;
  font-size: 0.95rem;
  font-family: 'Roboto', sans-serif;
  letter-spacing: 0.3px;
  transition: all 0.3s ease;
}

.header-buttons {
  display: flex;
  gap: 16px;
  align-items: center;
}

.theme-switch-btn {
  margin-right: 8px;
}

.header-buttons :deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  font-size: 0.95rem;
  font-weight: 500;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'Roboto', sans-serif;
  position: relative;
  overflow: hidden;
}

.header-buttons :deep(.el-button::before) {
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

.header-buttons :deep(.el-button:hover) {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 6px 16px var(--shadow-color);
}

.header-buttons :deep(.el-button:hover::before) {
  transform: translateX(100%);
}

.logout-btn {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  border: none;
  color: white;
}

.logout-btn:hover {
  background: linear-gradient(135deg, #c0392b, #e74c3c);
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .common-header {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }

  .header-buttons {
    width: 100%;
    justify-content: flex-end;
  }

  .title {
    font-size: 1.5rem;
  }
}
</style> 