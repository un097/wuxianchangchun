package cn.ffcs.wisdom.city.personcenter.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.sqlite.model.RememberInfo;
import cn.ffcs.wisdom.city.sqlite.service.RememberInfoService;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.Log;

/**
 * <p>Title:    记住账号提示框list适配器        </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-12-6           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RememberListAdapter extends BaseAdapter implements Filterable {

	private final LayoutInflater mInflater;
	private List<RememberInfo> rememberList;
	private ArrayList<RememberInfo> mOriginalList;
	private RememberFilter rememberFilter;
	private final Object mLock = new Object();
	private Context mContext;

	public RememberListAdapter(Activity activity, List<RememberInfo> rememberList) {
		mInflater = (LayoutInflater) activity.getLayoutInflater();
		mContext = activity.getApplicationContext();
		if (rememberList != null) {
			this.rememberList = rememberList;
		} else {
			this.rememberList = Collections.emptyList();
		}
	}

	@Override
	public int getCount() {
		return rememberList.size();
	}

	@Override
	public Object getItem(int position) {
		return rememberList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			Holder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.listview_item_remember, parent, false);
				holder = new Holder();
				holder.userName = (TextView) convertView.findViewById(R.id.remember_name);
				holder.del = (ImageView) convertView.findViewById(R.id.delete);
				holder.bg = (LinearLayout) convertView.findViewById(R.id.bg);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (position == 0) {
				holder.bg.setBackgroundResource(R.drawable.remember_top_selector);
			} else {
				holder.bg.setBackgroundResource(R.drawable.remember_bottom_selector);
			}
			RememberInfo info = rememberList.get(position);
			String newString = new String(Base64.decode(info.userName, Base64.DEFAULT));
			holder.userName.setText(newString);
			holder.del.setOnClickListener(new DeleteListener(info));
		} catch (Exception e) {
			Log.e(e.getMessage());
		}
		return convertView;
	}

	class DeleteListener implements OnClickListener {
		RememberInfo info;

		DeleteListener(RememberInfo info) {
			this.info = info;
		}

		@Override
		public void onClick(View v) {
			RememberInfoService.getInstance(mContext).deleteByPhone(info.userName);
			rememberList.remove(info);
			mOriginalList.remove(info);
			notifyDataSetChanged();
		}
	}

	static final class Holder {
		TextView userName;
		ImageView del;
		LinearLayout bg;
	}

	@Override
	public Filter getFilter() {
		if (rememberFilter == null) {
			rememberFilter = new RememberFilter();
		}
		return rememberFilter;
	}

	class RememberFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			if (mOriginalList == null) {
				synchronized (mLock) {
					// 将list的用户 集合转换给这个原始数据的ArrayList  
					mOriginalList = new ArrayList<RememberInfo>(rememberList);
				}
			}
			if (constraint == null || constraint.length() == 0) {
				synchronized (mLock) {
					ArrayList<RememberInfo> list = new ArrayList<RememberInfo>(mOriginalList);
					results.values = list;
					results.count = list.size();
				}
			} else {
				// 做正式的筛选  
				String prefixString = constraint.toString().toLowerCase();

				// 声明一个临时的集合对象 将原始数据赋给这个临时变量  
				final ArrayList<RememberInfo> values = mOriginalList;

				final int count = values.size();

				// 新的集合对象  
				final ArrayList<RememberInfo> newValues = new ArrayList<RememberInfo>(count);

				for (int i = 0; i < count; i++) {
					final RememberInfo value = (RememberInfo) values.get(i);
					String userName = new String(Base64.decode(value.userName, Base64.DEFAULT));
					if (userName.startsWith(prefixString)) {
						newValues.add(value);
					}
				}
				// 然后将这个新的集合数据赋给FilterResults对象  
				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			rememberList = (List<RememberInfo>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}
	}
}
