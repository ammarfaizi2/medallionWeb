package helpers.comparator;

import java.util.Comparator;

public class StringArrayComparator implements Comparator<String[]> {
	@Override
	public int compare(String[] o1, String[] o2) {
		if (o1.length > 0 && o2.length > 0)
			return o1[0].compareTo(o2[0]);
		return 0;
	}
}
