package vo;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class UploadDownloadParameters implements Serializable {

	private Long batchNo;
	private boolean errorOnly;

	private Long totalSuccess;
	private Long totalFail;

	public Long getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(Long batchNo) {
		this.batchNo = batchNo;
	}

	public boolean isErrorOnly() {
		return errorOnly;
	}

	public void setErrorOnly(boolean errorOnly) {
		this.errorOnly = errorOnly;
	}

	public Long getTotalSuccess() {
		return totalSuccess;
	}

	public void setTotalSuccess(Long totalSuccess) {
		this.totalSuccess = totalSuccess;
	}

	public Long getTotalFail() {
		return totalFail;
	}

	public void setTotalFail(Long totalFail) {
		this.totalFail = totalFail;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
