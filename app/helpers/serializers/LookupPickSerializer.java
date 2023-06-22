package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.constant.PropertyUdfConstants;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnLookupDetail;

public class LookupPickSerializer implements JsonSerializer<GnLookup> {
	@Override
	public JsonElement serialize(GnLookup src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getLookupId());
		object.addProperty("description", src.getLookupDescription());
		object.addProperty("lookupCode", src.getLookupCode());

		if (src.getDetail() != null) {
			for (GnLookupDetail lookupDetail : src.getDetail()) {
				if (PropertyUdfConstants.FIELD_SUPPLEMENTARY_REQUIRED.equals(lookupDetail.getUdfMaster().getFieldName())) {
					object.addProperty("isRequired", lookupDetail.getDetailValue());
				}
			}
		}
		return object;
	}
}
