package cn.ffcs.external.share.entity;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * <p>Title: 分享实体         </p>
 * <p>Description: 
 * 
 * </p>
 * <p>@author: Leo               </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-11-18             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class CustomSocialShareEntity implements Serializable {

	public static final long serialVersionUID = 1L;
	public String shareTitle;//分享标题：微信和易信的标题
	public String shareComment;//分享描述：微信和易信的内容
	public String shareSource;// 分享来源，作用：可以表示友盟分享的来源，也可以表示摄像头名字
	public String shareUrl;// 分享地址
	public String shareContent;// 分享内容
	public byte[] imageByte;// 分享图片
	public Bitmap imageBitmap;//分享图片
	public String imagePath;// 图片地址，随手拍截图的全地址
	public String imageUrl;
	public String shareType;// 分享类型，1：路况；2：景点
	public String cityCode;// 城市编号
	public String mobile;//手机号码
	public boolean isShowPhoto;//是否显示，用于关闭随手拍分享入口
	public String spreadCode;//邀请码
}
