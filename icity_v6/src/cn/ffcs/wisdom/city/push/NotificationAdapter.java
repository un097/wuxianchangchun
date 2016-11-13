package cn.ffcs.wisdom.city.push;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.sqlite.model.NotificationInfo;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.TimeUitls;

/**
 * <p>Title: 消息通知适配器     </p>
 * <p>Description: 
 *  消息通知适配器
 * </p>
 * <p>@author: Eric.wsd               	 </p>
 * <p>Copyright: Copyright (c) 2012   	 </p>
 * <p>Company: FFCS Co., Ltd.         	 </p>
 * <p>Create Time: 2012-6-15             </p>
 * <p>Update Time: 2013-1-14             </p>
 * <p>Updater: liaodl                    </p>
 * <p>Update Comments:                   </p>
 */
public class NotificationAdapter extends BaseAdapter {

	private List<NotificationInfo> notificationList;

	private LayoutInflater mInflater;

	public NotificationAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<NotificationInfo> list) {
		if (list == null) {
			notificationList = Collections.emptyList();
		} else {
			notificationList = list;
		}
	}

	@Override
	public int getCount() {
		return notificationList.size();
	}

	@Override
	public Object getItem(int pos) {
		return notificationList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			v = mInflater.inflate(R.layout.widget_notification_item, null);
			holder = new ViewHolder();
			holder.mTitleTv = (TextView) v.findViewById(R.id.notification_title);
			holder.mDateTv = (TextView) v.findViewById(R.id.notification_date);
			holder.mNewImg = (ImageView) v.findViewById(R.id.notification_new);

			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		try {
			NotificationInfo table = notificationList.get(pos);
			MsgEntity notice = JsonUtil.toObject(table.getMsgInfo(), MsgEntity.class);

			if ("true".equals(table.getIsNew())) {
				holder.mNewImg.setVisibility(View.VISIBLE);
			} else {
				holder.mNewImg.setVisibility(View.GONE);
			}

			holder.mTitleTv.setSelected(true);
			holder.mTitleTv.setText(notice.getTitle());
//			holder.mDateTv.setText(notice.getContent().getParam().getCreateTime());
			holder.mDateTv.setText(TimeUitls.LongToString(Long.parseLong(notice.getContent().getMsgContent())));
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		}

		return v;
	}

	class ViewHolder {
		TextView mTitleTv;
		TextView mDateTv;
		ImageView mNewImg;
	}
}
