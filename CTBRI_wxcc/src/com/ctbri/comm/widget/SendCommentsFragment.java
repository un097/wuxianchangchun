package com.ctbri.comm.widget;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.CurrentBean;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.MyBaseProtocol;
import com.ctbri.wxcc.R;
import com.google.gson.Gson;
import com.wookii.tools.net.WookiiHttpPost;

public class SendCommentsFragment extends Fragment {

	private static final long DELAY = 500;
	protected static final int EDIT_TEXT_MIN_LENGTH = 1;
//	protected static final int EDIT_TEXT_MAX_LENGTH = 200;
	private Activity context;
	private TextView count;
	private Button fire;
	private View view;
	private EditText edit;
	private View iamge;
	private View iamge2;
	private View text;
	private View functionView;
	private View editZone;
	private String requestUrl;
	private OnSendCommentsListener listener;
	private String id;
	private AnimatorSet animSet;
	private OnClickListener onClickListener;
	private ImageView confirm;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		this.context = activity;
		super.onAttach(activity);
	}

	public static SendCommentsFragment newInstance(String url,
			String community_id, int count) {
		SendCommentsFragment sendComments = new SendCommentsFragment();
		Bundle b = new Bundle();
		b.putString("community_count", String.valueOf(count));
		b.putString("url", url);
		b.putString("community_id", community_id);
		sendComments.setArguments(b);
		return sendComments;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		bindRootViewEvent();
	}

	private void bindRootViewEvent() {
		View root = getView();
		if (root == null)
			return;
		root.setFocusable(true);
		edit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				System.out.println("获取焦点" + hasFocus);
				if (!hasFocus) {
					// change by wuchen at 2015-2-26 fix bug
					InputMethodManager inputMethodManager = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(
							edit.getWindowToken(), 0);
					hideEditZone();
				}
			}
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle arguments = getArguments();
		view = inflater.inflate(R.layout.fragment_send_comments, null);
		editZone = view.findViewById(R.id.send_comments_edit_zone);
		fire = (Button) view.findViewById(R.id.send_comments_fire);
		fire.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openTextEdit();
			}
		});
		count = (TextView) view.findViewById(R.id.send_comments_count);
		edit = (EditText) view.findViewById(R.id.send_comments_edit);
		edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 需求变更 ，不现再限制评论字数。只要不为空即可。  by yyd 2015-05-20
				if (s.toString().length() >= EDIT_TEXT_MIN_LENGTH) {
					confirm.setImageResource(R.drawable.button_selector_confirm);
					confirm.setEnabled(true);
				} else {
					confirm.setImageResource(R.drawable.common_icon_confirm_unpressed);
					confirm.setEnabled(false);
				}
			}
		});
		iamge = view.findViewById(R.id.send_comments_image);
		iamge2 = view.findViewById(R.id.send_comments_image2);
		iamge2.setOnClickListener(onClickListener);
		text = view.findViewById(R.id.send_comments_text);
		count.setText(arguments.getString("community_count"));
		requestUrl = arguments.getString("url");
		id = arguments.getString("community_id");
		functionView = view.findViewById(R.id.send_comments_function_zone);
		initFunctionView(functionView);

		return view;
	}

	private void sendComments() {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		String user_id = MessageEditor.getUserId(context);
		String md5 =  MessageEditor.getHotLineMd5(context);
		
		if(TextUtils.isEmpty(user_id))
			user_id = "null";
		if(TextUtils.isEmpty(md5))
			md5 = "null";
		// 添加请求头 ，参数
		pairs.add(new BasicNameValuePair("md5",md5));
		pairs.add(new BasicNameValuePair("user_id", user_id));
		// 如果用户 创建之后，没有上传头像。用户 url 为空。则上传一个 空字符串。
		String user_url = MessageEditor.getUserUrl(context);
		if(TextUtils.isEmpty(user_url))
			user_url = "";
		pairs.add(new BasicNameValuePair("user_url", user_url));
		// 用户名 就是评论的昵称，不存在为空的情况
		pairs.add(new BasicNameValuePair("user_name", MessageEditor.getUserName(context)));
		// 增加用户手机号参数，by 闫亚迪 2015-05-25
		pairs.add(new BasicNameValuePair("mobile", MessageEditor.getTel(context)));
		 
		pairs.add(new BasicNameValuePair("content", edit.getText().toString()));
		new MyBaseProtocol(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String json = (String) msg.obj;
				Gson gson = new Gson();
				try {
					CurrentBean currentMessage = gson.fromJson(json,
							CurrentBean.class);
					if (listener != null) {
						listener.onSendComments(currentMessage.getRet());
					}
					// 清空评论
					edit.setText("");
				} catch (Exception e) {

				}
			}
		}, new WookiiHttpPost(), requestUrl).startInvoke(
				new JsonWookiiHttpContent(pairs), 0);
	}

	public void setOnSendCommentsListener(OnSendCommentsListener listener) {
		this.listener = listener;
	}

	public interface OnSendCommentsListener {
		public void onSendComments(int status);
	}

	private void initFunctionView(View view) {
		// TODO Auto-generated method stub
		((TextView) view.findViewById(R.id.action_bar_title)).setText("写评论");
		view.findViewById(R.id.action_bar_left_btn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						hideEditZone();
					}
				});

		confirm = (ImageView) view.findViewById(R.id.action_bar_right_btn);
		confirm.setEnabled(false);
		confirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断当前用户是否登录，未登录则跳转至登录窗口 by 闫亚迪 2015-3-10
				if (_Utils.checkLoginAndLogin(context)) {
					hideEditZone();
					sendComments();
				}
			}
		});
	}

	@SuppressLint("NewApi")
	protected void hideEditZone() {
		/**
		 * 改变edit的高度
		 */
		int startFire = edit.getHeight();
		final int end = fire.getHeight();
		ValueAnimator va = ValueAnimator.ofInt(startFire, end);
		va.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

				LayoutParams lp = edit.getLayoutParams();
				lp.height = (Integer) animation.getAnimatedValue();
				edit.setLayoutParams(lp);

			}
		});
		va.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				edit.setY(dip2px(context, 8));
				edit.setVisibility(View.GONE);
				functionView.setVisibility(View.GONE);
				showOrHide(View.VISIBLE);
				// 强行隐藏软键盘
				InputMethodManager inputMethodManager = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						edit.getWindowToken(), 0);

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		/*
		 * 改变editZone的高度
		 */
		int start = editZone.getHeight();
		ValueAnimator va2 = ValueAnimator.ofInt(start, dip2px(context, 48));
		va2.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				LayoutParams lp = editZone.getLayoutParams();
				lp.height = (Integer) animation.getAnimatedValue();
				editZone.setLayoutParams(lp);
			}
		});
		palyTo(va, va2);
	}

	private void showOrHide(int i) {
		fire.setVisibility(i);
		iamge.setVisibility(i);
		iamge2.setVisibility(i);
		text.setVisibility(i);
		count.setVisibility(i);
	}

	@SuppressLint("NewApi")
	protected void openTextEdit() {
		edit.setVisibility(View.VISIBLE);
		edit.requestFocus();
		showOrHide(View.GONE);

		final int offset = (editZone.getHeight() - fire.getHeight()) / 2;
		int start = editZone.getHeight();
		/*
		 * 改变editZone的高度
		 */
		ValueAnimator va = ValueAnimator.ofInt(start, dip2px(context, 174));
		va.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				LayoutParams lp = editZone.getLayoutParams();
				lp.height = (Integer) animation.getAnimatedValue();
				editZone.setLayoutParams(lp);
			}
		});
		va.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// 垂直纠偏
				final int end = editZone.getHeight() - edit.getHeight()
						- dip2px(context, 17);
				ValueAnimator va = ValueAnimator.ofFloat(offset, end);
				va.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						float y = (Float) animation.getAnimatedValue();
						edit.setY(y);
						if (y == end) {
							functionView.setVisibility(View.VISIBLE);
							functionView.setY(0);
							InputMethodManager imm = (InputMethodManager) context
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.showSoftInput(edit,
									InputMethodManager.SHOW_FORCED);
						}
					}
				});
				va.setDuration(100);
				va.start();
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});

		/**
		 * 改变edit的高度
		 */
		int startFire = edit.getHeight();
		ValueAnimator va2 = ValueAnimator
				.ofInt(startFire, dip2px(context, 100));
		va2.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

				LayoutParams lp = edit.getLayoutParams();
				lp.height = (Integer) animation.getAnimatedValue();
				edit.setLayoutParams(lp);

			}
		});
		palyTo(va, va2/* , va3 */);
	}

	@SuppressLint("NewApi")
	private AnimatorSet palyTo(Animator... items) {
		animSet = new AnimatorSet();
		animSet.playTogether(items);
		animSet.setDuration(300);
		// animSet.setStartDelay(DELAY);
		animSet.start();
		return animSet;
	}

	public void setCommentsCount(int int_count) {
		count.setText(int_count + "");
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		// TODO Auto-generated method stub
		this.onClickListener = onClickListener;
	}
}
