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
import com.simian.medallion.model.CsTransactionTemplate;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

//@Check("administration.transactionTemplate")
public class TransactionTemplates extends MedallionController {
	private static Logger log = Logger.getLogger(TransactionTemplates.class);

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> categories = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TRANSACTION_CATEGORY);
		renderArgs.put("categories", categories);

		List<SelectItem> usedBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._USED_BY);
		renderArgs.put("usedBy", usedBy);

		// List<SelectItem> defaultPayment =
		// generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID,
		// LookupConstants._PAYMENT_TYPE);
		// renderArgs.put("defaultPayment", defaultPayment);
	}

	@Check("administration.transactionTemplate")
	public static void list() {
		log.debug("list. ");

		List<CsTransactionTemplate> transactionTemplates = accountService.listTransactionTemplates(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_TEMPLATE));
		render(transactionTemplates);
	}

	@Check("administration.transactionTemplate")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsTransactionTemplate transactionTemplate = accountService.getTransactionTemplate(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_TEMPLATE));
		render("TransactionTemplates/detail.html", transactionTemplate, mode);
	}

	@Check("administration.transactionTemplate")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsTransactionTemplate transactionTemplate = new CsTransactionTemplate();
		transactionTemplate.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		// transactionTemplate.setIsActive(true);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_TEMPLATE));
		render("TransactionTemplates/detail.html", transactionTemplate, mode);
	}

	@Check("administration.transactionTemplate")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsTransactionTemplate transactionTemplate = accountService.getTransactionTemplate(id);
		String status = transactionTemplate.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_TEMPLATE));
		render("TransactionTemplates/detail.html", transactionTemplate, mode, status);
	}

	@Check("administration.transactionTemplate")
	public static void save(String mode, CsTransactionTemplate transactionTemplate, String status) {
		log.debug("save. mode: " + mode + " transactionTemplate: " + transactionTemplate + " status: " + status);

		if (transactionTemplate != null) {
			log.debug("Default Prematch = " + transactionTemplate.getDefPrematch());
			validation.required("Transaction Code is", transactionTemplate.getTransactionCode());
			validation.required("Description is", transactionTemplate.getDescription());
			validation.required("Transaction Category is", transactionTemplate.getTransactionCategory().getLookupId());
			if (LookupConstants.TRANSACTION_CATEGORY_TRANSACTION.equals(transactionTemplate.getTransactionCategory().getLookupId())) {
				validation.required("Used By is", transactionTemplate.getUsedBy().getLookupId());
			} else if (LookupConstants.TRANSACTION_CATEGORY_SETTLEMENT.equalsIgnoreCase(transactionTemplate.getTransactionCategory().getLookupId())) {
				// validation.required("Default Payment is",
				// transactionTemplate.getDefaultPayment().getLookupId());
			}
			validation.required("Security Type is", transactionTemplate.getSecurityType().getSecurityType());

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_TEMPLATE));
				render("TransactionTemplates/detail.html", transactionTemplate, mode, status);
			} else {
				Long id = transactionTemplate.getTransactionTemplateKey();
				serializerService.serialize(session.getId(), id, transactionTemplate);
				confirming(id, mode, status);
			}
		} else {
			entry();
		}
	}

	@Check("administration.transactionTemplate")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		boolean confirming = true;
		CsTransactionTemplate transactionTemplate = serializerService.deserialize(session.getId(), id, CsTransactionTemplate.class);

		// FOR Transaction Category
		if (((LookupConstants.TRANSACTION_CATEGORY_TRANSACTION).equals(transactionTemplate.getTransactionCategory().getLookupId())) || ((LookupConstants.TRANSACTION_CATEGORY_SETTLEMENT).equals(transactionTemplate.getTransactionCategory().getLookupId())) || ((LookupConstants.TRANSACTION_CATEGORY_CANCELATION).equals(transactionTemplate.getTransactionCategory().getLookupId()))) {
			renderArgs.put("checkTransactionTemplate", true);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_TEMPLATE));
		render("TransactionTemplates/detail.html", transactionTemplate, mode, confirming, status);

	}

	@Check("administration.transactionTemplate")
	public static void confirm(String mode, CsTransactionTemplate transactionTemplate, String status) {
		log.debug("confirm. mode: " + mode + " transactionTemplate: " + transactionTemplate + " status: " + status);

		try {
			if (transactionTemplate == null)
				back(null, mode, status);
			// DUPLICATE TRANSACTION CODE VALIDATION
			// List<TransactionTemplate> transactionTemplates =
			// accountService.listTransactionTemplates(UIConstants.SIMIAN_BANK_ID);;
			// for (TransactionTemplate templateInTable : transactionTemplates)
			// {
			// if (transactionTemplate.getTransactionCode() != null &&
			// transactionTemplate.getTransactionTemplateKey() == null) {
			// if
			// ((templateInTable.getTransactionCode().equals(transactionTemplate.getTransactionCode()))
			// &&
			// (templateInTable.getOrganization().getOrganizationId().equals(transactionTemplate.getOrganization().getOrganizationId())))
			// {
			// flash.error("Duplicate Error! Transaction Code : '" +
			// transactionTemplate.getTransactionCode() +
			// "' Already Exist Data");
			// boolean confirming = true;
			// render("TransactionTemplates/detail.html", transactionTemplate,
			// mode, confirming);
			// }
			// }
			// }
			log.debug("settlementTransaction ? " + transactionTemplate.getSettlementTransaction().getTransactionTemplateKey());
			// transactionTemplate.setSettlementTransaction(transactionTemplate);
			accountService.saveTransactionTemplate(MenuConstants.CS_TRANSACTION_TEMPLATE, transactionTemplate, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
			// =============================
		} catch (MedallionException e) {
			// validation.addError("global", "validation.duplicate",
			// transactionTemplate.getTransactionTemplateKey().toString());
			flash.error("Transaction Code : ' " + transactionTemplate.getTransactionCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_TEMPLATE));
			render("TransactionTemplates/detail.html", transactionTemplate, mode, confirming, status);
		}

	}

	@Check("administration.transactionTemplate")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		CsTransactionTemplate transactionTemplate = serializerService.deserialize(session.getId(), id, CsTransactionTemplate.class);
		log.debug("from back status = " + status);
		log.debug("defPremathc = " + transactionTemplate.getDefPrematch());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_TRANSACTION_TEMPLATE));
		render("TransactionTemplates/detail.html", transactionTemplate, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CsTransactionTemplate transactionTemplate = json.readValue(maintenanceLog.getNewData(), CsTransactionTemplate.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("TransactionTemplates/approval.html", transactionTemplate, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			accountService.approveTransactionTemplate(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			accountService.approveTransactionTemplate(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}