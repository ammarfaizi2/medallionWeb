package controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.BeanUtils;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.AbstractCsCtpTransaction;
import com.simian.medallion.model.CsCtpTransaction;
import com.simian.medallion.model.CsSubAccount;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.GnCurrencyProfile;
import com.simian.medallion.model.GnCustodianAccount;
import com.simian.medallion.model.GnCustodianInfo;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.CsCtpTransactionParam;
import com.simian.medallion.vo.SelectItem;

import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.CouponSchedulePickSerializer;
import play.i18n.Messages;
import play.mvc.Before;
import vo.CtpTransactionSearchParameters;

public class CtpTransaction extends MedallionController {
	
	private static Logger logger = Logger.getLogger(CtpTransaction.class);
	
	private static JsonHelper jsonCsCtpTransaction = new JsonHelper().getCsCtpTransactionSerializer();
	private static JsonHelper jsonCsTransaction = new JsonHelper().getCsCtpTransactionSerializer();
	
	@Before
	public static void setup() {	
		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(
															UIConstants.SIMIAN_BANK_ID,
															LookupConstants._CLASSIFICATION));
		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
		
		renderArgs.put("ctpReports", UIHelper.ctpReports());
		
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
		renderArgs.put("depositoryNA", LookupConstants.DEPOSITORY_CODE_NA);
		renderArgs.put("depositoryEuro", LookupConstants.DEPOSITORY_CODE_EUROCLEARS);
		
		renderArgs.put("EXCH", LookupConstants.SETTLEMENT_PURPOSE_EXCH);
		renderArgs.put("NONEXCHG", LookupConstants.SETTLEMENT_PURPOSE_NONEXCHG);
		renderArgs.put("OTHR", LookupConstants.SETTLEMENT_REASON_OTHR);
		
		renderArgs.put("INT_INPUT", AbstractCsCtpTransaction.CTP_REPORT_INPUT);
		renderArgs.put("INT_CONFIRM", AbstractCsCtpTransaction.CTP_REPORT_CONFIRM);
		
		renderArgs.put("INPUT", AbstractCsCtpTransaction.CTP_REPORT_OPERATOR_INPUT);
		renderArgs.put("CONFIRM", AbstractCsCtpTransaction.CTP_REPORT_OPERATOR_CONFIRM);
		
		renderArgs.put("CTP_REPORT_TYPE_ONE", LookupConstants._CTP_REPORT_TYPE_ONE);
		renderArgs.put("CTP_REPORT_TYPE_TWO", LookupConstants._CTP_REPORT_TYPE_TWO);
		
		renderArgs.put("CTP_REPORT_LATE_TRANSACTION_TYPE_O", LookupConstants._CTP_REPORT_LATE_TRANSACTION_TYPE_O);
		renderArgs.put("CTP_REPORT_LATE_TRANSACTION_TYPE_T", LookupConstants._CTP_REPORT_LATE_TRANSACTION_TYPE_T);
		
