package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * <p>Title: 收藏表   </p>
 * <p>Description: 
 *  用于缓存收藏数据，创建完实体类后，需要在res/values/dbconfig.xml配置相应的数据库实体
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-1-10             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@DatabaseTable(tableName = "t_favorite_info")
public class FavoriteInfoModel implements Serializable {
	private static final long serialVersionUID = -4385627951756290549L;

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = "city_code")
	private String citycode;

	@DatabaseField(columnName = "item_id")
	private String itemId;

	@DatabaseField(columnName = "app_info")
	private String appInfo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(String appInfo) {
		this.appInfo = appInfo;
	}
}
