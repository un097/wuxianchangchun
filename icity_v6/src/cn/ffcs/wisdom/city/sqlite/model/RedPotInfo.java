package cn.ffcs.wisdom.city.sqlite.model;

import java.io.Serializable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <p>Title:  小红点      </p>
 * <p>Description:  记录菜单栏目是否被访问，如果被访问，小红点直接不显示     </p>
 * <p>@author: Eric.wsd                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2014-3-11           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@DatabaseTable(tableName = "t_redpot_info")
public class RedPotInfo implements Serializable {
	private static final long serialVersionUID = -1999409046889939375L;

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = "menu_id")
	private String menuId; // 菜单id

	@DatabaseField(columnName = "is_read", dataType = DataType.BOOLEAN)
	private boolean isRead; // 菜单id

	@DatabaseField(columnName = "redRecordTime")
	private String redRecordTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getRedRecordTime() {
		return redRecordTime;
	}

	public void setRedRecordTime(String redRecordTime) {
		this.redRecordTime = redRecordTime;
	}

}
