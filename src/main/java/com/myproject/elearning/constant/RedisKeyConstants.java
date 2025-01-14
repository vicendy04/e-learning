package com.myproject.elearning.constant;

public class RedisKeyConstants {
    public static final String USER_AUTH_PREFIX = "auth:user:";
    public static final String BLACKLIST_PREFIX = "token:blacklist:";
    public static final String COURSE_PREFIX = "course:";
    public static final String POST_LIST_PREFIX = "posts:%d:likes";
    public static final String POST_LIKES_COUNT_PREFIX = "posts:%d:likes:count";
    public static final String OWNERSHIP_PREFIX = "ownership:%s:%d:%d";
    public static final String LIKE_STATUS = "1";
    public static final String UNLIKE_STATUS = "0";

    public static String getUserAuthKey(String username) {
        return USER_AUTH_PREFIX + username;
    }

    public static String getBlacklistKey(String jti) {
        return BLACKLIST_PREFIX + jti;
    }

    public static String getCourseKey(Long courseId) {
        return COURSE_PREFIX + courseId;
    }

    public static String getPostLikesKey(Long postId) {
        return String.format(POST_LIST_PREFIX, postId);
    }

    public static String getPostLikesCountKey(Long postId) {
        return String.format(POST_LIKES_COUNT_PREFIX, postId);
    }

    public static String getOwnershipKey(String type, Long resourceId, Long userId) {
        return String.format(OWNERSHIP_PREFIX, type, resourceId, userId);
    }
}
