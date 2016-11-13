package cn.ffcs.surfingscene.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.DataSetObserver;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.adapter.CityListAdapter;
import cn.ffcs.surfingscene.common.Config;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.CityListMgr;
import cn.ffcs.surfingscene.datamgr.GloCityMgr;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.ffcs.surfingscene.entity.AreaEntity;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>Title:省份列表          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-21             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ProvinceListActivity extends GlobaleyeBaseActivity {
	private ListView mProvinceListView;
	private CityListAdapter mCityListAdapter;
	private List<AreaEntity> mAreaEntityList = new ArrayList<AreaEntity>();
	private LoadingBar loadingBar;

	@Override
	protected void initComponents() {
		loadingBar = (LoadingBar) findViewById(R.id.loading_bar);
		loadingBar.setVisibility(View.VISIBLE);
		mProvinceListView = (ListView) findViewById(R.id.glo_citychange_listview);
		mProvinceListView.setOnItemClickListener(new OnProvinceItemClickListener());
		mCityListAdapter = new CityListAdapter(mContext);
	}

	class OnProvinceItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
			AreaEntity entity = mAreaEntityList.get(position);
			if (entity != null) {
				Intent intent = new Intent(mActivity, CityListActivity.class);
				String areaCode = entity.getAreaCode();
				String areaName = entity.getAreaName();
				intent.putExtra(Key.K_AREA_CODE, areaCode);
				intent.putExtra(Key.K_AREA_NAME, areaName);
				intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.glo_citychange_provincelist));
				startActivity(intent);
			}

		}
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.glo_citychange_title);
		if (CommonUtils.isNetConnectionAvailable(mContext)) {
			CityListMgr.getInstance(mContext).getListOfProvince(Config.TYPE_CITY,new GetListOfProvinceCallBack(),
					Config.METHOD_CITY_PROVINCE);
		} else {
			loadingBar.setVisibility(View.GONE);
			CommonUtils.showToast(mActivity, R.string.glo_net_work_error, Toast.LENGTH_SHORT);
		}
		registerDataSetObserver();
	}

	/**
	 * 注册观察者，关闭页面
	 */
	protected void registerDataSetObserver() {
		GloCityMgr.getInstance().registerDataSetObserver(new CityChangeObserver());
	}

	class CityChangeObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			boolean isSuccess = GloCityMgr.getInstance().isSuccess();
			if (isSuccess) {
				finish();
			}
		}
	}

	class GetListOfProvinceCallBack implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse response, String arg1) {
			loadingBar.setVisibility(View.GONE);
			if ("1".equals(response.getReturnCode())) {
				mAreaEntityList = response.getArea();
				refresh(mAreaEntityList);
			} else {
				CommonUtils.showToast(mActivity, R.string.glo_citychange_province_fail,
						Toast.LENGTH_SHORT);
			}
		}

	}

	/**
	 * 刷新数据
	 * @param list
	 */
	private void refresh(List<AreaEntity> list) {
		if (list != null && list.size() > 0) {
			mCityListAdapter.setData(list);
			mProvinceListView.setAdapter(mCityListAdapter);
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_citychange_list;
	}

	@Override
	protected Class<?> getResouceClass() {
		return null;
	}

}
