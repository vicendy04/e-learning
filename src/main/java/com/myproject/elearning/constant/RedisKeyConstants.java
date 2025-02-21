package com.myproject.elearning.constant;

public class RedisKeyConstants {
    public static final String USER_AUTH_PREFIX = "auth:user:%s";
    public static final String BLACKLIST_PREFIX = "token:blacklist:%s";
    public static final String COURSE_PREFIX = "course:%d";
    public static final String POST_LIST_PREFIX = "posts:%d:likes";
    public static final String POST_PREFIX = "posts:%d";
    public static final String TOP_LIKE_PREFIX = "top_like";
    public static final String POST_LIKES_COUNT_PREFIX = "posts:%d:likes:count";
    public static final String OWNERSHIP_PREFIX = "resource_owners:%d:%s";
    public static final String LIKE_STATUS = "1";
    public static final String UNLIKE_STATUS = "0";

    private RedisKeyConstants() {}

    public static String getUserAuthKey(String username) {
        return String.format(USER_AUTH_PREFIX, username);
    }

    public static String getBlacklistKey(String jti) {
        return String.format(BLACKLIST_PREFIX, jti);
    }

    public static String getCourseKey(Long courseId) {
        return String.format(COURSE_PREFIX, courseId);
    }

    public static String getPostLikesKey(Long postId) {
        return String.format(POST_LIST_PREFIX, postId);
    }

    public static String getPostKey(Long postId) {
        return String.format(POST_PREFIX, postId);
    }

    public static String getPostLikesCountKey(Long postId) {
        return String.format(POST_LIKES_COUNT_PREFIX, postId);
    }

    public static String getOwnershipKey(Long userId, String type) {
        return String.format(OWNERSHIP_PREFIX, userId, type);
    }
}
