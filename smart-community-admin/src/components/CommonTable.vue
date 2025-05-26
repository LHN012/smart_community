<template>
  <div class="common-table">
    <div class="table-container">
      <table>
        <thead>
          <tr>
            <th v-for="column in columns" 
                :key="column.prop"
                :style="{ width: columnWidths[column.prop] || column.width || 'auto' }"
                class="resizable-column"
                @mousedown="startResize($event, column)">
              {{ column.label }}
              <div class="resize-handle"></div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, index) in paginatedData" 
              :key="index"
              class="table-row">
            <td v-for="column in columns" 
                :key="column.prop"
                :style="{ width: columnWidths[column.prop] || column.width || 'auto' }">
              <slot :name="column.prop" :row="row">
                {{ row[column.prop] }}
              </slot>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="pagination-container">
      <div class="page-size-selector">
        <span>每页显示：</span>
        <select v-model="pageSize" @change="handlePageSizeChange">
          <option v-for="size in pageSizeOptions" 
                  :key="size" 
                  :value="size">
            {{ size }}条
          </option>
        </select>
      </div>
      
      <div class="pagination">
        <button 
          :disabled="currentPage === 1"
          @click="currentPage = 1"
          class="page-btn">
          首页
        </button>
        <button 
          :disabled="currentPage === 1"
          @click="currentPage--"
          class="page-btn">
          上一页
        </button>
        <span class="page-info">
          第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
        </span>
        <button 
          :disabled="currentPage === totalPages"
          @click="currentPage++"
          class="page-btn">
          下一页
        </button>
        <button 
          :disabled="currentPage === totalPages"
          @click="currentPage = totalPages"
          class="page-btn">
          末页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';

const props = defineProps({
  data: {
    type: Array,
    required: true
  },
  columns: {
    type: Array,
    required: true
  },
  pageSizeOptions: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  defaultSort: {
    type: Object,
    default: () => ({
      prop: 'id',
      order: 'desc'
    })
  }
});

const currentPage = ref(1);
const pageSize = ref(props.pageSizeOptions[0]);
const resizingColumn = ref(null);
const startX = ref(0);
const startWidth = ref(0);
const sortConfig = ref(props.defaultSort);
const columnWidths = ref({});

// 初始化列宽
onMounted(() => {
  props.columns.forEach(column => {
    if (column.width) {
      columnWidths.value[column.prop] = column.width;
    }
  });
});

const sortedData = computed(() => {
  const { prop, order } = sortConfig.value;
  return [...props.data].sort((a, b) => {
    if (order === 'desc') {
      return b[prop] - a[prop];
    }
    return a[prop] - b[prop];
  });
});

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return sortedData.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(props.data.length / pageSize.value);
});

const handlePageSizeChange = () => {
  currentPage.value = 1;
};

const startResize = (event, column) => {
  if (event.target.classList.contains('resize-handle')) {
    event.preventDefault();
    resizingColumn.value = column;
    startX.value = event.clientX;
    startWidth.value = event.target.parentElement.offsetWidth;
    
    // 添加拖拽状态类
    document.body.classList.add('resizing');
    
    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', stopResize);
  }
};

const handleMouseMove = (event) => {
  if (resizingColumn.value) {
    const tableContainer = document.querySelector('.table-container');
    const containerRect = tableContainer.getBoundingClientRect();
    
    // 获取当前列的位置信息
    const column = event.target.parentElement;
    const columnRect = column.getBoundingClientRect();
    
    // 计算拖拽的边界
    const minWidth = 100;
    const maxWidth = Math.min(
      containerRect.width - (columnRect.left - containerRect.left),
      containerRect.width * 0.3
    );
    
    // 限制鼠标位置在容器范围内
    const mouseX = Math.min(event.clientX, containerRect.right);
    const diff = mouseX - startX.value;
    
    // 计算新的宽度，并应用限制
    const newWidth = Math.min(Math.max(minWidth, startWidth.value + diff), maxWidth);
    
    // 确保总宽度不超过容器宽度
    const totalWidth = Object.values(columnWidths.value).reduce((sum, width) => {
      return sum + (parseInt(width) || 0);
    }, 0);
    
    if (totalWidth + (newWidth - startWidth.value) <= containerRect.width) {
      columnWidths.value[resizingColumn.value.prop] = `${newWidth}px`;
    }
    
    // 添加拖拽时的视觉反馈
    const handle = event.target;
    if (handle) {
      handle.classList.add('active');
    }
  }
};

const stopResize = () => {
  if (resizingColumn.value) {
    const handle = document.querySelector('.resize-handle.active');
    if (handle) {
      handle.classList.remove('active');
    }
    
    // 重置表格容器宽度
    const tableContainer = document.querySelector('.table-container');
    if (tableContainer) {
      tableContainer.style.width = '100%';
    }
  }
  resizingColumn.value = null;
  document.body.classList.remove('resizing');
  document.removeEventListener('mousemove', handleMouseMove);
  document.removeEventListener('mouseup', stopResize);
};

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove);
  document.removeEventListener('mouseup', stopResize);
});
</script>

