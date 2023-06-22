package vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.simian.medallion.model.CsBillingDetail;

public class BillingParameters {
	public static String FILTER_ALL = "ALL";
	public static String FILTER_CUSTOMER_NO = "CustomerNumber";
	public static String FILTER_ACCOUNT_NO = "AccountNumber";

	private String filter;
	private String customer;
	private String customerKey;
	private String customerDesc;
	private String account;
	private String accountKey;
	private String accountDesc;
	private String invMonth;
	private String invYear;
	private Date invDate;
	private Date invDueDate;
	private Boolean consolidate;
	private String message;
	private String billingKeys;
	private Long billingKey;
	private Boolean includeZeroAmount;
	private String invNo;
	private boolean query;
	private String sessionTag;
	private String processMark;

	private List<CsBillingDetail> details = new ArrayList<CsBillingDetail>();

	public BillingParameters() {
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getInvMonth() {
		return invMonth;
	}

	public void setInvMonth(String invMonth) {
		this.invMonth = invMonth;
	}

	public String getInvYear() {
		return invYear;
	}

	public void setInvYear(String invYear) {
		this.invYear = invYear;
	}

	public Date getInvDate() {
		return invDate;
	}

	public void setInvDate(Date invDate) {
		this.invDate = invDate;
	}

	public Date getInvDueDate() {
		return invDueDate;
	}

	public void setInvDueDate(Date invDueDate) {
		this.invDueDate = invDueDate;
	}

	public Boolean isConsolidate() {
		return consolidate;
	}

	public void setConsolidate(Boolean consolidate) {
		this.consolidate = consolidate;
	}

	public String getCustomerDesc() {
		return customerDesc;
	}

	public void setCustomerDesc(String customerDesc) {
		this.customerDesc = customerDesc;
	}

	public String getAccountDesc() {
		return accountDesc;
	}

	public void setAccountDesc(String accountDesc) {
		this.accountDesc = accountDesc;
	}

	public String getCustomerKey() {
		return customerKey;
	}

	public void setCustomerKey(String customerKey) {
		this.customerKey = customerKey;
	}

	public String getAccountKey() {
		return accountKey;
	}

	public void setAccountKey(String accountKey) {
		this.accountKey = accountKey;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	// public Boolean isIncludeZeroAmount() { return includeZeroAmount; }
	// public void setIncludeZeroAmount(Boolean includeZeroAmount) {
	// this.includeZeroAmount = includeZeroAmount; }
	public Boolean getIncludeZeroAmount() {
		return includeZeroAmount;
	}

	public void setIncludeZeroAmount(Boolean includeZeroAmount) {
		this.includeZeroAmount = includeZeroAmount;
	}

	public String getBillingKeys() {
		return billingKeys;
	}

	public void setBillingKeys(String billingKeys) {
		this.billingKeys = billingKeys;
	}

	public Long getBillingKey() {
		return billingKey;
	}

	public void setBillingKey(Long billingKey) {
		this.billingKey = billingKey;
	}

	public List<CsBillingDetail> getDetails() {
		return details;
	}

	public void setDetails(List<CsBillingDetail> details) {
		this.details = details;
	}

	public String getInvNo() {
		return invNo;
	}

	public void setInvNo(String invNo) {
		this.invNo = invNo;
	}

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

	@Override
	public String toString() {
		return "filter=" + filter + ", customer=" + customer + ", account=" + account + ", invMonth=" + invMonth + ", invYear=" + invYear + ", invDate=" + invDate + ", invDueDate=" + invDueDate + ", consolidate=" + consolidate + ", includeZeroAmount=" + includeZeroAmount;
	}
}
