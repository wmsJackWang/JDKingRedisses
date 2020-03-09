--获取参数模块
--库存商品的唯一 key
local key = KEYS[1]

--要减掉库存的数量 
local reduceValue = ARGV[1]
--类型转换
local reduceNumber = tonumber(reduceValue)


--比较模块，比较现存库存数量是否大于扣减量
--获取库现有存量
local result = redis.call('get',key)
--将result的string类型转换成数字类型
local resultNumber = tonumber(result)
--比较现有库存量和扣减量
if(resultNumber<reduceNumber)
then 
	return -1    -- 库存量不足，减库存失败

else
	local finalNumber = redis.call('DECRBY',key,reduceNumber)
	return finalNumber
end
	