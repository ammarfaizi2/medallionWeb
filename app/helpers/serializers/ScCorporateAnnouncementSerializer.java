package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScCorporateAnnouncement;

public class ScCorporateAnnouncementSerializer implements JsonSerializer<ScCorporateAnnouncement> {

	@Override
	public JsonElement serialize(ScCorporateAnnouncement arg0, Type arg1, JsonSerializationContext arg2) {
		JsonObject object = new JsonObject();
		object.addProperty("key", arg0.getCorporateAnnouncementKey());
		object.addProperty("code", arg0.getCorporateAnnouncementCode());
		object.addProperty("description", arg0.getDescription());
		return object;
	}

}
