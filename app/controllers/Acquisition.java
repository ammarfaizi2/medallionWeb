package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.AcquisitionSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CsDailyHolding;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.CsTransactionCharge;
import com.simian.medallion.model.CsTransactionTemplate;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.TxAcquisition;
import com.simian.medallion.model.TxAcquisitionDetail;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;


@With(Secure.class)
public class Acquisition extends MedallionController{
	public static Logger log = Logger.getLogger(PostingMF.class);
	
	@Before
	public static void setup() {	
		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(
															UIConstants.SIMIAN_BANK_ID,
															LookupConstants._CLASSIFICATION));
		renderArgs.put("depositoryCodes", generalService.listLookupsForDropDownAsSelectItem(
															UIConstants.SIMIAN_BANK_ID,
															LookupConstants._DEPOSITORY_CODE));
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

		renderArgs.put("transferMethodManual", LookupConstants.FUND_TRANSFER_METHOD_MANUAL);
		renderArgs.put("transferMethodFile", LookupConstants.FUND_TRANSFER_METHOD_FILE);
		renderArgs.put("transferMethodInterface", LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE);
		renderArgs.put("cbestOtb", LookupConstants.CBEST_MESSAGE_TYPE_OTB);

		GnSystemParameter cbestEnabled = generalService.getSystemParameter(SystemParamConstants.CBEST_ENABLED);
		renderArgs.put("cbestEnabled", (cbestEnabled != null)? cbestEnabled.getValue() : null);
	}
	
	public static void pagingAcquisition(Paging page, AcquisitionSearchParameters param) {
		if (param.isQuery()){
			//page.addParams("a.transactionDate", page.NOTEQUAL, Helper.parseDate(param.getFromDate(), "dd/MM/yyyy"));
			page.addParams("a.transactionDate", page.GREATEQUAL, Helper.parseDate(param.getFromDate(), "dd/MM/yyyy"));
			page.addParams("a.transactionDate", page.LESSEQUAL, Helper.parseDate(param.getToDate(), "dd/MM/yyyy"));
			page.addParams("a.account.customer.customerKey", page.EQUAL, param.getCustomerKey());
			page.addParams("a.security.securityType.securityType", page.EQUAL, param.getSecurityType());
			page.addParams("a.security.securityKey", page.EQUAL, param.getSecurityKey());
			page.addParams(Helper.searchAll("(to_char(a.transactionDate,'DD/MM/YYYY')||'_'||a.transactionNumber||'_'||a.account.accountNo||'_'||a.account.name||'_'||a.security.securityId||'_'||a.quantity)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			page = transactionService.pagingAcquisition(page);
		}
		renderJSON(page);
	}

	@Check("transaction.acquisition")
	public static void list() {
		render("Acquisition/list.html");
	}
	
	@Check("transaction.acquisition")
	public static void entry(String mode) {}
	
	@Check("transaction.acquisition")
	public static void edit(Long id) {
		log.debug("edit id="+id);
		
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsTransaction transaction = getCsTransaction(id);
		
		// Acquisition Block
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_ACQUISITION));
		
		String recordStatus = "";
		TxAcquisition acquisition = transactionService.getAcquisitionByTransactionKey(transaction.getTransactionKey());
		if (acquisition == null) {
			acquisition = new TxAcquisition();
			acquisition.setAcquisitionKey(-1L);
			acquisition.setTransaction(new CsTransaction());
			acquisition.getTransaction().setTransactionKey(transaction.getTransactionKey());
		}else{
			recordStatus = acquisition.getRecordStatus().trim();
			if (LookupConstants.__RECORD_STATUS_NEW.equals(acquisition.getRecordStatus().trim()) || LookupConstants.__RECORD_STATUS_UPDATED.equals(acquisition.getRecordStatus().trim())) {
				mode = UIConstants.DISPLAY_MODE_VIEW;
			}else{
				mode = UIConstants.DISPLAY_MODE_EDIT;
			}
		}
		BigDecimal priceUnit = transactionService.getPriceUnit(transaction.getSecurity().getSecurityType().getSecurityType());
		acquisition.setPriceUnit(priceUnit);
		
		try{
			acquisition.setJson((new JsonHelper()).serialize(acquisition));
		}catch(Exception e) { e.printStackTrace(); }
		
		render("Acquisition/entry.html", transaction, acquisition, mode, recordStatus);
	}
	
	@Check("transaction.acquisition")
	public static void view(String mode) {}
	
	@Check("transaction.acquisition")
	public static void save(TxAcquisition acquisition, String mode) {
		log.debug("save acquisition="+acquisition+", mode="+mode);
		
		//cleanup TxAcquisitionDetail
		List<TxAcquisitionDetail> newAcquisition = new ArrayList<TxAcquisitionDetail>();
		for (TxAcquisitionDetail detail : acquisition.getDetails()) {
			if (detail != null) { newAcquisition.add(detail); }
		}
		acquisition.setDetails(newAcquisition);
		
		try{
			String json = (new JsonHelper()).serialize(acquisition);
			log.error("============= "+json);
		}catch(Exception ee) {  }
		
		log.debug("AcquisitionKey "+acquisition.getAcquisitionKey());
		log.debug("TransactionKey "+acquisition.getTransaction().getTransactionKey());
		
		for (TxAcquisitionDetail detail : acquisition.getDetails()) {
			log.debug("AcquisitionDate "+detail.getAcquisitionDate());
			log.debug("Quantity "+detail.getQuantity());
			log.debug("Price "+detail.getPrice());
		}
		
		CsTransaction transaction = getCsTransaction(acquisition.getTransaction().getTransactionKey());
		
		// Validation
		
		CsTransaction cekTrxCancel=null;
		if (transaction.getTransactionKey()!=null)
			cekTrxCancel = transactionService.getTransactionByCancelledTransactionKey(transaction.getTransactionKey());
		
		if(cekTrxCancel != null){				
			validation.addError("",  Messages.get("transaction.prematch.cancel.reject"), "");
		}
		
		int ctr = 1;
		BigDecimal totalQuantity = new BigDecimal(0);
		for (TxAcquisitionDetail detail : acquisition.getDetails()) {
			validation.required("Acquisition Date ["+ctr+"]", detail.getAcquisitionDate());
			validation.required("Quantity ["+ctr+"]", detail.getQuantity());
			validation.required("Price ["+ctr+"]", detail.getPrice());
			
			if (Helper.isGreaterYMD(detail.getAcquisitionDate(), transaction.getSettlementDate())) {
				validation.addError("", "Acquisition Date ["+ctr+"], Must not greater then Settlement Date", "");
			}
			
			if (detail.getQuantity().doubleValue() > transaction.getQuantity().doubleValue()) {
				validation.addError("", "Quantity ["+ctr+"], Must not greater then Transaction Quantity", "");
			}
			
			totalQuantity = totalQuantity.add(detail.getQuantity());
			ctr++;
		}
		
		if (acquisition.getDetails().isEmpty()) {
			if (acquisition.getAcquisitionKey() == -1L) {
				validation.addError("","data no change", "");
			}	
		}else{
			String strTotalQuantity = Helper.format(totalQuantity, true);
			String strTransQuantity = Helper.format(transaction.getQuantity(), true);
			if (totalQuantity.doubleValue() != transaction.getQuantity().doubleValue()) {
				validation.addError("", "Total Acquisition quantity must be the same with Transacion quantity ["+strTotalQuantity+", "+strTransQuantity+"]", "");
			}
		}
		
		try{
			acquisition.setJson((new JsonHelper()).serialize(acquisition));
		}catch(Exception e) { e.printStackTrace(); }

		if (validation.hasErrors()) {
			render("Acquisition/entry.html", transaction, acquisition, mode);
		}else{
			Long id = acquisition.getAcquisitionKey();
			serializerService.serialize(session.getId(), id, acquisition);
			confirming(id, mode);
		}
	}
	
	@Check("transaction.acquisition")
	public static void confirming(Long id, String mode) {
		Boolean confirming = new Boolean(true);
		TxAcquisition acquisition = serializerService.deserialize(session.getId(), id, TxAcquisition.class);
		CsTransaction transaction = getCsTransaction(acquisition.getTransaction().getTransactionKey());
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_ACQUISITION));
		render("Acquisition/entry.html", transaction, acquisition, mode, confirming);		
	}
	
	@Check("transaction.acquisition")
	public static void confirm(Long id, String mode) {
		Boolean confirmSuccess = new Boolean(false);
		
		try {			
			TxAcquisition acquisition = serializerService.deserialize(session.getId(), id, TxAcquisition.class);
			acquisition = transactionService.saveAcquisition(MenuConstants.TX_ACQUISITION, acquisition, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			confirmSuccess = new Boolean(true);
		}catch (MedallionException e) {
			confirmSuccess = new Boolean(false);
			log.error(e.getMessage(), e);
			validation.addError("", "Aquisition. "+Messages.get(e.getMessage()), "");
		}catch(Exception e) {
			confirmSuccess = new Boolean(false);
			log.error(e.getMessage(), e);
			validation.addError("", "Aquisition "+e.getMessage(), "");
		}
		
		Boolean confirming = new Boolean(true);
		TxAcquisition acquisition = serializerService.deserialize(session.getId(), id, TxAcquisition.class);
		CsTransaction transaction = getCsTransaction(acquisition.getTransaction().getTransactionKey());
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_ACQUISITION));
		render("Acquisition/entry.html", transaction, acquisition, mode, confirming, confirmSuccess);
	}
	
	@Check("transaction.acquisition")
	public static void back(Long id, String mode) {
		TxAcquisition acquisition = serializerService.deserialize(session.getId(), id, TxAcquisition.class);
		CsTransaction transaction = getCsTransaction(acquisition.getTransaction().getTransactionKey());
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_ACQUISITION));
		render("Acquisition/entry.html", transaction, acquisition, mode);
	}
	
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			TxAcquisition acquisition = json.readValue(maintenanceLog.getNewData(), TxAcquisition.class);
			CsTransaction transaction = getCsTransaction(acquisition.getTransaction().getTransactionKey());
			
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			
			try{
				acquisition.setJson((new JsonHelper()).serialize(acquisition));
			}catch(Exception e) { e.printStackTrace(); }
			
			BigDecimal priceUnit = transactionService.getPriceUnit(transaction.getSecurity().getSecurityType().getSecurityType());
			acquisition.setPriceUnit(priceUnit);
			
			render("Acquisition/entry.html", transaction, acquisition, mode, taskId, maintenanceLogKey, from, operation);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		transactionService.approveAcquisition(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
	}
	
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		transactionService.approveAcquisition(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);
	}
	
	public static void cstransaction(Long id, String param) {
		Transactions.edit(id, param);
	}
	
	public static CsTransaction getCsTransaction(Long id) {
		CsTransaction transaction = accountService.getTransaction(id);
		
		BigDecimal txCharge = new BigDecimal(0);
		for (CsTransactionCharge chargeTrans : transaction.getTransactionCharges()) {
			BigDecimal taxAmount = chargeTrans.getTaxAmount();
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
		
		if (transaction.getHoldingRefs() != null){
			CsDailyHolding dailyHolding = accountService.getDailyHolding(transaction.getSettlementDate(), 
																		 transaction.getAccount().getCustodyAccountKey(), 
																		 transaction.getSecurity().getSecurityKey(), 
																		 transaction.getClassification().getLookupId(), 
																		 transaction.getHoldingRefs());
			BigDecimal amountPledging = transactionService.getOutstandingQuantityAmount(transaction.getAccount().getCustodyAccountKey(), 
																						transaction.getSecurity().getSecurityKey(), 
																						transaction.getClassification().getLookupId(), 
																						transaction.getHoldingRefs());
			
			transaction.setHoldingQuantity(dailyHolding.getSettledQuantity().subtract(amountPledging));
		}
		
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION);
		if (transaction.getUdf() != null) {
			try {
				Map<String, String> data = json.readValue(transaction.getUdf(), new TypeReference<HashMap<String, String>>() {});
				if (udfs!=null){
					for (GnUdfMaster udf : udfs) {
						udf.setValue(data.get(udf.getFieldName()));
						if ( udf.getLookupGroup() != null ) {
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
		
		ScMaster security = securityService.getSecurity(transaction.getSecurity().getSecurityKey());
		if (security.getAccrualBase() != null) { transaction.getSecurity().setAccrualBase(security.getAccrualBase()); }
		if (security.getYearBase() != null) { transaction.getSecurity().setYearBase(security.getYearBase()); }
		
		return transaction;
	}
	
	// dummy method agar include jalan
	public static void saveSettlePrematch() {}
	public static void settlementPrematch() {}
	public static void settlement() {}
	public static void validateSettlementPrematch() {}
	public static void getCouponDates() {}
	public static void charges() {}
	public static void validateThirdParty() {};
}
