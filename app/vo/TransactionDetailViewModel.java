package vo;

import java.util.List;

import com.simian.medallion.vo.SelectItem;

public class TransactionDetailViewModel {
	public List<SelectItem> classifications;
	public List<SelectItem> depositoryCodes;
	public String mode;
	public String transactionTypeCode;
	public boolean confirming;
	public boolean settlement;

}
