package cn.ffcs.surfingscene.road;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;
import cn.ffcs.external.share.view.CustomSocialShare;
import cn.ffcs.external.share.view.WeiBoSocialShare;
import cn.ffcs.external.share.view.YiXinSocialShare;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.advert.AdvertMgr;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.bo.RoadBo;
import cn.ffcs.surfingscene.road.upload.RoadUpLoadImageEntity;
import cn.ffcs.surfingscene.road.upload.RoadUpLoadImageEntity.UpLoadImage;
import cn.ffcs.surfingscene.road.widget.GlPlayerSurfaceView;
import cn.ffcs.surfingscene.sqlite.RoadCollectService;
import cn.ffcs.surfingscene.tools.GloImageLoader;
import cn.ffcs.surfingscene.tools.GloImageLoader2;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.MD5;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.tools.SystemCallUtil;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.function.SurfingScenePlayer;
import com.ffcs.surfingscene.function.onPlayListener;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;
import com.kj.guradc.VideoActivity;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * @author: yangchx
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: ffcs Co., Ltd.
 * </p>
 * <p>
 * Create Time: 2013-7-1
 * </p>
 * <p>
 * @author:
 * </p>
 * <p>
 * Update Time: 2013-10-16
 * </p>
 * <p>
 * Updater: zhangwsh
 * </p>
 * <p>
 * Update Comments: 添加新版播放器
 * </p>
 */
public class RoadPlayActivity extends VideoActivity {
	private Activity mActivity;
	private Context mContext;
	private SurfaceView surfaceview;// 旧版播放器
	private SurfingScenePlayer surfingplayer;// 旧版播放控制器
	private int geyeId = 0;// 全球眼类型
	private String rtsp;// rtsp播放地址
	private TextView loadingText;// 加载进度
	private ImageView notScreen;// 退出全屏
	private String eyeName;// 视频上部文字
	private TextView eyeNameView;// 视频上部文字控件
	private TextView videoDesView;// 描述文字控件
	private String videoDes;// 描述文字
	private long startTime;// 开始播放时间
	private String title;// 标题
	private ScrollView desLayout;// 描述布局
	private LinearLayout topLayout;// 顶部布局
	private String gloType = "1025";// 默认全球眼类型
	private String actionId = "712";// 默认活动action
	private View topRight;// 右上角按钮
	private boolean isHaveCollect;// 判断是否有收藏
	private RelativeLayout videoLayout;// 播放位置外层布局
	private RoadBo roadBo;// 路况bo
	private GlobalEyeEntity entity;// 摄像头实体，用于收藏
	private int screenWidth;// 屏幕宽
	private int screenHeight;// 屏幕高
	private String adImageUrl;// 播放器下方图片url
	private String adImageClick;// 播放器下方图片点击地址
	private ImageView adImage;// 播放器下方图片控件
	private GloImageLoader loader;
	private TextView shareBtn;// 分享按钮
	private TextView cutScreen;// 截屏

	private ImageView loadingImage;// 播放器中间加载图
	private ImageView mAdvertImage; // 广告图
	private TextView advertTime;// 广告倒计时
	private View advertLayout;// 广告布局
	private String advertUrl;
	private String duration;
	private String tyjxCode; // 天翼景象code
	private CountDown mc;

	private int stearmType = 2;// 高清还是标清，3：高清，2：标清 , 默认标清
	private String videoUIType;// 播放器类型
	private boolean isNew;// 新旧版标示
	private cn.ffcs.wisdom.http.HttpCallBack<BaseResp> upLoadImageCallBack = new UpLoadImageCallBack();// 图片上传回调
	private ReportPlayTimeCallBack reportPlayTimeCallBack = new ReportPlayTimeCallBack();// 上报播放时间回调
	private ToFullScreenListener toFullScreenListener = new ToFullScreenListener();// 变为全屏
	private NoFullScreenListener noFullScreenListener = new NoFullScreenListener();// 取消全屏
	private OnFinishListener onFinishListener = new OnFinishListener();// 请求结束，开始播放监听
	private ShareClick onShareClickListener = new ShareClick();// 分享点击
	private ShotClick onShotClickListener = new ShotClick();// 分享点击
	private String shotScreenDir = SdCardTool.getSdcardDir() + "/icityShot/";// 截屏SD卡目录
	private String shotFileName = shotScreenDir + "shot.jpg";// 截屏分享临时文件
	private boolean videoLoadComplete = false;// 视频加载完成标志
	private String playerType = "";// 播放器类型，1代表路况播放器，2代表景点播放器

