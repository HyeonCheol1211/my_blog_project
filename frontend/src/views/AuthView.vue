<template>
  <div class="auth-container">
    <h2>로그인</h2>
    <div class="login-form">
      <div class="input-group">
        <label>이메일</label>
        <input v-model="email" type="email" placeholder="email@example.com" />
      </div>
      <div class="input-group">
        <label>비밀번호</label>
        <input v-model="password" type="password" placeholder="비밀번호를 입력하세요" />
      </div>
      <button @click="handleLogin" class="login-btn">로그인</button>
      <p class="switch-auth">
        계정이 없으신가요? <router-link to="/signup">회원가입하기</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const email = ref('');
const password = ref('');

const handleLogin = () => {
  if (email.value && password.value) {
    alert(email.value + '님, 환영합니다!');
    router.push('/profile'); // 로그인 성공 시 프로필로 이동
  } else {
    alert('이메일과 비밀번호를 입력해주세요.');
  }
};
</script>

<style scoped>
.auth-container { max-width: 400px; margin: 50px auto; padding: 30px; border: 1px solid #eee; border-radius: 10px; }
.input-group { margin-bottom: 15px; text-align: left; }
.input-group label { display: block; margin-bottom: 5px; font-weight: bold; }
.input-group input { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
.login-btn { width: 100%; padding: 12px; background: #42b983; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
</style>