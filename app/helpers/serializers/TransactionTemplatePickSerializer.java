package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsTransactionTemplate;
import com.simian.medallion.model.helper.Helper;

public class TransactionTemplatePickSerializer implements JsonSerializer<CsTransactionTemplate> {
	@Override
	public JsonElement serialize(CsTransactionTemplate src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getTransactionTemplateKey());
		object.addProperty("transactionCode", src.getTransactionCode());
		object.addProperty("description", src.getDescription());
		object.addProperty("securityClassId", src.getSecurityType().getSecurityClass().getLookupId());
		object.addProperty("securityClass", src.getSecurityType().getSecurityClass().getLookupCode());
		object.addProperty("securityClassDesc", src.getSecurityType().getSecurityClass().getLookupDescription());
		object.addProperty("securityType", src.getSecurityType().getSecurityType());
		object.addProperty("securityTypeDesc", src.getSecurityType().getDescription());
		object.addProperty("priceUnit", src.getSecurityType().getPriceUnit());
		if (src.getPortfolioTransaction() != null) {
			object.addProperty("transactionType", src.getPortfolioTransaction().getTransactionType().getLookupCode());
		}
		object.addProperty("discounted", Helper.NullValue(src.getSecurityType().getIsDiscounted(), false));
		object.addProperty("hasInterest", Helper.NullValue(src.getSecurityType().getHasInterest(), false));
		object.addProperty("isPrice", Helper.NullValue(src.getSecurityType().getIsPrice(), false));

		if (src.getSettlementTransaction() != null) {
			object.addProperty("settlementTransactionKey", src.getSettlementTransaction().getTransactionTemplateKey());
			if (src.getSettlementTransaction().getCashTransaction() != null) {
				object.addProperty("cashTransactionCode", src.getSettlementTransaction().getCashTransaction().getTransactionMasterKey());
			}
		} else {
			if (src.getCashTransaction() != null) {
				object.addProperty("cashTransactionCodeSettlement", src.getCashTransaction().getTransactionMasterKey());
			}
		}

		// if (src.getSecurityType().getCbestMessageType()!=null)
		// object.addProperty("cBestMessageType",
		// src.getSecurityType().getCbestMessageType().getLookupId());
		object.addProperty("defPrematch", Helper.NullValue(src.getDefPrematch(), false));

		object.addProperty("defaultCorebanking", src.getDefaultCorebanking());

		object.addProperty("ctpRequired", src.getSecurityType().getCtpRequired());

		if (src.getSecurityType().getHoldingType() != null) {
			object.addProperty("holdingType", src.getSecurityType().getHoldingType().getLookupId());
		}
		
		object.addProperty("custodyTransactionCode", src.getPortfolioTransaction().getCustodyTransactionCode());

		return object;
	}
}