	@Override
	protected void initComponents() {
		mActivity = this;
		mContext = getApplicationContext();
		loadingImage = (ImageView) findViewById(R.id.loading_image);
		mAdvertImage = (ImageView) findViewById(R.id.advert_image);
		advertTime = (TextView) findViewById(R.id.advert_time);
		advertLayout = findViewById(R.id.advert_layout);
		shareBtn = (TextView) findViewById(R.id.share_btn);
		shareBtn.setOnClickListener(onShareClickListener);
		cutScreen = (TextView) findViewById(R.id.cut_screen);
		cutScreen.setOnClickListener(onShotClickListener);
		adImage = (ImageView) findViewById(R.id.ad_image);
		videoLayout = (RelativeLayout) findViewById(R.id.video_layout);
		desLayout = (ScrollView) findViewById(R.id.layout_des);
		topRight = findViewById(R.id.top_right);
		topLayout = (LinearLayout) findViewById(R.id.top);
		loadingText = (TextView) findViewById(R.id.play_press);
		notScreen = (ImageView) findViewById(R.id.to_not_sreen);
		eyeNameView = (TextView) findViewById(R.id.eye_name_to_full_sreen);
		videoDesView = (TextView) findViewById(R.id.ad_text);
		setReturnButton();// 设置返回按钮
		initVideoPlayer();// 根据类型切换播放器
	}
	public static final String FINISH_ACTION = "PLAY_FINISH_ACTION";
	/**
	 * 设置返回按钮
	 */
	private void setReturnButton() {
		TextView tv = (TextView) findViewById(R.id.btn_return);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// onBackPressed();
				Intent intent = new Intent("road_back_action");
//				intent.setClass(mActivity, RoadMapActivity.class);
				intent.putExtra("resultCode", 2);
				intent.setFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
				sendBroadcast(intent);
//				setResult(1,intent);
//				RoadPlayActivity.this.startActivityForResult(intent, 1);
				
				Intent intent123 = new Intent();  
				intent123.setAction(FINISH_ACTION);  
				mActivity.sendBroadcast(intent123);
				RoadPlayActivity.this.finish();
			}
		});
