package vo;

import java.util.List;

import com.simian.medallion.vo.SelectItem;

public class AccountFormViewModel {
	public String mode;
	public boolean confirming;
	public List<SelectItem> accountTypes;
	public List<SelectItem> identificationTypes;

	public String accountType;
	public List<SelectItem> accrualBase;
	public List<SelectItem> yearBase;
}
