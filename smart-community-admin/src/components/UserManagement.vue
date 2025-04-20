<template>
  <div class="user-management">
    <div class="header">
      <h2>普通用户信息管理</h2>
      <div class="header-buttons">
        <button class="add-btn" @click="showAddDialog">添加用户</button>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        placeholder="搜索用户名/姓名/邮箱" 
        @input="handleSearch"
      />
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>头像</th>
            <th>用户名</th>
            <th>真实姓名</th>
            <th>邮箱</th>
            <th>手机号</th>
            <th>地址</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.userId">
            <td>{{ user.userId }}</td>
            <td>
              <img 
                v-if="user.avatar" 
                :src="user.avatar" 
                class="avatar-img"
                alt="用户头像"
              />
              <span v-else>无头像</span>
            </td>
            <td>{{ user.username }}</td>
            <td>{{ user.realName }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.phoneNumber }}</td>
            <td>{{ user.address }}</td>
            <td>
              <button class="edit-btn" @click="showEditDialog(user)">编辑</button>
              <button class="delete-btn" @click="handleDelete(user.userId)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加/编辑对话框 -->
    <div v-if="showDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑用户' : '添加用户' }}</h3>
        <div class="form-group">
          <label>用户名</label>
          <input v-model="currentUser.username" :disabled="isEdit" />
        </div>
        <div class="form-group">
          <label>密码{{ isEdit ? '（留空表示不修改）' : '' }}</label>
          <input v-model="currentUser.password" type="password" />
        </div>
        <div class="form-group">
          <label>真实姓名</label>
          <input v-model="currentUser.realName" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="currentUser.email" type="email" />
        </div>
        <div class="form-group">
          <label>手机号</label>
          <input v-model="currentUser.phoneNumber" />
        </div>
        <div class="form-group">
          <label>地址</label>
          <input v-model="currentUser.address" />
        </div>
        <div class="form-group">
          <label>头像URL</label>
          <input v-model="currentUser.avatar" />
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeDialog">取消</button>
          <button class="submit-btn" @click="handleSubmit">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api';

const router = useRouter();
const users = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEdit = ref(false);
const currentUser = ref({
  username: '',
  password: '',
  realName: '',
  email: '',
  phoneNumber: '',
  address: '',
  avatar: ''
});

const filteredUsers = computed(() => {
  if (!searchKeyword.value) return users.value;
  const keyword = searchKeyword.value.toLowerCase();
  return users.value.filter(user => 
    user.username.toLowerCase().includes(keyword) ||
    user.realName?.toLowerCase().includes(keyword) ||
    user.email?.toLowerCase().includes(keyword)
  );
});

const fetchUsers = async () => {
  try {
    const response = await api.get('/api/admin/normal-users');
    if (response.data.code === 200) {
      users.value = response.data.data;
    }
  } catch (error) {
    console.error('获取普通用户列表失败:', error);
    alert('获取普通用户列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const showAddDialog = () => {
  isEdit.value = false;
  currentUser.value = {
    username: '',
    password: '',
    realName: '',
    email: '',
    phoneNumber: '',
    address: '',
    avatar: ''
  };
  showDialog.value = true;
};

const showEditDialog = (user) => {
  isEdit.value = true;
  currentUser.value = { ...user, password: '' };
  showDialog.value = true;
};

const closeDialog = () => {
  showDialog.value = false;
  currentUser.value = {
    username: '',
    password: '',
    realName: '',
    email: '',
    phoneNumber: '',
    address: '',
    avatar: ''
  };
};

const handleSubmit = async () => {
  if (!currentUser.value.username || (!isEdit.value && !currentUser.value.password)) {
    alert('请填写必要信息！');
    return;
  }

  try {
    if (isEdit.value) {
      // 如果是编辑模式且密码为空，删除密码字段
      if (!currentUser.value.password) {
        delete currentUser.value.password;
      }
      const response = await api.put(`/api/admin/update/${currentUser.value.userId}`, currentUser.value);
      if (response.data.code === 200) {
        alert('更新成功！');
        await fetchUsers();
        closeDialog();
      }
    } else {
      const response = await api.post('/api/admin/create-normal-user', currentUser.value);
      if (response.data.code === 200) {
        alert('创建成功！');
        await fetchUsers();
        closeDialog();
      }
    }
  } catch (error) {
    console.error(isEdit.value ? '更新失败:' : '创建失败:', error);
    alert((isEdit.value ? '更新' : '创建') + '失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleDelete = async (userId) => {
  if (!confirm('确定要删除该用户吗？')) return;
  
  try {
    const response = await api.delete(`/api/admin/delete-normal-user/${userId}`);
    if (response.data.code === 200) {
      alert('删除成功！');
      await fetchUsers();
    }
  } catch (error) {
    console.error('删除失败:', error);
    alert('删除失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleSearch = () => {
  // 搜索功能已通过计算属性实现
};

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    router.push('/login');
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.user-management {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: white;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header h2 {
  margin: 0;
  color: #333;
  font-size: 1.5rem;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-bar {
  margin-bottom: 20px;
  background-color: white;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.search-bar input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.edit-btn, .delete-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.edit-btn {
  background-color: #3498db;
  color: white;
}

.edit-btn:hover {
  background-color: #2980b9;
}

.delete-btn {
  background-color: #e74c3c;
  color: white;
}

.delete-btn:hover {
  background-color: #c0392b;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 25px;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.dialog h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 25px;
}

.cancel-btn,
.submit-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: #95a5a6;
  color: white;
}

.cancel-btn:hover {
  background-color: #7f8c8d;
}

.submit-btn {
  background-color: #2ecc71;
  color: white;
}

.submit-btn:hover {
  background-color: #27ae60;
}

.add-btn,
.logout-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.add-btn {
  background-color: #2ecc71;
  color: white;
}

.add-btn:hover {
  background-color: #27ae60;
}

.logout-btn {
  background-color: #e74c3c;
  color: white;
}

.logout-btn:hover {
  background-color: #c0392b;
}
</style>