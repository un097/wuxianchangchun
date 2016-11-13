package cn.ffcs.wisdom.city.simico.activity.home.adapter;

import java.util.ArrayList;

import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.api.model.ItemType;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;

public abstract class ListBaseSectionedAdapter extends SectionedBaseAdapter {

	public static final int STATE_EMPTY_ITEM = 0;
	public static final int STATE_LOAD_MORE = 1;
	public static final int STATE_NO_MORE = 2;
	public static final int STATE_NO_DATA = 3;
	public static final int STATE_LESS_ONE_PAGE = 4;
	private static final String TAG = ListBaseSectionedAdapter.class.getSimpleName();

	protected int state = STATE_LESS_ONE_PAGE;

	protected int _loadmoreText;
	protected int _loadFinishText;

	protected ArrayList<ItemType> _data = new ArrayList<ItemType>();

	public void setData(ArrayList data) {
		if (data == null) {
			_data = new ArrayList<ItemType>();
		} else {
			_data = data;
		}
		notifyDataSetChanged();
	}

	public ArrayList getData() {
		return _data;
	}

	public void addData(ArrayList<ItemType> data) {
		_data.addAll(data);
		notifyDataSetChanged();
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return this.state;
	}

	public void clear() {
		if (_data != null) {
			_data.clear();
		}
	}

	public ListBaseSectionedAdapter() {
		_loadmoreText = R.string.loading;
		_loadFinishText = R.string.loading_no_more;
	}

	@Override
	public Object getItem(int section, int position) {
		if (section < _data.size()) {
			return _data.get(section).getList().get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		return position;
	}

	@Override
	public int getSectionCount() {
		switch (state) {
		case STATE_EMPTY_ITEM:
			return _data.size() + 1;
		case STATE_LOAD_MORE:
			return _data.size() + 1;
		case STATE_NO_DATA:
			return 0;
		case STATE_NO_MORE:
			return _data.size() + 1;
		case STATE_LESS_ONE_PAGE:
			return _data.size();
		default:
			break;
		}
		return _data.size();
	}

	@Override
	public int getCountForSection(int section) {
		switch (state) {
		case STATE_EMPTY_ITEM:
			if (section == getSectionCount() - 1) {
				return 0;
			}
			break;
		case STATE_LOAD_MORE:
			if (section == getSectionCount() - 1) {
				return 0;
			}
		case STATE_NO_DATA:
			if (section == getSectionCount() - 1) {
				return 0;
			}
		case STATE_NO_MORE:
			if (section == getSectionCount() - 1) {
				return 0;
			}
		case STATE_LESS_ONE_PAGE:
			break;
		default:
			break;
		}
		return _data.get(section).getList().size();
	}

	@Override
	public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
		if (section == getSectionCount() - 1) {
			TLog.log("last header" + section);
			if (state == STATE_LOAD_MORE || state == STATE_NO_MORE || state == STATE_EMPTY_ITEM) {
				LinearLayout loadmore = (LinearLayout) LayoutInflater.from(parent.getContext())
						.inflate(R.layout.simico_list_cell_footer, null);
				ProgressBar progress = (ProgressBar) loadmore.findViewById(R.id.progressbar);
				TextView text = (TextView) loadmore.findViewById(R.id.text);
				switch (state) {
				case STATE_LOAD_MORE:
					TLog.log("STATE_LOAD_MORE");
					loadmore.setVisibility(View.VISIBLE);
					progress.setVisibility(View.VISIBLE);
					text.setVisibility(View.VISIBLE);
					text.setText(_loadmoreText);
					break;
				case STATE_NO_MORE:
					TLog.log("STATE_NO_MORE");
					loadmore.setVisibility(View.VISIBLE);
					progress.setVisibility(View.GONE);
					text.setVisibility(View.VISIBLE);
					text.setText(_loadFinishText);
					break;
				case STATE_EMPTY_ITEM:
					TLog.log("STATE_EMPTY_ITEM");
					loadmore.setVisibility(View.GONE);
					progress.setVisibility(View.GONE);
					text.setVisibility(View.GONE);
					break;
				default:
					break;
				}
				return loadmore;
			}
		}

		if (_data.size() > 0 && section < _data.size()) {
			ItemType t = _data.get(section);
			ViewHolder vh = null;
			if (convertView != null && convertView.getTag() != null) {
				vh = (ViewHolder) convertView.getTag();
			} else {
				convertView = LayoutInflater.from(parent.getContext()).inflate(
						R.layout.simico_list_cell_home_pinned_header, null);
				vh = new ViewHolder();
				vh.header = (TextView) convertView.findViewById(R.id.tv_group_name);
				vh.type2 = (TextView) convertView.findViewById(R.id.tv_time);
				convertView.setTag(vh);
			}
			vh.header.setText(t.getTypeName());
			vh.type2.setText(t.getTypeName2());
		} else {
			TLog.log(TAG, "Sticky Header emtpty");
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.simico_list_cell_home_pinned_header, null);
			convertView.findViewById(R.id.split).setVisibility(View.GONE);
			convertView.setVisibility(View.GONE);
		}
		return convertView;
	}

	static final String HEADER_TAG = "HEADER_TAG";

	static class ViewHolder {
		TextView header, type2;
	}
}
