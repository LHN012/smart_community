<template>
  <div class="transaction-records-container">
    <CommonHeader
      title="缴费/充值记录管理"
      description="查看和管理小区的缴费和充值记录"
    />

    <div class="content-wrapper">
      <CommonSearch
        :searchItems="searchItems"
        :initialValues="searchForm"
        @search="handleSearch"
        @reset="handleReset"
      />

      <div class="tabs">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="缴费记录" name="payment">
            <CommonTable
              :data="filteredPaymentRecords"
              :columns="paymentColumns"
              class="payment-table"
              :loading="loading"
            >
              <template #houseInfo="{ row }">
                <div>区域：{{ row.areaName }}</div>
                <div>楼栋：{{ row.buildingName }}</div>
                <div>单元：{{ row.unitName }}</div>
                <div>房屋：{{ row.houseName }}</div>
              </template>
              <template #userInfo="{ row }">
                <div>用户名：{{ row.username }}</div>
              </template>
              <template #paymentType="{ row }">
                 <el-tag :type="getPaymentTypeTag(row.type)">
                   {{ row.type }}
                 </el-tag>
              </template>
               <template #amount="{ row }">
                 <span class="amount">¥{{ row.deductedAmount }}</span>
               </template>
               <template #balanceBefore="{ row }">
                 <span class="amount">¥{{ row.balanceBefore }}</span>
               </template>
               <template #balanceAfter="{ row }">
                 <span class="amount">¥{{ row.balanceAfter }}</span>
               </template>
              <template #createDate="{ row }">
                 {{ formatDate(row.createDate) }}
              </template>
            </CommonTable>
          </el-tab-pane>

          <el-tab-pane label="充值记录" name="recharge">
             <CommonTable
               :data="filteredRechargeRecords"
               :columns="rechargeColumns"
               class="recharge-table"
               :loading="loading"
             >
               <template #houseInfo="{ row }">
                 <div>区域：{{ row.areaName }}</div>
                 <div>楼栋：{{ row.buildingName }}</div>
                 <div>单元：{{ row.unitName }}</div>
                 <div>房屋：{{ row.houseName }}</div>
               </template>
               <template #userInfo="{ row }">
                 <div>用户名：{{ row.username }}</div>
               </template>
                <template #balanceBefore="{ row }">
                  <span class="amount">¥{{ row.balanceBefore }}</span>
                </template>
                <template #balanceAfter="{ row }">
                  <span class="amount">¥{{ row.balanceAfter }}</span>
                </template>
                <template #rechargeAmount="{ row }">
                  <span class="amount">¥{{ row.rechargeAmount }}</span>
                </template>
               <template #rechargeChannel="{ row }">
                 <el-tag :type="row.rechargeChannel === '线上' ? 'success' : 'warning'">
                   {{ row.rechargeChannel }}
                 </el-tag>
               </template>
               <template #paymentMethod="{ row }">
                 <el-tag type="info">{{ row.paymentMethod }}</el-tag>
               </template>
               <template #rechargeTime="{ row }">
                 {{ formatDate(row.rechargeTime) }}
               </template>
             </CommonTable>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { ElMessage, ElTag } from 'element-plus';
import CommonHeader from '../components/CommonHeader.vue';
import CommonSearch from '../components/CommonSearch.vue';
import CommonTable from '../components/CommonTable.vue';

const activeTab = ref('payment');
const paymentRecords = ref([]);
const rechargeRecords = ref([]);
const loading = ref(false);
const searchKeyword = ref('');

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
    placeholder: '搜索房屋/用户/金额/类型'
  }
];

// 缴费记录表格列配置
const paymentColumns = [
  { prop: 'recordId', label: '记录ID', width: '80px' },
  { prop: 'houseInfo', label: '房屋信息', minWidth: '180px' },
  { prop: 'userInfo', label: '用户信息', width: '120px' },
  { prop: 'paymentType', label: '缴费类型', width: '100px' },
  { prop: 'amount', label: '扣除金额', width: '100px' },
  { prop: 'balanceBefore', label: '扣除前余额', width: '120px' },
  { prop: 'balanceAfter', label: '扣除后余额', width: '120px' },
  { prop: 'createDate', label: '创建日期', width: '160px' }
];

// 充值记录表格列配置
const rechargeColumns = [
  { prop: 'id', label: '记录ID', width: '80px' },
  { prop: 'houseInfo', label: '房屋信息', minWidth: '180px' },
  { prop: 'userInfo', label: '用户信息', width: '120px' },
  { prop: 'balanceBefore', label: '充值前余额', width: '120px' },
  { prop: 'balanceAfter', label: '充值后余额', width: '120px' },
  { prop: 'rechargeAmount', label: '充值金额', width: '100px' },
  { prop: 'rechargeChannel', label: '充值渠道', width: '100px' },
  { prop: 'paymentMethod', label: '支付方式', width: '100px' },
  { prop: 'rechargeTime', label: '充值时间', width: '160px' }
];

