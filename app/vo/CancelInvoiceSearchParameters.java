package vo;

import java.util.Date;

public class CancelInvoiceSearchParameters {
	public Date invoiceDateFrom;
	public Date invoiceDateTo;
	public String customerCode;
	public int invoiceNoOperator;
	public String invoiceSearchNoOperator;
	public String invoiceStatus;

	public String param;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}