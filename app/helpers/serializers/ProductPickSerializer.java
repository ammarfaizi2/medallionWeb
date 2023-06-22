package helpers.serializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.simian.medallion.model.AbstractRgSwitching;
import com.simian.medallion.model.RgFeeTier;
import com.simian.medallion.model.RgProduct;

import controllers.MedallionController;

public class ProductPickSerializer extends MedallionController implements JsonSerializer<RgProduct> {
	@Override
	public JsonElement serialize(RgProduct src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("productCode", src.getProductCode());
		object.addProperty("description", src.getName());
		object.addProperty("totalUnit", src.getTotalUnit());

		object.addProperty("subNavUsed", src.getSubNavUsed());
		object.addProperty("subPostPeriod", src.getSubPostPeriod());
		object.addProperty("subCot", src.getSubCot());

		object.addProperty("redNavUsed", src.getRedNavUsed());
		object.addProperty("redPostPeriod", src.getRedPostPeriod());
		object.addProperty("redPayPeriod", src.getRedPayPeriod());
		object.addProperty("redCot", src.getRedCot());

		object.addProperty("fixnav", src.isFixnav());
		object.addProperty("fixNavPrice", src.getFixNavPrice());

		object.addProperty("swiNavUsed", src.getSwiNavUsed());
		object.addProperty("swiPostPeriod", src.getSwiPostPeriod());
		object.addProperty("swiCot", src.getSwiCot());

		object.addProperty("divNavUsed", src.getDivNavUsed());
		object.addProperty("divPostPeriod", src.getDivPostPeriod());
		object.addProperty("divCumPeriod", src.getDivCumPeriod());
		object.addProperty("divPayPeriod", src.getDivPayPeriod());

		object.addProperty("minBalAmt", src.getMinBalAmt());
		object.addProperty("minBalUnit", src.getMinBalUnit());
		// object.addProperty("maxBalAmt", src.getMaxBalAmt());
		// object.addProperty("maxBalUnit", src.getMaxBalUnit());
		// object.addProperty("maxBalAmtPct", src.getMaxBalAmtPct());
		// object.addProperty("maxBalUnitPct", src.getMaxBalUnitPct());
		object.addProperty("redLimitAmt", src.getRedLimitAmt());
		object.addProperty("redLimitPct", src.getRedLimitPct());
		object.addProperty("swoLimitAmt", src.getSwoLimitAmt());
		object.addProperty("swoLimitPct", src.getSwoLimitPct());

		object.addProperty("offerPricePct", src.getOfferPricePct());
		object.addProperty("bidPricePct", src.getBidPricePct());

		object.addProperty("maxPaymentDate", src.getMaxPaymentDate());
		// print out ini jangan dihapus
		if (src.getTransactionDate() != null) {
			object.addProperty("transactionDate", src.getTransactionDate());
		}

		JsonObject thirdPartyByFundManager = new JsonObject();
		thirdPartyByFundManager.addProperty("thirdPartyKey", (src.getThirdPartyByFundManager() == null) ? 0 : src.getThirdPartyByFundManager().getThirdPartyKey());
		thirdPartyByFundManager.addProperty("thirdPartyCode", (src.getThirdPartyByFundManager() == null) ? "" : src.getThirdPartyByFundManager().getThirdPartyCode());
		thirdPartyByFundManager.addProperty("thirdPartyName", (src.getThirdPartyByFundManager() == null) ? "" : src.getThirdPartyByFundManager().getThirdPartyName());
		object.add("thirdPartyByFundManager", thirdPartyByFundManager);

		JsonObject taxMasterBySubTaxKey = new JsonObject();
		taxMasterBySubTaxKey.addProperty("taxRate", (src.getTaxMasterBySubTaxKey() == null) ? 0 : src.getTaxMasterBySubTaxKey().getTaxRate());
		taxMasterBySubTaxKey.addProperty("taxKey", (src.getTaxMasterBySubTaxKey() == null) ? 0 : src.getTaxMasterBySubTaxKey().getTaxKey());
		taxMasterBySubTaxKey.addProperty("description", (src.getTaxMasterBySubTaxKey() == null) ? "" : src.getTaxMasterBySubTaxKey().getDescription());
		taxMasterBySubTaxKey.addProperty("taxCode()", (src.getTaxMasterBySubTaxKey() == null) ? "" : src.getTaxMasterBySubTaxKey().getTaxCode());
		object.add("taxMasterBySubTaxKey", taxMasterBySubTaxKey);

		JsonObject taxMasterByRedTaxKey = new JsonObject();
		taxMasterByRedTaxKey.addProperty("taxRate", (src.getTaxMasterByRedTaxKey() == null) ? 0 : src.getTaxMasterByRedTaxKey().getTaxRate());
		taxMasterByRedTaxKey.addProperty("taxKey", (src.getTaxMasterByRedTaxKey() == null) ? 0 : src.getTaxMasterByRedTaxKey().getTaxKey());
		taxMasterByRedTaxKey.addProperty("description", (src.getTaxMasterByRedTaxKey() == null) ? "" : src.getTaxMasterByRedTaxKey().getDescription());
		taxMasterByRedTaxKey.addProperty("taxCode", (src.getTaxMasterByRedTaxKey() == null) ? "" : src.getTaxMasterByRedTaxKey().getTaxCode());
		object.add("taxMasterByRedTaxKey", taxMasterByRedTaxKey);

		JsonObject taxMasterBySwiTaxKey = new JsonObject();
		taxMasterBySwiTaxKey.addProperty("taxRate", (src.getTaxMasterBySwiTaxKey() == null) ? 0 : src.getTaxMasterBySwiTaxKey().getTaxRate());
		taxMasterBySwiTaxKey.addProperty("taxKey", (src.getTaxMasterBySwiTaxKey() == null) ? 0 : src.getTaxMasterBySwiTaxKey().getTaxKey());
		taxMasterBySwiTaxKey.addProperty("description", (src.getTaxMasterBySwiTaxKey() == null) ? "" : src.getTaxMasterBySwiTaxKey().getDescription());
		taxMasterBySwiTaxKey.addProperty("taxCode", (src.getTaxMasterBySwiTaxKey() == null) ? "" : src.getTaxMasterBySwiTaxKey().getTaxCode());
		object.add("taxMasterBySwiTaxKey", taxMasterBySwiTaxKey);

		JsonObject taxMasterByDivTaxKey = new JsonObject();
		taxMasterByDivTaxKey.addProperty("taxRate", (src.getTaxMasterByDivTaxKey() == null) ? 0 : src.getTaxMasterByDivTaxKey().getTaxRate());
		taxMasterByDivTaxKey.addProperty("taxKey", (src.getTaxMasterByDivTaxKey() == null) ? 0 : src.getTaxMasterByDivTaxKey().getTaxKey());
		taxMasterByDivTaxKey.addProperty("description", (src.getTaxMasterByDivTaxKey() == null) ? "" : src.getTaxMasterByDivTaxKey().getDescription());
		taxMasterByDivTaxKey.addProperty("taxCode()", (src.getTaxMasterByDivTaxKey() == null) ? "" : src.getTaxMasterByDivTaxKey().getTaxCode());
		object.add("taxMasterByDivTaxKey", taxMasterByDivTaxKey);

		// object.addProperty("subTaxRate",
		// src.getTaxMasterBySubTaxKey().getTaxRate());
		// object.addProperty("redTaxRate",
		// src.getTaxMasterByRedTaxKey().getTaxRate());
		// object.addProperty("swiTaxRate",
		// src.getTaxMasterBySwiTaxKey().getTaxRate());
		// object.addProperty("divTaxRate",
		// src.getTaxMasterByDivTaxKey().getTaxRate());

		object.addProperty("amountRoundType", src.getAmountRoundType());
		object.addProperty("amountRoundValue", src.getAmountRoundValue());
		object.addProperty("unitRoundType", src.getUnitRoundType());
		object.addProperty("unitRoundValue", src.getUnitRoundValue());
		object.addProperty("priceRoundType", src.getPriceRoundType());
		object.addProperty("priceRoundValue", src.getPriceRoundValue());

		JsonObject lookupBySubTierBy = new JsonObject();
		if (src.getLookupBySubTierBy() != null) {
			lookupBySubTierBy.addProperty("lookupId", src.getLookupBySubTierBy().getLookupId());
		}
		object.add("lookupBySubTierBy", lookupBySubTierBy);

		JsonObject lookupByRedTierBy = new JsonObject();
		if (src.getLookupByRedTierBy() != null) {
			lookupByRedTierBy.addProperty("lookupId", src.getLookupByRedTierBy().getLookupId());
		}
		object.add("lookupByRedTierBy", lookupByRedTierBy);

		JsonObject lookupBySwiTierBy = new JsonObject();
		if (src.getLookupBySwiTierBy() != null) {
			lookupBySwiTierBy.addProperty("lookupId", src.getLookupBySwiTierBy().getLookupId());
			object.add("lookupBySwiTierBy", lookupBySwiTierBy);
		}

		// JsonObject lookupByDivTierBy = new JsonObject();
		// lookupByDivTierBy.addProperty("lookupId",
		// src.getLookupByDivTierBy().getLookupId());
		// object.add("lookupByDivTierBy", lookupByDivTierBy);

		JsonObject lookupBySubTierType = new JsonObject();
		if (src.getLookupBySubTierType() != null) {
			lookupBySubTierType.addProperty("lookupId", src.getLookupBySubTierType().getLookupId());
		}
		object.add("lookupBySubTierType", lookupBySubTierType);

		JsonObject lookupByRedTierType = new JsonObject();
		if (src.getLookupByRedTierType() != null) {
			lookupByRedTierType.addProperty("lookupId", src.getLookupByRedTierType().getLookupId());
		}
		object.add("lookupByRedTierType", lookupByRedTierType);

		JsonObject lookupBySwiTierType = new JsonObject();
		if (src.getLookupBySwiTierType() != null) {
			lookupBySwiTierType.addProperty("lookupId", src.getLookupBySwiTierType().getLookupId());
		}
		object.add("lookupBySwiTierType", lookupBySwiTierType);
		
		JsonObject swiIntfTrfCharge = new JsonObject();
		if (src.getSwiIntfTrfCharge()!=null){
			swiIntfTrfCharge.addProperty("lookupId", src.getSwiIntfTrfCharge().getLookupId());
		object.add("swiIntfTrfCharge", swiIntfTrfCharge);
		}
		
		JsonObject swiIntfAccessAccount = new JsonObject();
		if (src.getSwiIntfAccessAccount()!=null){
			swiIntfAccessAccount.addProperty("lookupId", src.getSwiIntfAccessAccount().getLookupId());
		object.add("swiIntfAccessAccount", swiIntfAccessAccount);
		}
		
		JsonObject swiIntfAmountType = new JsonObject();
		if (src.getSwiIntfAmountType()!=null){
			swiIntfAmountType.addProperty("lookupId", src.getSwiIntfAmountType().getLookupId());
		object.add("swiIntfAmountType", swiIntfAmountType);
		}

		// JsonObject lookupByDivTierType = new JsonObject();
		// lookupByDivTierType.addProperty("lookupId",
		// src.getLookupByDivTierType().getLookupId());
		// object.add("lookupByDivTierType", lookupByDivTierType);

		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
		if (src.getEffectiveDate() != null) {
			// object.addProperty("effectiveDate",
			// src.getEffectiveDate().getTime());
			object.addProperty("effectiveDate", dateFormat.format(src.getEffectiveDate()));
			String effectiveDateForNav = dateFormat.format(src.getEffectiveDate());
			object.addProperty("effectiveDateForNav", effectiveDateForNav);
		}
		if (src.getLiquidDate() != null) {
			// object.addProperty("liquidDate", src.getLiquidDate().getTime());
			object.addProperty("liquidDate", dateFormat.format(src.getLiquidDate()));
			String liquidDateForNav = dateFormat.format(src.getLiquidDate());
			object.addProperty("liquidDateForNav", liquidDateForNav);

			if (src.getRedNavUsed() != null) {
				int longNavDate = src.getLiquidDate().getDate() + src.getRedNavUsed();
				Date navDateForMaturity = new Date();
				navDateForMaturity.setDate(longNavDate);
				object.addProperty("navDateForMaturity", navDateForMaturity.getTime());
			}

			if (src.getRedPostPeriod() != null) {
				int longPostDate = src.getLiquidDate().getDate() + src.getRedPostPeriod();
				Date postDateForMaturity = new Date();
				postDateForMaturity.setDate(longPostDate);
				object.addProperty("postDateForMaturity", postDateForMaturity.getTime());
			}

			if (src.getRedPayPeriod() != null) {
				int longPaymentDate = src.getLiquidDate().getDate() + src.getRedPayPeriod();
				Date paymentDateForMaturity = new Date();
				paymentDateForMaturity.setDate(longPaymentDate);
				object.addProperty("paymentDateForMaturity", paymentDateForMaturity.getTime());
			}

		}

		// Redemption
		JsonObject bankAccount = new JsonObject();
		bankAccount.addProperty("bankAccountKey", (src.getBankAccount() == null) ? "" : src.getBankAccount().getBankAccountKey().toString());
		bankAccount.addProperty("accountNo", (src.getBankAccount() == null) ? "" : src.getBankAccount().getAccountNo());
		bankAccount.addProperty("name", (src.getBankAccount() == null) ? "" : src.getBankAccount().getName());
		object.add("bankAccount", bankAccount);

		JsonObject bankCode = new JsonObject();
		bankCode.addProperty("thirdPartyCode", (src.getBankAccount() == null || src.getBankAccount().getBankCode() == null) ? "" : src.getBankAccount().getBankCode().getThirdPartyCode());
		bankCode.addProperty("thirdPartyKey", (src.getBankAccount() == null || src.getBankAccount().getBankCode() == null) ? 0 : src.getBankAccount().getBankCode().getThirdPartyKey());
		bankCode.addProperty("thirdPartyName", (src.getBankAccount() == null || src.getBankAccount().getBankCode() == null) ? "" : src.getBankAccount().getBankCode().getThirdPartyName());
		bankAccount.add("bankCode", bankCode);

		JsonObject currency = new JsonObject();
		currency.addProperty("currencyCode", (src.getBankAccount() == null || src.getBankAccount().getCurrency() == null) ? "" : src.getBankAccount().getCurrency().getCurrencyCode());
		bankAccount.add("currency", currency);

		// Subscribe
		JsonObject bankAccountSub = new JsonObject();
		bankAccountSub.addProperty("bankAccountKey", (src.getBankAccountSub() == null) ? "" : src.getBankAccountSub().getBankAccountKey().toString());
		bankAccountSub.addProperty("accountNo", (src.getBankAccountSub() == null) ? "" : src.getBankAccountSub().getAccountNo());
		bankAccountSub.addProperty("name", (src.getBankAccountSub() == null) ? "" : src.getBankAccountSub().getName());
		object.add("bankAccountSub", bankAccountSub);

		JsonObject bankCodeSub = new JsonObject();
		bankCodeSub.addProperty("thirdPartyCode", (src.getBankAccountSub() == null || src.getBankAccountSub().getBankCode() == null) ? "" : src.getBankAccountSub().getBankCode().getThirdPartyCode());
		bankCodeSub.addProperty("thirdPartyKey", (src.getBankAccountSub() == null || src.getBankAccountSub().getBankCode() == null) ? 0 : src.getBankAccountSub().getBankCode().getThirdPartyKey());
		bankCodeSub.addProperty("thirdPartyName", (src.getBankAccountSub() == null || src.getBankAccountSub().getBankCode() == null) ? "" : src.getBankAccountSub().getBankCode().getThirdPartyName());
		bankAccountSub.add("bankCode", bankCodeSub);

		JsonObject currencySub = new JsonObject();
		currencySub.addProperty("currencyCode", (src.getBankAccountSub() == null || src.getBankAccountSub().getCurrency() == null) ? "" : src.getBankAccountSub().getCurrency().getCurrencyCode());
		bankAccountSub.add("currency", currency);

		if (src.getMaxInvestor() != null) {
			object.addProperty("maxInvestor", src.getMaxInvestor());
		} else {
			object.addProperty("maxInvestor", "");
		}

		if (src.getMinNavAmt() != null) {
			object.addProperty("minNavAmt", src.getMinNavAmt());
		} else {
			object.addProperty("minNavAmt", "");
		}

		if (src.getDivLock() != null) {
			object.addProperty("divLock", src.getDivLock());
		} else {
			object.addProperty("divLock", "");
		}

		if (src.getSubLock() != null) {
			object.addProperty("subLock", src.getSubLock());
		} else {
			object.addProperty("subLock", "");
		}

		if (src.getRedLock() != null) {
			object.addProperty("redLock", src.getRedLock());
		} else {
			object.addProperty("redLock", "");
		}

		if (src.getDivByCash() != null) {
			object.addProperty("divByCash", src.getDivByCash());
		} else {
			object.addProperty("divByCash", "");
		}

		if (src.getDivByReinvest() != null) {
			object.addProperty("divByReinvest", src.getDivByReinvest());
		} else {
			object.addProperty("divByReinvest", "");
		}

		if (src.getDivByRedeem() != null) {
			object.addProperty("divByRedeem", src.getDivByRedeem());
		} else {
			object.addProperty("divByRedeem", "");
		}

		if (src.getDivInvestorOpt() != null) {
			object.addProperty("divInvestorOpt", src.getDivInvestorOpt());
		} else {
			object.addProperty("divInvestorOpt", "");
		}

		if (src.getDivIopByCashPct() != null) {
			object.addProperty("divIopByCashPct", src.getDivIopByCashPct());
		} else {
			object.addProperty("divIopByCashPct", "");
		}

		if (src.getDivIopByReinvestmentPct() != null) {
			object.addProperty("divIopByReinvestmentPct", src.getDivIopByReinvestmentPct());
		} else {
			object.addProperty("divIopByReinvestmentPct", "");
		}

		if (src.getDivIopByRedeemPct() != null) {
			object.addProperty("divIopByRedeemPct", src.getDivIopByRedeemPct());
		} else {
			object.addProperty("divIopByRedeemPct", "");
		}

		if (src.getSubInitMinAmt() != null) {
			object.addProperty("subInitMinAmt", src.getSubInitMinAmt());
		} else {
			object.addProperty("subInitMinAmt", "");
		}

		if (src.getRedMaxFee() != null) {
			object.addProperty("redMaxFee", src.getRedMaxFee());
		} else {
			object.addProperty("redMaxFee", "");
		}

		if (src.getSubMaxFee() != null) {
			object.addProperty("subMaxFee", src.getSubMaxFee());
		} else {
			object.addProperty("subMaxFee", "");
		}

		if (src.getRgFeeTiers().size() > 0) {
			Set<RgFeeTier> rgFeeTiers = src.getRgFeeTiers();
			for (RgFeeTier feeTier : rgFeeTiers) {
				if (feeTier.getId().getType().equalsIgnoreCase(AbstractRgSwitching.TYPE_SWITCHING)) {
					object.addProperty("swiDefaultPct", feeTier.getValue());
				}
			}
		}

		object.addProperty("currencyCode", src.getCurrency().getCurrencyCode());

		return object;
	}
}
