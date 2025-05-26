<template>
  <div class="notification-management">
    <CommonHeader
      title="通知管理"
      description="发布和管理小区通知"
    >
      <template #extra-buttons>
        <el-button type="primary" class="add-btn" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          发布通知
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
        :data="filteredNotifications"
        :columns="columns"
        class="notification-table"
      >
        <template #notificationType="{ row }">
          {{ getReceiverTypeName(row.notification_type || row.notificationType) }}
        </template>
        <template #receiverType="{ row }">
          {{ getReceiverTypeName(row.receiver_type || row.receiverType) }}
        </template>
        <template #receiverInfo="{ row }">
          {{ getReceiverInfo(row) }}
        </template>
        <template #sendTime="{ row }">
          {{ formatDate(row.send_time || row.sendTime) }}
        </template>
      </CommonTable>
    </div>

    <!-- 添加通知对话框 -->
    <el-dialog
      v-model="showDialog"
      title="发布通知"
      width="500px"
      class="notification-dialog"
    >
      <el-form :model="currentNotification" label-width="100px" class="notification-form">
        <el-form-item label="通知类型">
          <el-select v-model="currentNotification.notificationType" placeholder="请选择通知类型">
            <el-option v-for="option in notificationTypeOptions" :key="option.value" :label="option.label" :value="option.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="currentNotification.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="currentNotification.content"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
          />
        </el-form-item>
        <el-form-item label="接收对象类型">
          <el-select v-model="currentNotification.receiverType" placeholder="请选择接收对象类型">
            <el-option value="全体业主" label="全体业主"></el-option>
            <el-option value="物业员工" label="物业员工"></el-option>
            <!-- 特定房间业主需要额外的选择逻辑，这里先简化 -->
          </el-select>
        </el-form-item>
        <!-- 特定房间业主选择房屋的逻辑可以根据需要添加 -->
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="confirmSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 确认对话框 -->
    <el-dialog
      v-model="showConfirmDialog"
      title="确认发布"
      width="300px"
      class="notification-dialog"
    >
      <span>确定要发布这条通知吗？</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showConfirmDialog = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Plus } from '@element-plus/icons-vue';
import api from '../api';
import CommonHeader from '../components/CommonHeader.vue';
import CommonSearch from '../components/CommonSearch.vue';
import CommonTable from '../components/CommonTable.vue';

const router = useRouter();
const notifications = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showConfirmDialog = ref(false);

const currentNotification = ref({
  notificationType: '',
  title: '',
  content: '',
  receiverType: '全体业主' // 默认接收对象为全体业主
});

const notificationTypeOptions = [
  { label: '公告', value: '公告' },
  { label: '报修通知', value: '报修通知' },
  { label: '缴费通知', value: '缴费通知' },
  { label: '活动通知', value: '活动通知' },
  { label: '紧急通知', value: '紧急通知' },
  { label: '其他', value: '其他' }
];

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
    placeholder: '搜索通知标题/内容'
  }
];

// 表格列配置
const columns = [
  { prop: 'id', label: '通知ID', width: '80px' },
  { prop: 'notificationType', label: '通知类型', width: '100px' },
  { prop: 'title', label: '标题', width: '150px' },
  { prop: 'content', label: '内容', minWidth: '200px' },
  { prop: 'sender_name', label: '发送者', width: '100px' },
  { prop: 'receiverType', label: '接收类型', width: '100px' },
  { prop: 'receiverInfo', label: '接收对象', width: '150px' },
  { prop: 'sendTime', label: '发送时间', width: '160px' }
];

const filteredNotifications = computed(() => {
  if (!searchKeyword.value) return notifications.value;
  const keyword = searchKeyword.value.toLowerCase();
  return notifications.value.filter(notification =>
    (notification.title && notification.title.toLowerCase().includes(keyword)) ||
    (notification.content && notification.content.toLowerCase().includes(keyword))
  );
});

const getReceiverTypeName = (type) => {
  const typeMap = {
    '全体业主': '全体业主',
    '特定房间业主': '特定房间业主',
    '物业员工': '物业员工',
    'all': '全体业主',
    'specific': '特定房间业主',
    'staff': '物业员工'
  };
  return typeMap[type] || type || '未知类型';
};

const getReceiverInfo = (notification) => {
  // 注意：后端返回的数据结构可能需要调整或补充房屋信息才能准确显示特定房间业主
  if (notification.receiver_type === '特定房间业主' || notification.receiverType === '特定房间业主') {
     // 如果后端返回了详细的房屋信息，可以在这里使用
     // 例如：return `${notification.areaName}${notification.buildingName}${notification.unitName}${notification.roomName}`;
    return `房间ID: ${notification.receiver_id || '未知'}`;
  } else {
    return '全部';
  }
};

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
      minute: '2-digit',
      second: '2-digit'
    });
  } catch (error) {
    console.error('日期格式化错误:', error);
    return '';
  }
};

