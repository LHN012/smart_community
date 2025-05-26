<template>
  <div class="user-management">
    <CommonHeader 
      title="普通用户信息管理"
      description="管理社区内普通用户的相关信息"
    >
      <template #extra-buttons>
        <el-button type="primary" class="add-btn" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加用户
        </el-button>
      </template>
    </CommonHeader>

    <div class="content-wrapper">
      <CommonSearch
        :searchItems="searchItems"
        :initialValues="searchForm"
        @search="handleSearch"
        @reset="handleReset"
      />

      <CommonTable
        :data="users"
        :columns="columns"
        :defaultSort="{ prop: 'userId', order: 'desc' }"
        class="user-table"
      >
        <template #avatar="{ row }">
          <img 
            v-if="row.avatar" 
            :src="row.avatar" 
            class="avatar-img"
            alt="用户头像"
          />
          <span v-else>无头像</span>
        </template>

        <template #operation="{ row }">
          <el-button type="primary" size="small" class="action-btn edit-btn" @click="showEditDialog(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button type="danger" size="small" class="action-btn delete-btn" @click="handleDelete(row.userId)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </CommonTable>
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="showDialog"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
      class="user-dialog"
    >
      <el-form :model="currentUser" label-width="100px" class="user-form">
        <el-form-item label="用户名">
          <el-input v-model="currentUser.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item :label="'密码' + (isEdit ? '（留空表示不修改）' : '')">
          <el-input v-model="currentUser.password" type="password" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="currentUser.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="currentUser.email" type="email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="currentUser.phoneNumber" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="currentUser.address" />
        </el-form-item>
        <el-form-item label="头像URL">
          <el-input v-model="currentUser.avatar" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Plus, Edit, Delete } from '@element-plus/icons-vue';
import api from '../api';
import CommonTable from '../components/CommonTable.vue';
import CommonSearch from '../components/CommonSearch.vue';
import CommonHeader from '../components/CommonHeader.vue';

const router = useRouter();
const users = ref([]);
const searchForm = ref({
  keyword: '',
  phoneNumber: ''
});

const searchItems = [
  {
    prop: 'keyword',
    label: '搜索',
    type: 'input',
    placeholder: '搜索用户名/姓名/邮箱'
  },
  {
    prop: 'phoneNumber',
    label: '手机号',
    type: 'input',
    placeholder: '请输入手机号'
  }
];

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

const columns = [
  { prop: 'userId', label: 'ID', width: '80px' },
  { prop: 'avatar', label: '头像', width: '80px' },
  { prop: 'username', label: '用户名', width: '120px' },
  { prop: 'realName', label: '真实姓名', width: '120px' },
  { prop: 'email', label: '邮箱', width: '180px' },
  { prop: 'phoneNumber', label: '手机号', width: '120px' },
  { prop: 'address', label: '地址', width: '200px', minWidth: '150px' },
  { prop: 'operation', label: '操作', width: '160px', fixed: 'right' }
];

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

const handleSearch = async (formData) => {
  searchForm.value = formData;
  try {
    const response = await api.get('/api/admin/normal-users/search', {
      params: {
        keyword: formData.keyword,
        phoneNumber: formData.phoneNumber
      }
    });
    if (response.data.code === 200) {
      users.value = response.data.data;
    }
  } catch (error) {
    console.error('搜索用户失败:', error);
    alert('搜索用户失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleReset = async () => {
  searchForm.value = { 
    keyword: '',
    phoneNumber: ''
  };
  await fetchUsers();
};

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.dispatchEvent(new Event('storage'));
    router.push('/login');
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.user-management {
  padding: 24px;
  /* 使用 CSS 变量控制背景色 */
  background-color: var(--bg-color);
  min-height: 100vh;
  transition: background-color 0.3s ease;
}

.content-wrapper {
  /* 使用 CSS 变量控制背景色和阴影 */
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px var(--shadow-color);
  /* 调整顶部间距 */
  margin-top: 24px;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
}

.avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border-color);
  transition: border-color 0.3s ease;
}

.user-table {
  margin-top: 24px;
}

.action-btn {
  padding: 8px 16px;
  font-weight: 500;
  border-radius: 8px; /* 增加圆角 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05); /* 添加阴影 */
}

.action-btn:hover {
  transform: translateY(-2px); /* 悬停上浮 */
  box-shadow: 0 4px 12px var(--shadow-color); /* 悬停阴影 */
}

.edit-btn {
  background-color: #3498db;
  border-color: #3498db;
  color: white;
}

.edit-btn:hover {
  background-color: #2980b9;
  border-color: #2980b9;
}

.delete-btn {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: white;
}

.delete-btn:hover {
  background-color: #c0392b;
  border-color: #c0392b;
}

.add-btn {
  background-color: #2ecc71;
  border-color: #2ecc71;
  font-weight: 500;
  color: white;
}

.add-btn:hover {
  background-color: #27ae60;
  border-color: #27ae60;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.user-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  transition: border-color 0.3s ease;
}

.user-dialog :deep(.el-dialog__title) {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.user-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.user-form :deep(.el-input__inner) {
  border-radius: 8px; /* 增加圆角 */
  border-color: var(--border-color);
  transition: border-color 0.3s ease;
}

.user-form :deep(.el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .user-management {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
    margin-top: 16px; /* 调整间距 */
  }

  .action-btn {
    padding: 6px 12px;
  }
}

/* 暗色主题适配 */
.dark-theme .content-wrapper {
  background-color: var(--card-bg);
  box-shadow: 0 4px 16px var(--shadow-color);
  border-color: var(--border-color);
}

.dark-theme .avatar-img {
  border-color: var(--border-color);
}

.dark-theme .action-btn:hover {
   box-shadow: 0 4px 12px var(--shadow-color); /* 悬停阴影 */
}

.dark-theme .user-dialog :deep(.el-dialog__header) {
  border-bottom-color: var(--border-color);
}

.dark-theme .user-dialog :deep(.el-dialog__title) {
  color: var(--text-color);
}

.dark-theme .user-form :deep(.el-form-item__label) {
  color: var(--text-color);
}

.dark-theme .user-form :deep(.el-input__inner) {
  border-color: var(--border-color);
}

.dark-theme .user-form :deep(.el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.4);
}
</style>