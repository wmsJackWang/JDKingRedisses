package org.redisses.jdking.redpacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import redis.clients.jedis.Jedis;


/*
 * 实现了发红包下   拆分的子红包的数据原子性。
 */
public class PushRedPacket {
	
	private static String PushRedPacketScript ;
	//类加载进jvm的时候就执行初始化方法init
	static {
		init();
	}

    public static void init() {
   	 
   	 try {
   		 InputStream inputStream = Objects.requireNonNull(
   				PushRedPacket.class.getClassLoader().getResourceAsStream("PushRedPacketScript.lua"));
   		PushRedPacketScript = readFile(inputStream);
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
	
    private static String readFile(InputStream inputStream) throws IOException {
        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line)
                    .append(System.lineSeparator());
            }

            return stringBuilder.toString();
        } 
    }
    
    
    
    public static void pushRedPakcet(List<String> keys, List<String> values) {

		Jedis jedis = new Jedis("localhost");
		
		Object ret = jedis.eval(PushRedPacketScript, keys, values);
		
		System.out.println(ret);
    }
    
    
    public static void main(String[] args) {
//    	pushRedPakcet(Arrays.asList("test"));
	}
	
}
