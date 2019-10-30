local key = KEYS[1]

--删除锁

return  redis.call('del',key)
