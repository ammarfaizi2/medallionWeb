package vo;

import java.util.Date;

public class RgTradeSearchParameters {
	public Date rgTradeSearchTransactionDateFrom;
	public Date rgTradeSearchTransactionDateTo;
	public String rgTradeSearchFundKey;
	public String rgTradeSearchInvestorAcct;
	public int TransactionNoOperator;
	public String transactionSearchNoOperator;

	public String param;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
