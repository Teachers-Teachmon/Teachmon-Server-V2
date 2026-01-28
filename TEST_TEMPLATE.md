# 테스트 코드 템플릿

이 문서는 프로젝트의 일관된 테스트 코드 작성을 위한 템플릿입니다.

## 🏗️ 기본 테스트 구조

### 1. Repository 테스트 템플릿

```java
package solvit.teachmon.domain.{domain}.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("{Entity명} 저장소 테스트")
class {Entity}RepositoryTest {

    @Autowired
    private {Entity}Repository {entity}Repository;

    @Test
    @DisplayName("{기능}으로 {Entity}를 찾을 수 있다")
    void should{Action}When{Condition}() {
        // Given: {준비 상황 설명}
        {Entity} {entity} = {Entity}.builder()
                .{field}({value})
                .build();
        {entity}Repository.save({entity});

        // When: {실행 동작 설명}
        Optional<{Entity}> result = {entity}Repository.{method}({parameter});

        // Then: {예상 결과 설명}
        assertThat(result).isPresent();
        assertThat(result.get().get{Field}()).isEqualTo({expectedValue});
    }

    @Test
    @DisplayName("존재하지 않는 {조건}으로 찾으면 빈 결과가 반환된다")
    void shouldReturnEmptyWhen{Entity}NotExists() {
        // Given: {준비 상황 설명}
        
        // When: {실행 동작 설명}
        Optional<{Entity}> result = {entity}Repository.{method}({invalidParameter});

        // Then: {예상 결과 설명}
        assertThat(result).isEmpty();
    }
}
```

### 2. Service 테스트 템플릿 (Mock 사용)

```java
package solvit.teachmon.domain.{domain}.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("{Entity} 서비스 테스트")
class {Entity}ServiceTest {

    @Mock
    private {Entity}Repository {entity}Repository;

    private {Entity}Service {entity}Service;
    private {Entity} {entity};

    @BeforeEach
    void setUp() {
        {entity}Service = new {Entity}Service({entity}Repository);
        
        {entity} = {Entity}.builder()
                .{field}({value})
                .build();
    }

    @Test
    @DisplayName("{기능} 시 {Entity}가 성공적으로 {동작}된다")
    void should{Action}{Entity}Successfully() {
        // Given: {준비 상황 설명}
        given({entity}Repository.{method}(any())).willReturn({returnValue});

        // When: {실행 동작 설명}
        {ReturnType} result = {entity}Service.{method}({parameter});

        // Then: {예상 결과 설명}
        assertThat(result).isNotNull();
        assertThat(result.{getter}()).isEqualTo({expectedValue});
        
        // 상호작용 검증
        verify({entity}Repository).{method}({parameter});
    }

    @Test
    @DisplayName("{실패 조건} 시 {예외}가 발생한다")
    void shouldThrow{Exception}When{Condition}() {
        // Given: {준비 상황 설명}
        given({entity}Repository.{method}(any())).willReturn(Optional.empty());

        // When & Then: {실행 및 예상 결과}
        assertThatThrownBy(() -> {entity}Service.{method}({parameter}))
                .isInstanceOf({Exception}.class)
                .hasMessage({expectedMessage});
    }
}
```

### 3. Controller 테스트 템플릿

```java
package solvit.teachmon.domain.{domain}.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({Entity}Controller.class)
@DisplayName("{Entity} 컨트롤러 테스트")
class {Entity}ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private {Entity}Service {entity}Service;

    @Test
    @WithMockUser
    @DisplayName("{HTTP_METHOD} {endpoint} - {성공 케이스 설명}")
    void should{Action}{Entity}Successfully() throws Exception {
        // Given: {준비 상황 설명}
        {RequestDto} request = new {RequestDto}({parameters});
        given({entity}Service.{method}(any())).willReturn({responseData});

        // When & Then: {실행 및 예상 결과}
        mockMvc.perform({httpMethod}("/{endpoint}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().{expectedStatus}())
                .andExpect(jsonPath("$.{field}").value({expectedValue}));

        // 서비스 호출 검증
        verify({entity}Service).{method}({parameter});
    }

    @Test
    @WithMockUser
    @DisplayName("{HTTP_METHOD} {endpoint} - {실패 케이스 설명}")
    void should{Action}When{Condition}() throws Exception {
        // Given: {준비 상황 설명}
        {RequestDto} request = new {RequestDto}({invalidParameters});
        given({entity}Service.{method}(any())).willThrow(new {Exception}({message}));

        // When & Then: {실행 및 예상 결과}
        mockMvc.perform({httpMethod}("/{endpoint}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().{expectedErrorStatus}())
                .andExpect(jsonPath("$.message").value({expectedErrorMessage}));
    }
}
```

