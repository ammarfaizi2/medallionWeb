package vo;

public class JournalSearchParameters {

	public int idOperator;
	public String idSearchOperator;
	public int nameOperator;
	public String nameSearchOperator;

	public String param;
	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}
}
