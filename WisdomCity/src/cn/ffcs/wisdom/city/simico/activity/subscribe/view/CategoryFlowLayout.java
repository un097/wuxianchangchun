package cn.ffcs.wisdom.city.simico.activity.subscribe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

public class CategoryFlowLayout extends GridFlowLayout {

	private static final String TAG = CategoryFlowLayout.class.getSimpleName();
	private int rawX;
	private int rawY;
	private int mPointerId = -1;
	private boolean mDragging = false;
	private boolean mDraggable = false;
	private Rect currentMoveToViewRect = new Rect();
	private final int[] location = new int[2];
	private View mSourceView;
	private DragView mDragView;
	private DragListener mDragListener;

	public CategoryFlowLayout(Context context) {
		super(context);
	}

	public CategoryFlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CategoryFlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void a(MotionEvent event) {
		int index = (0xff00 & event.getAction()) >> 8;
		if (event.getPointerId(index) == mPointerId) {
			int pointerIndex = index == 0 ? 1 : 0;
			rawY = (int) event.getY(pointerIndex);
			mPointerId = event.getPointerId(pointerIndex);
		}
	}

	private View getMoveView() {
		int size = getChildCount();
		View view = null;
		for (int i = 0; i < size; i++) {
			view = getChildAt(i);
			view.getHitRect(currentMoveToViewRect);
			if (currentMoveToViewRect.contains(rawX - location[0], rawY - location[1])) {
				return view;
			}
		}
		return null;
	}

	public void initDrag(View view) {
		if (mDraggable) {
			view.setSelected(true);
			view.setEnabled(false);
			mSourceView = view;
			Bitmap bitmap = mSourceView.getDrawingCache();
			if (bitmap != null) {
				int[] bitmapWH = new int[2];
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				mSourceView.getLocationOnScreen(bitmapWH);
				int initX = rawX - bitmapWH[0];
				int initY = rawY - bitmapWH[1];
				mDragView = new DragView(getContext(), bitmap, initX, initY, 0, 0, width, height);
				mDragView.a(getApplicationWindowToken(), rawX, rawY);
			}
			mDragging = true;
			ViewParent parent = getParent();
			if (parent != null)
				parent.requestDisallowInterceptTouchEvent(true);
			if (mDragListener != null) {
				mDragListener.onDrag(1, getMoveView(), view);
			}
			invalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getAction();
		getLocationOnScreen(location);
		if (action == MotionEvent.ACTION_DOWN) {
			mPointerId = event.getPointerId(0);
			rawX = (int) event.getRawX();
			rawY = (int) event.getRawY();
			mDraggable = true;
			mDragging = false;
		}
		switch (action & 0xff) {
		case 1:// MotionEvent.ACTION_UP:
			if (mDragging) {
				mDragging = false;
				if (mDragListener != null) {
					View view = getMoveView();
					mDragListener.onDrag(3, view, mSourceView);
					mDragListener.onDrag(4, null, null);
					mSourceView = null;
				}
				invalidate();
				if (mDragView != null)
					mDragView.remove();
			}
			mPointerId = -1;
			mDraggable = false;
			break;
		case 2:// MotionEvent.ACTION_MOVE:
			int index = event.findPointerIndex(mPointerId);
			rawX = (int) event.getX(index) + location[0];
			rawY = (int) event.getY(index) + location[1];
			break;
		case 3:// MotionEvent.ACTION_CANCEL:
			if (mDragging) {
				if (mDragListener != null) {
					mDragListener.onDrag(4, null, mSourceView);
					mSourceView = null;
				}
				invalidate();
				if (mDragView != null)
					mDragView.remove();
			}
			mDragging = false;
			mPointerId = -1;
			mDraggable = false;
			break;
		case 4:
		case 5:
			return mDragging;
		case 6:// MotionEvent.ACTION_POINTER_UP:
			a(event);
			break;
		default:
			break;
		}
		return mDragging;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		getLocationOnScreen(location);
		boolean flag = mDragging;
		switch (action & 0xff) {
		case 1:// MotionEvent.ACTION_UP:
			if (mDragging) {
				mDragging = false;
				mPointerId = -1;
				if (mDragListener != null) {
					View view = getMoveView();
					mDragListener.onDrag(3, view, mSourceView);
					mDragListener.onDrag(4, null, null);
					mSourceView = null;
				}
				invalidate();
				if (mDragView != null)
					mDragView.remove();
			}
			mDraggable = false;
			break;
		case 2:// MotionEvent.ACTION_MOVE:
			int pointIdx = event.findPointerIndex(mPointerId);
			if (pointIdx == -1) {
				Log.d("CategoryFlowLayout", "Invalid pointerId=" + mPointerId
						+ " in onTouchEvent");
			} else {
				int x = (int) event.getX(pointIdx) + location[0];
				int y = (int) event.getY(pointIdx) + location[1];
				rawX = x;
				rawY = y;
				if (mDragging) {
					if (mDragListener != null) {
						mDragListener.onDrag(2, getMoveView(), mSourceView);
					}
					if (mDragView != null) {
						mDragView.moveTo(rawX, rawY);
					}
				}
			}
			break;
		case 3:// MotionEvent.ACTION_CANCEL:
			mDraggable = false;
			if (mDragging) {
				mPointerId = -1;
				mDragging = false;
				if (mDragListener != null) {
					mDragListener.onDrag(4, null, null);
					mSourceView = null;
				}
				invalidate();
				if (mDragView != null)
					mDragView.remove();
			}
			break;
		default:
			break;
		}
		return flag;
	}

	protected void logAction(String string, int action) {
		String msg = "";
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			msg = "ACTION_DOWN";
			break;
		case MotionEvent.ACTION_MOVE:
			msg = "ACTION_MOVE";
			break;
		case MotionEvent.ACTION_UP:
			msg = "ACTION_UP";
			break;
		case MotionEvent.ACTION_CANCEL:
			msg = "ACTION_CANCEL";
			break;
		default:
			break;
		}
		Log.d(TAG, string + ": " + msg);
	}

	public void setOnCateDragListener(DragListener bs1) {
		mDragListener = bs1;
	}
}
