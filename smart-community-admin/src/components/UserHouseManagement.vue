<template>
  <div class="user-house-management">
    <div class="header">
      <h2>用户房屋绑定信息</h2>
      <div class="header-buttons">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </div>

    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        placeholder="搜索用户名/真实姓名/手机号/邮箱" 
        @input="handleSearch"
      />
    </div>

    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th>关系ID</th>
            <th>用户信息</th>
            <th>房屋信息</th>
            <th>关系类型</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="relation in filteredRelations" :key="relation.relationId">
            <td>{{ relation.relationId }}</td>
            <td>
              <div>用户名：{{ relation.username }}</div>
              <div>真实姓名：{{ relation.realName }}</div>
              <div>手机号：{{ relation.phoneNumber }}</div>
              <div>邮箱：{{ relation.email }}</div>
            </td>
            <td>
              <div>区域：{{ relation.areaName }}</div>
              <div>楼栋：{{ relation.buildingName }}</div>
              <div>单元：{{ relation.unitName }}</div>
              <div>房屋：{{ relation.houseName }}</div>
            </td>
            <td>{{ relation.relationType }}</td>
            <td>
              <button class="edit-btn" @click="showEditDialog(relation)">编辑关系</button>
              <button class="delete-btn" @click="handleDelete(relation)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 编辑对话框 -->
    <div v-if="showDialog" class="dialog-overlay">
      <div class="dialog">
        <h3>编辑用户房屋关系</h3>
        <div class="form-group">
          <label>关系类型</label>
          <select v-model="currentRelation.relationType">
            <option value="户主">户主</option>
            <option value="成员">成员</option>
            <option value="租客">租客</option>
          </select>
        </div>
        <div class="dialog-buttons">
          <button class="cancel-btn" @click="closeDialog">取消</button>
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

const router = useRouter();
const relations = ref([]);
const searchKeyword = ref('');
const showDialog = ref(false);
const currentRelation = ref({
  relationId: null,
  relationType: ''
});

const filteredRelations = computed(() => {
  if (!searchKeyword.value) return relations.value;
  const keyword = searchKeyword.value.toLowerCase();
  return relations.value.filter(relation => 
    relation.username?.toLowerCase().includes(keyword) ||
    relation.realName?.toLowerCase().includes(keyword) ||
    relation.phoneNumber?.toLowerCase().includes(keyword) ||
    relation.email?.toLowerCase().includes(keyword)
  );
});

const fetchRelations = async () => {
  try {
    const response = await api.get('/api/user-houses/list');
    if (response.data.code === 200) {
      relations.value = response.data.data;
    }
  } catch (error) {
    console.error('获取用户房屋关系列表失败:', error);
    alert('获取用户房屋关系列表失败：' + (error.response?.data?.msg || error.message));
  }
};

const showEditDialog = (relation) => {
  currentRelation.value = { ...relation };
  showDialog.value = true;
};

const closeDialog = () => {
  showDialog.value = false;
  currentRelation.value = {
    relationId: null,
    relationType: ''
  };
};

const handleSubmit = async () => {
  if (!currentRelation.value.relationType) {
    alert('请选择关系类型！');
    return;
  }

  try {
    const response = await api.put(`/api/user-houses/update/${currentRelation.value.relationId}`, {
      relationType: currentRelation.value.relationType
    });
    if (response.data.code === 200) {
      alert('更新成功！');
      await fetchRelations();
      closeDialog();
    }
  } catch (error) {
    console.error('更新失败:', error);
    alert('更新失败：' + (error.response?.data?.msg || error.message));
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

const handleDelete = async (relation) => {
  if (!confirm('确定要删除这条用户房屋关系吗？')) {
    return;
  }

  try {
    const response = await api.delete(`/api/user-houses/delete/${relation.relationId}`);
    if (response.data.code === 200) {
      alert('删除成功！');
      await fetchRelations();
    }
  } catch (error) {
    console.error('删除失败:', error);
    alert('删除失败：' + (error.response?.data?.msg || error.message));
  }
};

onMounted(() => {
  fetchRelations();
});
</script>

<style scoped>
.user-house-management {
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

.edit-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #3498db;
  color: white;
}

.edit-btn:hover {
  background-color: #2980b9;
}

.delete-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #e74c3c;
  color: white;
}

.delete-btn:hover {
  background-color: #c0392b;
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
</style> 