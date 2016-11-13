package com.ctbri.comm.widget;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.ctbri.wxcc.CurrentBean;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.MyBaseProtocol;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.widget.ReportDialog;
import com.google.gson.Gson;
import com.wookii.tools.net.WookiiHttpPost;

public class ComplaintDialog extends DialogFragment {

	private Activity context;
	private String url;
	private String id;
	protected String content;
	private ReportDialog dialog;
	public static ComplaintDialog newInstance(String url, String id){
		Bundle b = new Bundle();
		b.putString("url", url);
		b.putString("id", id);
		ComplaintDialog f = new ComplaintDialog();
		f.setArguments(b);
		return f;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.context = activity;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		url = getArguments().getString("url");
		id = getArguments().getString("id");
		dialog = new ReportDialog(context, R.style.self_dialog);
		dialog.setData(R.array.complaint);
		dialog.setOnClickListener(new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								content = context.getResources()
										.getStringArray(R.array.complaint)[which];
							}
						});
		dialog.setCancelButton("取消", new OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dismiss();
					}
				});
		dialog.setDoneButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						request();
						dismiss();
					}
				});
		return dialog;
	}

	protected void request() {
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		pairs.add(new BasicNameValuePair("comment_id", id));
		if(TextUtils.isEmpty(content)){
			content = context.getResources().getStringArray(R.array.complaint)[0];
		}
		pairs.add(new BasicNameValuePair("report_reason", content));
		pairs.add(new BasicNameValuePair("user_id", MessageEditor.getUserId(context)));
		//添加用户名 user_name by yyd 2015-04-01
		pairs.add(new BasicNameValuePair("user_name", MessageEditor.getUserName(context)));
		
		new MyBaseProtocol(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String json = (String) msg.obj;
				Gson gson = new Gson();
				CurrentBean currentMessage = gson.fromJson(json,
						CurrentBean.class);
				if (currentMessage.getRet() == 0 && !context.isFinishing()) {
					Toast.makeText(context.getApplicationContext(), "举报成功", Toast.LENGTH_SHORT).show();
				}
			}
		}, new WookiiHttpPost(), url).startInvoke(
				new JsonWookiiHttpContent(pairs), 0);
	}
	
	
	public static void showDialog(Context context, String url, String id) {
	    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
	    ComplaintDialog newFragment = ComplaintDialog.newInstance(url, id);
	    newFragment.show(fragmentManager, "dialog");
	}
	
}
