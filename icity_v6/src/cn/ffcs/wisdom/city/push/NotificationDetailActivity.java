package cn.ffcs.wisdom.city.push;

import java.text.ParseException;
import java.util.Date;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.sqlite.service.NotificationInfoService;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.notify.MsgEntity.Content.Param;
import cn.ffcs.wisdom.notify.NotificationConstants;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.tools.TimeUitls;

/**
 * <p>Title: 通知详细页面                          </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-15           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class NotificationDetailActivity extends WisdomCityActivity {

	private TextView mNoticeTitle;
	private TextView mNoticeDate;
	// private TextView mNoticeAuthor;
	private TextView mNoticeContent;
	private ImageView mNoticeImg;

	private MsgEntity mNotice;
	private Param param;
	private CityImageLoader loader;
	private PushMsgBo pushBo;

	@Override
	protected void initComponents() {
		mNoticeTitle = (TextView) findViewById(R.id.notice_title);
		mNoticeDate = (TextView) findViewById(R.id.notice_date);
		// mNoticeAuthor = (TextView) findViewById(R.id.notice_author);
		mNoticeContent = (TextView) findViewById(R.id.notice_content);
		mNoticeImg = (ImageView) findViewById(R.id.notice_img);
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.notification_detail);
		loader = new CityImageLoader(mContext);
		pushBo = new PushMsgBo(mActivity);
		mNotice = (MsgEntity) getIntent().getSerializableExtra(
				NotificationConstants.NOTIFICATION_MESSAGE);
		if (mNotice != null && mNotice.getContent() != null) {
			param = mNotice.getContent().getParam();
		}
		// 显示标题
		showHead();
		// 显示消息图片
		showImage();
		// 显示内容
		showContent();
		// 标记为已读
		clearMsg();
	}

	/**
	 * 显示标题和副标题
	 */
	private void showHead() {
		if (param != null) {
			// String headStr = mNotice.getTitle();
			// String dateStr = mNotice.getDate();
			// String authorStr = mNotice.getAuthor();
			String headStr = mNotice.getTitle();

			String dateStr = param.getCreateTime();
			String pattern = "yyyy-MM-dd HH:mm:ss";
			Date currentDate;
			try {
				currentDate = TimeUitls.getDate(dateStr, pattern);
				dateStr = TimeUitls.formatDate(currentDate, pattern);
			} catch (ParseException e) {
				Log.e("日期格式错误" + e);
			}

			// String authorStr = param.getAuthor();

			mNoticeTitle.setText(headStr);
			mNoticeDate.setText(dateStr);
			// mNoticeAuthor.setText(authorStr);
		}
	}

	/**
	 * 显示消息图片
	 */
	private void showImage() {
		if (param != null) {
			if (!StringUtil.isEmpty(param.getPicUrl())) {
				String imageUrl = param.getPicUrl();
				if (!StringUtil.isEmpty(imageUrl)) {
					loader.loadUrl(mNoticeImg, imageUrl);
				}
				mNoticeImg.setVisibility(View.VISIBLE); // 显示图片
			} else {
				mNoticeImg.setVisibility(View.GONE); // 隐藏图片
			}
		}
	}

	/**
	 * 显示主体内容
	 */
	private void showContent() {
		if (param != null) {
			if (!StringUtil.isEmpty(mNotice.getContent().getMsgContent())) {
				String content = StringUtil.toSBC(mNotice.getContent().getMsgContent());
				mNoticeContent.setText(content);
			}
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_notification_detail;
	}

	@Override
	protected void onNewIntent(Intent intent) {		
		if (loader == null) {
			loader = new CityImageLoader(mContext);
		}
		if (pushBo == null) {
			pushBo = new PushMsgBo(mActivity);
		}
		mNotice = (MsgEntity) intent.getSerializableExtra(NotificationConstants.NOTIFICATION_MESSAGE);
		if (mNotice != null && mNotice.getContent() != null) {
			param = mNotice.getContent().getParam();
		}
		// 显示标题
		showHead();
		// 显示消息图片
		showImage();
		// 显示内容
		showContent();

		int msgId = intent.getIntExtra(NotificationConstants.NOTIFICATION_ID, -1);
		pushBo.clearMsg(msgId + "");
		pushBo.pushFeedback(Config.REBACK_USER_CLICK_MSG_TYPE, false);
		super.onNewIntent(intent);
	}

	//清除通知栏消息并本地标记为已读--防止直接从通知栏点击进到详情页
	protected void clearMsg() {
		if (mNotice == null || mNotice.getContent() == null) {
			return;
		}
		try {
			String msgId = mNotice.getContent().getMsgId();
			Log.i("--NotificationDetailActivity 清除推送消息的id是--：" + msgId);
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(Integer.valueOf(msgId));
			NotificationInfoService.getInstance(mContext).updateNewById(msgId);
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}
}
