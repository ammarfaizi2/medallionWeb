package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.ScEntitlementDetail;

public class EntitlementSerializer implements JsonSerializer<ScEntitlementDetail> {
	@Override
	public JsonElement serialize(ScEntitlementDetail src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getEntitlementDetailKey());
		object.addProperty("AnnouncementDetail", src.getAnnouncementDetail().getAnnouncementDetailKey());
		object.addProperty("account", src.getAccount().getAccountNo());
		object.addProperty("currency", src.getCurrency().getCurrencyCode());
		object.addProperty("sourceQuantity", src.getSourceQuantity());
		object.addProperty("targetQuantity", src.getTargetQuantity());
		object.addProperty("price", src.getPrice());
		object.addProperty("status", src.getStatus());
		object.addProperty("securityTarget", src.getAnnouncementDetail().getSecurityTarget().getSecurityId());
		object.addProperty("securitySource", src.getAnnouncementDetail().getSecuritySource().getSecurityId());

		// object.addOb
		return object;
	}
}
