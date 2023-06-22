package controllers;

import java.util.Comparator;
import java.util.Map;

import com.simian.medallion.model.UpdProfileDetail;

public class ValueComparator implements Comparator {

	Map map;

	public ValueComparator(Map map) {
		this.map = map;
	}

	@Override
	public int compare(Object arg0, Object arg1) {
		UpdProfileDetail valueA = (UpdProfileDetail) map.get(arg0);
		UpdProfileDetail valueB = (UpdProfileDetail) map.get(arg1);

		return valueA.getNoSeq().compareTo(valueB.getNoSeq());
	}
}
