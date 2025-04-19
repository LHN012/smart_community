<template>
  <div class="login-page">
    <div class="login-box">
      <h2>管理系统登录</h2>
      <div class="form-group">
        <label>用户名</label>
        <input 
          v-model="username" 
          type="text" 
          placeholder="请输入用户名"
          @keyup.enter="handleLogin"
        />
      </div>
      <div class="form-group">
        <label>密码</label>
        <input 
          v-model="password" 
          type="password" 
          placeholder="请输入密码"
          @keyup.enter="handleLogin"
        />
      </div>
      <button class="login-btn" @click="handleLogin">登录</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api';

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
      router.push('/admin');
    } else {
      alert(response.data?.msg || '登录失败');
    }
  } catch (error) {
    console.error('登录失败:', error);
    alert('登录失败：' + (error.response?.data?.msg || error.message));
  }
};
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f6fa;
}

.login-box {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.login-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #2c3e50;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #3498db;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #2980b9;
}
</style> 