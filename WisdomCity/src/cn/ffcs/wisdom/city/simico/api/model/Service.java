//package cn.ffcs.wisdom.city.simico.api.model;
//
//import java.util.ArrayList;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
///**
// * 服务项
// * 
// * @author Tonlin
// * 
// */
//public class Service implements Parcelable {
//
//	private int id;
//	private String icon;
//	private String name;
//	private int parentId;
//	private String parentName;
//
//	public Service() {
//	}
//
//	public Service(Parcel source) {
//		id = source.readInt();
//		icon = source.readString();
//		name = source.readString();
//		parentId = source.readInt();
//		parentName = source.readString();
//	}
//
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeInt(id);
//		dest.writeString(icon);
//		dest.writeString(name);
//		dest.writeInt(parentId);
//		dest.writeString(parentName);
//	}
//
//	// "icon":
//	// "upload/3507/menu/img/400/icon_0_1365406508057_32046204189533488295669823.png",
//	// "menuName": "母婴用品",
//	// "menuId": 785539,
//	// "menuPid": 427659,
//	// "cityCode": "3507",
//	// "menuType": "wap",
//	// "package_": null,
//	// "main": null,
//	// "url": "http://218.5.102.205:8080/zhqz/ysyymyyp.do",
//	// "appUrl": null,
//	// "menudesc": null,
//	// "mustLogin": "false",
//	// "baseLine": "400",
//	// "isApp": "0",
//	// "v6Icon":
//	// "upload/3507/menu/img/400/icon_4_1366334413822_22682276-7941570250990718315.png",
//	// "recommend": "0",
//	// "menuVer": 3643,
//	// "appsize": "K",
//	// "menuOrder": 3,
//	// "recommendOrder": null,
//	// "isNew": null,
//	// "isRed": 0,
//	// "commonOrder": null,
//	// "parMapString": ""
//	public static Service make(JSONObject json) throws JSONException {
//		Service service = new Service();
//		service.setId(json.optInt("menuId"));
//		service.setIcon(json.optString("icon"));
//		service.setName(json.optString("menuName"));
//		service.setParentId(json.optInt("menuPid"));
//		//service.setParentName(json.optString(""));
//		return service;
//	}
//
//	public static ArrayList<Service> makeAll(JSONArray array)
//			throws JSONException {
//		ArrayList<Service> list = new ArrayList<Service>();
//		final int size = array.length();
//		for (int i = 0; i < size; i++) {
//			list.add(make(array.getJSONObject(i)));
//		}
//		return list;
//	}
//
//	public String getIcon() {
//		return icon;
//	}
//
//	public void setIcon(String icon) {
//		this.icon = icon;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}
//
//	public int getParentId() {
//		return parentId;
//	}
//
//	public void setParentId(int parentId) {
//		this.parentId = parentId;
//	}
//
//	public String getParentName() {
//		return parentName;
//	}
//
//	public void setParentName(String parentName) {
//		this.parentName = parentName;
//	}
//
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	public static final Parcelable.Creator<Service> CREATOR = new Creator<Service>() {
//
//		@Override
//		public Service createFromParcel(Parcel source) {
//			return new Service(source);
//		}
//
//		@Override
//		public Service[] newArray(int size) {
//			return new Service[size];
//		}
//	};
//}
