package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScActionTemplate;

public class ActionTemplatePickSerializer implements JsonSerializer<ScActionTemplate> {
	@Override
	public JsonElement serialize(ScActionTemplate src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getActionTemplateKey());
		object.addProperty("transactionCode", src.getActionCode());
		object.addProperty("description", src.getDescription());
		// object.addProperty("isSameSource", src.getIsSameSource());
		if (src.getSecurityType() == null) {
			object.addProperty("securityType", "");	
		}else{
			object.addProperty("securityType", src.getSecurityType().getSecurityType());
			object.addProperty("securityTypeDesc", src.getSecurityType().getDescription());
		}


		object.addProperty("isCoupon", src.getIsCoupon());
		object.addProperty("targetType", src.getTargetType().getLookupId());
		if (src.getSourceTransaction() != null && src.getSourceTransaction().getTransactionTemplateKey() != null) {
			object.addProperty("sourceTransaction", src.getSourceTransaction().getTransactionTemplateKey());
		}
		if (src.getTargetTransaction() != null && src.getTargetTransaction().getTransactionTemplateKey() != null) {
			object.addProperty("targetTransaction", src.getTargetTransaction().getTransactionTemplateKey());
		}

		if (src.getTaxObject() != null) {
			object.addProperty("taxObject", src.getTaxObject().getLookupId());
			object.addProperty("taxObjectDesc", src.getTaxObject().getLookupDescription());
		} else {
			object.addProperty("taxObject", "");
		}

		object.addProperty("hasExercisePrice", src.getExercisePrice() != null ? src.getExercisePrice() : null);
		object.addProperty("actionCodeLinkKey", src.getActionTemplateLink() != null ? src.getActionTemplateLink().getActionTemplateKey() : null);
		// object.addProperty("actionCodeLinkCode",
		// src.getActionTemplateLink()!=null ?
		// src.getActionTemplateLink().getActionCode() : "");
		// object.addProperty("actionCodeLinkDesc",
		// src.getActionTemplateLink()!=null ?
		// src.getActionTemplateLink().getDescription() : "");

		object.addProperty("targetType", src.getTargetType().getLookupId());
		object.addProperty("holdingBaseOn", src.getHoldingBase() != null ? src.getHoldingBase().getLookupId() : "");
		object.addProperty("customerFund", src.getCustomerFund() != null ? src.getCustomerFund().getLookupId() : "");
		if (src.getLookupEntitlementDate() != null)
			object.addProperty("scheduleDate", src.getLookupEntitlementDate().getLookupId());
		return object;
	}
}
