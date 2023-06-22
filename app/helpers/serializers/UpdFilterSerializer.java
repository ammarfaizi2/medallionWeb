package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.UpdFilter;

public class UpdFilterSerializer implements JsonSerializer<UpdFilter> {

	@Override
	public JsonElement serialize(UpdFilter o, Type arg1, JsonSerializationContext arg2) {
		JsonObject object = new JsonObject();
		object.addProperty("filterKey", o.getFilterKey());
		object.addProperty("fieldName", o.getFieldName());
		object.addProperty("fieldKey", o.getFilterKey());
		object.addProperty("defValue", o.getDefValue());
		object.addProperty("dataType", o.getDataType());
		object.addProperty("noSeq", o.getNoSeq());
		object.addProperty("required", o.getRequired());
		object.addProperty("lookupDisplay", o.getLookupDisplay());
		object.addProperty("lookupView", o.getLookupView());

		JsonObject operators = new JsonObject();
		if (o.getDefOperator() != null) {
			operators.addProperty("lookupId", o.getDefOperator().getLookupId());
			operators.addProperty("lookupCode", o.getDefOperator().getLookupCode());
		}
		object.add("defOperator", operators);
		return object;
	}
}
