<template>
  <div class="edit-container">
    <h2>프로필 수정</h2>
    <div class="edit-card">
      <div class="input-group">
        <label>이름</label>
        <input v-model="user.name" />
      </div>
      <div class="input-group">
        <label>나이</label>
        <input v-model.number="user.age" type="number" />
      </div>
      <div class="input-group">
        <label>성별</label>
        <div class="radio-group">
          <label><input type="radio" v-model="user.gender" value="남성" /> 남성</label>
          <label><input type="radio" v-model="user.gender" value="여성" /> 여성</label>
        </div>
      </div>

      <hr class="divider" />

      <div class="password-section">
        <h3>비밀번호 변경</h3>
        <div class="input-group">
          <label>현재 비밀번호</label>
          <input v-model="passwordForm.currentPassword" type="password" placeholder="현재 비밀번호를 입력하세요" />
        </div>
        <div class="input-group">
          <label>새 비밀번호</label>
          <input v-model="passwordForm.newPassword" type="password" placeholder="새 비밀번호를 입력하세요" />
        </div>
        <div class="input-group">
          <label>새 비밀번호 확인</label>
          <input v-model="passwordForm.confirmPassword" type="password" placeholder="한 번 더 입력하세요" />
          <small v-if="passwordError" class="error-msg">{{ passwordError }}</small>
        </div>
      </div>

      <div class="actions">
        <button @click="saveProfile" class="btn-save">변경사항 저장</button>
        <button @click="$router.push('/profile')" class="btn-cancel">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const user = ref({ name: '', age: null, gender: '', email: '' });
const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 비밀번호 일치 여부 실시간 체크
const passwordError = computed(() => {
  if (passwordForm.value.newPassword && passwordForm.value.confirmPassword) {
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
      return "새 비밀번호가 일치하지 않습니다.";
    }
  }
  return "";
});

onMounted(() => {
  // 더미 데이터 로드
  user.value = { name: '홍길동', age: 25, gender: '남성', email: 'user@test.com' };
});

const saveProfile = () => {
  // 비밀번호를 입력했다면 유효성 검사
  if (passwordForm.value.newPassword || passwordForm.value.currentPassword) {
    if (!passwordForm.value.currentPassword) {
      alert("비밀번호를 변경하려면 현재 비밀번호를 입력해야 합니다.");
      return;
    }
    if (passwordError.value) {
      alert("새 비밀번호 확인이 일치하지 않습니다.");
      return;
    }
  }

  console.log("저장할 데이터:", { ...user.value, ...passwordForm.value });
  alert("성공적으로 수정되었습니다.");
  router.push('/profile');
};
</script>

<style scoped>
.edit-container { max-width: 450px; margin: 0 auto; padding: 20px; }
.edit-card { background: #fff; border: 1px solid #ddd; padding: 30px; border-radius: 12px; }
.divider { border: 0; border-top: 1px solid #eee; margin: 25px 0; }
h3 { font-size: 1.1rem; margin-bottom: 20px; color: #333; text-align: left; }
.input-group { margin-bottom: 15px; text-align: left; }
.input-group label { display: block; margin-bottom: 5px; font-weight: bold; font-size: 0.9rem; }
.input-group input { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 6px; box-sizing: border-box; }
.radio-group { display: flex; gap: 15px; }
.error-msg { color: #ff4757; font-size: 0.8rem; margin-top: 5px; }
.actions { display: flex; gap: 10px; margin-top: 25px; }
.btn-save { flex: 1; background: #42b983; color: white; border: none; padding: 12px; border-radius: 6px; cursor: pointer; font-weight: bold; }
.btn-cancel { flex: 1; background: #eee; border: none; padding: 12px; border-radius: 6px; cursor: pointer; }
</style>