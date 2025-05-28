<template>
  <div class="house-management">
    <CommonHeader 
      title="房屋管理"
      description="管理小区房屋、区域、楼栋、单元信息"
    >
      <template #extra-buttons>
        <el-button type="primary" class="add-btn" @click="showAddAreaDialog">
          <el-icon><Plus /></el-icon>
          添加区域
        </el-button>
        <el-button type="primary" class="add-btn" @click="showAddBuildingDialog">
          <el-icon><Plus /></el-icon>
          添加楼栋
        </el-button>
        <el-button type="primary" class="add-btn" @click="showAddUnitDialog">
          <el-icon><Plus /></el-icon>
          添加单元
        </el-button>
        <el-button type="primary" class="add-btn" @click="showAddHouseDialog">
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
        <template #areaInfo="{ row }">
          <div>区域：{{ getAreaName(row.areaId) }}</div>
          <div>楼栋：{{ getBuildingName(row.buildingId) }}</div>
          <div>单元：{{ getUnitName(row.unitId) }}</div>
          <div>房屋：{{ row.name }}</div>
        </template>

        <template #operation="{ row }">
          <el-button type="primary" size="small" class="action-btn edit-btn" @click="showEditDialog(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button type="danger" size="small" class="action-btn delete-btn" @click="handleDelete(row.houseId)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </CommonTable>
    </div>

    <!-- 添加/编辑区域对话框 -->
    <el-dialog
      v-model="showAreaDialog"
      :title="isEdit ? '编辑区域' : '添加区域'"
      width="500px"
      class="house-dialog"
    >
      <el-form :model="currentArea" label-width="100px" class="house-form">
        <el-form-item label="区域名称">
          <el-input v-model="currentArea.name" placeholder="请输入区域名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeAreaDialog">取消</el-button>
          <el-button type="primary" @click="handleAreaSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑楼栋对话框 -->
    <el-dialog
      v-model="showBuildingDialog"
      :title="isEdit ? '编辑楼栋' : '添加楼栋'"
      width="500px"
      class="house-dialog"
    >
      <el-form :model="currentBuilding" label-width="100px" class="house-form">
        <el-form-item label="楼栋名称">
          <el-input v-model="currentBuilding.name" placeholder="请输入楼栋名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeBuildingDialog">取消</el-button>
          <el-button type="primary" @click="handleBuildingSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑单元对话框 -->
    <el-dialog
      v-model="showUnitDialog"
      :title="isEdit ? '编辑单元' : '添加单元'"
      width="500px"
      class="house-dialog"
    >
      <el-form :model="currentUnit" label-width="100px" class="house-form">
        <el-form-item label="单元名称">
          <el-input v-model="currentUnit.name" placeholder="请输入单元名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeUnitDialog">取消</el-button>
          <el-button type="primary" @click="handleUnitSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑房屋对话框 -->
    <el-dialog
      v-model="showHouseDialog"
      :title="isEdit ? '编辑房屋' : '添加房屋'"
      width="500px"
      class="house-dialog"
    >
      <el-form :model="currentHouse" label-width="100px" class="house-form">
        <el-form-item label="区域">
          <el-select v-model="currentHouse.areaId" placeholder="请选择区域" @change="handleAreaChange">
            <el-option
              v-for="area in areas"
              :key="area.areaId"
              :label="area.name"
              :value="area.areaId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="currentHouse.buildingId" placeholder="请选择楼栋" @change="handleBuildingChange">
            <el-option
              v-for="building in filteredBuildings"
              :key="building.buildingId"
              :label="building.name"
              :value="building.buildingId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="单元">
          <el-select v-model="currentHouse.unitId" placeholder="请选择单元">
            <el-option
              v-for="unit in filteredUnits"
              :key="unit.unitId"
              :label="unit.name"
              :value="unit.unitId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房屋名称">
          <el-input v-model="currentHouse.name" placeholder="请输入房屋名称" />
        </el-form-item>
        <el-form-item label="面积(㎡)">
          <el-input-number v-model="currentHouse.size" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="currentHouse.price" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeHouseDialog">取消</el-button>
          <el-button type="primary" @click="handleHouseSubmit">确定</el-button>
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
    placeholder: '搜索房屋名称/单元号'
  }
];

// 表格列配置
const columns = [
  { prop: 'houseId', label: 'ID', width: '80px' },
  { prop: 'areaInfo', label: '房屋信息', minWidth: '200px' },
  { prop: 'size', label: '面积(㎡)', width: '100px' },
  { prop: 'balance', label: '余额', width: '100px' },
  { prop: 'price', label: '单价(元/㎡)', width: '120px' },
  { prop: 'operation', label: '操作', width: '150px', fixed: 'right' }
];

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
    // 获取区域列表
    const areasRes = await api.get('/api/house/area/list');
    if (areasRes.data.code === 200) {
      areas.value = areasRes.data.data;
      
      // 获取所有楼栋列表
      const buildingsRes = await api.get('/api/house/building/list');
      if (buildingsRes.data.code === 200) {
        buildings.value = buildingsRes.data.data;
        
        // 获取所有单元列表
        const unitsRes = await api.get('/api/house/unit/list');
        if (unitsRes.data.code === 200) {
          units.value = unitsRes.data.data;
          
          // 获取所有房屋列表
          const housesRes = await api.get('/api/house/list');
          if (housesRes.data.code === 200) {
            houses.value = housesRes.data.data;
          } else {
            alert('获取房屋列表失败：' + housesRes.data.msg);
          }
        } else {
          alert('获取单元列表失败：' + unitsRes.data.msg);
        }
      } else {
        alert('获取楼栋列表失败：' + buildingsRes.data.msg);
      }
    } else {
      alert('获取区域列表失败：' + areasRes.data.msg);
    }
  } catch (error) {
    console.error('获取数据失败:', error);
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
    balance: 0,
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
  fetchAllData();
});
</script>

<style scoped>
.house-management {
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

.house-table :deep(.edit-btn) {
  background-color: #3498db;
  border-color: #3498db;
  color: white;
}

.house-table :deep(.edit-btn:hover) {
  background-color: #2980b9;
  border-color: #2980b9;
}

.house-table :deep(.delete-btn) {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: white;
}

.house-table :deep(.delete-btn:hover) {
  background-color: #c0392b;
  border-color: #c0392b;
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

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .house-management {
    padding: 16px;
  }

  .content-wrapper {
    padding: 16px;
    margin-top: 16px;
  }
}

/* 暗色主题适配 */
.dark-theme .house-management {
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
</style>