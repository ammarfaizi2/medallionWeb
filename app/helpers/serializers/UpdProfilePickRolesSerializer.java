package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.UpdProfile;

public class UpdProfilePickRolesSerializer implements JsonSerializer<UpdProfile> {
	@Override
	public JsonElement serialize(UpdProfile src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getProfileKey());
		object.addProperty("profileKey", src.getProfileKey());
		object.addProperty("name", src.getName());
		object.addProperty("description", src.getDescription());

		return object;
	}
}
