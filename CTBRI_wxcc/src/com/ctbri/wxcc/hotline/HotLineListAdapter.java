package com.ctbri.wxcc.hotline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.wwcc.greenrobot.HotLine;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.MyBaseProtocol;
import com.ctbri.wxcc.R;
import com.wookii.tools.net.WookiiHttpPost;

public class HotLineListAdapter extends BaseExpandableListAdapter {

	private static final String MAIN_ID = "00000000";
	public static final int COLLECTION = 0;
	public static final int UN_COLLECTION = 1;
	private static final String TAG = "HotLineAdapter";
	public static final String SPLIT_CHAR = "-";
	private ArrayList<HotLine> group;
	private ArrayList<List<HotLine>> childList;
	private Activity context;
	private LayoutInflater inflater;
	private CollectionChangeListener listener;
	public static HashMap<Integer, Boolean> collcetionMap = new HashMap<Integer, Boolean>();
	public HotLineListAdapter(Activity context, ArrayList<HotLine> group,
			ArrayList<List<HotLine>> childList) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.group = group;
		this.childList = childList;
		this.inflater = LayoutInflater.from(context);
		
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupPosition).get(childPosition).getId();
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildHolder childHolder = null;
		if (convertView == null) {
			childHolder = new ChildHolder();
			convertView = inflater.inflate(R.layout.hotline_list_child, null);

			childHolder.textName = (TextView) convertView
					.findViewById(R.id.hotline_name);
			childHolder.telDes = (TextView) convertView.findViewById(R.id.hotline_tel_des);
			childHolder.tel = (TextView) convertView
					.findViewById(R.id.hotline_tel);
			childHolder.checkBox = (CheckBox) convertView
				.findViewById(R.id.hotline_is_collection);
			childHolder.line = (ImageView)convertView.findViewById(R.id.hotline_line);
			convertView.setTag(childHolder);
		} else {
			childHolder = (ChildHolder) convertView.getTag();
		}

		
		HotLine hotLine = (HotLine) getChild(groupPosition,
				childPosition);
		final String item_id = hotLine.getItem_id();
		boolean status = false;
		if(hotLine.getStatus() == COLLECTION) {
			status = true;
		}
		collcetionMap.put(childPosition, status);
		
		childHolder.checkBox.setChecked(collcetionMap.get(childPosition));
		childHolder.checkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean checked = ((CheckBox)v).isChecked();
				collcetionMap.put(childPosition, checked);
				HotLine hotline = childList.get(groupPosition).get(childPosition);
				int status = UN_COLLECTION;
				if(checked){
					status = COLLECTION;
				}
				animator(status);
				postStatus(status, item_id);
				hotline.setStatus(status);
				configMyCollectionView(status, hotline);
				notifyDataSetChanged();
				if(listener != null) {
					listener.onChange(status, null);
				}
			}
		});
		childHolder.textName.setText(hotLine.getNum_name());
		childHolder.telDes.setText(String.valueOf(hotLine.getTel_description()));
		String tel = hotLine.getTel();
		if(tel.contains(SPLIT_CHAR)) {
			String[] split = tel.split(SPLIT_CHAR);
			childHolder.tel.setText(split[1]);
		} else {
			childHolder.tel.setText(tel);
		}
		

		if(isLastChild) {
			childHolder.line.setVisibility(View.GONE);
		} else {
			childHolder.line.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	protected void animator(int status) {
		// TODO Auto-generated method stub
		String toastText = "";
		switch (status) {
		case UN_COLLECTION:
			toastText = "移出常用电话";
			break;
		case COLLECTION:
			toastText = "加入常用电话";
			break;
		default:
			break;
		}
		Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
	}

	protected void postStatus(int status, String item_id) {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("type", String.valueOf(status)));
		pairs.add(new BasicNameValuePair("number_id", String.valueOf(item_id)));
		pairs.add(new BasicNameValuePair("user_id", MessageEditor.getUserId(context)));
		new MyBaseProtocol(new Handler(){
			@Override
			public void handleMessage(Message msg) {
				
			}
		}, new WookiiHttpPost(), Constants.METHOD_HOT_LINE_COLLECTION)
		.startInvoke(new JsonWookiiHttpContent(pairs), 0);
	}

	public void configMyCollectionView(int status, HotLine hotline) {
		// TODO Auto-generated method stub
		boolean hasCollection = false;
		for(HotLine  item:group){
			if(MAIN_ID.equals(item.getGroup_id())) {
				hasCollection = true;
			}
		}
		if(!hasCollection) {
			group.add(0, new HotLine("0", null, null, null, null, MAIN_ID, "常用电话"));
			childList.add(0, new ArrayList<HotLine>());
		}
		
		if(status == COLLECTION) {
			childList.get(0).add(hotline);
		} else {
			String id = hotline.getItem_id();
			int removeIndex = 0;
			for (HotLine item : childList.get(0)) {
				if(!id.equals(item.getItem_id())) {
					removeIndex ++ ;
				} else {
					break;
				}
			}
			try {
				childList.get(0).remove(removeIndex);
				if(childList.get(0).size() == 0) {
					childList.remove(0);
					group.remove(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return group.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return group.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder groupHolder = null;
		if (convertView == null) {
			groupHolder = new GroupHolder();
			convertView = inflater.inflate(R.layout.hotline_list_group, null);
			groupHolder.textView = (TextView) convertView
					.findViewById(R.id.hotline_list_group_name);
			groupHolder.imageView = (ImageView) convertView
					.findViewById(R.id.image);
			convertView.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}

		groupHolder.textView.setText(((HotLine) getGroup(groupPosition))
				.getGroup_name());
		// if (isExpanded)// ture is Expanded or false is not isExpanded
		// groupHolder.imageView.setImageResource(R.drawable.expanded);
		// else
		// groupHolder.imageView.setImageResource(R.drawable.collapse);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	class GroupHolder {
		TextView textView;
		ImageView imageView;
	}

	class ChildHolder {
		public ImageView line;
		public CheckBox checkBox;
		TextView textName;
		TextView telDes;
		TextView tel;
	}

	public void setOnCollectionChangeListener(CollectionChangeListener listener) {
		this.listener = listener;
	}
}
