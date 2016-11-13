package cn.ffcs.changchuntv.activity.news;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.ffcs.changchun_base.activity.BaseFragmentActivity;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.activity.home.cache.IndexCacheUtils;
import cn.ffcs.wisdom.city.simico.activity.home.view.Category;
import cn.ffcs.wisdom.city.simico.activity.home.view.CategoryChangedListener;
import cn.ffcs.wisdom.city.simico.activity.home.view.CategoryScrollLayout;
import cn.ffcs.wisdom.city.simico.activity.subscribe.SubscribeDragActivity;
import cn.ffcs.wisdom.city.simico.api.model.ApiResponse;
import cn.ffcs.wisdom.city.simico.api.model.MenuHelper;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequestListener;
import cn.ffcs.wisdom.city.simico.api.request.GetChannelList;
import cn.ffcs.wisdom.city.simico.base.Application;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.viewpagerindicator.TabPageIndicator;
/**
 * 新闻资讯的Activity 包含头部左右滑动的频道 ---CategoryScrollLayout设置和数据载入（带缓存）
 * @author Administrator
 *
 */
public class NewsActivity extends BaseFragmentActivity implements OnClickListener, CategoryChangedListener {
	
	private ViewPager mViewPager;
	private NewsViewPagerAdapter mAdapter;
	private TabPageIndicator pageIndicator;
	
	private CategoryScrollLayout mCtgLayout;
	private List<Category> list = new ArrayList<Category>();
	private Category mCurrentCtg;
	private static final String PREFIX_MY_CHANNEL = "MY_CHANNEL";
	private static final String PREFIX_ALL_CHANNEL = "ALL_CHANNEL";
	private Context mContext;
	private int chnlId;
	

	@Override
	public int getLayoutId() {
		return R.layout.simico_activity_home;
	}

	@Override
	protected void init(Bundle savedInstanceState) {
		chnlId = getIntent().getIntExtra("chnlId", 0);
		initActionBar();
//		if (chnlId < 0) {
//			findViewById(R.id.category_layout).setVisibility(View.GONE);
//		}
		mContext = getApplicationContext();
		// init categories
		findViewById(R.id.icon_category).setOnClickListener(this);
		findViewById(R.id.icon_category).setVisibility(View.GONE);
		mCtgLayout = (CategoryScrollLayout) findViewById(R.id.topcategoryscroll);

		mCtgLayout.setCategoryChangedListener(this);
		// mCtgLayout.setCurCategory(mCurrentCtg);
		// mCtgLayout.setCategoryData(list);

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				mCurrentCtg = list.get(arg0);
				mCtgLayout.setCurCategory(mCurrentCtg);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		// mAdapter = new NewsViewPagerAdapter(getSupportFragmentManager());
		// mViewPager.setAdapter(mAdapter);

		// sendRequest();
		
		new LoadMyChannelCacheTask().execute();
		super.init(savedInstanceState);
	}

