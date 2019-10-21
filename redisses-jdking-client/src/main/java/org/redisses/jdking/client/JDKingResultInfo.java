package org.redisses.jdking.client;

public class JDKingResultInfo<T> {
	
	//是否成功过标识
	boolean isSuccess ;
	
	//返回命令执行结果,命令出错的时候返回null
	//命令正常的时候或者
	T resultObj;
	
	// 错误信息描述，成功时候为空
	String errMsg;
	
	//错误类型
	Exception exception;
	
	public JDKingResultInfo() {
		// TODO Auto-generated constructor stub
		isSuccess=false;
		resultObj=null;
		errMsg=null;
		exception=null;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public T getResultObj() {
		return resultObj;
	}

	public void setResultObj(T resultObj) {
		this.resultObj = resultObj;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		return "JDKingResultInfo [isSuccess=" + isSuccess + ", resultObj=" + resultObj + ", errMsg=" + errMsg
				+ ", exception=" + exception.getMessage() + ",exceptionClass=" + (exception==null?null:exception.getClass().getName()) + "]";
	}
	
}
