import { createRouter, createWebHistory } from 'vue-router';
import AdminManagement from '../components/AdminManagement.vue';
import HouseManagement from '../components/HouseManagement.vue';
import Login from '../components/Login.vue';

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/admin',
    name: 'AdminManagement',
    component: AdminManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/house',
    name: 'HouseManagement',
    component: HouseManagement,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  
  if (to.path === '/login') {
    if (token && user.id) {
      next('/admin');
    } else {
      next();
    }
  } else {
    if (token && user.id) {
      next();
    } else {
      next('/login');
    }
  }
});

export default router; 