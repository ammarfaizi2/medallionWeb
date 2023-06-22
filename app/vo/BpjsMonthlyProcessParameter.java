package vo;

public class BpjsMonthlyProcessParameter {
	public static String FILTER_ALL = "ALL";

	private String month;
	private String year;
	private String filterAll1;
	private String filterAll2;
	private String filterAll3;
	private String filterSpecific1;
	private String filterSpecific2;
	private String filterSpecific3;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getFilterAll1() {
		return filterAll1;
	}

	public void setFilterAll1(String filterAll1) {
		this.filterAll1 = filterAll1;
	}

	public String getFilterAll2() {
		return filterAll2;
	}

	public void setFilterAll2(String filterAll2) {
		this.filterAll2 = filterAll2;
	}

	public String getFilterAll3() {
		return filterAll3;
	}

	public void setFilterAll3(String filterAll3) {
		this.filterAll3 = filterAll3;
	}

	public String getFilterSpecific1() {
		return filterSpecific1;
	}

	public void setFilterSpecific1(String filterSpecific1) {
		this.filterSpecific1 = filterSpecific1;
	}

	public String getFilterSpecific2() {
		return filterSpecific2;
	}

	public void setFilterSpecific2(String filterSpecific2) {
		this.filterSpecific2 = filterSpecific2;
	}

	public String getFilterSpecific3() {
		return filterSpecific3;
	}

	public void setFilterSpecific3(String filterSpecific3) {
		this.filterSpecific3 = filterSpecific3;
	}

	@Override
	public String toString() {
		return "filterAll1 = " + filterAll1 + ", filterAll2 = " + filterAll2 + ", filterAll3 = " + filterAll3 + ", filterSpecific1 = " + filterSpecific1 + ", filterSpecific2 = " + filterSpecific2 + ", filterSpecific3 = " + filterSpecific3 + ", month = " + month + ", year = " + year;
	}
}