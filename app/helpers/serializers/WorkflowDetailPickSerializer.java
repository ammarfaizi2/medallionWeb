package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnWorkflowDetail;

public class WorkflowDetailPickSerializer implements JsonSerializer<GnWorkflowDetail> {
	@Override
	public JsonElement serialize(GnWorkflowDetail src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getWorkflowDetailKey());
		object.addProperty("description", src.getWorkflowName());
		object.addProperty("procDef", src.getProcessDefinition());
		object.addProperty("activityName", src.getActivityName());
		return object;
	}

}
