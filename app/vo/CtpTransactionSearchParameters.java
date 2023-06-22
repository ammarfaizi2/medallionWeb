package vo;

import java.util.Date;

public class CtpTransactionSearchParameters {
	
	private String custodyAccountKey;
	private String customerCodeSearchId;
	private String securityType;
	private String securityId;
	private Date transactionDateFrom;
	private Date transactionDateTo;
	private Date settlementDateFrom;
	private Date settlementDateTo;
	private Integer ctpReport;
	private String ctpReportOperator;
	
	private boolean query;
	//public boolean isQuery() { return query; }
	//public void setQuery(boolean query) { this.query = query; }

	public String getCustodyAccountKey() {
		return custodyAccountKey;
	}

	public void setCustodyAccountKey(String custodyAccountKey) {
		this.custodyAccountKey = custodyAccountKey;
	}

	public String getCustomerCodeSearchId() {
		return customerCodeSearchId;
	}

	public void setCustomerCodeSearchId(String customerCodeSearchId) {
		this.customerCodeSearchId = customerCodeSearchId;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public String getSecurityId() {
		return securityId;
	}

	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}

	public Date getTransactionDateFrom() {
		return transactionDateFrom;
	}

	public void setTransactionDateFrom(Date transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}

	public Date getTransactionDateTo() {
		return transactionDateTo;
	}

	public void setTransactionDateTo(Date transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
	}

	public Date getSettlementDateFrom() {
		return settlementDateFrom;
	}

	public void setSettlementDateFrom(Date settlementDateFrom) {
		this.settlementDateFrom = settlementDateFrom;
	}

	public Date getSettlementDateTo() {
		return settlementDateTo;
	}

	public void setSettlementDateTo(Date settlementDateTo) {
		this.settlementDateTo = settlementDateTo;
	}

	public Integer getCtpReport() {
		return ctpReport;
	}

	public void setCtpReport(Integer ctpReport) {
		this.ctpReport = ctpReport;
	}

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

	public String getCtpReportOperator() {
		return ctpReportOperator;
	}

	public void setCtpReportOperator(String ctpReportOperator) {
		this.ctpReportOperator = ctpReportOperator;
	}
	
	
}
