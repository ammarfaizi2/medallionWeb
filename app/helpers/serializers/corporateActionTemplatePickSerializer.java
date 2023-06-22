package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnLookup;

public class corporateActionTemplatePickSerializer implements JsonSerializer<GnLookup> {
	@Override
	public JsonElement serialize(GnLookup src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getLookupId());
		object.addProperty("description", src.getLookupDescription());
		return object;
	}
}
