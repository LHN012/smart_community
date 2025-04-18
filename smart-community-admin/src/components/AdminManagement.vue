<template>
  <div class="admin-management">
    <div class="header">
      <h2>管理员管理</h2>
      <div class="header-buttons">
        <button class="add-btn" @click="showAddDialog">添加管理员</button>
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
            <th>用户名</th>
            <th>真实姓名</th>
            <th>邮箱</th>
            <th>手机号</th>
            <th>地址</th>
            <th>角色</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="admin in filteredAdmins" :key="admin.userId">
            <td>{{ admin.userId }}</td>
            <td>{{ admin.username }}</td>
            <td>{{ admin.realName }}</td>
            <td>{{ admin.email }}</td>
            <td>{{ admin.phoneNumber }}</td>
            <td>{{ admin.address }}</td>
            <td>{{ getRoleName(admin.role) }}</td>
            <td>
              <button class="edit-btn" @click="showEditDialog(admin)">编辑</button>
              <button class="delete-btn" @click="handleDelete(admin.userId)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加/编辑对话框 -->
    <div v-if="showDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑管理员' : '添加管理员' }}</h3>
        <div class="form-group">
          <label>用户名</label>
          <input v-model="currentAdmin.username" :disabled="isEdit" />
        </div>
        <div class="form-group">
          <label>密码{{ isEdit ? '（留空表示不修改）' : '' }}</label>
          <input v-model="currentAdmin.password" type="password" />
        </div>
        <div class="form-group">
          <label>角色</label>
          <select v-model="currentAdmin.role">
            <option :value="2">管理员</option>
            <option :value="3">超级管理员</option>
          </select>
        </div>
        <div class="form-group">
          <label>真实姓名</label>
          <input v-model="currentAdmin.realName" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="currentAdmin.email" type="email" />
        </div>
        <div class="form-group">
          <label>手机号</label>
          <input v-model="currentAdmin.phoneNumber" />
        </div>
        <div class="form-group">
          <label>地址</label>
          <input v-model="currentAdmin.address" />
        </div>
        <div class="form-group">
          <label>头像URL</label>
          <input v-model="currentAdmin.avatar" />
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
const admins = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const isEdit = ref(false);
const currentAdmin = ref({
  username: '',
  password: '',
  realName: '',
  email: '',
  phoneNumber: '',
  address: '',
  avatar: '',
  role: 2  // 默认为管理员角色
});

const getRoleName = (role) => {
  switch (role) {
    case 3:
      return '超级管理员';
    case 2:
      return '管理员';
    case 1:
      return '普通用户';
    default:
      return '未知角色';
  }
};

const filteredAdmins = computed(() => {
  if (!searchKeyword.value) return admins.value;
  const keyword = searchKeyword.value.toLowerCase();
  return admins.value.filter(admin => 
    admin.username.toLowerCase().includes(keyword) ||
    admin.realName?.toLowerCase().includes(keyword) ||
    admin.email?.toLowerCase().includes(keyword)
  );
});

const fetchAdmins = async () => {
  try {
    const response = await api.get('/api/admin/list');
    if (response.data.code === 200) {
      admins.value = response.data.data;
    }
  } catch (error) {
    console.error('获取管理员列表失败:', error);
    alert('获取管理员列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const showAddDialog = () => {
  isEdit.value = false;
  currentAdmin.value = {
    username: '',
    password: '',
    realName: '',
    email: '',
    phoneNumber: '',
    address: '',
    avatar: '',
    role: 2  // 默认为管理员角色
  };
  showDialog.value = true;
};

const showEditDialog = (admin) => {
  isEdit.value = true;
  currentAdmin.value = { ...admin, password: '' };
  showDialog.value = true;
};

const closeDialog = () => {
  showDialog.value = false;
  currentAdmin.value = {
    username: '',
    password: '',
    realName: '',
    email: '',
    phoneNumber: '',
    address: '',
    avatar: '',
    role: 2  // 默认为管理员角色
  };
};

const handleSubmit = async () => {
  if (!currentAdmin.value.username || (!isEdit.value && !currentAdmin.value.password)) {
    alert('请填写必要信息！');
    return;
  }

  try {
    if (isEdit.value) {
      // 如果是编辑模式且密码为空，删除密码字段
      if (!currentAdmin.value.password) {
        delete currentAdmin.value.password;
      }
      const response = await api.put(`/api/admin/${currentAdmin.value.userId}`, currentAdmin.value);
      if (response.data.code === 200) {
        alert('更新成功！');
        await fetchAdmins();
        closeDialog();
      }
    } else {
      const response = await api.post('/api/admin/create', currentAdmin.value);
      if (response.data.code === 200) {
        alert('创建成功！');
        await fetchAdmins();
        closeDialog();
      }
    }
  } catch (error) {
    console.error(isEdit.value ? '更新失败:' : '创建失败:', error);
    alert((isEdit.value ? '更新' : '创建') + '失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleDelete = async (userId) => {
  if (!confirm('确定要删除该管理员吗？')) return;
  
  try {
    const response = await api.delete(`/api/admin/${userId}`);
    if (response.data.code === 200) {
      alert('删除成功！');
      await fetchAdmins();
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
    router.push('/login');
  }
};

onMounted(() => {
  fetchAdmins();
});
</script>

<style scoped>
.admin-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-bar {
  margin-bottom: 20px;
}

.search-bar input {
  width: 300px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.add-btn, .edit-btn, .delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
}

.edit-btn {
  background-color: #2196F3;
  color: white;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.logout-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background-color: #ff5722;
  color: white;
}

.logout-btn:hover {
  background-color: #f4511e;
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
}

.dialog {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn, .submit-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #f5f5f5;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
}
</style>