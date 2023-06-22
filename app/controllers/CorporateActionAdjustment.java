package controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.BnAccount;
import com.simian.medallion.model.CsAccount;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.CsTransactionBatch;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScCorporateAnnouncementAdj;
import com.simian.medallion.model.ScEntitlement;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.i18n.Messages;
import play.mvc.Before;
import vo.CorporateActionAdjustmentSearchParam;
import vo.CorporateActionParam;

public class CorporateActionAdjustment extends MedallionController {
	
	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CorporateActionAdjustment.class);
	
	static List<String> hitConfirm = new ArrayList<String>();
	static List<String> hitApprove = new ArrayList<String>();
	
	private static JsonHelper jsonEntitlement = new JsonHelper().getEntitlementSerializer();
	private static JsonHelper jsonScCorporateAnnouncementAdj = new JsonHelper().getScCorporateAnnouncementAdjSerializer();
	
	@Before(only={"list"})
	public static void setupList() {
		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);	
	}
	
	@Before(unless={"edit entry save back"})
	public static void setup() {
		renderArgs.put("currentDate", generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		List<SelectItem> transferMethods = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("transferMethods", transferMethods);
		renderArgs.put("viaManual", LookupConstants.FUND_TRANSFER_METHOD_MANUAL);
		renderArgs.put("viaInterface", LookupConstants.FUND_TRANSFER_METHOD_VIA_INTERFACE); 
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void list(){		
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CorporateActionAdjustmentSearchParam param = new CorporateActionAdjustmentSearchParam();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ADJUSTMENT));
		render("CorporateActionAdjustment/list.html", params, mode);
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void paging(Paging page, CorporateActionAdjustmentSearchParam param){
		
		page.addParams("a.status", page.EQUAL, LookupConstants.__RECORD_STATUS_ENTITLED);
		page.addParams("c.status", page.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
		page.addParams("a.taxable", page.EQUAL, Long.parseLong("1"));
			
		if(param.actionCode != null && !param.actionCode.equals("") ){
			page.addParams("a.action_template_key", page.EQUAL, Long.parseLong(param.actionCode));
		}
		
		if(param.securityType != null && !param.securityType.equals("") ){
			page.addParams("a2.security_type", page.EQUAL, param.securityType);
		}
		
		if(param.securityCode != null && !param.securityCode.equals("") ){
			page.addParams("a.security_key", page.EQUAL, Long.parseLong(param.securityCode));
		}
			
		if (!param.announcementNo.isEmpty()){
			page.addParams("a.corporate_announcement_code", param.announcementNoOperator, UIHelper.withOperator(param.announcementNo, param.announcementNoOperator));
		}
			
		page.addParams(Helper.searchAll("(d.transaction_number||ac.account_no||ac.name||a.description||d.settlement_amount)")
					    	,page.LIKE, UIHelper.withOperator(page.getsSearch().toUpperCase(), 1));
			
		page = transactionService.pagingCorporateActionAdjustment(page);
		
		renderJSON(page);
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void edit(Long id, Long annId, Long entId, Long entdId){
		logger.debug("edit id="+id+" annId="+annId+" entId="+entId+" entdId="+entdId);
		
		String mode = UIConstants.DISPLAY_MODE_EDIT;
		
		if (id == null) {
			mode = UIConstants.DISPLAY_MODE_ENTRY;
		}
		
		String taskId = "";
		
		ScCorporateAnnouncementAdj caAdjustment = securityService.getCaAnnouncementAdjustment(annId, entId, entdId);
		
		if(!caAdjustment.getRecordStatus().equals(LookupConstants.__RECORD_STATUS_APPROVED) && !(caAdjustment.getRecordStatus().equals(LookupConstants.__RECORD_STATUS_NEW) && caAdjustment.getAdjustmentKey()==null)) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		} 
		
		logger.debug("adjustment Key = "+caAdjustment.getAdjustmentKey());
		logger.debug("accountNo = "+caAdjustment.getAccountNo());
		logger.debug("accountName = "+caAdjustment.getAccountName());
		
		ScEntitlement entitlement = caAdjustment.getEntitlement();
		
		BnAccount bnAccount = bankAccountService.getBankAccount(caAdjustment.getBankAccount().getBankAccountKey());
		caAdjustment.setBankAccount(bnAccount);
			
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ADJUSTMENT));
		render("CorporateActionAdjustment/detail.html", entitlement, mode, caAdjustment, taskId);
		
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void view(Long id, Long annId, Long entId, Long entdId) {
		logger.debug("view print id "+id);
		
		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScCorporateAnnouncementAdj caAdjustment = securityService.getCaAnnouncementAdjustment(annId, entId, entdId);
		
		BnAccount bnAccount = bankAccountService.getBankAccount(caAdjustment.getBankAccount().getBankAccountKey());
		caAdjustment.setBankAccount(bnAccount);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ADJUSTMENT));
		render("CorporateActionAdjustment/detail.html", caAdjustment, mode);
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void save(String mode, ScCorporateAnnouncementAdj caAdjustment, ScEntitlement entitlement){
		
		validation.clear();
		
		logger.debug("validation.hasErrors() = "+validation.hasErrors());
		
		if (validation.hasErrors()){				
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ADJUSTMENT));
			render("CorporateActionAdjustment/detail.html", caAdjustment, mode);
		} else {
				
			Long id = caAdjustment.getAdjustmentKey();
			serializerService.serialize(session.getId(), id, caAdjustment);
			confirming(id, mode);
		
		}
		
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void confirming(Long id, String mode) {
		logger.debug("confirming id = "+id);
		renderArgs.put("confirming", true);
		
		ScCorporateAnnouncementAdj caAdjustment = serializerService.deserialize(session.getId(), id, ScCorporateAnnouncementAdj.class);

		caAdjustment.setTransaction(null);
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ADJUSTMENT));
		render("CorporateActionAdjustment/detail.html", mode, caAdjustment);
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void confirm(ScCorporateAnnouncementAdj caAdjustment, String mode){
		logger.debug("confirm  :" +caAdjustment);
		try {
			
			if (caAdjustment.getTaxAdjust() != null && caAdjustment.getTaxAdjust().compareTo(BigDecimal.ZERO) == 0) {
				caAdjustment.setTaxAdjust(null);
				caAdjustment.setSettlementAmountAdjust(null);
			}
			
			ScCorporateAnnouncementAdj caAdj = securityService.getCaAnnouncementAdjustment(caAdjustment.getCorporateAnnouncement().getCorporateAnnouncementKey()	, caAdjustment.getEntitlement().getEntitlementKey(), caAdjustment.getEntitlementDetail().getEntitlementDetailKey());
			caAdjustment.setTransaction(caAdj.getTransaction());
			
			caAdjustment.setCashTransactionEmpty(caAdjustment.getCashTransactionEmpty());
			
			caAdjustment = transactionService.saveCorporateAnnouncementAdj(MenuConstants.CS_CORPORATE_ACTION_ADJUSTMENT, caAdjustment, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY), "");
			logger.debug("success save CorporateAnnouncementAdj :" +caAdjustment);
			
			list();
			
		} catch (MedallionException e) {
			
			flash.error("Error : " + Messages.get(e.getMessage()));
			boolean confirming = true;
			
			validation.clear();
			
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ADJUSTMENT));
			render("CorporateActionAdjustment/detail.html", caAdjustment, confirming, mode);
			
		}
		
		list();
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void back(Long id, Long entitlementId, Long entitlementDetailId, String mode) {
		logger.debug("back id = "+id+" entId = "+entitlementId+" entDetailId = "+entitlementDetailId);
		
		ScCorporateAnnouncementAdj caAdjustment = serializerService.deserialize(session.getId(), id, ScCorporateAnnouncementAdj.class);
		
		if(caAdjustment == null) {
			mode = UIConstants.DISPLAY_MODE_ENTRY;
			
		} 
		
		List<SelectItem> transferMethods = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
		renderArgs.put("transferMethods", transferMethods);
		
		String editTransferMethod = "NO";
		
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CORPORATE_ACTION_ADJUSTMENT));
		render("CorporateActionAdjustment/detail.html", caAdjustment, mode, editTransferMethod);
	}
	
	public static void approval(String taskId, String keyId, String operation, Long maintenanceLogKey, String from){
		try {
			boolean approval = true;
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			ScCorporateAnnouncementAdj caAdjustment = jsonScCorporateAnnouncementAdj.deserialize(maintenanceLog.getNewData(), ScCorporateAnnouncementAdj.class);
			ScCorporateAnnouncementAdj caAdjustmentDb = securityService.getCaAnnouncementAdjustment(caAdjustment.getCorporateAnnouncement().getCorporateAnnouncementKey(), caAdjustment.getEntitlement().getEntitlementKey(), caAdjustment.getEntitlementDetail().getEntitlementDetailKey());
			ScEntitlement entitlement = caAdjustmentDb.getEntitlement();
			
			CsAccount account = accountService.getAccount(caAdjustmentDb.getEntitlementDetail().getAccount().getCustodyAccountKey());
			
			caAdjustment.setEntitlement(entitlement);
			caAdjustment.setCorporateAnnouncement(caAdjustmentDb.getCorporateAnnouncement());
			caAdjustment.setEntitlementDetail(caAdjustmentDb.getEntitlementDetail());
			caAdjustment.setAccount(account);
			
			caAdjustment.setCashTransactionEmpty(caAdjustmentDb.getCashTransactionEmpty());
			
			long start = System.currentTimeMillis();
			ScCorporateAnnouncementAdj caAdjustment2 = securityService.getCaAnnouncementAdjustment(
					caAdjustment.getCorporateAnnouncement().getCorporateAnnouncementKey(), caAdjustment.getEntitlement().getEntitlementKey(),
					caAdjustment.getEntitlementDetail().getEntitlementDetailKey());
			logger.debug("OPTIMIZE END caAdjustment " + (System.currentTimeMillis() - start)
					+ " ms ----------------------------");
			
			caAdjustment.setTransaction(caAdjustment2.getTransaction());
			
			BnAccount bnAccount = bankAccountService.getBankAccount(caAdjustment.getBankAccount().getBankAccountKey());
			caAdjustment.setBankAccount(bnAccount);
			caAdjustment.setAccountNo(caAdjustmentDb.getAccountNo());
			caAdjustment.setAccountName(caAdjustmentDb.getAccountName());
			caAdjustment.setHolding(caAdjustmentDb.getHolding());
			caAdjustment.setEntitledAmount(caAdjustmentDb.getEntitledAmount());
			caAdjustment.setTaxRate(caAdjustmentDb.getTaxRate());
			caAdjustment.setSettlementAmount(caAdjustmentDb.getSettlementAmount());
			caAdjustment.setEntitlementStatus(caAdjustmentDb.getEntitlementStatus());
			
			List<SelectItem> transferMethods = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants.FUND_TRANSFER_METHOD);
			renderArgs.put("transferMethods", transferMethods);
			
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("CorporateActionAdjustment/approval.html", entitlement, caAdjustment, approval, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			logger.error("Exchange Rate Approval", e);
		}
		
	}
	
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		logger.debug("approve maintenanceLogKey = "+maintenanceLogKey + " operation = "+operation);
		
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			transactionService.approveCorporateAnnouncementAdjustment(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
			
			result.put("status", "success");
		} catch (MedallionException e) {
			logger.error("medallionexception " + e.getMessage(), e);
			result.put("status", "error");
			result.put("message", Messages.get(e.getErrorCode()));
		}catch (Exception e) {
			logger.error("exception " + e.getMessage(), e);
			result.put("status", "error");
			result.put("message", "Failed to Save");
		}
		renderJSON(result);
	}
	
	public static void processAjax(CorporateActionParam param) {
		logger.debug("processAjax keyId="+param.getKeyId()+", taskId="+param.getTaskId()+", announcementKey="+param.getAnnouncementKey()+", typeTransaction="+param.getTypeTransaction());
		String keyId = param.getKeyId();
		String taskId = param.getTaskId();
		String announcementKey = param.getAnnouncementKey();
		String typeTransaction = param.getTypeTransaction();
		Map<String, Object> result = new HashMap<String, Object>();
		
		if (hitApprove.contains(keyId)) {
			result.put("status", "error");
			result.put("error","error.Record has already been modified");
			renderJSON(result);
		}else{
			hitApprove.add(keyId);
//			List<CsTransaction> transactions = accountService.listTransactionByBatchKey(Long.parseLong(keyId));
			List<CsTransaction> transactions = accountService.listCaSettlementTransaction(Long.parseLong(keyId));
			logger.debug("size of transactions = " +transactions.size());
			
			typeTransaction = "casettlement";
			
			try {	
				CsTransactionBatch batch = transactionService.approveTransactionSettlementCa(transactions, session.get(UIConstants.SESSION_USERNAME), taskId, announcementKey);	
				String message = Messages.get("batch.approved", batch.getTransactionBatchKey());
				result.put("status", "success");
				result.put("message", message);
				hitApprove.remove(keyId);
				renderJSON(result);	
			} catch (MedallionException ex) {
				hitApprove.remove(keyId);
				renderJSON(Formatter.resultError(ex));
			}catch (Exception e) {
				hitApprove.remove(keyId);
				renderJSON(Formatter.resultError(e));;
			} finally {
				hitApprove.remove(keyId);
			
			}
		}
	}
	
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		transactionService.approveCorporateAnnouncementAdjustment(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void entry(Long annId, Long entId, Long entdId){
		edit(null, annId, entId, entdId);
	}
	
	@Check("transaction.corporateactionadjustment")
	public static void cancel(Long id){
	}
}
