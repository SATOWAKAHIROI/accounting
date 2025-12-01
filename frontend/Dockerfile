# Build stage
FROM node:18-alpine AS build

WORKDIR /app

# package.jsonとpackage-lock.jsonをコピーして依存関係をインストール
COPY package*.json ./
RUN npm ci

# ソースコードをコピーしてビルド
COPY . .

# ビルド時の環境変数（デフォルト値を設定）
ARG VITE_API_URL=http://localhost:8080/api
ENV VITE_API_URL=$VITE_API_URL

RUN npm run build

# Runtime stage (Nginx)
FROM nginx:alpine

# ビルド成果物をNginxの公開ディレクトリにコピー
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx設定ファイルをコピー
COPY nginx.conf /etc/nginx/conf.d/default.conf

# ポート公開
EXPOSE 80

# Nginx起動
CMD ["nginx", "-g", "daemon off;"]
