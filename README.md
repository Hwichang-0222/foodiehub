# 🍽️ FoodieHub

> 맛집 리뷰 및 커뮤니티 플랫폼

FoodieHub는 사용자들이 맛집을 공유하고 리뷰를 작성하며, 커뮤니티를 통해 소통할 수 있는 웹 플랫폼입니다.

[![Release](https://img.shields.io/badge/release-v1.0.0-blue)](https://github.com/your-repo/foodiehub)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

---

## 📋 목차

- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시작하기](#시작하기)
- [프로젝트 구조](#프로젝트-구조)
- [화면 구성](#화면-구성)
- [개발 가이드](#개발-가이드)
- [버전 정보](#버전-정보)

---

## ✨ 주요 기능

### 👤 회원 관리
- 회원가입 / 로그인 (Spring Security)
- 소셜 로그인 (카카오, 네이버)
- 아이디/비밀번호 찾기
- 마이페이지 (내 리뷰, 내 게시글)
- 프로필 이미지 업로드

### 🍴 맛집 관리
- 맛집 목록 조회 (지역/카테고리 필터링)
- 맛집 상세 정보 (위치, 메뉴, 리뷰)
- 카카오맵 API 연동
- 평균 별점 및 리뷰 통계

### ⭐ 리뷰 시스템
- 별점 리뷰 작성 (1~5점)
- 리뷰 이미지 업로드 (최대 3장)
- 리뷰 수정/삭제
- 댓글 및 대댓글

### 📝 게시판
- 4가지 카테고리 (공지, 일반, 등급요청, 맛집추천)
- 게시글 작성/수정/삭제
- 검색 기능 (제목+내용)
- 페이지네이션
- 관리자 답글 (등급요청, 맛집추천)

### 🔐 권한 관리
- 역할 기반 접근 제어 (USER, OWNER, ADMIN)
- URL 단위 권한 체크
- 게시글/리뷰 작성자 검증

### 🛠️ 관리자 기능
- 회원 관리 (역할 변경, 검색)
- 식당 관리 (오너 지정, CRUD)
- 게시판 관리 (공지 작성, 미답변 관리)
- 통합 대시보드

---

## 🛠️ 기술 스택

### Backend
- **Java** 17
- **Spring Boot** 3.5.6
- **Spring Security** - 인증/인가
- **MyBatis** 3.0.5 - ORM
- **Thymeleaf** - 템플릿 엔진

### Frontend
- **HTML5 / CSS3**
- **JavaScript** (Vanilla)
- **Thymeleaf**
- **Kakao Maps API**

### Database
- **MySQL** 8.0.34
- **HikariCP** - 커넥션 풀

### Build Tool
- **Gradle**

### Dev Tools
- **Lombok**
- **Spring DevTools**
- **JUnit 5** - 테스트

---

## 🚀 시작하기

### 필수 요구사항

```bash
Java 17 이상
MySQL 8.0 이상
Gradle 8.x
```

### 설치 및 실행

1. **저장소 클론**
```bash
git clone https://github.com/your-username/foodiehub.git
cd foodiehub
```

2. **데이터베이스 설정**
```sql
CREATE DATABASE foodiehub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **application.properties 설정**
```properties
# 데이터베이스 연결
spring.datasource.url=jdbc:mysql://localhost:3306/foodiehub
spring.datasource.username=root
spring.datasource.password=your_password

# Kakao Map API Key
kakao.map.api.key=your_kakao_api_key
```

4. **애플리케이션 실행**
```bash
./gradlew bootRun
```

5. **브라우저에서 접속**
```
http://localhost:8080
```

### 초기 관리자 계정

회원가입 후 DB에서 직접 권한 변경:
```sql
UPDATE user SET role = 'ROLE_ADMIN' WHERE email = 'your_email@example.com';
```

---

## 📁 프로젝트 구조

```
src/main/
├── java/org/embed/
│   ├── controller/          # 컨트롤러 계층
│   │   ├── UserController
│   │   ├── RestaurantController
│   │   ├── ReviewController
│   │   ├── BoardController
│   │   └── AdminController
│   ├── service/             # 서비스 계층
│   │   ├── impl/            # 서비스 구현체
│   │   └── [Service interfaces]
│   ├── mapper/              # MyBatis 매퍼
│   ├── dto/                 # 데이터 전송 객체
│   ├── domain/              # 도메인 모델
│   ├── config/              # 설정 클래스
│   │   ├── SecureConfiguration    # Spring Security
│   │   ├── WebMvcConfiguration    # MVC 설정
│   │   └── DBConfiguration        # DB 설정
│   └── advice/              # 전역 예외 처리
│
└── resources/
    ├── mapper/              # MyBatis XML
    │   ├── sql-user.xml
    │   ├── sql-restaurant.xml
    │   ├── sql-review.xml
    │   ├── sql-board.xml
    │   └── sql-image.xml
    ├── templates/           # Thymeleaf 템플릿
    │   ├── layout/          # 공통 레이아웃
    │   ├── user/            # 회원 페이지
    │   ├── restaurant/      # 맛집 페이지
    │   ├── board/           # 게시판 페이지
    │   └── error/           # 에러 페이지
    └── static/
        ├── css/             # 스타일시트
        ├── js/              # 자바스크립트
        └── images/          # 이미지 파일
```

---

## 🖼️ 화면 구성

### 메인 페이지
- 맛집 목록 (카드 형식)
- 지역/카테고리 필터
- 검색 기능

### 맛집 상세
- 기본 정보 (주소, 카테고리, 영업시간)
- 카카오맵 위치
- 리뷰 목록 (별점 순, 최신 순)
- 리뷰 작성 폼

### 게시판
- 카테고리 탭 (공지/일반/등급요청/맛집추천)
- 게시글 목록 (페이지네이션)
- 검색 기능
- 공지사항 고정

### 관리자 대시보드
- 탭 메뉴 (회원관리/식당관리/게시판관리)
- 회원 검색 및 역할 변경
- 식당 오너 지정
- 미답변 요청 관리

---

## 📖 개발 가이드

### 코드 스타일

**Java 주석 규칙:**
```java
/* ============================================
   기능 분류 (예: 회원가입, 로그인)
============================================ */

// 메서드 기능 설명 - 한 줄
public void method() {
    // 중요한 로직에만 주석 (보안, DB 무결성 등)
}
```

**CSS 구조:**
```css
/* ============================================
   폴더명 공통 스타일
============================================ */

/* 페이지1 + 페이지2 공통 (선택) */

/* ============================================
   페이지명 (고유 스타일)
============================================ */
```

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트
./gradlew test --tests "org.embed.mappertest.UserMapperTest"
```

### 빌드

```bash
# JAR 파일 생성
./gradlew bootJar

# 생성된 파일 위치
build/libs/foodiehub-1.0.0.jar
```

---

## 🔐 보안 설정

### 비밀번호 암호화
- BCrypt 알고리즘 사용
- 강도: 10 rounds

### 접근 제어
```
/admin/**     → ROLE_ADMIN
/user/mypage  → ROLE_USER
/restaurant/edit/** → ROLE_ADMIN, ROLE_OWNER
```

### CSRF 보호
- 모든 POST 요청에 CSRF 토큰 검증
- Thymeleaf 자동 토큰 생성

---

## 🗄️ 데이터베이스 스키마

### 주요 테이블
- **user** - 사용자 정보
- **restaurant** - 맛집 정보
- **review** - 리뷰 (부모-자식 구조로 댓글 관리)
- **board** - 게시판 (부모-자식 구조로 답글 관리)
- **image** - 리뷰 이미지

### ERD
```
user (1) ─── (N) review ─── (N) image
  │                │
  │                └─── (1) restaurant
  │
  └─── (N) board
```

---

## 📌 버전 정보

### v1.0.0 (2025-01-XX)
**기본 기능 구현 완료**
- ✅ Spring Security 인증/인가
- ✅ 맛집 CRUD 및 리뷰 시스템
- ✅ 게시판 시스템 (4개 카테고리)
- ✅ 관리자 대시보드
- ✅ 소셜 로그인 (카카오, 네이버)
- ✅ 에러 페이지 및 전역 예외 처리
- ✅ CSS 재구조화 및 UI/UX 개선

### 향후 계획 (v2.0)
- [ ] 메뉴 확인 및 추가기능
- [ ] 식사 추천 알고리즘
- [ ] 맛집 북마크 기능
- [ ] 소셜네트워크 로그인 기능
- [ ] 맛집 리뷰 요약 기능

---

## 📝 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다.

---

## 👥 기여자

- **개발자** - [@your-username](https://github.com/your-username)

---

## 📧 문의

프로젝트에 대한 문의사항은 이슈를 등록해주세요.

**GitHub Issues**: https://github.com/your-username/foodiehub/issues

---

⭐ 이 프로젝트가 마음에 드셨다면 Star를 눌러주세요!
