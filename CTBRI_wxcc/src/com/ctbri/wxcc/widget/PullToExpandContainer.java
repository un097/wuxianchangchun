package com.ctbri.wxcc.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ctbri.wxcc.R;

public class PullToExpandContainer extends LinearLayout {

	private ViewGroup mHeaderContainer;
	private View mExpandView, mHeaderView;
	private InternalScrollView mScrollview;
	private View mFixedBar;
	private LinearLayout mRootContentContainer, mContentContainer;
	private int mHeaderHeight, mFixedBarHeight;
	// 当前 view 的高度
	private int mViewHeight;
	private Context context;
	private boolean mExpandViewShowing, mExpanded;
	private float mCurrentExpandHeight;

	private float mMinTouchSlop;
	private IShadowPainter mShadowPainter;
	//statusBar 高度
	private int mStatusBarHeight;
	
	private static final boolean DEBUG = false;

	/**
	 * head 可被卷起的高度。
	 */
	private int mHeadScrollHeight = -1;

	private RepairExpandViewRunner mRepairHeightRunner;

	/**
	 * expand view 的遮罩颜色透明度
	 */
	private static final float mDegressAlpha = 0.33f;

	private static final int MAX_SETTLE_DURATION = 300; // ms
	/**
	 * expand view 阻尼系数
	 */
	private static final float mDamping = 0.5f;

	private static final String TAG = PullToExpandContainer.class.toString();

	private OnExpandListener mExpandListener;
	/**
	 * 默认的阴影颜色
	 */
	private int mShadhowBlack;

	public OnExpandListener getExpandListener() {
		return mExpandListener;
	}

	public void setExpandListener(OnExpandListener mExpandListener) {
		this.mExpandListener = mExpandListener;
	}

	public PullToExpandContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		setOverScrollMode(OVER_SCROLL_NEVER);

		mRepairHeightRunner = new RepairExpandViewRunner();
		// 先设置默认的 headerHeight
		// mHeaderHeight = 300; // cell pixel

		initContentContainer(attrs);
		addView(mScrollview, 0);

		mHeaderContainer = makeHeaderContainer();
		mRootContentContainer.addView(mHeaderContainer, 0);

		mShadhowBlack = Color.argb((int) (mDegressAlpha * 2 * 255), 0, 0, 0);

