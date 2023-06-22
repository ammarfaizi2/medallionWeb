package vo;

import java.util.Date;

public class NavSearchParameters {
	public Date navSearchNavDateFrom;
	public Date navSearchNavDateTo;
	public String navSearchProductCode;

	public String param;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

}
