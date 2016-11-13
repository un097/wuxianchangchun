package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

public class PassWordView extends BaseEditText {

	public PassWordView(Context context) {
		this(context, null);
	}

	public PassWordView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
	}

}
