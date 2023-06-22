package vo;

import java.util.Date;

public class InquiryCorporateAnnoncementSearchParameter {

	public String actionCode;
	public String securityType;
	public String securityCode;
	public Date dateFrom;
	public Date dateTo;
	public int corporateNoOperator;
	public String corporateSearchNo;

	public String param;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

}
