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
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-box {
  width: 300px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 3px;
}

.login-btn {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.login-btn:hover {
  background-color: #0056b3;
}
</style>