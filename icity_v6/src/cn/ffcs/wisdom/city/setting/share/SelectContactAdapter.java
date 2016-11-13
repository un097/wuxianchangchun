package cn.ffcs.wisdom.city.setting.share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.ffcs.wisdom.city.v6.R;

/**
 * 
 * <p>Title: 联系人列表适配器         </p>
 * <p>Description: 
 *  1.配置联系人列表
 *  2.字母搜索适配
 * </p>
 * <p>@author: xzw                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-13             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */

public class SelectContactAdapter extends BaseAdapter implements SectionIndexer {
	private List<Map<String, Object>> list;
	private LayoutInflater mInflater;
	private HashMap<String, Integer> alphaIndexer;// 保存每个索引在list中的位置【#-0，A-4，B-10】
	private String[] sections;// 每个分组的索引表【A,B,C,F...】

	public SelectContactAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.alphaIndexer = new HashMap<String, Integer>();
	}

	public void setData(List<Map<String, Object>> list) {
		if (list != null) {
			this.list = list;
			for (int i = 0; i < list.size(); i++) {
				String name = getAlpha(list.get(i).get("toFirstSpell").toString());
				if (!alphaIndexer.containsKey(name)) {// 只记录在list中首次出现的位置
					alphaIndexer.put(name, i);
				}
			}
			Set<String> sectionLetters = alphaIndexer.keySet();
			ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
			Collections.sort(sectionList);
			sections = new String[sectionList.size()];
			sectionList.toArray(sections);

		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ContactHolder {
		TextView alpha;
		TextView phone;
		TextView name;
		ImageView checked;
		ImageView phoneImage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map<String, Object> map = list.get(position);
		ContactHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_check_box, null);
			holder = new ContactHolder();
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.phone = (TextView) convertView.findViewById(R.id.phone);
			holder.checked = (ImageView) convertView.findViewById(R.id.multiple_checkbox);
			holder.phoneImage = (ImageView) convertView.findViewById(R.id.phone_image);
			convertView.setTag(holder);
		} else {
			holder = (ContactHolder) convertView.getTag();
		}
		String name = map.get("name").toString();
		String phone = map.get("phone").toString();
		holder.phone.setText(phone);
		holder.name.setText(name);
		boolean isChecked = (Boolean) map.get("checked");
		if (isChecked) {
			holder.checked.setBackgroundResource(R.drawable.checkbox_check_true);
		} else {
			holder.checked.setBackgroundResource(R.drawable.checkbox_check_false);
		}
		String preSpell;
		String currentSpell = getAlpha(list.get(position).get("toFirstSpell").toString());
		if (position - 1 < 0) {
			preSpell = "";
		} else {
			preSpell = getAlpha(list.get(position - 1).get("toFirstSpell").toString());
		}

		if (!preSpell.equals(currentSpell)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentSpell);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}

	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	public Object[] getSections() {
		return null;
	}

	public int getSectionForPosition(int position) {
		return 0;
	}

	public int getPositionForSection(int section) {
		Map<String, Object> map;
		String l;
		if (section < 65) {
			return 0;
		} else {
			for (int i = 0; i < getCount(); i++) {
				map = list.get(i);
				l = map.get("toFirstSpell").toString();
				char firstChar = l.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}

			}
		}
		map = null;
		l = null;
		return -1;
	}
	

}
