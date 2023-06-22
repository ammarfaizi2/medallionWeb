package vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PublishMFParam {
	private List<String> parameter;
	private List<Long> fundKeys;
	private List<Date> publishDates;
	private Date appDate;

	private String sessionTag;
	private String processMark;

	public List<String> getParameter() {
		return parameter;
	}

	public void setParameter(List<String> parameter) {
		this.parameter = parameter;
	}

	public List<Long> getFundKeys() {
		return fundKeys;
	}

	public void setFundKeys(List<Long> fundKeys) {
		this.fundKeys = fundKeys;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public List<Date> getPublishDates() {
		return publishDates;
	}

	public void setPublishDates(List<Date> publishDates) {
		this.publishDates = publishDates;
	}

	public String getSessionTag() {
		return sessionTag;
	}

	public void setSessionTag(String sessionTag) {
		this.sessionTag = sessionTag;
	}

	public String getProcessMark() {
		return processMark;
	}

	public void setProcessMark(String processMark) {
		this.processMark = processMark;
	}

	public void populate() {
		fundKeys = new ArrayList<Long>();
		publishDates = new ArrayList<Date>();
		for (String param : parameter) {
			String[] array = param.split("\\|");
			fundKeys.add(new Long(array[0].trim()));
			publishDates.add(new Date(new Long(array[1].trim())));
		}
	}
}
