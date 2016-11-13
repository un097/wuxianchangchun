package cn.ffcs.wisdom.city.personcenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.GridNoScrollView;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.adapter.RelevanceAddGridAdapter;
import cn.ffcs.wisdom.city.personcenter.bo.PersonCenterBo;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance.MyRelevanceGroup;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance.MyRelevanceGroup.MyRelevanceDetail;
import cn.ffcs.wisdom.city.personcenter.entity.RelevanceAddEntity;
import cn.ffcs.wisdom.city.personcenter.entity.RelevanceAddEntity.RelevanceAdd;
import cn.ffcs.wisdom.city.sqlite.service.TrafficViolationsService;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 我的关联     </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-11-30          </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MyRelevanceActivity extends WisdomCityActivity {

	private PersonCenterBo personCenterBo;// 个人中心Bo
	private List<MyRelevance> list = new ArrayList<MyRelevance>();// 请求返回数据
	private LinearLayout datalayout;//数据存储布局
	private String paId;// 用户ID
	private LayoutInflater mInflater;
	private String content = "";
	private MyRelevanceGroup entity;
	private GridNoScrollView relevanceAddGrid;
	private LinearLayout relevanceAdd;
	private RelevanceAddGridAdapter relevacneAdapter;
	private TextView addTitleText;
	private LinearLayout loadingBar;// 加载条
	private boolean NoRelevance = false;
	private LinearLayout noData;// 没有任何数据

	/**
	 * 获取用户关联
	 * @param context
	 * @param iCall
	 */
	void getRelevance(String paId, Context context, HttpCallBack<BaseResp> iCall) {
		loadingBar.setVisibility(View.VISIBLE);
		relevanceAdd.setVisibility(View.GONE);
		personCenterBo.getRelevance(paId, context, iCall);
	}

	/**
	 * 获取用户关联回调
	 */
	class GetRelevanceCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			try {
				getRelevanceAdd(new GetRelevanceAddCallBack(), mContext);// 获取可添加关联数据
				if (response.isSuccess()) {
					noData.setVisibility(View.GONE);
					datalayout.removeAllViews();
					list = ((MyRelevanceEntity) response.getObj()).getData();
					if (list != null && list.size() != 0) {
						NoRelevance = false;
						addTitleText.setText(getString(R.string.person_center_can_relevance));
						for (int i = 0; i < list.size(); i++) {
							View titleView = mInflater
									.inflate(R.layout.widget_relevance_item, null);
							TextView title = (TextView) titleView.findViewById(R.id.title_tv);
							title.setText(list.get(i).getPbtName());
							datalayout.addView(titleView, getLayoutParams(45));
							for (int j = 0; j < list.get(i).getKeyGroupList().size(); j++) {
								View view = mInflater.inflate(R.layout.widget_relevance_sub_item,
										null);
								TextView contentTv = (TextView) view.findViewById(R.id.contextTv);
								// 显示内容
								String cityName = list.get(i).getKeyGroupList().get(j).getKeyList()
										.get(0).getCityName();
								String showValue = list.get(i).getKeyGroupList().get(j)
										.getKeyList().get(0).getKeyValue();
								if (!StringUtil.isEmpty(cityName)) {
									content = "(" + cityName + ")" + showValue;
								} else {
									content = showValue;
								}
								contentTv.setText(content);
								view.setTag(list.get(i).getKeyGroupList().get(j));
								datalayout.addView(view, getLayoutParams(55));
								view.setOnLongClickListener(new OnLongClickListener() {

									@Override
									public boolean onLongClick(View v) {
										entity = (MyRelevanceGroup) v.getTag();
										String contentTmp = entity.getKeyList().get(0)
												.getKeyValue();

										AlertBaseHelper.showConfirm(mActivity, null, "确定要删除"
												+ contentTmp + "吗?", new OnClickListener() {

											@Override
											public void onClick(View v) {
												AlertBaseHelper.dismissAlert(mActivity);
												int groupId = entity.getKeyGroupId();
												deleteRelevance(mContext, new DeleteCallBack(),
														String.valueOf(groupId));
											}
										});
										return true;
									}
								});
								view.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										MyRelevanceGroup groupEntity = (MyRelevanceGroup) v
												.getTag();
										int itemId = groupEntity.getItemId();
										String cityCode = groupEntity.getKeyList().get(0)
												.getCityCode();
										loadingBar.setVisibility(View.VISIBLE);
										List<MyRelevanceDetail> list = groupEntity.getKeyList();
										gotoMenu(itemId, cityCode, list, false,
												groupEntity.getKeyGroupId());
									}
								});
							}
						}
						loadingBar.setVisibility(View.GONE);
					} else {
						NoRelevance = true;
						addTitleText
								.setText(getString(R.string.person_center_no_data_can_relevance));
						loadingBar.setVisibility(View.GONE);
					}
				}
			} catch (Exception e) {
				Log.e("Exception" + e);
				loadingBar.setVisibility(View.GONE);
			}
		}

		/**
		 * 获取布局属性
		 * @return
		 */
		private LayoutParams getLayoutParams(int dip) {
			LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					CommonUtils.convertDipToPx(mContext, dip));
			return params;
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	/**
	 * 跳转
	 * @param itemId
	 * @param cityCode
	 * @param list
	 * @param isAdd
	 */
	public void gotoMenu(int itemId, String cityCode, List<MyRelevanceDetail> list, boolean isAdd,
			int keyGroupId) {
		personCenterBo.startGotoMenu(mActivity, cityCode, String.valueOf(itemId),
				new GotoMenuCallBack(), list, isAdd, keyGroupId);
	}

	/**
	 * 跳转回调
	 */
	class GotoMenuCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
			if (!resp.isSuccess()) {
				CommonUtils.showToast(mActivity, "栏目维护中，请日后再试...", Toast.LENGTH_SHORT);
			}
			loadingBar.setVisibility(View.GONE);
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	/**
	 * 获取可添加关联
	 * @param iCall
	 * @param context
	 */
	void getRelevanceAdd(HttpCallBack<BaseResp> iCall, Context context) {
		personCenterBo.getRelevanceAdd(iCall, context);
	}

	/**
	 * 获取用户可添加关联回调
	 */
	class GetRelevanceAddCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			try {
				if (response.isSuccess()) {
					List<RelevanceAdd> list = ((RelevanceAddEntity) response.getObj()).getData();
					if (list != null && list.size() > 0) {
						noData.setVisibility(View.GONE);
						relevacneAdapter = new RelevanceAddGridAdapter(mContext, list);
						relevanceAddGrid.setAdapter(relevacneAdapter);
						relevanceAdd.setVisibility(View.VISIBLE);
					} else {
						if (NoRelevance) {
							noData.setVisibility(View.VISIBLE);
						}
					}
				} else {
					if (NoRelevance) {
						noData.setVisibility(View.VISIBLE);
					}
				}
			} catch (Exception e) {
				Log.e("Exception" + e);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	@Override
	protected void initComponents() {
		datalayout = (LinearLayout) findViewById(R.id.relvancelayout);
		relevanceAdd = (LinearLayout) findViewById(R.id.relevance_add_part);
		relevanceAddGrid = (GridNoScrollView) findViewById(R.id.relevance_gridview);
		relevanceAddGrid.setOnItemClickListener(new AddRelevanceClick());
		addTitleText = (TextView) findViewById(R.id.title_text);
		loadingBar = (LinearLayout) findViewById(R.id.loading_bar);
		noData = (LinearLayout) findViewById(R.id.no_data);
	}

	/**
	 * 添加关联点击
	 */
	class AddRelevanceClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			RelevanceAdd entity = (RelevanceAdd) parent.getAdapter().getItem(position);
			String cityCode = MenuMgr.getInstance().getCityCode(mContext);
			int itemId = entity.getItemId();
			gotoMenu(itemId, cityCode, null, true, -1);
		}
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.person_center_my_relevance);
		paId = getIntent().getStringExtra("paId");
		personCenterBo = new PersonCenterBo();
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * 删除用户关联
	 * @param context
	 * @param
	 */
	private void deleteRelevance(Context context, HttpCallBack<BaseResp> iCall, String groupId) {
		personCenterBo.deleteRelevance(context, iCall, groupId);
	}

	class DeleteCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
			if (resp.isSuccess()) {
				Toast.makeText(mContext, "删除关联成功", Toast.LENGTH_SHORT).show();
				for (int j = 0; j < entity.getKeyList().size(); j++) {
					MyRelevanceDetail myRelevanceDetail = entity.getKeyList().get(j);
					String keyName = myRelevanceDetail.getKeyName();
					if (keyName.equals("violCarNo")) {
//						WZCarDBEntity wzEntity = new WZCarDBEntity();
						String keyValue = myRelevanceDetail.getKeyValue();
//						wzEntity.setCarNo("闽" + keyValue);
//						WzCarInfoService.getInstance(getApplicationContext()).remove(wzEntity);
						TrafficViolationsService.getInstance(mContext).deleteByCarNo(keyValue);
					}
				}
				datalayout.removeAllViews();
				getRelevance(paId, mContext, new GetRelevanceCallBack());
			} else {
				Toast.makeText(mContext, "删除关联失败", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_my_relevance_main;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	@Override
	protected void onResume() {
		datalayout.removeAllViews();
		getRelevance(paId, mContext, new GetRelevanceCallBack());
		super.onResume();
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
}
