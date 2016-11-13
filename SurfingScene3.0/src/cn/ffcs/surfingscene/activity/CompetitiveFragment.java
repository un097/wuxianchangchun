package cn.ffcs.surfingscene.activity;

import java.util.ArrayList;
import java.util.List;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.adapter.CompGridViewAdapter;
import cn.ffcs.surfingscene.adapter.ListViewAdapter;
import cn.ffcs.surfingscene.common.Config;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.FavoriteDataMgr;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;
import cn.ffcs.widget.GridNoScrollView;
import cn.ffcs.widget.ListNoScrollView;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;

import com.ffcs.surfingscene.entity.ActionEntity;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.function.LandscapeList;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>
 * Title: 精品
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * 
 * @author: caijj
 *          </p>
 *          <p>
 *          Copyright: Copyright (c) 2012
 *          </p>
 *          <p>
 *          Company: FFCS Co., Ltd.
 *          </p>
 *          <p>
 *          Create Time: 2013-6-18
 *          </p>
 *          <p>
 *          Update Time:
 *          </p>
 *          <p>
 *          Updater:
 *          </p>
 *          <p>
 *          Update Comments:
 *          </p>
 */
public class CompetitiveFragment extends BaseFragment {
	private Bundle paramBundle;// 获取参数
	private List<GlobalEyeEntity> globalEyeList = new ArrayList<GlobalEyeEntity>();
	private List<GlobalEyeEntity> globalEyeGridList = new ArrayList<GlobalEyeEntity>();
	private String tyjxCode;// 天翼景象编号
	private View baseView; // 布局顶层
	private View basicLayer; // 内容层

	// private TextView title; // 标题
	private TextView descript; // 描述

	private LoadingBar loadingBar;
	private View loadingError;

	private GridNoScrollView mGridView;
	private ListNoScrollView mListView;

	private ListViewAdapter mListAdapter;
	private CompGridViewAdapter mGridAdapter;

	private LandscapeList gridLoader; // 九宫格数据
	private LandscapeList listLoader; // 列表数据

	// private String name; // 精品名
	private String intro; // 精品简介

	boolean gridSuccess = false;
	boolean listSuccess = false;

	boolean refreshFlag = false;

	/**
	 * 初始化并传递参数
	 * 
	 * @param bundle
	 * @return
	 */
	public static CompetitiveFragment newInstance(Bundle bundle) {
		CompetitiveFragment fragMent = new CompetitiveFragment();
		fragMent.setArguments(bundle);
		return fragMent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		paramBundle = getArguments();
	}

	@Override
	protected View setOnCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		baseView = inflater.inflate(R.layout.glo_act_competitive, container,
				false);

		mGridView = (GridNoScrollView) baseView
				.findViewById(R.id.comp_gridview);
		mListView = (ListNoScrollView) baseView
				.findViewById(R.id.comp_listview);
		mListView.setOnItemClickListener(new OnItemClickClickListener());
		descript = (TextView) baseView.findViewById(R.id.descript);

		basicLayer = baseView.findViewById(R.id.basic_layer);
		basicLayer.setVisibility(View.GONE);

		loadingBar = (LoadingBar) baseView.findViewById(R.id.loading_bar);
		loadingBar.setVisibility(View.VISIBLE);

		loadingError = baseView.findViewById(R.id.loading_error);
		loadingError.setVisibility(View.GONE);

