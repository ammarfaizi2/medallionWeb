package helpers;

import java.util.ArrayList;
import java.util.List;

import com.simian.medallion.model.GnBranch;
import com.simian.medallion.model.GnLookup;

public class GridHelper {
	public static List<String[]> formatBranches(List<GnBranch> branches) {
		List<String[]> result = new ArrayList<String[]>();
		for (GnBranch branch : branches) {
			result.add(new String[] { branch.getBranchNo(), branch.getName() });
		}
		return result;
	}

	public static List<String[]> formatLookups(List<GnLookup> lookups) {
		List<String[]> result = new ArrayList<String[]>();
		for (GnLookup lookup : lookups) {
			result.add(new String[] { lookup.getLookupCode(), lookup.getLookupDescription() });
		}
		return result;
	}
}
