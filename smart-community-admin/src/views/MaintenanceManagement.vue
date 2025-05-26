<template>
  <div class="maintenance-management">
    <CommonHeader
      title="故障报修管理"
      description="管理小区用户的故障报修请求"
    >
      <template #extra-buttons>
        <el-button type="primary" class="add-btn" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          新增报修
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

      <div class="view-switch">
        <el-button
          :class="{ active: currentView === 'all' }"
          @click="switchView('all')"
        >
          全部报修
        </el-button>
        <el-button
          :class="{ active: currentView === 'my' }"
          @click="switchView('my')"
        >
          我的报修
        </el-button>
      </div>

      <CommonTable
        :data="filteredRequests"
        :columns="columns"
        class="maintenance-table"
      >
        <template #submitUser="{ row }">
          {{ usersInfo[row.userId] || '未知用户' }}
        </template>
        <template #handlerUser="{ row }">
          {{ row.updateUserId ? (usersInfo[row.updateUserId] || '未知用户') : '状态未更新' }}
        </template>
        <template #createdAt="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
        <template #updatedAt="{ row }">
          {{ formatDate(row.updatedAt) }}
        </template>
        <template #operation="{ row }">
          <el-button
            v-if="currentView === 'all'"
            type="primary"
            size="small"
            class="action-btn edit-btn"
            @click="showEditDialog(row)"
          >
            <el-icon><Edit /></el-icon>
            修改状态
          </el-button>
          <template v-else>
            <el-button type="primary" size="small" class="action-btn edit-btn" @click="showEditDialog(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" class="action-btn delete-btn" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </template>
      </CommonTable>
    </div>

    <!-- 新增报修对话框 -->
    <el-dialog
      v-model="showAddDialogFlag"
      title="新增报修"
      width="500px"
      class="maintenance-dialog"
    >
      <el-form :model="currentRequest" label-width="100px" class="maintenance-form">
        <el-form-item label="报修描述">
          <el-input
            v-model="currentRequest.description"
            type="textarea"
            :rows="4"
            placeholder="请输入报修描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeAddDialog">取消</el-button>
          <el-button type="primary" @click="handleAddSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="showEditDialogFlag"
      :title="currentView === 'all' ? '修改状态' : '编辑报修'"
      width="500px"
      class="maintenance-dialog"
    >
      <el-form :model="currentRequest" label-width="100px" class="maintenance-form">
        <el-form-item label="报修描述">
          <el-input
            v-model="currentRequest.description"
            type="textarea"
            :rows="4"
            :disabled="currentView === 'all'"
            placeholder="请输入报修描述"
          />
        </el-form-item>
        <el-form-item v-if="currentView === 'all'" label="维修状态">
          <el-select v-model="currentRequest.status" placeholder="请选择维修状态">
            <el-option label="待处理" value="待处理" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeEditDialog">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit">确定</el-button>
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
const requests = ref([]);
const currentView = ref('all');
const searchKeyword = ref('');
const showAddDialogFlag = ref(false);
const showEditDialogFlag = ref(false);

const currentRequest = ref({
  description: '',
  status: '待处理'
});

const usersInfo = ref({});

// CommonSearch 需要的表单数据
const searchForm = ref({
  keyword: ''
});

// CommonSearch 的搜索项配置
const searchItems = [
  {
    prop: 'keyword',
    label: '搜索',
    type: 'input',
    placeholder: '搜索报修描述'
  }
];

// 表格列配置
const columns = [
  { prop: 'requestId', label: '报修ID', width: '100px' },
  { prop: 'submitUser', label: '提交用户', width: '120px' },
  { prop: 'description', label: '报修描述', minWidth: '200px' },
  { prop: 'status', label: '维修状态', width: '100px' },
  { prop: 'handlerUser', label: '负责人', width: '120px' },
  { prop: 'createdAt', label: '创建时间', width: '160px' },
  { prop: 'updatedAt', label: '更新时间', width: '160px' },
  {
    prop: 'operation',
    label: '操作',
    width: '150px',
    fixed: 'right'
  }
];

const filteredRequests = computed(() => {
  if (!searchKeyword.value) return requests.value;
  const keyword = searchKeyword.value.toLowerCase();
  return requests.value.filter(request =>
    request.description.toLowerCase().includes(keyword)
  );
});

