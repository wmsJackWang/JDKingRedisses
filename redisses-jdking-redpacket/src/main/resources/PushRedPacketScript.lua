--local result ={} 
local e
--for i = 1,#(KEYS) do 
--   result[i]= redis.call('get',KEYS[i]) 
--end 

for i = 1,#(ARGV) do 
   e = ARGV[i]
   redis.call('lpush',KEYS[1],e)
end 

return 'SUCCESS'