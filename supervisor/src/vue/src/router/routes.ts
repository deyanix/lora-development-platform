import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        name: 'Dashboard',
        path: '/',
        component: () => import('pages/Dashboard/DashboardPage.vue'),
      },
      {
        name: 'Statistics',
        path: '/statistics',
        component: () => import('pages/Statistics/StatisticsPage.vue'),
      },
      {
        name: 'Node',
        path: '/node/:id',
        component: () => import('pages/Node/NodePage.vue'),
      },
      {
        path: '/:catchAll(.*)*',
        component: () => import('pages/Error/ErrorNotFound.vue'),
      },
    ],
  },
];

export default routes;
