package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnBranch;

public class BranchPickSerializer implements JsonSerializer<GnBranch> {
	@Override
	public JsonElement serialize(GnBranch src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getBranchKey());
		object.addProperty("description", src.getName());
		return object;
	}
}
