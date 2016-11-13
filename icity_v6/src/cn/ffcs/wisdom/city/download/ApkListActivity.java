package cn.ffcs.wisdom.city.download;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;
import cn.ffcs.wisdom.city.sqlite.service.ApkInfoService;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;

public class ApkListActivity extends WisdomCityActivity {

	private ViewPager mViewPager;
	private DownMgrAdapter adapter;
	private int curChannel;// 当前点击标题
	private int index = 0;// 滑动页面索引
	private TextView downingTitle;
	private TextView finishedTitle;

	private ApkItemLayout apkContainer;
	private ApkInfoService service;

	private LayoutInflater mInflater;
	private View downingApklist = null;
	private View downFinishApklist = null;
	private List<String> groupArray = new ArrayList<String>();
	private List<List<ApkInfo>> childArray = new ArrayList<List<ApkInfo>>();
	private ExpandableListView finishListView;
	private View noDataPic;
	private View noDatafinishPic;

	private List<View> mListView = new ArrayList<View>();// 页面列表
	private List<TextView> textViewList = new ArrayList<TextView>();// 标题栏目列表

	@Override
	protected void initComponents() {
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mViewPager = (ViewPager) findViewById(R.id.download_manager_switch_page);
		// 正在下载栏目
		downingApklist = mInflater.inflate(R.layout.widget_apklist_downloading, null);
		apkContainer = (ApkItemLayout) downingApklist.findViewById(R.id.apk_list_container);
		downingTitle = (TextView) findViewById(R.id.downloading_channel_title);
		downingTitle.setOnClickListener(new ChannelOnClickListener());
		noDataPic = downingApklist.findViewById(R.id.init_downloading_pic);
		// 已下载栏目
		downFinishApklist = mInflater.inflate(R.layout.widget_apklist_finished, null);
		finishedTitle = (TextView) findViewById(R.id.download_finished_channel_title);
		finishedTitle.setOnClickListener(new ChannelOnClickListener());
		noDatafinishPic = downFinishApklist.findViewById(R.id.init_download_finished_pic);
		finishListView = (ExpandableListView) downFinishApklist
				.findViewById(R.id.download_install_exlv);
	}

	@Override
	protected void initData() {
		index = 0;
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.download_manager);

		registerUpdateApkListBroadcast();

		textViewList.add(downingTitle);
		textViewList.add(finishedTitle);

		mListView.add(downingApklist);
		mListView.add(downFinishApklist);
		adapter = new DownMgrAdapter(mListView);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(new DownMgrSwitchListener());

		service = ApkInfoService.getInstance(mContext);

