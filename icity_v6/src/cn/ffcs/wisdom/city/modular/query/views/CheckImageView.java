package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cn.ffcs.wisdom.city.modular.query.widget.QueryViewBinder;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

public class CheckImageView extends LinearLayout implements QueryViewBinder {

	private String fieldName;
	private boolean mustInput;
	private String viewTitle;

	private EditText mCheck;
	private ImageView mNumber;

	private ProgressBar mProgress;

	public CheckImageView(Context context) {
		this(context, null);
	}

	public CheckImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout mMainLayout = (LinearLayout) mInflater.inflate(R.layout.widget_query_checkimg,
				null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(mMainLayout, params);

		mCheck = (EditText) findViewById(R.id.check);
		mNumber = (ImageView) findViewById(R.id.checknumber);
		mProgress = (ProgressBar) findViewById(R.id.check_progress);

		mNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				hideNumber();
				if (numberListener != null) {
					numberListener.onClick(v);
				}
			}
		});
		
		mCheck.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable edt) {
				try {
					String temp = edt.toString();
					String tem = temp.substring(temp.length() - 1, temp.length());

					char[] temC = tem.toCharArray();

					int mid = temC[0];

					if (mid >= 48 && mid <= 57) {// 数字
						return;
					}

					if (mid >= 65 && mid <= 90) {// 大写字母
						return;
					}

					if (mid > 97 && mid <= 122) {// 小写字母
						return;
					}
					edt.delete(temp.length() - 1, temp.length());
				} catch (Exception e) {
				}
			}
		});
		
	}

	public void hideNumber() {
		mNumber.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);
	}

	public void showNumber() {
		mNumber.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.GONE);
	}

	@Override
	public void setMyHint(String hint) {
		mCheck.setHint(hint);
	}

	@Override
	public String getValue() {
		return mCheck.getText().toString();
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public void clearValue() {
		mCheck.setText(null);
	}

	@Override
	public View getObject() {
		return mCheck;
	}

	public void setBackground(int resourceId) {
		mCheck.setBackgroundResource(resourceId);
	}

	public void setImageBitmap(Bitmap bitmap) {
		mNumber.setImageBitmap(bitmap);
	}

	@Override
	public boolean isEmpty() {
		return StringUtil.isEmpty(getValue());
	}

	@Override
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public boolean mustInput() {
		return mustInput;
	}

	@Override
	public void setMustInput(boolean mustInput) {
		this.mustInput = mustInput;
	}

	@Override
	public boolean verifyFail() {
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
		CommonUtils.showErrorByEditText(mCheck, viewTitle + "不能为空", animation);
		return true;
	}

	@Override
	public void setViewTitle(String title) {
		this.viewTitle = title;
	}

	@Override
	public void setDefaultValue(String value) {
		mCheck.setText(value);
	}

	@Override
	public void setMyTextSize(int size) {
		mCheck.setTextSize(size);
	}

	private OnNumberClickListener numberListener;

	public void setOnNumberClickListener(OnNumberClickListener l) {
		this.numberListener = l;
	}

	public interface OnNumberClickListener {
		void onClick(View v);
	}
	
	@Override
	public void setMyFocus(boolean focus) {
	}
	
	@Override
	public void setMyTextColor(int color) {
		mCheck.setTextColor(color);
	}

	@Override
	public void setMyHintTextColor(int color) {
		mCheck.setHintTextColor(color);
	}
}
