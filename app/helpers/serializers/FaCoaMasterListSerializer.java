package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.FaCoaMaster;

import extensions.StatusExtension;

public class FaCoaMasterListSerializer implements JsonSerializer<FaCoaMaster> {
	
	@Override
	public JsonElement serialize(FaCoaMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("coaMasterKey", src.getCoaMasterKey());
		object.addProperty("description", src.getDescription());
		object.addProperty("coaCode", src.getCoaCode());
		object.addProperty("nature", src.getNature());
		object.addProperty("currency", src.getCurrency().getCurrencyCode());
		object.addProperty("status", src.getIsActive());
		object.addProperty("sequence", String.format("%06d", src.getCoaMasterKey()));
		object.addProperty("level", src.getCoaLevel());
		object.addProperty("decodeStatus", StatusExtension.decodeStatus(src.getRecordStatus()));
		if (src.getIsActive() == true) {
			object.addProperty("isActive", "Yes");
		} else {
			object.addProperty("isActive", "No");
		}
		if (src.getCoaParent() != null) {
			object.addProperty("parentStatus", StatusExtension.decodeStatus(src.getCoaParent().getRecordStatus()));
			object.addProperty("coaParent", String.format("%06d", src.getCoaParent().getCoaMasterKey()));
		} else {
			object.addProperty("parentStatus", StatusExtension.decodeStatus(src.getRecordStatus()));
			object.addProperty("coaParent", "");
		}
		if (src.getRowNumber() > 0) {
			object.addProperty("rowNumber", src.getRowNumber());
		} else {
			object.addProperty("rowNumber", "");
		}
		if (src.getChildren() > 0) {
			object.addProperty("children", src.getChildren());
		} else {
			object.addProperty("children", "");
		}
		// if (src.getLastChild() != null) {
		// if (src.getLastChild() == 0){
		// object.addProperty("isLastChild", "No");
		// } else {
		// object.addProperty("isLastChild", "Yes");
		// }
		// } else {
		// object.addProperty("isLastChild", "");
		// }

		object.addProperty("sequenced", String.format("%03d", src.getRowNumber()));

		return object;
	}
}
