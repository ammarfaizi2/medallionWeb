package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.CsAccount;

public class AccountPickSerializer implements JsonSerializer<CsAccount> {
	@Override
	public JsonElement serialize(CsAccount src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("code", src.getCustodyAccountKey());
		object.addProperty("description", src.getName());
		object.addProperty("accountNo", src.getAccountNo());
		object.addProperty("balance", src.getBalance());
		object.addProperty("blocked", src.getBlocked());
		if (src.getSettlementAccount() != null) {
			object.addProperty("bankAccountKey", src.getSettlementAccount().getBankAccountKey());
			object.addProperty("bankAccountNo", src.getSettlementAccount().getAccountNo());
			object.addProperty("bankAccountDesc", src.getSettlementAccount().getName());
			if (src.getSettlementAccount().getBankCode() != null) {
				object.addProperty("bankCodeKey", src.getSettlementAccount().getBankCode().getThirdPartyKey());
				object.addProperty("bankCodeName", src.getSettlementAccount().getBankCode().getThirdPartyCode());
				object.addProperty("bankCodeDesc", src.getSettlementAccount().getBankCode().getThirdPartyName());
			}
			object.addProperty("bankCodeBeneficiary", src.getSettlementAccount().getCustomer().getCustomerName());
			object.addProperty("bankCodeCurrency", src.getSettlementAccount().getCurrency().getCurrencyCode());
		}
		if (src.getCurrentHolding() != null) {
			object.addProperty("currentHolding", src.getCurrentHolding());
		}

		if ((src.getCustomer() != null) && (src.getCustomer().getCustomerKey() != null)) {
			object.addProperty("customerKey", src.getCustomer().getCustomerKey());
		}

		return object;
	}
}
