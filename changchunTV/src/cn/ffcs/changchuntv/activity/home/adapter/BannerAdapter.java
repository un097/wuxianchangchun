package cn.ffcs.changchuntv.activity.home.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ctbri.wxcc.shake.SingMainActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cn.ffcs.changchuntv.R;
import cn.ffcs.changchuntv.entity.AdvertisingEntity.Advertising;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.MenuType;
import cn.ffcs.wisdom.city.simico.activity.detail.NewsDetailActivity;
import cn.ffcs.wisdom.city.simico.api.model.News;
import cn.ffcs.wisdom.city.simico.image.CommonImageLoader;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem.MenuTypeMedia;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;
import cn.ffcs.wisdom.tools.StringUtil;

public class BannerAdapter extends PagerAdapter {

	private List<Advertising> mList;
	private final LayoutInflater mInflater;
	private List<View> mViewList = new ArrayList<View>();
	private ImageLoader loader;
	private int count = 0;
	private Activity mActivity;
	private Context mContext;

	public BannerAdapter(Activity activity, List<Advertising> list) {
		this.mActivity = activity;
		this.mContext = activity.getApplicationContext();
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mList = list;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				View view = mInflater.inflate(R.layout.banner_item, null);
				mViewList.add(view);
			}
			count = list.size();
		} else {
			list = new ArrayList<Advertising>();
		}

		loader = ImageLoader.getInstance();
		ImageLoaderConfiguration createDefault = ImageLoaderConfiguration.createDefault(mContext);
		loader.init(createDefault);
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		if (position >= mViewList.size()) {
			if (mViewList.size() != 0) {
				int newPosition = position % mViewList.size();
				position = newPosition;
				count++;
			}
		}
		if (position < 0) {
			position = -position;
			count--;
		}
		try {
			View view = mViewList.get(position);
			Advertising entity = mList.get(position);

			String url = Config.GET_SERVER_ROOT_URL() + entity.android_pic;
//			if (StringUtil.isEmpty(url)) {
//				url = "http://ccgd.153.cn:50081/" + entity.ios_pic;
//			}
			ImageView imageView = (ImageView) view.findViewById(R.id.banner_img);
			imageView.setOnClickListener(new OnBannerClick(entity));
//			loader.loadUrl(imageView, url);
			DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false)
					.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
			loader.displayImage(url, imageView, options);
			((ViewPager) container).addView(mViewList.get(position), 0);
		} catch (Exception e) {
		}
		if (mViewList.size() > 0) {
			return mViewList.get(position);
		}
		return null;
	}

	class OnBannerClick implements OnClickListener {
		private Advertising advertising;

		public OnBannerClick(Advertising entity) {
			this.advertising = entity;
		}

		@Override
		public void onClick(View v) {
			if (advertising.url.contains("web_type=banner_adv_type")) {
				Intent intent = new Intent(mActivity, SingMainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("url", advertising.url);
				mContext.startActivity(intent);
			}else {
				if (!advertising.banner_adv_type.equals("image_type")) {
//					MenuItem item = new MenuItem();
//					item.setMenuType(MenuType.WAP);
//					item.setMenuName("新闻资讯");
//					item.setUrl(advertising.url);
//					item.media = MenuTypeMedia.bannerWapShare;
//					// 首页Banner点击跳转到wap，右上角显示分享按钮
//					AppMgrUtils.launchAPP(mActivity, item, R.string.home_name);
					
					//服务器给的banner_adv_type字段不等于image_type  可以跳转！
					Intent intent = new Intent(mActivity, NewsDetailActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("type", 1);
					News news = new News();
//					news.setContent("");
//					news.setType(advertising.);
					news.setSourceUrl(advertising.android_pic);
					news.setSubtitle(advertising.title);
					news.setType(advertising.banner_adv_type);
					news.setWapUrl(advertising.url);
//					news.set
					intent.putExtra("news", news);
					intent.putExtra("url", advertising.url);
					intent.putExtra("showMore", true);
					intent.setExtrasClassLoader(News.class.getClassLoader());
					mContext.startActivity(intent);
				}
			}
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		if (position >= mViewList.size()) {
			if (mViewList.size() != 0) {
				int newPosition = position % mViewList.size();
				position = newPosition;
			}

		}
		if (position < 0) {
			position = -position;
		}
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

}
