package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgFeeTier;

public class FeeTierPickSerializer implements JsonSerializer<RgFeeTier> {
	@Override
	public JsonElement serialize(RgFeeTier src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("productcode", src.getId().getProductCode());
		object.addProperty("type", src.getId().getType());
		object.addProperty("tierNumber", src.getId().getTierNumber());
		object.addProperty("upperLimit", src.getUpperLimit());
		object.addProperty("value", src.getValue());
		return object;
	}
}
