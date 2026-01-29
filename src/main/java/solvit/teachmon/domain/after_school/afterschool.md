# After School API Documentation

## Overview
API for updating existing after-school programs/classes.

## Endpoint  
**PUT** `/afterschool`

## Request Body
```json
{
    "grade": 2,
    "week_day": "MON", 
    "period": "SEVEN_AND_EIGHT_PERIOD",
    "after_school_id": 32523532,
    "teacher_id": 4334235534,
    "place_id": 35346235234,
    "name": "정보처리 산업기사 Java",
    "students_id": [
        2423523523
    ]
}
```

## Request Parameters
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `grade` | Integer | Yes | Grade level (1-3) |
| `week_day` | String | Yes | Day of the week (MON, TUE, WED, THU) |
| `period` | String | Yes | School period (SEVEN_PERIOD, EIGHT_AND_NINE_PERIOD, TEN_AND_ELEVEN_PERIOD) |
| `after_school_id` | Long | Yes | ID of the after-school program to update |
| `teacher_id` | Long | Yes | ID of the teacher assigned to this after-school program |
| `place_id` | Long | Yes | ID of the location/place where the program will be held |
| `name` | String | Yes | Name of the after-school program |
| `students_id` | Array[Long] | Yes | List of student IDs enrolled in this program |

## Response
**Status Code:** `200 OK`

### Success Response
```json

```

## Error Responses
| Status Code | Description |
|-------------|-------------|
| `400 Bad Request` | Invalid request parameters |
| `404 Not Found` | After-school program, teacher, or place not found |

## Business Rules
1. Teacher must exist in the system
2. Place must exist and be available
3. Students must exist in the system
4. **Available WeekDays**: Only MON, TUE, WED, THU are supported
   - `WeekDay.MON` - Monday
   - `WeekDay.TUE` - Tuesday  
   - `WeekDay.WED` - Wednesday
   - `WeekDay.THU` - Thursday
5. **Available SchoolPeriods**: Only three periods are supported
   - `SchoolPeriod.SEVEN_PERIOD` - 7교시
   - `SchoolPeriod.EIGHT_AND_NINE_PERIOD` - 8~9교시
   - `SchoolPeriod.TEN_AND_ELEVEN_PERIOD` - 10~11교시

## Implementation Requirements
- Use Spring Validation for request validation
- Implement proper error handling with custom exceptions
- Follow existing project patterns for DTOs and service layer
- Add proper transaction management
- Include Korean validation messages matching project style
- **Prevent JPA N+1 problems** - Use batch queries and proper fetch strategies
- **Use QueryDSL for all queries** - Implement optimized queries with QueryDSL
- **Query optimization** - Apply famous reference patterns for performance
- Return `204 No Content` status code (no response message needed)

## Code Review Requirements
- **Final code review with famous references** - Apply Effective Java, Clean Code principles
- **Code readability enhancement** - Implement well-known design patterns and best practices
- **Performance optimization** - Follow industry standards for query and code optimization
- **No comments in code** - Code should be self-documenting through clear naming and structure

## Required Files to Create/Update

### Domain Layer
- `AfterSchoolEntity.java` ✅ (exists - may need updates)
  - **MUST implement entity validation** - Reference existing entities for validation patterns
  - Follow validation patterns from `TeamEntity`, `TeacherEntity`, `StudentEntity`
- `AfterSchoolRepository.java` ✅ (exists - may need updates)
- `AfterSchoolQueryDslRepository.java` ❌ (create new)
- `AfterSchoolRepositoryImpl.java` ❌ (create new - QueryDSL implementation)

### Application Layer
- `AfterSchoolService.java` ❌ (create new)

### Presentation Layer
- `AfterSchoolController.java` ❌ (create new)
- `AfterSchoolRequestDto.java` ❌ (create new)

### Exception Layer
- `AfterSchoolNotFoundException.java` ❌ (create new) - extends TeachmonBusinessException (404)
- `InvalidAfterSchoolInfoException.java` ❌ (create new) - extends TeachmonBusinessException (400)

### Exception Requirements
- **PlaceNotFoundException**: Create in `place` domain, NOT in `after_school` domain
- **Do NOT create AfterSchoolConflictException**: Remove if exists

### Existing Dependencies (Referenced entities)
- `TeacherEntity.java` ✅ (exists)
- `PlaceEntity.java` ✅ (exists)  
- `StudentEntity.java` ✅ (exists)
- `TeacherRepository.java` ✅ (exists)
- `StudentRepository.java` ✅ (exists)

### Total Files to Create: 8 new files
### Total Files to Update: 2 existing files

## Additional Implementation Requirements
- **Exception Inheritance Rules**:
  - Client errors (400-499): extends `TeachmonBusinessException`
  - Server errors (500+): extends `TeachmonSystemError`
  - **TeachmonSystemError MUST have HttpStatus 500 or higher ONLY**
  - **TeachmonBusinessException for HttpStatus 400-499 ONLY**
  - **MANDATORY**: ALL exceptions must extend ONLY `TeachmonBusinessException` or `TeachmonSystemError`
  - **NO OTHER exception types are allowed** (no RuntimeException, IllegalArgumentException, etc.)
- **WeekDay Constraints**: Only MON, TUE, WED, THU are available
- **SchoolPeriod Constraints**: Only SEVEN_PERIOD, EIGHT_AND_NINE_PERIOD, TEN_AND_ELEVEN_PERIOD are available
- **Code Style Requirements**:
  - Avoid `final` keyword in service methods
  - No inner classes or record patterns
  - Remove unused methods and empty method implementations
  - **CRITICAL**: Absolutely NO IDE errors/warnings allowed (except entity column mapping exceptions)
  - **WARNING-FREE CODE MANDATORY**: Fix ALL IDE warnings including unused imports, variables, etc.
  - **ZERO TOLERANCE for IDE warnings**: Code must compile and run without any IDE warnings
- **Clean Code Standards**:
  - Self-documenting code without comments
  - Follow existing project patterns
  - Maintain consistency with codebase style
