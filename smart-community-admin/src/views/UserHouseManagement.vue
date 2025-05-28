<template>
  <div class="user-house-management">
    <CommonHeader 
      title="用户房屋绑定信息"
      description="管理用户与房屋的绑定关系"
    />

    <div class="content-wrapper">
      <CommonSearch
        :searchItems="searchItems"
        :initialValues="searchForm"
        @search="handleSearch"
        @reset="handleReset"
      />

      <CommonTable
        :data="filteredRelations"
        :columns="columns"
        class="user-house-table"
      >
        <template #userInfo="{ row }">
          <div>用户名：{{ row.username }}</div>
          <div>真实姓名：{{ row.realName }}</div>
          <div>手机号：{{ row.phoneNumber }}</div>
          <div>邮箱：{{ row.email }}</div>
        </template>

        <template #houseInfo="{ row }">
          <div>区域：{{ row.areaName }}</div>
          <div>楼栋：{{ row.buildingName }}</div>
          <div>单元：{{ row.unitName }}</div>
          <div>房屋：{{ row.houseName }}</div>
        </template>

        <template #operation="{ row }">
          <el-button type="primary" size="small" class="action-btn edit-btn" @click="showEditDialog(row)">
            <el-icon><Edit /></el-icon>
            编辑关系
          </el-button>
          <el-button type="danger" size="small" class="action-btn delete-btn" @click="handleDelete(row)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </CommonTable>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="showDialog"
      title="编辑用户房屋关系"
      width="500px"
      class="user-house-dialog"
    >
      <el-form :model="currentRelation" label-width="120px" class="user-house-form">
        <el-form-item label="关系类型">
          <el-select v-model="currentRelation.relationType" placeholder="请选择关系类型">
            <el-option label="户主" value="户主"></el-option>
            <el-option label="成员" value="成员"></el-option>
            <el-option label="租客" value="租客"></el-option>
          </el-select>
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
import { Edit, Delete } from '@element-plus/icons-vue';
import api from '../api';
import CommonHeader from '../components/CommonHeader.vue';
import CommonSearch from '../components/CommonSearch.vue';
import CommonTable from '../components/CommonTable.vue';

const router = useRouter();
const relations = ref([]);
const searchKeyword = ref(''); // 仍使用 searchKeyword 进行前端过滤
const showDialog = ref(false);
const currentRelation = ref({
  relationId: null,
  relationType: ''
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
    placeholder: '搜索用户名/姓名/手机号/邮箱'
  }
];

// 前端过滤逻辑，使用 computed 属性
const filteredRelations = computed(() => {
  if (!searchKeyword.value) return relations.value;
  const keyword = searchKeyword.value.toLowerCase();
  return relations.value.filter(relation => 
    relation.username?.toLowerCase().includes(keyword) ||
    relation.realName?.toLowerCase().includes(keyword) ||
    relation.phoneNumber?.toLowerCase().includes(keyword) ||
    relation.email?.toLowerCase().includes(keyword)
  );
});

const columns = [
  { prop: 'relationId', label: '关系ID', width: '100px' },
  { prop: 'userInfo', label: '用户信息', width: '250px' },
  { prop: 'houseInfo', label: '房屋信息', width: '250px' },
  { prop: 'relationType', label: '关系类型', width: '100px' },
  { prop: 'operation', label: '操作', width: '150px', fixed: 'right' }
];

