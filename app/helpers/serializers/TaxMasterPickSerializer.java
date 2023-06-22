package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnTaxMaster;

public class TaxMasterPickSerializer implements JsonSerializer<GnTaxMaster> {
	@Override
	public JsonElement serialize(GnTaxMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getTaxKey());
		object.addProperty("taxCode", src.getTaxCode());
		object.addProperty("description", src.getDescription());
		object.addProperty("taxRate", src.getTaxRate());
		return object;
	}
}
