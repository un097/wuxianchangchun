package cn.ffcs.wisdom.city.myapp.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.myapp.MyFavAddActivity;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;

/**
 * <p>Title：我的应用适配器          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-7             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MyAppGridViewAdapter extends BaseAdapter {
	private final int REQUESTCODE_ADD = 1;
	private Context mContext;
	private Activity mActivity;
	private final LayoutInflater mInflater;
	private List<MenuItem> mAppList = new ArrayList<MenuItem>();
	private CityImageLoader loader;

	public MyAppGridViewAdapter(Context context, Activity activity, List<MenuItem> list) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
		this.mActivity = activity;
		this.mAppList = list;
		loader = new CityImageLoader(context);
	}

	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.gridview_item_myapp_edit, parent, false);
		}
		RelativeLayout mMyAppLayout = (RelativeLayout) convertView.findViewById(R.id.bg);
		ImageView mMyAppIcon = (ImageView) convertView.findViewById(R.id.gird_item_indicator);
		TextView mMyAppText = (TextView) convertView.findViewById(R.id.gird_item_content);
		mMyAppText.setSelected(true);
		mMyAppIcon.setVisibility(View.VISIBLE);
		mMyAppText.setText("");
		final MenuItem item = mAppList.get(position);
		if ((position + 1) == mAppList.size()) {
			mMyAppIcon.setVisibility(View.GONE);
			mMyAppText.setText("");
			mMyAppLayout.setBackgroundResource(R.drawable.myapp_add_btn);
		} else {
			mMyAppLayout.setBackgroundResource(R.drawable.btn_blue_selector);
			if (item != null) {
				mMyAppText.setText(item.getMenuName());
				loader.loadUrl(mMyAppIcon, item.getV6Icon());
			}
		}

		// 点击应用，跳转到相应界面
		mMyAppLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ((position + 1) == getCount()) {// 不含任何应用，跳转到添加列表
					Intent intent = new Intent(mActivity, MyFavAddActivity.class);
					intent.putExtra(Key.K_RETURN_TITLE, mActivity.getString(R.string.myapp_title));
					intent.putExtra("mMyAppList", (Serializable) mAppList);
					mActivity.startActivityForResult(intent, REQUESTCODE_ADD);
				} else {
					if (position >= 0 && position < getCount()) {
						MenuItem item = mAppList.get(position);
						if (item != null) {
							AppMgrUtils.launchAPP(mActivity, item,
									mContext.getString(R.string.myapp_title));
						}
					}
				}
			}
		});
		return convertView;
	}
}
