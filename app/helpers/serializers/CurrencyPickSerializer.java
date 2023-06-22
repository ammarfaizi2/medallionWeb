package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnCurrency;

public class CurrencyPickSerializer implements JsonSerializer<GnCurrency> {
	@Override
	public JsonElement serialize(GnCurrency src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getCurrencyCode());
		object.addProperty("description", src.getDescription());
		return object;
	}

}
