package cn.ffcs.wisdom.city.modular.query.resp;

import java.util.List;

import cn.ffcs.wisdom.city.modular.query.entity.QueryInfo;
import cn.ffcs.wisdom.http.BaseResp;

/**
 * <p>Title:  查询类配置信息响应类</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-10-19             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class QueryInfoResp extends BaseResp {

	private List<QueryInfo> home;

	public List<QueryInfo> getHome() {
		return home;
	}

	public void setHome(List<QueryInfo> home) {
		this.home = home;
	}

}