const filteredPaymentRecords = computed(() => {
  if (!searchKeyword.value) return paymentRecords.value;
  const keyword = searchKeyword.value.toLowerCase();
  return paymentRecords.value.filter(record =>
    (record.areaName && record.areaName.toLowerCase().includes(keyword)) ||
    (record.buildingName && record.buildingName.toLowerCase().includes(keyword)) ||
    (record.unitName && record.unitName.toLowerCase().includes(keyword)) ||
    (record.houseName && record.houseName.toLowerCase().includes(keyword)) ||
    (record.username && record.username.toLowerCase().includes(keyword)) ||
    (record.type && record.type.toLowerCase().includes(keyword)) ||
    (record.deductedAmount != null && String(record.deductedAmount).includes(keyword)) ||
    (record.balanceBefore != null && String(record.balanceBefore).includes(keyword)) ||
    (record.balanceAfter != null && String(record.balanceAfter).includes(keyword)) ||
     (record.recordId != null && String(record.recordId).includes(keyword))
  );
});

const filteredRechargeRecords = computed(() => {
  if (!searchKeyword.value) return rechargeRecords.value;
  const keyword = searchKeyword.value.toLowerCase();
   return rechargeRecords.value.filter(record =>
    (record.areaName && record.areaName.toLowerCase().includes(keyword)) ||
    (record.buildingName && record.buildingName.toLowerCase().includes(keyword)) ||
    (record.unitName && record.unitName.toLowerCase().includes(keyword)) ||
    (record.houseName && record.houseName.toLowerCase().includes(keyword)) ||
    (record.username && record.username.toLowerCase().includes(keyword)) ||
    (record.rechargeChannel && record.rechargeChannel.toLowerCase().includes(keyword)) ||
    (record.paymentMethod && record.paymentMethod.toLowerCase().includes(keyword)) ||
    (record.rechargeAmount != null && String(record.rechargeAmount).includes(keyword)) ||
    (record.balanceBefore != null && String(record.balanceBefore).includes(keyword)) ||
    (record.balanceAfter != null && String(record.balanceAfter).includes(keyword)) ||
    (record.id != null && String(record.id).includes(keyword))
  );
});

const fetchPaymentRecords = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/admin/payment-records');

    if (response.data && Array.isArray(response.data)) {
      const paymentRecordsData = response.data;

      // 提取所有相关的 houseId 和 userId
      const houseIds = [...new Set(paymentRecordsData.map(record => record.houseId).filter(id => id != null))];
      const userIds = [...new Set(paymentRecordsData.map(record => record.userId).filter(id => id != null))];

      // 批量获取房屋信息和用户信息 (获取所有用户)
      const [housesResponse, normalUsersResponse, adminUsersResponse] = await Promise.all([
        houseIds.length > 0 ? axios.get('/api/houses/list') : Promise.resolve({ data: { code: 200, data: [] } }),
        userIds.length > 0 ? axios.get('/api/admin/normal-users') : Promise.resolve({ data: { code: 200, data: [] } }),
        userIds.length > 0 ? axios.get('/api/admin/list') : Promise.resolve({ data: { code: 200, data: [] } })
      ]);

      const housesMap = {};
      if (housesResponse.data.code === 200 && Array.isArray(housesResponse.data.data)) {
        housesResponse.data.data.forEach(house => {
          housesMap[house.houseId] = house;
        });
      }

      const usersMap = {};
      const allUsers = [];
      if (normalUsersResponse.data.code === 200 && Array.isArray(normalUsersResponse.data.data)) {
          allUsers.push(...normalUsersResponse.data.data);
      }
      if (adminUsersResponse.data.code === 200 && Array.isArray(adminUsersResponse.data.data)) {
          allUsers.push(...adminUsersResponse.data.data);
      }

      allUsers.forEach(user => {
          usersMap[user.userId] = user;
      });

      // 组合数据
      paymentRecords.value = paymentRecordsData.map(record => {
        const houseInfo = housesMap[record.houseId];
        const userInfo = usersMap[record.userId];
        return {
          ...record,
          areaName: houseInfo?.areaName || '未知区域',
          buildingName: houseInfo?.buildingName || '未知楼栋',
          unitName: houseInfo?.unitName || '未知单元',
          houseName: houseInfo?.name || '未知房屋',
          username: userInfo?.username || '未知用户'
        };
      });

    } else {
       paymentRecords.value = [];
       ElMessage.warning('获取缴费记录列表数据为空');
    }

  } catch (error) {
    console.error('获取缴费记录失败:', error);
    ElMessage.error('获取缴费记录失败: ' + (error.response?.data?.message || error.message));
    paymentRecords.value = [];
  } finally {
    loading.value = false;
  }
};

