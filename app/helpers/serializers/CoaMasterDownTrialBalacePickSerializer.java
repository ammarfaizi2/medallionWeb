package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.FaCoaMaster;

public class CoaMasterDownTrialBalacePickSerializer implements JsonSerializer<FaCoaMaster> {
	
	@Override
	public JsonElement serialize(FaCoaMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getCoaMasterKey());
		object.addProperty("coaCode", src.getCoaCode());
		object.addProperty("description", src.getDescription());

		return object;
	}
}
