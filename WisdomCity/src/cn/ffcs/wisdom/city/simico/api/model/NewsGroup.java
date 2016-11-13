package cn.ffcs.wisdom.city.simico.api.model;

public class NewsGroup extends ItemType {

	public NewsGroup() {
	}

	@SuppressWarnings("unchecked")
	public void addNews(News news){
		list.add(news);
	}
}
