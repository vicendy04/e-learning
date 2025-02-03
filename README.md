# E-Learning Platform

I’m building my first practical RESTful API project to explore JPA/Hibernate fundamentals, focusing on efficient data access, avoiding common persistence layer pitfalls, and experimenting with caching techniques.

The front-end is quite simple and incomplete, and to be honest, it won’t be finished. Let’s focus on the back-end instead!

Here are some features that go beyond basic CRUD operations.

## Homepage

The homepage is designed for dynamic content loading, adapting to user preferences. It's also built to be easily extendable and customizable.

Here's a simple demonstration:

```java
// Controller.java
@GetMapping("")
public ApiRes<PagedRes<TopicCoursesRes>> getCourses(@Valid @ModelAttribute CourseFilters filters) {
    CourseSearcher searcher = strategyFactory.getStrategy();
    var pageRequest = PageBuilder.of(filters, Sort.by("id"));
    var courses = courseService.getCourses(filters, pageRequest, searcher);
    var res = COURSE_MAPPER.toTopicCoursesRes(courses);
    return successRes("Retrieved successfully", PagedRes.of(res));
}
```

```java
// StrategyFactory.java
public CourseSearcher getStrategy() {
    var userType = userType();
    return switch (userType) {
        case PERSONALIZED -> personalizedSearcher;
        case DEFAULT -> defaultSearcher;
    };
}

// custom logic
// ex: check if the user is in a special list,
// and if so, provide some offers
private static UserType userType() {
    return SecurityUtils.getLoginId().isPresent() ? PERSONALIZED : DEFAULT;
}
```

```java
// PersonalizedSearcher.java
public Page<CourseData> search(CourseFilters filters, PageRequest request) {
    // apply special offers
    // log ...

    // custom logic
    var userId = SecurityUtils.getLoginId().orElseThrow(AnonymousUserEx::new);
    Set<Long> ids = userRepository.getMyPreferencesIds(userId);
    // find course ids based on user interests
    Page<Long> courseIds = courseRepository.findIdsByTopicIds(ids, request);

    // read cache
    // fetch from database and be aware of the 1 + N problem
}
```

```java
// Service.java
@Transactional(readOnly = true)
public Page<CourseData> getCourses(
        CourseFilters filters,
        PageRequest pageRequest,
        CourseSearcher searcher) {
    // common logic
    return searcher.search(filters, pageRequest);
}
```

This allows us to insert our custom logic into the common logic.

## Cyclomatic Complexity & Testing

To be honest, this project is not complex enough to require an analysis of Cyclomatic Complexity. However, I’ll briefly mention it below for completeness. It might not be entirely accurate.

Here's a simple demonstration:

```java
// Service.java
@Transactional
public void setInitialPreferences(Long userId, Set<Long> topicIds) {
    long userPrefCount = userRepository.countUserPreferences(userId);
    if (userPrefCount > 0) {
        throw new EmailUsedEx("Preferences already set for this user");
    }
    long topicCount = topicRepository.countByIdIn(topicIds);
    if (topicCount != topicIds.size()) {
        throw new InvalidIdEx("Some topics not found");
    }
    userRepository.bulkAddPref(new UserPreferencesData(userId, topicIds));
}
```

```java
// Test.java
@Test
void setInitialPreferences_ShouldThrowIfTopicsNotFound() {
    // setup
    when(userRepository.countUserPreferences(any())).thenReturn(0L);
    when(topicRepository.countByIdIn(Set.of(1L, 99L))).thenReturn(1L); // only 1 found out of 2 

    // act & assert
    assertThrows(InvalidIdEx.class, () -> service.setInitialPreferences(1L, Set.of(1L, 99L)));
}
```

- Test case depends on both repos, even though only one condition is tested.

- Test case could fail due to incorrect mocking of unrelated dependencies.

- When the validation logic changes, test cases related to this method are affected.

### Refactored version

```java
@Transactional
public void setInitialPreferences(Long userId, Set<Long> topicIds) {
    validateUserPreferencesNotSet(userId);
    validateAllTopicsExist(topicIds);
    userRepository.bulkAddPref(new UserPreferencesData(userId, topicIds));
}
```

