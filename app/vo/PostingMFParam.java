package vo;

import java.util.Date;

public class PostingMFParam {
	private String fundCode;
	private Long fundCodeKey;
	private String fundCodeDesc;
	private Date fromDate;
	private Date toDate;
	private String fromDateStr;
	private String toDateStr;
	private String appDate;

	private String sessionTag;
	private String processMark;

	public String getFundCode() {
		return fundCode;
	}

	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}

	public Long getFundCodeKey() {
		return fundCodeKey;
	}

	public void setFundCodeKey(Long fundCodeKey) {
		this.fundCodeKey = fundCodeKey;
	}

	public String getFundCodeDesc() {
		return fundCodeDesc;
	}

	public void setFundCodeDesc(String fundCodeDesc) {
		this.fundCodeDesc = fundCodeDesc;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getFromDateStr() {
		return fromDateStr;
	}

	public void setFromDateStr(String fromDateStr) {
		this.fromDateStr = fromDateStr;
	}

	public String getToDateStr() {
		return toDateStr;
	}

	public void setToDateStr(String toDateStr) {
		this.toDateStr = toDateStr;
	}

	public String getAppDate() {
		return appDate;
	}

	public void setAppDate(String appDate) {
		this.appDate = appDate;
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

	@Override
	public String toString() {
		return "funCode=" + fundCode + ", fundCodeKey=" + fundCodeDesc + ", fundCodeDesc=" + fundCodeDesc + ", fromDate=" + fromDate + ", toDate=" + toDate;
	}
}
