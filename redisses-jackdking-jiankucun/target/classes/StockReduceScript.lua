--获取参数模块
--库存商品的唯一 key
local key = KEYS[1]

--获取减库存请求订单的订单号
local orderid = KEYS[2]

--要减掉库存的数量 
local reduceValue = ARGV[1]
--类型转换
local reduceNumber = tonumber(reduceValue)


--比较模块，比较现存库存数量是否大于扣减量
--获取库现有存量
local result = redis.call('get',key)
--将result的string类型转换成数字类型
local resultNumber = tonumber(result)

--减库存后 将成功订单放入到list中，list名称
local listName = "orderlist"

--比较现有库存量和扣减量
if(resultNumber<reduceNumber)
then 
	return -1    -- 库存量不足，减库存失败

else
	--进行减库存操作。
	local finalNumber = redis.call('DECRBY',key,reduceNumber)
	local reducevalue = tostring(reduceNumber)
	--将成功减库存的订单号和 减少的库存量 用冒号拼接在一起 作为value放入到异步队列中
	--异步队列 就被 其他系统执行
	local listvalue = orderid..":"..reducevalue
	--将减库存信息放入到 出货队列中去。
	redis.call('lpush',listName,listvalue)
	
	return finalNumber
end
	