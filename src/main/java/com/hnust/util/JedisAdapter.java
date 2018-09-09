package com.hnust.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;

/**
 * 运行 jedis 程序之前要通过 cmd 开启 redis 服务，jedis 默认连接到本地 127.0.0.1 的 6379 端口
 */
@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);


    public static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }
    public static void InitJedis() {
        Jedis jedis = new Jedis();  // jedis 默认连接到本地 127.0.0.1 的 6379 端口
//        jedis.flushAll();   //将数据库都删掉

//        jedis.set("hello", "world");    //将键值存入数据库
//        print(1, jedis.get("hello"));      //打印键 hello 对应的 值 world
//        jedis.rename("hello", "newhello");
//        print(2, jedis.get("newhello"));
//
//        jedis.set("pv", "100");
//        jedis.incr("pv");   //加一
//        print(3, jedis.get("pv"));
//        jedis.incrBy("pv", 5);  //加 5
//        print(3, jedis.get("pv"));
//
//        //列表操作
//        String listName = "listA";
//        for (int i = 0; i < 10; i++) {
//            jedis.lpush(listName,"a" + String.valueOf(i));
//        }
//        print(4, jedis.lrange(listName, 0, 12) );
//        print(5, jedis.llen(listName));
//        print(6, jedis.lpop(listName));
//        print(7, jedis.llen(listName));
//        print(8, jedis.lindex(listName, 5));
//        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
//        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "yy"));
//        print(10, jedis.lrange(listName, 0, 12));
//
//        //哈希
//        String userKey = "user12";
//        jedis.hset(userKey, "name", "Currey");
//        jedis.hset(userKey, "age", "28");
//        jedis.hset(userKey, "phone", "110");
//        print(12, jedis.hget(userKey, "name"));
//        print(13, jedis.hgetAll(userKey));
//        jedis.hdel(userKey,"phone");
//        print(14, jedis.hgetAll(userKey));
//        print(15, jedis.hkeys(userKey));
//        print(16, jedis.hvals(userKey));
//        print(17, jedis.hexists(userKey, "email"));
//        print(18, jedis.hexists(userKey, "name"));
//        jedis.hsetnx(userKey, "school", "HNUST");   //不存在键school就set一个，存在就不set
//        jedis.hsetnx(userKey, "name", "WP");
//        print(19, jedis.hgetAll(userKey));
//
//
//        //集合
//        String likeKeys1 = "newsLike1";
//        String likeKeys2 = "newsLike2";
//        for (int i = 0; i < 10; i++) {
//            jedis.sadd(likeKeys1, String.valueOf(i));
//            jedis.sadd(likeKeys2, String.valueOf(i*2));
//        }
//        print(20, jedis.smembers(likeKeys1));
//        print(21, jedis.smembers(likeKeys2));
//        print(22, jedis.sinter(likeKeys1, likeKeys2));  //求两个集合的交集
//        print(23, jedis.sunion(likeKeys1, likeKeys2));    //求两个集合的并集
//        print(24, jedis.sdiff(likeKeys1, likeKeys2));    //求两个集合的补集
//        print(25, jedis.sismember(likeKeys1, "5")); //集合 likeKey1 中是否存在元素 5
//        jedis.srem(likeKeys1, "5");     //删除集合 likeKey1 中的元素 5
//        print(26, jedis.smembers(likeKeys1));
//        print(27, jedis.scard(likeKeys1));      //获得集合中元素的数量
//        jedis.smove(likeKeys2, likeKeys1, "14");   //将集合 likeKey2 中的元素 移动到 likeKey1
//        print(28, jedis.scard(likeKeys1));      //获得集合中元素的数量
//        print(29, jedis.smembers(likeKeys1));
//
//        //优先队列
//        String rankKey = "rankey";
//        jedis.zadd(rankKey, 80, "Wade"); //添加成员到排序集合
//        jedis.zadd(rankKey, 96, "James"); //添加成员到排序集合
//        jedis.zadd(rankKey, 95, "Kobe"); //添加成员到排序集合
//        jedis.zadd(rankKey, 90, "KD"); //添加成员到排序集合
//        jedis.zadd(rankKey, 88, "KG"); //添加成员到排序集合
//        print(30, jedis.zcard(rankKey));
//        print(31, jedis.zcount(rankKey, 60, 100));
//        print(32, jedis.zscore(rankKey, "James"));  //获取与排序集中给定成员关联的分数
//        print(33, jedis.zincrby(rankKey,3.3, "KG"));   //增加排序集中成员的分数
//        print(34, jedis.zrange(rankKey, 0, 3));         //分数从小到大排序的第0--3名成员
//        print(35, jedis.zrevrange(rankKey, 0, 3));   //分数从大到小排序的第0--3名成员
//
//        //按分数返回排序集中的成员数
//        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey,"0", "100")) {
//            print(36, tuple.getElement() + ":" + tuple.getScore());
//        }
//        print(37, jedis.zrank(rankKey, "Kobe"));    //指定成员的排名
//        print(38, jedis.zrevrank(rankKey, "Kobe"));    //指定成员的排名

//        JedisPool jedisPool = new JedisPool();      //线程池
//        for (int i = 0; i < 100; i++) {
//            Jedis jedis1 = jedisPool.getResource();
//            jedis1.get("a");
//            System.out.println("POOL" + i);
//            jedis1.close();
//        }
    }

    private Jedis jedis = null;
    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    private Jedis getJedis() {
        return pool.getResource();
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return getJedis().get(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 把点赞的用户存入set集合中去
     * @param key:根据具体的业务生成的key
     * @param value:用户id
     * @return
     */
    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("出现异常：" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 从集合中移除key和当前的用户（点踩操作）
     * @param key：根据具体的业务生成的key
     * @param value：用户id
     * @return
     */
    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("出现异常：" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 集合中是否已经有key和当前的用户（即判断是否已经点赞）
     * @param key：likeKey
     * @param value：用户id
     * @return
     */
    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("出现异常：" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    /**
     * 统计点赞人数
     * @param likeKey：点赞业务的key
     * @return
     */
    public long scard(String likeKey) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(likeKey);
        } catch (Exception e) {
            logger.error("出现异常：" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 存对象
     * @param key
     * @param obj
     */
    public void setObject(String key, Object obj) {
        //把对象obj 序列化成一个json串，并存储
        set(key, JSON.toJSONString(obj));
    }

    /**
     * 取对象
     */
    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
