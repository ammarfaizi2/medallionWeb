package vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgTrade;

public class RegistryPaymentSearchParameters implements Serializable {
	private static final long serialVersionUID = -5848955570826592755L;

	public static final String DISPATCH_CONFIRM = "confirm";
	public static final String DISPATCH_VIEW = "view";
	public static final String DISPATCH_SAVE = "save";
	public static final String DISPATCH_APPROVE = "approve";

	private Long paymentKey;
	private Date paymentDate;
	private RgProduct rgProduct;
	private String type;
	private String filterBy;
	private Long filterByNo;
	private String remarks;
	private String dispatch;

	private BigDecimal amount;
	private String selected;
	private String selectedNominal;
	private String selectedInvestorBankAccountKey;
	private String selectedInvestorAccountNo;
	private String selectedInvestorThirdPartyCode;
	private String selectedProductBankAccountKey;
	private String selectedProductAccountNo;
	private String selectedProductThirdPartyCode;
	private String selectedbankCodeChange;
	private String selectedbankCodeNameChange;
	private String selectedProductThirdPartyCodeChange;
	private String selectedInvestorThirdPartyCodeChange;
	private BigDecimal totalPaidAmount;
	private boolean query;
	private List<RgTrade> trades = new ArrayList<RgTrade>();
	private GnLookup transferMethod;

	public Long getPaymentKey() {
		return paymentKey;
	}

	public void setPaymentKey(Long paymentKey) {
		this.paymentKey = paymentKey;
	}

	public String getDispatch() {
		return dispatch;
	}

	public void setDispatch(String dispatch) {
		this.dispatch = dispatch;
	}

	public boolean isReadOnly() {
		return DISPATCH_SAVE.equals(dispatch) || DISPATCH_CONFIRM.equals(dispatch) || DISPATCH_APPROVE.equals(dispatch);
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public RgProduct getRgProduct() {
		return rgProduct;
	}

	public void setRgProduct(RgProduct rgProduct) {
		this.rgProduct = rgProduct;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}

	public Long getFilterByNo() {
		return filterByNo;
	}

	public void setFilterByNo(Long filterByNo) {
		this.filterByNo = filterByNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public boolean isQuery() {
		return query;
	}

	public void setQuery(boolean query) {
		this.query = query;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getSelectedInvestorBankAccountKey() {
		return selectedInvestorBankAccountKey;
	}

	public void setSelectedInvestorBankAccountKey(String selectedInvestorBankAccountKey) {
		this.selectedInvestorBankAccountKey = selectedInvestorBankAccountKey;
	}

	public String getSelectedInvestorAccountNo() {
		return selectedInvestorAccountNo;
	}

	public void setSelectedInvestorAccountNo(String selectedInvestorAccountNo) {
		this.selectedInvestorAccountNo = selectedInvestorAccountNo;
	}

	public String getSelectedInvestorThirdPartyCode() {
		return selectedInvestorThirdPartyCode;
	}

	public void setSelectedInvestorThirdPartyCode(String selectedInvestorThirdPartyCode) {
		this.selectedInvestorThirdPartyCode = selectedInvestorThirdPartyCode;
	}

	public String getSelectedProductBankAccountKey() {
		return selectedProductBankAccountKey;
	}

	public void setSelectedProductBankAccountKey(String selectedProductBankAccountKey) {
		this.selectedProductBankAccountKey = selectedProductBankAccountKey;
	}

	public String getSelectedProductAccountNo() {
		return selectedProductAccountNo;
	}

	public void setSelectedProductAccountNo(String selectedProductAccountNo) {
		this.selectedProductAccountNo = selectedProductAccountNo;
	}

	public String getSelectedProductThirdPartyCode() {
		return selectedProductThirdPartyCode;
	}

	public void setSelectedProductThirdPartyCode(String selectedProductThirdPartyCode) {
		this.selectedProductThirdPartyCode = selectedProductThirdPartyCode;
	}

	public String getSelectedNominal() {
		return selectedNominal;
	}

	public void setSelectedNominal(String selectedNominal) {
		this.selectedNominal = selectedNominal;
	}

	public BigDecimal getTotalPaidAmount() {
		return totalPaidAmount;
	}

	public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
		this.totalPaidAmount = totalPaidAmount;
	}

	public List<RgTrade> getTrades() {
		return trades;
	}

	public void setTrades(List<RgTrade> trades) {
		this.trades = trades;
	}

	public String getSelectedProductThirdPartyCodeChange() {
		return selectedProductThirdPartyCodeChange;
	}

	public void setSelectedProductThirdPartyCodeChange(String selectedProductThirdPartyCodeChange) {
		this.selectedProductThirdPartyCodeChange = selectedProductThirdPartyCodeChange;
	}

	public String getSelectedInvestorThirdPartyCodeChange() {
		return selectedInvestorThirdPartyCodeChange;
	}

	public void setSelectedInvestorThirdPartyCodeChange(String selectedInvestorThirdPartyCodeChange) {
		this.selectedInvestorThirdPartyCodeChange = selectedInvestorThirdPartyCodeChange;
	}

	public String getSelectedbankCodeChange() {
		return selectedbankCodeChange;
	}

	public void setSelectedbankCodeChange(String selectedbankCodeChange) {
		this.selectedbankCodeChange = selectedbankCodeChange;
	}

	public String getSelectedbankCodeNameChange() {
		return selectedbankCodeNameChange;
	}

	public void setSelectedbankCodeNameChange(String selectedbankCodeNameChange) {
		this.selectedbankCodeNameChange = selectedbankCodeNameChange;
	}

	public GnLookup getTransferMethod() {
		return transferMethod;
	}

	public void setTransferMethod(GnLookup transferMethod) {
		this.transferMethod = transferMethod;
	}
}