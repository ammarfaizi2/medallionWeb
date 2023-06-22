package vo;

public class InvestmentAccountSearchParameters {
	public int InvestmentAccountNoOperator;
	public String InvestmentAccountSearchNo;
	public int InvestmentAccountNameOperator;
	public String InvestmentAccountSearchName;
	public int InvestmentAccountCurrencyOperator;
	public String InvestmentAccountSearchCurrency;
	public String InvestmentAccountFundCode;
	public int InvestmentAccountCifAperdOperator;
	public String InvestmentAccountSearchCifAperd;

	public String param;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}