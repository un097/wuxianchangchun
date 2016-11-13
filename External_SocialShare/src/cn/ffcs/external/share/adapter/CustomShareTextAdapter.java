package cn.ffcs.external.share.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
public class CustomShareTextAdapter extends BaseAdapter {
	private Activity mActivity;
	private List<CustomSocialEntity> mList = new ArrayList<CustomSocialEntity>();
	private final LayoutInflater mInflater;
	private String shareTitle;
	private String shareContent;
	private String shareUrl;

	public CustomShareTextAdapter(Activity activity, List<CustomSocialEntity> list,
			String shareTitle, String shareContent, String shareUrl) {
		this.mActivity = activity;
		this.mList = list;
		this.shareTitle = shareTitle;
		this.shareContent = shareContent;
		this.shareUrl = shareUrl;
		mInflater = (LayoutInflater) activity.getLayoutInflater();
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

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (entity != null) {
					CustomSocialApp.startTextApp(mActivity, entity, shareTitle, shareContent,
							shareUrl);
					CustomSocialShare.dismissAlertDialog(mActivity);
				}
			}
		});

		return convertView;
	}
}
