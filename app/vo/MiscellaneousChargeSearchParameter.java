package vo;

import java.util.Date;

public class MiscellaneousChargeSearchParameter {

	public Date miscellaneousFrom;
	public Date miscellaneousTo;
	public Long customerCodeId;
	public Long chargeKey;
	public int transactionNoOperator;
	public String transactionId;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

}
