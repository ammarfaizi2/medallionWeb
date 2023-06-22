package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgPortfolio;

public class RgPortfolioPickSerializer implements JsonSerializer<RgPortfolio> {
	@Override
	public JsonElement serialize(RgPortfolio src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("navPrice", src.getNavPrice());
		object.addProperty("balanceAmount", src.getBalanceAmount());
		object.addProperty("unit", src.getUnit());

		JsonObject id = new JsonObject();
		id.addProperty("accountNumber", (src.getId() == null) ? "" : src.getId().getAccountNumber());
		id.addProperty("holdingDate", (src.getId() == null) ? 0 : src.getId().getHoldingDate().getTime());
		object.add("id", id);

		return object;
	}
}
