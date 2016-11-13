package cn.ffcs.wisdom.city.simico.activity.subscribe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.activity.home.cache.IndexCacheUtils;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.api.model.MenuHelper;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequestListener;
import cn.ffcs.wisdom.city.simico.api.request.GetChannelList;
import cn.ffcs.wisdom.city.simico.api.request.GetSubscribeServiceRequest;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;
import cn.ffcs.wisdom.city.simico.activity.subscribe.view.Channel;

//import com.actionbarsherlock.app.ActionBar;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

public class SubscribeDragActivity extends SubscribeActivity {

	protected static final String TAG = SubscribeActivity.class.getSimpleName();
	private static final String PREFIX_MY_CHANNEL = "MY_CHANNEL";
	private static final String PREFIX_ALL_CHANNEL = "ALL_CHANNEL";
	private LayoutTransition i;
	private boolean mIsLoadingData;
	private JSONArray lastMyChannel;

	public SubscribeDragActivity() {
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		i = new LayoutTransition();
		i.setDuration(100L);

		refresh();
	}

	private void initActionBar() {
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//		View titleBar = getLayoutInflater().inflate(R.layout.simico_titlebar_common, null);
//		actionBar.setCustomView(titleBar);
		View titleBar = findViewById(R.id.titlebar_subscribe);
		titleBar.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		TextView title = (TextView) titleBar.findViewById(R.id.tv_title);
		title.setText("频道管理");
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void starChannelAnim(View view, float fromXValue, float fromYValue, float toXValue,
			float toYValue) {
		super.starChannelAnim(view, fromXValue, fromYValue, toXValue, toYValue);
		mFvAllChannel.setLayoutTransition(null);
		mFvMyChannel.setLayoutTransition(null);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean onDrag(int action, View moveView, View souceView) {
		TextView tv = null;
		boolean flag = false;
		if (souceView != null) {
			tv = (TextView) souceView.findViewById(R.id.text_item);
		}
		switch (action) {
		case 1:// 开始拖动
			mFvMyChannel.setLayoutTransition(i);
			mFvAllChannel.setLayoutTransition(i);
			if (tv != null) {
				tv.setVisibility(View.INVISIBLE);
				flag = true;
			} else {
				flag = false;
			}
			break;
		case 2:
			// Log.d(TAG, "正在拖动...");
			// if (i.isRunning())
			// flag = true;
			// else
			flag = handleMoveDrag(souceView, moveView);
			break;
		case 3:// 结束拖动
		case 4:// 结束拖动
			if (tv != null)
				tv.setVisibility(View.VISIBLE);
			resetData(mFvMyChannel, mMyChannel);
			resetData(mFvAllChannel, mAllChannel);
			mFvMyChannel.setLayoutTransition(null);
			mFvAllChannel.setLayoutTransition(null);
			if (souceView != null)
				souceView.setSelected(false);
			saveChannelData();
			flag = true;
			break;
		default:
			break;
		}
		return flag;
	}

	@Override
	public void refresh() {
		long current = System.currentTimeMillis();
		// 如果超过该同步订阅数据时间则先同步后再请求列表数据
		// if (current - Application.getLastSyncSubscribeChannelTime() >=
		// Constants.INTEVAL_SYNC_TIME) {
		// TLog.log(TAG, "syncSubscribeService");
		// syncSubscribeService();
		// } else {
		TLog.log(TAG, "sendRequest");
		// sendRequest();
		new LoadMyChannelCacheTask().execute();
		// }
	}

	// 同步订阅数据
	private void syncSubscribeService() {
		mIsLoadingData = true;
		GetSubscribeServiceRequest req = new GetSubscribeServiceRequest(new BaseRequestListener() {

			@Override
			public void onRequestSuccess(ApiResponse json) throws Exception {
				JSONArray array = (JSONArray) json.getData();
				mMyChannel = new ArrayList<Channel>();
				// int count = 0;
				if (array != null) {
					mMyChannel = MenuHelper.makeMyChannelV2(array);
					// count = mMyChannel.size();
					// 保存缓存
					new SaveCacheTask(PREFIX_MY_CHANNEL).execute(array);
				}
				// Application.setSubscribeServiceCount(count);
				// Application.setLastSyncSubscribeServiceIds(serviceIds);
				// Application.setLastSyncSubscribeServiceJSON(jsonStr);
				Application.setLastSyncSubscribeChannelTime(System.currentTimeMillis());
			}

			@Override
			public void onRequestFinish() {
				sendRequest();
			}

			@Override
			public void onRequestFaile(ApiResponse json, Exception e) {
				TLog.error(getErrorMessage(json));
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				TLog.error(arg0.getMessage());
				sendRequest();
			}
		});
		Application.instance().addToRequestQueue(req);
	}

	private void sendRequest() {
		mIsLoadingData = true;
		if ((!TDevice.hasInternet() || System.currentTimeMillis()
				- Application.getChannelLastCacheTime() <= Constants.INTEVAL_SYNC_TIME)) {
			TLog.log(TAG, "优先读取缓存CHANNEL");
			new LoadAllChannelCacheTask().execute();
			return;
		}
		getDataFromNetwork();
	}

	private void getDataFromNetwork() {
		mIsLoadingData = true;
		TLog.log(TAG, "准备从网络获取数据CHANNEL");
		GetChannelList req = new GetChannelList(new BaseRequestListener() {

			@Override
			public void onRequestSuccess(ApiResponse json) throws Exception {
				JSONArray tagJson = (JSONArray) json.getData();
				TLog.log(TAG, tagJson.toString());
				if (tagJson != null) {
					mAllChannel = MenuHelper.makeAllChannelV2(tagJson, mMyChannel);
					// 保存缓存
					new SaveCacheTask(PREFIX_ALL_CHANNEL).execute(tagJson);
					Application.setChannelLastCacheTime(System.currentTimeMillis());
				}
			}

			@Override
			public void onRequestFinish() {
				initChannelView(mAllChannel, mFvAllChannel, true);
				mIsLoadingData = false;
			}

			@Override
			public void onRequestFaile(ApiResponse json, Exception e) {
				e.printStackTrace();
				Application.showToastShort(getErrorMessage(json));
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Application.showToastShort(BaseRequestListener.getErrorMessage(null));
				error.printStackTrace();
				new LoadAllChannelCacheTask().execute();
			}
		});
		Application.instance().addToRequestQueue(req);
	}

	class SaveCacheTask extends AsyncTask<JSONArray, Void, Void> {

		private String prefix;

		SaveCacheTask(String prefix) {
			this.prefix = prefix;
		}

		@Override
		protected Void doInBackground(JSONArray... params) {
			IndexCacheUtils.saveCacheNoUser(params[0], prefix + Application.getCurrentCity());
			return null;
		}
	}

	class LoadAllChannelCacheTask extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {
			String cacheContent = IndexCacheUtils.getCacheNoUser(PREFIX_ALL_CHANNEL
					+ Application.getCurrentCity());
			if (!TextUtils.isEmpty(cacheContent)) {
				try {
					JSONArray tagJson = new JSONArray(cacheContent);
					if (tagJson != null) {
						mAllChannel = MenuHelper.makeAllChannelV2(tagJson, mMyChannel);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray tagJson) {
			mIsLoadingData = false;
			initChannelView(mAllChannel, mFvAllChannel, true);
			super.onPostExecute(tagJson);
		}
	}

	class LoadMyChannelCacheTask extends AsyncTask<Void, Void, JSONArray> {

		@Override
		protected JSONArray doInBackground(Void... params) {
			String cacheContent = IndexCacheUtils.getCacheNoUser(PREFIX_MY_CHANNEL
					+ Application.getCurrentCity());
			if (!TextUtils.isEmpty(cacheContent)) {
				try {
					JSONArray tagJson = new JSONArray(cacheContent);
					if (tagJson != null) {
						lastMyChannel = tagJson;
						mMyChannel = MenuHelper.makeMyChannelV2(tagJson);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray tagJson) {
			sendRequest();
			mIsLoadingData = false;
			initChannelView(mMyChannel, mFvMyChannel, true);
			super.onPostExecute(tagJson);
		}
	}

	@Override
	protected void saveChannelData() {
		JSONArray array = new JSONArray();
		for (Channel chnl : mMyChannel) {
			try {
				array.put(new JSONObject(chnl.e));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		new SaveCacheTask(PREFIX_MY_CHANNEL).execute(array);
		Application.setNeedRefreshChannelOnResume(true);
	}
}
