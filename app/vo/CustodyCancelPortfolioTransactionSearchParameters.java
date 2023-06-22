package vo;

import java.util.Date;

public class CustodyCancelPortfolioTransactionSearchParameters {
	public String transactionNo;
	public String custodyAccountKey;
	public String customerCodeSearchId;
	public String securityType;
	public String securityId;
	public Date transactionDateFrom;
	public Date transactionDateTo;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
