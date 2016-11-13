package cn.ffcs.wisdom.city.modular.query.emuns;

import cn.ffcs.wisdom.city.modular.query.views.CheckImageView;
import cn.ffcs.wisdom.city.modular.query.views.DateView;
import cn.ffcs.wisdom.city.modular.query.views.DateYYMMView;
import cn.ffcs.wisdom.city.modular.query.views.EditViewCH;
import cn.ffcs.wisdom.city.modular.query.views.EditViewEN;
import cn.ffcs.wisdom.city.modular.query.views.EditViewEnNum;
import cn.ffcs.wisdom.city.modular.query.views.EditViewNum;
import cn.ffcs.wisdom.city.modular.query.views.ListButton;
import cn.ffcs.wisdom.city.modular.query.views.PassWordView;

/**
 * <p>Title:  控件类型</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-10-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@SuppressWarnings({ "rawtypes"})
public enum ViewType {

	DATE("qitDate", DateView.class)/*年月日时间格式*/, 
	DATE_YYMM("qitDateMonth", DateYYMMView.class)/*年月时间格式*/, 
	TEXT_EN("qitTextEn", EditViewEN.class)/*英文文本*/, 
	TEXT_CH("qitTextCh", EditViewCH.class)/*中文文本*/, 
	TEXT_NUMBER("qitTextNumber", EditViewNum.class)/*数字文本*/, 
	TEXT_EN_NUMBER("qitTextEnNumber",EditViewEnNum.class),/*英文和数字*/ 
	LIST("qitList",ListButton.class),/*下拉列表*/
	CHECK_IMAGE("qitCheckImage", CheckImageView.class)/*验证码*/, 
	
	PASSWORD("qitPassword", PassWordView.class)/*密码输入*/;

	ViewType(String id,  Class clazz) {
		this.id = id;
		this.clazz = clazz;
	}

	private String id;
	
	private Class clazz;
	
	public static ViewType getViewType(String id) {
		final ViewType[] types = values();
		for (ViewType type : types) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
	

}
