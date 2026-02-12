<template>
  <div class="form-container">
    <h2>{{ isEdit ? '게시글 수정' : '새 게시글 작성' }}</h2>
    <form @submit.prevent="submitForm">
      <div class="input-group">
        <label>제목</label>
        <input v-model="post.title" placeholder="제목을 입력하세요" required />
      </div>
      <div class="input-group">
        <label>내용</label>
        <textarea v-model="post.content" rows="10" placeholder="내용을 입력하세요" required></textarea>
      </div>
      <div class="actions">
        <button type="submit" class="save-btn">{{ isEdit ? '수정완료' : '등록하기' }}</button>
        <button type="button" @click="$router.back()" class="cancel-btn">취소</button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const isEdit = ref(false);
// 입력 폼과 연결된 반응형 데이터
const post = ref({
  title: '',
  content: ''
});

// 페이지가 열릴 때 실행
onMounted(() => {
  const id = route.params.id;

  if (id) {
    // ID가 있다면 수정 모드
    isEdit.value = true;
    fetchPostData(id);
  }
});

// 기존 데이터를 불러오는 함수
const fetchPostData = (id) => {
  // 실제로는 axios.get(`/api/posts/${id}`)를 호출해서 데이터를 가져와야 합니다.
  // 지금은 기능을 확인하기 위해 더미 데이터를 채워넣습니다.

  // 예시: 백엔드에서 받아온 데이터라고 가정
  const dummyData = {
    1: { title: '스프링 부트와 Vue.js로 만드는 현대적 웹 서비스', content: '기존에 작성된 상세 내용입니다...' },
    2: { title: '나의 첫 도커(Docker) 사용기', content: '도커 환경 설정에 대한 기존 내용입니다.' }
  };

  const existingPost = dummyData[id];

  if (existingPost) {
    post.value.title = existingPost.title;
    post.value.content = existingPost.content;
  } else {
    // 만약 더미데이터에 없는 ID라면 간단한 예시 데이터로 채움
    post.value.title = id + "번 글의 기존 제목";
    post.value.content = "이것은 기존에 작성되었던 본문 내용입니다. 여기서부터 수정을 시작하세요.";
  }
};

const submitForm = () => {
  if (isEdit.value) {
    // axios.put(`/api/posts/${route.params.id}`, post.value) 호출 예정
    alert("수정이 완료되었습니다!");
  } else {
    // axios.post('/api/posts', post.value) 호출 예정
    alert("새 글이 등록되었습니다!");
  }
  router.push('/');
};
</script>

<style scoped>
.form-container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 10px; }
.input-group { margin-bottom: 20px; text-align: left; }
.input-group label { display: block; margin-bottom: 8px; font-weight: bold; }
.input-group input, .input-group textarea { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; font-family: inherit; }
.actions { display: flex; gap: 10px; }
.save-btn { flex: 1; background: #42b983; color: white; border: none; padding: 12px; border-radius: 5px; cursor: pointer; font-weight: bold; }
.cancel-btn { flex: 1; background: #eee; border: none; padding: 12px; border-radius: 5px; cursor: pointer; }
</style>