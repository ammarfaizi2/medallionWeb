package vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class TradeSearchParameters implements Serializable {

	public String uniqeId;
	public Date transactionDateFrom;
	public Date transactionDateTo;
	public String paymentDate;
	public String type;
	public String cancelBy;
	public String cancelByNo;
	public String fundKey;
	public String fundCode;
	public String productCode;
	public String investorNo;
	public Long transactionNo;
	public String fundActionKey;
	public Integer transactionNoOperator;
	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
