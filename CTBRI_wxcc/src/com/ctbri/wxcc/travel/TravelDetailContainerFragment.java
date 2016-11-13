package com.ctbri.wxcc.travel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.Watcher;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;
import com.ctbri.wxcc.community.BaseFragment.FinishClickListener;

public class TravelDetailContainerFragment extends BaseFragment {

	private String detail_id, title;
	private int type_id = -1;
	private TextView tv_action_title;
	private ImageView right_btn;
	private WatcherManager watcher;
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		if(activity_ instanceof WatcherManagerFactory){
			watcher = ((WatcherManagerFactory) activity_).getManager();
		}
	}
	public static TravelDetailContainerFragment newInstance(String detail_id, int type_id, String title) {
		TravelDetailContainerFragment detailFragment = new TravelDetailContainerFragment();
		Bundle args = new Bundle();
		args.putString(TravelContentDetail.KEY_DETAIL_ID, detail_id);
		args.putInt(TravelListFragment.KEY_TYPEID , type_id);
		args.putString("title", title);
		detailFragment.setArguments(args);
		return detailFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args!=null){
			detail_id = args.getString(TravelContentDetail.KEY_DETAIL_ID, "");;
			type_id = args.getInt(TravelListFragment.KEY_TYPEID, -1);
			title = args.getString("title","");
		}
	}
	private Fragment getDetailFragment(){
		if(type_id==-1){
			return TravelContentDetail.newInstance(detail_id);
		}else
			return TravelCommonDetail.newInstance(detail_id, type_id);
			
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.travel_detail_container, container,
					false);
		tv_action_title = ((TextView)v.findViewById(R.id.action_bar_title));
		tv_action_title.setText(title);
		
		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new FinishClickListener());
		right_btn = (ImageView)v.findViewById(R.id.action_bar_right_btn);
		right_btn.setImageResource(R.drawable.share_button_selector);
		right_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(watcher!=null)
					watcher.trigger(Watcher.TYPE_SHARE , null);
			}
		});

		
		getChildFragmentManager().beginTransaction().replace(R.id.ll_detail, getDetailFragment(), "detail").commit();
		return v;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

}
