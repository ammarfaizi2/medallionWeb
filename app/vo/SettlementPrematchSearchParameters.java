package vo;

import java.util.Date;

public class SettlementPrematchSearchParameters {
	public String accountNo;
	public String customerCode;
	public String securityType;
	public String securityId;
	public String thirdPartyCode;
	public Date settlementDateFrom;
	public Date settlementDateTo;
	public String transactionNo;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
