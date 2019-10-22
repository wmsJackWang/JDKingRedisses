package com.jsondream.redisses.Mq.pushPull.bean;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/16
 */
public class BaseBean implements Serializable {
	
	public BaseBean() {
		// TODO Auto-generated constructor stub
		
	}
	//初始化创建类的名称
	public BaseBean(String classKey) {
		this.classKey = classKey;
	}
	
    // 塞入队列的时间戳
    protected long timeMillis;
    // 为了以后支持集群的扩展,寻找对应的server在处理的id
    protected String serverId;
    // 重新定义的消息Id,这个是为了完成消费者玩的幂等性
    protected String messageId;
    // 消息消费的时间(塞入到doing队列的时间)
    protected long execTime;
    // 存放消息类 的类名称
    protected String classKey;
	public long getTimeMillis() {
		return timeMillis;
	}
	public void setTimeMillis(long timeMillis) {
		this.timeMillis = timeMillis;
	}
	public String getServerId() {
		return serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public long getExecTime() {
		return execTime;
	}
	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}
	public String getClassKey() {
		return classKey;
	}
	public void setClassKey(String classKey) {
		this.classKey = classKey;
	}
}
