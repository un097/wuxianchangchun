package cn.ffcs.widget;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * <p>Title:  首行缩进文本                                                    </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-2-27           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class IndentTextView extends TextView {

	public IndentTextView(Context context) {
		super(context);
	}

	public IndentTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		String intentSpace = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		String info = (String) super.getText();
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<body>");
		sb.append("<pre>");
		sb.append(intentSpace);
		sb.append(info);
		sb.append("</pre>");
		sb.append("</body>");
		sb.append("</html>");
		super.setText(Html.fromHtml(sb.toString()));
	}

	public IndentTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

}
