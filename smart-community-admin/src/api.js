// 前端 axios 配置（smart-community-admin/src/api.js）
import axios from 'axios';

const instance = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
instance.interceptors.response.use(
    response => {
        return response;
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 未授权，清除token并跳转到登录页
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                    break;
                case 403:
                    console.error('没有权限访问该资源');
                    if (error.response.data && error.response.data.msg) {
                        console.error('错误信息:', error.response.data.msg);
                    }
                    break;
                case 500:
                    console.error('服务器错误');
                    if (error.response.data && error.response.data.msg) {
                        console.error('错误信息:', error.response.data.msg);
                    }
                    break;
                default:
                    console.error('请求失败:', error.response.status);
                    if (error.response.data && error.response.data.msg) {
                        console.error('错误信息:', error.response.data.msg);
                    }
            }
        } else if (error.request) {
            console.error('请求未收到响应:', error.request);
        } else {
            console.error('请求配置错误:', error.message);
        }
        return Promise.reject(error);
    }
);

export default instance;