package cn.ffcs.wisdom.city.push;

import java.util.List;


import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.sqlite.model.CityConfig;
import cn.ffcs.wisdom.city.sqlite.model.NotificationInfo;
import cn.ffcs.wisdom.city.sqlite.service.NotificationInfoService;
import cn.ffcs.wisdom.city.utils.AppUtil;
import cn.ffcs.wisdom.city.utils.Callback;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.notify.NotificationConstants;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

public class NotificationActivity extends WisdomCityActivity {

	private NotificationAdapter mAdapter;
	private ListView mListView;
	private List<NotificationInfo> noticeList;
	private View mNoDataImg;
	private View mClear;

	private NotificationInfoService service;

	private PushMsgBo pushBo;
	private boolean fromPushFlag;

	@Override
	protected void initComponents() {
		mListView = (ListView) findViewById(R.id.notification_listview);
		mListView.setOnItemClickListener(new OnItemClickLinstener());
//		mListView.setOnCreateContextMenuListener(this);
		mNoDataImg = findViewById(R.id.notification_nodata);
		mClear = findViewById(R.id.top_right_title);
		mClear.setOnClickListener(new OnClearNoticeListener());
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.notification_title);
		TopUtil.updateRightTitle(mActivity, R.id.top_right_title, R.string.person_center_clear_notice);
		service = NotificationInfoService.getInstance(mContext);
		pushBo = new PushMsgBo(mActivity);
		pushBo.pushFeedback(Config.REBACK_USER_CLICK_MSG_TYPE, false);//用户点击消息（通知栏点击、弹出点击确定按钮）--回执

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
				creatDeleteDialog(pos);
				return true;
			}

		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}


	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);// 必须要调用这句
	}
	
	private void creatDeleteDialog(final int pos) {
		OnClickListener confirmClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertBaseHelper.dismissAlert(mActivity);
				NotificationInfo table = noticeList.get(pos);
				MsgEntity entity = JsonUtil.toObject(table.getMsgInfo(), MsgEntity.class);
				if (entity != null) {
					service.remove(entity);
					// 重新刷新数据
					updateListView();
					CommonUtils.showToast(mActivity, R.string.notice_cancel_success,
							Toast.LENGTH_SHORT);
				}
			}
		};
		AlertBaseHelper.showConfirm(mActivity, R.string.notice_cancel, R.string.notice_delete_desc,
				confirmClick);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateListView();
	}

	/**
	 * 更新ListView视图
	 */
	protected void updateListView() {
		// 获取数据
		List<NotificationInfo> noticeList = getNotificationInfoList();
		// 更新Adapter
		if (mAdapter == null) {
			mAdapter = new NotificationAdapter(mContext);
			mAdapter.setData(noticeList);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.setData(noticeList);
			mAdapter.notifyDataSetChanged();
		}
		// 没有数据时，显示无数据通知背景图
		if (noticeList != null && noticeList.size() == 0) {
			mNoDataImg.setVisibility(View.VISIBLE);
		} else {
			mNoDataImg.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取所有通知数据
	 */
	protected List<NotificationInfo> getNotificationInfoList() {
//		noticeList = service.findSystemNotice(mContext);
		noticeList = service.findAll(mContext);
		for (int i = 0; i < noticeList.size(); i++) {
			pushBo.clearMsg(noticeList.get(i).getMsgId());
		}
		return noticeList;
	}

//	//添加长按点击 
//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//		if (noticeList != null && noticeList.size() > 0) {
//			int pos = info.position;
//			NotificationInfo table = noticeList.get(pos);
//			MsgEntity notice = JsonUtil.toObject(table.getMsgInfo(), MsgEntity.class);
//			menu.setHeaderTitle(notice.getTitle());
//			menu.add(Menu.NONE, 0, 0, getString(R.string.notice_cancel));
//		} else {
//			super.onCreateContextMenu(menu, v, menuInfo);
//		}
//	}
//
//	//长按菜单响应函数  
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//		if (item.getItemId() == 0) {
//			int pos = info.position;
//			NotificationInfo table = noticeList.get(pos);
//			MsgEntity entity = JsonUtil.toObject(table.getMsgInfo(), MsgEntity.class);
//			if (entity != null) {
//				service.remove(entity);
//				// 重新刷新数据
//				updateListView();
//				CommonUtils.showToast(mActivity, R.string.notice_cancel_success, Toast.LENGTH_LONG);
//				return true;
//			}
//		}
//		return false;
//	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_notification;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}
	
	class OnClearNoticeListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (noticeList.isEmpty()) {
				AlertBaseHelper.showMessage(mActivity, "数据为空！");
				return;
			}
			OnClickListener confirmClick = new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertBaseHelper.dismissAlert(mActivity);
					service.clear();
					// 重新刷新数据
					updateListView();
					CommonUtils.showToast(mActivity, R.string.notice_clear_success,
							Toast.LENGTH_SHORT);
				}
			};
			AlertBaseHelper.showConfirm(mActivity, R.string.notice_clear, R.string.notice_clear_desc,
					confirmClick);
			
		}
		
	}

	class OnItemClickLinstener implements OnItemClickListener {

		String url = "";

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			NotificationInfo info = (NotificationInfo) parent.getAdapter().getItem(pos);
			if (info == null) {
				return;
			}
			try {
				MsgEntity notice = JsonUtil.toObject(info.getMsgInfo(), MsgEntity.class);
				String url = notice.getContent().getDesc();
				final String activity = notice.getContent().getActivity();
				String isLogin = notice.getContent().getIsLogin();

				if (!StringUtil.isEmpty(url)) {
					Intent intent = new Intent(mActivity, BrowserActivity.class);
					intent.putExtra(Key.U_BROWSER_URL, url);
					intent.putExtra(Key.K_RETURN_TITLE, mActivity.getString(R.string.home_name));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mActivity.startActivity(intent);
					return;
				}
				if(!StringUtil.isEmpty(activity)){
					if(Boolean.parseBoolean(isLogin)){
						AppUtil.getCurrentUser(mContext, new Callback<Account>() {
							@Override
							public boolean onData(Account data) {
								Intent intent = new Intent();
								intent.setClassName(mActivity, activity);
								mActivity.startActivity(intent);
								return false;
							}
						});
						return;
					}else{
						Intent intent = new Intent();
						intent.setClassName(mActivity, activity);
						mActivity.startActivity(intent);
					}
				}
			}catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}
	}

	private void startBrowserDownload(String url) {
		Intent i = new Intent(mActivity, BrowserActivity.class);
		i.putExtra(Key.U_BROWSER_TITLE, "下载第三方应用");
		i.putExtra(Key.U_BROWSER_URL, url);
		i.putExtra(Key.K_RETURN_TITLE, getString(R.string.notice_msg_list));
		i.putExtra(Key.U_BROWSER_QUERY, "1");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

}
