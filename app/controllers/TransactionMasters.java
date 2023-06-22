package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsTransactionMaster;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

//@Check("administration.custodyTransactionMaster")
public class TransactionMasters extends MedallionController {
	private static Logger log = Logger.getLogger(TransactionMasters.class);

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> transactionTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CS_TRANSACTION_TYPE);
		renderArgs.put("transactionTypes", transactionTypes);

	}

	@Check("administration.custodyTransactionMaster")
	public static void list() {
		log.debug("list. ");

		List<CsTransactionMaster> custodyTransactionMasters = accountService.listTransactionMasters(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_MASTER));
		render(custodyTransactionMasters);
	}

	@Check("administration.custodyTransactionMaster")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsTransactionMaster custodyTransactionMaster = accountService.getTransactionMaster(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_MASTER));
		render("TransactionMasters/detail.html", custodyTransactionMaster, mode);
	}

	@Check("administration.custodyTransactionMaster")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsTransactionMaster custodyTransactionMaster = new CsTransactionMaster();
		custodyTransactionMaster.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		// custodyTransactionMaster.setIsActive(true);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_MASTER));
		render("TransactionMasters/detail.html", custodyTransactionMaster, mode);
	}

	@Check("administration.custodyTransactionMaster")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsTransactionMaster custodyTransactionMaster = accountService.getTransactionMaster(id);
		String status = custodyTransactionMaster.getRecordStatus() + " ";
		log.debug("from edit status = --" + status + "--");
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_MASTER));
		render("TransactionMasters/detail.html", custodyTransactionMaster, mode, status);
	}

	@Check("administration.custodyTransactionMaster")
	public static void save(String mode, CsTransactionMaster custodyTransactionMaster, String status) {
		log.debug("save. mode: " + mode + " custodyTransactionMaster: " + custodyTransactionMaster + " status: " + status);

		// Validate here
		if (custodyTransactionMaster != null) {
			validation.required("Transaction Code is", custodyTransactionMaster.getCustodyTransactionCode());
			;
			validation.required("Description is", custodyTransactionMaster.getTransactionDescription());
			validation.required("Transaction Type is", custodyTransactionMaster.getTransactionType().getLookupId());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_MASTER));
				render("TransactionMasters/detail.html", custodyTransactionMaster, mode, status);
			} else {
				Long id = custodyTransactionMaster.getTransactionMasterKey();
				String json = serializerService.serialize(session.getId(), custodyTransactionMaster.getTransactionMasterKey(), custodyTransactionMaster);
				log.debug("serialized: " + json);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", custodyTransactionMaster);
			entry();
		}

	}

	@Check("administration.custodyTransactionMaster")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		CsTransactionMaster custodyTransactionMaster = serializerService.deserialize(session.getId(), id, CsTransactionMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_MASTER));
		render("TransactionMasters/detail.html", custodyTransactionMaster, mode, status);
	}

	@Check("administration.custodyTransactionMaster")
	public static void confirm(String mode, CsTransactionMaster custodyTransactionMaster, String status) {
		log.debug("confirm. mode: " + mode + " custodyTransactionMaster: " + custodyTransactionMaster + " status: " + status);

		try {
			if (custodyTransactionMaster == null)
				back(null, mode, status);
			// DUPLICATE TRANSACTION CODE VALIDATION
			// ========================================================================================
			// List<TransactionMaster> custodyTransactionMasters =
			// accountService.listTransactionMasters(UIConstants.SIMIAN_BANK_ID);
			// for (TransactionMaster trxMstInTable : custodyTransactionMasters)
			// {
			// if (custodyTransactionMaster.getCustodyTransactionCode() != null
			// && custodyTransactionMaster.getTransactionMasterKey() == null) {
			// logger.debug("masuk sini");
			// if
			// ((trxMstInTable.getCustodyTransactionCode().equals(custodyTransactionMaster.getCustodyTransactionCode()))
			// &&
			// (trxMstInTable.getOrganization().getOrganizationId().equals(custodyTransactionMaster.getOrganization().getOrganizationId())))
			// {
			// flash.error("Duplicate Error! Transaction Code : '" +
			// custodyTransactionMaster.getCustodyTransactionCode() +
			// "' Already Exist Data");
			// boolean confirming = true;
			// render("TransactionMasters/detail.html",
			// custodyTransactionMaster, mode, confirming);
			// }
			// }
			// }
			// ==================================================================================================================
			// accountService.saveTransactionMaster(custodyTransactionMaster);
			accountService.saveTransactionMaster(MenuConstants.CS_TRANSACTION_MASTER, custodyTransactionMaster, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			log.debug("masuk exception");
			flash.error("Transaction Code : ' " + custodyTransactionMaster.getCustodyTransactionCode() + " ' " + Messages.get(e.getMessage()));
			// flash.error(Messages.get(ExceptionConstants.DATA_DUPLICATE,
			// custodyTransactionMaster.getCustodyTransactionCode()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_MASTER));
			render("TransactionMasters/detail.html", custodyTransactionMaster, mode, confirming, status);
		}
	}

	@Check("administration.custodyTransactionMaster")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		CsTransactionMaster custodyTransactionMaster = serializerService.deserialize(session.getId(), id, CsTransactionMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_MASTER));
		render("TransactionMasters/detail.html", custodyTransactionMaster, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CsTransactionMaster custodyTransactionMaster = json.readValue(maintenanceLog.getNewData(), CsTransactionMaster.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("TransactionMasters/approval.html", custodyTransactionMaster, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			accountService.approveTransactonMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			accountService.approveTransactonMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}