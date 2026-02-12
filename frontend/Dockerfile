# frontend/Dockerfile 수정
FROM node:22-slim

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# Vite 서버 실행
CMD ["npm", "run", "dev", "--", "--host"]