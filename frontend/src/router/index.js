import { createRouter, createWebHistory } from 'vue-router';
// 나중에 만들 컴포넌트들을 연결합니다.
import HomeView from '../views/HomeView.vue';
import PostFormView from '../views/PostFormView.vue';
import AuthView from '../views/AuthView.vue';
import ProfileView from '../views/ProfileView.vue';
import ProfileEditView from '../views/ProfileEditView.vue';
import PostDetailView from '../views/PostDetailView.vue';
import SignupView from '../views/SignupView.vue';

const routes = [
    {
        path: '/',
        name: 'home',
        component: HomeView
    },
    {
        path: '/write',
        name: 'post-create',
        component: PostFormView
    },
    {
        path: '/edit/:id',
        name: 'post-edit',
        component: PostFormView,
        props: true // URL의 id를 컴포넌트의 props로 전달합니다.
    },
    {
        path: '/login',
        name: 'login',
        component: AuthView
    },
    {
        path: '/profile',
        name: 'profile',
        component: ProfileView
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/AuthView.vue')
    },
    {
        path: '/signup', // 회원가입 경로 추가
        name: 'signup',
        component: SignupView
    },
    {
        path: '/profile',
        name: 'profile',
        component: () => import('../views/ProfileView.vue')
    },
    {
        path: '/',
        name: 'home',
        component: () => import('../views/HomeView.vue')
    },
    {
        path: '/post/:id', // 상세 페이지 경로 추가
        name: 'post-detail',
        component: PostDetailView
    },
    {
        path: '/profile/edit', // 새로 추가된 경로
        name: 'profile-edit',
        component: ProfileEditView
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;