package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import vo.MiscellaneousChargeSearchParameter;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.CsTransactionCharge;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;


public class MiscellaneousChargeEntries extends MedallionController{
	private static Logger logger = Logger.getLogger(MiscellaneousChargeEntries.class);
	
//	@Before(unless={"list"})
	@Before
	public static void setup() {
		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
		
		List<SelectItem> paymentType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PAYMENT_TYPE);
		renderArgs.put("paymentType", paymentType);
		
		List<SelectItem> transOperators = UIHelper.stringOperators();
		renderArgs.put("transOperators", transOperators);	
		
		String recordStatusNew = LookupConstants.__RECORD_STATUS_NEW;
		renderArgs.put("recordStatusNew", recordStatusNew);
		
		String recordStatusUpdate = LookupConstants.__RECORD_STATUS_UPDATED;
		renderArgs.put("recordStatusUpdate", recordStatusUpdate);
		
		/*String menuCancel = MenuConstants.CS_MISCELLANEOUS_CANCEL;
		renderArgs.put("menuCancel", menuCancel);
		
		String menuEntry = MenuConstants.CS_MISCELLANEOUS_CHARGE;
		renderArgs.put("menuEntry", menuEntry);*/
		
	}
	
	@Check("transaction.miscellaneousCharge")
	public static void list(MiscellaneousChargeSearchParameter param) {
		String menuEntry = MenuConstants.CS_MISCELLANEOUS_CHARGE;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
		MiscellaneousChargeSearchParameter params = new MiscellaneousChargeSearchParameter();
		render("MiscellaneousChargeEntries/list.html",param,menuEntry, params);
	}
	
	@Check("transaction.miscellaneousCharge")
	public static void view(Long id) {
		logger.debug("id view "+id);
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsTransaction transaction = transactionService.getTransaction(id);
		CsTransactionCharge charge = new CsTransactionCharge();
		if (!transaction.getTransactionCharges().isEmpty()){
			Set<CsTransactionCharge> transChanges = transaction.getTransactionCharges();
			charge = transChanges.iterator().next();
		}
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
		render("MiscellaneousChargeEntries/entry.html", transaction, charge, mode);

	}
	
	@Check("transaction.miscellaneousCharge")
	public static void paging(Paging page,MiscellaneousChargeSearchParameter param){
		logger.debug("Masuk paging entry");
		if(param.isQuery()){
			page.addParams("ct.TRANSACTION_DATE",Paging.GREATEQUAL, param.miscellaneousFrom);
			page.addParams("ct.TRANSACTION_DATE",Paging.LESSEQUAL,param.miscellaneousTo);
			page.addParams("cm.CUSTOMER_KEY", Paging.EQUAL, param.customerCodeId);
			page.addParams("cc.CHARGE_KEY",Paging.EQUAL,param.chargeKey);
			page.addParams("ct.TRANSACTION_NUMBER", param.transactionNoOperator, UIHelper.withOperator(param.transactionId,param.transactionNoOperator));
			page.addParams("ct.TRANSACTION_STATUS",Paging.EQUAL,LookupConstants.__RECORD_STATUS_REJECTED);
			page.addParams("ct.RECORD_STATUS",Paging.EQUAL, LookupConstants.__RECORD_STATUS_NEED_REVISION);
			page.addParams("ctt.USED_BY",Paging.EQUAL,LookupConstants.USED_BY_MISC_CHARGES);
			
			page.addParams(Helper.searchAll("(cc.CHARGE_VALUE||to_char(ct.SETTLEMENT_DATE,'"+Helper.dateOracle(appProp.getDateFormat())+"')||" +
					"ct.TRANSACTION_NUMBER||ca.ACCOUNT_NO||ca.NAME||gc.DESCRIPTION)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			
			page = transactionService.searchCsTransactionPaging(page,"");
		}
		
		renderJSON(page);
		
	}
	
	@Check("transaction.miscellaneousCharge")
	public static void entry() {
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsTransaction transaction = new CsTransaction();
		CsTransactionCharge charge = new CsTransactionCharge();
		transaction.setSettlementDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		GnSystemParameter systemParameter = generalService.getSystemParameter(SystemParamConstants.PARAM_TRANSACTION_MISCELLANEOUS);
		transaction.setTransactionTemplate(transactionService.getTransactionTemplate(Long.parseLong(systemParameter.getValue())));
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
		render("MiscellaneousChargeEntries/entry.html", transaction, charge, mode);
	}

	@Check("transaction.miscellaneousCharge")
	public static void edit(Long id) {
		logger.debug("id edit "+id);
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsTransaction transaction = transactionService.getTransaction(id);
		CsTransactionCharge charge = new CsTransactionCharge();
		if (!transaction.getTransactionCharges().isEmpty()){
			Set<CsTransactionCharge> transChanges = transaction.getTransactionCharges();
			charge = transChanges.iterator().next();
		}
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
		render("MiscellaneousChargeEntries/entry.html", transaction, charge, mode);
	}
	
	@Check("transaction.miscellaneousCharge")
	public static void save(CsTransaction transaction, CsTransactionCharge charge, String mode) {
		// Validate here		
		if(transaction != null){
			logger.debug("transaction key "+transaction.getTransactionKey());
			validation.required("Transaction Date is", transaction.getSettlementDate());
			validation.required("Account No is", transaction.getAccount().getAccountNo());
			validation.required("Charge Code is", charge.getChargeMaster().getChargeCode());
			validation.required("Charge Value is", charge.getChargeValue());
			if (validation.hasErrors()){
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
				render("MiscellaneousChargeEntries/entry.html", transaction, charge, mode);
			} else {
				Set<CsTransactionCharge> transCharges = new HashSet<CsTransactionCharge>();
				if (charge!=null){
						transCharges.add(charge);
				}
				transaction.setTransactionCharges(transCharges);
				Long id = transaction.getTransactionKey();
				String json = serializerService.serialize(session.getId(), id, transaction);
				confirming(transaction.getTransactionKey(), mode);
			}
		}	
		else {
			//flash.error("argument.null", transaction);
			entry();
		}
	}
	
	@Check("transaction.miscellaneousCharge")
	public static void confirming(Long id, String mode) {
		try {
			renderArgs.put("confirming", true);
			CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
			Set<CsTransactionCharge> charges = transaction.getTransactionCharges();
			CsTransactionCharge charge = charges.iterator().next();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
			render("MiscellaneousChargeEntries/entry.html", transaction, charge, mode);
		} catch (Exception e) {
			logger.debug(e.getMessage(),e);
		}
	}
	
	@Check("transaction.miscellaneousCharge")
	public static void confirm(CsTransaction transaction, CsTransactionCharge charge, String mode){
		if(transaction == null)
			back(null, mode);
		try {
			charge.setChargeCapitalized(true);
			Set<CsTransactionCharge> transCharges = new HashSet<CsTransactionCharge>();
			if (charge!=null){
				transCharges.add(charge);
			}
			transaction.setTransactionCharges(transCharges);
			transaction.setChargeValueMisc(charge.getChargeValue());
			logger.debug("Desc charge master "+charge.getChargeMaster().getDescription());
			transaction.setChargeNameMisc(charge.getChargeMaster().getDescription());
//			Date currentBusinessDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
//			transaction.setTransactionDate(currentBusinessDate);
			boolean confirming = true;
//			CsTransaction trx = transactionService.createTransactionWithSerialized(menuCode, transaction, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
	
			CsTransaction trx = transactionService.createMiscellaneousChargeTransaction(MenuConstants.CS_MISCELLANEOUS_CHARGE, transaction, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
//			CsTransaction trx = transactionService.createTransactionWithSerialized(MenuConstants.CS_MISCELLANEOUS_CHARGE, transaction, session.get(UIConstants.SESSION_USERNAME),"", session.get(UIConstants.SESSION_USER_KEY));
			
			if (trx.getTransactionKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("transaction.confirmed.successful", trx.getTransactionNumber()));
				renderJSON(result);
				//renderText (trx.getTransactionNumber());
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
				render("MiscellaneousChargeEntries/detail.html", trx, mode, confirming);		
			}
		} catch (MedallionException ex) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
			renderJSON(Formatter.resultError(ex));
		}catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
	
	@Check("transaction.miscellaneousCharge")
	public static void back(Long id, String mode) {
		logger.debug("id -- > "+id);
		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
		Set<CsTransactionCharge> charges = transaction.getTransactionCharges();
		CsTransactionCharge charge = charges.iterator().next();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
		renderTemplate("MiscellaneousChargeEntries/entry.html", transaction, charge, mode);
	}
	
	@Check("transaction.miscellaneousCharge")
	public static void clear(String mode) {
		CsTransaction transaction = new CsTransaction();
		CsTransactionCharge charge = new CsTransactionCharge();
		render("MiscellaneousChargeEntries/detail.html", transaction, charge, mode);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition, Long maintenanceLogKey) {
		logger.debug("key id "+keyId);
		logger.debug("maintenance log key "+maintenanceLogKey);
		logger.debug("operatin "+operation);
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		boolean approval = true;
		CsTransaction transaction = accountService.getTransaction(keyId);
		CsTransactionCharge charge = new CsTransactionCharge();
		Set<CsTransactionCharge> charges = transaction.getTransactionCharges();
		charge = charges.iterator().next();

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			render("MiscellaneousChargeEntries/approval.html", transaction, charge, taskId, mode,from, operation, maintenanceLogKey,approval);
		} else{
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			render("MiscellaneousChargeEntries/approval.html", transaction, charge, taskId, mode,from, operation, maintenanceLogKey,approval);
		}
	}
	
	
	
	/*public static void reject(String taskId, Long keyId) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			logger.debug("task iD >>" +taskId + " <<< KEY ID >>>> " +keyId);
			CsTransaction transaction = transactionService.rejectTransaction(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
			result.put("status", "success");
			result.put("message", Messages.get("transaction.rejected", transaction.getTransactionNumber()));
		} catch (MedallionException ex) {
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			for (Object param : ex.getParams()) {
				params.add(Messages.get(param));
			}
			result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
		}
		renderJSON(result);
	}*/
	
	/*public static void approve(String taskId, Long keyId){
	Map<String, Object> result = new HashMap<String, Object>();
	try {
		CsTransaction transaction = transactionService.approveTransaction(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);
		result.put("status", "success");
		result.put("message", Messages.get("transaction.approved", transaction.getTransactionNumber()));
	} catch (MedallionException ex) {
		result.put("status", "error");
		List<String> params = new ArrayList<String>();
		for (Object param : ex.getParams()) {
			params.add(Messages.get(param));
		}
		result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
	}
	renderJSON(result);
}
*/
	public static void approve(Long keyId,String taskId, Long maintenanceLogKey, String operation, String correction) {
		logger.debug("key id "+keyId);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
//			CsTransaction transaction = transactionService.approveMiscellaneousCharge(keyId,session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null);
			CsTransaction transaction = transactionService
					.approveTransactionWithSerialized(
							session.get(UIConstants.SESSION_USERNAME),
							taskId, operation, maintenanceLogKey,
							WorkflowConstants.PROCESS_TYPE_APPROVE,
							null);
			result.put("status", "success");
			result.put("message", Messages.get("transaction.approved", transaction.getTransactionNumber()));
		} catch (MedallionException e) {
			logger.error("medallionexception " + e.getMessage(), e);
			result.put("status", "error");
			result.put("message", Messages.get(e.getErrorCode()));
		} catch (Exception e) {
			logger.error("exception " + e.getMessage(), e);
			result.put("status", "error");
			result.put("message", "Failed to Save");
		}
		renderJSON(result);
	}
	
	public static void reject(Long keyId,String taskId, Long maintenanceLogKey, String operation, String correction) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			CsTransaction transaction = new CsTransaction();
			if (Helper.isNullOrEmpty(correction)) {
//				transaction = transactionService.approveMiscellaneousCharge(keyId,session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, correction);
				transaction = transactionService
						.approveTransactionWithSerialized(
								session.get(UIConstants.SESSION_USERNAME),
								taskId, operation, maintenanceLogKey,
								WorkflowConstants.PROCESS_TYPE_REJECT,
								null);
			} else {
//				transaction = transactionService.approveMiscellaneousCharge(keyId,session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction);
				transaction = transactionService.approveTransactionWithSerialized(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction);
				
			}
			result.put("status", "success");
			result.put("message", Messages.get("transaction.rejected", transaction.getTransactionNumber()));
		} catch (MedallionException e) {
			logger.error("medallionexception " + e.getMessage(), e);
			result.put("status", "error");
			result.put("message", Messages.get(e.getErrorCode()));
		} catch (Exception e) {
			logger.error("exception " + e.getMessage(), e);
			result.put("status", "error");
			result.put("message", "Failed to Save");
		}
		renderJSON(result);	
	}

}
