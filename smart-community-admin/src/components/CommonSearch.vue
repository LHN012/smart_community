<template>
  <div class="common-search">
    <el-form
      ref="searchForm"
      :model="formData"
      :inline="true"
      class="search-form"
      @keyup.enter="handleSearch"
    >
      <el-form-item
        v-for="item in searchItems"
        :key="item.prop"
        :label="item.label"
        :prop="item.prop"
      >
        <!-- 输入框 -->
        <el-input
          v-if="item.type === 'input'"
          v-model="formData[item.prop]"
          :placeholder="item.placeholder || `请输入${item.label}`"
          clearable
          @clear="handleClear(item.prop)"
        />
        
        <!-- 选择器 -->
        <el-select
          v-else-if="item.type === 'select'"
          v-model="formData[item.prop]"
          :placeholder="item.placeholder || `请选择${item.label}`"
          clearable
          @clear="handleClear(item.prop)"
        >
          <el-option
            v-for="option in item.options"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
        
        <!-- 日期选择器 -->
        <el-date-picker
          v-else-if="item.type === 'date'"
          v-model="formData[item.prop]"
          :type="item.dateType || 'date'"
          :placeholder="item.placeholder || `请选择${item.label}`"
          clearable
          @clear="handleClear(item.prop)"
        />
        
        <!-- 日期范围选择器 -->
        <el-date-picker
          v-else-if="item.type === 'daterange'"
          v-model="formData[item.prop]"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          clearable
          @clear="handleClear(item.prop)"
        />
      </el-form-item>
      
      <el-form-item class="search-buttons">
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <slot name="extra-buttons"></slot>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { Search, Refresh } from '@element-plus/icons-vue';

const props = defineProps({
  searchItems: {
    type: Array,
    required: true,
    // 每个搜索项的配置
    // {
    //   prop: 'name',          // 字段名
    //   label: '姓名',         // 标签名
    //   type: 'input',        // 类型：input/select/date/daterange
    //   placeholder: '请输入', // 占位符
    //   options: [],          // 选项（select类型需要）
    //   dateType: 'date'      // 日期类型（date类型需要）
    // }
  },
  initialValues: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['search', 'reset']);

// 表单数据
const formData = reactive({...props.initialValues});

// 监听初始值变化
watch(() => props.initialValues, (newVal) => {
  Object.assign(formData, newVal);
}, { deep: true });

// 搜索
const handleSearch = () => {
  emit('search', {...formData});
};

// 重置
const handleReset = () => {
  Object.keys(formData).forEach(key => {
    formData[key] = '';
  });
  emit('reset');
};

// 清除单个字段
const handleClear = (prop) => {
  formData[prop] = '';
};
</script>

<style scoped>
.common-search {
  background: var(--card-bg);
  padding: 20px 24px; /* 调整内边距 */
  border-radius: 16px;
  box-shadow: 0 4px 16px var(--shadow-color),
              0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
  transition: all 0.4s ease; /* 调整过渡时间 */
  margin-bottom: 24px; /* 增加底部间距 */
}

/* 移除搜索框的流光动画，与 Header 保持一致 */
.common-search::before {
  display: none;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 20px; /* 调整表单项间距 */
  align-items: flex-end;
  position: relative;
  z-index: 1;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
  flex: 1;
  min-width: 220px; /* 调整最小宽度 */
}

.search-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-color); /* 使用变量 */
  font-size: 0.95rem;
  font-family: 'Montserrat', sans-serif;
  letter-spacing: 0.5px;
  transition: color 0.4s ease; /* 调整过渡时间 */
}

.search-form :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--border-color);
  border-radius: 10px; /* 调整圆角 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: var(--card-bg); /* 使用变量 */
  backdrop-filter: blur(5px);
}

.search-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #3498db,
              0 4px 12px rgba(52, 152, 219, 0.2); /* 调整阴影 */
  transform: translateY(-2px); /* 增强悬停效果 */
}

.search-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #3498db,
              0 4px 12px rgba(52, 152, 219, 0.3); /* 增强聚焦阴影 */
  transform: translateY(-3px); /* 增强聚焦效果 */
}

.search-form :deep(.el-input__inner) {
  height: 44px; /* 调整输入框高度 */
  font-size: 1rem; /* 调整字体大小 */
  color: var(--text-color); /* 使用变量 */
  font-family: 'Roboto', sans-serif;
  padding: 0 16px;
}

.search-form :deep(.el-input__inner::placeholder) {
  color: var(--text-color);
  opacity: 0.6; /* 调整占位符颜色 */
  font-size: 0.95rem;
}

.search-form :deep(.el-select .el-input__wrapper) {
  padding: 0 8px;
}

.search-form :deep(.el-date-editor .el-input__wrapper) {
  padding: 0 8px;
}

.search-buttons {
  display: flex;
  gap: 12px;
  margin-left: auto;
}

.search-buttons :deep(.el-button) {
  padding: 12px 24px;
  font-size: 1rem; /* 调整按钮字体大小 */
  font-weight: 500;
  border-radius: 10px; /* 调整按钮圆角 */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'Roboto', sans-serif;
  position: relative;
  overflow: hidden;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 按钮阴影 */
}

.search-buttons :deep(.el-button::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, 
    rgba(255, 255, 255, 0.15),
    rgba(255, 255, 255, 0.25)
  );
  transform: translateX(-100%);
  transition: transform 0.4s ease; /* 调整光效速度 */
}

.search-buttons :deep(.el-button:hover) {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 6px 16px var(--shadow-color); /* 调整悬停阴影 */
}

.search-buttons :deep(.el-button:hover::before) {
  transform: translateX(100%);
}

.search-buttons :deep(.el-button--primary) {
  background: linear-gradient(135deg, #3498db, #2980b9);
  color: white;
}

.search-buttons :deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #2980b9, #3498db);
}

.search-buttons :deep(.el-button--default) {
  background: linear-gradient(135deg, var(--hover-bg), var(--border-color)); /* 使用变量和渐变 */
  color: var(--text-color); /* 使用变量 */
}

.search-buttons :deep(.el-button--default:hover) {
  background: linear-gradient(135deg, var(--border-color), var(--hover-bg)); /* 使用变量和渐变 */
  color: #3498db; /* 悬停颜色 */
}

.search-buttons :deep(.el-button .el-icon) {
  margin-right: 8px;
  font-size: 1.1em;
  vertical-align: -0.1em;
}

/* 响应式布局 */
@media screen and (max-width: 768px) {
  .common-search {
    padding: 16px;
    margin-bottom: 16px; /* 调整底部间距 */
  }

  .search-form {
    gap: 12px;
  }

  .search-form :deep(.el-form-item) {
    min-width: 100%;
  }

  .search-buttons {
    width: 100%;
    justify-content: flex-end;
  }

  .search-buttons :deep(.el-button) {
    padding: 10px 20px;
  }
}
</style> 