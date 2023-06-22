package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsAccount;

public class CsAccountPickSerializer implements JsonSerializer<CsAccount> {
	@Override
	public JsonElement serialize(CsAccount src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getCustodyAccountKey());
		object.addProperty("accountNo", src.getAccountNo());
		object.addProperty("description", src.getName());
		return object;
	}
}