		loadingError.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CommonUtils.isNetConnectionAvailable(mContext)) {
					showLoading();
					hideBasicLayer();
					loadCompData();
				} else {
					CommonUtils.showToast(getActivity(),
							R.string.glo_net_work_error, Toast.LENGTH_SHORT);
				}
			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				GlobalEyeEntity entity = globalEyeGridList.get(position);
				if (entity != null) {
					VideoPlayerTool.playVideo(getActivity(), entity);
				}
			}

		});
		return baseView;
	}

	class OnItemClickClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			GlobalEyeEntity entity = globalEyeList.get(position);
			if (entity != null) {
				VideoPlayerTool.playVideo(getActivity(), entity);
			}
		}
	}

	@Override
	protected void initData() {
		gridLoader = new LandscapeList(mContext);
		listLoader = new LandscapeList(mContext);
		mListAdapter = new ListViewAdapter(getActivity());
		mGridAdapter = new CompGridViewAdapter(mContext);
		tyjxCode = paramBundle.getString(Key.K_AREA_CODE);

		if (CommonUtils.isNetConnectionAvailable(mContext)) {
			loadCompData();
		} else {
			showError();
			CommonUtils.showToast(getActivity(), R.string.glo_net_work_error,
					Toast.LENGTH_SHORT);
		}

		FavoriteDataMgr.getInstance().registerDataSetObserver(favoriteObs);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (refreshFlag) {
			if (mListAdapter != null) {
				mListAdapter.notifyDataSetChanged();
			}

			if (mGridAdapter != null) {
				mGridAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		FavoriteDataMgr.getInstance().unregisterDataSetObserver(favoriteObs);
		favoriteObs = null;
	}

	protected void showError() {
		hideLoading();
		hideBasicLayer();
		if (loadingError != null) {
			loadingError.setVisibility(View.VISIBLE);
		}
	}

	protected void hideError() {
		if (loadingError != null) {
			loadingError.setVisibility(View.GONE);
		}
	}

	protected void hideLoading() {
		if (loadingBar != null) {
			loadingBar.setVisibility(View.GONE);
		}
	}

	protected void showLoading() {
		hideError();
		hideBasicLayer();
		if (loadingBar != null) {
			loadingBar.setVisibility(View.VISIBLE);
		}
	}

	protected void showBasicLayer() {
		hideError();
		hideLoading();
		if (basicLayer != null) {
			basicLayer.setVisibility(View.VISIBLE);
		}
	}

	protected void hideBasicLayer() {
		if (basicLayer != null) {
			basicLayer.setVisibility(View.GONE);
		}
	}

	/**
	 * 加载数据
	 */
	private void loadCompData() {
		loadEyeGrid();
		loadEyeList();
	}

	/**
	 * 获取九宫格景点数据 1022
	 */
	protected void loadEyeGrid() {
		gridLoader.getAllActionList(tyjxCode, Config.TYPE_COMPE_RECOMMEND,
				mGridCall, Config.METHOD_CITY_EYELIST); // 九宫格
	}

	/**
	 * 获取列表数据 1021
	 */
	protected void loadEyeList() {
		listLoader.getAllActionList(tyjxCode, Config.TYPE_COMP_LIST, mListCall,
				Config.METHOD_CITY_EYELIST); // 列表
	}

	/**
	 * 刷新九宫格数据
	 * 
	 * @param data
	 */
	protected void refreshGrid(List<GlobalEyeEntity> data) {
		if (data != null && data.size() > 0) {
			mGridAdapter.setData(data);
			mGridView.setAdapter(mGridAdapter);
		}
	}

	/**
	 * 刷新列表
	 * 
	 * @param data
	 */
	protected void refreshList(List<GlobalEyeEntity> data) {
		if (data != null && data.size() > 0) {
			mListAdapter.setData(data);
			mListView.setAdapter(mListAdapter);
		}
	}

	/**
	 * 刷新收藏状态
	 */
	public void refreshFavorite() {
		if (mListAdapter != null) {
			mListAdapter.notifyDataSetChanged();
		}
		if (mGridAdapter != null) {
			mGridAdapter.notifyDataSetChanged();
		}
	}

	HttpCallBack<BaseResponse> mGridCall = new HttpCallBack<BaseResponse>() {

		@Override
		public void callBack(BaseResponse resp, String arg1) {
			showBasicLayer();
			try {
				if (resp != null && "1".equals(resp.getReturnCode())) { // 成功
					List<GlobalEyeEntity> eyes = new ArrayList<GlobalEyeEntity>();
					List<ActionEntity> actions = resp.getActions();
					if (actions != null && actions.size() > 0) {
						ActionEntity entity = actions.get(0);
						if (entity != null) {
							eyes = entity.getGeyes();
							globalEyeGridList = eyes;
							// name = entity.getActionName();
							intro = entity.getIntro();
							gridSuccess = true;
							refreshGrid(eyes);
							// title.setText(name);
							descript.setText(Html.fromHtml(intro));
						}
					}
				}
			} catch (Exception e) {
				Log.d("加载精品数据失败");
			}

		}
	};

	HttpCallBack<BaseResponse> mListCall = new HttpCallBack<BaseResponse>() {
		@Override
		public void callBack(BaseResponse resp, String arg1) {
			showBasicLayer();
			if (resp != null && "1".equals(resp.getReturnCode())) { // 成功
				List<GlobalEyeEntity> eyes = new ArrayList<GlobalEyeEntity>();
				List<ActionEntity> actions = resp.getActions();
				if (actions != null && actions.size() > 0) {
					eyes = actions.get(0).getGeyes();
					listSuccess = true;
					globalEyeList = eyes;
					refreshList(eyes);
				}
			}
		};
	};

	DataSetObserver favoriteObs = new DataSetObserver() {
		public void onChanged() {
			refreshFlag = true;
		}
	};

}
