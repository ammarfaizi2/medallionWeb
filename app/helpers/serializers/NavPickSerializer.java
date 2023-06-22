package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgNav;

public class NavPickSerializer implements JsonSerializer<RgNav> {
	@Override
	public JsonElement serialize(RgNav src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("nav", src.getNav());
		object.addProperty("offerPct", src.getOfferPricePct());
		object.addProperty("bidPct", src.getBidPricePct());
		return object;
	}
}