const fetchRechargeRecords = async () => {
  loading.value = true;
  try {
    const response = await axios.get('/api/admin/recharge-records');

    if (response.data && Array.isArray(response.data)) {
      const rechargeRecordsData = response.data;

       // 提取所有相关的 houseId 和 userId
      const houseIds = [...new Set(rechargeRecordsData.map(record => record.houseId).filter(id => id != null))];
      const userIds = [...new Set(rechargeRecordsData.map(record => record.userId).filter(id => id != null))];

      // 批量获取房屋信息和用户信息 (获取所有用户)
      const [housesResponse, normalUsersResponse, adminUsersResponse] = await Promise.all([
        houseIds.length > 0 ? axios.get('/api/houses/list') : Promise.resolve({ data: { code: 200, data: [] } }),
        userIds.length > 0 ? axios.get('/api/admin/normal-users') : Promise.resolve({ data: { code: 200, data: [] } }),
        userIds.length > 0 ? axios.get('/api/admin/list') : Promise.resolve({ data: { code: 200, data: [] } })
      ]);

      const housesMap = {};
      if (housesResponse.data.code === 200 && Array.isArray(housesResponse.data.data)) {
        housesResponse.data.data.forEach(house => {
          housesMap[house.houseId] = house;
        });
      }

      const usersMap = {};
      const allUsers = [];
        if (normalUsersResponse.data.code === 200 && Array.isArray(normalUsersResponse.data.data)) {
            allUsers.push(...normalUsersResponse.data.data);
        }
        if (adminUsersResponse.data.code === 200 && Array.isArray(adminUsersResponse.data.data)) {
            allUsers.push(...adminUsersResponse.data.data);
        }

        allUsers.forEach(user => {
            usersMap[user.userId] = user;
        });

      // 组合数据
      rechargeRecords.value = rechargeRecordsData.map(record => {
         const houseInfo = housesMap[record.houseId];
        const userInfo = usersMap[record.userId];
        return {
          ...record,
          areaName: houseInfo?.areaName || '未知区域',
          buildingName: houseInfo?.buildingName || '未知楼栋',
          unitName: houseInfo?.unitName || '未知单元',
          houseName: houseInfo?.name || '未知房屋',
          username: userInfo?.username || '未知用户'
        };
      });

    } else {
      rechargeRecords.value = [];
      ElMessage.warning('获取充值记录列表数据为空');
    }

  } catch (error) {
    console.error('获取充值记录失败:', error);
    ElMessage.error('获取充值记录失败: ' + (error.response?.data?.message || error.message));
    rechargeRecords.value = [];
  } finally {
    loading.value = false;
  }
};

const getPaymentTypeTag = (type) => {
  const typeMap = {
    '水': 'primary',
    '电': 'success',
    '燃气': 'warning',
    '物业': 'danger',
    '提出余额': 'info'
  };
  return typeMap[type] || 'info';
};

const formatDate = (date) => {
  if (!date) return '';
  const d = new Date(date);
   if (isNaN(d.getTime())) {
    // Handle invalid date string
    console.error('Invalid date string:', date);
    return date; // Or return a default string like 'Invalid Date'
  }
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

const handleSearch = (formData) => {
  searchKeyword.value = formData.keyword;
};

const handleReset = () => {
  searchKeyword.value = '';
  searchForm.value.keyword = '';
};

onMounted(() => {
  fetchPaymentRecords();
  fetchRechargeRecords();
});
</script>

<style scoped>
.transaction-records-container {
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

.tabs {
  margin-top: 20px; /* Keep some space below search */
}

.amount {
  color: var(--primary-color);
  font-weight: bold;
}

/* Element Plus Tabs and Table styles within the component */
.transaction-records-container :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color);
}

.transaction-records-container :deep(.el-tabs__item.is-active) {
    color: var(--primary-color);
}

.transaction-records-container :deep(.el-tabs__header) {
    border-bottom: 1px solid var(--border-color);
}

.transaction-records-container :deep(.el-tabs__nav) {
    border: none !important;
}

.transaction-records-container :deep(.el-tabs__item) {
    border-left: 1px solid var(--border-color) !important;
}

.transaction-records-container :deep(.el-tabs__item:first-child) {
     border-left: none !important;
}

.transaction-records-container :deep(.el-tabs__item:last-child) {
    border-right: 1px solid var(--border-color) !important;
}

.transaction-records-container :deep(.el-tabs__nav-wrap) {
    margin-bottom: 0;
}


/* Common Table styles within tabs */
.transaction-records-container :deep(.common-table) {
   margin-top: 15px;
}

/* ElTag styles within CommonTable */
.transaction-records-container :deep(.el-tag) {
  font-size: 13px;
}

/* Dark theme specific styles */
.dark-theme .transaction-records-container :deep(.el-tabs__item) {
    color: var(--dark-text-color);
}

.dark-theme .transaction-records-container :deep(.el-tabs__item.is-active) {
    color: var(--dark-primary-color);
}

.dark-theme .transaction-records-container :deep(.el-tabs__header) {
    border-bottom-color: var(--dark-border-color);
}

.dark-theme .transaction-records-container :deep(.el-tabs__item) {
     border-color: var(--dark-border-color) !important;
}

.dark-theme .amount {
    color: var(--dark-primary-color);
}

</style> 