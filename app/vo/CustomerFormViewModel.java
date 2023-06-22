package vo;

import java.util.List;

import com.simian.medallion.vo.SelectItem;

public class CustomerFormViewModel {
	public String mode;
	public boolean confirming;
	public List<SelectItem> customerTypes;
	public List<SelectItem> identificationTypes;
	public List<SelectItem> investorTypes;
	public List<SelectItem> mailingOptions;
	public List<SelectItem> annualIncome;
	public List<SelectItem> maritalStatus;
}
