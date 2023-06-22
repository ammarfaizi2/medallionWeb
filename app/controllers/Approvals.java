package controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.AbstractRgTrade;
import com.simian.medallion.model.AbstractTransaction;
import com.simian.medallion.model.ApprovalSearchParameters;
import com.simian.medallion.model.CBestMessage;
import com.simian.medallion.model.CfMaster;
import com.simian.medallion.model.CsPortfolioUpdate;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.FaJournalMaster;
import com.simian.medallion.model.FaRecurring;
import com.simian.medallion.model.FaTransaction;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnUser;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.PlTransaction;
import com.simian.medallion.model.ReturnParam;
import com.simian.medallion.model.RgNav;
import com.simian.medallion.model.RgPayment;
import com.simian.medallion.model.RgProduct;
import com.simian.medallion.model.RgSwitching;
import com.simian.medallion.model.RgTrade;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScEntitlement;
import com.simian.medallion.model.TdMaster;
import com.simian.medallion.model.comparators.BatchApprovalWfComparator;
import com.simian.medallion.service.ApprovalService;
import com.simian.medallion.vo.ApproveItem;
import com.simian.medallion.vo.SelectItem;
import com.simian.medallion.vo.WorkflowGridItem;

import helpers.UIConstants;
import play.exceptions.UnexpectedException;
import play.i18n.Messages;
import play.modules.spring.Spring;
import play.mvc.Before;
import play.mvc.With;
import vo.ApprovalDetailViewModel;

@With(Secure.class)
public class Approvals extends MedallionController {
	private static Logger log = Logger.getLogger(Approvals.class);

	public static final String SPECIAL_CHAR = ",,";
	
	static List<String> dataProcess = new ArrayList<String>();

	@Before
	public static void setup() {
		log.debug("setup. ");

		GnUser gnUser = applicationService.getUser(session.get("username"));

		List<SelectItem> activities = new ArrayList<SelectItem>();
		try {
			activities = workflowService.getActivityList(gnUser.getUserKey());
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}

		ApproveItem search = new ApproveItem();

		renderArgs.put("activities", activities);
		renderArgs.put("search", search);
		renderArgs.put("specialChar", SPECIAL_CHAR);
	}

	public static void listgo() {
		log.debug("listgo. ");

		session.remove(ApprovalSearchParameters.SESSION_SINGLE_ID);
		list(null, null, null, null, null, null);
	}

	public static void list(String processDefinition, String taskName, List<String> listFails, List<String> listSuccess, String processType, ApproveItem search) {
		log.debug("list. processDefinition: " + processDefinition + " taskName: " + taskName + " listFails: " + listFails + " listSuccess: " + listSuccess + " processType: " + processType + " search: " + search);

		String json = session.get(ApprovalSearchParameters.SESSION_SINGLE_ID);
		ApprovalSearchParameters param = null;
		try {
			if (json != null && !"".equals(json)) {
				param = (new JsonHelper()).deserialize(json, ApprovalSearchParameters.class);
			}
		} catch (Exception e) {
		}

		listpaging(param);
	}

	public static void listbatchgo() {
		log.debug("listbatchgo. ");

		session.remove(ApprovalSearchParameters.SESSION_BATCH_ID);
		listbatch(null);
	}

	public static void listbatch(ApproveItem search) {
		log.debug("listbatch. search: " + search);

		String json = session.get(ApprovalSearchParameters.SESSION_BATCH_ID);
		ApprovalSearchParameters param = null;
		try {
			if (json != null && !"".equals(json)) {
				param = (new JsonHelper()).deserialize(json, ApprovalSearchParameters.class);
			}
		} catch (Exception e) {
		}

		listbatchpaging(param);
	}

