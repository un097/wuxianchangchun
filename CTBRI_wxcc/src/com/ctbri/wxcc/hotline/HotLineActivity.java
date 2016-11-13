package com.ctbri.wxcc.hotline;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.R;
import com.ctbri.wxcc.hotline.HotLineFragment.SearchViewChangeListener;

public class HotLineActivity extends FragmentActivity {

	private HotLineFragment hotLineFragment;
	private View leftBtn;
	private OnClickListener goList;
	private OnClickListener goBack;
	private ImageView searchView;
	private TextView title;
	private int theView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotline);
		hotLineFragment = new HotLineFragment();
		hotLineFragment.setOnSearchViewChangeListener(new SearchViewChangeListener(){

			@Override
			public void OnChange(int index) {
				// TODO Auto-generated method stub
				switch (index) {
				case HotLineFragment.SEARCH_VIEW:
					leftBtn.setOnClickListener(goList);
					searchView.setVisibility(View.GONE);
					break;
				case HotLineFragment.LIST_VIEW:
					leftBtn.setOnClickListener(goBack);
					searchView.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
				theView = index;
			}});
		
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_hotline, hotLineFragment).commit();
		goBack = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		};
		
		goList = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hotLineFragment.goList();
				title.setText("便民热线");
			}
		};
		
		leftBtn = findViewById(R.id.action_bar_left_btn);
		leftBtn.setOnClickListener(goBack);
		title = (TextView)findViewById(R.id.action_bar_title);
		title.setText("便民热线");
		searchView = (ImageView)findViewById(R.id.action_bar_right_btn);
		searchView.setImageResource(R.drawable.common_icon_search);
		searchView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hotLineFragment.goSearch();
				title.setText("搜索");
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(theView == HotLineFragment.SEARCH_VIEW) {
			hotLineFragment.goList();
			title.setText("便民热线");
		} else {
			finish();
		}
	}
}
