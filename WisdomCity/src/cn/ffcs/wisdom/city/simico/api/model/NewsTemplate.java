package cn.ffcs.wisdom.city.simico.api.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import cn.ffcs.wisdom.city.simico.activity.home.view.TagView;

public class NewsTemplate implements Parcelable {

	private int id;
	private int displayType;
	private String title;
	// private String source;
	private ArrayList<TagView.Tag> tags = new ArrayList<TagView.Tag>();
	private ArrayList<String> imgs = new ArrayList<String>();

	private String tag;

	public NewsTemplate() {
	}

	@SuppressWarnings("unchecked")
	public NewsTemplate(Parcel source) {
		id = source.readInt();
		displayType = source.readInt();
		title = source.readString();
		// this.source = source.readString();
		//tags = source.readArrayList(Tag.class.getClassLoader());
		imgs = source.readArrayList(String.class.getClassLoader());

		tag = source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(displayType);
		dest.writeString(title);
		// dest.writeString(source);
		//dest.writeList(tags);
		dest.writeList(imgs);

		dest.writeString(tag);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDisplayType() {
		return displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<TagView.Tag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<TagView.Tag> tags) {
		this.tags = tags;
	}

	public ArrayList<String> getImgs() {
		return imgs;
	}

	public void setImgs(ArrayList<String> imgs) {
		this.imgs = imgs;
	}

	public void addImage(String img) {
		this.imgs.add(img);
	}

	// public String getSource() {
	// return source;
	// }

	// public void setSource(String source) {
	// this.source = source;
	// }

	@Override
	public int describeContents() {
		return 0;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public static final Parcelable.Creator<NewsTemplate> CREATOR = new Creator<NewsTemplate>() {

		@Override
		public NewsTemplate[] newArray(int size) {
			return new NewsTemplate[size];
		}

		@Override
		public NewsTemplate createFromParcel(Parcel source) {
			return new NewsTemplate(source);
		}
	};
}
