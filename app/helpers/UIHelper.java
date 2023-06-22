package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.UploadDownloadConstants;
import com.simian.medallion.vo.SelectItem;

public class UIHelper {

	public static String copyFile(File source, File dest) {
		String fileName = null;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(dest);
			fileName = dest.getName();
			IOUtils.copy(fis, fos);
			return fileName;
		} catch (Exception ex) {
			throw new RuntimeException("Error when moving file " + ex.getMessage());
		} finally {
			try {
				fos.flush();
				fis.close();
				fos.close();
				fis = null;
				fos = null;
			} catch (Exception ex) {
			}
		}
	}

	public static List<SelectItem> months = new ArrayList<SelectItem>(12);

	static {
		months.add(new SelectItem("1", "January"));
		months.add(new SelectItem("2", "February"));
		months.add(new SelectItem("3", "March"));
		months.add(new SelectItem("4", "April"));
		months.add(new SelectItem("5", "May"));
		months.add(new SelectItem("6", "June"));
		months.add(new SelectItem("7", "July"));
		months.add(new SelectItem("8", "August"));
		months.add(new SelectItem("9", "September"));
		months.add(new SelectItem("10", "October"));
		months.add(new SelectItem("11", "November"));
		months.add(new SelectItem("12", "December"));
	}

	public static List<SelectItem> years() {
		List<SelectItem> years = new ArrayList<SelectItem>();
		Calendar cal = Calendar.getInstance();
		for (int i = cal.get(Calendar.YEAR) - 10; i <= cal.get(Calendar.YEAR) + 10; i++) {
			years.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
		}
		return years;
	}

	public static List<SelectItem> stringOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem(0, "equals"));
		operators.add(new SelectItem(1, "like"));
		operators.add(new SelectItem(2, "starts with"));
		operators.add(new SelectItem(3, "ends with"));
		return operators;
	}

	public static List<SelectItem> operatorNominal() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "> 0"));
		operators.add(new SelectItem("false", ">= 0"));
		return operators;
	}

	public static List<SelectItem> outputTypes(boolean includeDbf) {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_CSV, UploadDownloadConstants.EXTENSION_CSV));
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_TXT, UploadDownloadConstants.EXTENSION_TXT));
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_XLS, UploadDownloadConstants.EXTENSION_XLS));
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_XLSX, UploadDownloadConstants.EXTENSION_XLSX));
		if (includeDbf)
			operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_DBF, UploadDownloadConstants.EXTENSION_DBF));
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_XML, UploadDownloadConstants.EXTENSION_XML));
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_OTC, UploadDownloadConstants.EXTENSION_OTC));
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_OTB, UploadDownloadConstants.EXTENSION_OTB));
		//operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_WT, UploadDownloadConstants.EXTENSION_WT));
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_SDI, UploadDownloadConstants.EXTENSION_SDI));
		operators.add(new SelectItem(UploadDownloadConstants.EXTENSION_CW, UploadDownloadConstants.EXTENSION_CW));
		return operators;
	}

	public static List<SelectItem> separatorOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem(",", ","));
		operators.add(new SelectItem(";", ";"));
		operators.add(new SelectItem("|", "|"));
		return operators;
	}

	public static String withOperator(String value, int operator) {
		switch (operator) {
		case 1:
			return "%" + value + "%";
		case 2:
			return value + "%";
		case 3:
			return "%" + value;
		default:
			return value;
		}
	}

	public static List<SelectItem> yesNoOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "Yes"));
		operators.add(new SelectItem("false", "No"));
		return operators;
	}

	public static List<SelectItem> amlInterfaceIndividuOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem(SystemParamConstants.PARAM_AML_TIPEINDIVIDU, "Customer"));
		operators.add(new SelectItem(SystemParamConstants.PARAM_AML_TIPEINDIVIDU_BO, "Beneficial Ownner"));
		return operators;
	}

	public static List<SelectItem> redeemOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "Redeem All"));
		operators.add(new SelectItem("false", "Partial"));
		return operators;
	}

	public static List<SelectItem> journalTypeOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("MANUAL", "Manual"));
		operators.add(new SelectItem("SYSTEM", "System"));
		return operators;
	}

	public static List<SelectItem> scheduleTypeOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("CALENDAR", "Calendar"));
		operators.add(new SelectItem("SPECIFIC", "Specific"));
		return operators;
	}

	public static List<SelectItem> autoManualOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "Auto"));
		operators.add(new SelectItem("false", "Manual"));
		return operators;
	}

	public static List<SelectItem> signOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("<=", "<="));
		operators.add(new SelectItem("<", "<"));
		operators.add(new SelectItem("=", "="));
		operators.add(new SelectItem(">", ">"));
		operators.add(new SelectItem(">=", ">="));
		operators.add(new SelectItem("<>", "<>"));
		return operators;
	}

	public static List<SelectItem> limitRules() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("ASSUM", "TOTAL"));
		operators.add(new SelectItem("SEPARATE", "Separate"));
		return operators;
	}

	public static List<SelectItem> dividenTypes() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("By Cash", "By Cash"));
		operators.add(new SelectItem("By Reinvestment", "By Reinvestment"));
		operators.add(new SelectItem("By Redemption", "By Redemption"));
		operators.add(new SelectItem("Investor Option", "Investor Option"));
		return operators;
	}

	public static List<SelectItem> inputByOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("AMOUNT", "Amount"));
		operators.add(new SelectItem("AMOUNTPERUNIT", "Amount/Unit"));
		return operators;
	}

	public static List<SelectItem> ratioPerUnitByOptions() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("TOTAL_AMOUNT", ""));
		operators.add(new SelectItem("AMOUNT_PER_UNIT", "Amount"));
		operators.add(new SelectItem("UNIT_PER_UNIT", "Unit"));
		return operators;
	}

	public static List<SelectItem> roundingOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("ROUND", "Round"));
		operators.add(new SelectItem("ROUNDUP", "Round Up"));
		operators.add(new SelectItem("ROUNDDOWN", "Round Down"));
		// operators.add(new SelectItem("TRUNC", "Trunc"));
		return operators;
	}

	public static List<SelectItem> amountUnitOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("AMOUNT", "Amount"));
		operators.add(new SelectItem("UNIT", "Unit"));
		return operators;
	}

	public static List<SelectItem> activeNonActiveOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "Active"));
		operators.add(new SelectItem("false", "In-Active"));
		return operators;
	}

	public static List<SelectItem> sourceTargetOperator() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "Source"));
		operators.add(new SelectItem("false", "Target"));
		return operators;
	}

	public static List<SelectItem> actionTemplateTypeOperator() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("BUY", "BUY"));
		operators.add(new SelectItem("SELL", "SELL"));
		operators.add(new SelectItem("DEPOSIT", "DEPOSIT"));
		operators.add(new SelectItem("WITHDRAWAL", "WITHDRAWAL"));
		return operators;
	}

	public static List<SelectItem> udfOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("1", "Code1"));
		operators.add(new SelectItem("2", "Code2"));
		operators.add(new SelectItem("3", "Code3"));
		return operators;
	}

	public static List<SelectItem> amountPercentOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("A", "AMOUNT"));
		operators.add(new SelectItem("P", "PERCENT"));
		return operators;
	}

	public static List<SelectItem> debitCreditOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("D", "Debit"));
		operators.add(new SelectItem("C", "Credit"));
		return operators;
	}

	public static List<SelectItem> bilyetNoOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "All"));
		operators.add(new SelectItem("false", "Individu"));
		return operators;
	}

	public static List<SelectItem> mainSecurityOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "Target"));
		operators.add(new SelectItem("false", "Source"));
		return operators;
	}

	public static List<SelectItem> securityPriceMethodOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("marketPrice", "ALL SECURITY BY MARKET PRICE"));
		operators.add(new SelectItem("specificPrice", "All SECURITY BY SPECIFIC PRICE"));
		operators.add(new SelectItem("setupSecType", "SETUP SPECIFIC PRICE BY SECURITY TYPE"));
		return operators;
	}

	public static List<SelectItem> periodOfInterestOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("D", "Days"));
		operators.add(new SelectItem("M", "Months"));
		return operators;
	}

	public static List<SelectItem> sendOptionOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("false", "Postal"));
		operators.add(new SelectItem("true", "Email"));
		return operators;
	}

	public static List<SelectItem> viewAsOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "All"));
		operators.add(new SelectItem("false", "Active"));
		return operators;
	}

	public static List<SelectItem> viewAction() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("1", "CREATE"));
		operators.add(new SelectItem("2", "UPDATE"));
		return operators;
	}

	public static List<SelectItem> debetType() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "Cash Account"));
		operators.add(new SelectItem("false", "GL Account"));
		return operators;
	}

	public static List<SelectItem> redeemSwitchOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "All"));
		operators.add(new SelectItem("false", "Partial"));
		return operators;
	}

	public static List<SelectItem> subscriptionSwitchOperators() {
		List<SelectItem> operators = new ArrayList<SelectItem>();
		operators.add(new SelectItem("true", "New"));
		operators.add(new SelectItem("false", "Top-up"));
		return operators;
	}

	public static List<SelectItem> ctpReports() {
		List<SelectItem> ctps = new ArrayList<SelectItem>();
		ctps.add(new SelectItem("INPUT", "Input"));
		ctps.add(new SelectItem("CONFIRM", "Confirm"));
		ctps.add(new SelectItem("ALL", "ALL"));
		return ctps;
	}

}