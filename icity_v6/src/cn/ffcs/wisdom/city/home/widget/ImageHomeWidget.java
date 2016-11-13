package cn.ffcs.wisdom.city.home.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ffcs.config.ExternalKey;
//import cn.ffcs.external.news.city.NewsPushActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.LogReportMgr;
import cn.ffcs.wisdom.city.home.widget.bo.CommonWidgetBo;
import cn.ffcs.wisdom.city.home.widget.entity.CommonWidgetEntity;
import cn.ffcs.wisdom.city.home.widget.entity.CommonWidgetEntity.CommonWidgetData;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.notify.NotificationConstants;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * 
 * <p>Title: 图片类控件       </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: yangchx                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-4-9            </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ImageHomeWidget extends BaseHomeWidget {
	private ImageView mPicLayout;
	private LinearLayout mProgressBar;// 进度条
	private TextView mErrorText;// 错误信息提示
	private TextView mPicTitle;// 图片描述
	private String widgetType = WidgetType.IMAGE_WIDGET.getValue();// 图片类型
	private String mWapUrl;// 跳转地址
	private CommonWidgetBo widgetbo;
	private String mMenuId;// 菜单编号
	private TextView mProgressTip;
	private CommonWidgetEntity widgetEntity;
	private CityImageLoader loader;
	private String mTitle;// 标题
	private CommonWidgetData widgetData;

	public ImageHomeWidget(Context context) {
		super(context);
		initComponents();
	}

	public ImageHomeWidget(Context context, AttributeSet attr) {
		super(context, attr);
		// 初始化控件
		initComponents();
	}

	public void initComponents() {
		loader = new CityImageLoader(mContext);
		mPicLayout = (ImageView) super.findViewById(R.id.widget_pic_layout);
		mPicLayout.setOnClickListener(new OnRedirect());
		mProgressBar = (LinearLayout) super.findViewById(R.id.loading_bar);
		mProgressTip = (TextView) super.findViewById(R.id.loading_bar_tip);
		mProgressTip.setTextAppearance(mContext, R.style.white_title_14sp);
		mProgressTip.setText(mContext.getString(R.string.common_loading));
		mErrorText = (TextView) super.findViewById(R.id.widget_pic_reload);
		mPicTitle = (TextView) super.findViewById(R.id.widget_pic_title);
		mErrorText.setOnClickListener(new OnReload());
		mProgressBar.setVisibility(View.VISIBLE);
	}

	class OnReload implements OnClickListener {

		@Override
		public void onClick(View v) {
			mProgressBar.setVisibility(View.VISIBLE);
			mErrorText.setVisibility(View.GONE);
			refresh();
		}

	}

	class OnRedirect implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!StringUtil.isEmpty(mWapUrl)) {
				LogReportMgr.getInstance().addWidgetLog(mContext, widgetData);
				new CommonWidgetBo(null, mContext).reportWidget(mMenuId, widgetType);
				if (WidgetType.NEWS_WIDGET.equals(widgetType)) {
//					Intent intent = new Intent(mContext, NewsPushActivity.class);
//					intent.putExtra(Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
//					intent.putExtra(ExternalKey.K_NEWS_TITLE, mTitle);
//					intent.putExtra(ExternalKey.K_IS_WIDGET, true);
//					intent.putExtra(NotificationConstants.NOTIFICATION_URL, mWapUrl);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					mContext.startActivity(intent);
				} else if (WidgetType.IMAGE_WIDGET.equals(widgetType)) {
					Intent intent = new Intent(mContext, BrowserActivity.class);
					intent.putExtra(Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
					intent.putExtra(Key.U_BROWSER_URL, mWapUrl);
					intent.putExtra(Key.U_BROWSER_TITLE, mTitle);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				}
			}
		}
	}

	@Override
	public void refresh() {
		widgetbo = new CommonWidgetBo(new Imagecall(), mContext);
		if (mMenuId != null) {
			widgetbo.queryWidget(mMenuId, widgetType);
		}
	}

	// 设置参数
	public void setParams(String mMenuId, String widgetType) {
		this.mMenuId = mMenuId;
		this.widgetType = widgetType;
	}

	@Override
	public int setContentView() {
		return R.layout.widget_home_image;
	}

	class Imagecall implements HttpCallBack<BaseResp> {
		@Override
		public void call(BaseResp response) {
			try {
				if (response.isSuccess()) {
					mProgressBar.setVisibility(View.GONE);
					widgetEntity = (CommonWidgetEntity) response.getObj();
					if (widgetEntity != null) {
						List<CommonWidgetData> widgetDataList = widgetEntity.getData();
						if (widgetDataList != null && widgetDataList.size() > 0) {
							widgetData = widgetDataList.get(0);
							if (widgetData != null) {
								String imgUrl = widgetData.getImageUrl();
								String imageurl = Config.GET_IMAGE_ROOT_URL() + imgUrl;
								mWapUrl = widgetData.getWapUrl();
								loader.setDefaultFailImage(0);
								loader.loadUrl(mPicLayout, imageurl,
										AppHelper.getScreenWidth(mContext),
										CommonUtils.convertDipToPx(mContext, 138));
								mTitle = widgetData.getDesc();
								mPicTitle.setText(mTitle);
							}
						}
					}
				} else {
					mProgressBar.setVisibility(View.GONE);
					mErrorText.setVisibility(View.VISIBLE);
					Log.d("获取图片数据失败");
				}
			} catch (Exception e) {
				mProgressBar.setVisibility(View.GONE);
				mErrorText.setVisibility(View.VISIBLE);
				Log.d("获取图片数据失败");
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
