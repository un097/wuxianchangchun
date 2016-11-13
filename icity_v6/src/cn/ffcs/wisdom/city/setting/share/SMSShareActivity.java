package cn.ffcs.wisdom.city.setting.share;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.entity.ConfigParams;
import cn.ffcs.wisdom.city.setting.entity.ShareNoticeEntity;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

public class SMSShareActivity extends WisdomCityActivity implements OnClickListener {

	private View mSend; // 发送
	private View mAddContacts; // 添加联系人
	private EditText mContacts; // 联系人
	private EditText mContent; // 内容

	private SmsShareBo bo;
	private int contracts = 0;
	private List<Map<String, Object>> list;// 传递list
	private LoadingBar loadingBar = null;// 进度条
	private TextView shareNotice;
	private String contacts;// 联系人
	private String content;// 分享内容
	private static int AUTO_REQUEST = 2;// 请求超时，自动请求两次

	@Override
	protected void initComponents() {
		mSend = findViewById(R.id.share_submit);
		mSend.setOnClickListener(this);
		mAddContacts = findViewById(R.id.add_contacts);
		mAddContacts.setOnClickListener(this);
		mContacts = (EditText) findViewById(R.id.contacts_edit);
		mContent = (EditText) findViewById(R.id.content);
		loadingBar = (LoadingBar) findViewById(R.id.loading_bar);
		loadingBar.setMessage(getString(R.string.share_sent_progress));
		loadingBar.setVisibility(View.GONE);// 显示加载控件
		shareNotice = (TextView) findViewById(R.id.share_notice);
	}

	@Override
	protected void initData() {
		String shareTitle = getIntent().getStringExtra(Key.K_SHATE_TITLE);// 分享标题
		String content = getIntent().getStringExtra(Key.K_SHARE_CONTENT);
		if (!StringUtil.isEmpty(content)) {
			mContent.setText(content);
		} else {
			ConfigParams params = ConfigUtil.readConfigParams(this);
			if (params != null) {
				mContent.setText(params.getSoftsharesms());
			}
		}
		if (!StringUtil.isEmpty(shareTitle)) {
			TopUtil.updateTitle(this, R.id.top_title, shareTitle);
		} else {
			TopUtil.updateTitle(this, R.id.top_title, getString(R.string.share_message));
		}
		bo = new SmsShareBo(mContext, new SMSCall());// 初始化BO
		bo.getShareNotice(new ShareNoticeCallBack());
	}

	/**
	 * 发送分享
	 */
	private void sendShare() {
		if (bo == null) {
			bo = new SmsShareBo(mContext, new SMSCall());
		}
		mSend.setEnabled(false);// 设置按钮不可点击，防止重复提交
		loadingBar.setVisibility(View.VISIBLE);// 显示加载控件
		bo.send(contacts, content);
	}

	class ShareNoticeCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			try {
				if (response.isSuccess()) {
					ShareNoticeEntity entity = (ShareNoticeEntity) response.getObj();
					String notice = entity.getData().getReminderContent();
					shareNotice.setText(Html.fromHtml(notice));
					shareNotice.setVisibility(View.VISIBLE);
				}
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	class SMSCall implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
			mSend.setEnabled(true);
			loadingBar.setVisibility(View.GONE);// 显示加载控件
			if (resp.isSuccess()) {
				AUTO_REQUEST = 0;
				SMSResp smsResp = (SMSResp) resp;
				String nativeNet = smsResp.getNative_net();
				if (!"true".equals(nativeNet)) {
					sendSMS(smsResp); // 发送短信息
				} else {
					CommonUtils.showToast(mActivity, resp.getDesc(), Toast.LENGTH_LONG);
				}
				finish();
			} else {
				if (BaseResp.NETWORK_ERROR.equals(resp.getStatus())) {
					autoRequestTwice();
				} else if ("1".equals(resp.getStatus())) {
					CommonUtils.showToast(mActivity, resp.getDesc(), Toast.LENGTH_LONG);
				} else {
					AUTO_REQUEST = 0;
					CommonUtils.showToast(mActivity, "服务器异常,请稍后再试", Toast.LENGTH_LONG);
				}
			}

		}

