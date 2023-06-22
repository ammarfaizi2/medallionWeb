package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgProductBnAccount;

public class RgProductBankAccountPickSerializer implements JsonSerializer<RgProductBnAccount> {
	@Override
	public JsonElement serialize(RgProductBnAccount src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("key", src.getBnAccount().getBankAccountKey());
		object.addProperty("code", src.getBnAccount().getBankAccountKey());
		object.addProperty("bankAccountKey", src.getBnAccount().getBankAccountKey());
		object.addProperty("description", src.getBnAccount().getName());
		object.addProperty("name", src.getBnAccount().getName());
		object.addProperty("bankAccountNo", src.getBnAccount().getAccountNo());
		object.addProperty("thirdPartyCode", src.getBnAccount().getBankCode().getThirdPartyCode());
		object.addProperty("thirdPartyKey", src.getBnAccount().getBankCode().getThirdPartyKey());
		object.addProperty("thirdPartyName", src.getBnAccount().getBankCode().getThirdPartyName());

		JsonObject bankCode = new JsonObject();
		bankCode.addProperty("thirdPartyKey", (src.getBnAccount().getBankCode() == null) ? null : src.getBnAccount().getBankCode().getThirdPartyKey());
		bankCode.addProperty("thirdPartyCode", (src.getBnAccount().getBankCode() == null) ? "" : src.getBnAccount().getBankCode().getThirdPartyCode());
		bankCode.addProperty("thirdPartyName", (src.getBnAccount().getBankCode() == null) ? "" : src.getBnAccount().getBankCode().getThirdPartyName());
		object.add("bankCode", bankCode);

		return object;
	}
}