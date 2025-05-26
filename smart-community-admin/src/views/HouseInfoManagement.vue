<template>
  <div class="house-info-management">
    <CommonHeader 
      title="房屋信息管理"
      description="管理小区房屋信息、充值、绑定用户等"
    >
      <template #extra-buttons>
        <el-button type="primary" class="add-btn" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加房屋
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
        :data="filteredHouses"
        :columns="columns"
        class="house-table"
      >
        <template #houseInfo="{ row }">
          <div>区域：{{ row.areaName }}</div>
          <div>楼栋：{{ row.buildingName }}</div>
          <div>单元：{{ row.unitName }}</div>
          <div>房屋：{{ row.name }}</div>
        </template>

        <template #priceInfo="{ row }">
          <div>单价：{{ row.price }}元/㎡</div>
          <div>面积：{{ row.size }}㎡</div>
        </template>

        <template #operation="{ row }">
          <el-button type="success" size="small" class="action-btn recharge-btn" @click="openRechargeDialog(row)">
            <el-icon><Money /></el-icon>
            充值
          </el-button>
          <el-button type="primary" size="small" class="action-btn bind-btn" @click="openBindDialog(row)">
            <el-icon><Link /></el-icon>
            绑定用户
          </el-button>
          <el-button type="warning" size="small" class="action-btn notify-btn" @click="openNotifyDialog(row)">
            <el-icon><Bell /></el-icon>
            发送通知
          </el-button>
          <el-button type="danger" size="small" class="action-btn extract-btn" @click="handleExtract(row)">
            <el-icon><Wallet /></el-icon>
            提取
          </el-button>
        </template>
      </CommonTable>
    </div>

    <!-- 充值对话框 -->
    <el-dialog
      v-model="showRechargeDialog"
      title="房屋充值"
      width="500px"
      class="house-dialog"
    >
      <el-form :model="rechargeForm" label-width="100px" class="house-form">
        <el-form-item label="充值金额">
          <el-input v-model="rechargeForm.amount" type="number" placeholder="请输入充值金额" />
        </el-form-item>
        <el-form-item label="充值渠道">
          <div class="fixed-value">线下</div>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="rechargeForm.paymentMethod" placeholder="请选择支付方式">
            <el-option label="微信" value="微信" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="银行卡" value="银行卡" />
            <el-option label="现金" value="现金" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeRechargeDialog">取消</el-button>
          <el-button type="primary" @click="handleRecharge">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 绑定用户对话框 -->
    <el-dialog
      v-model="showBindDialog"
      title="绑定用户"
      width="500px"
      class="house-dialog"
    >
      <el-form :model="bindForm" label-width="100px" class="house-form">
        <el-form-item label="选择用户">
          <el-select v-model="bindForm.userId" placeholder="请选择用户">
            <el-option
              v-for="user in users"
              :key="user.userId"
              :label="user.username"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关系类型">
          <el-select v-model="bindForm.relationType" placeholder="请选择关系类型">
            <el-option label="户主" value="户主" />
            <el-option label="成员" value="成员" />
            <el-option label="租客" value="租客" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeBindDialog">取消</el-button>
          <el-button type="primary" @click="handleBind">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 发送通知对话框 -->
    <el-dialog
      v-model="showNotifyDialog"
      title="发送通知"
      width="500px"
      class="house-dialog"
    >
      <el-form :model="notifyForm" label-width="100px" class="house-form">
        <el-form-item label="通知类型">
          <el-select v-model="notifyForm.type" placeholder="请选择通知类型">
            <el-option label="公告" value="公告" />
            <el-option label="报修通知" value="报修通知" />
            <el-option label="缴费通知" value="缴费通知" />
            <el-option label="活动通知" value="活动通知" />
            <el-option label="紧急通知" value="紧急通知" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="notifyForm.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="notifyForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入通知内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeNotifyDialog">取消</el-button>
          <el-button type="primary" @click="handleSendNotification">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Plus, Money, Link, Bell, Wallet } from '@element-plus/icons-vue';
import api from '../api';
import CommonHeader from '../components/CommonHeader.vue';
import CommonSearch from '../components/CommonSearch.vue';
import CommonTable from '../components/CommonTable.vue';

const router = useRouter();
const houses = ref([]);
const users = ref([]);
const searchKeyword = ref('');
const showRechargeDialog = ref(false);
const showBindDialog = ref(false);
const showNotifyDialog = ref(false);
const currentHouse = ref(null);

// 表单数据
const rechargeForm = ref({
  amount: '',
  paymentMethod: '微信'
});

const bindForm = ref({
  userId: '',
  relationType: '户主'
});

const notifyForm = ref({
  type: '公告',
  title: '',
  content: ''
});

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
    placeholder: '搜索区域/楼栋/单元/房屋名称'
  }
];