		/**
		 * 请求失败返回结果
		 * @param frequency
		 * @param desc
		 */
		private void autoRequestTwice() {
			if (AUTO_REQUEST > 0) {
				AUTO_REQUEST--;
				sendShare();
			} else {
				CommonUtils.showToast(mActivity, "网络超时，稍后重试", Toast.LENGTH_SHORT);
			}
		}

		/**
		 * 发送短信息
		 * @param smsResp
		 */
		private void sendSMS(SMSResp smsResp) {
			MobclickAgent.onEvent(mContext, "E_C_SMSShare_SMSShareClick");
			List<String> mobileIdList = smsResp.getList();
			String content = mContent.getText().toString();

			for (Iterator<String> it = mobileIdList.iterator(); it.hasNext();) {
				String mobileId = it.next();
				String mobileIds[] = mobileId.split(":");
				bo.sendSMS(mContext, content, mobileIds[0], mobileIds[1]); // 发送短信
			}
			CommonUtils.showToast(mActivity, getString(R.string.share_sms_send_success),
					Toast.LENGTH_SHORT);
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}

	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_message_share_page;
	}

	/**
	 * 展示联系人列表
	 */
	private void showContacts() {
		Intent i = new Intent(SMSShareActivity.this, SelectContactActivity.class);
		i.putExtra(Key.K_RETURN_TITLE, getString(R.string.share_message));
		i.putExtra("contractsNum", contracts);
		startActivityForResult(i, 1);
	}

	/**
	 * 转换号码数据
	 * @param contacts
	 * @return
	 */
	private String convert(String contacts) {
		if (StringUtil.isEmpty(contacts)) {
			return "";
		}
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);

		String[] contactsTemp = contacts.split(";");

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < contactsTemp.length; i++) {
			if (!StringUtil.isEmpty(contactsTemp[i]) && contactsTemp[i].length() >= 11) {
				int length = contactsTemp[i].length();
				String contactStr = contactsTemp[i].substring(length - 11, length).trim();
				if (CommonUtils.isMobileNoValid(contactStr)) {
					sb.append(contactStr).append(",");
				} else {
					CommonUtils.showErrorByEditText(mContacts,
							R.string.share_sms_contact_invalid_mobile, shake);
					return null;
				}
			}
		}

		if (sb.length() == 0)
			return "";
		return sb.substring(0, sb.lastIndexOf(","));
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.add_contacts) {
			showContacts();
		} else if (id == R.id.share_submit) {
			// 发送分享短信
			contacts = mContacts.getText().toString();
			content = mContent.getText().toString();

			// 号码是否为空或者非法号码
			Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
			if (StringUtil.isEmpty(contacts)) {
				CommonUtils.showErrorByEditText(mContacts, R.string.share_sms_contact_tips, shake);
				return;
			} else if (contacts.length() < 11) {
				CommonUtils.showErrorByEditText(mContacts,
						R.string.share_sms_contact_invalid_mobile, shake);
				return;
			}
			String mobiles = convert(contacts);
			if(StringUtil.isEmpty(mobiles)){
				return;
			}
			// 隐藏键盘
			CommonUtils.hideKeyboard(mActivity);
			mSend.setEnabled(false);// 设置按钮不可点击，防止重复提交
			loadingBar.setVisibility(View.VISIBLE);// 显示加载控件
			bo.send(mobiles, content); // 发送分享信息到后台，并由后台进行短信发送
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
			contracts = 0;
			StringBuilder sb = new StringBuilder();// 存储用户号码
			list = (List<Map<String, Object>>) data.getExtras().getSerializable("phoneList");
			if (list == null) {
				Log.e("list为空");
				return;
			}
			for (Iterator<Map<String, Object>> it = list.iterator(); it.hasNext();) {
				Map<String, Object> map = it.next();
				if ((Boolean) map.get("checked")) {
					String phone = map.get("name").toString() + "-" + map.get("phone").toString();
					sb.append(phone);
					sb.append(";");
					contracts++;
				}
			}
			mContacts.setText(sb.toString());
			mContacts.setSelection(sb.length());
			if (contracts != 0) {
				CommonUtils.hideErrorByEditText(mContacts);
			}
		} else {
			Log.e("获取失败");
		}
	}

	@Override
	public void finish() {
		ContactAsyncQueryHandler.getInstance(getContentResolver()).initContacts();
		super.finish();
	}
}
