<template>
  <div id="app">
    <nav>
      <router-link to="/">홈</router-link> |
      <router-link to="/write">글쓰기</router-link> |
      <router-link to="/login">로그인</router-link> |
      <router-link to="/profile">프로필</router-link>
    </nav>

    <router-view />
  </div>
</template>

<style>
nav { padding: 30px; text-align: center; border-bottom: 1px solid #eee; margin-bottom: 20px; }
nav a { font-weight: bold; color: #2c3e50; text-decoration: none; margin: 0 10px; }
nav a.router-link-exact-active { color: #42b983; }
</style>