// 表格列配置
const columns = [
  { prop: 'houseId', label: '房屋ID', width: '100px' },
  { prop: 'houseInfo', label: '房屋信息', minWidth: '200px' },
  { prop: 'priceInfo', label: '价格信息', width: '150px' },
  { prop: 'balance', label: '余额', width: '120px' },
  { prop: 'operation', label: '操作', width: '300px', fixed: 'right' }
];

const filteredHouses = computed(() => {
  if (!searchKeyword.value) return houses.value;
  const keyword = searchKeyword.value.toLowerCase();
  return houses.value.filter(house => 
    house.areaName?.toLowerCase().includes(keyword) ||
    house.buildingName?.toLowerCase().includes(keyword) ||
    house.unitName?.toLowerCase().includes(keyword) ||
    house.name?.toLowerCase().includes(keyword)
  );
});

const fetchHouses = async () => {
  try {
    const response = await api.get('/api/houses/list');
    if (response.data.code === 200) {
      houses.value = response.data.data;
    }
  } catch (error) {
    console.error('获取房屋列表失败:', error);
    alert('获取房屋列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const fetchUsers = async () => {
  try {
    const response = await api.get('/api/admin/normal-users');
    if (response.data.code === 200) {
      users.value = response.data.data;
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    alert('获取用户列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const openRechargeDialog = (house) => {
  currentHouse.value = house;
  rechargeForm.value = {
    amount: '',
    paymentMethod: '微信'
  };
  showRechargeDialog.value = true;
};

const closeRechargeDialog = () => {
  showRechargeDialog.value = false;
  currentHouse.value = null;
  rechargeForm.value = {
    amount: '',
    paymentMethod: '微信'
  };
};

const openBindDialog = (house) => {
  currentHouse.value = house;
  bindForm.value = {
    userId: '',
    relationType: '户主'
  };
  showBindDialog.value = true;
};

const closeBindDialog = () => {
  showBindDialog.value = false;
  currentHouse.value = null;
  bindForm.value = {
    userId: '',
    relationType: '户主'
  };
};

const openNotifyDialog = (house) => {
  currentHouse.value = house;
  notifyForm.value = {
    type: '公告',
    title: '',
    content: ''
  };
  showNotifyDialog.value = true;
};

const closeNotifyDialog = () => {
  showNotifyDialog.value = false;
  currentHouse.value = null;
  notifyForm.value = {
    type: '公告',
    title: '',
    content: ''
  };
};

const handleRecharge = async () => {
  if (!rechargeForm.value.amount || parseFloat(rechargeForm.value.amount) <= 0) {
    alert('请输入有效的充值金额！');
    return;
  }

  if (!confirm(`确定要为房屋 ${currentHouse.value.name} 充值 ${rechargeForm.value.amount} 元吗？`)) {
    return;
  }

  try {
    const user = JSON.parse(localStorage.getItem('user'));
    const requestData = {
      houseId: currentHouse.value.houseId,
      userId: user.userId,
      rechargeAmount: parseFloat(rechargeForm.value.amount),
      rechargeChannel: '线下',
      paymentMethod: rechargeForm.value.paymentMethod,
      userRole: user.role
    };
    
    const response = await api.post('/api/houses/recharge', requestData);
    
    if (response.data.code === 200) {
      alert('充值成功！');
      await fetchHouses();
      closeRechargeDialog();
    } else {
      alert('充值失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('充值请求发生错误:', error);
    alert('充值失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleBind = async () => {
  if (!bindForm.value.userId) {
    alert('请选择用户！');
    return;
  }

  try {
    const response = await api.post('/api/user-houses/bind', {
      houseId: currentHouse.value.houseId,
      userId: bindForm.value.userId,
      relationType: bindForm.value.relationType
    });
    if (response.data.code === 200) {
      alert('绑定成功！');
      closeBindDialog();
    } else {
      alert(response.data.msg || '绑定失败');
    }
  } catch (error) {
    console.error('绑定失败:', error);
    const errorMsg = error.response?.data?.msg || error.message;
    if (errorMsg.includes('已经绑定过此房屋')) {
      alert('该用户已经绑定过此房屋，请勿重复绑定！');
    } else {
      alert('绑定失败：' + errorMsg);
    }
  }
};

const handleSendNotification = async () => {
  if (!notifyForm.value.type) {
    alert('请选择通知类型！');
    return;
  }
  if (!notifyForm.value.title.trim()) {
    alert('请输入通知标题！');
    return;
  }
  if (!notifyForm.value.content.trim()) {
    alert('请输入通知内容！');
    return;
  }
  if (!currentHouse.value?.houseId) {
    alert('房屋信息不完整！');
    return;
  }

  try {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user?.userId) {
      alert('用户信息不完整，请重新登录！');
      return;
    }

    const requestData = {
      notificationType: notifyForm.value.type,
      title: notifyForm.value.title.trim(),
      content: notifyForm.value.content.trim(),
      senderId: user.userId,
      receiverType: '特定房间业主',
      roomId: currentHouse.value.houseId
    };
    
    const response = await api.post('/api/notifications', requestData);
    
    if (response.data.code === 200) {
      alert('通知发送成功！');
      closeNotifyDialog();
    } else {
      alert('通知发送失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('发送通知失败:', error);
    alert('通知发送失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleExtract = async (house) => {
  const extractAmount = prompt(`请输入要提取的金额（当前余额：${house.balance} 元）：`);
  if (extractAmount === null || extractAmount === '') return;

  const amount = parseFloat(extractAmount);
  if (isNaN(amount) || amount <= 0) {
    alert('请输入有效的金额！');
    return;
  }

  if (amount > house.balance) {
    alert('提取金额不能大于当前余额！');
    return;
  }

  if (!confirm(`确定要从房屋 ${house.areaName}${house.buildingName}${house.unitName}${house.name} 提取 ${amount} 元吗？`)) {
    return;
  }

  try {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user?.userId) {
      alert('用户信息不完整，请重新登录！');
      return;
    }

    const response = await api.post('/api/house/extract', {
      houseId: house.houseId,
      userId: user.userId,
      extractAmount: amount
    });

    if (response.data.code === 200) {
      alert('提取成功！');
      fetchHouses();
    } else {
      alert('提取失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('提取失败:', error);
    alert('提取失败：' + (error.response?.data?.msg || error.message));
  }
};

// CommonSearch 的搜索事件处理
const handleSearch = (formData) => {
  searchForm.value = formData;
  searchKeyword.value = formData.keyword;
};

// CommonSearch 的重置事件处理
const handleReset = () => {
  searchForm.value.keyword = '';
  searchKeyword.value = '';
};

onMounted(() => {
  fetchHouses();
  fetchUsers();
});
</script>

<style scoped>
.house-info-management {
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
.house-table :deep(.action-btn) {
  padding: 6px 12px;
  font-weight: 500;
  border-radius: 6px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  margin-right: 8px;
}

.house-table :deep(.action-btn:last-child) {
  margin-right: 0;
}

.house-table :deep(.action-btn:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px var(--shadow-color);
}

.house-table :deep(.recharge-btn) {
  background-color: #2ecc71;
  border-color: #2ecc71;
  color: white;
}

.house-table :deep(.recharge-btn:hover) {
  background-color: #27ae60;
  border-color: #27ae60;
}

.house-table :deep(.bind-btn) {
  background-color: #3498db;
  border-color: #3498db;
  color: white;
}

.house-table :deep(.bind-btn:hover) {
  background-color: #2980b9;
  border-color: #2980b9;
}

.house-table :deep(.notify-btn) {
  background-color: #9b59b6;
  border-color: #9b59b6;
  color: white;
}

.house-table :deep(.notify-btn:hover) {
  background-color: #8e44ad;
  border-color: #8e44ad;
}

.house-table :deep(.extract-btn) {
  background-color: #f39c12;
  border-color: #f39c12;
  color: white;
}

.house-table :deep(.extract-btn:hover) {
  background-color: #e67e22;
  border-color: #e67e22;
}

/* 对话框样式 */
.house-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  transition: border-color 0.3s ease;
}

.house-dialog :deep(.el-dialog__title) {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.house-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.house-form :deep(.el-input__inner),
.house-form :deep(.el-select .el-input__inner) {
  border-radius: 8px;
  border-color: var(--border-color);
  transition: border-color 0.3s ease;
}

.house-form :deep(.el-input__inner:focus),
.house-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.fixed-value {
  padding: 10px;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--text-color);
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .house-info-management {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
    margin-top: 16px;
  }
}

/* 暗色主题适配 */
.dark-theme .house-info-management {
  background-color: var(--bg-color);
}

.dark-theme .content-wrapper {
  background-color: var(--card-bg);
  box-shadow: 0 4px 16px var(--shadow-color);
  border-color: var(--border-color);
}

.dark-theme .house-table :deep(.action-btn:hover) {
  box-shadow: 0 4px 10px var(--shadow-color);
}

.dark-theme .house-dialog :deep(.el-dialog__header) {
  border-bottom-color: var(--border-color);
}

.dark-theme .house-dialog :deep(.el-dialog__title) {
  color: var(--text-color);
}

.dark-theme .house-form :deep(.el-form-item__label) {
  color: var(--text-color);
}

.dark-theme .house-form :deep(.el-input__inner),
.dark-theme .house-form :deep(.el-select .el-input__inner) {
  border-color: var(--border-color);
}

.dark-theme .house-form :deep(.el-input__inner:focus),
.dark-theme .house-form :deep(.el-select .el-input__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.4);
}

.dark-theme .fixed-value {
  background-color: var(--bg-color);
  border-color: var(--border-color);
  color: var(--text-color);
}
</style> 