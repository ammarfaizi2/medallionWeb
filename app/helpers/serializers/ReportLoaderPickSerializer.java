package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnReportLoader;

public class ReportLoaderPickSerializer implements JsonSerializer<GnReportLoader> {
	@Override
	public JsonElement serialize(GnReportLoader src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getReportKey());
		object.addProperty("name", src.getReportName());
		return object;
	}
}
