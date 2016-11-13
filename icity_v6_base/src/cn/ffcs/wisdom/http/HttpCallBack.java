package cn.ffcs.wisdom.http;

public interface HttpCallBack<T extends BaseResp> {

	void call(T response);
	
	void progress(Object... obj);
	
	void onNetWorkError();
}