	/**
	 * 初始化操作标题栏
	 */
	private void initActionBar() {
		Button back = (Button) findViewById(R.id.btn_back);
		back.setBackgroundDrawable(null);
		Drawable drawable = getResources().getDrawable(R.drawable.action_bar_back);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		back.setCompoundDrawables(drawable, null, null, null);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
		TextView title = (TextView) findViewById(R.id.tv_name);
		title.setTextColor(getResources().getColor(R.color.text_color_title));
		if (chnlId == -1) {
			title.setText("政务发布");
		} else if (chnlId == -2) {
			title.setText("高速路况");
		} else {
			title.setText("新闻资讯");
		}
		findViewById(R.id.top_refresh).setVisibility(View.GONE);
		findViewById(R.id.top_progress).setVisibility(View.GONE);
		findViewById(R.id.ly_location).setVisibility(View.GONE);
		
	}
	
	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (id == R.id.icon_category) {
			//订阅添加按钮
			Intent intent = new Intent(this, SubscribeDragActivity.class);
			startActivity(intent);
		} else if (id == R.id.title_layout) {
			int index = mViewPager.getCurrentItem();
			NewsFragment fragment = (NewsFragment) mAdapter.getFragment(index);
			if (fragment != null)
				fragment.refresh();
		}
	}
	
	public void updateRefresh(boolean isRefresh) {
		
	}

	@Override
	public void onChanged(Category currentCtg) {
		int index = mViewPager.getCurrentItem();
		NewsFragment fragment = (NewsFragment) mAdapter.getFragment(index);
		if (fragment != null)
			fragment.refresh();
		
	}

	/**
	 * 顶部类别改变
	 */
	@Override
	public void onChanged(Category currentCtg, Category clickCtg) {
		mCurrentCtg = clickCtg;
		mCtgLayout.setCurCategory(mCurrentCtg);

		mViewPager.setCurrentItem(mCtgLayout.getCurPosition());
		
	}
	
	private void getDataFromNetwork() {
		GetChannelList req = new GetChannelList(new BaseRequestListener() {

			@Override
			public void onRequestSuccess(ApiResponse json) throws Exception {
				JSONArray tagJson = (JSONArray) json.getData();
				if (tagJson != null) {
					new SaveCacheTask(PREFIX_ALL_CHANNEL).execute(tagJson);
					JSONArray tagJson2 = new JSONArray();
//					setJson(tagJson2, "0", "推荐");
					for (int i = 0; i < 4 && i < tagJson.length(); i++) {
						tagJson2.put(tagJson.get(i));
					}
					new SaveCacheTask(PREFIX_MY_CHANNEL).execute(tagJson2);
					Application.setChannelLastCacheTime(System
							.currentTimeMillis());
					list = MenuHelper.makeMyCategoryV2(tagJson2);
				}
			}

			@Override
			public void onRequestFinish() {
				loadChannel();
			}

			@Override
			public void onRequestFaile(ApiResponse json, Exception e) {
				e.printStackTrace();
				Application.showToastShort(getErrorMessage(json));
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Application.showToastShort(BaseRequestListener
						.getErrorMessage(null));
			}
		});
		Application.instance().addToRequestQueue(req);
	}
	
	class SaveCacheTask extends AsyncTask<JSONArray, Void, Void> {

		private String prefix;

		SaveCacheTask(String prefix) {
			this.prefix = prefix;
		}

		@Override
		protected Void doInBackground(JSONArray... params) {
			IndexCacheUtils.saveCacheNoUser(params[0],
					prefix + Application.getCurrentCity());
			return null;
		}
	}
	
	class LoadMyChannelCacheTask extends AsyncTask<Void, Void, JSONArray> {

		private boolean flag = false;

		@Override
		protected JSONArray doInBackground(Void... params) {
			flag = false;
//			String cacheContent = IndexCacheUtils
//					.getCacheNoUser(PREFIX_MY_CHANNEL
//							+ Application.getCurrentCity());
//			if (!TextUtils.isEmpty(cacheContent)) {
//				try {
//					JSONArray tagJson = new JSONArray(cacheContent);
//					if (tagJson != null) {
//						list = MenuHelper.makeMyCategoryV2(tagJson);
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			} else {
//				try {
//					JSONArray tagJson = new JSONArray();
//					setJson(tagJson, "0", "推荐");
//					list = MenuHelper.makeMyCategoryV2(tagJson);
//					new SaveCacheTask(PREFIX_MY_CHANNEL).execute(tagJson);
//					flag = true;
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
			try {
				if (chnlId == -1) {
					JSONArray tagJson = new JSONArray();
					setJson(tagJson, "268", "政务发布 ");
					list = MenuHelper.makeMyCategoryV2(tagJson);
					mCurrentCtg = list.get(0);
				} else if (chnlId == -2) {
					JSONArray tagJson = new JSONArray();
					setJson(tagJson, "269", "高速路况 ");
					list = MenuHelper.makeMyCategoryV2(tagJson);
					mCurrentCtg = list.get(0);
				} else {
					JSONArray tagJson = new JSONArray();
//					setJson(tagJson, "0", "今日推荐 ");
					setJson(tagJson, "257", "热点新闻 ");
					setJson(tagJson, "256", "长春知道 ");
					setJson(tagJson, "258", "天下新闻 ");
					setJson(tagJson, "266", "生活娱乐 ");
//					setJson(tagJson, "259", "趣闻轶事 ");
//					setJson(tagJson, "267", "日读文章 ");
					list = MenuHelper.makeMyCategoryV2(tagJson);
					mCurrentCtg = list.get(chnlId);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONArray tagJson) {
			loadChannel();
			if (flag) {
				getDataFromNetwork();
			}
			super.onPostExecute(tagJson);
		}
	}
	
	private void loadChannel() {
		mCtgLayout.setCategoryData(list);
		if (mCurrentCtg == null)
			mCurrentCtg = list.get(0);
		if (!list.contains(mCurrentCtg))
			mCurrentCtg = list.get(Math.min(mCtgLayout.getCurPosition(),
					list.size() - 1));
		mCtgLayout.setCurCategory(mCurrentCtg);
		mViewPager.removeAllViewsInLayout();
		int ids[] = mCtgLayout.getCategoryIds();
		if (getSupportFragmentManager().getFragments() != null
				&& getSupportFragmentManager().getFragments().size() > 0)
			getSupportFragmentManager().getFragments().clear();
		ArrayList<Fragment> flist = new ArrayList<Fragment>();
		for (int i = 0; i < ids.length; i++) {
			NewsFragment fragment = (NewsFragment) NewsFragment.newInstance(ids[i]);
			fragment.setTitle(list.get(i).mName);
			flist.add(fragment);
		}
		mAdapter = new NewsViewPagerAdapter(getSupportFragmentManager(), flist);
		mViewPager.setAdapter(mAdapter);
		pageIndicator = (TabPageIndicator) findViewById(R.id.tab_page_indicator);
		pageIndicator.setViewPager(mViewPager);
//		if (chnlId > 0) {
			pageIndicator.setVisibility(View.VISIBLE);
//		}
		if (mCtgLayout.getCurPosition() !=0)
			mViewPager.setCurrentItem(mCtgLayout.getCurPosition());
	}
	
	private void setJson(JSONArray tagJson, String id, String name)
			throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("id", id);
		obj.put("name", name);
		tagJson.put(obj);
	}

}
