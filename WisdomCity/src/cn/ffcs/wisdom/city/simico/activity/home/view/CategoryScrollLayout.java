package cn.ffcs.wisdom.city.simico.activity.home.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.kit.util.StringUtils;

public class CategoryScrollLayout extends RelativeLayout {

	private static final String TAG = CategoryScrollLayout.class
			.getSimpleName();
	protected ImageView mLeftBlock;
	protected ImageView mRightBlock;
	private ListenerScrollView mScrollView;
	private LinearLayout mCtgList;
	private CategoryChangedListener mCtgChangedListener;
	private List<Category> mCtgData;
	private ArrayList<TextView> mCtgViews = new ArrayList<TextView>();
	private Category mCurrentCtg;
	private Category mCategory;
	private int mCurPosition = 0;
	private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			d();
		}
	};
	private OnClickListener mCtgClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Category clickCtg = (Category) view.getTag();
			if (mCtgChangedListener != null) {
				if (mCurrentCtg == null || !mCurrentCtg.mValue.equals(clickCtg.mValue))
					mCtgChangedListener.onChanged(mCurrentCtg, clickCtg);
				else
					mCtgChangedListener.onChanged(mCurrentCtg);
			}
		}
	};
	private ScrollViewListener mScrollListener = new ScrollViewListener() {

		@Override
		public void onScrollChanged(ListenerScrollView view, int l, int t,
				int oldl, int oldt) {
			if (l > 0)
				mLeftBlock.setVisibility(View.VISIBLE);
			else
				mLeftBlock.setVisibility(View.GONE);
			if (l + mScrollView.getWidth() >= mCtgList.getWidth())
				mRightBlock.setVisibility(View.GONE);
			else
				mRightBlock.setVisibility(View.VISIBLE);
		}
	};

	protected CategoryScrollLayout(Context context) {
		super(context);
		init();
	}

	public CategoryScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void a(TextView textview, Resources resources) {
		setPaddingAndBackground(textview,
				R.drawable.simico_top_category_scroll_text_view_bg);
		textview.setTextColor(resources
				.getColorStateList(R.color.simico_top_category_scroll_text_color_day));
		mLeftBlock.setBackgroundResource(R.drawable.channel_leftblock);
		mRightBlock.setBackgroundResource(R.drawable.channel_rightblock);
	}

	public static void setPaddingAndBackground(View view, int res) {
		int left = view.getPaddingLeft();
		int right = view.getPaddingRight();
		int top = view.getPaddingTop();
		int bottom = view.getPaddingBottom();
		view.setBackgroundResource(res);
		view.setPadding(left, top, right, bottom);
	}

	private void init() {
		// c = com.ss.android.article.base.a.e();
		View view = inflate(getContext(), R.layout.simico_categoryscrolllayout, this);
		mScrollView = (ListenerScrollView) view
				.findViewById(R.id.categoryscroll);
		mLeftBlock = (ImageView) view.findViewById(R.id.leftblock);
		mRightBlock = (ImageView) view.findViewById(R.id.rightblock);
		mCtgList = (LinearLayout) view.findViewById(R.id.category_list);
		mScrollView.setScrollViewListener(mScrollListener);
		getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
	}

	private void d() {
		if (mCategory != null && (mCategory != mCurrentCtg || mCurrentCtg == null)) {
			mCurrentCtg = mCategory;
			Resources resources = getResources();
			TextView textview = null;
			Iterator<TextView> iterator = mCtgViews.iterator();
			while (iterator.hasNext()) {
				TextView tv = iterator.next();
				Category bl1 = (Category) tv.getTag();
				a(tv, resources);
				if (bl1 != null && mCurrentCtg.mValue.equals(bl1.mValue)) {
					tv.setSelected(true);
				} else {
					tv.setSelected(false);
					tv = textview;
				}
				textview = tv;
			}
			if (textview != null) {
				int i1 = textview.getLeft() - mScrollView.getWidth() / 2;
				if (i1 < 0)
					i1 = 0;
				int j1 = mCtgList.getWidth() - mScrollView.getWidth();
				if (j1 < 0)
					j1 = 0;
				if (i1 > j1)
					i1 = j1;
				if (mScrollView.getScrollX() != i1)
					mScrollView.smoothScrollTo(i1, 0);
			}
		}
	}

	public void a() {
		String s = "local_city_name";// c.z();
		if (!StringUtils.isEmpty(s)) {
			Iterator<TextView> iterator = mCtgViews.iterator();
			while (iterator.hasNext()) {
				TextView textview = iterator.next();
				Object obj = textview.getTag();
				Category bl1;
				if (obj instanceof Category)
					bl1 = (Category) obj;
				else
					bl1 = null;
				if (bl1 != null && "news_local".equals(bl1.mValue)
						&& !s.equals(textview.getText()))
					textview.setText(s);
			}
		}
	}

	public void b() {
		Resources resources = getResources();
		Iterator<TextView> iterator = mCtgViews.iterator();
		while (iterator.hasNext()) {
			a(iterator.next(), resources);
		}
	}

	public void setCategoryChangedListener(CategoryChangedListener listener) {
		mCtgChangedListener = listener;
	}

	public void setCategoryData(List<Category> list) {
		mCurrentCtg = null;
		mCtgData = list;
		mCtgList.removeAllViews();
		Resources res = getResources();
		int padding = res
				.getDimensionPixelSize(R.dimen.top_category_scroll_text_padding);
		int ctgHeight = res.getDimensionPixelSize(R.dimen.top_category_height);
		int margin = res
				.getDimensionPixelSize(R.dimen.top_category_scroll_item_margin);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2,
				ctgHeight);
		params.leftMargin = margin;
		params.rightMargin = margin;
		if (list != null) {
			int l1 = list.size();
			Log.d(TAG, "current item size is " + l1 + " view size is "
					+ mCtgViews.size());
			if (l1 < mCtgViews.size()) {
				for (int j = l1; j < mCtgViews.size(); j++)
					mCtgViews.get(j).setTag(null);
			}
			for (int i = 0; i < l1; i++) {
				String s = null;
				String s1;
				Category ctg = mCtgData.get(i);
				TextView tvCtg = null;
				if (ctg != null) {
					if (i < mCtgViews.size()) {
						tvCtg = mCtgViews.get(i);
						ViewParent viewparent = tvCtg.getParent();
						if (viewparent != null)
							((ViewGroup) viewparent).removeView(tvCtg);
					} else {
						Log.d(TAG, "construct new textview");
						tvCtg = new TextView(getContext());
						tvCtg.setTextAppearance(getContext(),
								R.style.top_category_scroll_view_item_text);
						a(tvCtg, res);
						tvCtg.setTextColor(getResources().getColor(
								R.color.simico_top_category_scroll_text_color_day));
						mCtgViews.add(tvCtg);
					}
					tvCtg.setGravity(17);
					s = ctg.mName;
					if (!"news_local".equals(ctg.mValue)) {
						s1 = s;
					} else {
						s1 = "local_city_name";// c.z();
						if (StringUtils.isEmpty(s1)) {
							s1 = s;
						}
					}

					tvCtg.setText(s1);
					tvCtg.setTag(ctg);
					tvCtg.setPadding(padding, 0, padding, 0);
					tvCtg.setOnClickListener(mCtgClickListener);
					mCtgList.addView(tvCtg, params);
				}
			}
			setCurCategory(mCurrentCtg);
		}
		postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mCtgList.getMeasuredWidth() > mScrollView.getMeasuredWidth())
					mRightBlock.setVisibility(View.VISIBLE);
				else
					mRightBlock.setVisibility(View.GONE);
			}
		}, 500L);
	}

	public void setCurCategory(Category ctg) {
		if (ctg != null) {
			mCategory = ctg;
			mCurPosition = mCtgData.indexOf(mCategory);
		}
	}
	
	public int getCurPosition() {
		return mCurPosition;
	}
	
	public int[] getCategoryIds() {
		int len = mCtgData.size();
		int chnl_id[] = new int[len];
		for (int i = 0; i < len; i++) {
			Category category = mCtgData.get(i);
			chnl_id[i] = category.a;
		}
		return chnl_id;
	}
}
