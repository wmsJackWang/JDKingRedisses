package org.redisses.jdking.redpacket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;

/**
 * Hello world!
 *
 */
public class RedPacket 
{
	static String host = "localhost";
	static int honBaoCount = 10;
	static double hongBao = 98.8;
	
	static int threadCount = 20;
	
	static String hongBaoList = "hongBaoList";
	static String hongBaoConsumedList = "hongBaoConsumedList";
	static String hongBaoConsumedMap = "hongBaoConsumedMap";
	
	static Random random = new Random();
	
//	-- 函数：尝试获得红包，如果成功，则返回json字符串，如果不成功，则返回空
//	-- 参数：红包队列名， 已消费的队列名，去重的Map名，用户ID
//	-- 返回值：nil 或者 json字符串，包含用户ID：userId，红包ID：id，红包金额：money
	static String tryGetHongBaoScript = 
//			"local bConsumed = redis.call('hexists', KEYS[3], KEYS[4]);\n"
//			+ "print('bConsumed:' ,bConsumed);\n"
			"if redis.call('hexists', KEYS[3], KEYS[4]) ~= 0 then\n"
			+ "return nil\n"
			+ "else\n"
			+ "local hongBao = redis.call('rpop', KEYS[1]);\n"
//			+ "print('hongBao:', hongBao);\n"
			+ "if hongBao then\n"
			+ "local x = cjson.decode(hongBao);\n"
			+ "x['userId'] = KEYS[4];\n"
			+ "local re = cjson.encode(x);\n"
			+ "redis.call('hset', KEYS[3], KEYS[4], KEYS[4]);\n"
			+ "redis.call('lpush', KEYS[2], re);\n"
			+ "return re;\n"
			+ "end\n"
			+ "end\n"
			+ "return nil";
	static StopWatch watch = new StopWatch();
	
	public static void main(String[] args) throws InterruptedException {
//		testEval();
//		generateTestData();
		testTryGetHongBao();
	}
	
	static public void generateTestData() throws InterruptedException {
		Jedis jedis = new Jedis(host);
		jedis.flushAll();
		final CountDownLatch latch = new CountDownLatch(threadCount);
		double[] redpackets = RedPacketSplitAlgorithm.generateDoubleRedPacket(hongBao, honBaoCount);
		
  
		JSONObject object = new JSONObject();
		List<String> values = new ArrayList<String>();
		for(int j = 0; j < honBaoCount; j++) {
			object.put("id", j);
			object.put("money", redpackets[j]);
//			jedis.lpush(hongBaoList, object.toJSONString());
			values.add(object.toJSONString());
		}
		List<String> keys = Arrays.asList(RedPacket.hongBaoList);
		PushRedPacket.pushRedPakcet(keys,values);
	}
	
	static public void testTryGetHongBao() throws InterruptedException {
//		final CountDownLatch latch = new CountDownLatch(threadCount);
		System.err.println("start:" + System.currentTimeMillis()/1000);
		Jedis jedis = new Jedis(host);
		String sha = jedis.scriptLoad(tryGetHongBaoScript);
//		watch.start();
					
		int j = honBaoCount;
		while(true) {
			Object object = jedis.evalsha(sha , 4, hongBaoList, hongBaoConsumedList, hongBaoConsumedMap, "" + j);
//						Object object = jedis.eval(tryGetHongBaoScript, 4, hongBaoList, hongBaoConsumedList, hongBaoConsumedMap, "" + j);
			j++;
			if (object != null) {
				System.out.println("get hongBao:" + object);
			}else {
				//已经取完了
				if(jedis.llen(hongBaoList) == 0)
					System.out.println("红包已经取完了");
					break;
			}
		}
		
//		latch.await();
//		watch.stop();
//		
//		System.err.println("time:" + watch.getTime());
//		System.err.println("speed:" + 1000*honBaoCount/watch.getTime());
//		System.err.println("end:" + System.currentTimeMillis()/1000);
	}
}
