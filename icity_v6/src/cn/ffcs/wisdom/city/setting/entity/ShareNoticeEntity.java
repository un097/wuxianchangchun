package cn.ffcs.wisdom.city.setting.entity;

public class ShareNoticeEntity {
	
	private ShareNotice data;

	public ShareNotice getData() {
		return data;
	}

	public void setData(ShareNotice data) {
		this.data = data;
	}

	public class ShareNotice {
		private String reminderContent;

		public String getReminderContent() {
			return reminderContent;
		}

		public void setReminderContent(String reminderContent) {
			this.reminderContent = reminderContent;
		}
	}
}
