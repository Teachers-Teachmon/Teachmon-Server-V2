# Teachmon Server V2 백엔드 인수인계 정리

## 1. 서비스 한 줄 요약
- 이 서비스는 **교내 학생 상태(자습/방과후/이석/이탈/조퇴)**를 시간표 단위로 관리하는 백엔드입니다.
- 핵심은 `student_schedule` 도메인이고, 다른 도메인(`self_study`, `after_school`, `leave_seat`)은 이 값을 생성/갱신하는 입력원입니다.

---

## 2. 프로젝트 구조(핵심만)
```text
src/main/java/solvit/teachmon
├── global
│   ├── configuration    # Security, Redis, JPA, Scheduler 등 공통 설정
│   ├── security         # JWT 필터, UserDetails
│   ├── exception        # 전역 예외 처리
│   └── aspect           # @Trace 로깅
└── domain
    ├── auth             # OAuth2 로그인, JWT 재발급/로그아웃
    ├── user             # 교사 정보 조회
    ├── management       # 학생/교사 관리
    ├── branch           # 분기(기간) 관리
    ├── place            # 장소 조회
    ├── self_study       # 정규/추가 자습
    ├── leave_seat       # 고정/일반 이석
    ├── after_school     # 방과후, 출장, 보강, 스프레드시트
    ├── supervision      # 감독 일정/교환/자동배정
    ├── team             # 팀 및 팀원 관리
    └── student_schedule # 학생 상태 시간표(핵심)
```

---

## 3. 요청 처리 큰 흐름
1. `SecurityFilterChain`에서 URL 권한 검사
2. `JwtAuthenticationFilter`가 `Authorization: Bearer ...` 파싱
3. JWT subject(mail)로 교사 로드 후 `SecurityContext` 설정
4. Controller -> Service/Facade -> Repository
5. 예외는 `GlobalExceptionHandler`에서 공통 응답

핵심 파일:
- `global/configuration/SecurityConfiguration.java`
- `global/security/filter/JwtAuthenticationFilter.java`
- `global/security/filter/JwtAuthenticationExceptionFilter.java`
- `global/exception/GlobalExceptionHandler.java`

---

## 4. 인증/인가(Auth) 정리

## 4.1 로그인 방식
- Google OAuth2 로그인 성공 시:
  - AccessToken 생성
  - RefreshToken 쿠키 발급(`HttpOnly`, `Secure`, `SameSite=None`)
  - AccessToken은 Redis의 `AuthCode`와 매핑 후 프론트에 `#code=...`로 전달

주요 클래스:
- `domain/auth/infrastructure/security/handler/TeachmonOAuth2SuccessHandler`
- `domain/auth/application/service/AuthService`
- `domain/auth/infrastructure/jwt/JwtManager`

## 4.2 재발급/로그아웃
- `POST /auth/reissue`: refresh cookie 검증 -> access/refresh 재발급
- `POST /auth/logout`: refresh token 삭제 + 만료 쿠키 반환

## 4.3 계정 허용 규칙
- Google 메일 화이트리스트/패턴이 코드에 하드코딩돼 있음
- 파일: `domain/auth/infrastructure/security/strategy/impl/GoogleOAuth2Strategy.java`

---

## 5. 데이터 모델 핵심(꼭 이해해야 함)

## 5.1 가장 중요한 모델
- `StudentScheduleEntity`: 학생-날짜-교시 슬롯(예: 2학년 3반 12번, 2026-03-10, 8~9교시)
- `ScheduleEntity`: 해당 슬롯의 상태 이력 스택
  - `stack_order`로 우선순위(최근 상태) 관리
  - 타입: `SELF_STUDY`, `AFTER_SCHOOL`, `LEAVE_SEAT`, `EXIT`, `AWAY`, ...

즉, **학생의 실제 현재 상태는 Schedule 스택 최상단 타입**으로 결정됩니다.

핵심 파일:
- `domain/student_schedule/domain/entity/StudentScheduleEntity.java`
- `domain/student_schedule/domain/entity/ScheduleEntity.java`

## 5.2 상태 상세 테이블
- `self_study_schedule`, `after_school_schedule`, `leave_seat_schedule`, `exit_schedule`, `away_schedule` 등
- `ScheduleEntity`와 1:1로 연결되어, 타입별 상세 정보를 담습니다.

---

## 6. 도메인별 책임 요약

## 6.1 `student_schedule` (중심축)
- 학생 시간표 조회, 장소별 상태 조회, 수동 상태 변경(이탈/조퇴), 주간 스케줄 생성
- 전략 패턴으로 타입별 세팅/변경 로직 분리

핵심 클래스:
- `application/service/StudentScheduleSettingService`
- `application/service/StudentScheduleGenerator`
- `application/strategy/setting/*`
- `application/strategy/change/*`
- `application/facade/PlaceStudentScheduleService`

## 6.2 `self_study`
- 정규 자습(요일/교시/학년 단위) 설정
- 일별 추가자습 설정
- 현재 주차 데이터면 즉시 student_schedule에 반영

