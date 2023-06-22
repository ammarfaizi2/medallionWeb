package vo;

import java.io.Serializable;

public class AcquisitionSearchParameters implements Serializable {
	private static final long serialVersionUID = -553907100655760889L;

	private String fromDate;
	private String toDate;
	private Long customerKey;
	private String securityType;
	private Long securityKey;
	private boolean query;

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public Long getCustomerKey() {
		return customerKey;
	}

	public void setCustomerKey(Long customerKey) {
		this.customerKey = customerKey;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public Long getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(Long securityKey) {
		this.securityKey = securityKey;
	}

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
