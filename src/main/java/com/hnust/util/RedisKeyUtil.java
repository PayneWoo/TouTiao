package com.hnust.util;

/**
 * 根据规范生成一些key
 * key的前一个字符串表示业务，后边表示参数
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";        //点赞业务
    private static String BIZ_DISLIKE = "DISLIKE";  //点踩业务
    private static String BIZ_EVENT = "EVENT";

    public static String getEventQueueKey() {
        return BIZ_EVENT;
    }

    public static String getLikeKey(int entityId, int entityType) {
        return BIZ_LIKE + SPLIT +String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityId, int entityType) {
        return BIZ_DISLIKE + SPLIT +String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}
