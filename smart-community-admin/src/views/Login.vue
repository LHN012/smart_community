<template>
  <div class="login-page">
    <NetworkBackground />
    <div class="login-wrapper">
      <div class="login-box">
        <div class="login-header">
          <h2>智能社区管理系统</h2>
          <p class="subtitle">Smart Community Management System</p>
        </div>
        <div class="form-group">
          <label>
            <i class="fas fa-user"></i>
            用户名
          </label>
          <input
              v-model="username"
              type="text"
              placeholder="请输入用户名"
              @keyup.enter="handleLogin"
          />
        </div>
        <div class="form-group">
          <label>
            <i class="fas fa-lock"></i>
            密码
          </label>
          <input
              v-model="password"
              type="password"
              placeholder="请输入密码"
              @keyup.enter="handleLogin"
          />
        </div>
        <button class="login-btn" @click="handleLogin">
          <span>登录</span>
          <i class="fas fa-arrow-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api';
import NetworkBackground from '../components/NetworkBackground.vue';

const router = useRouter();
const username = ref('');
const password = ref('');

const handleLogin = async () => {
  if (!username.value || !password.value) {
    alert('请输入用户名和密码！');
    return;
  }

  try {
    const response = await api.post('/api/auth/login', {
      username: username.value,
      password: password.value
    });

    if (response.data && response.data.code === 200) {
      const { token, user } = response.data.data;
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));
      window.dispatchEvent(new Event('storage'));
      if (user.role === 2) {
        router.push('/user-management');
      } else {
        router.push('/admin');
      }
    } else {
      alert(response.data?.msg || '登录失败');
    }
  } catch (error) {
    console.error('登录失败:', error);
    alert(error.response?.data?.msg || '登录失败，请稍后重试');
  }
};
</script>

<style scoped>
.login-page {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-wrapper {
  width: 100%;
  max-width: 400px;
  padding: 20px;
  z-index: 1;
}

.login-box {
  background: rgba(255, 255, 255, 0.95);
  padding: 40px;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  transform: translateY(0);
  transition: all 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #2c3e50;
  font-size: 28px;
  margin-bottom: 10px;
  font-weight: 600;
}

.subtitle {
  color: #7f8c8d;
  font-size: 14px;
  margin-top: 5px;
}

.form-group {
  margin-bottom: 25px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #34495e;
  font-weight: 500;
}

label i {
  margin-right: 8px;
  color: #3498db;
}

input {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
}

input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  outline: none;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(45deg, #3498db, #2980b9);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  background: linear-gradient(45deg, #2980b9, #3498db);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn i {
  transition: transform 0.3s ease;
}

.login-btn:hover i {
  transform: translateX(5px);
}
</style>