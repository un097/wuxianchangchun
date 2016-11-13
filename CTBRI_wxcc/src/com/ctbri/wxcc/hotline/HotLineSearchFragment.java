package com.ctbri.wxcc.hotline;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.wwcc.greenrobot.HotLine;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.MyBaseProtocol;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.db.DBHelper;
import com.wookii.tools.net.WookiiHttpPost;

public class HotLineSearchFragment extends Fragment implements TextWatcher{

	protected static final String TAG = "HotLineSearchFragment";
	private DBHelper dbHelper;
	private Activity context;
	private EditText searchView;
	private ListView searchList;
	private CollectionChangeListener listener;
	private ResultAdapter resultAdapter;
	private TextView resultText;
	private View zeroG;
	private SpannableString spannableStringResult;
	private int spanStartIndex;
	private String spanString;
	private View clear;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		if (dbHelper == null) {
			dbHelper = DBHelper.getInstance(activity);
		}
		this.context = activity;
		super.onAttach(activity);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		bindRootViewEvent();
	}
	
	private void bindRootViewEvent() {
		View root = getView();
		if (root == null)
			return;
		root.setFocusable(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_hotline_search, null);
		searchView = (EditText)view.findViewById(R.id.hotline_search);
		searchView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, final boolean hasFocus) {
				new Thread(){
					public void run() {
						
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								
								InputMethodManager inputManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
								if(hasFocus) {
									inputManager.showSoftInput(searchView,
											InputMethodManager.SHOW_FORCED);
								} else {
									inputManager.hideSoftInputFromWindow(
											searchView.getWindowToken(), 0);
								}
							}
						});
					};
				}.start();
			}
		});
		
		searchView.addTextChangedListener(this);
		searchList = (ListView)view.findViewById(R.id.hotline_search_result_list);
		searchList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				ResultAdapter adapter = (ResultAdapter)searchList.getAdapter();
				HotLine child = (HotLine)adapter.getItem(arg2);
				Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
				Uri.parse("tel:" + child.getTel()));
				startActivity(phoneIntent);
				
			}
		});
		searchList.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					new Thread(){
						public void run() {
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							context.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									InputMethodManager inputManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
									inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
								}
							});
							
						}
					}.start();
					
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		
		resultText = (TextView)view.findViewById(R.id.hotline_search_zero);
		spanString = resultText.getText().toString();
		spanStartIndex = spanString.indexOf("%");
		
		zeroG = view.findViewById(R.id.hotline_zero_g);
		
		clear = view.findViewById(R.id.hotline_delete_world);
		clear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchView.setText("");
			}
		});
		return view;
		
	}
	
	@Override
	public void onPause() {
		Log.i(TAG, "search view on pause");
		super.onPause();
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		String query = s.toString();
		List<HotLine> queryTel = dbHelper.queryTel(query);
		resultText.setVisibility(View.VISIBLE);
		
		//format
		int size = queryTel.size();
		String format = String.format(spanString, size);
		SpannableString spannableStringResult = new SpannableString(format);
		spannableStringResult.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.result_red))
		,spanStartIndex, spanStartIndex + String.valueOf(size).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		resultText.setText(spannableStringResult);
		
		resultAdapter = new ResultAdapter(context, queryTel, query);
		searchList.setAdapter(resultAdapter);
		if(size == 0) {
			zeroG.setVisibility(View.VISIBLE);
			searchList.setVisibility(View.GONE);
		} else {
			zeroG.setVisibility(View.GONE);
			searchList.setVisibility(View.VISIBLE);
		}
		
		if(query.length() == 0) {
			zeroG.setVisibility(View.GONE);
			clear.setVisibility(View.GONE);
		} else {
			clear.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
	
	class ResultAdapter extends BaseAdapter {

		
		private List<HotLine> data;
		private Activity context;
		private String query;

		public ResultAdapter(Activity context, List<HotLine> data, String query) {
			this.data = data;
			this.context = context;
			this.query = query;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return data.get(position).getId();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.hotline_list_child, null);
				viewHolder.textName = (TextView) convertView
						.findViewById(R.id.hotline_name);
				viewHolder.telDes = (TextView) convertView.findViewById(R.id.hotline_tel_des);
				viewHolder.tel = (TextView) convertView
						.findViewById(R.id.hotline_tel);
				viewHolder.checkBox = (CheckBox) convertView
					.findViewById(R.id.hotline_is_collection);
				viewHolder.line = (ImageView)convertView.findViewById(R.id.hotline_line);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			final HotLine hotLine = data.get(position);
			final String item_id = hotLine.getItem_id();
			boolean status = false;
			if(hotLine.getStatus() == HotLineListAdapter.COLLECTION) {
				status = true;
			}
			
			HotLineListAdapter.collcetionMap.put(position, status);
			viewHolder.checkBox.setChecked(HotLineListAdapter.collcetionMap.get(position));
			viewHolder.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					boolean checked = ((CheckBox)v).isChecked();
					HotLineListAdapter.collcetionMap.put(position, checked);
					int status = HotLineListAdapter.UN_COLLECTION;
					if(checked){
						status = HotLineListAdapter.COLLECTION;
					}
					animator(status);
					postStatus(status, item_id);
					hotLine.setStatus(status);
					if(listener != null ) {
						listener.onChange(status, hotLine);
					}
					notifyDataSetChanged();
				}
			});
			String num_name = hotLine.getNum_name();
			int start = num_name.indexOf(query);
			SpannableString spannableString = new SpannableString(num_name);
			spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.result_blue))
			,start, start + query.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			viewHolder.textName.setText(spannableString);
			viewHolder.telDes.setText(String.valueOf(hotLine.getTel_description()));
			String tel = hotLine.getTel();
			if(tel.contains(HotLineListAdapter.SPLIT_CHAR)) {
				String[] split = tel.split(HotLineListAdapter.SPLIT_CHAR);
				viewHolder.tel.setText(split[1]);
			} else {
				viewHolder.tel.setText(tel);
			}
			return convertView;
		}
		
		protected void animator(int status) {
			// TODO Auto-generated method stub
			String toastText = "";
			switch (status) {
			case HotLineListAdapter.UN_COLLECTION:
				toastText = "移出常用电话";
				break;
			case HotLineListAdapter.COLLECTION:
				toastText = "加入常用电话";
				break;
			default:
				break;
			}
			Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
		}
		
		protected void postStatus(int status, String item_id) {
			ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
			pairs.add(new BasicNameValuePair("type", String.valueOf(status)));
			pairs.add(new BasicNameValuePair("number_id", String.valueOf(item_id)));
			pairs.add(new BasicNameValuePair("user_id", MessageEditor.getUserId(context)));
			new MyBaseProtocol(new Handler(){
				@Override
				public void handleMessage(Message msg) {
					
				}
			}, new WookiiHttpPost(), Constants.METHOD_HOT_LINE_COLLECTION)
			.startInvoke(new JsonWookiiHttpContent(pairs), 0);
		}
		class ViewHolder {

			public ImageView line;
			public CheckBox checkBox;
			public TextView tel;
			public TextView telDes;
			public TextView textName;
			
		}
	}
	
	
	public void setOnCollectionChangeListener(CollectionChangeListener listener){
		this.listener = listener;
	}

	public void notifyListUpdate() {
		if(resultAdapter != null) {
			resultAdapter.notifyDataSetChanged();
		}
	}
}
