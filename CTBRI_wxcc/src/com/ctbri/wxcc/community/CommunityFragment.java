package com.ctbri.wxcc.community;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctbri.comm.util.DialogUtils;
import com.ctbri.comm.util._Utils;
import com.ctbri.comm.widget.PublishActivity;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.CommunityColumnBean;
import com.ctbri.wxcc.entity.CommunityColumnBean.Column;
import com.ctbri.wxcc.entity.CommunityColumnBean.CommunityData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.viewpagerindicator.TabPageIndicator;
import com.wookii.tools.comm.LogS;

public class CommunityFragment extends BaseFragment {

	private static final String EMPTY_NAME = "";

	TabPageIndicator tabIndicator;
	ViewPager vpContainer;
	ColumnAdapter1 vpAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_community, container, false);

		vpContainer = (ViewPager) v.findViewById(R.id.vp_community);
		tabIndicator = (TabPageIndicator) v
				.findViewById(R.id.tab_page_indicator);
		vpAdapter = new ColumnAdapter1(getChildFragmentManager());
		// vpContainer.setOffscreenPageLimit(4);
		vpContainer.setAdapter(vpAdapter);
		tabIndicator.setViewPager(vpContainer);

		((TextView) v.findViewById(R.id.action_bar_title))
				.setText(R.string.title_community);
		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(
				new FinishClickListener());

		v.findViewById(R.id.btn_new_community).setOnClickListener(
				new NewCommunityListener());
		getColumns();

		return v;
	}

	/**
	 * 获取爆笑社区 分类列表
	 */
	private void getColumns() {
		DialogUtils.showLoading(getFragmentManager());
		request(Constants.METHOD_COMMUNITY_COLUMN_LIST, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				loadColumns(json);
				DialogUtils.hideLoading(getFragmentManager());
			}

			@Override
			public void requestFailed(int errorCode) {
				DialogUtils.hideLoading(getFragmentManager());
			}
		});
	}

	/**
	 * 加载取回 爆料社区 分类列表
	 * 
	 * @param json
	 */
	private void loadColumns(String json) {
		Gson gson = new Gson();
		CommunityColumnBean data = gson.fromJson(json,
				new TypeToken<CommunityColumnBean>() {
				}.getType());
		vpAdapter.setData(data.getData());
		vpAdapter.notifyDataSetChanged();
		tabIndicator.notifyDataSetChanged();

	}

	class ColumnAdapter1 extends FragmentPagerAdapter {
		private CommunityData data;

		public CommunityData getData() {
			return data;
		}

		public void setData(CommunityData data) {
			this.data = data;
		}

		public ColumnAdapter1(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Bundle args = new Bundle();
			Column col = getColumnByPosition(position);

			args.putString(CommunityFragmentList.KEY_NAME, col.getColumn_name());
			args.putString(CommunityFragmentList.KEY_COLUMN_ID,
					col.getColumn_id());
			LogS.i("createFragment", " create  =" + col.getColumn_name());
			return Fragment.instantiate(getActivity(),
					CommunityFragmentList.class.getName(), args);
		}

		private Column getColumnByPosition(int position) {
			if (data != null)
				return data.getColumns().get(position);
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Column col = getColumnByPosition(position);
			if (col != null)
				return col.getColumn_name();

			return null;
		}

		@Override
		public int getCount() {
			if (data != null)
				return data.getColumns().size();
			return 0;
		}

	}

	class ColumnAdapter extends FragmentStatePagerAdapter {

		private CommunityData data;

		public CommunityData getData() {
			return data;
		}

		public void setData(CommunityData data) {
			this.data = data;
		}

		public ColumnAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Bundle args = new Bundle();
			Column col = getColumnByPosition(position);

			args.putString(CommunityFragmentList.KEY_NAME, col.getColumn_name());
			args.putString(CommunityFragmentList.KEY_COLUMN_ID,
					col.getColumn_id());
			LogS.i("createFragment", " create  =" + col.getColumn_name());
			return Fragment.instantiate(getActivity(),
					CommunityFragmentList.class.getName(), args);
		}

		private Column getColumnByPosition(int position) {
			if (data != null)
				return data.getColumns().get(position);
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Column col = getColumnByPosition(position);
			if (col != null)
				return col.getColumn_name();

			return null;
		}

		@Override
		public int getCount() {
			if (data != null)
				return data.getColumns().size();
			return 0;
		}

	}

	class NewCommunityListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (_Utils.checkLoginAndLogin(activity)) {
				Intent it = new Intent(activity, PublishActivity.class);
				startActivity(it);
			}
		}

	}

	@Override
	protected String getAnalyticsTitle() {
		return "community_category_list";
	}
}