```java
@Test
void validateUserPreferencesNotSet_ShouldThrowWhenPreferencesExist() {
    when(userRepository.countUserPreferences(1L)).thenReturn(1L);
    assertThrows(EmailUsedEx.class, () -> service.validateUserPreferencesNotSet(1L));
}

@Test
void validateUserPreferencesNotSet_ShouldPassWhenNoPreferences() {
    when(userRepository.countUserPreferences(1L)).thenReturn(0L);
    assertDoesNotThrow(() -> service.validateUserPreferencesNotSet(1L));
}

@Test
void validateAllTopicsExist_ShouldThrowWhenTopicsMissing() {
    when(topicRepository.countByIdIn(Set.of(1L, 99L))).thenReturn(1L);
    assertThrows(InvalidIdEx.class, () -> service.validateAllTopicsExist(Set.of(1L, 99L)));
}

@Test
void validateAllTopicsExist_ShouldPassWhenAllTopicsExist() {
    ...
}
```

- Each validation is tested separately, without being affected by other logic.

todo: edit

## Group Chat Using Pub/Sub

Redis is used as the channel layer to broadcast messages to all users in a chat room, enabling real-time group chat functionality.

![pub_sub](docs/pub_sub.png)

## Using Redis

- Cache data that is frequently reused and rarely changes, such as course details (e.g., information, images). For paginated queries (e.g., LIMIT 10 OFFSET 10), only the static content of the courses should be cached. The pagination order or structure itself should not be cached to ensure that any updates (e.g., new courses being added, changes in sorting based on trends) are reflected correctly.

- Temporary Data: Redis is also used for temporary data like OTPs.

- Redis enables quick cache writes and periodically persists data to the database.

![heavy_write](docs/heavy_write.png)

- The cache can group multiple writes and commit them as single operations to the database. The trade-off is the potential for data loss or inconsistency.

## Security Related

![auth_flow](docs/auth_flow.png)

Revoked tokens (from logout) are stored in the cache for a short time, enabling us to perform additional monitoring actions to detect any unusual activities that could potentially be from a hacker.

To enhance security, hash-based double-submit cookies are used to protect against CSRF attacks (not yet implemented).

![double_submit_cookies](docs/double_submit_cookies.png)

## Database Transaction & Lost Updates

### Version 1

```java
@Transactional
public EnrollmentRes enrollCourse(Long courseId, Long userId) {
    // check then enrollment save

    // atomic update (update on set count = count + 1)
    // acquire exclusive lock on the object
    // no pending transactions can modify the row while the operation is in progress
    courseRepository.incrementEnrollmentCount(courseId);

    // response
}
```

Pros:

- Directly update in the database without requiring entity state.

- Prevent lost updates

Cons:

- Compare to Optimistic Locking, there is no retry mechanism available in this approach.

- Complex business logic require other techniques (logic depends on multiple fields, ex: max_capacity, course_status, etc).

### Version 2: Optimistic Lock

todo: continue writing

### Version 3: Pessimistic Lock

```java
// Service.java
@Transactional
public boolean consumeDiscount(String discountCode, Long courseId) {
    // select for update
    var discount = discountRepository.findByDiscountCodeWithLock(discountCode)
            .orElseThrow(() -> new InvalidDiscountEx());
    var course = courseRepository.findBy(courseId)
            .orElseThrow(() -> new InvalidIdEx(courseId));
    // complex validation
    if (isApplicableToCourse(discount, course)) {
        discount.setUsesCount(discount.getUsesCount() + 1);
        discountRepository.save(discount);
        return true;
    }
    return false;
}
```

```java
// Repository.java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@EntityGraph(attributePaths = {"specificCourseIds"})
@Query("SELECT d FROM Discount d WHERE d.discountCode = :discountCode")
Optional<Discount> findByDiscountCodeWithLock(@Param("discountCode") String discountCode);
```

todo: continue writing
