package vo;

import java.util.Date;

public class LiquidationProcessSearchParameter {
	public Date liquidationSearchLiquidationDateFrom;
	public Date liquidationSearchLiquidationDateTo;
	public String param;

	public Date maturityDateFrom;
	public Date maturityDateTo;
	public Date maxDate;
	public String fundCode;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

}
