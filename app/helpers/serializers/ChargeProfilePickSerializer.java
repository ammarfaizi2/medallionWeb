package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsChargeProfile;

public class ChargeProfilePickSerializer implements JsonSerializer<CsChargeProfile> {
	@Override
	public JsonElement serialize(CsChargeProfile src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getChargeProfileKey());
		object.addProperty("description", src.getDescription());
		object.addProperty("name", src.getName());
		return object;
	}

}
