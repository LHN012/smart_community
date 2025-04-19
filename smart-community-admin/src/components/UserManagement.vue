<template>
  <div>
    <h3>普通用户信息管理</h3>
    <table>
      <thead>
      <tr>
        <th>用户ID</th>
        <th>用户名</th>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="user in normalUsers" :key="user.id">
        <td>{{ user.id }}</td>
        <td>{{ user.username }}</td>
        <td>
          <button @click="editUser(user.id)">编辑</button>
          <button @click="deleteUser(user.id)">删除</button>
        </td>
      </tr>
      </tbody>
    </table>
    <button @click="addUser">添加用户</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import api from '../api';

const normalUsers = ref([]);

const fetchNormalUsers = async () => {
  try {
    const response = await api.get('/api/users?role=1');
    normalUsers.value = response.data.data;
  } catch (error) {
    console.error('获取普通用户信息失败:', error);
  }
};

const editUser = (userId) => {
  // 实现编辑用户逻辑
  console.log(`编辑用户: ${userId}`);
};

const deleteUser = (userId) => {
  // 实现删除用户逻辑
  console.log(`删除用户: ${userId}`);
};

const addUser = () => {
  // 实现添加用户逻辑
  console.log('添加新用户');
};

fetchNormalUsers();
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

th,
td {
  border: 1px solid #ccc;
  padding: 8px;
  text-align: left;
}

button {
  padding: 5px 10px;
  cursor: pointer;
}
</style>