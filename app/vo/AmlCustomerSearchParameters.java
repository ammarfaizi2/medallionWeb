package vo;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AmlCustomerSearchParameters implements Serializable {
	public int amlKeyOperator;
	public String amlSearchKey;
	public int customerNameOperator;
	public String customerSearchName;

	public String param;
	public String activetab;

	private boolean query;

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
