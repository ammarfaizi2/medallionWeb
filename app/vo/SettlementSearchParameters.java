package vo;

import java.io.Serializable;
import java.util.Date;

import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.ScTypeMaster;

public class SettlementSearchParameters implements Serializable {
	private static final long serialVersionUID = -5848955570826592755L;

	public static final String DISPATCH_CONFIRM = "confirm";
	public static final String DISPATCH_VIEW = "view";
	public static final String DISPATCH_SETTLE = "settle";

	private Long batchKey;
	private Date settlementDate;
	private ScTypeMaster securityType;
	private ScMaster security;
	private String depository;
	private boolean sendToDipository;
	private Long settlementDateMili;
	private Long[] transactionKey;
	private String dispatch;
	private String depositoryId;
	private Long issuerKey;
	private boolean ctpRequired;
	private String marketOfRiskId;
	private boolean query;

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public boolean isReadOnly() {
		return DISPATCH_SETTLE.equals(dispatch) || DISPATCH_CONFIRM.equals(dispatch);
	}

	public Long getBatchKey() {
		return batchKey;
	}

	public void setBatchKey(Long batchKey) {
		this.batchKey = batchKey;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public ScTypeMaster getSecurityType() {
		return securityType;
	}

	public void setSecurityType(ScTypeMaster securityType) {
		this.securityType = securityType;
	}

	public ScMaster getSecurity() {
		return security;
	}

	public void setSecurity(ScMaster security) {
		this.security = security;
	}

	public String getDepository() {
		return depository;
	}

	public void setDepository(String depository) {
		this.depository = depository;
	}

	public boolean isSendToDipository() {
		return sendToDipository;
	}

	public void setSendToDipository(boolean sendToDipository) {
		this.sendToDipository = sendToDipository;
	}

	public Long getSettlementDateMili() {
		return settlementDateMili;
	}

	public void setSettlementDateMili(Long settlementDateMili) {
		this.settlementDateMili = settlementDateMili;
	}

	public Long[] getTransactionKey() {
		return transactionKey;
	}

	public void setTransactionKey(Long[] transactionKey) {
		this.transactionKey = transactionKey;
	}

	public String getDepositoryId() {
		return depositoryId;
	}

	public void setDepositoryId(String depositoryId) {
		this.depositoryId = depositoryId;
	}

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

	public Long getIssuerKey() {
		return issuerKey;
	}

	public void setIssuerKey(Long issuerKey) {
		this.issuerKey = issuerKey;
	}

	public boolean isCtpRequired() {
		return ctpRequired;
	}

	public void setCtpRequired(boolean ctpRequired) {
		this.ctpRequired = ctpRequired;
	}

	public String getMarketOfRiskId() {
		return marketOfRiskId;
	}

	public void setMarketOfRiskId(String marketOfRiskId) {
		this.marketOfRiskId = marketOfRiskId;
	}
}