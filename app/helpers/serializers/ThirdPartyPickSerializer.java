package helpers.serializers;

import java.lang.reflect.Type;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnThirdPartyBank;

public class ThirdPartyPickSerializer implements JsonSerializer<GnThirdParty> {
	@Override
	public JsonElement serialize(GnThirdParty src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getThirdPartyKey());
		object.addProperty("description", src.getThirdPartyName());
		object.addProperty("name", src.getThirdPartyCode());
		object.addProperty("email", src.getEmail());

		// FOR EXTERNAL_PRODUCT
		object.addProperty("type", src.getThirdPartyType().getLookupId());

		if (src.getCurrency() != null) {
			object.addProperty("currencyCode", src.getCurrency().getCurrencyCode());
		}

		if (src.getFundManager() != null) {
			object.addProperty("fundManagerKey", src.getFundManager().getThirdPartyKey());
			object.addProperty("fundManagerCode", src.getFundManager().getThirdPartyCode());
			object.addProperty("fundManagerName", src.getFundManager().getThirdPartyName());
		}

		GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
		if ((src.getThirdPartyBanks() != null) && (!src.getThirdPartyBanks().isEmpty())) {
			Set<GnThirdPartyBank> tpBanks = src.getThirdPartyBanks();
			thirdPartyBank = tpBanks.iterator().next();

			object.addProperty("bankThirdPartyBankKey", thirdPartyBank.getThirdPartyBankKey());
			if (thirdPartyBank.getBankCode() != null) {
				object.addProperty("bankCodeThirdPartyKey", thirdPartyBank.getBankCode().getThirdPartyKey());
				object.addProperty("bankCodeThirdPartyCode", thirdPartyBank.getBankCode().getThirdPartyCode());
				object.addProperty("bankCodeThirdPartyName", thirdPartyBank.getBankCode().getThirdPartyName());
			}
			object.addProperty("bankAccountNo", thirdPartyBank.getAccountNo());
			object.addProperty("bankAccountName", thirdPartyBank.getAccountName());

			if (thirdPartyBank.getCurrency() != null) {
				object.addProperty("bankCurrencyCode", thirdPartyBank.getCurrency().getCurrencyCode());
			}
		}

		object.addProperty("custodianBank", src.getCustodianBank());

		return object;
	}
}