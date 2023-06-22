package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsChargeMaster;

public class ChargeMasterPickSerializer implements JsonSerializer<CsChargeMaster> {
	@Override
	public JsonElement serialize(CsChargeMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getChargeKey());
		object.addProperty("description", src.getDescription());
		if (src.getChargeFrequency() != null) {
			object.addProperty("frequencyCode", src.getChargeFrequency().getLookupCode());
			object.addProperty("frequencyID", src.getChargeFrequency().getLookupId());

		}
		if (src.getMinimumValue() != null) {
			object.addProperty("minValue", src.getMinimumValue());
		}
		object.addProperty("maxValue", src.getMaximumValue());
		object.addProperty("chargeCode", src.getChargeCode());
		if (src.getCurrency() != null) {
			object.addProperty("currency", src.getCurrency().getCurrencyCode());
		}
		if (src.getTaxMaster() != null) {
			object.addProperty("taxCode", src.getTaxMaster().getTaxCode());
			object.addProperty("taxKey", src.getTaxMaster().getTaxKey());
			object.addProperty("taxName", src.getTaxMaster().getDescription());
			object.addProperty("taxRate", src.getTaxMaster().getTaxRate());
		}
		return object;
	}

}
