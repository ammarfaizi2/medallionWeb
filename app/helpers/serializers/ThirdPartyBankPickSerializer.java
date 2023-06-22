package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnThirdPartyBank;

public class ThirdPartyBankPickSerializer implements JsonSerializer<GnThirdPartyBank> {
	@Override
	public JsonElement serialize(GnThirdPartyBank src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();

		// FOR EXTERNAL_PRODUCT
		object.addProperty("bankThirdPartyBankKey", src.getThirdPartyBankKey());
		object.addProperty("bankCodeThirdPartyKey", src.getBankCode().getThirdPartyKey());
		object.addProperty("bankCodeThirdPartyCode", src.getBankCode().getThirdPartyCode());
		object.addProperty("bankCodeThirdPartyName", src.getBankCode().getThirdPartyName());
		object.addProperty("bankAccountNo", src.getAccountNo());
		object.addProperty("bankAccountName", src.getAccountName());
		object.addProperty("bankCurrencyCode", src.getCurrency().getCurrencyCode());
		object.addProperty("custodianBank", src.getBankCode().getCustodianBank());

		return object;
	}
}