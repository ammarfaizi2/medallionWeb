package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.TxProfile;
import com.simian.medallion.vo.SelectItem;

public class TaxProfiles extends MedallionController {
	private static Logger log = Logger.getLogger(TaxProfiles.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.taxProfiles")
	public static void list() {
		log.debug("list. ");

		List<TxProfile> taxProfiles = generalService.listTaxProfiles();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_PROFILE));
		render(taxProfiles);
	}

	@Check("administration.taxProfiles")
	public static void view(String id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		TxProfile taxProfile = generalService.getTaxProfile(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_PROFILE));
		render("TaxProfiles/detail.html", taxProfile, mode);
	}

	@Check("administration.taxProfiles")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		TxProfile taxProfile = new TxProfile();
		boolean isNewRec = true;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_PROFILE));
		render("TaxProfiles/detail.html", taxProfile, mode, isNewRec);
	}

	@Check("administration.taxProfiles")
	public static void edit(String id, boolean isNewRec) {
		log.debug("edit. id: " + id + " isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		isNewRec = false;
		TxProfile taxProfile = generalService.getTaxProfile(id);
		String status = taxProfile.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_PROFILE));
		render("TaxProfiles/detail.html", taxProfile, mode, isNewRec, status);
	}

	@Check("administration.taxProfiles")
	public static void save(String mode, TxProfile taxProfile, boolean isNewRec, String status) {
		log.debug("save. mode: " + mode + " taxProfile: " + taxProfile + " isNewRec: " + isNewRec + " status: " + status);

		// Validate here
		if (taxProfile != null) {
			validation.required("Code is", taxProfile.getTaxProfileCode());
			validation.required("Description is", taxProfile.getDescription());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_PROFILE));
				render("TaxProfiles/detail.html", taxProfile, mode, status);
			} else {
				String id = taxProfile.getTaxProfileCode();
				log.debug("in save method id :" + id);
				String printSerialize = serializerService.serialize(session.getId(), id, taxProfile);
				log.debug("in save method printSerialize :" + printSerialize);
				log.debug("about confirming id :" + id);
				confirming(id, mode, isNewRec, status);
			}
		} else {
			// flash.error("argument.null", taxProfile);
			entry();
		}
	}

	@Check("administration.taxProfiles")
	public static void confirming(String id, String mode, boolean isNewRec, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		renderArgs.put("confirming", true);
		log.debug("getting parameter confirming method id " + id);
		TxProfile taxProfile = serializerService.deserialize(session.getId(), id, TxProfile.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_PROFILE));
		render("TaxProfiles/detail.html", taxProfile, mode, isNewRec, status);
	}

	@Check("administration.taxProfiles")
	public static void confirm(TxProfile taxProfile, String mode, boolean isNewRec, String status) {
		log.debug("confirm. taxProfile: " + taxProfile + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		try {
			if (taxProfile == null)
				back(null, mode, isNewRec, status);
			// CHECK DUPLICATE DATA
			List<TxProfile> profiles = generalService.listTaxProfiles();
			for (TxProfile profInTable : profiles) {
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					if (profInTable.getTaxProfileCode().equals(taxProfile.getTaxProfileCode())) {
						flash.error("Tax Profile Code : ' " + taxProfile.getTaxProfileCode() + " ' " + ExceptionConstants.DATA_DUPLICATE);
						boolean confirming = true;
						render("TaxProfiles/detail.html", taxProfile, mode, confirming, isNewRec, status);
					}
				}
			}
			// --------------
			generalService.saveTaxProfile(MenuConstants.TX_TAX_PROFILE, taxProfile, session.get(UIConstants.SESSION_USERNAME), "", isNewRec, session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			validation.clear();
			flash.error("Tax Profile Code : '" + taxProfile.getTaxProfileCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_PROFILE));
			render("TaxProfiles/detail.html", taxProfile, mode, confirming, isNewRec, status);
		}
	}

	@Check("administration.taxProfiles")
	public static void back(String id, String mode, boolean isNewRec, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		TxProfile taxProfile = serializerService.deserialize(session.getId(), id, TxProfile.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_PROFILE));
		render("TaxProfiles/detail.html", taxProfile, mode, isNewRec, status);
	}

	public static void approval(String taskId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			TxProfile taxProfile = json.readValue(maintenanceLog.getNewData(), TxProfile.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("TaxProfiles/approval.html", taxProfile, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveTaxProfile(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveTaxProfile(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}