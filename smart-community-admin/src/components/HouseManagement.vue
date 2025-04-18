<template>
  <div class="house-management">
    <div class="header">
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          placeholder="搜索房屋编号/单元/楼层" 
          @input="handleSearch"
        />
        <button class="search-btn">
          <i class="fas fa-search"></i> 搜索
        </button>
      </div>
      <button class="add-btn" @click="showAddDialog = true">
        <i class="fas fa-plus"></i> 添加房屋
      </button>
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>房屋编号</th>
            <th>单元</th>
            <th>楼层</th>
            <th>面积(㎡)</th>
            <th>余额(元)</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="house in filteredHouses" :key="house.houseId">
            <td>{{ house.houseId }}</td>
            <td>{{ house.houseNo }}</td>
            <td>{{ house.unitId }}</td>
            <td>{{ house.floor }}</td>
            <td>{{ house.size }}</td>
            <td>{{ house.balance }}</td>
            <td>
              <button class="edit-btn" @click="handleEdit(house)">
                <i class="fas fa-edit"></i> 修改
              </button>
              <button class="delete-btn" @click="handleDelete(house.houseId)">
                <i class="fas fa-trash"></i> 删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加/编辑对话框 -->
    <div v-if="showAddDialog || showEditDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ showEditDialog ? '编辑房屋' : '添加房屋' }}</h3>
        <div class="form-group">
          <label>房屋编号</label>
          <input v-model="currentHouse.houseNo" :disabled="showEditDialog" />
        </div>
        <div class="form-group">
          <label>单元</label>
          <input v-model="currentHouse.unitId" type="number" />
        </div>
        <div class="form-group">
          <label>楼层</label>
          <input v-model="currentHouse.floor" type="number" />
        </div>
        <div class="form-group">
          <label>面积(㎡)</label>
          <input v-model="currentHouse.size" type="number" step="0.01" />
        </div>
        <div class="form-group">
          <label>余额(元)</label>
          <input v-model="currentHouse.balance" type="number" step="0.01" />
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeDialog">取消</button>
          <button class="confirm-btn" @click="handleSubmit">
            {{ showEditDialog ? '保存' : '添加' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../api';

const houses = ref([]);
const searchQuery = ref('');
const showAddDialog = ref(false);
const showEditDialog = ref(false);
const currentHouse = ref({
  houseNo: '',
  unitId: '',
  floor: '',
  size: '',
  balance: ''
});

// 过滤后的房屋列表
const filteredHouses = computed(() => {
  if (!searchQuery.value) return houses.value;
  const query = searchQuery.value.toLowerCase();
  return houses.value.filter(house => 
    house.houseNo.toLowerCase().includes(query) ||
    house.unitId.toString().includes(query) ||
    house.floor.toString().includes(query)
  );
});

// 获取房屋列表
const fetchHouses = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('请先登录！');
      return;
    }
    
    const response = await api.get('/house/list', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    if (response.data && response.data.data) {
      houses.value = response.data.data;
    } else {
      console.error('返回数据格式错误:', response.data);
      alert('获取房屋列表失败：数据格式错误');
    }
  } catch (error) {
    console.error('获取房屋列表失败:', error);
    if (error.response && error.response.status === 403) {
      alert('获取房屋列表失败：权限不足，需要超级管理员权限');
    } else {
      alert('获取房屋列表失败：' + (error.response?.data?.msg || error.message));
    }
  }
};

// 处理搜索
const handleSearch = () => {
  // 实时搜索，不需要额外处理
};

// 处理编辑
const handleEdit = (house) => {
  currentHouse.value = { ...house };
  showEditDialog.value = true;
};

// 处理删除
const handleDelete = async (houseId) => {
  if (!confirm('确定要删除该房屋吗？')) return;
  
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('请先登录！');
      return;
    }
    
    const response = await api.delete(`/house/delete/${houseId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    if (response.data && response.data.code === 200) {
      alert('删除成功！');
      fetchHouses();
    } else {
      alert(response.data?.msg || '删除失败');
    }
  } catch (error) {
    console.error('删除失败:', error);
    if (error.response && error.response.status === 403) {
      alert('删除失败：权限不足，需要超级管理员权限');
    } else {
      alert('删除失败：' + (error.response?.data?.msg || error.message));
    }
  }
};

// 处理提交
const handleSubmit = async () => {
  if (!currentHouse.value.houseNo || !currentHouse.value.unitId || !currentHouse.value.floor) {
    alert('房屋编号、单元和楼层必填！');
    return;
  }
  
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('请先登录！');
      return;
    }

    // 如果是创建新房屋，先检查房屋编号是否已存在
    if (!showEditDialog.value) {
      const checkResponse = await api.get(`/house/check-house-no/${currentHouse.value.unitId}/${currentHouse.value.houseNo}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      
      if (checkResponse.data && checkResponse.data.code === 200 && !checkResponse.data.data) {
        alert('该单元下已存在相同编号的房屋！');
        return;
      }
    }
    
    const url = showEditDialog.value 
      ? `/house/update/${currentHouse.value.houseId}`
      : '/house/create';
    
    const method = showEditDialog.value ? 'put' : 'post';
    
    const response = await api[method](url, currentHouse.value, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    if (response.data && response.data.code === 200) {
      alert(showEditDialog.value ? '更新成功！' : '创建成功！');
      closeDialog();
      fetchHouses();
    } else {
      alert(response.data?.msg || (showEditDialog.value ? '更新失败' : '创建失败'));
    }
  } catch (error) {
    console.error(showEditDialog.value ? '更新失败:' : '创建失败:', error);
    if (error.response && error.response.status === 403) {
      alert((showEditDialog.value ? '更新' : '创建') + '失败：权限不足，需要超级管理员权限');
    } else {
      alert((showEditDialog.value ? '更新' : '创建') + '失败：' + (error.response?.data?.msg || error.message));
    }
  }
};

// 关闭对话框
const closeDialog = () => {
  showAddDialog.value = false;
  showEditDialog.value = false;
  currentHouse.value = {
    houseNo: '',
    unitId: '',
    floor: '',
    size: '',
    balance: ''
  };
};

onMounted(() => {
  fetchHouses();
});
</script>

<style scoped>
.house-management {
  padding: 20px;
  margin-left: 200px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  align-items: center;
}

.search-box input {
  padding: 8px;
  width: 300px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 10px;
}

.search-btn, .add-btn {
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background-color: #3498db;
  color: white;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
}

.add-btn {
  background-color: #2ecc71;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow: hidden;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.edit-btn, .delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
}

.edit-btn {
  background-color: #f1c40f;
  color: white;
}

.delete-btn {
  background-color: #e74c3c;
  color: white;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.dialog {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.dialog-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn,
.confirm-btn {
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #95a5a6;
  color: white;
}

.confirm-btn {
  background-color: #2ecc71;
  color: white;
}
</style>