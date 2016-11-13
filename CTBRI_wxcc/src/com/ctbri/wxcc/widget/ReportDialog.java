package com.ctbri.wxcc.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ctbri.wxcc.R;

public class ReportDialog extends Dialog {

	private Context context;
	private ListView lvList;
	private int resId;
	private int choiceMode = ListView.CHOICE_MODE_SINGLE;
	private OnClickListener mOnClickListener;

	public ReportDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public ReportDialog(Context context) {
		this(context, R.style.self_dialog);
	}

	private String cancelButtonText;
	private OnClickListener cancelButtonClk;
	private String doneButtonText;
	private OnClickListener doneButtonClk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.report_layout);
		lvList = (ListView) findViewById(R.id.lv_report_items);
		lvList.setChoiceMode(choiceMode);
		lvList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				mOnClickListener.onClick(ReportDialog.this, position);
			}
		});
		fire();
	}

	public OnClickListener getmOnClickListener() {
		return mOnClickListener;
	}

	public void setOnClickListener(OnClickListener mOnClickListener) {
		this.mOnClickListener = mOnClickListener;
	}

	public void setData(int resId) {
		this.resId = resId;
	}

	private void fire() {
		if (resId != 0) {
			String[] strs = context.getResources().getStringArray(resId);
			ArrayAdapter<String> arrAdapter = new ArrayAdapter<String>(context,
					R.layout.report_list_item, strs);
			lvList.setAdapter(arrAdapter);
			//让list列表  默认选中第一项，by  闫亚迪 2015-03-10
			lvList.setItemChecked(0, true);
		}

		if (doneButtonText != null) {
			Button btnDone = (Button) findViewById(R.id.btn_done);
			btnDone.setText(doneButtonText);
			btnDone.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					doneButtonClk.onClick(ReportDialog.this,
							Dialog.BUTTON_NEGATIVE);
				}
			});

		}
		if (cancelButtonText != null) {
			Button btnDone = (Button) findViewById(R.id.btn_cancel);
			btnDone.setText(cancelButtonText);
			btnDone.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					cancelButtonClk.onClick(ReportDialog.this,
							Dialog.BUTTON_POSITIVE);
				}
			});
		}
	}

	public void setCancelButton(String text, OnClickListener clk) {
		this.cancelButtonText = text;
		this.cancelButtonClk = clk;
	}

	public void setDoneButton(String text, OnClickListener clk) {
		this.doneButtonText = text;
		this.doneButtonClk = clk;
	}
}
