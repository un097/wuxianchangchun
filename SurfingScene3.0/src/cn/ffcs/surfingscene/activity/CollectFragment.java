package cn.ffcs.surfingscene.activity;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.adapter.ListViewFavoriteAdapter;
import cn.ffcs.surfingscene.sqlite.GlobalEye;
import cn.ffcs.surfingscene.sqlite.GlobalEyesService;

/**
 * <p>Title:  收藏</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CollectFragment extends BaseFragment {
	private TextView mEmptyTip;// 空列表提示
	private ListView mListView;
	private ListViewFavoriteAdapter mAdapter;
	private List<GlobalEye> list;
	private View basicView;

	/**
	 * 实例化并且传递参数
	 * @param bundle
	 * @return
	 */
	public static CollectFragment newInstance(Bundle bundle) {
		CollectFragment fragMent = new CollectFragment();
		fragMent.setArguments(bundle);
		return fragMent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected View setOnCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		basicView = inflater.inflate(R.layout.glo_act_collect, container, false);

		mEmptyTip = (TextView) basicView.findViewById(R.id.favorite_empty);
		mListView = (ListView) basicView.findViewById(R.id.favorite_list);
		mAdapter = new ListViewFavoriteAdapter(getActivity());
		return basicView;
	}

	@Override
	protected void initData() {
		list = GlobalEyesService.getInstance(mContext).queryFavorite();
		if (list == null || list.size() == 0) {
			if (mEmptyTip != null) {
				mEmptyTip.setVisibility(View.VISIBLE);
			}
		} else {
			if (mEmptyTip != null) {
				mEmptyTip.setVisibility(View.GONE);
			}
		}
		mAdapter.setData(list);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

}
