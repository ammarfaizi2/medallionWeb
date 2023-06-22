package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.CouponSchedulePickSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.CorpActionSettlementSearchParameters;
import vo.SettlementPrematchSearchParameters;
import vo.SettlementSearchParameters;
import vo.TransactionSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.AbstractTransaction;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CsChargeMaster;
import com.simian.medallion.model.CsDailyHolding;
import com.simian.medallion.model.CsPortfolio;
import com.simian.medallion.model.CsSubAccount;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.CsTransactionBatch;
import com.simian.medallion.model.CsTransactionCharge;
import com.simian.medallion.model.CsTransactionTemplate;
import com.simian.medallion.model.GnAdjustmentDetail;
import com.simian.medallion.model.GnCustodianBank;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnThirdPartyAccount;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.model.ScEntitlement;
import com.simian.medallion.model.ScEntitlementDetail;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.TxProfRuleIntDetail;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Transactions extends Registry {
	private static Logger log = Logger.getLogger(Transactions.class);

	final static String SECURITY_CLASS_FIX_INCOME = "SECURITY_CLASS-FI";
	final static String COUNTRY_INDONESIA = "COUNTRY-ID";
	final static String blockedAccountMessage = "Account is Blocked";

	final static String RVP = "RVP";
	final static String DVP = "DVP";
	final static String RFOP = "RFOP";
	final static String DFOP = "DFOP";
	
	static List<String> hitApprove = new ArrayList<String>();

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);
	} 

	@Before
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
		renderArgs.put("depositoryCodes", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITORY_CODE));
		List<SelectItem> paymentType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PAYMENT_TYPE);
		renderArgs.put("paymentType", paymentType);

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> prematchStatus = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PREMATCH_STATUS);
		renderArgs.put("prematchStatus", prematchStatus);

		List<SelectItem> matchStatus = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._MATCH_STATUS);
		renderArgs.put("matchStatus", matchStatus);

		List<SelectItem> settlementPurpose = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._SETTLEMENT_PURPOSE);
		renderArgs.put("settlementPurpose", settlementPurpose);

		List<SelectItem> prematchTypeUI = UIHelper.autoManualOperators();
		renderArgs.put("prematchTypeUI", prematchTypeUI);

		GnLookup classTrd = generalService.getLookup(LookupConstants.CLASSIFICATION_TRADING);
		renderArgs.put("classificationTrd", LookupConstants.CLASSIFICATION_TRADING);
		renderArgs.put("classTrd", classTrd);

		GnLookup classAfs = generalService.getLookup(LookupConstants.CLASSIFICATION_AFS);
		renderArgs.put("classificationAfs", LookupConstants.CLASSIFICATION_AFS);
		renderArgs.put("classAfs", classAfs);

		GnLookup classHtm = generalService.getLookup(LookupConstants.CLASSIFICATION_HTM);
		renderArgs.put("classificationHtm", LookupConstants.CLASSIFICATION_HTM);
		renderArgs.put("classHtm", classHtm);
		renderArgs.put("chargeFreqT", LookupConstants.CHARGE_FREQUENCY_TRANSACTION_CODE);
		renderArgs.put("transTypeS", LookupConstants.TRANSACTION_TYPE_SELL_CODE);
		renderArgs.put("transTypeB", LookupConstants.TRANSACTION_TYPE_BUY_CODE);
		renderArgs.put("secClassEQ", LookupConstants.SECURITY_CLASS_EQUITY_CODE);
		renderArgs.put("secClassFI", LookupConstants.SECURITY_CLASS_FIXED_INCOME_CODE);
		renderArgs.put("secClassMM", LookupConstants.SECURITY_CLASS_MONEY_MARKET_CODE);
		renderArgs.put("valuationAmortized", LookupConstants.VALUATION_METHOD_AMORTIZED);
		renderArgs.put("countryId", LookupConstants.COUNTRY_LOCAL);
		renderArgs.put("prematchP", LookupConstants.PREMACH_STATUS_P);
		renderArgs.put("prematchA", LookupConstants.PREMACH_STATUS_A);
		renderArgs.put("prematchO", LookupConstants.PREMACH_STATUS_O);
		renderArgs.put("prematchW", LookupConstants.PREMACH_STATUS_W);
		renderArgs.put("prematchWU", LookupConstants.PREMACH_STATUS_WU);
		renderArgs.put("prematchWM", LookupConstants.PREMACH_STATUS_WM);
		renderArgs.put("prematchWO", LookupConstants.PREMACH_STATUS_WO);
		renderArgs.put("matchU", LookupConstants.MATCH_STATUS_U);
		renderArgs.put("matchM", LookupConstants.MATCH_STATUS_M);
		renderArgs.put("bnTransTypeDebet", LookupConstants.BN_TRANSACTION_TYPE_DEBIT);
		renderArgs.put("bnTransTypeCredit", LookupConstants.BN_TRANSACTION_TYPE_CREDIT);
		renderArgs.put("depositoryCbest", LookupConstants.DEPOSITORY_CODE_KSEI_CBEST);
		renderArgs.put("depositoryBis", LookupConstants.DEPOSITORY_CODE_BI_BISSSS);
		renderArgs.put("depositoryNA", LookupConstants.DEPOSITORY_CODE_NA);
		renderArgs.put("depositoryEuro", LookupConstants.DEPOSITORY_CODE_EUROCLEARS);
		renderArgs.put("holdingTypeTotal", LookupConstants.HOLDING_TYPE_TOTAL);
		renderArgs.put("thirdPartyCounterPart", LookupConstants.THIRD_PARTY_COUNTER_PART);
		renderArgs.put("EXCH", LookupConstants.SETTLEMENT_PURPOSE_EXCH);
		renderArgs.put("NONEXCHG", LookupConstants.SETTLEMENT_PURPOSE_NONEXCHG);
		renderArgs.put("OTHR", LookupConstants.SETTLEMENT_REASON_OTHR);

		List<SelectItem> transferMethod = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("transferMethod", transferMethod);
		
		List<SelectItem> counterPartyType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.COUNTER_PARTY_TYPE);
		renderArgs.put("counterPartyType", counterPartyType);
		
		renderArgs.put("counterPartyTypeInternal", LookupConstants.COUNTER_PARTY_TYPE_INTERNAL);
		renderArgs.put("counterPartyTypeExternal", LookupConstants.COUNTER_PARTY_TYPE_EXTERNAL);

		renderArgs.put("transferMethodManual", LookupConstants.FUND_TRANSFER_METHOD_MANUAL);
		//renderArgs.put("transferMethodFile", LookupConstants.FUND_TRANSFER_METHOD_FILE);
		renderArgs.put("transferMethodInterface", LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE);
		renderArgs.put("cbestOtb", LookupConstants.CBEST_MESSAGE_TYPE_OTB);

		renderArgs.put("paramPrematch", UIConstants.PARAM_SETTLE_PREMATCH);

		GnSystemParameter cbestEnabled = generalService.getSystemParameter(SystemParamConstants.CBEST_ENABLED);
		renderArgs.put("cbestEnabled", (cbestEnabled != null) ? cbestEnabled.getValue() : null);
		
		List<SelectItem> transferType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.TRANSFER_TYPE);
		renderArgs.put("transferType", transferType);
		
		renderArgs.put("transferTypePool", LookupConstants.TRANSFER_TYPE_POOL);
		renderArgs.put("transferManual", LookupConstants.TRANSFER_TYPE_MANUAL);
		
		renderArgs.put("defaultbankcode", getValueDefaultBankCode());
		
		GnSystemParameter paramRetailExclude = generalService.getSystemParameter(SystemParamConstants.INTERFACE_RETAIL_GROUP_EXCLUDE);
		renderArgs.put("paramRetailExclude", (paramRetailExclude != null)? paramRetailExclude.getValue() : null);
		
		GnSystemParameter paramExcludeAcctno = generalService.getSystemParameter(SystemParamConstants.INTERFACE_ACCTNO_EXCLUDE);
		renderArgs.put("paramExcludeAcctno", (paramExcludeAcctno != null)? paramExcludeAcctno.getValue() : null);
		
		renderArgs.put("menuPrematch",UIConstants.PARAM_SETTLE_PREMATCH);

	}
	
	private static String getValueDefaultBankCode() {
		GnSystemParameter systemParameter = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_ORG);
		GnThirdParty bankThirdParty = generalService.getThirdParty(Long.parseLong(systemParameter.getValue()));
		return bankThirdParty.getThirdPartyCode();
	}

	public static void test(long key) {
		log.debug("test. key: " + key);

		CsTransaction transaction = accountService.getTransaction(key);
		render(transaction);
	}

	public static void testJSON(long key) {
		log.debug("testJSON. key: " + key);

		CsTransaction transaction = accountService.getTransaction(key);
		String data = serialize(transaction);
		renderJSON(data);
	}

	@Check("transaction.buySell")
	public static void list(String mode, String param) {
		log.debug("list. mode: " + mode + " param: " + param);

		render("Transactions/list.html", param, mode);
	}

	@Check("transaction.buySell")
	public static void search(TransactionSearchParameters params) {
		log.debug("search. params: " + params);

		List<CsTransaction> transactions = transactionService.searchTransaction(UIHelper.withOperator(params.transactionSearchNoOperator, params.TransactionNoOperator), params.customerCodeSearchId, params.securityTypeSearchId, params.securityCodeSearchId, params.dateFrom, params.dateTo);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_BUY_SELL));
		render("Transactions/grid_transaction.html", transactions);
	}

	@Check("transaction.buySell")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsTransaction transaction = new CsTransaction();
		transaction.setTransactionDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		/*
		 * GnTaxMaster capGainTax =
		 * generalService.getTaxMaster(Long.parseLong(generalService
		 * .getValueParam(SystemParamConstants.PARAM_DEFAULT_CAP_GAIN_TAX)));
		 * transaction.setCapitalGainTaxMaster(capGainTax);
		 */
		// transaction.setCapitalGainTax(BigDecimal.ZERO);
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (udfs != null) {
			for (GnUdfMaster udf : udfs) {
				// START UDF FOR DROPDOWN
				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
				// END DROPDOWN
			}
		}
		// BigDecimal tst = BigDecimal.valueOf(51192.98, 2, RoundEnvir);
		// transaction.setAccruedInterest();
		// logger.debug("HOLIDAYS >>>" +holidays.size());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_BUY_SELL));
		boolean isreload = false;
		render("Transactions/entry.html", transaction, udfs, mode, isreload);
	}

	/*
	 * public static void save(CsTransaction transaction,
	 * List<CsTransactionCharge> charges, List<GnUdfMaster> udfs, boolean
	 * settlement) { logger.debug("SETTLEMENT DATE = " +
	 * transaction.getSettlementDate()); logger.debug("EFFECTIVE DATE = " +
	 * transaction.getEffectiveDate()); logger.debug("MATURITY DATE = " +
	 * transaction.getMaturityDate()); logger.debug("LAST PAYMENT DATE = " +
	 * transaction.getLastPaymentDate()); logger.debug("NEXT PAYMENT DATE = " +
	 * transaction.getNextPaymentDate());
	 * 
	 * if (transaction != null && charges != null) {
	 * transaction.getTransactionCharges().clear();
	 * transaction.getTransactionCharges().addAll(charges); }
	 * 
	 * // TODO : CHANGE THIS try { logger.debug("udfs:" + udfs); if (udfs !=
	 * null) { Map<String, String> data = new HashMap<String, String>(); for
	 * (GnUdfMaster udf : udfs) { data.put(udf.getFieldName(), udf.getValue());
	 * } String udfString = json.writeValueAsString(data);
	 * transaction.setUdf(udfString); } } catch (IOException e) {
	 * logger.debug(e.getMessage(),e); } //validate
	 * validation.required("Transaction Date is",
	 * transaction.getTransactionDate()); validation.required("Account No is",
	 * transaction.getAccount().getAccountNo());
	 * validation.required("Transaction Code is",
	 * transaction.getTransactionTemplate().getTransactionCode());
	 * validation.required("Security Code is",
	 * transaction.getSecurity().getSecurityId());
	 * validation.required("Counter Party is",
	 * transaction.getCounterParty().getThirdPartyCode());
	 * validation.required("Classification is",
	 * transaction.getClassification().getLookupId());
	 * validation.required("Settlement Date is",
	 * transaction.getSettlementDate());
	 * validation.required("Settlement Account is",
	 * transaction.getSettlementAccount().getAccountNo());
	 * 
	 * if (transaction.getTransactionTemplate().getPortfolioTransaction().
	 * getTransactionType
	 * ().getLookupCode().equals(LookupConstants.TRANSACTION_TYPE_SELL_CODE)){
	 * validation.required("Portfolio is", transaction.getHoldingRefs()); }
	 * 
	 * validation.required("Quantity/Face Value is", transaction.getQuantity());
	 * validation.required("Price is", transaction.getPrice());
	 * 
	 * logger.debug("DISCOUNTED = "
	 * +transaction.getTransactionTemplate().getSecurityType
	 * ().getIsDiscounted()); logger.debug("IS_PRICE = "
	 * +transaction.getTransactionTemplate().getSecurityType().getIsPrice());
	 * 
	 * if
	 * (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted
	 * ()==true &&
	 * transaction.getTransactionTemplate().getSecurityType().getIsPrice
	 * ()==false){ validation.required("Nominal is", transaction.getAmount());
	 * validation.required("Tax Amount (left) is",
	 * transaction.getDiscountTax()); }
	 * 
	 * if
	 * (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted
	 * ()==true &&
	 * transaction.getTransactionTemplate().getSecurityType().getIsPrice
	 * ()==true){ validation.required("Tax Amount is",
	 * transaction.getDiscountTax()); }
	 * 
	 * if
	 * (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted
	 * ()==true){ validation.required("Tax Code (left) is",
	 * transaction.getDiscountTaxMaster().getTaxCode()); }
	 * 
	 * if
	 * (transaction.getTransactionTemplate().getSecurityType().getHasInterest(
	 * )==true){ validation.required("Interest Rate is",
	 * transaction.getInterestRate());
	 * validation.required("Accrued Interest is",
	 * transaction.getAccruedInterest());
	 * validation.required("Tax Code (right) is",
	 * transaction.getTaxOnInterestMaster().getTaxCode());
	 * validation.required("Tax Amount (right) is",
	 * transaction.getTaxOnInterest()); }
	 * 
	 * if
	 * ((transaction.getTransactionTemplate().getSecurityType().getSecurityClass
	 * (
	 * ).getLookupCode().equals(LookupConstants.SECURITY_CLASS_MONEY_MARKET_CODE
	 * ))&& (transaction.getTransactionTemplate().getPortfolioTransaction().
	 * getTransactionType
	 * ().getLookupCode().equals(LookupConstants.TRANSACTION_TYPE_BUY_CODE))){
	 * validation.required("Maturity Date is", transaction.getMaturityDate()); }
	 * if (charges!=null){ if
	 * (validation.errorsMap().values().containsAll(charges) == false){
	 * validation.clear(); } } if (validation.hasErrors()){ String mode =
	 * UIConstants.DISPLAY_MODE_ENTRY; flash.put(UIConstants.BREADCRUMB,
	 * applicationService
	 * .getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_BUY_SELL));
	 * render("Transactions/entry.html", transaction, charges, udfs, settlement,
	 * mode); } logger.debug("settlement = "+settlement); if (!settlement) {
	 * logger.debug("masuk sini"); if
	 * (transaction.getAccruedInterest().compareTo(BigDecimal.ZERO) < 0) {
	 * logger.debug(">>>>>>><<<<<<<<<<<<<<<)))))))))))))))");
	 * transaction.setAccruedInterest(BigDecimal.ZERO); } }
	 * logger.debug("ACCRUED INTEREST = " +transaction.getAccruedInterest());
	 * serializerService.serialize(session.getId(),
	 * transaction.getTransactionKey(), transaction);
	 * confirming(transaction.getTransactionKey(), settlement, "");
	 * //render("Transactions/detail.html", transaction, taskId, model);
	 * 
	 * }
	 */

	/*
	 * public static void confirming(Long transactionKey, boolean settlement,
	 * String param) { String mode = UIConstants.DISPLAY_MODE_ENTRY; boolean
	 * confirming = true; CsTransaction transaction =
	 * serializerService.deserialize(session.getId(), transactionKey,
	 * CsTransaction.class);
	 * logger.debug("[CONFIRMING] transaction.dailyPortfolioFlag = " +
	 * transaction.getDailyPortfolioFlag());
	 * logger.debug("[CONFIRMING] transaction.getMatchStatus = " + (
	 * transaction.getMatchStatus() != null ?
	 * transaction.getMatchStatus().getLookupId() : "null" ));
	 * 
	 * logger.debug(">>> [JUN] transaction=" + transaction);
	 * 
	 * List<GnUdfMaster> udfs =
	 * generalService.listUdfMastersByCategory(LookupConstants
	 * .UDF_MASTER_CATEGORY_TRANSACTION); //TODO : CHANGE THIS if
	 * (transaction.getUdf() != null) { try { Map<String, String> data =
	 * json.readValue(transaction.getUdf(), new TypeReference<HashMap<String,
	 * String>>() {}); for (GnUdfMaster udf : udfs) {
	 * udf.setValue(data.get(udf.getFieldName())); if ( udf.getLookupGroup() !=
	 * null ) {
	 * udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup
	 * ().getLookupGroup()));
	 * udf.setOptions(generalService.listLookupsForDropDownAsSelectItem
	 * (UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup())); } }
	 * } catch (JsonParseException e) { // TODO Auto-generated catch block
	 * logger.error(e.getMessage(), e); } catch (JsonMappingException e) { //
	 * TODO Auto-generated catch block logger.error(e.getMessage(), e); } catch
	 * (IOException e) { // TODO Auto-generated catch block
	 * logger.error(e.getMessage(), e); } } if (settlement) {
	 * renderArgs.put("cbestFlows",
	 * generalService.listLookupsForDropDownAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID, LookupConstants._CBEST_FLOW));
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT));
	 * render("Transactions/settleSingle.html", transaction, udfs, mode,
	 * confirming); } else { if
	 * (param.equals(UIConstants.PARAM_SETTLE_PREMATCH)) { param =
	 * UIConstants.PARAM_SETTLE_PREMATCH_VIEW; flash.put(UIConstants.BREADCRUMB,
	 * applicationService
	 * .getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH)); } else
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants
	 * .CS_TRANSACTION_BUY_SELL)); render("Transactions/entry.html",
	 * transaction, udfs, mode, confirming, param); }
	 * 
	 * }
	 */

	/*
	 * @Transactional(propagation=Propagation.REQUIRED) public static void
	 * confirm(CsTransaction transaction, List<CsTransactionCharge> charges,
	 * List<GnUdfMaster> udfs, String param) {
	 * logger.debug("[CONFIRM] transaction.dailyPortfolioFlag = " +
	 * transaction.getDailyPortfolioFlag()); if (transaction != null && charges
	 * != null) { transaction.getTransactionCharges().clear();
	 * transaction.getTransactionCharges().addAll(charges); } if (udfs != null)
	 * { try { Map<String, String> data = new HashMap<String, String>(); for
	 * (GnUdfMaster udf : udfs) { data.put(udf.getFieldName(), udf.getValue());
	 * }
	 * 
	 * String udfString = json.writeValueAsString(data);
	 * transaction.setUdf(udfString); } catch (IOException e) {
	 * logger.debug(e.getMessage(),e); } }
	 * 
	 * String mode = UIConstants.DISPLAY_MODE_ENTRY; boolean confirming = true;
	 * try { List<GnCalendar> holidays =
	 * generalService.listHolidays(UIConstants.SIMIAN_BANK_ID); for (GnCalendar
	 * holiday : holidays){ if (holiday.getId().getCalendarDate().getTime() ==
	 * (transaction.getSettlementDate().getTime())){ throw new
	 * MedallionException(ExceptionConstants.DATE_IS_HOLIDAY,
	 * "transaction.settlementDate"); }
	 * 
	 * if (holiday.getId().getCalendarDate().getTime() ==
	 * (transaction.getTransactionDate().getTime())){ throw new
	 * MedallionException(ExceptionConstants.DATE_IS_HOLIDAY,
	 * "transaction.transactionDate"); } }
	 * logger.debug("### TransactionCharges: " +
	 * transaction.getTransactionCharges().size());
	 * 
	 * String menuCode = null; if
	 * ((UIConstants.PARAM_SETTLE_PREMATCH.equals(param)) ||
	 * (UIConstants.PARAM_SETTLE_PREMATCH_VIEW.equals(param))) { menuCode =
	 * MenuConstants.CS_SETTLEMENT_PREMATCH; } else { menuCode =
	 * MenuConstants.CS_TRANSACTION_BUY_SELL; }
	 * 
	 * CsTransaction trx = transactionService.createTransaction(menuCode,
	 * transaction, session.get(UIConstants.SESSION_USERNAME),
	 * session.get(UIConstants.SESSION_USER_KEY));
	 * 
	 * if (param.equals(UIConstants.PARAM_SETTLE_PREMATCH) ||
	 * param.equals(UIConstants.PARAM_SETTLE_PREMATCH_VIEW)) { Map<String,
	 * Object> result = new HashMap<String, Object>(); result.put("status",
	 * "success"); result.put("prematch", "yes"); result.put("message",
	 * Messages.get("transaction.prematch.yes", trx.getTransactionNumber()));
	 * renderJSON(result);
	 * 
	 * //settlementPrematch(); } else if (trx.getTransactionKey() != null) {
	 * Map<String, Object> result = new HashMap<String, Object>();
	 * result.put("status", "success"); result.put("message",
	 * Messages.get("transaction.confirmed.successful",
	 * trx.getTransactionNumber())); renderJSON(result); } else {
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants
	 * .CS_TRANSACTION_BUY_SELL)); render("Transactions/detail.html", trx, mode,
	 * confirming); } } catch (MedallionException ex) { Map<String, Object>
	 * result = new HashMap<String, Object>(); result.put("status", "error");
	 * List<String> params = new ArrayList<String>(); for (Object paramItem :
	 * ex.getParams()) { params.add(Messages.get(paramItem)); }
	 * result.put("error", Messages.get("error." + ex.getErrorCode(),
	 * params.toArray())); renderJSON(result); } }
	 */

	/*
	 * public static void back(Long transactionKey, boolean settlement, String
	 * param) throws JsonParseException, JsonMappingException, IOException {
	 * logger.debug("BACK AREA with transactionKey = " + transactionKey +
	 * "; settlement=" + settlement + "; param=" + param); CsTransaction
	 * transaction = serializerService.deserialize(session.getId(),
	 * transactionKey, CsTransaction.class); //START UDFS // if
	 * (transaction.getUdf() != null) { // Map<String, String> data =
	 * json.readValue(transaction.getUdf(), TypeFactory.mapType(HashMap.class,
	 * String.class, String.class)); // // udfs =
	 * generalService.listUdfMastersByCategory
	 * (LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION); // for (UdfMaster udf
	 * : udfs) { // udf.setValue(data.get(udf.getFieldName())); // if (
	 * udf.getLookupGroup() != null ) { //
	 * udf.setLookupGroup(generalService.getLookupGroup
	 * (udf.getLookupGroup().getLookupGroup())); //
	 * udf.setOptions(generalService
	 * .listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
	 * udf.getLookupGroup().getLookupGroup())); // } // //item.options =
	 * generalService
	 * .listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
	 * item.getLookupGroup().getLookupGroup()); // } // } //END UDFS String mode
	 * = UIConstants.DISPLAY_MODE_ENTRY; if (settlement) {
	 * logger.debug("settle single"); renderArgs.put("cbestFlows",
	 * generalService.listLookupsForDropDownAsSelectItem(
	 * UIConstants.SIMIAN_BANK_ID, LookupConstants._CBEST_FLOW));
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT));
	 * render("Transactions/settleSingle.html", transaction, mode, settlement);
	 * } else if (param.equals(UIConstants.PARAM_SETTLE_PREMATCH_VIEW)) { param
	 * = UIConstants.PARAM_SETTLE_PREMATCH; mode =
	 * UIConstants.DISPLAY_MODE_VIEW; flash.put(UIConstants.BREADCRUMB,
	 * applicationService
	 * .getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
	 * render("Transactions/entry.html", mode, transaction, param); } else {
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants
	 * .CS_TRANSACTION_BUY_SELL)); render("Transactions/entry.html",
	 * transaction, mode); } // render("Transactions/entry.html", transaction,
	 * mode); // render(transaction, mode); }
	 */

	@Check("transaction.buySell.save")
	public static void save(CsTransaction transaction, CsTransactionCharge[] charges, GnUdfMaster[] udfs, boolean settlement, String currencyCodeDisplay, String blockedAccount, String mode, boolean isreload) {
		log.debug("save. transaction: " + transaction + " charges: " + charges + " udfs: " + udfs + " settlement: " + settlement + " currencyCodeDisplay: " + currencyCodeDisplay + " blockedAccount: " + blockedAccount + " mode: " + mode + " isreload: " + isreload);

		if (transaction != null && charges != null) {
			transaction.getTransactionCharges().clear();
			// transaction.getTransactionCharges().addAll(charges);
			for (CsTransactionCharge charge : charges) {
				transaction.getTransactionCharges().add(charge);
			}
		}

		// TODO : CHANGE THIS
		// for temporary

		if (transaction != null) {
			try {
				log.debug("udfs:" + udfs);
				Map<String, String> data = new HashMap<String, String>();
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						log.debug("Field Name = " + udf.getFieldName());
						log.debug("Field Value = " + udf.getValue());
						data.put(udf.getFieldName(), udf.getValue());
						if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
					String udfString = json.writeValueAsString(data);
					log.debug("UDF STRING = " + udfString);
					transaction.setUdf(udfString);
				}
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
			// get default bank code
			GnSystemParameter systemParameter = generalService.getSystemParameter(SystemParamConstants.PARAM_BANK_ORG);
			GnThirdParty bankThirdParty = generalService.getThirdParty(Long.parseLong(systemParameter.getValue()));
			//Date currentDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			// validate
			validation.required("Transaction Date is", transaction.getTransactionDate());
			validation.required("Account No is", transaction.getAccount().getAccountNo());
			validation.required("Transaction Code is", transaction.getTransactionTemplate().getTransactionCode());
			validation.required("Security Code is", transaction.getSecurity().getSecurityId());
			if (transaction.getSecurity().getIsExpire() != null) {
				if (transaction.getSecurity().getIsExpire() == true) {
					if (fmtYYYYMMDD(transaction.getSecurity().getExpiredDate()).compareTo(fmtYYYYMMDD(transaction.getTransactionDate())) <= 0) {
						if (transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equals(LookupConstants.TRANSACTION_TYPE_BUY_CODE)) {
							validation.addError("", Messages.get(ExceptionConstants.SECURITY_CODE_EXPIRED, transaction.getSecurity().getSecurityId()));
						}
					}
				}
			}
			validation.required("Broker is", transaction.getCounterParty().getThirdPartyCode());
			validation.required("Classification is", transaction.getClassification().getLookupId());
			validation.required("Settlement Date is", transaction.getSettlementDate());
			validation.required("Settlement Account is", transaction.getSettlementAccount().getAccountNo());

			if (transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equals(LookupConstants.TRANSACTION_TYPE_SELL_CODE)) {
				validation.required("Holding Ref is", transaction.getHoldingRefs());
			}

			validation.required("Quantity/Face Value is", transaction.getQuantity());
			validation.required("Price is", transaction.getPrice());

			log.debug("DISCOUNTED = " + transaction.getTransactionTemplate().getSecurityType().getIsDiscounted());
			log.debug("IS_PRICE = " + transaction.getTransactionTemplate().getSecurityType().getIsPrice());

			if (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted() == true && transaction.getTransactionTemplate().getSecurityType().getIsPrice() == false) {
				validation.required("Nominal is", transaction.getAmount());
				validation.required("Tax Amount (Discount) is", transaction.getDiscountTax());
			}
			if (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted() == true) {
				validation.required("Tax Code (Discount) is", transaction.getDiscountTaxMaster().getTaxCode());
			}

			if (transaction.getTransactionTemplate().getSecurityType().getIsDiscounted() == true && transaction.getTransactionTemplate().getSecurityType().getIsPrice() == true) {
				validation.required("Tax Amount (Discount) is", transaction.getDiscountTax());
			}

			if (transaction.getTransactionTemplate().getSecurityType().getHasInterest() == true) {
				validation.required("Interest Rate is", transaction.getInterestRate());
				// validation.required("Accrued Interest is",
				// transaction.getAccruedInterest());
				// validation.required("Tax Code (Accrued) is",
				// transaction.getTaxOnInterestMaster().getTaxCode());
				validation.required("Tax Amount (Accrued) is", transaction.getTaxOnInterest());
			}

			if ((transaction.getTransactionTemplate().getSecurityType().getSecurityClass().getLookupCode().equals(LookupConstants.SECURITY_CLASS_MONEY_MARKET_CODE)) && (transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equals(LookupConstants.TRANSACTION_TYPE_BUY_CODE))) {
				validation.required("Maturity Date is", transaction.getMaturityDate());
			}

			/*
			 * String prematchRequiredTemp = params.get("prematchRequiredTemp");
			 * renderArgs.put("prematchRequiredTemp", prematchRequiredTemp);
			 */
			// if
			// ((transaction.getTransactionTemplate().getSecurityType().getSecurityClass().getLookupId().equalsIgnoreCase(SECURITY_CLASS_FIX_INCOME))
			// &&
			// (securityService.getSecurity(transaction.getSecurity().getSecurityKey()).getMarketOfRisk().getLookupId().equalsIgnoreCase(COUNTRY_INDONESIA))
			// && (!Boolean.parseBoolean(prematchRequiredTemp))) {
			if ((transaction.getTransactionTemplate().getSecurityType().getCtpRequired() == true) && (transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS) || transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) && (transaction.getTransactionTemplate().getDefPrematch()) == false) {
				validation.required("CTP Reference is", transaction.getCtpNo());
			}

			validation.required("Tax Amount (Capital Gain) is", transaction.getCapitalGainTax());

			/*
			 * if (charges!=null){ if (transaction.getRecordStatus()) if
			 * (validation.errorsMap().values().containsAll(charges) == false){
			 * validation.clear(); } }
			 */

			if ((!Helper.isNullOrEmpty(blockedAccount)) && (blockedAccount.equalsIgnoreCase(blockedAccountMessage))) {
				// validation.addError("", blockedAccountMessage);
				validation.addError("", Messages.get(ExceptionConstants.ACCOUNT_BLOCKED, transaction.getAccount().getAccountNo()));
			}

			if (!currencyCodeDisplay.equalsIgnoreCase(transaction.getSettlementAccount().getCurrency().getCurrencyCode())) {
				validation.addError("", "Currency for Customer's Cash Account should be [" + currencyCodeDisplay + "]");
			}

			Boolean isDefCoreBanking = false;
			if (transaction.getTransactionTemplate().getDefaultCorebanking() != null) {
				isDefCoreBanking = transaction.getTransactionTemplate().getDefaultCorebanking();
			}
			if ((isDefCoreBanking == true) && (transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equalsIgnoreCase(LookupConstants.TRANSACTION_TYPE_BUY_CODE)) && (!transaction.getSettlementAccount().getBankCode().getThirdPartyCode().equalsIgnoreCase(bankThirdParty.getThirdPartyCode()))) {
				validation.addError("", "Customer's Cash Account Bank must be MEGA");
			}

			String holdingType = (transaction.getTransactionTemplate().getSecurityType().getHoldingType() != null ? transaction.getTransactionTemplate().getSecurityType().getHoldingType().getLookupId() : null);
			boolean isAmotization = false;
			if (transaction.getValuationTrade() != null && transaction.getClassification().getLookupId().equals(LookupConstants.CLASSIFICATION_TRADING)) {
				if (transaction.getValuationTrade().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
					isAmotization = true;
				}
			}
			if (transaction.getValuationAFS() != null && transaction.getClassification().getLookupId().equals(LookupConstants.CLASSIFICATION_AFS)) {
				if (transaction.getValuationAFS().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
					isAmotization = true;
				}
			}
			if (transaction.getValuationHTM() != null && transaction.getClassification().getLookupId().equals(LookupConstants.CLASSIFICATION_HTM)) {
				if (transaction.getValuationHTM().equals(LookupConstants.VALUATION_METHOD_AMORTIZED)) {
					isAmotization = true;
				}
			}

			BigDecimal holdingQuantity = transaction.getHoldingQuantity() != null ? transaction.getHoldingQuantity() : null;

			if (transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equals(LookupConstants.TRANSACTION_TYPE_SELL_CODE)) {
				if (holdingType != null) {
					if (holdingType.equals(LookupConstants.HOLDING_TYPE_TRANSACTION)) {
						if (holdingQuantity != null && transaction.getQuantity().compareTo(holdingQuantity) != 0) {
							validation.addError("", "Quantity must be same with Holding Quantity");
						}
					} else {
						if (isAmotization) {
							if (holdingQuantity != null && transaction.getQuantity().compareTo(holdingQuantity) != 0) {
								validation.addError("", "Quantity must be same with Holding Quantity");
							}
						}
					}
				} else {
					if (isAmotization) {
						if (holdingQuantity != null && transaction.getQuantity().compareTo(holdingQuantity) != 0) {
							validation.addError("", "Quantity must be same with Holding Quantity");
						}
					}
				}
			}
			
			//start end yusuf 5737 std dari 5542,untuk validasi transaksi unik
			List<CsTransaction> csTransactionTemp = transactionService.getTransactionBy5737(transaction.getTransactionDate(), transaction.getTransactionTemplate().getTransactionCode(), transaction.getExternalReference());  
			if(csTransactionTemp.size()>0) {
				 validation.addError("", "Transaction already exists");
			}
			//end yusuf
			
			if (validation.hasErrors()) {
				Map<String, String> data = new HashMap<String, String>();
				try {
					if (transaction.getUdf() != null)
						data = json.readValue(transaction.getUdf(), TypeFactory.mapType(HashMap.class, String.class, String.class));
				} catch (JsonParseException e) {
					log.error(e.getMessage(), e);
				} catch (JsonMappingException e) {
					log.error(e.getMessage(), e);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				// udfs =
				// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						// START UDF FOR DROPDOWN
						// if (udf.getLookupGroup() != null) {
						if (!udf.getLookupGroup().getLookupGroup().isEmpty()) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
						// END DROPDOWN
					}
				}

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_BUY_SELL));
				render("Transactions/entry.html", transaction, charges, udfs, settlement, currencyCodeDisplay, mode, isreload);
			}
			log.debug("settlement = " + settlement);
			if (!settlement) {
				log.debug("masuk sini");
				if ((transaction.getAccruedInterest() == null) || (transaction.getAccruedInterest().compareTo(BigDecimal.ZERO) < 0)) {
					log.debug(">>>>>>><<<<<<<<<<<<<<<)))))))))))))))");
					transaction.setAccruedInterest(BigDecimal.ZERO);
				}
			}
			log.debug("ACCRUED INTEREST = " + transaction.getAccruedInterest());
			log.debug("ACCRUED INTEREST 2 = " + transaction.getAccruedInterest());
			log.debug("WAVE ACCRUED INTEREST 2 = " + transaction.getWaveAccrued());
			log.debug("------------------------");
			log.debug("amount = " + transaction.getAmount());
			serializerService.serialize(session.getId(), transaction.getTransactionKey(), transaction);
			confirming(transaction.getTransactionKey(), settlement, currencyCodeDisplay, "", mode, isreload);
			// render("Transactions/detail.html", transaction, taskId, model);
		} else {
			entry();
		}

	}

	@Check("transaction.buySell.confirming")
	public static void confirming(Long transactionKey, boolean settlement, String currencyCodeDisplay, String param, String mode, boolean isreload) {
		log.debug("confirming. transactionKey: " + transactionKey + " settlement: " + settlement + " currencyCodeDisplay: " + currencyCodeDisplay + " param: " + param + " mode: " + mode + " isreload: " + isreload);

		boolean confirming = true;
		CsTransaction transaction = serializerService.deserialize(session.getId(), transactionKey, CsTransaction.class);
		log.debug("[CONFIRMING] transaction.dailyPortfolioFlag = " + transaction.getDailyPortfolioFlag());
		log.debug("[CONFIRMING] transaction.getMatchStatus = " + (transaction.getMatchStatus() != null ? transaction.getMatchStatus().getLookupId() : "null"));

		log.debug(">>> [JUN] transaction=" + transaction);

		log.debug("ACCRUED INTEREST CONF = " + transaction.getAccruedInterest());
		log.debug("WAVE ACCRUED INTEREST CONF = " + transaction.getWaveAccrued());
		if (transaction.getRemarkCorrection() != null) {
			transaction.setNeedCorrection(true);
		}
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		// TODO : CHANGE THIS
		if (transaction.getUdf() != null) {
			try {
				log.debug("TRX UDF = " + transaction.getUdf());
				Map<String, String> data = new HashMap<String, String>();
				if (transaction.getUdf() != null) {
					data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
					});
				}

				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						log.debug(">>> udf field name = " + udf.getFieldName());
						log.debug(">>> udf value = " + udf.getValue());
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			}
		}
		if (settlement) {
			renderArgs.put("cbestFlows", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CBEST_FLOW));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT));
			render("Transactions/settleSingle.html", transaction, udfs, mode, confirming, isreload);
		} else {
			BnAccount bankAccount = new BnAccount();
			if (param.equals(UIConstants.PARAM_SETTLE_PREMATCH)) {
				param = UIConstants.PARAM_SETTLE_PREMATCH_VIEW;
				String accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET;
				if (LookupConstants.CS_TRANSACTION_TYPE_BUY.equals(transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupId())) {
					accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT;
				}

				ScMaster security = securityService.getSecurity(transaction.getSecurity().getSecurityKey());
				//GnCustodianBank cb = accountService.getCustodianBank(transaction.getSecurity().getDepositoryCode().getLookupId(), security.getCurrency().getCurrencyCode(), accountDomain);
				String usedBy = "'"+LookupConstants.USED_BY_BUY_SELL+"'";
				GnCustodianBank cb = transactionService.getCustodianBank(security.getCurrency().getCurrencyCode(), accountDomain, usedBy);
				log.debug("cb => " + cb);
				if (cb != null) {
					bankAccount.setAccountNo(cb.getAccountNo());
					bankAccount.setBankCode(cb.getBankCode());
					bankAccount.setName(cb.getAccountName());
					bankAccount.setCurrency(cb.getCurrency());
				}
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
			} else
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_BUY_SELL));
			render("Transactions/entry.html", transaction, bankAccount, udfs, mode, currencyCodeDisplay, confirming, param, isreload);
		}
	}

	private static Map<Long, Long> keyMap = new HashMap<Long, Long>();

	@Transactional(propagation = Propagation.REQUIRED)
	public static void confirm(CsTransaction transaction, CsTransactionCharge[] charges, GnUdfMaster[] udfs, String param, String mode, boolean isreload, boolean warningMsg) {
		log.debug("confirm. transaction: " + transaction + " charges: " + charges + " udfs: " + udfs + " param: " + param + " mode: " + mode + " isreload: " + isreload + " warningMsg: " + warningMsg);

		if (transaction != null && charges != null) {
			transaction.getTransactionCharges().clear();
			// transaction.getTransactionCharges().addAll(charges);
			for (CsTransactionCharge charge : charges) {
				if (charge.getChargeWaived() == null)
					charge.setChargeWaived(false);
				transaction.getTransactionCharges().add(charge);
			}
		}
		if (udfs != null) {
			try {
				Map<String, String> data = new HashMap<String, String>();
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
				}

				String udfString = json.writeValueAsString(data);
				transaction.setUdf(udfString);
			} catch (IOException e) {
				log.debug(e.getMessage(), e);
			}
		}

		// set prematchRequired as value from transaction template,
		// in Web UI, prematchRequired is kept hidden
		// CsTransactionTemplate currTransactionTemplate =
		// accountService.getTransactionTemplate(transaction.getTransactionTemplate().getTransactionTemplateKey());
		// transaction.setPrematchRequired(currTransactionTemplate.getDefPrematch());
		transaction.setPrematchRequired(transaction.getTransactionTemplate().getDefPrematch());

		if (transaction.getSettlementAgent() != null && transaction.getSettlementAgent().getThirdPartyKey() == null) {
			transaction.setSettlementAgent(null);
		}

		if (transaction.getSettlementPurpose() != null && "".equals(transaction.getSettlementPurpose().getLookupId())) {
			transaction.setSettlementPurpose(null);
		}

		if (transaction.getSettlementReason() != null && "".equals(transaction.getSettlementReason().getLookupId())) {
			transaction.setSettlementReason(null);
		}
		
		if(transaction.getTransferType() == null || (transaction.getTransferType() != null 
				&& (transaction.getTransferType().getLookupId() == null ||
						transaction.getTransferType().getLookupId().equals(""))
				)){
			transaction.setTransferType(null);
		}

		boolean confirming = true;
		try {
			/*
			 * List<GnCalendar> holidays =
			 * generalService.listHolidays(UIConstants.SIMIAN_BANK_ID); for
			 * (GnCalendar holiday : holidays){ if
			 * (holiday.getId().getCalendarDate().getTime() ==
			 * (transaction.getSettlementDate().getTime())){ throw new
			 * MedallionException(ExceptionConstants.DATE_IS_HOLIDAY,
			 * "transaction.settlementDate"); }
			 * 
			 * if (holiday.getId().getCalendarDate().getTime() ==
			 * (transaction.getTransactionDate().getTime())){ throw new
			 * MedallionException(ExceptionConstants.DATE_IS_HOLIDAY,
			 * "transaction.transactionDate"); } }
			 */
			/*
			 * GnCalendar settlementDateHoliday =
			 * generalService.getCalendar(transaction.getSettlementDate());
			 * GnCalendar transactionDateHoliday =
			 * generalService.getCalendar(transaction.getTransactionDate()); if
			 * (settlementDateHoliday!=null){ throw new
			 * MedallionException(ExceptionConstants.DATE_IS_HOLIDAY,
			 * "transaction.settlementDate"); } if
			 * (transactionDateHoliday!=null){ throw new
			 * MedallionException(ExceptionConstants.DATE_IS_HOLIDAY,
			 * "transaction.transactionDate"); }
			 */
			log.debug("### TransactionCharges: " + transaction.getTransactionCharges().size());

			String menuCode = null;
			if ((UIConstants.PARAM_SETTLE_PREMATCH.equals(param)) || (UIConstants.PARAM_SETTLE_PREMATCH_VIEW.equals(param))) {
				menuCode = MenuConstants.CS_SETTLEMENT_PREMATCH;
				if (!isValidDuplicatePrematch(transaction.getTransactionKey().toString())) {
					throw new MedallionException("Record has already been modified", "");
				}

				if (transaction.getTransactionKey() != null) {
					CsTransaction prematch = transactionService.getTransactionPrematch(transaction.getTransactionKey());
					if (prematch == null) {
						throw new MedallionException("Record has been modified", "");
					} else {
						if (keyMap.get(transaction.getTransactionKey()) != null) {
							throw new MedallionException(Messages.get("Record has been modified"));
						} else {
							keyMap.put(transaction.getTransactionKey(), transaction.getTransactionKey());
						}
					}
				}
				//3075
				if (transaction.getHoldingRefs() != null && !transaction.getHoldingRefs().isEmpty()) {
					CsDailyHolding dailyHolding = accountService.getDailyHolding(transaction.getSettlementDate(), transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
					BigDecimal amountPledging = transactionService.getOutstandingQuantityAmount(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
					if(amountPledging==null) amountPledging = BigDecimal.ZERO;
					transaction.setHoldingQuantity(BigDecimal.ZERO);
					if(dailyHolding!= null){
						transaction.setHoldingQuantity(dailyHolding.getActualSettledQuantity().subtract(amountPledging));
					}
				}
			} else {
				menuCode = MenuConstants.CS_TRANSACTION_BUY_SELL;
			}
			
			if ((transaction.getHoldingRefs() != null && !transaction.getHoldingRefs().isEmpty()) 
					&& transaction.getHoldingQuantity().compareTo(transaction.getQuantity()) < 0) {
				keyMap.remove(transaction.getTransactionKey());
				throw new MedallionException(ExceptionConstants.TRANSACTION_NOT_ENOUGH_SOLD_QUANTITY, "");
			}

			// validate for edit in need correction case
			if (transaction.getTransactionKey() != null) {
				if (menuCode != MenuConstants.CS_SETTLEMENT_PREMATCH) {
					CsTransaction currentTransaction = accountService.getTransaction(transaction.getTransactionKey());
					if ((currentTransaction.getRecordStatus().equalsIgnoreCase(LookupConstants.__RECORD_STATUS_UPDATED))) {
						keyMap.remove(transaction.getTransactionKey());
						throw new MedallionException(ExceptionConstants.DATA_CANT_EDITED, "");
					}
				}
			}

			// if not waive == waive Accrued 0
			if (transaction.getTransactionTemplate().getSecurityType().getHasInterest()) {
				if ((!transaction.isWaiveAccruedInterest()) && (transaction.getAccruedInterest().compareTo(BigDecimal.ZERO) > 0))
					transaction.setWaveAccrued(BigDecimal.ZERO);
			} else {
				transaction.setWaveAccrued(BigDecimal.ZERO);
			}
			if (transaction.getWaveAccrued() != null) {
				if (transaction.getWaveAccrued().compareTo(BigDecimal.ZERO) < 0) {
					transaction.setWaveAccrued(BigDecimal.ZERO);
				}
			}

			transaction.setRemarkCorrection(null);
			BnAccount bankAccount = bankAccountService.getBankAccount(transaction.getSettlementAccount().getBankAccountKey());
			transaction.setPaymentType(bankAccount.getPaymentType());
			CsTransaction cekTrxCancel = null;
			if (transaction.getTransactionKey() != null)
				cekTrxCancel = transactionService.getTransactionByCancelledTransactionKey(transaction.getTransactionKey());

			if (cekTrxCancel != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "error");
				result.put("error", Messages.get("transaction.prematch.cancel.reject"));
				renderJSON(result);
			} else {
				if (transaction.getTransferMethod() != null && "".equals(transaction.getTransferMethod().getLookupId()))
					transaction.setTransferMethod(null);
				if (param.equals(UIConstants.PARAM_SETTLE_PREMATCH) || param.equals(UIConstants.PARAM_SETTLE_PREMATCH_VIEW)) {
					transaction.setAutoPrematchType(false);
					transaction.setMatchStatus(generalService.getLookup(LookupConstants.MATCH_STATUS_M));
				}

				log.debug("************************************");
				log.debug("transaction amount = " + transaction.getAmount());
				if (!Helper.isNullOrEmpty(menuCode) && menuCode.trim().equals(MenuConstants.CS_TRANSACTION_BUY_SELL)) {
					// #3024 Point 1
					String compliancePre = transactionService.fCompliancePre(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getAmount(), transaction.getTransactionDate());
					if ((!compliancePre.equalsIgnoreCase(SUCCESS_COMPLIANCE_PRE)) && (transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equalsIgnoreCase(LookupConstants.TRANSACTION_TYPE_BUY_CODE)) && !warningMsg) {
						keyMap.remove(transaction.getTransactionKey());
						throw new MedallionException(ExceptionConstants.TRANSACTION_SECURITY_NOT_ALLOWED, "");
					}
				}
				CsTransaction trx = transactionService.createTransactionWithSerialized(menuCode, transaction, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
				keyMap.remove(transaction.getTransactionKey());
				if (param.equals(UIConstants.PARAM_SETTLE_PREMATCH) || param.equals(UIConstants.PARAM_SETTLE_PREMATCH_VIEW)) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("prematch", "yes");
					result.put("message", Messages.get("transaction.prematch.yes", trx.getTransactionNumber()));
					
					renderJSON(result);

					// settlementPrematch();
				} else if (trx.getTransactionKey() != null) {
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("status", "success");
					result.put("message", Messages.get("transaction.confirmed.successful", trx.getTransactionNumber()));
					renderJSON(result);
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_BUY_SELL));
					render("Transactions/detail.html", trx, mode, confirming, isreload);
				}
			}

		} catch (MedallionException ex) {
			log.debug(ex);
			keyMap.remove(transaction.getTransactionKey());
			Map<String, Object> result = new HashMap<String, Object>();
			List<String> params = new ArrayList<String>();
			if (ex.getParams() != null) {
				for (Object paramItem : ex.getParams()) {
					log.debug("PARAMS ITEM = " + paramItem);

					params.add(Messages.get(paramItem));
				}
				if (ex.getErrorCode().equals(ExceptionConstants.TRANSACTION_SECURITY_NOT_ALLOWED)) {
					result.put("status", "warning");
					result.put("warning", Messages.get("error." + ex.getErrorCode(), params.toArray()));
				} else {
					result.put("status", "error");
					result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
				}
			} else {
				result.put("status", "error");
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			renderJSON(result);
		}catch (Exception e) {
			log.debug(e);
			keyMap.remove(transaction.getTransactionKey());
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error",e.getMessage());
			e.printStackTrace();
			renderJSON(result);
		} finally {
			keyMap.remove(transaction.getTransactionKey());
		}
	}

	public static void back(Long transactionKey, boolean settlement, String currencyCodeDisplay, String param, String mode, boolean isreload) throws JsonParseException, JsonMappingException, IOException {
		log.debug("back. transactionKey: " + transactionKey + " settlement: " + settlement + " currencyCodeDisplay: " + currencyCodeDisplay + " param: " + param + " mode: " + mode + " isreload: " + isreload);

		CsTransaction transaction = serializerService.deserialize(session.getId(), transactionKey, CsTransaction.class);

		CsTransactionTemplate template = accountService.getTransactionTemplate(UIConstants.SIMIAN_BANK_ID, transaction.getTransactionTemplate().getTransactionCode(), "USED_BY-1");
		if (template != null) {
			String prematchRequiredTemp = Boolean.toString(template.getDefPrematch());
			renderArgs.put("prematchRequiredTemp", prematchRequiredTemp);
		}

		if (transaction.getRemarkCorrection() != null) {
			transaction.setNeedCorrection(true);
		}
		// START UDFS
		// if (transaction.getUdf() != null) {
		// Map<String, String> data = json.readValue(transaction.getUdf(),
		// TypeFactory.mapType(HashMap.class, String.class, String.class));
		//
		// udfs =
		// generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		// for (UdfMaster udf : udfs) {
		// udf.setValue(data.get(udf.getFieldName()));
		// if ( udf.getLookupGroup() != null ) {
		// udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
		// udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// udf.getLookupGroup().getLookupGroup()));
		// }
		// //item.options =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// item.getLookupGroup().getLookupGroup());
		// }
		// }
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		// TODO : CHANGE THIS
		if (transaction.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			}
		}
		// END UDFS

		BnAccount bankAccount = new BnAccount();
		if (settlement) {
			log.debug("settle single");
			renderArgs.put("cbestFlows", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CBEST_FLOW));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT));
			render("Transactions/settleSingle.html", transaction, mode, settlement, isreload);
		} else if (param.equals(UIConstants.PARAM_SETTLE_PREMATCH_VIEW)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
			String currentDate = dateFormat.format(taService.getCurrentBusinessDate());
			param = UIConstants.PARAM_SETTLE_PREMATCH;
			mode = UIConstants.DISPLAY_MODE_VIEW;

			String accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET;
			if (LookupConstants.CS_TRANSACTION_TYPE_BUY.equals(transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupId())) {
				accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT;
			}

			ScMaster security = securityService.getSecurity(transaction.getSecurity().getSecurityKey());
			//GnCustodianBank cb = accountService.getCustodianBank(transaction.getSecurity().getDepositoryCode().getLookupId(), security.getCurrency().getCurrencyCode(), accountDomain);
			String usedBy = "'"+LookupConstants.USED_BY_BUY_SELL+"'";
			GnCustodianBank cb = transactionService.getCustodianBank(security.getCurrency().getCurrencyCode(), accountDomain, usedBy);
			log.debug("cb => " + cb);
			if (cb != null) {
				bankAccount.setAccountNo(cb.getAccountNo());
				bankAccount.setBankCode(cb.getBankCode());
				bankAccount.setName(cb.getAccountName());
				bankAccount.setCurrency(cb.getCurrency());
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
			render("Transactions/entry.html", mode, bankAccount, transaction, currencyCodeDisplay, param, currentDate, isreload);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_BUY_SELL));
			render("Transactions/entry.html", transaction, currencyCodeDisplay, mode, udfs, isreload);
		}
		// render("Transactions/entry.html", transaction, mode);
		// render(transaction, mode);
	}

	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		CsTransaction transaction = new CsTransaction();
		transaction.setTransactionDate(new Date());
		transaction = accountService.getTransactionCharges(UIConstants.SIMIAN_BANK_ID, transaction);
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory("TRANSACTION");
		render("Transactions/detail.html", transaction, mode, udfs);

	}

	public static void cancel(String taskId) {
		log.debug("cancel. taskId: " + taskId);
	}

	public static void charges(Long custodyAccountKey, String securityClass, String securityType, Long transactionTemplateKey, Long securityKey, BigDecimal quantity, BigDecimal nominal) {
		log.debug("charges. custodyAccountKey: " + custodyAccountKey + " securityClass: " + securityClass + " securityType: " + securityType + " transactionTemplateKey: " + transactionTemplateKey + " securityKey: " + securityKey + " quantity: " + quantity + " nominal: " + nominal);

		if (custodyAccountKey != null) {

			List<CsTransactionCharge> transactionCharges = transactionService.getDefaultCharges(LookupConstants.CHARGE_CATEGORY_TRANSACTION, custodyAccountKey, securityClass, securityType, transactionTemplateKey, securityKey, quantity, nominal);
			String charges = null;
			for (CsTransactionCharge csTransactionCharge : transactionCharges) {
				BigDecimal taxAmount = csTransactionCharge.getTaxAmount();
				if (taxAmount == null) {
					taxAmount = BigDecimal.ZERO;
					csTransactionCharge.setTaxAmount(taxAmount);
				}
				csTransactionCharge.setChargeNetAmount(csTransactionCharge.getChargeValue().add(taxAmount));
				csTransactionCharge.setTaxMaster(csTransactionCharge.getChargeMaster().getTaxMaster());
			}
			try {
				charges = json.writeValueAsString(transactionCharges);
			} catch (JsonGenerationException e) {
				log.debug("serialize", e);
			} catch (JsonMappingException e) {
				log.debug("serialize", e);
			} catch (IOException e) {
				log.debug("serialize", e);
			}
			log.debug("### charges: " + charges);
			renderJSON(charges);
		}
	}

	public static void charge(String code, BigDecimal quantity, BigDecimal nominal, Long custodyAccountKey) {
		log.debug("charge. code: " + code + " quantity: " + quantity + " nominal: " + nominal + " custodyAccountKey: " + custodyAccountKey);

		// CsChargeMaster charge =
		// generalService.getChargeMaster(UIConstants.SIMIAN_BANK_ID, code);
		CsChargeMaster charge = generalService.getChargeMasterForTransaction(UIConstants.SIMIAN_BANK_ID, code);
		if (charge != null) {
			CsTransactionCharge transactionCharge = transactionService.calculateCharge(charge, quantity, nominal, custodyAccountKey);
			BigDecimal taxAmount = transactionCharge.getTaxAmount();
			// if (taxAmount==null) taxAmount = BigDecimal.ZERO;
			if (taxAmount == null) {
				taxAmount = BigDecimal.ZERO;
				transactionCharge.setTaxAmount(taxAmount);
			}
			transactionCharge.setChargeNetAmount(transactionCharge.getChargeValue().add(taxAmount));
			transactionCharge.setTaxMaster(transactionCharge.getChargeMaster().getTaxMaster());
			// transactionCharge.setTaxAmount(taxAmount);
			try {
				renderJSON(json.writeValueAsString(transactionCharge));
			} catch (JsonGenerationException e) {
				log.debug("serialize", e);
			} catch (JsonMappingException e) {
				log.debug("serialize", e);
			} catch (IOException e) {
				log.debug("serialize", e);
			}
		}
	}

	public static void edit(Long id, String param) {
		log.debug("edit. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsTransaction transaction = accountService.getTransaction(id);
		BigDecimal txCharge = new BigDecimal(0);
		for (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
			BigDecimal taxAmount = chargeTrans.getTaxAmount();
			// if (taxAmount==null) taxAmount = BigDecimal.ZERO;
			if (taxAmount == null) {
				taxAmount = BigDecimal.ZERO;
				chargeTrans.setTaxAmount(taxAmount);
			}
			if (!Helper.isNullOrEmpty(chargeTrans.getChargeFrequency()) && !LookupConstants.CHARGE_FREQUENCY_MONTHLY.equals(LookupConstants._CHARGE_FREQUENCY + "-" + chargeTrans.getChargeFrequency())) {
				txCharge = taxAmount.add(txCharge);
			}

			chargeTrans.setChargeNetAmount((chargeTrans.getChargeValue() == null ? BigDecimal.ZERO : chargeTrans.getChargeValue()).add(chargeTrans.getTaxAmount()));
			chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
		}
		transaction.setTaxOfCharges(txCharge);
		if ((transaction.getAccruedInterest() == null) || (transaction.getAccruedInterest().compareTo(BigDecimal.ZERO) < 0)) {
			transaction.setAccruedInterest(BigDecimal.ZERO);
		}

		CsTransactionTemplate template = accountService.getTransactionTemplate(UIConstants.SIMIAN_BANK_ID, transaction.getTransactionTemplate().getTransactionCode(), "USED_BY-1");
		String prematchRequiredTemp = Boolean.toString(template.getDefPrematch());
		renderArgs.put("prematchRequiredTemp", prematchRequiredTemp);

		renderArgs.put("securityClass", template.getSecurityType().getSecurityClass().getLookupCode());

		if (transaction.getHoldingRefs() != null) {
			CsDailyHolding dailyHolding = accountService.getDailyHolding(transaction.getSettlementDate(), transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
			BigDecimal amountPledging = transactionService.getOutstandingQuantityAmount(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());

			transaction.setHoldingQuantity(dailyHolding.getSettledQuantity().subtract(amountPledging));
		}

		/*
		 * List<GnUdfMaster> udfs =
		 * generalService.listUdfMastersByCategory(LookupConstants
		 * .UDF_MASTER_CATEGORY_TRANSACTION);
		 * 
		 * for (GnUdfMaster udf : udfs) { if ( udf.getLookupGroup() != null ) {
		 * udf
		 * .setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().
		 * getLookupGroup()));
		 * udf.setOptions(generalService.listLookupsForDropDownAsSelectItem
		 * (UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
		 * } }
		 */
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (transaction.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
				}
			} catch (JsonParseException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_BUY_SELL));
		boolean isreload = true;

		setBase(transaction);

		render("Transactions/entry.html", transaction, param, udfs, mode, isreload);
	}

	/*
	 * public static void edit(Long key, String taskId) { // try { String mode =
	 * UIConstants.DISPLAY_MODE_EDIT; CsTransaction transaction =
	 * accountService.getTransaction(key);
	 * 
	 * //START UDFS // Map<String, String> data =
	 * json.readValue(transaction.getUdf(), TypeFactory.mapType(HashMap.class,
	 * String.class, String.class)); // // List<UdfMaster> udfs =
	 * generalService.
	 * listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION
	 * ); // // // for (UdfMaster udf : udfs) { //
	 * udf.setValue(data.get(udf.getFieldName())); // if ( udf.getLookupGroup()
	 * != null ) { //
	 * udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup
	 * ().getLookupGroup())); //
	 * udf.setOptions(generalService.listLookupsForDropDownAsSelectItem
	 * (UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup())); //
	 * } // //item.options =
	 * generalService.listLookupsForDropDownAsSelectItem(UIConstants
	 * .SIMIAN_BANK_ID, item.getLookupGroup().getLookupGroup()); // } //END UDFS
	 * 
	 * render("Transactions/entry.html", transaction, taskId, mode); // } catch
	 * (IOException e) { // logger.debug(e.getMessage(),e); // } }
	 */

	/*
	 * public static void approval(String taskId, Long keyId, String from,
	 * String processDefinition) { String mode = UIConstants.DISPLAY_MODE_VIEW;
	 * CsTransaction transaction = accountService.getTransaction(keyId); for
	 * (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
	 * BigDecimal taxAmount = chargeTrans.getTaxAmount(); if (taxAmount==null)
	 * taxAmount = BigDecimal.ZERO;
	 * chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue
	 * ().add(chargeTrans.getTaxAmount()));
	 * chargeTrans.setTaxMaster(chargeTrans.getChargeMaster().getTaxMaster()); }
	 * CsTransactionCharge charge = new CsTransactionCharge();
	 * if(transaction.getSource()!=null){
	 * if((transaction.getSource().equals(MenuConstants
	 * .CS_MISCELLANEOUS_CHARGE))){ Set<CsTransactionCharge> charges =
	 * transaction.getTransactionCharges(); charge = charges.iterator().next();
	 * } }
	 * 
	 * if (transaction.getHoldingRefs() != null){ CsDailyHolding dailyHolding =
	 * accountService.getDailyHolding(transaction.getSettlementDate(),
	 * transaction.getAccount().getCustodyAccountKey(),
	 * transaction.getSecurity().getSecurityKey(),
	 * transaction.getClassification().getLookupId(),
	 * transaction.getHoldingRefs()); BigDecimal amountPledging =
	 * transactionService
	 * .getOutstandingQuantityAmount(transaction.getAccount().
	 * getCustodyAccountKey (), transaction.getSecurity().getSecurityKey(),
	 * transaction.getClassification().getLookupId(),
	 * transaction.getHoldingRefs()); // dailyHolding.getSettledQuantity();
	 * transaction
	 * .setHoldingQuantity(dailyHolding.getSettledQuantity().subtract(
	 * amountPledging)); } boolean settlement = false; if
	 * (transaction.getTransactionTemplate() != null) { if
	 * (LookupConstants.TRANSACTION_CATEGORY_SETTLEMENT
	 * .equals(transaction.getTransactionTemplate().getTransactionCategory())) {
	 * settlement = true; } } List<GnUdfMaster> udfs =
	 * generalService.listUdfMastersByCategory
	 * (LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION); if
	 * (transaction.getUdf() != null) { try { Map<String, String> data =
	 * json.readValue(transaction.getUdf(), new TypeReference<HashMap<String,
	 * String>>() {}); for (GnUdfMaster udf : udfs) {
	 * udf.setValue(data.get(udf.getFieldName())); if ( udf.getLookupGroup() !=
	 * null ) {
	 * udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup
	 * ().getLookupGroup()));
	 * udf.setOptions(generalService.listLookupsForDropDownAsSelectItem
	 * (UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup())); } }
	 * } catch (JsonParseException e) { // TODO Auto-generated catch block
	 * logger.error(e.getMessage(), e); } catch (JsonMappingException e) { //
	 * TODO Auto-generated catch block logger.error(e.getMessage(), e); } catch
	 * (IOException e) { // TODO Auto-generated catch block
	 * logger.error(e.getMessage(), e); } }
	 * logger.debug("### ProcessDefinition: " + processDefinition); if (null !=
	 * processDefinition && processDefinition.contains("SettlementPrematch")) {
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants
	 * .CS_SETTLEMENT_PREMATCH)); String param =
	 * UIConstants.PARAM_SETTLE_PREMATCH_VIEW;
	 * render("Transactions/approval.html", transaction, udfs, taskId, charge,
	 * mode, param);
	 * 
	 * } else if (from.equals(UIConstants.WORKFLOW_FROM)) {
	 * flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
	 * render("Transactions/approval.html", transaction, udfs, taskId,charge,
	 * mode, settlement, from); } else { flash.put(UIConstants.BREADCRUMB,
	 * applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
	 * render("Transactions/approval.html", transaction, udfs, taskId,charge,
	 * mode, settlement, from); } }
	 * 
	 * public static void approve(String taskId, Long keyId) { Map<String,
	 * Object> result = new HashMap<String, Object>(); try { CsTransaction
	 * transaction = transactionService.approveTransaction(keyId,
	 * session.get(UIConstants.SESSION_USERNAME), taskId); result.put("status",
	 * "success"); result.put("message", Messages.get("transaction.approved",
	 * transaction.getTransactionNumber())); } catch (MedallionException ex) {
	 * result.put("status", "error"); List<String> params = new
	 * ArrayList<String>(); for (Object param : ex.getParams()) {
	 * params.add(Messages.get(param)); } result.put("error",
	 * Messages.get("error." + ex.getErrorCode(), params.toArray()));
	 * 
	 * } renderJSON(result); }
	 * 
	 * public static void reject(String taskId, Long keyId) { // try { //
	 * //transactionService.rejectTransaction(keyId, taskId); //
	 * renderText("rejected"); // } catch (MedallionException ex) { //
	 * renderText("error"); // } Map<String, Object> result = new
	 * HashMap<String, Object>(); try { logger.debug("task iD >>" +taskId +
	 * " <<< KEY ID >>>> " +keyId); CsTransaction transaction =
	 * transactionService.rejectTransaction(keyId,
	 * session.get(UIConstants.SESSION_USERNAME), taskId); result.put("status",
	 * "success"); result.put("message", Messages.get("transaction.rejected",
	 * transaction.getTransactionNumber())); } catch (MedallionException ex) {
	 * result.put("status", "error"); List<String> params = new
	 * ArrayList<String>(); for (Object param : ex.getParams()) {
	 * params.add(Messages.get(param)); } result.put("error",
	 * Messages.get("error." + ex.getErrorCode(), params.toArray())); }
	 * renderJSON(result); }
	 */

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " processDefinition: " + processDefinition + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			
			GnMaintenanceLog maintenance = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			
			// CsTransaction took from database
			// GnMaintenanceLog maintenance =
			// maintenanceService.getMaintenanceLog(maintenanceLogKey);
			// CsTransaction transaction =
			// json.readValue(maintenance.getNewData(), CsTransaction.class);
			CsTransaction transaction = accountService.getTransaction(keyId);
			
			if(maintenance!=null) {
				CsTransaction temp = json.readValue(maintenance.getNewData(), CsTransaction.class);
				transaction.setIsUpload(temp.getIsUpload());
			}
			
			BigDecimal txCharge = new BigDecimal(0);
			for (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
				BigDecimal taxAmount = chargeTrans.getTaxAmount();

				BigDecimal chargeValue = chargeTrans.getChargeValue();
				if (taxAmount == null)
					taxAmount = BigDecimal.ZERO;
				if (chargeValue == null)
					chargeValue = BigDecimal.ZERO;
				if (!Helper.isNullOrEmpty(chargeTrans.getChargeFrequency()) && !LookupConstants.CHARGE_FREQUENCY_MONTHLY.equals(LookupConstants._CHARGE_FREQUENCY + "-" + chargeTrans.getChargeFrequency())) {
					txCharge = taxAmount.add(txCharge);
				}
				chargeTrans.setChargeNetAmount(chargeValue.add(chargeTrans.getTaxAmount()));
				if (chargeTrans.getTaxMaster() == null) {
					chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
					;
				}

			}
			transaction.setTaxOfCharges(txCharge);

			CsTransactionCharge charge = new CsTransactionCharge();
			if (transaction.getSource() != null) {
				if ((transaction.getSource().equals(MenuConstants.CS_MISCELLANEOUS_CHARGE))) {
					Set<CsTransactionCharge> charges = transaction.getTransactionCharges();
					charge = charges.iterator().next();
				}
			}
			if (transaction.getWaveAccrued() != null) {
				if (transaction.getWaveAccrued().compareTo(BigDecimal.ZERO) > 0) {
					transaction.setWaiveAccruedInterest(true);
				}
			}
			if (transaction.getHoldingRefs() != null) {
				// CsPortfolio portfolio =
				// accountService.getPortfolio(transaction.getSettlementDate(),
				// transaction.getAccount().getCustodyAccountKey(),
				// transaction.getSecurity().getSecurityKey(),
				// transaction.getClassification().getLookupId(),
				// transaction.getHoldingRefs());
				CsPortfolio portfolio = accountService.getPortfolio(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
				// transaction.setHoldingQuantity(portfolio.getPortfolioQuantity());
				// logger.debug("Portfolio = "
				// +portfolio.getPortfolioQuantity());
				if (portfolio != null)
					transaction.setPortfolioQuantity(portfolio.getPortfolioQuantity());
				else
					transaction.setPortfolioQuantity(BigDecimal.ZERO);
				// untuk settlement holdingQuantity diambil dari
				// dailyHolding.settledActualQuantity
				CsDailyHolding dailyHolding = accountService.getDailyHolding(transaction.getSettlementDate(), transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
				BigDecimal amountPledging = transactionService.getOutstandingQuantityAmount(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
				// dailyHolding.getSettledQuantity();
				transaction.setActualSettledQuantity(dailyHolding.getActualSettledQuantity());
				transaction.setHoldingQuantity(dailyHolding.getSettledQuantity().subtract(amountPledging));
			}
			boolean settlement = false;
			if (transaction.getTransactionTemplate() != null) {
				if (LookupConstants.TRANSACTION_CATEGORY_SETTLEMENT.equals(transaction.getTransactionTemplate().getTransactionCategory())) {
					settlement = true;
				}
			}

			List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
			if (transaction.getUdf() != null) {
				try {
					Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
					});
					if (udfs != null) {
						for (GnUdfMaster udf : udfs) {
							udf.setValue(data.get(udf.getFieldName()));
							if (udf.getLookupGroup() != null) {
								udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
								udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
							}
						}
					}
				} catch (JsonParseException e) {
					log.error(e.getMessage(), e);
				} catch (JsonMappingException e) {
					log.error(e.getMessage(), e);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			boolean isreload = true;
			log.debug("### ProcessDefinition: " + processDefinition);
			// logger.debug("MATCH STATUS = "
			// +transaction.getMatchStatus().getLookupId());
			// if (null != processDefinition &&
			// processDefinition.contains("SettlementPrematch")) {

			BnAccount bankAccount = new BnAccount();
			String accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET;
			if (LookupConstants.CS_TRANSACTION_TYPE_BUY.equals(transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupId())) {
				accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT;
			}

			ScMaster security = securityService.getSecurity(transaction.getSecurity().getSecurityKey());
			//GnCustodianBank cb = accountService.getCustodianBank(transaction.getSecurity().getDepositoryCode().getLookupId(), security.getCurrency().getCurrencyCode(), accountDomain);
			String usedBy = "'"+LookupConstants.USED_BY_BUY_SELL+"'";
			GnCustodianBank cb = transactionService.getCustodianBank(security.getCurrency().getCurrencyCode(), accountDomain, usedBy);
			log.debug("cb => " + cb);
			if (cb != null) {
				bankAccount.setAccountNo(cb.getAccountNo());
				bankAccount.setBankCode(cb.getBankCode());
				bankAccount.setName(cb.getAccountName());
				bankAccount.setCurrency(cb.getCurrency());
			}

			if (transaction.getMatchStatus() != null) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
				String param = UIConstants.PARAM_SETTLE_PREMATCH_VIEW;
				SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
				String currentDate = dateFormat.format(taService.getCurrentBusinessDate());
				boolean fromApproval = true;
				boolean validateAccount = false;
				boolean validateThirdParty = false;

				List<CsSubAccount> subAccounts = accountService.listSubAccountByAccount(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getDepositoryCode().getLookupId());
				if (!transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_NA)) {
					if (!subAccounts.isEmpty()) {
						for (CsSubAccount csSubAccount : subAccounts) {
							if ((transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) && (csSubAccount.getDepository().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST))) {
								int length = csSubAccount.getCode().length();
								if (csSubAccount.getCode().substring(length - 2, length).equals("XX")) {
									validateAccount = true;
									break;
								} else {
									validateAccount = false;
								}
							} else if ((transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)) && (csSubAccount.getDepository().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS))) {
								validateAccount = false;
							}
						}
					} else {
						validateAccount = true;
					}
				}
				// if(transaction.getAccount().getSubAccounts().isEmpty())
				// validateAccount = false;
				if ((transaction.getSettlementAgent() != null) && (transaction.getSettlementAgent().getThirdPartyKey() != null)) {
					Long counterPartyKey = transaction.getSettlementAgent().getThirdPartyKey();
					List<GnThirdPartyAccount> thirdPartyAccs = generalService.listThirdPartyAccountByDepository(counterPartyKey, transaction.getSecurity().getDepositoryCode().getLookupId());
					if (!transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_NA)) {
						if (thirdPartyAccs != null) {
							validateThirdParty = true;
							for (GnThirdPartyAccount gnThirdPartyAccount : thirdPartyAccs) {
								if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(transaction.getSecurity().getDepositoryCode().getLookupId())) {
									validateThirdParty = false;
									transaction.getSettlementAgent().setCbestAccount(gnThirdPartyAccount);
									break;
								}
							}
						} else {
							validateThirdParty = true;
						}
					}
				}

				render("Transactions/approval.html", transaction, bankAccount, currentDate, udfs, fromApproval, taskId, charge, mode, param, validateAccount, validateThirdParty, maintenanceLogKey, from, isreload);
			} else if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
				render("Transactions/approval.html", transaction, bankAccount, udfs, taskId, charge, mode, settlement, from, maintenanceLogKey, operation, keyId, isreload);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
				render("Transactions/approval.html", transaction, bankAccount, udfs, taskId, charge, mode, settlement, from, maintenanceLogKey, operation, keyId, isreload);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction, boolean warningMsg, boolean warningMsg2) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction + " warningMsg: " + warningMsg);

		try {
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CsTransaction newTransaction = new CsTransaction();
			try {
				newTransaction = json.readValue(maintenanceLog.getNewData(), CsTransaction.class);
			} catch (JsonParseException e) {
				throw new MedallionException(ExceptionConstants.INVALID_OPERATION, e);
			} catch (JsonMappingException e) {
				throw new MedallionException(ExceptionConstants.INVALID_OPERATION, e);
			} catch (IOException e) {
				throw new MedallionException(ExceptionConstants.INVALID_OPERATION, e);
			}
			
			if(newTransaction.getTransactionTemplate().getPortfolioTransaction() == null) {
				CsTransaction transactionOld = transactionService.getTransaction(newTransaction.getTransactionKey());
				newTransaction.getTransactionTemplate().setPortfolioTransaction(transactionOld.getTransactionTemplate().getPortfolioTransaction());
			}
			
			String compliancePre = transactionService.fCompliancePre(newTransaction.getAccount().getCustodyAccountKey(), newTransaction.getSecurity().getSecurityKey(), newTransaction.getAmount(), newTransaction.getTransactionDate());
			
			
			if (!warningMsg) {
				if ((!compliancePre.equalsIgnoreCase(SUCCESS_COMPLIANCE_PRE)) && (newTransaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equalsIgnoreCase(LookupConstants.TRANSACTION_TYPE_BUY_CODE))) {
					throw new MedallionException(ExceptionConstants.TRANSACTION_SECURITY_NOT_ALLOWED, "");
				}
			}
			
			if (!warningMsg2) {
				if (newTransaction.isFromUpload()) {
					if (!accountService.isAdjust(newTransaction)) {
						throw new MedallionException(ExceptionConstants.TRANSACTION_SETTLEMENT_NOT_ALLOWED, "");
					}
				}
			}

			CsTransaction transaction = transactionService.approveTransactionWithSerialized(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.approved", transaction.getTransactionNumber())));
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex.getParams() != null) {
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				if (ex.getErrorCode().equals(ExceptionConstants.TRANSACTION_SECURITY_NOT_ALLOWED)) {
					result.put("status", "warning");
					result.put("warning", Messages.get("error." + ex.getErrorCode(), params.toArray()));
				}
				if (ex.getErrorCode().equals(ExceptionConstants.TRANSACTION_SETTLEMENT_NOT_ALLOWED)) {
					result.put("status", "warning2");
					result.put("warning", Messages.get("error." + ex.getErrorCode(), params.toArray()));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			} else {
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			renderJSON(result);
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction, String csno) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction + " csno: " + csno);
		try {
			CsTransaction transaction = null;
			if (Helper.isNullOrEmpty(correction)) {
				transaction = transactionService.approveTransactionWithSerialized(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, null);
			} else {
				transaction = transactionService.approveTransactionWithSerialized(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction);
			}

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.rejected", transaction == null ? csno : transaction.getTransactionNumber())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("transaction.settlement")
	// public static void settlement(Date settlementDate, Long batchKey, String
	// accountNo, String securityId, boolean success) {
	// if (settlementDate == null) {
	// settlementDate =
	// generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
	// render(settlementDate, success);
	// } else {
	// // logger.debug(">>> [FRS] organizationId:" + organizationId);
	// log.debug("from settlement >>> [FRS] settlementDate:" + settlementDate);
	// log.debug("from settlement >>> [FRS] batchKey:" + batchKey);
	// log.debug("from settlement >>> [FRS] accountNo:" + accountNo);
	// log.debug("from settlement >>> [FRS] securityId:" + securityId);
	// List<CsTransaction> transactions =
	// accountService.listPendingSettlement(UIConstants.SIMIAN_BANK_ID,
	// settlementDate, batchKey, accountNo, securityId);
	// renderJSON(transactions, new TransactionGridSerializer());
	// // render(transactions, new TransactionGridSerializer(), success);
	// }
	// }

	@Check("transaction.settlement")
	public static void reset() {
		log.debug("reset. ");

		SettlementSearchParameters param = new SettlementSearchParameters();
		param.setSettlementDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		param.setSettlementDateMili(param.getSettlementDate().getTime());
		param.setSecurityType(new ScTypeMaster());
		param.setSecurity(new ScMaster());
		param.setDispatch(SettlementSearchParameters.DISPATCH_VIEW);
		param.setSendToDipository(true);

		render("Transactions/settlement.html", param);
	}

	@Check("transaction.settlement")
	public static void settlement(SettlementSearchParameters param) {
		log.debug("settlement. param: " + param);

		if (param == null) {
			param = new SettlementSearchParameters();
			param.setSettlementDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
			param.setSettlementDateMili(param.getSettlementDate().getTime());
			param.setSecurityType(new ScTypeMaster());
			param.setSecurity(new ScMaster());
			param.setSendToDipository(true);
		}
		param.setDispatch(SettlementSearchParameters.DISPATCH_VIEW);
		param.setTransactionKey(null);

		render("Transactions/settlement.html", param);
	}

	@Check("transaction.settlement")
	public static void settleBatch(SettlementSearchParameters param) {
		log.debug("settleBatch. param: " + param);

		param.setDispatch(SettlementSearchParameters.DISPATCH_SETTLE);

		if (param.getTransactionKey() != null)
			for (Long transactionKey : param.getTransactionKey()) {
				log.debug("elvino transactionKey " + transactionKey);
			}

		List<Long> listTransactionKey = new ArrayList<Long>();
		for (Long key : param.getTransactionKey()) {
			if (key != null) {
				listTransactionKey.add(key);
			}
		}

		CsTransactionBatch batch = new CsTransactionBatch();
		batch.setBatchDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		batch.setTransactions(new HashSet<CsTransaction>(accountService.listPendingSettlement(listTransactionKey)));
		serializerService.serialize(session.getId(), null, batch);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT));
		render("Transactions/settlement.html", param);
	}

	@Check("transaction.settlement")
	public static void validateSettlementDate(Long milisecond) {
		log.debug("validateSettlementDate. milisecond: " + milisecond);

		List<String> errors = new ArrayList<String>();

		Date settlementDate = new Date(milisecond);
		Date applicationDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		if (Helper.isGreaterYMD(settlementDate, applicationDate)) {
			errors.add("Settlement Date can not be greater then appliation Date [" + Helper.format(applicationDate, appProp.getDateFormat()) + "]");
		}

		if (errors.isEmpty())
			errors.add("");

		renderJSON(errors);
	}

	@Check("transaction.settlementPrematch")
	public static void validateThirdParty(String param) {
		log.debug("validateThirdParty. param: " + param);

		Map<String, Object> result = new HashMap<String, Object>();
		if (param != null) {
			String[] p = param.split("\\|");

			boolean validateThirdParty = false;
			Long counterPartyKey = Long.parseLong(p[1]);
			log.debug("validateThirdParty counterPartyKey = " + counterPartyKey);
			CsTransaction transaction = accountService.getTransaction(Long.parseLong(p[0]));
			List<GnThirdPartyAccount> thirdPartyAccs = generalService.listThirdPartyAccountByDepository(counterPartyKey, transaction.getSecurity().getDepositoryCode().getLookupId());
			if (!transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_NA)) {
				if (thirdPartyAccs != null) {
					validateThirdParty = true;
					for (GnThirdPartyAccount gnThirdPartyAccount : thirdPartyAccs) {
						if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(transaction.getSecurity().getDepositoryCode().getLookupId())) {
							validateThirdParty = false;
							break;
						}
					}
				} else {
					validateThirdParty = true;
				}
			}

			log.debug("validateThirdParty validateThirdParty = " + validateThirdParty);
			if ("true".equals(p[2]) && validateThirdParty)
				result.put("message", "This Counter Party is not member of " + p[3]);
		}
		renderJSON(result);
	}

	@Check("transaction.settlementPrematch")
	public static void validateSettlementDuplicate(String param) {
		log.debug("validateSettlementDuplicate. param: " + param);

		Map<String, Object> result = new HashMap<String, Object>();
		String[] p = param.split("\\|");
		if (!isValidDuplicatePrematch(p[0])) {
			result.put("message", "error.Record has already been modified");
		}
		renderJSON(result);
	}

	// redmine 3562
	private static Boolean isValidDuplicatePrematch(String tk) {
		Boolean isValid = true;
		Long transactionKey = Long.parseLong(tk);

		CsTransaction t = transactionService.getTransactionMatchStatus(transactionKey);
		if (t != null) {
			if (t.getMatchStatus() != null && t.getMatchStatus().getLookupId() != null) {
				isValid = false;
			}
		}

		return isValid;
	}

	public static void validateSettlementPrematch(String param) {
		log.debug("validateSettlementPrematch. param: " + param);

		Map<String, Object> result = new HashMap<String, Object>();
		if (param != null) {
			try {
				String[] p = param.split("\\|");
				if (p[0] != null && !"".equals(p[0])) {
					Long trnKeyOri = Long.parseLong(p[6]);
					List<CsTransaction> cst = accountService.getTrnByRedmine2847(trnKeyOri, p[0], new BigDecimal(p[4]), p[5], p[3], new BigDecimal(p[9]));
					log.debug("cst size => " + cst.size());
					if (cst.size() < 1) {
						result.put("message", "Counterparty, Quantity, and Sec Code, Price should be matched.");
					} else {
						CsTransaction t = accountService.getTrnByRedmine2847(cst, trnKeyOri);
						if (t != null) {
							if (accountService.checkTrnByRedmine2847part1(t.getTransactionNumber(), trnKeyOri, t)) {
								if (accountService.checkTrnByRedmine2847part2(t.getTransactionNumber(), trnKeyOri, t)) {
									log.debug("p[2] => " + p[2] + " " + t.getTransactionTemplate().getPortfolioTransaction().getCustodyTransactionCode());
									if ((RVP).equals(t.getTransactionTemplate().getPortfolioTransaction().getCustodyTransactionCode()) && (DVP).equals(p[2])) {
										// valid
									} else if ((RFOP).equals(t.getTransactionTemplate().getPortfolioTransaction().getCustodyTransactionCode()) && (DFOP).equals(p[2])) {
										// valid
									} else if ((DVP).equals(t.getTransactionTemplate().getPortfolioTransaction().getCustodyTransactionCode()) && (RVP).equals(p[2])) {
										// valid
									} else if ((DFOP).equals(t.getTransactionTemplate().getPortfolioTransaction().getCustodyTransactionCode()) && (RFOP).equals(p[2])) {
										// valid
									} else {
										result.put("message", "Invalid Transaction Code.");
									}
								} else {
									result.put("message", "Selected Inhouse Transfer already has Inhouse Transfer.");
								}
							} else {
								result.put("message", "In House Transfer already used.");
							}
						} else {
							result.put("message", "Please Prematching Inhouse Transfer First.");
						}
					}
				}

				if (!"".equals(p[1]) && !"".equals(p[7])) {
					GnSystemParameter settleVal = generalService.getSystemParameter(SystemParamConstants.SETTLE_VAL);
					if (settleVal != null) {
						GnAdjustmentDetail gnAdjDet = generalService.getAdjDetByCodeAndCurrency(settleVal.getValue(), p[8]);
						if (gnAdjDet != null) {
							BigDecimal settlementAmount = new BigDecimal(p[7]);
							BigDecimal settlementAmountAdj = new BigDecimal(p[1]);
							BigDecimal selisih = settlementAmountAdj.subtract(settlementAmount);
							BigDecimal adjValue = gnAdjDet.getValue();
							String operator = "";
							String operatorCode = gnAdjDet.getOperator().getLookupId();
							if (operatorCode.equals(LookupConstants.OPERATOR_FILTER_EQUAL)) {
								operator = "=";
							} else if (operatorCode.equals(LookupConstants.OPERATOR_FILTER_GREATER_THAN)) {
								operator = ">";
							} else if (operatorCode.equals(LookupConstants.OPERATOR_FILTER_IS_NOT)) {
								operator = "!=";
							} else if (operatorCode.equals(LookupConstants.OPERATOR_FILTER_LESS_THAN)) {
								operator = "<";
							} else if (operatorCode.equals(LookupConstants.OPERATOR_FILTER_GREATER_THAN_EQUAL)) {
								operator = ">=";
							} else if (operatorCode.equals(LookupConstants.OPERATOR_FILTER_LESS_THAN_EQUAL)) {
								operator = "<=";
							} else if (operatorCode.equals(LookupConstants.OPERATOR_FILTER_IN)) {
								operator = " in ";
							}
							// dijadiin absolute kata QA
							if (("0").equals(accountService.isValidSettlementinRange(selisih.abs(), adjValue, operator))) {
								result.put("message", "Settlement Amount Adjustment is out of range.");
							}
						} else {
							result.put("message", "Different currency with adjustment parameter.");
						}
					}
				}
			} catch (MedallionException e) {
				result.put("message", "In House Transfer not found.");
			}
		}
		renderJSON(result);
	}

	@Check("transaction.settlement")
	public static void validateTransaction(String depositoryId, Long custodyAccountKey, String ctpNo, boolean ctpRequired, String marketOfRiskId, Long transactionKey, boolean sendToDipository) {
		log.debug("validateTransaction. depositoryId: " + depositoryId + " custodyAccountKey: " + custodyAccountKey + " ctpNo: " + ctpNo + " ctpRequired: " + ctpRequired + " marketOfRiskId: " + marketOfRiskId + " transactionKey: " + transactionKey + " sendToDipository: " + sendToDipository);

		//String countryCode = generalService.getSystemParameter(SystemParamConstants.PARAM_ID_NATIONALITY).getValue();

		List<String> errors = new ArrayList<String>();
		if (sendToDipository) {
			if (!LookupConstants.DEPOSITORY_CODE_NA.equals(depositoryId)) {
				List<CsSubAccount> subAccounts = accountService.listSubAccountByAccount(custodyAccountKey, depositoryId);
				if (subAccounts.isEmpty()) {
					errors.add("Account is not exist");
				} else {
					if (LookupConstants.DEPOSITORY_CODE_KSEI_CBEST.equals(depositoryId)) {
						for (CsSubAccount subAccount : subAccounts) {
							if (subAccount.getCode().endsWith("XX")) {
								errors.add("Account is not exist");
							}
						}
					}
				}
			}

			if (ctpRequired && (LookupConstants.DEPOSITORY_CODE_KSEI_CBEST.equals(depositoryId) || LookupConstants.DEPOSITORY_CODE_BI_BISSSS.equals(depositoryId))) {
				if (ctpNo == null || "".equals(ctpNo)) {
					errors.add("CTP No is required");
				}
			}

			try {
				CsTransaction csTran = accountService.getCashTransaction(transactionKey);
				List<GnThirdPartyAccount> thirdPartyAccount = generalService.listThirdPartyAccountByDepository(csTran.getCounterParty().getThirdPartyKey(), depositoryId);
				if (thirdPartyAccount.isEmpty()) {
					errors.add("Issuer Account is not exist");
				}
			} catch (MedallionException me) {
				errors.add(me.getMessage());
			}
		}

		try {
			CsTransaction csTran = accountService.getCashTransaction(transactionKey);
			transactionService.checkHolding(csTran, "");
		} catch (MedallionException me) {
			List<String> params = new ArrayList<String>();
			if(me.getParams() != null){
				for (Object param : me.getParams()) {
					params.add(Messages.get(param));
				}
				errors.add(Messages.get("error." + me.getErrorCode(), params.toArray()));
			}else{
				errors.add(Messages.get("error." + me.getErrorCode()));
			}
			
		}catch (Exception e) {
			errors.add(Messages.get("error." + e.getMessage()));
		}

		if (errors.isEmpty())
			errors.add("");

		renderJSON(errors);

	}

	public static void confirmSettleBatch(List<Long> transactionKeys, boolean sendToDepository) {
		log.debug("confirmSettleBatch. transactionKeys: " + transactionKeys + " sendToDepository: " + sendToDepository);

		try {
			Map<Long, Boolean> sendToDepositoryMap = new HashMap<Long, Boolean>();
			for (Long transactionKey : transactionKeys) {
				sendToDepositoryMap.put(transactionKey, new Boolean(sendToDepository));
			}

			CsTransactionBatch batch = transactionService.settleTransactionNontrans(transactionKeys, session.get(UIConstants.SESSION_USERNAME), UIConstants.SIMIAN_BANK_ID, "ca-settlement", session.get(UIConstants.SESSION_USER_KEY), sendToDepositoryMap);

			for (String[] content : batch.getErrors()) {
				if (content[1].indexOf("|") > 0) {
					String[] array = content[1].split("\\|");
					content[1] = Messages.get("error." + array[0], array[1].split("_"));
				}
			}

			renderJSON(batch);
		} catch (MedallionException ex) {
			CsTransactionBatch batch = new CsTransactionBatch();
			batch.getErrors().add(new String[] { "global", ex.getMessage() });
			renderJSON(batch);
		}
	}

	@Check("transaction.settlement")
	public static void pagingSettlement(Paging page, SettlementSearchParameters param) {
		log.debug("pagingSettlement. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());

		if (param.isQuery()) {
			log.debug("SettlementDate " + param.getSettlementDate());
			log.debug("SecurityType " + param.getSecurityType().getSecurityType());
			log.debug("SecurityId " + param.getSecurity().getSecurityId());
			log.debug("param.getTransactionKey() " + param.getTransactionKey());

			param.setSettlementDate(new Date(param.getSettlementDateMili()));

			page.getParamFixMap().put("organizationId", UIConstants.SIMIAN_BANK_ID);
			page.getParamFixMap().put("transactionKeys", param.getTransactionKey());

			if (param.getTransactionKey() != null) {
				for (Long transactionKey : param.getTransactionKey()) {
					log.debug("transactionKey " + transactionKey);
				}
			}

			page.addParams("tx.settlementDate", Paging.LESSEQUAL, param.getSettlementDate());
			page.addParams("tx.security.securityType.securityType", Paging.EQUAL, param.getSecurityType().getSecurityType());
			page.addParams("tx.security.securityId", Paging.EQUAL, param.getSecurity().getSecurityId());
			page.addParams(Helper.searchAll("(tx.transactionNumber||tx.account.accountNo||" + "tx.account.name||tx.transactionTemplate.description||tx.settlementAmount)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = accountService.pagingSettlement(page);
		}
		renderJSON(page);
	}

	// @Check("transaction.settlement")
	// public static void settleBatch(List<Long> transactionKeys) {
	// if (transactionKeys == null) {
	// CsTransactionBatch batch = serializerService.deserialize(session.getId(),
	// null, CsTransactionBatch.class);
	// if (batch == null) {
	// throw new MedallionException(ExceptionConstants.PARAMETER_NULL,
	// "transactionKeys");
	// }
	// flash.put(UIConstants.BREADCRUMB,
	// applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT));
	// render("Transactions/settlementBatch.html", batch);
	// } else {
	// // Need to omit null items before calling the service
	// List<Long> keys = new ArrayList<Long>();
	// for (Long key : transactionKeys) {
	// if (key != null) {
	// keys.add(key);
	// }
	// }
	// List<CsTransaction> transactions =
	// accountService.listPendingSettlement(keys);
	// CsTransactionBatch batch = new CsTransactionBatch();
	// batch.setBatchDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
	// batch.setTransactions(new HashSet<CsTransaction>(transactions));
	// serializerService.serialize(session.getId(), null, batch);
	// flash.put(UIConstants.BREADCRUMB,
	// applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT));
	// render(batch);
	// }
	// }

	@Check("transaction.settlement")
	public static void settleSingle(Long key) {
		log.debug("settleSingle. key: " + key);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		boolean settlement = true;
		CsTransaction transaction = accountService.getTransaction(key);
		BigDecimal txCharge = new BigDecimal(0);
		for (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
			BigDecimal taxAmount = chargeTrans.getTaxAmount();
			if (taxAmount == null)
				taxAmount = BigDecimal.ZERO;
			txCharge = taxAmount.add(txCharge);
			chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
			chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
		}
		transaction.setTaxOfCharges(txCharge);
		if (transaction.getHoldingRefs() != null) {
			// untuk settlement holdingQuantity diambil dari
			// dailyHolding.settledActualQuantity
			CsDailyHolding dailyHolding = accountService.getLatestDailyHolding(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
			dailyHolding.getSettledQuantity();
			transaction.setHoldingQuantity(dailyHolding.getActualSettledQuantity());

			// untuk settlement holdingQuantity diambil dari
			// portfolio.portfolioQuantity
			// CsPortfolio portfolio =
			// accountService.getPortfolio(transaction.getSettlementDate(),
			// transaction.getAccount().getCustodyAccountKey(),
			// transaction.getSecurity().getSecurityKey(),
			// transaction.getClassification().getLookupId(),
			// transaction.getHoldingRefs());
			// transaction.setHoldingQuantity(portfolio.getPortfolioQuantity());

		}
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (transaction.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				for (GnUdfMaster udf : udfs) {
					udf.setValue(data.get(udf.getFieldName()));
					if (udf.getLookupGroup() != null) {
						udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
						udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
					}
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			}
		} else {
			for (GnUdfMaster udf : udfs) {
				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
			}
		}
		renderArgs.put("cbestFlows", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CBEST_FLOW));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT));
		render(transaction, udfs, mode, settlement);
	}

	@Transactional
	public static void confirmSettle(CsTransaction transaction, String taskId, String mode) {
		log.debug("confirmSettle. transaction: " + transaction + " taskId: " + taskId + " mode: " + mode);

		try {
			CsTransactionBatch batch = transactionService.settleTransaction(transaction, session.get(UIConstants.SESSION_USERNAME), UIConstants.SIMIAN_BANK_ID, AbstractTransaction.PARAM_CATEGORY_TRANSACTION_SETTLEMENT, session.get("userKey"));
			CsTransaction trx = batch.getTransactions().iterator().next();
			if (trx.getSettlementNumber() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("settlement.success", trx.getSettlementNumber(), batch.getTransactionBatchKey()));
				renderJSON(result);
			} else {
				boolean confirming = true;
				boolean settlement = true;
				render("Transactions/detail.html", trx, mode, confirming, settlement);
			}
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if(ex.getParams()!= null){
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			}else{
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			renderJSON(result);
		}catch (Exception e) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error",e.getMessage());
			renderJSON(result);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public static void confirmBatch(List<Long> transactionKeys, String from) {
		log.debug("confirmBatch. transactionKeys: " + transactionKeys + " from: " + from);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//String menuCode = null;
			//if (UIConstants.PARAM_CA_SETTLE.equals(from)) {
				//menuCode = MenuConstants.CS_CA_SETTLEMENT_ENTRY;
			//} else {
				//menuCode = MenuConstants.CS_SETTLEMENT;
			//}
			CsTransactionBatch batch = transactionService.settleTransaction(transactionKeys, session.get(UIConstants.SESSION_USERNAME), UIConstants.SIMIAN_BANK_ID, from, session.get(UIConstants.SESSION_USER_KEY));
			result.put("status", "success");
			result.put("message", Messages.get("settlement.batch.successful", batch.getTransactionBatchKey()));
			renderJSON(result);
		} catch (MedallionException ex) {
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if(ex.getParams()!= null){
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			}else{
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			renderJSON(result);
		}catch (Exception e) {
			result.put("status", "error");
			result.put("error",e.getMessage());
			renderJSON(result);
		}
	}

	public static void settleApprove() {
		log.debug("settleApprove. ");
	}

	// settlementApproval before date 30-jan-2013
	// public static void settlementApproval(String taskId, Long keyId, Long
	// refKey, String from) {
	// logger.debug("from settlement approval >>> taskId = " + taskId +
	// " keyId = " + keyId + " refKey = " + refKey + " from = " + from);
	// CsTransactionBatch batch = accountService.getTransactionBatch(refKey);
	// //
	// logger.debug("from settlement approval >>> batch.transactionBatchKey = "
	// + batch.getTransactionBatchKey() + " & batch.batchDate = " +
	// batch.getBatchDate());
	//
	// for (CsTransaction transaction : batch.getTransactions()) {
	// logger.debug("transactionKey = " + transaction.getTransactionKey());
	// CsTransaction transactionOriginal =
	// accountService.getTransactionForSettlement(transaction.getTransactionNumber());
	// transaction.setTransactionTemplate(transactionOriginal.getTransactionTemplate());
	// }
	//
	// ScEntitlement entitlement = securityService.getEntitlement(keyId);
	// ScCorporateAnnouncement corporateAnnouncement = null;
	// Set<ScEntitlementDetail> entitlementDetails = null;
	// if (entitlement != null) {
	// corporateAnnouncement =
	// securityService.getCorporateAnnouncementById(entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey());
	// entitlementDetails = entitlement.getEntitlementDetails();
	// for (ScEntitlementDetail detail : entitlementDetails) {
	// detail.setTotalHolding(detail.getTargetHolding());
	// }
	// }
	//
	// String mode = UIConstants.DISPLAY_MODE_VIEW;
	// if (from.equals(UIConstants.WORKFLOW_FROM)) {
	// flash.put(UIConstants.BREADCRUMB,
	// applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
	// } else {
	// flash.put(UIConstants.BREADCRUMB,
	// applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
	// }
	// render(batch, mode, taskId, from, corporateAnnouncement, entitlement,
	// entitlementDetails);
	// }

	public static void settlementApproval(String taskId, Long keyId, Long refKey, String from) {
		log.debug("settlementApproval. taskId: " + taskId + " keyId: " + keyId + " refKey: " + refKey + " from: " + from);

		CsTransaction transaction = accountService.getTransaction(keyId);

		BigDecimal txCharge = new BigDecimal(0);

		for (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
			BigDecimal taxAmount = chargeTrans.getTaxAmount();
			if (taxAmount == null)
				taxAmount = BigDecimal.ZERO;
			txCharge = taxAmount.add(txCharge);
			chargeTrans.setChargeNetAmount(chargeTrans.getChargeValue().add(chargeTrans.getTaxAmount()));
			chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
		}
		transaction.setTaxOfCharges(txCharge);
		/*
		 * if (transaction != null) { CsTransaction transactionOriginal =
		 * accountService
		 * .getTransactionForSettlement(transaction.getTransactionNumber());
		 * transaction
		 * .setTransactionTemplate(transactionOriginal.getTransactionTemplate
		 * ());
		 * transaction.setTransactionDate(transactionOriginal.getTransactionDate
		 * ()); // issue #1028 }
		 */

		if (transaction.getHoldingRefs() != null) {
			// // untuk settlement holdingQuantity diambil dari
			// portfolio.settleQuantity
			// CsPortfolio portfolio =
			// accountService.getPortfolio(transaction.getSettlementDate(),
			// transaction.getAccount().getCustodyAccountKey(),
			// transaction.getSecurity().getSecurityKey(),
			// transaction.getClassification().getLookupId(),
			// transaction.getHoldingRefs());
			// transaction.setHoldingQuantity(portfolio.getPortfolioQuantity());
			CsPortfolio portfolio = accountService.getPortfolio(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
			// transaction.setHoldingQuantity(portfolio.getPortfolioQuantity());
			if (portfolio != null)
				transaction.setPortfolioQuantity(portfolio.getPortfolioQuantity());
			else
				transaction.setPortfolioQuantity(BigDecimal.ZERO);
			// untuk settlement holdingQuantity diambil dari
			// dailyHolding.settledActualQuantity
			CsDailyHolding dailyHolding = accountService.getLatestDailyHolding(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getSecurityKey(), transaction.getClassification().getLookupId(), transaction.getHoldingRefs());
			dailyHolding.getSettledQuantity();
			transaction.setHoldingQuantity(dailyHolding.getActualSettledQuantity());
			transaction.setActualSettledQuantity(dailyHolding.getActualSettledQuantity());

		}

		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (transaction.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
				}
			} catch (JsonParseException e) {
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}

		ScEntitlement entitlement = securityService.getEntitlement(keyId);
		ScCorporateAnnouncement corporateAnnouncement = null;
		Set<ScEntitlementDetail> entitlementDetails = null;
		if (entitlement != null) {
			corporateAnnouncement = securityService.getCorporateAnnouncementById(entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey());
			entitlementDetails = entitlement.getEntitlementDetails();
			for (ScEntitlementDetail detail : entitlementDetails) {
				detail.setTotalHolding(detail.getTargetHolding());
			}
		}

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}

		BnAccount bankAccount = new BnAccount();
		String accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET;
		if (LookupConstants.CS_TRANSACTION_TYPE_BUY.equals(transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupId())) {
			accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT;
		}

		ScMaster security = securityService.getSecurity(transaction.getSecurity().getSecurityKey());
		//GnCustodianBank cb = accountService.getCustodianBank(transaction.getSecurity().getDepositoryCode().getLookupId(), security.getCurrency().getCurrencyCode(), accountDomain);
		String usedBy = "'"+LookupConstants.USED_BY_BUY_SELL+"'";
		GnCustodianBank cb = transactionService.getCustodianBank(security.getCurrency().getCurrencyCode(), accountDomain, usedBy);
		log.debug("cb => " + cb);
		if (cb != null) {
			bankAccount.setAccountNo(cb.getAccountNo());
			bankAccount.setBankCode(cb.getBankCode());
			bankAccount.setName(cb.getAccountName());
			bankAccount.setCurrency(cb.getCurrency());
		}

		render(transaction, bankAccount, udfs, mode, taskId, from, corporateAnnouncement, entitlement, entitlementDetails);
	}

	public static void approveSettlementEmail(Long keyId) {
		log.debug("approveSettlementEmail. keyId: " + keyId);

		try {
			transactionService.approveTransactionBatchEmail(keyId);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approveSettlement(String taskId, Long keyId, String typeTransaction) {
		log.debug("approveSettlement. taskId: " + taskId + " keyId: " + keyId + " typeTransaction: " + typeTransaction);

		Map<String, Object> result = new HashMap<String, Object>();
		if (hitApprove.contains(taskId)) {
			result.put("status", "error");
			result.put("error","error.Record has already been modified");
			renderJSON(result);
		}else{
			hitApprove.add(taskId);
			try {
				log.debug("### keyId: " + keyId);
				CsTransactionBatch batch = transactionService.approveTransactionBatch(keyId, session.get(UIConstants.SESSION_USERNAME), taskId, typeTransaction, true);
				CsTransaction transaction = null;
				String message = null;
				if (batch == null) {
					transaction = accountService.getTransaction(keyId);
					// message = Messages.get("settlement.approved",
					// transaction.getSettlementNumber(),
					// transaction.getTransactionKey());
					message = Messages.get("settlement.approved", transaction.getSettlementNumber(), transaction.getTransactionNumber());
				} else {
					message = Messages.get("batch.approved", batch.getTransactionBatchKey());
				}
				hitApprove.remove(taskId);
				renderJSON(Formatter.resultSuccess(message));
			} catch (MedallionException e) {
				hitApprove.remove(taskId);
				renderJSON(Formatter.resultError(e));
			} catch (Exception e) {
				hitApprove.remove(taskId);
				renderJSON(Formatter.resultError(e));
			}
		}
		
	}

	public static void rejectSettlement(String taskId, Long keyId) {
		log.debug("rejectSettlement. taskId: " + taskId + " keyId: " + keyId);

		try {
			log.debug("task iD >>" + taskId + " <<< KEY ID >>>> " + keyId);
			CsTransactionBatch batch = transactionService.rejectTransactionBatch(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
			CsTransaction transaction = null;
			String message = null;
			if (batch == null) {
				transaction = accountService.getTransaction(keyId);
				message = Messages.get("settlement.rejected", transaction.getSettlementNumber(), transaction.getTransactionKey());
			} else {
				message = Messages.get("batch.approved", batch.getTransactionBatchKey());
			}

			// renderText("rejected");
			renderJSON(Formatter.resultSuccess(message));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.viewSettlement")
	public static void view(long id, boolean settlement, String param, String viewFrom, boolean backSettlement) {
		log.debug("view. id: " + id + " settlement: " + settlement + " param: " + param + " viewFrom: " + viewFrom + " backSettlement: " + backSettlement);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsTransaction transaction = accountService.getTransaction(id);
		if(transaction.getTransferType() == null ) {
			transaction.setTransferType(new GnLookup());
		}
		log.debug("Transaction Template = " + transaction.getTransactionTemplate().getTransactionCode());
		System.out.println("Transaction Template = " + transaction.getTransactionTemplate().getSettlementTransaction().getTransactionCode());
		System.out.println("Transaction Template = " + transaction.getTransactionTemplate().getSettlementTransaction().getDefaultCorebanking());
		log.debug("[VIEW] transaction.dailyPortfolioFlag = " + transaction.getDailyPortfolioFlag());
		BigDecimal txCharge = new BigDecimal(0);
		for (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
			BigDecimal taxAmount = chargeTrans.getTaxAmount();
			// if (taxAmount==null) {taxAmount = BigDecimal.ZERO;
			if (taxAmount == null) {
				taxAmount = BigDecimal.ZERO;
				chargeTrans.setTaxAmount(taxAmount);
			}
			if (!Helper.isNullOrEmpty(chargeTrans.getChargeFrequency()) && !LookupConstants.CHARGE_FREQUENCY_MONTHLY.equals(LookupConstants._CHARGE_FREQUENCY + "-" + chargeTrans.getChargeFrequency())) {
				txCharge = taxAmount.add(txCharge);
			}

			chargeTrans.setChargeNetAmount((chargeTrans.getChargeValue() == null ? BigDecimal.ZERO : chargeTrans.getChargeValue()).add(chargeTrans.getTaxAmount()));
			if (chargeTrans.getChargeMaster().getTaxMaster() != null)
				chargeTrans.setTaxMaster(generalService.getTaxMaster(chargeTrans.getChargeMaster().getTaxMaster().getTaxKey()));
		}
		transaction.setTaxOfCharges(txCharge);
		Boolean dailyPortfolioFlag = null;
		if (transaction.getDailyPortfolioFlag() != null)
			dailyPortfolioFlag = transaction.getDailyPortfolioFlag();
		else
			dailyPortfolioFlag = false;
		// CsTransaction portfolioTransaction = null;
		String transactionDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());

		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		log.debug("from settlement Approval >>> udfs size = " + udfs.size());
		log.debug("from settlement Approval >>> transaction.udf = " + transaction.getUdf());
		if (transaction.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {
				});
				if (udfs != null) {
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if (udf.getLookupGroup() != null) {
							udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
							udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
						}
					}
				}
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
			}
		} else {
			for (GnUdfMaster udf : udfs) {
				if (udf.getLookupGroup() != null) {
					udf.setLookupGroup(generalService.getLookupGroup(udf.getLookupGroup().getLookupGroup()));
					udf.setOptions(generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, udf.getLookupGroup().getLookupGroup()));
				}
			}
		}

		log.debug("[view] param = " + param);
		if (transaction.getTransactionNumber() != null) {
			if (transaction.getSource().equals(MenuConstants.CS_SETTLEMENT)) {
				CsTransaction transactionOriginal = accountService.getTransactionForSettlement(transaction.getTransactionNumber());
				transaction.setTransactionTemplate(transactionOriginal.getTransactionTemplate());
				// portfolioTransaction =
				// accountService.getTransactionForSettlement(transaction.getTransactionNumber());
				transactionDate = dateFormat.format(transactionOriginal.getTransactionDate());
			}

			BnAccount bankAccount = new BnAccount();
			String currentDate = dateFormat.format(taService.getCurrentBusinessDate());
			boolean validateAccount = false;
			boolean validateThirdParty = false;
			// SETTLEMENT PREMATCH
			if (param != null && param.equals(UIConstants.PARAM_SETTLE_PREMATCH)) {
				String accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_DEBET;
				if (LookupConstants.CS_TRANSACTION_TYPE_BUY.equals(transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupId())) {
					accountDomain = LookupConstants.DOMAIN_CUSTODIAN_BANK_CREDIT;
				}
				
				//System.out.println("-"+transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupId()+"-");
				//System.out.println("-"+LookupConstants.CS_TRANSACTION_TYPE_BUY+"-");
				String usedBy = "'"+LookupConstants.USED_BY_BUY_SELL+"'";
				//GnCustodianBank cb = accountService.getCustodianBank(transaction.getSecurity().getDepositoryCode().getLookupId(), transaction.getSecurity().getCurrency().getCurrencyCode(), accountDomain);
				GnCustodianBank cb = transactionService.getCustodianBank(transaction.getSecurity().getCurrency().getCurrencyCode(), accountDomain, usedBy);
				log.debug("cb => " + cb);
				if (cb != null) {
					bankAccount.setAccountNo(cb.getAccountNo());
					bankAccount.setBankCode(cb.getBankCode());
					bankAccount.setName(cb.getAccountName());
					bankAccount.setCurrency(cb.getCurrency());
				}

				transaction.setSentToInterface(true);
				List<CsSubAccount> subAccounts = accountService.listSubAccountByAccount(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getDepositoryCode().getLookupId());
				if (!transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_NA)) {
					if (!subAccounts.isEmpty()) {
						for (CsSubAccount csSubAccount : subAccounts) {
							if ((transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) && (csSubAccount.getDepository().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST))) {
								int length = csSubAccount.getCode().length();
								if (csSubAccount.getCode().substring(length - 2, length).equals("XX")) {
									validateAccount = true;
									break;
								} else {
									validateAccount = false;
								}
							} else if ((transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)) && (csSubAccount.getDepository().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS))) {
								validateAccount = false;
							}
						}
					} else {
						validateAccount = true;
					}
				}
				// if(transaction.getAccount().getSubAccounts().isEmpty())
				// validateAccount = false;
				if ((transaction.getSettlementAgent() != null) && (transaction.getSettlementAgent().getThirdPartyKey() != null)) {
					Long counterPartyKey = transaction.getSettlementAgent().getThirdPartyKey();

					List<GnThirdPartyAccount> thirdPartyAccs = generalService.listThirdPartyAccountByDepository(counterPartyKey, transaction.getSecurity().getDepositoryCode().getLookupId());
					if (!transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_NA)) {
						if (thirdPartyAccs != null) {
							validateThirdParty = true;
							for (GnThirdPartyAccount gnThirdPartyAccount : thirdPartyAccs) {
								if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(transaction.getSecurity().getDepositoryCode().getLookupId())) {
									validateThirdParty = false;
									transaction.getSettlementAgent().setCbestAccount(gnThirdPartyAccount);
									break;
								}
							}
						} else {
							validateThirdParty = true;
						}
					}
				}

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
			}
			
			BigDecimal checkIssuerCounterPartyType = accountService.checkIssuerCounterPartyType(id);
			log.debug("checkIssuerCounterPartyType ="+checkIssuerCounterPartyType);

			setBase(transaction);
			render("Transactions/entry.html", transaction, bankAccount, udfs, mode, settlement, transactionDate, param, viewFrom, backSettlement, dailyPortfolioFlag, validateAccount, validateThirdParty, currentDate, checkIssuerCounterPartyType);
		}

		// logger.debug("[VIEW] transaction date from csPortfolioTransaction = "
		// + portfolioTransaction.getTransactionDate());
		setBase(transaction);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
		render("Transactions/entry.html", transaction, udfs, mode, settlement, transactionDate, param, viewFrom, dailyPortfolioFlag);
	}

	@Check("transaction.settlementPrematch")
	public static void settlementPrematch() {
		log.debug("settlementPrematch. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String param = UIConstants.PARAM_SETTLE_PREMATCH;
		SettlementPrematchSearchParameters params = new SettlementPrematchSearchParameters();
		Date currentDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
		render("Transactions/settlementPrematch.html", mode, param, currentDate, params);
	}

	@Check("transaction.settlementPrematch")
	public static void pagingSettlementPrematch(Paging page, SettlementPrematchSearchParameters param) {
		log.debug("pagingSettlementPrematch. page: " + page + " param: " + param);

		if (param.isQuery()) {

			page.addParams("tx.transactionNumber", page.EQUAL, param.transactionNo);
			page.addParams("tx.account.customer.customerKey", page.EQUAL, param.customerCode);
			page.addParams("tx.security.securityType.securityType", page.EQUAL, param.securityType);
			page.addParams("tx.security.securityId", page.EQUAL, param.securityId);
			page.addParams("trunc(tx.settlementDate)", page.GREATEQUAL, param.settlementDateFrom);
			page.addParams("trunc(tx.settlementDate)", page.LESSEQUAL, param.settlementDateTo);

			page.addParams("trim(tx.transactionStatus)", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
			page.addParams("1", page.EQUAL, 1);

			page.addParams(Helper.searchAll("(tx.transactionNumber||tx.account.accountNo||tx.account.name||" + "tx.transactionTemplate.description||tx.security.securityId||tx.settlementAmount||" + "to_char(tx.settlementDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

			page = transactionService.pagingSearchPrematch(page);
		}
		renderJSON(page);
	}

	@Check("transaction.settlementPrematch")
	public static void searchSettlementPrematch(SettlementPrematchSearchParameters params) {
		log.debug("searchSettlementPrematch. params: " + params);

		// List<CsTransaction> transactions =
		// transactionService.searchPrematch(params.accountNo,
		// params.securityType,
		// params.securityId,
		// params.thirdPartyCode,
		// params.settlementDateFrom
		// );
		List<CsTransaction> transactions = transactionService.searchPrematch(params.settlementDateFrom, params.settlementDateTo, params.customerCode, params.securityType, params.securityId, params.transactionNo);
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(UIConstants.));
		render("Transactions/grid_prematch.html", transactions);
	}

	/*
	 * public static void saveSettlePrematch(CsTransaction transaction,
	 * List<CsTransactionCharge> charges, List<GnUdfMaster> udfs, String param)
	 * { logger.debug("[SAVE SETTLE PREMATCH] transaction.dailyPortfolioFlag = "
	 * + transaction.getDailyPortfolioFlag()); String mode =
	 * UIConstants.DISPLAY_MODE_VIEW; param = UIConstants.PARAM_SETTLE_PREMATCH;
	 * 
	 * if (transaction != null && charges != null) {
	 * transaction.getTransactionCharges().clear();
	 * transaction.getTransactionCharges().addAll(charges); }
	 * 
	 * // TODO : CHANGE THIS try { logger.debug("udfs:" + udfs); if (udfs !=
	 * null) { Map<String, String> data = new HashMap<String, String>(); for
	 * (GnUdfMaster udf : udfs) { data.put(udf.getFieldName(), udf.getValue());
	 * } String udfString = json.writeValueAsString(data);
	 * transaction.setUdf(udfString); } } catch (IOException e) {
	 * logger.debug(e.getMessage(),e); }
	 * serializerService.serialize(session.getId(),
	 * transaction.getTransactionKey(), transaction);
	 * confirming(transaction.getTransactionKey(), false, param); }
	 */

	/*
	 * public static void saveSettlePrematch(CsTransaction transaction,
	 * List<CsTransactionCharge> charges, List<GnUdfMaster> udfs, String param)
	 * { logger.debug("[SAVE SETTLE PREMATCH] transaction.dailyPortfolioFlag = "
	 * + transaction.getDailyPortfolioFlag()); String mode =
	 * UIConstants.DISPLAY_MODE_VIEW; param = UIConstants.PARAM_SETTLE_PREMATCH;
	 * 
	 * if (transaction != null && charges != null) {
	 * transaction.getTransactionCharges().clear();
	 * transaction.getTransactionCharges().addAll(charges); }
	 * 
	 * // TODO : CHANGE THIS try { logger.debug("udfs:" + udfs); if (udfs !=
	 * null) { Map<String, String> data = new HashMap<String, String>(); for
	 * (GnUdfMaster udf : udfs) { data.put(udf.getFieldName(), udf.getValue());
	 * } String udfString = json.writeValueAsString(data);
	 * transaction.setUdf(udfString); } } catch (IOException e) {
	 * logger.debug(e.getMessage(),e); }
	 * serializerService.serialize(session.getId(),
	 * transaction.getTransactionKey(), transaction);
	 * confirming(transaction.getTransactionKey(), false, param, mode); }
	 */

	@Check("transaction.settlementPrematch")
	public static void saveSettlePrematch(CsTransaction transaction, List<CsTransactionCharge> charges, List<GnUdfMaster> udfs, String currencyCodeDisplay, String param, boolean isreload) {
		log.debug("saveSettlePrematch. transaction: " + transaction + " charges: " + charges + " udfs: " + udfs + " currencyCodeDisplay: " + currencyCodeDisplay + " param: " + param + " isreload: " + isreload);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		param = UIConstants.PARAM_SETTLE_PREMATCH;

		transaction.setAutoPrematchType(false);
		transaction.setMatchStatus(generalService.getLookup(LookupConstants.MATCH_STATUS_M));

		if (transaction != null && charges != null) {
			transaction.getTransactionCharges().clear();
			transaction.getTransactionCharges().addAll(charges);
		}

		if (transaction.getAutoPrematchType() == null)
			transaction.setAutoPrematchType(false);
		try {
			log.debug("udfs:" + udfs);
			if (udfs != null) {
				Map<String, String> data = new HashMap<String, String>();
				for (GnUdfMaster udf : udfs) {
					data.put(udf.getFieldName(), udf.getValue());
				}
				String udfString = json.writeValueAsString(data);
				transaction.setUdf(udfString);
			}
		} catch (IOException e) {
			log.debug(e.getMessage(), e);
		}
		
		String settAmountCurr = transaction.getSettlementAccount().getCurrency().getCurrencyCode();
		if(!settAmountCurr.equalsIgnoreCase(currencyCodeDisplay)){
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
			validation.addError("", "Currency for Customer's Cash Account should be ["+ currencyCodeDisplay +"]");
			
				Boolean settlement = true;
				Boolean backSettlement = false;
				String transactionDate = null;
				SimpleDateFormat dateFormat = new SimpleDateFormat(appProp.getDateFormat());
				CsTransaction transactionOriginal = accountService.getTransactionForSettlement(transaction.getTransactionNumber());
				transaction.setTransactionTemplate(transactionOriginal.getTransactionTemplate());
//				portfolioTransaction = accountService.getTransactionForSettlement(transaction.getTransactionNumber());
				transactionDate = dateFormat.format(transactionOriginal.getTransactionDate());
				
				boolean validateAccount = false;
				boolean validateThirdParty = false;
				
				List<CsSubAccount> subAccounts = accountService.listSubAccountByAccount(transaction.getAccount().getCustodyAccountKey(), transaction.getSecurity().getDepositoryCode().getLookupId());
				if (!transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_NA)) {
					if (!subAccounts.isEmpty()){
						for (CsSubAccount csSubAccount : subAccounts) {
							if ((transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) &&
								(csSubAccount.getDepository().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST))) {
								int length = csSubAccount.getCode().length();
								if (csSubAccount.getCode().substring(length-2, length).equals("XX")){
									validateAccount = true;
									break;
								} else {
									validateAccount = false;
								}
							} else if ((transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)) &&
									(csSubAccount.getDepository().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS))) {
								validateAccount = false;
							}
						}
					} else {
						validateAccount = true;
					}
				}
				
				Long counterPartyKey = transaction.getCounterParty().getThirdPartyKey();
				
				List<GnThirdPartyAccount> thirdPartyAccs = generalService.listThirdPartyAccountByDepository(counterPartyKey, transaction.getSecurity().getDepositoryCode().getLookupId());
				if (!transaction.getSecurity().getDepositoryCode().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_NA)) {
					if (thirdPartyAccs!=null){
						validateThirdParty = true;
						for (GnThirdPartyAccount gnThirdPartyAccount : thirdPartyAccs) {
							if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(transaction.getSecurity().getDepositoryCode().getLookupId())) {
								validateThirdParty = false;
								break;
							}
						}
					} else {
						validateThirdParty = true;
					}
				}
			
				
			setBase(transaction);
			String currentDate = dateFormat.format(taService.getCurrentBusinessDate());
			param = UIConstants.PARAM_SETTLE_PREMATCH;
			mode = UIConstants.DISPLAY_MODE_VIEW;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_SETTLEMENT_PREMATCH));
			render("Transactions/entry.html", transaction, udfs, mode, settlement, transactionDate, param, backSettlement, transaction.getDailyPortfolioFlag(), validateAccount, validateThirdParty, currentDate);
		}else{
			serializerService.serialize(session.getId(), transaction.getTransactionKey(), transaction);
			confirming(transaction.getTransactionKey(), false, currencyCodeDisplay, param, mode, isreload);
		}
	}

	public static void approveSettlementPrematch(String taskId, Long maintenanceLogKey, Long keyId, boolean isApproveYes) {
		log.debug("approveSettlementPrematch. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " keyId: " + keyId + " isApproveYes: " + isApproveYes);
		Map<String, Object> result = new HashMap<String, Object>();
		if (hitApprove.contains(taskId)) {
			result.put("status", "error");
			result.put("error","error.Record has already been modified");
			renderJSON(result);
		}else{
			hitApprove.add(taskId);
			try {
				// CsTransaction transaction =
				// transactionService.approveSettlementPrematch(keyId,
				// session.get(UIConstants.SESSION_USERNAME), taskId,
				// UIConstants.USER_SYSTEM, isApproveYes, false);
				Map<String, String> resultApprove = transactionService.preApproveSettlementPrematch(taskId, keyId.toString(), WorkflowConstants.PROCESS_TYPE_APPROVE, session.get(UIConstants.SESSION_USERNAME), UIConstants.USER_SYSTEM, false, maintenanceLogKey, isApproveYes);
				String message = Messages.get("settlement.prematch.approved", resultApprove.get("message")) + "\r\n" + Messages.get("settlement.success", resultApprove.get("settlement").toString(), Long.parseLong(resultApprove.get("transactionBatch")));
				if(!resultApprove.get("message2").equals("")){
					message = message + "\r\n" + resultApprove.get("message2");
				}
				hitApprove.remove(taskId);
				renderJSON(Formatter.resultSuccess(message));
			} catch (MedallionException e) {
				hitApprove.remove(taskId);
				renderJSON(Formatter.resultError(e));
			} catch (Exception e) {
				hitApprove.remove(taskId);
				renderJSON(Formatter.resultError(e));
			}
		}
		
	}

	public static void rejectSettlementPrematch(String taskId, Long maintenanceLogKey, Long keyId) {
		log.debug("rejectSettlementPrematch. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " keyId: " + keyId);

		try {
			CsTransaction transaction = transactionService.rejectSettlementPrematch(keyId, session.get(UIConstants.SESSION_USERNAME), taskId, maintenanceLogKey);

			renderJSON(Formatter.resultSuccess(Messages.get("settlement.prematch.rejected", transaction.getTransactionNumber())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void getTaxAccInt(Long custodyAccountKey, String securityType, String transactionDate) throws ParseException {
		log.debug("getTaxAccInt. custodyAccountKey: " + custodyAccountKey + " securityType: " + securityType + " transactionDate: " + transactionDate);

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date transDate = formatter.parse(transactionDate);

			TxProfRuleIntDetail detail = generalService.getTaxAccruedForTrans(custodyAccountKey, securityType, transDate);
			result.put("status", "success");
			if (detail != null) {
				result.put("taxCode", detail.getTaxMaster().getTaxCode());
			} else {
				result.put("taxCode", "");
			}
		} catch (MedallionException ex) {
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if(ex.getParams()!= null){
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			}else{
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
		}catch (Exception e) {
			result.put("status", "error");
			result.put("error",e.getMessage());
		}

		renderJSON(result);
	}

	public static void getAdjMaint(String currency) {
		log.debug("getAdjMaint. currency: " + currency);

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			GnAdjustmentDetail detail = generalService.getAdjtDtlForTrans(currency);
			result.put("status", "success");
			result.put("operator", detail.getOperator().getLookupCode());
			result.put("value", detail.getValue());
		} catch (MedallionException ex) {
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if(ex.getParams()!= null){
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			}else{
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
		}catch (Exception e) {
			result.put("status", "error");
			result.put("error",e.getMessage());
		}

		renderJSON(result);
	}

	public static void getCouponDates(long securityKey, Date settlementDate) {
		log.debug("getCouponDates. securityKey: " + securityKey + " settlementDate: " + settlementDate);

		ScCouponSchedule schedule = securityService.getCouponScheduleOn(securityKey, settlementDate);
		if (schedule == null) {
			ScMaster security = securityService.getSecurity(securityKey);
			if (LookupConstants.SECURITY_CLASS_FIXED_INCOME.equals(security.getSecurityType().getSecurityClass().getLookupId())) {
				if (settlementDate.compareTo(security.getIssueDate()) <= 0) {
					ScCouponSchedule couponSchedule = securityService.getCouponScheduleSecurity(security.getSecurityKey(), 1);
					if (couponSchedule != null) {
						schedule = new ScCouponSchedule();
						BeanUtils.copyProperties(couponSchedule, schedule);
					}
				}

				if (settlementDate.compareTo(security.getMaturityDate()) >= 0) {
					ScCouponSchedule couponSchedule = securityService.getScScheduleMaxLastPaymentDate(security.getSecurityKey());
					if (couponSchedule != null) {
						schedule = new ScCouponSchedule();
						BeanUtils.copyProperties(couponSchedule, schedule);
					}
				}
			}
		}
		renderJSON(schedule, new CouponSchedulePickSerializer());
	}

	public static void getCouponDatesOnly(long securityKey, Date settlementDate) {
		log.debug("getCouponDatesOnly. securityKey: " + securityKey + " settlementDate: " + settlementDate);

		ScCouponSchedule schedule = null;
		if (settlementDate != null) {
			schedule = securityService.getCouponScheduleOn(securityKey, settlementDate);
		}
		renderJSON(schedule, new CouponSchedulePickSerializer());
	}

	public static void caSettlementEntry() {
		log.debug("caSettlementEntry. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		String param = UIConstants.PARAM_CA_SETTLE;
		Date currentDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		String newDate = Registry.fmtMMDDYYYY(currentDate);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CA_SETTLEMENT_ENTRY));
		CorpActionSettlementSearchParameters params = new CorpActionSettlementSearchParameters();
		params.distributionDateSearch = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		log.debug("###>.." + params.distributionDateSearch);
		render("Transactions/caSettlementEntry.html", params, mode, param, newDate);
	}

	public static void searchCaSettlement(CorpActionSettlementSearchParameters params) {
		log.debug("searchCaSettlement. params: " + params);

		List<ScEntitlement> scEntitlements = transactionService.searchCorpActionSettlement(params.entitlementNo, params.announcementCode, params.securityType, params.securityId, params.distributionDateSearch);
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(UIConstants.));
		render("Transactions/caSettlementGrid.html", scEntitlements);
	}

	public static void viewSettlementEntry(Long keyId, String from) {
		log.debug("viewSettlementEntry. keyId: " + keyId + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(keyId);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CA_SETTLEMENT_ENTRY));
		render("Entitlements/approval.html", corporateAnnouncement, mode, from);
	}

	public static void settleCorporateAction(Long corporateAnnouncementKey, Long entitlementKey, String from) {
		log.debug("settleCorporateAction. corporateAnnouncementKey: " + corporateAnnouncementKey + " entitlementKey: " + entitlementKey + " from: " + from);

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			transactionService.approveCorpActionSettlement(corporateAnnouncementKey, entitlementKey, session.get(UIConstants.SESSION_USERNAME));
			result.put("status", "success");
			String message = Messages.get("settlement.prematch.approved");
			result.put("message", message);
		} catch (MedallionException ex) {
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if(ex.getParams()!= null){
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			}else{
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
		}catch (Exception e) {
			result.put("status", "error");
			result.put("error",e.getMessage());
		}
		renderJSON(result);
	}

	public static void checkComplianceSyariah(Long custodyAccountKey, Long securityKey) {
		log.debug("checkComplianceSyariah, custodyAccountKey = "+custodyAccountKey+", securityKey = "+securityKey);
		
		Map<String,Object> result = new HashMap<String, Object>();
		Boolean valid = generalService.checkComplianceTransaction(custodyAccountKey, securityKey);
		String res = (valid)?"1":"0";
		result.put("valid", res);
		renderJSON(result);
	}
	
	public static void validateHolidaysSettlementDate() {
		log.debug("validateHolidaysSettlementDate. ");
	}

	public static void getCashAccountAmountType(Long transactionTemplateKey) {
		log.debug("getCashAccountAmountType. transactionTemplateKey: " + transactionTemplateKey);

		Map<String, Object> result = new HashMap<String, Object>();
		String amountType = transactionService.getCashAccountAmountType(transactionTemplateKey);
		result.put("amounttype", amountType);
		renderJSON(result);
	}

	private static void setBase(CsTransaction transaction) {
		log.debug("setBase. transaction: " + transaction);

		ScMaster security = securityService.getSecurity(transaction.getSecurity().getSecurityKey());
		if (security.getAccrualBase() != null) {
			transaction.getSecurity().setAccrualBase(security.getAccrualBase());
		}
		if (security.getYearBase() != null) {
			transaction.getSecurity().setYearBase(security.getYearBase());
		}
	}

	public static void getProcessManualCouponAdjustment(Long securityKey, String securityClass, String accruedInterest, String settlementDate) {
		log.debug("getProcessManualCouponAdjustment. securityKey: " + securityKey + " securityClass: " + securityClass + " accruedInterest: " + accruedInterest + " settlementDate: " + settlementDate);

		Date psettlementDate = Helper.parseDate(settlementDate, appProp.getDateFormat());
		Boolean askForManually = transactionService.getProcessManualCouponAdjustment(securityKey, securityClass, accruedInterest, psettlementDate);
		renderJSON(askForManually);
	}
	
	public static void getCounterPartyType(Long settlementAgentKey){
		
		log.debug("settlementAgentKey: " + settlementAgentKey);
		Map<String, Object> result = new HashMap<String, Object>();
		GnThirdPartyAccount gnThirdPartyAccount = transactionService.getGnThirdPartyAccountByThirdPartyKeyAndDepository(settlementAgentKey, LookupConstants.DEPOSITORY_CODE_KSEI_CBEST);
		result.put("accountCode", gnThirdPartyAccount.getAccountCode());
		result.put("accountAgentCode", gnThirdPartyAccount.getAccountAgentCode());
		renderJSON(result);
	}
	
	public static void getBankInfoCustodian(Long accountNoKey, Long securityKey, Boolean isCustodianCredit, String currrencyCode){
		Map<String, String> result = new HashMap<String, String>();
		if(currrencyCode == null || (currrencyCode != null && currrencyCode.trim().equals(""))){
			currrencyCode = "IDR";
		}
		log.debug("accountNoKey = "+accountNoKey+" securityKey = "+securityKey+" isCustodianCredit = "+isCustodianCredit+", currrencyCode"+currrencyCode);
		result = transactionService.getBankInfoCustodian2(currrencyCode, null, LookupConstants.TRANSFER_TYPE_POOL, isCustodianCredit);
		renderJSON(result);
	}
	
	public static void getSwiftStatus2(Long transactionKey) {
		try{
			String result = swiftService.getSwiftStatus2(transactionKey);
			renderJSON(result);
		}catch(Exception e) { renderJSON("ERROR"); } 
	}
}