package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.BnAccount;

public class BankAccountPickSerializer implements JsonSerializer<BnAccount> {
	@Override
	public JsonElement serialize(BnAccount src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("key", src.getBankAccountKey());
		object.addProperty("code", src.getBankAccountKey());
		object.addProperty("bankAccountKey", src.getBankAccountKey());
		object.addProperty("description", src.getName());
		object.addProperty("name", src.getName());
		object.addProperty("branch", src.getBranch());
		object.addProperty("interestRate", (src.getInterestRate() == null) ? null : src.getInterestRate());
		object.addProperty("interestBase", (src.getInterestBase() == null) ? null : src.getInterestBase());
		object.addProperty("yearBase", (src.getYearBase() == null) ? null : src.getYearBase());
		object.addProperty("bankAccountNo", src.getAccountNo());
		object.addProperty("thirdPartyCode", src.getBankCode().getThirdPartyCode());
		object.addProperty("thirdPartyKey", src.getBankCode().getThirdPartyKey());
		object.addProperty("thirdPartyName", src.getBankCode().getThirdPartyName());
		object.addProperty("paymentCharge", (src.getPaymentCharge() == null) ? -1 : src.getPaymentCharge());

		JsonObject bankCode = new JsonObject();
		bankCode.addProperty("thirdPartyKey", (src.getBankCode() == null) ? null : src.getBankCode().getThirdPartyKey());
		bankCode.addProperty("thirdPartyCode", (src.getBankCode() == null) ? "" : src.getBankCode().getThirdPartyCode());
		bankCode.addProperty("thirdPartyName", (src.getBankCode() == null) ? "" : src.getBankCode().getThirdPartyName());
		object.add("bankCode", bankCode);

		JsonObject customer = new JsonObject();
		customer.addProperty("customerName", (src.getCustomer() == null) ? null : src.getCustomer().getCustomerName());
		object.add("customer", customer);

		JsonObject currency = new JsonObject();
		currency.addProperty("currencyCode", (src.getCurrency() == null) ? null : src.getCurrency().getCurrencyCode());
		currency.addProperty("description", (src.getCurrency() == null) ? null : src.getCurrency().getDescription());
		object.add("currency", currency);

		return object;
	}
}