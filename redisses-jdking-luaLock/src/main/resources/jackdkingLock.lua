--获取锁key和锁设置的过期时间
local key = KEYS[1]
local expireTime = ARGV[1]

local value = ARGV[2] 

--使用redis的setnx命令加锁 
local result = redis.call('setnx',key,value)

if result == 1 
then 
	-- 加锁成功
	--expireResult==0的情况是为了支持Redis versions <2.1.3情形下，过期时间存在则不会设置，之后的版本则会覆盖过期时间
	local res = redis.call('expire',key,expireTime)
	if res == 1
	then
		return 1
	else
		return 0
	end
else
	return 0
end