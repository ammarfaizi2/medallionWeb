package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CfMaster;

public class CustomerPickSerializer implements JsonSerializer<CfMaster> {
	@Override
	public JsonElement serialize(CfMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getCustomerKey());
		object.addProperty("customerNo", src.getCustomerNo());
		object.addProperty("customerKey", src.getCustomerKey());
		object.addProperty("description", src.getCustomerName());
		return object;
	}
}
