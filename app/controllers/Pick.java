package controllers;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.PickCodeConstants;
import com.simian.medallion.helper.Tools;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.BnTransactionMaster;
import com.simian.medallion.model.CBestMessage;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.CpComplianceProfile;
import com.simian.medallion.model.CpComplianceRule;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.CsChargeMaster;
import com.simian.medallion.model.CsChargeProfile;
import com.simian.medallion.model.CsDailyHolding;
import com.simian.medallion.model.CsPortfolio;
import com.simian.medallion.model.CsPortfolioTransaction;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.CsTransactionMaster;
import com.simian.medallion.model.CsTransactionTemplate;
import com.simian.medallion.model.FaCoaMaster;
import com.simian.medallion.model.FaJournalTemplate;
import com.simian.medallion.model.FaMaster;
import com.simian.medallion.model.FaPostingProfile;
import com.simian.medallion.model.FaTransactionMaster;
import com.simian.medallion.model.GnApplicationDate;
import com.simian.medallion.model.GnBranch;
import com.simian.medallion.model.GnCurrency;
import com.simian.medallion.model.GnGroup;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMenu;
import com.simian.medallion.model.GnReportList;
import com.simian.medallion.model.GnReportLoader;
import com.simian.medallion.model.GnReportPdi;
import com.simian.medallion.model.GnTaxMaster;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnThirdPartyBank;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.GnWorkflowDetail;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.PickData;
import com.simian.medallion.model.PickParameters;
import com.simian.medallion.model.PickRow;
import com.simian.medallion.model.PlTransaction;
import com.simian.medallion.model.RgFeeTier;
import com.simian.medallion.model.RgInvestmentaccount;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgPortfolio;
import com.simian.medallion.model.RgProdEod;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgProductBnAccount;
import com.simian.medallion.model.ScActionTemplate;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.TdTransaction;
import com.simian.medallion.model.TdTransactionTemplate;
import com.simian.medallion.model.TxProfile;
import com.simian.medallion.model.UpdBatch;
import com.simian.medallion.model.UpdProfile;
import com.simian.medallion.model.UpdProfileDetail;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.PortfolioHolding;
import com.simian.medallion.vo.ReportParam;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;
import helpers.UIConstants;
import helpers.serializers.AccountPickSerializer;
import helpers.serializers.ActionTemplatePickSerializer;
import helpers.serializers.AnnouncementPickSerializer;
import helpers.serializers.BankAccountPickSerializer;
import helpers.serializers.BankAccountProductPickSerializer;
import helpers.serializers.BankTransactionMasterPickSerializer;
import helpers.serializers.BranchPickSerializer;
import helpers.serializers.CBestMessagePickSerializer;
import helpers.serializers.ChargeMasterPickSerializer;
import helpers.serializers.ChargeProfilePickSerializer;
import helpers.serializers.CoaMasterDownTrialBalacePickSerializer;
import helpers.serializers.CoaMasterPickSerializer;
import helpers.serializers.ComplianceProfilePickSerializer;
import helpers.serializers.ComplianceRulePickSerializer;
import helpers.serializers.CouponSchedulePickSerializer;
import helpers.serializers.CsAccountPickSerializer;
import helpers.serializers.CsPortfolioPickSerializer;
import helpers.serializers.CurrencyPickSerializer;
import helpers.serializers.CustomerPickSerializer;
import helpers.serializers.FaFundSetupPickSerializer;
import helpers.serializers.FaJournalTemplatePickSerializer;
import helpers.serializers.FaPostingProfilePickSerializer;
import helpers.serializers.FaTransactionMasterPickSerializer;
import helpers.serializers.FeeTierPickSerializer;
import helpers.serializers.GroupPickSerializer;
import helpers.serializers.InvestmentAccountPickSerializer;
import helpers.serializers.LookupPickSerializer;
import helpers.serializers.MenuPickSerializer;
import helpers.serializers.NavPickSerializer;
import helpers.serializers.PledgingPickSerializer;
import helpers.serializers.PortfolioHoldingPickSerializer;
import helpers.serializers.ProductDrnkPickSerializer;
import helpers.serializers.ProductPickSerializer;
import helpers.serializers.ReportLoaderPickSerializer;
import helpers.serializers.ReportMaintenancePickSerializer;
import helpers.serializers.ReportPdiPickSerializer;
import helpers.serializers.RgPortfolioPickSerializer;
import helpers.serializers.RgProdEodPickSerializer;
import helpers.serializers.RgProductBankAccountPickSerializer;
import helpers.serializers.ScCorporateAnnouncementSerializer;
import helpers.serializers.SecurityPickForChargeItemSerializer;
import helpers.serializers.SecurityPickSerializer;
import helpers.serializers.SecurityTypePickSerializer;
import helpers.serializers.TaxMasterPickSerializer;
import helpers.serializers.TblNamePickSerializer;
import helpers.serializers.TdMasterPickSerializer;
import helpers.serializers.ThirdPartyBankPickSerializer;
import helpers.serializers.ThirdPartyPickSerializer;
import helpers.serializers.TransactionForCertificatePickSerializer;
import helpers.serializers.TransactionForPickSerializer;
import helpers.serializers.TransactionMasterPickSerializer;
import helpers.serializers.TransactionTemplatePickSerializer;
import helpers.serializers.TxProfilePickSerializer;
import helpers.serializers.UpdBatchSerializer;
import helpers.serializers.UpdFilterSerializer;
import helpers.serializers.UpdProfileDetailPickSerializer;
import helpers.serializers.UpdProfilePickRolesSerializer;
import helpers.serializers.UpdProfilePickSerializer;
import helpers.serializers.UserPickSerializer;
import helpers.serializers.WorkflowDetailPickSerializer;
import play.cache.Cache;
import play.mvc.Http.Response;
import vo.DynamicValueVO;

public class Pick extends MedallionController {
	private static Logger log = Logger.getLogger(Pick.class);

	public static final String SPECIAL_CHAR = ",,";

	/*
	 * public static void accounts() {
	 * renderList(accountService.listAccountsAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Account For List
	public static void accounts() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT, null));
	}

	public static void account(String code) {
		CsAccount account = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(account, new AccountPickSerializer());
	}

	public static void accountGetCurrentHolding(String code, Long filter) {
		CsAccount account = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, code);
		if ((account != null) && (filter != null)) {
			BigDecimal totQuantity = accountService.getPortfolioQuantity(account.getCustodyAccountKey(), filter);
			if (totQuantity != null) {
				account.setCurrentHolding(totQuantity);
			}
		}

		renderJSON(account, new AccountPickSerializer());
	}

	/*
	 * public static void accountsByCustomer(long customerKey) { if (customerKey
	 * == -99) {
	 * renderList(accountService.listActiveAccountByCustomerAsSelectItem());
	 * }else{
	 * renderList(accountService.listActiveAccountByCustomerAsSelectItem(customerKey
	 * )); } }
	 */

