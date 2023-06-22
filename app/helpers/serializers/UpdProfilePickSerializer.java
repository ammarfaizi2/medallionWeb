package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.UpdProfile;

public class UpdProfilePickSerializer implements JsonSerializer<UpdProfile> {
	@Override
	public JsonElement serialize(UpdProfile src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		object.addProperty("code", src.getProfileKey());
		object.addProperty("profileKey", src.getProfileKey());
		object.addProperty("name", src.getName());
		object.addProperty("actionType", src.getActionType());
		object.addProperty("source", src.getSource());
		object.addProperty("description", src.getDescription());
		object.addProperty("separatorCsv", src.getSeparatorCsv());
		object.addProperty("separatorTxt", src.getSeparatorTxt());
		object.addProperty("templete", src.getTemplete());
		object.addProperty("filetype", src.getFileType());
		object.addProperty("fileprefix", src.getFilePrefix());
		object.addProperty("includeHeader", src.getIncludeHeader());
		object.addProperty("processDescription", src.getProcessDescription());
		object.addProperty("process", src.getProcess());
		object.add("details", context.serialize(src.getDetails()));
		object.add("filters", context.serialize(src.getFilters()));

		JsonObject separator = new JsonObject();
		if (src.getSeparator() != null) {
			separator.addProperty("lookupId", src.getSeparator().getLookupId());
			separator.addProperty("lookupDescription", src.getSeparator().getLookupDescription());
			separator.addProperty("lookupCode", src.getSeparator().getLookupCode());
		}

		object.add("separator", separator);

		/*
		 * JsonObject filterObject = new JsonObject(); String filters = null; if
		 * (!src.getFilters().isEmpty()){ JsonHelper json = new
		 * JsonHelper().getUpdFilterSerializer(); try { filters =
		 * json.serialize(src.getFilters()); } catch (JsonGenerationException e)
		 * { logger.error(e.getMessage(), e); } catch (JsonMappingException e) {
		 * logger.error(e.getMessage(), e); } catch (IOException e) {
		 * logger.error(e.getMessage(), e); } object.addProperty("filters",
		 * filters); } else { object.addProperty("filters", filters); }
		 */
		return object;
	}
}
