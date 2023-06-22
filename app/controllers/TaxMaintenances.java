package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnTaxMaster;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class TaxMaintenances extends MedallionController {
	private static Logger log = Logger.getLogger(TaxMaintenances.class);

	@Before(unless = { "list", "edit", "entry", "save", "back" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.taxMaster")
	public static void list() {
		log.debug("list. ");

		List<GnTaxMaster> taxMasters = generalService.listTaxMasters();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_TAX));
		render(taxMasters);
	}

	@Check("administration.taxMaster")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnTaxMaster taxMaster = generalService.getTaxMaster(id);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_TAX));
			render("TaxMaintenances/detail.html", taxMaster, mode);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.taxMaster")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnTaxMaster taxMaster = new GnTaxMaster();
		// taxMaster.setIsActive(true);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_TAX));
		render("TaxMaintenances/detail.html", taxMaster, mode);
	}

	@Check("administration.taxMaster")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnTaxMaster taxMaster = generalService.getTaxMaster(id);
		String status = taxMaster.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_TAX));
		render("TaxMaintenances/detail.html", taxMaster, mode, status);
	}

	@Check("administration.taxMaster")
	public static void save(GnTaxMaster taxMaster, String mode, String status) {
		log.debug("save. taxMaster: " + taxMaster + " mode: " + mode + " status: " + status);

		if (taxMaster != null) {
			validation.required("Tax Code is", taxMaster.getTaxCode());
			;
			validation.required("Tax Description is", taxMaster.getDescription());
			validation.required("Tax Rate is", taxMaster.getTaxRate());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_TAX));
				render("TaxMaintenances/detail.html", taxMaster, mode, status);
			} else {
				Long id = taxMaster.getTaxKey();
				serializerService.serialize(session.getId(), taxMaster.getTaxKey(), taxMaster);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", taxMaster);
			entry();
		}
	}

	@Check("administration.taxMaster")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		GnTaxMaster taxMaster = serializerService.deserialize(session.getId(), id, GnTaxMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_TAX));
		render("TaxMaintenances/detail.html", taxMaster, mode, status);
	}

	@Check("administration.taxMaster")
	public static void confirm(GnTaxMaster taxMaster, String mode, String status) {
		log.debug("confirm. taxMaster: " + taxMaster + " mode: " + mode + " status: " + status);

		if (taxMaster == null)
			back(null, mode, status);
		try {
			generalService.saveTaxMaster(MenuConstants.GN_TAX, taxMaster, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Tax Code : ' " + taxMaster.getTaxCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_TAX));
			render("TaxMaintenances/detail.html", taxMaster, mode, confirming, status);
		}
	}

	@Check("administration.taxMaster")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		GnTaxMaster taxMaster = serializerService.deserialize(session.getId(), id, GnTaxMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_TAX));
		renderTemplate("TaxMaintenances/detail.html", taxMaster, mode, status);
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnTaxMaster taxMaster = json.readValue(maintenanceLog.getNewData(), GnTaxMaster.class);
			log.debug("status >>> " + taxMaster.getRecordStatus() + "");
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("TaxMaintenances/approval.html", taxMaster, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveTaxMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveTaxMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}