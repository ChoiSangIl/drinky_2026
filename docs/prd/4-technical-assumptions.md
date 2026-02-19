# 4. Technical Assumptions

## 4.1 Repository Structure (저장소 구조)

**Monorepo (모노레포)**
- 프론트엔드와 백엔드를 단일 저장소에서 관리
- 1인 풀스택 개발자 환경에 적합
- 추천 도구: pnpm workspaces 또는 Turborepo

## 4.2 Service Architecture (서비스 아키텍처)

**Monolith (모놀리식)**
- MVP 단계에서 복잡한 마이크로서비스 불필요
- 단일 백엔드 서버로 모든 API 처리
- 추후 필요시 서비스 분리 가능한 구조로 설계

## 4.3 Tech Stack (기술 스택)

| 영역 | 선택 | 근거 |
|------|------|------|
| **Frontend** | React + TypeScript | 생태계 풍부, PWA 지원 우수, 채용/유지보수 용이 |
| **UI Framework** | TailwindCSS + shadcn/ui | 빠른 개발, 일관된 디자인 시스템 |
| **Backend** | Node.js (Express 또는 Fastify) | 프론트엔드와 언어 통일, 빠른 개발 |
| **Database** | PostgreSQL (Supabase) | 관계형 데이터 적합, Supabase로 인증/실시간 기능 포함 |
| **Hosting** | Vercel (Frontend) + Supabase (Backend/DB) | 무료 티어 활용, 빠른 배포 |
| **Push Notification** | Firebase Cloud Messaging (FCM) | 무료, PWA 지원 우수 |
| **Analytics** | Mixpanel 또는 PostHog | 사용자 행동 분석, 무료 티어 |

## 4.4 Testing Requirements (테스트 요구사항)

**Unit + Integration (단위 + 통합 테스트)**
- 핵심 비즈니스 로직(스트릭 계산, 기록 저장) 단위 테스트 필수
- API 엔드포인트 통합 테스트
- E2E 테스트는 MVP 이후 고려
- 테스트 도구: Vitest (프론트엔드), Jest (백엔드)

## 4.5 Additional Technical Assumptions (추가 기술 가정)

- **인증:** Supabase Auth 활용 (이메일, Google, Kakao 소셜 로그인)
- **상태 관리:** Zustand 또는 React Context (가벼운 상태 관리)
- **API 통신:** React Query (TanStack Query) - 캐싱, 로딩 상태 관리
- **PWA:** Vite PWA 플러그인 활용
- **암호화:** 음주 기록은 DB 레벨 암호화 (Supabase RLS + 컬럼 암호화)
- **CI/CD:** 개발 완료 후 GitHub Actions 배포 파이프라인 구성 (개발 중에는 로컬 환경에서만 동작)
- **개발 기간:** MVP 4-6주 (1인 풀스택 개발자 기준)

## 4.6 Data Requirements (데이터 요구사항)

### 핵심 데이터 엔터티

| 엔터티 | 주요 필드 | 설명 |
|--------|----------|------|
| **users** | id, email, nickname, created_at | 사용자 계정 정보 |
| **drinking_records** | id, user_id, date, did_drink, created_at, updated_at | 일별 음주 기록 |
| **user_settings** | user_id, notification_enabled, notification_time, fcm_token | 사용자 설정 |
| **notification_logs** | id, user_id, sent_at, message_type, status | 알림 발송 이력 |

### 데이터 정책

- **보존 기간:** 음주 기록은 계정 존재 시 무기한 보존
- **백업:** Supabase 자동 백업 (일 1회, Point-in-Time Recovery)
- **삭제 정책:** 계정 삭제 요청 시 30일 내 모든 데이터 영구 삭제
- **암호화:** 음주 기록은 민감 정보로 취급, DB 레벨 암호화 적용
- **익명화:** 분석용 데이터는 개인 식별 정보 제거 후 집계

## 4.7 Operational Requirements (운영 요구사항)

### 배포 및 환경

| 환경 | 용도 | URL 패턴 | 상태 |
|------|------|----------|------|
| **Local** | 개발 환경 | localhost:8080 | **현재 사용** |
| **Production** | 실 서비스 (개인 서버) | xxx.iptime.org | 개발 완료 후 구성 |

### 배포 전략

**Phase 1: 개발 단계 (현재)**
- 로컬 환경(localhost:8080)에서만 개발 및 테스트
- PostgreSQL 로컬 인스턴스 사용
- dev 프로파일로 실행

**Phase 2: 배포 단계 (개발 완료 후)**
- GitHub Actions CI/CD 파이프라인 구성
- 개인 Linux 서버에 Nginx + systemd 기반 배포
- main 브랜치 푸시 시 자동 배포
- 배포 관련 스크립트 사전 준비 완료 (`scripts/` 디렉토리)

### 모니터링 및 알림

| 항목 | 도구 | 임계값 |
|------|------|--------|
| **프론트엔드 성능** | Vercel Analytics | LCP 2.5s 초과 시 알림 |
| **에러 추적** | Sentry (무료 티어) | 에러율 5% 초과 시 Slack 알림 |
| **DB 모니터링** | Supabase Dashboard | 연결 수 80% 도달 시 알림 |
| **사용자 분석** | Mixpanel/PostHog | 일간 리텐션 리포트 |

### 지원 및 유지보수

- **버그 대응:** Critical 24시간 내, Major 48시간 내, Minor 1주 내
- **문의 채널:** 앱 내 이메일 링크 (support@drinky.app)
- **유지보수 창:** 매주 화요일 새벽 2-4시 (필요 시)

---
