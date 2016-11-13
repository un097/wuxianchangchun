package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.wisdom.city.modular.query.widget.QueryViewBinder;
import cn.ffcs.wisdom.tools.StringUtil;

public class BaseTextView extends TextView implements QueryViewBinder {

	private String fieldName;
	private String viewTitle;
	private boolean mustInput;

	public BaseTextView(Context context) {
		this(context, null);
	}

	public BaseTextView(Context context, AttributeSet attrs) {
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
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public void clearValue() {
		setText(null);
	}

	@Override
	public void setDefaultValue(String value) {
		setText(value);
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
	public boolean mustInput() {
		return mustInput;
	}

	@Override
	public void setMustInput(boolean mustInput) {
		this.mustInput = mustInput;
	}

	@Override
	public boolean verifyFail() {
		Toast.makeText(getContext(), viewTitle + "不能为空", Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void setViewTitle(String title) {
		this.viewTitle = title;
	}

	@Override
	public void setMyTextSize(int size) {
		setTextSize(size);
	}

	@Override
	public void setMyFocus(boolean focus) {
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
