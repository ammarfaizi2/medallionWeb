package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.helper.Helper;

public class SecurityPickForChargeItemSerializer implements JsonSerializer<ScMaster> {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SecurityPickForChargeItemSerializer.class);

	@Override
	public JsonElement serialize(ScMaster src, Type typeOfSrc, JsonSerializationContext context) {
		log.debug("----------------------->" + src.getIssuer());
		log.debug("----------------------->" + src.getIssuer().getThirdPartyKey());
		log.debug("----------------------->" + src.getIssuer().getThirdPartyCode());
		log.debug("----------------------->" + src.getIssuer().getThirdPartyName());

		JsonObject object = new JsonObject();
		object.addProperty("securityKey", Helper.NullValue(src.getSecurityKey(), 0));
		object.addProperty("code", Helper.NullValue(src.getSecurityKey(), 0));
		object.addProperty("securityId", Helper.NullValue(src.getSecurityId(), ""));
		object.addProperty("description", Helper.NullValue(src.getDescription(), ""));
		object.addProperty("securityType", src.getSecurityType().getSecurityType());
		object.addProperty("securityTypeDesc", src.getSecurityType().getDescription());
		object.addProperty("securityClass", src.getSecurityType().getSecurityClass().getLookupCode());
		object.addProperty("securityClassDesc", src.getSecurityType().getSecurityClass().getLookupDescription());
		object.addProperty("securityClassKey", src.getSecurityType().getSecurityClass().getLookupId());
		object.addProperty("depositoryDesc", (src.getDepositoryCode() == null) ? "" : src.getDepositoryCode().getLookupDescription());
		object.addProperty("depositoryId", (src.getDepositoryCode() == null) ? "" : src.getDepositoryCode().getLookupId());
		object.addProperty("issuerKey", (src.getIssuer() == null) ? -1 : src.getIssuer().getThirdPartyKey());
		object.addProperty("marketOfRiskId", (src.getMarketOfRisk() == null) ? "" : src.getMarketOfRisk().getLookupId());
		return object;
	}
}
