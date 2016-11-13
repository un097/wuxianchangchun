package cn.ffcs.surfingscene.activity;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.adapter.ChooseCityAdapter;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.AreaMgr;
import cn.ffcs.surfingscene.sqlite.AreaList;
import cn.ffcs.ui.tools.TopUtil;

public class ChooseCityActivity extends GlobaleyeBaseActivity {
	private String type;
	private String parentCode;
	private ListView listview;
	private ChooseCityAdapter adapter;
	private List<AreaList> list;
//	private int selectIndex = 0;
//	private ImageView sure;

	@Override
	protected void initComponents() {
		type = getIntent().getStringExtra(Key.K_CITY_TYPE);
		listview = (ListView) this.findViewById(R.id.road_choose_lv);
//		sure = (ImageView) findViewById(R.id.top_right);
//		sure.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				try {
//					AreaList entity = (AreaList) adapter.getItem(selectIndex);
//					Intent i = new Intent();
//					i.putExtra(Key.K_RETURN_ENTITY, (Serializable) entity);
//					setResult(RESULT_OK, i);
//					finish();
//				} catch (Exception e) {
//					
//				}
//			}
//		});
	}

	@Override
	protected void initData() {
		parentCode = getIntent().getStringExtra(Key.K_PARENT_CODE);
		TopUtil.updateTitle(this, R.id.top_title, type); // 设置标题	  
		//TopUtil.updateLeftTitle(this, R.id.btn_return, R.string.traffic);
//		TopUtil.updateRight(this, R.id.top_right, R.drawable.choose_city_btn_sure);
		setListView();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				selectIndex = position;
				AreaList entity = (AreaList) adapter.getItem(position);
				Intent i = new Intent();
				i.putExtra(Key.K_RETURN_ENTITY, (Serializable) entity);
				setResult(RESULT_OK, i);
				finish();
			}
		});
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_choose_city;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	private void setListView() {
		if (parentCode.equals("0")) {
			list = AreaMgr.getInstance().getFirstAreaList(mContext);
		} else if (type.equals(getString(R.string.glo_choose_city))) {
			list = AreaMgr.getInstance().getSecondAreaList(mContext, parentCode);
		} else if (type.equals(getString(R.string.glo_choose_county))) {
			list = AreaMgr.getInstance().getThridAreaList(mContext, parentCode);
		}
		adapter = new ChooseCityAdapter(mContext, list);
		listview.setAdapter(adapter);
	}
}
