<template>
  <div class="notification-management">
    <div class="header">
      <h2>通知管理</h2>
      <div class="header-buttons">
        <button class="add-btn" @click="showAddDialog">发布通知</button>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        placeholder="搜索通知标题/内容" 
        @input="handleSearch"
      />
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>通知ID</th>
            <th>通知类型</th>
            <th>标题</th>
            <th>内容</th>
            <th>发送者</th>
            <th>接收类型</th>
            <th>接收对象</th>
            <th>发送时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="notification in filteredNotifications" :key="notification.id">
            <td>{{ notification.id }}</td>
            <td>{{ notification.notification_type }}</td>
            <td>{{ notification.title }}</td>
            <td>{{ notification.content }}</td>
            <td>{{ notification.sender_name }}</td>
            <td>{{ getReceiverTypeName(notification.receiverType) }}</td>
            <td>{{ getReceiverInfo(notification) }}</td>
            <td>{{ formatDate(notification.sendTime) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加通知对话框 -->
    <div v-if="showDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>发布通知</h3>
        <div class="form-group">
          <label>通知类型</label>
          <select v-model="currentNotification.notificationType">
            <option value="">请选择通知类型</option>
            <option v-for="option in notificationTypeOptions" :key="option.value" :value="option.value">{{ option.label }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>标题</label>
          <input v-model="currentNotification.title" />
        </div>
        <div class="form-group">
          <label>内容</label>
          <textarea v-model="currentNotification.content" rows="4"></textarea>
        </div>
        <div class="form-group">
          <label>接收对象类型</label>
          <select v-model="currentNotification.receiverType" @change="handleReceiverTypeChange">
            <option value="">请选择接收对象类型</option>
            <option value="全体业主">全体业主</option>
            <option value="特定房间业主">特定房间业主</option>
            <option value="物业员工">物业员工</option>
          </select>
        </div>
        <template v-if="currentNotification.receiverType === '特定房间业主'">
          <div class="form-group">
            <label>区域</label>
            <select v-model="currentNotification.areaId" @change="handleAreaChange">
              <option value="">请选择区域</option>
              <option v-for="area in areas" :key="area.id" :value="area.id">{{ area.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>楼栋</label>
            <select v-model="currentNotification.buildingId" @change="handleBuildingChange">
              <option value="">请选择楼栋</option>
              <option v-for="building in buildings" :key="building.id" :value="building.id">{{ building.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>单元</label>
            <select v-model="currentNotification.unitId" @change="handleUnitChange">
              <option value="">请选择单元</option>
              <option v-for="unit in units" :key="unit.id" :value="unit.id">{{ unit.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>房屋</label>
            <select v-model="currentNotification.roomId">
              <option value="">请选择房屋</option>
              <option v-for="house in houses" :key="house.id" :value="house.id">{{ house.name }}</option>
            </select>
          </div>
        </template>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeDialog">取消</button>
          <button class="submit-btn" @click="confirmSubmit">确定</button>
        </div>
      </div>
    </div>

    <!-- 确认对话框 -->
    <div v-if="showConfirmDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>确认发布</h3>
        <p>确定要发布这条通知吗？</p>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="showConfirmDialog = false">取消</button>
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
import { ElTable, ElTableColumn } from 'element-plus';
import 'element-plus/dist/index.css';

const router = useRouter();
const notifications = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const showConfirmDialog = ref(false);
const areas = ref([]);
const buildings = ref([]);
const units = ref([]);
const houses = ref([]);

const currentNotification = ref({
  notificationType: '',
  title: '',
  content: '',
  receiverType: '',
  areaId: null,
  buildingId: null,
  unitId: null,
  roomId: null
});

const notificationTypeOptions = [
  { label: '公告', value: '公告' },
  { label: '报修通知', value: '报修通知' },
  { label: '缴费通知', value: '缴费通知' },
  { label: '活动通知', value: '活动通知' },
  { label: '紧急通知', value: '紧急通知' },
  { label: '其他', value: '其他' }
]

const receiverTypeOptions = [
  { label: '全体业主', value: '全体业主' },
  { label: '特定房间业主', value: '特定房间业主' },
  { label: '物业员工', value: '物业员工' }
]

const filteredNotifications = computed(() => {
  if (!searchKeyword.value) return notifications.value;
  const keyword = searchKeyword.value.toLowerCase();
  return notifications.value.filter(notification =>
    notification.title.toLowerCase().includes(keyword) ||
    notification.content.toLowerCase().includes(keyword)
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
  return typeMap[type] || type;
};

const getReceiverInfo = (notification) => {
  if (notification.receiverType === '特定房间业主') {
    const info = [
      notification.areaName,
      notification.buildingName,
      notification.unitName,
      notification.roomName
    ].filter(Boolean).join(' ');
    return info || '未找到房屋信息';
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
      const notificationsData = response.data.data;

      // 获取所有特定房间的通知
      const specificRoomNotifications = notificationsData.filter(
        notification => notification.receiver_type === '特定房间业主' || notification.receiverType === '特定房间业主'
      );

      // 获取所有特定房间的ID
      const roomIds = specificRoomNotifications.map(notification => {
        const roomId = notification.receiver_id;
        return roomId;
      }).filter(id => id);

      // 批量获取房屋信息
      let roomDetails = {};
      if (roomIds.length > 0) {
        try {
          // 获取房屋基本信息
          const houseResponse = await api.get(`/api/houses/list`);
          if (houseResponse.data.code === 200) {
            const houses = houseResponse.data.data;
            const house = houses.find(h => h.houseId === roomIds[0]);
            if (house) {
              // 获取区域信息
              const areaResponse = await api.get('/api/house/area/list');
              const areas = areaResponse.data.code === 200 ? areaResponse.data.data : [];
              const area = areas.find(a => a.areaId === house.areaId);

              // 获取楼栋信息
              const buildingResponse = await api.get('/api/house/building/list');
              const buildings = buildingResponse.data.code === 200 ? buildingResponse.data.data : [];
              const building = buildings.find(b => b.buildingId === house.buildingId);

              // 获取单元信息
              const unitResponse = await api.get('/api/house/unit/list');
              const units = unitResponse.data.code === 200 ? unitResponse.data.data : [];
              const unit = units.find(u => u.unitId === house.unitId);

              roomDetails[roomIds[0]] = {
                areaName: area?.name || '',
                buildingName: building?.name || '',
                unitName: unit?.name || '',
                roomName: house.name || ''
              };
            }
          }
        } catch (error) {
          console.error('获取房屋信息失败:', error);
          console.error('错误详情:', error.response?.data);
          console.error('请求配置:', error.config);
        }
      }

      // 处理通知数据
      notifications.value = notificationsData.map(notification => {
        const roomId = notification.receiver_id;
        const roomInfo = roomDetails[roomId];

        return {
          ...notification,
          receiverType: notification.receiver_type || notification.receiverType,
          sendTime: notification.send_time || notification.sendTime,
          areaName: roomInfo?.areaName || '',
          buildingName: roomInfo?.buildingName || '',
          unitName: roomInfo?.unitName || '',
          roomName: roomInfo?.roomName || ''
        };
      });
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
    receiverType: '',
    areaId: null,
    buildingId: null,
    unitId: null,
    roomId: null
  };
  showDialog.value = true;
};

const closeDialog = () => {
  showDialog.value = false;
  currentNotification.value = {
    notificationType: '',
    title: '',
    content: '',
    receiverType: '',
    areaId: null,
    buildingId: null,
    unitId: null,
    roomId: null
  };
};

const confirmSubmit = () => {
  if (!currentNotification.value.title || !currentNotification.value.content) {
    alert('请填写必要信息！');
    return;
  }
  showDialog.value = false;
  showConfirmDialog.value = true;
};

const handleSubmit = async () => {
  try {
    // 打印要发送的数据
    console.log('准备发送的数据:', currentNotification.value);

    // 获取token
    const token = localStorage.getItem('token');
    console.log('当前token:', token);

    if (!token) {
      alert('请先登录');
      router.push('/login');
      return;
    }

    const response = await api.post('/api/notifications', {
      notificationType: currentNotification.value.notificationType,
      title: currentNotification.value.title,
      content: currentNotification.value.content,
      receiverType: currentNotification.value.receiverType,
      roomId: currentNotification.value.roomId
    }, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    console.log('服务器响应:', response.data);

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

const handleSearch = () => {
  // 搜索功能已通过计算属性实现
};

const handleLogout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  router.push('/login');
};

const handleReceiverTypeChange = () => {
  if (currentNotification.value.receiverType === '特定房间业主') {
    loadAreas();
  } else {
    resetHouseSelection();
  }
};

const loadAreas = async () => {
  try {
    const response = await api.get('/api/house/areas');
    if (response.data.code === 200) {
      areas.value = response.data.data;
    }
  } catch (error) {
    console.error('获取区域列表失败:', error);
    alert('获取区域列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleAreaChange = async (areaId) => {
  if (areaId) {
    try {
      const response = await api.get(`/api/house/buildings/${areaId}`);
      if (response.data.code === 200) {
        buildings.value = response.data.data;
        currentNotification.value.buildingId = null;
        currentNotification.value.unitId = null;
        currentNotification.value.roomId = null;
      }
    } catch (error) {
      console.error('获取楼栋列表失败:', error);
      alert('获取楼栋列表失败：' + (error.response?.data?.msg || error.message));
    }
  } else {
    buildings.value = [];
    units.value = [];
    houses.value = [];
  }
};

const handleBuildingChange = async (buildingId) => {
  if (buildingId) {
    try {
      const response = await api.get(`/api/house/units/${buildingId}`);
      if (response.data.code === 200) {
        units.value = response.data.data;
        currentNotification.value.unitId = null;
        currentNotification.value.roomId = null;
      }
    } catch (error) {
      console.error('获取单元列表失败:', error);
      alert('获取单元列表失败：' + (error.response?.data?.msg || error.message));
    }
  } else {
    units.value = [];
    houses.value = [];
  }
};

const handleUnitChange = async (unitId) => {
  if (unitId) {
    try {
      const response = await api.get(`/api/house/houses/${unitId}`);
      if (response.data.code === 200) {
        houses.value = response.data.data;
        currentNotification.value.roomId = null;
      }
    } catch (error) {
      console.error('获取房屋列表失败:', error);
      alert('获取房屋列表失败：' + (error.response?.data?.msg || error.message));
    }
  } else {
    houses.value = [];
  }
};

const resetHouseSelection = () => {
  currentNotification.value.areaId = null;
  currentNotification.value.buildingId = null;
  currentNotification.value.unitId = null;
  currentNotification.value.roomId = null;
  areas.value = [];
  buildings.value = [];
  units.value = [];
  houses.value = [];
};

onMounted(() => {
  fetchNotifications();
  loadAreas();
});
</script>

<style scoped>
.notification-management {
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
.form-group textarea {
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