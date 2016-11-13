/**
 * 
 */
package cn.ffcs.wisdom.city.setting.share;

import java.util.Collections;
import java.util.List;

import cn.ffcs.wisdom.http.BaseResp;


/**
 * <p>Title: 分享有礼         </p>
 * <p>Description: 
 *  分享有礼
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-6-9             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class SMSResp extends BaseResp {
	private String native_net;
	private List<String> list;
	
	public String getNative_net() {
		return native_net;
	}
	public void setNative_net(String native_net) {
		this.native_net = native_net;
	}
	public List<String> getList() {
		if(list == null) 
			return Collections.emptyList();
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
	}
	
	
}
