package org.nercita.bcp.util.redis;

import org.nercita.bcp.record.util.FilesPathUtils;

/**
 * 使用redis加分布式锁的教程
 * 系统没有任何地方调用了这里的方法，这个class本身只调用了RedisPoolUtil.java
 * 不会对系统的功能有任何影响
 * lvty
 */
public class DistributedLockUtil {
	
	private DistributedLockUtil(){
    }
 
    public static boolean lock(String lockName){//lockName可以为共享变量名，也可以为方法名，主要是用于模拟锁信息
        System.out.println(Thread.currentThread() + "开始尝试加锁！");
        Long result = RedisPoolUtil.setnx(lockName, String.valueOf(System.currentTimeMillis() + 5000));
        //result为0，未获取到锁。result为1，获取到锁。
        if (result != null && result.intValue() == 1){
            System.out.println(Thread.currentThread() + "加锁成功！");
            //设置锁过期时间，单位是秒
            RedisPoolUtil.expire(lockName, 5);
            System.out.println(Thread.currentThread() + "执行业务逻辑！");
            //删除锁
            RedisPoolUtil.del(lockName);
            return true;
        } else {
        	//未获取到锁，继续判断时间戳，看是否可以重置并取到锁
            String lockValueStr = RedisPoolUtil.get(lockName);
            if (lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)){
                String getSetResult = RedisPoolUtil.getSet(lockName, String.valueOf(System.currentTimeMillis() + 5000));
                if (getSetResult == null || (getSetResult != null && getSetResult.equals(lockValueStr))){
                    System.out.println(Thread.currentThread() + "加锁成功！");
                    //设置锁过期时间，单位是秒
                    RedisPoolUtil.expire(lockName, 5);
                    System.out.println(Thread.currentThread() + "执行业务逻辑！");
                    //删除锁
                    RedisPoolUtil.del(lockName);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
    
    /*
     * 针对方案数据处理的加锁模板， 真实场景在/record/material/processBreedingListMaterialData中。
     * 根据方案数据处理10000条数据的极限时间，设定验证锁的时间戳为900000毫秒(900秒)，锁过期时间为1000秒(自动删除)。
     * 超过10000条数据就会直接进入任务处理，和锁就没有任何关系了。
     * 
     * 900秒只内则会提示方案正在处理
     * 900-1000秒之间点击处理同一方案，会重新设置锁的时间戳为当前时间+900秒
     * 1000秒之后点击，则锁已经被自动删除，所以会设置新的锁。
     * 
     * 不要管这个方法的方法名， 因为只看方法里的代码就行
     */
    public static String breedingListProcessLock(String breedingListId){//lockName可以为共享变量名，也可以为方法名，主要是用于模拟锁信息
    	//从配置文件中获取ifUsingRedis字段，为yes时使用redis锁，不为yes时不使用redis锁。如果需要自己的关键字，需要自己在配置文件里面设置新的字段
    	String ifUsingRedis = FilesPathUtils.getProperty("redis.ifUsing");
		if(ifUsingRedis.equals("yes")) {//ifUsingRedis为yes时，使用redis锁
			System.out.println(Thread.currentThread() + "开始尝试加锁！");
	        String lockName = "processBreedingList-"+breedingListId;
	        //锁定key为lockName，value为当前时间戳+900000毫秒
	        Long result = RedisPoolUtil.setnx(lockName, String.valueOf(System.currentTimeMillis() + 900000));
	        //result为0，未获取到锁。result为1，获取到锁。
	        if (result != null && result.intValue() == 1){
	            System.out.println(Thread.currentThread() + "加锁成功！");
	            //设置锁过期时间，单位是秒
	            RedisPoolUtil.expire(lockName, 1000);
	            System.out.println(Thread.currentThread() + "执行业务逻辑！");
	            //-----------------业务逻辑-----------------
	            //删除锁
	            RedisPoolUtil.del(lockName);
	            return "ok";
	        } else {
	        	//未获取到锁，继续判断时间戳，看是否可以重置并取到锁
	            String lockValueStr = RedisPoolUtil.get(lockName);
	            if (lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)){
	            	//重新锁定key为lockName，value为当前时间戳+900000毫秒
	                String getSetResult = RedisPoolUtil.getSet(lockName, String.valueOf(System.currentTimeMillis() + 900000));
	                if (getSetResult == null || (getSetResult != null && getSetResult.equals(lockValueStr))){
	                    System.out.println(Thread.currentThread() + "加锁成功！");
	                    //设置锁过期时间，单位是秒
	                    RedisPoolUtil.expire(lockName, 1000);
	                    System.out.println(Thread.currentThread() + "执行业务逻辑！");
	                    //-----------------业务逻辑-----------------
	                    //删除锁
	                    RedisPoolUtil.del(lockName);
	                } else {
	                	return "breedingListProcessing";
	                }
	            } else {
	            	return "breedingListProcessing";
	            }
	        }
		}else{//ifUsingRedis不为yes时，不使用redis锁
			//-----------------业务逻辑-----------------
		}
		return "ok"; 
    }
}
