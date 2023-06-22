package helpers.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsTransaction;

import controllers.MedallionController;

public class TransactionForCertificatePickSerializer extends MedallionController implements JsonSerializer<CsTransaction> {
	@Override
	public JsonElement serialize(CsTransaction src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getTransactionNumber());
		object.addProperty("description", src.getTransactionTemplate().getDescription());
		object.addProperty("transactionKey", src.getTransactionKey());
		object.addProperty("transactionNo", src.getTransactionNumber());
		// object.addProperty("transactionDate",
		// src.getTransactionDate().getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		object.addProperty("transactionDate", dateFormat.format(src.getTransactionDate()));
		// object.addProperty("settlemetDate",
		// src.getSettlementDate().getTime());
		object.addProperty("settlemetDate", dateFormat.format(src.getSettlementDate()));
		object.addProperty("transactionStatus", src.getTransactionStatus());
		object.addProperty("quantity", src.getQuantity());
		object.addProperty("price", src.getPrice());
		object.addProperty("settlementAmount", src.getSettlementAmount());
		object.addProperty("holdingRefs", src.getHoldingRefs());
		object.addProperty("amount", src.getAmount());
		object.addProperty("transCodeTemplate", src.getTransactionTemplate().getTransactionCode());
		object.addProperty("transDescTemplate", src.getTransactionTemplate().getDescription());
		object.addProperty("transKeyTemplate", src.getTransactionTemplate().getTransactionTemplateKey());

		if (src.getAccount() != null) {
			object.addProperty("accountKey", src.getAccount().getCustodyAccountKey());
			object.addProperty("accountNo", src.getAccount().getAccountNo());
			object.addProperty("accountDesc", src.getAccount().getName());
		}
		if (src.getSecurity() != null) {
			object.addProperty("securityKey", src.getSecurity().getSecurityKey());
			object.addProperty("securityCode", src.getSecurity().getSecurityId());
			object.addProperty("securityDesc", src.getSecurity().getDescription());
			if (src.getSecurity().getSecurityType() != null) {
				object.addProperty("securityType", src.getSecurity().getSecurityType().getSecurityType());
			}
		}
		return object;
	}
}
