package vo;

import java.util.Date;

public class FaTransactionSearchParametrs {
	public Date faTransactionSearchTransactionDateFrom;
	public Date faTransactionSearchTransactionDateTo;
	public String faTransactionSearchFundKey;
	public int TransactionNoOperator;
	public String transactionSearchNoOperator;
	public String journalTypeOperator;

	public String param;
	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
