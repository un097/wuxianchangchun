package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 意见反馈实体类
 * @author Eric.wsd
 * @since 2012-3-19
 */
public class FeedbackEntity implements Serializable {

	private static final long serialVersionUID = -2001421057759186655L;

	private String status;
	private String desc;
	private List<FeedbackItem> list;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<FeedbackItem> getList() {
		return list;
	}

	public void setList(List<FeedbackItem> list) {
		this.list = list;
	}

	public class FeedbackItem implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2859199575020194492L;

		private String feedback;
		private String feedbackReplyTime;
		private String feedbackReply;
		private String feedbackTime;

		public String getFeedback() {
			return feedback;
		}

		public void setFeedback(String feedback) {
			this.feedback = feedback;
		}

		public String getFeedbackReplyTime() {
			return feedbackReplyTime;
		}

		public void setFeedbackReplyTime(String feedbackReplyTime) {
			this.feedbackReplyTime = feedbackReplyTime;
		}

		public String getFeedbackReply() {
			return feedbackReply;
		}

		public void setFeedbackReply(String feedbackReply) {
			this.feedbackReply = feedbackReply;
		}

		public String getFeedbackTime() {
			return feedbackTime;
		}

		public void setFeedbackTime(String feedbackTime) {
			this.feedbackTime = feedbackTime;
		}
	}
}
