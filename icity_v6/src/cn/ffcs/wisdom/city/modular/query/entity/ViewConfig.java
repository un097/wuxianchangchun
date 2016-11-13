package cn.ffcs.wisdom.city.modular.query.entity;

import java.util.List;

/**
 * <p>Title:  查询类控件配置信息</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-10-19             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ViewConfig {

	private String queryPlaceholder; // 默认提示文本
	private String queryInputTitle; // 输入字段的标题
	private String inputType; // 输入类型
	private String queryDefault; // 输入的默认值
	private String fieldName; // 字段名
	private boolean isMustInput; // 是否为必填
	private String inputId; // 输入标识
	private List<DataArray> queryInputDataInfo;

	public List<DataArray> getQueryInputDataInfo() {
		return queryInputDataInfo;
	}

	public void setQueryInputDataInfo(List<DataArray> queryInputDataInfo) {
		this.queryInputDataInfo = queryInputDataInfo;
	}

	public void setMustInput(boolean isMustInput) {
		this.isMustInput = isMustInput;
	}

	public String getQueryPlaceholder() {
		return queryPlaceholder;
	}

	public void setQueryPlaceholder(String queryPlaceholder) {
		this.queryPlaceholder = queryPlaceholder;
	}

	public String getQueryInputTitle() {
		return queryInputTitle;
	}

	public void setQueryInputTitle(String queryInputTitle) {
		this.queryInputTitle = queryInputTitle;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getQueryDefault() {
		return queryDefault;
	}

	public void setQueryDefault(String queryDefault) {
		this.queryDefault = queryDefault;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean getIsMustInput() {
		return isMustInput;
	}

	public void setIsMustInput(boolean isMustInput) {
		this.isMustInput = isMustInput;
	}

	public String getInputId() {
		return inputId;
	}

	public void setInputId(String inputId) {
		this.inputId = inputId;
	}

}
