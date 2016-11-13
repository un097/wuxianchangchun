package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

public class EditViewEnNum extends BaseEditText {

	public EditViewEnNum(Context context) {
		this(context, null);
	}

	public EditViewEnNum(Context context, AttributeSet attrs) {
		super(context, attrs);

//		setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		setSingleLine(true);

		addTextChangedListener(new TextWatcher() {

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

}
