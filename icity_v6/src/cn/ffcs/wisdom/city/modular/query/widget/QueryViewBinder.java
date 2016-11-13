package cn.ffcs.wisdom.city.modular.query.widget;

import android.view.View;

public interface QueryViewBinder {

	public void setMyHint(String hint);

	public String getValue();

	public String getFieldName();

	public void setFieldName(String fieldName);

	public void clearValue();
	
	public void setDefaultValue(String value);

	public View getObject();

	public boolean isEmpty();
	
	public boolean mustInput();
	
	public void setMustInput(boolean mustInput);
	
	public boolean verifyFail();
	
	public void setViewTitle(String title);
	
	public void setMyTextSize(int size);
	
	public void setMyTextColor(int color);
	
	public void setMyFocus(boolean focus);
	
	public void setMyHintTextColor(int color);

}
