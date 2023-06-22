package helpers.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.TdMaster;

public class TdMasterPickSerializer implements JsonSerializer<TdMaster> {
	@Override
	public JsonElement serialize(TdMaster src, Type typeOfSrc, JsonSerializationContext context) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		JsonObject object = new JsonObject();
		object.addProperty("depositoNo", src.getDepositoNo());
		object.addProperty("depositoKey", src.getDepositoKey());
		object.addProperty("amount", src.getAmount());
		if (src.getPlacementDate() != null)
			object.addProperty("placementDate", dateFormat.format(src.getPlacementDate()));
		object.addProperty("depositoStatus", src.getDepositoStatus());
		object.addProperty("accountNo", src.getAccount().getAccountNo());
		object.addProperty("accountKey", src.getAccount().getCustodyAccountKey());
		object.addProperty("accountName", src.getAccount().getName());
		object.addProperty("securityType", src.getSecurity().getSecurityType().getSecurityType());
		object.addProperty("securityCode", src.getSecurity().getSecurityId());
		object.addProperty("securityDesc", src.getSecurity().getDescription());
		object.addProperty("securityKey", src.getSecurity().getSecurityKey());
		object.addProperty("currencyCode", src.getSecurity().getCurrency().getCurrencyCode());
		object.addProperty("thirdPartyCode", src.getSecurity().getIssuer().getThirdPartyCode());
		object.addProperty("thirdPartyName", src.getSecurity().getIssuer().getThirdPartyName());
		object.addProperty("branchCode", src.getBranchCode());
		object.addProperty("script", src.getScript());
		object.addProperty("interestRate", src.getInterestRate());
		if (src.getMaturityDate() != null)
			object.addProperty("maturityDate", dateFormat.format(src.getMaturityDate()));
		if (src.getEffectiveDate() != null)
			object.addProperty("effectiveDate", dateFormat.format(src.getEffectiveDate()));
		object.addProperty("nominal", src.getNominal());
		object.addProperty("maturityInstructionId", src.getMaturityInstruction() == null ? "" : src.getMaturityInstruction().getLookupId());
		object.addProperty("maturityInstructionDesc", src.getMaturityInstruction() == null ? "" : src.getMaturityInstruction().getLookupDescription());
		object.addProperty("interestFrequencyId", src.getInterestFrequency() == null ? "" : src.getInterestFrequency().getLookupId());
		object.addProperty("interestFrequencyDesc", src.getInterestFrequency() == null ? "" : src.getInterestFrequency().getLookupDescription());
		return object;
	}
}
