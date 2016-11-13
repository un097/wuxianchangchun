package cn.ffcs.wisdom.city.download;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.download.report.ApkReportUtil;
import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;
import cn.ffcs.wisdom.city.sqlite.service.ApkInfoService;
import cn.ffcs.wisdom.city.utils.CityDownloader;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:   自定义单个下载项                                    </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-24           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ApkItemLayout extends LinearLayout {

	private Context mContext;
	private LayoutInflater mInflater;

	private List<ApkInfo> mData = new ArrayList<ApkInfo>();
	private CityImageLoader imgLoader;

	private ApkInfoService service;

	private Activity mActivity;
	private static ConcurrentHashMap<String, CityDownloader> downLoaderMap;
	private static ConcurrentHashMap<CityDownloader, ProgressBar> apkProgressMap;
	private static ConcurrentHashMap<CityDownloader, TextView> apkSizeMap;

	public ApkItemLayout(Context context) {
		super(context);
		init(context);
	}

	public ApkItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		mActivity = (ApkListActivity) context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setOrientation(LinearLayout.VERTICAL);

		imgLoader = new CityImageLoader(context);
		service = ApkInfoService.getInstance(mContext);
	}

	public void refreshData(List<ApkInfo> list) {
		try {
			mData.clear();
			mData.addAll(list);
			requestView();
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}
	}

	private void requestView() throws Exception {
		removeAllViews();
		for (int i = 0; i < mData.size(); i++) {
			View apkItem = mInflater.inflate(R.layout.listview_item_apklist, null);
			TextView apkName = (TextView) apkItem.findViewById(R.id.apk_name);
			TextView apkSize = (TextView) apkItem.findViewById(R.id.apk_size);
			ImageView apkIcon = (ImageView) apkItem.findViewById(R.id.apk_icon);
			ProgressBar apkProgress = (ProgressBar) apkItem.findViewById(R.id.apk_progress);
			Button pauseBtn = (Button) apkItem.findViewById(R.id.download_pause_btn);
			Button startBtn = (Button) apkItem.findViewById(R.id.download_start_btn);

			ApkInfo apkInfo = mData.get(i);
			String name = apkInfo.getApkName();
			if (StringUtil.isEmpty(name)) {
				name = mContext.getString(R.string.download_apk_default_name);
			}
			apkName.setText(name);
			apkName.setSelected(true);

			String iconUrl = apkInfo.getIconUrl();
			imgLoader.loadUrl(apkIcon, iconUrl);

			DownListener listener = new DownListener(apkInfo, apkSize, apkProgress, pauseBtn,
					startBtn);
			pauseBtn.setOnClickListener(listener);
			startBtn.setOnClickListener(listener);

			apkItem.setClickable(true);
			apkItem.setOnLongClickListener(listener);

			addView(apkItem);
		}
		requestLayout();
	}

	class DownListener extends AjaxCallBack<File> implements OnClickListener, OnLongClickListener {

		private ApkInfo apkInfo;
		private TextView apkSize;
		private ProgressBar apkProgress;
		private Button pauseBtn;
		private Button startBtn;

		public static final int PENDING = 1;
		public static final int RUNNING = 2;
		public static final int PAUSE = 3;
		public static final int SUCCESS = 4;

		private int curStatu; // 1 未下载, 2 下载中, 3暂停, 4下载完成
		private int preStatu; // 记录从主页跳进来的状态

		private String mUrl;
		private String mApkName;

		private CityDownloader downLoader;

		private boolean isDelete;

		public DownListener(ApkInfo apkInfo, TextView apkSize, ProgressBar apkProgress,
				Button pauseBtn, Button startBtn) {
			this.apkInfo = apkInfo;
			this.apkSize = apkSize;
			this.apkProgress = apkProgress;
			this.pauseBtn = pauseBtn;
			this.startBtn = startBtn;

			curStatu = PENDING;
			isDelete = false;

			mUrl = apkInfo.getUrl();
			mApkName = apkInfo.getApkName();

			if (downLoaderMap == null) {
				downLoaderMap = new ConcurrentHashMap<String, CityDownloader>();
			}
			if (apkProgressMap == null) {
				apkProgressMap = new ConcurrentHashMap<CityDownloader, ProgressBar>();
			}
			if (apkSizeMap == null) {
				apkSizeMap = new ConcurrentHashMap<CityDownloader, TextView>();
			}

			initUI();
			initDownLoader();
		}

		protected void initUI() {
			initProgress();

			if (isRunning()) {
				preStatu = RUNNING;
				showPauseBtn();
			} else if (isPause()) {
				preStatu = PAUSE;
				showStartBtn();
			} else {
				preStatu = PENDING;
				showStartBtn();
			}
		}

		private void initProgress() {
			int count = 0;
			int current = 0;
			String fileSize = "";
			String completeSize = "";
			if (apkInfo.getFileSize() > 0) {
				count = apkInfo.getFileSize();
				fileSize = ByteUtil.bytes2KBorMB(count);
			}
			if (apkInfo.getCompleteSize() > 0) {
				current = apkInfo.getCompleteSize();
				completeSize = ByteUtil.bytes2KBorMB(current);
			}
			if (!StringUtil.isEmpty(fileSize) && !StringUtil.isEmpty(completeSize)) {
				apkSize.setText(completeSize + "/" + fileSize);
				apkProgress.setMax(count);
				apkProgress.setProgress(current);
			} else if (!StringUtil.isEmpty(fileSize)) {
				apkSize.setText(fileSize);
			} else {
				apkSize.setText(mContext.getString(R.string.common_loading));
			}
		}

		private void initDownLoader() {
			downLoader = downLoaderMap.get(mUrl);
			if (downLoader == null) {
				downLoader = new CityDownloader(mContext);
				downLoader.setCategory(CityDownloader.CATEGORY_APK);
				downLoaderMap.put(mUrl, downLoader);
			}

			apkProgressMap.put(downLoader, apkProgress);
			apkSizeMap.put(downLoader, apkSize);

			if (downLoader.isRunning()) {
				showPauseBtn();
			} else {
				showStartBtn();
			}

			startDownload();
		}

		@Override
		public boolean isProgress() {
			return super.isProgress();
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			try {
				if (t == null) {
					return;
				}
				if (StringUtil.isEmpty(strMsg)) {
					strMsg = t.getMessage();
				}
				if (StringUtil.isEmpty(strMsg) && t.getCause() != null) {
					strMsg = t.getCause().getMessage();
				}
				handleDiyMsg(strMsg);
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}

		private void handleDiyMsg(String strMsg) {
			boolean flag = true; // 提示标志
			if (strMsg.contains(getString(R.string.download_pause_afinal_tip))) {
				strMsg = mApkName + " 暂停";
				flag = false;
			} else if (strMsg.contains(getString(R.string.download_afinal_http_error_tip))) {
				String reDownFormat = getString(R.string.download_re_down_tip);
				strMsg = String.format(reDownFormat, mApkName);
			}
			if (StringUtil.isEmpty(strMsg)) {
				strMsg = mApkName + "下载异常";
			}
			if (flag && !isDelete) {
				CommonUtils.showToast(mActivity, strMsg, 200);
			}
		}

		@Override
		public void onLoading(long count, long current) {
			try {
				String fileSize = ByteUtil.bytes2KBorMB(count);
				String completeSize = ByteUtil.bytes2KBorMB(current);

				apkProgress = apkProgressMap.get(downLoader);
				if (apkProgress != null) {
					apkProgress.setMax((int) count);
					apkProgress.setProgress((int) current);
				}
				apkSize = apkSizeMap.get(downLoader);
				if (apkSize != null) {
					apkSize.setText(completeSize + "/" + fileSize);
				}

				updateApk(count, current);
				requestLayout();
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}

		// 保存更新下载信息
		private void updateApk(long count, long current) {
			ApkInfo newInfo = new ApkInfo();
			int oldId = apkInfo.getId();
			newInfo = apkInfo;
			newInfo.setId(oldId);
			newInfo.setFileSize((int) count);
			newInfo.setCompleteSize((int) current);
			if (curStatu == RUNNING) {
				newInfo.setDownloadStatu(ApkMgrConstants.DOWNLOAD_RUNNING);
			} else if (curStatu == PAUSE) {
				newInfo.setDownloadStatu(ApkMgrConstants.DOWNLOAD_PAUSE);
			}
			service.updateApk(newInfo);
		}

		@Override
		public void onStart() {
			service.updateInstallStatu(ApkMgrConstants.INSTALL_FAIL, mUrl);
		}

		@Override
		public void onSuccess(final File t) {
			try {
				File dest = new File(t.getAbsolutePath() + ApkMgrConstants.POSTFIX);
				t.renameTo(dest);
				service.updateDownStatu(ApkMgrConstants.DOWNLOAD_SUCCESS, mUrl);
				sendRefreshBroadcast(ApkMgrConstants.RECEIVER_DELETE_APK, apkInfo);
				ApkReportUtil.addApkLog(mContext, apkInfo);//add 2013-11-6 上报
			} catch (Exception e) {
				Log.e(e.getMessage(), e);
			}
		}

		@Override
		public AjaxCallBack<File> progress(boolean progress, int rate) {
			return super.progress(progress, rate);
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (R.id.download_pause_btn == id) {
				curStatu = PENDING;
			} else if (R.id.download_start_btn == id) {
				curStatu = RUNNING;
			}
			preStatu = PENDING;

			switch (curStatu) {
			case PENDING: // 当前状态为暂停
				stopDownload();
				break;
			case RUNNING: // 当前状态为下载
				startDownload();
				break;
			default:
				break;
			}
		}

		private void startDownload() {
			if (preStatu == RUNNING) {
				showPauseBtn();
				setRunning();
				return;
			}
			if (preStatu == PAUSE) {
				showStartBtn();
				setPause();
				return;
			}
			showPauseBtn();
			downLoader.down(mUrl, mApkName, this);
			setRunning();
			curStatu = RUNNING;
		}

		private void stopDownload() {
			if (preStatu == PAUSE) {
				showPauseBtn();
				setPause();
				return;
			}
			showStartBtn();
			downLoader.stopDownload();
			setPause();
			curStatu = PAUSE;
		}

		@Override
		public boolean onLongClick(View v) {
			creatDeleteTaskDialog(apkInfo, downLoader);
			return false;
		}

		public void showStartBtn() {
			startBtn.setVisibility(View.VISIBLE);
			pauseBtn.setVisibility(View.GONE);
		}

		public void showPauseBtn() {
			startBtn.setVisibility(View.GONE);
			pauseBtn.setVisibility(View.VISIBLE);
		}

		public void sendRefreshBroadcast(int control) {
			this.sendRefreshBroadcast(control, null);
		}

		public void sendRefreshBroadcast(int control, ApkInfo entity) {
			if (isDelete && ApkMgrConstants.RECEIVER_DELETE_APK == control) {//已删除此下载任务
				return;
			}
			Intent sendIntent = new Intent(Config.ACTION_REFRESH_APK_LIST);
			sendIntent.putExtra(ApkMgrConstants.INTENT_CONTROL_APK, control);
			if (entity != null) {
				sendIntent.putExtra(ApkMgrConstants.INTENT_APK_ENTITY, entity);
			}
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(sendIntent);
		}

		/**
		 * 创建删除下载任务 确认对话框
		 * @param entity
		 * @param downLoader 
		 * @param isDelete 
		 */
		public void creatDeleteTaskDialog(final ApkInfo entity, final CityDownloader downLoader) {
			String title = "删除下载任务";
			if (StringUtil.isEmpty(mApkName)) {
				mApkName = "此下载任务";
			}

			String msg = "\n您确定是否要删除\"" + mApkName.trim() + "\"下载任务？\n";
			OnClickListener confirmClick = new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertBaseHelper.dismissAlert(mActivity);
					dodelete(entity, downLoader);
					sendRefreshBroadcast(ApkMgrConstants.RECEIVER_UPDATE);
				}
			};
			AlertBaseHelper.showConfirm(mActivity, title, msg, "删除", "", confirmClick, null);
		}

		/**
		 * 具体删除任务动作
		 * @param mActivity
		 * @param entity
		 * @param downLoader
		 * @param isDelete 
		 */
		private void dodelete(ApkInfo entity, CityDownloader downLoader) {
			isDelete = true;
			downLoader.stopDownload();
			service.deleteByUrl(entity.getUrl());
			new DownMgrBo(mActivity).deleteApk(entity);
		}

		private void setRunning() {
			service.updateDownStatu(ApkMgrConstants.DOWNLOAD_RUNNING, mUrl);
		}

		private void setPause() {
			service.updateDownStatu(ApkMgrConstants.DOWNLOAD_PAUSE, mUrl);
		}

		/**
		 * 首次初始化判断状态
		 * @return
		 */
		private boolean isRunning() {
			return ApkMgrConstants.DOWNLOAD_RUNNING == apkInfo.getDownloadStatu() ? true : false;
		}

		/**
		 * 首次初始化判断状态
		 * @return
		 */
		private boolean isPause() {
			return ApkMgrConstants.DOWNLOAD_PAUSE == apkInfo.getDownloadStatu() ? true : false;
		}

	}

	private String getString(int resId) {
		return mContext.getString(resId);
	}

}
