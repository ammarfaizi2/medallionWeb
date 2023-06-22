package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.FaTransactionMaster;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class FaTransactionMasters extends MedallionController {
	private static Logger log = Logger.getLogger(FaTransactionMasters.class);

	@Before(only = { "entry", "view", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);

		List<SelectItem> nabPublish = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._NAB_PUBLISH);
		renderArgs.put("nabPublish", nabPublish);

		List<SelectItem> instructionTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._POSTING_TREATED);
		renderArgs.put("instructionTypes", instructionTypes);
	}

	@Check("administration.faTransactionMaster")
	public static void list() {
		log.debug("list. ");

		List<FaTransactionMaster> faTransactionMasters = fundService.listFaTransactionMaster();
		render(faTransactionMasters);
	}

	@Check("administration.faTransactionMaster")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaTransactionMaster faTransactionMaster = new FaTransactionMaster();
		faTransactionMaster.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		faTransactionMaster.setInstructionType(new GnLookup(LookupConstants.POSTING_TREATED_TRANSACTION));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_TRANSACTION_MASTER));
		render("FaTransactionMasters/detail.html", faTransactionMaster, mode);
	}

	@Check("administration.faTransactionMaster")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			FaTransactionMaster faTransactionMaster = fundService.getFaTransactionMaster(id);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_TRANSACTION_MASTER));
			render("FaTransactionMasters/detail.html", faTransactionMaster, mode);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.faTransactionMaster")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaTransactionMaster faTransactionMaster = fundService.getFaTransactionMaster(id);
		String status = faTransactionMaster.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_TRANSACTION_MASTER));
		render("FaTransactionMasters/detail.html", mode, faTransactionMaster, status);
	}

	@Check("administration.faTransactionMaster")
	public static void save(FaTransactionMaster faTransactionMaster, String mode, String status) {
		log.debug("save. faTransactionMaster: " + faTransactionMaster + " mode: " + mode + " status: " + status);

		if (faTransactionMaster != null) {
			validation.required("Transaction Code is", faTransactionMaster.getTransactionCode());
			validation.required("Description is", faTransactionMaster.getTransactionDescription());
			validation.required("Instruction Type is", faTransactionMaster.getInstructionType().getLookupId());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_TRANSACTION_MASTER));
				render("FaTransactionMasters/detail.html", faTransactionMaster, mode, status);
			} else {
				Long id = faTransactionMaster.getTransactionMasterKey();
				serializerService.serialize(session.getId(), faTransactionMaster.getTransactionMasterKey(), faTransactionMaster);
				confirming(id, mode, status);
			}
		} else {
			// flash.error(ExceptionConstants.PARAMETER_NULL,
			// faTransactionMaster);
			entry();
		}
	}

	@Check("administration.faTransactionMaster")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		FaTransactionMaster faTransactionMaster = serializerService.deserialize(session.getId(), id, FaTransactionMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_TRANSACTION_MASTER));
		render("FaTransactionMasters/detail.html", faTransactionMaster, mode, status);
	}

	@Check("administration.faTransactionMaster")
	public static void confirm(FaTransactionMaster faTransactionMaster, String mode, String status) {
		log.debug("confirm. faTransactionMaster: " + faTransactionMaster + " mode: " + mode + " status: " + status);

		try {
			if (faTransactionMaster == null)
				back(null, mode, null, status);
			fundService.saveFaTransactionMaster(MenuConstants.FA_TRANSACTION_MASTER, faTransactionMaster, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			// flash.error("Duplicate Error! Code : ' " +
			// faTransactionMaster.getTransactionCode() + " ' Already Exist",
			// faTransactionMaster.getTransactionCode());
			flash.error("Transaction Master Code : ' " + faTransactionMaster.getTransactionCode() + " ' " + e.getMessage());
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_TRANSACTION_MASTER));
			render("FaTransactionMasters/detail.html", faTransactionMaster, mode, confirming, status);
		}
	}

	@Check("administration.faTransactionMaster")
	public static void back(Long id, String mode, FaTransactionMaster faTransactionMaster, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " faTransactionMaster: " + faTransactionMaster + " status: " + status);

		//boolean confirming = false;
		faTransactionMaster = serializerService.deserialize(session.getId(), id, FaTransactionMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_TRANSACTION_MASTER));
		render("FaTransactionMasters/detail.html", faTransactionMaster, mode, status);
	}

	public static void sync(String param) {
		log.debug("sync. param: " + param);

		String organizationId = UIConstants.SIMIAN_BANK_ID;
		int count = fundService.countResult(organizationId);
		fundService.syncTransactionMaster(MenuConstants.FA_TRANSACTION_MASTER, organizationId, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
		// SYNC MESSAGE
		if (count == 0) {
			flash.error("No Data Inserted");
		} else {
			flash.error("Sync With " + count + " New Data Inserted");
		}
		// =====================
		list();
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			FaTransactionMaster faTransactionMaster = json.readValue(maintenanceLog.getNewData(), FaTransactionMaster.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("FaTransactionMasters/approval.html", faTransactionMaster, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaTransactionMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			fundService.approveFaTransactionMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

}
