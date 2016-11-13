package cn.ffcs.wisdom.city.personcenter.entity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:       我的关联实体           </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-13           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MyRelevanceEntity {

	private List<MyRelevance> data;

	public List<MyRelevance> getData() {
		return data;
	}

	public void setData(List<MyRelevance> data) {
		this.data = data;
	}

	public class MyRelevance {
		
		public static final String ID_TRAFFIC = "1"; // 违章查询
		
		public static final String ID_GONGJIJIN = "2"; // 公积金
		public static final String ID_YILIAO = "3"; // 医疗保险
		public static final String ID_SHEHUI = "4"; // 社会保险
		public static final String ID_YANGLAO = "5"; // 养老保险
		
		public static final String ID_WATER = "6"; // 水费
		public static final String ID_ELETRIC = "7"; // 电费
		public static final String ID_COAL = "8"; // 煤气费
		
		private List<MyRelevanceGroup> keyGroupList;
		private String pbtId;
		private String pbtName;

		public List<MyRelevanceGroup> getKeyGroupList() {
			return keyGroupList;
		}

		public void setKeyGroupList(List<MyRelevanceGroup> keyGroupList) {
			this.keyGroupList = keyGroupList;
		}

		public String getPbtId() {
			return pbtId;
		}

		public void setPbtId(String pbtId) {
			this.pbtId = pbtId;
		}

		public String getPbtName() {
			return pbtName;
		}

		public void setPbtName(String pbtName) {
			this.pbtName = pbtName;
		}

		public class MyRelevanceGroup implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = -24428066339673075L;

			private int keyGroupId;
			private String isPrimaryChk;
			private String isPrivateChk;
			private int itemId;

			private List<MyRelevanceDetail> keyList;

			public int getKeyGroupId() {
				return keyGroupId;
			}

			public void setKeyGroupId(int keyGroupId) {
				this.keyGroupId = keyGroupId;
			}

			public List<MyRelevanceDetail> getKeyList() {
				return keyList;
			}

			public void setKeyList(List<MyRelevanceDetail> keyList) {
				this.keyList = keyList;
			}

			public String getIsPrimaryChk() {
				return isPrimaryChk;
			}

			public void setIsPrimaryChk(String isPrimaryChk) {
				this.isPrimaryChk = isPrimaryChk;
			}

			public String getIsPrivateChk() {
				return isPrivateChk;
			}

			public void setIsPrivateChk(String isPrivateChk) {
				this.isPrivateChk = isPrivateChk;
			}

			public int getItemId() {
				return itemId;
			}

			public void setItemId(int itemId) {
				this.itemId = itemId;
			}

			public class MyRelevanceDetail implements Serializable {
				/**
				 * 
				 */
				private static final long serialVersionUID = -6675452960683449182L;
				private String cityCode;
				private String cityName;
				private String keyName;
				private String keyText;
				private String keyDesc;
				private String keyValue;

				public String getCityCode() {
					return cityCode;
				}

				public void setCityCode(String cityCode) {
					this.cityCode = cityCode;
				}

				public String getCityName() {
					return cityName;
				}

				public void setCityName(String cityName) {
					this.cityName = cityName;
				}

				public String getKeyText() {
					return keyText;
				}

				public void setKeyText(String keyText) {
					this.keyText = keyText;
				}

				public String getKeyDesc() {
					return keyDesc;
				}

				public void setKeyDesc(String keyDesc) {
					this.keyDesc = keyDesc;
				}

				public String getKeyValue() {
					return keyValue;
				}

				public void setKeyValue(String keyValue) {
					this.keyValue = keyValue;
				}

				public String getKeyName() {
					return keyName;
				}

				public void setKeyName(String keyName) {
					this.keyName = keyName;
				}
			}
		}
	}
}
