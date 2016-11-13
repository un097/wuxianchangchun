package cn.ffcs.wisdom.interfaces;


/**
 * <p>Title:      截屏回调接口                 </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-5           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public interface IScreenShotCallBack {

	/**
	 * 回调方法
	 * @param filePath 文件路径
	 */
	public void call(String filePath);
}
