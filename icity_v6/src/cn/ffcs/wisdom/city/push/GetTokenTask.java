package cn.ffcs.wisdom.city.push;

import android.content.Context;
import cn.ffcs.wisdom.base.BaseTask;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

public class GetTokenTask extends BaseTask<Void, Void, Void> {

	protected IGetToken call;
	protected String deviceToken;

	public GetTokenTask(HttpCallBack<?> iCall, Context context) {
		super(iCall, context);
	}

	public void setCallBack(IGetToken iCall) {
		this.call = iCall;
	}

	@Override
	protected BaseResp doInBackground(Void... params) {
//		MCRegistration registration = new MCRegistration();
//		String mVersion = AppHelper.getVersionName(mContext);
//		String packageName = AppHelper.getPackageName(mContext);
//		Log.i("向消息中心注册的版本号：" + mVersion + ",包名：" + packageName + ",应用id：" + Config.PUSH_APPLICATION_ID);
//		deviceToken = registration.register(mContext, mVersion, Config.PUSH_APPLICATION_ID, packageName);
		return null;
	}

	@Override
	protected void onPostExecute(BaseResp response) {
		if (call != null) {
			call.getToken(deviceToken);
		}
	}
}
