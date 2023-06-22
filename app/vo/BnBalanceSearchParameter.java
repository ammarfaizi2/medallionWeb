package vo;

import java.util.Date;

public class BnBalanceSearchParameter {

	public String fundCode;
	public Date dateFrom;
	public Date dateTo;
	public int accountNoOperator;
	public String accountSearchNo;

	public String param;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
