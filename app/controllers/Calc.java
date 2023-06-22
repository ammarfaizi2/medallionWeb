package controllers;

public class Calc extends MedallionController {
	//private final static String TRANSACTION_BY_UNIT = "TRANSACTION_BY_UNIT";
	//private final static String TRANSACTION_BY_AMOUNT = "TRANSACTION_BY_AMOUNT";
	//private final static String INPUT_BY_GROSS = "INPUT_BY_GROSS";
	//private final static String INPUT_BY_NET = "INPUT_BY_NET";
	//private final static String TIER_COMPARISON_TYPE_PROGRESSIVE = "TIER_COMPARISON_TYPE-PROGRESSIVE";
	//private final static String TIER_COMPARISON_TYPE_FLAT = "TIER_COMPARISON_TYPE-FLAT";

	// Subscribe
	public static void calcSubFeeAmount(double feePct, String inputByVal, double amount) {
		Double val = CalcHelper.calcSubFeeAmount(feePct, inputByVal, amount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcSubDiscAmount(double discPct, double feeAmount) {
		Double val = CalcHelper.calcSubDiscAmount(discPct, feeAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcSubOtherAmount(double otherPct, String inputByVal, double amount) {
		Double val = CalcHelper.calcSubOtherAmount(otherPct, inputByVal, amount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcSubTotalFeeAmount(double feeAmount, double discAmount, double otherAmount) {
		Double val = CalcHelper.calcSubTotalFeeAmount(feeAmount, discAmount, otherAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcSubUnit(double price, double amountOrNetAmount) {
		Double val = CalcHelper.calcSubUnit(price, amountOrNetAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcSubNetAmount(String inputByVal, double amount, double totFeeAmount) {
		Double val = CalcHelper.calcSubNetAmount(inputByVal, amount, totFeeAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcSubAmount(String inputByVal, double netAmount, double totalFeeAmount) {
		Double val = CalcHelper.calcSubAmount(inputByVal, netAmount, totalFeeAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcSubTaxFeeAmount(double feeAmount, double discAmount, double taxFeePct) {
		Double val = CalcHelper.calcSubTaxFeeAmount(feeAmount, discAmount, taxFeePct);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	// Redem
	public static void calcRedFeeAmount(double feePct, String tradeBy, String netAmountTag, double netAmount) {
		Double val = CalcHelper.calcRedFeeAmount(feePct, tradeBy, netAmountTag, netAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedDiscAmount(String feeAmountTag, double discPct, double feeAmount) {
		Double val = CalcHelper.calcRedDiscAmount(feeAmountTag, discPct, feeAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedOtherAmount(String netAmountTag, double otherPct, double netAmount) {
		Double val = CalcHelper.calcRedOtherAmount(netAmountTag, otherPct, netAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedTaxFeeAmount(String feeAmountTag, String discAmountTag, double feeAmount, double discAmount, double taxFeePct) {
		Double val = CalcHelper.calcRedTaxFeeAmount(feeAmountTag, discAmountTag, feeAmount, discAmount, taxFeePct);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedTotalFeeAmount(double feeAmount, double discAmount, double otherAmount) {
		Double val = CalcHelper.calcRedTotalFeeAmount(feeAmount, discAmount, otherAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedUnit(String tradeBy, double price, double amountOrNetAmount) {
		Double val = CalcHelper.calcRedUnit(tradeBy, price, amountOrNetAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedNetAmount(String tradeBy, String priceTag, String unitTag, double price, double unit) {
		Double val = CalcHelper.calcRedNetAmount(tradeBy, priceTag, unitTag, price, unit);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedAmount(String feeAmountTag, double netAmount, double totalFeeAmount) {
		Double val = CalcHelper.calcRedAmount(feeAmountTag, netAmount, totalFeeAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedCapGainTaxAmount(double capTaxRate, double netAmount) {
		Double val = CalcHelper.calcRedCapGainTaxAmount(capTaxRate, netAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcRedPayment(String netAmountTag, double amount, double capTaxAmount) {
		Double val = CalcHelper.calcRedPayment(netAmountTag, amount, capTaxAmount);
		renderJSON(val == null ? "" : val.doubleValue());
	}

	public static void calcSubPayment() {
	}
}
