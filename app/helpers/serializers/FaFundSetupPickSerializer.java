package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.FaMaster;

public class FaFundSetupPickSerializer implements JsonSerializer<FaMaster> {
	@Override
	public JsonElement serialize(FaMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getFundKey());
		object.addProperty("fundCode", src.getFundCode());
		object.addProperty("description", src.getFundDescription());
		return object;
	}
}
