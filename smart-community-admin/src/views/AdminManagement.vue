<template>
  <div class="admin-management">
    <CommonHeader 
      title="管理员管理"
      description="管理系统管理员账户"
    >
      <template #extra-buttons>
        <el-button type="primary" class="add-btn" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加管理员
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
        :data="filteredAdmins"
        :columns="columns"
        class="admin-table"
      >
        <template #avatar="{ row }">
          <img 
            v-if="row.avatar" 
            :src="row.avatar" 
            class="avatar-img"
            alt="管理员头像"
          />
          <span v-else>无头像</span>
        </template>

         <template #role="{ row }">
          {{ getRoleName(row.role) }}
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
      :title="isEdit ? '编辑管理员' : '添加管理员'"
      width="500px"
      class="admin-dialog"
    >
      <el-form :model="currentAdmin" label-width="100px" class="admin-form">
        <el-form-item label="用户名">
          <el-input v-model="currentAdmin.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item :label="'密码' + (isEdit ? '（留空表示不修改）' : '')">
          <el-input v-model="currentAdmin.password" type="password" />
        </el-form-item>
         <el-form-item label="角色">
          <el-select v-model="currentAdmin.role" placeholder="请选择角色">
            <el-option label="管理员" :value="2"></el-option>
            <el-option label="超级管理员" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="currentAdmin.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="currentAdmin.email" type="email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="currentAdmin.phoneNumber" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="currentAdmin.address" />
        </el-form-item>
        <el-form-item label="头像URL">
          <el-input v-model="currentAdmin.avatar" />
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
import CommonHeader from '../components/CommonHeader.vue';
import CommonSearch from '../components/CommonSearch.vue';
import CommonTable from '../components/CommonTable.vue';

const router = useRouter();
const admins = ref([]);
const searchKeyword = ref(''); // 仍使用 searchKeyword 进行前端过滤
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

// CommonSearch 需要的表单数据，这里只用于绑定输入框值，实际过滤仍使用 searchKeyword
const searchForm = ref({
  keyword: ''
});

// CommonSearch 的搜索项配置
const searchItems = [
  {
    prop: 'keyword',
    label: '搜索',
    type: 'input',
    placeholder: '搜索用户名/姓名/邮箱'
  }
];

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

// 前端过滤逻辑，使用 computed 属性
const filteredAdmins = computed(() => {
  if (!searchKeyword.value) return admins.value;
  const keyword = searchKeyword.value.toLowerCase();
  return admins.value.filter(admin => 
    admin.username.toLowerCase().includes(keyword) ||
    admin.realName?.toLowerCase().includes(keyword) ||
    admin.email?.toLowerCase().includes(keyword)
  );
});

const columns = [
  { prop: 'userId', label: 'ID', width: '80px' },
  { prop: 'avatar', label: '头像', width: '80px' },
  { prop: 'username', label: '用户名', width: '120px' },
  { prop: 'realName', label: '真实姓名', width: '120px' },
  { prop: 'email', label: '邮箱', width: '180px' },
  { prop: 'phoneNumber', label: '手机号', width: '120px' },
  { prop: 'address', label: '地址', width: '200px' },
  { prop: 'role', label: '角色', width: '100px' },
  { prop: 'operation', label: '操作', width: '150px', fixed: 'right' }
];

