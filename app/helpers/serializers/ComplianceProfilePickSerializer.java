package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CpComplianceProfile;

public class ComplianceProfilePickSerializer implements JsonSerializer<CpComplianceProfile> {

	@Override
	public JsonElement serialize(CpComplianceProfile src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getComplianceProfCode());
		object.addProperty("description", src.getDescription());
		return object;
	}
}
