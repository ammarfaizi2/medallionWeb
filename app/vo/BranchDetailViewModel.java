package vo;

import java.util.List;

import com.simian.medallion.model.GnLookup;
import com.simian.medallion.vo.SelectItem;

public class BranchDetailViewModel {
	public String mode;
	public boolean confirming;
	public String orgs;
	public GnLookup country;
	public GnLookup state;
	public GnLookup area;
	public List<SelectItem> name;
}
