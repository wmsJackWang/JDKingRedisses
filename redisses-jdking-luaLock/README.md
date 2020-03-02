LuaSample.java 类的两种方法分别测试了jedis的两种执行lua脚本的api
- testLua()  // eval命令执行lua脚本
- testEvalshaLua()  //evalsha+script load  执行事先缓存到redis服务器上的lua脚本

lua脚本代码如下：<br/>
  redis.call('incr',KEYS[1])<br/>
  if tonumber(num)==1 then <br/>
    redis.call('pexpire',KEYS[1],ARGV[1])<br/>
    return 1<br/>
  elseif tonumber(num)>tonumber(ARGV[2]) then<br/>
    return 0<br/>
  else<br/>
    return 1<br/>
  end<br/>
  
  该lua脚本实现的是 限制ip的流量，在某段时间内  限制ip请求的数量。
