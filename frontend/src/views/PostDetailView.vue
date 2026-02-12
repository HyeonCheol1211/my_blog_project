<template>
  <div class="detail-container" v-if="post">
    <div class="detail-header">
      <button @click="$router.push('/')" class="btn-back">← 목록으로</button>
      <h2>{{ post.title }}</h2>
      <div class="post-meta">작성일: 2025-12-19 | 작성자: 관리자</div>
    </div>

    <div class="detail-content">
      {{ post.content }}
    </div>

    <div class="detail-actions">
      <button @click="$router.push('/edit/' + post.id)" class="btn-edit">수정하기</button>
      <button @click="handleDelete" class="btn-delete">삭제하기</button>
    </div>
  </div>
  <div v-else class="loading">글을 불러오는 중입니다...</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const post = ref(null);

onMounted(() => {
  const postId = route.params.id;
  // 실제로는 axios.get(`/api/posts/${postId}`)로 데이터를 가져옵니다.
  // 우선 테스트를 위한 가짜 데이터를 세팅합니다.
  post.value = {
    id: postId,
    title: postId + "번 게시글의 제목입니다",
    content: "이것은 상세 내용입니다. 백엔드와 연동하면 DB에 저장된 실제 본문이 여기에 나타나게 됩니다."
  };
});

const handleDelete = () => {
  if (confirm("정말로 이 글을 삭제하시겠습니까?")) {
    // 실제로는 axios.delete(`/api/posts/${post.value.id}`) 호출
    alert("삭제되었습니다.");
    router.push('/');
  }
};
</script>

<style scoped>
.detail-container { max-width: 700px; margin: 0 auto; padding: 30px; text-align: left; }
.btn-back { background: none; border: none; color: #42b983; cursor: pointer; margin-bottom: 20px; }
.detail-header { border-bottom: 2px solid #eee; padding-bottom: 20px; margin-bottom: 30px; }
.post-meta { color: #888; font-size: 0.9rem; margin-top: 10px; }
.detail-content { line-height: 1.8; font-size: 1.1rem; min-height: 300px; white-space: pre-wrap; }
.detail-actions { margin-top: 50px; border-top: 1px solid #eee; padding-top: 20px; display: flex; gap: 10px; }
.btn-edit { background: #42b983; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; }
.btn-delete { background: #ff4757; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; }
</style>