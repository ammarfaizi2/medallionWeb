package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.gson.JsonParseException;
import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.CsAccountCharge;
import com.simian.medallion.model.CsChargeItem;
import com.simian.medallion.model.CsChargeMaster;
import com.simian.medallion.model.CsChargeOverride;
import com.simian.medallion.model.CsChargeOverrideTier;
import com.simian.medallion.model.CsChargeProfile;
import com.simian.medallion.model.CsPortfolio;
import com.simian.medallion.model.CsSubAccount;
import com.simian.medallion.model.GnBranch;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScPriceSetup;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.ChargeProfilePickSerializer;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.AccountSearchParameters;

@With(Secure.class)
public class Accounts extends MedallionController {
	private static Logger log = Logger.getLogger(Accounts.class);

	public static final String DEFAULT_PRICE_GROUP = "priceGroup.default";

	@Before(only = { "list", "dedupe" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

	}

	@Before(only = { "entry", "view", "edit", "confirming", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> chargesType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_TYPE);
		renderArgs.put("chargesType", chargesType);

		List<SelectItem> securityPriceType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._SECURITY_PRICE_TYPE);
		renderArgs.put("securityPriceType", securityPriceType);

		List<SelectItem> costMethod = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._COST_METHOD);
		renderArgs.put("costMethod", costMethod);

