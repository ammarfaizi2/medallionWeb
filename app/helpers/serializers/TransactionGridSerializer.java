package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsTransaction;

public class TransactionGridSerializer implements JsonSerializer<CsTransaction> {

	@Override
	public JsonElement serialize(CsTransaction src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject object = new JsonObject();
		object.addProperty("transactionKey", src.getTransactionKey());
		object.addProperty("transactionNumber", src.getTransactionNumber());
		object.addProperty("settlementDate", src.getSettlementDate().getTime());
		object.addProperty("settlementAmount", src.getSettlementAmount());
		if (src.getTransactionBatch() != null) {
			object.addProperty("transactionBatch", src.getTransactionBatch().getTransactionBatchKey());
		} else {
			object.addProperty("transactionBatch", "");
		}
		if (src.getAccount() != null) {
			object.addProperty("accountNo", src.getAccount().getAccountNo());
		} else {
			object.addProperty("accountNo", "");
		}
		if (src.getSecurity() != null) {
			object.addProperty("securityId", src.getSecurity().getSecurityId());
		} else {
			object.addProperty("securityId", "");
		}
		if (src.getTransactionTemplate() != null) {
			object.addProperty("description", src.getTransactionTemplate().getDescription());
			object.addProperty("transactionTemplate", src.getTransactionTemplate().getTransactionTemplateKey());
			if (src.getTransactionTemplate().getSettlementTransaction().getCashTransaction() != null) {
				object.addProperty("cashTransaction", src.getTransactionTemplate().getSettlementTransaction().getCashTransaction().getTransactionMasterKey());
			} else {
				object.addProperty("cashTransaction", "");
			}
		} else {
			object.addProperty("description", "");
			object.addProperty("transactionTemplate", "");
		}
		object.addProperty("selected", false);

		return object;
	}

}
