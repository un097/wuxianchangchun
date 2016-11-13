package cn.ffcs.wisdom.city.simico.api.model;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import cn.ffcs.wisdom.city.simico.activity.home.view.TagView;
import cn.ffcs.wisdom.city.simico.activity.home.view.TagView.Tag;
import cn.ffcs.wisdom.city.simico.api.request.BaseRequest;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;
import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;
import cn.ffcs.wisdom.tools.StringUtil;

import com.umeng.analytics.MobclickAgent;

/**
 * 资讯
 * 
 * @author Tonlin
 * 
 */
// chnl_id
// transcoding_url
// source_url
// wap_url
// is_top
// is_front_show
// effice_time
// create_time
// id
// type
public class News implements Parcelable, Comparable<News> {

	private static final String HTTP_PREFIX = "http://";
	private int id;
	private int chnlId;
	private String channelName;
	private String type;
	private String transcodingUrl;
	private String sourceUrl;
	private String wapUrl;
	private boolean isTop;
	private boolean isFrontShow;
	private long efficeTime;
	private long createTime;
	private NewsTemplate template;
	private String content;
	private String sourceName;
	private boolean isToday;
	private long collectTime;
	
	private int clickCount;
	private String subtitle;

	// private

	public News() {
	}

	public News(Parcel source) {
		id = source.readInt();
		chnlId = source.readInt();
		channelName = source.readString();
		type = source.readString();
		transcodingUrl = source.readString();
		sourceUrl = source.readString();
		wapUrl = source.readString();
		isTop = source.readByte() == 1;
		isFrontShow = source.readByte() == 1;
		efficeTime = source.readLong();
		createTime = source.readLong();
		collectTime = source.readLong();
		template = source.readParcelable(NewsTemplate.class.getClassLoader());
		content = source.readString();
		sourceName = source.readString();
		isToday = source.readByte() == 1;
		
		clickCount = source.readInt();
		subtitle = source.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(chnlId);
		dest.writeString(channelName);
		dest.writeString(type);
		dest.writeString(transcodingUrl);
		dest.writeString(sourceUrl);
		dest.writeString(wapUrl);
		dest.writeByte(isTop ? (byte) 1 : (byte) 0);
		dest.writeByte(isFrontShow ? (byte) 1 : (byte) 0);
		dest.writeLong(efficeTime);
		dest.writeLong(createTime);
		dest.writeLong(collectTime);
		dest.writeParcelable(template, 1);
		dest.writeString(content);
		dest.writeString(sourceName);
		dest.writeInt(isToday ? (byte) 1 : (byte) 0);
		
		dest.writeInt(clickCount);
		dest.writeString(subtitle);
	}

