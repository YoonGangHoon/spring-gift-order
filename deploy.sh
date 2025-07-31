#!/bin/bash

echo "==> 로컬 배포 스크립트 시작"

# 1. 빌드
echo "==> Gradle 빌드 시작"
./gradlew clean build -x test

if [ $? -ne 0 ]; then
  echo "❌ 빌드 실패. 배포 중단."
  exit 1
fi

# 2. JAR 경로 설정
JAR_PATH=$(ls ./build/libs/*.jar | grep -v plain | head -n 1)

if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR 파일을 찾을 수 없습니다: $JAR_PATH"
  exit 1
fi

JAR_NAME=$(basename "$JAR_PATH")
DEPLOY_PATH="./$JAR_NAME"

# 3. 기존 프로세스 종료
echo "==> 기존 프로세스 종료 시도 중..."
PID=$(pgrep -f "$JAR_NAME")

if [ -n "$PID" ]; then
  echo "==> 프로세스 종료 중 (PID: $PID)"
  kill -15 "$PID"
  sleep 2
else
  echo "==> 실행 중인 프로세스 없음"
fi

# 4. JAR 복사
echo "==> JAR 복사 중..."
cp "$JAR_PATH" "$DEPLOY_PATH"

# 5. 앱 실행
echo "==> 애플리케이션 실행"
nohup java -jar "$DEPLOY_PATH" > app.log 2>&1 &

sleep 2

# 6. 실행 확인
NEW_PID=$(pgrep -f "$JAR_NAME")

if [ -n "$NEW_PID" ]; then
  echo "✅ 배포 완료 (PID: $NEW_PID)"
  echo "📄 로그: tail -f app.log"
else
  echo "❌ 애플리케이션 실행 실패. 로그 확인 필요."
  echo "📄 로그: cat app.log"
fi