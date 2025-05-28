import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/main.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

// 配置axios默认值
axios.defaults.baseURL = 'http://localhost:8080'  // 设置后端API的基础URL
axios.defaults.headers.common['Content-Type'] = 'application/json'

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
