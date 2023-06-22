package helpers.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.helper.Helper;

import controllers.MedallionController;

public class SecurityPickSerializer extends MedallionController implements JsonSerializer<ScMaster> {
	@Override
	public JsonElement serialize(ScMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("securityKey", Helper.NullValue(src.getSecurityKey(), 0));
		object.addProperty("code", Helper.NullValue(src.getSecurityKey(), 0));
		object.addProperty("securityId", Helper.NullValue(src.getSecurityId(), ""));
		object.addProperty("description", Helper.NullValue(src.getDescription(), ""));
		object.addProperty("securityType", src.getSecurityType().getSecurityType());
		object.addProperty("marketOfRisk", Helper.NullValue(src.getMarketOfRisk().getLookupId(), ""));
		object.addProperty("interestRate", Helper.NullValue(src.getInterestRate(), 0));
		object.addProperty("settlementDays", Helper.NullValue(src.getSettlementDays(), 0));
		object.addProperty("isScript", Helper.NullValue(src.getIsScript(), false));
		object.addProperty("isFraction", Helper.NullValue(src.getIsFraction(), false));
		object.addProperty("fractionRatioSource", Helper.NullValue(src.getFractionRatioSource(), 0));
		object.addProperty("fractionRatioTarget", Helper.NullValue(src.getFractionRatioTarget(), 0));
		object.addProperty("currency", Helper.NullValue(src.getCurrency().getCurrencyCode(), ""));
		object.addProperty("tab", Helper.NullValue(src.getTab(), ""));
		object.addProperty("currentHolding", Helper.NullValue(src.getCurrentHolding(), 0));

		if (src.getAccrualBase() != null) {
			object.addProperty("accrualBase", Helper.NullValue(src.getAccrualBase().getLookupId(), ""));
		}
		if (src.getYearBase() != null) {
			object.addProperty("yearBase", Helper.NullValue(src.getYearBase().getLookupId(), ""));
		}
		if (src.getFrequency() != null) {
			object.addProperty("frequency", Helper.NullValue(src.getFrequency().getLookupId(), ""));
		}
		if (src.getLastPaymentDate() != null) {
			object.addProperty("lastPaymentDate", src.getLastPaymentDate().getTime());
		}
		if (src.getNextPaymentDate() != null) {
			object.addProperty("nextPaymentDate", src.getNextPaymentDate().getTime());
		}
		if (src.getMaturityDate() != null) {
			object.addProperty("maturityDate", src.getMaturityDate().getTime());
		}
		if (src.getDepositoryCode() != null) {
			object.addProperty("depositoryCode", Helper.NullValue(src.getDepositoryCode().getLookupId(), ""));
		}

		object.addProperty("parValue", src.getParUnitValue());

		if (src.getValuationMethodTrade() != null) {
			object.addProperty("valuationMethodTrade", Helper.NullValue(src.getValuationMethodTrade().getLookupId(), ""));
		}
		if (src.getValuationMethodAFS() != null) {
			object.addProperty("valuationMethodAFS", Helper.NullValue(src.getValuationMethodAFS().getLookupId(), ""));
		}
		if (src.getValuationMethodHTM() != null) {
			object.addProperty("valuationMethodHTM", Helper.NullValue(src.getValuationMethodHTM().getLookupId(), ""));
		}
		if (src.getAmortizationMethodTrade() != null)
			object.addProperty("amortizationMethodTrade", Helper.NullValue(src.getAmortizationMethodTrade().getLookupId(), ""));
		if (src.getAmortizationMethodAFS() != null)
			object.addProperty("amortizationMethodAFS", Helper.NullValue(src.getAmortizationMethodAFS().getLookupId(), ""));
		if (src.getAmortizationMethodHTM() != null)
			object.addProperty("amortizationMethodHTM", Helper.NullValue(src.getAmortizationMethodHTM().getLookupId(), ""));

		if (src.getMinTrxQuantity() != null) {
			object.addProperty("minTrxQuantity", src.getMinTrxQuantity());
		}

		if ((src.getCurrency() != null) && (!Helper.isNullOrEmpty(src.getCurrency().getCurrencyCode()))) {
			object.addProperty("securityCurrency", src.getCurrency().getCurrencyCode());
		}
		if (src.getIsExpire() != null) {
			object.addProperty("isExpiredDate", src.getIsExpire());
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		if (src.getExpiredDate() != null) {
			// object.addProperty("expiredDate",
			// src.getExpiredDate().toGMTString());
			object.addProperty("expiredDate", dateFormat.format(src.getExpiredDate()));
		}

		JsonObject issuerBank = new JsonObject();
		issuerBank.addProperty("thirdPartyKey", (src.getIssuer() == null) ? null : src.getIssuer().getThirdPartyKey());
		issuerBank.addProperty("thirdPartyCode", (src.getIssuer() == null) ? null : src.getIssuer().getThirdPartyCode());
		issuerBank.addProperty("thirdPartyName", (src.getIssuer() == null) ? null : src.getIssuer().getThirdPartyName());
		object.add("issuerBank", issuerBank);

		if (src.getIssueDate() != null) {
			object.addProperty("issueDate", dateFormat.format(src.getIssueDate()));
		}
		if (src.getFirstCouponDate() != null) {
			object.addProperty("firstCouponDate", dateFormat.format(src.getFirstCouponDate()));
		}
		return object;
	}
}
