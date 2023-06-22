package vo;

import java.util.Date;

public class AnnouncementSearchParameter {

	public int announcementNoOperator;
	public String announcementNo;
	public String actionCode;
	public String securityType;
	public String securityCode;
	public Date dateFrom;
	public Date dateTo;
	public String param;
	public String fieldDate;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
