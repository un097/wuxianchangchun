package cn.ffcs.wisdom.base;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * 异步线程基础类
 * 
 * HttpCallBack 回调监听
 * 
 * @author  caijj
 * @version 1.00, 2012-4-6
 */
@SuppressWarnings("rawtypes")
public abstract class BaseTask<Params, Progress, V> extends AsyncTask<Params, Progress, BaseResp> {

	private HttpCallBack iCall;
	protected Context mContext;

	public BaseTask(HttpCallBack iCall, Context context) {
		this(iCall);
		this.mContext = context;
	}

	public BaseTask(HttpCallBack iCall) {
		this.iCall = iCall;
	}

	@SuppressWarnings("unchecked")
	protected void callBack(BaseResp response) {
		if (iCall != null)
			iCall.call(response);
	}

	@Override
	protected void onPostExecute(BaseResp response) {
		if (response != null) {
			if (BaseResp.NETWORK_ERROR.equals(response.getStatus())) {
				onNetWorkError();
				return;
			}

			
			if (BaseResp.ERROR.equals(response.getStatus())) {
				if(StringUtil.isEmpty(response.getDesc())){
					response.setDesc("服务器或网络异常，请求失败，请稍后再试！");
				}
			}
			callBack(response);
		}
	};

	@Override
	protected void onProgressUpdate(Progress... values) {
		if (iCall != null) {
			iCall.progress(values);
		}
	};

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/**
	 * network error
	 */
	protected void onNetWorkError() {
		if (iCall != null) {
			iCall.onNetWorkError();
		}
	}

	private static final int MESSAGE_POST_NETWORK_ERROR = 0x1;

	@SuppressWarnings("unused")
	private static final InternalHandler sdHandler = new InternalHandler();

	private static class InternalHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			DownloadResult result = (DownloadResult) msg.obj;
			switch (msg.what) {
			case MESSAGE_POST_NETWORK_ERROR:
				result.task.onNetWorkError();
				break;
			}
		}
	}

	private static class DownloadResult {

		final BaseTask task;

		@SuppressWarnings("unused")
		DownloadResult(BaseTask task) {
			this.task = task;
		}
	}

}
