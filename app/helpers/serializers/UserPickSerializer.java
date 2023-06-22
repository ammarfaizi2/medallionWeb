package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnUser;

public class UserPickSerializer implements JsonSerializer<GnUser> {
	@Override
	public JsonElement serialize(GnUser src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getUserKey());
		object.addProperty("userId", src.getUserId());
		object.addProperty("description", src.getUserName());
		return object;
	}
}
