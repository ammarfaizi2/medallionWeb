package vo;

public class BankAccountSearchParameters {
	public int bankAccountNoOperator;
	public String bankAccountSearchNo;
	public int bankAccountNameOperator;
	public String bankAccountSearchName;
	public int bankAccountCurrencyOperator;
	public String bankkAccountSearchCurrency;

	public String param;
	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

}
