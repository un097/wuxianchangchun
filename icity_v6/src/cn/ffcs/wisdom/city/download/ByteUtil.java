package cn.ffcs.wisdom.city.download;

import java.math.BigDecimal;

/**
 * <p>Title: 字节工具类         </p>
 * <p>Description: 
 * 	应用大小大于1兆的，单位以'MB'显示，默认是'KB'显示
 * </p>
 * <p>@author: liaodl               	</p>
 * <p>Copyright: Copyright (c) 2012    	</p>
 * <p>Company: ffcs Co., Ltd.         	</p>
 * <p>Create Time: 2012-10-16           </p>
 * <p>@author:                        	</p> 
 * <p>Update Time:                     	</p>
 * <p>Updater:                         	</p>
 * <p>Update Comments:                 	</p>
 */
public class ByteUtil {
	/**
	 * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytes2KBorMB(Long bytes) {
		BigDecimal filesize = new BigDecimal(bytes);
		BigDecimal megabyte = new BigDecimal(1024 * 1024);
		float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
		if (returnValue > 1)
			return (returnValue + "M");
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
		return (returnValue + "K");
	}
	
	public static String bytes2KBorMB(int bytes) {
		BigDecimal filesize = new BigDecimal(bytes);
		BigDecimal megabyte = new BigDecimal(1024 * 1024);
		float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
		if (returnValue > 1)
			return (returnValue + "M");
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
		return (returnValue + "K");
	}
}
