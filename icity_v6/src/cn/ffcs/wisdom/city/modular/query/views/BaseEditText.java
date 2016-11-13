package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import cn.ffcs.wisdom.city.modular.query.widget.QueryViewBinder;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.StringUtil;

public class BaseEditText extends EditText implements QueryViewBinder {

	private String fieldName;
	private String viewTitle;
	private boolean mustInput;

	public BaseEditText(Context context) {
		this(context, null);
	}

	public BaseEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	@Override
	public void setMyHint(String hint) {
		setHint(hint);
	}

	@Override
	public String getValue() {
		return getText().toString();
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public void clearValue() {
		setText(null);
	}

	@Override
	public View getObject() {
		return this;
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
		CommonUtils.showErrorByEditText(this, viewTitle + "不能为空", animation);
		return true;
	}

	@Override
	public void setViewTitle(String title) {
		this.viewTitle = title;
	}

	@Override
	public void setDefaultValue(String value) {
		setText(value);
	}

	@Override
	public void setMyTextSize(int size) {
		setTextSize(size);
	}

	@Override
	public void setMyFocus(boolean focus) {
//		@SuppressWarnings("static-access")
//		final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
//				getContext().INPUT_METHOD_SERVICE);
//		imm.showSoftInput(this, 0);
	}

	@Override
	public void setMyTextColor(int color) {
		setTextColor(color);
	}
	
	@Override
	public void setMyHintTextColor(int color) {
		setHintTextColor(color);
	}
}