		String completeDownlaod = getIntent()
				.getStringExtra(ApkMgrConstants.COMPLETE_DOWNLOAD_FLAG);
		if (ApkMgrConstants.COMPLETE_DOWNLOAD_YES.equals(completeDownlaod)) {
			move2finishChannel();
		}
	}

	// 栏目点击
	class ChannelOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == curChannel) {
				return;
			}
			if (id == R.id.downloading_channel_title) {
				index = 0;
				curChannel = R.id.downloading_channel_title;
			} else if (id == R.id.download_finished_channel_title) {
				index = 1;
				curChannel = R.id.download_finished_channel_title;
			}
			mViewPager.setCurrentItem(index);
			changeTitleBar(index);
		}
	}

	//页面滑动监听
	class DownMgrSwitchListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			index = position;
			curChannel = textViewList.get(position).getId();
			changeTitleBar(position);
		}
	}

	/**
	 * 点击或滑动时 改变title_bar背景
	 * @param index
	 */
	public void changeTitleBar(int index) {
		downingTitle.setBackgroundResource(R.drawable.download_channel_normal);
		finishedTitle.setBackgroundResource(R.drawable.download_channel_normal);
		switch (index) {
		case 0:
			downingTitle.setBackgroundResource(R.drawable.download_channel_select);
			break;
		case 1:
			finishedTitle.setBackgroundResource(R.drawable.download_channel_select);
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新整体界面显示
	 */
	private void refreshUI() {
		refreshDowningPageData();
		refreshDownfinishPageData();
		statistics();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 统计各个栏目的下载中数目、已下载数目
	 */
	private void statistics() {
		List<ApkInfo> noCompleteList = service.queryNoCompleteApk();
		int runningCount = noCompleteList.size();
		List<ApkInfo> noInstallList = service.queryNoInstallList();
		List<ApkInfo> installList = service.queryInstallList();
		int finishedCount = noInstallList.size() + installList.size();
		String downingTxt = "";
		String finishTxt = "";
		String downingNum = getString(R.string.download_running);
		String finishNum = getString(R.string.download_finished);
		downingTxt = String.format(downingNum, runningCount);
		finishTxt = String.format(finishNum, finishedCount);
		downingTitle.setText(downingTxt);
		finishedTitle.setText(finishTxt);
	}

	/**
	 * 刷新下载页数据
	 */
	private void refreshDowningPageData() {
		List<ApkInfo> noCompleteApkList = service.queryNoCompleteApk();
		if (noCompleteApkList != null && noCompleteApkList.size() > 0) {
			noDataPic.setVisibility(View.GONE);
		} else {
			noDataPic.setVisibility(View.VISIBLE);
		}
		apkContainer.refreshData(noCompleteApkList);
	}

	/**
	 * 刷新已下载页数据<br/>
	 * <br/>
	 * 以下3种情况 会 更新已下载界面数据：<br/>
	 * 1."下载中"界面有应用下载完成时，,自动跳到"已下载"界面，并刷新"已下载"界面数据<br/>
	 * 2.用户删除了下载文件，点"安装"按钮后,会刷新界面，并删除此记录<br/>
	 * 3.程序被卸载情况 下,更新已下载界面,并删除此记录<br/>
	 */
	private void refreshDownfinishPageData() {
		//修正应用安装状态
		updateInstallStatu();

		List<ApkInfo> installList = service.queryInstallList();
		List<ApkInfo> noInstallList = service.queryNoInstallList();

		groupArray.clear();
		childArray.clear();

		int noInstallListSize = noInstallList.size();
		int installListSize = installList.size();

		String noInstallTxt = "";
		String installTxt = "";
		String noInstalledNum = getString(R.string.download_no_installed);
		String installedNum = getString(R.string.download_installed);
		noInstallTxt = String.format(noInstalledNum, noInstallListSize);
		installTxt = String.format(installedNum, installListSize);

		if (noInstallListSize > 0) {
			groupArray.add(noInstallTxt);
			childArray.add(noInstallList);
		}
		if (installListSize > 0) {
			groupArray.add(installTxt);
			childArray.add(installList);
		}
		if (noInstallListSize > 0 || installListSize > 0) {
			noDatafinishPic.setVisibility(View.GONE);
		} else {
			noDatafinishPic.setVisibility(View.VISIBLE);
		}
		setExpandableList();
	}

	private void updateInstallStatu() {
		List<ApkInfo> allApk = service.queryDownSuccessList();
		if (allApk != null && allApk.size() > 0) {
			for (ApkInfo entity : allApk) {
				PackageInfo packageInfo = AppHelper.getPackageInfo(mActivity,
						entity.getPackageName());
				if (packageInfo != null) {// 已经安装过apk
					service.updateInstallStatu(ApkMgrConstants.INSTALL_SUCCESS, entity.getUrl());// 10:已安装
				} else {
					service.updateInstallStatu(ApkMgrConstants.INSTALL_FAIL, entity.getUrl());// 20:未安装
				}
			}
		}
	}

	// 设置ExpandableList初始属性
	private void setExpandableList() {
		if (groupArray == null || childArray == null /*
													 * || groupArray.size() <= 0
													 * || childArray.size() <= 0
													 */) {
			return;
		}
		final DownloadExpandableListAdapter adapter = new DownloadExpandableListAdapter(mActivity,
				groupArray, childArray);
		finishListView.setAdapter(adapter);
		// 设置ExpandableListView 默认是展开的
		int groupCount = adapter.getGroupCount();
		for (int i = 0; i < groupCount; i++) {
			finishListView.expandGroup(i);
		}
		// 禁用ExpandableListView的GroupItem点击事件
		finishListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
					long id) {
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		refreshUI();
		super.onResume();
	}

	/**
	 * 注册更新下载应用列表广播
	 */
	private void registerUpdateApkListBroadcast() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Config.ACTION_REFRESH_APK_LIST);
		LocalBroadcastManager.getInstance(mContext).registerReceiver(new UpdateApkListReceiver(),
				filter);
	}

	class UpdateApkListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int control = intent.getIntExtra(ApkMgrConstants.INTENT_CONTROL_APK, -1);
			switch (control) {
			case ApkMgrConstants.RECEIVER_DELETE_APK:// 从apk列表界面删除，在已下载界面显示
				move2finishChannel();
				break;
			case ApkMgrConstants.RECEIVER_UPDATE:// 删除任务时刷新"下载中"界面
				break;
			case ApkMgrConstants.RECEIVER_PACKAGE_REFRESH:// 安装包不存在，被删除情况 或 程序被卸载情况 下 更新已下载界面
				break;
			default:
				break;
			}
			refreshUI();
		}

	}

	//跳转到已下载界面
	private void move2finishChannel() {
		if (R.id.download_finished_channel_title == curChannel) {
			return;
		}
		index = 1;
		curChannel = R.id.download_finished_channel_title;
		mViewPager.setCurrentItem(index);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_apk_manager;
	}

}
