package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsDailyHolding;

public class DailyHoldingPickSerializer implements JsonSerializer<CsDailyHolding> {
	@Override
	public JsonElement serialize(CsDailyHolding src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getId().getHoldingRefs());
		object.addProperty("quantity", src.getSettledQuantity());
		return object;
	}
}
