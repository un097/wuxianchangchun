package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.v6.R;

public class AgreementCheck extends LinearLayout implements OnClickListener {

	OnClickListener mListener;

	private CheckBox mIsRead;

	public AgreementCheck(Context context) {
		this(context, null);
	}

	public AgreementCheck(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	private void init() {
		Context context = getContext();
		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.widget_agreement_checkbox, null);

		mIsRead = (CheckBox) rootView.findViewById(R.id.checkbox_agreement);

		TextView textView = (TextView) rootView.findViewById(R.id.agreement);
		textView.setText(Html.fromHtml("已了解服务协议"));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		SpannableString spanStr = new SpannableString(Html.fromHtml("已了解服务协议"));
		spanStr.setSpan(new SpanClick(), 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(spanStr);
		// textView.setOnClickListener(this);
		mIsRead.setOnClickListener(this);
		addView(rootView);
	}

	@Override
	public void onClick(View v) {
		mListener.onClick(v);
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		this.mListener = listener;
	}

	public boolean isChecked() {
		return mIsRead.isChecked();
	}
	
	public void setChecked(boolean ischeck) {
		this.mIsRead.setChecked(ischeck);
	}

	class SpanClick extends ClickableSpan {

		@Override
		public void onClick(View widget) {
			if (mListener != null) {
				mListener.onClick(widget);
			}
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(Color.parseColor("#0799ea"));
			ds.setUnderlineText(true);
		}

	}

}