<style scoped>
.common-table {
  width: 100%;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08),
              0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  overflow: hidden;
  position: relative;
  max-width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.table-container {
  width: 100%;
  overflow-x: auto;
  background: transparent;
  position: relative;
  scrollbar-width: thin;
  scrollbar-color: #3498db #f8f9fa;
  scroll-behavior: smooth;
}

/* 移除流光动画相关样式 */
.table-container::before {
  display: none;
}

.table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-family: 'Roboto', sans-serif;
  table-layout: fixed;
}

.table th {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  color: #2c3e50;
  font-weight: 600;
  font-size: 0.95rem;
  padding: 16px;
  text-align: left;
  border-bottom: 2px solid #e9ecef;
  white-space: normal;
  word-break: break-word;
  position: relative;
  font-family: 'Montserrat', sans-serif;
  letter-spacing: 0.5px;
  min-width: 80px;
}

.table th::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #3498db, #2ecc71);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.table th:hover::after {
  transform: scaleX(1);
}

.table td {
  padding: 16px;
  border-bottom: 1px solid #e9ecef;
  color: #2c3e50;
  font-size: 0.9rem;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  white-space: normal;
  word-break: break-word;
  overflow: hidden;
  text-overflow: ellipsis;
  min-width: 80px;
}

.table tr {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.table tr:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.table tr:hover td {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.table tr:last-child td {
  border-bottom: none;
}

.resizable-column {
  position: relative;
  user-select: none;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.resize-handle {
  position: absolute;
  right: 0;
  top: 0;
  bottom: 0;
  width: 8px;
  cursor: col-resize;
  background-color: transparent;
  transition: all 0.3s ease;
  z-index: 1;
  opacity: 0;
}

.resize-handle::after {
  content: '';
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  width: 2px;
  background: linear-gradient(180deg, #3498db, #2ecc71);
  transform: translateX(-50%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.resize-handle:hover::after,
.resize-handle.active::after {
  opacity: 1;
}

.resizable-column:hover .resize-handle {
  opacity: 1;
  background-color: rgba(52, 152, 219, 0.1);
}

.resize-handle.active {
  opacity: 1;
  background-color: rgba(52, 152, 219, 0.2);
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  border-top: 1px solid rgba(233, 236, 239, 0.5);
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: 'Roboto', sans-serif;
  color: #2c3e50;
}

.page-size-selector select {
  padding: 8px 12px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background: white;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Roboto', sans-serif;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.page-size-selector select:hover {
  border-color: #3498db;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.1);
}

.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  background: white;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'Roboto', sans-serif;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.page-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  border-color: #3498db;
  color: #3498db;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.1);
}

.page-btn:disabled {
  cursor: not-allowed;
  color: #95a5a6;
  background: #f8f9fa;
  box-shadow: none;
}

.page-info {
  color: #2c3e50;
  margin: 0 12px;
  font-weight: 500;
  font-family: 'Roboto', sans-serif;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .table th,
  .table td {
    padding: 12px;
    font-size: 0.85rem;
  }

  .pagination-container {
    padding: 16px;
    flex-direction: column;
    gap: 16px;
  }

  .page-size-selector,
  .pagination {
    width: 100%;
    justify-content: center;
  }
}

/* 添加表格行交替背景色 */
.table-row:nth-child(even) {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

/* 优化表格边框和圆角 */
.table-container:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 添加拖拽时的全局样式 */
:global(body.resizing) {
  cursor: col-resize !important;
  user-select: none;
}

:global(body.resizing) * {
  pointer-events: none;
}

:global(body.resizing) .resize-handle,
:global(body.resizing) .resizable-column {
  pointer-events: auto;
}

/* 优化滚动条样式 */
.table-container::-webkit-scrollbar {
  height: 6px;
  width: 6px;
}

.table-container::-webkit-scrollbar-track {
  background: #f8f9fa;
  border-radius: 3px;
}

.table-container::-webkit-scrollbar-thumb {
  background: #3498db;
  border-radius: 3px;
  transition: background-color 0.3s ease;
}

.table-container::-webkit-scrollbar-thumb:hover {
  background: #2980b9;
}

/* 优化固定列样式 */
.table th[fixed="right"],
.table td[fixed="right"] {
  position: sticky;
  right: 0;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
  z-index: 2;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
}

/* 优化表格行样式 */
.table-row {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.table-row:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1;
}

.table-row:hover td {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

/* 优化固定列在悬停时的样式 */
.table-row:hover td[fixed="right"] {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}
</style> 