package helpers.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsTransaction;

import controllers.MedallionController;

public class TransactionForPickSerializer extends MedallionController implements JsonSerializer<CsTransaction> {
	@Override
	public JsonElement serialize(CsTransaction src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getTransactionNumber());
		object.addProperty("transactionKey", src.getTransactionKey());
		object.addProperty("transactionNo", src.getTransactionNumber());
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		object.addProperty("transactionDate", dateFormat.format(src.getTransactionDate()));
		object.addProperty("settlemetDate", dateFormat.format(src.getSettlementDate()));
		object.addProperty("transactionStatus", src.getTransactionStatus());
		object.addProperty("quantity", src.getQuantity());
		object.addProperty("price", src.getPrice());
		object.addProperty("settlementAmount", src.getSettlementAmount());
		object.addProperty("holdingRefs", src.getHoldingRefs());
		object.addProperty("amount", src.getAmount());
		return object;
	}
}