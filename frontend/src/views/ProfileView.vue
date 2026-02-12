<template>
  <div class="profile-container">
    <h2>내 프로필</h2>
    <div class="profile-card">
      <div class="avatar">U</div>
      <div class="info">
        <p><strong>이름:</strong> {{ user.name }}</p>
        <p><strong>이메일:</strong> {{ user.email }}</p>
        <p><strong>나이:</strong> {{ user.age }}세</p>
        <p><strong>성별:</strong> {{ user.gender }}</p>
        <p><strong>가입일:</strong> 2025-12-19</p>
      </div>
    </div>

    <div class="button-group">
      <button @click="$router.push('/profile/edit')" class="btn-go-edit">프로필 수정하기</button>
      <button @click="handleLogout" class="btn-logout">로그아웃</button>
      <button @click="handleWithdraw" class="btn-withdraw">회원 탈퇴</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 더미 데이터에 나이와 성별 추가
const user = ref({
  name: '홍길동',
  email: 'user@test.com',
  age: 25,
  gender: '남성'
});


const handleWithdraw = () => {
  if (confirm("정말로 탈퇴하시겠습니까? 그동안 작성한 모든 정보가 사라집니다.")) {
    // 실제로는 여기서 백엔드 API를 호출합니다.
    // axios.delete('/api/users/me').then(...)

    alert("탈퇴 처리가 완료되었습니다. 이용해 주셔서 감사합니다.");
    router.push('/signup'); // 가입 페이지나 홈으로 이동
  }
};

const handleLogout = () => {
  alert("로그아웃 되었습니다.");
  router.push('/login');
};
</script>

<style scoped>
.profile-card { display: flex; align-items: center; gap: 20px; padding: 25px; background: #f9f9f9; border-radius: 12px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.avatar { width: 70px; height: 70px; background: #42b983; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 28px; font-weight: bold; }
.info { text-align: left; }
.info p { margin: 8px 0; line-height: 1.4; }
.button-group { display: flex; flex-direction: column; gap: 10px; }
.btn-go-edit { background: #42b983; color: white; border: none; padding: 12px; border-radius: 8px; cursor: pointer; font-weight: bold; }
.btn-logout { background: #ff4757; color: white; border: none; padding: 12px; border-radius: 8px; cursor: pointer; }
.btn-withdraw {
  margin-top: 30px;
  background: none;
  border: none;
  color: #999;
  text-decoration: underline;
  cursor: pointer;
  font-size: 0.9rem;
}
.btn-withdraw:hover {
  color: #ff4757;
}
</style>