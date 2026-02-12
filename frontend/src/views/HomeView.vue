<template>
  <div class="home-container">
    <div class="header-section">
      <h2>블로그 포스트</h2>
      <p class="subtitle">총 {{ posts.length }}개의 글이 있습니다.</p>
    </div>

    <div v-if="posts.length > 0" class="post-list">
      <div v-for="post in posts" :key="post.id" class="post-card" @click="$router.push('/post/' + post.id)">
        <div class="post-info">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-excerpt">{{ post.content.substring(0, 80) }}...</p>
          <div class="post-footer">
            <span class="author">작성자: {{ post.author }}</span>
            <span class="date">{{ post.date }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="no-posts">
      <p>게시글이 없습니다. 첫 글을 작성해보세요!</p>
      <button @click="$router.push('/write')">글 쓰러 가기</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 풍성한 더미 데이터 생성
const posts = ref([
  {
    id: 1,
    title: '스프링 부트와 Vue.js로 만드는 현대적 웹 서비스',
    content: '이 포스트에서는 도커를 활용하여 프론트엔드와 백엔드를 독립적인 컨테이너로 띄우고, 데이터베이스까지 연동하는 전체 과정을 다룹니다. 풀스택 개발의 기초를 다지기에 아주 좋은 예제입니다.',
    author: '관리자',
    date: '2025-12-19'
  },
  {
    id: 2,
    title: '나의 첫 도커(Docker) 사용기',
    content: '도커를 처음 접했을 때는 복잡해 보였지만, 한 번 익숙해지니 환경 설정의 지옥에서 탈출할 수 있었습니다. 특히 docker-compose는 여러 서비스를 동시에 관리할 때 필수적인 도구입니다.',
    author: '홍길동',
    date: '2025-12-18'
  },
  {
    id: 3,
    title: 'JPA 영속성 컨텍스트의 이해',
    content: 'Spring Data JPA를 사용하면서 가장 중요한 개념 중 하나는 영속성 컨텍스트입니다. 엔티티의 생명주기를 이해해야 효율적인 DB 조작이 가능해집니다.',
    author: '공부벌레',
    date: '2025-12-17'
  },
  {
    id: 4,
    title: '반응형 UI를 위한 Vue 3 Composition API',
    content: 'Vue 3의 script setup 방식은 코드를 매우 간결하게 만들어줍니다. ref와 reactive를 적절히 섞어 쓰면 복잡한 상태 관리도 식은 죽 먹기죠.',
    author: 'Vue마스터',
    date: '2025-12-16'
  }
]);
</script>

<style scoped>
.home-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header-section {
  text-align: left;
  margin-bottom: 40px;
  border-bottom: 2px solid #42b983;
  padding-bottom: 10px;
}

.subtitle {
  color: #666;
  font-size: 0.9rem;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.post-card {
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 25px;
  background-color: #fff;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
  text-align: left;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
}

.post-title {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 1.4rem;
}

.post-excerpt {
  color: #555;
  line-height: 1.6;
  margin-bottom: 20px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: #999;
}

.no-posts {
  padding: 100px 0;
  color: #888;
}

.no-posts button {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 15px;
}
</style>