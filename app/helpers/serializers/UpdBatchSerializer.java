package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.UpdBatch;

public class UpdBatchSerializer implements JsonSerializer<UpdBatch> {

	@Override
	public JsonElement serialize(UpdBatch src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("batchKey", src.getBatchKey());
		object.addProperty("profile", src.getProfile().getProfileKey());
		object.addProperty("status", src.getStatus());
		object.addProperty("userId", src.getUserId());
		object.addProperty("description", src.getProfile().getName());
		object.addProperty("filename", src.getFilename());
		object.addProperty("hasTitle", src.getHasTitle());
		return object;
	}

}
