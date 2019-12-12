package org.nercita.bcp.util.redis;

import org.nercita.bcp.record.util.FilesPathUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis连接池
 * lvty
 *
 */
public class RedisPool {
	private static JedisPool pool; //jedis连接池
	private static Integer maxTotal = 20;//最大连接数
	private static Integer maxIdle = 10;//jedispool中最大idle(空闲)状态的jedis实例
	private static Integer minIdle = 2;//jedispool中最小idle(空闲)状态的jedis实例
	private static Boolean testOnBorrow = true;//在borrow一个jedis实例的时候，需要进行验证。设置为true时，借实例必定可用。
	private static Boolean testOnReturn = false;//在return一个jedis实例的时候，需要进行验证。设置为true时，还实例必定可用。
	
	private static String redisHost = FilesPathUtils.getProperty("redis.host");
	private static Integer redisPort = Integer.valueOf(FilesPathUtils.getProperty("redis.port"));
	private static String redisPassword = FilesPathUtils.getProperty("redis.password");
	
	private static void initPool(){
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		
		config.setBlockWhenExhausted(true);
		
		pool = new JedisPool(config, redisHost, redisPort, 1000*2 , redisPassword);
		//pool = new JedisPool(config, redisHost, redisPort, 1000*2);
	}
	
	static{
		initPool();
	}
	
	public static Jedis getJedis(){
		return pool.getResource();
	}
	
	public static void returnResource(Jedis jedis){
		if(jedis != null){
			pool.returnResource(jedis);
		}
	}
	
	public static void returnBrokenResource(Jedis jedis){
		if(jedis != null){
			pool.returnBrokenResource(jedis);
		}
	}
	
	public static void main(String[] args) {
		Jedis jedis = pool.getResource();
		jedis.set("tyKey1", "tyValue1");
		returnResource(jedis);
		System.out.println("success");
		pool.destroy();
	}
	
	
}
