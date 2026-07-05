import { createRouter, createWebHashHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/Login.vue'),
      meta: { noAuth: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/login/Register.vue'),
      meta: { noAuth: true }
    },
    {
      path: '/',
      component: () => import('@/views/layout/MainLayout.vue'),
      redirect: '/books',
      children: [
        {
          path: 'books',
          name: 'BookList',
          component: () => import('@/views/book/BookList.vue')
        },
        {
          path: 'books/:id',
          name: 'BookDetail',
          component: () => import('@/views/book/BookDetail.vue')
        },
        {
          path: 'borrows',
          name: 'MyBorrows',
          component: () => import('@/views/borrow/MyBorrows.vue')
        },
        {
          path: 'notices',
          name: 'NoticeList',
          component: () => import('@/views/notice/NoticeList.vue')
        },
        {
          path: 'admin/users',
          name: 'UserManage',
          component: () => import('@/views/admin/UserManage.vue'),
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/books',
          name: 'BookManage',
          component: () => import('@/views/admin/BookManage.vue'),
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/notices',
          name: 'NoticeManage',
          component: () => import('@/views/admin/NoticeManage.vue'),
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/borrows',
          name: 'BorrowManage',
          component: () => import('@/views/admin/BorrowManage.vue'),
          meta: { role: 'ADMIN' }
        }
      ]
    }
  ]
})

// Navigation guard
router.beforeEach((to, _from, next) => {
  const token = getToken()

  if (to.meta.noAuth) {
    if (token && (to.path === '/login' || to.path === '/register')) {
      next('/')
    } else {
      next()
    }
    return
  }

  if (!token) {
    next('/login')
    return
  }

  next()
})

export default router
