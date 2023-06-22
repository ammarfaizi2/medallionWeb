package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.TxProfile;

public class TxProfilePickSerializer implements JsonSerializer<TxProfile> {

	@Override
	public JsonElement serialize(TxProfile src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getTaxProfileCode());
		object.addProperty("description", src.getDescription());
		return object;
	}

}
