<template>
  <div class="maintenance-management">
    <div class="header">
      <h2>故障报修管理</h2>
      <div class="header-buttons">
        <button class="add-btn" @click="showAddDialog">新增报修</button>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="view-switch">
      <button 
        :class="['switch-btn', { active: currentView === 'all' }]" 
        @click="switchView('all')"
      >
        全部报修
      </button>
      <button 
        :class="['switch-btn', { active: currentView === 'my' }]" 
        @click="switchView('my')"
      >
        我的报修
      </button>
    </div>

    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        placeholder="搜索报修描述" 
        @input="handleSearch"
      />
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>报修ID</th>
            <th>提交用户</th>
            <th>报修描述</th>
            <th>维修状态</th>
            <th>负责人</th>
            <th>创建时间</th>
            <th>更新时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="request in filteredRequests" :key="request.requestId">
            <td>{{ request.requestId }}</td>
            <td>{{ usersInfo[request.userId] || '未知用户' }}</td>
            <td>{{ request.description }}</td>
            <td>{{ request.status }}</td>
            <td>{{ request.updateUserId ? (usersInfo[request.updateUserId] || '未知用户') : '状态未更新' }}</td>
            <td>{{ formatDate(request.createdAt) }}</td>
            <td>{{ formatDate(request.updatedAt) }}</td>
            <td>
              <button 
                v-if="currentView === 'all'" 
                class="edit-btn" 
                @click="showEditDialog(request)"
              >
                修改状态
              </button>
              <template v-else>
                <button class="edit-btn" @click="showEditDialog(request)">编辑</button>
                <button class="delete-btn" @click="handleDelete(request)">删除</button>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增报修对话框 -->
    <div v-if="showAddDialogFlag" class="dialog-overlay">
      <div class="dialog">
        <h3>新增报修</h3>
        <div class="form-group">
          <label>报修描述</label>
          <textarea v-model="currentRequest.description" rows="4"></textarea>
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeAddDialog">取消</button>
          <button class="submit-btn" @click="handleAddSubmit">确定</button>
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <div v-if="showEditDialogFlag" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ currentView === 'all' ? '修改状态' : '编辑报修' }}</h3>
        <div class="form-group">
          <label>报修描述</label>
          <textarea 
            v-model="currentRequest.description" 
            rows="4"
            :disabled="currentView === 'all'"
          ></textarea>
        </div>
        <div v-if="currentView === 'all'" class="form-group">
          <label>维修状态</label>
          <select v-model="currentRequest.status">
            <option value="待处理">待处理</option>
            <option value="处理中">处理中</option>
            <option value="已完成">已完成</option>
          </select>
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeEditDialog">取消</button>
          <button class="submit-btn" @click="handleEditSubmit">确定</button>
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
        }
      }
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
    const userId = JSON.parse(localStorage.getItem('user')).userId;
    
    const data = {
      description: currentRequest.value.description
    };
    
    if (currentView.value === 'all') {
      data.status = currentRequest.value.status;
      data.updateUserId = userId;
    } else {
      data.updateUserId = null;
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
    }
  } catch (error) {
    console.error('删除失败:', error);
    alert('删除失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  router.push('/login');
};

onMounted(() => {
  fetchRequests();
});
</script>

<style scoped>
.maintenance-management {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.view-switch {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.switch-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
}

.switch-btn.active {
  background-color: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.search-bar {
  margin-bottom: 20px;
}

.search-bar input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f5f5f5;
  font-weight: 600;
  color: #333;
}

tr:hover {
  background-color: #f9f9f9;
}

button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
}

.add-btn:hover {
  background-color: #45a049;
}

.logout-btn {
  background-color: #f44336;
  color: white;
}

.logout-btn:hover {
  background-color: #d32f2f;
}

.edit-btn {
  background-color: #2196F3;
  color: white;
  margin-right: 5px;
}

.edit-btn:hover {
  background-color: #0b7dda;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.delete-btn:hover {
  background-color: #d32f2f;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  width: 500px;
  max-width: 90%;
}

.dialog h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #333;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn {
  background-color: #9e9e9e;
  color: white;
}

.cancel-btn:hover {
  background-color: #757575;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
}

.submit-btn:hover {
  background-color: #45a049;
}
</style> 