package cn.ffcs.wisdom.city.personcenter.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.personcenter.MyNotifierActivity;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.sqlite.model.NotificationInfo;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.notify.MsgEntity;
import cn.ffcs.wisdom.tools.JsonUtil;

public class MyNotifiAdapter extends BaseAdapter {
	private LayoutInflater myInflater;
	private List<NotificationInfo> datalist;
	public static HashMap<Integer, Boolean> isSelected = new HashMap<Integer, Boolean>();
	private CityImageLoader loader;
	private boolean loginFlag;

	public MyNotifiAdapter(Context context, List<NotificationInfo> list) {
		this.datalist = list;
		myInflater = LayoutInflater.from(context);
		initDate();
		loader = new CityImageLoader(context);
		loginFlag = AccountMgr.getInstance().isLogin(context);
	}

	@Override
	public int getCount() {
		return datalist.size();
	}

	@Override
	public Object getItem(int position) {
		return datalist.get(position);

	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parentView) {

		NotificationInfo notificationInfo = datalist.get(position);
		MsgEntity msgEntity = JsonUtil.toObject(notificationInfo.getMsgInfo(), MsgEntity.class);
		String content = msgEntity.getContent().getMsgContent();
		String url = msgEntity.getContent().getParam().getPicUrl();
		String date = notificationInfo.getInsertDate();
		Hold hold;
		if (convertView == null) {
			convertView = myInflater.inflate(R.layout.listview_item_person_center_notice,
					parentView, false);
			hold = new Hold();
			hold.typeIcon = (ImageView) convertView.findViewById(R.id.type_icon);
			hold.chechbox = (CheckBox) convertView.findViewById(R.id.personcenter_checkbox);
			hold.title = (TextView) convertView.findViewById(R.id.notice_title);
			hold.time = (TextView) convertView.findViewById(R.id.notice_time);

			if (MyNotifierActivity.isedit) {
				hold.chechbox.setVisibility(View.GONE);
			} else {
				hold.chechbox.setVisibility(View.VISIBLE);
			}
			convertView.setTag(hold);
		} else {
			hold = (Hold) convertView.getTag();
		}
		// 根据isSelected来设置checkbox的选中状况    
		hold.chechbox.setChecked(getIsSelected().get(position));
		loader.loadUrl(hold.typeIcon, url);
		if (content != null) {
			if(!loginFlag){
				hold.title.setText(R.string.notice_person_msg_login_tip);
			} else {
				hold.title.setText(content);
			}
			if ("true".equals(notificationInfo.getIsNew())) {
				hold.title.setTextColor(convertView.getResources().getColor(R.color.black));
			} else {
				hold.title.setTextColor(convertView.getResources().getColor(R.color.dark_gray_787878));
			}
		}
		if (date != null) {
			try {
				SimpleDateFormat formatdata = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date dated = formatdata.parse(date);
				String mydate = new SimpleDateFormat("MM-dd HH:mm").format(dated);
				hold.time.setText(mydate);
			} catch (Exception e) {
				hold.time.setText(date);
				e.printStackTrace();
			}
		}

		return convertView;

	}

	public static class Hold {
		public TextView title;
		public CheckBox chechbox;
		ImageView typeIcon;
		TextView time;
	}

	// 初始化isSelected的数据  
	private void initDate() {
		for (int i = 0; i <= getCount(); i++) {
			getIsSelected().put(i, false);
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		MyNotifiAdapter.isSelected = isSelected;
	}
}
