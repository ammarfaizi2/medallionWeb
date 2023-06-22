package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.FaTransactionMaster;

public class FaTransactionMasterPickSerializer implements JsonSerializer<FaTransactionMaster> {
	@Override
	public JsonElement serialize(FaTransactionMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getTransactionMasterKey());
		object.addProperty("description", src.getTransactionDescription());
		object.addProperty("instructionType", src.getInstructionType().getLookupId());
		return object;
	}

}
