package vo;

import java.io.Serializable;
import java.util.Date;

import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.helper.Helper;

public class DepositoryMaturityProcessParameters implements Serializable {
	private static final long serialVersionUID = -478866895520849312L;

	public static final String DISPATCH_LIST = "list";
	public static final String DISPATCH_PROCESS = "process";
	public static final String DISPATCH_CONFIRM = "confirm";

	private Date maturityDate;
	private Long maturityDateMili;
	private CfMaster cfMaster = new CfMaster();
	private boolean all;
	private ScTypeMaster scTypeMaster = new ScTypeMaster();
	private ScMaster scMaster = new ScMaster();
	private int depositNoSign;
	private String depositNo;
	private String dispatch;
	private boolean query;
	private Long[] depositoKey;

	private String sessionTag;
	private String processMark;

	public boolean isReadOnly() {
		return DISPATCH_PROCESS.equals(getDispatch()) || DISPATCH_CONFIRM.equals(getDispatch());
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
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

	public Long[] getDepositoKey() {
		return depositoKey;
	}

	public void setDepositoKey(Long[] depositoKey) {
		this.depositoKey = depositoKey;
	}

	public Long getMaturityDateMili() {
		return maturityDateMili;
	}

	public void setMaturityDateMili(Long maturityDateMili) {
		this.maturityDateMili = maturityDateMili;
	}

	public String getSessionTag() {
		return sessionTag;
	}

	public void setSessionTag(String sessionTag) {
		this.sessionTag = sessionTag;
	}

	public String getProcessMark() {
		return processMark;
	}

	public void setProcessMark(String processMark) {
		this.processMark = processMark;
	}

	@Override
	public String toString() {
		return "Dispatch:" + getDispatch() + ", MaturityDate:" + Helper.formatDMY(getMaturityDate()) + ", CfMaster:" + getCfMaster().getCustomerNo() + ", All:" + isAll() + ", ScTypeMaster:" + getScTypeMaster().getSecurityType() + ", ScMaster:" + getScMaster().getSecurityId() + ", DepositSign:" + getDepositNoSign() + ", DepositNo:" + getDepositNo();
	}

}