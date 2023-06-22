package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.UpdBatchDetail;

public class UpdBatchDetailsSerializer implements JsonSerializer<UpdBatchDetail> {

	@Override
	public JsonElement serialize(UpdBatchDetail details, Type arg1, JsonSerializationContext arg2) {
		JsonObject object = new JsonObject();
		object.addProperty("errorDescription", ((details.getErrorDescription() == null) || (details.getErrorDescription().trim().length() == 0)) ? "" : details.getErrorDescription());
		object.addProperty("rawMessage", details.getRawMessage());
		object.addProperty("status", ((details.getStatus() == null) || (details.getStatus().getLookupCode().trim().length() == 0)) ? "" : details.getStatus().getLookupCode());
		object.addProperty("batchDetailId", details.getBatchDetailId());
		object.addProperty("batchId", details.getUpdBatch().getBatchKey());

		return object;
	}

}