## 6.3 `leave_seat`
- 일반 이석: 특정 날짜/교시에 학생들을 특정 장소로 이동
- 고정 이석: 요일 기준 반복 이석, 주간 스케줄 생성 시 일반 이석으로 확장

## 6.4 `after_school`
- 방과후 생성/수정/삭제/중단, 출장/보강 처리
- 학생 배정 변경 시 added/removed 계산 후 스케줄 반영
- 스프레드시트 업로드/동기화 지원(조건부 Bean)
- 자정 스케줄러로 종료 자동 처리

## 6.5 `supervision`
- 감독 일정 CRUD
- 감독 교환 요청/수락/거절
- 자동 배정(월~목): 금지요일, 최근 감독일, 누적 감독 횟수 기반 우선순위 계산

## 6.6 `management`, `user`, `team`, `branch`, `place`
- `management`: 학생/교사 관리(Admin 중심)
- `user`: 교사 프로필/검색
- `team`: 팀/팀원 관리
- `branch`: 분기 기간 관리(자습/방과후 계산 기준 기간)
- `place`: 장소 검색/배치 기준

---

## 7. 주간 스케줄 생성 로직(실무 중요)

## 7.1 자동 실행
- 매주 일요일 00:00(Asia/Seoul)
- 다음 주 월요일 기준으로 student_schedule 재생성 후 타입별 전략 적용
- 파일: `domain/student_schedule/application/scheduler/StudentScheduleSettingScheduler.java`

## 7.2 적용 순서
`StudentScheduleSettingStrategyComposite#getAllStrategies()` 기준:
1. SELF_STUDY
2. AFTER_SCHOOL
3. FIXED_LEAVE_SEAT
4. LEAVE_SEAT
5. ADDITIONAL_SELF_STUDY
6. AFTER_SCHOOL_REINFORCEMENT
7. EXIT
8. AWAY

이 순서가 스택 결과(최종 상태)에 직접 영향이 있습니다.

---

## 8. 운영 중 자주 보는 API
- 인증
  - `POST /auth/code`
  - `POST /auth/reissue`
  - `POST /auth/logout`
- 학생 상태
  - `GET /student-schedule`
  - `PATCH /student-schedule/{scheduleId}` (EXIT/AWAY 변경)
  - `DELETE /student-schedule/{scheduleId}` (변경 취소)
  - `GET /student-schedule/place/state`
- 자습/이석/방과후
  - `POST /self-study`
  - `POST /self-study/additional`
  - `POST /leaveseat`
  - `POST /leaveseat/static`
  - `POST /afterschool`
  - `POST /afterschool/business-trip`
  - `POST /afterschool/reinforcement`
- 감독
  - `POST /supervision/schedule`
  - `POST /supervision/schedule/auto`
  - `POST /supervision/exchange`

---

## 9. 장애/문의 대응 시 체크 순서
1. 요청한 날짜/교시의 `student_schedule` 존재 여부
2. 해당 슬롯의 `schedule` 스택 최상단 타입 확인
3. 타입별 상세 테이블(`*_schedule`) 레코드 확인
4. 원본 도메인 데이터 확인
   - 자습: `self_study`, `additional_self_study`
   - 이석: `leave_seat`, `fixed_leave_seat`
   - 방과후: `after_school`, `after_school_business_trip`, `after_school_reinforcement`
5. 권한/인증 확인
   - Security 매핑
   - JWT subject(mail) 존재 여부

---

## 10. 환경 설정 포인트
- 기본 프로필: `application.yml` -> `${ENVIRONMENT:local}`
- 로컬: `application-local.yml` + `env/local.env`
- 주요 외부 의존:
  - MySQL
  - Redis(토큰/코드 저장)
  - Google OAuth2
  - (옵션) Google Sheets
  - Discord Webhook(에러 알림)

---

## 11. 빠른 온보딩 권장 순서
1. `SecurityConfiguration` + `JwtAuthenticationFilter` 이해
2. `student_schedule`의 `StudentScheduleEntity/ScheduleEntity` 관계 이해
3. `StudentScheduleSettingService` + 전략 8개 읽기
4. `after_school`와 `leave_seat`가 schedule에 어떻게 쌓는지 확인
5. 마지막으로 `supervision` 자동배정 로직 확인

---

## 12. 참고: 핵심 파일 목록
- `src/main/java/solvit/teachmon/global/configuration/SecurityConfiguration.java`
- `src/main/java/solvit/teachmon/domain/auth/application/service/AuthService.java`
- `src/main/java/solvit/teachmon/domain/student_schedule/application/service/StudentScheduleSettingService.java`
- `src/main/java/solvit/teachmon/domain/student_schedule/domain/entity/ScheduleEntity.java`
- `src/main/java/solvit/teachmon/domain/after_school/application/service/AfterSchoolService.java`
- `src/main/java/solvit/teachmon/domain/leave_seat/application/facade/LeaveSeatFacadeService.java`
- `src/main/java/solvit/teachmon/domain/supervision/application/service/SupervisionAutoAssignService.java`

