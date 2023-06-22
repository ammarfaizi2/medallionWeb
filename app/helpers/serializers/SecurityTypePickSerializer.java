package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScTypeMaster;

public class SecurityTypePickSerializer implements JsonSerializer<ScTypeMaster> {
	@Override
	public JsonElement serialize(ScTypeMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getSecurityType());
		object.addProperty("description", src.getDescription());
		object.addProperty("securityClass", src.getSecurityClass().getLookupId());
		object.addProperty("securityClassCode", src.getSecurityClass().getLookupCode());
		object.addProperty("securityClassDesc", src.getSecurityClass().getLookupDescription());
		object.addProperty("securityType", src.getSecurityType());
		object.addProperty("securityTypeDesc", src.getDescription());
		object.addProperty("ctpRequired", src.getCtpRequired());
		object.addProperty("tabCertificate", src.getTabCertificate());
		if (src.getHasInterest() != null)
			object.addProperty("hasInterest", src.getHasInterest());

		if (src.getIsPrice() != null)
			object.addProperty("isPrice", src.getIsPrice());

		if (src.getIsDiscounted() != null)
			object.addProperty("isDiscounted", src.getIsDiscounted());

		object.addProperty("priceUnit", src.getPriceUnit());

		if (src.getDepositoTrxTemplate() != null) {
			object.addProperty("depositoTemplateKey", src.getDepositoTrxTemplate().getDepositoTemplateKey());
			object.addProperty("transactionTemplateKey", src.getDepositoTrxTemplate().getTransactionTemplate().getTransactionTemplateKey());
		}

		return object;
	}
}
