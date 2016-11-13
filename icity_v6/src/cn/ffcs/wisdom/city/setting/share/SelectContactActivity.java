package cn.ffcs.wisdom.city.setting.share;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 选择联系人  </p>
 * <p>Description:                     </p>
 * <p>@author: xzw                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-8-13           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class SelectContactActivity extends WisdomCityActivity {

	private ListView selectListView = null;// 联系人listview
	private LinearLayout loading_bar = null;// 进度条
	private Button sure;// 确定按钮
	private Button cancel;// 取消按钮
	private EditText searchContent;// 搜索框
	private SideLetterBar sideLetterBar;// 旁边字母控件
	private TextView mDialogText;// 字母选中控件
	private TextView topTile;// 头部标题
	private LinearLayout noContacts;// 没有联系人

	private int contractsNum = 0;// 联系人选中数量
	private WindowManager mWindowManager;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // 联系人列表
	private List<Map<String, Object>> searchList = new ArrayList<Map<String, Object>>(); // 搜索联系人列表
	private SelectContactAdapter selectContactAdapter = null;// 适配器

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		CommonUtils.hideKeyboard(SelectContactActivity.this);
		return true;
	}

	public void dispSearch() {
		selectContactAdapter = new SelectContactAdapter(mContext);
		selectContactAdapter.setData(list);
		selectListView.setAdapter(selectContactAdapter);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		sideLetterBar.setVisibility(View.VISIBLE);
		sideLetterBar.setTextView(mDialogText);
		sideLetterBar.setListView(selectListView);
		sure.setEnabled(true);
		cancel.setEnabled(true);
		sure.setText(mContext.getString(R.string.share_number, contractsNum));
	}

	/*
	 * 单击某个联系人
	 */
	class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			ImageView checkBox = (ImageView) view.findViewById(R.id.multiple_checkbox);
			boolean isChecked = (Boolean) list.get(position).get("checked");
			if (isChecked) {
				checkBox.setBackgroundResource(R.drawable.checkbox_check_false);
				contractsNum--;
			} else {
				checkBox.setBackgroundResource(R.drawable.checkbox_check_true);
				contractsNum++;
			}
			sure.setText(mContext.getString(R.string.share_number, contractsNum));
			if (searchList != null && searchList.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					String searchString = searchList.get(position).get("phone").toString();
					String listString = list.get(i).get("phone").toString();
					if (searchString.equals(listString)) {
						list.get(i).put("checked", !isChecked);
					}
				}
			} else {
				list.get(position).put("checked", !isChecked);
			}

		}
	}

	/*
	 * 点击事件
	 */
	class ButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.sure) {
				Intent i = new Intent();
				i.putExtra("phoneList", (Serializable) list);
				i.putExtra("contractsNum", contractsNum);
				setResult(RESULT_OK, i);
				ContactAsyncQueryHandler.getInstance(getContentResolver()).sureContacts();
				SelectContactActivity.this.finish();
			} else if (id == R.id.cancel) {
				onBackPressed();
			}
		}
	}

	/*
	 * 联系人搜索
	 */
	class OnTextChangeListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			try {
				String searchKeyWord = searchContent.getText().toString();
				if (StringUtil.isEmpty(searchKeyWord)) {
					if (list != null && list.size() != 0) {
						selectContactAdapter.setData(list);
						selectContactAdapter.notifyDataSetChanged();
						searchList.clear();
					}
				} else {
					if (list != null && list.size() != 0) {
						searchList.clear();
						for (int i = 0; i < list.size(); i++) {
							Map<String, Object> map = list.get(i);
							String phone = map.get("phone").toString();
							String name = map.get("name").toString();
							String pyPhone = map.get("pyPhone").toString();
							StringBuilder sb = new StringBuilder();
							if (pyPhone.indexOf(",") > 0) {
								String[] pyPhones = pyPhone.split(",");
								for (int j = 0; j < pyPhones.length; j++) {
									if (pyPhones[j].length() > 0) {
										sb.append(pyPhones[j].substring(0, 1));
									}
								}
							}
							if (phone.contains(searchKeyWord) || name.contains(searchKeyWord)
									|| sb.toString().contains(searchKeyWord)) {
								searchList.add(map);
							}
						}

						selectContactAdapter.setData(searchList);
						selectContactAdapter.notifyDataSetChanged();
					}
				}
			} catch (Exception e) {
				Log.e("Exception" + e);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}

	@Override
	protected void initComponents() {
		selectListView = (ListView) findViewById(R.id.select_listview);
		loading_bar = (LinearLayout) findViewById(R.id.loading_bar);

		sure = (Button) findViewById(R.id.sure);
		sure.setOnClickListener(new ButtonOnClickListener());

		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new ButtonOnClickListener());

		noContacts = (LinearLayout) findViewById(R.id.no_data);

		searchContent = (EditText) findViewById(R.id.search_keyword);
		topTile = (TextView) findViewById(R.id.top_title);
		sideLetterBar = (SideLetterBar) findViewById(R.id.sideBar);
		mDialogText = (TextView) LayoutInflater.from(this)
				.inflate(R.layout.view_letter_toast, null);
	}

	@Override
	protected void initData() {
		// 加载联系人时，按钮不可用
		sure.setEnabled(false);
		cancel.setEnabled(false);

		contractsNum = getIntent().getIntExtra("contractsNum", 0);// 获取选中的个数，没有的默认为0
		if(contractsNum==0){
			ContactAsyncQueryHandler.getInstance(getContentResolver()).initContacts();
		}
		indataContacts();
		topTile.setText(getString(R.string.share_sms_add_contact));
		selectListView.setOnItemClickListener(new ItemClickListener());
		searchContent.addTextChangedListener(new OnTextChangeListener());

		mDialogText.setVisibility(View.INVISIBLE);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_select_contact;
	}

	@Override
	public void finish() {
		if (mWindowManager != null) {
			mWindowManager.removeView(mDialogText);
		}
		ContactAsyncQueryHandler.getInstance(getContentResolver()).cancelContacts();
		super.finish();
	}

	/**
	 * 获取联系人
	 */
	public void indataContacts() {
		ContactAsyncQueryHandler.getInstance(getContentResolver()).getList(new GetContactListener());
	}

	// 获取联系人监听
	class GetContactListener implements OnQueryContactsListener {

		@Override
		public void onQueryStart() {
			loading_bar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onQueryFinish(List<Map<String, Object>> mList) {
			if (mList != null && mList.size() > 0) {
				// 已加载有联系人
				list.addAll(mList);
				dispSearch();
				loading_bar.setVisibility(View.GONE);
			} else {
				sure.setEnabled(true);
				cancel.setEnabled(true);
				loading_bar.setVisibility(View.GONE);
				noContacts.setVisibility(View.VISIBLE);
			}
		}
	}
}
