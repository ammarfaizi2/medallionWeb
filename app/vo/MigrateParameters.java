package vo;

import java.io.Serializable;

public class MigrateParameters implements Serializable {
	private static final long serialVersionUID = -9091775191895702389L;

	private String asOfDate;
	private String month;
	private String year;
	private String caType;
	private String security;
	private String recordingDate;
	private String referenceNo;

	public String getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}

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

	public String getCaType() {
		return caType;
	}

	public void setCaType(String caType) {
		this.caType = caType;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getRecordingDate() {
		return recordingDate;
	}

	public void setRecordingDate(String recordingDate) {
		this.recordingDate = recordingDate;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getFillMonth() {
		if (getMonth() == null)
			return null;
		if (getMonth().length() == 1)
			return "0" + getMonth();
		return getMonth();
	}
}
