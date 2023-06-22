package vo;

public class CorporateActionAdjustmentSearchParam {

	public Long announcementKey;
	
	public String actionCode;
	public int announcementNoOperator;
	public String announcementNo;
	public String accountNo;
	public String securityType;
	public String securityCode;
	
	public String param;
	
	private boolean query; 
	public boolean isQuery() { return query; }
	public void setQuery(boolean query) { this.query = query; }
}
