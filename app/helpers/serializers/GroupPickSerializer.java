package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnGroup;

public class GroupPickSerializer implements JsonSerializer<GnGroup> {
	@Override
	public JsonElement serialize(GnGroup src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getGroupKey());
		object.addProperty("description", src.getGroupName());
		return object;
	}
}
