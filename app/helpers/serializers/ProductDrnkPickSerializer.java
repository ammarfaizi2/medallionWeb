package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgProduct;

import controllers.MedallionController;

public class ProductDrnkPickSerializer extends MedallionController implements JsonSerializer<RgProduct> {
	@Override
	public JsonElement serialize(RgProduct src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("productCode", src.getProductCode());
		object.addProperty("description", src.getName());
		return object;
	}
}