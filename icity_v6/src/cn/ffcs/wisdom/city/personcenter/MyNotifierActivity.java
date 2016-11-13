package cn.ffcs.wisdom.city.personcenter;

import java.util.List;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.personcenter.adapter.MyNotifiAdapter;
import cn.ffcs.wisdom.city.personcenter.adapter.MyNotifiAdapter.Hold;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.push.PushMsgBo;
import cn.ffcs.wisdom.city.sqlite.model.NotificationInfo;
import cn.ffcs.wisdom.city.sqlite.service.NotificationInfoService;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.notify.NotificationConstants;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;

public class MyNotifierActivity extends WisdomCityActivity implements OnItemClickListener,
		OnClickListener {
	private ListView mylistview;
	private List<NotificationInfo> tableList;
	private PushMsgBo pushBo;
	private Button btn_del;
	public static boolean isedit = true;
	private View edit_btn;
	private NotificationInfoService service;
	private MsgEntity msgEntity;
	private TextView text_nodata;
	private MyNotifiAdapter adapter;
	private boolean fromPushFlag;

	@Override
	protected void initComponents() {
		isedit = true;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		mylistview = (ListView) this.findViewById(R.id.notification_listview);
		text_nodata = (TextView) this.findViewById(R.id.notification_text_nodata);
		btn_del = (Button) this.findViewById(R.id.pc_btn_del_notifi);
		TopUtil.updateTitle(this, R.id.top_title, R.string.person_center_my_notice);
		TopUtil.updateRight(this, R.id.top_right, R.drawable.btn_edit_icon_selector);
		edit_btn = this.findViewById(R.id.top_right);
		edit_btn.setOnClickListener(this);
		service = NotificationInfoService.getInstance(mContext);
	}

	@Override
	protected void initData() {
		pushBo = new PushMsgBo(mActivity);
		pushBo.pushFeedback(Config.REBACK_USER_CLICK_MSG_TYPE, false);// 用户点击消息（通知栏点击、弹出点击确定按钮）--回执
		canReadMyMsg();
	}

	/**
	 * 登录才可以查询我的消息
	 */
	private void canReadMyMsg() {
		fromPushFlag = getIntent().getBooleanExtra(NotificationConstants.NOTIFICATION_FLAG, false);
		if (fromPushFlag) {
			boolean loginFlag = AccountMgr.getInstance().isLogin(mContext);
			if (!loginFlag) {
				AlertBaseHelper.showMessage(mActivity, R.string.notice_person_msg_login_tip,
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								finish();
							}
						});
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		tableList = pushBo.getPersonCenterMsg();
		if (tableList != null && tableList.size() != 0) {
			adapter = new MyNotifiAdapter(mContext, tableList);
			mylistview.setAdapter(adapter);
			text_nodata.setVisibility(View.GONE);
		} else {
			mylistview.setVisibility(View.GONE);
		}
		mylistview.setOnItemClickListener(this);
		btn_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (tableList != null && tableList.size() != 0) {
					for (int i = mylistview.getCount(); i >= 0; i--) {
						if (MyNotifiAdapter.getIsSelected().get(i)) {
							Log.d("i=" + String.valueOf(i));
							NotificationInfo info = tableList.get(i);
							tableList.remove(info);
							// 删除数据库
							msgEntity = JsonUtil.toObject(info.getMsgInfo(), MsgEntity.class);
							service.remove(msgEntity);
						}
					}
					btn_del.setVisibility(View.GONE);
					if (tableList == null || tableList.size() == 0) {
						text_nodata.setVisibility(View.VISIBLE);
					} else {
						for (int i = 0; i <= mylistview.getCount(); i++) {
							MyNotifiAdapter.getIsSelected().put(i, false);
						}
					}
					isedit = true;
					MyNotifierActivity.this.mylistview.setAdapter(adapter);
				}

			}
		});
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_my_notification;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
		MyNotifiAdapter.Hold holder = (Hold) view.getTag();
		if (!isedit) {
			// 改变CheckBox的状态
			holder.chechbox.toggle();
			// 将CheckBox的选中状况记录下来
			MyNotifiAdapter.getIsSelected().put(position, holder.chechbox.isChecked());
		} else {
			try {
				NotificationInfo info = (NotificationInfo) parent.getAdapter().getItem(position);
				MsgEntity entity = JsonUtil.toObject(info.getMsgInfo(), MsgEntity.class);
				Intent intent = pushBo.initIntent(mActivity, entity);
				int msgId = Integer.valueOf(info.getMsgId());
				intent.putExtra(NotificationConstants.NOTIFICATION_ID, msgId);
				if (fromPushFlag) {
					intent.putExtra(NotificationConstants.NOTIFICATION_FLAG, true);
				}
				startActivity(intent);
				pushBo.clearMsg(info.getMsgId());
				// 消息回执 --liaodl
				pushBo.pushFeedBack(entity, Config.REBACK_PERSON_CENTER_MSG_TYPE);
				adapter.notifyDataSetChanged();
			} catch (NumberFormatException e) {
				Log.w("--消息id转成整型异常，请检查平台给的消息id字符串是否合法--" + e.getMessage());
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
				CommonUtils.showToast(mActivity, R.string.notice_run_error, Toast.LENGTH_SHORT);
			}

		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.top_right) {
			if (tableList != null && tableList.size() != 0) {
				if (isedit) {
					isedit = false;
					btn_del.setVisibility(View.VISIBLE);
					mylistview.setAdapter(adapter);
				} else {
					isedit = true;
					btn_del.setVisibility(View.GONE);
					for (int i = 0; i <= mylistview.getCount(); i++) {
						MyNotifiAdapter.getIsSelected().put(i, false);
					}
					mylistview.setAdapter(adapter);
				}
			} else {
				CommonUtils.showToast(mActivity, R.string.preson_center_not_edit,
						Toast.LENGTH_SHORT);
			}
		}
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}

}
