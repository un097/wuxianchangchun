package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:  省份城市列表实体类		   </p>
 * <p>Description:<br/>          
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-1-28           </p>
 */
public class ProvinceListEntity implements Serializable {

	private static final long serialVersionUID = 9212949558335250629L;

	private String desc;
	private String status;
	private List<ProvinceEntity> provinces;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ProvinceEntity> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<ProvinceEntity> provinces) {
		this.provinces = provinces;
	}

}
