package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgTrade;

public class RegistryPaymentGridSerializer implements JsonSerializer<RgTrade> {

	@Override
	public JsonElement serialize(RgTrade src, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject object = new JsonObject();
		object.addProperty("tradeKey", src.getTradeKey());
		object.addProperty("amount", src.getAmount());
		object.addProperty("totFeeAmount", src.getTotFeeAmt());
		object.addProperty("paymentDate", src.getPaymentDate().getTime());
		object.addProperty("tradeDate", src.getTradeDate().getTime());
		object.addProperty("externalReference", src.getExternalReference());
		object.addProperty("liquidation", src.getLiquidation());

		if (src.getSaCode() != null) {
			object.addProperty("sellingAgentThirdPartyCode", src.getSaCode().getThirdPartyCode());
			object.addProperty("sellingAgentThirdPartyName", src.getSaCode().getThirdPartyName());
		} else {
			object.addProperty("sellingAgentThirdPartyCode", "");
			object.addProperty("sellingAgentThirdPartyName", "");
		}

		if (src.getRgProduct() != null) {
			object.addProperty("rgProductCode", src.getRgProduct().getProductCode());
			object.addProperty("rgProductName", src.getRgProduct().getName());
			object.addProperty("rgProductCurrencyCode", src.getRgProduct().getCurrency().getCurrencyCode());
			object.addProperty("rgProductCurrencyDescription", src.getRgProduct().getCurrency().getDescription());

			if (src.getRgProduct().getCfMaster() != null) {
				object.addProperty("rgProductCustomerKey", src.getRgProduct().getCfMaster().getCustomerKey());
			}
		} else {
			object.addProperty("rgProductCode", "");
			object.addProperty("rgProductName", "");
			object.addProperty("rgProductCurrencyCode", "");
			object.addProperty("rgProductCurrencyDescription", "");
			object.addProperty("rgProductCustomerKey", "");
		}

		if (src.getRgInvestmentaccount() != null) {
			object.addProperty("investmentAccountNo", src.getRgInvestmentaccount().getAccountNumber());
			object.addProperty("investmentAccountName", src.getRgInvestmentaccount().getName());
			object.addProperty("investmentCurrencyCode", src.getRgInvestmentaccount().getBankAccount().getCurrency().getCurrencyCode());
			object.addProperty("investmentCurrencptionyDescription", src.getRgInvestmentaccount().getBankAccount().getCurrency().getDescription());
		} else {
			object.addProperty("investmentAccountNo", "");
			object.addProperty("investmentAccountName", "");
			object.addProperty("investmentCurrencyCode", "");
			object.addProperty("investmentCurrencyDescription", "");
		}

		if (src.getRgProductBnAccount() != null) {
			object.addProperty("productBankAccountNo", src.getRgProductBnAccount().getAccountNo());
			object.addProperty("productBankAccountKey", src.getRgProductBnAccount().getBankAccountKey());
			object.addProperty("productBankName", src.getRgProductBnAccount().getName());
			object.addProperty("productBankThirdPartyKey", src.getRgProductBnAccount().getBankCode().getThirdPartyKey());
			object.addProperty("productBankThirdPartyCode", src.getRgProductBnAccount().getBankCode().getThirdPartyCode());
			object.addProperty("productBankThirdPartyName", src.getRgProductBnAccount().getBankCode().getThirdPartyName());
			object.addProperty("productBankCurrencyCode", src.getRgProductBnAccount().getCurrency().getCurrencyCode());
			object.addProperty("productBankCurrencyDescription", src.getRgProductBnAccount().getCurrency().getDescription());

			if (src.getRgProductBnAccount().getCustomer() != null) {
				object.addProperty("productBankCustomerName", src.getRgProductBnAccount().getCustomer().getCustomerName());
			}
		} else {
			object.addProperty("productBankAccountNo", "");
			object.addProperty("productBankAccountKey", "");
			object.addProperty("productBankAccountName", "");
			object.addProperty("productBankThirdPartyKey", "");
			object.addProperty("productBankThirdPartyCode", "");
			object.addProperty("productBankThirdPartyName", "");
			object.addProperty("productBankCurrencyCode", "");
			object.addProperty("productBankCurrencyDescription", "");
			object.addProperty("productBankCustomerName", "");
		}

		if (src.getBankAccount() != null) {
			object.addProperty("investorBankAccountNo", src.getBankAccount().getAccountNo());
			object.addProperty("investorBankAccountKey", src.getBankAccount().getBankAccountKey());
			object.addProperty("investorBankAccountName", src.getBankAccount().getName());
			object.addProperty("investorBankThirdPartyKey", src.getBankAccount().getBankCode().getThirdPartyKey());
			object.addProperty("investorBankThirdPartyCode", src.getBankAccount().getBankCode().getThirdPartyCode());
			object.addProperty("investorBankThirdPartyName", src.getBankAccount().getBankCode().getThirdPartyName());
			object.addProperty("investorBankCurrencyCode", src.getBankAccount().getCurrency().getCurrencyCode());
			object.addProperty("investorBankCurrencyDescription", src.getBankAccount().getCurrency().getDescription());

			if (src.getBankAccount().getCustomer() != null) {
				object.addProperty("investorBankCustomerName", src.getBankAccount().getCustomer().getCustomerName());
			}
		} else {
			object.addProperty("investorBankAccountNo", "");
			object.addProperty("investorBankAccountKey", "");
			object.addProperty("investorBankAccountName", "");
			object.addProperty("investorBankThirdPartyKey", "");
			object.addProperty("investorBankThirdPartyCode", "");
			object.addProperty("investorBankThirdPartyName", "");
			object.addProperty("investorBankCurrencyCode", "");
			object.addProperty("investorBankCurrencyDescription", "");
			object.addProperty("investorBankCustomerName", "");
		}

		if (src.getRgTransaction() != null) {
			object.addProperty("transactionUnit", src.getRgTransaction().getUnit());
			object.addProperty("transactionPrice", src.getRgTransaction().getPrice());
			object.addProperty("transactionAmount", src.getRgTransaction().getAmount());
			object.addProperty("transactionFeeAmount", src.getRgTransaction().getFeeAmount());
			object.addProperty("transactionDiscAmount", src.getRgTransaction().getDiscAmount());
			object.addProperty("transactionOtherAmount", src.getRgTransaction().getOtherAmount());
		} else {
			object.addProperty("transactionUnit", "");
			object.addProperty("transactionPrice", "");
			object.addProperty("transactionAmount", "");
			object.addProperty("transactionFeeAmount", "");
			object.addProperty("transactionDiscAmount", "");
			object.addProperty("transactionOtherAmount", "");
		}

		return object;
	}
}