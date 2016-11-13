package cn.ffcs.wisdom.city.home.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import cn.ffcs.surfingscene.activity.GlobaleyeMainActivity;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.SearchResultActivity;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.home.widget.interfaces.OnEditClickListener;
import cn.ffcs.wisdom.city.v6.R;

/**
 * 
 * <p>Title: 路况视频控件        </p>
 * <p>Description: 
 * 1.查看收藏的路况视频
 * 2.搜索关键字的路况视频
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-4-11             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RoadVideoWidget extends BaseHomeWidget {

	private Button search;
	private Button collection;
	private EditText keyEdit;
	private OnEditClickListener listener;

	public RoadVideoWidget(Context context) {
		super(context);
		initData(context);
	}

	public RoadVideoWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}
	
	/**
	 * @param context
	 */
	private void initData(Context context) {
		search = (Button) super.findViewById(R.id.search);
		search.setOnClickListener(new BtnClick());
		collection = (Button) super.findViewById(R.id.look_collection);
		collection.setOnClickListener(new BtnClick());
		keyEdit = (EditText) super.findViewById(R.id.key);
		keyEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doClickEvent(v, ViewGroup.FOCUS_BEFORE_DESCENDANTS);
			}
		});
	}

	class BtnClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			String sufCode = MenuMgr.getInstance().getTyjxCitycode(mContext);
//			String cityName = MenuMgr.getInstance().getCityName(mContext);
			int id = v.getId();
			if (id == R.id.search) {
				doClickEvent(v, ViewGroup.FOCUS_BEFORE_DESCENDANTS);
				Intent intent = new Intent(mContext, SearchResultActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Key.K_SEARCH_CONTENT, keyEdit.getText().toString());
				bundle.putString(Key.K_AREA_CODE, sufCode);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			} else if (id == R.id.look_collection) {
				Intent intent = new Intent(mContext, GlobaleyeMainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean(Key.K_IS_COLLECT, true);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		}
	}

	@Override
	public void refresh() {

	}

	@Override
	public int setContentView() {
		return R.layout.widget_road_video;
	}

	public void setOnEditClickListener(OnEditClickListener listener) {
		this.listener = listener;
	}

	private void doClickEvent(View v, int focusability) {
		if (listener != null) {
			listener.onClick(v, focusability);
		}
		v.requestFocus();
		((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
				.showSoftInput(v, 0);
	}
}
