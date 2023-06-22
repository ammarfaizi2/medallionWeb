package helpers.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsPortfolio;

public class CsPortfolioPickSerializer implements JsonSerializer<CsPortfolio> {
	@Override
	public JsonElement serialize(CsPortfolio src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getPortfolioKey());
		object.addProperty("description", src.getClassification().getLookupDescription());
		object.addProperty("classicationLookupId", src.getClassification().getLookupId());
		object.addProperty("holdingRefs", src.getHoldingRefs());
		object.addProperty("portoQuantity", src.getPortfolioQuantity());
		object.addProperty("totalQuantity", src.getTotalQuantity());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if (src.getEffectiveDate() != null)
			object.addProperty("effectiveDate", dateFormat.format(src.getEffectiveDate()));
		// object.addProperty("effectiveDate",
		// src.getEffectiveDate().getTime());

		return object;
	}
}
