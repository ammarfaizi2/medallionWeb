package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.Tools;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.RgFeeTier;
import com.simian.medallion.model.RgNavMinimum;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgProductBnAccount;
import com.simian.medallion.model.RgProductLockException;
import com.simian.medallion.model.RgSaBnAccount;
import com.simian.medallion.vo.SelectItem;

public class RegistryProduct extends Registry {
	private static Logger log = Logger.getLogger(RegistryProduct.class);

	public static final String SPECIAL_CHAR_PAGAR = ",,";
	public static final String SPECIAL_CHAR_QUOTE = ",,";

	@Before(only = { "edit", "view", "entry", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("limitrules", UIHelper.limitRules());
		renderArgs.put("operators", UIHelper.yesNoOperators());
		// renderArgs.put("roundingType", UIHelper.roundingOperators());
		renderArgs.put("taxs", generalService.listTaxMastersAsSelectItem());
		renderArgs.put("tierby", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TIER_COMPARISON));
		renderArgs.put("tiertype", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TIER_COMPARISON_TYPE));

		List<SelectItem> roundingType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ROUNDING_TYPE);
		renderArgs.put("roundingType", roundingType);

		final GnLookup lookupAmount = generalService.getLookup(LookupConstants._TIER_COMPARISON_AMOUNT);
		renderArgs.put("tierbyamount", new ArrayList<SelectItem>() {
			{
				add(new SelectItem(lookupAmount.getLookupId(), lookupAmount.getLookupDescription()));
			}
		});

		final GnLookup lookupPeriod = generalService.getLookup(LookupConstants._TIER_COMPARISON_PERIOD);
		renderArgs.put("tierbyperiod", new ArrayList<SelectItem>() {
			{
				add(new SelectItem(lookupPeriod.getLookupId(), lookupPeriod.getLookupDescription()));
			}
		});

		final GnLookup lookupFlat = generalService.getLookup(LookupConstants._TIER_COMPARISON_TYPE_FLAT);
		renderArgs.put("tiertypeflat", new ArrayList<SelectItem>() {
			{
				add(new SelectItem(lookupFlat.getLookupId(), lookupFlat.getLookupDescription()));
			}
		});
		List<SelectItem> paymentType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PAYMENT_TYPE);
		renderArgs.put("paymentType", paymentType);

		List<SelectItem> targetAccount = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TARGET_ACCOUNT);
		renderArgs.put("targetAccount", targetAccount);

		GnLookup targetAcct1 = generalService.getLookup(LookupConstants._TARGET_ACCOUNT_1);
		renderArgs.put("targetAcct1", targetAcct1);

		GnLookup targetAcct2 = generalService.getLookup(LookupConstants._TARGET_ACCOUNT_2);
		renderArgs.put("targetAcct2", targetAcct2);
		
		GnLookup targetAcctManual = generalService.getLookup(LookupConstants._TARGET_ACCOUNT_MANUAL);
		renderArgs.put("targetAcctManual", targetAcctManual);

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		renderArgs.put("specialCharPagar", SPECIAL_CHAR_PAGAR);
		renderArgs.put("specialCharQuote", SPECIAL_CHAR_QUOTE);

		renderArgs.put("domainBankSub", LookupConstants._DOMAIN_BANK_SUB);
		renderArgs.put("domainBankRed", LookupConstants._DOMAIN_BANK_RED);

		renderArgs.put("maturityDateMax", "31/12/9999");
		
		renderArgs.put("defaultbankcode", getValueDefaultBankCode());
		
		renderArgs.put("fullAccess", LookupConstants._ACCESS_ACCOUNT_FULL_ACCESS);
		renderArgs.put("chargeOnsender", LookupConstants._CHARGE_PAYMENT_SENDER);
		
		List<SelectItem> transferCharge = generalService.listLookupsForDropDownAsSelectItem2(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_PAYMENT);
		renderArgs.put("transferCharge", transferCharge);
		
		List<SelectItem> accessAccount = generalService.listLookupsForDropDownAsSelectItem2(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCESS_ACCOUNT);
		renderArgs.put("accessAccount", accessAccount);
		
		List<SelectItem> amountType = generalService.listLookupsForDropDownAsSelectItem2(UIConstants.SIMIAN_BANK_ID, LookupConstants._AMOUNT_TYPE);
		renderArgs.put("amountType", amountType);
	}
	
	private static String getValueDefaultBankCode() {
		GnSystemParameter systemParameter = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_ORG);
		GnThirdParty bankThirdParty = generalService.getThirdParty(Long.parseLong(systemParameter.getValue()));
		return bankThirdParty.getThirdPartyCode();
	}

	@Check("administration.registryProduct")
	public static void list() {
		log.debug("list. ");

		// List<RgProduct> prds = taService.listProduct();
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
		// render("RegistryProduct/list.html", prds);
		renderArgs.put("specialCharPagar", SPECIAL_CHAR_PAGAR);
		renderArgs.put("specialCharQuote", SPECIAL_CHAR_QUOTE);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
		render("RegistryProduct/list.html");
	}

	@Check("administration.registryProduct")
	public static void edit(String id, boolean isNewRec) {
		log.debug("edit. id: " + id + " isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		isNewRec = false;

		if (id.contains(SPECIAL_CHAR_PAGAR))
			id = id.replaceAll(SPECIAL_CHAR_PAGAR, "#");

		RgProduct prd = taService.loadProduct(id);

		String status = prd.getRecordStatus();
		log.debug("status >>" + status + "-");
		prd.splitProductFees();
		prd.splitProductLockExceptions();
		// prd.putToListRgNavMin();
		prd.putToListRgProdBnAccount();

		log.debug("[EDIT] size rgProdLockExce = " + prd.getRgProductLockExceptions().size());
		log.debug("[EDIT] size rgFeeTiers = " + prd.getRgFeeTiers().size());
		log.debug("[EDIT] size of subFees = " + prd.getSubFees().size());
		log.debug("[EDIT] size of bankAccounts = " + prd.getBankAccounts().size());
		log.debug("[EDIT] size of rg nav min = " + prd.getRgNavMinimums().size());

		String productCode = prd.getProductCode();
		if (productCode != null && prd.getProductCode().contains("\'"))
			productCode = productCode.replaceAll("\'", SPECIAL_CHAR_QUOTE);
		prd.setProductCode(productCode);

		List<RgProductBnAccount> tmpSub = new ArrayList<RgProductBnAccount>();
		List<RgProductBnAccount> tmpRed = new ArrayList<RgProductBnAccount>();
		for (RgProductBnAccount ba : prd.getBankAccounts()) {
			if (ba != null && ba.getId().getDomain() != null) {
				BnAccount bnAccount = bankAccountService.getBankAccount(ba.getId().getBankAccountKey());
				ba.setBnAccount(bnAccount);

				GnLookup domain = generalService.getLookup(ba.getId().getDomain());
				ba.setDomain(domain);

				if (LookupConstants._DOMAIN_BANK_SUB.equals(ba.getId().getDomain())) {
					String constantdefaultRgProdBankAccount = ba.getBnAccount().getBankCode().getThirdPartyKey().toString() + ba.getBnAccount().getAccountNo();
					ba.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));

					String constantdefaultProdBankAccount = prd.getBankAccountSub().getBankCode().getThirdPartyKey().toString() + prd.getBankAccountSub().getAccountNo();
					ba.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));

					if (constantdefaultRgProdBankAccount.equals(constantdefaultProdBankAccount)) {
						prd.setDefaultProductBankAccountSub(ba.getDefaultRgProductBankAccount());
					}
					tmpSub.add(ba);
				} else {
					String constantdefaultRgProdBankAccount = ba.getBnAccount().getBankCode().getThirdPartyKey().toString() + ba.getBnAccount().getAccountNo();
					ba.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));

					String constantdefaultProdBankAccount = prd.getBankAccount().getBankCode().getThirdPartyKey().toString() + prd.getBankAccount().getAccountNo();
					ba.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));

					if (constantdefaultRgProdBankAccount.equals(constantdefaultProdBankAccount)) {
						prd.setDefaultProductBankAccountRed(ba.getDefaultRgProductBankAccount());
					}
					tmpRed.add(ba);
				}
			}
		}

		if (prd.getSaBnAccounts() != null) {
			List<RgSaBnAccount> rgSaBnAccList = new ArrayList<RgSaBnAccount>();
			rgSaBnAccList.addAll(prd.getSaBnAccounts());
			prd.getSaBnAccounts().clear();
			for (RgSaBnAccount rgSaBnAcc : rgSaBnAccList) {
				RgSaBnAccount dbSaBnAcc = taService.loadRgSaBnAccount(rgSaBnAcc.getBankAccountKey());
				prd.getSaBnAccounts().add(dbSaBnAcc);
			}
		}
		
		if(prd.getSwiIntfTrfCharge() == null || (prd.getSwiIntfTrfCharge() != null && prd.getSwiIntfTrfCharge().getLookupId() == null) ){
			prd.setSwiIntfTrfCharge(new GnLookup(LookupConstants._CHARGE_PAYMENT_SENDER));
		}
		
		if(prd.getSwiIntfAmountType() == null || (prd.getSwiIntfAmountType() != null && prd.getSwiIntfAmountType().getLookupId() == null) ){//set defautlt jika null
			prd.setSwiIntfAmountType(new GnLookup(LookupConstants._AMOUNT_TYPE_TRANSACTION));
		}

		String navMins = null;
		String bankAccountsSub = null;
		String bankAccountsRed = null;
		String saBnAccounts = null;

		try {
			navMins = json.writeValueAsString(prd.getRgNavMinimums());
			bankAccountsSub = json.writeValueAsString(tmpSub);
			bankAccountsRed = json.writeValueAsString(tmpRed);
			saBnAccounts = json.writeValueAsString(prd.getSaBnAccounts());
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
		}

		if (prd.getEreportCode() != null)
			prd.setCheckGenerateToAria(true);

		log.debug("[EDIT] DEFAULTBANKSUB => " + prd.getDefaultProductBankAccountSub());
		log.debug("[EDIT] DEFAULTBANKRED => " + prd.getDefaultProductBankAccountRed());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
		render("RegistryProduct/detail.html", prd, mode, isNewRec, status, navMins, bankAccountsSub, bankAccountsRed, saBnAccounts);
	}

	@Check("administration.registryProduct")
	public static void view(String id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;

		if (id.contains(SPECIAL_CHAR_PAGAR))
			id = id.replaceAll(SPECIAL_CHAR_PAGAR, "#");

		RgProduct prd = taService.loadProduct(id);

		String productCode = prd.getProductCode();
		if (productCode != null && prd.getProductCode().contains("\'"))
			productCode = productCode.replaceAll("\'", SPECIAL_CHAR_QUOTE);
		prd.setProductCode(productCode);

		prd.splitProductFees();
		prd.splitProductLockExceptions();
		// prd.putToListRgNavMin();
		prd.putToListRgProdBnAccount();

		List<RgProductBnAccount> tmpSub = new ArrayList<RgProductBnAccount>();
		List<RgProductBnAccount> tmpRed = new ArrayList<RgProductBnAccount>();
		if (prd.getBankAccounts() != null) {
			for (RgProductBnAccount rgProductBnAccount : prd.getBankAccounts()) {
				BnAccount bnAccount = bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey());
				GnLookup domain = generalService.getLookup(rgProductBnAccount.getId().getDomain());
				rgProductBnAccount.setDomain(domain);

				if (LookupConstants._DOMAIN_BANK_SUB.equals(rgProductBnAccount.getId().getDomain())) {
					String constantdefaultRgProductBankAccount = bnAccount.getBankCode().getThirdPartyKey().toString() + bnAccount.getAccountNo();
					rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProductBankAccount.replace(" ", "_"));
					log.debug("default bank sub >>> " + rgProductBnAccount.getDefaultRgProductBankAccount());
					if (prd.getBankAccountSub() != null) {
						if (rgProductBnAccount.getId().getBankAccountKey().toString().equals(prd.getBankAccountSub().getBankAccountKey().toString())) {
							prd.setDefaultProductBankAccountSub(rgProductBnAccount.getDefaultRgProductBankAccount());
						}
					}
					rgProductBnAccount.setBnAccount(bnAccount);
					rgProductBnAccount.setRgProduct(null);
					tmpSub.add(rgProductBnAccount);
				} else {
					String constantdefaultRgProductBankAccount = bnAccount.getBankCode().getThirdPartyKey().toString() + bnAccount.getAccountNo();
					rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProductBankAccount.replace(" ", "_"));
					log.debug("default bank red >>> " + rgProductBnAccount.getDefaultRgProductBankAccount());
					if (prd.getBankAccount() != null) {
						if (rgProductBnAccount.getId().getBankAccountKey().toString().equals(prd.getBankAccount().getBankAccountKey().toString())) {
							prd.setDefaultProductBankAccountRed(rgProductBnAccount.getDefaultRgProductBankAccount());
						}
					}
					rgProductBnAccount.setBnAccount(bnAccount);
					rgProductBnAccount.setRgProduct(null);
					tmpRed.add(rgProductBnAccount);
				}
			}
		}

		if (prd.getSaBnAccounts() != null) {
			List<RgSaBnAccount> rgSaBnAccList = new ArrayList<RgSaBnAccount>();
			rgSaBnAccList.addAll(prd.getSaBnAccounts());
			prd.getSaBnAccounts().clear();
			for (RgSaBnAccount rgSaBnAcc : rgSaBnAccList) {
				RgSaBnAccount dbSaBnAcc = taService.loadRgSaBnAccount(rgSaBnAcc.getBankAccountKey());
				prd.getSaBnAccounts().add(dbSaBnAcc);
			}
		}
		String navMins = null;
		String bankAccountsSub = null;
		String bankAccountsRed = null;
		String saBnAccounts = null;

		try {
			navMins = json.writeValueAsString(prd.getRgNavMinimums());
			bankAccountsSub = json.writeValueAsString(tmpSub);
			bankAccountsRed = json.writeValueAsString(tmpRed);
			saBnAccounts = json.writeValueAsString(prd.getSaBnAccounts());

			log.debug(">>> NAV MINIMUMS =>>> " + navMins);
			log.debug(">>> BANK ACCOUNTS SUB =>>> " + bankAccountsSub);
			log.debug(">>> BANK ACCOUNTS RED =>>> " + bankAccountsRed);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		if (prd.getEreportCode() != null)
			prd.setCheckGenerateToAria(true);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
		render("RegistryProduct/detail.html", prd, mode, navMins, bankAccountsSub, bankAccountsRed, saBnAccounts);
	}

	public static RgProduct addMaxFee(RgProduct prd) {
		log.debug("addMaxFee. prd: " + prd);

		if ((prd.getSubFees() == null) || (prd.getSubFees().isEmpty())) {
			RgFeeTier feeTier = new RgFeeTier();
			feeTier.setTierNumber(1);
			feeTier.setUpperLimit(null);
			feeTier.setValue(null);
			feeTier.setMaxValue(null);
			prd.getSubFees().add(feeTier);
		}

		if ((prd.getRedFees() == null) || (prd.getRedFees().isEmpty())) {
			RgFeeTier feeTier = new RgFeeTier();
			feeTier.setTierNumber(1);
			feeTier.setUpperLimit(null);
			feeTier.setValue(null);
			feeTier.setMaxValue(null);
			prd.getRedFees().add(feeTier);
		}

		if ((prd.getSwiFees() == null) || (prd.getSwiFees().isEmpty())) {
			RgFeeTier feeTier = new RgFeeTier();
			feeTier.setTierNumber(1);
			feeTier.setUpperLimit(null);
			feeTier.setValue(null);
			feeTier.setMaxValue(null);
			prd.getSwiFees().add(feeTier);
		}

		return prd;
	}

	public static RgProduct addMaxNav(RgProduct prd) {
		log.debug("addMaxNav. prd: " + prd);

		if ((prd.getRgNavMinimums() == null) || (prd.getRgNavMinimums().isEmpty())) {
			RgNavMinimum navMin = new RgNavMinimum();
			navMin.setEffectiveTo(null);
			prd.getRgNavMinimums().add(navMin);
		}
		return prd;
	}

	@Check("administration.registryProduct")
	public static void entry() {
		log.debug("entry. ");

		RgProduct prd = new RgProduct();
		prd = addMaxFee(prd);
		prd = addMaxNav(prd);
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		boolean isNewRec = true;

		// GnSystemParameter paramCustBank =
		// generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_CUSTODY);
		GnSystemParameter paramSubCot = generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_COT);
		GnSystemParameter paramNavUsed = generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_NAV_USED);
		GnSystemParameter paramMinFee = generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_MIN_FEE);
		// GnSystemParameter paramMaxFee =
		// generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_MAX_FEE);
		// GnSystemParameter paramTierComparasion =
		// generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_TIER_COMPARASION);
		// GnSystemParameter paramTierPeriod =
		// generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_TIER_PERIOD);
		// GnSystemParameter paramTierType =
		// generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_TIER_TYPE);

		// GnThirdParty custBank =
		// generalService.getThirdParty(Long.parseLong(paramCustBank.getValue()));
		// prd.setThirdPartyByCustodianBank(custBank);

		prd.setSubCot(Integer.parseInt(paramSubCot.getValue()));
		prd.setSubNavUsed(Integer.parseInt(paramNavUsed.getValue()));
		prd.setSubMinFee(new BigDecimal(paramMinFee.getValue()));
		// prd.setSubMaxFee(null);
		// prd.setLookupBySubTierBy(generalService.getLookup(paramTierComparasion.getValue()));
		// prd.setLookupBySubTierType(generalService.getLookup(paramTierType.getValue()));

		prd.setRedCot(Integer.parseInt(paramSubCot.getValue()));
		prd.setRedNavUsed(Integer.parseInt(paramNavUsed.getValue()));
		prd.setRedMinFee(new BigDecimal(paramMinFee.getValue()));
		// prd.setRedMaxFee(null);
		// prd.setLookupByRedTierBy(generalService.getLookup(paramTierPeriod.getValue()));
		// prd.setLookupByRedTierType(generalService.getLookup(paramTierType.getValue()));

		prd.setSwiCot(Integer.parseInt(paramSubCot.getValue()));
		prd.setSwiNavUsed(Integer.parseInt(paramNavUsed.getValue()));
		prd.setSwiMinFee(new BigDecimal(paramMinFee.getValue()));
		// prd.setSwiMaxFee(null);
		// prd.setLookupBySwiTierBy(generalService.getLookup(paramTierPeriod.getValue()));
		// prd.setLookupBySwiTierType(generalService.getLookup(paramTierType.getValue()));

		prd.setDivInvestorOpt(false);

		prd.setCheckBidOffer(false);
		prd.setFeeCap(false);

		// GnLookup lookupAmount =
		// generalService.getLookup(LookupConstants.ROUNDING_TYPE_HALD_ROUND_UP);
		// prd.setUnitRoundType(lookupAmount);
		// prd.setPriceRoundType(lookupAmount);
		// prd.setAmountRoundType(lookupAmount);

		prd.setAmountRoundValue(2);
		prd.setUnitRoundValue(4);
		prd.setPriceRoundValue(4);

		prd.setCheckSubFeeTierMaxValue(true);
		prd.setCheckRedFeeTierMaxValue(true);
		prd.setCheckSwiFeeTierMaxValue(true);
		
		prd.setSwiIntfTrfCharge(new GnLookup(LookupConstants._CHARGE_PAYMENT_SENDER));

		prd.setFixnav(false);
		// nilai default dividen
		prd.setDivByCash(true);
		prd.setDivByRedeem(false);
		prd.setDivByReinvest(true);
		prd.setDivLock(false);
		prd.setDivCumPeriod(-1);
		prd.setDivNavUsed(0);
		prd.setDivPostPeriod(1);
		prd.setDivPayPeriod(1);

		prd.setCheckGenerateToAria(true);
		prd.setCheckMaturityDateMax(true);
		
		if(prd.getSwiIntfAmountType() == null || (prd.getSwiIntfAmountType() != null && prd.getSwiIntfAmountType().getLookupId() == null) ){//set defautlt jika null
			prd.setSwiIntfAmountType(new GnLookup(LookupConstants._AMOUNT_TYPE_TRANSACTION));
		}

		List<RgProductBnAccount> tmpSub = new ArrayList<RgProductBnAccount>();
		List<RgProductBnAccount> tmpRed = new ArrayList<RgProductBnAccount>();
		for (RgProductBnAccount ba : prd.getBankAccounts()) {
			if (ba != null && ba.getId().getDomain() != null) {
				BnAccount bnAccount = bankAccountService.getBankAccount(ba.getId().getBankAccountKey());
				ba.setBnAccount(bnAccount);

				GnLookup domain = generalService.getLookup(ba.getId().getDomain());
				ba.setDomain(domain);

				if (LookupConstants._DOMAIN_BANK_SUB.equals(ba.getId().getDomain())) {
					String constantdefaultRgProdBankAccount = ba.getBnAccount().getBankCode().getThirdPartyKey().toString() + ba.getBnAccount().getAccountNo();
					ba.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));

					String constantdefaultProdBankAccount = prd.getBankAccountSub().getBankCode().getThirdPartyKey().toString() + prd.getBankAccountSub().getAccountNo();
					ba.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));

					if (constantdefaultRgProdBankAccount.equals(constantdefaultProdBankAccount)) {
						prd.setDefaultProductBankAccountSub(ba.getDefaultRgProductBankAccount());
					}
					tmpSub.add(ba);
				} else {
					String constantdefaultRgProdBankAccount = ba.getBnAccount().getBankCode().getThirdPartyKey().toString() + ba.getBnAccount().getAccountNo();
					ba.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));

					String constantdefaultProdBankAccount = prd.getBankAccount().getBankCode().getThirdPartyKey().toString() + prd.getBankAccount().getAccountNo();
					ba.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));

					if (constantdefaultRgProdBankAccount.equals(constantdefaultProdBankAccount)) {
						prd.setDefaultProductBankAccountRed(ba.getDefaultRgProductBankAccount());
					}
					tmpRed.add(ba);
				}
			}
		}

		String navMins = null;
		String bankAccountsSub = null;
		String bankAccountsRed = null;
		String saBnAccounts = null;

		try {
			navMins = json.writeValueAsString(prd.getRgNavMinimums());
			bankAccountsSub = json.writeValueAsString(tmpSub);
			bankAccountsRed = json.writeValueAsString(tmpRed);
			saBnAccounts = json.writeValueAsString(prd.getSaBnAccounts());

			log.debug(">>> NAV MINIMUMS =>>> " + navMins);
			log.debug(">>> BANK ACCOUNTS SUB =>>> " + bankAccountsSub);
			log.debug(">>> BANK ACCOUNTS RED =>>> " + bankAccountsRed);
			log.debug(">>> SELLING AGENTS =>>> " + saBnAccounts);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
		render("RegistryProduct/detail.html", prd, mode, isNewRec, navMins, bankAccountsSub, bankAccountsRed, saBnAccounts);
	}

	public static List<RgProductBnAccount> cleanNullRgProdBankAccounts(List<RgProductBnAccount> bankAccounts) {
		log.debug("cleanNullRgProdBankAccounts. bankAccounts: " + bankAccounts);

		List<RgProductBnAccount> tmp = new ArrayList<RgProductBnAccount>();
		for (RgProductBnAccount bankAccount : bankAccounts) {
			if (bankAccount != null)
				tmp.add(bankAccount);
		}
		return tmp;
	}

	public static List<RgFeeTier> cleanNullRgFeeTiers(List<RgFeeTier> tiers) {
		log.debug("cleanNullRgFeeTiers. tiers: " + tiers);

		List<RgFeeTier> tmp = new ArrayList<RgFeeTier>();
		for (RgFeeTier fee : tiers) {
			if (fee != null)
				tmp.add(fee);
		}
		return tmp;
	}

	public static List<RgNavMinimum> cleanNullRgNavMinimums(List<RgNavMinimum> navs) {
		log.debug("cleanNullRgNavMinimums. navs: " + navs);

		List<RgNavMinimum> tmp = new ArrayList<RgNavMinimum>();
		for (RgNavMinimum nav : navs) {
			if (nav != null)
				tmp.add(nav);
		}
		return tmp;
	}

	public static List<RgProductLockException> cleanNullRgProductLockExceptions(List<RgProductLockException> lockExceptions) {
		log.debug("cleanNullRgProductLockExceptions. lockExceptions: " + lockExceptions);

		List<RgProductLockException> tmp = new ArrayList<RgProductLockException>();
		for (RgProductLockException lockDate : lockExceptions) {
			if (lockDate != null)
				tmp.add(lockDate);
		}
		return tmp;
	}

	public static void save(RgProduct prd, String mode, boolean isNewRec, String status, List<RgNavMinimum> rgNavMinimums, List<RgProductBnAccount> rgProductBnAccountsSub, List<RgProductBnAccount> rgProductBnAccountsRed, List<RgSaBnAccount> rgSaBnAccounts) {
		log.debug("save. prd: " + prd + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status + " rgNavMinimums: " + rgNavMinimums + " rgProductBnAccountsSub: " + rgProductBnAccountsSub + " rgProductBnAccountsRed: " + rgProductBnAccountsRed + " rgSaBnAccounts: " + rgSaBnAccounts);

		String navMins = null;
		//String bankAccounts = null;
		String saBnAccounts = null;

		if (prd != null && prd.getProductCode().contains(SPECIAL_CHAR_PAGAR)) {
			prd.setProductCode(prd.getProductCode().replaceAll(SPECIAL_CHAR_PAGAR, "#"));
		}

		if (prd == null)
			entry();

		String bankAccountsSub = null;
		String bankAccountsRed = null;
		List<RgProductBnAccount> tmpSub = new ArrayList<RgProductBnAccount>();
		List<RgProductBnAccount> tmpRed = new ArrayList<RgProductBnAccount>();
		List<RgProductBnAccount> rgProductBnAccounts = new ArrayList<RgProductBnAccount>();
		if (rgProductBnAccountsSub != null)
			rgProductBnAccounts.addAll(rgProductBnAccountsSub);
		if (rgProductBnAccountsRed != null)
			rgProductBnAccounts.addAll(rgProductBnAccountsRed);

		try {
			List<RgNavMinimum> listNavs = new ArrayList<RgNavMinimum>();
			for (RgNavMinimum rgNavMinimum : rgNavMinimums) {
				if (rgNavMinimum != null)
					listNavs.add(rgNavMinimum);
			}
			rgNavMinimums.clear();
			rgNavMinimums = listNavs;

			if (rgNavMinimums == null)
				rgNavMinimums = new ArrayList<RgNavMinimum>();
			navMins = json.writeValueAsString(rgNavMinimums);

			if (rgSaBnAccounts == null) {
				rgSaBnAccounts = new ArrayList<RgSaBnAccount>();
				saBnAccounts = json.writeValueAsString(rgSaBnAccounts);
			} else {
				List<RgSaBnAccount> listSaBnAccouts = new ArrayList<RgSaBnAccount>();
				for (RgSaBnAccount saBnAccount : rgSaBnAccounts) {
					if (saBnAccount != null)
						listSaBnAccouts.add(saBnAccount);
				}
				rgSaBnAccounts.clear();
				rgSaBnAccounts = listSaBnAccouts;
				saBnAccounts = json.writeValueAsString(rgSaBnAccounts);
			}

			if (rgProductBnAccounts == null) {
				rgProductBnAccounts = new ArrayList<RgProductBnAccount>();
				for (RgProductBnAccount ba : rgProductBnAccounts) {
					if (ba != null && ba.getId().getDomain() != null) {
						BnAccount bnAccount = bankAccountService.getBankAccount(ba.getId().getBankAccountKey());
						ba.setBnAccount(bnAccount);

						GnLookup domain = generalService.getLookup(ba.getId().getDomain());
						ba.setDomain(domain);

						if (LookupConstants._DOMAIN_BANK_SUB.equals(ba.getId().getDomain())) {
							tmpSub.add(ba);
						} else {
							tmpRed.add(ba);
						}
					}
				}
				// bankAccounts = json.writeValueAsString(rgProductBnAccounts);
			} else {
				// for (RgProductBnAccount rgProductBnAccount :
				// rgProductBnAccounts) {
				// if (rgProductBnAccount != null) {
				// BnAccount bnAccount =
				// bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey());
				// rgProductBnAccount.setBnAccount(bnAccount);
				// prd.getListBankAccounts().add(rgProductBnAccount);
				// }
				// }

				for (RgProductBnAccount ba : rgProductBnAccounts) {
					if (ba != null && ba.getId().getDomain() != null) {
						BnAccount bnAccount = bankAccountService.getBankAccount(ba.getId().getBankAccountKey());
						ba.setBnAccount(bnAccount);

						GnLookup domain = generalService.getLookup(ba.getId().getDomain());
						ba.setDomain(domain);

						if (LookupConstants._DOMAIN_BANK_SUB.equals(ba.getId().getDomain())) {
							tmpSub.add(ba);
							prd.getListBankAccountsSub().add(ba);
						} else {
							tmpRed.add(ba);
							prd.getListBankAccountsRed().add(ba);
						}
						prd.getListBankAccounts().add(ba);
					}
				}
				// bankAccounts =
				// json.writeValueAsString(prd.getListBankAccounts());
			}

			bankAccountsSub = json.writeValueAsString(tmpSub);
			bankAccountsRed = json.writeValueAsString(tmpRed);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		if (validation.errorsMap().values().containsAll(rgNavMinimums) == false)
			validation.clear();
		if (validation.errorsMap().values().containsAll(rgProductBnAccounts) == false)
			validation.clear();

		log.debug("Customer key => " + prd.getCfMaster().getCustomerKey());
		log.debug("[SAVE] size of subFees before => " + prd.getSubFees().size());

		prd.setSubFees(cleanNullRgFeeTiers(prd.getSubFees()));
		prd.setRedFees(cleanNullRgFeeTiers(prd.getRedFees()));
		prd.setSwiFees(cleanNullRgFeeTiers(prd.getSwiFees()));

		prd.setSubLockExceptions(cleanNullRgProductLockExceptions(prd.getSubLockExceptions()));
		prd.setRedLockExceptions(cleanNullRgProductLockExceptions(prd.getRedLockExceptions()));
		prd.setSwiLockExceptions(cleanNullRgProductLockExceptions(prd.getSwiLockExceptions()));
		prd.setDivLockExceptions(cleanNullRgProductLockExceptions(prd.getDivLockExceptions()));

		prd.setListBankAccounts(cleanNullRgProdBankAccounts(prd.getListBankAccounts()));
		prd.setListBankAccountsSub(cleanNullRgProdBankAccounts(prd.getListBankAccountsSub()));
		prd.setListBankAccountsRed(cleanNullRgProdBankAccounts(prd.getListBankAccountsRed()));

		// Helper.showProperties(prd);
		prd.shortRgTier();
		prd.shortRgProductLockException();
		log.debug("[SAVE] size of subFees after = " + prd.getSubFees().size());

		validation.required("Fund Code is", prd.getProductCode());
		validation.required("Fund Name is", prd.getName());
		validation.required("Customer Code is", prd.getCfMaster().getCustomerNo());
		validation.required("Fund Manager is", prd.getThirdPartyByFundManager().getThirdPartyCode());
		// validation.required("Cust. Bank is",
		// prd.getThirdPartyByCustodianBank().getThirdPartyCode());
		validation.required("Fund Type is", prd.getLookupByType().getLookupCode());
		validation.required("Fund Class is", prd.getLookupByClass().getLookupCode());
		validation.required("Currency is", prd.getCurrency().getCurrencyCode());
		log.debug("[SAVE] TransactionDate1 => " + prd.getTransactionDate());
		log.debug("[SAVE] TransactionDate2 => " + prd.getTransactionDate());
		validation.required("Transaction Date is", prd.getTransactionDate());
		validation.required("Posting Date is", prd.getSubPostPeriod());

		// tab subscription
		validation.required("Min. Initial Amount in tab Subscription is", prd.getSubInitMinAmt());
		validation.required("Min. Top up Amount in tab Subscription is", prd.getSubMinAmt());

		// validation.required("Minimum Fee in tab Subscription is",
		// prd.getSubMinFee());
		// validation.required("Tier By in tab Subscription is",
		// prd.getLookupBySubTierBy().getLookupId());
		// validation.required("Tier Type in tab Subscription is",
		// prd.getLookupBySubTierType().getLookupId());

		if (!prd.isCheckMaxSub()) {
			validation.required("Maximum Amount in tab Subscription is", prd.getSubMaxAmt());
		}

		validation.required("Tax Code in tab Subscription is", prd.getTaxMasterBySubTaxKey().getTaxCode());

		if (prd.getSubFees() != null) {
			for (RgFeeTier fee : prd.getSubFees()) {
				validation.required("Default Fee in tab Subscription is", fee.getValue());
			}
		}

		if (!prd.isCheckSubFeeTierMaxValue()) {
			if (prd.getSubFees() != null) {
				for (RgFeeTier fee : prd.getSubFees()) {
					validation.required("Maximum Fee in tab Subscription is", fee.getMaxValue());
				}
			}
		}

		// tab redemption
		validation.required("Minimum Amount in tab Redemption is", prd.getRedMinAmt());
		// validation.required("Minimum Fee in tab Redemption is",
		// prd.getRedMinFee());

		if (!prd.isCheckMaxRed()) {
			validation.required("Maximum Amount in tab Redemption is", prd.getRedMaxAmt());
		}

		validation.required("Payment Date in tab Redemption is", prd.getRedPayPeriod());

		validation.required("Tax Code in tab Redemption is", prd.getTaxMasterByRedTaxKey().getTaxCode());

		if (prd.getRedFees() != null) {
			for (RgFeeTier fee : prd.getRedFees()) {
				validation.required("Default Fee in tab Redemption is", fee.getValue());
			}
		}

		if (!prd.isCheckRedFeeTierMaxValue()) {
			if (prd.getRedFees() != null) {
				for (RgFeeTier fee : prd.getRedFees()) {
					validation.required("Maximum Fee in tab Redemption is", fee.getMaxValue());
				}
			}
		}

		// tab switching
		validation.required("Minimum Amount in tab Switching is", prd.getSwiMinAmt());
		// validation.required("Minimum Fee in tab Switching is",
		// prd.getSwiMinFee());

		if (!prd.isCheckMaxSwi()) {
			validation.required("Maximum Amount in tab Switching is", prd.getSwiMaxAmt());
		}

		validation.required("Tax Code in tab Switching is", prd.getTaxMasterBySwiTaxKey().getTaxCode());

		if (prd.getSwiFees() != null) {
			for (RgFeeTier fee : prd.getSwiFees()) {
				validation.required("Default Fee in tab Switching is", fee.getValue());
			}
		}

		if (!prd.isCheckSwiFeeTierMaxValue()) {
			if (prd.getSwiFees() != null) {
				for (RgFeeTier fee : prd.getSwiFees()) {
					validation.required("Maximum Fee in tab Switching is", fee.getMaxValue());
				}
			}
		}

		// tab dividen
		if (prd.getDivByCash() != null || prd.getDivByRedeem() != null || prd.getDivByReinvest() != null) {
			validation.required("Cum Date in tab Dividend is", prd.getDivCumPeriod());
			validation.required("NAV Date in tab Dividend is", prd.getDivNavUsed());
			validation.required("Posting Date in tab Dividend is", prd.getDivPostPeriod());
			validation.required("Payment Date in tab Dividend is", prd.getDivPayPeriod());
		}

		// if (prd.getDivInvestorOpt()!=null){
		// if (prd.getDivInvestorOpt() == true) {
		// validation.required("By Cash  in tab Dividend is",
		// prd.getDivIopByCashPct());
		// validation.required("By Reinvestment in tab Dividend is",
		// prd.getDivIopByReinvestmentPct());
		// validation.required("By Redemption in tab Dividend is",
		// prd.getDivIopByRedeemPct());
		// }
		// }

		// tab more info
		if (prd.isCheckGenerateToAria()) {
			validation.required("ARIA Code in tab More Information is", prd.getEreportCode());
		}

		validation.required("Effective Date in tab More Information is", prd.getEffectiveDate());

		if (!prd.isCheckMaturityDateMax()) {
			validation.required("Maturity Date in tab More Information is", prd.getLiquidDate());
		}

		validation.required("Maximum Payment Date is in tab More Information is", prd.getMaxPaymentDate());
		validation.required("Initial NAV Price in tab More Information is", prd.getInitNavPrice());
		validation.required("Minimum NAV Amount in tab More Information is", prd.getMinNavAmt());

		validation.required("Maximum unit issued in tab More Information is", prd.getTotalUnit());
		validation.required("Limit Tolerance in tab More Information is", prd.getUnitLimitTolerance());

		if (!prd.isCheckMaxInvestor()) {
			validation.required("Maximum Unit Holders in tab More Information is", prd.getMaxInvestor());
		}

		validation.required("Type For Amount in tab More Information is", prd.getAmountRoundType());
		validation.required("No. of Decimal For Amount in tab More Information is", prd.getAmountRoundValue());
		validation.required("Type For Unit in tab More Information is", prd.getUnitRoundType());
		validation.required("No. of Decimal For Unit in tab More Information is", prd.getUnitRoundValue());
		validation.required("Type For Price in tab More Information is", prd.getPriceRoundType());
		validation.required("No. of Decimal For Value Price in tab More Information is", prd.getPriceRoundValue());

		if (prd.getIsMinBalAmount()) {
			validation.required("Minimum Balance By Amount in tab More Information is", prd.getMinBalAmt());
		}

		if (prd.getIsMinBalUnit()) {
			validation.required("Minimum Balance By Unit in tab More Information is", prd.getMinBalUnit());
		}

		// if (rgProductBnAccounts.size() < 1)
		// validation.required("Bank Account is", prd.getListBankAccounts());
		if ((rgProductBnAccountsSub != null && rgProductBnAccountsSub.size() < 1) || rgProductBnAccountsSub == null) {
			validation.required("Bank Account Sub is", prd.getListBankAccountsSub());
		}
		if ((rgProductBnAccountsRed != null && rgProductBnAccountsRed.size() < 1) || rgProductBnAccountsRed == null) {
			validation.required("Bank Account Red is", prd.getListBankAccountsRed());
		}

		if (validation.hasErrors()) {
			String productCode = prd.getProductCode();
			if (productCode != null && prd.getProductCode().contains("\'")) {
				productCode = productCode.replaceAll("\'", SPECIAL_CHAR_QUOTE);
			}
			prd.setProductCode(productCode);

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
			render("RegistryProduct/detail.html", prd, mode, isNewRec, status, rgNavMinimums, navMins, rgProductBnAccounts, bankAccountsSub, bankAccountsRed, rgSaBnAccounts, saBnAccounts);
		} else {
			log.debug("RED_LIMIT_AMT 1>> " + prd.getRedLimitAmt());
			log.debug("RED_LIMIT_PCT 1>> " + prd.getRedLimitPct());
			log.debug("SWO_LIMIT_AMT 1>> " + prd.getSwoLimitAmt());
			log.debug("SWO_LIMIT_PCT 1>> " + prd.getSwoLimitPct());

			if ((prd.getRedLimitAmt() != null) && (prd.getSwoLimitAmt() != null)) {
				prd.setRedLimitPct(null);
				prd.setSwoLimitPct(null);
			}

			if ((prd.getRedLimitPct() != null) && (prd.getSwoLimitPct() != null)) {
				prd.setRedLimitAmt(null);
				prd.setSwoLimitAmt(null);
			}

			if ((prd.getRedLimitAmt() != null) && (prd.getSwoLimitAmt() == null)) {
				prd.setRedLimitPct(null);
				prd.setSwoLimitAmt(new BigDecimal(0));
			}

			if ((prd.getRedLimitPct() != null) && (prd.getSwoLimitPct() == null)) {
				prd.setRedLimitAmt(null);
				prd.setSwoLimitPct(new BigDecimal(0));
			}

			log.debug("RED_LIMIT_AMT 2>> " + prd.getRedLimitAmt());
			log.debug("RED_LIMIT_PCT 2>> " + prd.getRedLimitPct());
			log.debug("SWO_LIMIT_AMT 2>> " + prd.getSwoLimitAmt());
			log.debug("SWO_LIMIT_PCT 2>> " + prd.getSwoLimitPct());
			// prd.splitProductFees();
			// prd.splitProductLockExceptions();
			// String productcode = prd.getProductCode();
			// Helper.showProperties(prd);

			log.debug("[SAVE] size of subFees = " + prd.getSubFees().size());
			log.debug("[SAVE] size of redFees = " + prd.getRedFees().size());
			log.debug("[SAVE] size of swiFees = " + prd.getSwiFees().size());

			log.debug("[SAVE] size of subLockExceptions = " + prd.getSubLockExceptions().size());
			log.debug("[SAVE] size of redLockExceptions = " + prd.getRedLockExceptions().size());
			log.debug("[SAVE] size of swiLockExceptions = " + prd.getSwiLockExceptions().size());
			log.debug("[SAVE] size of divLockExceptions = " + prd.getDivLockExceptions().size());

			if (rgNavMinimums != null) {
				for (RgNavMinimum rgNavMinimum : rgNavMinimums) {
					if (rgNavMinimum != null)
						prd.getRgNavMinimums().add(rgNavMinimum);
				}
			}

			if (rgSaBnAccounts != null) {
				for (RgSaBnAccount rgSaBnAccount : rgSaBnAccounts) {
					if (rgSaBnAccount != null)
						prd.getSaBnAccounts().add(rgSaBnAccount);
				}
			}

			String idSerialize = prd.getProductCode();
			if (prd != null && prd.getProductCode() != null && prd.getProductCode().contains(SPECIAL_CHAR_QUOTE)) {
				idSerialize = prd.getProductCode().replaceAll(SPECIAL_CHAR_QUOTE, "'");
			}

			log.debug("[SAVE] size of navMins = " + prd.getRgNavMinimums().size());
			log.debug("[SAVE] size of bankAccounts = " + prd.getListBankAccounts().size());
			serializerService.serialize(session.getId(), idSerialize, prd);

			String productCode = prd.getProductCode();
			if (productCode != null && prd.getProductCode().contains("#")) {
				productCode = productCode.replaceAll("#", SPECIAL_CHAR_PAGAR);
			}

			confirming(productCode, mode, isNewRec, status);
		}
	}

	public static void confirming(String id, String mode, boolean isNewRec, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		renderArgs.put("confirming", true);

		if (id.contains(SPECIAL_CHAR_PAGAR))
			id = id.replaceAll(SPECIAL_CHAR_PAGAR, "#");
		if (id.contains(SPECIAL_CHAR_QUOTE))
			id = id.replaceAll(SPECIAL_CHAR_QUOTE, "'");

		RgProduct prd = serializerService.deserialize(session.getId(), id, RgProduct.class);

		log.debug("[CONFIRMING] size of subFees = " + prd.getSubFees().size());
		log.debug("[CONFIRMING] size of redFees = " + prd.getRedFees().size());
		log.debug("[CONFIRMING] size of swiFees = " + prd.getSwiFees().size());

		log.debug("[CONFIRMING] size of navMins = " + prd.getRgNavMinimums().size());
		log.debug("[CONFIRMING] size of rgSaBnAccounts = " + prd.getSaBnAccounts().size());
		log.debug("[CONFIRMING] size of bankAccounts = " + prd.getListBankAccounts().size());

		Set<RgNavMinimum> rgNavMinimums = prd.getRgNavMinimums();
		Set<RgSaBnAccount> rgSaBnAccounts = prd.getSaBnAccounts();
		String navMins = null;
		String saBnAccounts = null;

		String productCode = prd.getProductCode();
		if (productCode != null && prd.getProductCode().contains("\'")) {
			productCode = productCode.replaceAll("\'", SPECIAL_CHAR_QUOTE);
		}
		prd.setProductCode(productCode);

		List<RgProductBnAccount> rgProductBnAccounts = prd.getListBankAccounts();
		// List<RgProductBnAccount> bas = new ArrayList<RgProductBnAccount>();
		// for (RgProductBnAccount ba : rgProductBnAccounts) {
		// if (ba != null) {
		// BnAccount bnAccount =
		// bankAccountService.getBankAccount(ba.getId().getBankAccountKey());
		// String constantdefaultRgProductBankAccount =
		// bnAccount.getBankCode().getThirdPartyKey().toString() +
		// bnAccount.getAccountNo();
		// ba.setDefaultRgProductBankAccount(constantdefaultRgProductBankAccount.replace(" ",
		// "_"));
		// ba.setBnAccount(bnAccount);
		//
		// bas.add(ba);
		// }
		// }

		String bankAccountsSub = null;
		String bankAccountsRed = null;
		List<RgProductBnAccount> tmpSub = new ArrayList<RgProductBnAccount>();
		List<RgProductBnAccount> tmpRed = new ArrayList<RgProductBnAccount>();

		if (rgProductBnAccounts != null) {
			for (RgProductBnAccount rgProductBnAccount : rgProductBnAccounts) {
				if (rgProductBnAccount != null) {
					BnAccount bnAccount = bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey());
					rgProductBnAccount.setBnAccount(bnAccount);
					rgProductBnAccount.getId().setProductCode(prd.getProductCode());

					GnLookup domain = generalService.getLookup(rgProductBnAccount.getId().getDomain());
					rgProductBnAccount.setDomain(domain);

					if (LookupConstants._DOMAIN_BANK_SUB.equals(rgProductBnAccount.getId().getDomain())) {
						String constantdefaultRgProdBankAccount = rgProductBnAccount.getBnAccount().getBankCode().getThirdPartyKey().toString() + rgProductBnAccount.getBnAccount().getAccountNo();
						rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));
						tmpSub.add(rgProductBnAccount);
					} else {
						String constantdefaultRgProdBankAccount = rgProductBnAccount.getBnAccount().getBankCode().getThirdPartyKey().toString() + rgProductBnAccount.getBnAccount().getAccountNo();
						rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));
						tmpRed.add(rgProductBnAccount);
					}
				}
			}
		}

		try {
			navMins = json.writeValueAsString(rgNavMinimums);
			saBnAccounts = json.writeValueAsString(rgSaBnAccounts);
			// bankAccounts = json.writeValueAsString(bas);
			bankAccountsSub = json.writeValueAsString(tmpSub);
			bankAccountsRed = json.writeValueAsString(tmpRed);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));

		render("RegistryProduct/detail.html", prd, mode, isNewRec, status, navMins, rgNavMinimums, bankAccountsSub, bankAccountsRed, rgProductBnAccounts, saBnAccounts, rgSaBnAccounts);
	}

	public static void confirm(RgProduct prd, String mode, boolean isNewRec, String status, List<RgNavMinimum> rgNavMinimums, List<RgProductBnAccount> rgProductBnAccountsSub, List<RgProductBnAccount> rgProductBnAccountsRed, List<RgSaBnAccount> rgSaBnAccounts) {
		log.debug("confirm. prd: " + prd + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status + " rgNavMinimums: " + rgNavMinimums + " rgProductBnAccountsSub: " + rgProductBnAccountsSub + " rgProductBnAccountsRed: " + rgProductBnAccountsRed + " rgSaBnAccounts: " + rgSaBnAccounts);

		if (prd == null) {
			prd = new RgProduct();
			prd.setCfMaster(new CfMaster());
		}

		if (prd != null && prd.getProductCode() != null && prd.getProductCode().contains(SPECIAL_CHAR_QUOTE)) {
			prd.setProductCode(prd.getProductCode().replaceAll(SPECIAL_CHAR_QUOTE, "'"));
		}
		
		if(prd.getSwiIntfTrfCharge() == null || (prd.getSwiIntfTrfCharge() != null && (prd.getSwiIntfTrfCharge().getLookupId() == null || prd.getSwiIntfTrfCharge().getLookupId().equals("")))){
			prd.setSwiIntfTrfCharge(null);
		}
		
		if(prd.getSwiIntfAccessAccount() == null || (prd.getSwiIntfAccessAccount() != null && (prd.getSwiIntfAccessAccount().getLookupId() == null || prd.getSwiIntfAccessAccount().getLookupId().equals("")))){
			prd.setSwiIntfAccessAccount(null);
		}
		
		if(prd.getSwiIntfAmountType() == null || (prd.getSwiIntfAmountType() != null && (prd.getSwiIntfAmountType().getLookupId() == null || prd.getSwiIntfAmountType().getLookupId().equals("")))){
			prd.setSwiIntfAmountType(null);
		}

		// String productCode = prd.getProductCode();
		// List<RgProductBnAccount> bas = new ArrayList<RgProductBnAccount>();

		List<RgProductBnAccount> rgProductBnAccounts = new ArrayList<RgProductBnAccount>();
		rgProductBnAccounts.addAll(rgProductBnAccountsSub);
		rgProductBnAccounts.addAll(rgProductBnAccountsRed);

		if (rgNavMinimums == null)
			rgNavMinimums = new ArrayList<RgNavMinimum>();
		if (rgProductBnAccounts == null)
			rgProductBnAccounts = new ArrayList<RgProductBnAccount>();
		if (rgSaBnAccounts == null)
			rgSaBnAccounts = new ArrayList<RgSaBnAccount>();

		String bankAccountsSub = null;
		String bankAccountsRed = null;
		List<RgProductBnAccount> tmpSub = new ArrayList<RgProductBnAccount>();
		List<RgProductBnAccount> tmpRed = new ArrayList<RgProductBnAccount>();

		String navMins = null;
		String saBnAccounts = null;
		try {
			List<RgNavMinimum> listNavs = new ArrayList<RgNavMinimum>();
			for (RgNavMinimum rgNavMinimum : rgNavMinimums) {
				if (rgNavMinimum != null)
					listNavs.add(rgNavMinimum);
			}
			rgNavMinimums.clear();
			rgNavMinimums = listNavs;

			navMins = json.writeValueAsString(rgNavMinimums);
			// bankAccounts = json.writeValueAsString(rgProductBnAccounts);
			saBnAccounts = json.writeValueAsString(rgSaBnAccounts);

			if (rgNavMinimums != null) {
				for (RgNavMinimum rgNavMinimum : rgNavMinimums) {
					if (rgNavMinimum != null) {
						rgNavMinimum.setRgProduct(prd);
						prd.getRgNavMinimums().add(rgNavMinimum);
					}
				}
			}

			if (rgProductBnAccounts != null) {
				for (RgProductBnAccount rgProductBnAccount : rgProductBnAccounts) {
					if (rgProductBnAccount != null) {
						BnAccount bnAccount = bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey());
						rgProductBnAccount.setBnAccount(bnAccount);
						rgProductBnAccount.getId().setProductCode(prd.getProductCode());

						if (LookupConstants._DOMAIN_BANK_SUB.equals(rgProductBnAccount.getId().getDomain())) {
							String constantdefaultRgProdBankAccount = rgProductBnAccount.getBnAccount().getBankCode().getThirdPartyKey().toString() + rgProductBnAccount.getBnAccount().getAccountNo();
							rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));
							if (rgProductBnAccount.getDefaultRgProductBankAccount().equals(prd.getDefaultProductBankAccountSub())) {
								prd.setBankAccountSub(bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey()));
							}
							rgProductBnAccounts = prd.getListBankAccounts();
							tmpSub.add(rgProductBnAccount);
						} else {
							String constantdefaultRgProdBankAccount = rgProductBnAccount.getBnAccount().getBankCode().getThirdPartyKey().toString() + rgProductBnAccount.getBnAccount().getAccountNo();
							rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));
							if (rgProductBnAccount.getDefaultRgProductBankAccount().equals(prd.getDefaultProductBankAccountSub())) {
								prd.setBankAccount(bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey()));
							}
							rgProductBnAccounts = prd.getListBankAccounts();
							tmpRed.add(rgProductBnAccount);
						}
						prd.getListBankAccounts().add(rgProductBnAccount);
					}
				}
				// bankAccounts = json.writeValueAsString(rgProductBnAccounts);
			}

			bankAccountsSub = json.writeValueAsString(tmpSub);
			bankAccountsRed = json.writeValueAsString(tmpRed);

			if (rgSaBnAccounts != null) {
				for (RgSaBnAccount rgSaBnAcct : rgSaBnAccounts) {
					if (rgSaBnAcct != null) {
						rgSaBnAcct.setRgProduct(prd);
						prd.getSaBnAccounts().add(rgSaBnAcct);
					}
				}
			}
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}

		try {
			prd.setSubFees(cleanNullRgFeeTiers(prd.getSubFees()));
			prd.setRedFees(cleanNullRgFeeTiers(prd.getRedFees()));
			prd.setSwiFees(cleanNullRgFeeTiers(prd.getSwiFees()));

			prd.setSubLockExceptions(cleanNullRgProductLockExceptions(prd.getSubLockExceptions()));
			prd.setRedLockExceptions(cleanNullRgProductLockExceptions(prd.getRedLockExceptions()));
			prd.setSwiLockExceptions(cleanNullRgProductLockExceptions(prd.getSwiLockExceptions()));
			prd.setDivLockExceptions(cleanNullRgProductLockExceptions(prd.getDivLockExceptions()));

			prd.setListBankAccounts(cleanNullRgProdBankAccounts(prd.getListBankAccounts()));
			prd.setListBankAccountsSub(cleanNullRgProdBankAccounts(prd.getListBankAccountsSub()));
			prd.setListBankAccountsRed(cleanNullRgProdBankAccounts(prd.getListBankAccountsRed()));

			prd.combineProductFees();
			prd.combineProductLockExceptions();
			prd.putToSetRgProdBnAccount();
			// Helper.showProperties(prd);

			List<RgProduct> products = taService.listProduct();
			for (RgProduct productInTable : products) {
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					if (productInTable.getProductCode().equals(prd.getProductCode())) {
						throw new MedallionException(ExceptionConstants.DATA_DUPLICATE);
					}

					if (productInTable.getCfMaster() != null) {
						if (productInTable.getCfMaster().getCustomerKey().equals(prd.getCfMaster().getCustomerKey()) && (productInTable.getIsActive().equals(true))) {
							flash.error("Customer Code : ' " + prd.getCfMaster().getCustomerNo() + " ' " + Messages.get(ExceptionConstants.DATA_DUPLICATE));
							renderArgs.put("confirming", true);
							flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
							render("RegistryProduct/detail.html", prd, mode, isNewRec, status, navMins, bankAccountsSub, bankAccountsRed, saBnAccounts, rgNavMinimums, rgProductBnAccounts);
						}
					}
				} else {
					if (productInTable.getCfMaster() != null) {
						if ((!productInTable.getProductCode().equals(prd.getProductCode())) && productInTable.getCfMaster().getCustomerKey().equals(prd.getCfMaster().getCustomerKey()) && (productInTable.getIsActive().equals(true)) && (prd.getIsActive().equals(true))) {
							flash.error("Customer Code : ' " + prd.getCfMaster().getCustomerNo() + " ' " + Messages.get(ExceptionConstants.DATA_DUPLICATE));
							renderArgs.put("confirming", true);
							flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
							render("RegistryProduct/detail.html", prd, mode, isNewRec, status, navMins, bankAccountsSub, bankAccountsRed, saBnAccounts, rgNavMinimums, rgProductBnAccounts);
						}
					}
				}
			}

			GnSystemParameter paramCustBank = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_CUSTODY);
			GnSystemParameter paramTierComparasion = generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_TIER_COMPARASION);
			GnSystemParameter paramTierPeriod = generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_TIER_PERIOD);
			GnSystemParameter paramTierType = generalService.getSystemParameter(SystemParamConstants.PARAM_PRODUCT_TIER_TYPE);

			GnThirdParty custBank = generalService.getThirdParty(Long.parseLong(paramCustBank.getValue()));

			prd.setThirdPartyByCustodianBank(custBank);
			prd.setLookupBySubTierBy(generalService.getLookup(paramTierComparasion.getValue()));
			prd.setLookupBySubTierType(generalService.getLookup(paramTierType.getValue()));

			prd.setLookupByRedTierBy(generalService.getLookup(paramTierPeriod.getValue()));
			prd.setLookupByRedTierType(generalService.getLookup(paramTierType.getValue()));

			prd.setLookupBySwiTierBy(generalService.getLookup(paramTierPeriod.getValue()));
			prd.setLookupBySwiTierType(generalService.getLookup(paramTierType.getValue()));

			log.debug("[SIZE RGNAV] " + prd.getRgNavMinimums().size());

			for (RgNavMinimum rgNavMinimum : prd.getRgNavMinimums()) {
				log.debug("[SAVE CONTROLLER] Prod rgNavMinimum.minnavKey >>> " + rgNavMinimum.getMinnavKey());
				log.debug("[SAVE CONTROLLER] Prod rgNavMinimum.rgProduct >>> " + rgNavMinimum.getRgProduct().getProductCode());
			}

			CfMaster customer = customerService.getCustomer(prd.getCfMaster().getCustomerKey());
			prd.setCfMaster(customer);

			prd.setCheckMaxSubFee(true);
			prd.setCheckMaxRedFee(true);
			prd.setCheckMaxSwiFee(true);

			for (RgFeeTier fee : prd.getSubFees()) {
				if (fee.getMaxValue() == null) {
					prd.setCheckSubFeeTierMaxValue(true);
				} else {
					prd.setCheckSubFeeTierMaxValue(false);
				}
			}

			for (RgFeeTier fee : prd.getRedFees()) {
				if (fee.getMaxValue() == null) {
					prd.setCheckRedFeeTierMaxValue(true);
				} else {
					prd.setCheckRedFeeTierMaxValue(false);
				}
			}

			for (RgFeeTier fee : prd.getSwiFees()) {
				if (fee.getMaxValue() == null) {
					prd.setCheckSwiFeeTierMaxValue(true);
				} else {
					prd.setCheckSwiFeeTierMaxValue(false);
				}
			}

			if (prd.getMinBalAmt() == null) {
				prd.setIsMinBalAmount(true);
			} else {
				prd.setIsMinBalAmount(false);
			}

			if (prd.getMinBalUnit() == null) {
				prd.setIsMinBalUnit(true);
			} else {
				prd.setIsMinBalUnit(false);
			}

			if (prd.isCheckMaturityDateMax()) {
				try {
					prd.setLiquidDate(Tools.parseMMDDYYYY("12/31/9999"));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}

			log.debug("[CONFIRM] size of subLockExceptions = " + prd.getSubLockExceptions().size());
			log.debug("[CONFIRM] size of redLockExceptions = " + prd.getRedLockExceptions().size());
			log.debug("[CONFIRM] size of swiLockExceptions = " + prd.getSwiLockExceptions().size());
			log.debug("[CONFIRM] size of divLockExceptions = " + prd.getDivLockExceptions().size());

			log.debug("prd.getProductCode() " + prd.getProductCode());
			if (prd.getProductCode() != null) {
				taService.saveProduct(MenuConstants.RG_PRODUCT, prd, session.get(UIConstants.SESSION_USERNAME), "", isNewRec, session.get(UIConstants.SESSION_USER_KEY));
			}

			list();
		} catch (MedallionException ex) {
			flash.error("Product Code : ' " + prd.getProductCode() + " ' " + Messages.get(ex.getMessage()));
			renderArgs.put("confirming", true);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
			render("RegistryProduct/detail.html", prd, mode, isNewRec, status, navMins, bankAccountsSub, bankAccountsRed, saBnAccounts, rgNavMinimums, rgProductBnAccounts);
		}
	}

	public static void back(String id, String mode, boolean isNewRec, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
			isNewRec = true;
		} else {
			isNewRec = false;
		}

		if (id.contains(SPECIAL_CHAR_PAGAR))
			id = id.replaceAll(SPECIAL_CHAR_PAGAR, "#");
		if (id.contains(SPECIAL_CHAR_QUOTE))
			id = id.replaceAll(SPECIAL_CHAR_QUOTE, "\'");

		log.debug("id back " + id);
		log.debug("session.getId() " + session.getId());

		RgProduct prd = serializerService.deserialize(session.getId(), id, RgProduct.class);

		String productCode = prd.getProductCode();
		if (productCode != null && prd.getProductCode().contains("\'")) {
			productCode = productCode.replaceAll("\'", SPECIAL_CHAR_QUOTE);
		}
		prd.setProductCode(productCode);

		List<RgProductBnAccount> rgProductBnAccounts = prd.getListBankAccounts();
		// List<RgProductBnAccount> rgProductBnAccountsSub =
		// prd.getListBankAccountsSub();
		// List<RgProductBnAccount> rgProductBnAccountsRed =
		// prd.getListBankAccountsRed();
		// List<RgProductBnAccount> bas = new ArrayList<RgProductBnAccount>();
		// for (RgProductBnAccount ba : rgProductBnAccounts) {
		// if (ba != null) {
		// BnAccount bnAccount =
		// bankAccountService.getBankAccount(ba.getId().getBankAccountKey());
		// String constantdefaultRgProductBankAccount =
		// bnAccount.getBankCode().getThirdPartyKey().toString() +
		// bnAccount.getAccountNo();
		// ba.setDefaultRgProductBankAccount(constantdefaultRgProductBankAccount.replace(" ",
		// "_"));
		// ba.setBnAccount(bnAccount);
		// bas.add(ba);
		// }
		// }

		String navMins = null;
		String saBnAccounts = null;
		String bankAccountsSub = null;
		String bankAccountsRed = null;
		List<RgProductBnAccount> tmpSub = new ArrayList<RgProductBnAccount>();
		List<RgProductBnAccount> tmpRed = new ArrayList<RgProductBnAccount>();

		for (RgProductBnAccount rgProductBnAccount : rgProductBnAccounts) {
			if (rgProductBnAccount != null) {
				BnAccount bnAccount = bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey());
				rgProductBnAccount.setBnAccount(bnAccount);
				rgProductBnAccount.getId().setProductCode(prd.getProductCode());

				GnLookup domain = generalService.getLookup(rgProductBnAccount.getId().getDomain());
				rgProductBnAccount.setDomain(domain);

				if (LookupConstants._DOMAIN_BANK_SUB.equals(rgProductBnAccount.getId().getDomain())) {
					String constantdefaultRgProdBankAccount = rgProductBnAccount.getBnAccount().getBankCode().getThirdPartyKey().toString() + rgProductBnAccount.getBnAccount().getAccountNo();
					rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));
					tmpSub.add(rgProductBnAccount);
				} else {
					String constantdefaultRgProdBankAccount = rgProductBnAccount.getBnAccount().getBankCode().getThirdPartyKey().toString() + rgProductBnAccount.getBnAccount().getAccountNo();
					rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));
					tmpRed.add(rgProductBnAccount);
				}
			}
		}

		try {
			navMins = json.writeValueAsString(prd.getRgNavMinimums());
			// bankAccounts = json.writeValueAsString(bas);
			bankAccountsSub = json.writeValueAsString(tmpSub);
			bankAccountsRed = json.writeValueAsString(tmpRed);
			saBnAccounts = json.writeValueAsString(prd.getSaBnAccounts());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.RG_PRODUCT));
		render("RegistryProduct/detail.html", prd, mode, isNewRec, status, navMins, bankAccountsSub, bankAccountsRed, saBnAccounts);
	}

	public static void approval(String taskId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			log.debug("maintenance log key = " + maintenanceLogKey);
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);

			RgProduct prd = json.readValue(maintenanceLog.getNewData(), RgProduct.class);

			// FOR SHOW RG FEE TIERS PROPERTIES
			if (operation.equals(LookupConstants.MAINTENANCE_OPERATION_CREATE)) {
				prd.splitProductFees();
				prd.splitProductLockExceptions();
				prd.putToListRgProdBnAccount();
			} else {
				prd.getRgFeeTiers().clear();
				prd.splitProductFees();
				prd.getRgProductLockExceptions().clear();
				prd.splitProductLockExceptions();
				// prd.getBankAccounts().clear();
				// prd.putToListRgProdBnAccount();
			}
			// ===================================================================

			String bankAccountsSub = null;
			String bankAccountsRed = null;
			List<RgProductBnAccount> tmpSub = new ArrayList<RgProductBnAccount>();
			List<RgProductBnAccount> tmpRed = new ArrayList<RgProductBnAccount>();

			// show list bankAccount on grid
			Set<RgProductBnAccount> bankAccountProducts = prd.getBankAccounts();
			log.debug("Size bankAccounts ===>>> " + bankAccountProducts.size());

			String navMins = json.writeValueAsString(prd.getRgNavMinimums());
			String saBnAccounts = json.writeValueAsString(prd.getSaBnAccounts());
			String productCode = prd.getProductCode();
			if (productCode != null && prd.getProductCode().contains("\'")) {
				productCode = productCode.replaceAll("\'", SPECIAL_CHAR_QUOTE);
			}

			if (bankAccountProducts != null) {
				// for(RgProductBnAccount rgProductBnAccount:
				// bankAccountProducts) {
				// BnAccount bnAccount =
				// bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey());
				// rgProductBnAccount.setBnAccount(bnAccount);
				//
				// String constantdefaultRgProductBankAccount =
				// bnAccount.getBankCode().getThirdPartyKey().toString() +
				// bnAccount.getAccountNo();
				// rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProductBankAccount.replace(" ",
				// "_"));
				// logger.debug("tes >>> "+
				// rgProductBnAccount.getDefaultRgProductBankAccount());
				//
				// bankAccountProducts.add(rgProductBnAccount);
				// }

				for (RgProductBnAccount rgProductBnAccount : bankAccountProducts) {
					if (rgProductBnAccount != null) {
						BnAccount bnAccount = bankAccountService.getBankAccount(rgProductBnAccount.getId().getBankAccountKey());
						rgProductBnAccount.setBnAccount(bnAccount);
						rgProductBnAccount.getId().setProductCode(prd.getProductCode());

						GnLookup domain = generalService.getLookup(rgProductBnAccount.getId().getDomain());
						rgProductBnAccount.setDomain(domain);

						if (LookupConstants._DOMAIN_BANK_SUB.equals(rgProductBnAccount.getId().getDomain())) {
							String constantdefaultRgProdBankAccount = rgProductBnAccount.getBnAccount().getBankCode().getThirdPartyKey().toString() + rgProductBnAccount.getBnAccount().getAccountNo();
							rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));
							tmpSub.add(rgProductBnAccount);
						} else {
							String constantdefaultRgProdBankAccount = rgProductBnAccount.getBnAccount().getBankCode().getThirdPartyKey().toString() + rgProductBnAccount.getBnAccount().getAccountNo();
							rgProductBnAccount.setDefaultRgProductBankAccount(constantdefaultRgProdBankAccount.replace(" ", "_"));
							tmpRed.add(rgProductBnAccount);
						}

						bankAccountProducts.add(rgProductBnAccount);
					}
				}
			}

			prd.setListBankAccounts(new ArrayList<RgProductBnAccount>(bankAccountProducts));
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			// =============================

			// String bankAccounts =
			// json.writeValueAsString(bankAccountProducts);
			bankAccountsSub = json.writeValueAsString(tmpSub);
			bankAccountsRed = json.writeValueAsString(tmpRed);

			if (prd.getEreportCode() != null)
				prd.setCheckGenerateToAria(true);

			render("RegistryProduct/approval.html", prd, mode, taskId, operation, maintenanceLogKey, from, navMins, bankAccountsSub, bankAccountsRed, saBnAccounts);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			taService.approveProduct(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			taService.approveProduct(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("administration.registryProduct")
	public static void paging(Paging page) {
		log.debug("paging. page: " + page);

		page.addParams("ps.productCode||ps.name", page.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = taService.pagingProductSetup(page);
		renderJSON(page);
	}

	@Check("administration.registryProduct")
	public static void isPayableTradeByProductCode(String productCode, String sellingAgent) {
		log.debug("isPayableTradeByProductCode. productCode: " + productCode + " sellingAgent: " + sellingAgent);

		Long sellingAgentKey = Long.parseLong(sellingAgent);
		Boolean isPayable = taService.isPayableTradeByProductCode(productCode, sellingAgentKey);
		renderJSON(isPayable);
	}
}