const fetchRelations = async () => {
  try {
    const response = await api.get('/api/user-houses/list');
    if (response.data.code === 200) {
      relations.value = response.data.data;
      // 同步 searchForm 的值到 searchKeyword
      searchKeyword.value = searchForm.value.keyword;
    }
  } catch (error) {
    console.error('获取用户房屋关系列表失败:', error);
    alert('获取用户房屋关系列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const showEditDialog = (relation) => {
  currentRelation.value = { ...relation };
  showDialog.value = true;
};

const closeDialog = () => {
  showDialog.value = false;
  currentRelation.value = {
    relationId: null,
    relationType: ''
  };
};

const handleSubmit = async () => {
  if (!currentRelation.value.relationType) {
    alert('请选择关系类型！');
    return;
  }

  try {
    const response = await api.put(`/api/user-houses/update/${currentRelation.value.relationId}`, {
      relationType: currentRelation.value.relationType
    });
    if (response.data.code === 200) {
      alert('更新成功！');
      await fetchRelations(); // 更新成功后重新获取数据
      closeDialog();
    }
  } catch (error) {
    console.error('更新失败:', error);
    alert('更新失败：' + (error.response?.data?.msg || error.message));
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
    localStorage.removeItem('user');
    router.push('/login');
  }
};

const handleDelete = async (relation) => {
  if (!confirm('确定要删除这条用户房屋关系吗？')) {
    return;
  }

  try {
    const response = await api.delete(`/api/user-houses/delete/${relation.relationId}`);
    if (response.data.code === 200) {
      alert('删除成功！');
      await fetchRelations();
    }
  } catch (error) {
    console.error('删除失败:', error);
    alert('删除失败：' + (error.response?.data?.msg || error.message));
  }
};

onMounted(() => {
  fetchRelations();
});
</script>

<style scoped>
.user-house-management {
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

/* 针对 CommonTable 中用户信息和房屋信息的特殊样式 */
.user-house-table :deep(.el-table__cell div) {
  line-height: 1.6; /* 调整行高 */
}

.user-house-table :deep(.el-table__cell) {
    white-space: normal; /* 允许换行 */
    word-break: break-word; /* 允许在任意字符间断行 */
}

/* 针对 CommonTable 的操作列样式 */
.user-house-table :deep(.action-btn) {
  padding: 6px 12px; /* 调整按钮内边距 */
  font-weight: 500;
  border-radius: 6px; /* 调整圆角 */
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05); /* 添加阴影 */
}

.user-house-table :deep(.action-btn:hover) {
  transform: translateY(-1px); /* 悬停上浮 */
  box-shadow: 0 4px 10px var(--shadow-color); /* 悬停阴影 */
}

.user-house-table :deep(.edit-btn) {
  background-color: #3498db;
  border-color: #3498db;
  color: white;
}

.user-house-table :deep(.edit-btn:hover) {
  background-color: #2980b9;
  border-color: #2980b9;
}

.user-house-table :deep(.delete-btn) {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: white;
}

.user-house-table :deep(.delete-btn:hover) {
  background-color: #c0392b;
  border-color: #c0392b;
}

/* 编辑对话框样式 */
.user-house-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  transition: border-color 0.3s ease;
}

.user-house-dialog :deep(.el-dialog__title) {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.user-house-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.user-house-form :deep(.el-input__inner),
.user-house-form :deep(.el-select .el-input__inner) {
  border-radius: 8px; /* 增加圆角 */
  border-color: var(--border-color);
  transition: border-color 0.3s ease;
}

.user-house-form :deep(.el-input__inner:focus),
.user-house-form :deep(.el-select .el-input__inner:focus) {
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
  .user-house-management {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
    margin-top: 16px;
  }
}

/* 暗色主题适配 */
.dark-theme .user-house-management {
  background-color: var(--bg-color);
}

.dark-theme .content-wrapper {
  background-color: var(--card-bg);
  box-shadow: 0 4px 16px var(--shadow-color);
  border-color: var(--border-color);
}

.dark-theme .user-house-table :deep(.action-btn:hover) {
  box-shadow: 0 4px 10px var(--shadow-color);
}

.dark-theme .user-house-dialog :deep(.el-dialog__header) {
  border-bottom-color: var(--border-color);
}

.dark-theme .user-house-dialog :deep(.el-dialog__title) {
  color: var(--text-color);
}

.dark-theme .user-house-form :deep(.el-form-item__label) {
  color: var(--text-color);
}

.dark-theme .user-house-form :deep(.el-input__inner),
.dark-theme .user-house-form :deep(.el-select .el-input__inner) {
  border-color: var(--border-color);
}

.dark-theme .user-house-form :deep(.el-input__inner:focus),
.dark-theme .user-house-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.4);
}
</style> 