### 4. Entity/Domain 테스트 템플릿

```java
package solvit.teachmon.domain.{domain}.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("{Entity} 엔티티 테스트")
class {Entity}Test {

    private {Entity} {entity};

    @BeforeEach
    void setUp() {
        {entity} = {Entity}.builder()
                .{field}({value})
                .build();
    }

    @Test
    @DisplayName("{기능}이 정상적으로 동작한다")
    void should{Action}Successfully() {
        // Given: {준비 상황 설명}
        {Type} {parameter} = {value};

        // When: {실행 동작 설명}
        {entity}.{method}({parameter});

        // Then: {예상 결과 설명}
        assertThat({entity}.get{Field}()).isEqualTo({expectedValue});
    }

    @Test
    @DisplayName("{유효성 검증} 시 예외가 발생한다")
    void shouldThrowExceptionWhen{Condition}() {
        // Given: {준비 상황 설명}
        {Type} {invalidParameter} = {invalidValue};

        // When & Then: {실행 및 예상 결과}
        assertThatThrownBy(() -> {Entity}.builder()
                        .{field}({invalidParameter})
                        .build())
                .isInstanceOf({Exception}.class)
                .hasMessage({expectedMessage});
    }
}
```

## 📋 테스트 작성 규칙

### BDD 패턴 준수
- **Given**: 테스트를 위한 준비 상황
- **When**: 테스트할 동작/행위
- **Then**: 예상되는 결과

### @DisplayName 작성 가이드
```java
// ✅ 좋은 예시
@DisplayName("유효한 이메일로 사용자를 찾을 수 있다")
@DisplayName("존재하지 않는 ID로 조회 시 UserNotFoundException이 발생한다")
@DisplayName("POST /users - 사용자 생성 시 201 상태코드를 반환한다")

// ❌ 나쁜 예시  
@DisplayName("testFindUser")
@DisplayName("사용자 테스트")
```

### 테스트 메서드명 규칙
- **should{Action}When{Condition}()** 패턴 사용
- 예: `shouldReturnUserWhenValidEmailProvided()`
- 예: `shouldThrowExceptionWhenInvalidIdProvided()`

### 어노테이션 사용법
```java
// Repository 테스트
@SpringBootTest
@ActiveProfiles("test") 
@Transactional

// Service 테스트 (Mock)
@ExtendWith(MockitoExtension.class)

// Controller 테스트
@WebMvcTest({Controller}.class)

// Entity 테스트 (어노테이션 없음)
```

### Import 구문
```java
// AssertJ 사용
import static org.assertj.core.api.Assertions.*;

// Mockito BDD 스타일
import static org.mockito.BDDMockito.*;

// MockMvc
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
```

### 공통 설정
```java
@BeforeEach
void setUp() {
    // 테스트 전 초기화 로직
}

@AfterEach  
void tearDown() {
    // 테스트 후 정리 로직 (필요시)
}
```

## 🎯 테스트 케이스 작성 가이드

### 성공 케이스
- 정상적인 입력값으로 기대하는 결과가 나오는지 확인
- 경계값 테스트 (최소/최대값)

### 실패 케이스
- null 값, 빈 문자열 등 유효하지 않은 입력값
- 존재하지 않는 데이터 조회
- 비즈니스 규칙 위반

### Edge Case
- 빈 리스트/컬렉션
- 중복 데이터
- 동시성 문제

## 🔍 테스트 실행 및 디버깅 가이드

### 테스트 실행 프로세스
1. **테스트 코드 작성** - 위 템플릿 사용
2. **테스트 실행** - `./gradlew test` 또는 IDE에서 실행
3. **결과 분석** - 성공/실패 확인
4. **실패 시 원인 분석 및 수정**
5. **모든 테스트 성공까지 반복**

### 테스트 실패 시 분석 절차

#### 1단계: 오류 유형 분석
```bash
# 전체 테스트 실행
./gradlew test

# 특정 클래스 테스트 실행
./gradlew test --tests "solvit.teachmon.domain.team.*"

# 특정 메서드 테스트 실행
./gradlew test --tests "solvit.teachmon.domain.team.TeamServiceTest.shouldCreateTeamSuccessfully"
```

