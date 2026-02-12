<template>
  <div class="auth-container">
    <h2>회원가입</h2>
    <form @submit.prevent="handleSignup" class="signup-form">
      <div class="input-group">
        <label>이름</label>
        <input v-model="form.name" type="text" placeholder="실명을 입력하세요" required />
      </div>

      <div class="input-group">
        <label>이메일 (아이디)</label>
        <input v-model="form.email" type="email" placeholder="example@test.com" required />
      </div>

      <div class="input-group">
        <label>비밀번호</label>
        <input v-model="form.password" type="password" placeholder="8자 이상 입력하세요" required />
      </div>

      <div class="input-group">
        <label>비밀번호 확인</label>
        <input v-model="form.confirmPassword" type="password" placeholder="비밀번호를 한 번 더 입력하세요" required />
        <small v-if="passwordError" class="error-msg">{{ passwordError }}</small>
      </div>

      <div class="row-group">
        <div class="input-group flex-1">
          <label>나이</label>
          <input v-model.number="form.age" type="number" placeholder="나이" required />
        </div>
        <div class="input-group flex-1">
          <label>성별</label>
          <select v-model="form.gender" required>
            <option value="" disabled>선택</option>
            <option value="남성">남성</option>
            <option value="여성">여성</option>
            <option value="기타">기타</option>
          </select>
        </div>
      </div>

      <button type="submit" class="signup-btn" :disabled="!!passwordError">가입하기</button>

      <p class="switch-auth">
        이미 계정이 있으신가요? <router-link to="/login">로그인하기</router-link>
      </p>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

const form = ref({
  name: '',
  email: '',
  password: '',
  confirmPassword: '',
  age: null,
  gender: ''
});

const passwordError = computed(() => {
  if (form.value.password && form.value.confirmPassword) {
    if (form.value.password !== form.value.confirmPassword) {
      return "비밀번호가 일치하지 않습니다.";
    }
  }
  return "";
});

const handleSignup = async () => {
  try {
    // 백엔드 API 호출
    const response = await axios.post('http://localhost:8080/api/auth/sign-up', {
      name: form.value.name,
      email: form.value.email,
      password: form.value.password,
      age: form.value.age,
      gender: form.value.gender
    });

    // 성공 시 처리
    console.log("서버 응답:", response.data);
    alert(`${form.value.name}님, 회원가입을 축하합니다!`);
    router.push('/login');

  } catch (error) {
    // 에러 발생 시 처리 (예: 중복 이메일 등)
    console.error("회원가입 실패:", error);
    alert(error.response?.data?.message || "회원가입 중 오류가 발생했습니다.");
  }
};
</script>

<style scoped>
.auth-container { max-width: 450px; margin: 50px auto; padding: 30px; border: 1px solid #eee; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.input-group { margin-bottom: 15px; text-align: left; }
.input-group label { display: block; margin-bottom: 5px; font-weight: bold; font-size: 0.9rem; }
.input-group input, .input-group select { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 6px; box-sizing: border-box; }
.row-group { display: flex; gap: 15px; }
.flex-1 { flex: 1; }
.signup-btn { width: 100%; padding: 12px; background: #42b983; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 1rem; font-weight: bold; margin-top: 10px; }
.signup-btn:disabled { background: #ccc; cursor: not-allowed; }
.error-msg { color: #ff4757; font-size: 0.8rem; margin-top: 5px; }
.switch-auth { margin-top: 20px; font-size: 0.9rem; color: #666; }
.switch-auth a { color: #42b983; text-decoration: none; font-weight: bold; }
</style>