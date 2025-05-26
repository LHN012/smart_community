import { createRouter, createWebHistory } from 'vue-router';
import AdminManagement from '../views/AdminManagement.vue';
import HouseManagement from '../views/HouseManagement.vue';
import Login from '../views/Login.vue';
import UserManagement from '../views/UserManagement.vue';
import UserHouseManagement from '../views/UserHouseManagement.vue'
import HouseInfoManagement from '../views/HouseInfoManagement.vue'
import TransactionRecords from '../views/TransactionRecords.vue'
import NotificationManagement from '../views/NotificationManagement.vue'
import MaintenanceManagement from '../views/MaintenanceManagement.vue'

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
  },
  {
    path: '/user-management',
    name: 'UserManagement',
    component: UserManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/user-house-management',
    name: 'UserHouseManagement',
    component: UserHouseManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/house-info',
    name: 'HouseInfoManagement',
    component: HouseInfoManagement,
    meta: { requiresAuth: true }
  },
  {
    path: '/transaction-records',
    name: 'TransactionRecords',
    component: TransactionRecords,
    meta: { requiresAuth: true }
  },
  {
    path: '/notifications',
    name: 'NotificationManagement',
    component: NotificationManagement
  },
  {
    path: '/maintenance',
    name: 'MaintenanceManagement',
    component: MaintenanceManagement,
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
    if (token && user.userId) {
      if (user.role === 2) {
        next('/user-management');
      } else {
        next('/admin');
      }
    } else {
      next();
    }
  } else {
    if (token && user.userId) {
      next();
    } else {
      next('/login');
    }
  }
});

export default router;