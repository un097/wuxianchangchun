package cn.ffcs.wisdom.city.simico.ui.grid;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.TDevice;

public class EmptyView extends LinearLayout {

	public static final int STATE_ERROR = 0;
	public static final int STATE_NETWORK = 1;
	public static final int STATE_NO_DATA = 2;
	public static final int STATE_LOADING = 4;
	public static final int STATE_NONE = 3;
	private static final String TAG = null;

	private ImageView mIvIcon;
	private TextView mTvMessage, mTvTip;
	private int state = STATE_NONE;

	private boolean isWebView;
	
	private ImageView _spinner;
//	private ObjectAnimator _spinnerAnim;
	private Animation _spinnerAnim;

	public EmptyView(Context context) {
		super(context);
		init(context);
	}

	public EmptyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public EmptyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		inflate(context, R.layout.simico_view_empty, this);
		mIvIcon = (ImageView) findViewById(R.id.iv_icon);
		mTvMessage = (TextView) findViewById(R.id.tv_message);
		mTvMessage.setVisibility(View.GONE);
		mTvTip = (TextView) findViewById(R.id.tv_tip);
		mTvTip.setVisibility(View.GONE);
		// tip = context.getResources().getString(R.string.empty_click_refresh);

		_spinner = (ImageView) findViewById(R.id.loading_pb);
		//_spinnerAnim = ObjectAnimator.ofFloat(_spinner, "rotation",
		//		new float[] { 2.0f, 8, 12, 21, 38, 63, 100, 149, 205, 255, 292,
		//				318, 336, 348, 356, 360 });
		// _spinnerAnim.setStartDelay(500L);
//		_spinnerAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_loading_rate);
		_spinnerAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		_spinnerAnim.setInterpolator(new LinearInterpolator());
		_spinnerAnim.setRepeatCount(Animation.INFINITE);
		_spinnerAnim.setRepeatMode(Animation.RESTART);
		_spinnerAnim.setDuration(1200);
		_spinnerAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				if (_spinner.getVisibility() != View.VISIBLE)
					_spinnerAnim.cancel();
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
//		_spinnerAnim.setDuration(1000L);
//		_spinnerAnim.setRepeatCount(-1);
//		_spinnerAnim.setRepeatMode(1);
//		_spinnerAnim.addListener(new AnimatorListener() {
//
//			@Override
//			public void onAnimationCancel(Animator animator) {
//				_spinnerAnim.setCurrentPlayTime(0);
//			}
//
//			@Override
//			public void onAnimationEnd(Animator animator) {
//			}
//
//			@Override
//			public void onAnimationRepeat(Animator animator) {
//				if (_spinner.getVisibility() != View.VISIBLE)
//					_spinnerAnim.cancel();
//			}
//
//			@Override
//			public void onAnimationStart(Animator animator) {
//			}
//		});

		updateView();
	}

	public void setState(int state) {
		this.state = state;
		updateView();
	}

	public int getState() {
		return this.state;
	}

	public void setTip(int res) {
		mTvTip.setText(res);
	}

	public void setTip(String text) {
		mTvTip.setText(text);
	}

	public void setMessage(int res) {
		mTvMessage.setText(res);
	}

	public void setMessage(String res) {
		mTvMessage.setText(res);
	}

	public void setIcon(int icon) {
		mIvIcon.setImageResource(icon);
	}

	private void updateView() {
		if ((state == STATE_ERROR && !TDevice.hasInternet())
				|| state == STATE_NETWORK) {
			state = STATE_NETWORK;
		}
		TLog.log(TAG, "空视图状态:"+getStateStr()+" isWebView:"+isWebView);
		switch (state) {
		case STATE_NONE:
			setVisibility(View.GONE);
			mIvIcon.setVisibility(View.GONE);
			mTvMessage.setVisibility(View.GONE);
			mTvTip.setVisibility(View.GONE);

			_spinnerAnim.cancel();
			_spinner.clearAnimation();
			_spinner.setVisibility(View.GONE);
			break;
		case STATE_ERROR:
			setVisibility(View.VISIBLE);
			mIvIcon.setVisibility(View.VISIBLE);
			mIvIcon.setImageResource(R.drawable.simico_ic_error);

			mTvMessage.setText(R.string.empty_error);
			mTvMessage.setVisibility(View.VISIBLE);

			mTvTip.setText(R.string.empty_error_tip);
			mTvTip.setVisibility(View.VISIBLE);
			
			if(isWebView) {
				mTvMessage.setText(R.string.empty_error_tip);
				mTvMessage.setVisibility(View.VISIBLE);
				
				mTvTip.setText(R.string.empty_click_to_refresh);
				mTvTip.setVisibility(View.VISIBLE);
			}
			
			_spinnerAnim.cancel();
			_spinner.clearAnimation();
			_spinner.setVisibility(View.GONE);
			break;
		case STATE_NETWORK:
			setVisibility(View.VISIBLE);
			_spinnerAnim.cancel();
			_spinner.clearAnimation();
			_spinner.setVisibility(View.GONE);
			
			mIvIcon.setVisibility(View.VISIBLE);
			mIvIcon.setImageResource(R.drawable.simico_ic_error);

			mTvMessage.setText(R.string.empty_network);
			mTvMessage.setVisibility(View.VISIBLE);

			mTvTip.setText(R.string.empty_network_tip);
			mTvTip.setVisibility(View.VISIBLE);
			
			if(isWebView) {
				mTvMessage.setText(R.string.empty_network);
				mTvMessage.setVisibility(View.VISIBLE);
				
				mTvTip.setText(R.string.empty_click_to_refresh);
				mTvTip.setVisibility(View.VISIBLE);
			}
			break;
		case STATE_NO_DATA:
			setVisibility(View.VISIBLE);
			
			//_spinnerAnim.setRepeatMode(Animation.INFINITE);
			_spinnerAnim.cancel();
			_spinner.clearAnimation();
			_spinner.setVisibility(View.GONE);
			
			mIvIcon.setVisibility(View.VISIBLE);
			mIvIcon.setImageResource(R.drawable.simico_ic_error);

			mTvMessage.setText(R.string.empty_no_data);
			mTvMessage.setVisibility(View.VISIBLE);

			mTvTip.setText(R.string.empty_no_data_tip);
			mTvTip.setVisibility(View.VISIBLE);
			
			break;
		case STATE_LOADING:
			setVisibility(View.VISIBLE);
			mIvIcon.setVisibility(View.GONE);
			mTvMessage.setVisibility(View.GONE);
			mTvTip.setVisibility(View.GONE);

			TDevice.showAnimatedView(_spinner);
			//_spinnerAnim.start();
			_spinnerAnim.cancel();
			_spinner.startAnimation(_spinnerAnim);
			_spinner.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		
		//message = "";
		//tip = "";
	}

	private String getStateStr() {
		switch (state) {
		case STATE_ERROR:
			return "STATE_ERROR";
		case STATE_LOADING:
			return "STATE_LOADING";
		case STATE_NETWORK:
			return "STATE_NETWORK";
		case STATE_NO_DATA:
			return "STATE_NO_DATA";
		case STATE_NONE:
			return "STATE_NONE";
		default:
			break;
		}
		return null;
	}

	public boolean isWebView() {
		return isWebView;
	}

	public void setWebView(boolean isWebView) {
		this.isWebView = isWebView;
	}
}
