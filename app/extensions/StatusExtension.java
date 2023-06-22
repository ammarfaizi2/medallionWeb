package extensions;

import play.templates.JavaExtensions;

import com.simian.medallion.constant.LookupConstants;

public class StatusExtension extends JavaExtensions {

	public static String decodeStatus(String status) {
		String value = (status != null) ? status.trim() : "";
		if (LookupConstants.__RECORD_STATUS_NEW.equals(value))
			return "New";
		if (LookupConstants.__RECORD_STATUS_APPROVED.equals(value))
			return "";
		if (LookupConstants.__RECORD_STATUS_UPDATED.equals(value))
			return "Updated";
		if (LookupConstants.__RECORD_STATUS_REJECTED.equals(value))
			return "Reject";
		if (LookupConstants.__RECORD_STATUS_DELIVERED.equals(value))
			return "Deliver";
		return status;
	}

	public static String decodeDataStatus(String status) {
		String value = (status != null) ? status.trim() : "";
		if (LookupConstants.__RECORD_STATUS_NEW.equals(value))
			return "New";
		if (LookupConstants.__RECORD_STATUS_APPROVED.equals(value))
			return "Approved";
		if (LookupConstants.__RECORD_STATUS_VALID.equals(value))
			return "Valid";
		if (LookupConstants.__RECORD_STATUS_POSTED.equals(value))
			return "Posted";
		if (LookupConstants.__RECORD_STATUS_UPDATED.equals(value))
			return "Updated";
		if (LookupConstants.__RECORD_STATUS_REJECTED.equals(value))
			return "Reject";
		if (LookupConstants.__RECORD_STATUS_CANCELED.equals(value))
			return "Cancelled";
		if (LookupConstants.__RECORD_STATUS_OPEN.equals(value))
			return "Open";
		if (LookupConstants.__RECORD_STATUS_CLOSE_CANCEL_OPEN.equals(value))
			return "Cancel Open";
		if (LookupConstants.__RECORD_STATUS_SETTLED.equals(value))
			return "Settled";
		if (LookupConstants.TRX_STATUS_WAITING_PREMATCHING.equals(value))
			return "Waiting for prematching";
		if (LookupConstants.TRX_STATUS_WAITING_PREMATCH_APPROVE.equals(value))
			return "Waiting for prematch approval";
		if (LookupConstants.TRX_STATUS_WAITING_SETTLEMENT.equals(value))
			return "Waiting for settlement";
		if (LookupConstants.__RECORD_STATUS_ENTITLED.equals(value))
			return "Waiting for settlement";
		if (LookupConstants.TRX_STATUS_WAITING_ENTITLEMENT_APPROVE.equals(value))
			return "Waiting for entitlement";
		if (LookupConstants.TRX_STATUS_WAITING_SETTLEMENT_APPROVE.equals(value))
			return "Waiting for settlement approval";
		if (LookupConstants.__RECORD_STATUS_WAITING_SETTLEMENT_APPROVE.equals(value))
			return "Waiting for settlement approval";
		if (LookupConstants.__RECORD_STATUS_PAID.equals(value))
			return "Paid";
		return status;
	}

}
