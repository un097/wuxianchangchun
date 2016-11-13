package cn.ffcs.wisdom.city.simico.api.model;

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.ffcs.wisdom.city.sqlite.model.MenuItem;

public class ServiceTag {
	private String id;
	private String name;
	private int order;
	private ArrayList<MenuItem> menuList = new ArrayList<MenuItem>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public ArrayList<MenuItem> getMenuList() {
		return menuList;
	}

	public void setMenuList(ArrayList<MenuItem> menuList) {
		this.menuList = menuList;
	}

	public static ServiceTag make(JSONObject json, Set<String> subscribes) throws JSONException {
		ServiceTag tag = new ServiceTag();
		tag.setId(json.optString("tag_id"));
		tag.setName(json.optString("tag_name"));
		tag.setOrder(json.optInt("tag_order"));
		JSONArray services = json.optJSONArray("menu_list");
		if (services != null) {
			tag.setMenuList(MenuHelper.makeAllV2WithSubscribe(services, subscribes));
		}
		return tag;
	}

	public static ArrayList<ServiceTag> makeAll(JSONArray tagJson, Set<String> subscribes)
			throws JSONException {
		ArrayList<ServiceTag> tags = new ArrayList<ServiceTag>();
		final int size = tagJson.length();
		for (int i = 0; i < size; i++) {
			tags.add(make(tagJson.getJSONObject(i), subscribes));
		}
		return tags;
	}

	public static ServiceTag make(JSONObject json) throws JSONException {
		ServiceTag tag = new ServiceTag();
		tag.setId(json.optString("tag_id"));
		tag.setName(json.optString("tag_name"));
		tag.setOrder(json.optInt("tag_order"));
		JSONArray services = json.optJSONArray("menu_list");
		if (services != null) {
			tag.setMenuList(MenuHelper.makeAllV2(services));
		}
		return tag;
	}

	public static ArrayList<ServiceTag> makeAll(JSONArray tagJson)
			throws JSONException {
		ArrayList<ServiceTag> tags = new ArrayList<ServiceTag>();
		final int size = tagJson.length();
		for (int i = 0; i < size; i++) {
			tags.add(make(tagJson.getJSONObject(i)));
		}
		return tags;
	}

	public void addMenu(MenuItem item) {
		if (menuList == null) {
			menuList = new ArrayList<MenuItem>();
		}
		menuList.add(item);
	}
}
