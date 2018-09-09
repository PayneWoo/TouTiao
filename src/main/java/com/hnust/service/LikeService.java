package com.hnust.service;

import com.hnust.util.JedisAdapter;
import com.hnust.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 判断某个用户对于某个实体的喜欢状态
     * 喜欢返回1，不喜欢返回-1，否则返回0
     */
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType); //构造出一个对所有元素喜欢的一个key
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey =RedisKeyUtil.getDisLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(disLikeKey, String.valueOf(userId))) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 点赞
     */
    public long like(int userId, int entityType, int entityId) {
        //将构造出来的喜欢key 添加到喜欢者集合中
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        //将构造出来的不喜欢key从集合中移除
        String disLikeKey =RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);     //返回喜欢数
    }

    /**
     *点踩
     */
    public long disLike(int userId, int entityType, int entityId) {
        //将构造出来的不喜欢key从集合中移除
        String disLikeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));

        //将构造出来的喜欢key 添加到喜欢者集合中
        String likeKey =RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);     //返回喜欢数
    }
}
