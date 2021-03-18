-- 函数：尝试获得红包，如果成功，则返回json字符串，如果不成功，则返回空 
-- 参数：红包队列名， 已消费的队列名，去重的Map名，用户ID 
-- 返回值：nil 或者 json字符串，包含用户ID：userId，红包ID：id，红包金额：money 

-- 如果队列不存在，则表示红包信息不存在 。
if rediscall('EXISTS',KEYS[1])==false or rediscall('llen',KEYS[1]) == 0 then
	return nil
end
 
-- 如果用户已抢过红包，则返回nil 
if rediscall('hexists', KEYS[3], KEYS[4]) ~= 0 then 
 return nil 
else 
 -- 先取出一个小红包 
 local hongBao = rediscall('rpop', KEYS[1]); 
 if hongBao then 
  local x = cjsondecode(hongBao); --这个相当于将json字符串转化成jsonobject
  -- 加入用户ID信息 
  x['userId'] = KEYS[4]; 
  local re = cjsonencode(x); --这个 就是将jsonobject转化成json字符串
  -- 把用户ID放到去重的set里 
  rediscall('hset', KEYS[3], KEYS[4], KEYS[4]); 
  -- 把红包放到已消费队列里 
  rediscall('lpush', KEYS[2], re); 
  return re; 
 end 
end 
return nil 