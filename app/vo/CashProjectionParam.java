package vo;

import java.util.Date;
import java.util.List;

public class CashProjectionParam {
	private List<String> parameter;
	private String customer;
	private String customerKey;
	private String customerDesc;
	private Date fromDate;
	private Date toDate;
	private String appDate;
	private String sessionTag;
	private String processMark;
	private Boolean sentMail;
	
	
	public List<String> getParameter() {
		return parameter;
	}
	public void setParameter(List<String> parameter) {
		this.parameter = parameter;
	}
	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}
	
	public String getCustomerKey() {
		return customerKey;
	}

	public void setCustomerKey(String customerKey) {
		this.customerKey = customerKey;
	}
	
	public String getCustomerDesc() {
		return customerDesc;
	}

	public void setCustomerDesc(String customerDesc) {
		this.customerDesc = customerDesc;
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
	public Boolean getSentMail() {
		return sentMail;
	}
	public void setSentMail(Boolean sentMail) {
		this.sentMail = sentMail;
	}
	
}
