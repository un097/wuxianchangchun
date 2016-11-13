package cn.ffcs.wisdom.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * <h3>事件处理器基类      </h3>
 */
public abstract class BaseProcessor implements IProcessor {

	public Context mContext;
	public Intent mIntent;
	public String mAction;
	
	private static final String ROUTER_RESP_ACTION_TYPE = "k_router_response_action_type";

	public BaseProcessor(Context context, Intent intent) {
		this.mContext = context;
		this.mIntent = intent;
	}
	
	/**
	 * 获取返回的Action
	 * @return String
	 * 		相关action
	 */
	public String getRespAction() {
		if(mIntent != null) {
			Bundle b = mIntent.getExtras();
			return b.getString(ROUTER_RESP_ACTION_TYPE);
		}
		
		return "";
	}
}
