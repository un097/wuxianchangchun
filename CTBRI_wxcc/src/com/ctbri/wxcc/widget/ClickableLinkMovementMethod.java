package com.ctbri.wxcc.widget;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public class ClickableLinkMovementMethod extends LinkMovementMethod {

	private static ClickableLinkMovementMethod instance;
	public static ClickableLinkMovementMethod getInstance(){
		if(instance==null)
			instance = new ClickableLinkMovementMethod();
		return instance;
	}
	
	private LocationSpan<?> locationSpan;
	
	@Override
	public boolean onTouchEvent(TextView textView, Spannable spannable,
			MotionEvent event) {
		int action = event.getAction();
		
		if(action == MotionEvent.ACTION_DOWN){
			locationSpan = getPressedSpan(textView, spannable, event);
			if(locationSpan != null){
				locationSpan.setPressed(true);
				Selection.setSelection(spannable, spannable.getSpanStart(locationSpan), spannable.getSpanEnd(locationSpan));
			}
			return true;
		}
		else if(action == MotionEvent.ACTION_MOVE){
			LocationSpan<?> tmpSpan = getPressedSpan(textView, spannable, event);
			if(locationSpan!=null && tmpSpan!=locationSpan){
				locationSpan.setPressed(false);
				locationSpan = null;
	            Selection.removeSelection(spannable);
				
			}
		}else{
			if (locationSpan != null) {
				locationSpan.setPressed(false);
            }
			locationSpan = null;
            Selection.removeSelection(spannable);
		}
		return super.onTouchEvent(textView, spannable, event);
		}
	/**
	 * 从按下的坐标，获取所属区域的 Span
	 * @param textView
	 * @param spannable
	 * @param event
	 * @return
	 */
	private LocationSpan<?> getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);
//        layout.getlineF
        int off = layout.getOffsetForHorizontal(line, x);

        LocationSpan<?>[] link = spannable.getSpans(off, off, LocationSpan.class);
        LocationSpan<?> touchedSpan = null;
        if (link.length > 0) {
            touchedSpan = link[0];
        }
        return touchedSpan;
    }
}
