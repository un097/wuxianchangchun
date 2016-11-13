package com.ctbri.wxcc.travel;

import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.entity.TravelRaidersBean;
import com.ctbri.wxcc.entity.TravelRaidersBean.Raiders;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.umeng.analytics.MobclickAgent;

public class TravelMainFragment extends
		CommonList<TravelRaidersBean, TravelRaidersBean.Raiders> {

	/**
	 * 类别为旅游
	 */
	public final static int MENU_TRAVEL = 1;
	/**
	 * 类别为美食
	 */
	public final static int MENU_FOOD = 3;
	/**
	 * 类别为聚会
	 */
	public final static int MENU_PARTY = 2;
	/**
	 * 类别为特产
	 */
	public final static int MENU_LOCAL = 4;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected String getActionBarTitle() {
		return getString(R.string.title_travel);
	}

	@Override
	protected void onBackPress() {
		super.onBackPress();
	}

	@Override
	protected String getListUrl() {
		return Constants.METHOD_TRAVEL_CONTENT_LIST + "?type=0";
	}

	@Override
	protected Class<TravelRaidersBean> getGsonClass() {
		return TravelRaidersBean.class;
	}

	@Override
	protected List<Raiders> getEntitys(TravelRaidersBean bean) {
		return bean.getData().getTravel_list();
	}

	@Override
	protected boolean isEnd(TravelRaidersBean bean) {
		return bean.getData().getIs_end() == 0;
	}

	@Override
	protected boolean initHeaderDetail(LoadMorePTRListView lv_list,
			LayoutInflater inflater) {
		View detailHeader = inflater.inflate(R.layout.travel_main_header, lv_list.getRefreshableView(), false);
		lv_list.addHeader(detailHeader, null, false);

		fillTravelMenu((ViewGroup) detailHeader.findViewById(R.id.ll_menu_container));

		Fragment details = Fragment.instantiate(activity,
				ImageLooperFragment.class.getName());
		getChildFragmentManager().beginTransaction()
				.replace(R.id.travel_detail_header, details).commit();
		return true;
	}

	private void fillTravelMenu(ViewGroup menuContainer) {
		int titles[] = new int[] { R.string.travel_menu_title_spot,
				R.string.travel_menu_title_food,
				R.string.travel_menu_title_party,
				R.string.travel_menu_title_locale };
		int images[] = new int[] { 
				R.drawable.icon_travel_menu_spot,
				R.drawable.icon_travel_menu_food,
				R.drawable.icon_travel_menu_party,
				R.drawable.icon_travel_menu_local };
		int ids[] = new int[] { MENU_TRAVEL, MENU_FOOD, MENU_PARTY, MENU_LOCAL };
		final int childCount = menuContainer.getChildCount();
		MenuClick clk = new MenuClick();
		for (int i = 0; i < childCount; i++) {
			View child = menuContainer.getChildAt(i);
			child.setTag(ids[i]);
			((TextView) child.findViewById(R.id.tv_menu_title))
					.setText(titles[i]);
			((ImageView) child.findViewById(R.id.iv_menu_icon))
					.setImageResource(images[i]);
			child.setOnClickListener(clk);
		}

	}

	@Override
	protected View getListItemView(int position, View convertView,
			ViewGroup parent, Raiders data, ImageLoader imgloader,
			DisplayImageOptions dio) {

		TravelRaidersHolder trHolder = null;
		if (convertView == null) {
			trHolder = new TravelRaidersHolder();
			convertView = activity.getLayoutInflater().inflate(
					R.layout.travel_main_list_item, parent, false);
			trHolder.tv_subtitle = (TextView) convertView
					.findViewById(R.id.tv_subtitle);
			trHolder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			trHolder.iv_travel = (ImageView) convertView
					.findViewById(R.id.iv_detail_image);
			trHolder.iv_recommend = (ImageView) convertView
					.findViewById(R.id.iv_recommend);
			convertView.setTag(trHolder);
		} else {
			trHolder = (TravelRaidersHolder) convertView.getTag();
		}
		trHolder.tv_subtitle.setText(data.getSubtitle());
		trHolder.tv_title.setText(data.getTitle());
		trHolder.iv_recommend
				.setVisibility(data.getIs_recommend() == 0 ? View.VISIBLE
						: View.GONE);

		imgloader.displayImage(data.getPic_url().trim(), trHolder.iv_travel,dio);

		return convertView;
	}

	

	class TravelRaidersHolder {
		TextView tv_title, tv_subtitle;
		ImageView iv_travel, iv_recommend;
	}

	class MenuClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			final int menu_id = (Integer) v.getTag();
			Intent it = new Intent(activity, TravelListActivity.class);
			it.putExtra(TravelListFragment.KEY_TYPEID, menu_id);
			startActivity(it);

			// 统计每个字模块的点击次数
			MobclickAgent.onEvent(activity, Constants_Community.EVENT_IDS[menu_id]);
		}

	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position,
			long id, Raiders entity) {

		Intent it = new Intent(activity, TravelDetailActivity.class);
		it.putExtra(TravelContentDetail.KEY_DETAIL_ID, entity.getTravel_id());
		it.putExtra("title", getString(R.string.title_raiders));
		startActivity(it);

	}

	class ScaledDisplayer implements BitmapDisplayer {

		@Override
		public void display(Bitmap loadedImage, ImageAware view,
				LoadedFrom loadedFrom) {

			// 使用重新创建 缩放 的Bitmap 的开式
			scaleImageByNewBitmap(view, loadedImage);

			// 使用 ImageView.setImageMatrix 的方式
			// scaleImageByMatrix(view, loadedImage);
		}

	}

	private void scaleImageByNewBitmap(ImageAware view, Bitmap loadedImage) {
		BitmapDrawable bd = new BitmapDrawable(getResources(), loadedImage);

		float scale;
		float dx = 0, dy = 0;
		int dwidth = bd.getIntrinsicWidth();
		int dheight = bd.getIntrinsicHeight();
		int vheight = view.getHeight();
		int vwidth = view.getWidth();

		if (dwidth * vheight > vwidth * dheight) {
			scale = (float) vheight / (float) dheight;
			dx = (vwidth - dwidth * scale) * 0.5f;
		} else {
			scale = (float) vwidth / (float) dwidth;
			dy = (vheight - dheight * scale) * 0.5f;
		}

		Bitmap real = Bitmap.createScaledBitmap(loadedImage,
				(int) (dwidth * scale), (int) (dheight * scale), false);
		view.setImageBitmap(real);
	}

	/**
	 * 该实现方式有 bug , 当前获取 imageview 的宽度为 0 时，只能显示出一半的图片。
	 * 
	 * @param view
	 * @param loadedImage
	 */
	private void scaleImageByMatrix(ImageAware view, Bitmap loadedImage) {
		BitmapDrawable bd = new BitmapDrawable(getResources(), loadedImage);

		float scale;
		float dx = 0, dy = 0;
		int dwidth = bd.getIntrinsicWidth();
		int dheight = bd.getIntrinsicHeight();
		int vheight = view.getHeight();
		int vwidth = view.getWidth();

		if (dwidth * vheight > vwidth * dheight) {
			scale = (float) vheight / (float) dheight;
			dx = (vwidth - dwidth * scale) * 0.5f;
		} else {
			scale = (float) vwidth / (float) dwidth;
			// 居中
			// dy = (vheight - dheight * scale) * 0.5f;

			// 显示底部
			dy = vheight - (dheight * scale);

			// 显示顶部
			// dy=0;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		matrix.postTranslate(dx, dy);

		((ImageView) view.getWrappedView()).setImageMatrix(matrix);
		view.setImageBitmap(loadedImage);
	}

	@Override
	protected String getAnalyticsTitle() {
		return "travel_main";
	}

}
