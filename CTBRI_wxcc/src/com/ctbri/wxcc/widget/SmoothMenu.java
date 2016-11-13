package com.ctbri.wxcc.widget;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.ctbri.wxcc.R;

/**
 * Smooth Menu Item
 * 
 * @author CHEN
 * @version 1.0
 */
public class SmoothMenu extends ViewGroup {
	/**
	 * tan a = deltaX / deltaY > 2 判定敏感度，水平与垂直
	 */
	private static final int TAN = 4;
	private static final float SCALE_BORDER_VALUE = 80;
	private static final float CHANGE_BORDER_VALUE = 103.5f;
	private static final float SCALE = 1.6f;
	private static final float ALPHA = 1.0f;
	private static final String TAG = "SmoothMenu";
	private float mLastX, mMotionX;
	private View contentView;
	private View menuView;
	private float mMotionY;
	public static final int DEFAULT_ICON_COUNT = 4;
	public ArrayList<Drawable> menuList = new ArrayList<Drawable>();
	private boolean canSliding;
	private float mLastY;
	private SmoothMenuItem menuItemLeft;
	private boolean menuItemIsChooice;
	private SmoothMenuItem menuItemRight;
	private SmoothMenuItem currentSelectMenuItem;
	private int scaleBorderPx;
	private int changeBorderPx;
	private static final boolean DEBUG =  false;

	float distance = CHANGE_BORDER_VALUE - SCALE_BORDER_VALUE;
	float distancePx = dip2px(getContext(), distance);
	private OnSmoothMenuItemSelectListener listener;

	public static final int[] indexs = new int[] { R.styleable.Smooth_Menu_sm_menu_icon_1, R.styleable.Smooth_Menu_sm_menu_icon_2, R.styleable.Smooth_Menu_sm_menu_icon_3,
			R.styleable.Smooth_Menu_sm_menu_icon_4 };

