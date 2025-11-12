#!/bin/bash

# ============================================
# FoodieHub Git 저장소 정리 스크립트
# ============================================

echo "🔧 Git 저장소 정리 시작..."

# 1. 현재 상태 확인
echo ""
echo "📋 1단계: 현재 Git 상태 확인"
git status

# 2. 이미 커밋된 불필요한 파일 제거
echo ""
echo "🗑️  2단계: Git 캐시에서 불필요한 파일 제거 (로컬 파일은 유지됨)"

# bin 폴더 제거
if [ -d "bin/" ]; then
    echo "   - bin/ 폴더 제거 중..."
    git rm -r --cached bin/
fi

# uploads 폴더 제거
if [ -d "uploads/" ]; then
    echo "   - uploads/ 폴더 제거 중..."
    git rm -r --cached uploads/
fi

# application.properties 제거
if [ -f "src/main/resources/application.properties" ]; then
    echo "   - application.properties 제거 중..."
    git rm --cached src/main/resources/application.properties
fi

# 3. 수정된 README.md와 .gitignore 추가
echo ""
echo "📝 3단계: 수정된 파일 추가"
git add README.md
git add .gitignore

# 4. 상태 확인
echo ""
echo "📋 4단계: 변경사항 확인"
git status

# 5. 커밋
echo ""
echo "💾 5단계: 커밋 생성"
git commit -m "chore: Update .gitignore and README.md

- Fix GitHub repository links (Hwichang-0222)
- Mark v1.0.0 features as completed
- Exclude bin/, uploads/, application.properties from Git
- Add IntelliJ and VSCode ignore patterns
- Update future plans (AI features, notifications)"

# 6. 푸시 (선택)
echo ""
echo "🚀 GitHub에 푸시하려면 다음 명령어를 실행하세요:"
echo "   git push origin main"
echo ""
echo "✅ Git 정리 완료!"
