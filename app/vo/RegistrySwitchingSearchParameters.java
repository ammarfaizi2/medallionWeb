package vo;

import java.io.Serializable;
import java.util.Date;

public class RegistrySwitchingSearchParameters implements Serializable {
	private static final long serialVersionUID = -5848955570826592755L;

	private String switchingKey;

	private Date switchDateFrom;
	private Date switchDateTo;
	private Integer switchNoOperator;
	private String switchingType;

	private String investorNo;
	private String investorKey;
	private String investorNoDesc;

	private boolean query;

	public String getSwitchingKey() {
		return switchingKey;
	}

	public void setSwitchingKey(String switchingKey) {
		this.switchingKey = switchingKey;
	}

	public Date getSwitchDateFrom() {
		return switchDateFrom;
	}

	public void setSwitchDateFrom(Date switchDateFrom) {
		this.switchDateFrom = switchDateFrom;
	}

	public Date getSwitchDateTo() {
		return switchDateTo;
	}

	public void setSwitchDateTo(Date switchDateTo) {
		this.switchDateTo = switchDateTo;
	}

	public Integer getSwitchNoOperator() {
		return switchNoOperator;
	}

	public void setSwitchNoOperator(Integer switchNoOperator) {
		this.switchNoOperator = switchNoOperator;
	}

	public String getSwitchingType() {
		return switchingType;
	}

	public void setSwitchingType(String switchingType) {
		this.switchingKey = switchingType;
	}

	public String getInvestorNo() {
		return investorNo;
	}

	public void setInvestorNo(String investorNo) {
		this.investorNo = investorNo;
	}

	public String getInvestorKey() {
		return investorKey;
	}

	public void setInvestorKey(String investorKey) {
		this.investorKey = investorKey;
	}

	public String getInvestorNoDesc() {
		return investorNoDesc;
	}

	public void setInvestorNoDesc(String investorNoDesc) {
		this.investorNoDesc = investorNoDesc;
	}

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

}