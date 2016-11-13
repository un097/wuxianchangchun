package cn.ffcs.external.share.entity;

import java.io.Serializable;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * <p>Title:自定义分享实体          </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: Leo               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-11-16             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CustomSocialEntity implements Serializable {
	public static final long serialVersionUID = 1L;
	public String socialNameEn;// 英文名
	public String socialNameCn;// 中文名
	public int socialImageResId;// 图片
	public SHARE_MEDIA media;// 分享平台
	public CustomSocialShareEntity shareEntity;//分享实体

}
