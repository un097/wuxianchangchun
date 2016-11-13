package cn.ffcs.surfingscene.road;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.activity.GlobaleyeBaseActivity;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.adapter.OftenBlockedAdapter;
import cn.ffcs.surfingscene.road.adapter.SearchAdapter;
import cn.ffcs.surfingscene.sqlite.SearchKey;
import cn.ffcs.surfingscene.sqlite.SearchKeyService;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.function.CameraList;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;
import com.umeng.analytics.MobclickAgent;

public class SearchResultActivity extends GlobaleyeBaseActivity implements OnClickListener,
		HttpCallBack<BaseResponse>, TextWatcher {
	private ListView road_search_lv;
	public String search_type = "1000,1024";
	private String collectType = "1024";// 收藏类型统一使用1024
	private OftenBlockedAdapter searchAdapter;
	private SearchAdapter searchKeyAdapter;
	private EditText road_search_ed;
	private Button road_search_btn;
	private String cityId;
	private String phone;
	private List<GlobalEyeEntity> geyelist = new ArrayList<GlobalEyeEntity>();
	private List<SearchKey> searchlist = new ArrayList<SearchKey>();
	private LinearLayout noSearchData;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.road_search_btn) {
			String puname = road_search_ed.getText().toString().trim();
			CameraList cameralist = new CameraList(this);
			if ("".equals(puname)) {
				CommonUtils.showToast(mActivity, "关键字不能为空", Toast.LENGTH_SHORT);
			} else {
				MobclickAgent.onEvent(mContext, "E_C_trafficVideo_searchClick");
				CommonUtils.hideKeyboard(mActivity);
				showProgressBar("搜索中");
				cameralist.searchCameraofName(cityId, search_type, puname, this,
						"/geye/findByPuname");
				SearchKeyService.getInstance(mContext).saveKey(puname);
			}
		}
	}

	@Override
	public void callBack(BaseResponse resp, String url) {
		geyelist = resp.getGeyes();
		if (geyelist != null && geyelist.size() > 0) {
			searchAdapter = new OftenBlockedAdapter(mActivity, phone, collectType);
			searchAdapter.setData(geyelist);
			searchAdapter.setHaveCollectAnim(false);
			road_search_lv.setAdapter(searchAdapter);
			noSearchData.setVisibility(View.GONE);
		} else {
			noSearchData.setVisibility(View.VISIBLE);
		}
		hideProgressBar();
	}

	public void showProgressBar(String message) {
		LoadingDialog.getDialog(mActivity).setMessage(message).show();
	}

	public void hideProgressBar() {
		LoadingDialog.getDialog(mActivity).cancel();
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_road_search;
	}

	@Override
	protected void initData() {
		cityId = getIntent().getStringExtra(Key.K_AREA_CODE);
		phone = getIntent().getStringExtra(Key.K_PHONE_NUMBER);
		String search_content = getIntent().getStringExtra(Key.K_SEARCH_CONTENT);
		if (!StringUtil.isEmpty(cityId) && !StringUtil.isEmpty(search_content)) {
			road_search_ed.setText(search_content);
			road_search_btn.performClick();
		}
		getSearchHistory();
	}

	class OnListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			if (parent.getAdapter() instanceof OftenBlockedAdapter) {
				GlobalEyeEntity entity = (GlobalEyeEntity) parent.getAdapter().getItem(position);
				Intent intent = new Intent(SearchResultActivity.this, RoadPlayActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.glo_search));
				VideoPlayerTool.startRoadVideo(mActivity, entity, intent);
			} else if (parent.getAdapter() instanceof SearchAdapter) {
				SearchKey searchKey = (SearchKey) parent.getAdapter().getItem(position);
				road_search_ed.setText(searchKey.keyWord);
				road_search_btn.performClick();
			}
		}
	}

	/**
	 * 获取搜索历史
	 */
	private void getSearchHistory() {
		searchlist = SearchKeyService.getInstance(mContext).getSearchKey();
		searchKeyAdapter = new SearchAdapter(mContext);
		searchKeyAdapter.setData(searchlist);
		road_search_lv.setAdapter(searchKeyAdapter);
		searchAdapter = null;
	}

	@Override
	protected void initComponents() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.glo_search); // 设置标题
		road_search_lv = (ListView) findViewById(R.id.road_search_lv);
		road_search_ed = (EditText) findViewById(R.id.road_search_edit);
		road_search_ed.addTextChangedListener(this);
		road_search_btn = (Button) findViewById(R.id.road_search_btn);
		noSearchData = (LinearLayout) findViewById(R.id.no_search_data);
		road_search_btn.setOnClickListener(this);
		road_search_lv.setOnItemClickListener(new OnListItemClickListener());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		CommonUtils.showKeyboard(mActivity, road_search_ed);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (StringUtil.isEmpty(s.toString())) {
			getSearchHistory();
			noSearchData.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (searchAdapter != null) {
			searchAdapter.notifyDataSetChanged();
		}
	}
}