const fetchNotifications = async () => {
  try {
    const response = await api.get('/api/notifications');
    if (response.data.code === 200) {
      notifications.value = response.data.data;
    } else {
      alert('获取通知列表失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('获取通知列表失败:', error);
    console.error('错误详情:', error.response?.data);
    alert('获取通知列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const showAddDialog = () => {
  currentNotification.value = {
    notificationType: '',
    title: '',
    content: '',
    receiverType: '全体业主' // 默认接收对象为全体业主
  };
  showDialog.value = true;
};

const closeDialog = () => {
  showDialog.value = false;
  currentNotification.value = {
    notificationType: '',
    title: '',
    content: '',
    receiverType: '全体业主'
  };
};

const confirmSubmit = () => {
  if (!currentNotification.value.notificationType || !currentNotification.value.title || !currentNotification.value.content || !currentNotification.value.receiverType) {
    alert('请填写所有必填项！');
    return;
  }
  showDialog.value = false;
  showConfirmDialog.value = true;
};

const handleSubmit = async () => {
  try {
    // 获取token
    const token = localStorage.getItem('token');
    if (!token) {
      alert('请先登录');
      router.push('/login');
      return;
    }

    const user = JSON.parse(localStorage.getItem('user'));
    if (!user || !user.userId) {
         alert('无法获取用户信息，请重新登录！');
         return;
    }

    const requestData = {
      notificationType: currentNotification.value.notificationType,
      title: currentNotification.value.title.trim(),
      content: currentNotification.value.content.trim(),
      senderId: user.userId, // 使用当前登录用户的ID作为发送者ID
      receiverType: currentNotification.value.receiverType,
      // 如果 receiverType 是特定房间业主，需要添加 receiverId 字段
      // receiverId: currentNotification.value.receiverType === '特定房间业主' ? selectedHouseId : null,
    };

    const response = await api.post('/api/notifications', requestData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.data.code === 200) {
      alert('发布成功！');
      await fetchNotifications();
      showConfirmDialog.value = false;
      closeDialog();
    } else {
      alert('发布失败：' + response.data.msg);
      showConfirmDialog.value = false;
    }
  } catch (error) {
    console.error('发布失败:', error);
    console.error('错误详情:', error.response?.data);
    console.error('请求配置:', error.config);
    alert('发布失败：' + (error.response?.data?.msg || error.message));
    showConfirmDialog.value = false;
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
  fetchNotifications();
});
</script>

<style scoped>
.notification-management {
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

/* 针对 CommonTable 的样式 */
.notification-table :deep(.action-btn) {
  padding: 6px 12px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  margin-right: 8px;
}

.notification-table :deep(.action-btn:last-child) {
  margin-right: 0;
}

.notification-table :deep(.action-btn:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px var(--shadow-color);
}

/* 对话框样式 */
.notification-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  transition: border-color 0.3s ease;
}

.notification-dialog :deep(.el-dialog__title) {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.notification-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.notification-form :deep(.el-input__inner),
.notification-form :deep(.el-textarea__inner),
.notification-form :deep(.el-select .el-input__inner) {
  border-radius: 8px;
  border-color: var(--border-color);
  transition: border-color 0.3s ease;
}

.notification-form :deep(.el-input__inner:focus),
.notification-form :deep(.el-textarea__inner:focus),
.notification-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .notification-management {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
    margin-top: 16px;
  }
}

/* 暗色主题适配 */
.dark-theme .notification-management {
  background-color: var(--bg-color);
}

.dark-theme .content-wrapper {
  background-color: var(--card-bg);
  box-shadow: 0 4px 16px var(--shadow-color);
  border-color: var(--border-color);
}

.dark-theme .notification-table :deep(.action-btn:hover) {
  box-shadow: 0 4px 10px var(--shadow-color);
}

.dark-theme .notification-dialog :deep(.el-dialog__header) {
  border-bottom-color: var(--border-color);
}

.dark-theme .notification-dialog :deep(.el-dialog__title) {
  color: var(--text-color);
}

.dark-theme .notification-form :deep(.el-form-item__label) {
  color: var(--text-color);
}

.dark-theme .notification-form :deep(.el-input__inner),
.dark-theme .notification-form :deep(.el-textarea__inner),
.dark-theme .notification-form :deep(.el-select .el-input__inner) {
  border-color: var(--border-color);
}

.dark-theme .notification-form :deep(.el-input__inner:focus),
.dark-theme .notification-form :deep(.el-textarea__inner:focus),
.dark-theme .notification-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.4);
}
</style> 