package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.helper.Helper;

import controllers.MedallionController;

public class SecurityPickSingleSerializer extends MedallionController implements JsonSerializer<ScMaster> {
	@Override
	public JsonElement serialize(ScMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("securityKey", Helper.NullValue(src.getSecurityKey(), 0));
		object.addProperty("code", Helper.NullValue(src.getSecurityKey(), 0));
		object.addProperty("securityId", Helper.NullValue(src.getSecurityId(), ""));
		object.addProperty("description", Helper.NullValue(src.getDescription(), ""));
		return object;
	}

}
