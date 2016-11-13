package cn.ffcs.external.share.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.external.share.entity.CustomSocialEntity;
import cn.ffcs.external.share.utils.CustomSocialApp;
import cn.ffcs.external.share.view.CustomSocialShare;

import com.example.external_socialshare.R;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * <p>Title: 分享适配器         </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: Leo               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-11-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CustomShareImageAdapter extends BaseAdapter {
	private Activity mActivity;
	private List<CustomSocialEntity> mList = new ArrayList<CustomSocialEntity>();
	private final LayoutInflater mInflater;
	private boolean showPhoto;

	public CustomShareImageAdapter(Activity activity, List<CustomSocialEntity> list,
			boolean showPhoto) {
		this.mActivity = activity;
		this.mList = list;
		mInflater = (LayoutInflater) activity.getLayoutInflater();
		this.showPhoto = showPhoto;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.custom_umeng_socialize_shareboard_item,
					parent, false);
		}
		ImageView icon = (ImageView) convertView
				.findViewById(R.id.umeng_socialize_shareboard_image);
		TextView name = (TextView) convertView
				.findViewById(R.id.umeng_socialize_shareboard_pltform_name);
		final CustomSocialEntity entity = mList.get(position);
		if (entity != null) {
			icon.setImageResource(entity.socialImageResId);
			name.setText(entity.socialNameCn);
		} else {
			icon.setVisibility(View.GONE);
			name.setText("");
		}
		int size = 0;
		if (showPhoto) {
			size = mList.size();
		} else {
			size = mList.size() - 1;
		}
		if (position < size) {
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (entity != null) {
						String shareUrl = entity.shareEntity.shareUrl;
						if (shareUrl.contains("?")) {
							shareUrl = shareUrl + "&app=";
						} else {
							shareUrl = shareUrl + "?app=";
						}
						if ("sina".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "sina";
						} else if ("tencent".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "tencent";
						} else if ("douban".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "douban";
						} else if ("weixin".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "weixin";
						} else if ("weixincircle".equals(entity.socialNameEn)) {
//							entity.shareEntity.shareTitle = entity.shareEntity.shareContent;
//							entity.shareEntity.shareTitle = mActivity.getString(R.string.spread_string_weixin, entity.shareEntity.spreadCode);
							entity.shareEntity.shareUrl = shareUrl + "weixincircle";
						} else if ("photo".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "photo";
						} else if ("yixin".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "yixin";
						} else if ("yixincircle".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "yixincircle";
						} else if ("QQ".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "qq";
						} else if ("QZONE".equals(entity.socialNameEn)) {
							entity.shareEntity.shareUrl = shareUrl + "qzone";
						}
						String clientType = "changchunTV_ver";
						entity.shareEntity.shareUrl = entity.shareEntity.shareUrl + "&clientType=" + clientType;
						Log.e("fmj", "entity.shareEntity.shareUrl   "  + entity.shareEntity.shareUrl);
						CustomSocialApp.startImageApp(mActivity, entity);
						CustomSocialShare.dismissAlertDialog(mActivity);
					}
				}
			});
		} else {
			convertView.setBackgroundColor(Color.WHITE);
		}

		return convertView;
	}
}
