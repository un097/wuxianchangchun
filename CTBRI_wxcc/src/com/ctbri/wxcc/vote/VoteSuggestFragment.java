package com.ctbri.wxcc.vote;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;


public class VoteSuggestFragment extends BaseFragment {

	private EditText txt_title, txt_content;
	private int min_length,max_length;
	private ImageView rightBtn;
	/**
	 * 提交成功后，延迟关闭的时间 
	 */
	private final int exit_dealy = 1000;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View tmpView = inflater.inflate(R.layout.vote_sugget, container, false);
		txt_title = (EditText)tmpView.findViewById(R.id.txt_title);
		txt_content = (EditText)tmpView.findViewById(R.id.txt_content);
		rightBtn = (ImageView)tmpView.findViewById(R.id.action_bar_right_btn);
		rightBtn.setOnClickListener(new SubmitSuggetListener());
		rightBtn.setImageResource(R.drawable.action_bar_right_button_selector);
		rightBtn.setEnabled(false);
		tmpView.findViewById(R.id.action_bar_left_btn).setOnClickListener(new FinishClickListener());
		//设置标题
		((TextView)tmpView.findViewById(R.id.action_bar_title)).setText(R.string.title_suggest);
		return tmpView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ContentTextWatcher length_watcher = new ContentTextWatcher();
		txt_content.addTextChangedListener(length_watcher);
		txt_title.addTextChangedListener(length_watcher);
		
		min_length = activity.getResources().getInteger(R.integer.vote_suggest_min_length);
		max_length = activity.getResources().getInteger(R.integer.vote_suggest_max_length);
	}
	
	class ContentTextWatcher implements TextWatcher{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {}
		@Override
		public void afterTextChanged(Editable s) {
			int len = txt_content.getText().length();
			int title_len = txt_title.getText().length();
//			txt_content.setError(error)
			//标题、和内容 的文字长度 不能少于 min_length 不能多于 max_length
			rightBtn.setEnabled( (title_len > min_length && title_len < max_length && len > min_length && len < max_length));
		}
		
	}
	
	class SubmitSuggetListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			String title = txt_title.getText().toString();
			String content = txt_content.getText().toString();
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>()	;
			params.add(new BasicNameValuePair("title", title));
			params.add(new BasicNameValuePair("content", content));
			/** 添加 user_name 参数，提交投票后显示用户名. by yyd  2015-03-27 **/
			params.add(new BasicNameValuePair("user_name", MessageEditor.getUserName(activity)));
			/** 增加用户手机号参数，by 闫亚迪 2015-05-25 **/
			params.add(new BasicNameValuePair("mobile", MessageEditor.getTel(activity)));
			
			request(Constants.METHOD_INVESTIGATION_SUGGEST, new RequestCallback() {
				@Override
				public void requestSucc(String json) {
					toast("提交成功!");
					txt_content.setText("");
					txt_title.setText("");
					txt_content.postDelayed(new ExitRunnable(), exit_dealy);
					
//					activity.setResult(Activity.RESULT_OK);
//					activity.finish();
				}

				@Override
				public void requestFailed(int errorCode) {
					
				}
			}, params);
		}
		
	}
	class ExitRunnable implements Runnable{

		@Override
		public void run() {
			activity.finish();			
		}
		
	}

	@Override
	protected String getAnalyticsTitle() {
		return "vote_new_suggest";
	}

}
