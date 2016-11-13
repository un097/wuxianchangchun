package cn.ffcs.wisdom.city.simico.api.model;

public class ProfileResource {

	public String id;
	public String name;
	public String tip;
	public int img;
	public int count = 0;

	public ProfileResource(String id, String name, String tip, int img,
			int count) {
		this.id = id;
		this.name = name;
		this.tip = tip;
		this.img = img;
		this.count = count;
	}
}
