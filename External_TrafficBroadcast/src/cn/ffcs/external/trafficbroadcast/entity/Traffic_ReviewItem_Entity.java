package cn.ffcs.external.trafficbroadcast.entity;

/**
 * 评论列表中单条评论详情类
 * 
 * @author daizhq
 * 
 * @date 2014.12.05
 * */
public class Traffic_ReviewItem_Entity {

	// 评论主键ID
	private int id;
	// 用户id（非手机号）
	private int user_id;
	// 评论的主题
	private String title;
	// 评论类型 1—资讯评论 2—路况评论
	private int type;
	// 评论所属模块Id
	private int info_id;
	// 评论的内容
	private String content;
	// 点赞数
	private String praise_num;
	// 发布评论的时间 格式为yyyy-MM-dd HH:mm:ss，例如：2008-01-25 20:23:30
	private String create_time;
	// 审核的时间 格式为yyyy-MM-dd HH:mm:ss，例如：2008-01-25 20:23:30
	private String audit_time;
	//评论用户手机号
	private String mobile;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getInfo_id() {
		return info_id;
	}

	public void setInfo_id(int info_id) {
		this.info_id = info_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(String praise_num) {
		this.praise_num = praise_num;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getAudit_time() {
		return audit_time;
	}

	public void setAudit_time(String audit_time) {
		this.audit_time = audit_time;
	}

}
