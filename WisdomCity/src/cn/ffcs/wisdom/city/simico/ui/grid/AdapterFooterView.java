package cn.ffcs.wisdom.city.simico.ui.grid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * STATE_LOADING = 0;<br/>
 * STATE_LOADED = 1;<br/>
 * STATE_NONE = 2;<br/>
 * 
 * @author Jerry
 * 
 */
public class AdapterFooterView extends RelativeLayout {

	public static final int STATE_LOADING = 0;
	public static final int STATE_LOADED = 1;
	public static final int STATE_NONE = 2;
	private View _loaded;
	// private TextView _loadedTv;
	private ImageView _spinner;
	private ObjectAnimator _spinnerAnim;
	private int _state = STATE_NONE;
	private LinearLayout _spinnerContainer;

	public AdapterFooterView(Context context) {
		super(context);
		init();
	}

	public AdapterFooterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AdapterFooterView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		inflate(getContext(), R.layout.simico_list_footer, this);
		_spinnerContainer = (LinearLayout) findViewById(R.id.loadmore_container);
		_spinner = (ImageView) findViewById(R.id.loading_pb);
		_spinnerAnim = ObjectAnimator.ofFloat(_spinner, "rotation",
				new float[] { 2.0f, 8, 12, 21, 38, 63, 100, 149, 205, 255, 292,
						318, 336, 348, 356, 360 });
		_spinnerAnim.setStartDelay(500);
		_spinnerAnim.setDuration(1000);
		_spinnerAnim.setRepeatCount(-1);
		_spinnerAnim.setRepeatMode(1);
		_spinnerAnim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(Animator animator) {
				_spinnerAnim.setCurrentPlayTime(0);
				if (_state == STATE_LOADED) {
					_spinner.setVisibility(View.GONE);
				}
			}

			@Override
			public void onAnimationEnd(Animator animator) {
				if (_state == STATE_LOADED) {
					_spinner.setVisibility(View.GONE);
				}
			}

			@Override
			public void onAnimationRepeat(Animator animator) {
				if (_spinner.getVisibility() != View.VISIBLE)
					_spinnerAnim.cancel();
			}

			@Override
			public void onAnimationStart(Animator animator) {
			}
		});
		_loaded = findViewById(R.id.message_container);
		// _loadedTv = (TextView) findViewById(R.id.title_tv);
		updateView();
	}

	public static final void setState(AdapterFooterView view, int state) {
		if (view != null) {
			view.setState(state);
		}
	}

	private void updateView() {
		switch (_state) {
		case STATE_LOADING:
			_spinnerAnim.start();
			_spinnerContainer.setVisibility(View.VISIBLE);
			_loaded.setVisibility(View.GONE);
			setVisibility(View.VISIBLE);
			break;
		case STATE_LOADED:
			_spinnerAnim.cancel();
			_spinnerContainer.setVisibility(View.GONE);
			_loaded.setVisibility(View.VISIBLE);
			setVisibility(View.VISIBLE);
			break;
		case STATE_NONE:
			setVisibility(View.GONE);
			_spinnerAnim.cancel();
			_spinnerContainer.setVisibility(View.GONE);
			_loaded.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	public void setMessage(int resid) {
		// _loadedTv.setText(resid);
	}

	public void setMessage(String text) {
		// _loadedTv.setText(text);
	}

	public void setShadowVisibility(int visibi) {
		// findViewById(R.id.dropshadow).setVisibility(visibi);
	}

	/**
	 * 设置状态
	 * 
	 * @param state
	 *            0:STATE_LOADING,1:STATE_LOADED,2:STATE_NONE
	 */
	public void setState(int state) {
		_state = state;
		log(state);
		updateView();
	}

	private void log(int state) {
		String s = "STATE_LOADING";
		if (state == 1) {
			s = "STATE_LOADED";
		} else if (state == 2) {
			s = "STATE_NONE";
		}
		TLog.log("FooterView", "footer view state:" + s);
	}
}
