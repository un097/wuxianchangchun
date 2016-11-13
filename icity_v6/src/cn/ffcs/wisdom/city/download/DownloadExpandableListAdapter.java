package cn.ffcs.wisdom.city.download;

import java.io.File;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.wisdom.city.sqlite.model.ApkInfo;
import cn.ffcs.wisdom.city.sqlite.service.ApkInfoService;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 已下载列表 适配器                                             </p>
 * <p>Description:  <br/>
 * 已下载列表:  <br/>
 *    |--未安装 <br/>
 *    |--已安装<br/>
 *  </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-10           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class DownloadExpandableListAdapter extends BaseExpandableListAdapter {

	private List<String> groupArray;
	private List<List<ApkInfo>> childArray;
	private LayoutInflater mInflater;
	private Activity activity;
	protected DownMgrBo downloadBo;
	private ApkInfoService service;
	private CityImageLoader loader;

	public DownloadExpandableListAdapter(Activity activity, List<String> groupArray,
			List<List<ApkInfo>> childArray) {
		this.activity = activity;
		downloadBo = new DownMgrBo(activity);
		service = ApkInfoService.getInstance(activity);
		mInflater = (LayoutInflater) activity.getApplicationContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		if (groupArray == null) {
			this.groupArray = Collections.emptyList();
		}
		this.groupArray = groupArray;
		this.childArray = childArray;
		loader = new CityImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getGroupCount() {
		return groupArray.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childArray.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupArray.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childArray.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
			ViewGroup parent) {
		ParentHolder parentHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.widget_download_group_layout, null);

			parentHolder = new ParentHolder();
			parentHolder.mDownloadGroupname = (TextView) convertView
					.findViewById(R.id.download_group_name);

			convertView.setTag(parentHolder);
		} else {
			parentHolder = (ParentHolder) convertView.getTag();
		}

		// 设置分组的名称
		parentHolder.mDownloadGroupname.setText(this.getGroup(groupPosition).toString());
		parentHolder.mDownloadGroupname.setClickable(false);

		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild,
			View convertView, ViewGroup parent) {
		ChildHolder childHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.widget_download_child_layout, null);
			childHolder = new ChildHolder();
			childHolder.mChildItemImg = (ImageView) convertView
					.findViewById(R.id.download_child_item_pic);
			childHolder.mChildTitleTv = (TextView) convertView
					.findViewById(R.id.download_child_item_name);
			childHolder.mChildItemSizeTv = (TextView) convertView
					.findViewById(R.id.download_child_item_size);
			childHolder.mInstallBtn = (Button) convertView
					.findViewById(R.id.download_child_install_btn);
			convertView.setTag(childHolder);
		} else {
			childHolder = (ChildHolder) convertView.getTag();
		}

		try {
			final ApkInfo entity = (ApkInfo) getChild(groupPosition, childPosition);
			//设置应用图标
			String imgUrl = entity.getIconUrl();
			loader.loadUrl(childHolder.mChildItemImg, imgUrl);
			//设置应用名称
			String name = entity.getApkName().trim();
			if (!StringUtil.isEmpty(name)) {
				childHolder.mChildTitleTv.setVisibility(View.VISIBLE);
				childHolder.mChildTitleTv.setText(name);
			} else {
				childHolder.mChildTitleTv.setVisibility(View.GONE);
			}
			childHolder.mChildTitleTv.setSelected(true);
			//设置应用大小
			String size = entity.getApkSize();
			if (StringUtil.isEmpty(size)) {
				size = "0";
			}
			if (size.contains("K") || size.contains("k")) {
				size = size.trim().replace("K", "").replace("k", "");
			}
			String formateSize = ByteUtil.bytes2KBorMB(Integer.parseInt(size.trim()) * 1024);
			if (!StringUtil.isEmpty(size) && !size.equals("0")) {
				childHolder.mChildItemSizeTv.setVisibility(View.VISIBLE);
				childHolder.mChildItemSizeTv.setText(formateSize);
			} else {
				childHolder.mChildItemSizeTv.setVisibility(View.GONE);
			}
			//设置应用安装按钮
			if (groupArray.get(groupPosition).indexOf("未安装") > -1) {
				setInstallBtn(childHolder, entity);
			}
			if (groupArray.get(groupPosition).indexOf("已安装") > -1) {
				setLunchApp(childHolder, entity);
			}

			convertView.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (groupArray.get(groupPosition).indexOf("未安装") > -1) {
						CreatDeleteDialog(entity);
					}
					if (groupArray.get(groupPosition).indexOf("已安装") > -1) {
						CreatUninstallDialog(entity);
					}
					return false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	/**
	 * 创建删除apk文件 选择对话框
	 * @param entity
	 */
	public void CreatDeleteDialog(final ApkInfo entity) {
		String title = "删除安装包";
		String msg = "\n您确定是否要删除此安装包？\n";
		OnClickListener confirmClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertBaseHelper.dismissAlert(activity);
				ApkInfoService.getInstance(activity).deleteByUrl(entity.getUrl());
				File file = new File(entity.getDir() + entity.getApkName() + ApkMgrConstants.POSTFIX);
				if (file.isFile()) {
					if (file.delete()) {
						CommonUtils.showToast(activity, R.string.download_delete_success,
								Toast.LENGTH_SHORT);
					} else {
						CommonUtils.showToast(activity, R.string.download_delete_file_fail,
								Toast.LENGTH_SHORT);
					}
				}
				downloadBo.sendRefreshBroadcast(ApkMgrConstants.RECEIVER_PACKAGE_REFRESH);
			}
		};
		AlertBaseHelper.showConfirm(activity, title, msg, "删除", "", confirmClick, null);
	}

	/**
	 * 创建卸载apk应用 确定对话框
	 * @param entity
	 */
	public void CreatUninstallDialog(final ApkInfo entity) {
		String apkName = entity.getApkName();
		if (StringUtil.isEmpty(apkName)) {
			apkName = "该应用";
		}

		String msg = "\n您确定是否要卸载\"" + apkName.trim() + "\"？\n";
		OnClickListener confirmClick = new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertBaseHelper.dismissAlert(activity);
				//卸载apk
				Uri packageURI = Uri.parse("package:" + entity.getPackageName());
				Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
				activity.startActivity(uninstallIntent);
				service.updateInstallStatu(ApkMgrConstants.INSTALL_FAIL, entity.getUrl());
				downloadBo.sendRefreshBroadcast(ApkMgrConstants.RECEIVER_PACKAGE_REFRESH);
			}
		};
		AlertBaseHelper.showConfirm(activity, "卸载应用", msg, "卸载", "", confirmClick, null);

	}

	private void setLunchApp(ChildHolder childHolder, final ApkInfo entity) {
		childHolder.mInstallBtn.setText("运行");
		childHolder.mInstallBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadBo.runApp(entity);
			}
		});
	}

	private void setInstallBtn(ChildHolder childHolder, final ApkInfo entity) {
		childHolder.mInstallBtn.setText("安装");
		childHolder.mInstallBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadBo.installAppByFile(entity);
				downloadBo.sendRefreshBroadcast(ApkMgrConstants.RECEIVER_PACKAGE_REFRESH);
			}
		});
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	class ParentHolder {
		TextView mDownloadGroupname;
	}

	class ChildHolder {
		ImageView mChildItemImg;
		TextView mChildTitleTv;
		TextView mChildItemSizeTv;
		Button mInstallBtn;
	}
}
