# BackEnd
소프트웨어공학 바이브코딩 과제 BE
습관 추적 및 관리 서비스 **ROUTINER**의 백엔드 API 서버입니다.

## 기술 스택

- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0 (AWS RDS)
- Gradle
- Swagger (springdoc-openapi)

## 인프라

- AWS EC2 (Ubuntu)
- AWS RDS (MySQL)
- Nginx + Certbot (HTTPS)
- GitHub Actions (CI/CD)

## 프로젝트 구조

```
src/main/java/org/swengineer
├── auth                          # 인증
│   ├── controller/AuthController
│   ├── service/AuthService
│   ├── dto/request               # SignUpRequest, LoginRequest
│   ├── dto/response              # TokenResponse
│   └── code                      # AuthErrorCode, AuthSuccessCode
├── habit                         # 습관 관리
│   ├── controller/HabitController
│   ├── service/HabitService
│   ├── dto/request               # CreateHabitRequest, UpdateHabitRequest
│   ├── dto/response              # HabitResponse, HabitListResponse, TodayHabitResponse
│   ├── entity                    # Habit, enums (HabitCategory, FrequencyType)
│   ├── repository/HabitRepository
│   └── code                      # HabitErrorCode, HabitSuccessCode
├── checkin                       # 체크인 (습관 완료 기록)
│   ├── controller/CheckInController
│   ├── service/CheckInService
│   ├── entity/CheckIn
│   └── repository/CheckInRepository
├── common/base/BaseEntity        # 공통 엔티티 (created_at, updated_at, deleted_at)
└── global/api
    ├── config                    # SecurityConfig, SwaggerConfig, WebConfig
    ├── jwt                       # JwtTokenProvider, JwtAuthenticationFilter
    ├── code                      # ResultCode, ErrorResultCode, SuccessResultCode
    ├── exception                 # CustomException, GlobalExceptionHandler
    └── response/dto              # ApiResponse, SuccessResponse, FailureResponse
```

## API 명세

### 인증

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/auth/signup` | 회원가입 |
| POST | `/api/v1/auth/login` | 로그인 |

### 습관

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/habits` | 습관 등록 |
| GET | `/api/v1/habits/today` | 오늘의 습관 조회 (메인 홈) |
| GET | `/api/v1/habits` | 습관 목록 조회 (전체보기) |
| PUT | `/api/v1/habits/{habitId}` | 습관 수정 |
| DELETE | `/api/v1/habits/{habitId}` | 습관 삭제 |


> 전체 API 문서: https://loutiner.p-e.kr/swagger-ui/index.html

## 로컬 실행 방법

### 1. 프로젝트 클론

```bash
git clone https://github.com/SW-TaskTracker/BackEnd.git
cd BackEnd
```

### 2. 로컬 설정 파일 생성

`src/main/resources/application-local.yml` 생성:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/routiner
    username: root
    password: 본인DB비밀번호
```

### 3. 실행

IntelliJ에서 Run Configuration → VM options에 추가:

```
-Dspring.profiles.active=local
```

또는 터미널에서:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 4. Swagger 접속

```
http://localhost:8080/swagger-ui/index.html
```

## 브랜치 전략

```
main ── 배포 브랜치 (push 시 자동 배포)
 └── feat/#이슈번호/기능명 ── 기능 개발 브랜치
 └── infra/#이슈번호/설명 ── 인프라 관련 브랜치
```

## 팀원

| 이름 | 역할                |
|------|-------------------|
| 변희민 | 인증, 습관 관리, 인프라/배포 |