	// New Pick Account By Customer For List
	public static void accountsByCustomer(long customerKey, String filter) {
		/*
		 * long custKey; if (filter.equals("")) filter = null; if
		 * (customerKey==0 && filter!=null) custKey = Long.parseLong(filter);
		 * else if (customerKey!=0 && filter==null) custKey = customerKey; else
		 * custKey = -99;
		 */
		if (!filter.isEmpty())
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT_BY_CUST, new Object[] { filter }));

		if (customerKey == -99) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT, null));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT_BY_CUST, new Object[] { customerKey }));
		}
	}

	// New Pick Account By Customer For List
	public static void accountsByCustomerNotInFa(long customerKey, String filter) {
		if (!filter.isEmpty())
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT_BY_CUST, new Object[] { filter }));

		if (customerKey == -99) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT_NOT_IN_FA, null));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT_BY_CUST, new Object[] { customerKey }));
		}
	}

	public static void accountByCustomer(long customerKey, String code, String filter) {
		CsAccount csAccount = new CsAccount();
		if (!filter.isEmpty())
			customerKey = Long.parseLong(filter);
		if (customerKey == -99)
			csAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, code);
		else
			csAccount = accountService.getAccountByCustomer(UIConstants.SIMIAN_BANK_ID, customerKey, code);
		renderJSON(csAccount, new CsAccountPickSerializer());
	}

	// tambahan rizki redmine #815
	public static void accountByCustomer2(long customerKey, String code, String filter) {
		CsAccount csAccount = new CsAccount();
		if (!filter.isEmpty())
			customerKey = Long.parseLong(filter);
		if (customerKey == -99)
			csAccount = accountService.getAccountByNo2(UIConstants.SIMIAN_BANK_ID, code);
		else
			csAccount = accountService.getAccountByCustomer(UIConstants.SIMIAN_BANK_ID, customerKey, code);
		renderJSON(csAccount, new CsAccountPickSerializer());
	}

	// DISUSED
	public static void accountsWithSpecialPrice(String filter) {
		renderList(accountService.listAccountsWithSpecialPriceAsSelectItem(UIConstants.SIMIAN_BANK_ID, filter));
	}

	// DISUSED
	public static void accountWithSpecialPrice(String code, String filter) {
		CsAccount accountWithSpecialPrice = accountService.getAccountsWithSpecialPrice(UIConstants.SIMIAN_BANK_ID, code, filter);
		renderJSON(accountWithSpecialPrice, new AccountPickSerializer());
	}

	/*
	 * public static void actionTemplates() {
	 * renderList(securityService.listActionTemplateAsSelectItems(
	 * UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Action Template For List
	public static void actionTemplates(String filter) {
		if (!filter.isEmpty()) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_ACTION_TEMPLATE_WITH_SEC_TYPE, new Object[] { filter }));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_ACTION_TEMPLATE, null));
		}
	}

	public static void actionTemplate(String code, String filter) {
		ScActionTemplate actionTemplate = new ScActionTemplate();
		if (!filter.isEmpty()) {
			actionTemplate = securityService.getActionTemplateByCodeAndSecurityType(code, filter);
		} else {
			actionTemplate = securityService.getActionTemplateByCode(code);
		}
		renderJSON(actionTemplate, new ActionTemplatePickSerializer());
	}

	public static void announcementLinks(String filter) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_ANNOUNCEMENT_LINK, new Object[] { Long.parseLong(filter) }));
	}

	public static void announcementLink(String code, String filter) {
		// ScCorporateAnnouncement announcement =
		// securityService.getCorporateAnnouncementByCode(code);
		ScCorporateAnnouncement announcement = securityService.getCorporateAnnouncementForLink(code, Long.parseLong(filter));
		renderJSON(announcement, new AnnouncementPickSerializer());
	}

	/*
	 * public static void cpRules() {
	 * renderList(generalService.listComplianceRuleItems()); }
	 */

	public static void cpRulesPortfolio() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CP_COMP_RULE_PORTFOLIO, null));
	};

	public static void cpRulePortfolio(String code) {
		CpComplianceRule rule = generalService.getComplianceActiveRulePortfolio(code);
		renderJSON(rule, new ComplianceRulePickSerializer());
	}

	// New Pick CP Rule For List
	public static void cpRules() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CP_COMP_RULE, null));
	};

	public static void cpRule(String code) {
		CpComplianceRule rule = generalService.getComplianceActiveRule(code);
		renderJSON(rule, new ComplianceRulePickSerializer());
	}

	public static void cpRulesByRuleType(String filter) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CP_COMP_RULE_BY_RULE_TYPE, new Object[] { filter }));
	}

	public static void cpRuleByRuleType(String code, String filter) {
		CpComplianceRule rule = generalService.getComplianceRulesByRuleType(code, filter);
		renderJSON(rule, new ComplianceRulePickSerializer());
	}

	/*
	 * public static void cpProfiles() {
	 * renderList(generalService.listComplianceProfileAsSelectItems()); }
	 */

	// New Pick CP Rule For List
	public static void cpProfiles() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CP_COMP_PROFILE, null));
	};

	public static void cpProfile(String code) {
		CpComplianceProfile profile = generalService.getComplianceProfile(code);
		renderJSON(profile, new ComplianceProfilePickSerializer());
	}

	/*
	 * public static void bankAccounts(String filter, String by) {
	 * logger.debug("Filter = " +filter); if (!Helper.isNullOrEmpty(filter)) {
	 * long key = Long.parseLong(filter); if ("customer".equals(by)) {
	 * renderList
	 * (bankAccountService.listBankAccountsByCustomerAsSelectItem(key)); } else
	 * {
	 * renderList(bankAccountService.listBankAccountByAccountAsSelectItem(key));
	 * } } else {
	 * renderList(bankAccountService.listBankAccountAsSelectItem(UIConstants
	 * .SIMIAN_BANK_ID)); } }
	 */

	// New Pick Bank Account For List
	public static void bankAccounts(String filter, String by, String domain) {
		if (filter.equals("undefined")) {
			filter = "";
		}

		if (!Helper.isNullOrEmpty(filter)) {
			long key = Long.parseLong(filter);
			if ("customer".equals(by)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_CUST, new Object[] { domain, key }));
			} else if ("customer2".equals(by)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_CUST2, new Object[] { domain, key }));
			} else if ("famaster".equals(by)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_FA_MASTER, new Object[] { key, domain }));
			} else if ("customerCaOrNull".equals(by)) {
					renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_ACC_CA_OR_NULL, new Object[]{key, domain}));
			} else {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_ACC, new Object[] { key, domain }));
			}
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT, new Object[] { domain }));
		}
	}

	public static void bankAccountsByAcctNo(String filter, String by, String domain) {
		if (filter.equals("undefined")) {
			filter = "";
		}

		String[] filters = getArray(filter);
		if (!Helper.isNullOrEmpty(filters[0])) {
			long key = Long.parseLong(filters[0]);
			if ("customer".equals(by)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_CUST, new Object[] { domain, key }));
			} else if ("famaster".equals(by)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_FA_MASTER, new Object[] { key, domain }));
			} else if ("bnaccount".equals(by)){
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_ACC_NEW, new Object[]{key, domain}));	
			} else {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_ACC, new Object[] { key, domain }));
			}
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT, new Object[] { domain }));
		}
	}

	// bankAccountForSettlementAccountPick
	public static void bankAccountProductByAccNo(String productCode, String code) {
		BnAccount bnAccount = bankAccountService.getBankAccountByProduct(productCode, code);
		renderJSON(bnAccount, new BankAccountPickSerializer());
	}

	public static void bankAccountProductByAccNoAndDomain(String productCode, String domain, String code) {
		if (productCode.contains("|")) {
			String[] arrays = productCode.split("\\|");
			productCode = arrays[0];
			domain = arrays[1];
		}

		if (productCode != null && productCode.contains(SPECIAL_CHAR)) {
			productCode = productCode.replaceAll(SPECIAL_CHAR, "#");
		}

		BnAccount bnAccount = bankAccountService.getBankAccountByProduct(productCode, domain, code);
		renderJSON(bnAccount, new BankAccountPickSerializer());
	}

	public static void bankAccountByInvestmentProductCurrAccNo(String currencyCode, String code, Long customerKey) {

		BnAccount bnAccount = bankAccountService.getBankAccountByInvestmentProductCurrAccNo(currencyCode, code, customerKey);
		renderJSON(bnAccount, new BankAccountPickSerializer());
	}

	public static void bankAccountForSettlementAccountPick(String code, String filter, String domain) {
		String[] codeSplit = code.split("\\");
		String accountNo = codeSplit[0];
		String bankCode = (codeSplit.length == 2) ? codeSplit[1] : null;
		BnAccount bankAccount = bankAccountService.bankAccountForSettlementAccountPick(UIConstants.SIMIAN_BANK_ID, accountNo, bankCode, domain);
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	public static void bankAccountForSettlementAccountPickByCustomerKey(String code, String filter, String domain) {
		String[] codeSplit = code.split("\\");
		String accountNo = codeSplit[0];
		String bankCode = (codeSplit.length == 2) ? codeSplit[1] : null;
		BnAccount bankAccount = bankAccountService.bankAccountForSettlementAccountPickByCustomerKey(UIConstants.SIMIAN_BANK_ID, accountNo, bankCode, domain, Long.parseLong(filter));
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	public static void PickbankAccountForSettlementAccount(String code, String filter, String domain) {
		String[] codeSplit = code.split("\\");
		String accountNo = codeSplit[0];
		String bankCode = (codeSplit.length == 2) ? codeSplit[1] : null;
		BnAccount bankAccount = bankAccountService.pickbankAccountForSettlementAccount(UIConstants.SIMIAN_BANK_ID, accountNo, bankCode, domain, Long.parseLong(filter));
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	public static void bankAccountByAccountNoAndCustomerKey(String code, String filter, String domain) {
		String[] filters = getArray(filter);
		String[] codeSplit = code.split("\\");
		String accountNo = codeSplit[0];
		String bankCode = (codeSplit.length == 2) ? codeSplit[1] : null;
		Long customerKey = !Helper.isNullOrEmpty(filters[1]) ? new Long(filters[1]) : -1;
		BnAccount bankAccount = bankAccountService.bankAccountByCustomerKey(UIConstants.SIMIAN_BANK_ID, accountNo, bankCode, domain, customerKey);
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	public static void bankAccountByCustomerKey(String code, String filter, String domain) {
		String[] codeSplit = code.split("\\");
		String accountNo = codeSplit[0];
		String bankCode = (codeSplit.length == 2) ? codeSplit[1] : null;
		BnAccount bankAccount = bankAccountService.bankAccountByCustomerKey(UIConstants.SIMIAN_BANK_ID, accountNo, bankCode, domain, new Long(filter));
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	public static void bankAccountProductByCustomerKey(String code, String filter, String domain) {
		String[] codeSplit = code.split("\\");
		String accountNo = codeSplit[0];
		String bankCode = (codeSplit.length == 2) ? codeSplit[1] : null;
		BnAccount bankAccount = bankAccountService.bankAccountByCustomerKey(UIConstants.SIMIAN_BANK_ID, accountNo, bankCode, domain, new Long(filter));
		renderJSON(bankAccount, new BankAccountProductPickSerializer());
	}

	public static void bankAccountByFaMaster(String code, String filter, String domain) {
		String[] codeSplit = code.split("\\");
		String accountNo = codeSplit[0];
		String bankCode = (codeSplit.length == 2) ? codeSplit[1] : null;

		BnAccount bnAccount = null;
		if (!filter.isEmpty()) {
			bnAccount = bankAccountService.bankAccountByFaMaster(UIConstants.SIMIAN_BANK_ID, accountNo, bankCode, domain, Long.parseLong(filter));
		}
		BnAccount bankAccount = null;
		if (bnAccount != null) {
			bankAccount = bankAccountService.getBankAccount(bnAccount.getBankAccountKey());
		}
		// logger.debug("BANK ACCOUNT = " +bankAccount);
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	public static void bankAccount(String code, String filter, String by) {
		BnAccount bankAccount = bankAccountService.getBankAccountByNumber(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	/*
	 * public static void bankTransactionMasters() {
	 * renderList(bankAccountService
	 * .listBankTransactionMastersAsSelectItem(UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Bank Account For List
	public static void bankTransactionMasters() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_TRX_MASTER, null));
	}

	public static void bankTransactionMaster(String code) {
		BnTransactionMaster bnTemplateMaster = bankAccountService.getBankTransactionMaster(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(bnTemplateMaster, new BankTransactionMasterPickSerializer());
	}

	/*
	 * public static void branches() {
	 * renderList(generalService.listBranchesAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Branch For List
	public static void branches() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_BRANCH, null));
	}

	public static void branch(String code) {
		GnBranch branch = generalService.getBranch(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(branch, new BranchPickSerializer());
	}

	/*
	 * public static void charges() {
	 * renderList(generalService.listChargeMastersAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Charge Master For List
	public static void charges() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_CHARGE_MASTER, null));
	}

	public static void charge(String code) {
		CsChargeMaster chargeMaster = generalService.getChargeMaster(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(chargeMaster, new ChargeMasterPickSerializer());
	}

	/*
	 * public static void chargesByCategoryTransaction() {
	 * renderList(generalService
	 * .listChargeMastersAsSelectItemByCategoryTransaction(
	 * UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Charge Master By Category For List
	public static void chargesByCategoryTransaction() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_CHARGE_MASTER_BY_CAT_TRX, null));
	}

	public static void chargesMiscellaneousByCategoryTransaction() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_CHARGE_MASTER_BY_MISC, null));
	}

	public static void chargeByCategoryTransaction(String code) {
		CsChargeMaster chargeMaster = generalService.getChargeMasterByCategoryTransaction(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(chargeMaster, new ChargeMasterPickSerializer());
	}

	/*
	 * public static void chargesForTransaction() {
	 * renderList(generalService.listChargeMasterAsSelectItemForTransaction(
	 * UIConstants.SIMIAN_BANK_ID)); }
	 */

	public static void chargesForTransaction() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_CHARGE_MASTER_BY_CAT_TRX, null));
	}

	public static void chargeForTransaction(String code) {
		CsChargeMaster chargeMaster = generalService.getChargeMasterForTransaction(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(chargeMaster, new ChargeMasterPickSerializer());
	}

	/*
	 * public static void chargeProfiles() {
	 * renderList(generalService.listChargeProfileAsSelectItem
	 * (UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Charge Profile For List
	public static void chargeProfiles() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_CHARGE_PROFILE, null));
	}

	public static void chargeProfile(String code) {
		// CsChargeProfile chargeProfile =
		// generalService.getChargeProfile(profileKey);
		CsChargeProfile chargeProfile = generalService.getChargeProfileForPick(code);
		renderJSON(chargeProfile, new ChargeProfilePickSerializer());
	}

	/*
	 * public static void cifs (String organizationId) {
	 * renderList(accountService
	 * .listCifAsSelectItem(UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Customer / CIF For List
	public static void cifs() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTER, null));
	}

	// public static void cif (String organizationId) {
	// renderList(accountService.getCifAsSelectItem(UIConstants.SIMIAN_BANK_ID));
	// }
	// elvino jgn lupa di uncomment
	/*
	 * public static void coaMasters() {
	 * renderList(fundService.listFaCoaMasterForSelectItem()); }
	 */

	// New Pick Coa Master For List
	public static void coaMasters() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_FA_COA, null));
	}

	public static void coaMaster(String code) {
		FaCoaMaster faCoaMaster = fundService.getFaCoaMasterForPick(code);
		renderJSON(faCoaMaster, new CoaMasterPickSerializer());
	}

	/*
	 * public static void coaMastersNotParent() {
	 * renderList(fundService.listFaCoaMasterByBottomLevelForSelectItem()); }
	 */

	// New Pick Coa Master Level Bottom / haven't parent For List
	public static void coaMastersNotParent() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_FA_COA_BOTTOM_LEVEL, null));
	}

	public static void coaMasterNotParent(String code) {
		// FaCoaMaster faCoaMaster =
		// fundService.getFaCoaMasterByBottomLevelForPick(code);
		FaCoaMaster faCoaMaster = fundService.getFaCoaMasterForPick(code);

		renderJSON(faCoaMaster, new CoaMasterPickSerializer());
	}

	// DIUSED
	public static void couponSchedules(String filter) {
		if (Helper.isNullOrEmpty(filter)) {
			renderList(new ArrayList<SelectItem>());
			return;
		}
		long securityKey = Long.valueOf(filter);
		renderList(securityService.listCouponScheduleAsSelectItems(securityKey));
	}

	// New Pick Coupon Schedule For List
	/*
	 * public static void couponSchedulePicks(String filter) { if
	 * (Helper.isNullOrEmpty(filter)) { renderList1(new PickData()); return; }
	 * long securityKey = Long.valueOf(filter);
	 * renderList1(generalService.getPickLookup
	 * (PickCodeConstants.PICK_SC_COUPON_SCHEDULE, new Object[]{securityKey}));
	 * }
	 */
	// DIUSED
	public static void couponSchedule(Integer code, Long filter) {
		ScCouponSchedule couponSchedule = securityService.getCouponScheduleFilter(filter, code);

		// logger.debug("coupon schedule" + couponSchedule.getCouponNo());
		renderJSON(couponSchedule, new CouponSchedulePickSerializer());
	}

	public static void couponSchedulesWithPaid(String filter) {

	}

	public static void couponScheduleWithPaid(String filter) {

	}

	// New Pick Coupon Schedule With Paid For List
	public static void couponSchedulesWithNoPaid(String filter) {
		if (Helper.isNullOrEmpty(filter)) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_COUPON_SCHEDULE_WITH_NO_PAID, new Object[] { Long.parseLong("0000") }));
		}
		long securityKey = Long.valueOf(filter);
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_COUPON_SCHEDULE_WITH_NO_PAID, new Object[] { securityKey }));
	}

	public static void couponScheduleWithNoPaid(Integer code, Long filter) {
		ScCouponSchedule couponSchedule = securityService.getCouponSecheduleFilterWithNoPaid(filter, code);
		renderJSON(couponSchedule, new CouponSchedulePickSerializer());
	}

	/*
	 * public static void currencies() {
	 * renderList(generalService.listCurrenciesAsSelectItem()); }
	 */

	// New Pick Currency For List
	public static void currencies() {
		PickData pick = generalService.getPickLookup(PickCodeConstants.PICK_GN_CURRENCY, null);
		renderList1(pick);
	}

	public static void currency(String code) {
		GnCurrency currency = generalService.getCurrencyPick(code);
		renderJSON(currency, new CurrencyPickSerializer());
	}

	public static void currenciesWithExclude(String filter) {
		PickData pick = generalService.getPickLookup(PickCodeConstants.PICK_GN_CURRENCY_EXCLUDE, new Object[] { filter });
		renderList1(pick);
	}

	public static void currencyWithExclude(String code, String filter) {
		GnCurrency currency = generalService.getCurrencyExceludePick(code, filter);
		renderJSON(currency, new CurrencyPickSerializer());
	}

	/*
	 * public static void customers() {
	 * renderList(customerService.listCustomersAsSelectItem()); }
	 */

	// New Pick Customer List
	public static void customersNotInFa() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTER_NOT_IN_FA_MASTER, null));
	}
	
	public static void customersCashProjection() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTER_CASH_PROJECTION, null));
	}

	// New Pick Customer List
	public static void customers() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTER, null));
	}

	// New Pick Customer List
	public static void customers2() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTER_2, null));
	}

	public static void customersNonRetail() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTERZ_NON_RETAIL, null));
	}

	public static void customersNonRetailBilling() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTER_NON_RETAIL_BILLING, null));
	}

	public static void customer(String code) {
		CfMaster customer = customerService.getCustomerByNoForPick(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(customer, new CustomerPickSerializer());
	}

	public static void customerPickNotInFA(String code, String domain) {
		CfMaster customer = customerService.getCustomerPickNotInFA(UIConstants.SIMIAN_BANK_ID, code, domain);
		renderJSON(customer, new CustomerPickSerializer());
	}
	
	public static void customerCashProjection(String code, String domain) {
		CfMaster customer = customerService.customerCashProjection(code);
		renderJSON(customer, new CustomerPickSerializer());
	}

	public static void customerNonRetail(String code) {
		CfMaster customer = customerService.getCustomerNonRetailByNoForPick(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(customer, new CustomerPickSerializer());
	}

	/*
	 * public static void customerAccounts() {
	 * renderList(accountService.listAccountsAsSelectItem
	 * (UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Customer Account List
	public static void customerAccounts() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT, null));
	}

	// New Pick Customer Account List
	public static void customerAccountsNotInFa() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_ACCOUNT_NOT_IN_FA, null));
	}

	public static void customerAccount(String code) {
		CsAccount csAccount = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(csAccount, new CsAccountPickSerializer());
	}

	public static void customerAccount2(String code) {
		CsAccount csAccount = accountService.getAccountByNo2(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(csAccount, new CsAccountPickSerializer());
	}

	/*
	 * public static void customerInvestments() {
	 * renderList(customerService.listCustomerInvestmentAsSelectItem()); }
	 */

	// New Pick Customer InvestmentAccount List
	public static void customerInvestments() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTER_IN_RG_INVESTMENT, null));
	}

	public static void customerInvestment(String code) {
		CfMaster customer = customerService.getCustomerByNo(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(customer, new CustomerPickSerializer());
	}

	/*
	 * public static void faTransactionMasters() {
	 * renderList(fundService.listFaTransactionMastersAsSelectItem()); }
	 */

	// New Pick Fa Transaction Master For List
	public static void faTransactionMasters() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_FA_TRX_MASTER, null));
	}

	public static void faTransactionMaster(String code) {
		FaTransactionMaster faTransactionMaster = fundService.getFaTransactionMasterForPick(code);
		renderJSON(faTransactionMaster, new FaTransactionMasterPickSerializer());
	}

	public static void groups() {
		renderList(applicationService.listGroupsAsSelectItem(UIConstants.SIMIAN_BANK_ID));
	}

	public static void group(Long code) {
		GnGroup group = applicationService.getGroupForPick(code);
		renderJSON(group, new GroupPickSerializer());
	}

	/*
	 * public static void lookups(String group, Long key, String filter) {
	 * logger.debug("filter: %s", filter); if (Helper.isNullOrEmpty(filter)) {
	 * renderList
	 * (generalService.listLookupsForPickAsSelectItem(UIConstants.SIMIAN_BANK_ID
	 * , group)); } else {
	 * renderList(generalService.listLookupsFilteredAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID, group, key, filter)); } }
	 */

	// New Pick Lookup For List
	public static void lookups(String group, Long key, String filter) {
		log.trace("group: "+ group + ", key: "+ key +", filter:" + filter);
		if (Helper.isNullOrEmpty(filter)) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_LOOKUP, new Object[] { group }));
		} else {
			if (LookupConstants.CUSTOMER_LEGAL_DOMICILE_NASIONAL.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_LOOKUP_CUST_DOM_NAS, new Object[] { group }));
			} else if (LookupConstants.CUSTOMER_LEGAL_DOMICILE_ASING.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_LOOKUP_CUST_DOM_ASING, new Object[] { group }));
			}
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_LOOKUP_BY_LOK_REF, new Object[] { group, key, filter }));
		}
	}

	public static void lookup(String group, String code, Long key, String filter) {
		GnLookup lookup = null;
		if (Helper.isNullOrEmpty(filter)) {
			lookup = generalService.getLookup(UIConstants.SIMIAN_BANK_ID, group, code);
		} else {
			lookup = generalService.getLookupFiltered(UIConstants.SIMIAN_BANK_ID, group, code, key, filter);
		}
		renderJSON(lookup, new LookupPickSerializer());
	}

	public static void lookupByUdfs(String group, String filter) {
		String[] filters = getArray(filter);
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_LOOKUP_BY_UDF_MASTER, new Object[] { group, filters[0], filters[1] }));
	}

	public static void lookupByUdf(String group, String code, String filter) {
		String filters[] = getArray(filter);
		GnLookup lookup = generalService.getLookupByUdf(UIConstants.SIMIAN_BANK_ID, group, code, filters[0], filters[1]);
		renderJSON(lookup, new LookupPickSerializer());
	}

	public static void announcements() {
		PickData pick = generalService.getPickLookup(PickCodeConstants.PICK_SC_CORPORATE_ANNOUNCEMENT, null);
		renderList1(pick);
	}

	public static void announcement(String code) {
		ScCorporateAnnouncement announcement = generalService.getAnnouncementPick(code);
		renderJSON(announcement, new ScCorporateAnnouncementSerializer());
	}

	/*
	 * @SuppressWarnings("unchecked") public static void menus() {
	 * List<SelectItem> menus = Cache.get(UIConstants.CACHE_MENU, List.class);
	 * if (menus == null) { Map<String, GnMenu> urls =
	 * Cache.get(UIConstants.CACHE_URLS, Map.class); if (urls != null) { menus =
	 * new ArrayList<SelectItem>(); for (GnMenu menu : urls.values()) {
	 * menus.add(new SelectItem(menu.getMenuNumber(),
	 * menu.getMenuBreadCrumb())); } Cache.add(UIConstants.CACHE_MENU, menus); }
	 * } renderList(menus); }
	 */

	@SuppressWarnings("unchecked")
	public static void menus() {
		List<SelectItem> menus = Cache.get(UIConstants.CACHE_MENU, List.class);
		PickData menuPick = new PickData();
		menuPick.setTitles(new String[] { "Code", "Description" });
		menuPick.setTypes(new String[] { "String", "String" });
		menuPick.setWidths(new String[] { "25", "75" });
		menuPick.setPks(new String[] { "0" });
		if (menus == null) {
			Map<String, GnMenu> urls = Cache.get(UIConstants.CACHE_URLS, Map.class);
			if (urls != null) {
				for (GnMenu menu : urls.values()) {
					PickRow row = new PickRow(new String[] { "0" }, new String[] { "String", "String" }, new Object[] { menu.getMenuNumber(), menu.getMenuBreadCrumb() });
					menuPick.getRows().add(row);
				}
				Cache.add(UIConstants.CACHE_MENU, menus);
			}
		}
		renderList1(menuPick);
	}

	@SuppressWarnings("unchecked")
	public static void menu(String code) {
		if (code != null) {
			Map<String, String> programList = Cache.get("programList", Map.class);
			Map<String, GnMenu> urlList = Cache.get("urlList", Map.class);
			if (programList.containsKey(code)) {
				String menuLink = programList.get(code);
				if (urlList.containsKey(menuLink)) {
					GnMenu menu = urlList.get(menuLink);
					renderJSON(menu, new MenuPickSerializer());
				}
			}
		}
	}

	/*
	 * public static void csPortfolios(String filter) { String[] filters =
	 * getArray(filter);
	 * 
	 * if
	 * ((filters[0].isEmpty())||(filters[1].isEmpty())||(filters[2].isEmpty()))
	 * renderList(null);
	 * 
	 * Long accountKey = Long.parseLong(filters[0]); String secType =
	 * filters[1]; Long securityKey = Long.parseLong(filters[2]);
	 * logger.debug("Code: %s, secType: %s, securityKey: %s", accountKey,
	 * secType, securityKey);
	 * renderList(accountService.listPortfolioAsSelectItemByAccountSecTypeSecurity
	 * (accountKey, secType, securityKey)); }
	 */

	// New Pick Cs Portfolio For List
	public static void csPortfolios(String filter) {
		String[] filters = getArray(filter);
		if ((filters[0].isEmpty()) || (filters[1].isEmpty()) || (filters[2].isEmpty())) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_PORTFOLIO, new Object[] { "", "", "" }));
		}

		Long accountKey = Long.parseLong(filters[0]);
		String secType = filters[1];
		Long securityKey = Long.parseLong(filters[2]);
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_PORTFOLIO, new Object[] { accountKey, secType, securityKey }));
	}

	public static void csPortfolio(String code, String filter) {
		// String[] codeSplit = code.split("\\|");
		// String portoNo = codeSplit[0];
		// String holdRefs = codeSplit[1];

		String[] filters = getArray(filter);
		Long accountKey = Long.parseLong(filters[0]);
		String secType = filters[1];
		Long securityKey = Long.parseLong(filters[2]);
		// logger.debug("portoNo: %s, holdrefs: %s", portoNo, holdRefs);
		// logger.debug("portoNo: %s, holdrefs: %s", portoNo);

		CsPortfolio portfolio = accountService.getPortfolioForPick(Long.parseLong(code), accountKey, secType, securityKey);
		renderJSON(portfolio, new CsPortfolioPickSerializer());
	}

	public static void pledingPortfolios(String filter) {
		String[] filters = getArray(filter);

		Long accountKey = 0L;
		String secType = new String();
		Long securityKey = 0L;
		String classification = new String();
		try {
			accountKey = Long.parseLong(filters[0]);
			secType = filters[1];
			securityKey = Long.parseLong(filters[2]);
			classification = filters[3];
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_PL_PLEDGING_PORTFOLIO, new Object[] { accountKey, securityKey, classification, accountKey, secType, securityKey, classification }));
	}

	public static void pledingPortfolio(String code, String filter) {

		String[] filters = getArray(filter);
		Long accountKey = null;
		String secType = null;
		Long securityKey = null;
		String classification = null;

		CsPortfolio portfolio = null;

		try {
			accountKey = Long.parseLong(filters[0]);
			secType = filters[1];
			securityKey = Long.parseLong(filters[2]);
			classification = filters[3];

			portfolio = accountService.getPledgingPortfolio(code, accountKey, classification, secType, securityKey);

			BigDecimal amountPledging = transactionService.getOutstandingQuantityAmount(accountKey, securityKey, classification, code);
			if (amountPledging == null)
				amountPledging = BigDecimal.ZERO;

			if (portfolio != null) {
				// beware this is just VO field !!
				portfolio.setPortfolioQuantity(portfolio.getTotalQuantity().subtract(amountPledging));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		renderJSON(portfolio, new CsPortfolioPickSerializer());
	}

	/*
	 * public static void holdings(String filter) { String[] filters =
	 * getArray(filter); SimpleDateFormat df = new
	 * SimpleDateFormat(UIConstants.DATE_FORMAT); Date settlementDate = new
	 * Date();
	 * 
	 * try { settlementDate = df.parse(filters[0]); } catch (ParseException e) {
	 * renderList(null); } Long accountKey = Long.parseLong(filters[1]); Long
	 * securityKey = Long.parseLong(filters[2]); String classification =
	 * filters[3]; logger.debug("ACCOUNT KEY = " +accountKey);
	 * logger.debug("SECURITY KEY = " +securityKey);
	 * logger.debug("CLASSIFICATION = " +classification);
	 * logger.debug("Holding DATE = " +settlementDate); if
	 * (accountKey.equals("")) accountKey = null; List<CsDailyHolding>
	 * dailyHoldings = accountService.listDailyHoldingBy(settlementDate,
	 * accountKey, securityKey, classification); logger.debug("SIZE HOLDINGS = "
	 * +dailyHoldings.size()); if (dailyHoldings != null) { DecimalFormat
	 * formatter = new DecimalFormat("#,##0.00"); List<SelectItem> list = new
	 * ArrayList<SelectItem>(); for (CsDailyHolding dailyHolding :
	 * dailyHoldings) { list.add(new
	 * SelectItem(dailyHolding.getId().getHoldingRefs(),
	 * formatter.format(dailyHolding.getSettledQuantity())));
	 * logger.debug("List result = "+list.toString()); } renderList(list); }
	 * else { renderList(new ArrayList<SelectItem>()); } }
	 */

	// New Pick Holding For list
	public static void holdings(String filter) {

		String[] filters = getArray(filter);
		SimpleDateFormat df = new SimpleDateFormat(appProp.getDateFormat());
		Date settlementDate = new Date();

		try {
			settlementDate = df.parse(filters[0]);
		} catch (ParseException e) {
			settlementDate = null;
		}
		Long accountKey = (!filters[1].equals("")) ? Long.parseLong(filters[1]) : null;
		Long securityKey = (!filters[2].equals("")) ? Long.parseLong(filters[2]) : null;
		String classification = (!filters[3].equals("")) ? filters[3] : null;
		PickData pick = null;
		if (settlementDate != null && accountKey != null && securityKey != null && classification != null)
			pick = generalService.getPickLookup(PickCodeConstants.PICK_CS_DAILY_HOLDING_PORTFOLIO, new Object[] { accountKey, securityKey, classification, accountKey, securityKey, classification, settlementDate, accountKey, securityKey, classification });

		if (pick != null)
			renderList1(pick);
		else
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_DAILY_HOLDING_NULL, null));
	}

	public static void holding(String code, String filter) {

		String[] filters = getArray(filter);
		SimpleDateFormat df = new SimpleDateFormat(appProp.getDateFormat());
		Date holdingDate = new Date();
		try {
			holdingDate = df.parse(filters[0]);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		Long accountKey = Long.parseLong(filters[1]);
		Long securityKey = Long.parseLong(filters[2]);
		String classification = filters[3];

		CsDailyHolding dailyHolding = accountService.getDailyHolding(holdingDate, accountKey, securityKey, classification, code);
		BigDecimal amountPledging = transactionService.getOutstandingQuantityAmount(accountKey, securityKey, classification, code);
		if (amountPledging == null)
			amountPledging = BigDecimal.ZERO;
		PortfolioHolding portfolioHolding = new PortfolioHolding();

		if (dailyHolding != null) {
			portfolioHolding.setHoldingRefs(dailyHolding.getId().getHoldingRefs());
			portfolioHolding.setSettledQuantity(dailyHolding.getSettledQuantity().subtract(amountPledging));
			// logger.debug("holding Type = "+dailyHolding.getSecurity().getSecurityType().getHoldingType().getLookupId());
			CsPortfolio portfolio = accountService.getPortfolio(accountKey, securityKey, classification, code);
			if (portfolio != null) {
				portfolioHolding.setLastPaymentDate(portfolio.getLastPaymentDate());
				portfolioHolding.setNextPaymentDate(portfolio.getNextPaymentDate());
				portfolioHolding.setMaturityDate(portfolio.getMaturityDate());
				portfolioHolding.setEffectiveDate(portfolio.getEffectiveDate());
				portfolioHolding.setInterestRate(portfolio.getInterestRate());
				portfolioHolding.setYield(portfolio.getYield());
			}
		}

		renderJSON(portfolioHolding, new PortfolioHoldingPickSerializer());
	}

	public static void faDailyNavTotalUnit(Long fundKey, String navDate) {
		BigDecimal totalunit = fundService.getFaUnitTransactionTotalUnit(fundKey, navDate);
		renderJSON(totalunit);
	}

	public static void faUnitTransactionTotalUnit(Long fundKey, String transactionDate) {
		BigDecimal totalunit = fundService.getFaUnitTransactionTotalUnit(fundKey, transactionDate);
		renderJSON(totalunit);
	}

	/*
	 * public static void faFundSetups() {
	 * renderList(fundService.listFaFundSetupsAsSelectItem()); }
	 */

	// New Pick Fa Fund Setup For List
	public static void faFundSetups() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_FA_MASTER, null));
	}

	public static void faMasterInCfMaster() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_FA_MASTER_IN_CFMASTER, null));
	}

	public static void faFundSetup(String code) {
		FaMaster faMaster = fundService.getFaFundSetupForPick(code);
		renderJSON(faMaster, new FaFundSetupPickSerializer());

	}

	/*
	 * public static void faFundProfileSetups() {
	 * renderList(fundService.listFaFundProfileSetupsAsSelectItem()); }
	 */

	// New Pick Fa Fund Profile nSetup For List
	public static void faFundProfileSetups() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_FA_MASTER_PROFILE, null));
	}

	public static void faFundProfileSetup(String code) {
		FaMaster faMaster = fundService.getFaFundProfileSetupForPick(code);
		renderJSON(faMaster, new FaFundSetupPickSerializer());

	}

	/*
	 * public static void faPostingProfiles() {
	 * renderList(fundService.listFaPostingProfilesAsSelectItem()); }
	 */

	// New Pick Fa Posting Profile nSetup For List
	public static void faPostingProfiles() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_FA_POSTING_PROFILE, null));
	}

	public static void faPostingProfile(String code) {
		FaPostingProfile faPostingProfile = fundService.getFaPostingProfileByCode(code);
		renderJSON(faPostingProfile, new FaPostingProfilePickSerializer());
	}

	public static void reports() {
		renderList(generalService.listReportForSelectItem());
	}

	public static void report(Long code) {
		GnReportList report = generalService.getReport(code);
		renderJSON(report, new ReportMaintenancePickSerializer());
	}

	/*
	 * public static void reportLoaders() {
	 * renderList(generalService.listReportLoaderForSelectItem()); }
	 */

	// New Pic report loader for pick
	public static void reportLoaders() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_REPORT_LOADER, null));
	}

	public static void reportLoader(Long code) {
		GnReportLoader report = generalService.viewReportLoader(code);
		renderJSON(report, new ReportLoaderPickSerializer());
	}

	public static void reportsPdi() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_REPORT_PDI, null));
	}

	public static void reportPdi(Long code) {
		GnReportPdi report = generalService.viewReportPdi(code);
		renderJSON(report, new ReportPdiPickSerializer());
	}

	/*
	 * public static void worklists() {
	 * renderList(workflowService.listWorkflowForSelectItem()); }
	 */

	// New Pick Workflow For List
	public static void worklists() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_WORKFLOW_DETAIL, null));
	}

	public static void worklist(String code) {
		// GnWorkflowDetail worklist =
		// workflowService.getWorklistForMapping(code);
		GnWorkflowDetail worklist = workflowService.getWorklistForPick(code);
		renderJSON(worklist, new WorkflowDetailPickSerializer());
	}

	/*
	 * public static void securities(String filter) {
	 * renderList(securityService.
	 * listSecuritiesBySecurityTypeAsSelectItem(filter)); }
	 */

	// New Pick Security For List
	public static void securities(String filter) {
		if (Helper.isNullOrEmpty(filter)) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_MASTER, null));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_MASTER_BY_SEC_TYPE, new Object[] { filter }));
		}
	}

	public static void securitiesgetQuantity(String filter) {
		String[] filters = filter.split("-");
		if (Helper.isNullOrEmpty(filter)) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_MASTER, null));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_MASTER_BY_SEC_TYPE, new Object[] { filters[0] }));
		}
	}

	public static void securitiesBySecurityTypeAndThirdParty(String filter) {
		String[] filters = getArray(filter);
		String securityType = filters[0];
		String thirdPartyCode = filters[1];

		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_MASTER_BY_SEC_TYPE_AND_THIRD_PARTY, new Object[] { securityType, thirdPartyCode }));
	}

	public static void securitygetQuantity(String code, String filter, Date transactionDate) {
		String[] filters = filter.split("-");
		String securityType = filters[0];
		Long accountKey;
		if (filters.length == 1) {
			accountKey = null;
		} else {
			accountKey = Long.parseLong(filters[1]);
		}
		ScMaster security = securityService.getSecurity(code, securityType);
		if (security != null) {
			// Force settlement value to 0 if null
			if (security.getSettlementDays() == null) {
				security.setSettlementDays(0);
			}
			Calendar cal = Calendar.getInstance();
			if (transactionDate != null) {
				int settlementDays = security.getSettlementDays();
				cal.setTime(transactionDate);
				cal.add(Calendar.DATE, settlementDays);
				while (true) {
					int holidays = generalService.countHolidaysBetween(transactionDate, cal.getTime(), "");
					if ((settlementDays - holidays) == security.getSettlementDays()) {
						security.setSettlementDays(settlementDays);
						break;
					}
					settlementDays += 1;
					cal.add(Calendar.DATE, 1);
				}
			}

			ScCouponSchedule schedule = securityService.getCouponScheduleOn(security.getSecurityKey(), cal.getTime());
			if (schedule != null) {
				// JUN: lastPaymentDate di coupon - 1 hari ud diimplement di
				// service
				// cal.setTime(schedule.getLastPaymentDate());
				// cal.add(Calendar.DATE, -1);
				// security.setLastPaymentDate(cal.getTime());
				security.setLastPaymentDate(schedule.getLastPaymentDate());
				security.setInterestRate(schedule.getInterestRate());
				security.setNextPaymentDate(schedule.getNextPaymentDate());
				security.setFractionRatioSource(schedule.getFractionBase());
				security.setFractionRatioTarget(schedule.getFraction());
			}

			List<TdTransactionTemplate> transTemplate = accountService.listTdTransactionTemplate(code);
			if (transTemplate.size() == 0) {
				security.setTab("trans");
			} else {
				security.setTab("deposito");
			}

			if (accountKey != null) {
				BigDecimal totQuantity = accountService.getPortfolioQuantity(accountKey, security.getSecurityKey());
				if (totQuantity != null) {
					security.setCurrentHolding(totQuantity);
				}
			}
		}
		renderJSON(security, new SecurityPickSerializer());
	}

	public static void security(String code, String filter, Date transactionDate) {
		ScMaster security = securityService.getSecurity(code, filter);
		if (security != null) {
			// Force settlement value to 0 if null
			if (security.getSettlementDays() == null) {
				security.setSettlementDays(0);
			}
			Calendar cal = Calendar.getInstance();
			if (transactionDate != null) {
				int settlementDays = security.getSettlementDays();
				cal.setTime(transactionDate);
				cal.add(Calendar.DATE, settlementDays);
				while (true) {
					int holidays = generalService.countHolidaysBetween(transactionDate, cal.getTime(), "");
					if ((settlementDays - holidays) == security.getSettlementDays()) {
						security.setSettlementDays(settlementDays);
						break;
					}
					settlementDays += 1;
					cal.add(Calendar.DATE, 1);
				}
			}

			ScCouponSchedule schedule = securityService.getCouponScheduleOn(security.getSecurityKey(), cal.getTime());
			if (schedule != null) {
				// JUN: lastPaymentDate di coupon - 1 hari ud diimplement di
				// service
				// cal.setTime(schedule.getLastPaymentDate());
				// cal.add(Calendar.DATE, -1);
				// security.setLastPaymentDate(cal.getTime());
				security.setLastPaymentDate(schedule.getLastPaymentDate());
				security.setInterestRate(schedule.getInterestRate());
				security.setNextPaymentDate(schedule.getNextPaymentDate());
				security.setFractionRatioSource(schedule.getFractionBase());
				security.setFractionRatioTarget(schedule.getFraction());
			} else {
				if (LookupConstants.SECURITY_CLASS_FIXED_INCOME.equals(security.getSecurityType().getSecurityClass().getLookupId())) {
					if (cal.getTime().compareTo(security.getIssueDate()) <= 0) {
						ScCouponSchedule couponSchedule = securityService.getCouponScheduleSecurity(security.getSecurityKey(), 1);
						if (couponSchedule != null) {
							security.setLastPaymentDate(couponSchedule.getLastPaymentDate());
							security.setNextPaymentDate(couponSchedule.getNextPaymentDate());
							security.setFractionRatioSource(couponSchedule.getFractionBase());
							security.setFractionRatioTarget(couponSchedule.getFraction());
						}
					}

					if (cal.getTime().compareTo(security.getMaturityDate()) >= 0) {
						ScCouponSchedule couponSchedule = securityService.getScScheduleMaxLastPaymentDate(security.getSecurityKey());
						if (couponSchedule != null) {
							security.setLastPaymentDate(couponSchedule.getLastPaymentDate());
							security.setNextPaymentDate(couponSchedule.getNextPaymentDate());
							security.setFractionRatioSource(couponSchedule.getFractionBase());
							security.setFractionRatioTarget(couponSchedule.getFraction());
						}
					}
				}
			}

			List<TdTransactionTemplate> transTemplate = accountService.listTdTransactionTemplate(code);
			if (transTemplate.size() == 0) {
				security.setTab("trans");
			} else {
				security.setTab("deposito");
			}

		}
		renderJSON(security, new SecurityPickSerializer());
	}

	public static void securityBySecurityTypeAndThirdParty(String code, String filter, Date transactionDate) {
		String[] filters = getArray(filter);
		String securityType = filters[0];
		String thirdPartyCode = filters[1];

		ScMaster security = securityService.getSecurityBySecurityTypeAndThirdParty(code, securityType, thirdPartyCode);
		if (security != null) {
			// Force settlement value to 0 if null
			if (security.getSettlementDays() == null) {
				security.setSettlementDays(0);
			}
			Calendar cal = Calendar.getInstance();
			if (transactionDate != null) {
				int settlementDays = security.getSettlementDays();
				cal.setTime(transactionDate);
				cal.add(Calendar.DATE, settlementDays);
				while (true) {
					int holidays = generalService.countHolidaysBetween(transactionDate, cal.getTime(), "");
					if ((settlementDays - holidays) == security.getSettlementDays()) {
						security.setSettlementDays(settlementDays);
						break;
					}
					settlementDays += 1;
					cal.add(Calendar.DATE, 1);
				}
			}

			ScCouponSchedule schedule = securityService.getCouponScheduleOn(security.getSecurityKey(), cal.getTime());
			if (schedule != null) {
				// JUN: lastPaymentDate di coupon - 1 hari ud diimplement di
				// service
				// cal.setTime(schedule.getLastPaymentDate());
				// cal.add(Calendar.DATE, -1);
				// security.setLastPaymentDate(cal.getTime());
				security.setLastPaymentDate(schedule.getLastPaymentDate());
				security.setInterestRate(schedule.getInterestRate());
				security.setNextPaymentDate(schedule.getNextPaymentDate());
				security.setFractionRatioSource(schedule.getFractionBase());
				security.setFractionRatioTarget(schedule.getFraction());
			}
		}
		renderJSON(security, new SecurityPickSerializer());
	}

	public static void securityPickForChargeItem(String code) {
		ScMaster security = securityService.getSecurityPickForChargeItem(code);
		renderJSON(security, new SecurityPickForChargeItemSerializer());
	}

	public static void securityPickForChargeItem2(String type, String code) {

		ScMaster security = securityService.getSecurityPickForChargeItem2(type, code);
		renderJSON(security, new SecurityPickForChargeItemSerializer());
	}

	/*
	 * public static void securityTypes(String securityClass, String filter) {
	 * renderList(securityService .listSecurityTypesAsSelectItem(securityClass,
	 * filter)); }
	 */

	// New Pick Security Type For List
	public static void securityTypes(String filter) {
		if (!Helper.isNullOrEmpty(filter)) {
			if (LookupConstants.__SECURITY_TYPE_CASH.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_CASH, null));
			} else if (LookupConstants.__TRANSACTION_TEMPLATE_USED_BY.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_NOT_CASH, null));
			} else if (LookupConstants.__INTEREST_TAX_SETUP.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_BY_HAS_INTEREST, null));
			} else if (LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_DEPOSITO_BY_TEMPLATE, new Object[] { filter }));
			} else if (LookupConstants.DEPOSITO_TEMPLATE_BREAK.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_DEPOSITO_BY_TEMPLATE, new Object[] { filter }));
			}
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_BY_SEC_CLASS, new Object[] { filter }));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER, null));
		}
	}
	
	public static void securityTypesUnique(String filter) {
		log.debug("FILTER = " +filter);
		if (!Helper.isNullOrEmpty(filter)) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_UNIQUE, new Object[]{filter}));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER, null));
		}
	}

	public static void securityType(String code) {
		ScTypeMaster securityType = securityService.getSecurityTypeByStatusActive(code);

		if (securityType != null) {
			List<TdTransactionTemplate> transTemplate = new ArrayList<TdTransactionTemplate>();
			transTemplate = accountService.listTdTransactionTemplate(securityType.getSecurityType());

			if (transTemplate.size() == 0) {
				securityType.setTabCertificate(new BigDecimal(0));
			} else {
				securityType.setTabCertificate(new BigDecimal(1));
			}
		}

		renderJSON(securityType, new SecurityTypePickSerializer());
	}

	public static void securityTypeWithCash(String code) {
		ScTypeMaster securityType = securityService.getSecurityTypeWithCash(code);
		renderJSON(securityType, new SecurityTypePickSerializer());
	}

	/*
	 * public static void securityTypesWithPrice() { renderList
	 * (securityService.listSecurityTypesWithPriceAsSelectItem()); }
	 */

	// New Pick Security Type With Price For List
	public static void securityTypesWithPrice() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_BY_PRICE, null));
	}

	public static void securityTypeWithPrice(String code) {
		ScTypeMaster securityTypeWithPrice = securityService.getSecurityTypesWithPrice(code);
		renderJSON(securityTypeWithPrice, new SecurityTypePickSerializer());
	}

	public static void securityTypeWithInterestTax(String code) {
		ScTypeMaster securityTypeWithInterestTax = securityService.getSecurityTypeWithInterestTax(code);
		renderJSON(securityTypeWithInterestTax, new SecurityTypePickSerializer());
	}

	public static void securityTypesDeposito() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_SC_TYPE_MASTER_DEPOSITO, null));
	}

	public static void securityTypeDeposito(String code, String filter) {
		ScTypeMaster secType = securityService.getSecurityTypeDeposito(code, filter);
		ScTypeMaster securityType = new ScTypeMaster();
		if (secType != null) {
			securityType = securityService.getSecurityTypeByStatusActive(secType.getSecurityType());
			TdTransactionTemplate depTemplate = depositoService.getTdTransactionTemplate(filter, securityType.getSecurityType());
			securityType.setDepositoTrxTemplate(depTemplate);
			renderJSON(securityType, new SecurityTypePickSerializer());
		} else {
			renderJSON(secType, new SecurityTypePickSerializer());
		}
	}

	/*
	 * public static void taxMasters() {
	 * renderList(generalService.listTaxMastersAsSelectItem()); }
	 */

	// New Pick Tax Master For List
	public static void taxMasters() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_TAX_MASTER, null));
	}

	public static void taxMaster(String code) {
		GnTaxMaster taxMaster = generalService.getTaxMaster(code);
		renderJSON(taxMaster, new TaxMasterPickSerializer());
	}

	public static void chargeMaster(String chargeCode) {
		CsChargeMaster master = generalService.getChargeMasterByCode(chargeCode);
		renderJSON(master, new ChargeMasterPickSerializer());
	}

	public static void taxMasterKey(Long taxKey) {
		GnTaxMaster taxMaster = generalService.getTaxMaster(taxKey);
		renderJSON(taxMaster, new TaxMasterPickSerializer());
	}

	public static void txProfiles() {
		PickData pick = generalService.getPickLookup(PickCodeConstants.PICK_TX_PROFILE, null);
		renderList1(pick);

	}

	public static void txProfile(String code) {
		TxProfile txProfile = generalService.getTxProfile(code);
		renderJSON(txProfile, new TxProfilePickSerializer());
	}

	/*
	 * public static void rgProducts() {
	 * renderList(taService.listProductAsSelectItem()); }
	 */

	public static void listRgProducts() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_PRODUCT_ALL, null));
	}

	public static void getRgProduct(String code) {
		if (code != null && code.contains(SPECIAL_CHAR)) {
			code = code.replaceAll(SPECIAL_CHAR, "#");
		}

		RgProduct rgProduct = taService.loadProduct(code);
		renderJSON(rgProduct, new ProductPickSerializer());
	}

	// New Pick Tax Master For List
	public static void rgProducts() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_PRODUCT, null));
	}

	public static void rgProduct(String code) {
		if (code != null && code.contains(SPECIAL_CHAR)) {
			code = code.replaceAll(SPECIAL_CHAR, "#");
		}

		RgProduct rgProduct = taService.loadRgProductForPick(code);
		renderJSON(rgProduct, new ProductPickSerializer());
	}

	/*
	 * public static void rgProductByEffDateAndLiqDates(){
	 * renderList(taService.listProductForPickByEffAndLiqDate()); }
	 */

	// New Pick For Rg Producut by eff date and liq date
	public static void rgProductByEffDateAndLiqDates() {
		rgProducts();
	}

	public static void rgProductByEffDateAndLiqDate(String code) {
		RgProduct rgProduct = taService.loadProduct(code);
		renderJSON(rgProduct, new ProductPickSerializer());
	}

	public static void rgPortfolio(String productCode, String accountnumber, String holdingdate) throws ParseException {
		// RgPortfolio rgPortfolio = taService.loadPortfolio(accountnumber,
		// holdingdate);
		RgPortfolio rgPortfolio = taService.loadLastPortfolio(accountnumber, Registry.parseYYYYMMDD(holdingdate));
		RgNav rgNav = taService.loadLatestNav(productCode);
		// BigDecimal outstandingUnit =
		// taService.loadOutstandingUnit(accountnumber);
		// logger.debug("1. " + rgPortfolio.getUnit() + " & " + rgNav.getNav());
		// logger.debug("2. " + Registry.parseYYYYMMDD(holdingdate));
		BigDecimal outstandingUnit = taService.loadOutstanding(productCode, accountnumber, Registry.parseYYYYMMDD(holdingdate));
		if (rgPortfolio != null && rgNav != null) {
			BigDecimal unit = rgPortfolio.getUnit() == null ? BigDecimal.ZERO : rgPortfolio.getUnit();
			BigDecimal nav = rgNav.getNav() == null ? BigDecimal.ZERO : rgNav.getNav();
			rgPortfolio.setUnit(rgPortfolio.getUnit().subtract(outstandingUnit));
			// rgPortfolio.setUnit(rgPortfolio.getUnit());
			if (outstandingUnit != null) {
			}
			rgPortfolio.setBalanceAmount(unit.multiply(nav));
		}
		renderJSON(rgPortfolio, new RgPortfolioPickSerializer());
	}

	public static void rgPortfolioUnit(String productCode, String holdingdate) {
		BigDecimal totalunit = taService.loadPortfolioProduct(productCode, holdingdate);
		renderJSON(totalunit);
	}

	public static void calcTotalUnitMaturity(String productCode, String postDate, String navDate) {
		BigDecimal totalunit = BigDecimal.ZERO;
		try {
			totalunit = taService.calcTotalUnitMaturity(productCode, Helper.parse(postDate, "yyyyMMdd"), Helper.parse(navDate, "yyyyMMdd"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		renderJSON(totalunit);
	}
	
	public static void getVwFundUnitBal(String productCode, String holdingdate) {
		BigDecimal totalunit = taService.getVwFundUnitBal(productCode, holdingdate);
		renderJSON(totalunit);
	}

	public static void getUnitFromDwnBal(String productCode, String tradeDate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			BigDecimal totalunit = taService.getUnitFromDwnBal(productCode, sdf.parse(tradeDate));
			renderJSON(totalunit);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void rgPortfolioUnitAccount(String accountNumber, String holdingdate) {
		BigDecimal totalunit = taService.loadPortfolioProductAccount(accountNumber, holdingdate);
		renderJSON(totalunit);
	}

	/*
	 * public static void rgInvestmentAccts(String type) {
	 * renderList(taService.listInvestmentAsSelectItem(type)); }
	 */

	// List all RgInvestmentaccount
	public static void rgInvestmentAccounts() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST, new Object[] {}));
	}

	// New Pick RgInvestment Acc By Product
	public static void rgInvestmentAccts(String type) {
		if (type != null && type.contains(SPECIAL_CHAR)) {
			type = type.replaceAll(SPECIAL_CHAR, "#");
		}

		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_PROD, new Object[] { type }));
	}

	public static void rgInvestmentsAccounts(String type) {
		if (type.isEmpty()) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST, new Object[] {}));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_PROD, new Object[] { type }));
		}
	}

	public static void rgInvestmentAcctsByProductCodeAndSaCode(String filter) {
		String[] filters = getArray(filter);
		String productCode = filters[0].trim();
		String saCode = filters[1].trim();

		if (productCode.isEmpty() && saCode.isEmpty()) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST, new Object[] {}));
		} else if (!productCode.isEmpty() && saCode.isEmpty()) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_PROD, new Object[] { productCode }));
		} else if (productCode.isEmpty() && !saCode.isEmpty()) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_SACODE, new Object[] { saCode }));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_PROD_AND_SACODE, new Object[] { productCode, saCode }));
		}
	}

	public static void rgInvestmentAcctByProductCodeAndSaCode(String filter, String code) {
		String[] filters = getArray(filter);
		String productCode = filters[0];
		Long saCode = (!filters[1].isEmpty()) ? Long.parseLong(filters[1]) : null;
		RgInvestmentaccount rgInvestmentaccount = taService.loadInvestmentByRgProductAndSaCode(productCode, saCode, code);
		renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
	}

	public static void rgInvestmentAcctsByProductCodeAndInvestor(String filter) {
		String[] filters = getArray(filter);
		String productCode = filters[0];
		String customerKey = filters[1];
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_PROD_AND_INVESTOR, new Object[] { productCode, customerKey }));
	}

	public static void rgInvestmentAcctByProductCodeAndInvestor(String filter, String code) {
		String[] filters = getArray(filter);
		String productCode = filters[0];
		String customerKey = filters[1];
		RgInvestmentaccount rgInvestmentaccount = taService.loadInvestmentByRgProductAndInvestor(productCode, Long.parseLong(customerKey), code);
		renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
	}

	public static void rgInvestmentAcct(String type, String code) {
		type = "null".equals(type) ? null : type;

		if (type != null && type.contains(SPECIAL_CHAR)) {
			type = type.replaceAll(SPECIAL_CHAR, "#");
		}

		RgInvestmentaccount rgInvestmentaccount = taService.loadInvestment(type, code);
		renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
	}

	public static void rgInvestmentAcctDownloadUnitTrust(String type, String code) {
		RgInvestmentaccount rgInvestmentaccount = new RgInvestmentaccount();
		if (!type.trim().isEmpty()) {
			type = "null".equals(type) ? null : type;
			rgInvestmentaccount = taService.loadInvestment(type, code);
		} else {
			rgInvestmentaccount = taService.loadInvestment(code);
		}

		renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
	}

	// skip new pick for this method cause for temporary there aren't modul call
	// it
	public static void rgInvestmentByCustomers(String customercode, String fmgrCode, String saCode, String currencyCode) {
		renderList(taService.listInvestmentByCustomer(customercode, fmgrCode, saCode, currencyCode));
	}

	// New Pick RgInvestment By Cust For List -- SKIP
	/*
	 * public static void rgInvestmentByCustomerPicks(String customercode,
	 * String fmgrCode, String saCode, String currencyCode) { if (fmgrCode ==
	 * null && saCode == null && currencyCode == null) {
	 * renderList1(generalService
	 * .getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_CUST, new
	 * Object[]{customercode})); } else {
	 * renderList1(generalService.getPickLookup
	 * (PickCodeConstants.PICK_RG_INVEST_BY_CUST_WITH_FMGR_SA_CURR, new
	 * Object[]{customercode, fmgrCode, saCode, currencyCode})); } }
	 */

	public static void rgInvestmentByCustomer(String customercode, String fmgrCode, String saCode, String currencyCode, String code) {
		RgInvestmentaccount rgInvestmentaccount = taService.loadInvestmentByCustomer(customercode, fmgrCode, saCode, currencyCode, code);
		renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
	}

	/*
	 * public static void rgInvestmentByProducts(String productCode, Long
	 * customerKey, String fmgrCode, String saCode, String currencyCode) {
	 * renderList(taService.listInvestmentByProduct(productCode, customerKey,
	 * fmgrCode, saCode, currencyCode)); }
	 */

	// New Pick RgInvestmentByProduct for List
	public static void rgInvestmentByProducts(String productCode, Long customerKey, String fmgrCode, String saCode, String currencyCode) {
		if (fmgrCode == null && saCode == null && currencyCode == null && customerKey == null) {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_PROD, new Object[] { productCode }));
		} else {
			renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_INVEST_BY_PROD_CUST_FMGR_SA_CURR, new Object[] { productCode, customerKey, fmgrCode, saCode, currencyCode }));
		}
	}

	public static void rgInvestmentByProduct(String productCode, Long customerKey, String fmgrCode, String saCode, String currencyCode, String code) {
		RgInvestmentaccount rgInvestmentaccount = taService.loadInvestmentByProduct(productCode, customerKey, fmgrCode, saCode, currencyCode, code);
		renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
	}

	/*
	 * public static void rgBankAccounts(String accountnumber) {
	 * renderList(bankAccountService
	 * .listBankAccountsByAccoutNumberAsSelectItem(accountnumber)); }
	 */

	// New Pick rgBank Account
	public static void rgBankAccounts(String accountnumber) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_INVEST_ACC, new Object[] { accountnumber }));
	}

	// New Pick rgBank Account
	public static void bankAccountsByAccountNumberCurrency(String currencyCode, Long customerKey) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_INVEST_ACC_ALL, new Object[] { customerKey, currencyCode }));
	}

	public static void rgBankAccount(String accountnumber, String code) {
		BnAccount bankAccount = bankAccountService.getBankAccountByNumber(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	/*
	 * public static void rgProdBankAccounts(String productCode) {
	 * renderList(taService
	 * .listProdBankAccountsByProductCodeAsSelectItem(productCode)); }
	 */

	// Pick for Bank Account By RG_PRODUCT_BN_ACCOUNT
	public static void bankAccountsByRgProductBnAccounts(String productCode) {

		if (productCode != null && productCode.contains(SPECIAL_CHAR)) {
			productCode = productCode.replaceAll(SPECIAL_CHAR, "#");
		}

		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_RG_PRODUCT_BN_ACCOUNT, new Object[] { productCode }));
	}

	public static void bankAccountsByRgProductBnAccountsDomain(String productCode) {
		String domain = "";
		if (productCode.contains("|")) {
			String[] arrays = productCode.split("\\|");
			productCode = arrays[0];
			domain = arrays[1];
		}

		if (productCode != null && productCode.contains(SPECIAL_CHAR)) {
			productCode = productCode.replaceAll(SPECIAL_CHAR, "#");
		}

		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_RG_PRODUCT_BN_ACCOUNT_DOMAIN, new Object[] { productCode, domain }));
	}

	// Pick for Bank Account By RG_PRODUCT_BN_ACCOUNT
	public static void bankAccountsActiveByProduct(String productCode, Long customerKey) {

		if (productCode != null && productCode.contains(SPECIAL_CHAR)) {
			productCode = productCode.replaceAll(SPECIAL_CHAR, "#");
		}

		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_ACTIVE_BY_RG_PRODUCT, new Object[] { productCode }));
	}

	public static void bankAccountsByRgProductBnAccount(String productCode, String code) {
		BnAccount bankAccount = bankAccountService.getBankAccountByProduct(productCode, code);
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	public static void bankAccountsByRgProductBnAccountDomain(String productCode, String code) {
		String domain = "";
		if (productCode.contains("|")) {
			String[] arrays = productCode.split("\\|");
			productCode = arrays[0];
			domain = arrays[1];
		}

		BnAccount bankAccount = bankAccountService.getBankAccountByProduct(productCode, domain, code);
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	// Pick for Bank Account By RG_INVESTMENTACCOUNT
	public static void bankAccountbyRgInvestments(String productCode, String accountNumber) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_RG_INVESTMENTACCT_AND_PRODUCT, new Object[] { productCode, accountNumber }));
	}

	public static void bankAccountbyRgInvestment(String productCode, String accountNumber, String code) {
		BnAccount bankAccount = bankAccountService.getBankAccountByInvestmentAccount(productCode, accountNumber, code);
		renderJSON(bankAccount, new BankAccountPickSerializer());
	}

	// Pick for Bank Account By RG_INVESTMENTACCOUNT on payment
	public static void bankAccountbyRgInvestmentspayments(String currencyCode, Long customerKey) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_BY_INVEST_ACC_ALL, new Object[] { customerKey, currencyCode }));
	}

	// New Pick rgProdBankAccoount For list
	public static void rgProdBankAccounts(String productCode) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_PROD_BANK_ACC, new Object[] { productCode }));
	}

	// public static void rgProdBankAccount(String code) {
	// RgProdBnAccount bankAccount = taService.getProdBankAccountByNumber(code);
	// renderJSON(bankAccount, new RgProdBankAccountPickSerializer());
	// }

	public static void rgProdBankAccount(String code) {
		RgProductBnAccount bankAccount = taService.getProductBankAccountByNumber(code);
		renderJSON(bankAccount, new RgProductBankAccountPickSerializer());
	}

	public static void rgProductBankAccountThirdPartyBankCode(String code) {
		RgProductBnAccount productBankAccount = taService.getProductBankAccountThirdPartyBankCodeByNumber(code);
		renderJSON(productBankAccount, new RgProductBankAccountPickSerializer());
	}

	public static void rgNav(String navDate, String productcode) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		RgNav nav = new RgNav();
		try {
			//RgNavId id = new RgNavId(productcode, sdf.parse(navDate));
			// nav = taService.loadNav(id);
			nav = taService.loadActiveNav(productcode, sdf.parse(navDate));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		renderJSON(nav, new NavPickSerializer());
	}

	public static void priceByNav(String navDate, String productcode) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		RgNav nav = new RgNav();
		try {
			RgProduct product = taService.loadProduct(productcode);
			if (product.isFixnav()) {
				nav.setNav(product.getFixNavPrice());
			} else {
				nav = taService.loadActiveNav(productcode, sdf.parse(navDate));
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		renderJSON(nav, new NavPickSerializer());
	}

	/*
	 * public static void listRgProdEodAsSelectitems() {
	 * renderList(taService.listRgProdEodAsSelectitem()); }
	 */

	// New Pick listRgProdEods For List
	public static void listRgProdEodAsSelectitems() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_RG_PROD_EOD, null));
	}

	public static void getRgProdEodAsSelectitem(String code) {
		RgProdEod rgProdEod = taService.getRgProdEod(code);
		renderJSON(rgProdEod, new RgProdEodPickSerializer());
	}

	public static void addWorkingDate(String yyyyMMdd, String day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Date workingDate = generalService.getWorkingDate(sdf.parse(yyyyMMdd), new Integer(day));
			yyyyMMdd = sdf.format(workingDate);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		renderJSON(yyyyMMdd);
	}

	public static void addCutOfTime(String yyyyMMdd, String hour) {
		hour = hour + "00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String hourSys = (new SimpleDateFormat("HHmm")).format(new Date());
		int intHour = Integer.parseInt(hour);
		int intHourSys = Integer.parseInt(hourSys);
		// if (hourSys.compareTo(hour) > 0) { ---> this compare issue where 1000
		// compare 900 always false
		if (intHourSys > intHour) {
			try {
				Date workingDate = generalService.addWorkingDate(sdf.parse(yyyyMMdd), 1);
				yyyyMMdd = sdf.format(workingDate);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}

		renderJSON(yyyyMMdd);
	}

	public static void rgFeeTier(String productcode, String type, String amount) {
		BigDecimal newAmount = BigDecimal.ZERO;
		if (amount.equals("undefined") || amount.isEmpty()) {
			newAmount = null;
		} else {
			newAmount = new BigDecimal(amount);
		}
		RgFeeTier feeTier = taService.loadFeeTier(productcode, type, newAmount);
		renderJSON(feeTier, new FeeTierPickSerializer());
	}

	public static void rgFeeAmount(String productcode, String type, String amount, String inputBy) {
		BigDecimal feeAmount = taService.loadFeeAmount(productcode, type, new BigDecimal(amount), inputBy);
		renderJSON(feeAmount);
	}

	public static void getApplicationDate() {
		GnApplicationDate gnAppDate = generalService.getApplicationDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
		renderJSON(gnAppDate.getCurrentBusinessDate().getTime());
	}

	public static void getServerDate() {
		GnApplicationDate gnAppDate = generalService.getApplicationDate(LookupConstants.SIMIAN_ORGANIZATION_ID);
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int secods = cal.get(Calendar.SECOND);
		cal.setTime(gnAppDate.getCurrentBusinessDate());
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, secods);
		renderJSON(cal.getTime());
	}

	/*
	 * public static void thirdParties(String type) {
	 * renderList(generalService.listThirdPartiesAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID, type)); }
	 */

	public static void thirdPartiesSaCode() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_DWN_SELLING_AGENT, null));
	}

	public static void thirdPartySaCode(String code) {
		GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.THIRD_PARTY_SELLINGAGENT, code);
		renderJSON(thirdParty, new ThirdPartyPickSerializer());
	}

	// New Pick Third Party For List
	public static void thirdParties(String type) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_THIRD_PARTY, new Object[] { type }));
	}

	/*
	 * public static void thirdPartiesWithParent(String type) {
	 * renderList(generalService.listThirdPartiesWithParentAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID, type)); }
	 */

	// New Pick Third Party Parent For List
	public static void thirdPartiesWithParent(String type) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_THIRD_PARTY_PARENT, new Object[] { type }));
	}

	/*
	 * public static void thirdPartiesWithChild(Long key) {
	 * renderList(generalService.listThirdPartiesWithChildAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID, key)); }
	 */

	// New Pick Third Party Child For List
	public static void thirdPartiesWithChild(Long key) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_THIRD_PARTY_CHILD, new Object[] { key }));
	}

	public static void thirdParty(String type, String code) {
		GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, type, code);
		/*
		 * if (type.equals(LookupConstants.THIRD_PARTY_ISSUER)) { thirdParty =
		 * generalService.getGnThirdPartyByCode(code); } else { thirdParty =
		 * generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID,
		 * type, code); }
		 */
		renderJSON(thirdParty, new ThirdPartyPickSerializer());
	}

	public static void thirdPartyByKey(Long key) {
		GnThirdParty thirdParty = generalService.getThirdParty(key);
		renderJSON(thirdParty, new ThirdPartyPickSerializer());
	}

	/*
	 * public static void thirdPartyByTypeCustodians() {
	 * renderList(generalService
	 * .listThirdPartyByTypeCustodiansAsSelectItem(UIConstants.SIMIAN_BANK_ID));
	 * }
	 */

	// New Pick third party by type custodian For List
	public static void thirdPartyByTypeCustodians() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_THIRD_PARTY_TYPE_CUSTODIAN, null));
	}

	public static void thirdPartyByTypeCustodian(String code) {
		GnThirdParty thirdParty = generalService.getThirdPartyByTypeCustodian(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(thirdParty, new ThirdPartyPickSerializer());
	}

	/*
	 * public static void transactions(String filter) {
	 * renderList(customerService.listTransactionAsSelectItem(filter)); }
	 */

	// New Pick tdMaster for List
	public static void tdMasters(String filter) {
		String[] filters = filter.split("-");
		Long accountKey = Long.parseLong(filters[0]);
		Long securityKey = Long.parseLong(filters[1]);
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_TD_MASTER, new Object[] { accountKey, securityKey }));

	}

	public static void tdMaster(String code) {
		TdMaster tdMaster = generalService.getTdMaster(code);
		if (tdMaster != null) {
			TdTransaction tdTransaction = generalService.getAmountTransaction(tdMaster.getDepositoKey());
			if (tdTransaction != null) {
				if (tdTransaction.getAmount() != null) {
					tdMaster.setNominal(tdTransaction.getAmount());
				}
			}
		}
		renderJSON(tdMaster, new TdMasterPickSerializer());
	}

	public static void tdMasterPick(String code, Long account, Long security) {
		TdMaster tdMaster = depositoService.getPickTdMaster(code, account, security);
		if (tdMaster != null) {
			TdTransaction tdTransaction = generalService.getAmountTransaction(tdMaster.getDepositoKey());
			if (tdTransaction != null) {
				if (tdTransaction.getAmount() != null) {
					tdMaster.setNominal(tdTransaction.getAmount());
				}
			}
		}
		renderJSON(tdMaster, new TdMasterPickSerializer());
	}

	public static void depositos(String filter) {
		String[] filters = getArray(filter);

		Long accountKey = (long) 0;
		if (!Helper.isNullOrEmpty(filters[0])) {
			accountKey = Long.parseLong(filters[0]);
		}

		Long securityKey = (long) 0;
		if (!Helper.isNullOrEmpty(filters[1])) {
			securityKey = Long.parseLong(filters[1]);
		}

		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_DEPOSITO, new Object[] { accountKey, securityKey }));
	}

	public static void deposito(String code, String filter) {
		String[] filters = getArray(filter);

		Long accountKey = (long) 0;
		if (!Helper.isNullOrEmpty(filters[0])) {
			accountKey = Long.parseLong(filters[0]);
		}

		Long securityKey = (long) 0;
		if (!Helper.isNullOrEmpty(filters[1])) {
			securityKey = Long.parseLong(filters[1]);
		}
		TdMaster tdMaster = depositoService.getDepositoByPick(code, accountKey, securityKey);
		renderJSON(tdMaster, new TdMasterPickSerializer());
	}

	// New Pick transaction for List
	public static void transactions(String filter) {
		String[] filters = filter.split("-");
		Long accountKey = Long.parseLong(filters[0]);
		Long securityKey = Long.parseLong(filters[1]);
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRX_BY_ACC_SEC, new Object[] { accountKey, securityKey }));
	}

	public static void transactionsDetail(String filter) {
		String[] filters = filter.split("-");
		Long accountKey = Long.parseLong(filters[0]);
		Long securityKey = Long.parseLong(filters[1]);
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRX_BY_ACC_SEC_ADDDESC, new Object[] { accountKey, securityKey }));

	}

	public static void transaction(String code, String filter) {
		if (code == null) {
			code = new String();
		}

		CsTransaction transaction = customerService.getTransactionForCertificate(code, filter);
		// CsPortfolioTransaction portfolio =
		// accountService.getPortfolioTransaction(transaction.getTransactionKey());

		// BigDecimal t
		if (transaction != null) {
			// transaction.setCurrentHolding(totQuantity);
			//List<CsTransaction> transactions = accountService.listTransactionByTransNo(transaction.getTransactionNumber());
			CsPortfolioTransaction portfolio = accountService.getPortfolioTransaction(transaction.getTransactionNumber(), LookupConstants.__RECORD_STATUS_SETTLED);

			if (portfolio != null) {
				transaction.setHoldingRefs(portfolio.getHoldingRefs());
				/*
				 * logger.debug("not null portfolion "+transaction.getHoldingRefs
				 * ()); //*** TRANSACTION STATUS "O" **
				 *//*
					 * if ((transaction.getTransactionStatus().trim().equals(
					 * LookupConstants.__RECORD_STATUS_OPEN) &&
					 * (transaction.getTransactionTemplate
					 * ().getTransactionCategory
					 * ().getLookupId().equals(LookupConstants
					 * .TRANSACTION_CATEGORY_TRANSACTION))) ||
					 * (portfolio.getTransactionStatus
					 * ().trim().equals(LookupConstants.__RECORD_STATUS_OPEN) ||
					 * (portfolio.getTransactionStatus().isEmpty()))){
					 * transaction
					 * .setTransactionStatus(LookupConstants.__RECORD_STATUS_OPEN
					 * ); }
					 * 
					 * //*** TRANSACTION STATUS "A" **
					 *//*
						 * if
						 * (((transaction.getTransactionStatus().trim().equals
						 * (LookupConstants.__RECORD_STATUS_APPROVED)) &&
						 * (transaction
						 * .getTransactionTemplate().getTransactionCategory
						 * ().getLookupId().equals(LookupConstants.
						 * TRANSACTION_CATEGORY_TRANSACTION))) &&
						 * (portfolio.getTransactionStatus
						 * ().trim().equals(LookupConstants
						 * .__RECORD_STATUS_APPROVED))){
						 * transaction.setTransactionStatus
						 * (LookupConstants.__RECORD_STATUS_APPROVED); }
						 * 
						 * for (CsTransaction csTransaction : transactions) { if
						 * (csTransaction.getSettlementNumber()!=null) {
						 * transaction =
						 * accountService.getTransaction(csTransaction
						 * .getTransactionKey());
						 * logger.debug("TRANSACTION CATEGORY =  "
						 * +transaction.getTransactionTemplate
						 * ().getTransactionCategory().getLookupId());
						 * logger.debug("TRANSACTION STATUS = "
						 * +transaction.getTransactionStatus());
						 * logger.debug("PORTFOLIO STATUS = "
						 * +portfolio.getTransactionStatus());
						 * logger.debug("SETTLE NUMBER = "
						 * +transaction.getSettlementNumber());
						 * 
						 * //*** TRANSACTION STATUS "SE" **
						 *//*
							 * if
							 * (((transaction.getTransactionStatus().trim().equals
							 * (LookupConstants.__RECORD_STATUS_OPEN)) &&
							 * (transaction
							 * .getTransactionTemplate().getTransactionCategory
							 * ().getLookupId().equals(LookupConstants.
							 * TRANSACTION_CATEGORY_SETTLEMENT))) &&
							 * (portfolio.getTransactionStatus
							 * ().trim().equals(LookupConstants
							 * .__RECORD_STATUS_APPROVED))){
							 * transaction.setTransactionStatus
							 * (LookupConstants._TRANSACTION_STATUS_SETTLE_ENTRY
							 * ); }
							 * 
							 * //*** TRANSACTION STATUS "SA" **
							 *//*
								 * if
								 * (((transaction.getTransactionStatus().trim(
								 * ).equals
								 * (LookupConstants.__RECORD_STATUS_APPROVED))
								 * && (transaction.getTransactionTemplate().
								 * getTransactionCategory
								 * ().getLookupId().equals(
								 * LookupConstants.TRANSACTION_CATEGORY_SETTLEMENT
								 * ))) &&
								 * (portfolio.getTransactionStatus().trim(
								 * ).equals
								 * (LookupConstants.__RECORD_STATUS_SETTLED))){
								 * transaction
								 * .setTransactionStatus(LookupConstants
								 * ._TRANSACTION_STATUS_SETTLE_APPROVE); }
								 * 
								 * //*** TRANSACTION STATUS "A" (Another
								 * Condition) **
								 *//*
									 * if
									 * (((transaction.getTransactionStatus().trim
									 * ().equals(LookupConstants.
									 * __RECORD_STATUS_REJECTED)) &&
									 * (transaction.getTransactionTemplate().
									 * getTransactionCategory
									 * ().getLookupId().equals(LookupConstants.
									 * TRANSACTION_CATEGORY_SETTLEMENT))) &&
									 * (portfolio
									 * .getTransactionStatus().trim().equals
									 * (LookupConstants
									 * .__RECORD_STATUS_APPROVED))){
									 * transaction.
									 * setTransactionStatus(LookupConstants
									 * .__RECORD_STATUS_APPROVED); } } }
									 */

			}
		}
		renderJSON(transaction, new TransactionForCertificatePickSerializer());
	}

	/*
	 * public static void transactionMasters() {
	 * renderList(accountService.listTransactionMastersAsSelectItem
	 * (UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick Transaction Master For List
	public static void transactionMasters() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRX_MASTER, null));
	}

	public static void transactionMaster(String code) {
		CsTransactionMaster templateMaster = accountService.getTransactionMaster(UIConstants.SIMIAN_BANK_ID, code);
		renderJSON(templateMaster, new TransactionMasterPickSerializer());
	}

	/*
	 * public static void transactionTemplates(String filter) {
	 * logger.debug("FILTER trans template = " +filter);
	 * renderList(accountService
	 * .listTransactionTemplatesAsSelectItem(UIConstants.SIMIAN_BANK_ID,
	 * filter)); }
	 */

	// New Pick Transaction Template For List
	public static void transactionTemplates(String filter) {
		if (!Helper.isNullOrEmpty(filter)) {
			if (LookupConstants.__TRANSACTION_TEMPLATE_TYPE_SETTLEMENT.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRX_TEMPLATE_CAT_SETTLEMENT, null));
			} else if (LookupConstants.__TRANSACTION_TEMPLATE_TYPE_CANCEL.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRX_TEMPLATE_CAT_CANCELATION, null));
			} else {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRX_TEMPLATE_USED_BY, new Object[] { filter }));
			}
		}
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRX_TEMPLATE, null));

	}

	public static void transactionTemplate(String code, String filter) {
		CsTransactionTemplate template = accountService.getTransactionTemplate(UIConstants.SIMIAN_BANK_ID, code, filter);
		renderJSON(template, new TransactionTemplatePickSerializer());
	}

	/*
	 * public static void transactionTemplateWithSecurityTypes(String filter){
	 * renderList
	 * (accountService.listChargesProfileTransactionTemplatesAsSelectItem
	 * (UIConstants.SIMIAN_BANK_ID, filter)); }
	 */

	// New Pick Transaction Template By Security Type
	public static void transactionTemplateWithSecurityTypes(String filter) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRX_TEMPLATE_BY_SEC_TYPE, new Object[] { filter }));
	}

	public static void transactionTemplateBuyWithSecurityTypes(String filter) {
		renderList(accountService.listInHouseTransferBuyTransactionTemplatesAsSelectItem(UIConstants.SIMIAN_BANK_ID, filter));
	}

	public static void transactionTemplateWithSecurityType(String code, String filter) {
		CsTransactionTemplate templateWithSecurityType = accountService.getChargesProfilesTransactionTemplate(UIConstants.SIMIAN_BANK_ID, code, filter);
		renderJSON(templateWithSecurityType, new TransactionTemplatePickSerializer());
	}

	/*
	 * public static void users() {
	 * renderList(applicationService.listUsersAsSelectItem
	 * (UIConstants.SIMIAN_BANK_ID)); }
	 */

	// New Pick User For List
	public static void users() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_USER, null));
	}

	public static void user(String code) {
		GnUser user = applicationService.getUser(code);
		renderJSON(user, new UserPickSerializer());
	}

	private static void renderList(List<SelectItem> items) {
		render("Pick/grid.html", items);
	}

	private static void renderList1(PickData pick) {
		render("Pick/grid1.html", pick);
	}

	private static boolean isArray(String value) {
		if (value == null)
			return false;
		if (value.startsWith("[") && value.endsWith("]"))
			return true;
		return false;
	}

	private static String[] getArray(String value) {
		if (isArray(value)) {
			String stripped = value.substring(1, value.length() - 1);
			String[] splitted = stripped.split(",");
			for (int i = 0; i < splitted.length; i++) {
				if (splitted[i].startsWith("\"") && splitted[i].endsWith("\"")) {
					splitted[i] = splitted[i].substring(1, splitted[i].length() - 1);
				}
			}
			return splitted;
		} else {
			return null;
		}
	}

	public static void pledgings(String filter) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_PL_PLEDGING, new Object[] { filter }));
	}

	public static void pledging(String code, String filter) {
		if (!filter.isEmpty()) {
			PlTransaction pledging = transactionService.getPledgingForPick(Long.parseLong(code), Long.parseLong(filter));
			renderJSON(pledging, new PledgingPickSerializer());
		}
	}

	public static void udBatch(String filter) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_UD_BATCH, null));
	}

	public static void udBatchByBatchKey(String code) {
		UpdBatch updBatch = generalService.getUpdBatch(Long.valueOf(code));
		renderJSON(updBatch, new UpdBatchSerializer());
	}

	public static void udProfiles(String filter) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_UD_PROFILE, new Object[] { filter }));
	}

	public static void udProfilesTemplete(String filter) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_UD_PROFILE_TEMPLATE, new Object[] { filter }));
	}

	public static void fundBankAccounts(String code) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_BN_ACCOUNT_PRODUCT_CUSTOMER, new Object[] { code }));
	}

	public static void fundBankAccount(Long code) {
		BnAccount bnAcc = bankAccountService.getBankAccount(code);
		renderJSON(bnAcc, new BankAccountPickSerializer());
	}

	public static void udProfile(String code, String filter) {
		UpdProfile udProfile = generalService.getUpdProfileByName(code, filter);
		renderJSON(udProfile, new UpdProfilePickSerializer());
	}

	public static void udProfileForRole(String code, String filter) {
		UpdProfile udProfile = generalService.getUpdProfileByName(code, filter);
		renderJSON(udProfile, new UpdProfilePickRolesSerializer());
	}

	public static void udProfileByName(String code, String filter) {
		UpdProfile udProfile = generalService.getUpdProfileByName(code, filter);
		if (udProfile != null) {
			udProfile.setDetails(generalService.getUpdProfileDetail(udProfile.getProfileKey()));
		}
		renderJSON(udProfile, new UpdProfileDetailPickSerializer(), new UpdProfilePickSerializer(), new UpdFilterSerializer());
	}

	public static void getUdProfilePickByName(String code, String filter) {
		UpdProfile udProfile = generalService.getUdProfilePickByName(code, filter);
		if (udProfile != null) {
			udProfile.setDetails(generalService.getUpdProfileDetail(udProfile.getProfileKey()));
		}
		renderJSON(udProfile, new UpdProfileDetailPickSerializer(), new UpdProfilePickSerializer(), new UpdFilterSerializer());
	}

	public static void retrieveFieldListOfView(String source) {
		PickData pickData = generalService.getPickLookup(PickCodeConstants.PICK_TBLNAME, new Object[] { source.toUpperCase() });
		pickData.setQuery("");
		renderJSON(pickData);
	}

	public static void tblNames(String source) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_TBLNAME, new Object[] { source.toUpperCase() }));
	}

	public static void tblName(String code, String source) {
		Object udProfil = generalService.getTblName(source.toUpperCase(), code.toUpperCase());
		renderJSON(udProfil, new TblNamePickSerializer());
	}

	public static void getColumns(String process, String filter) {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GET_COLUMN, new Object[] { process, filter }));
	}

	public static void getColumnsBySource(String profileKey, String exceptionCols) {
		String[] splitted = exceptionCols.split(";");
		List<String> listExceptional = new ArrayList<String>();
		for (int x = 0; x < splitted.length; x++) {
			listExceptional.add(splitted[x]);
		}
		PickData pickData = generalService.getPickLookup(PickCodeConstants.PICK_GET_COLUMNS_BY_SOURCE, new Object[] { profileKey });
		List<PickRow> filteredPickRow = new ArrayList<PickRow>();
		for (PickRow row_ : pickData.getRows()) {
			if (!listExceptional.contains(row_.getData()[0].toString())) {
				filteredPickRow.add(row_);
			}
		}
		pickData.setRows(filteredPickRow);
		renderList1(pickData);
	}

	public static void getColumnBySource(String profileKey, String code) {
		UpdProfileDetail updProfilDetail = generalService.getUpdProfileDetail(Long.parseLong(profileKey), code);
		renderJSON(updProfilDetail, new UpdProfileDetailPickSerializer());
	}

	public static void getColumn(String code, String process, String filter) {
		Object udProfil = generalService.getColumn(code, process, filter);
		renderJSON(udProfil, new TblNamePickSerializer());
	}

	public static void getCancelInstructionsMessageType(String filter) {
		if (!Helper.isNullOrEmpty(filter)) {
			if (LookupConstants.CBESTMESSAGE_MESSAGE_TYPE_PREMATCHDATA.equals(filter)) {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CBESTMESSAGE_CANCELINSTRUCTION_PREMATCH, null));
			} else {
				renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CBESTMESSAGE_CANCELINSTRUCTION_EXTDATA, new Object[] { filter }));
			}
		}
	}

	public static void getCancelInstructionMessageType(String code) {
		final int noParam = 3;
		CBestMessage cBestMessage = null;
		String uniqueIdentifier = "";
		String externalReference = "";
		String name = "";
		String[] params = code.split("\\|");

		if ((params != null) && (params.length == noParam)) {
			if ((params[0] != null) && (params[0].trim().length() > 0)) {
				uniqueIdentifier = params[0];
			}
			if ((params[1] != null) && (params[1].trim().length() > 0)) {
				externalReference = params[1];
			}
			if ((params[2] != null) && (params[2].trim().length() > 0)) {
				name = params[2];
			}
		}

		List<CBestMessage> result = null;

		if (name.equalsIgnoreCase("PrematchData")) {
			result = cbestService.getMessagePrematchForRow(uniqueIdentifier, externalReference, name);
		} else {
			result = cbestService.getMessageExtRefForRow(uniqueIdentifier, externalReference, name);
		}

		if ((result != null) && (result.size() > 0)) {
			cBestMessage = result.get(0);
		}

		renderJSON(cBestMessage, new CBestMessagePickSerializer());
	}

	public static void getDecode() {
		Map map = new HashMap<String, String>();
		map.put(LookupConstants.__RECORD_STATUS_NEW, StatusExtension.decodeStatus(LookupConstants.__RECORD_STATUS_NEW));
		map.put(LookupConstants.__RECORD_STATUS_APPROVED, StatusExtension.decodeStatus(LookupConstants.__RECORD_STATUS_APPROVED));
		map.put(LookupConstants.__RECORD_STATUS_UPDATED, StatusExtension.decodeStatus(LookupConstants.__RECORD_STATUS_UPDATED));
		map.put(LookupConstants.__RECORD_STATUS_REJECTED, StatusExtension.decodeStatus(LookupConstants.__RECORD_STATUS_REJECTED));
		map.put(LookupConstants.__RECORD_STATUS_DELIVERED, StatusExtension.decodeStatus(LookupConstants.__RECORD_STATUS_DELIVERED));
		renderJSON(map); 
	}

	public static void getDecodeData() {
		Map map = new HashMap<String, String>();
		map.put(LookupConstants.__RECORD_STATUS_NEW, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_NEW));
		map.put(LookupConstants.__RECORD_STATUS_APPROVED, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_APPROVED));
		map.put(LookupConstants.__RECORD_STATUS_VALID, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_VALID));
		map.put(LookupConstants.__RECORD_STATUS_POSTED, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_POSTED));
		map.put(LookupConstants.__RECORD_STATUS_UPDATED, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_UPDATED));
		map.put(LookupConstants.__RECORD_STATUS_REJECTED, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_REJECTED));
		map.put(LookupConstants.__RECORD_STATUS_CANCELED, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_CANCELED));
		map.put(LookupConstants.__RECORD_STATUS_OPEN, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_OPEN));
		map.put(LookupConstants.__RECORD_STATUS_CLOSE_CANCEL_OPEN, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_CLOSE_CANCEL_OPEN));
		map.put(LookupConstants.__RECORD_STATUS_SETTLED, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_SETTLED));
		map.put(LookupConstants.TRX_STATUS_WAITING_PREMATCHING, StatusExtension.decodeDataStatus(LookupConstants.TRX_STATUS_WAITING_PREMATCHING));
		map.put(LookupConstants.TRX_STATUS_WAITING_PREMATCH_APPROVE, StatusExtension.decodeDataStatus(LookupConstants.TRX_STATUS_WAITING_PREMATCH_APPROVE));
		map.put(LookupConstants.TRX_STATUS_WAITING_SETTLEMENT, StatusExtension.decodeDataStatus(LookupConstants.TRX_STATUS_WAITING_SETTLEMENT));
		map.put(LookupConstants.__RECORD_STATUS_ENTITLED, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_ENTITLED));
		map.put(LookupConstants.TRX_STATUS_WAITING_ENTITLEMENT_APPROVE, StatusExtension.decodeDataStatus(LookupConstants.TRX_STATUS_WAITING_ENTITLEMENT_APPROVE));
		map.put(LookupConstants.TRX_STATUS_WAITING_SETTLEMENT_APPROVE, StatusExtension.decodeDataStatus(LookupConstants.TRX_STATUS_WAITING_SETTLEMENT_APPROVE));
		map.put(LookupConstants.__RECORD_STATUS_WAITING_SETTLEMENT_APPROVE, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_WAITING_SETTLEMENT_APPROVE));
		map.put(LookupConstants.__RECORD_STATUS_PAID, StatusExtension.decodeDataStatus(LookupConstants.__RECORD_STATUS_PAID));
		renderJSON(map);
	}

	// Mengarahkan ke html grid2, dimana di halaman itu akan di generate
	// datatable berdasarkan method pagingPickData
	public static void pickGrid(PickParameters param) {
		// variables :
		// "{username}" = variable placement for variable
		// "{userkey}" = variable placement for userkey

		String username = session.get(UIConstants.SESSION_USERNAME);
		String userkey = session.get(UIConstants.SESSION_USER_KEY);

		String filter = param.getFilter();
		filter = filter.replace("{username}", username);
		filter = filter.replace("{userkey}", userkey);
		param.setFilter(filter);

		render("Pick/grid2.html", param);
	}

	// Mengambil property yang di perlukan untuk meng-constract table, cukup
	// dengan operan lookupId
	public static void pickProp(String lookupId) throws Exception {
		PickData pickData = generalService.getPagingPickData(lookupId);
		renderJSON(pickData);
	}

	// generate paging,
	public static void paging(Paging page, PickParameters param, ReportParam[] reportParams) throws Exception {
		page.getParamFixMap().put("PICK_PARAMETERS", param);

		if (param.isQuery()) {
			page.setDateFormat(appProp.getDateFormat());
			if (reportParams == null) {
				page = generalService.pagingLookup(page);
			} else {
				page = generalService.pagingLookup(page, reportParams);
			}
		}

		renderJSON(page);
	}

	// akan mengambil data berdasarkan yang di pilih oke user saat popoup
	// PENTING bila param.data kosong, ini karena dia isi langsung tidak pake
	// popup
	// Bila ada param.data dengan delimeter | maka dia pilih dari popup
	public static void pick(PickParameters param) {
		
		if (PickCodeConstants.PICK_GN_CURRENCY.equals(param.getLookupId())) {
			GnCurrency currency = generalService.getCurrencyPick(param.getCode());
			renderJSON(currency, new CurrencyPickSerializer());
		}
		
		if (PickCodeConstants.PICK_SC_TYPE_MASTER_BY_SEC_CLASS.equals(param.getLookupId())) {
			ScTypeMaster secType = securityService.getSecurityType(param.getCode());
			if (secType != null) {
				if (!param.getFilter().equals(secType.getSecurityClass().getLookupId())) {
					secType = null;
				}
			}
			renderJSON(secType, new SecurityTypePickSerializer());
		}

		if (PickCodeConstants.PICK_SC_MASTER_BY_SECURITY_CLASS.equals(param.getLookupId())) {
			ScMaster scMaster = securityService.getSecurity(param.getCode());
			if (scMaster != null) {
				if (!param.getFilter().equals(scMaster.getSecurityType().getSecurityClass().getLookupId())) {
					scMaster = null;
				}
			}
			renderJSON(scMaster, new SecurityPickSerializer());
		}

		if (PickCodeConstants.PICK_SWIFT_RECEIVER.equals(param.getLookupId())) {
//			String depositoryId = "INI HARUS ADA OPEAN DEPOSITORY";
//			GnCustodianAccount bean = swiftService.getCustodianAccountByTerminalCode(param.getCode(), depositoryId);
//			renderJSON(bean, new CustodianAccountPickSerializer());
			
			SelectItem item = swiftService.getReceiver(param.getCode());
			
			Map<String, String> map = new HashMap<String, String>();
			if (item != null) {
				map.put("key", String.valueOf(item.value));
				map.put("code", String.valueOf(item.value));
				map.put("description", item.text);
			}else{
				map = null;
			}
			
			renderJSON(map);
		}
		
		if (PickCodeConstants.PICK_SWIFT_SENDER.equals(param.getLookupId())) {
//			GnThirdPartyAccount gntpa = swiftService.getThirdPartyAccountByInvestorCode(param.getCode());
//
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("key", String.valueOf(gntpa.getThirdPartyAccountKey()));
//			map.put("code", gntpa.getInvestorCode());
//			map.put("description", gntpa.getThirdParty().getThirdPartyName());
//
//			renderJSON(map);
			
			SelectItem item = swiftService.getSender(param.getCode());
			
			Map<String, String> map = new HashMap<String, String>();
			if (item != null) {
				map.put("key", String.valueOf(item.value));
				map.put("code", String.valueOf(item.value));
				map.put("description", item.text);
			}else{
				map = null;
			}
			
			renderJSON(map);
		}
		
		if (PickCodeConstants.PICK_SWIFT_PLACE_CODE.equals(param.getLookupId())) {
			GnLookup lookup = generalService.getLookUpByTypeAndCode("SWIFT_PLACE_CODE", param.getCode());
			if (lookup != null)
				lookup.setDetail(null);
			renderJSON(lookup, new LookupPickSerializer());
		}
		
		if (PickCodeConstants.PICK_SWIFT_REASON_STATUS.equals(param.getLookupId())) {
			GnLookup lookup = generalService.getLookUpByTypeAndCode("SWIFT_REASON_STATUS", param.getCode());
			if (lookup != null)
				lookup.setDetail(null);
			renderJSON(lookup, new LookupPickSerializer());
		}
		
		
		
		if (PickCodeConstants.PICK_ISIN_CODE.equals(param.getLookupId())) {
			log.debug("param.getCode() "+param.getCode());
			ScMaster security = securityService.getSecurityByIsinCodeSingle(param.getCode());
			ScMaster tsecurity = new ScMaster();
			tsecurity.setSecurityId(security.getSecurityId());
			tsecurity.setSecurityKey(security.getSecurityKey());
			tsecurity.setDescription(security.getDescription());
			renderJSON(tsecurity, new SecurityPickForChargeItemSerializer());
		}
		if (PickCodeConstants.PICK_BN_ACCOUNT_BY_FA_MASTER.equals(param.getLookupId())) {
			String filter = param.getFilter();
			if (filter != null) {
				String[] arrays = filter.split("\\|");
				if (arrays.length >= 1) {
					String[] codeSplit = param.getCode().split("\\");
					String accountNo = codeSplit[0];
					String bankCode = (codeSplit.length == 2) ? codeSplit[1] : null;
					BnAccount bnAccount = null;
					if (!filter.isEmpty()) {
						bnAccount = bankAccountService.bankAccountByFaMaster(UIConstants.SIMIAN_BANK_ID, accountNo, bankCode, arrays[1], Long.parseLong(arrays[0]));
					}
					BnAccount bankAccount = null;
					if (bnAccount != null) {
						bankAccount = bankAccountService.getBankAccount(bnAccount.getBankAccountKey());
					}

					renderJSON(bankAccount, new BankAccountPickSerializer());

				}
			}

		}

		if (PickCodeConstants.PICK_BN_ACCOUNT.equals(param.getLookupId())) {
			// String[] filter = param.getCode().split("\\");
			BnAccount bnAccount = null;
			renderJSON(bnAccount, new BankAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_BN_ACCOUNT_BY_RG_PRODUCT_BN_ACCOUNT.equals(param.getLookupId())) {
			BnAccount bnAccount = bankAccountService.getBankAccountByProduct(param.getFilter(), param.getCode());
			renderJSON(bnAccount, new BankAccountPickSerializer());
		}
		
		if (PickCodeConstants.PICK_BN_ACCOUNT_BY_ACC_CA_OR_NULL.equals(param.getLookupId())){
			log.debug("param.getFilter() => " + param.getFilter());
			String[] filter = param.getFilter().split("\\|");
			Long key = Long.parseLong(filter[0]);
			BnAccount bnAccount = bankAccountService.getBankAccountByAccKeyForCaAdj(key, filter[1]);
			renderJSON(bnAccount, new BankAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_BN_ACCOUNT_BY_RG_PRODUCT_BN_ACCOUNT_DOMAIN.equals(param.getLookupId())) {
			String[] filter = param.getFilter().split("\\|");
			BnAccount bnAccount = bankAccountService.getBankAccountByProduct(filter[0], filter[1], param.getCode());
			renderJSON(bnAccount, new BankAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_BN_ACCOUNT_BY_INVEST_ACC_ALL.equals(param.getLookupId())) {
			String[] filter = param.getFilter().split("\\|");
			BnAccount bnAccount = bankAccountService.getBankAccountByInvestmentProductCurrAccNo(filter[1], param.getCode(), Long.parseLong(filter[0]));
			renderJSON(bnAccount, new BankAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_CS_ACCOUNT.equals(param.getLookupId()) || PickCodeConstants.PICK_CS_ACCOUNT_FUND_TRANSFER.equals(param.getLookupId())) {
			CsAccount account = accountService.getAccountByNo(UIConstants.SIMIAN_BANK_ID, param.getCode());
			renderJSON(account, new AccountPickSerializer());
		}

		if (PickCodeConstants.PICK_CF_MASTER_NOT_IN_FA_MASTER.equals(param.getLookupId())) {
			CfMaster customer = customerService.getCustomerByNoForPick(UIConstants.SIMIAN_BANK_ID, param.getCode());
			renderJSON(customer, new CustomerPickSerializer());
		}

		if (PickCodeConstants.PICK_CF_MASTERZ_NON_RETAIL.equals(param.getLookupId())) {
			CfMaster customer = customerService.getCustomerNonRetailByNoForPick(UIConstants.SIMIAN_BANK_ID, param.getCode());
			renderJSON(customer, new CustomerPickSerializer());
		}

		if (PickCodeConstants.PICK_CF_MASTER_NON_RETAIL_BILLING.equals(param.getLookupId())) {
			CfMaster customer = customerService.getCustomerNonRetailByNoForPick(UIConstants.SIMIAN_BANK_ID, param.getCode());
			renderJSON(customer, new CustomerPickSerializer());
		}

		if (PickCodeConstants.PICK_GN_THIRD_PARTY.equals(param.getLookupId())) {
			GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, param.getFilter(), param.getCode());
			renderJSON(thirdParty, new ThirdPartyPickSerializer());
		}

		if (PickCodeConstants.PICK_GN_LOOKUP.equals(param.getLookupId())) {
			GnLookup lookup = generalService.getLookUpByTypeAndCode(param.getFilter(), param.getCode());
			if (lookup != null)
				lookup.setDetail(null);
			renderJSON(lookup, new LookupPickSerializer());
		}

		// SELECT_USER Hanya untuk testing
		if ("SELECT_USER".equals(param.getLookupId())) {
			GnUser user = applicationService.getUser(param.getCode());
			renderJSON(user, new UserPickSerializer());
		}

		if (PickCodeConstants.PICK_UD_PROFILE.equals(param.getLookupId())) {
			UpdProfile udProfile = generalService.getUpdProfileByName(param.getCode(), param.getFilter());
			renderJSON(udProfile, new UpdProfilePickSerializer());
		}

		if (PickCodeConstants.PICK_UD_PROFILE_ROLE.equals(param.getLookupId())) {
			String username = session.get(UIConstants.SESSION_USERNAME);
			String userkey = session.get(UIConstants.SESSION_USER_KEY);
			String filter = param.getFilter();
			filter = filter.replace("{username}", username);
			filter = filter.replace("{userkey}", userkey);

			String[] arrays = filter.split("\\|");

			UpdProfile udProfile = generalService.getUdProfileByRole(arrays, param.getCode());
			if (udProfile != null)
				udProfile.setDetails(generalService.getUpdProfileDetail(udProfile.getProfileKey()));
			renderJSON(udProfile, new UpdProfileDetailPickSerializer(), new UpdProfilePickSerializer(), new UpdFilterSerializer());
		}

		if (PickCodeConstants.PICK_CF_MASTER.equals(param.getLookupId())) {
			CfMaster customer = customerService.getCustomerByNoForPick(UIConstants.SIMIAN_BANK_ID, param.getCode());
			renderJSON(customer, new CustomerPickSerializer());
		}
		if (PickCodeConstants.PICK_CA_TYPE.equals(param.getLookupId())) {
			ScActionTemplate templete = securityService.getActionTemplateByCode(param.getCode());
			renderJSON(templete, new ActionTemplatePickSerializer());
		}

		if (PickCodeConstants.PICK_RG_PRODUCT.equals(param.getLookupId())) {
			if (param.getCode() != null && param.getCode().contains(SPECIAL_CHAR)) {
				param.setCode(param.getCode().replaceAll(SPECIAL_CHAR, "#"));
			}
			RgProduct rgProduct = taService.loadProduct(param.getCode());
			renderJSON(rgProduct, new ProductPickSerializer());
		}

		if (PickCodeConstants.PICK_RG_PRODUCT_CLASS.equals(param.getLookupId())) {
			if (param.getCode() != null && param.getCode().contains(SPECIAL_CHAR)) {
				param.setCode(param.getCode().replaceAll(SPECIAL_CHAR, "#"));
			}
			RgProduct rgProduct = taService.loadProductByClass(param.getCode(), param.getFilter());
			renderJSON(rgProduct, new ProductPickSerializer());
		}

		if (PickCodeConstants.PICK_RG_INVEST_CIF.equals(param.getLookupId()) || PickCodeConstants.PICK_RG_INVEST_CIF_BY_PROD.equals(param.getLookupId()) || PickCodeConstants.PICK_RG_INVEST_CIF_BY_SACODE.equals(param.getLookupId()) || PickCodeConstants.PICK_RG_INVEST_CIF_BY_PROD_AND_SACODE.equals(param.getLookupId())) {
			// CfMaster cfMaster =
			// customerService.getCustomerForUpload(Long.parseLong(param.getCode()));
			CfMaster cfMaster = customerService.getCustomerByCustomerNo(param.getCode());
			renderJSON(cfMaster, new CustomerPickSerializer());
		}

		// EDITAN
		if (PickCodeConstants.PICK_RG_INVEST.equals(param.getLookupId())) {
			RgInvestmentaccount rgInvestmentaccount = taService.loadInvestment(null, param.getCode());
			renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_RG_INVEST_CLASS.equals(param.getLookupId())) {
			RgInvestmentaccount rgInvestmentaccount = taService.loadInvestmentByClass(param.getCode(), param.getFilter());
			renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_CF_MASTER_BY_INVESTOR.equals(param.getLookupId())) {
			CfMaster customer = customerService.getCustomerByInvestor(param.getCode());
			renderJSON(customer, new CustomerPickSerializer());
		}

		if (PickCodeConstants.PICK_RG_INVEST_BY_PROD.equals(param.getLookupId())) {
			RgInvestmentaccount rgInvestmentaccount = taService.loadInvestment(param.getFilter(), param.getCode());
			renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_RG_INVEST_BY_SACODE.equals(param.getLookupId())) {
			Long saCode = (!param.getFilter().isEmpty()) ? Long.parseLong(param.getFilter()) : null;
			RgInvestmentaccount rgInvestmentaccount = taService.loadInvestmentByRgProductAndSaCode("", saCode, param.getCode());
			renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_RG_INVEST_BY_PROD_AND_SACODE.equals(param.getLookupId())) {
			String[] filters = param.getFilter().split("\\|");
			String productCode = filters[0];
			Long saCode = (!filters[1].isEmpty()) ? Long.parseLong(filters[1]) : null;
			RgInvestmentaccount rgInvestmentaccount = taService.loadInvestmentByRgProductAndSaCode(productCode, saCode, param.getCode());
			renderJSON(rgInvestmentaccount, new InvestmentAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_DWN_CUSTOMER.equals(param.getLookupId()) || PickCodeConstants.PICK_DWN_CUSTOMER_ALL.equals(param.getLookupId())) {
			CfMaster customer = null;
			if (param.getCode().trim().equals("ALL")) {
				customer = new CfMaster();
				customer.setCustomerNo(" ALL");
				customer.setCustomerName(" ALL");
			} else {
				customer = customerService.getCustomerByNoForPick(UIConstants.SIMIAN_BANK_ID, param.getCode());
			}
			renderJSON(customer, new CustomerPickSerializer());
		}
		
		if (PickCodeConstants.PICK_DWN_REPORT_TYPE_ALL.equals(param.getLookupId())){
			if(param.getCode().trim().equals("ALL")){
				GnLookup lookup = new GnLookup();
				lookup.setLookupCode(" ALL");
				lookup.setLookupDescription(" ALL");
				renderJSON(lookup, new LookupPickSerializer());
			}else{
				GnLookup lookup = generalService.getLookup(UIConstants.SIMIAN_BANK_ID, LookupConstants._CTP_REPORT_TYPE,param.getCode());
				renderJSON(lookup, new LookupPickSerializer());
			}
		}

		if (PickCodeConstants.PICK_DWN_SEC_TYPE_NON_DEPOSIT.equals(param.getLookupId()) || PickCodeConstants.PICK_DWN_SEC_NON_DEPOSIT.equals(param.getLookupId())) {
			DynamicValueVO vo = new DynamicValueVO();
			vo.code = param.getCode();
			vo.description = param.getCode();

			renderJSON(vo);
		}

		if (PickCodeConstants.PICK_DWN_PRODUCT_DNRK.equals(param.getLookupId())) {
			RgProduct product = taService.getProductDnrkForPick(param.getCode());
			renderJSON(product, new ProductDrnkPickSerializer());
		}

		if (PickCodeConstants.PICK_DWN_PRODUCT_FM.equals(param.getLookupId())) {
			RgProduct product = null;
			if (param.getCode().trim().equals("ALL")) {
				product = new RgProduct();
				product.setProductCode(" ALL");
				product.setName(" ALL");
			} else {
				product = taService.getProductFundManagerForPick(new Long(param.getFilter()), param.getCode());
			}
			renderJSON(product, new ProductDrnkPickSerializer());
		}

		if (PickCodeConstants.PICK_DWN_PRODUCT_FM_ALL.equals(param.getLookupId())) {
			RgProduct product = null;
			if (param.getCode().trim().equals("ALL")) {
				product = new RgProduct();
				product.setProductCode(" ALL");
				product.setName(" ALL");
			} else {
				product = taService.getProductFundManagerForPick(null, param.getCode());
			}
			renderJSON(product, new ProductDrnkPickSerializer());
		}

		if (PickCodeConstants.PICK_DWN_SELLING_AGENT.equals(param.getLookupId())) {
			GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.THIRD_PARTY_SELLINGAGENT, param.getCode());
			renderJSON(thirdParty, new ThirdPartyPickSerializer());
		}

		if (PickCodeConstants.PICK_DWN_FUND_MGR_ALL.equals(param.getLookupId())) {
			if (param.getCode().trim().equals("ALL")) {
				GnThirdParty thirdParty = new GnThirdParty();
				thirdParty.setThirdPartyCode(" ALL");
				thirdParty.setThirdPartyName(" ALL");
				thirdParty.setThirdPartyType(new GnLookup());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			} else {
				GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.THIRD_PARTY_FUND_MANAGER, param.getCode());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			}
		}
		
		if (PickCodeConstants.PICK_DWN_RETAIL_ALL.equals(param.getLookupId()) || PickCodeConstants.PICK_DWN_RETAIL_AND_NON_ALL.equals(param.getLookupId())) {
			if (param.getCode().trim().equals("ALL")) {
				GnThirdParty thirdParty = new GnThirdParty();
				thirdParty.setThirdPartyCode(" ALL");
				thirdParty.setThirdPartyName(" ALL");
				thirdParty.setThirdPartyType(new GnLookup());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			} else if (param.getCode().trim().equals("NON RITEL")) {
				GnThirdParty thirdParty = new GnThirdParty();
				thirdParty.setThirdPartyCode(" NON RITEL");
				thirdParty.setThirdPartyName(" NON RITEL");
				thirdParty.setThirdPartyType(new GnLookup());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			} else {
				GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.THIRD_PARTY_RETAIL, param.getCode());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			}
		}

		if (PickCodeConstants.PICK_DWN_SELLING_AGENT_ALL.equals(param.getLookupId())) {
			if (param.getCode().trim().equals("ALL")) {
				GnThirdParty thirdParty = new GnThirdParty();
				thirdParty.setThirdPartyCode(" ALL");
				thirdParty.setThirdPartyName(" ALL");
				thirdParty.setThirdPartyType(new GnLookup());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			} else {
				GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.THIRD_PARTY_SELLINGAGENT, param.getCode());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			}
		}

		if (PickCodeConstants.PICK_SC_TYPE_MASTER_DEPOSITO.equals(param.getLookupId())) {
			ScTypeMaster secType = securityService.getSecurityTypeDeposito(param.getCode(), param.getFilter());
			if (secType != null) {
				ScTypeMaster securityType = securityService.getSecurityTypeByStatusActive(secType.getSecurityType());
				TdTransactionTemplate depTemplate = depositoService.getTdTransactionTemplate(param.getFilter(), securityType.getSecurityType());
				securityType.setDepositoTrxTemplate(depTemplate);
				renderJSON(securityType, new SecurityTypePickSerializer());
			} else {
				renderJSON(secType, new SecurityTypePickSerializer());
			}
		}
		
		if (PickCodeConstants.PICK_SC_TYPE_MASTER.equals(param.getLookupId())) {
			ScTypeMaster secType = securityService.getSecurityType(param.getCode());
			renderJSON(secType, new SecurityTypePickSerializer());
		}

		if (PickCodeConstants.PICK_SC_MASTER.equals(param.getLookupId())) {
			// ini tanpa filter
			ScMaster security = securityService.getSecurity(param.getCode(), param.getFilter());
			renderJSON(security, new SecurityPickSerializer());
		}

		if (PickCodeConstants.PICK_SC_MASTER_TD.equals(param.getLookupId())) {
			ScMaster security = securityService.getSecurity(param.getCode(), param.getFilter());
			renderJSON(security, new SecurityPickSerializer());
		}

		if (PickCodeConstants.PICK_SC_MASTER_BY_SEC_TYPE.equals(param.getLookupId())) {
			ScMaster security = securityService.getSecurity(param.getCode(), param.getFilter());
			renderJSON(security, new SecurityPickSerializer());
		}

		if (PickCodeConstants.PICK_UD_BATCH.equals(param.getLookupId())) {
			//Long start = System.currentTimeMillis();
			UpdBatch updBatch = generalService.getUpdBatch(Long.valueOf(param.getCode()));
			updBatch.setDetails(null);
			//Long end = System.currentTimeMillis();
			renderJSON(updBatch, new UpdBatchSerializer());
		}

		if (PickCodeConstants.PICK_DWN_INSTRUCTION_TYPE.equals(param.getLookupId())) {
			CsTransactionMaster templateMaster = null;
			if (param.getCode().trim().equals("ALL")) {
				templateMaster = new CsTransactionMaster();
				templateMaster.setCustodyTransactionCode(" ALL");
				templateMaster.setTransactionDescription(" ALL");
			} else {
				templateMaster = accountService.getTransactionMaster(UIConstants.SIMIAN_BANK_ID, param.getCode());
			}
			renderJSON(templateMaster, new TransactionMasterPickSerializer());
		}

		if (param.getLookupId().startsWith("PICKRPT")) {
			DynamicValueVO vo = new DynamicValueVO();
			if (param.getCode().equals("ALL"))
				param.setCode(" All");

			PickData pickData = generalService.getPickLookup4Report(param.getLookupId() + "-get", param.getCode(), param.getReportParams());
			if (pickData.getRows() != null) {
				if (pickData.getRows().size() == 0) {
					// Ada picker yang tidak ada Allnya di grid, tapi success
					// kalo isi All
					// if(param.getCode().equals(" All")){
					// vo.code = param.getCode();
					// vo.description = param.getCode();
					// }else{
					// Response.current().status=204;
					// }
					Response.current().status = 204;
				} else {
					Object[] data_ = pickData.getRows().get(0).getData();
					if (data_.length > 0) {
						vo.code = data_[0];
						vo.description = data_[0];
					}
					if (data_.length > 1) {
						vo.description = data_[1];
						if (data_.length > 2) {
							vo.id = data_[0];
							vo.code = data_[1];
							vo.description = data_[2];
						}
					}
				}

				renderJSON(vo);

			} else {
				renderJSON(vo);
			}
		}

		if (PickCodeConstants.PICK_FA_JOURNAL_MASTER.equals(param.getLookupId())) {
			List<FaJournalTemplate> faJournalTemplate = fundService.getFaJournalTemplate(param.getCode());
			renderJSON(faJournalTemplate, new FaJournalTemplatePickSerializer());
		}

		/* ELVINO ADD START */

		if (PickCodeConstants.PICK_FA_MASTER.equals(param.getLookupId())) {
			FaMaster faMaster = fundService.getFaFundSetupForPick(param.getCode());
			renderJSON(faMaster, new FaFundSetupPickSerializer());
		}

		if (PickCodeConstants.PICK_FA_TRX_MASTER.equals(param.getLookupId())) {
			FaTransactionMaster faTransactionMaster = fundService.getFaTransactionMasterForPick(param.getCode());
			renderJSON(faTransactionMaster, new FaTransactionMasterPickSerializer());
		}
			
		if (Tools.inArray(param.getLookupId(), new String[]{PickCodeConstants.PICK_FA_COA_BOTTOM_LEVEL,
															PickCodeConstants.PICK_FA_COA_BOTTOM_LEVEL_AND_PARENT})) {				
			FaCoaMaster faCoaMaster = fundService.getFaCoaMasterForPick(param.getCode());
			renderJSON(faCoaMaster, new CoaMasterPickSerializer());
		}

		if (PickCodeConstants.PICK_FA_COA.equals(param.getLookupId())) {
			FaCoaMaster faCoaMaster = fundService.getFaCoaMasterForPick(param.getCode());
			renderJSON(faCoaMaster, new CoaMasterPickSerializer());
		}

		if (PickCodeConstants.PICK_CS_ACCOUNT_NOT_IN_FA.equals(param.getLookupId())) {
			CsAccount csAccount = accountService.getAccountByNo2(UIConstants.SIMIAN_BANK_ID, param.getCode());
			renderJSON(csAccount, new CsAccountPickSerializer());
		}

		if (PickCodeConstants.PICK_GN_THIRD_PARTY.equals(param.getLookupId())) {
			GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, param.getFilter(), param.getCode());
			renderJSON(thirdParty, new ThirdPartyPickSerializer());
		}

		if (PickCodeConstants.PICK_DEPOSITO.equals(param.getLookupId())) {
			String[] array = param.getFilter().split("\\|");
			Long accountKey = "".equals(array[0].trim()) ? 0 : Long.valueOf(array[0].trim());
			Long securityKey = "".equals(array[1].trim()) ? 0 : Long.valueOf(array[1].trim());
			TdMaster tdMaster = depositoService.getDepositoByPick(param.getCode(), accountKey, securityKey);
			renderJSON(tdMaster, new TdMasterPickSerializer());
		}

		if (PickCodeConstants.PICK_TD_MASTER.equals(param.getLookupId())) {
			String[] array = param.getFilter().split("\\|");
			Long accountKey = "".equals(array[0].trim()) ? 0 : Long.valueOf(array[0].trim());
			Long securityKey = "".equals(array[1].trim()) ? 0 : Long.valueOf(array[1].trim());
			TdMaster tdMaster = depositoService.getPickTdMaster(param.getCode(), accountKey, securityKey);
			if (tdMaster != null) {
				TdTransaction tdTransaction = generalService.getAmountTransaction(tdMaster.getDepositoKey());
				if (tdTransaction != null) {
					if (tdTransaction.getAmount() != null) {
						tdMaster.setNominal(tdTransaction.getAmount());
					}
				}
			}
			renderJSON(tdMaster, new TdMasterPickSerializer());
		}

		if (PickCodeConstants.PICK_CS_ACCOUNT_BY_CUST.equals(param.getLookupId())) {
			Long customerKey = Long.valueOf(param.getFilter());
			CsAccount csAccount = accountService.getAccountByCustomer(UIConstants.SIMIAN_BANK_ID, customerKey, param.getCode());
			renderJSON(csAccount, new CsAccountPickSerializer());
		}

		/* ELVINO ADD END */

		/*
		 * Start Download Trial balance v3ya 'PICK_DWN_COA', 'PICK_DWN_FUND',
		 * 'PICK_DWN_FUND_MGR'
		 */
		if (PickCodeConstants.PICK_DWN_COA.equals(param.getLookupId())) {
			FaCoaMaster faCoaMaster;
			if (param.getCode().trim().equals("ALL")) {
				faCoaMaster = new FaCoaMaster();
				faCoaMaster.setCoaCode(" ALL");
				faCoaMaster.setDescription(" ALL");
			} else {
				faCoaMaster = fundService.getFaCoaMasterForPick(param.getCode());
			}
			renderJSON(faCoaMaster, new CoaMasterDownTrialBalacePickSerializer());
		}
		
		if (PickCodeConstants.PICK_DWN_FUND.equals(param.getLookupId())) {
			FaMaster faMaster;
			if (param.getCode().trim().equals("ALL")) {
				faMaster = new FaMaster();
				faMaster.setFundCode(" ALL");
				faMaster.setFundDescription(" ALL");
			} else {
				faMaster = fundService.getFaFundSetupForPick(param.getCode());
			}
			renderJSON(faMaster, new FaFundSetupPickSerializer());
		}

		if (PickCodeConstants.PICK_DWN_FUND_REFERENCE.equals(param.getLookupId())) {
			FaMaster faMaster;
			if (param.getCode().trim().equals("ALL")) {
				faMaster = new FaMaster();
				faMaster.setFundCode(" ALL");
				faMaster.setFundDescription(" ALL");
			} else {
				faMaster = fundService.getFaFundSetupForTrialBalance(param.getCode(), param.getFilter());
			}
			renderJSON(faMaster, new FaFundSetupPickSerializer());
		}
		if (PickCodeConstants.PICK_DWN_CS_ACCOUNT.equals(param.getLookupId())) {
			CsAccount csAccount = new CsAccount();
			if (param.getCode().trim().equals("ALL")) {
				csAccount.setAccountNo(" ALL");
				csAccount.setName(" ALL");
			} else {
				csAccount = accountService.getAccountByNo(param.getCode());
			}
			renderJSON(csAccount, new CsAccountPickSerializer());
		}

		/* end Download Trial balance v3ya */

		if (PickCodeConstants.PICK_SC_CORPORATE_ANNOUNCEMENT.equals(param.getLookupId())) {
			ScCorporateAnnouncement announcement = generalService.getAnnouncementPick(param.getCode());
			renderJSON(announcement, new ScCorporateAnnouncementSerializer());
		}

		if (param.getLookupId().equals(PickCodeConstants.PICK_DWN_CUSTOMER_HOLDING)) {
			CfMaster customer = null;
			if (param.getCode().trim().equals("ALL")) {
				customer = new CfMaster();
				customer.setCustomerNo(" ALL");
				customer.setCustomerName(" ALL");
			} else {
				customer = customerService.getCustomerByCustomerNo(param.getCode());
			}
			renderJSON(customer, new CustomerPickSerializer());
		}
		if (param.getLookupId().equals(PickCodeConstants.PICK_DWN_RETAIL_HOLDING)) {
			if (param.getCode().trim().equalsIgnoreCase("ALL")) {
				GnThirdParty thirdParty = new GnThirdParty();
				thirdParty.setThirdPartyCode(" ALL");
				thirdParty.setThirdPartyName(" ALL");
				thirdParty.setThirdPartyType(new GnLookup());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			} else {
				GnThirdParty thirdParty = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.THIRD_PARTY_RETAIL, param.getCode());
				renderJSON(thirdParty, new ThirdPartyPickSerializer());
			}
		}
		
		//TODO: Add by Fadly #7507
		if (PickCodeConstants.PICK_DWN_SECURITY_CLASS_ALL.equals(param.getLookupId())){
			if(param.getCode().trim().equals("ALL")){
				GnLookup lookup = new GnLookup();
				lookup.setLookupCode(" ALL");
				lookup.setLookupDescription(" ALL");
				renderJSON(lookup, new LookupPickSerializer());
			}else{
				GnLookup lookup = generalService.getLookup(UIConstants.SIMIAN_BANK_ID, LookupConstants._SECURITY_CLASS, param.getCode());
				renderJSON(lookup, new LookupPickSerializer());
			}
		}

		if (PickCodeConstants.PICK_DWN_SEC_TYPE.equals(param.getLookupId())) {
			GnLookup lookup = new GnLookup();
			if(param.getCode().trim().equals("ALL")){
				lookup.setLookupCode(" ALL");
				lookup.setLookupDescription(" ALL");
				renderJSON(lookup, new LookupPickSerializer());
			}else{
				ScTypeMaster secType = 	securityService.getSecurityType(param.getCode());
				lookup.setLookupCode(param.getCode());
				lookup.setLookupDescription(secType.getDescription());
				renderJSON(lookup, new LookupPickSerializer());
			}
		}

		if (PickCodeConstants.PICK_DWN_SEC_ID.equals(param.getLookupId())) {
			GnLookup lookup = new GnLookup();
			if(param.getCode().trim().equals("ALL")){
				lookup.setLookupCode(" ALL");
				lookup.setLookupDescription(" ALL");
				renderJSON(lookup, new LookupPickSerializer());
			}else{
				ScMaster sec = 	securityService.getSecurity(param.getCode());
				lookup.setLookupCode(param.getCode());
				lookup.setLookupDescription(sec.getDescription());
				renderJSON(lookup, new LookupPickSerializer());
			}
		}
		//End #7507
		
		//TODO: Add by Fadly #7527
		if (PickCodeConstants.PICK_DWN_CLOSEREASON.equals(param.getLookupId())) {
			if(param.getCode().trim().equals("ALL")){
				GnLookup lookup = new GnLookup();
				lookup.setLookupCode(" ALL");
				lookup.setLookupDescription(" ALL");
				renderJSON(lookup, new LookupPickSerializer());
			}else{
				GnLookup lookup = generalService.getLookup(UIConstants.SIMIAN_BANK_ID, LookupConstants.AML_CLOSEREASON, param.getCode());
				renderJSON(lookup, new LookupPickSerializer());
			}
		}
		
		if (PickCodeConstants.PICK_DWN_AML.equals(param.getLookupId()) || PickCodeConstants.PICK_DWN_LOOKUP_GROUP.equals(param.getLookupId()) 
			|| PickCodeConstants.PICK_DWN_TRANSACTION_CATEGORY.equals(param.getLookupId()) || PickCodeConstants.PICK_DWN_USED_BY.equals(param.getLookupId())
			|| PickCodeConstants.PICK_DWN_AML_CUSTOMER.equals(param.getLookupId())) {
			DynamicValueVO vo = new DynamicValueVO();
			vo.code = param.getCode();
			vo.description = param.getCode();

			renderJSON(vo);
		}
		//end #7527

	}

	public static void pickSelect(String id) {
		PickData pickData = generalService.getPickLookup(id, null);
		renderJSON(pickData);
	}

	// GnThirdPartyBank thirdPartyBank
	public static void thirdPartyBanks(String filter) {
		String[] filters = getArray(filter);
		String thirdPartyKey = filters[0];
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_GN_THIRD_PARTY_BANK, new Object[] { thirdPartyKey }));
	}

	public static void thirdPartyBank(String code) {
		GnThirdPartyBank thirdPartyBank = generalService.getThirdPartyBankByAccountNo(code);
		renderJSON(thirdPartyBank, new ThirdPartyBankPickSerializer());
	}

	public static void thirdPartiesBanks() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_DWN_BANKS, null));
	}

	public static void thirdPartyBankByCode(String code) {
		GnThirdParty thirdPartyBank = generalService.getThirdPartyByTypeAndCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.THIRD_PARTY_BANK, code);
		renderJSON(thirdPartyBank, new ThirdPartyPickSerializer());
	}

	public static void customerByInvestors() {
		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CF_MASTER_BY_INVESTOR, null));
	}

	public static void customerByInvestor(String code) {
		CfMaster customer = customerService.getCustomerByInvestor(code);
		renderJSON(customer, new CustomerPickSerializer());
	}

	public static void getListTransaction(String filter) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String securityId = null;
		String settlementAgentCode = "0";
		Date transactionDate = null;
		Date settlementDate = null;
		String quantity = null;
		String price = null;
		String ctpNo = "0";
		String custodyAccountKey = null;
		String transactionType = null;

		try {
			if (filter.contains("|")) {
				String[] arrays = filter.split("\\|");
				securityId = arrays[0];
				if (!"".equals(arrays[1]))
					settlementAgentCode = arrays[1];
				transactionDate = sdf.parse(arrays[2]);
				settlementDate = sdf.parse(arrays[3]);
				quantity = arrays[4];
				price = arrays[5];
				if (!"".equals(arrays[6]))
					ctpNo = arrays[6];
				custodyAccountKey = arrays[7];
				transactionType = arrays[8];
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		renderList1(generalService.getPickLookup(PickCodeConstants.PICK_CS_TRN, new Object[] { securityId, settlementAgentCode, transactionDate, settlementDate, quantity, price, ctpNo, custodyAccountKey, transactionType }));
	}

	public static void getTransactionNew(String filter, String code) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String securityId = null;
		String settlementAgentCode = "0";
		Date transactionDate = null;
		Date settlementDate = null;
		String quantity = null;
		String price = null;
		String ctpNo = "0";
		String custodyAccountKey = null;
		String transactionType = null;

		try {
			if (filter.contains(",")) {
				String[] arrays0 = filter.split("\\,");
				if (arrays0[0].contains("|")) {
					String[] arrays = arrays0[0].split("\\|");
					securityId = arrays[0];
					if (!"".equals(arrays[1]))
						settlementAgentCode = arrays[1];
					transactionDate = sdf.parse(arrays[2]);
					settlementDate = sdf.parse(arrays[3]);
					quantity = arrays[4];
					price = arrays[5];
					if (!"".equals(arrays[6]))
						ctpNo = arrays[6];
					custodyAccountKey = arrays[7];
					transactionType = arrays[8];
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		CsTransaction ct = transactionService.getTransactionNew(securityId, settlementAgentCode, transactionDate, settlementDate, quantity, price, ctpNo, code, custodyAccountKey, transactionType);
		renderJSON(ct, new TransactionForPickSerializer());
	}
	
}