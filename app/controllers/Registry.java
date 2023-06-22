package controllers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.FastDateFormat;

public class Registry extends MedallionController {
	// UTILS

	public static final String ROUND = "ROUND";
	public static final String ROUNDUP = "ROUNDUP";
	public static final String ROUNDDOWN = "ROUNDDOWN";
	public static final String TRUNC = "TRUNC";

	public static Date parseDate(String date, String pattern) throws ParseException {
		return new SimpleDateFormat(pattern).parse(date);
	}

	public static Date parseYYYYMMDD(String string) throws ParseException {
		return parseDate(string, "yyyyMMdd");
	}

	public static String fmtYYYYMMDD(Date date) {
		if (date == null)
			return "";
		return FastDateFormat.getInstance("yyyyMMdd").format(date);
	}

	public static String fmtDDMMMYYYYY(Date date) {
		if (date == null)
			return "";
		return FastDateFormat.getInstance("dd-MMM-yyyy").format(date);
	}

	public static String fmtYYYY(Date date) {
		if (date == null)
			return "";
		return FastDateFormat.getInstance("yyyy").format(date);
	}

	public static Date parseMMDDYYYY(String string) throws ParseException {
		return parseDate(string, "MM/dd/yyyy");
	}

	public static String fmtMMDDYYYY(Date date) {
		if (date == null)
			return "";
		// return FastDateFormat.getInstance("MM/dd/yyyy").format(date);
		return FastDateFormat.getInstance("dd/MM/yyyy").format(date);
	}

	public static Date parseMMDDYYYYHHMMSS(String string) throws ParseException {
		return parseDate(string, "MM/dd/yyyy hh:mm:ss");
	}

	public static String fmtMMDDYYYYHHMMSS(Date date) {
		if (date == null)
			return "";
		return FastDateFormat.getInstance("MM/dd/yyyy hh:mm:ss").format(date);
	}

	public static String fmtDDMMYYYYHHMMSS(Date date) {
		if (date == null)
			return "";
		return FastDateFormat.getInstance("dd/MM/yyyy hh:mm:ss").format(date);
	}

	public static BigDecimal round(String type, double value, int digit) {
		double factor = Math.pow(10, digit);
		double interestedInZeroDPs = value * factor;

		if (ROUND.equals(type)) {
			value = Math.round(interestedInZeroDPs) / factor;
			// if (digit == 0) value = Math.round(interestedInZeroDPs) / factor;
			// if (digit > 0) value = Math.round(interestedInZeroDPs) / factor;
			// if (digit < 0) value = Math.round(interestedInZeroDPs) / factor;
		}

		if (ROUNDUP.equals(type)) {
			value = Math.ceil(interestedInZeroDPs) / factor;
			// if (digit == 0) value = Math.ceil(interestedInZeroDPs) / factor;
			// if (digit > 0) value = Math.ceil(interestedInZeroDPs) / factor;
			// if (digit < 0) value = Math.ceil(interestedInZeroDPs) / factor;
		}

		if (ROUNDDOWN.equals(type)) {
			value = Math.floor(interestedInZeroDPs) / factor;
			// if (digit == 0) value = Math.floor(interestedInZeroDPs) / factor;
			// if (digit > 0) value = Math.floor(interestedInZeroDPs) / factor;
			// if (digit < 0) value = Math.floor(interestedInZeroDPs) / factor;
		}

		return new BigDecimal(value);
	}

	public static Date addDate(Date fromDate, int addDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		cal.add(Calendar.DATE, addDay);
		return cal.getTime();
	}

	public static int fmtDOY(Date date) {
		if (date == null)
			return -1;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_YEAR);
	}

}
