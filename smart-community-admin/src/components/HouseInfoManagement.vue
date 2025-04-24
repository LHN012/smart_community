<template>
  <div class="house-info-management">
    <div class="header">
      <h2>房屋信息管理</h2>
      <div class="header-buttons">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        placeholder="搜索区域/楼栋/单元/房屋名称" 
        @input="handleSearch"
      />
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>房屋ID</th>
            <th>房屋信息</th>
            <th>价格信息</th>
            <th>余额</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="house in filteredHouses" :key="house.houseId">
            <td>{{ house.houseId }}</td>
            <td>
              <div>区域：{{ house.areaName }}</div>
              <div>楼栋：{{ house.buildingName }}</div>
              <div>单元：{{ house.unitName }}</div>
              <div>房屋：{{ house.name }}</div>
            </td>
            <td>
              <div>单价：{{ house.price }}元/㎡</div>
              <div>面积：{{ house.size }}㎡</div>
            </td>
            <td>{{ house.balance }}元</td>
            <td>
              <button class="recharge-btn" @click="openRechargeDialog(house)">充值</button>
              <button class="bind-btn" @click="openBindDialog(house)">绑定用户</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 充值对话框 -->
    <div v-if="showRechargeDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>房屋充值</h3>
        <div class="form-group">
          <label>充值金额</label>
          <input type="number" v-model="rechargeAmount" placeholder="请输入充值金额" />
        </div>
        <div class="form-group">
          <label>充值渠道</label>
          <div class="fixed-value">线下</div>
        </div>
        <div class="form-group">
          <label>支付方式</label>
          <select v-model="paymentMethod">
            <option value="微信">微信</option>
            <option value="支付宝">支付宝</option>
            <option value="银行卡">银行卡</option>
            <option value="现金">现金</option>
          </select>
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeRechargeDialog">取消</button>
          <button class="submit-btn" @click="handleRecharge">确定</button>
        </div>
      </div>
    </div>

    <!-- 绑定用户对话框 -->
    <div v-if="showBindDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>绑定用户</h3>
        <div class="form-group">
          <label>选择用户</label>
          <select v-model="selectedUserId">
            <option v-for="user in users" :key="user.userId" :value="user.userId">
              {{ user.username }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>关系类型</label>
          <select v-model="relationType">
            <option value="户主">户主</option>
            <option value="成员">成员</option>
            <option value="租客">租客</option>
          </select>
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeBindDialog">取消</button>
          <button class="submit-btn" @click="handleBind">确定</button>
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
const houses = ref([]);
const users = ref([]);
const searchKeyword = ref('');
const showRechargeDialog = ref(false);
const showBindDialog = ref(false);
const currentHouse = ref(null);
const rechargeAmount = ref('');
const rechargeChannel = ref('线下');
const paymentMethod = ref('微信');
const selectedUserId = ref('');
const relationType = ref('户主');

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
  rechargeAmount.value = '';
  rechargeChannel.value = '线下';
  paymentMethod.value = '微信';
  showRechargeDialog.value = true;
};

const closeRechargeDialog = () => {
  showRechargeDialog.value = false;
  currentHouse.value = null;
  rechargeAmount.value = '';
};

const openBindDialog = (house) => {
  currentHouse.value = house;
  selectedUserId.value = '';
  relationType.value = '户主';
  showBindDialog.value = true;
};

const closeBindDialog = () => {
  showBindDialog.value = false;
  currentHouse.value = null;
  selectedUserId.value = '';
};

const handleRecharge = async () => {
  if (!rechargeAmount.value || parseFloat(rechargeAmount.value) <= 0) {
    alert('请输入有效的充值金额！');
    return;
  }

  if (!confirm(`确定要为房屋 ${currentHouse.value.name} 充值 ${rechargeAmount.value} 元吗？`)) {
    return;
  }

  try {
    const user = JSON.parse(localStorage.getItem('user'));
    const requestData = {
      houseId: currentHouse.value.houseId,
      userId: user.userId,
      rechargeAmount: parseFloat(rechargeAmount.value),
      rechargeChannel: rechargeChannel.value,
      paymentMethod: paymentMethod.value,
      userRole: user.role
    };
    
    console.log('发送充值请求，参数：', requestData);
    
    const response = await api.post('/api/houses/recharge', requestData);
    console.log('充值响应：', response);
    
    if (response.data.code === 200) {
      alert('充值成功！');
      console.log('开始刷新房屋列表');
      await fetchHouses();
      console.log('房屋列表刷新完成');
      closeRechargeDialog();
    } else {
      console.error('充值失败，响应码：', response.data.code);
      alert('充值失败：' + response.data.msg);
    }
  } catch (error) {
    console.error('充值请求发生错误:', error);
    console.error('错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status
    });
    alert('充值失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleBind = async () => {
  if (!selectedUserId.value) {
    alert('请选择用户！');
    return;
  }

  try {
    const response = await api.post('/api/user-houses/bind', {
      houseId: currentHouse.value.houseId,
      userId: selectedUserId.value,
      relationType: relationType.value
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
  fetchHouses();
  fetchUsers();
});
</script>

<style scoped>
.house-info-management {
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

.recharge-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #2ecc71;
  color: white;
}

.recharge-btn:hover {
  background-color: #27ae60;
}

.bind-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #3498db;
  color: white;
}

.bind-btn:hover {
  background-color: #2980b9;
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

.logout-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #e74c3c;
  color: white;
}

.logout-btn:hover {
  background-color: #c0392b;
}

.fixed-value {
  padding: 10px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: #666;
}
</style> 