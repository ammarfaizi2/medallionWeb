package helpers.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.RgInvestmentaccount;

public class InvestmentAccountPickSerializer implements JsonSerializer<RgInvestmentaccount> {
	@Override
	public JsonElement serialize(RgInvestmentaccount src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject investmentAccount = new JsonObject();
		investmentAccount.addProperty("accountNumber", src.getAccountNumber());
		investmentAccount.addProperty("code", src.getAccountNumber());
		investmentAccount.addProperty("description", src.getName());
		investmentAccount.addProperty("name", src.getName());
		investmentAccount.addProperty("acountNumber", src.getAccountNumber());
		investmentAccount.addProperty("fmgrAcc", src.getFmgrAcc());
		investmentAccount.addProperty("firstTxn", src.getIsFirstTxn());

		JsonObject rgProduct = new JsonObject();
		rgProduct.addProperty("productCode", (src.getRgProduct() == null) ? "" : src.getRgProduct().getProductCode());
		rgProduct.addProperty("name", (src.getRgProduct() == null) ? "" : src.getRgProduct().getName());
		if (src.getRgProduct().getSwiNavUsed() != null) {
			rgProduct.addProperty("swiNavUsed", (src.getRgProduct() == null) ? 0 : src.getRgProduct().getSwiNavUsed());
		}
		if (src.getRgProduct().getSwiPostPeriod() != null) {
			rgProduct.addProperty("swiPostPeriod", (src.getRgProduct() == null) ? 0 : src.getRgProduct().getSwiPostPeriod());
		}

		if (src.getRgProduct().getSwiCot() != null) {
			rgProduct.addProperty("swiCot", (src.getRgProduct() == null) ? 0 : src.getRgProduct().getSwiCot());
		}
		rgProduct.addProperty("amountRoundValue", (src.getRgProduct().getAmountRoundValue() == null) ? 0 : src.getRgProduct().getAmountRoundValue());
		rgProduct.addProperty("amountRoundType", (src.getRgProduct().getAmountRoundType() == null) ? null : src.getRgProduct().getAmountRoundType());
		rgProduct.addProperty("unitRoundValue", (src.getRgProduct().getUnitRoundValue() == null) ? 0 : src.getRgProduct().getUnitRoundValue());
		rgProduct.addProperty("unitRoundType", (src.getRgProduct().getUnitRoundType() == null) ? null : src.getRgProduct().getUnitRoundType());
		rgProduct.addProperty("priceRoundValue", (src.getRgProduct().getPriceRoundValue() == null) ? 0 : src.getRgProduct().getPriceRoundValue());
		rgProduct.addProperty("priceRoundType", (src.getRgProduct().getPriceRoundType() == null) ? null : src.getRgProduct().getPriceRoundType());

		JsonObject thirdPartyByFundManager = new JsonObject();
		thirdPartyByFundManager.addProperty("thirdPartyKey", (src.getRgProduct().getThirdPartyByFundManager() == null) ? 0 : src.getRgProduct().getThirdPartyByFundManager().getThirdPartyKey());
		thirdPartyByFundManager.addProperty("thirdPartyCode", (src.getRgProduct().getThirdPartyByFundManager() == null) ? "" : src.getRgProduct().getThirdPartyByFundManager().getThirdPartyCode());
		thirdPartyByFundManager.addProperty("thirdPartyName", (src.getRgProduct().getThirdPartyByFundManager() == null) ? "" : src.getRgProduct().getThirdPartyByFundManager().getThirdPartyName());
		rgProduct.add("thirdPartyByFundManager", thirdPartyByFundManager);

		JsonObject currency = new JsonObject();
		currency.addProperty("currencyCode", (src.getRgProduct().getCurrency() == null) ? "" : src.getRgProduct().getCurrency().getCurrencyCode());
		currency.addProperty("description", (src.getRgProduct().getCurrency() == null) ? "" : src.getRgProduct().getCurrency().getDescription());
		rgProduct.add("currency", currency);

		JsonObject lookupBySubTierType = new JsonObject();
		lookupBySubTierType.addProperty("lookupId", (src.getRgProduct() == null) ? null : src.getRgProduct().getLookupBySubTierType().getLookupId());
		rgProduct.add("lookupBySubTierType", lookupBySubTierType);

		JsonObject lookupBySwiTierType = new JsonObject();
		if (src.getRgProduct().getLookupBySwiTierType() != null) {
			lookupBySwiTierType.addProperty("lookupId", (src.getRgProduct() == null) ? null : src.getRgProduct().getLookupBySwiTierType().getLookupId());
		}
		rgProduct.add("lookupBySwiTierType", lookupBySwiTierType);

		JsonObject taxMasterByRedTaxKey = new JsonObject();
		taxMasterByRedTaxKey.addProperty("taxRate", (src.getRgProduct().getTaxMasterByRedTaxKey() == null) ? 0 : src.getRgProduct().getTaxMasterByRedTaxKey().getTaxRate());
		taxMasterByRedTaxKey.addProperty("taxKey", (src.getRgProduct().getTaxMasterByRedTaxKey() == null) ? 0 : src.getRgProduct().getTaxMasterByRedTaxKey().getTaxKey());
		taxMasterByRedTaxKey.addProperty("description", (src.getRgProduct().getTaxMasterByRedTaxKey() == null) ? "" : src.getRgProduct().getTaxMasterByRedTaxKey().getDescription());
		taxMasterByRedTaxKey.addProperty("taxCode", (src.getRgProduct().getTaxMasterByRedTaxKey() == null) ? "" : src.getRgProduct().getTaxMasterByRedTaxKey().getTaxCode());
		rgProduct.add("taxMasterByRedTaxKey", taxMasterByRedTaxKey);

		JsonObject taxMasterBySubTaxKey = new JsonObject();
		taxMasterBySubTaxKey.addProperty("taxRate", (src.getRgProduct().getTaxMasterBySubTaxKey() == null) ? 0 : src.getRgProduct().getTaxMasterBySubTaxKey().getTaxRate());
		taxMasterBySubTaxKey.addProperty("taxKey", (src.getRgProduct().getTaxMasterBySubTaxKey() == null) ? 0 : src.getRgProduct().getTaxMasterBySubTaxKey().getTaxKey());
		taxMasterBySubTaxKey.addProperty("description", (src.getRgProduct().getTaxMasterBySubTaxKey() == null) ? "" : src.getRgProduct().getTaxMasterBySubTaxKey().getDescription());
		taxMasterBySubTaxKey.addProperty("taxCode", (src.getRgProduct().getTaxMasterBySubTaxKey() == null) ? "" : src.getRgProduct().getTaxMasterBySubTaxKey().getTaxCode());
		rgProduct.add("taxMasterBySubTaxKey", taxMasterBySubTaxKey);
		investmentAccount.add("rgProduct", rgProduct);

		JsonObject thirdPartyBySaCode = new JsonObject();
		thirdPartyBySaCode.addProperty("thirdPartyCode", (src.getThirdPartyBySaCode() == null) ? "" : src.getThirdPartyBySaCode().getThirdPartyCode());
		thirdPartyBySaCode.addProperty("thirdPartyKey", (src.getThirdPartyBySaCode() == null) ? 0 : src.getThirdPartyBySaCode().getThirdPartyKey());
		thirdPartyBySaCode.addProperty("thirdPartyName", (src.getThirdPartyBySaCode() == null) ? "" : src.getThirdPartyBySaCode().getThirdPartyName());
		thirdPartyBySaCode.addProperty("custodianBank", (src.getThirdPartyBySaCode() == null) ? "" : src.getThirdPartyBySaCode().getCustodianBank());
		investmentAccount.add("thirdPartyBySaCode", thirdPartyBySaCode);

		JsonObject thirdPartyByTrnSABranch = new JsonObject();
		// thirdPartyByTrnSABranch.addProperty("thirdPartyCode",
		// (src.getOpeningSABranch() == null) ? "" :
		// src.getOpeningSABranch().getThirdPartyCode());
		// thirdPartyByTrnSABranch.addProperty("thirdPartyKey",
		// (src.getOpeningSABranch() == null) ? 0 :
		// src.getOpeningSABranch().getThirdPartyKey());
		// thirdPartyByTrnSABranch.addProperty("thirdPartyName",
		// (src.getOpeningSABranch() == null) ? "" :
		// src.getOpeningSABranch().getThirdPartyName());
		thirdPartyByTrnSABranch.addProperty("trnSABranchKey", (src.getOpeningSABranchKey() == null) ? 0 : src.getOpeningSABranchKey());
		investmentAccount.add("thirdPartyByTrnSABranch", thirdPartyByTrnSABranch);

		JsonObject bankAccount = new JsonObject();
		bankAccount.addProperty("bankAccountKey", (src.getBankAccount() == null) ? "" : src.getBankAccount().getBankAccountKey().toString());
		bankAccount.addProperty("accountNo", (src.getBankAccount() == null) ? "" : src.getBankAccount().getAccountNo());
		bankAccount.addProperty("name", (src.getBankAccount() == null) ? "" : src.getBankAccount().getName());
		investmentAccount.add("bankAccount", bankAccount);

		JsonObject bankCode = new JsonObject();
		bankCode.addProperty("thirdPartyCode", (src.getBankAccount() == null || src.getBankAccount().getBankCode() == null) ? "" : src.getBankAccount().getBankCode().getThirdPartyCode());
		bankCode.addProperty("thirdPartyName", (src.getBankAccount() == null || src.getBankAccount().getBankCode() == null) ? "" : src.getBankAccount().getBankCode().getThirdPartyName());
		bankAccount.add("bankCode", bankCode);

		currency = new JsonObject();
		currency.addProperty("currencyCode", (src.getBankAccount() == null || src.getBankAccount().getCurrency() == null) ? "" : src.getBankAccount().getCurrency().getCurrencyCode());
		bankAccount.add("currency", currency);

		JsonObject customer = new JsonObject();
		customer.addProperty("customerKey", src.getCustomer().getCustomerKey());
		investmentAccount.add("customer", customer);
		return investmentAccount;
	}
}
