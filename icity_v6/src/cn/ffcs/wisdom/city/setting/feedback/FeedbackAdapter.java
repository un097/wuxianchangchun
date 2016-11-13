package cn.ffcs.wisdom.city.setting.feedback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.ffcs.wisdom.city.entity.FeedbackEntity.FeedbackItem;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 意见反馈列表适配器       </p>
 * <p>Description: 
 * 意见反馈列表适配器
 * </p>
 * <p>@author: xzw                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-7-11             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class FeedbackAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private DateFormat df;
	private List<FeedbackItem> mList;

	public FeedbackAdapter(Context context, List<FeedbackItem> list) {
		this.mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if (list == null) {
			mList = Collections.emptyList();
		} else {
			mList = list;
		}
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mList.get(pos);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int pos, View v, ViewGroup viewGroup) {
		if (v == null) {
			v = mInflater.inflate(R.layout.listview_item_feedback_conversations, null);
		}
		FeedbackItem item = mList.get(pos);

		TextView feedbackTv = (TextView) v.findViewById(R.id.feedback);
		TextView feedbackDate = (TextView) v.findViewById(R.id.feedback_date);
		TextView feedbackReplyTv = (TextView) v.findViewById(R.id.feedback_reply);
        TextView feedbackReplyDate=(TextView) v.findViewById(R.id.feedback_reply_date);
		feedbackTv.setText(item.getFeedback());
		Date date;
		try {
			date = df.parse(item.getFeedbackTime());
			feedbackDate.setText(df.format(date));
		} catch (ParseException e) {
			Log.e(e.getMessage(), e);
		}
		if (!StringUtil.isEmpty(item.getFeedbackReply())) {
			try {
				date = df.parse(item.getFeedbackReplyTime());
				feedbackReplyTv.setText(item.getFeedbackReply());
				feedbackReplyDate.setText(df.format(date));
			} catch (ParseException e) {
				Log.e(e.getMessage(), e);
			}
		}else{
			feedbackReplyTv.setText(mContext.getString(R.string.feedback_default_reply));
		}
		return v;
	}
}
