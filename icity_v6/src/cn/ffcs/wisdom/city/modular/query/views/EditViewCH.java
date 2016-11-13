package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

public class EditViewCH extends BaseEditText {

	public EditViewCH(Context context) {
		this(context, null);
	}

	public EditViewCH(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	protected void init() {
		setInputType(InputType.TYPE_CLASS_TEXT);
	}

}
