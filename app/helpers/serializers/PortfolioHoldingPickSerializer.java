package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.vo.PortfolioHolding;

public class PortfolioHoldingPickSerializer implements JsonSerializer<PortfolioHolding> {
	@Override
	public JsonElement serialize(PortfolioHolding src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getHoldingRefs());
		object.addProperty("quantity", src.getSettledQuantity());
		if (src.getLastPaymentDate() != null) {
			object.addProperty("lastPaymentDate", src.getLastPaymentDate().getTime());
		}
		if (src.getNextPaymentDate() != null) {
			object.addProperty("nextPaymentDate", src.getNextPaymentDate().getTime());
		}
		if (src.getMaturityDate() != null) {
			object.addProperty("maturityDate", src.getMaturityDate().getTime());
		}
		if (src.getEffectiveDate() != null) {
			object.addProperty("effectiveDate", src.getEffectiveDate().getTime());
		}
		object.addProperty("interestRate", src.getInterestRate());

		if (src.getYield() != null)
			object.addProperty("yield", src.getYield());
		return object;
	}
}
