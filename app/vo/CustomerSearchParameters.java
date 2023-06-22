package vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

public class CustomerSearchParameters implements Serializable {
	public int customerNoOperator;
	public String customerSearchNo;
	public int customerNameOperator;
	public String customerSearchName;
	public Date customerSearchBirthDate;
	public int identificationNoOperator;
	public String customerSearchIdentificationNo;
	public int contactNoOperator;
	public String customerSearchContactName;
	public Date searchDateFrom;
	public Date searchDateTo;
	public String searchInvtAcct;
	public int externalNo;
	public String customerSearchExternalNo;

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