const fetchAdmins = async () => {
  try {
    const response = await api.get('/api/admin/list');
    if (response.data.code === 200) {
      admins.value = response.data.data;
      // 同步 searchForm 的值到 searchKeyword
       searchKeyword.value = searchForm.value.keyword;
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
      const response = await api.put(`/api/admin/update/${currentAdmin.value.userId}`, currentAdmin.value);
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
    const response = await api.delete(`/api/admin/delete/${userId}`);
    if (response.data.code === 200) {
      alert('删除成功！');
      await fetchAdmins();
    }
  } catch (error) {
    console.error('删除失败:', error);
    alert('删除失败：' + (error.response?.data?.msg || error.message));
  }
};

// CommonSearch 的搜索事件处理
const handleSearch = (formData) => {
  searchForm.value = formData;
  searchKeyword.value = formData.keyword; // 将 CommonSearch 的值同步到 searchKeyword
};

// CommonSearch 的重置事件处理
const handleReset = () => {
  searchForm.value.keyword = '';
  searchKeyword.value = ''; // 清空 searchKeyword
};

// 由于原界面有退出登录按钮，这里也保留其逻辑，虽然 CommonHeader 里已经有
const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token');
    localStorage.removeItem('user'); // 也移除用户角色信息
    router.push('/login');
  }
};

onMounted(() => {
  fetchAdmins();
});
</script>

<style scoped>
.admin-management {
  padding: 24px; /* 调整内边距 */
  background-color: var(--bg-color); /* 使用 CSS 变量 */
  min-height: 100vh;
  transition: background-color 0.3s ease;
}

.content-wrapper {
  background-color: var(--card-bg); /* 使用 CSS 变量 */
  border-radius: 12px; /* 增加圆角 */
  padding: 24px; /* 调整内边距 */
  box-shadow: 0 4px 16px var(--shadow-color); /* 使用 CSS 变量 */
  margin-top: 24px; /* 调整顶部间距 */
  transition: all 0.3s ease;
  border: 1px solid var(--border-color); /* 使用 CSS 变量 */
}

.avatar-img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--border-color);
  transition: border-color 0.3s ease;
}

/* 针对 CommonTable 的样式 */
.admin-table :deep(.action-btn) {
  padding: 6px 12px; /* 调整按钮内边距 */
  font-weight: 500;
  border-radius: 6px; /* 调整圆角 */
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05); /* 添加阴影 */
}

.admin-table :deep(.action-btn:hover) {
  transform: translateY(-1px); /* 悬停上浮 */
  box-shadow: 0 4px 10px var(--shadow-color); /* 悬停阴影 */
}

.admin-table :deep(.edit-btn) {
  background-color: #3498db;
  border-color: #3498db;
  color: white;
}

.admin-table :deep(.edit-btn:hover) {
  background-color: #2980b9;
  border-color: #2980b9;
}

.admin-table :deep(.delete-btn) {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: white;
}

.admin-table :deep(.delete-btn:hover) {
  background-color: #c0392b;
  border-color: #c0392b;
}

/* 编辑对话框样式 */
.admin-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  transition: border-color 0.3s ease;
}

.admin-dialog :deep(.el-dialog__title) {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.admin-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.admin-form :deep(.el-input__inner),
.admin-form :deep(.el-select .el-input__inner) {
  border-radius: 8px; /* 增加圆角 */
  border-color: var(--border-color);
  transition: border-color 0.3s ease;
}

.admin-form :deep(.el-input__inner:focus),
.admin-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .admin-management {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
    margin-top: 16px;
  }
}

/* 暗色主题适配 */
.dark-theme .admin-management {
  background-color: var(--bg-color);
}

.dark-theme .content-wrapper {
  background-color: var(--card-bg);
  box-shadow: 0 4px 16px var(--shadow-color);
  border-color: var(--border-color);
}

.dark-theme .avatar-img {
  border-color: var(--border-color);
}

.dark-theme .admin-table :deep(.action-btn:hover) {
   box-shadow: 0 4px 10px var(--shadow-color); /* 悬停阴影 */
}

.dark-theme .admin-dialog :deep(.el-dialog__header) {
  border-bottom-color: var(--border-color);
}

.dark-theme .admin-dialog :deep(.el-dialog__title) {
  color: var(--text-color);
}

.dark-theme .admin-form :deep(.el-input__inner),
.dark-theme .admin-form :deep(.el-select .el-input__inner) {
  border-color: var(--border-color);
}

.dark-theme .admin-form :deep(.el-input__inner:focus),
.dark-theme .admin-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.4);
}

</style>