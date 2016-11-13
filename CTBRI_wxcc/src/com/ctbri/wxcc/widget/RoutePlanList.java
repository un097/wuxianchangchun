package com.ctbri.wxcc.widget;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKRoutePlan;
import com.baidu.mapapi.search.MKTransitRoutePlan;
import com.ctbri.comm.util.DialogUtils;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.ObjectAdapter;
import com.ctbri.wxcc.community.Watcher;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;
import com.ctbri.wxcc.widget.RoutePlanFragment.RouteType;

public class RoutePlanList extends BaseFragment {
	
	private RouteType planType;
	private PlanAdapter listAdapter;
	private boolean showLoading= false;
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		if(activity_ instanceof WatcherManagerFactory){
			watchManager = ((WatcherManagerFactory)activity_).getManager();
			watchManager.addWatcher(new PlanUpdateWatcher());
		}
	}
	public static RoutePlanList newInstance(RouteType type){
		RoutePlanList fragment = new RoutePlanList();
		Bundle args = new Bundle();
		args.putSerializable ("planType", type);
		fragment.setArguments(args);
		return fragment;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		planType = (RouteType)getSerialzeable("planType");
	}
	private WatcherManager watchManager;
	private ListView list;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		list = (ListView)inflater.inflate(R.layout.common_route_plan_list, null);
		list.setOnItemClickListener(new ItemClickImpl());
		return list;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		listAdapter = new PlanAdapter(activity, null);
		list.setAdapter(listAdapter);
	}
	
	private void showLoading(){
		DialogUtils.showLoading(getFragmentManager());
	}
	private void hideLoading(){
		DialogUtils.hideLoading(getFragmentManager());
	}
	
	protected boolean isEnabledAnalytics() {
		return false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}
	
	class PlanAdapter extends ObjectAdapter<Object>{

		public PlanAdapter(Activity activity, List<Object> data_) {
			super(activity, data_);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Object route = getItem(position);
			
			Holder hold = null;
			if(convertView==null){
				convertView = inflater.inflate(R.layout.common_route_paln_list_item, null);
				hold = new Holder();
				convertView.setTag(hold);
				hold.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				hold.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
				hold.tv_time =(TextView) convertView.findViewById(R.id.tv_time);
				hold.tv_walk_distance = (TextView) convertView.findViewById(R.id.tv_walk_distance);
			}else{
				hold = (Holder) convertView.getTag();
			}
			if(route instanceof MKRoutePlan){
				fillData((MKRoutePlan) route, hold) ;
			}else{
				fillData((MKTransitRoutePlan) route, hold) ;
			}
			
			
			return convertView;
		}
		private String getSubStep(MKRoutePlan plan){
			String tip = plan.getRoute(0).getTip();
			tip =  tip==null ? "" : tip;
			return tip.replaceAll("_", "-");
		}
		private void fillData(MKRoutePlan route, Holder hold){
//			getString(R.string.tip_plan_index,1)
			hold.tv_name.setText(getSubStep(route));
			hold.tv_time.setText(_Utils.getTime(route.getTime()));
			hold.tv_distance.setText(_Utils.getDistance(route.getDistance()));
		}
		
		private void fillData(MKTransitRoutePlan route, Holder hold){
			hold.tv_name.setText(route.getContent().replaceAll("_", "-"));
			hold.tv_distance.setText( _Utils.getDistance(route.getDistance()));
			hold.tv_time.setText(_Utils.getTime(route.getTime()));
			hold.tv_walk_distance.setText( getString(R.string.tip_walk_distance, _Utils.getDistance(calcWalkInstance(route))) );
		}
		private int calcWalkInstance(MKTransitRoutePlan route){
			int c = route.getNumRoute();
			int count = 0;
			for(int i=0; i < c; i++)
					count+= route.getRoute(i).getDistance();
			return count;
		}
	}
	class Holder{
		TextView tv_name;
		TextView tv_distance;
		TextView tv_time;
		TextView tv_walk_distance;
	}
	
	class ItemClickImpl implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			Object obj = listAdapter.getItem(position);
			
			LocateNavVersion.route_type = planType;
			LocateNavVersion.route_plan = obj;
			Intent toNavPlan = new Intent(activity, LocateRouteVersion.class);
			startActivity(toNavPlan);
			
//			LocateNavVersion locateActivity = (LocateNavVersion)activity;
//			switch(planType){
//			case Driver:
//			case Walk:
//				locateActivity.updateRoutePlan(planType, (MKRoutePlan)obj);
//				break;
//			case Transite:
//				locateActivity.updateRoutePlan(planType, (MKTransitRoutePlan)obj);
//				break;
//			}
		}
		
	}
	
	class PlanUpdateWatcher implements Watcher{

		@Override
		public void trigger(Object obj) {
			System.out.println("回填数据 "+ planType.name() + "  [data] = " + obj);
			//如果返回的是异常代码，则隐藏 loading
			if(Integer.class.isInstance(obj)){
				hideLoading();
				Fragment fragment = getParentFragment();
				((RoutePlanFragment)fragment).cancelInit(planType);
				return ;
			}
			
			listAdapter.clearData();
			listAdapter.addAll((List<Object>)obj);
			listAdapter.notifyDataSetChanged();
			hideLoading();
		}

		@Override
		public int getType() {
			return planType.ordinal();
		}
		
	}

}
