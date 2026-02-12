import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 위에서 만든 라우터 가져오기

const app = createApp(App)

app.use(router) // 라우터 등록
app.mount('#app')