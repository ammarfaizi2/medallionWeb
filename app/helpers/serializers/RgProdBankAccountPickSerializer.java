package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgProdBnAccount;

public class RgProdBankAccountPickSerializer implements JsonSerializer<RgProdBnAccount> {
	@Override
	public JsonElement serialize(RgProdBnAccount src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("key", src.getBankAccountKey());
		object.addProperty("code", src.getBankAccountKey());
		object.addProperty("bankAccountKey", src.getBankAccountKey());
		object.addProperty("description", src.getName());
		object.addProperty("name", src.getName());
		object.addProperty("bankAccountNo", src.getAccountNo());
		object.addProperty("thirdPartyCode", src.getBankCode().getThirdPartyCode());
		object.addProperty("thirdPartyKey", src.getBankCode().getThirdPartyKey());
		object.addProperty("thirdPartyName", src.getBankCode().getThirdPartyName());

		JsonObject bankCode = new JsonObject();
		bankCode.addProperty("thirdPartyKey", (src.getBankCode() == null) ? null : src.getBankCode().getThirdPartyKey());
		bankCode.addProperty("thirdPartyCode", (src.getBankCode() == null) ? "" : src.getBankCode().getThirdPartyCode());
		bankCode.addProperty("thirdPartyName", (src.getBankCode() == null) ? "" : src.getBankCode().getThirdPartyName());
		object.add("bankCode", bankCode);

		return object;
	}
}
