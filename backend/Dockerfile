# Build stage
FROM maven:3.8.6-openjdk-11-slim AS build

WORKDIR /app

# pom.xmlをコピーして依存関係をダウンロード（キャッシュ活用）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ソースコードをコピーしてビルド
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:11-jre-jammy

WORKDIR /app

# ビルドステージから成果物をコピー
COPY --from=build /app/target/*.jar app.jar

# 環境変数設定
ENV JAVA_OPTS=""

# ポート公開
EXPOSE 8080

# アプリケーション起動
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
