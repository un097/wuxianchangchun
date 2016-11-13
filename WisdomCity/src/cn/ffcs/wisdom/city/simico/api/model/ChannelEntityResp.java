package cn.ffcs.wisdom.city.simico.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: 频道实体         </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: Leo               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2014年6月11日             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ChannelEntityResp implements Serializable {

	public List<ChannelEntity> data;

	private static final long serialVersionUID = 1L;

	public class ChannelEntity implements Serializable{
		private static final long serialVersionUID = 1L;
		public int parent_id;// 频道父类id
		public int chnl_order;// 频道序号
		public String name;// 频道名字
		public int id;// 频道id
		public String type;// 类型，暂不使用
		public String description;// 描述
		public String logo;// 图标
	}

}
