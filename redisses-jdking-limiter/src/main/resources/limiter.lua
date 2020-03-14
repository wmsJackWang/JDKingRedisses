--将限流key 的值自增 1,并返回自增后的值
local num=redis.call('incr',KEYS[1])
--判断自增后值的大小，如果值等于1:表示这是第一次访问，则设定一个过期时间,并返回一个值
if tonumber(num)==1 then
	redis.call('pexpire',KEYS[1],ARGV[1])
	return 1
--如果值大于设定的流量阙值 ARGV[2], 则表示 流量请求数量超过阙值，则返回失败 0
elseif tonumber(num)>tonumber(ARGV[2]) then
	return 0
	
--如果返回的值在1到阙值之间(包括阙值)，则返回成功1
else 
	return 1
end