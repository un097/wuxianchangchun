package cn.ffcs.wisdom.city.simico.ui;

import cn.ffcs.wisdom.city.R;
import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;

public class ViewHelper {
	private static final int MAX_TEXT_LENGTH = 6;

	public ViewHelper() {
	}

	public static void changeViewLeftTo(View view, int i) {
		if (view != null) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view
					.getLayoutParams();
			params.addRule(0, i);
			view.setLayoutParams(params);
		}
	}

	public static boolean isKeyboardAction(int i, int j, KeyEvent keyevent) {
		boolean flag;
		if (j == i || j == 0 || keyevent != null && keyevent.getKeyCode() == 66)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static View listviewFooter(Activity activity) {
		return viewById(activity, 0x7f03008d);
	}

	public static void setLeftDrawable(TextView textview, int i) {
		if (textview != null)
			textview.setCompoundDrawablesWithIntrinsicBounds(i, 0, 0, 0);
	}

	public static void setRightDrawable(TextView textview, int i) {
		if (textview != null)
			textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, i, 0);
	}

	public static void setVisibility(View view, int i) {
		if (view != null)
			view.setVisibility(i);
	}

	public static void showFollowMark(TextView textview, boolean flag) {
	}

	public static void showIconForLongText(TextView textview, int i) {
		if (textview != null && textview.getText().length() > MAX_TEXT_LENGTH) {
			textview.setText(R.string.ellipsis);
			setLeftDrawable(textview, i);
		}
	}

	public static TabHost.TabSpec tabSpec(final TabHost host,
			String s) {
		return host.newTabSpec(s.toLowerCase()).setIndicator(s)
				.setContent(new TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return new View(host.getContext());
					}
				});
	}

	public static View viewById(Activity activity, int i) {
		return activity.getLayoutInflater().inflate(i, null);
	}

	public static View viewById(Context context, int i) {
		return LayoutInflater.from(context).inflate(i, null);
	}
}
