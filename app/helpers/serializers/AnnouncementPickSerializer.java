package helpers.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScCorporateAnnouncement;

import controllers.MedallionController;

public class AnnouncementPickSerializer extends MedallionController implements JsonSerializer<ScCorporateAnnouncement> {
	@Override
	public JsonElement serialize(ScCorporateAnnouncement src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		object.addProperty("code", src.getCorporateAnnouncementKey());
		object.addProperty("description", src.getDescription());
		object.addProperty("exDate", dateFormat.format(src.getExDate()));
		object.addProperty("recordingDate", dateFormat.format(src.getRecordingDate()));

		return object;
	}

}
