package com.myproject.elearning.constant;

public class RedisKeyConstants {
    public static final String USER_AUTH_CACHE_KEY = "auth:user:";
    public static final String BLACKLIST_PREFIX = "token:blacklist:";
    public static final String COURSE_CACHE_KEY = "course:";
    public static final String ENROLLMENT_COUNT_CACHE_KEY = "course:%d:enrollment_count";
    public static final String POST_LIKES_KEY_FORMAT = "posts:%d:likes";
    public static final String POST_LIKES_COUNT_KEY_FORMAT = "posts:%d:likes:count";

    public static String getUserAuthKey(String username) {
        return USER_AUTH_CACHE_KEY + username;
    }

    public static String getBlacklistKey(String jti) {
        return BLACKLIST_PREFIX + jti;
    }

    public static String getCourseKey(Long courseId) {
        return COURSE_CACHE_KEY + courseId;
    }

    public static String getEnrollmentCountKey(Long courseId) {
        return String.format(ENROLLMENT_COUNT_CACHE_KEY, courseId);
    }

    public static String getPostLikesKey(Long postId) {
        return String.format(POST_LIKES_KEY_FORMAT, postId);
    }

    public static String getPostLikesCountKey(Long postId) {
        return String.format(POST_LIKES_COUNT_KEY_FORMAT, postId);
    }
}
