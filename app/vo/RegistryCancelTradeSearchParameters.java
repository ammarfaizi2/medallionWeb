package vo;

import java.io.Serializable;
import java.util.Date;

import com.simian.medallion.model.RgProduct;

public class RegistryCancelTradeSearchParameters implements Serializable {
	private static final long serialVersionUID = -5848955570826592755L;

	public static final String DISPATCH_CONFIRM = "confirm";
	public static final String DISPATCH_VIEW = "view";
	public static final String DISPATCH_SAVE = "save";
	public static final String DISPATCH_APPROVE = "approve";

	private Date tradeDateFrom;
	private Date tradeDateTo;
	private RgProduct rgProduct;
	private String type;
	private Integer typeOrder;
	private Integer transactionNoOperator;
	private String redemRefKey;
	private String remark;
	private String dispatch;

	private String selected;

	private boolean query;

	public Date getTradeDateFrom() {
		return tradeDateFrom;
	}

	public void setTradeDateFrom(Date tradeDateFrom) {
		this.tradeDateFrom = tradeDateFrom;
	}

	public Date getTradeDateTo() {
		return tradeDateTo;
	}

	public void setTradeDateTo(Date tradeDateTo) {
		this.tradeDateTo = tradeDateTo;
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

	public Integer getTypeOrder() {
		return typeOrder;
	}

	public void setTypeOrder(Integer typeOrder) {
		this.typeOrder = typeOrder;
	}

	public Integer getTransactionNoOperator() {
		return transactionNoOperator;
	}

	public void setTransactionNoOperator(Integer transactionNoOperator) {
		this.transactionNoOperator = transactionNoOperator;
	}

	public String getRedemRefKey() {
		return redemRefKey;
	}

	public void setRedemRefKey(String redemRefKey) {
		this.redemRefKey = redemRefKey;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

}