	// chnl_id
	// transcoding_url
	// source_url
	// wap_url
	// is_top
	// is_front_show
	// effice_time
	// create_time
	// id
	// type
	public static final News make(JSONObject json) throws JSONException {
		News news = new News();
		news.setId(json.optInt("id"));
		news.setChnlId(json.optInt("chnl_id"));
		news.setChannelName(json.optString("chnl_name"));
		news.setType(json.optString("type"));
		news.setTranscodingUrl(json.optString("transcoding_url"));
		news.setSourceUrl(json.optString("source_url"));
		news.setWapUrl(json.optString("wap_url"));
		news.setTop(json.optInt("is_top", 0) == 1 ? true : false);
		// news.setTop(true);
		news.setFrontShow(json.optInt("is_front_show", 0) == 1 ? true : false);
		news.setEfficeTime(DateUtil.stringToDate(json.optString("effice_time"),
				"yyyy-MM-dd HH:mm:ss").getTime());
		news.setCreateTime(DateUtil.stringToDate(json.optString("create_time"),
				"yyyy-MM-dd HH:mm:ss").getTime());
		news.setCollectTime(DateUtil.stringToDate(
				json.optString("collection_time"), "yyyy-MM-dd HH:mm:ss")
				.getTime());
		news.setContent(json.optString("content"));
		news.setSourceName(getSafeString(json, "source_name"));
		// "id": null,
		// "type": "2",
		// "tags": "标签1,标签2,标签3",
		// "title": "title",
		// "urls": "http://123,http://123,http://123"
		JSONObject tJson = json.optJSONObject("infoTemplateVO");
		if (tJson != null) {
			NewsTemplate temp = new NewsTemplate();

			temp.setTitle(tJson.optString("title"));
			temp.setId(tJson.optInt("id"));
			temp.setDisplayType(tJson.optInt("type"));
			// temp.setSource(j)

			String tags = tJson.optString("tags");

			temp.setTag(tags.replace(",", " "));

			if (!TextUtils.isEmpty(tags)) {
				String[] ts = tags.split(",");
				temp.setTags(asListForTag(ts));
			}

			String imgs = tJson.optString("urls");
			if (!TextUtils.isEmpty(imgs)) {
				String[] ts = imgs.split(",");
				temp.setImgs(asList(ts));
			}
			news.setTemplate(temp);
		}

		if (news.isTop || DateUtil.isToday(news.getEfficeTime())) {
			news.setToday(true);
		} else {
			news.setToday(false);
		}
		
		news.setClickCount(json.optInt("click_count"));
		news.setSubtitle(json.optString("subtitle"));

		return news;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("id", this.id);
		obj.put("chnl_id", this.chnlId);
		obj.put("chnl_name", getValue(this.channelName));
		obj.put("type", getValue(this.type));
		obj.put("transcoding_url", getValue(this.transcodingUrl));
		obj.put("source_url", getValue(this.sourceUrl));
		obj.put("wap_url", getValue(this.wapUrl));
		obj.put("is_top", this.isTop);
		obj.put("is_front_show", this.isFrontShow);
		obj.put("effice_time",
				DateUtil.getDateStr(this.efficeTime, "yyyy-MM-dd HH:mm:ss"));
		obj.put("create_time",
				DateUtil.getDateStr(this.createTime, "yyyy-MM-dd HH:mm:ss"));
		obj.put("collection_time",
				DateUtil.getDateStr(this.collectTime, "yyyy-MM-dd HH:mm:ss"));
		obj.put("content", getValue(this.content));
		obj.put("source_name", this.sourceName == null ? "null"
				: this.sourceName);
		if (this.template != null) {
			JSONObject tJson = new JSONObject();
			tJson.put("title", getValue(this.template.getTitle()));
			tJson.put("id", this.template.getId());
			tJson.put("type", this.template.getDisplayType());
			tJson.put("tags", getValue(this.template.getTag()));
			tJson.put("urls", StringUtil.join(this.template.getImgs(), ","));
			obj.put("infoTemplateVO", tJson);
		}
		obj.put("click_count", this.clickCount);
		obj.put("subtitle", this.subtitle);
		return obj;
	}

	private String getValue(String s) {
		return s == null ? "" : s;
	}

	private static String getSafeString(JSONObject json, String key) {
		String val = json.optString(key);
		if ("null".equals(val))
			return null;
		return val;
	}

	private static ArrayList<TagView.Tag> asListForTag(String[] arrays) {
		ArrayList<TagView.Tag> list = new ArrayList<TagView.Tag>();
		for (String s : arrays) {
			if (!TextUtils.isEmpty(s))
				list.add(new Tag(s, Color.parseColor("#F97070")));//#7C83DE
		}
		return list;
	}

	private static ArrayList<String> asList(String[] arrays) {
		ArrayList<String> list = new ArrayList<String>();
		for (String url : arrays) {
			list.add(url.startsWith(HTTP_PREFIX) ? url
					: (BaseRequest.IMG_URL + url));
		}
		return list;
	}

