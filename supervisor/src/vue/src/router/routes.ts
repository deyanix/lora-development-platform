import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      // {
      //   path: '/',
      //   component: () => import('pages/IndexPage.vue'),
      // },
      {
        name: 'Dashboard',
        path: '/',
        component: () => import('pages/Dashboard/DashboardPage.vue'),
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
