package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.BnTransactionMaster;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class BankTransactionMasters extends MedallionController {
	private static Logger log = Logger.getLogger(BankTransactionMasters.class);

	// @Before(unless = {"list", "edit", "entry", "save", "back" })
	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> transactionTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._BN_TRANSACTION_TYPE);
		renderArgs.put("transactionTypes", transactionTypes);

	}

	@Check("administration.bankTransactionMaster")
	public static void list() {
		log.debug("list. ");

		List<BnTransactionMaster> bankTransactionMasters = bankAccountService.listBankTransactionMasters(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_TRANSACTION_MASTER));
		render(bankTransactionMasters);
	}

	@Check("administration.bankTransactionMaster")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		BnTransactionMaster bankTransactionMaster = bankAccountService.getBankTransactionMaster(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_TRANSACTION_MASTER));
		render("BankTransactionMasters/detail.html", bankTransactionMaster, mode);
	}

	@Check("administration.bankTransactionMaster")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		BnTransactionMaster bankTransactionMaster = new BnTransactionMaster();
		bankTransactionMaster.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		// bankTransactionMaster.setIsActive(true);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_TRANSACTION_MASTER));
		render("BankTransactionMasters/detail.html", bankTransactionMaster, mode);
	}

	@Check("administration.bankTransactionMaster")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		BnTransactionMaster bankTransactionMaster = bankAccountService.getBankTransactionMaster(id);
		log.debug("bankTransactionMaster.recordStatus = " + bankTransactionMaster.getTransactionDescription());
		String status = bankTransactionMaster.getRecordStatus() + " ";
		log.debug("from edit status = --" + status + "--");
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_TRANSACTION_MASTER));
		render("BankTransactionMasters/detail.html", bankTransactionMaster, mode, status);
	}

	@Check("administration.bankTransactionMaster")
	public static void save(String mode, BnTransactionMaster bankTransactionMaster, String status) {
		log.debug("save. mode: " + mode + " bankTransactionMaster: " + bankTransactionMaster + " status: " + status);

		// Validate here
		if (bankTransactionMaster != null) {
			validation.required("Transaction Code is", bankTransactionMaster.getBankTransactionCode());
			;
			validation.required("Description is", bankTransactionMaster.getTransactionDescription());
			validation.required("Transaction Type is", bankTransactionMaster.getTransactionType().getLookupId());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_TRANSACTION_MASTER));
				render("BankTransactionMasters/detail.html", bankTransactionMaster, mode, status);
			} else {
				Long id = bankTransactionMaster.getTransactionMasterKey();
				String json = serializerService.serialize(session.getId(), bankTransactionMaster.getTransactionMasterKey(), bankTransactionMaster);
				log.debug("serialized: " + json);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", bankTransactionMaster);
			entry();
		}
	}

	@Check("administration.bankTransactionMaster")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		BnTransactionMaster bankTransactionMaster = serializerService.deserialize(session.getId(), id, BnTransactionMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_TRANSACTION_MASTER));
		render("BankTransactionMasters/detail.html", bankTransactionMaster, mode, status);
	}

	@Check("administration.bankTransactionMaster")
	public static void confirm(String mode, BnTransactionMaster bankTransactionMaster, String status) {
		log.debug("confirm. mode: " + mode + " bankTransactionMaster: " + bankTransactionMaster + " status: " + status);

		try {
			if (bankTransactionMaster == null)
				back(null, mode, status);
			// DUPLICATE TRANSACTION CODE VALIDATION
			// ========================================================================================
			// List<BankTransactionMaster> custodyTransactionMasters =
			// bankAccountService.listBankTransactionMasters(UIConstants.SIMIAN_BANK_ID);
			// for (BankTransactionMaster bankTrxMstInTable :
			// custodyTransactionMasters) {
			// if (bankTransactionMaster.getBankTransactionCode() != null &&
			// bankTransactionMaster.getTransactionMasterKey() == null) {
			// logger.debug("masuk sini");
			// if
			// ((bankTrxMstInTable.getBankTransactionCode().equals(bankTransactionMaster.getBankTransactionCode()))
			// &&
			// (bankTrxMstInTable.getOrganization().getOrganizationId().equals(bankTransactionMaster.getOrganization().getOrganizationId())))
			// {
			// flash.error("Duplicate Error! Transaction Code : '" +
			// bankTransactionMaster.getBankTransactionCode() +
			// "' Already Exist Data");
			// boolean confirming = true;
			// render("BankTransactionMasters/detail.html",
			// bankTransactionMaster, mode, confirming);
			// }
			// }
			// }
			// ==================================================================================================================
			// bankAccountService.saveBankTransactionMaster(bankTransactionMaster);
			bankAccountService.saveBankTransactionMaster(MenuConstants.BN_TRANSACTION_MASTER, bankTransactionMaster, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Transaction Code : ' " + bankTransactionMaster.getBankTransactionCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_TRANSACTION_MASTER));
			render("BankTransactionMasters/detail.html", bankTransactionMaster, mode, confirming, status);
		}
	}

	@Check("administration.bankTransactionMaster")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		BnTransactionMaster bankTransactionMaster = serializerService.deserialize(session.getId(), id, BnTransactionMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_TRANSACTION_MASTER));
		render("BankTransactionMasters/detail.html", bankTransactionMaster, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			BnTransactionMaster bankTransactionMaster = json.readValue(maintenanceLog.getNewData(), BnTransactionMaster.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("BankTransactionMasters/approval.html", bankTransactionMaster, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			bankAccountService.approveBankTransactonMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			bankAccountService.approveBankTransactonMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}