package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.FaPostingProfile;

public class FaPostingProfilePickSerializer implements JsonSerializer<FaPostingProfile> {
	@Override
	public JsonElement serialize(FaPostingProfile src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getPostingProfileKey());
		object.addProperty("description", src.getProfileName());
		object.addProperty("profileCode", src.getProfileCode());
		return object;
	}
}
