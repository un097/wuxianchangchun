package com.ctbri.wxcc.widget;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util.DialogUtils;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

public class RoutePlanFragment extends BaseFragment {
	private RouteType type;
	private RouteType[] planTypes;
	private boolean[] inits ;
	private String target;
	private TextView tv_start, tv_end;
	private ImageView iv_my_position, iv_target_position;
	private ViewPager vp_plan;
	private PageIndicator indicator;
	//标示是否交换起始位置
	private boolean isReverse = false;

	public static RoutePlanFragment newInstance(RouteType type, String targetLocation) {
		RoutePlanFragment fragment = new RoutePlanFragment();
		Bundle args = new Bundle();
		args.putSerializable("planType", type);
		args.putString("target", targetLocation);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = (RouteType) getSerialzeable("planType");
		target = getArgs("target");
	}

	/**
	 * 路线规划类型
	 * 
	 * @author yanyadi
	 * 
	 */
	public static enum RouteType {
		Walk(R.string.walk, R.drawable.walk_button_selector), Driver(R.string.driver, R.drawable.driver_button_selector), Transite(
				R.string.transite, R.drawable.transite_button_selector);
		RouteType(int res, int icon) {
			this.desc = res;
			this.icon = icon;
		}
		private int desc;
		private int icon;
		public int getIcon(){
			return icon;
		}
		public int getDesc() {
			return desc;
		}
	}

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.common_route_plan, container, false);
		vp_plan = (ViewPager) v.findViewById(R.id.vp_plan);
		indicator = (PageIndicator) v.findViewById(R.id.tab_page_indicator);
		indicator.setOnPageChangeListener(new PageChangeListener());
		
		tv_end = (TextView)v.findViewById(R.id.tv_end);
		tv_start =(TextView) v.findViewById(R.id.tv_start);
		tv_start.setText(R.string.tip_my_location);
		
		iv_my_position = (ImageView) v.findViewById(R.id.iv_position);
		iv_target_position = (ImageView) v.findViewById(R.id.iv_target_position);
		if(target != null)
			tv_end.setText(target);
		
		v.findViewById(R.id.btn_swap_locate).setOnClickListener(new SwapLocateListener());
		return v;
	}
	public void updatePlan(RouteType type){
		int index = getPlanIndexByType(type);
		vp_plan.setCurrentItem(index);
	}
	
	private int getPlanIndexByType(RouteType type){
		for(int i=0; i < planTypes.length;i++){
			if(planTypes[i]==type)
			{
				return i;
			}
		}
		return -1;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		planTypes = new RouteType[] {RouteType.Walk,RouteType.Transite, RouteType.Driver };
		inits = new boolean[]{false, false, false};

		vp_plan.setAdapter(new PlanAdapter(getChildFragmentManager(), planTypes));
		vp_plan.setOffscreenPageLimit(2);
		indicator.setViewPager(vp_plan);
		
		int index = getPlanIndexByType(type);
		if(index==0){
			queryData(index);
		}else{
			vp_plan.setCurrentItem(index);
		}
	}
	/**
	 * 在网络异常时，或者百度地图未返回相关数据时。设置初始化标志们为 false;
	 * @param type
	 */
	public void cancelInit(RouteType type){
		//返回的 type 类型，有问题。 百度 MKSearch 查询时，查询 驾车路线，返回公交线路异常。。。真TM操蛋。
		
		//取消指定类别类型的初始化状态
//		inits[ getPlanIndexByType(type) ] = false;;
		
		//取消所有类型的初始化状态
		for(int i=0;i < inits.length;i++)
			inits[i] = false;
	}
	
	private void queryData(int position){
		queryData(position, isReverse);
	}
	private void queryData(int position, boolean isReverse){
//		System.out.println("数据初始化");
//		System.out.println(planTypes[position].name());
		if(!inits[position])
		{
			//显示Loading, 在 RoutePlanList 中调用隐藏方法
			DialogUtils.showLoading(getChildFragmentManager());
			
			((LocateNavVersion)(activity)).routeSearch(planTypes[position] , isReverse);
			inits[position] = true;
		}
	}
	
	class PageChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			queryData(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			
		}
		
	}
	class SwapLocateListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			CharSequence tmpText = tv_end.getText();
			
			tv_end.setText(tv_start.getText());
			tv_start.setText(tmpText);
			
			Drawable target = iv_target_position.getDrawable();
			iv_target_position.setImageDrawable(iv_my_position.getDrawable());
			iv_my_position.setImageDrawable(target);
			
			int position = vp_plan.getCurrentItem();
			isReverse=!isReverse;
			
			cancelInit(type);
			queryData(position,isReverse);
		}
	}
	
	class PlanAdapter extends FragmentPagerAdapter implements IconPagerAdapter{
		private RouteType[] planType;

		public PlanAdapter(FragmentManager fm, RouteType[] type) {
			super(fm);
			this.planType = type;
		}

		@Override
		public Fragment getItem(int position) {
			
			return RoutePlanList.newInstance(planType[position]);
		}

		@Override
		public int getCount() {
			if (planType == null)
				return 0;
			return planType.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
//			return 
			return null;
		}

		@Override
		public int getIconResId(int index) {
			return planType[index].icon;
		}

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
