package cn.ffcs.surfingscene.activity;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.adapter.BannerListViewAdapter;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

import com.ffcs.surfingscene.entity.ActionEntity;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.function.LandscapeList;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>Title:  广告里面的景点列表        </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-25             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class GloBannerListActivity extends GlobaleyeBaseActivity {
	private TextView mBannerEmptyTip;
	private BannerListViewAdapter mBannerAdapter;
	private ListView mBannerList;
	private ActionEntity entity;
	private List<GlobalEyeEntity> globalEyeList = new ArrayList<GlobalEyeEntity>();
	private String title;// 标题
	private String tyjxCode;// 天翼景象编号
	private int actionId;// 景点编号
	private LandscapeList landScapeList;
	private LoadingBar loadingBar;

	@Override
	protected void initComponents() {
		loadingBar = (LoadingBar) findViewById(R.id.loading_bar);
		mBannerEmptyTip = (TextView) findViewById(R.id.glo_banner_empty);
		mBannerList = (ListView) findViewById(R.id.glo_banner_listview);
		mBannerAdapter = new BannerListViewAdapter(mActivity);
		mBannerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				GlobalEyeEntity entity = globalEyeList.get(position);
				if (entity != null) {
					VideoPlayerTool.playVideo(mActivity, entity);
				}
			}
		});
	}

	@Override
	protected void initData() {
		loadingBar.setVisibility(View.VISIBLE);
		entity = (ActionEntity) getIntent().getSerializableExtra(Key.K_BANNER_LIST);
		landScapeList = new LandscapeList(mContext);
		if (entity != null) {
			title = entity.getActionName();
			tyjxCode = entity.getCityId();
			actionId = entity.getActionId().intValue();
			if (!StringUtil.isEmpty(tyjxCode)) {
				if (CommonUtils.isNetConnectionAvailable(mContext)) {
					loadData(tyjxCode, actionId);
				} else {
					CommonUtils.showToast(mActivity, R.string.glo_net_work_error,
							Toast.LENGTH_SHORT);
				}
			}
		} else {
			loadingBar.setVisibility(View.GONE);
			mBannerEmptyTip.setVisibility(View.VISIBLE);
		}
		if (StringUtil.isEmpty(title)) {
			title = getString(R.string.glo_app_title);
		}
		TopUtil.updateTitle(mActivity, R.id.top_title, title);
	}

	/**
	 * 获取数据
	 * @param cityId
	 * @param actionId
	 */
	private void loadData(String cityId, int actionId) {
		landScapeList.getActionEyeList(cityId, actionId, new GlobalEyeListCallback());
	}

	class GlobalEyeListCallback implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse response, String arg1) {
			loadingBar.setVisibility(View.GONE);
			if (response != null && "1".equals(response.getReturnCode())) {
				globalEyeList = response.getGeyes();
				refresh(globalEyeList);
			} else {
				CommonUtils
						.showToast(mActivity, R.string.glo_currentcity_error, Toast.LENGTH_SHORT);
			}
		}

	}

	/**
	 * 刷新列表
	 * @param list
	 */
	private void refresh(List<GlobalEyeEntity> list) {
		if (list != null) {
			if (list.size() == 0) {
				mBannerEmptyTip.setVisibility(View.VISIBLE);
			} else {
				mBannerEmptyTip.setVisibility(View.GONE);
			}
			mBannerAdapter.setData(list);
			mBannerList.setAdapter(mBannerAdapter);
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_banner;
	}

	@Override
	protected Class<?> getResouceClass() {
		return null;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
