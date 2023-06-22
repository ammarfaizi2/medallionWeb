package vo;

public class MonitoringAmlSearchParameters {
	public String monitoringAmlKey;
	public String monitoringAmlName;
	public String retailOf;
	public String status;
	public int monitoringNoOperator;
	public int monitoringNameOperator;
	public String monitoringSearchNoOperator;
	
	public String param;
	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