#### 2단계: 오류 분류 및 대응

##### A. 테스트 코드 문제인 경우 → **즉시 수정**
- **컴파일 에러**: Import 누락, 타입 불일치
- **Mock 설정 오류**: given/when 설정 누락
- **Assertion 오류**: 잘못된 예상값
- **어노테이션 오류**: @Test, @MockBean 누락

```java
// ❌ 잘못된 예시
given(repository.findById(1L)).willReturn(user); // Optional 누락

// ✅ 올바른 예시  
given(repository.findById(1L)).willReturn(Optional.of(user));
```

##### B. 애플리케이션 코드 문제인 경우 → **분석결과.md 생성**

**분석결과.md 템플릿:**
```markdown
# 테스트 실패 분석 결과

## 📋 기본 정보
- **테스트 클래스**: {TestClass}
- **실패한 메서드**: {testMethodName}
- **실행 날짜**: {YYYY-MM-DD HH:mm:ss}
- **분석자**: Claude Code

## 🚨 오류 내용
```
{실제 오류 메시지 복사}
```

## 🔍 원인 분석
### 1. 오류 발생 위치
- **파일**: `src/main/java/solvit/teachmon/domain/{domain}/{category}/{ClassName}.java`
- **메서드**: `{methodName}()` 
- **라인**: {lineNumber}

### 2. 문제점
{구체적인 문제 설명}

### 3. 예상 원인
- [ ] 비즈니스 로직 오류
- [ ] 데이터 타입 불일치  
- [ ] Null 처리 누락
- [ ] 예외 처리 부족
- [ ] DB 연동 문제
- [ ] 의존성 주입 문제
- [ ] 기타: {설명}

## 💡 수정 방안
### 현재 코드
```java
{문제가 있는 현재 코드}
```

### 수정 제안
```java  
{수정된 코드 제안}
```

## 📝 추가 확인사항
- [ ] 관련된 다른 테스트들도 영향받는지 확인
- [ ] DB 스키마 변경이 필요한지 확인
- [ ] API 스펙 변경이 필요한지 확인

## 🎯 후속 작업
1. {할 일 1}
2. {할 일 2}
3. {할 일 3}
```

#### 3단계: 자동 재실행 스크립트

**테스트 자동화 명령어:**
```bash
# 실패한 테스트만 재실행하며 성공까지 반복
while ! ./gradlew test --rerun-tasks; do
    echo "테스트 실패 - 코드 수정 후 재시도..."
    sleep 5
done
echo "모든 테스트 성공!"
```

### 🛠️ 일반적인 테스트 문제 해결법

#### Mock 관련 문제
```java
// 문제: Mock 객체 미설정
@MockBean
private UserRepository userRepository; // 설정 없이 사용

// 해결: given/when 설정
given(userRepository.findById(1L)).willReturn(Optional.of(user));
```

#### SpringBootTest 관련 문제
```java
// 문제: Profile 설정 누락
@SpringBootTest // test profile 없음

// 해결: Profile 명시
@SpringBootTest  
@ActiveProfiles("test")
```

#### 데이터 정합성 문제
```java
// 문제: 테스트간 데이터 오염
@Test
void test1() {
    userRepository.save(user); // 데이터 남아있음
}

// 해결: 트랜잭션 롤백
@Transactional
@Test
void test1() {
    userRepository.save(user); // 테스트 후 자동 롤백
}
```

### 📊 테스트 성공률 추적
```java
// 테스트 클래스 상단에 성공률 코멘트 추가
/**
 * 테스트 성공률: 95% (19/20)
 * 최종 수정일: 2025-01-21
 * 미해결 이슈: 동시성 테스트 1건 (TC#20)
 */
@DisplayName("팀 서비스 테스트")
class TeamServiceTest {
    // ...
}
```

### 🎯 테스트 품질 체크리스트
- [ ] 모든 테스트가 독립적으로 실행 가능
- [ ] Given-When-Then 구조 명확
- [ ] @DisplayName으로 테스트 의도 명확
- [ ] Edge case 포함 (null, empty, boundary)
- [ ] Mock 사용 시 verify() 검증 포함
- [ ] 예외 케이스 테스트 포함

이 가이드를 따라 체계적으로 테스트 코드를 작성하고 디버깅하세요.