package cn.ffcs.wisdom.city.modular.query.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import cn.ffcs.wisdom.city.modular.query.widget.QueryViewBinder;
import cn.ffcs.wisdom.tools.StringUtil;

public class ListButton extends Button implements QueryViewBinder {

	private String fieldName;
	private boolean mustInput;
	private String viewTitle;
	private String listTitle;

	private String[] items = new String[] {};
	private String[] itemValues = new String[] {};

	private int selected = -1;

	public ListButton(Context context) {
		this(context, null);
	}

	public ListButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		setOnClickListener(new OnSelectClickListener());
		setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
	}

	public void setList(String[] items, String[] itemValues) {
		this.items = items;
		this.itemValues = itemValues;
	}

	public void setValue(int index) {
		if (items != null && items.length > index) {
			setText(items[index]);
			selected = index;
		}
	}

	class OnSelectClickListener implements OnClickListener {
		public void onClick(View v) {
			Context context = getContext();
			new AlertDialog.Builder(context).setTitle(listTitle)
					.setItems(items, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							selected = which;
							setValue(selected);
						}
					}).show();// 构造车辆类型对话框
		}
	}

	@Override
	public void setMyHint(String hint) {
		setText(hint);
		setListTitle(hint);
	}

	public void setListTitle(String title) {
		this.listTitle = title;
	}

	@Override
	public void setDefaultValue(String value) {
		if (StringUtil.isEmpty(value)) {
			return;
		}

		if (itemValues != null && itemValues.length > 0) {
			for (int i = 0; i < itemValues.length; i++) {
				String v = itemValues[i];
				if (value.equals(v)) {
					setValue(i);
				}
			}
		}
	}

	@Override
	public String getValue() {
		if (selected == -1)
			return null;
		else
			return itemValues[selected];
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
		setValue(0);
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
		// Animation animation = AnimationUtils.loadAnimation(getContext(),
		// R.anim.shake);
		// CommonUtils.showErrorByEditText(mCheck, viewTitle + "不能为空",
		// animation);
		Toast.makeText(getContext(), "请选择:" + viewTitle, Toast.LENGTH_SHORT).show();
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
