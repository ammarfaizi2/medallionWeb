package vo;

import java.io.Serializable;
import java.util.Date;

import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.helper.Helper;

public class DepositoryInterestPaymentProcessParameters implements Serializable {
	private static final long serialVersionUID = -4511410971026064523L;

	public static final String DISPATCH_LIST = "list";
	public static final String DISPATCH_PROCESS = "process";
	public static final String DISPATCH_CONFIRM = "confirm";

	private Date paymentDate;
	private Long paymentDateMili;
	private CfMaster cfMaster = new CfMaster();
	private boolean all;
	private ScTypeMaster scTypeMaster = new ScTypeMaster();
	private ScMaster scMaster = new ScMaster();
	private int depositNoSign;
	private String depositNo;
	private String dispatch;
	private boolean query;
	private String[] ids;
	private String customerNo;

	public boolean isReadOnly() {
		return DISPATCH_PROCESS.equals(getDispatch()) || DISPATCH_CONFIRM.equals(getDispatch());
	}

	public CfMaster getCfMaster() {
		return cfMaster;
	}

	public void setCfMaster(CfMaster cfMaster) {
		this.cfMaster = cfMaster;
	}

	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	public ScTypeMaster getScTypeMaster() {
		return scTypeMaster;
	}

	public void setScTypeMaster(ScTypeMaster scTypeMaster) {
		this.scTypeMaster = scTypeMaster;
	}

	public ScMaster getScMaster() {
		return scMaster;
	}

	public void setScMaster(ScMaster scMaster) {
		this.scMaster = scMaster;
	}

	public int getDepositNoSign() {
		return depositNoSign;
	}

	public void setDepositNoSign(int depositNoSign) {
		this.depositNoSign = depositNoSign;
	}

	public String getDepositNo() {
		return depositNo;
	}

	public void setDepositNo(String depositNo) {
		this.depositNo = depositNo;
	}

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Long getPaymentDateMili() {
		return paymentDateMili;
	}

	public void setPaymentDateMili(Long paymentDateMili) {
		this.paymentDateMili = paymentDateMili;
	}

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	@Override
	public String toString() {
		return "Dispatch:" + getDispatch() + ", PaymentDate:" + Helper.formatDMY(getPaymentDate()) + ", CfMaster:" + getCfMaster().getCustomerNo() + ", All:" + isAll() + ", ScTypeMaster:" + getScTypeMaster().getSecurityType() + ", ScMaster:" + getScMaster().getSecurityId() + ", DepositSign:" + getDepositNoSign() + ", DepositNo:" + getDepositNo();
	}

}