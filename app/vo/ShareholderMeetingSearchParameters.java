package vo;

import java.util.Date;

public class ShareholderMeetingSearchParameters {
	public int MeetingNoOperator;
	public String meetingSearchNoOperator;
	public String searchThirdPartyKey;
	public Date meetingDateFrom;
	public Date meetingDateTo;
	public String param;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
