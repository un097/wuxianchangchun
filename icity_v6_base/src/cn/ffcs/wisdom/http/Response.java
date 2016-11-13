package cn.ffcs.wisdom.http;

public abstract interface Response {

	public boolean isSuccess();

	public String getHttpResult();
}
