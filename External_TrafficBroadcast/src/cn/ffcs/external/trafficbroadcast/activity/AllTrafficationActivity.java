package cn.ffcs.external.trafficbroadcast.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.external.trafficbroadcast.adapter.AllTrafficationAdapter;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_AllList_Bo;
import cn.ffcs.external.trafficbroadcast.entity.TrafficInfo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_AllItem_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_AllList_Entity;
import cn.ffcs.widget.PullToRefreshBase.OnRefreshListener;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.widget.PullToRefreshListView;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.example.external_trafficbroadcast.R;

/**
 * 全部路况列表界面
 * 
 * @author daizhq
 * 
 * @date 2014.12.02
 * */
public class AllTrafficationActivity extends Activity implements
		OnClickListener {

	// 返回键
	private LinearLayout ll_back;
	// 跳转到地图页面
	private Button iv_tomap;

	// 搜索输入框
	private EditText et_input;
	// 搜索键
	private Button btn_search;
	// 搜索关键词
	private String searchStr;

	// 下拉列表
	private PullToRefreshListView mRoadPullListView;
	private ListView mRoadListView;
	//AllTrafficationAdapter!!!!!!!!!!!!!!!!!!!!!
	private AllTrafficationAdapter adapter;

	/**
	 * 是否是手动刷新 true:可手动刷新
	 */
	private boolean isRefrash = true;
	private int page = 1;
	private boolean isLastPage = false;

	private Traffic_AllList_Bo allListBo = null;
	private Traffic_AllList_Entity allListEntity = null;
	// 每次加载得到的列表
	private List<Traffic_AllItem_Entity> allList = new ArrayList<Traffic_AllItem_Entity>();
	// 第一页的列表
	private List<Traffic_AllItem_Entity> list_save = new ArrayList<Traffic_AllItem_Entity>();

	// 展示出来的列表
	public static List<Traffic_AllItem_Entity> list_show = new ArrayList<Traffic_AllItem_Entity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_traffic_all);

		loadView();
		loadData();
	}
	
	/**
	 * 加载页面控件
	 * */
	private void loadView() {
		// TODO Auto-generated method stub

		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		iv_tomap = (Button) findViewById(R.id.tv_tomap);

		et_input = (EditText) findViewById(R.id.et_input);
		btn_search = (Button) findViewById(R.id.btn_search);

		mRoadPullListView = (PullToRefreshListView) findViewById(R.id.road_all_search_listview);
		mRoadListView = mRoadPullListView.getRefreshableView();

		// 单项点击，跳转到路况详情界面
		mRoadListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AllTrafficationActivity.this,
						TrafficDetailActivity.class);
				System.out.println("选中的id===>>"
						+ String.valueOf(list_show.get(position).getId()));
				intent.putExtra("road_id",
						String.valueOf(list_show.get(position).getId()));
				startActivity(intent);
			}
		});

		ll_back.setOnClickListener(this);
		iv_tomap.setOnClickListener(this);
		btn_search.setOnClickListener(this);
	}

	/**
	 * 加载页面数据以及控件监听
	 * */
	private void loadData() {
		// TODO Auto-generated method stub
		
		//每次进入该页面清空残留数据
		allList.clear();
		list_show.clear();
		list_save.clear();

		searchStr = getIntent().getStringExtra("searchStr");
		if (searchStr != null || !"".equals(searchStr)) {
			et_input.setText(searchStr);
		}
		getAllList(searchStr, 1);

	}

	/**
	 * 获取所有的路况列表
	 * */
	private void getAllList(String key_word, int page) {
		// TODO Auto-generated method stub

		showProgressBar("正在加载列表...");

		allListBo = new Traffic_AllList_Bo(AllTrafficationActivity.this);

		Map<String, String> params = new HashMap<String, String>(1);

		Account account = AccountMgr.getInstance().getAccount(
				AllTrafficationActivity.this);
		 String user_id = String.valueOf(account.getData().getUserId());
		 if("0".equals(user_id)){
			 user_id = "unknown";
		 }
		// 测试使用用户账号
//		String user_id = "7623773";
		String mobile = account.getData().getMobile();
		String lat = LocationUtil.getLatitude(AllTrafficationActivity.this);
		String lng = LocationUtil.getLongitude(AllTrafficationActivity.this);
		// String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		String sign = user_id;

		if (lat == null || lat.equals("")) {
			lat = "unknown";
		}
		if (lng == null || lng.equals("")) {
			lng = "unknown";
		}
		if (mobile == null || mobile.equals("")) {
			mobile = "unknown";
		}

		params.put("city_code", "2201");
		params.put("org_code", "2201");
		params.put("mobile", mobile);
		params.put("longitude", lng);
		params.put("latitude", lat);
		params.put("sign", sign);

		params.put("user_id", user_id);
		params.put("page_no", String.valueOf(page));
		params.put("page_size", "10");
		params.put("key_word", key_word);

		allListBo
				.startRequestTask(
						new getAllListCallBack(),
						AllTrafficationActivity.this,
						params,
						"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/getRoadList");
	}

	/**
	 * 获取所有道路列表回调
	 * */
	class getAllListCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out
					.println("quanbu daolu ====>>" + response.getHttpResult());
			if (response.isSuccess()) {
				if(page == 1){
					page++;
					allListEntity = (Traffic_AllList_Entity) response.getObj();
					allList.clear();
					allList = allListEntity.getData();
					if (allList.size() < 10) {
						// 加载出最后一页了
						isLastPage = true;
					}
					// 首次进来的时候，保存一份全部路况列表
					System.out.println("size===="+list_save.size());
					System.out.println("searchStr===="+searchStr);
					if (list_save.size() == 0 && searchStr == null) {
						list_save.addAll(allList);
					}
					list_show.addAll(allList);
					// AllTrafficationAdapter!!!!!!!!!!!!!!
					adapter = new AllTrafficationAdapter(
							AllTrafficationActivity.this, list_show, handler);

					mRoadPullListView.setVisibility(View.VISIBLE);
					mRoadListView.setVisibility(View.VISIBLE);// 原生控件也要设成可见

					mRoadListView.setAdapter(adapter);
					mRoadListView.setDividerHeight(0);
					mRoadListView.setDivider(null);
					mRoadPullListView
							.setFootPullLabel(getString(R.string.foot_pull_label));
					mRoadPullListView
							.setFootRefreshingLabel(getString(R.string.foot_refreshing_label));
					mRoadPullListView
							.setFootReleaseLabel(getString(R.string.foot_release_label));
					mRoadPullListView.setOnRefreshListener(new OnRefreshListener() {
						@Override
						public void onRefresh() {
							// 接口没做分页,不分下拉刷新和下拉加载更多，直接结束动画
							if (!isRefrash) {
								mRoadPullListView.onRefreshComplete();
							} else {
								mRoadPullListView.onRefreshComplete();
								if (mRoadPullListView.hasPullFromTop()) { // 下拉刷新
									page = 1;
									isLastPage = false;
									list_show.clear();
									getAllList(searchStr, 1);
								} else {
									if (isLastPage) {
										CommonUtils.showToast(
												AllTrafficationActivity.this,
												"已经到最后一页了...", Toast.LENGTH_SHORT);
										return;
									}
									getAllList(searchStr, page);
								}
							}
						}
					});
					// 等待1秒后关闭进度条
					handler.postDelayed(mRun, 1000);
					//当输入框被清空之后返回显示全部道路列表（第一页）
					et_input.addTextChangedListener(new TextWatcher() {
						@Override
						public void onTextChanged(CharSequence arg0, int arg1, int arg2,
								int arg3) {
							// TODO Auto-generated method stub
							if (et_input.getText().toString().trim() == null
									|| "".equals(et_input.getText().toString().trim())) {
								isRefrash = true;
								isLastPage = false;
								searchStr = null;
								if(list_save.size() == 0){
									list_show.clear();
									getAllList("", 1);
								}else{
									list_show.clear();
									list_show.addAll(list_save);
									adapter.notifyDataSetChanged();
								}
							}
						}
						@Override
						public void beforeTextChanged(CharSequence arg0, int arg1,
								int arg2, int arg3) {
							// TODO Auto-generated method stub
						}
						@Override
						public void afterTextChanged(Editable arg0) {
							// TODO Auto-generated method stub
						}
					});
					return;
				}
				page++;
				allListEntity = (Traffic_AllList_Entity) response.getObj();
				allList.clear();
				allList = allListEntity.getData();
				if (allList.size() < 10) {
					// 加载出最后一页了
					isLastPage = true;
				}
				// 首次进来的时候，保存一份全部路况列表
				if (list_save.size() == 0 && searchStr == null) {
					list_save.addAll(allList);
				}
				list_show.addAll(allList);
				adapter.notifyDataSetChanged();
				
			}
			// 等待1秒后关闭进度条
			handler.postDelayed(mRun, 1000);
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * 显示更新UI
	 * 
	 * @author 戴志强
	 * @date 2014/12/09
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case 0:
				adapter.notifyDataSetChanged();
				break;
				
			//点击收藏，未登录就提示登录
			case 1:
				Intent intent = new Intent();
				intent.setClassName(AllTrafficationActivity.this, "cn.ffcs.changchuntv.activity.login.LoginActivity");
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_tomap) {
			Intent intent = new Intent();
			intent.setClass(AllTrafficationActivity.this,
					TrafficBroadcastActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (id == R.id.ll_back) {
			finish();
		} else if (id == R.id.btn_search) {
			searchStr = et_input.getText().toString().trim();
			if (searchStr == null || searchStr.equals("")) {
				isRefrash = true;
				getAllList("", 1);
				return;
			}
			isLastPage = false;
			page = 1;
			isRefrash = true;
			getAllList(searchStr, 1);
			list_show.clear();
		} else {
		}
	}

	/**
	 * 读取进度条
	 * */
	public void showProgressBar(String message) {
		LoadingDialog.getDialog(AllTrafficationActivity.this)
				.setMessage(message).show();
	}

	/**
	 * 隐藏进度条
	 * */
	public void hideProgressBar() {
		LoadingDialog.getDialog(AllTrafficationActivity.this).cancel();
	}

	private Runnable mRun = new Runnable() {
		@Override
		public void run() {
			hideProgressBar();
		}
	};
}
