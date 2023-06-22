package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.vo.ColumnName;

public class TblNamePickSerializer implements JsonSerializer<ColumnName> {
	@Override
	public JsonElement serialize(ColumnName src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		object.addProperty("targetField", src.getTargetField());
		object.addProperty("dataType", src.getDataType());

		return object;
	}

}
