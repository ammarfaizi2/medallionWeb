package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsTransactionMaster;

public class TransactionMasterPickSerializer implements JsonSerializer<CsTransactionMaster> {
	@Override
	public JsonElement serialize(CsTransactionMaster src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getTransactionMasterKey());
		object.addProperty("transactionCode", src.getCustodyTransactionCode());
		object.addProperty("description", src.getTransactionDescription());

		return object;
	}
}
