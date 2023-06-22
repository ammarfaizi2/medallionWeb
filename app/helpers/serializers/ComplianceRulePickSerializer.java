package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CpComplianceRule;

public class ComplianceRulePickSerializer implements JsonSerializer<CpComplianceRule> {
	@Override
	public JsonElement serialize(CpComplianceRule src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getRuleId());
		object.addProperty("name", src.getRuleCode());
		object.addProperty("description", src.getDescription());
		object.addProperty("type", src.getRuleType().getLookupCode());
		object.addProperty("typeDesc", src.getRuleType().getLookupDescription());
		if (src.getComparisonValue() != null) {
			object.addProperty("comparisonDesc", src.getComparisonValue().getLookupDescription());
		} else {
			object.addProperty("comparisonDesc", "");
		}
		object.addProperty("operator", src.getOperator());
		object.addProperty("value", src.getValue());
		return object;
	}

}