	public SmoothMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

	}

	private void init() {
		this.menuView = createMenuView();
		addView(menuView, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		scaleBorderPx = dip2px(getContext(), (int) SCALE_BORDER_VALUE);
		changeBorderPx = dip2px(getContext(), (int) CHANGE_BORDER_VALUE);
	}

	public SmoothMenu(Context context) {
		this(context, null);
	}

	public void setContentView(View contentView) {
		this.contentView = contentView;
		addView(contentView, 1);
	}

	public void setMenuView(View menuView) {
		this.menuView = menuView;
		addView(menuView, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		measureChildren(widthMeasureSpec, heightMeasureSpec);

		int count = getChildCount();
		int mChildHeight = count > 0 ? getChildAt(0).getMeasuredHeight() : 0;
		for (int i = 1; i < count; i++) {
			int mHeight = getChildAt(i).getMeasuredHeight();
			if (mChildHeight < mHeight)
				mChildHeight = mHeight;
		}

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (heightSize < mChildHeight) {
			heightSize = mChildHeight;
			int mRealHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mChildHeight, MeasureSpec.EXACTLY);
			measureChildren(widthMeasureSpec, mRealHeightMeasureSpec);
		}
		if(DEBUG)
		Log.i(TAG, " onMeasure w=" + widthSize + "  h=" + heightSize);

		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);

			int measuredWidth = childView.getMeasuredWidth();
			int measuredHeight = childView.getMeasuredHeight();

			childView.layout(0, 0, l + measuredWidth, measuredHeight);
		}
	}

	/**
	 * can smooth
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean canSmooth(int x, float y) {
		if (canSliding)
			return true;
		float deltaX = mMotionX - x;
		float deltaY = mMotionY - y;
		float absX = Math.abs(deltaX);
		float absY = Math.abs(deltaY);
		if (absX <= 0 || absY <= 0 || absX < absY * TAN) {
			return false;
		}
		return true;
	}

	/**
	 * Smooth
	 * 
	 * @param x
	 * @param y
	 */
	private void smooth(float x, float y) {
		// TODO Auto-generated method stub
		float deltaX = x - mLastX;
		if (x != mLastX) {
			int l = contentView.getLeft();
			int t = contentView.getTop();
			int b = contentView.getBottom();
			int r = contentView.getRight();
			contentView.layout(l + (int) deltaX, t, +(int) deltaX + r, b);
		}
		mLastX = x;
		mLastY = y;
	}

	/**
	 * 
	 * @param x
	 */
	@SuppressLint("NewApi")
	private void smoothMenuItem(float x) {
		int contentViewLeft = contentView.getLeft();
		// setting the direction of left.
		int dir = -1;
		if (contentViewLeft >= 0) {
			dir = 1;
		}
		// vector
		int vector = contentViewLeft * dir;
		currentSelectMenuItem = excuSmoothMenuItem(dir, vector, chooseMenuItem(dir));
	}

	/**
	 * 
	 * @param dir
	 * @return
	 */
	private SmoothMenuItem chooseMenuItem(int dir) {
		if (dir == 1) {
			if(DEBUG)
			Log.i(TAG, "IS menuItemLeft?");
			return menuItemLeft.setType(OnSmoothMenuItemSelectListener.left);
		} else {
			return menuItemRight.setType(OnSmoothMenuItemSelectListener.right);
		}
	}

	private SmoothMenuItem excuSmoothMenuItem(int dir, int vector, SmoothMenuItem smoothMenuItem) {
		int menuItemWidth = smoothMenuItem.getItemWwidth();
		if (vector > menuItemWidth) {
			// change
			if (vector > changeBorderPx) {
				smoothMenuItem.setSelected(true);
				menuView.setBackgroundResource(R.color.pink);
			} else {
				smoothMenuItem.setSelected(false);
				menuView.setBackgroundResource(android.R.color.darker_gray);
			}

			// scale & alpha
			if (vector > scaleBorderPx && vector <= changeBorderPx) {
				float px = vector - scaleBorderPx;
				float scaleStep = (SCALE - 1) / distancePx;
				float scaleXY = px * scaleStep + 1;
				smoothMenuItem.setScale(scaleXY);
				float alphaStep = ALPHA / distancePx;
				smoothMenuItem.setAlpha(px * alphaStep);
			}
			// fixed
			if (vector > changeBorderPx) {
				smoothMenuItem.setScale(SCALE);
				smoothMenuItem.setAlpha(ALPHA);
			}
			int start = 0;
			if (dir == -1) {
				start = (vector - menuItemWidth) / 2 + contentView.getRight();
			} else {
				start = (vector - menuItemWidth) / 2;
			}

			smoothMenuItem.sommth(start);
		}

		return smoothMenuItem;
	}

	@SuppressLint("NewApi")
	public void animations(final int x) {
		if (currentSelectMenuItem != null && currentSelectMenuItem.isSelected()) {
			float scale2 = currentSelectMenuItem.getScale();
			ValueAnimator anim = ValueAnimator.ofFloat(scale2, scale2 * 1.2f, scale2);
			anim.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float animatedValue = (Float) animation.getAnimatedValue();
					currentSelectMenuItem.setScale(animatedValue);
				}
			});
			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					animationRelease(x);
					if (listener != null) {
						listener.onSmoothMenuItemSelected(SmoothMenu.this, currentSelectMenuItem.getType());
					}
				}
			});
			anim.setStartDelay(150);
			anim.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.accelerate_decelerate_interpolator));
			anim.start();
		} else {
			animationRelease(x);
		}
	}

	public static interface OnSmoothMenuItemSelectListener {
		int left = 0;
		int right = 1;

		void onSmoothMenuItemSelected(SmoothMenu sm, int type);
	}

	public void setOnSmoothMenuItemSelected(OnSmoothMenuItemSelectListener listener) {
		this.listener = listener;
	}

	/**
	 * when the up
	 * 
	 * @param x
	 */
	@SuppressLint("NewApi")
	private void animationRelease(final int x) {
		float deltaX = x - mMotionX;
		if(DEBUG)
		Log.i(TAG, "animations deltaX::" + deltaX);
		ValueAnimator anim = ValueAnimator.ofFloat(contentView.getX(), 0);
		if(DEBUG)
		Log.i(TAG, "animations getLeft::" + contentView.getX());
		anim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float animatedValue = (Float) animation.getAnimatedValue();
				contentView.layout((int) animatedValue, contentView.getTop(), contentView.getWidth() + (int) animatedValue, contentView.getBottom());

			}
		});
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				menuView.setBackgroundResource(android.R.color.darker_gray);
				resetSmoothItem();
			}
		});
		anim.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.accelerate_decelerate_interpolator));
		anim.start();
	}

	/**
	 * reset position of smooth's items.
	 */
	@SuppressLint("NewApi")
	protected void resetSmoothItem() {
		if (currentSelectMenuItem != null)
			currentSelectMenuItem.resetPosition();
	}

	private View createMenuView() {
		// TODO Auto-generated method stub
		View view = inflate(getContext(), R.layout.smooth_menu, null);
		menuItemLeft = new SmoothMenuItem(view, R.id.smooth_menu_left_item, OnSmoothMenuItemSelectListener.left);
		menuItemRight = new SmoothMenuItem(view, R.id.smooth_menu_right_item, OnSmoothMenuItemSelectListener.right);
		return view;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/**
		 * 判断 menuitem 是否可以滚动，如果已经滑动。则拦截子组件事件。 2015年3月
		 */
		boolean canSliding = onRequireTouchEvent(ev);
		if (canSliding) {
			// getParent().requestDisallowInterceptTouchEvent(true);
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean performClick() {
		return super.performClick();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean canSliding = onRequireTouchEvent(event);
		if (canSliding) {
			getParent().requestDisallowInterceptTouchEvent(true);
			return true;
		}

		boolean mSuper = super.onTouchEvent(event);
		if (mSuper)
			return mSuper;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			return true;
		case MotionEvent.ACTION_MOVE:
			return true;
			/* 2015年3月16日添加该事件 */
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			performClick();
			break;
		}

		return mSuper;

	}

	public boolean onRequireTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		final int x = (int) event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			mMotionX = x;
			mMotionY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			canSliding = canSmooth(x, y);
			if (canSliding) {
				// smoothing content view
				smooth(x, y);
				// smoothing menu items
				smoothMenuItem(x);
			}
			return canSliding;
			/* 2015年3月16日添加该事件 */
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			canSliding = false;
			animations(x);
			getParent().requestDisallowInterceptTouchEvent(false);
			break;
		}

		return false;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
