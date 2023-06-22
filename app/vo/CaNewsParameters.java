package vo;

import java.io.Serializable;
import java.util.Date;

public class CaNewsParameters implements Serializable {
	private static final long serialVersionUID = -5169062912568908596L;
	
	public Date fromDate;
	public Date toDate;
	public String securityType;
	public String securityKey;
	public boolean query;
	
	@Override
	public String toString() {
		return "CaNewsParameters [fromDate=" + fromDate + ", toDate=" + toDate + ", securityType=" + securityType
				+ ", securityKey=" + securityKey + ", query=" + query + "]";
	}
}