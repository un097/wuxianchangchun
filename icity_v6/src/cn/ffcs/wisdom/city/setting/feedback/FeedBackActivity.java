package cn.ffcs.wisdom.city.setting.feedback;

import java.io.File;
import java.io.FileNotFoundException;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * 
 * <p>Title: 意见反馈        </p>
 * <p>Description: 
 * 发送反馈信息
 * </p>
 * <p>@author: xzw                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class FeedBackActivity extends WisdomCityActivity implements OnClickListener {

	private TextView topTitle;// 头部标题
	private ImageView topRight;// 右侧图标
	private EditText mContentEdit; // 内容
	private Button feedBackSubmit;// 发送按钮
	private String content;// 反馈内容
	private String filePath;// 反馈图片路径
	private ImageView pathImg;
	private String feedBackSourceTitle;

	private FeedBackBo bo;

	@Override
	protected void initComponents() {
		topTitle = (TextView) findViewById(R.id.top_title);

		topRight = (ImageView) findViewById(R.id.top_right);
		topRight.setOnClickListener(this);

		mContentEdit = (EditText) findViewById(R.id.feedback_content);

		feedBackSubmit = (Button) findViewById(R.id.feedback_submit);
		feedBackSubmit.setOnClickListener(this);

		pathImg = (ImageView) findViewById(R.id.feedback_img);
	}

	@Override
	protected void initData() {
		filePath = getIntent().getStringExtra("path");
		feedBackSourceTitle = getIntent().getStringExtra("title");
		if (filePath != null) {
			File file = new File(filePath);
			if (file.exists()) {
				try {
					pathImg.setVisibility(View.VISIBLE);
					pathImg.setImageBitmap(BitmapUtil.readBitMapByLowMemory(filePath));
				} catch (FileNotFoundException e) {
					Log.e(e.getMessage(), e);
				}
			}
		}
		bo = new FeedBackBo(mContext, new SendAdviceCallBack());
		topTitle.setText(getResources().getString(R.string.setting_feedback));
		TopUtil.updateRight(topRight, R.drawable.setting_feedback_history);
		String isLogin = SharedPreferencesUtil.getValue(mContext, Key.K_IS_LOGIN);
		if (!isLogin.equals("true")) {
			topRight.setVisibility(View.GONE);
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_feedback;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.feedback_submit) {
			content = mContentEdit.getText().toString().trim();
			if (StringUtil.isEmpty(content)) {
				CommonUtils.showToast(mActivity, "反馈信息不能为空", Toast.LENGTH_SHORT);
				return;
			}
			if (!CommonUtils.isNetConnectionAvailable(mContext)) {
				CommonUtils.showToast(mActivity, "网络异常，请检查网络连接！", Toast.LENGTH_SHORT);
				return;
			}

			LoadingDialog.getDialog(mActivity).setMessage("反馈提交中，请稍后...").show();
			if (!StringUtil.isEmpty(filePath)) {
				bo.uploadImage(new PicUpCallBack(), filePath,
						MenuMgr.getInstance().getCityCode(mContext));
			} else {
				bo.sendFeedBack(content, null, null);
			}
		} else if (id == R.id.top_right) {
			Intent intent = new Intent(mContext, FeedBackReplyActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getResources().getString(R.string.setting_feedback));
			startActivity(intent);
		}
	}

	class PicUpCallBack implements HttpCallBack<UpLoadImageResp> {

		@Override
		public void call(UpLoadImageResp response) {
			if (response.isSuccess()) {
				String ImageUrl = response.getList().get(0).getFilePath();
				bo.sendFeedBack(content, ImageUrl, feedBackSourceTitle);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	// 发送反馈信息之后的回调函数
	class SendAdviceCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
			LoadingDialog.getDialog(mActivity).cancel();
			CommonUtils.hideKeyboard(mActivity);
			if (resp.isSuccess()) {// 发送成功
				MobclickAgent.onEvent(mContext, "E_C_feedback_feedbackClick");
				CommonUtils.showToast(mActivity, "提交成功", Toast.LENGTH_SHORT);
				boolean is_login = AccountMgr.getInstance().isLogin(mActivity);
				if (is_login) {
					Intent intent = new Intent(mContext, FeedBackReplyActivity.class);// 跳转到反馈历史信息页面
					intent.putExtra(Key.K_RETURN_TITLE, "意见反馈");
					startActivity(intent);
				}
			} else {
				CommonUtils.showToast(mActivity, "提交失败", Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}
	}
}
