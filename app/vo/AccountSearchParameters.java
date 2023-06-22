package vo;

public class AccountSearchParameters {
	public int accountNoOperator;
	public String accountSearchNo;
	public int accountNameOperator;
	public String accountSearchName;
	public int accountCurrencyOperator;
	public String accountSearchCurrency;

	public String param;
	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

}
