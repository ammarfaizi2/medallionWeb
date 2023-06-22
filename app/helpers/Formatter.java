package helpers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.i18n.Messages;

import com.simian.medallion.exception.MedallionException;

public class Formatter {
	private static String DEFAULT_DATE_FORMAT = "MM/dd/yyyy";

	public static String dateFormat(Date source, String format) {
		if (source == null)
			return "";
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(source);
	}

	public static String dateFormat(Date source) {
		return dateFormat(source, DEFAULT_DATE_FORMAT);
	}

	public static Map<String, Object> resultSuccess() {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", "success");
		return result;
	}

	public static Map<String, Object> resultSuccess(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", "success");
		result.put("message", message);
		return result;
	}

	public static Map<String, Object> resultError(Exception e) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", "error");
		result.put("error", (e == null || e.getMessage() == null) ? "java.lang.NullpointerException" : e.getMessage());
		result.put("message", (e == null || e.getMessage() == null) ? "java.lang.NullpointerException" : e.getMessage());
		return result;
	}

	public static Map<String, Object> resultError(String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", "error");
		result.put("error", message);
		result.put("message", message);
		return result;
	}

	public static Map<String, Object> resultError(MedallionException e) {
		Map<String, Object> result = new HashMap<String, Object>();

		if (e == null) {
			result.put("status", "error");
			result.put("error", "java.lang.NullpointerException");
			result.put("message", "java.lang.NullpointerException");
		} else {
			List<String> params = new ArrayList<String>();
			if (e.getParams() != null) {
				for (Object param : e.getParams()) {
					params.add(Messages.get(param));
				}
			}
			result.put("status", "error");
			result.put("code", e.getErrorCode());
			result.put("error", Messages.get("error." + e.getErrorCode(), params.toArray()));
			result.put("message", Messages.get("error." + e.getErrorCode(), params.toArray()));
		}
		return result;
	}
}
