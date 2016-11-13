package cn.ffcs.external.trafficbroadcast.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_ReviewItem_Entity;

import com.example.external_trafficbroadcast.R;

/**
 * 路况评论列表适配器
 * 
 * @author daizhq
 * 
 * @date 2014.12.01
 */
public class TrafficReviewAdapter extends BaseAdapter {

	public List<Traffic_ReviewItem_Entity> dates;
	private Context context;
	private LayoutInflater MyInflater;

	public TrafficReviewAdapter(Context context, List<Traffic_ReviewItem_Entity> list_review) {
		this.context = context;
		this.dates = list_review;
		this.MyInflater = LayoutInflater.from(this.context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		if(dates != null){
			return dates.size();
		}else{
			return 0;
		}
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dates.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = MyInflater.inflate(R.layout.item_review, null, false);
			viewHolder = new ViewHolder();
			viewHolder.phone = (TextView) convertView
					.findViewById(R.id.tv_phone);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.tv_content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.phone.setText(dates.get(position).getMobile() + ":");
		viewHolder.content.setText(dates.get(position).getContent());
		return convertView;
	}

	class ViewHolder {
		// 用户
		TextView phone;
		// 内容
		TextView content;
	}

}
