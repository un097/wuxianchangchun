package cn.ffcs.wisdom.city.simico.activity.home.view;

public interface CategoryChangedListener {

	public abstract void onChanged(Category currentCtg);

	public abstract void onChanged(Category currentCtg, Category clickCtg);
}