		List<SelectItem> invoiceCharge = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INVOICE_CHARGE);
		renderArgs.put("invoiceCharge", invoiceCharge);

		List<SelectItem> chargeCategory = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_CATEGORY);
		renderArgs.put("chargeCategory", chargeCategory);

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		renderArgs.put("sendOption", UIHelper.sendOptionOperators());

		GnLookup defaultKSEIDepository = generalService.getLookup(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST);
		renderArgs.put("defaultkseidepositorycode", defaultKSEIDepository.getLookupCode());

		renderArgs.put("accountcategoryksei", LookupConstants.DEPOSITORY_CODE_KSEI_CBEST);

		List<SelectItem> fundGuaranteeType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._FUND_GUARANTEE_TYPE);
		renderArgs.put("fundGuaranteeType", fundGuaranteeType);

		GnLookup fundGuaranteeType1 = generalService.getLookup(LookupConstants._FUND_GUARANTEE_TYPE_1);
		renderArgs.put("fundGuaranteeType1", fundGuaranteeType1);

		GnLookup fundGuaranteeType2 = generalService.getLookup(LookupConstants._FUND_GUARANTEE_TYPE_2);
		renderArgs.put("fundGuaranteeType2", fundGuaranteeType2);

		List<SelectItem> portfolioReport = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PORTFOLIO_REPORT);
		renderArgs.put("portfolioReport", portfolioReport);

		GnLookup portfolioReport1 = generalService.getLookup(LookupConstants._PORTFOLIO_REPORT_1);
		renderArgs.put("portfolioReport1", portfolioReport1);

		GnLookup portfolioReport2 = generalService.getLookup(LookupConstants._PORTFOLIO_REPORT_2);
		renderArgs.put("portfolioReport2", portfolioReport2);

		List<SelectItem> accountCashUsedOpt = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCOUNT_CASH);
		renderArgs.put("accountCashUsedOpt", accountCashUsedOpt);

	}

	public static void dedupe() {
		log.debug("dedupe. ");

		String mode = "view";
		String param = "cust-acct";
		String breadcrumb = "Register";
		render("Accounts/list.html", mode, param, breadcrumb);
	}

	/*
	 * @Check("maintenance.account.list") public static void list(String mode,
	 * String param) { if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_EDIT)); }
	 * else { flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_VIEW)); }
	 * if (param != null){ if (param.equals(UIConstants.PARAM_CHARGE_OVERRIDE))
	 * { flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants
	 * .CS_ACCOUNT_CHARGE_OVERRIDE)); } } render(mode, param); }
	 */

	@Check("maintenance.account.list")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		AccountSearchParameters params = new AccountSearchParameters();

		if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_VIEW));
		}
		if (param != null) {
			if (param.equals(UIConstants.PARAM_CHARGE_OVERRIDE)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_CHARGE_OVERRIDE));
			}
		}
		renderTemplate("Accounts/list.html", params, mode, param);
	}

	public static void search(AccountSearchParameters params) {
		log.debug("search. params: " + params);

		String param = params.param;
		List<CsAccount> accounts = accountService.searchAccount(UIHelper.withOperator(params.accountSearchNo, params.accountNoOperator), UIHelper.withOperator(params.accountSearchName, params.accountNameOperator));
		CsChargeProfile chargeProfile = new CsChargeProfile();
		String profileName = "";
		Map<Long, CsChargeProfile> chargeMap = new HashMap<Long, CsChargeProfile>();
		for (CsAccount account : accounts) {
			if (account.getChargeProfile() != null) {
				Long chargeKey = account.getChargeProfile().getChargeProfileKey();
				chargeProfile = chargeMap.get(chargeKey);
				if (chargeProfile == null) {
					log.debug("charge profile with key:" + chargeKey + " not loaded yet, fetching from database now..");
					chargeProfile = generalService.getChargeProfile(chargeKey);
					chargeMap.put(chargeKey, chargeProfile);
				} else {
					log.debug("already loaded charge profile with key:" + chargeKey);
				}
				account.setChargeProfile(chargeProfile);
			}
		}
		chargeMap.clear();
		render("Accounts/grid.html", accounts, param, profileName);
	}

	public static void pagingAccount(Paging page, AccountSearchParameters param) {
		log.debug("pagingAccount. page: " + page + " param: " + param);

		if (param.isQuery()) {

			page.addParams("a.accountNo", param.accountNoOperator, UIHelper.withOperator(param.accountSearchNo, param.accountNoOperator));
			page.addParams("a.name", param.accountNameOperator, UIHelper.withOperator(param.accountSearchName, param.accountNameOperator));

			page.addParams("1", page.EQUAL, 1);

			page.addParams(Helper.searchAll("(a.accountNo||a.name||a.balance)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = accountService.pagingAccounts(page);
		}
		renderJSON(page);
	}

	@Check("maintenance.account.register")
	public static void entry(long customerKey, String param) {
		log.debug("entry. customerKey: " + customerKey + " param: " + param);

		renderArgs.put("confirming", false);
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		boolean dataEntry = false;
		CsAccount account = new CsAccount();
		CfMaster customer = customerService.getCustomer(customerKey);
		account.setCustomer(customer);
		Long custKey = account.getCustomer().getCustomerKey();
		account.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(customerKey);
		// account.setDefaultPriceGroup(generalService.getLookup(LookupConstants.SECURITY_PRICE_GROUP_IDX));
		GnSystemParameter sysParam = generalService.getSystemParameter(DEFAULT_PRICE_GROUP);
		account.setDefaultPriceGroup(generalService.getLookup(sysParam.getValue()));
		log.debug("ADDRESS TYPE = " + addressType);
		String accounts = null;
		String chargeOverrides = null;

		String chargeMasterss = null;
		String chargeProfiles = null;

		try {
			chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
			JsonHelper json = new JsonHelper().getAccountSerializer();
			accounts = json.serialize(account);
			log.debug("isi accounts " + accounts);
			// accounts = json.writeValueAsString(account);
			if (account.getChargeOverrides() == null) {
				account.setChargeOverrides(new HashSet<CsChargeOverride>(0));
			}
			// chargeOverrides = json.serialize(account.getChargeOverrides());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		CsChargeProfile chargeProfile = new CsChargeProfile();
		List<CsChargeMaster> chargeMasters = new ArrayList<CsChargeMaster>();
		List<CsChargeMaster> chargeMasterTable = new ArrayList<CsChargeMaster>();
		Boolean isChargeProfile = true;

		if (account.getChargeProfile() != null) {
			chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());

		} else {
			// isChargeProfile = false;
			chargeProfile = generalService.getChargeProfileByDefault();
		}
		chargeMasters = generalService.listChargeItemForChargeOverride(chargeProfile.getChargeProfileKey());
		chargeMasterTable = generalService.listChargeItemForChargeMaster(chargeProfile.getChargeProfileKey());
		log.debug("isi chargeProfile " + chargeProfile.getChargeItems().size());
		try {

			JsonHelper json = new JsonHelper().getChargeItemTableSerializer();
			chargeProfiles = json.serialize(chargeProfile.getChargeItems());
			log.debug("chargeProfiles = " + chargeProfiles);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		try {
			chargeMasterss = json.writeValueAsString(chargeMasterTable);
			log.debug("chargeMasterss = " + chargeMasterss);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		GnSystemParameter defaultThirp = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_CUSTODY);
		GnSystemParameter defaultCost = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_COST);
		GnSystemParameter defaultBranch = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_BRANCH);
		GnSystemParameter defaultExchangeProfile = generalService.getSystemParameter(SystemParamConstants.PARAM_CURRRENCY_PROFILE);
		log.debug(">> " + defaultBranch.getValue());
		GnBranch branch = generalService.getBranch(Long.parseLong(defaultBranch.getValue()));
		GnLookup lookup = generalService.getLookupReference(defaultCost.getValue());
		GnThirdParty thirdP = generalService.getThirdParty(Long.parseLong(defaultThirp.getValue()));
		GnLookup exchangeProfile = generalService.getLookupReference(defaultExchangeProfile.getValue());

		account.setBranch(branch);
		account.setCostMethod(lookup);
		account.setCustodianBank(thirdP);
		account.setCurrencyProfile(exchangeProfile);
		account.setNetting(true);
		if (account.getFundGuaranteeType() != null) {
			account.setIsFundGuarantee(true);
		} else {
			account.setIsFundGuarantee(false);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
		render("Accounts/detail.html", mode, param, accounts, account, customer, addressType, custKey, dataEntry, chargeOverrides, chargeMasters, isChargeProfile, chargeMasterss, chargeProfiles);
	}

	@Check("maintenance.account.view")
	public static void view(Long id, String param) {
		log.debug("view. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsAccount account = accountService.getAccountCopy(id);
		// logger.debug("ukuranny nya : "+ tes);
		// List<ChargeMaster> chargeMaster =
		// List<ChargeOverride> chargeOverrides =
		// accountService.listChargeOverrides(custodyAccountKey);
		Long size = accountService.getCountChargeOverride(id);
		log.debug("SIze" + size);

		Long key = null;
		if (account.getChargeProfile() != null) {
			key = account.getChargeProfile().getChargeProfileKey();
		}
		Long custKey = account.getCustomer().getCustomerKey();
		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(custKey);

		String accounts = null;
		String chargeOverrides = null;

		String chargeMasterss = null;
		String chargeProfiles = null;

		try {
			chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
			JsonHelper json = new JsonHelper().getAccountSerializer();
			accounts = json.serialize(account);
			if (account.getChargeOverrides() == null) {
				account.setChargeOverrides(new HashSet<CsChargeOverride>(0));
			}
			// chargeOverrides = json.serialize(account.getChargeOverrides());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (param != null) {
			if (param.equals("register-cust-acct")) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
			} else if (param.equals(UIConstants.DISPLAY_MODE_VIEW)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_VIEW));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_EDIT));
			}
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_VIEW));
		}

		CsChargeProfile chargeProfile = new CsChargeProfile();
		List<CsChargeMaster> chargeMasters = new ArrayList<CsChargeMaster>();
		List<CsChargeMaster> chargeMasterTable = new ArrayList<CsChargeMaster>();
		Boolean isChargeProfile = true;
		log.debug("isi account.getChargeProfile() " + account.getChargeProfile());
		if (account.getChargeProfile() != null) {
			chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
			chargeMasters = generalService.listChargeItemForChargeOverride(chargeProfile.getChargeProfileKey());
			chargeMasterTable = generalService.listChargeItemForChargeMaster(chargeProfile.getChargeProfileKey());

		} else {
			isChargeProfile = false;
			chargeProfile = generalService.getChargeProfileByDefault();
		}
		List<CsChargeItem> cci = new ArrayList<CsChargeItem>();
		try {

			JsonHelper json = new JsonHelper().getChargeItemTableSerializer();
			if (account.getChargeProfile() != null) {
				chargeProfiles = json.serialize(chargeProfile.getChargeItems());
			} else {
				chargeProfiles = json.serialize(cci);
			}

			log.debug("chargeProfiles = " + chargeProfiles);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		if (account.getChargeOverrides().size() == 0) {
			account.setIsCharge(false);
		} else {
			account.setIsCharge(true);
			chargeMasterTable = generalService.listChargeItemForChargeMaster(chargeProfile.getChargeProfileKey());
		}

		if (account.getFundGuaranteeType() != null) {
			account.setIsFundGuarantee(true);
		} else {
			account.setIsFundGuarantee(false);
		}

		try {
			chargeMasterss = json.writeValueAsString(chargeMasterTable);
			log.debug("chargeMasterss = " + chargeMasterss);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		// logger.debug("SIZE nya : "+ account.getChargeOverrides().size());
		render("Accounts/detail.html", accounts, account, mode, param, size, key, custKey, addressType, chargeOverrides, chargeMasters, isChargeProfile, chargeMasterss, chargeProfiles);
	}

	@Check("maintenance.account.edit")
	public static void edit(Long id, String param) {
		log.debug("edit. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsAccount account = null;
		String accounts = null;
		String status = null;
		Long size = null;
		Long key = null;
		Long custKey = null;
		List<SelectItem> addressType = null;
		String chargeOverrides = null;
		String chargeMasterss = null;
		String chargeProfiles = null;
		// Long valueBankAccKey = null;
		if (id != null) {
			account = accountService.getAccountCopy(id);
			size = accountService.getCountChargeOverride(id);

			if (account.getChargeProfile() != null) {
				key = account.getChargeProfile().getChargeProfileKey();
			}

			custKey = account.getCustomer().getCustomerKey();

			addressType = customerService.listAddressTypeByCustomerAsSelectItem(account.getCustomer().getCustomerKey());

			if (account.getCustomer().getEmail() != null) {
				log.debug("from edit >>> account.customer.email = " + account.getCustomer().getEmail());
			}

			status = account.getRecordStatus();

			GnSystemParameter defaultBranch = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_BRANCH);
			GnBranch branch = generalService.getBranch(Long.parseLong(defaultBranch.getValue()));
			account.setBranch(branch);

			if (account != null) {
				try {
					chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
					JsonHelper json = new JsonHelper().getAccountSerializer();
					accounts = json.serialize(account);
					log.debug("DATA = " + accounts);
					log.debug("chargeOverrides = " + chargeOverrides);
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}

		CsChargeProfile chargeProfile = new CsChargeProfile();
		List<CsChargeMaster> chargeMasters = new ArrayList<CsChargeMaster>();
		List<CsChargeMaster> chargeMasterTable = new ArrayList<CsChargeMaster>();
		Boolean isChargeProfile = true;

		if (account.getChargeProfile() != null) {
			chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
			chargeMasters = generalService.listChargeItemForChargeOverride(chargeProfile.getChargeProfileKey());
			chargeMasterTable = generalService.listChargeItemForChargeMaster(chargeProfile.getChargeProfileKey());

		} else {

			chargeProfile = generalService.getChargeProfileByDefault();

		}

		if (account.getChargeOverrides().size() == 0) {
			account.setIsCharge(false);
		} else {
			account.setIsCharge(true);
			chargeMasterTable = generalService.listChargeItemForChargeMaster(chargeProfile.getChargeProfileKey());
		}

		if (account.getFundGuaranteeType() != null) {
			account.setIsFundGuarantee(true);
		} else {
			account.setIsFundGuarantee(false);
		}

		List<CsChargeItem> cci = new ArrayList<CsChargeItem>();
		try {

			JsonHelper json = new JsonHelper().getChargeItemTableSerializer();
			if (account.getChargeProfile() != null) {
				chargeProfiles = json.serialize(chargeProfile.getChargeItems());
			} else {
				chargeProfiles = json.serialize(cci);
			}

			log.debug("chargeProfiles = " + chargeProfiles);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		try {
			chargeMasterss = json.writeValueAsString(chargeMasterTable);
			log.debug("chargeMasterss = " + chargeMasterss);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		GnSystemParameter defaultThirp = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_CUSTODY);
		GnSystemParameter defaultCost = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_COST);

		GnSystemParameter defaultExchangeProfile = generalService.getSystemParameter(SystemParamConstants.PARAM_CURRRENCY_PROFILE);

		GnLookup lookup = generalService.getLookupReference(defaultCost.getValue());
		GnThirdParty thirdP = generalService.getThirdParty(Long.parseLong(defaultThirp.getValue()));
		GnLookup exchangeProfile = generalService.getLookupReference(defaultExchangeProfile.getValue());

		account.setCostMethod(lookup);
		account.setCustodianBank(thirdP);
		account.setCurrencyProfile(exchangeProfile);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_EDIT));
		render("Accounts/detail.html", accounts, account, chargeProfile, chargeOverrides, mode, param, size, key, custKey, addressType, status, chargeMasters, id, isChargeProfile, chargeMasterss, chargeProfiles);
	}

	public static void chargeOverride(Long id, String status) {
		log.debug("chargeOverride. id: " + id + " status: " + status);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsAccount account = null;
		String chargeOverrides = null;
		if (id != null) {
			account = accountService.getAccount(id);
			// if (!(account.getRecordStatus().trim().equals("A"))) {
			// mode = UIConstants.DISPLAY_MODE_VIEW;
			// }

			if (account != null) {
				try {
					chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}

		CsChargeProfile chargeProfile = new CsChargeProfile();
		List<CsChargeMaster> chargeMasters = new ArrayList<CsChargeMaster>();
		Boolean isChargeProfile = true;
		if (account.getChargeProfile() != null) {
			chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());
		} else {
			isChargeProfile = false;
			chargeProfile = generalService.getChargeProfileByDefault();

		}
		chargeMasters = generalService.listChargeItemForChargeOverride(chargeProfile.getChargeProfileKey());
		if (status.equals("Updated")) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_CHARGE_OVERRIDE));
		render("ChargeOverrides/overrideCharge.html", account, chargeProfile, chargeOverrides, chargeMasters, mode, id, status, isChargeProfile);
	}

	public static void save(String body, String mode, String param, Long id, Long key, Long size, Long custKey, boolean dataEntry) {
		log.debug("save. body: " + body + " mode: " + mode + " param: " + param + " id: " + id + " key: " + key + " size: " + size + " custKey: " + custKey + " dataEntry: " + dataEntry);

		try {
			log.debug("Data entry Save = " + dataEntry);
			CsAccount account = json.readValue(body, CsAccount.class);
			log.debug("DATA = " + body);

			log.debug("panjang override " + account.getChargeOverrides().size());
			log.debug("ACCOUNT NO = " + account.getAccountNo());
			log.debug("SIZE Sec Price = " + account.getPriceGroupSetups());
			log.debug("size set acc = " + account.getBankAccounts().size());
			log.debug("size account Charge = " + account.getAccountCharges().size());
			log.debug("account.customerName = " + account.getCustomer().getCustomerName());
			log.debug("account.customerNo = " + account.getCustomer().getCustomerNo());
			log.debug("account.customerKey = " + account.getCustomer().getCustomerKey());
			log.debug("value settlement Account = " + account.getSettlementAccount().getBankAccountKey());
			log.debug("value Tax Profile = " + account.getTxProfile().getTaxProfileCode());
			log.debug("value account.isFundGuarantee = " + account.getIsFundGuarantee());
			log.debug("value account.isFundGuarantee.code = " + account.getFundGuaranteeType().getLookupCode());

			log.debug("CONTACT = " + account.getBillingAddress().getContactKey());
			if (account.getBillingAddress().getContactKey() == null) {
				account.setBillingAddress(null);
			}
			if (account != null) {

				// if (account.getSecurityTypes() != null) {
				// Set<ScTypeMaster> securityTypes = account.getSecurityTypes();
				// for (ScTypeMaster securityType : securityTypes) {
				// if (securityType != null) {
				// account.getSecurityTypes().add(securityType);
				// }
				// }
				// }
				if (account.getPriceGroupSetups() != null) {
					Set<ScPriceSetup> priceSetups = account.getPriceGroupSetups();
					for (ScPriceSetup priceSetup : priceSetups) {
						if (priceSetup != null)
							account.getPriceGroupSetups().add(priceSetup);
					}
				}

				if (account.getBankAccounts() != null) {
					Set<BnAccount> bankAccounts = account.getBankAccounts();
					for (BnAccount bankAccount : bankAccounts) {
						if (bankAccount != null) {
							account.getBankAccounts().add(bankAccount);
						}
					}
				}

				if (account.getAccountCharges() != null) {
					Set<CsAccountCharge> accountCharges = account.getAccountCharges();
					for (CsAccountCharge accountCharge : accountCharges) {
						if (accountCharge != null) {
							account.getAccountCharges().add(accountCharge);
						}
					}
				}

				if (account.getSubAccounts() != null) {
					Set<CsSubAccount> subAccounts = account.getSubAccounts();
					for (CsSubAccount csSubAccount : subAccounts) {
						if (csSubAccount != null) {
							account.getSubAccounts().add(csSubAccount);
						}
					}
				}

				if (account.getChargeOverrides() != null) {
					Set<CsChargeOverride> accountOverride = account.getChargeOverrides();

					for (int i = 0; i < accountOverride.size(); i++) {
						for (CsChargeOverride csc : accountOverride) {

							if (account.getCustodyAccountKey() != null) {
								csc.setAccount(account);
							}

							if (csc.getChargeOverrideTiers() != null) {
								Set<CsChargeOverrideTier> cscTier = csc.getChargeOverrideTiers();
								int j = 1;
								for (CsChargeOverrideTier chargeTier : cscTier) {
									if (csc.getChargeOverrideKey() != null) {
										chargeTier.getId().setChargeOverrideKey(csc.getChargeOverrideKey());
									}

									if (chargeTier.getId().getRowNumber() == 0) {
										chargeTier.getId().setRowNumber(j);
									}

									j++;
									csc.getChargeOverrideTiers().add(chargeTier);
								}
							}
							account.getChargeOverrides().add(csc);
						}
					}

				}

				id = account.getCustodyAccountKey();
				serializerService.serialize(session.getId(), id, account);
			}

		} catch (JsonParseException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		}
	}

	@Check("maintenance.account.save")
	public static void confirming(Long id, String mode, Long key, Long size, Long custKey, String status, boolean dataEntry) {
		log.debug("confirming. id: " + id + " mode: " + mode + " key: " + key + " size: " + size + " custKey: " + custKey + " status: " + status + " dataEntry: " + dataEntry);

		// logger.debug("MODE IN CONFIRMING = " +mode);
		// logger.debug("dataEntry confirming =" +dataEntry);
		renderArgs.put("confirming", true);
		// renderArgs.put("back", false);
		Boolean isChargeProfile = true;
		CsAccount account = serializerService.deserialize(session.getId(), id, CsAccount.class);
		// logger.debug("account.getcustomer = " +account.getCustomer());
		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(account.getCustomer().getCustomerKey());

		String accounts = null;
		String chargeOverrides = null;
		String chargeMasterss = null;
		String chargeProfiles = null;
		try {
			accounts = json.writeValueAsString(account);
			chargeOverrides = json.writeValueAsString(account.getChargeOverrides());

		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		CsChargeProfile chargeProfile = new CsChargeProfile();
		chargeProfile = account.getChargeProfile();
		// logger.debug("panjang overide "+account.getChargeOverrides().size());
		List<CsChargeMaster> chargeMasters = new ArrayList<CsChargeMaster>();
		if (account.getChargeProfile() != null) {
			chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());

		}

		try {
			chargeMasters = generalService.listChargeItemForChargeMaster(chargeProfile.getChargeProfileKey());
			chargeMasterss = json.writeValueAsString(chargeMasters);
			JsonHelper json = new JsonHelper().getChargeItemTableSerializer();
			chargeProfiles = json.serialize(chargeProfile.getChargeItems());
			// logger.debug("isi chargeMasterss "+chargeMasterss);
			// logger.debug("isi chargeProfiles "+chargeProfiles);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_EDIT));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
		}
		String fromMethod = "approval";
		log.debug("default " + account.getChargeProfile().getIsDefault());
		render("Accounts/detail.html", accounts, account, chargeProfile, chargeOverrides, mode, key, size, custKey, addressType, status, dataEntry, isChargeProfile, chargeMasters, chargeMasterss, chargeProfiles, fromMethod);
	}

	@Check("maintenance.account.save")
	public static void confirm(Long id, String mode, CsAccount account, Long key, Long size, Long custKey, Long valueSettlement, String status) {
		log.debug("confirm. id: " + id + " mode: " + mode + " account: " + account + " key: " + key + " size: " + size + " custKey: " + custKey + " valueSettlement: " + valueSettlement + " status: " + status);

		account = serializerService.deserialize(session.getId(), id, CsAccount.class);
		valueSettlement = account.getSettlementAccount().getBankAccountKey();
		// if (account.getSecurityTypes() != null)
		// account.setSecurityTypes(new
		// HashSet<ScTypeMaster>(account.getSecurityTypes()));

		if (account.getPriceGroupSetups() != null)
			account.setPriceGroupSetups(new HashSet<ScPriceSetup>(account.getPriceGroupSetups()));

		if (account.getBankAccounts() != null) {
			account.setBankAccounts(new HashSet<BnAccount>(account.getBankAccounts()));
			for (BnAccount bankAccount : account.getBankAccounts()) {
				if (bankAccount != null) {
					if (valueSettlement != null) {
						if (bankAccount.getBankAccountKey().equals(valueSettlement)) {
							valueSettlement = bankAccount.getBankAccountKey();
							account.setSettlementAccount(bankAccount);
						}
						account.getBankAccounts().add(bankAccount);
					} else {
						flash.error("Account No : ' " + account.getAccountNo() + " ' " + ExceptionConstants.CUSTODY_ACCOUNT_DEFAULT_SETTLEMENT_ACCOUNT_NULL);
						String error = "error";
						List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(account.getCustomer().getCustomerKey());
						renderText(error);
						renderText(addressType);

					}
				}
				// account.setSettlementAccount(bankAccount);
			}
		}

		if (account.getAccountCharges() != null)
			account.setAccountCharges(new HashSet<CsAccountCharge>(account.getAccountCharges()));

		if (account.getSubAccounts() != null)
			account.setSubAccounts(new HashSet<CsSubAccount>(account.getSubAccounts()));

		if (account.getChargeOverrides() != null) {
			for (CsChargeOverride csc : account.getChargeOverrides()) {
				if (csc.getChargeOverrideTiers() != null) {
					csc.setChargeOverrideTiers(new HashSet<CsChargeOverrideTier>(csc.getChargeOverrideTiers()));
					for (CsChargeOverrideTier cscTier : csc.getChargeOverrideTiers()) {
						log.debug("key " + cscTier.getChargeOverride().getChargeOverrideKey());
					}
				}
			}
			account.setChargeOverrides(new HashSet<CsChargeOverride>(account.getChargeOverrides()));
		}

		account.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));

		try {
			String menuCode = null;
			log.debug(">>> MODE = " + mode);
			if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
				menuCode = MenuConstants.CS_ACCOUNT_ENTRY;
			} else {
				menuCode = MenuConstants.CS_ACCOUNT_EDIT;
			}

			/****** VALIDATE DATA DUPLICATE ******/

			List<CsAccount> accounts = accountService.listAccounts(UIConstants.SIMIAN_BANK_ID);
			if (account.getCustodyAccountKey() == null) {
				for (CsAccount accountInDb : accounts) {
					if (accountInDb.getAccountNo().trim().equals(account.getAccountNo().trim())) {
						List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(account.getCustomer().getCustomerKey());
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
						flash.error("Account No : ' " + account.getAccountNo() + " ' " + Messages.get(ExceptionConstants.DATA_DUPLICATE));
						String error = "error";
						renderText(error);
						renderText(addressType);
					}
				}
			} else {
				CsAccount oldAccount = accountService.getAccount(account.getCustodyAccountKey());
				if (!account.getAccountNo().trim().equals(oldAccount.getAccountNo().trim())) {
					for (CsAccount accountInDb : accounts) {
						if (accountInDb.getAccountNo().trim().equals(account.getAccountNo().trim())) {
							List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(account.getCustomer().getCustomerKey());
							flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_EDIT));
							flash.error("Account No : ' " + account.getAccountNo() + " ' " + Messages.get(ExceptionConstants.DATA_DUPLICATE));
							String error = "error";
							renderText(error);
							renderText(addressType);
						}
					}
				}
			}
			if (account.getIsCharge() == null) {
				account.setIsCharge(false);
			}
			if (account.getIsCharge() == false) {
				account.setChargeOverrides(new HashSet<CsChargeOverride>());
				if (account.getDcProfil()) {
					account.setChargeProfile(null);
				}
			}

			if (account.getIsFundGuarantee() == null) {
				account.setIsFundGuarantee(false);
			}

			if (account.getIsFundGuarantee() == false) {
				account.setFundGuaranteeType(null);
			}

			GnThirdParty thirdP = generalService.getThirdParty(Long.parseLong(generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_CUSTODY).getValue()));
			account.setCustodianBank(thirdP);

			accountService.saveAccount(menuCode, account, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));

		} catch (MedallionException e) {
			flash.error("Account No : ' " + account.getAccountNo() + " ' " + Messages.get(e.getMessage()));
			String error = "error";
			List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(account.getCustomer().getCustomerKey());
			if (mode.equals(UIConstants.DISPLAY_MODE_EDIT)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_EDIT));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
			}
			renderText(error);
			renderText(addressType);
			// boolean confirming = true;
			// render("Accounts/detail.html", accounts, mode, confirming, key,
			// size, custKey, addressType);
		}
	}

	@Check("maintenance.account.save")
	public static void back(Long id, String mode, Long custKey, Long key, Long size, String status, boolean dataEntry) {
		log.debug("back. id: " + id + " mode: " + mode + " custKey: " + custKey + " key: " + key + " size: " + size + " status: " + status + " dataEntry: " + dataEntry);

		// renderArgs.put("back", true);
		CsAccount account = serializerService.deserialize(session.getId(), id, CsAccount.class);
		Boolean isChargeProfile = true;

		if (account.getRecordStatus() != null)
			status = account.getRecordStatus();

		List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(account.getCustomer().getCustomerKey());

		CsChargeProfile chargeProfile = new CsChargeProfile();
		chargeProfile = account.getChargeProfile();
		log.debug("panjang overide " + account.getChargeOverrides().size());
		List<CsChargeMaster> chargeMasters = new ArrayList<CsChargeMaster>();
		if (account.getChargeProfile() != null) {
			chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());

		}

		String accounts = null;
		String chargeOverrides = null;
		String chargeMasterss = null;
		String chargeProfiles = null;
		try {
			accounts = json.writeValueAsString(account);
			chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
			chargeMasters = generalService.listChargeItemForChargeMaster(chargeProfile.getChargeProfileKey());
			chargeMasterss = json.writeValueAsString(chargeMasters);
			JsonHelper json = new JsonHelper().getChargeItemTableSerializer();
			chargeProfiles = json.serialize(chargeProfile.getChargeItems());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_ENTRY));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ACCOUNT_EDIT));
		}
		account.setChargeProfile(chargeProfile);
		log.debug("def " + account.getChargeProfile().getIsDefault());
		render("Accounts/detail.html", accounts, account, mode, size, key, custKey, addressType, status, dataEntry, isChargeProfile, chargeOverrides, chargeMasterss, chargeProfiles);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			String accounts = null;
			String chargeOverrides = null;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CsAccount account = json.readValue(maintenanceLog.getNewData(), CsAccount.class);

			// Set<ScTypeMaster> securityTypes = account.getSecurityTypes();
			Set<ScPriceSetup> priceGroupSetups = account.getPriceGroupSetups();
			Set<BnAccount> bankAccounts = account.getBankAccounts();
			Set<CsAccountCharge> accountCharges = account.getAccountCharges();
			Set<CsSubAccount> subAccounts = account.getSubAccounts();
			log.debug("isi account.getChargeOverrides() " + account.getChargeOverrides());
			if (account.getChargeOverrides() == null) {
				account.setChargeOverrides(new HashSet<CsChargeOverride>(0));
			}
			List<SelectItem> addressType = customerService.listAddressTypeByCustomerAsSelectItem(account.getCustomer().getCustomerKey());

			String chargeMasterss = null;
			String chargeProfiles = null;
			try {
				accounts = json.writeValueAsString(account);
				log.debug("DATA APP = " + accounts);
				chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
				log.debug("isi chargeOverrides " + chargeOverrides);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}

			CsChargeProfile chargeProfile = new CsChargeProfile();
			chargeProfile = account.getChargeProfile();
			log.debug("panjang overide " + account.getChargeOverrides().size());
			if (account.getChargeOverrides().size() == 0) {
				account.setIsCharge(false);
			} else {
				account.setIsCharge(true);
			}
			List<CsChargeMaster> chargeMasters = new ArrayList<CsChargeMaster>();
			if (account.getChargeProfile() != null) {
				chargeProfile = generalService.getChargeProfile(account.getChargeProfile().getChargeProfileKey());

			}
			List<CsChargeItem> cci = new ArrayList<CsChargeItem>();
			try {
				if (account.getChargeProfile() != null) {
					chargeMasters = generalService.listChargeItemForChargeMaster(chargeProfile.getChargeProfileKey());
				}
				chargeMasterss = json.writeValueAsString(chargeMasters);
				JsonHelper json = new JsonHelper().getChargeItemTableSerializer();
				if (account.getChargeProfile() != null) {
					chargeProfiles = json.serialize(chargeProfile.getChargeItems());
				} else {
					chargeProfiles = json.serialize(cci);
				}

				log.debug("isi chargeMasterss " + chargeMasterss);
				log.debug("isi chargeProfiles " + chargeProfiles);

			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			if (account.getFundGuaranteeType() != null) {
				account.setIsFundGuarantee(true);
			} else {
				account.setIsFundGuarantee(false);
			}

			String fromMethod = "approval";
			render("Accounts/approval.html", accounts, mode, account, taskId, operation, priceGroupSetups, bankAccounts, accountCharges, chargeOverrides, maintenanceLogKey, from, addressType, chargeProfile, chargeMasters, chargeOverrides, chargeMasterss, chargeProfiles, subAccounts, fromMethod);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			accountService.approveAccount(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
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
			accountService.approveAccount(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);
			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void getPortfolio(String accountNo) {
		log.debug("getPortfolio. accountNo: " + accountNo);

		boolean value = false;
		log.debug("isi accountNo " + accountNo);
		CsAccount account = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, accountNo);
		log.debug("isi account " + account);
		if (account != null) {
			List<CsPortfolio> csPortfolio = accountService.listPortfolioByCustody(account.getCustodyAccountKey());
			if (csPortfolio != null) {
				for (CsPortfolio csp : csPortfolio) {
					if (csp.getPortfolioQuantity() != null) {
						if (csp.getPortfolioQuantity().doubleValue() > 0) {
							value = true;
						}
					}

					if (csp.getTotalQuantity() != null) {
						if (csp.getTotalQuantity().doubleValue() > 0) {
							value = true;
						}
					}
				}
			}
		}
		log.debug("isi value " + value);
		renderText(value);
	}

	public static void getChargeProfileEntry(Long key) {
		log.debug("getChargeProfileEntry. key: " + key);

		CsChargeProfile chargeProfile = null;

		if (key != null) {
			chargeProfile = generalService.getChargeProfile(key);
		}

		renderJSON(chargeProfile, new ChargeProfilePickSerializer());
	}

	public static void getChargeProfile(Long key) {
		log.debug("getChargeProfile. key: " + key);

		CsChargeProfile chargeProfiless = new CsChargeProfile();

		if (key != null) {
			chargeProfiless = generalService.getChargeProfile(key);
		}
		String chargeProfiles = null;
		try {
			JsonHelper json = new JsonHelper().getChargeItemTableSerializer();
			chargeProfiles = json.serialize(chargeProfiless.getChargeItems());
			// chargeProfile = json.serialize(chargeProfiles);
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}
		renderJSON(chargeProfiles);
	}

	public static void getChargeMaster(Long key) {
		log.debug("getChargeMaster. key: " + key);

		List<CsChargeMaster> chargeMasters = new ArrayList<CsChargeMaster>();

		if (key != null) {
			chargeMasters = generalService.listChargeItemForChargeMaster(key);
		}
		String chargeMasterss = null;
		try {
			chargeMasterss = json.writeValueAsString(chargeMasters);
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}
		renderJSON(chargeMasterss);
	}

	public static void getChargeOverides(Long key) {
		log.debug("getChargeOverides. key: " + key);

		CsAccount account = null;
		if (key == null) {
			account = new CsAccount();
		} else {
			account = accountService.getAccount(key);
		}

		String chargeOverrides = null;
		try {
			if (account != null) {
				chargeOverrides = json.writeValueAsString(account.getChargeOverrides());
				log.debug(" getChargeOverides " + chargeOverrides);
			}
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}

		if (chargeOverrides != null) {
			renderJSON(chargeOverrides);
		}
	}

	public static void isChargeOverides(Long key) {
		log.debug("isChargeOverides. key: " + key);

		boolean value = false;
		CsAccount account = null;

		if (key == null) {
			account = new CsAccount();
		} else {
			account = accountService.getAccount(key);
		}

		if (account.getChargeOverrides().size() > 0) {
			value = true;
		}

		renderText(value);
	}

	public static void getChargeProfileDefault() {
		log.debug("getChargeProfileDefault. ");

		CsChargeProfile chargeProfiless = new CsChargeProfile();
		chargeProfiless = generalService.getChargeProfileByDefault();
		String chargeProfiles = null;
		try {
			JsonHelper json = new JsonHelper().getChargeProfileSerializer();
			chargeProfiles = json.serialize(chargeProfiless);
			log.debug("isi chargeProfiles " + chargeProfiles);
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}
		renderJSON(chargeProfiles);
	}

	public static void getSubAccountCode(String accountCategory) {
		log.debug("getSubAccountCode. accountCategory: " + accountCategory);

		Map<String, Object> result = new HashMap<String, Object>();
		GnLookup depositoryCode = null;
		depositoryCode = generalService.getLookup(accountCategory);
		String generatedCode = accountService.getSubAccountCode(depositoryCode);
		result.put("generatedsubaccountcode", generatedCode);

		renderJSON(result);
	}
	
	public static void isActiveAccountExist(String customerNo){
		boolean value = false;
		log.debug("isi customerNo "+customerNo);
		if (customerNo != null) {
			List<CsAccount> activeAccounts = accountService.getActiveAccountsByCustomer(customerNo);
			if (activeAccounts != null && activeAccounts.size() > 0 ) value = true;
		}
		log.debug("isi value "+value);
		renderText(value);
	}
	
	public static void isCustomerActive(String accountNo) {
		boolean value = false;
		log.debug("isi accountNo "+accountNo);
		if (accountNo != null) {
			List<CfMaster> activeCustomers = customerService.getActiveCustomerByAccount(accountNo);
			if (activeCustomers != null && activeCustomers.size() > 0 ) value = true;
		}
		log.debug("isi value "+value);
		renderText(value);
	}
}