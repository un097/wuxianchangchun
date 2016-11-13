package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: WisdomCity</p>
 * <p>Description: </p>
 * <p>Author: tianya</p>
 * <p>CreateTime: 2012-4-8 上午11:29:59 </p>
 * <p>CopyRight: 4.0.2 </p>
 */
public class ConfigParamsEntity implements Serializable {

	private static final long serialVersionUID = 882412737800151605L;

	private String status;
	private String desc;
	private List<ConfigParams> codelist = new ArrayList<ConfigParams>();

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<ConfigParams> getCodelist() {
		return codelist;
	}

	public void setCodelist(List<ConfigParams> codelist) {
		this.codelist = codelist;
	}
}