		List<SelectItem> prematchStatus = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PREMATCH_STATUS);
		renderArgs.put("prematchStatus", prematchStatus);
		
		List<SelectItem> reportTypes = generalService.listLookupsForDropDownAsSelectItemCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._CTP_REPORT_TYPE);
		renderArgs.put("reportTypes", reportTypes);
		
		List<SelectItem> transactionTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CTP_REPORT_LATE_TRANSACTION_TYPE);
		renderArgs.put("transactionTypes", transactionTypes);
		
		List<SelectItem> lateReasonTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CTP_REPORT_LATE_REASON_TYPE);
		renderArgs.put("lateReasonTypes", lateReasonTypes);

		List<SelectItem> matchStatus = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._MATCH_STATUS);
		renderArgs.put("matchStatus", matchStatus);

		List<SelectItem> settlementPurpose = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants._SETTLEMENT_PURPOSE);
		renderArgs.put("settlementPurpose", settlementPurpose);

		List<SelectItem> transferMethod = generalService.listLookupsForDropDownAsSelectItemWithoutCode(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("transferMethod", transferMethod);

		renderArgs.put("transferMethodManual", LookupConstants.FUND_TRANSFER_METHOD_MANUAL);
		renderArgs.put("transferMethodFile", LookupConstants.FUND_TRANSFER_METHOD_FILE);
		renderArgs.put("transferMethodInterface", LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE);
		renderArgs.put("cbestOtb", LookupConstants.CBEST_MESSAGE_TYPE_OTB);

		GnSystemParameter cbestEnabled = generalService.getSystemParameter(SystemParamConstants.CBEST_ENABLED);
		renderArgs.put("cbestEnabled", (cbestEnabled != null)? cbestEnabled.getValue() : null);

		GnSystemParameter paramSwift = generalService.getSystemParameter(SystemParamConstants.PARAM_USING_SWIFT);
		renderArgs.put("paramSwift", (paramSwift != null)? paramSwift.getValue() : null);
	}
	
	@Check("transaction.ctpTransaction")
	public static void list() {
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CtpTransactionSearchParameters params = new CtpTransactionSearchParameters();
		params.setCtpReport(new Integer(0));
//		params.transactionDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CTP_TRANSACTION));
		render("CtpTransaction/list.html", params, mode);
	}
	
	@Check("transaction.ctpTransaction")
	public static void searchCtpTransaction(CtpTransactionSearchParameters params) {
		List<CsTransaction> csCancelTrades = accountService.searchCtpTransaction(params.getCustomerCodeSearchId(),
																					 params.getSecurityType(),
																					 params.getSecurityId(),
																					 params.getTransactionDateFrom(), 
																					 params.getTransactionDateTo(),
																					 params.getSettlementDateFrom(), 
																					 params.getSettlementDateTo());

		render("CtpTransaction/grid.html", csCancelTrades);
	}
	
	@Check("transaction.ctpTransaction")
	public static void pagingCtpTransaction(Paging page, CtpTransactionSearchParameters param) {
		if(param.isQuery()){
			
			page.addParams("a.customer_key", page.EQUAL, param.getCustomerCodeSearchId());
			page.addParams("a.security_type", page.EQUAL, param.getSecurityType());
			page.addParams("a.security_id", page.EQUAL, param.getSecurityId());
			page.addParams("a.transaction_date", page.GREATEQUAL, param.getTransactionDateFrom());
			page.addParams("a.transaction_date", page.LESSEQUAL, param.getTransactionDateTo());
			page.addParams("a.settlement_date", page.GREATEQUAL, param.getSettlementDateFrom());
			page.addParams("a.settlement_date", page.LESSEQUAL, param.getSettlementDateTo());
			page.addParams("trim(a.transaction_status)", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
			page.addParams("a.transaction_category", page.EQUAL, LookupConstants.TRANSACTION_CATEGORY_TRANSACTION);
			page.addParams("a.used_by", page.EQUAL, LookupConstants.USED_BY_BUY_SELL);
			page.addParams("1", page.EQUAL, 1);
			
			param.setCtpReport(new Integer(-1));
			
			if (AbstractCsCtpTransaction.CTP_REPORT_OPERATOR_INPUT.equals(param.getCtpReportOperator())) {
				param.setCtpReport(new Integer(0));
			} else if (AbstractCsCtpTransaction.CTP_REPORT_OPERATOR_CONFIRM.equals(param.getCtpReportOperator())) {
				param.setCtpReport(new Integer(1));
			} 
			
			page.addParams(Helper.searchAll("(a.transaction_number||a.account_no||a.name||" +
					"a.description||a.security_id||a.settlement_amount||" +
					"to_char(a.transaction_date,'"+Helper.dateOracle(appProp.getDateFormat())+"') || to_char(a.settlement_date,'"+Helper.dateOracle(appProp.getDateFormat())+"') )"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			
			page = accountService.pagingCtpTransaction(page, param.getCtpReport());
			
		}
		renderJSON(page);
	}
	
	@Check("transaction.ctpTransaction")
	public static void view(Long id, String fromView) {}
	
	@Check("transaction.ctpTransaction")
	public static void save(CsCtpTransaction ctpTransaction, CsTransaction transaction, String mode, String status) {
		//Date applicationDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		mode = UIConstants.DISPLAY_MODE_EDIT;
		
		if(ctpTransaction != null){
			
			// Validasi di sini ---------------------------------
			//
			// --------------------------------------------------
			
			if (validation.hasErrors()){
				logger.debug("[SAVE] masuk sini!!");
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CTP_TRANSACTION));
				render("CtpTransaction/detail.html", ctpTransaction, mode, status);
			} else {
				Long id = ctpTransaction.getSeqCtpId();
				serializerService.serialize(session.getId(), id, ctpTransaction);
				
				confirming(id, transaction, mode, status);
			}
		} else {
			
			entry(transaction.getTransactionKey());
		}
		
	}
	
	@Check("transaction.ctpTransaction")
	public static void confirming(Long id, CsTransaction csTransaction, String mode, String status) {
		boolean confirming = true;
		renderArgs.put("confirming", true);
		
		CsCtpTransaction ctpTransaction = serializerService.deserialize(session.getId(), id, CsCtpTransaction.class);
		
		CsTransaction transaction = accountService.getTransaction(ctpTransaction.getTransaction().getTransactionKey());
		logger.debug("[EDIT CTP TRANSACTION] transaction = "+transaction);
		
		String strCsTransaction = null;
		try {
			strCsTransaction = jsonCsTransaction.serialize(csTransaction);
			strCsTransaction = strCsTransaction.replaceAll("\'", "&#39;");
		} catch (JsonGenerationException ex) {
			logger.error("json.serialize", ex);
		} catch (JsonMappingException ex) {
			logger.error("json.serialize", ex);
		} catch (IOException ex) {
			logger.error("json.serialize", ex);
		}
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CTP_TRANSACTION));
		render("CtpTransaction/entry.html", ctpTransaction, transaction, strCsTransaction, mode, confirming);
	}
	
	@Check("transaction.ctpTransaction")
	public static void confirm(CsTransaction transaction, CsCtpTransaction ctpTransaction, String mode) {
		
		//String mode = UIConstants.DISPLAY_MODE_EDIT;
		boolean confirming = true;
		try {
			
			logger.debug("[CONFIRM] ctpTransaction = " + ctpTransaction);
			
			GnLookup transactionType = transaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType();
			
			CsTransaction newTransaction = new CsTransaction();
			newTransaction.setTransactionKey(transaction.getTransactionKey());
			newTransaction.setInhouseReference(transaction.getInhouseReference());
			newTransaction.setTransactionNumber(transaction.getTransactionNumber());
			
			ctpTransaction.setTransaction(newTransaction);
			
			CsCtpTransaction csCtpTransaction = transactionService.saveCsCtpTransaction(MenuConstants.CS_CTP_TRANSACTION, ctpTransaction, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY), "");
			logger.debug("Success save csCtpTransaction = "+csCtpTransaction);
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CTP_TRANSACTION));
			mode = UIConstants.DISPLAY_MODE_CONFIRM;
			
			list();
		} catch (Exception ex) {
			
			flash.error("Transaction NO : ' " +transaction.getTransactionNumber()+" ' "+ Messages.get(ex.getMessage()));
			renderArgs.put("confirming", true);
			
			String strCtpTransaction = null;
			try {
				strCtpTransaction = jsonCsCtpTransaction.serialize(transaction.getCsCtpTransaction());
				strCtpTransaction = strCtpTransaction.replaceAll("\'", "&#39;");//handle jika ada error single quote (#3554)
			} catch (JsonGenerationException ex1) {
				logger.error("json.serialize", ex1);
			} catch (JsonMappingException ex1) {
				logger.error("json.serialize", ex1);
			} catch (IOException ex1) {
				logger.error("json.serialize", ex1);
			}
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CTP_TRANSACTION));
			render("CtpTransaction/entry.html", transaction, strCtpTransaction, mode);
		}
	}
	
	@Check("transaction.ctpTransaction")
	public static void back(Long id, CsTransaction transaction, String mode, String fromView) throws JsonParseException, JsonMappingException, IOException {
		logger.debug("BACK AREA with id = " + id + ";  fromView=" + fromView + " mode = " + mode);
		CsCtpTransaction ctpTransaction = serializerService.deserialize(session.getId(), id, CsCtpTransaction.class);
		logger.debug("ctpTransaction.getTransaction() = "+ctpTransaction);
		if (ctpTransaction != null) {
			transaction = accountService.getTransaction(ctpTransaction.getTransaction().getTransactionKey());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CTP_TRANSACTION));
			render("CtpTransaction/entry.html", ctpTransaction, transaction, mode, fromView);
		} else {
			list();
		}
	}
	
	@Check("transaction.ctpTransaction")
	public static void getCouponDates(long securityKey, Date settlementDate) {
		logger.debug("[GET COUPON DATES] securityKey = " + securityKey + " settlementDate = " + settlementDate);
		ScCouponSchedule schedule = securityService.getCouponScheduleOn(securityKey, settlementDate);
		renderJSON(schedule, new CouponSchedulePickSerializer());
	}
	
	public static void entry(Long transactionKey){
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsCtpTransaction ctpTransaction = new CsCtpTransaction();
		
		CsCtpTransactionParam param = new CsCtpTransactionParam();
		param.setTransactionKey(transactionKey);
		List<CsCtpTransaction> ctps = new ArrayList<CsCtpTransaction>(transactionService.getCtpTransactionPopulate(param));
		logger.debug("ctps size = "+ctps.size());
		if (ctps.size() > 0) {
			CsCtpTransaction csCtpTransaction = (CsCtpTransaction)ctps.get(0);			
			BeanUtils.copyProperties(csCtpTransaction, ctpTransaction);
			logger.debug("csCtpTransaction.getTransactionNumberReceiver() = "+ ctpTransaction.getTransactionNumberReceiver());
		}
		
		CsTransaction transaction = accountService.getTransaction(transactionKey);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CTP_TRANSACTION));
		render("CtpTransaction/entry.html", ctpTransaction, mode, transaction);
	}
	
	public static void edit(Long id, Long transactionKey){
		logger.debug("[EDIT CTP TRANSACTION] id = " + id);
		logger.debug("[EDIT CTP TRANSACTION] key = " + transactionKey);
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		
		CsCtpTransaction oldCtpTransaction = null;
		if(id != null) {
			oldCtpTransaction = transactionService.getCsCtpTransaction(id);
		} else {
			mode = UIConstants.DISPLAY_MODE_ENTRY;
		}
		
		CsTransaction transaction = accountService.getTransaction(transactionKey);
		logger.debug("[EDIT CTP TRANSACTION] transaction = "+transaction);
		
		CsCtpTransactionParam param = new CsCtpTransactionParam();
		param.setTransactionKey(transactionKey);
		List<CsCtpTransaction> ctps = new ArrayList<CsCtpTransaction>(transactionService.getCtpTransactionPopulate(param));
		if (ctps.size() > 0) {
			CsCtpTransaction csCtpTransaction = (CsCtpTransaction)ctps.get(0);
			if(oldCtpTransaction == null) {
				oldCtpTransaction = new CsCtpTransaction();
				BeanUtils.copyProperties(csCtpTransaction, oldCtpTransaction);
			} else {
				oldCtpTransaction.setTransaction(csCtpTransaction.getTransaction());
				oldCtpTransaction.setCtp(csCtpTransaction.getCtp());
				if (oldCtpTransaction.getTransactionNumberDeliverer() == null || oldCtpTransaction.getTransactionNumberDeliverer().trim() == "")
					oldCtpTransaction.setTransactionNumberDeliverer(csCtpTransaction.getTransactionNumberDeliverer());
				if (oldCtpTransaction.getTransactionNumberReceiver() == null || oldCtpTransaction.getTransactionNumberReceiver().trim() == "")
					oldCtpTransaction.setTransactionNumberReceiver(csCtpTransaction.getTransactionNumberReceiver());
				oldCtpTransaction.setBroker(csCtpTransaction.getBroker());
				oldCtpTransaction.setCounterPartyDeliverer(csCtpTransaction.getCounterPartyDeliverer());
				oldCtpTransaction.setCounterPartyReceiver(csCtpTransaction.getCounterPartyReceiver());
				if (oldCtpTransaction.getReferenceDeliver() == null) 
					oldCtpTransaction.setReferenceDeliver(csCtpTransaction.getReferenceDeliver());
				if (oldCtpTransaction.getReferenceReceiver() == null) 
					oldCtpTransaction.setReferenceReceiver(csCtpTransaction.getReferenceReceiver());
				if (oldCtpTransaction.getTransactingPartiesCodeDeliverer() == null) 
					oldCtpTransaction.setTransactingPartiesCodeDeliverer(csCtpTransaction.getTransactingPartiesCodeDeliverer());
				if (oldCtpTransaction.getTransactingPartiesCodeReceiver() == null) 
					oldCtpTransaction.setTransactingPartiesCodeReceiver(csCtpTransaction.getTransactingPartiesCodeReceiver());
				if (oldCtpTransaction.getTransactingPartiesNameDeliverer() == null) 
					oldCtpTransaction.setTransactingPartiesNameDeliverer(csCtpTransaction.getTransactingPartiesNameDeliverer());
				if (oldCtpTransaction.getTransactingPartiesNameReceiver() == null) 
					oldCtpTransaction.setTransactingPartiesNameReceiver(csCtpTransaction.getTransactingPartiesNameReceiver());
				oldCtpTransaction.setInHouseReference(csCtpTransaction.isInHouseReference());
				oldCtpTransaction.setPrice(csCtpTransaction.getPrice());
				oldCtpTransaction.setYield(csCtpTransaction.getYield());
				oldCtpTransaction.setNetAmount(csCtpTransaction.getNetAmount());
				oldCtpTransaction.setTradeDate(csCtpTransaction.getTradeDate());
				oldCtpTransaction.setTradeTime(csCtpTransaction.getTradeTime());
				oldCtpTransaction.setTransactionTypeCode(csCtpTransaction.getTransactionTypeCode());
				oldCtpTransaction.setSecurityCode(csCtpTransaction.getSecurityCode());
				oldCtpTransaction.setSettlementDate(csCtpTransaction.getSettlementDate());
				oldCtpTransaction.setTransactionRefDate(csCtpTransaction.getTransactionRefDate());
				oldCtpTransaction.setSettlementRefDate(csCtpTransaction.getSettlementRefDate());
			}
		}
		
		CsCtpTransaction ctpTransaction = new CsCtpTransaction();
		BeanUtils.copyProperties(oldCtpTransaction, ctpTransaction);
		
		logger.debug("[EDIT CTP TRANSACTION] ctpTransaction = "+ctpTransaction);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CTP_TRANSACTION));
		render("CtpTransaction/entry.html", transaction, ctpTransaction, mode);
	}
	
	public static void charges(){}
	
}