	/**
	 * 组织资讯列表分组显示
	 * 
	 * @param source
	 * @param array
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<NewsGroup> makeAll(ArrayList<NewsGroup> source,
			JSONArray array, HashSet<Integer> outIds) throws JSONException {
		if (outIds == null) {
			outIds = new HashSet<Integer>();
		} else {
			// outIds.clear();
		}

		ArrayList<NewsGroup> list = new ArrayList<NewsGroup>();

		// ArrayList<News> temps = new ArrayList<News>();
		final int size = array.length();
		// for (int i = 0; i < size; i++) {
		// temps.add(news);
		// }
		// Collections.sort(temps);

		NewsGroup currentGroup = null;
		for (int i = 0; i < size; i++) {
			News news = make(array.getJSONObject(i));

			if ("adv".equals(news.getType())) {
				MobclickAgent.onEvent(Application.context(), "" + news.getId(),
						news.getTemplate().getTitle());
				TLog.log("youmeng", news.getTemplate().getTitle());
			}

			// 记录ID
			outIds.add(news.getId());

			// News news = temps.get(i);
			String currentNewsType = news.isToday() ? DateUtil.getDateStr(
					System.currentTimeMillis(), "yyyy.MM.dd EEEE") : DateUtil
					.getDateStr(news.getEfficeTime(), "yyyy.MM.dd EEEE");//
			// DateUtil.getFormatTime(news.getEfficeTime());
			// DateUtil.getDateStr(news.getEfficeTime(),"MM月dd日 hh:mm");

			if (i == 0) { // need append ?
				if (source != null && source.size() > 0) {
					list = source;
					if (source.get(source.size() - 1).getTypeName()
							.equals(currentNewsType)) {
						TLog.log("讲数据追加到尾部");
						currentGroup = list.get(list.size() - 1);
					} else {
						currentGroup = null;
					}
				}
			}

			// 如果当前组为空或者当前组与当前资讯所属组不匹配则创建新组
			if (currentGroup == null //
					|| (currentGroup != null && currentGroup.isTop() && !news
							.isToday()) // 当前组指定当前资讯非置顶
					|| (currentGroup != null && !currentGroup.isTop() && !currentGroup
							.getTypeName().equals(currentNewsType))) {
				// TLog.log("found new news type :" + currentNewsType);
				currentGroup = new NewsGroup();
				currentGroup.setTop(news.isToday());
				String typeName2 = "";
				if (news.isToday()) {
					typeName2 = "今日";
				} else if (DateUtil.isYestaday(news.getEfficeTime())) {
					typeName2 = "昨日";
				}
				currentGroup.setTypeName2(typeName2);
				currentGroup.setTypeName(currentNewsType);
				list.add(currentGroup);
			}
			currentGroup.addNews(news);
		}
		if (list.size() == 0 || array.length() == 0) {
			list = source;
		}
		return list;
	}

	public static ArrayList<News> makeAllForNews(JSONArray array)
			throws JSONException {
		ArrayList<News> temps = new ArrayList<News>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			News news = make(array.getJSONObject(i));
			temps.add(news);
		}
		return temps;
	}

	public static ArrayList<News> makeAllForCollectNews(JSONArray array,
			JSONArray localArray, HashSet<Integer> ids,
			HashSet<Integer> localIds) throws JSONException {
		HashSet<Integer> nowids = new HashSet<Integer>();
		ArrayList<News> temps = new ArrayList<News>();
		int size = array.length(), size2 = localArray.length();
		News news = null;
		News localNews = null;
		for (int i = 0, j = 0; i < size || j < size2;) {
			if (news == null && i < size) {
				news = make(array.getJSONObject(i));
				if (!ids.contains(news.getId())
						|| nowids.contains(news.getId())) {
					news = null;
					i++;
					continue;
				}
			}
			if (localNews == null && j < size2) {
				localNews = make(localArray.getJSONObject(j));
				if (!localIds.contains(localNews.getId())
						|| nowids.contains(localNews.getId())) {
					localNews = null;
					j++;
					continue;
				}
			}
			if (news == null) {
				nowids.add(localNews.getId());
				temps.add(localNews);
				localNews = null;
				j++;
			} else if (localNews == null) {
				nowids.add(news.getId());
				temps.add(news);
				news = null;
				i++;
			} else {
				if (news.getId() == localNews.getId()) {
					nowids.add(news.getId());
					temps.add(news);
					localNews = null;
					news = null;
					i++;
					j++;
				} else if (news.getCollectTime() >= localNews.getCollectTime()) {
					nowids.add(news.getId());
					temps.add(news);
					news = null;
					i++;
				} else {
					nowids.add(localNews.getId());
					temps.add(localNews);
					localNews = null;
					j++;
				}
			}
		}
		nowids.clear();
		return temps;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTranscodingUrl() {
		return transcodingUrl;
	}

	public void setTranscodingUrl(String transcodingUrl) {
		this.transcodingUrl = transcodingUrl;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getWapUrl() {
		return wapUrl;
	}

	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public boolean isFrontShow() {
		return isFrontShow;
	}

	public void setFrontShow(boolean isFrontShow) {
		this.isFrontShow = isFrontShow;
	}

	public long getEfficeTime() {
		return efficeTime;
	}

	public void setEfficeTime(long efficeTime) {
		this.efficeTime = efficeTime;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(long collectTime) {
		this.collectTime = collectTime;
	}

	public NewsTemplate getTemplate() {
		return template;
	}

	public void setTemplate(NewsTemplate template) {
		this.template = template;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public int getChnlId() {
		return chnlId;
	}

	public void setChnlId(int chnlId) {
		this.chnlId = chnlId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static final Parcelable.Creator<News> CREATOR = new Creator<News>() {

		@Override
		public News[] newArray(int size) {
			return new News[size];
		}

		@Override
		public News createFromParcel(Parcel source) {
			return new News(source);
		}
	};

	@Override
	public int compareTo(News anther) {
		if (isTop && !anther.isTop)
			return 1;
		if (isTop && anther.isTop || (!isTop && !anther.isTop())) {
			if (isFrontShow && !anther.isFrontShow) {
				return 1;
			}
			if (isFrontShow && anther.isFrontShow
					|| (!isFrontShow && !anther.isFrontShow)) {
				if (createTime > anther.getCreateTime()) {
					return 1;
				}
			}
			return -1;
		}
		return 0;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public boolean isToday() {
		return isToday;
	}

	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
}
