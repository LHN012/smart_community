<template>
  <div class="transaction-records">
    <div class="header">
      <h2>交易记录管理</h2>
      <div class="header-buttons">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="search-bar">
      <div class="search-controls">
        <input 
          v-model="searchKeyword" 
          placeholder="搜索房屋ID/用户ID/交易金额" 
          @input="handleSearch"
        />
        <select v-model="recordType" @change="handleRecordTypeChange">
          <option value="all">全部记录</option>
          <option value="payment">缴费记录</option>
          <option value="recharge">充值记录</option>
        </select>
      </div>
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>记录ID</th>
            <th>房屋信息</th>
            <th>用户信息</th>
            <th>交易类型</th>
            <th>交易金额</th>
            <th>交易前余额</th>
            <th>交易后余额</th>
            <th>交易时间</th>
            <th>支付方式</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="record in filteredRecords" :key="record.id">
            <td>{{ record.id }}</td>
            <td>
              <div>房屋ID：{{ record.houseId }}</div>
              <div>房屋名称：{{ record.houseName }}</div>
            </td>
            <td>
              <div>用户ID：{{ record.userId }}</div>
              <div>用户名：{{ record.username }}</div>
            </td>
            <td>{{ record.type === 'payment' ? '缴费' : '充值' }}</td>
            <td>{{ record.amount }}元</td>
            <td>{{ record.balanceBefore }}元</td>
            <td>{{ record.balanceAfter }}元</td>
            <td>{{ formatDate(record.transactionTime) }}</td>
            <td>{{ record.paymentMethod }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api';

const router = useRouter();
const records = ref([]);
const searchKeyword = ref('');
const recordType = ref('all');

const filteredRecords = computed(() => {
  let filtered = records.value;
  
  // 根据记录类型过滤
  if (recordType.value !== 'all') {
    filtered = filtered.filter(record => record.type === recordType.value);
  }
  
  // 根据搜索关键词过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(record => 
      record.houseId.toString().includes(keyword) ||
      record.userId.toString().includes(keyword) ||
      record.amount.toString().includes(keyword)
    );
  }
  
  return filtered;
});

const fetchRecords = async () => {
  try {
    const response = await api.get('/api/transaction-records/list');
    if (response.data.code === 200) {
      records.value = response.data.data;
    }
  } catch (error) {
    console.error('获取交易记录失败:', error);
    alert('获取交易记录失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleSearch = () => {
  // 搜索功能已通过计算属性实现
};

const handleRecordTypeChange = () => {
  // 记录类型变更时自动触发过滤
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    router.push('/login');
  }
};

onMounted(() => {
  fetchRecords();
});
</script>

<style scoped>
.transaction-records {
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

.search-controls {
  display: flex;
  gap: 15px;
  align-items: center;
}

.search-controls input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-controls select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
  min-width: 120px;
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
</style> 