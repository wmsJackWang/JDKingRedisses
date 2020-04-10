本项目是一个简单的 高并发减库存 场景实现 代码demo

Redis官方号称是 1s内能执行10万条命令的性能，充分利用好redis性能，用于高并发常见的场景：减库存。

本项目的 lua脚本就是一个简单的减库存逻辑模板，可套用在任何高并发的减库存场景。

jiakuncun项目更详细解释和逻辑描述请查看我这边文章：<a href="http://bittechblog.com:8080/article/jedis-lua-1#4">lua脚本在redis中的使用-lua高并发减库存</a>