	public static void show(long id, int displayMode) {
		log.debug("show. id: " + id + " displayMode: " + displayMode);

		ApprovalService approvalService = (ApprovalService) Spring.getBean("approvalService");
		GnMaintenanceLog maintenance = approvalService.getApproval(id);

		ApprovalDetailViewModel model = new ApprovalDetailViewModel();
		model.displayMode = displayMode;
		model.maintenance = maintenance;

		ObjectMapper json = new ObjectMapper();

		CfMaster oldData = new CfMaster();
		CfMaster newData = new CfMaster();

		try {
			oldData = json.readValue(maintenance.getOldData(), CfMaster.class);
			newData = json.readValue(maintenance.getNewData(), CfMaster.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		render("Approvals/detail.html", model, oldData, newData);
	}

	public static void rejectBatch(List<String> listBatch) {
		log.debug("rejectBatch. listBatch: " + listBatch);

		processBatch(listBatch, WorkflowConstants.PROCESS_TYPE_REJECT);
	}

	public static void approveBatch(List<String> listBatch) {
		log.debug("approveBatch. listBatch: " + listBatch);

		processBatch(listBatch, WorkflowConstants.PROCESS_TYPE_APPROVE);
	}

	private static String processCustodySettlement(String[] param, String processType){
		String transacTiontype = "";
		String approveResult="";
		boolean sendEmail = true;
		if (param[5].contains("Corporate Action Settlement")) {
			transacTiontype = UIConstants.PARAM_CA_SETTLE;
			sendEmail = false;
		}
		if (param[5].contains("Entitlement Approval")) {
			transacTiontype = UIConstants.PARAM_CA_ENTITLEMENT;
			sendEmail = false;
		}
		CsTransaction transaction = accountService.getTransaction(Long.parseLong(param[6]));
		if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
			transactionService.rejectTransactionBatch(transaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
			approveResult = WorkflowConstants.APPROVE_SUCCESS;
		} else {
			transactionService.checkHolding(transaction, AbstractTransaction.PARAM_CATEGORY_TRANSACTION_SETTLEMENT);
			transactionService.approveTransactionBatch(transaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0], transacTiontype, sendEmail);
			approveResult = WorkflowConstants.APPROVE_SUCCESS;
		}
		return approveResult;
	}
	public static void processBatchSelectAll(ApprovalSearchParameters param, String processType) throws Exception {
		log.debug("processBatchSelectAll. param: " + param + " processType: " + processType);

		Paging page = new Paging();
		page.setDateFormat(appProp.getDateFormat());
		if (param.getCreatedDateMili() != null)
			param.setCreatedDate(new Date(param.getCreatedDateMili()));

		JsonHelper helper = new JsonHelper();
		session.put(param.isFromSingle() ? ApprovalSearchParameters.SESSION_SINGLE_ID : ApprovalSearchParameters.SESSION_BATCH_ID, helper.serialize(param));
		if (param.isQuery()) {

			GnUser gnUser = applicationService.getUser(session.get("username"));
			GnUser newGnUser = new GnUser();
			newGnUser.setUserName(gnUser.getUserName());
			newGnUser.setUserId(gnUser.getUserId());
			newGnUser.setUserKey(gnUser.getUserKey());

			page.getParamFixMap().put("user", newGnUser);
			page.getParamFixMap().put("param", param);
			page.setiDisplayStart(0);
			page.setiDisplayLength(999999);
			page = workflowService.pagingWorkflow2(page);
			List<WorkflowGridItem> listBatch = new ArrayList<WorkflowGridItem>();
			for (Object array : page.getData()) {
				WorkflowGridItem item = (WorkflowGridItem) array;
				listBatch.add(item);
			}
			String data = json.writeValueAsString(listBatch);
			renderText(data);
		}

	}

	public static void processBatch(List<String> listBatch, String processType) {
		log.debug("processBatch. listBatch: " + listBatch + " processType: " + processType);

		// if (null == processType || "".equals(processType)) processType =
		// "approve";
		if (null == processType || "".equals(processType))
			processType = WorkflowConstants.PROCESS_TYPE_APPROVE;
		// int counter = 0;
		// StringBuffer tes = new StringBuffer();
		List<String> listFails = new ArrayList<String>();
		List<String> listSucess = new ArrayList<String>();
		Collections.sort(listBatch, new BatchApprovalWfComparator());
		for (String data : listBatch) {
			// logger.debug("[APPROVE BATCH] data >>> " + data);
			String[] param = data.split("\\|");
			if (param.length < 8) {
				log.debug("invalid length approval = (" + data + ")");
				listFails.add("");
				continue;
			}
			log.debug("[APPROVE BATCH] param[0] >>> " + param[0]);
			log.debug("[APPROVE BATCH] param[1] >>> " + param[1]);
			log.debug("[APPROVE BATCH] param[2] >>> " + param[2]);
			log.debug("[APPROVE BATCH] param[3] >>> " + param[3]);
			log.debug("[APPROVE BATCH] param[4] >>> " + param[4]);
			log.debug("[APPROVE BATCH] param[5] >>> " + param[5]);
			log.debug("[APPROVE BATCH] param[6] >>> " + param[6]);
			log.debug("[APPROVE BATCH] param[8] create date >>> " + param[8]);

			String approveResult = "";
			log.debug("[APPROVE BATCH] what param[3] ? " + param[3]);
			String key = "";

			try {
				String[] datas = param[4].trim().split(" ");
				key = datas[datas.length - 1];
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			log.debug("KEY1 " + key);
			
			try {
				
				if (WorkflowConstants.PROCDEF_FUND_TRANSFER_MAINTENANCE.equals(param[3])){					
					Map<String, Object> mapProcess = fundService.approveFundTransfer(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
					approveResult = mapProcess.get("status").toString();
				}

				// BN1001 -- BANK TRANSACTION MASTER //
				if (WorkflowConstants.PROCDEF_BANK_TRANSACTION_MASTER_MAINTENANCE.equals(param[3])) {
					approveResult = bankAccountService.approveBankTransactonMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// TX2001 : Acquisition //
				if (WorkflowConstants.PROCDEF_ACQUISITION.equals(param[3])) {
					approveResult = transactionService.approveAcquisition(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				if (WorkflowConstants.PROCDEF_BANK_ACCOUNT_MAINTENANCE.equals(param[3])) {
					approveResult = bankAccountService.approveBankAccount(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				if (WorkflowConstants.PROCDEF_BANK_ACCOUNT_REDEMPTION_MAINTENANCE.equals(param[3])) {
					approveResult = bankAccountService.approveBankAccount(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// BN4001 -- CASH TRANSACTION //
				if (WorkflowConstants.PROCDEF_CASH_TRANSACTION.equals(param[3])) {
					CsTransaction transaction = accountService.getTransaction(Long.parseLong(param[6]));
					Map<String, Object> result = new HashMap<String, Object>();
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						transactionService.rejectTransaction(transaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						transactionService.approveTransaction(transaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
					result.put("status", "success");
					result.put("message", Messages.get("transaction.approved", transaction.getTransactionNumber()));
				}

				// BN4006 -- BANK ACCOUNT BALANCE //
				if (WorkflowConstants.PROCDEF_BANK_BALANCE.equals(param[3])) {
					approveResult = bankAccountService.approveBankBalance(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CB0001, CB0002, CB0005, CB0006 -- CBEST SECURITY TRANSFER,
				// BOOK TRANSFER, CANCEL INSTRUCTION, WIRE TRANSFER //
				if (WorkflowConstants.PROCDEF_CBESTMESSAGE__MAINTENANCE.equals(param[3])) {
					CBestMessage cBestMessage = cbestService.getMessage(Long.parseLong(param[6]));
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						cbestService.approveTransaction(cBestMessage.getMsgid(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						cbestService.rejectTransaction(cBestMessage.getMsgid(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
				}

				// CF2001 & CF2002 -- CUSTOMER REGISTER & EDIT //
				if (WorkflowConstants.PROCDEF_CUSTOMER_MAINTENANCE.equals(param[3])) {
					approveResult = customerService.approveCustomer(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CF3001 & CF3002 -- INVESTOR REGISTER & EDIT //
				if (WorkflowConstants.PROCDEF_INVESTOR_MAINTENANCE.equals(param[3])) {
					approveResult = customerService.approveInvestor(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CM2001 & CM2002 -- CERTIFICATE REGISTER & EDIT //
				if (WorkflowConstants.PROCDEF_CERTIFICATE.equals(param[3])) {
					approveResult = customerService.approveCertificate(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS1001 -- CHARGE PROFILE //
				if (WorkflowConstants.PROCDEF_CHARGE_PROFILE_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveChargeProfile(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS1002 -- CHARGE MASTER //
				if (WorkflowConstants.PROCDEF_CHARGE_MASTER_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveChargeMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS1003 -- TRANSACTION TEMPLATE //
				if (WorkflowConstants.PROCDEF_TRANSACTION_TEMPLATE_MAINTENANCE.equals(param[3])) {
					approveResult = accountService.approveTransactionTemplate(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS1004 -- TRANSACTION MASTER //
				if (WorkflowConstants.PROCDEF_CUSTODY_TRANSACTION_MASTER_MAINTENANCE.equals(param[3])) {
					approveResult = accountService.approveTransactonMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS1005, CS1006, SC1005 -- CUSTOMER, TRANSACTION, DAN SECURITY
				// UDF MAINTENANCE //
				if (WorkflowConstants.PROCDEF_UDF_MASTER_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveUdfMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS2001 & CS2002 -- CUSTODY ACCOUNT REGISTER & EDIT //
				if (WorkflowConstants.PROCDEF_CUSTODY_ACCOUNT_MAINTENANCE.equals(param[3])) {
					approveResult = accountService.approveAccount(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS4001 -- PORTFOLIO TRANSACTION BUY / SELL //
				if (WorkflowConstants.PROCDEF_TRANSACTION.equals(param[3])) {
					approveResult = processTransaction(session.get(UIConstants.SESSION_USERNAME), param, key, processType);
				}

				// CS4002 -- CORPORATE ACTION ANNOUNCEMENT //
				if (WorkflowConstants.PROCDEF_NEW_CORPORATE_ANNOUNCEMENT.equals(param[3])) {
					String number = key;
					ScCorporateAnnouncement announcement = securityService.getCorporateAnnouncementByCode(number);
					Map<String, Object> result = new HashMap<String, Object>();
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						securityService.approveNewAnnouncement(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						securityService.approveNewAnnouncement(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
					result.put("status", "success");
					result.put("message", Messages.get("transaction.approved", announcement.getCorporateAnnouncementCode()));
				}

				// CS4004 - SETTLEMENT PREMATCH //
				if (WorkflowConstants.PROCDEF_SETTLEMENT_PREMATCH.equals(param[3])) {

					Map<String, Object> result = new HashMap<String, Object>();
					if(dataProcess.contains(param[0])){
						result.put("status", "error");
						result.put("message", "Data currenly in process");
					}else{
						dataProcess.add(param[0]);
						Map<String, String> resultApprove = transactionService.preApproveSettlementPrematch(param[0], param[6], processType, session.get(UIConstants.SESSION_USERNAME), UIConstants.USER_SYSTEM, true, Long.valueOf(param[1]), false);
						approveResult = resultApprove.get("approveResult");
						result.put("status", "success");
						result.put("message", Messages.get("transaction.approved", resultApprove.get("message")));
					}
					
				}

				// CS4005 -- CANCEL TRANSACTION SETTLEMENT //
				if (WorkflowConstants.PROCDEF_CANCEL_TRANSACTION_SETTLEMENT.equals(param[3])) {
					CsTransaction transaction = accountService.getTransactionCanceledSettlementByNumber(key);
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						transactionService.rejectCsCancelTransactionSettlement(transaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						transactionService.approveCsCancelTransactionSettlement(transaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
				}

				// CS4006 -- CANCEL TRANSACTION PORTFOLIO //
				if (WorkflowConstants.PROCDEF_CANCEL_TRANSACTION_PORTFOLIO.trim().equals(param[3])) {
					String number = key;

					CsTransaction csTransaction = transactionService.getTransactionCanceledByNumber(number);

					Map<String, Object> result = new HashMap<String, Object>();
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						transactionService.rejectCsCancelPortfolioTransaction(Long.parseLong(number), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						transactionService.approveCsCancelPortfolioTransaction(Long.parseLong(number), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
					result.put("status", "success");
					result.put("message", Messages.get("transaction.approved", csTransaction.getTransactionKey()));
				}

				// CS4008 -- MISCELLANEOUS CHARGE //
				if (WorkflowConstants.PROCDEF_MISCELLANEOUS_CHARGE.trim().equals(param[3])) {
					transactionService.approveTransactionWithSerialized(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
					approveResult = WorkflowConstants.APPROVE_SUCCESS;
				}

				// CS4012 -- PORTFOLIO UPDATE //
				if (WorkflowConstants.PROCDEF_CS_PORTFOLIO_UPDATE.trim().equals(param[3])) {
					String number = key;
					CsPortfolioUpdate portfolioUpdate = accountService.getCsPortfolioUpdate(Long.parseLong(number));
					Map<String, Object> result = new HashMap<String, Object>();
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						accountService.rejectCsPortfolioUpdate(portfolioUpdate.getPortfolioUpdateKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						accountService.approveCsPortfolioUpdate(portfolioUpdate.getPortfolioUpdateKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
					result.put("status", "success");
					result.put("message", Messages.get("transaction.approved", portfolioUpdate.getPortfolio().getPortfolioKey()));
				}

				// CS4014 & CS4019-- SHARE HOLDER MEETING DAN CANCEL SHARE
				// HOLDER MEETING //
				if (WorkflowConstants.PROCDEF_SHAREHOLDERMEETING__MAINTENANCE.equals(param[3])) {
					approveResult = shareHolderService.approveMeeting(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS4022 -- CANCEL CORPORATE ENTITLEMENT //
				if (WorkflowConstants.PROCDEF_CANCEL_CA_ENTITLEMENT.trim().equals(param[3])) {
					approveResult = securityService.approveCancelCAEntitlement(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, session.get(UIConstants.SESSION_USER_KEY));
				}

				// CS4023 -- CANCEL INVOICE //
				if (WorkflowConstants.PROCDEF_CANCEL_INVOICE.equals(param[3])) {
					transactionService.approveCancelInvoice(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
					approveResult = WorkflowConstants.APPROVE_SUCCESS;
				}

				// CS4024 -- CANCEL CORPORATE ANNOUNCEMENT //
				if (WorkflowConstants.PROCDEF_CANCEL_CA_ANNOUNCEMENT.trim().equals(param[3])) {
					approveResult = securityService.approveCancelAnnouncement(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, session.get(UIConstants.SESSION_USER_KEY));
				}

				// CS4025 -- CANCEL MISCELLANEOUS CHARGE //
				if (WorkflowConstants.PROCDEF_CANCEL_MISCELLANEOUS_CHARGE.trim().equals(param[3])) {
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_APPROVE)) {
						transactionService.approveCancelMiscellaneousCharge(Long.parseLong(param[6]), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						transactionService.rejectMiscellaneousCharge(Long.parseLong(param[6]), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
				}

				// CS5001 -- SETTLEMENT ENTRY //
				if (WorkflowConstants.PROCDEF_SETTLEMENT.equals(param[3])) {
					log.debug("[FRS] [APPROVALS] [PROC BTCH] = " + param[5]);
					approveResult = processCustodySettlement(param, processType);
				}

				// CS5002 -- CORPORATE ACTION SETTLEMENT //
				if (WorkflowConstants.PROCDEF_CORPORATE_SETTLEMENT.trim().equals(param[3])) {
					GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(Long.parseLong(param[1]));
					ScEntitlement newEntitlement = json.readValue(maintenanceLog.getNewData(), ScEntitlement.class);
					Date oldDistributionDate = newEntitlement.getCorporateAnnouncement().getDistributionDateTemp();
					ScEntitlement entitlement = securityService.getEntitlementByAnnouncement(Long.parseLong(param[6]));
					List<CsTransaction> listTransSettlements = accountService.listCaSettlementTransaction(entitlement.getEntitlementKey());
					Map<String, Object> result = new HashMap<String, Object>();
					if(dataProcess.contains(param[3])){
						result.put("status", "error");
						result.put("message", "Data currenly in process");
					}else{
						dataProcess.add(param[3]);
						if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
							transactionService.rejectTransactionSettlementCa(listTransSettlements, session.get(UIConstants.SESSION_USERNAME), param[0], param[6], oldDistributionDate); // umifalah
							approveResult = WorkflowConstants.APPROVE_SUCCESS;
						} else {
							transactionService.approveTransactionSettlementCa(listTransSettlements, session.get(UIConstants.SESSION_USERNAME), param[0], param[6]);
							approveResult = WorkflowConstants.APPROVE_SUCCESS;
						}
					
						result.put("status", "success");
						result.put("message", Messages.get("batch.approved", entitlement.getTransactionBatch().getTransactionBatchKey()));
					}
				}

				// CP1001 -- COMPLIANCE RULE MASTER //
				if (WorkflowConstants.PROCDEF_CP_RULEMASTER_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveRuleMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CP1002 -- COMPLIANCE RULE PROFILE //
				if (WorkflowConstants.PROCDEF_CP_RULEPROFILE_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveComplianceProfile(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CP1003 -- COMPLIANCE AFFILIATION //
				if (WorkflowConstants.PROCDEF_CP_AFFILATION_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveAffiliation(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CP1004 -- POSITIF SECURITY / COMPLIANCE PORTFOLIO//
				if (WorkflowConstants.PROCDEF_CP_PORTFOLIO_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approvePortfolio(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CP1005 -- COMPLIANCE RULE EXCEPTION //
				if (WorkflowConstants.PROCDEF_CP_RULEEXCEPTION_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveRuleexception(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CP1006 -- COMPLIANCE SECURITY LIMIT //
				if (WorkflowConstants.PROCDEF_CP_SECURITYLIMIT_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveSecurityLimit(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CP1007 -- COMPLIANCE SECURITY SECTOR //
				if (WorkflowConstants.PROCDEF_CP_SECURITY_SECTOR_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveSector(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// FA1001 -- COA MASTER //
				if (WorkflowConstants.PROCDEF_FA_COA_MASTER_MAINTENANCE.equals(param[3])) {
					approveResult = fundService.approveFaCoaMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// FA1002 -- POSTING PROFILE & POSTING RULE //
				if (WorkflowConstants.PROCDEF_FA_POSTING_PROFILE.equals(param[3])) {
					approveResult = fundService.approveFaPostingProfile(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}
				if (WorkflowConstants.PROCDEF_FA_POSTING_RULE.equals(param[3])) {
					approveResult = fundService.approveFaPostingRule(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// FA1004 -- FEE ACCRUAL SETUP //
				if (WorkflowConstants.PROCDEF_FA_FEE_MASTER.equals(param[3])) {
					approveResult = fundService.approveFeeAccrual(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// FA1006 -- FA TRANSACTION MASTER //
				if (WorkflowConstants.PROCDEF_FA_TRANSACTION_MASTER.equals(param[3])) {
					approveResult = fundService.approveFaTransactionMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}
				// FA1007 -- JOURNAL TEMPLATE //
				if (WorkflowConstants.PROCDEF_JOURNAL_TEMPLATE.trim().equals(param[3])) {
					String number = param[6];
					FaJournalMaster faJournalMaster = fundService.getFaJournalMaster(Long.parseLong(number));
					Map<String, Object> result = new HashMap<String, Object>();
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						fundService.approveJournalTemplate(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						fundService.approveJournalTemplate(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
					result.put("status", "success");
					result.put("message", Messages.get("transaction.approved", faJournalMaster.getJournalMasterKey()));
				}

				// FA2001 -- FUND SETUP //
				if (WorkflowConstants.PROCDEF_FA_MASTER_MAINTENANCE.equals(param[3])) {
					approveResult = fundService.approveFaMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// FA4001 -- MANUAL JURNAL //
				if (WorkflowConstants.PROCDEF_MANUAL_JOURNAL.trim().equals(param[3])) {
					String number = key;
					FaTransaction faTransaction = fundService.getFaTransaction(Long.parseLong(number));
					Map<String, Object> result = new HashMap<String, Object>();
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						fundService.approveManualJournal(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						fundService.approveManualJournal(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
					result.put("status", "success");
					result.put("message", Messages.get("transaction.approved", faTransaction.getTransactionKey()));
				}

				// FA4002 -- INJECT TOTAL UNIT FUND //
				if (WorkflowConstants.PROCDEF_FA_UNIT_TRANSACTION.equals(param[3])) {
					approveResult = fundService.approveFaUnitTransaction(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// FA4003 -- CANCEL MANUAL JURNAL //
				if (WorkflowConstants.PROCDEF_CANCEL_MANUAL_JOURNAL.trim().equals(param[3])) {
					FaTransaction faTransaction = fundService.getFaTransaction(Long.parseLong(param[6]));
					Map<String, Object> result = new HashMap<String, Object>();
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						fundService.rejectCancelManualJournal(faTransaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						fundService.approveCancelManualJournal(faTransaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
					result.put("status", "success");
					result.put("message", Messages.get("transaction.approved", faTransaction.getTransactionKey()));
				}

				// FA4005 -- RECURRING JURNAL //
				if (WorkflowConstants.PROCDEF_RECURRING_JOURNAL.trim().equals(param[3])) {
					String number = key;
					FaRecurring faRecurring = fundService.getFaRecurring(Long.parseLong(number));
					Map<String, Object> result = new HashMap<String, Object>();
					if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
						fundService.approveRecurringJournal(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					} else {
						fundService.approveRecurringJournal(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
						approveResult = WorkflowConstants.APPROVE_SUCCESS;
					}
					result.put("status", "success");
					result.put("message", Messages.get("transaction.approved", faRecurring.getRecurringKey()));
				}

				// FA4006 -- CANCEL RECURRING JURNAL //
				if (WorkflowConstants.PROCDEF_CANCEL_RECURRING_JOURNAL.trim().equals(param[3])) {
					log.debug("CANCEL RECURRING JURNAL ");
					fundService.approveCancelRecurringJournal(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
					approveResult = WorkflowConstants.APPROVE_SUCCESS;
				}

				// FA4009 -- POSTING MF ROLLBACK //
				if (WorkflowConstants.PROCDEF_FA_POSTING_ROLLBACK.trim().equals(param[3])) {
					approveResult = taService.approvePostingMFRollback(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// GN1001 -- ORGANIZATION //
				if (WorkflowConstants.PROCDEF_ORGANIZATION_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveOrganization(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// GN1003 -- BRANCH MAINTENANCE //
				if (WorkflowConstants.PROCDEF_BRANCH_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveBranch(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// GN1005 -- CURRENCY //
				if (WorkflowConstants.PROCDEF_CURRENCY_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveCurrency(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// GN1006 -- LOOKUP //
				if (WorkflowConstants.PROCDEF_LOOKUP_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveLookup(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// GN1010 -- TAX MASTER //
				if (WorkflowConstants.PROCDEF_TAX_MASTER_MAINTENANCE.equals(param[3])) {
					approveResult = generalService.approveTaxMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// GN1012 -- USER //
				if (WorkflowConstants.PROCDEF_USER_MAINTENANCE.equals(param[3])) {
					approveResult = applicationService.approveUser(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// GN1014 -- ROLE //
				if (WorkflowConstants.PROCDEF_ROLE.equals(param[3])) {
					approveResult = applicationService.approveRole(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				// CS5003 -- ADJUSTMENT MAINTENANCE //

				if (WorkflowConstants.PROCDEF_ADJUSTMENT_MAINTENANCE.trim().equals(param[3])) {
					generalService.approveAdjustmentMaster(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
					approveResult = WorkflowConstants.APPROVE_SUCCESS;
				}
				
				// TD4001 -- DEPOSITO PLACEMENT //
				if (WorkflowConstants.PROCDEF_TD_DEPOSITO_PLACEMENT.trim().equals(param[3])) {
					approveResult = processDeposito(session.get(UIConstants.SESSION_USERNAME), param, processType);

				}
				
				// CA4002 -- CORPORATE ANNOUNCEMENT ADJ //
				log.debug("WorkflowConstants.PROCDEF_CORPORATE_ANNOUNCEMENT_ADJ = "+WorkflowConstants.PROCDEF_CORPORATE_ANNOUNCEMENT_ADJ);
				log.debug("param3 = "+param[3]);
				if (WorkflowConstants.PROCDEF_CORPORATE_ANNOUNCEMENT_ADJ.trim().equals(param[3])) {
					approveResult = transactionService.approveCorporateAnnouncementAdjustment(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
				}

				/*** KALO ADA PENAMBAHAN MASUKKAN KEDALAM METHOD APPROVALHANDLING **/

				// if
				// (WorkflowConstants.PROCDEF_CHARGE_OVERRIDE.equals(param[3])){
				// approveResult =
				// accountService.approveCsChargeOverride(session.get(UIConstants.SESSION_USERNAME),
				// param[0], param[2], Long.valueOf(param[1]), processType);
				// }
				// if (WorkflowConstants.PROCDEF_GROUP.equals(param[3])){
				// approveResult =
				// applicationService.approveGroup(session.get(UIConstants.SESSION_USERNAME),
				// param[0], param[2], Long.valueOf(param[1]), processType);
				// }
				// if
				// (WorkflowConstants.PROCDEF_REPORT_MAINTENANCE.equals(param[3])){
				// approveResult =
				// generalService.approveReport(session.get(UIConstants.SESSION_USERNAME),
				// param[0], param[2], Long.valueOf(param[1]), processType);
				// }

				approveResult = approvalHandling(processType, param, approveResult, key);

				// if (approveResult.equals(WorkflowConstants.APPROVE_SUCCESS))
				// {
				if (approveResult.equals(WorkflowConstants.APPROVE_SUCCESS) || approveResult.startsWith(WorkflowConstants.APPROVE_SUCCESS)) {
					// listSucess.add(param[0]); //taskId
					// Redmine 996 task id rubah ke Ref no
					// listSucess.add(param[4]); //update jd ilangin aja
					listSucess.add("");
				}
			} catch (MedallionException e) {
				log.error(e.getMessage(), e);
				log.error("ERROR 1= " + e.getMessage(), e);
				approveResult = WorkflowConstants.APPROVE_FAIL;
			} catch (UnexpectedException e) {
				log.error(e.getMessage(), e);
				log.error("ERROR 2= " + e.getMessage(), e);
				approveResult = WorkflowConstants.APPROVE_FAIL;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				log.error("ERROR 3= " + e.getMessage(), e);
				approveResult = WorkflowConstants.APPROVE_FAIL;
			} finally {
				//masing masing menu implement final disini
				if (WorkflowConstants.PROCDEF_CORPORATE_SETTLEMENT.trim().equals(param[3])) {
					dataProcess.remove(param[3]);
				}
				if (WorkflowConstants.PROCDEF_SETTLEMENT_PREMATCH.trim().equals(param[3])) {
					dataProcess.remove(param[0]);
				}
			}

			if (approveResult.equals(WorkflowConstants.APPROVE_FAIL)) {
				// listFails.add(param[0]); //taskId
				// Redmine 996 task id rubah ke Ref no
				// listFails.add(param[4]); //update jadi ilangin aja
				listFails.add("");
				log.debug("LIST FAILED = " + listFails);
			}

			log.debug("LIST FAILED 1 = " + listFails);
		} // end for list batch
		log.debug("[APPROVE BATCH] listSucess = " + listSucess.size());
		log.debug("[APPROVE BATCH] listFails = " + listFails.size());
		// list("","", listFails, listSucess, processType, new ApproveItem());
		// //tambahin variable utk passing list yg gagal

		ReturnParam returnParam = new ReturnParam();
		returnParam.setSuccess(listSucess);
		returnParam.setFail(listFails);
		renderJSON(returnParam);
	}

	private static String approvalHandling(String processType, String[] param, String approveResult, String key) {
		log.debug("approvalHandling. processType: " + processType + " param: " + param + " approveResult: " + approveResult + " key: " + key);

		// GN1015, GN1016, GN1017, GN1018, GN1020, GN1021, GN1022 -- ALL MODUL
		// THIRD PARTY //
		if (WorkflowConstants.PROCDEF_THIRD_PARTY_MAINTENANCE.equals(param[3])) {
			approveResult = generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// GN1019 -- EXCHANGE RATE //
		if (WorkflowConstants.PROCDEF_EXCHANGECURRENCY_MAINTENANCE.equals(param[3])) {
			approveResult = generalService.approveExchangeCurrency(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// PL4001 -- PLEDGING //
		if (WorkflowConstants.PROCDEF_PLEDGING.equals(param[3])) {
			String number = key;
			Long pledgingKey = new Long(Long.parseLong(number));
			PlTransaction plTransaction = transactionService.getPledging(pledgingKey);
			Map<String, Object> result = new HashMap<String, Object>();
			if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
				transactionService.rejectPledging(plTransaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			} else {
				transactionService.approvePledging(plTransaction.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			}
			result.put("status", "success");
			result.put("message", Messages.get("transaction.approved", plTransaction.getTransactionKey()));
		}

		// PL4004 -- RELEASE PLEDGING //
		if (WorkflowConstants.PROCDEF_CLOSE_CANCEL_PLEDGING.equals(param[3])) {
			String number = key;
			Long pledgingKey = new Long(Long.parseLong(number));
			PlTransaction plCloseCancelPleding = transactionService.getPledging(pledgingKey);
			Map<String, Object> result = new HashMap<String, Object>();
			if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
				transactionService.rejectCancelPledging(plCloseCancelPleding.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			} else {
				transactionService.approveCancelPledging(plCloseCancelPleding.getTransactionKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			}
			result.put("status", "success");
			result.put("message", Messages.get("transaction.approved", plCloseCancelPleding.getTransactionKey()));
		}

		// RG1001 - REGISTRY PRODUCT //
		if (WorkflowConstants.PROCDEF_RG_PRODUCT.equals(param[3])) {
			approveResult = taService.approveProduct(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// RG2001 & RG2002 -- PRODUCT ACCOUNT REGISTER DAN EDIT //
		if (WorkflowConstants.PROCDEF_RG_INVESTMENT_ACCOUNT.equals(param[3])) {
			approveResult = taService.approveInvestmentAccount(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}
		
		//s #7528
		// KL2001 && KL2002 - AMLCUSTOMER REGISTER DAN EDIT
		if (WorkflowConstants.PROCDEF_AML_CUSTOMER_MAINTENANCE.equals(param[3])) {
			approveResult = amlCustomerService.approveCustomer(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}
		//e #7528
		
		// RG4001 & RG4002 -- SUBSCRIPTION DAN REDEMPTION //
		if (WorkflowConstants.PROCDEF_RG_TRADE.trim().equals(param[3])) {

			approveResult = taService.preApproveTrade(param[0], param[1], param[2], param[5], param[6], processType, session.get(UIConstants.SESSION_USERNAME));
			/*
			 * Chain chain = new Chain();
			 * maintenanceService.line("TRACING START"); // SUBSCRIBE // if
			 * (param[5].equals("Subscription Approval")){
			 * logger.debug("SUBSCRIPTION APPROVAL"); if
			 * (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)){
			 * taService.approveTrade(session.get(UIConstants.SESSION_USERNAME),
			 * param[0], param[2], Long.valueOf(param[1]), processType, null);
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else { try{
			 * logger.debug("APPROVE TRADE SUBSCRIBE");
			 * maintenanceService.line("QUERYT getMaintenanceLog");
			 * GnMaintenanceLog maintenance =
			 * maintenanceService.getMaintenanceLog(Long.valueOf(param[1]));
			 * maintenanceService.line("QUERYF getMaintenanceLog");
			 * chain.maintenanceLog = maintenance; // put to cache;
			 * 
			 * maintenanceService.line("QUERYT loadTrade"); // RgTrade rgTrade =
			 * taService.loadTrade(Long.parseLong(maintenance.getEntityKey()));
			 * RgTrade rgTrade =
			 * taService.loadTradeForApproval(Long.parseLong(maintenance
			 * .getEntityKey())); chain.rgTrade = rgTrade; // put to cache
			 * maintenanceService.line("QUERYF loadTrade");
			 * 
			 * Map<String, BigDecimal> holdingMap = new HashMap<String,
			 * BigDecimal>(); boolean isPriceNotNull = false; RgProduct
			 * rgProduct = rgTrade.getRgProduct(); if( rgProduct != null ){ if(
			 * rgProduct.isFixnav() ){ isPriceNotNull = true; }else{
			 * maintenanceService.line("QUERYT loadActiveNav"); RgNav nav =
			 * taService.loadActiveNav(rgProduct.getProductCode(),
			 * rgTrade.getNavDate());
			 * maintenanceService.line("QUERYF loadActiveNav"); chain.rgNav =
			 * nav; // put to cache
			 * 
			 * if( nav != null ){ if( nav.getNav() != null ){ isPriceNotNull =
			 * true; } } } }
			 * 
			 * RgTrade trade =
			 * taService.approveTrade(session.get(UIConstants.SESSION_USERNAME),
			 * param[0], param[2], Long.valueOf(param[1]), processType, null,
			 * chain); if(trade != null){ if(trade.getRecordStatus() != null){
			 * if(trade.getRecordStatus().trim().equals(LookupConstants.
			 * __RECORD_STATUS_APPROVED)){ if( isPriceNotNull ){
			 * trade.setAmount(rgTrade.getAmount());
			 * trade.setPrice(rgTrade.getPrice());
			 * trade.setUnit(rgTrade.getUnit());
			 * 
			 * rgTrade = taService.processTransactionValidation(trade,
			 * holdingMap, chain);
			 * if(!rgTrade.getMessage().equals(LookupConstants
			 * .__TRADE_VALIDATION_VALID)){ throw new
			 * MedallionException("transaction.validation"); } } } }
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else {
			 * approveResult = WorkflowConstants.APPROVE_FAIL; } RgTrade trade =
			 * taService
			 * .approveTradeSubscribe(session.get(UIConstants.SESSION_USERNAME),
			 * param[0], param[2], Long.valueOf(param[1]), processType, null);
			 * if (((trade.getMessage() == null) ||
			 * (trade.getMessage().trim().length() == 0)) ||
			 * (trade.getMessage().
			 * equals(LookupConstants.__TRADE_VALIDATION_VALID))) {
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else {
			 * approveResult = WorkflowConstants.APPROVE_FAIL; }
			 * 
			 * logger.debug("Approval Result = " +approveResult);
			 * }catch(Exception e) { logger.error(e.getMessage(), e); } }
			 * 
			 * }
			 * 
			 * // REDEMPTION // if (param[5].equals("Redemption Approval")){
			 * logger.debug("REDEMPTION APPROVAL"); if
			 * (processType.equals(WorkflowConstants.PROCESS_TYPE_APPROVE)){
			 * RgTrade rgTrade = taService.loadTrade(Long.parseLong(param[6]));
			 * Map<String, BigDecimal> holdingMap = new HashMap<String,
			 * BigDecimal>(); boolean isPriceNotNull = false; RgProduct
			 * rgProduct = rgTrade.getRgProduct();
			 * 
			 * // price checking to know whether to process transaction
			 * validation or not if( rgProduct != null ){ if(
			 * rgProduct.isFixnav() ){ if( rgProduct.getFixNavPrice() != null ){
			 * isPriceNotNull = true; } }else{ RgNav nav =
			 * taService.loadActiveNav(rgProduct.getProductCode(),
			 * rgTrade.getNavDate()); if( nav != null ){ if( nav.getNav() !=
			 * null ){ isPriceNotNull = true; } } } }
			 * 
			 * RgTrade trade =
			 * taService.approveTradeRedeem(Long.parseLong(param[6]),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]); if(trade !=
			 * null){ if(trade.getRecordStatus() != null){
			 * if(trade.getRecordStatus
			 * ().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED)){ if(
			 * isPriceNotNull ){ trade.setAmount(rgTrade.getAmount());
			 * trade.setPrice(rgTrade.getPrice());
			 * trade.setUnit(rgTrade.getUnit());
			 * 
			 * rgTrade = taService.processTransactionValidation(trade,
			 * holdingMap); if(!rgTrade.getMessage().equals(LookupConstants.
			 * __TRADE_VALIDATION_VALID)){ throw new
			 * MedallionException("transaction.validation"); } } } }
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else {
			 * approveResult = WorkflowConstants.APPROVE_FAIL; } } else {
			 * taService.rejectTrade(Long.parseLong(param[6]),
			 * session.get(UIConstants.SESSION_USERNAME), param[0], null, null);
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; }
			 * 
			 * } maintenanceService.line("TRACING END"); chain.clear();
			 */}

		// RG4003 -- SWITCHING //
		if (WorkflowConstants.PROCDEF_RG_SWITCHING.trim().equals(param[3])) {

			/*
			 * Map<String, Object> result = new HashMap<String, Object>();
			 * Map<String, Object> resultApprove =
			 * taService.preApproveSwitching(key, processType,
			 * session.get(UIConstants.SESSION_USERNAME), param[0] );
			 * approveResult = (String) resultApprove.get("approveResult");
			 * result.put("status", "success"); result.put("message",
			 * Messages.get("transaction.approved",
			 * (Long)resultApprove.get("message")));
			 */

			String number = key;
			RgSwitching rgSwitching = taService.loadSwitching(Long.parseLong(number));
			Map<String, Object> result = new HashMap<String, Object>();
			if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
				taService.rejectSwitching(rgSwitching.getSwitchingKey(), session.get(UIConstants.SESSION_USERNAME), param[0]);
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			} else {
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
				Map<String, BigDecimal> holdingMap = new HashMap<String, BigDecimal>();

				// Set<RgTrade> rgTrades = rgSwitching.getRgTrades();
				List<RgTrade> rgTrades = taService.listRgTradeBySwitching(rgSwitching.getSwitchingKey());
				taService.approveSwitching(rgSwitching.getSwitchingKey(), session.get(UIConstants.SESSION_USERNAME), param[0], param[1] == null ? null : Long.valueOf(param[1]));

				for (RgTrade rgTrade : rgTrades) {
					rgTrade = taService.loadTrade(rgTrade.getTradeKey());
					RgProduct rgProduct = rgTrade.getRgProduct();

					// rg_trade.product_code = rg_nav.product_code and
					// rg_trade.nav_date = rg_nav.nav_date exist
					if (rgProduct != null) {
						RgNav nav = taService.loadActiveNav(rgProduct.getProductCode(), rgTrade.getNavDate());

						log.debug("[APPROVE] rgProduct.isFixnav => " + rgProduct.isFixnav());

						if ((rgProduct != null) && (rgProduct.isFixnav())) {
							rgTrade = taService.processTransactionValidation(rgTrade, holdingMap);
							if (!rgTrade.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
								log.debug("[APPROVE ERROR] transaction message => " + rgTrade.getMessage());
								// throw new
								// MedallionException("transaction.validation",
								// rgTrade.getMessage());
							}
						} else if ((nav != null) && (nav.getNav() != null) && (nav.getNav().compareTo(BigDecimal.ZERO) > 0)) {
							rgTrade = taService.processTransactionValidation(rgTrade, holdingMap);
							if (!rgTrade.getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
								log.debug("[APPROVE ERROR] transaction message => " + rgTrade.getMessage());
								// throw new
								// MedallionException("transaction.validation",
								// rgTrade.getMessage());
							}
						}
					}
				}
			}

			result.put("status", "success");
			result.put("message", Messages.get("transaction.approved", rgSwitching.getSwitchingKey()));
		}

		// RG4004 -- INJECT NAV PRICE //
		if (WorkflowConstants.PROCDEF_RG_INJECT_NAV.equals(param[3])) {
			approveResult = taService.approveNav(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// RG4005 -- DIVIDEN ENTRY //
		if (WorkflowConstants.PROCDEF_RG_FUND_ACTION.trim().equals(param[3])) {

			Map<String, Object> result = new HashMap<String, Object>();

			Map<String, Object> resultApprove = taService.preDividenEntryApproval(Long.valueOf(key), processType, session.get(UIConstants.SESSION_USERNAME), param[0]);

			approveResult = (String) resultApprove.get("approveResult");
			result.put("status", "success");
			result.put("message", Messages.get("transaction.approved", (Long) resultApprove.get("message")));

			/*
			 * String number = key;
			 * 
			 * RgFundAction rgFundAction =
			 * taService.loadFundAction(Long.parseLong(number)); List<RgTrade>
			 * rgTrades =
			 * taService.listTradesByFundActionKey(Long.parseLong(number)); for
			 * (RgTrade rgTrade : rgTrades) { if
			 * (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)){
			 * taService.rejectTrade(rgTrade.getTradeKey(),
			 * session.get(UIConstants.SESSION_USERNAME), null); } else {
			 * taService.approveTrade(rgTrade.getTradeKey(),
			 * session.get(UIConstants.SESSION_USERNAME), null); } } Map<String,
			 * Object> result = new HashMap<String, Object>(); if
			 * (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)){
			 * taService.rejectFundAction(rgFundAction.getFundActionKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else {
			 * taService.approveFundAction(rgFundAction.getFundActionKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; }
			 * result.put("status", "success"); result.put("message",
			 * Messages.get("transaction.approved",
			 * rgFundAction.getFundActionKey()));
			 */}

		// RG4008 -- REGISTRY PAYMENT //
		if (WorkflowConstants.PROCDEF_RG_PAYMENT.trim().equals(param[3])) {

			/*
			 * Map<String, Object> result = new HashMap<String, Object>();
			 * Map<String, Object> resultApprove =
			 * taService.preApprovePayment(key, processType,
			 * session.get(UIConstants.SESSION_USERNAME), param[0] );
			 * approveResult = (String) resultApprove.get("approveResult");
			 * result.put("status", "success"); result.put("message",
			 * Messages.get("transaction.approved",
			 * (Long)resultApprove.get("message")));
			 */

			Map<String, Object> result = new HashMap<String, Object>();
			if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
				taService.rejectPayment(Long.parseLong(key), session.get(UIConstants.SESSION_USERNAME), param[0]);
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			} else {
				RgPayment rgPayment = taService.approvePayment(Long.parseLong(key), session.get(UIConstants.SESSION_USERNAME), param[0]);
				rgPayment = taService.loadPayment(rgPayment.getPaymentKey());
				if (rgPayment.getRgTrades() != null) {
					log.debug("[APPROVE BULK] size rgTrades = " + rgPayment.getRgTrades().size());
					for (RgTrade rgTrade : rgPayment.getRgTrades()) {
						if (LookupConstants.__RECORD_STATUS_APPROVED.equals(rgPayment.getPaymentStatus().trim())) {
							Integer cancelFlag = 0;
							String type = "Pay " + rgTrade.getType();

							if (AbstractRgTrade.TRADETYPE_REDEEM_SWITCHING.equals(rgTrade.getType()))
								type = "Pay Switch-Out";

							taService.sentFaInterface(rgTrade.getTradeKey(), type, cancelFlag, rgPayment.getPaymentDate());
						}
					}
				}

				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			}
			result.put("status", "success");
			result.put("message", Messages.get("transaction.approved", Long.parseLong(key)));

			/*
			 * String number = key; RgPayment rgPayment =
			 * taService.loadPayment(Long.parseLong(number)); Map<String,
			 * Object> result = new HashMap<String, Object>(); if
			 * (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)){
			 * taService.rejectPayment(rgPayment.getPaymentKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else {
			 * rgPayment = taService.approvePayment(rgPayment.getPaymentKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * List<RgTrade> rgTrades =
			 * taService.listTransactionForApproval(rgPayment.getPaymentKey());
			 * for(RgTrade rgTrade : rgTrades) { if
			 * (LookupConstants.__RECORD_STATUS_APPROVED
			 * .equals(rgPayment.getPaymentStatus().trim())) { Integer
			 * cancelFlag = 0; String type = "Pay " + rgTrade.getType();
			 * taService.sentFaInterface(rgTrade.getTradeKey(), type,
			 * cancelFlag, rgPayment.getPaymentDate()); } } approveResult =
			 * WorkflowConstants.APPROVE_SUCCESS; } result.put("status",
			 * "success"); result.put("message",
			 * Messages.get("transaction.approved", rgPayment.getPaymentKey()));
			 */
		}

		// RG4010 -- REGISTRY CANCEL PAYMENT //
		if (WorkflowConstants.PROCDEF_RG_CANCEL_PAYMENT.equals(param[3])) {
			String[] arr = taService.approveCancelPayment(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
			approveResult = arr[0];
		}

		// RG4011 -- REGISTRY CANCEL TRADE //
		if (WorkflowConstants.PROCDEF_RG_TA_CANCEL_TRANSACTION.equals(param[3])) {

			approveResult = taService.preApproveCancelTrade(param[0], param[7], key, processType, session.get(UIConstants.SESSION_USERNAME));
			/*
			 * logger.debug("PARAM 7 = " +param[7]); // SUBSCRIPTION DAN
			 * REDEMPTION if
			 * (AbstractRgTrade.TRADETYPE_SUBSCRIPTION.equals(param[7]) ||
			 * AbstractRgTrade.TRADETYPE_REDEMPTION.equals(param[7])){ String
			 * number = key; RgTrade trade =
			 * taService.loadTrade(Long.parseLong(number));
			 * 
			 * if(trade.getCancelTrade() != null) { trade =
			 * taService.loadTrade(trade.getCancelTrade().getTradeKey()); }
			 * 
			 * Map<String, Object> result = new HashMap<String, Object>(); if
			 * (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)){
			 * taService.rejectCancelTrade(trade.getTradeKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else {
			 * Map<String, BigDecimal> holdingMap = new HashMap<String,
			 * BigDecimal>(); boolean isPriceNotNull = false;
			 * 
			 * RgTransaction rt =
			 * taService.loadTransaction(trade.getTradeKey()); if(rt != null) {
			 * logger.debug("[APPROVE] RGTRANSACTION BY RGTRADE ORIGINAL => " +
			 * rt.getTradeKey());
			 * 
			 * if(rt.getPrice() != null) { isPriceNotNull = true; }
			 * 
			 * //Go with trans validation if(isPriceNotNull) {
			 * taService.approveCancelTrade(trade.getTradeKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * 
			 * RgTrade tradeCancel =
			 * taService.loadTradeByCancelTrade(trade.getTradeKey());
			 * tradeCancel
			 * .setCancelTrade(taService.loadTrade(tradeCancel.getCancelTrade
			 * ().getTradeKey()));
			 * 
			 * RgTrade rgTradeCancel =
			 * taService.processTransactionValidation(tradeCancel, holdingMap);
			 * if(!rgTradeCancel.getMessage().equals(LookupConstants.
			 * __TRADE_VALIDATION_VALID)) {
			 * logger.debug("[APPROVE BATCH] TRANSACTION-VALIDATION ERROR " +
			 * rgTradeCancel.getMessage()); throw new
			 * MedallionException("transaction.validation"); } }
			 * 
			 * 
			 * } else { taService.approveCancelTrade(trade.getTradeKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]); }
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; }
			 * result.put("status", "success"); result.put("message",
			 * Messages.get("transaction.approved", trade.getTradeKey())); }
			 * 
			 * // FUND ACTION / DIVIDEN if
			 * (AbstractRgTrade.TRADETYPE_DIVIDEND.equals(param[7])){ String
			 * number = key; RgFundAction fa =
			 * taService.loadFundAction(Long.parseLong(number));
			 * logger.debug("BATCH1 " + fa.getFundActionKey());
			 * 
			 * if(fa.getCancelFundAction() != null) { fa =
			 * taService.loadFundAction
			 * (fa.getCancelFundAction().getFundActionKey()); }
			 * 
			 * logger.debug("BATCH2 " + fa.getFundActionKey()); Map<String,
			 * Object> result = new HashMap<String, Object>(); if
			 * (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)){
			 * taService.rejectCancelFundAction(fa.getFundActionKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else {
			 * Map<String, BigDecimal> holdingMap = new HashMap<String,
			 * BigDecimal>(); boolean isPriceNotNull = false;
			 * 
			 * taService.approveCancelFundAction(fa.getFundActionKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * 
			 * List<RgTransaction> transactions =
			 * taService.listRgTransactionByRgFundAction(fa.getFundActionKey());
			 * logger.debug("[APPROVE] RGTRANSACTION BY RGFUNDACTION => " +
			 * transactions.size()); //RgTransaction rt =
			 * taService.loadRgTransactionByRgSwitching(fa.getFundActionKey());
			 * //logger.debug("[APPROVE] RGTRANSACTION BY RGFUNDACTION => " +
			 * rt); for(RgTransaction rt : transactions) { if(rt != null) {
			 * if(rt.getPrice() != null) { isPriceNotNull = true; }
			 * 
			 * logger.debug("[APPROVE] RGTRANSACTION PRICE => " +
			 * rt.getPrice());
			 * 
			 * //Go with trans validation if(isPriceNotNull) {
			 * //taService.approveCancelFundAction(fa.getFundActionKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * //RgFundAction rgFund =
			 * taService.loadFundActionByCancelFundAction
			 * (fa.getFundActionKey()); //RgTrade trade =
			 * taService.loadRgTradeByRgFundAction(rgFund.getFundActionKey());
			 * RgTrade trade =
			 * taService.loadTradeByCancelTrade(rt.getRgTrade().getTradeKey());
			 * trade.setCancelTrade(taService.loadTrade(trade.getCancelTrade().
			 * getTradeKey())); RgTrade rgTradeCancel =
			 * taService.processTransactionValidation(trade, holdingMap);
			 * if(!rgTradeCancel
			 * .getMessage().equals(LookupConstants.__TRADE_VALIDATION_VALID)) {
			 * throw new MedallionException("transaction.validation"); } } } }
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; }
			 * result.put("status", "success"); result.put("message",
			 * Messages.get("transaction.approved", fa.getFundActionKey())); }
			 * 
			 * // SWITCHING
			 * if(AbstractRgTrade.TRADETYPE_SWITCHING.equals(param[7])) { String
			 * number = key; RgSwitching swi =
			 * taService.loadSwitching(Long.parseLong(number));
			 * 
			 * if(swi.getCancelSwitching() != null) { swi =
			 * taService.loadSwitching
			 * (swi.getCancelSwitching().getSwitchingKey()); }
			 * 
			 * if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)){
			 * taService.rejectCancelSwitching(swi.getSwitchingKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } else {
			 * Map<String, BigDecimal> holdingMap = new HashMap<String,
			 * BigDecimal>(); boolean isPriceNotNull = false;
			 * 
			 * RgTransaction rt =
			 * taService.loadRgTransactionByRgSwitching(swi.getSwitchingKey());
			 * logger.debug("[APPROVE] RGTRANSACTION BY RGSWITCHING => " + rt);
			 * if(rt != null) { if(rt.getPrice() != null) { isPriceNotNull =
			 * true; }
			 * 
			 * logger.debug("[APPROVE] RGTRANSACTION PRICE => " +
			 * rt.getPrice());
			 * 
			 * //Go with trans validation if(isPriceNotNull) {
			 * taService.approveCancelSwitching(swi.getSwitchingKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]);
			 * 
			 * RgSwitching rswi =
			 * taService.loadSwitchingByCancelSwitching(swi.getSwitchingKey());
			 * //RgTrade trade =
			 * taService.loadRgTradeByRgSwitching(rswi.getSwitchingKey());
			 * List<RgTrade> trades =
			 * taService.listRgTradeByRgSwitching(rswi.getSwitchingKey()); for
			 * (RgTrade rgTrade : trades) {
			 * rgTrade.setCancelTrade(taService.loadTrade
			 * (rgTrade.getCancelTrade().getTradeKey())); RgTrade rgTradeCancel
			 * = taService.processTransactionValidation(rgTrade, holdingMap);
			 * if(!rgTradeCancel.getMessage().equals(LookupConstants.
			 * __TRADE_VALIDATION_VALID)) { throw new
			 * MedallionException("transaction.validation"); } } } } else {
			 * taService.approveCancelSwitching(swi.getSwitchingKey(),
			 * session.get(UIConstants.SESSION_USERNAME), param[0]); }
			 * approveResult = WorkflowConstants.APPROVE_SUCCESS; } }
			 */

		}

		/*
		 * if (WorkflowConstants.PROCDEF_RG_CANCEL_TRADE.equals(param[3])) {
		 * 
		 * }
		 */

		if (WorkflowConstants.PROCDEF_RG_CANCEL_SWITCHING.equals(param[3])) {

			/*
			 * result.put("status", "success"); result.put("message",
			 * Messages.get("transaction.approved", swi.getSwitchingKey()));
			 */
		}
		if (WorkflowConstants.PROCDEF_RG_CANCEL_DIVIDEND.equals(param[3])) {

		}

		// SC1001 -- SECURITY INFORMATION //
		if (WorkflowConstants.PROCDEF_SECURITY_MASTER_MAINTENANCE.equals(param[3])) {
			approveResult = securityService.approveSecurity(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// SC1002 -- SECURITY TYPE //
		if (WorkflowConstants.PROCDEF_SECURITY_TYPE_MAINTENANCE.equals(param[3])) {
			approveResult = securityService.approveSecurityType(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// SC1003 -- CORPORATE ACTION TEMPLATE //
		if (WorkflowConstants.PROCDEF_ACTION_TEMPLATE_MAINTENANCE.equals(param[3])) {
			approveResult = securityService.approveActionTemplate(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// SC4001 -- MARKET PRICE //
		if (WorkflowConstants.PROCDEF_MARKET_PRICE_MAINTENANCE.equals(param[3])) {
			approveResult = securityService.approveMarketPrice(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// TD4002 -- DEPOSITO UPDATE //
		if (WorkflowConstants.PROCDEF_TD_DEPOSITO_UPDATE.trim().equals(param[3])) {
			depositoService.approveDepositoUpdate(MenuConstants.TD_DEPOSITO_UPDATE, session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null, session.get(UIConstants.SESSION_USER_KEY));
			approveResult = WorkflowConstants.APPROVE_SUCCESS;

		}

		// TD4005 -- DEPOSITO BREAK //
		if (WorkflowConstants.PROCDEF_TD_DEPOSITO_BREAK.trim().equals(param[3])) {
			depositoService.approveDepositoBreak(MenuConstants.TD_DEPOSITO_BREAK, session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null, session.get(UIConstants.SESSION_USER_KEY));
			approveResult = WorkflowConstants.APPROVE_SUCCESS;

		}

		// TD4006 -- CANCEL DEPOSITO PLACEMENT //
		if (WorkflowConstants.PROCDEF_TD_CANCEL_DEPOSITO_PLACEMENT.trim().equals(param[3])) {
			if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
				depositoService.approveCancelDepositoPlacement(MenuConstants.TD_DEPOSITO_CANCEL_BREAK, session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, session.get(UIConstants.SESSION_USER_KEY));
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			} else {
				depositoService.approveCancelDepositoPlacement(MenuConstants.TD_DEPOSITO_CANCEL_BREAK, session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, session.get(UIConstants.SESSION_USER_KEY));
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			}
		}

		// TD4007 -- CANCEL DEPOSITO BREAK //
		if (WorkflowConstants.PROCDEF_TD_CANCEL_DEPOSITO_BREAK.trim().equals(param[3])) {
			if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
				depositoService.approveCancelDepositoBreak(MenuConstants.TD_DEPOSITO_CANCEL_BREAK, session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, session.get(UIConstants.SESSION_USER_KEY));
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			} else {
				depositoService.approveCancelDepositoBreak(MenuConstants.TD_DEPOSITO_CANCEL_BREAK, session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, session.get(UIConstants.SESSION_USER_KEY));
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
			}
		}

		// TX1001 -- TAX PROFILE //
		if (WorkflowConstants.PROCDEF_TX_PROFILE.equals(param[3])) {
			approveResult = generalService.approveTaxProfile(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		// TX1002 -- TAX PROFILE RULE //
		if (WorkflowConstants.PROCDEF_TX_PROFILE_RULE.equals(param[3])) {
			approveResult = generalService.approveTxProfileRule(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}
		
		// CA4001 -- CA News Maintenance
		if (WorkflowConstants.PROCDEF_CANEWS_MAINTENANCE.equals(param[3])) {
			approveResult = securityService.approveCaNews(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType);
		}

		/*
		 * if (WorkflowConstants.PROCDEF_INTEREST_TAX_MASTER.equals(param[3])){
		 * approveResult =
		 * securityService.approveInterestTaxMaster(session.get(UIConstants
		 * .SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]),
		 * processType); }
		 */
		return approveResult;
	}

	public static void reject(long id) {
		log.debug("reject. id: " + id);

		GnMaintenanceLog approval = approvalService.getApproval(id);
		Calendar calendar = Calendar.getInstance();

		approval.setRecordStatus(LookupConstants.__RECORD_STATUS_REJECTED);
		approval.setRecordCanceledBy("approver");
		approval.setRecordCanceledDate(calendar.getTime());
		approvalService.save(approval, "reject");

		// TODO : Replace this emtpy string
		list("", "", null, null, null, new ApproveItem());
	}

	public static void form(Long id, int displayMode, GnMaintenanceLog approval) {
		log.debug("form. id: " + id + " displayMode: " + displayMode + " approval: " + approval);

		ApprovalService service = (ApprovalService) Spring.getBean("approvalService");
		GnMaintenanceLog maintenance = service.getApproval(id);

		ApprovalDetailViewModel model = new ApprovalDetailViewModel();
		model.displayMode = displayMode;
		model.maintenance = maintenance;

		ObjectMapper json = new ObjectMapper();

		CfMaster oldData = new CfMaster();
		CfMaster newData = new CfMaster();

		try {
			oldData = json.readValue(maintenance.getOldData(), CfMaster.class);
			newData = json.readValue(maintenance.getNewData(), CfMaster.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		render(approval, model, oldData, newData);
	}

	public static void save(Long id, String mode, GnMaintenanceLog approval) {
		log.debug("save. id: " + id + " mode: " + mode + " approval: " + approval);

		if ("confirm".equals(mode)) {
			approvalService.save(approval, "approve");
			return;
		}

		if ("edit".equals(mode)) {
			form(id, 2, approval);
			return;
		}

		form(id, 1, approval);
	}

	public static String processDeposito(String uName, String[] param, String processType) throws MedallionException, JsonParseException, JsonMappingException, IOException, Exception {
		String approveResult = "";
		GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(Long.parseLong(param[1]));
		
		TdMaster newTdMaster = json.readValue(maintenanceLog.getNewData(), TdMaster.class);
		
		Boolean valid = generalService.checkComplianceDeposito(newTdMaster.getAccount().getCustodyAccountKey(), newTdMaster.getSecurity().getSecurityKey());
		
		if(!valid) {
			approveResult = WorkflowConstants.APPROVE_FAIL;
		}else {
			depositoService.approveDepositoPlacement(MenuConstants.TD_DEPOSITO_PLACEMENT, session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null, session.get(UIConstants.SESSION_USER_KEY));
			approveResult = WorkflowConstants.APPROVE_SUCCESS;
		}
		return approveResult;
	}
	
	public static String processTransaction(String uName, String[] param, String key, String processType) throws MedallionException, JsonParseException, JsonMappingException, IOException, Exception {
		String approveResult = "";
		CsTransaction transaction = accountService.getTransactionByNumber(key);

		Map<String, Object> result = new HashMap<String, Object>();
		if (processType.equals(WorkflowConstants.PROCESS_TYPE_REJECT)) {
			transactionService.approveTransactionWithSerialized(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
			approveResult = WorkflowConstants.APPROVE_SUCCESS;
		} else {
			if (transaction.getAdjustmentManual() != null && transaction.getAdjustmentManual().booleanValue()) {
				approveResult = WorkflowConstants.APPROVE_FAIL;
				throw new Exception("Adjustment Manual can't be process though bulk process");
			}

			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(Long.parseLong(param[1]));
			CsTransaction newTransaction = json.readValue(maintenanceLog.getNewData(), CsTransaction.class);
			
			log.debug("is upload = "+newTransaction.getIsUpload());
			
			Boolean valid = generalService.checkComplianceTransaction(newTransaction.getAccount().getCustodyAccountKey(), newTransaction.getSecurity().getSecurityKey());
			
			String compliancePre = transactionService.fCompliancePre(newTransaction.getAccount().getCustodyAccountKey(), newTransaction.getSecurity().getSecurityKey(), newTransaction.getAmount(), newTransaction.getTransactionDate());
			if ((!compliancePre.equalsIgnoreCase(SUCCESS_COMPLIANCE_PRE)) && (newTransaction.getTransactionTemplate().getPortfolioTransaction().getTransactionType().getLookupCode().equalsIgnoreCase(LookupConstants.TRANSACTION_TYPE_BUY_CODE))) {
				approveResult = WorkflowConstants.APPROVE_FAIL;
				//throw new MedallionException(ExceptionConstants.TRANSACTION_SECURITY_NOT_ALLOWED, "");
			} else if (newTransaction.isFromUpload() && !accountService.isAdjust(newTransaction)) {
				approveResult = WorkflowConstants.APPROVE_FAIL;
				//throw new MedallionException("Net Proceed from file upload is not match Settlement Amount");
			}else if(!valid && !newTransaction.isFromUpload() && (newTransaction.getTransactionTemplate().getDescription().contains("RVP") || newTransaction.getTransactionTemplate().getDescription().contains("RFOP")) ) {
				approveResult = WorkflowConstants.APPROVE_FAIL;
			} else{
				transactionService.approveTransactionWithSerialized(session.get(UIConstants.SESSION_USERNAME), param[0], param[2], Long.valueOf(param[1]), processType, null);
				approveResult = WorkflowConstants.APPROVE_SUCCESS;
				result.put("status", "success");
				result.put("message", Messages.get("transaction.approved", transaction.getTransactionNumber()));
			}
		}
		return approveResult;
	}
	
	public static void reset(ApprovalSearchParameters param) {
		log.debug("reset. param: " + param);

		param = new ApprovalSearchParameters();
		param.setDispatch(ApprovalSearchParameters.DISPATCH_LIST);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		render("Approvals/listpaging.html", param);
	}

	public static void resetBatch(ApprovalSearchParameters param) {
		log.debug("resetBatch. param: " + param);

		param = new ApprovalSearchParameters();
		param.setDispatch(ApprovalSearchParameters.DISPATCH_LIST);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		render("Approvals/listbatchpaging.html", param);
	}

	public static void listpaging(ApprovalSearchParameters param) {
		log.debug("listpaging. param: " + param);

		if (param == null) {
			param = new ApprovalSearchParameters();
		}
		param.setDispatch(ApprovalSearchParameters.DISPATCH_LIST);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		render("Approvals/listpaging.html", param);
	}

	public static void listbatchpaging(ApprovalSearchParameters param) {
		log.debug("listbatchpaging. param: " + param);

		if (param == null) {
			param = new ApprovalSearchParameters();
		}
		param.setDispatch(ApprovalSearchParameters.DISPATCH_LIST);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		render("Approvals/listbatchpaging.html", param);
	}

	public static void paging(Paging page, ApprovalSearchParameters param) throws Exception {
		log.debug("paging. page: " + page + " param: " + param);

		page.setDateFormat(appProp.getDateFormat());
		if (param.getCreatedDateMili() != null)
			param.setCreatedDate(new Date(param.getCreatedDateMili()));

		JsonHelper helper = new JsonHelper();
		session.put(param.isFromSingle() ? ApprovalSearchParameters.SESSION_SINGLE_ID : ApprovalSearchParameters.SESSION_BATCH_ID, helper.serialize(param));

		if (param.isQuery()) {
			log.debug("Parameters : " + param.toString());

			GnUser gnUser = applicationService.getUser(session.get("username"));
			GnUser newGnUser = new GnUser();
			newGnUser.setUserName(gnUser.getUserName());
			newGnUser.setUserId(gnUser.getUserId());
			newGnUser.setUserKey(gnUser.getUserKey());

			page.getParamFixMap().put("user", newGnUser);
			page.getParamFixMap().put("param", param);

			page = workflowService.pagingWorkflow(page);

		}
		renderJSON(page);
	}
}