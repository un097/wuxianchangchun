package cn.ffcs.wisdom.city.simico.api.model;

import java.util.ArrayList;
import java.util.List;

import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class Category implements Comparable<Category> {

	private MenuItem menu;
	private boolean showMore = true;
	private List<MenuItem> mServices = new ArrayList<MenuItem>();

	public Category() {
	}

	public List<MenuItem> getServices() {
		return mServices;
	}

	public void setServices(List<MenuItem> mServices) {
		this.mServices = mServices;
	}

	public MenuItem getMenu() {
		return menu;
	}

	public void setMenu(MenuItem menu) {
		this.menu = menu;
	}

	public void addSubMenu(MenuItem subMenu) {
		this.mServices.add(subMenu);
	}

	public boolean isShowMore() {
		return showMore;
	}

	public void setShowMore(boolean showMore) {
		this.showMore = showMore;
	}

	@Override
	public int compareTo(Category another) {
		return menu.getMenuOrder() > another.getMenu().getMenuOrder() ? 1 : -1;
	}
}
