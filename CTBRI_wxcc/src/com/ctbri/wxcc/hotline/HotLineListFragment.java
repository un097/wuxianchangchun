package com.ctbri.wxcc.hotline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.ctbri.comm.util.DialogUtils;
import com.ctbri.wwcc.greenrobot.HotLine;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.CurrentBean;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.MyBaseProtocol;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.db.DBHelper;
import com.ctbri.wxcc.hotline.HotLineFragment.MyAdapter;
import com.ctbri.wxcc.widget.NoScrollViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.umeng.analytics.MobclickAgent;
import com.wookii.tools.comm.DeviceTool;
import com.wookii.tools.net.WookiiHttpPost;
import com.wookii.widget.ryg.expandable.ui.PinnedHeaderExpandableListView;
import com.wookii.widget.ryg.expandable.ui.PinnedHeaderExpandableListView.OnHeaderUpdateListener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.LayoutParams;
import android.widget.ExpandableListView.OnChildClickListener;

public class HotLineListFragment extends Fragment implements OnHeaderUpdateListener{

	private static final String TAG = "HotLineFragment";
	private Activity context;
	private DBHelper dbHelper;
	protected HotLineListAdapter hotLineAdapter;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			HashMap<String, Object> data = (HashMap<String, Object>) msg.obj;
			ArrayList<HotLine> group = (ArrayList<HotLine>) data.get("group");
			ArrayList<List<HotLine>> childList = (ArrayList<List<HotLine>>) data.get("child");
			hotLineAdapter = new HotLineListAdapter(context, group, childList);
			hotLineAdapter.setOnCollectionChangeListener(listener);
			expandablelist.setAdapter(hotLineAdapter);
			// 展开所有group
		    for (int i = 0, count = expandablelist.getCount(); i < count; i++) {
		        expandablelist.expandGroup(i);
		    }
		    
		    switch (msg.what) {
			case 0x123:
				requestData();
				break;
			case 0x124:
				DialogUtils.hideLoading(getFragmentManager());
				break;
			default:
				break;
			}
		    
		};
	};
	private PinnedHeaderExpandableListView expandablelist;
	private NoScrollViewPager pager;
	private CollectionChangeListener listener;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		if (dbHelper == null) {
			dbHelper = DBHelper.getInstance(activity);
		}
		this.context = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View hotline = inflater.inflate(R.layout.fragment_hotline_list, null);
		expandablelist = (PinnedHeaderExpandableListView)hotline.findViewById(R.id.hotline_expandablelist);
		expandablelist.setOnHeaderUpdateListener(this);
		expandablelist.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				HotLineListAdapter adapter = (HotLineListAdapter)parent.getExpandableListAdapter();
				HotLine child = (HotLine)adapter.getChild(groupPosition, childPosition);
				Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
				Uri.parse("tel:" + child.getTel()));
				startActivity(phoneIntent);
				return false;
			}
		});
		new Thread(){
			public void run() {
				HashMap<String, Object> data = dbHelper.reload();
				Message msg = Message.obtain();
				msg.obj = data;
				msg.what = 0x123;
				handler.sendMessage(msg);
			}
		}.start();
		return hotline;
	}
	
	
	private void requestData() {
		if(!DeviceTool.isNetworkAvailable(context)) {
			Toast.makeText(context, "网络不可用！", Toast.LENGTH_SHORT).show();
			return;
		}
		DialogUtils.showLoading(getFragmentManager());
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("md5", MessageEditor
				.getHotLineMd5(context)));
		pairs.add(new BasicNameValuePair("user_id", MessageEditor
				.getUserId(context)));
		new MyBaseProtocol(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String json = (String) msg.obj;
				Gson gson = new Gson();
				try {
					CurrentBean currentMessage = gson.fromJson(json,
							CurrentBean.class);
					if (currentMessage.getRet() == 0) {
						saveData(json);
					} else {
						DialogUtils.hideLoading(getFragmentManager());
					}
					MobclickAgent.onEvent(context, "E_C_HotLineFragment_hotLineClick");
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				}
			}
		}, new WookiiHttpPost(), Constants.METHOD_HOT_LINE).startInvoke(
				new JsonWookiiHttpContent(pairs), 0);
	}

	protected void saveData(final String json) {
		new Thread() {
			@Override
			public void run() {
				
				Gson gson = new Gson();
				HotLineBean bean = gson.fromJson(json, HotLineBean.class);
				if(bean.getData() != null){//更新本地数据，保存md5
					MessageEditor.setHotLineMd5(context, json);
					dbHelper.clearAll();
					List<HotLine> items = bean.getData().getTel_items();
					if(items != null && items.size() != 0) {
						for (HotLine hotLine : items) {
							try {
								dbHelper.addTel(hotLine);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						HashMap<String, Object> data = dbHelper.reload();
						Message msg = Message.obtain();
						msg.obj = data;
						msg.what = 0x124;
						handler.sendMessage(msg);
					}
				}
			}
		}.start();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(context);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(context);
	}
	
	@Override
	public View getPinnedHeader() {
		// TODO Auto-generated method stub
		View headerView = (ViewGroup) context.getLayoutInflater().inflate(
				R.layout.hotline_list_group, null);
		headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		return headerView;
	}

	@Override
	public void updatePinnedHeader(View headerView, int firstVisibleGroupPos) {
		// TODO Auto-generated method stub
		if(hotLineAdapter != null) {
			HotLine firstVisibleGroup = (HotLine) hotLineAdapter.getGroup(firstVisibleGroupPos);
	        TextView textView = (TextView) headerView.findViewById(R.id.hotline_list_group_name);
	        textView.setText(firstVisibleGroup.getGroup_name());
		}
	}

	public void changeCollectionOutside(int status, HotLine hotLine) {
		hotLineAdapter.configMyCollectionView(status, hotLine);
		hotLineAdapter.notifyDataSetChanged();
	}

	public void setOnCollectionChangeListener(
			CollectionChangeListener listener) {
		this.listener = listener;
	}
}
