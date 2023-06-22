package vo;

import java.util.List;

import com.simian.medallion.model.GnLookup;
import com.simian.medallion.vo.SelectItem;

public class CoaDetailViewModel {
	public String mode;
	public boolean confirming;
	public GnLookup coaParentKey;
	public List<SelectItem> coaDc;
}
