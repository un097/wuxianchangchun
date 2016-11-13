package com.ctbri.wxcc.travel;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.entity.TravelRaidersBean;
import com.ctbri.wxcc.entity.TravelRaidersBean.Raiders;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TravelListFragment extends CommonList<TravelRaidersBean, TravelRaidersBean.Raiders> {
	public static final String KEY_TYPEID = "type_id";
	
	private LayoutInflater inflater;
	
	public static TravelListFragment newInstance(int typeId){
		TravelListFragment fragment = new TravelListFragment();
		Bundle args = new Bundle();
		args.putInt(KEY_TYPEID, typeId);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	private int getTitle(int menu_id){
		int titleId = 0;
		switch(menu_id){
		case TravelMainFragment.MENU_FOOD:
			titleId = R.string.travel_menu_title_food;
			break;
		case TravelMainFragment.MENU_LOCAL:
			titleId = R.string.travel_menu_title_locale;
			break;
		case TravelMainFragment.MENU_PARTY:
			titleId = R.string.travel_menu_title_party;
			break;
		case TravelMainFragment.MENU_TRAVEL:
			titleId = R.string.travel_menu_title_spot;
			break;
		}
		return titleId;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		((TextView)v.findViewById(R.id.action_bar_title)).setText(getTitle(type_id));
		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new FinishClickListener());
		
		return v;
	}
	private int type_id;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		Bundle args = getArguments();
		type_id =  args==null?-1 : args.getInt(KEY_TYPEID, -1);
	}
	@Override
	protected String getListUrl() {
		return Constants.METHOD_TRAVEL_CONTENT_LIST + "?type=" + type_id;
	}

	@Override
	protected Class<TravelRaidersBean> getGsonClass() {
		return TravelRaidersBean.class;
	}

	@Override
	protected List<Raiders> getEntitys(TravelRaidersBean bean) {
		return bean.getData().getTravel_list();
	}

	@Override
	protected boolean isEnd(TravelRaidersBean bean) {
		return bean.getData().getIs_end()==0;
	}

	@Override
	protected boolean initHeaderDetail(LoadMorePTRListView lv_list,
			LayoutInflater inflater) {
		this.inflater = inflater;
		return false;
	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent, Raiders data, ImageLoader imgloader,
			DisplayImageOptions dio) {
		TravelCommonHolder tch = null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.travel_common_list_item, parent, false);
			tch = new TravelCommonHolder();
			convertView.setTag(tch);
			
			tch.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
			//星级 标签只显示在 旅游线路类别中
			tch.tv_tag = (TextView)convertView.findViewById(R.id.tv_tag);
			
			if(type_id== TravelMainFragment.MENU_TRAVEL)
				tch.tv_tag.setVisibility(View.VISIBLE);
			
			tch.tv_subtitle	= (TextView)convertView.findViewById(R.id.tv_subtitle);
			tch.iv_img = (ImageView)convertView.findViewById(R.id.iv_travel_common_list_img);
			tch.iv_recommend = (ImageView)convertView.findViewById(R.id.iv_recommend);
			
		}else{
			tch = (TravelCommonHolder)convertView.getTag();
		}
		tch.tv_subtitle.setText(data.getSubtitle());
		tch.tv_title.setText(data.getTitle());
		tch.tv_tag.setText(data.getTag());
		
		tch.iv_recommend.setVisibility(data.getIs_recommend()==0 ? View.VISIBLE : View.GONE);
		imgloader.displayImage(data.getPic_url().trim(), tch.iv_img, dio);
		
		return convertView;
	}

	class TravelCommonHolder{
		TextView tv_title, tv_subtitle,tv_tag;
		ImageView iv_img, iv_recommend;
		
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position,
			long id, Raiders entity) {
		String title = entity.getTitle();
		Intent it = new Intent(activity, TravelDetailActivity.class);
		it.putExtra(TravelContentDetail.KEY_DETAIL_ID, entity.getTravel_id());
		it.putExtra(KEY_TYPEID, type_id);
		it.putExtra("title", title);
		startActivity(it);
		
		postClickEvent(Constants_Community.ITEM_EVENT_IDS[type_id], Constants_Community.EVENT_PARAMS[type_id], title);
	}

	@Override
	protected String getAnalyticsTitle() {
		return "travel_content_list";
	}
}
