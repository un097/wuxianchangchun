package com.ctbri.wxcc.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.ctbri.wxcc.R;

/**
 * 方形 imageview ，在layout之后。按指定的比例，重新计算高度，使自己变成方形
 * 
 * @author yanyadi
 * 
 */
public class SquareImageView extends ImageView {

	private int size;
	private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private static final int COLORDRAWABLE_DIMENSION = 2;

	private static final int DEFAULT_BORDER_WIDTH = 0;
	private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
	private static final boolean DEFAULT_BORDER_OVERLAY = false;
	private static final int DEFAULT_IMAGE_RADIUS = 8;

	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();

	private final Matrix mShaderMatrix = new Matrix();
	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();

	private int mBorderColor = DEFAULT_BORDER_COLOR;
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;

	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	private int mBitmapWidth;
	private int mBitmapHeight;
	private int mCalcHeight;

	private float mDrawableRadius;
	private float mBorderRadius;
	/**
	 * 高度与宽度的比值
	 */
	private float mHeightRatio;
	/**
	 * 默认的图片高度比值
	 */
	private static final float DEFAULT_HEIGHT_RATIO = 1.0f;

	private ColorFilter mColorFilter;

	private boolean mReady;
	private boolean mSetupPending;
	private boolean mBorderOverlay;
	private int mRadius;

	private RectF mBounds;

	public SquareImageView(Context context) {
		super(context);
		init();
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.square_imageview_style, defStyle, 0);
		// 获取圆角大小
		mRadius = a.getDimensionPixelSize(R.styleable.square_imageview_style_radius, DEFAULT_IMAGE_RADIUS);
		// 获取高度比值
		mHeightRatio = a.getFloat(R.styleable.square_imageview_style_heightRatio, DEFAULT_HEIGHT_RATIO);
		mBorderWidth = 0;// a.getDimensionPixelSize(R.styleable.CircleImageView_border_width,
							// DEFAULT_BORDER_WIDTH);
		mBorderColor = DEFAULT_BORDER_COLOR; // a.getColor(R.styleable.CircleImageView_border_color,
												// DEFAULT_BORDER_COLOR);
		mBorderOverlay = false;// a.getBoolean(R.styleable.CircleImageView_border_overlay,
								// DEFAULT_BORDER_OVERLAY);

		a.recycle();

		init();
	}

	private void init() {
		super.setScaleType(SCALE_TYPE);
		mReady = true;

		if (mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	@Override
	public ScaleType getScaleType() {
		return SCALE_TYPE;
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (scaleType != SCALE_TYPE) {
			throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
		}
	}

	@Override
	public void setAdjustViewBounds(boolean adjustViewBounds) {
		if (adjustViewBounds) {
			throw new IllegalArgumentException("adjustViewBounds not supported.");
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}

		// canvas.drawRoundRect(mBounds,(float)(getWidth() / 2), (float)
		// getHeight() / 2, mBitmapPaint);
		canvas.drawRoundRect(mBounds, mRadius, mRadius, mBitmapPaint);
		// canvas.drawRoundRect((float) (getWidth() / 2), (float) getHeight() /
		// 2, mDrawableRadius, mBitmapPaint);
		// if (mBorderWidth != 0) {
		// canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius,
		// mBorderPaint);
		// }
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setup();
	}

	public int getBorderColor() {
		return mBorderColor;
	}

	public void setBorderColor(int borderColor) {
		if (borderColor == mBorderColor) {
			return;
		}

		mBorderColor = borderColor;
		mBorderPaint.setColor(mBorderColor);
		invalidate();
	}

	public void setBorderColorResource(int borderColorRes) {
		setBorderColor(getContext().getResources().getColor(borderColorRes));
	}

	public int getBorderWidth() {
		return mBorderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		if (borderWidth == mBorderWidth) {
			return;
		}

		mBorderWidth = borderWidth;
		setup();
	}

	public boolean isBorderOverlay() {
		return mBorderOverlay;
	}

	public void setBorderOverlay(boolean borderOverlay) {
		if (borderOverlay == mBorderOverlay) {
			return;
		}

		mBorderOverlay = borderOverlay;
		setup();
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
		setup();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mBitmap = getBitmapFromDrawable(drawable);
		setup();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		if (cf == mColorFilter) {
			return;
		}

		mColorFilter = cf;
		mBitmapPaint.setColorFilter(mColorFilter);
		invalidate();
	}

	private Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		try {
			Bitmap bitmap;

			if (drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
			}

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	private void setup() {
		if (!mReady) {
			mSetupPending = true;
			return;
		}

		if (mBitmap == null) {
			return;
		}

		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);

		mBorderPaint.setStyle(Paint.Style.FILL);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		// mBorderPaint.setStrokeWidth(mBorderWidth);

		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();

		mBorderRect.set(0, 0, getWidth(), getHeight());
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

		mDrawableRect.set(mBorderRect);
		if (!mBorderOverlay) {
			mDrawableRect.inset(mBorderWidth, mBorderWidth);
		}
		mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

		updateShaderMatrix();
		invalidate();
	}

	private void updateShaderMatrix() {
		float scale;
		float dx = 0;
		float dy = 0;

		mShaderMatrix.set(null);

		if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
			scale = mDrawableRect.height() / (float) mBitmapHeight;
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
		} else {
			scale = mDrawableRect.width() / (float) mBitmapWidth;
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
		}

		mShaderMatrix.setScale(scale, scale);
		mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

		mBitmapShader.setLocalMatrix(mShaderMatrix);
	}

	// @Override
	// protected void onLayout(boolean changed, int left, int top, int right,
	// int bottom) {
	// super.onLayout(changed, left, top, right, bottom);
	// }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int mMeasureWidth = getMeasuredWidth();
		int mRealHeight = calculateHeight(mMeasureWidth);
		setMeasuredDimension(mMeasureWidth, mRealHeight);
	}

	private int calculateHeight(int w) {
		if (size == w)
			return mCalcHeight;
		size = w;
		mCalcHeight = (int) (size * mHeightRatio);
		mBounds = new RectF(0, 0, w, mCalcHeight);
		return mCalcHeight;
	}

	private void resetLayout(int w) {
		if (size == w)
			return;
		size = w;
		int height = (int) (size * mHeightRatio);
		mBounds = new RectF(0, 0, w, height);

		final LayoutParams lp = getLayoutParams();
		lp.width = size;
		lp.height = height;
		post(new Runnable() {
			@Override
			public void run() {
				setLayoutParams(lp);
			}
		});
	}

}