//		String returnTitle = getIntent().getStringExtra(Key.K_RETURN_TITLE);
//		if (!StringUtil.isEmpty(returnTitle)) {
//			tv.setText(returnTitle);
//		}
		playerType = getIntent().getStringExtra(Key.K_PLAYER_TYPE);
		if (!"2".equals(playerType)) {
			playerType = "1";
		}
	}

	/**
	 * 选择播放器
	 */
	private void initVideoPlayer() {
		videoUIType = getIntent().getStringExtra(Key.K_VIDEO_TYPE);
		if ("1".equals(videoUIType)) {
			isNew = true;
			surfaceview = (SurfaceView) findViewById(R.id.new_surface_view);
			surfaceview.setVisibility(View.VISIBLE);
			setPlayListener(onFinishListener);
		} else {
			isNew = false;
			surfaceview = (SurfaceView) findViewById(R.id.surface_view);
			surfaceview.setVisibility(View.VISIBLE);
			surfingplayer = new SurfingScenePlayer(mActivity);
			surfingplayer.init((GlPlayerSurfaceView) surfaceview);
			surfingplayer.setprogress(loadingText);
			surfingplayer.setPlayListener(onFinishListener);
			surfingplayer.setSreen_Nochange();
			cutScreen.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initData() {
		tyjxCode = AdvertMgr.getInstance().getTyjxCode(mContext);
		advertUrl = AdvertMgr.getInstance().getAdvertImg(mContext, tyjxCode);
		duration = AdvertMgr.getInstance()
				.getAdvertDuration(mContext, tyjxCode);

		screenWidth = AppHelper.getScreenWidth(mContext);
		screenHeight = AppHelper.getScreenHeight(mContext);
		loader = new GloImageLoader(mContext);

		GloImageLoader2 loader2 = new GloImageLoader2(mContext);
		loader2.loadUrl(mAdvertImage, advertUrl);

		roadBo = RoadBo.getInstance();
		getIntentData();// 获取传输数据
		if (entity != null) {
			initCollect();// 设置收藏
		}
		changeWrapLayout();// 设置宽高
		notScreen.setOnClickListener(noFullScreenListener);
		eyeNameView.setOnClickListener(toFullScreenListener);

		if (!StringUtil.isEmpty(duration)) {
			int total = Integer.parseInt(duration) * 1000;
			mc = new CountDown(total, 1000);
			mc.start();
		} else {
			advertLayout.setVisibility(View.GONE);
		}
		YiXinSocialShare.yixinRegister(mActivity);
	}

	// 转成全屏
	class ToFullScreenListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			changeToLandscape();
			desLayout.setVisibility(View.GONE);
			eyeNameView.setVisibility(View.GONE);
			topLayout.setVisibility(View.GONE);
			notScreen.setVisibility(View.VISIBLE);
			changeFullLayout();
		}
	}

	// 取消全屏
	class NoFullScreenListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			changeToPortrait();
			desLayout.setVisibility(View.VISIBLE);
			eyeNameView.setVisibility(View.VISIBLE);
			topLayout.setVisibility(View.VISIBLE);
			if (videoDes != null) {
				videoDesView.setVisibility(View.VISIBLE);
			} else {
				videoDesView.setVisibility(View.GONE);
			}
			notScreen.setVisibility(View.GONE);
			changeWrapLayout();
		}
	}

	// 请求结束，开始播放监听
	class OnFinishListener implements onPlayListener {

		@Override
		public void onPlayFail(int arg0) {
			CommonUtils.showToast(mActivity, R.string.glo_rtsp_play_error,
					Toast.LENGTH_SHORT);
		}

		@Override
		public void setOnPlaysuccess() {
			videoLoadComplete = true;
			loadingImage.setVisibility(View.GONE);
			loadingText.setVisibility(View.GONE);
		}
	}

	/**
	 * 截屏
	 */
	class ShotClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!videoLoadComplete) {
				CommonUtils.showToast(mActivity,
						R.string.glo_wait_load_complete, Toast.LENGTH_SHORT);
				return;
			}
			String localFileName = shotScreenDir
					+ MD5.getMD5Str(String.valueOf(System.currentTimeMillis()))
					+ ".jpg";
			if (cutScreen(localFileName)) {
				// 刷新相册
				SystemCallUtil.refreshPhoto(mContext, localFileName);
				CommonUtils.showToast(mActivity, "文件已保存于：" + localFileName,
						Toast.LENGTH_SHORT);
			}
		}
	}

	/**
	 * 分享
	 */
	class ShareClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!videoLoadComplete) {
				CommonUtils.showToast(mActivity,
						R.string.glo_wait_load_complete, Toast.LENGTH_SHORT);
				return;
			}

			try {
				if (isNew) {
					if (cutScreen(shotFileName)) {
						upLoadFile();
					} else {
						CustomSocialShare.shareTextPlatform(mActivity, "",
								getString(R.string.glo_share_content, eyeName),
								getString(R.string.glo_share_url));
					}
				} else {
					CustomSocialShare.shareTextPlatform(mActivity, "",
							getString(R.string.glo_share_content, eyeName),
							getString(R.string.glo_share_url));
				}
				roadBo.shareReport(mContext, String.valueOf(geyeId), gloType,
						actionId);
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}
	}

	// /**
	// * 直接分享图片
	// * @throws IOException
	// */
	// private void onlyShareImage() throws IOException {
	// byte[] fileByte = getBytesFromFile(new File(shotFileName));
	// SocialShare.shareTextWithWeixinOnlyImage(mActivity, fileByte,
	// getString(R.string.glo_share_url));
	// }

	private byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		// 获取文件大小
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// 文件太大，无法读取
			is.close();
			throw new IOException("File is to large " + file.getName());
		}
		// 创建一个数据来保存文件数据
		byte[] bytes = new byte[(int) length];
		// 读取数据到byte数组中
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file "
					+ file.getName());
		}
		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	/**
	 * 截屏并生成目录
	 * 
	 * @param fileName
	 */
	private boolean cutScreen(String fileName) {
		if (!SdCardTool.isMounted()) {
			CommonUtils.showToast(mActivity, R.string.glo_no_sdcard,
					Toast.LENGTH_SHORT);
			return false;
		}
		File file = new File(shotScreenDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		RTSPClientSnap(fileName);
		return true;
	}

	/**
	 * 获取数据
	 */
	private void getIntentData() {
		isHaveCollect = getIntent().getBooleanExtra(Key.K_IS_COLLECT, true);
		geyeId = getIntent().getIntExtra(ExternalKey.K_EYE_ID, 0);
		title = getIntent().getStringExtra(ExternalKey.K_EYE_TITLE);
		rtsp = getIntent().getStringExtra(ExternalKey.K_RTSP_ADDRESS);
		Serializable serializable = getIntent().getSerializableExtra(
				Key.K_GLO_ENTITY);
		if (serializable != null) {
			entity = (GlobalEyeEntity) serializable;
		}
		eyeName = getIntent().getStringExtra(Key.K_EYE_NAME);
		eyeNameView.setText(eyeName);
		gloType = getIntent().getStringExtra(Key.K_GLO_TYPE);
		actionId = getIntent().getStringExtra(Key.K_ACTION_ID);
		videoDes = getIntent().getStringExtra(Key.K_EYE_INTRO);

		if (StringUtil.isEmpty(actionId) || actionId.equals("0")) {
			actionId = "712";
		}

		if (StringUtil.isEmpty(gloType)) {
			gloType = "1025";
		}

		int highFlag = getIntent().getIntExtra(ExternalKey.K_HIGH_FLAG, 0);
		stearmType = getStreamType(highFlag);

		if (title != null) {
			TopUtil.updateTitle(mActivity, R.id.top_title, title); // 设置标题
		} else {
			TopUtil.updateTitle(mActivity, R.id.top_title,
					R.string.glo_road_video); // 设置标题
		}

		if (!StringUtil.isEmpty(videoDes)) {
			adImage.setVisibility(View.GONE);
			videoDesView.setVisibility(View.VISIBLE);
			videoDesView.setText(Html.fromHtml(videoDes));
			videoDesView.setBackgroundColor(getResources().getColor(
					R.color.white));
		} else {
			videoDesView.setVisibility(View.GONE);
			adImageUrl = getIntent().getStringExtra(Key.K_AD_IMAGE);
			adImageClick = getIntent().getStringExtra(Key.K_AD_CLICK);
			if (!StringUtil.isEmpty(adImageUrl)) {
				adImage.setVisibility(View.VISIBLE);
				loader.loadUrl(adImage, adImageUrl, screenWidth,
						screenHeight / 2);
				if (!StringUtil.isEmpty(adImageClick)) {
					adImage.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							try {
								Intent i = new Intent();
								i.setClassName(mContext,
										"cn.ffcs.wisdom.city.web.BrowserActivity");
								i.putExtra(Key.K_RETURN_TITLE, eyeName);
								i.putExtra(Key.U_BROWSER_URL, adImageClick);
								startActivity(i);
							} catch (Exception e) {
								Log.e(e.getMessage(), e);
							}
						}
					});
				}
			}
		}
	}

	/**
	 * 获取高清标清
	 * 
	 * @param highFlag
	 * @return
	 */
	private int getStreamType(int highFlag) {
		int returnInt = 2;
		switch (highFlag) {
		case 1:
			returnInt = 3;
			break;
		case 0:
			returnInt = 2;
			break;
		default:
			break;
		}
		return returnInt;
	}

	/**
	 * 判断是否收藏
	 */
	private void initCollect() {
		if (isHaveCollect) {
			String isLogin = SharedPreferencesUtil.getValue(mContext,
					Key.K_IS_LOGIN);
			String phone = "";
			if ("true".equals(isLogin)) {
				phone = SharedPreferencesUtil.getValue(mContext,
						Key.K_PHONE_NUMBER);
			}
			boolean isCollect = RoadCollectService.getInstance(mContext)
					.isCollect(phone, geyeId);
			if (isCollect) {
				TopUtil.updateRight(topRight, R.drawable.glo_video_collect);
			} else {
				TopUtil.updateRight(topRight, R.drawable.glo_video_uncollect);
			}
			topRight.setOnClickListener(new OnCollectClick(isCollect, phone));
		}
	}

	/**
	 * 收藏点击
	 */
	class OnCollectClick implements OnClickListener {
		private boolean isCollect;
		private String phone;

		OnCollectClick(boolean isCollect, String phone) {
			this.isCollect = isCollect;
			this.phone = phone;
		}

		@Override
		public void onClick(final View v) {
			v.setEnabled(false);
			if (isCollect) {
				if (!StringUtil.isEmpty(phone)) {
					roadBo.collect(mContext, phone, gloType, geyeId, 1,
							new HttpCallBack<BaseResponse>() {

								@Override
								public void callBack(BaseResponse resp,
										String arg1) {
									RoadCollectService.getInstance(mContext)
											.unCollect(phone, geyeId);
									initCollect();
									v.setEnabled(true);
								}
							});
				} else {
					RoadCollectService.getInstance(mContext).unCollect(phone,
							geyeId);
					v.setEnabled(true);
				}
			} else {
				if (!StringUtil.isEmpty(phone)) {
					roadBo.collect(mContext, phone, gloType, geyeId, 0,
							new HttpCallBack<BaseResponse>() {

								@Override
								public void callBack(BaseResponse resp,
										String arg1) {
									RoadCollectService.getInstance(mContext)
											.saveCollect(phone, entity);
									initCollect();
									v.setEnabled(true);
								}
							});
				} else {
					RoadCollectService.getInstance(mContext).saveCollect(phone,
							entity);
					v.setEnabled(true);
				}
			}
			initCollect();
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_road_playvideo;
	}

	/**
	 * 变横屏
	 */
	private void changeFullLayout() {
		surfaceview.getLayoutParams().width = LinearLayout.LayoutParams.FILL_PARENT;
		surfaceview.getLayoutParams().height = LinearLayout.LayoutParams.FILL_PARENT;
		videoLayout.getLayoutParams().width = LinearLayout.LayoutParams.FILL_PARENT;
		videoLayout.getLayoutParams().height = LinearLayout.LayoutParams.FILL_PARENT;
	}

	/**
	 * 变竖屏
	 */
	private void changeWrapLayout() {
		videoLayout.getLayoutParams().height = screenWidth * 5 / 8;// 比例 4:2.5
		surfaceview.getLayoutParams().height = screenWidth * 5 / 8;
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopPlay();
	}

	@Override
	public void finish() {
		super.finish();
		if (!isNew) {
			Long endtime = System.currentTimeMillis();
			int playStatus = surfingplayer.playStatus;
			int playSubStatus;
			if (playStatus == 0) {
				playSubStatus = 3;
			} else {
				playSubStatus = 1;
			}
			long playLongTime = endtime - startTime;
			surfingplayer.playStatistics(geyeId, reportPlayTimeCallBack,
					"/geye/playCallback", playStatus, playSubStatus,
					playLongTime, tyjxCode, advertUrl, Long
							.parseLong(StringUtil.isEmpty(duration) ? "0"
									: duration));
			surfingplayer.playEnd();
		}
		if (mc != null) {
			mc.cancel();
			advertLayout.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		play();// 播放
		super.onResume();
	}

	/**
	 * 播放
	 */
	private void play() {
		if (!isNew) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						if (!StringUtil.isEmpty(rtsp)) {
							surfingplayer.playVideo(rtsp, 3, 1);
							startTime = System.currentTimeMillis();
						} else if (geyeId != 0) {
							surfingplayer.playerVideo(geyeId, stearmType, 2,
									gloType, actionId);
							startTime = System.currentTimeMillis();
						}
					} catch (Exception e) {
						Log.e(e.getMessage(), e);
					}
				}
			}).start();
		} else {
			loadingText.setVisibility(View.VISIBLE);
			if (!StringUtil.isEmpty(rtsp)) {
				mplayer(rtsp, 3, 1);
				startTime = System.currentTimeMillis();
			} else if (geyeId != 0) {
				getRtsp(geyeId, stearmType, 2,
						new HttpCallBack<BaseResponse>() {

							@Override
							public void callBack(BaseResponse resp, String arg1) {
								if ("1".equals(resp.getReturnCode())) {
									GlobalEyeEntity entity = resp.getGeye();
									mplayer(entity.getRtspAddr(),
											entity.getNetType(),
											entity.getLinkType());
									startTime = System.currentTimeMillis();
								} else {
									CommonUtils.showToast(mActivity,
											R.string.glo_get_rtsp_error,
											Toast.LENGTH_SHORT);
								}
							}
						}, gloType, actionId);
			}
		}
	}

	public void stopPlay() {
		try {
			if (isNew) {
				loadingImage.setVisibility(View.VISIBLE);
				Long endtime = System.currentTimeMillis();
				long playLongTime = endtime - startTime;
				int playSubStatus;
				if (playStatus == 0) {
					playSubStatus = 3;
				} else {
					playSubStatus = 1;
				}
				playStatistics(geyeId, reportPlayTimeCallBack,
						"/geye/playCallback", playStatus, playSubStatus,
						playLongTime, tyjxCode, advertUrl,
						Long.parseLong(StringUtil.isEmpty(duration) ? "0"
								: duration));
				RTSPClientStop();
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	// 上报播放时间回调
	class ReportPlayTimeCallBack implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse resp, String arg1) {

		}
	}

	/**
	 * 设置直屏
	 */
	private void changeToPortrait() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	/**
	 * 横屏
	 */
	private void changeToLandscape() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	public int setSurViewId() {
		return R.id.new_surface_view;
	}

	protected Class<?> getResouceClass() {
		return R.class;
	}

	/* 广告倒计时 */
	class CountDown implements Runnable {
		private final long mCountdownInterval;
		private long mStopTimeInFuture;
		boolean starting;
		Thread mThread;

		public CountDown(long millisInFuture, long countDownInterval) {
			this.mCountdownInterval = countDownInterval;
			this.mStopTimeInFuture = millisInFuture;
			starting = false;
		}

		Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				switch (what) {
				case 0:
					final long millisLeft = mStopTimeInFuture;
					String format = getString(R.string.glo_advert_time);
					format = String.format(format, millisLeft / 1000 < 1 ? "1"
							: millisLeft / 1000 + "");
					advertTime.setText(format);
					break;
				case 1:
					advertLayout.setVisibility(View.GONE);
					break;
				}

			}
		};

		public void start() {
			if (starting) {
				return;
			}

			mThread = new Thread(this);
			mThread.start();
			starting = true;
		}

		public void cancel() {
			if (starting) {
				mThread = null;
				mStopTimeInFuture = 0;
				starting = false;
			}
		}

		public void run() {
			while (mStopTimeInFuture > 0 && starting) {
				mStopTimeInFuture = mStopTimeInFuture - mCountdownInterval;
				mHandler.sendEmptyMessage(0);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			mHandler.sendEmptyMessage(1);
			mHandler.removeMessages(0);
			starting = false;
		};
	}

	/**
	 * 分享图片
	 * 
	 * @throws IOException
	 */
	private void startSharePlatform(String shareContent, byte[] fileByte,
			String shareUrl, String imgUrl) throws IOException {
		CustomSocialShareEntity shareEntity = new CustomSocialShareEntity();
		shareEntity.shareTitle = shareContent;// 标题与内容一致
		shareEntity.shareContent = shareContent;// 预先填写的文字
		shareEntity.shareContent = "我正通过@无线长春 观看长春的街路视频";
		shareEntity.imageByte = fileByte;// 友盟分享的图片
		shareEntity.shareSource = eyeName;// 分享来源
		shareEntity.shareUrl = shareUrl;// 分享地址
		shareEntity.imagePath = shotFileName;// 跳转到随手拍页面，读取图片地址
		shareEntity.cityCode = AdvertMgr.getInstance().getCityCode(mContext);
		shareEntity.shareType = playerType;
		String phoneNum = SharedPreferencesUtil.getValue(mContext,
				Key.K_PHONE_NUMBER);
		shareEntity.mobile = phoneNum;
		shareEntity.imageUrl = imgUrl;// 分享到qq和qq空间的图片地址
		// CustomSocialShare.shareImagePlatform(mActivity, shareEntity, true);
		CustomSocialShare.shareImagePlatform(mActivity, shareEntity, false);// 去掉随手拍分享
	}

	/**
	 * 上传文件到平台
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void upLoadFile() throws UnsupportedEncodingException {
		LoadingDialog.getDialog(mActivity).setMessage("图片上传中...").show();
		String mobile = "";
		String isLogin = SharedPreferencesUtil.getValue(mContext,
				Key.K_IS_LOGIN);
		if ("true".equals(isLogin)) {
			mobile = SharedPreferencesUtil.getValue(mContext,
					Key.K_PHONE_NUMBER);
		}
		roadBo.upLoadFile(mContext,
				AdvertMgr.getInstance().getCityCode(mContext),
				getString(R.string.glo_share_content, eyeName), shotFileName,
				mobile, upLoadImageCallBack);
	}

	class UpLoadImageCallBack implements
			cn.ffcs.wisdom.http.HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			LoadingDialog.getDialog(mActivity).cancel();
			if (response.isSuccess()) {
				try {
					RoadUpLoadImageEntity entity = (RoadUpLoadImageEntity) response
							.getObj();
					if (entity != null) {
						List<UpLoadImage> list = entity.relist;
						if (list != null && list.size() > 0) {
							String shareUrl = entity.relist.get(0).shareWapUrl;
							String imgUrl = entity.relist.get(0).imageUrl;
							byte[] fileByte = getBytesFromFile(new File(
									shotFileName));
							startSharePlatform(
									getString(R.string.glo_share_content,
											eyeName), fileByte, shareUrl,
									imgUrl);
						} else {
							CommonUtils.showToast(mActivity, "图片上传失败",
									Toast.LENGTH_SHORT);
						}
					} else {
						CommonUtils.showToast(mActivity, "图片上传失败",
								Toast.LENGTH_SHORT);
					}
				} catch (IOException e) {
					Log.e(e.getMessage(), e);
				}
			} else {
				CommonUtils.showToast(mActivity, response.getDesc(),
						Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (WeiBoSocialShare.mSsoHandler != null) {
			WeiBoSocialShare.mSsoHandler.authorizeCallBack(requestCode,
					resultCode, data);
		}
	}
}
