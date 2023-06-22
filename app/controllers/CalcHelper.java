package controllers;

import com.simian.medallion.model.helper.Helper;

public class CalcHelper {
	private final static String TRANSACTION_BY_UNIT = "TRANSACTION_BY_UNIT";
	private final static String TRANSACTION_BY_AMOUNT = "TRANSACTION_BY_AMOUNT";
	private final static String INPUT_BY_GROSS = "INPUT_BY_GROSS";
	private final static String INPUT_BY_NET = "INPUT_BY_NET";
	private final static String TIER_COMPARISON_TYPE_PROGRESSIVE = "TIER_COMPARISON_TYPE-PROGRESSIVE";
	private final static String TIER_COMPARISON_TYPE_FLAT = "TIER_COMPARISON_TYPE-FLAT";

	public static Double calcSubFeeAmount(double feePct, String inputByVal, double amount) {
		return Helper.calcSubFeeAmount(feePct, inputByVal, amount);
	}

	public static Double calcSubDiscAmount(double discPct, double feeAmount) {
		if (discPct != 0D && feeAmount != 0D) {
			double discAmt = (discPct / 100) * feeAmount * -1;
			return new Double(discAmt);
		} else if (discPct == 0) {
			return new Double(0);
		} else if (feeAmount == 0) {
			return null;
		}
		return null;
	}

	public static Double calcSubOtherAmount(double otherPct, String inputByVal, double amount) {
		if (otherPct == 0D) {
			return new Double(0);
		} else {
			double otherAmount = 0;
			if (INPUT_BY_GROSS.equals(inputByVal))
				otherAmount = (otherPct / 100) / ((otherPct / 100) + 1) * amount;
			if (INPUT_BY_NET.equals(inputByVal))
				otherAmount = (otherPct / 100) * amount;
			return new Double(otherAmount);
		}
	}

	public static Double calcSubTotalFeeAmount(double feeAmount, double discAmount, double otherAmount) {
		double totFeeAmount = (feeAmount + discAmount + otherAmount);
		return new Double(totFeeAmount);
	}

	public static Double calcSubUnit(double price, double amountOrNetAmount) {
		return Helper.calcSubUnit(price, amountOrNetAmount);
	}

	public static Double calcSubNetAmount(String inputByVal, double amount, double totFeeAmount) {
		return Helper.calcSubNetAmount(inputByVal, amount, totFeeAmount);
	}

	public static Double calcSubAmount(String inputByVal, double netAmount, double totalFeeAmount) {
		return Helper.calcSubAmount(inputByVal, netAmount, totalFeeAmount);
	}

	public static Double calcSubTaxFeeAmount(double feeAmount, double discAmount, double taxFeePct) {
		double taxFeeAmount = ((feeAmount + discAmount) / ((taxFeePct / 100) + 1)) * ((taxFeePct / 100));
		return new Double(taxFeeAmount);
	}

	public static Double calcRedFeeAmount(double feePct, String tradeBy, String netAmountTag, double netAmount) {
		return Helper.calcRedFeeAmount(feePct, tradeBy, netAmountTag, netAmount);
	}

	public static Double calcRedDiscAmount(String feeAmountTag, double discPct, double feeAmount) {
		if (feeAmountTag.isEmpty()) {
			return null;
		} else {
			if (discPct != 0D && feeAmount != 0D) {
				double discAmt = (discPct / 100) * feeAmount * -1;
				return new Double(discAmt);
			} else if (discPct == 0D) {
				return new Double(0);
			} else if (feeAmount == 0D) {
				return null;
			}
		}
		return null;
	}

	public static Double calcRedOtherAmount(String netAmountTag, double otherPct, double netAmount) {
		if (netAmountTag.isEmpty()) {
			return new Double(0);
		} else {
			if (otherPct == 0D) {
				return new Double(0);
			} else {
				double otherAmout = (otherPct / 100) * netAmount;
				return new Double(otherAmout);
			}
		}
	}

	public static Double calcRedTaxFeeAmount(String feeAmountTag, String discAmountTag, double feeAmount, double discAmount, double taxFeePct) {
		if (feeAmountTag.isEmpty() || discAmountTag.isEmpty()) {
			return null;
		} else {
			double taxFeeAmount = ((feeAmount + discAmount) / ((taxFeePct / 100) + 1)) * ((taxFeePct / 100));
			return new Double(taxFeeAmount);
		}
	}

	public static Double calcRedTotalFeeAmount(double feeAmount, double discAmount, double otherAmount) {
		return Helper.calcRedTotalFeeAmount(feeAmount, discAmount, otherAmount);
	}

	public static Double calcRedUnit(String tradeBy, double price, double amountOrNetAmount) {
		return Helper.calcRedUnit(tradeBy, price, amountOrNetAmount);
	}

	public static Double calcRedNetAmount(String tradeBy, String priceTag, String unitTag, double price, double unit) {
		return Helper.calcRedNetAmount(tradeBy, priceTag, unitTag, price, unit);
	}

	public static Double calcRedAmount(String feeAmountTag, double netAmount, double totalFeeAmount) {
		return Helper.calcRedAmount(feeAmountTag, netAmount, totalFeeAmount);
	}

	public static Double calcRedCapGainTaxAmount(double capTaxRate, double netAmount) {
		return Helper.calcRedCapGainTaxAmount(capTaxRate, netAmount);
	}

	public static Double calcRedPayment(String netAmountTag, double amount, double capTaxAmount) {
		return Helper.calcRedPayment(netAmountTag, amount, capTaxAmount);
	}

}