package org.nercita.bcp.util.redis;

import redis.clients.jedis.Jedis;

/**
 * redis工具类
 * lvty
 *
 */
public class RedisPoolUtil {
	
	//设置key的有效期是多久,单位时秒
	public static Long expire(String key, int exTime){
		Jedis jedis = null;
		Long result = null;
		
		try{
			jedis = RedisPool.getJedis();
			result = jedis.expire(key,exTime);
		}catch (Exception e){
			e.printStackTrace();
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	//设置过期时间，expire time,单位是秒
	public static String setEx(String key, String value, int exTime){
		Jedis jedis = null;
		String result = null;
		
		try{
			jedis = RedisPool.getJedis();
			result = jedis.setex(key,exTime,value);
		}catch (Exception e){
			e.printStackTrace();
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	public static String set(String key, String value){
		Jedis jedis = null;
		String result = null;
		
		try{
			jedis = RedisPool.getJedis();
			result = jedis.set(key, value);
		}catch (Exception e){
			e.printStackTrace();
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	public static String get(String key){
		Jedis jedis = null;
		String result = null;
		
		try{
			jedis = RedisPool.getJedis();
			result = jedis.get(key);
		}catch (Exception e){
			e.printStackTrace();
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	//delete方法
	public static Long del(String key){
		Jedis jedis = null;
		Long result = null;
		
		try{
			jedis = RedisPool.getJedis();
			result = jedis.del(key);
		}catch (Exception e){
			e.printStackTrace();
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	//set key if not exist
	public static Long setnx(String key, String value){
		Jedis jedis = null;
		Long result = null;
		
		try{
			jedis = RedisPool.getJedis();
			result = jedis.setnx(key, value);
		}catch (Exception e){
			e.printStackTrace();
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	//设置一个新值，同时拿回返回值(原子性)
	public static String getSet(String key, String value){
		Jedis jedis = null;
		String result = null;
		try{
			jedis = RedisPool.getJedis();
			result = jedis.getSet(key, value);
		}catch (Exception e){
			e.printStackTrace();
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	public static void main(String[] args) {
		Jedis jedis = RedisPool.getJedis();
		RedisPoolUtil.set("keyTest", "value");
		//RedisPoolUtil.del("keyTest");
		
		/*RedisPoolUtil.set("keyTest", "value");
		
		String value = RedisPoolUtil.get("keyTest");
		System.out.println(value);
		
		RedisPoolUtil.setEx("keyex", "valueex", 60*10);
		
		RedisPoolUtil.expire("keyex", 60*20);
		
		RedisPoolUtil.del("keyTest");
		
		System.out.println("end");*/
	}
}
