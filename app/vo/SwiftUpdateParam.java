package vo;

import java.io.Serializable;
import java.util.Date;

public class SwiftUpdateParam implements Serializable{
	private static final long serialVersionUID = 3925529057341844845L;
	
	private String swiftNo;
	private String messageMode;
	private Date fromDate;
	private Date toDate;
	private String sender;
	private String senderDesc; 
	private String receiver;
	private String receiverDesc;
	private String securityCode;
	private String securityCodeDesc;
	private String securityType;
	private String securityTypeDesc;
	private String isinCode;
	private String isinCodeDesc;
	private String status;
	private boolean query; 
	private Date settleFrom;
	private Date settleTo;
	
	public boolean isQuery() { return query; }
	public void setQuery(boolean query) { this.query = query; }
	
	public String getMessageMode() { return messageMode; }
	public void setMessageMode(String messageMode) { this.messageMode = messageMode; }
	
	public Date getFromDate() { return fromDate; }
	public void setFromDate(Date fromDate) { this.fromDate = fromDate; }
	
	public Date getToDate() { return toDate; }
	public void setToDate(Date toDate) { this.toDate = toDate; }
	
	public String getSender() { return sender; }
	public void setSender(String sender) { this.sender = sender; }
	
	public String getReceiver() { return receiver; }
	public void setReceiver(String receiver) { this.receiver = receiver; }
	
	public String getSecurityType() { return securityType; }
	public void setSecurityType(String securityType) { this.securityType = securityType; }
	
	public String getSecurityCode() { return securityCode; }
	public void setSecurityCode(String securityCode) { this.securityCode = securityCode; }
	
	public String getIsinCode() { return isinCode; }
	public void setIsinCode(String isinCode) { this.isinCode = isinCode; }
	
	public String getSwiftNo() { return swiftNo; }
	public void setSwiftNo(String swiftNo) { this.swiftNo = swiftNo; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	
	public String getSenderDesc() { return senderDesc; }
	public void setSenderDesc(String senderDesc) { this.senderDesc = senderDesc; }
	
	public String getReceiverDesc() { return receiverDesc; }
	public void setReceiverDesc(String receiverDesc) { this.receiverDesc = receiverDesc; }
	
	public String getSecurityTypeDesc() { return securityTypeDesc; }
	public void setSecurityTypeDesc(String securityTypeDesc) { this.securityTypeDesc = securityTypeDesc; }
	
	public String getSecurityCodeDesc() { return securityCodeDesc; }
	public void setSecurityCodeDesc(String securityCodeDesc) { this.securityCodeDesc = securityCodeDesc; }
	
	public String getIsinCodeDesc() { return isinCodeDesc; }
	public void setIsinCodeDesc(String isinCodeDesc) { this.isinCodeDesc = isinCodeDesc; }
	
	public Date getSettleFrom() {return settleFrom;}
	public void setSettleFrom(Date settleFrom) {this.settleFrom = settleFrom;}
	
	public Date getSettleTo() {return settleTo;}
	public void setSettleTo(Date settleTo) {this.settleTo = settleTo;}
	
	
}