		// 如果 当前系统版本大于 JELLY_BEAN ，即可以使用
		if (VERSION.SDK_INT >= 16 && false) {
			// mShadowPainter = new NewPainter();
		} else {
			mShadowPainter = new OldPainter();
		}
		ViewConfiguration vc = ViewConfiguration.get(this.context);
		mMinTouchSlop = vc.getScaledTouchSlop();

	}

	public int getHeadScrollHeight() {
		return mHeadScrollHeight;
	}

	public void setHeadScrollHeight(int mHeadScrollHeight) {
		this.mHeadScrollHeight = mHeadScrollHeight;
		mFixedBarHeight = mHeaderHeight - mHeadScrollHeight;
	}

	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	public View getFixedBar() {
		return mFixedBar;
	}

	public void setFixedBar(View mBar) {
		if (mFixedBar != null) {
			((ViewGroup) mFixedBar.getParent()).removeView(mFixedBar);
		}
		this.mFixedBar = mBar;
		addView(mBar);
	}

	public void closeExpand() {
		if (mExpandViewShowing) {
			if (mExpandListener != null) {
				mExpandListener.onPreClosed();
			}
			mContentContainer.setVisibility(View.VISIBLE);
			mExpandViewShowing = false;
			mCurrentExpandHeight = mViewHeight - mFixedBarHeight;
			mRepairHeightRunner.startAnim();
		}
	}

	/**
	 * 在 version code 小于 16 时
	 */
	private class OldPainter implements IShadowPainter {
		private BitmapDrawable mDrawable;

		public OldPainter() {
			mDrawable = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.yinpinxiahua_yinying));
		}

		@Override
		public void doDraw(Canvas canvas, float ratio) {
			mDrawable.setBounds(mExpandView.getLeft(), mExpandView.getTop(), mExpandView.getRight(), mExpandView.getBottom());
			// 绘制渐变阴影
			mDrawable.draw(canvas);
			// 翻转，绘制底部阴影
			canvas.save();
			// mDrawable.setBounds(mExpandView.getLeft(), 0,
			// mExpandView.getRight(), mExpandView.getHeight());
			canvas.rotate(180, mExpandView.getWidth() / 2, mExpandView.getTop() + mExpandView.getHeight() / 2);
			mDrawable.draw(canvas);
			canvas.restore();
		}
	};

	/**
	 * 在 version code 大于16时
	 */
	@TargetApi(16)
	private class NewPainter implements IShadowPainter {
		/**
		 * 遮罩阴影
		 */
		private GradientDrawable mShadowDrawable;

		private int[] mShadowColors;

		public NewPainter() {
			mShadowColors = new int[] { mShadhowBlack, 0, 0, 0, mShadhowBlack };
			mShadowDrawable = new GradientDrawable(Orientation.TOP_BOTTOM, null);
			mShadowDrawable.setShape(GradientDrawable.RECTANGLE);
		}

		@Override
		public void doDraw(Canvas canvas, float mPullRatio) {
			int alpha = (int) (mPullRatio * 255 * mDegressAlpha);
			mShadowColors[0] = Color.argb(alpha * 2, 0, 0, 0);
			mShadowColors[1] = Color.argb(alpha, 0, 0, 0);
			mShadowColors[2] = Color.argb(alpha, 0, 0, 0);
			mShadowColors[3] = Color.argb(alpha, 0, 0, 0);
			mShadowColors[4] = Color.argb(alpha * 2, 0, 0, 0);

			// 暂时流注掉，因为FF没有对应的SDK
			// mShadowDrawable.setColors(mShadowColors);

			// 只绘制阴影
			// mShadowPaint.setColor(Color.argb(alpha, 0, 0, 0));
			// canvas.drawRect(mExpandView.getLeft(), mExpandView.getTop(),
			// mExpandView.getRight(), mExpandView.getBottom(),
			// mShadowPaint);

			mShadowDrawable.setBounds(mExpandView.getLeft(), mExpandView.getTop(), mExpandView.getRight(), mExpandView.getBottom());
			// 绘制渐变阴影
			mShadowDrawable.draw(canvas);
		}
	};

	/**
	 * expandView 下拉结束后的，回滚操作
	 * 
	 * @author yanyadi
	 * 
	 */
	private class RepairExpandViewRunner implements Runnable {
		private long mStartTime;
		private int mCurrentScrollY;

		@Override
		public void run() {
			if (run1())
				return;

			long mCurrentTime = SystemClock.currentThreadTimeMillis();
			float mInputValue = (float) (mCurrentTime - mStartTime) / MAX_SETTLE_DURATION;
			float mVector = sInterpolator.getInterpolation(mInputValue);

			if (mVector > 1.0F)
				mVector = 1.0F;

			float height = 0, mTmpScrollHeight = 0;
			if (mExpandViewShowing) {
				mTmpScrollHeight = (mViewHeight - mFixedBarHeight - mCurrentExpandHeight) * mVector;
				height = mCurrentExpandHeight + mTmpScrollHeight;

			} else {
				height = mCurrentExpandHeight * (1.0f - mVector);
				mTmpScrollHeight = height;
			}

			mHeaderContainer.getLayoutParams().height = mFixedBarHeight + (int) height;
			mHeaderContainer.setLayoutParams(mHeaderContainer.getLayoutParams());

			float mRealExpandViewHeight = 0;
			float mScrollHeight = mHeaderContainer.getScrollY() + mTmpScrollHeight;
			if (mHeadScrollHeight != -1 && mScrollHeight > mHeadScrollHeight) {
				mRealExpandViewHeight = height;
				if (mExpandViewShowing)
					mScrollHeight = mHeadScrollHeight;
				else
					mScrollHeight = height;

			} else {
				if (mExpandViewShowing) {
					// 如果展开，并且 headView 还没有完全隐藏掉。 展开的高度是拉开的两倍
					mRealExpandViewHeight = height * 2;
				} else {
					// 如果闭合，并且 headView 还没有完全隐藏掉。 展开的高度是拉开的1/2
					mRealExpandViewHeight = height / 2;
				}
				mScrollHeight = height;
			}
			if (DEBUG)
				Log.d(TAG, " expand anim   fixedBarHeight" + mFixedBarHeight + "   height=" + height + "  realHeight=" + mRealExpandViewHeight);

			mExpandView.getLayoutParams().height = (int) mRealExpandViewHeight;
			mExpandView.setLayoutParams(mExpandView.getLayoutParams());
			mHeaderContainer.scrollTo(0, (int) mScrollHeight);

			if (mVector < 1.0f) {
				post(this);
			} else {
				if (mExpandViewShowing) {
					mExpandView.getLayoutParams().height = mViewHeight - mFixedBarHeight;
					mExpandView.setLayoutParams(mExpandView.getLayoutParams());

					mHeaderContainer.getLayoutParams().height = mViewHeight; // mHeaderHeight
																				// +
																				// (int)
																				// height;
					mHeaderContainer.setLayoutParams(mHeaderContainer.getLayoutParams());
					mHeaderContainer.scrollTo(0, (int) mScrollHeight);

					mContentContainer.setVisibility(View.GONE);

					fireExpandEvent();
				} else if (mExpanded) {
					fireClosedEvent();
				}
			}
		}

		public boolean run1() {

			long mCurrentTime = SystemClock.currentThreadTimeMillis();
			float mInputValue = (float) (mCurrentTime - mStartTime) / MAX_SETTLE_DURATION;
			float mVector = sInterpolator.getInterpolation(mInputValue);

			if (mVector > 1.0F)
				mVector = 1.0F;

			float height = 0, mTmpScrollHeight = 0;
			if (mExpandViewShowing) {
				// 剩余的高度，就要滚动到顶部了
				mTmpScrollHeight = (mViewHeight - mFixedBarHeight - mCurrentExpandHeight) * mVector;
				// 求出当前 headerContainer 的高度
				height = mCurrentExpandHeight + mTmpScrollHeight;

				int mCurrentScrollHeight = mHeaderContainer.getScrollY(), mTmpContainerHeight;
				int mTmpExpandHeight = 0;
				// 如果当前卷到顶部的 高度 小于可以卷动的最大高度
				if (mCurrentScrollHeight < mHeadScrollHeight) {
					int mTmpCurScrollHeight = mCurrentScrollHeight + (int) mTmpScrollHeight;
					if (mCurrentScrollHeight + mTmpScrollHeight > mHeadScrollHeight)
						mTmpCurScrollHeight = mHeadScrollHeight;

					mTmpExpandHeight = (int) height * 2;

					mHeaderContainer.scrollTo(0, (int) mTmpCurScrollHeight);

					mTmpContainerHeight = mHeaderHeight + (int) height;
				} else {
					mTmpExpandHeight = (int) height;
					mTmpContainerHeight = mHeadScrollHeight + (int) height;
					if (mTmpContainerHeight > mViewHeight)
						mTmpContainerHeight = mViewHeight;
				}
				mHeaderContainer.getLayoutParams().height = mTmpContainerHeight;
				mHeaderContainer.setLayoutParams(mHeaderContainer.getLayoutParams());

				mExpandView.getLayoutParams().height = mTmpExpandHeight;
				mExpandView.setLayoutParams(mExpandView.getLayoutParams());
			} else {
				// 求出当前 headerContainer 的高度
				mTmpScrollHeight = mCurrentExpandHeight * mVector;
				height = mCurrentExpandHeight - mTmpScrollHeight;

				int mCurrentScrollHeight = mHeaderContainer.getScrollY(), mTmpContainerHeight;
				int mTmpExpandHeight = 0;
				// 如果当前卷到顶部的 高度 小于可以卷动的最大高度
				if (mCurrentScrollHeight != 0) {
					int mTmpCurScrollHeight = (int) (mCurrentScrollY * (1 - mVector));
					if (DEBUG)
						Log.i(TAG, "close  ==" + mTmpCurScrollHeight);
					if (mTmpCurScrollHeight < 0)
						mTmpCurScrollHeight = 0;

					mTmpExpandHeight = (int) height;// (int)(height -
													// mTmpCurScrollHeight); //
													// (int)(
													// mCurrentExpandHeight -
													// mTmpCurScrollHeight -
													// mTmpScrollHeight);

					mHeaderContainer.scrollTo(0, (int) mTmpCurScrollHeight);

					mTmpContainerHeight = mTmpExpandHeight + mFixedBarHeight + (mHeadScrollHeight - mTmpCurScrollHeight);// mViewHeight
																															// -
																															// (int)mTmpCurScrollHeight
																															// -
																															// (int)mTmpScrollHeight;
				} else {
					mTmpExpandHeight = (int) height;
					mTmpContainerHeight = mHeaderHeight + (int) height;
					if (mTmpContainerHeight < mHeaderHeight)
						mTmpContainerHeight = mHeaderHeight;
				}
				mHeaderContainer.getLayoutParams().height = mTmpContainerHeight;
				mHeaderContainer.setLayoutParams(mHeaderContainer.getLayoutParams());

				mExpandView.getLayoutParams().height = mTmpExpandHeight;
				mExpandView.setLayoutParams(mExpandView.getLayoutParams());
			}
			// mExpandView.getLayoutParams().height = (int)
			// mRealExpandViewHeight;
			// mExpandView.setLayoutParams(mExpandView.getLayoutParams());
			//
			//
			//
			// float mRealExpandViewHeight = 0;
			// float mScrollHeight = mHeaderContainer.getScrollY() +
			// mTmpScrollHeight;
			// if(mHeadScrollHeight != -1 && mScrollHeight > mHeadScrollHeight){
			// mRealExpandViewHeight = height;
			// if(mExpandViewShowing)
			// mScrollHeight = mHeadScrollHeight;
			// else
			// mScrollHeight = height;
			//
			// }else{
			// if(mExpandViewShowing)
			// {
			// //如果展开，并且 headView 还没有完全隐藏掉。 展开的高度是拉开的两倍
			// mRealExpandViewHeight = height * 2 ;
			// }else{
			// //如果闭合，并且 headView 还没有完全隐藏掉。 展开的高度是拉开的1/2
			// mRealExpandViewHeight = height / 2;
			// }
			// mScrollHeight = height;
			// }
			// Log.d(TAG, " expand anim   fixedBarHeight"+ mFixedBarHeight +
			// "   height=" + height + "  realHeight="+ mRealExpandViewHeight);
			//
			//
			// mExpandView.getLayoutParams().height = (int)
			// mRealExpandViewHeight;
			// mExpandView.setLayoutParams(mExpandView.getLayoutParams());
			// mHeaderContainer.scrollTo(0, (int) mScrollHeight);

			if (mVector < 1.0f) {
				post(this);
			} else {
				if (mExpandViewShowing) {
					mExpandView.getLayoutParams().height = mViewHeight - mFixedBarHeight;
					mExpandView.setLayoutParams(mExpandView.getLayoutParams());

					mHeaderContainer.getLayoutParams().height = mViewHeight; // mHeaderHeight
																				// +
																				// (int)
																				// height;
					mContentContainer.setVisibility(View.GONE);

					fireExpandEvent();
				} else if (mExpanded) {
					fireClosedEvent();
				}
			}

			return true;
		}

		public void startAnim() {
			mStartTime = SystemClock.currentThreadTimeMillis();
			mCurrentScrollY = mHeaderContainer.getScrollY();
			post(this);
		}

	}

	/**
	 * 触发 expand 事件
	 */
	private void fireExpandEvent() {
		mExpanded = true;
		if (mExpandListener != null)
			mExpandListener.onExpand();
	}

	/**
	 * 触发 expand 事件
	 */
	private void fireClosedEvent() {
		mExpanded = false;
		if (mExpandListener != null)
			mExpandListener.onClose();
	}

	public void setHeaderView(View header, View expand) {
		if (mHeaderContainer != null) {
			mHeaderContainer.removeAllViews();

			mHeaderView = header;
			LayoutParams contentParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			contentParams.gravity = Gravity.TOP;
			mHeaderContainer.addView(header, contentParams);

			mExpandView = expand;
			LayoutParams expandParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
			contentParams.gravity = Gravity.BOTTOM;
			mHeaderContainer.addView(expand, expandParams);

		}
	}

	/**
	 * 
	 * @author yanyadi
	 * 
	 */
	private class InnernalExpandLayout extends LinearLayout {

		public InnernalExpandLayout(Context context) {
			super(context);
		}

		public InnernalExpandLayout(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void dispatchDraw(Canvas canvas) {
			super.dispatchDraw(canvas);
			drawExpandShadow(canvas);
		}
	}

	/**
	 * 绘制 expand view 阴影
	 * 
	 * @param canvas
	 */
	private void drawExpandShadow(Canvas canvas) {

		if (!mExpanded && mCurrentExpandHeight != 0) {

			float mPullRatio = (float) (mCurrentExpandHeight / (float) mHeaderHeight);
			if (mPullRatio >= 1) {
				mPullRatio = 0;
			} else {
				mPullRatio = 1 - mPullRatio;
			}
			mShadowPainter.doDraw(canvas, mPullRatio);
			// 如果 当前系统版本大于 JELLY_BEAN ，即可以使用
			if (VERSION.SDK_INT >= 16) {

			} else {

			}
		}
	}

	public void setContentView(View conView) {
		if (mContentContainer != null) {
			mContentContainer.addView(conView);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mFixedBar != null) {
			int c = this.getChildCount();
			for(int i=0; i < c; i++){
				View child = getChildAt(i);
				if(child==mFixedBar)continue;
				child.layout(l, 0, r, b-t);
			}
			if(mFixedBar.getVisibility() != View.GONE)
				mFixedBar.layout(l,0, r, mFixedBar.getMeasuredHeight());
		} else {
			super.onLayout(changed, l, t, r, b);
		}
		// mHeaderContainer
		if (mHeaderHeight == 0) {
			if (mHeaderView != null)
				mHeaderHeight = mHeaderView.getHeight();
			mViewHeight = getHeight();
			mFixedBarHeight = mHeaderHeight - mHeadScrollHeight;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		
		int c = this.getChildCount();
		int mBarWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
		for(int i=0; i < c; i++){
			View child = getChildAt(i);
			if(child == mFixedBar){
				int mBarHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				child.measure(mBarWidthSpec , mBarHeightSpec);
				mStatusBarHeight = child.getMeasuredHeight();
			}else{
				measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
			}
		}
		setMeasuredDimension(width, height);
	}
	private ViewGroup initContentContainer(AttributeSet attrs) {
		mScrollview = new InternalScrollView(getContext(), attrs);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mScrollview.setLayoutParams(lp);
		mScrollview.setScrollListener(mScrollListener);

		LayoutParams child_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		mRootContentContainer = new LinearLayout(getContext());
		mRootContentContainer.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mRootContentContainer.setOrientation(LinearLayout.VERTICAL);
		mRootContentContainer.setLayoutParams(child_lp);
		mScrollview.addView(mRootContentContainer);

		LayoutParams child_content_lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mContentContainer = new LinearLayout(getContext());
		mContentContainer.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mContentContainer.setOrientation(LinearLayout.VERTICAL);
		mContentContainer.setLayoutParams(child_content_lp);
		mRootContentContainer.addView(mContentContainer);

		return mScrollview;
	}

	private class InternalScrollView extends ScrollView {

		private OnScrollListener mScrollListener;

		public void setScrollListener(OnScrollListener mScrollListener) {
			this.mScrollListener = mScrollListener;
		}

		public InternalScrollView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			// TODO Auto-generated method stub
			super.onScrollChanged(l, t, oldl, oldt);

			if (mScrollListener != null)
				mScrollListener.onScrollChanged(l, t, oldl, oldt);
		}

	}

	private OnScrollListener mScrollListener = new OnScrollListener() {

		@Override
		public void onScrollChanged(int l, int t, int oldl, int oldt) {
			// channel 面板已经被遮住了
			if(mFixedBar!=null)
				if(mHeaderHeight - mStatusBarHeight < t )
					mFixedBar.setVisibility(View.VISIBLE);
				else
					mFixedBar.setVisibility(View.GONE);
		}
	};

	/**
	 * InnernalScrollView 滚动事件
	 * 
	 * @author yanyadi
	 * 
	 */
	public static interface OnScrollListener {
		void onScrollChanged(int l, int t, int oldl, int oldt);
	}

	private ViewGroup makeHeaderContainer() {
		android.view.ViewGroup.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		LinearLayout mFrame = new InnernalExpandLayout(this.context);
		mFrame.setOrientation(LinearLayout.VERTICAL);
		mFrame.setClipChildren(false);
		mFrame.setLayoutParams(lp);
		return mFrame;
	}

	/**
	 * 是否可以向下拉开 expandView
	 * 
	 * @return
	 */
	private boolean canPullDown() {
		/****
		 * 1. scrollView 没有滚动。 2. 并且 expandview 没有展开
		 * 
		 */
		return mScrollview.getScrollY() <= 0 && !mExpanded;
	}

	/**
	 * 判断滑动的方向 是不是 上下 滑动。
	 * 
	 * @param event
	 */
	private void determineDrag(MotionEvent event) {

		if (mIsBeginDragged || mIsUnableDragged)
			return;

		final float mCurrentX = event.getX();

		float mXDiff = Math.abs(mCurrentX - mInitialMotionX);
		// 如果是横向滑动，则此次操作将失效
		if (mXDiff > mMinTouchSlop)
			mIsUnableDragged = true;

		final float mCurrentY = event.getY();
		float mYDiff = mCurrentY - mInitialMotionY;
		// 如果是纵向滑动，此次操作是允许的
		if (mYDiff > mMinTouchSlop) {
			if (canPullDown()) {
				// 如果是 scrollView 在顶部，并且是向下滑动。是允许的
				mLastMotionX = mInitialMotionX = event.getX();
				mLastMotionY = mInitialMotionY = event.getY();
				mIsBeginDragged = true;
			}
		}
	}

	/**
	 * 重置此次 触摸事件
	 */
	private void endDragged() {
		mIsBeginDragged = false;
		mIsUnableDragged = false;
		mRepairHeightRunner.startAnim();
	}

	// 只在事件 开始时 调用一次
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int mScrollContainerTop = mScrollview.getTop();

		final int action = ev.getAction();
		if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			mIsBeginDragged = false;
			return false;
		}
		if (action != MotionEvent.ACTION_DOWN && mIsBeginDragged)
			return true;

		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			if (DEBUG)
			Log.i(TAG, " top view ACTION_MOVE " + mScrollContainerTop);
			if (!mIsBeginDragged) {
				determineDrag(ev);
				if (mIsUnableDragged) {
					if (DEBUG)
						Log.d(TAG, " top view ACTION_MOVE mIsUnableDragged true");
					return false;
				}
			}

		}
			break;
		case MotionEvent.ACTION_DOWN:
			if (DEBUG)
				Log.i(TAG, " top view ACTION_DOWN " + mScrollContainerTop);
			mIsBeginDragged = mIsUnableDragged = false;
			if (mExpanded)
				return false;

			mLastMotionX = mInitialMotionX = ev.getX();
			mLastMotionY = mInitialMotionY = ev.getY();

			break;
		}
		return mIsBeginDragged;
	}

	private float mLastMotionX, mLastMotionY, mInitialMotionX, mInitialMotionY;
	private boolean mIsBeginDragged, mIsUnableDragged;
	private int mInitialScrollY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (DEBUG)
				Log.i(TAG, "EVENT_FLAG = " + event.getEdgeFlags());
		}

		switch (event.getAction()) {
		// 按下事件， 保存第一次按下的 x 坐标，y坐标
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = mInitialMotionX = event.getX();
			mLastMotionY = mInitialMotionY = event.getY();
			if (DEBUG)
				Log.i(TAG, "ACTION_DOWN  EVENT_FLAG = " + event.getEdgeFlags());
			return true;
		case MotionEvent.ACTION_MOVE:
			// 如果mScrollView 没有滚动到最上面
			if (mIsBeginDragged) {

				mLastMotionY = event.getY();

				float mPullHeight = mLastMotionY - mInitialMotionY;
				// 如果是向上滑动，是不允许的。
				if (mPullHeight < 0)
					mPullHeight = 0;
				updateExpandView(mPullHeight * mDamping);
				if (DEBUG)
					Log.i(TAG, "ACTON_MOVE  mPullHeight =" + mPullHeight);
				return true;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (DEBUG)
				Log.i(TAG, "ACTION_UP ");
			endDragged();
			break;
		}

		return true;
	}

	private void updateExpandView(float height) {
		if (mExpandView != null) {
			// 如果拉开的高度，大于 headerHeight 高度的 1/3 。
			if (height >= mHeaderHeight / 3) {
				mExpandViewShowing = true;
				// height = mHeaderHeight;
			} else {
				mExpandViewShowing = false;
			}

			float mRealExpandViewHeight = height;

			// 设置 headerContainer 的高度。它的高度为 原来的高度 加上已经拉开的距离
			mHeaderContainer.getLayoutParams().height = mHeaderHeight + (int) height;
			mHeaderContainer.setLayoutParams(mHeaderContainer.getLayoutParams());

			if (mHeadScrollHeight != -1 && height > mHeadScrollHeight) {
				mRealExpandViewHeight = height + mHeadScrollHeight;
				height = mHeadScrollHeight;
			} else {
				mRealExpandViewHeight = height * 2;
			}
			mExpandView.getLayoutParams().height = (int) mRealExpandViewHeight;
			mExpandView.setLayoutParams(mExpandView.getLayoutParams());

			// mHeaderContainer.getLayoutParams().height = mHeaderHeight + (int)
			// mRealExpandViewHeight + mHeaderHeight - mHeadScrollHeight ;

			mCurrentExpandHeight = mRealExpandViewHeight;

			mHeaderContainer.scrollTo(0, (int) height);

		}
	}

	public boolean isExpand() {
		return mExpanded;
	}

	public static interface OnExpandListener {
		void onExpand();

		void onClose();

		void onPreClosed();
	}

	/**
	 * 为兼容多版本，抽象出一个绘制阴影接口
	 * 
	 * @author yanyadi
	 * 
	 */
	public static interface IShadowPainter {
		void doDraw(Canvas canvas, float pullRatio);
	}
}