const formatDate = (dateString) => {
  if (!dateString) return '';
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      console.error('无效的日期格式:', dateString);
      return '';
    }
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    console.error('日期格式化错误:', error);
    return '';
  }
};

const fetchRequests = async () => {
  try {
    const token = localStorage.getItem('token');
    const userId = JSON.parse(localStorage.getItem('user')).userId;

    const endpoint = currentView.value === 'all'
      ? '/api/maintenance/requests'
      : `/api/maintenance/requests/user/${userId}`;

    const response = await api.get(endpoint, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (response.data.code === 200) {
      requests.value = response.data.data.requests;
      // 确保用户信息存在
      if (response.data.data.usersInfo) {
        usersInfo.value = response.data.data.usersInfo;
      } else {
        // 如果没有用户信息，尝试获取
        const usersResponse = await api.get('/api/admin/normal-users', {
          headers: { 'Authorization': `Bearer ${token}` }
        });
        if (usersResponse.data.code === 200) {
          const usersList = usersResponse.data.data;
          const usersMap = {};
          usersList.forEach(user => {
            usersMap[user.userId] = user.username || user.realName || '未知用户';
          });
          usersInfo.value = usersMap;
        } else {
           alert('获取用户信息失败：' + usersResponse.data.msg);
        }
      }
    } else {
        alert('获取报修列表失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('获取报修列表失败:', error);
    alert('获取报修列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const switchView = (view) => {
  currentView.value = view;
  fetchRequests();
};

const showAddDialog = () => {
  currentRequest.value = {
    description: '',
    status: '待处理'
  };
  showAddDialogFlag.value = true;
};

const closeAddDialog = () => {
  showAddDialogFlag.value = false;
  currentRequest.value = {
    description: '',
    status: '待处理'
  };
};

const handleAddSubmit = async () => {
  if (!currentRequest.value.description) {
    alert('请填写报修描述！');
    return;
  }

  try {
    const token = localStorage.getItem('token');
    const userId = JSON.parse(localStorage.getItem('user')).userId;

    const response = await api.post('/api/maintenance/requests', {
      userId: userId,
      description: currentRequest.value.description,
      status: '待处理'
    }, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (response.data.code === 200) {
      alert('报修成功，正在加紧解决！');
      closeAddDialog();
      fetchRequests();
    } else {
      alert('提交失败：' + (response.data.msg || '未知错误'));
    }
  } catch (error) {
    console.error('提交失败:', error);
    alert('提交失败：' + (error.response?.data?.msg || error.message || '网络错误，请稍后重试'));
  }
};

const showEditDialog = (request) => {
  currentRequest.value = { ...request };
  showEditDialogFlag.value = true;
};

const closeEditDialog = () => {
  showEditDialogFlag.value = false;
  currentRequest.value = {
    description: '',
    status: '待处理'
  };
};

const handleEditSubmit = async () => {
  try {
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));
    const userId = user ? user.userId : null; // 获取当前登录用户ID

    if (!userId) {
      alert('无法获取用户信息，请重新登录！');
      return;
    }

    const data = {
      description: currentRequest.value.description,
      // 在编辑时始终包含requestId
      requestId: currentRequest.value.requestId
    };

    if (currentView.value === 'all') {
      data.status = currentRequest.value.status;
      data.updateUserId = userId; // 设置负责人为当前管理员
    } else {
      // 用户只能修改描述，负责人字段不在此处修改
      // data.updateUserId = null; // 用户视图不应修改updateUserId
    }

    const response = await api.put(
      `/api/maintenance/requests/${currentRequest.value.requestId}`,
      data,
      {
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );

    if (response.data.code === 200) {
      alert('更新成功！');
      closeEditDialog();
      fetchRequests();
    } else {
       alert('更新失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('更新失败:', error);
    alert('更新失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleDelete = async (request) => {
  if (!confirm('确定要删除这条报修记录吗？')) return;

  try {
    const token = localStorage.getItem('token');
    const response = await api.delete(
      `/api/maintenance/requests/${request.requestId}`,
      {
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );

    if (response.data.code === 200) {
      alert('删除成功！');
      fetchRequests();
    } else {
       alert('删除失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('删除失败:', error);
    alert('删除失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleSearch = (formData) => {
  searchForm.value = formData;
  searchKeyword.value = formData.keyword;
};

const handleReset = () => {
  searchForm.value.keyword = '';
  searchKeyword.value = '';
};

onMounted(() => {
  fetchRequests();
});
</script>

<style scoped>
.maintenance-management {
  padding: 24px;
  background-color: var(--bg-color);
  min-height: 100vh;
  transition: background-color 0.3s ease;
}

.content-wrapper {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px var(--shadow-color);
  margin-top: 24px;
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
}

.view-switch {
  margin-bottom: 20px;
  display: flex;
  border-bottom: 1px solid var(--border-color);
}

.view-switch :deep(.el-button) {
  flex-grow: 1;
  padding: 12px 16px; /* Adjusted padding */
  border-radius: 0; /* Remove border-radius */
  margin-right: -1px; /* Overlap borders */
  transition: all 0.3s ease;
  background-color: transparent; /* Transparent background */
  color: var(--text-color); /* Default text color */
  border: 1px solid transparent; /* Transparent border */
  border-bottom: none; /* Remove bottom border */
  font-size: 16px;
  font-weight: 500;
}

.view-switch :deep(.el-button:first-child) {
    border-left: none;
}

.view-switch :deep(.el-button:last-child) {
  margin-right: 0;
   border-right: none;
}


.view-switch :deep(.el-button:hover) {
  color: var(--primary-color);
}

.view-switch :deep(.el-button.active) {
  color: var(--primary-color);
  border-color: var(--border-color);
  border-bottom-color: var(--card-bg); /* Match content wrapper background */
  z-index: 1;
}

/* 针对 CommonTable 的样式 */
.maintenance-table :deep(.action-btn) {
  padding: 6px 12px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  margin-right: 8px;
}

.maintenance-table :deep(.action-btn:last-child) {
  margin-right: 0;
}

.maintenance-table :deep(.action-btn:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px var(--shadow-color);
}

/* 对话框样式 */
.maintenance-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  transition: border-color 0.3s ease;
}

.maintenance-dialog :deep(.el-dialog__title) {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.maintenance-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.maintenance-form :deep(.el-input__inner),
.maintenance-form :deep(.el-textarea__inner),
.maintenance-form :deep(.el-select .el-input__inner) {
  border-radius: 8px;
  border-color: var(--border-color);
  transition: border-color 0.3s ease;
}

.maintenance-form :deep(.el-input__inner:focus),
.maintenance-form :deep(.el-textarea__inner:focus),
.maintenance-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .maintenance-management {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
    margin-top: 16px;
  }
}

/* 暗色主题适配 */
.dark-theme .maintenance-management {
  background-color: var(--bg-color);
}

.dark-theme .content-wrapper {
  background-color: var(--card-bg);
  box-shadow: 0 4px 16px var(--shadow-color);
  border-color: var(--border-color);
}

.dark-theme .view-switch :deep(.switch-btn) {
   background-color: var(--button-bg);
   color: var(--button-text-color);
   border-color: var(--button-border-color);
}

.dark-theme .view-switch :deep(.switch-btn:hover) {
   background-color: var(--button-hover-bg);
   color: var(--button-hover-text-color);
   border-color: var(--button-hover-border-color);
}

.dark-theme .view-switch :deep(.switch-btn.active) {
   background-color: var(--primary-color);
   color: var(--primary-text-color);
   border-color: var(--primary-color);
}


.dark-theme .maintenance-table :deep(.action-btn:hover) {
  box-shadow: 0 4px 10px var(--shadow-color);
}

.dark-theme .maintenance-dialog :deep(.el-dialog__header) {
  border-bottom-color: var(--border-color);
}

.dark-theme .maintenance-dialog :deep(.el-dialog__title) {
  color: var(--text-color);
}

.dark-theme .maintenance-form :deep(.el-form-item__label) {
  color: var(--text-color);
}

.dark-theme .maintenance-form :deep(.el-input__inner),
.dark-theme .maintenance-form :deep(.el-textarea__inner),
.dark-theme .maintenance-form :deep(.el-select .el-input__inner) {
  border-color: var(--border-color);
}

.dark-theme .maintenance-form :deep(.el-input__inner:focus),
.dark-theme .maintenance-form :deep(.el-textarea__inner:focus),
.dark-theme .maintenance-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.4);
}
</style> 