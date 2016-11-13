package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

public class EditViewNum extends BaseEditText {

	public EditViewNum(Context context) {
		this(context, null);
	}

	public EditViewNum(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	protected void init() {
		setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
	}

}
