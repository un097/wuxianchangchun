package cn.ffcs.wisdom.city.simico.kit.adapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.image.CommonImageLoader;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class PageMenuAdapter extends BaseAdapter {

	private ArrayList<MenuItem> data = null;
	private int mInitWidth;

	private CommonImageLoader mImageLoader;
	private WeakReference<MenuDelegate> mDelegate;

	public interface MenuDelegate {
		public void onMenuClick(MenuItem menu);
	}

	public PageMenuAdapter(MenuDelegate delegate) {
		mDelegate = new WeakReference<PageMenuAdapter.MenuDelegate>(delegate);
		mImageLoader = new CommonImageLoader(Application.context()
				.getApplicationContext());
		mImageLoader.setDefaultFailImage(R.drawable.simico_default_service);
	}

	public void setData(ArrayList<MenuItem> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data == null ? 0 : data.size();
	}

	@Override
	public MenuItem getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.simico_list_cell_menu, null);
			vh = new ViewHolder();
			vh.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			vh.icon_up = (ImageView) convertView.findViewById(R.id.iv_up);
			vh.cover = (ImageView) convertView.findViewById(R.id.iv_cloer);
			vh.name = (TextView) convertView.findViewById(R.id.tv_name);
			vh.remark = (TextView) convertView.findViewById(R.id.tv_remark);

			RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) vh.icon
					.getLayoutParams();
			if (p != null && mInitWidth == 0) {
				mInitWidth = p.width;
			}

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) vh.icon
				.getLayoutParams();
		RelativeLayout.LayoutParams p2 = (RelativeLayout.LayoutParams) vh.cover
				.getLayoutParams();
		p.height = mInitWidth;
		p.width = mInitWidth;
		p2.height = mInitWidth;
		p2.width = mInitWidth;

		MenuItem menu = data.get(position);
		if (isInstallByread(menu.getPackage_()))
			vh.icon_up.setVisibility(View.GONE);
		else
			vh.icon_up.setVisibility(View.VISIBLE);
		vh.icon.setTag(menu.getIcon());
		mImageLoader.loadUrl(vh.icon, menu.getIcon());
//		Application.getPicasso().load(menu.getIcon()).into(vh.icon);
		vh.name.setText(menu.getMenuName());
		vh.remark.setText(menu.getMenudesc());
		final MenuItem service = menu;

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mDelegate != null) {
					mDelegate.get().onMenuClick(service);
				}
			}
		});

		return convertView;
	}

	private boolean isInstallByread(String packageName) {
		return new File("/data/data/" + packageName).exists();
	}
	static class ViewHolder {
		ImageView icon, cover, icon_up;
		TextView name, remark;
	}

}
