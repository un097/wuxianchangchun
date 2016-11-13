package cn.ffcs.surfingscene.activity;

import java.util.ArrayList;
import java.util.List;

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
import cn.ffcs.wisdom.tools.StringUtil;

import com.ffcs.surfingscene.entity.AreaEntity;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>Title: 某个省份的城市列表         </p>
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
public class CityListActivity extends GlobaleyeBaseActivity {
	private ListView mCityListView;
	private List<AreaEntity> mAreaEntityList = new ArrayList<AreaEntity>();
	private CityListAdapter mCityListAdapter;
	private LoadingBar loadingBar;

	@Override
	protected void initComponents() {
		loadingBar = (LoadingBar) findViewById(R.id.loading_bar);
		loadingBar.setVisibility(View.VISIBLE);
		mCityListView = (ListView) findViewById(R.id.glo_citychange_listview);
		mCityListView.setOnItemClickListener(new OnCityItemClickListener());
		mCityListAdapter = new CityListAdapter(mContext);
	}

	class OnCityItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			AreaEntity entity = mAreaEntityList.get(position);
			if (entity != null) {
				String cityCode = entity.getAreaCode();
				String cityName = entity.getAreaName();
				GloCityMgr.getInstance().setCityName(cityName);
				GloCityMgr.getInstance().refreshTyjxCode(cityCode);
				finish();
			}
		}

	}

	@Override
	protected void initData() {
		String title = getIntent().getStringExtra(Key.K_AREA_NAME);
		String areaCode = getIntent().getStringExtra(Key.K_AREA_CODE);
		if (StringUtil.isEmpty(title)) {
			title = getString(R.string.glo_citychange_title);
		}
		TopUtil.updateTitle(mActivity, R.id.top_title, title);
		if (!StringUtil.isEmpty(areaCode) && CommonUtils.isNetConnectionAvailable(mContext)) {
			CityListMgr.getInstance(mContext).getListOfCityToProvince(Config.TYPE_CITY, areaCode,
					new GetCityToProvinceCallback(), Config.METHOD_CITY_PRO_CITY);
		} else {
			loadingBar.setVisibility(View.GONE);
			CommonUtils.showToast(mActivity, R.string.glo_net_work_error, Toast.LENGTH_SHORT);
		}
	}

	class GetCityToProvinceCallback implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse response, String arg1) {
			loadingBar.setVisibility(View.GONE);
			if (response != null && "1".equals(response.getReturnCode())) {
				mAreaEntityList = response.getArea();
				refresh(mAreaEntityList);
			} else {
				CommonUtils.showToast(mActivity, R.string.glo_citychange_city_fail,
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
			mCityListView.setAdapter(mCityListAdapter);
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
