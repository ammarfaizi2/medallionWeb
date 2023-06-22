// medallionWeb/app/helpers/serializers

package helpers.serializers;

import java.lang.reflect.Type;

import org.apache.commons.lang.time.FastDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgProdEod;

public class RgProdEodPickSerializer implements JsonSerializer<RgProdEod> {
	@Override
	public JsonElement serialize(RgProdEod src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("productCode", src.getProductCode());
		object.addProperty("name", src.getRgProduct().getName());
		object.addProperty("eodDate", FastDateFormat.getInstance("MM/dd/yyyy").format(src.getEodDate()));
		object.addProperty("lastEod", FastDateFormat.getInstance("MM/dd/yyyy").format(src.getLastEod()));
		return object;
	}
}
