package controllers;

import helpers.UIConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.simian.medallion.model.RgProdEod;

public class RegistryEndOfDayNew extends Registry {
	private static Logger log = Logger.getLogger(RegistryEndOfDayNew.class);

	@Check("transaction.registryEndOfDayNew")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		//String oRetval = "";

		Date lastDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getLastBusinessDate();
		Date currentDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		Date nextDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getNextBusinessDate();

		List<RgProdEod> dataRgProdEod = taService.listRgProdEod("");

		boolean readOnly = false;

		render("RegistryEndOfDayNew/list.html", dataRgProdEod, currentDate, nextDate, lastDate, readOnly, mode);
	}

	@Check("transaction.registryEndOfDayNew")
	public static void buttonRun(String productCode) {
		log.debug("buttonRun. productCode: " + productCode);

		String getEodDate = "";
		String getLastEod = "";
		String getStarts = "";
		String getEnds = "";
		String getMessage = "";
		String oRetval = "Successfull";

		String callPRgPortfolio = taService.pRgPortfolio(productCode, oRetval);

		oRetval = callPRgPortfolio;

		List<RgProdEod> dataRgProdEod = taService.listRgProdEod(productCode);

		for (RgProdEod prodEod : dataRgProdEod) {
			getEodDate = fmtMMDDYYYY(prodEod.getEodDate());
			getLastEod = fmtMMDDYYYY(prodEod.getLastEod());
			getStarts = fmtMMDDYYYYHHMMSS(prodEod.getStarts());
			getEnds = fmtMMDDYYYYHHMMSS(prodEod.getEnds());
			getMessage = prodEod.getMessage();
		}

		Map<String, Object> outputData = new HashMap<String, Object>();

		outputData.put("getEodDate", getEodDate);
		outputData.put("getLastEod", getLastEod);
		outputData.put("getStarts", getStarts);
		outputData.put("getEnds", getEnds);
		outputData.put("getMessage", getMessage);

		renderJSON(outputData);
	}

	@Check("transaction.registryEndOfDayNew")
	public static void buttonRunAll(String productCode) {
		log.debug("buttonRunAll. productCode: " + productCode);

		String oRetval = "Successfull";

		String callPRgPortfolioAll = taService.pRgPortfolioAll(productCode, oRetval);

		oRetval = callPRgPortfolioAll;
	}

	@Check("transaction.registryEndOfDayNew")
	public static void buttonChangeDate() {
		log.debug("buttonChangeDate. ");

		String resultString = taService.fChangeDateCount();

		Map<String, Object> outputData = new HashMap<String, Object>();

		outputData.put("countResult", resultString);

		renderJSON(outputData);

	}

	@Check("transaction.registryEndOfDayNew")
	public static void buttonRollbackProcess(String productCode, Date fundDate) {
		log.debug("buttonRollbackProcess. productCode: " + productCode + " fundDate: " + fundDate);

		String resultString = taService.fCheckDate(productCode, fundDate);

		Map<String, Object> outputData = new HashMap<String, Object>();

		outputData.put("countResult", resultString);

		renderJSON(outputData);

	}

	@Check("transaction.registryEndOfDayNew")
	public static void getProcessDate(Date paramDate) {
		log.debug("getProcessDate. paramDate: " + paramDate);

		Date currentDate = taService.getProcessDate(paramDate, "Current");
		Date LastDate = taService.getProcessDate(currentDate, "Last");
		// Date nextEod = taService.getProcessDate(currentDate, "Nest");

		Map<String, Object> outputData = new HashMap<String, Object>();

		String fundDateString = fmtMMDDYYYY(currentDate);
		String lastEodString = fmtMMDDYYYY(LastDate);

		outputData.put("fundDateString", fundDateString);
		outputData.put("lastEodString", lastEodString);

		renderJSON(outputData);
	}

	@Check("transaction.registryEndOfDayNew")
	public static void buttonProcessRollback(String productCode, Date fundDate) {
		log.debug("buttonProcessRollback. productCode: " + productCode + " fundDate: " + fundDate);

		Date currentDate = taService.getProcessDate(fundDate, "Current");
		Date lastDate = taService.getProcessDate(currentDate, "Last");
		Date nextDate = taService.getProcessDate(currentDate, "Next");
		taService.pRgProdEodRollback(productCode, lastDate, currentDate, nextDate);
	}

	@Check("transaction.registryEndOfDayNew")
	public static void pGnApplicationDateUpd() {
		log.debug("pGnApplicationDateUpd. ");

		Date currentDateOld = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();

		taService.pGnApplicationDateUpd(currentDateOld);

		generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getLastBusinessDate();
		Date currentDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		Date nextDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getNextBusinessDate();

		Map<String, Object> outputData = new HashMap<String, Object>();

		String fundDateString = fmtMMDDYYYY(currentDate);
		String nextDateString = fmtMMDDYYYY(nextDate);

		outputData.put("fundDateString", fundDateString);
		outputData.put("nextDateString", nextDateString);

		renderJSON(outputData);

	}

	private static String lastBusinessDate;
	private static String currentBusinessDate;
	private static String nextBusinessDate;

	@Check("transaction.registryEndOfDayNew")
	public static void buttonTest() {
		log.debug("buttonTest. ");

		Date currentDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();

		String getOutput = taService.pTest(currentDate);
		String separator = "|";

		devideString(getOutput, separator);

		Map<String, Object> outputData = new HashMap<String, Object>();

		outputData.put("result_00", getOutput);
		outputData.put("result_01", lastBusinessDate);
		outputData.put("result_02", currentBusinessDate);
		outputData.put("result_03", nextBusinessDate);

		renderJSON(outputData);

	}

	@Check("transaction.registryEndOfDayNew")
	public static String devideString(String inputString, String separator) {
		log.debug("devideString. inputString: " + inputString + " separator: " + separator);

		String stringSubs = inputString;
		String resultDevide = "";

		lastBusinessDate = "";
		currentBusinessDate = "";
		nextBusinessDate = "";

		for (int i = 1; i <= 3; i++) {
			int cursorPosition = stringSubs.indexOf(separator);

			if (cursorPosition > 0) {
				resultDevide = stringSubs.substring(0, cursorPosition);
			} else {
				resultDevide = stringSubs;
			}

			stringSubs = stringSubs.replace(resultDevide + separator, "");

			if (cursorPosition > 0) {
				if (i == 1) {
					lastBusinessDate = resultDevide;
				} else if (i == 2) {
					currentBusinessDate = resultDevide;
				} else if (i == 3) {
					nextBusinessDate = resultDevide;
				} else {
					break;
				}
			} else {
				if (i == 1) {
					lastBusinessDate = stringSubs;
				} else if (i == 2) {
					currentBusinessDate = stringSubs;
				} else if (i == 3) {
					nextBusinessDate = stringSubs;
				} else {
					break;
				}
				break;
			}

		}

		return "";
	}

	@Check("transaction.registryEndOfDayNew")
	public static void StringToCalender() throws ParseException {
		log.debug("StringToCalender. ");

		String stringDate = "11-June-12";

		DateFormat formatter;
		Date stringToDate;
		formatter = new SimpleDateFormat("dd-MMM-yy");
		stringToDate = formatter.parse(stringDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(stringToDate);

		Map<String, Object> outputData = new HashMap<String, Object>();

		outputData.put("result_00", stringToDate);

		renderJSON(outputData);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

}
