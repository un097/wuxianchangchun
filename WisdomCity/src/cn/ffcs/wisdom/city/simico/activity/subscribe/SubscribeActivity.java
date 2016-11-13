package cn.ffcs.wisdom.city.simico.activity.subscribe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.kit.activity.PSFragmentActivity;
import cn.ffcs.wisdom.city.simico.activity.subscribe.view.CategoryFlowLayout;
import cn.ffcs.wisdom.city.simico.activity.subscribe.view.Channel;
import cn.ffcs.wisdom.city.simico.activity.subscribe.view.DragListener;
import cn.ffcs.wisdom.city.simico.activity.subscribe.view.GridFlowLayout;

//import com.actionbarsherlock.app.ActionBar;

public class SubscribeActivity extends PSFragmentActivity implements
		DragListener {
	protected static final String TAG = SubscribeActivity.class.getSimpleName();
	// protected static List<Channel> h = new ArrayList<Channel>();
	protected List<Channel> mAllChannel = new ArrayList<Channel>();
	protected List<Channel> mMyChannel = new ArrayList<Channel>();
	protected CategoryFlowLayout mFvMyChannel;
	protected GridFlowLayout mFvAllChannel;
	private TextView j;
	private View mChangeChannelView;
	private View mTitleBar;
	private boolean isUpdating;
	protected boolean c;
	private int[] sourceLocation = new int[2];
	private int[] titleLocation = new int[2];
	private int[] targetLocation = new int[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simico_activity_subscribe);
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setCustomView(new TextView(this));
//		actionBar.setDisplayShowCustomEnabled(true);
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//		mTitleBar = actionBar.getCustomView();
		mTitleBar = findViewById(R.id.titlebar_subscribe);

		mFvMyChannel = (CategoryFlowLayout) findViewById(R.id.my_category);
		mFvAllChannel = (GridFlowLayout) findViewById(R.id.more_category);
		j = (TextView) findViewById(R.id.text_item);
		mChangeChannelView = findViewById(R.id.category_layout);
		initChannelView(mMyChannel, mFvMyChannel, true);
		// initChannelView(mAllChannel, mFvAllChannel, true);
		mFvMyChannel.setOnCateDragListener(this);
	}

	protected void initChannelView(List<Channel> data, ViewGroup container,
			boolean init) {
		if (container != null && data != null) {
			if (init) {
				container.removeAllViews();
			}
			final int size = data.size();
			for (int i = 0; i < size; i++) {
				Channel channel = data.get(i);
				if (channel != null) {
					View view = container.getChildAt(i);
					if (view == null) {
						view = getLayoutInflater().inflate(
								R.layout.subcribe_category_item, null, false);
						view.setDrawingCacheEnabled(true);
					}
					if (init) {
						View icon = view.findViewById(R.id.icon_new);
						icon.setVisibility(channel.hasIcon ? View.VISIBLE
								: View.GONE);
						((TextView) view.findViewById(R.id.text_item)).setText(
								getDisplayName(channel), BufferType.SPANNABLE);
						view.setTag(channel);
						container.addView(view);
						if ("__all__".equals(channel.b)) {
							view.setEnabled(false);
						} else {
							view.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									updateChannel(v);
								}
							});
							if (channel.isMy)
								view.setOnLongClickListener(new OnLongClickListener() {

									@Override
									public boolean onLongClick(View v) {
										mFvMyChannel.initDrag(v);
										v.setSelected(true);
										v.setEnabled(true);
										Object obj = v.getTag();
										if (obj instanceof Channel) {
											Channel channel = (Channel) obj;
											Log.d("", "drag_mine_" + channel.b);
										}
										return true;
									}
								});
						}
					}
				}
			}
			container.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
			container.requestLayout();
		}
	}

	@Override
	public boolean onDrag(int action, View moveView, View sourceView) {
		Log.d(TAG, "onDrag action:" + action);
		boolean flag = false;
		TextView tvSouce = null;
		if (sourceView != null)
			tvSouce = (TextView) sourceView.findViewById(R.id.text_item);
		switch (action) {
		case 1: // 开始拖动
			if (tvSouce != null) {
				tvSouce.setVisibility(View.INVISIBLE);
				flag = true;
			} else {
				flag = false;
			}
			break;
		case 2:// 正在拖动
			flag = handleMoveDrag(sourceView, moveView);
			break;
		case 3:// 停止拖动
		case 4:
			if (tvSouce != null)
				tvSouce.setVisibility(View.VISIBLE);
			resetData(mFvMyChannel, mMyChannel);
			resetData(mFvAllChannel, mAllChannel);
			if (sourceView != null)
				sourceView.setSelected(false);
			flag = true;
			break;
		default:
			break;
		}
		return flag;
	}

	protected boolean handleMoveDrag(View sourceView, View moveView) {
		Channel sourceChannel = null;
		Channel moveChannel = null;
		boolean flag = false;
		if (sourceView != null && (sourceView.getTag() instanceof Channel))
			sourceChannel = (Channel) sourceView.getTag();
		if (moveView != null && (moveView.getTag() instanceof Channel))
			moveChannel = (Channel) moveView.getTag();

		if (sourceChannel == null || moveChannel == null)
			flag = false;
		else if (moveView.equals(sourceView))
			flag = false;
		else if (sourceChannel.isMy != moveChannel.isMy)
			flag = false;
		else if (sourceChannel.isMy)
			flag = handleChangeLocation(moveView, sourceView, mFvMyChannel);
		else
			flag = handleChangeLocation(moveView, sourceView, mFvAllChannel);
		return flag;
	}

	/**
	 * 处理正在拖拽视图时是否要交换位置，如果满足条件，则交换双方位置
	 * 
	 * @param moveView
	 *            移动到moveView视图上方
	 * @param sourceView
	 *            被拖动的视图
	 * @param container
	 *            容器
	 * @return 是否交换成功
	 */
	protected boolean handleChangeLocation(View moveView, View sourceView,
			ViewGroup container) {
		if (container == null || moveView == null || !moveView.isEnabled()
				|| sourceView == null) {
			return false;
		}
		int childCount = container.getChildCount();
		if (childCount > 0) {
			int targetIdx = -1;
			View findView = null;
			for (int i = 0; i < childCount; i++) {
				View childView = container.getChildAt(i);
				if (childView.equals(moveView)) {
					targetIdx = i;
				}
				if (childView.equals(sourceView)) {
					findView = childView;
				}
			}
			if (targetIdx >= 0 && findView != null) {
				sourceView.setSelected(true);
				sourceView.setEnabled(true);
				container.removeView(sourceView);
				container.addView(sourceView, targetIdx);
				c = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * 在拖动排序结束后，调用此方法重新设置原数据排序
	 * 
	 * @param container
	 * @param list
	 *            原数据
	 * @return
	 */
	protected boolean resetData(ViewGroup container, List<Channel> list) {
		if (container != null && !list.isEmpty()) {
			final int count = container.getChildCount();
			if (count <= 0) {
				return false;
			}
			list.clear();
			for (int i = 0; i < count; i++) {
				View view = container.getChildAt(i);
				if (view != null) {
					Object tag = view.getTag();
					if (tag instanceof Channel) {
						list.add((Channel) tag);
					}
				}
			}
			initChannelView(list, container, false);
			return true;
		}
		return false;
	}

	private void updateChannel(final View view) {
		if (!isUpdating) {
			isUpdating = true;
			Object obj = view.getTag();
			if (!(obj instanceof Channel)) {
				isUpdating = false;
			} else {
				Channel channel = (Channel) obj;
				channel.isMy = !channel.isMy;
				if (channel.isMy) {
					// h.add(channel);
					mMyChannel.add(channel);
					mAllChannel.remove(channel);
					log("subscribe_add_" + channel.b);
				} else {
					// h.remove(channel);
					mAllChannel.add(channel);
					mMyChannel.remove(channel);
					log("subscribe_remove_" + channel.b);
				}

				j.setText(getDisplayName(channel));

				view.getLocationInWindow(sourceLocation);

				final ViewGroup targetContainer = channel.isMy ? mFvMyChannel
						: mFvAllChannel;

				View newView = getLayoutInflater().inflate(
						R.layout.subcribe_category_item, null, false);
				TextView tvItem = (TextView) newView
						.findViewById(R.id.text_item);

				tvItem.setText(getDisplayName(channel),
						TextView.BufferType.SPANNABLE);
				targetContainer.addView(newView);
				newView.setSelected(true);
				newView.setEnabled(true);
				tvItem.setVisibility(View.INVISIBLE);
				View iconNew = newView.findViewById(R.id.icon_new);
				iconNew.setVisibility(channel.hasIcon ? View.VISIBLE
						: View.INVISIBLE);

				targetContainer.post(new Runnable() {

					@Override
					public void run() {
						int count = targetContainer.getChildCount();
						if (count > 0) {
							View view = targetContainer.getChildAt(count - 1);
							if (view != null)
								view.getLocationInWindow(targetLocation);
						} else {
							targetContainer.getLocationInWindow(targetLocation);
						}
						mTitleBar.getLocationInWindow(titleLocation);
						// titleLocation[1] = getStatuBarHeight();
						Log.d(TAG, "titlebar location x:" + titleLocation[0]
								+ ",y:" + titleLocation[1]
								+ " statusbarheight:" + getStatuBarHeight()
								+ " viewheight:" + view.getHeight());
//						int height = getStatuBarHeight()
//								+ getSupportActionBar().getHeight();
						int height = mTitleBar.getHeight();

						starChannelAnim(mChangeChannelView,
								sourceLocation[0]/* fromXValue */,
								sourceLocation[1] - height/*
														 * 2 * view.getHeight()
														 * titleLocation[1]-20
														 */,
								targetLocation[0]/* toXValue */,
								targetLocation[1] - height/*
														 * 2 * view.getHeight()
														 * titleLocation[1]-20
														 */);

						mChangeChannelView.setVisibility(View.VISIBLE);

						TextView tv = (TextView) view
								.findViewById(R.id.text_item);
						if (tv != null) {
							tv.setVisibility(View.INVISIBLE);
						}
					}
				});
				
				saveChannelData();
			}
		}
	}

	protected void starChannelAnim(View view, float fromXValue,
			float fromYValue, float toXValue, float toYValue) {
		TranslateAnimation anim = new TranslateAnimation(Animation.ABSOLUTE,
				fromXValue, Animation.ABSOLUTE, toXValue, Animation.ABSOLUTE,
				fromYValue, Animation.ABSOLUTE, toYValue);
		anim.setDuration(300L);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mChangeChannelView.setVisibility(View.GONE);
				initChannelView(mMyChannel, mFvMyChannel, true);
				initChannelView(mAllChannel, mFvAllChannel, true);
				c = true;
				isUpdating = false;
			}
		});
		view.setAnimation(anim);
		anim.startNow();
	}

	protected String getDisplayName(Channel bt1) {
		String s2 = "";
		if (bt1 != null) {
			String s1 = bt1.c;
			if (bt1.b.equals("news_local")) {
				// s2 = g.y();
				// if (!az.a(s2))
				// continue; /* Loop/switch isn't completed */
			}
			s2 = s1;
		}// goto _L2; else goto _L1
		return s2;
	}
	
	protected void saveChannelData() {
		
	}

	protected void log(String log) {
		Log.d(TAG, log);
	}

	public int getStatuBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 38;// 默认为38，貌似大部分是这样的
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = getResources().getDimensionPixelSize(x);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	private int getTitleBarHeight(int statusBarHeight) {
		int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();
		// statusBarHeight是上面所求的状态栏的高度
		int titleBarHeight = contentTop - statusBarHeight;
		// int contentTop =
		// getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// statusBarHeight是上面所求的状态栏的高度
		// int titleBarHeight = contentTop - statusBarHeight;
		return titleBarHeight;
	}
}
