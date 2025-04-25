<template>
  <div class="house-management">
    <div class="header">
      <h2>房屋管理</h2>
      <div class="header-buttons">
        <button class="add-btn" @click="showAddAreaDialog">添加区域</button>
        <button class="add-btn" @click="showAddBuildingDialog">添加楼栋</button>
        <button class="add-btn" @click="showAddUnitDialog">添加单元</button>
        <button class="add-btn" @click="showAddHouseDialog">添加房屋</button>
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        placeholder="搜索房屋名称/单元号" 
        @input="handleSearch"
      />
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>区域</th>
            <th>楼栋</th>
            <th>单元</th>
            <th>房屋名称</th>
            <th>面积(㎡)</th>
            <th>余额</th>
            <th>单价(元/㎡)</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="house in filteredHouses" :key="house.houseId">
            <td>{{ house.houseId }}</td>
            <td>{{ getAreaName(house.areaId) }}</td>
            <td>{{ getBuildingName(house.buildingId) }}</td>
            <td>{{ getUnitName(house.unitId) }}</td>
            <td>{{ house.name }}</td>
            <td>{{ house.size }}</td>
            <td>{{ house.balance }}</td>
            <td>{{ house.price }}</td>
            <td>
              <button class="edit-btn" @click="showEditDialog(house)">编辑</button>
              <button class="delete-btn" @click="handleDelete(house.houseId)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 添加/编辑区域对话框 -->
    <div v-if="showAreaDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑区域' : '添加区域' }}</h3>
        <div class="form-group">
          <label>区域名称</label>
          <input v-model="currentArea.name" />
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeAreaDialog">取消</button>
          <button class="submit-btn" @click="handleAreaSubmit">确定</button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑楼栋对话框 -->
    <div v-if="showBuildingDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑楼栋' : '添加楼栋' }}</h3>
        <div class="form-group">
          <label>楼栋名称</label>
          <input v-model="currentBuilding.name" />
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeBuildingDialog">取消</button>
          <button class="submit-btn" @click="handleBuildingSubmit">确定</button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑单元对话框 -->
    <div v-if="showUnitDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑单元' : '添加单元' }}</h3>
        <div class="form-group">
          <label>单元名称</label>
          <input v-model="currentUnit.name" />
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeUnitDialog">取消</button>
          <button class="submit-btn" @click="handleUnitSubmit">确定</button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑房屋对话框 -->
    <div v-if="showHouseDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑房屋' : '添加房屋' }}</h3>
        <div class="form-group">
          <label>区域</label>
          <select v-model="currentHouse.areaId" @change="handleAreaChange">
            <option v-for="area in areas" :key="area.areaId" :value="area.areaId">
              {{ area.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>楼栋</label>
          <select v-model="currentHouse.buildingId" @change="handleBuildingChange">
            <option v-for="building in filteredBuildings" :key="building.buildingId" :value="building.buildingId">
              {{ building.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>单元</label>
          <select v-model="currentHouse.unitId">
            <option v-for="unit in filteredUnits" :key="unit.unitId" :value="unit.unitId">
              {{ unit.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>房屋名称</label>
          <input v-model="currentHouse.name" />
        </div>
        <div class="form-group">
          <label>面积(㎡)</label>
          <input v-model="currentHouse.size" type="number" />
        </div>
        <div class="form-group">
          <label>单价</label>
          <input v-model="currentHouse.price" type="number" />
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeHouseDialog">取消</button>
          <button class="submit-btn" @click="handleHouseSubmit">确定</button>
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
const areas = ref([]);
const buildings = ref([]);
const units = ref([]);
const searchKeyword = ref('');

// 对话框状态
const showAreaDialog = ref(false);
const showBuildingDialog = ref(false);
const showUnitDialog = ref(false);
const showHouseDialog = ref(false);
const isEdit = ref(false);

// 当前编辑对象
const currentArea = ref({ name: '' });
const currentBuilding = ref({ name: '' });
const currentUnit = ref({ name: '' });
const currentHouse = ref({
  houseId: '',
  name: '',
  areaId: '',
  buildingId: '',
  unitId: '',
  size: 0,
  balance: 0,
  price: 0
});

// 获取区域名称
const getAreaName = (areaId) => {
  const area = areas.value.find(a => a.areaId === areaId);
  return area ? area.name : '未知区域';
};

// 获取楼栋名称
const getBuildingName = (buildingId) => {
  const building = buildings.value.find(b => b.buildingId === buildingId);
  return building ? building.name : '未知楼栋';
};

// 获取单元名称
const getUnitName = (unitId) => {
  const unit = units.value.find(u => u.unitId === unitId);
  return unit ? unit.name : '未知单元';
};

// 过滤后的房屋列表
const filteredHouses = computed(() => {
  if (!searchKeyword.value) return houses.value;
  const keyword = searchKeyword.value.toLowerCase();
  return houses.value.filter(house => 
    house.name.toLowerCase().includes(keyword) ||
    getUnitName(house.unitId).toLowerCase().includes(keyword)
  );
});

// 过滤后的楼栋列表
const filteredBuildings = computed(() => {
  return buildings.value;
});

// 过滤后的单元列表
const filteredUnits = computed(() => {
  return units.value;
});

// 区域变化处理
const handleAreaChange = () => {
  currentHouse.value.buildingId = '';
  currentHouse.value.unitId = '';
};

// 楼栋变化处理
const handleBuildingChange = () => {
  currentHouse.value.unitId = '';
};

// 获取所有数据
const fetchAllData = async () => {
  try {
    console.log('开始获取数据...');
    
    // 先获取区域列表
    const areasRes = await api.get('/api/house/area/list');
    console.log('区域列表响应:', areasRes.data);
    if (areasRes.data.code === 200) {
      areas.value = areasRes.data.data;
      console.log('区域列表:', areas.value);
      
      // 获取所有楼栋列表
      const buildingsRes = await api.get('/api/house/building/list');
      console.log('楼栋列表响应:', buildingsRes.data);
      if (buildingsRes.data.code === 200) {
        buildings.value = buildingsRes.data.data;
        console.log('楼栋列表:', buildings.value);
        
        // 获取所有单元列表
        const unitsRes = await api.get('/api/house/unit/list');
        console.log('单元列表响应:', unitsRes.data);
        if (unitsRes.data.code === 200) {
          units.value = unitsRes.data.data;
          console.log('单元列表:', units.value);
          
          // 获取所有房屋列表
          const housesRes = await api.get('/api/house/list');
          console.log('房屋列表响应:', housesRes.data);
          if (housesRes.data.code === 200) {
            houses.value = housesRes.data.data;
            console.log('房屋列表:', houses.value);
          } else {
            console.error('获取房屋列表失败:', housesRes.data.msg);
          }
        } else {
          console.error('获取单元列表失败:', unitsRes.data.msg);
        }
      } else {
        console.error('获取楼栋列表失败:', buildingsRes.data.msg);
      }
    } else {
      console.error('获取区域列表失败:', areasRes.data.msg);
    }
  } catch (error) {
    console.error('获取数据失败:', error);
    console.error('错误详情:', error.response?.data);
    alert('获取数据失败：' + (error.response?.data?.msg || error.message));
  }
};

// 区域相关方法
const showAddAreaDialog = () => {
  isEdit.value = false;
  currentArea.value = { name: '' };
  showAreaDialog.value = true;
};

const closeAreaDialog = () => {
  showAreaDialog.value = false;
  currentArea.value = { name: '' };
};

const handleAreaSubmit = async () => {
  if (!currentArea.value.name) {
    alert('请输入区域名称！');
    return;
  }

  try {
    const response = await api.post('/api/house/area/create', currentArea.value);
    if (response.data.code === 200) {
      alert('创建成功！');
      await fetchAllData();
      closeAreaDialog();
    } else {
      alert(response.data.msg || '创建失败');
    }
  } catch (error) {
    console.error('创建失败:', error);
    alert('创建失败：' + (error.response?.data?.msg || error.message));
  }
};

// 楼栋相关方法
const showAddBuildingDialog = () => {
  isEdit.value = false;
  currentBuilding.value = { name: '' };
  showBuildingDialog.value = true;
};

const closeBuildingDialog = () => {
  showBuildingDialog.value = false;
  currentBuilding.value = { name: '' };
};

const handleBuildingSubmit = async () => {
  if (!currentBuilding.value.name) {
    alert('请填写楼栋名称！');
    return;
  }

  try {
    const response = await api.post('/api/house/building/create', currentBuilding.value);
    if (response.data.code === 200) {
      alert('创建成功！');
      await fetchAllData();
      closeBuildingDialog();
    } else {
      alert(response.data.msg || '创建失败');
    }
  } catch (error) {
    console.error('创建失败:', error);
    alert('创建失败：' + (error.response?.data?.msg || error.message));
  }
};

// 单元相关方法
const showAddUnitDialog = () => {
  isEdit.value = false;
  currentUnit.value = { name: '' };
  showUnitDialog.value = true;
};

const closeUnitDialog = () => {
  showUnitDialog.value = false;
  currentUnit.value = { name: '' };
};

const handleUnitSubmit = async () => {
  if (!currentUnit.value.name) {
    alert('请填写单元名称！');
    return;
  }

  try {
    const response = await api.post('/api/house/unit/create', currentUnit.value);
    if (response.data.code === 200) {
      alert('创建成功！');
      await fetchAllData();
      closeUnitDialog();
    } else {
      alert(response.data.msg || '创建失败');
    }
  } catch (error) {
    console.error('创建失败:', error);
    alert('创建失败：' + (error.response?.data?.msg || error.message));
  }
};

// 房屋相关方法
const showAddHouseDialog = () => {
  isEdit.value = false;
  currentHouse.value = {
    houseId: '',
    name: '',
    areaId: '',
    buildingId: '',
    unitId: '',
    size: 0,
    balance: 0,  // 默认余额为0
    price: 0
  };
  showHouseDialog.value = true;
};

const showEditDialog = (house) => {
  isEdit.value = true;
  currentHouse.value = { ...house };
  showHouseDialog.value = true;
};

const closeHouseDialog = () => {
  showHouseDialog.value = false;
  currentHouse.value = {
    houseId: '',
    name: '',
    areaId: '',
    buildingId: '',
    unitId: '',
    size: 0,
    balance: 0,
    price: 0
  };
};

const handleHouseSubmit = async () => {
  if (!currentHouse.value.areaId || !currentHouse.value.buildingId || 
      !currentHouse.value.unitId || !currentHouse.value.name || 
      !currentHouse.value.size || !currentHouse.value.price) {
    alert('请填写完整信息！');
    return;
  }

  try {
    if (isEdit.value) {
      const response = await api.put(`/api/house/update/${currentHouse.value.houseId}`, currentHouse.value);
      if (response.data.code === 200) {
        alert('更新成功！');
        await fetchAllData();
        closeHouseDialog();
      } else {
        alert(response.data.msg || '更新失败');
      }
    } else {
      // 确保新建时余额为0
      currentHouse.value.balance = 0;
      const response = await api.post('/api/house/create', currentHouse.value);
      if (response.data.code === 200) {
        alert('创建成功！');
        await fetchAllData();
        closeHouseDialog();
      } else {
        alert(response.data.msg || '创建失败');
      }
    }
  } catch (error) {
    console.error(isEdit.value ? '更新失败:' : '创建失败:', error);
    alert((isEdit.value ? '更新' : '创建') + '失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleDelete = async (houseId) => {
  if (!confirm('确定要删除该房屋吗？')) return;
  
  try {
    const response = await api.delete(`/api/house/delete/${houseId}`);
    if (response.data.code === 200) {
      alert('删除成功！');
      await fetchAllData();
    }
  } catch (error) {
    console.error('删除失败:', error);
    alert('删除失败：' + (error.response?.data?.msg || error.message));
  }
};

const handleSearch = () => {
  // 搜索功能已通过计算属性实现
};

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token');
    router.push('/login');
  }
};

onMounted(() => {
  fetchAllData();
});
</script>

<style scoped>
.house-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.search-bar {
  margin-bottom: 20px;
}

.search-bar input {
  width: 300px;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f5f5f5;
  font-weight: bold;
}

tr:hover {
  background-color: #f9f9f9;
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
  border-radius: 8px;
  width: 400px;
}

.dialog h3 {
  margin-top: 0;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
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

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.add-btn {
  background-color: #4CAF50;
  color: white;
}

.edit-btn {
  background-color: #2196F3;
  color: white;
}

.delete-btn {
  background-color: #f44336;
  color: white;
}

.cancel-btn {
  background-color: #f5f5f5;
  color: #333;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
}

.logout-btn {
  background-color: #f44336;
  color: white;
}
</style>