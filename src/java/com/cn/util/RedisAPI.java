/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 * @author LFeng
 */
public class RedisAPI {
    private static final Logger logger = Logger.getLogger(RedisAPI.class);
    private static JedisPool jedisPool = null;
    
    /**
     * 构建redis连接池
     * 
     * @param ip
     * @param port
     * @return JedisPool
     */
    private static JedisPool getPool(String ip, int port) {
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxActive(500);
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(5);
            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWait(1000 * 100);
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            jedisPool = new JedisPool(config, ip, port);
        }
        return jedisPool;
    }
    
    /**
     * 返还到连接池
     * 
     * @param pool 
     * @param redis
     */
    private static void returnResource(JedisPool pool, Jedis redis) {
        if (redis != null) {
            pool.returnResource(redis);
        }
    }
    
    /**
     * 获取数据
     * 
     * @param key
     * @return
     */
    public static String get(String key){
        try {
            Properties prop = new Properties();
            prop.load(RedisAPI.class.getClassLoader().getResourceAsStream("./config.properties"));
            String value = null;
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = getPool(prop.getProperty("REDIS_HOST"), Integer.valueOf(prop.getProperty("REDIS_PORT")));
                jedis = pool.getResource();
                value = jedis.get(key);
            } catch (Exception e) {
                //释放redis对象
                if (null != pool)
                    pool.returnBrokenResource(jedis);
                logger.error("Redis读取出错", e);
            } finally {
                //返还到连接池
                returnResource(pool, jedis);
            }
            return value;
        } catch (IOException ex) {
            logger.error("Redis配置文件读取错误", ex);
        }
        return null;
    }
    
    public static String set(String key, String value) {
        try {
            Properties prop = new Properties();
            prop.load(RedisAPI.class.getClassLoader().getResourceAsStream("./config.properties"));
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = getPool(prop.getProperty("REDIS_HOST"), Integer.valueOf(prop.getProperty("REDIS_PORT")));
                jedis = pool.getResource();
                return jedis.set(key, value);
            } catch (Exception e) {
                //释放redis对象
                if (null != pool)
                    pool.returnBrokenResource(jedis);
                logger.error("Redis写入出错", e);
            } finally {
                //返还到连接池
                returnResource(pool, jedis);
            }
        } catch (IOException ex) {
            logger.error("Redis配置文件读取错误", ex);
        }
        return null;
    }
    
    public static Long push(String key, String value) {
        try {
            Properties prop = new Properties();
            prop.load(RedisAPI.class.getClassLoader().getResourceAsStream("./config.properties"));
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = getPool(prop.getProperty("REDIS_HOST"), Integer.valueOf(prop.getProperty("REDIS_PORT")));
                jedis = pool.getResource();
                return jedis.rpush(key, value);
            } catch (Exception e) {
                //释放redis对象
                if (null != pool)
                    pool.returnBrokenResource(jedis);
                logger.error("Redis写入出错", e);
            } finally {
                //返还到连接池
                returnResource(pool, jedis);
            }
        } catch (IOException ex) {
            logger.error("Redis配置文件读取错误", ex);
        }
        return null;
    }
    
    public static List<String> getSet(String key){
        try {
            Properties prop = new Properties();
            prop.load(RedisAPI.class.getClassLoader().getResourceAsStream("./config.properties"));
            List<String> value = null;
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = getPool(prop.getProperty("REDIS_HOST"), Integer.valueOf(prop.getProperty("REDIS_PORT")));
                jedis = pool.getResource();
                value = jedis.lrange(key, 0, -1);
            } catch (Exception e) {
                //释放redis对象
                if (null != pool)
                    pool.returnBrokenResource(jedis);
                logger.error("Redis读取出错", e);
            } finally {
                //返还到连接池
                returnResource(pool, jedis);
            }
            return value;
        } catch (IOException ex) {
            logger.error("Redis配置文件读取错误", ex);
        }
        return null;
    }
    
    public static void delKeys(String pattern) {
        try {
            Properties prop = new Properties();
            prop.load(RedisAPI.class.getClassLoader().getResourceAsStream("./config.properties"));
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = getPool(prop.getProperty("REDIS_HOST"), Integer.valueOf(prop.getProperty("REDIS_PORT")));
                jedis = pool.getResource();
                Iterator<String> keyIterator = jedis.keys(pattern).iterator();
                while (keyIterator.hasNext()) {
                    jedis.del(keyIterator.next());
                }
            } catch (Exception e) {
                //释放redis对象
                if (null != pool)
                    pool.returnBrokenResource(jedis);
                logger.error("Redis写入出错", e);
            } finally {
                //返还到连接池
                returnResource(pool, jedis);
            }
        } catch (IOException ex) {
            logger.error("Redis配置文件读取错误", ex);
        }
    }
    
    public static String flushDB(){
        try {
            Properties prop = new Properties();
            prop.load(RedisAPI.class.getClassLoader().getResourceAsStream("./config.properties"));
            String value = null;
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = getPool(prop.getProperty("REDIS_HOST"), Integer.valueOf(prop.getProperty("REDIS_PORT")));
                jedis = pool.getResource();
                value = jedis.flushDB();
            } catch (Exception e) {
                //释放redis对象
                if (null != pool)
                    pool.returnBrokenResource(jedis);
                logger.error("Redis读取出错", e);
            } finally {
                //返还到连接池
                returnResource(pool, jedis);
            }
            return value;
        } catch (IOException ex) {
            logger.error("Redis配置文件读取错误", ex);
        }
        return null;
    }
    
    public static String flushAll(){
        try {
            Properties prop = new Properties();
            prop.load(RedisAPI.class.getClassLoader().getResourceAsStream("./config.properties"));
            String value = null;
            JedisPool pool = null;
            Jedis jedis = null;
            try {
                pool = getPool(prop.getProperty("REDIS_HOST"), Integer.valueOf(prop.getProperty("REDIS_PORT")));
                jedis = pool.getResource();
                value = jedis.flushAll();
            } catch (Exception e) {
                //释放redis对象
                if (null != pool)
                    pool.returnBrokenResource(jedis);
                logger.error("Redis读取出错", e);
            } finally {
                //返还到连接池
                returnResource(pool, jedis);
            }
            return value;
        } catch (IOException ex) {
            logger.error("Redis配置文件读取错误", ex);
        }
        return null;
    }
    /*
    public static void main(String[] args) {
        System.out.println(RedisAPI.get("partBaseInfo_2803011-02-W01D"));
    }
    */
}
