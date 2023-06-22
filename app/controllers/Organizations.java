package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Organizations extends MedallionController {
	private static Logger log = Logger.getLogger(Organizations.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.organization")
	public static void list() {
		log.debug("list. ");

		List<GnOrganization> organizations = generalService.listOrganizations();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ORGANIZATION));
		render(organizations);
	}

	public static void view(String id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		GnOrganization organization = generalService.getOrganization(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ORGANIZATION));
		render("Organizations/detail.html", organization, mode);
	}

	@Check("administration.organization")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnOrganization organization = new GnOrganization();
		// organization.setIsActive(true);
		boolean isNewRec = true;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ORGANIZATION));
		render("Organizations/detail.html", organization, mode, isNewRec);
	}

	public static void edit(String id, boolean isNewRec) {
		log.debug("edit. id: " + id + " isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		isNewRec = false;
		GnOrganization organization = generalService.getOrganization(id);
		String status = organization.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ORGANIZATION));
		render("Organizations/detail.html", organization, mode, isNewRec, status);
	}

	public static void save(GnOrganization organization, String mode, boolean isNewRec, String status) {
		log.debug("save. organization: " + organization + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (organization != null) {
			validation.required("Organization Id is", organization.getOrganizationId());
			;
			validation.required("Name is", organization.getName());
			if (validation.hasErrors()) {
				// isNewRec = false;
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ORGANIZATION));
				render("Organizations/detail.html", organization, mode, isNewRec, status);
			} else {
				String id = organization.getOrganizationId();
				String json = serializerService.serialize(session.getId(), organization.getOrganizationId(), organization);
				log.debug("json 1 >>> : " + json);
				// organization.setRecordStatus(recStatus);
				// logger.debug("tes 123 >>> : "+
				// organization.getRecordStatus());
				confirming(id, mode, isNewRec, status);
			}
		} else {
			// flash.error("argument.null", organization);
			entry();
		}
	}

	public static void confirming(String id, String mode, boolean isNewRec, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		id = id.replace('+', ' ');
		renderArgs.put("confirming", true);
		GnOrganization organization = serializerService.deserialize(session.getId(), id, GnOrganization.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ORGANIZATION));
		render("Organizations/detail.html", organization, mode, isNewRec, status);
	}

	// public static void confirm(Organization organization, String mode) {
	// try {
	// if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)){
	// generalService.createOrganization(organization);
	// }else{
	// generalService.updateOrganization(organization);
	// }
	// list();
	// }catch (MedallionException e) {
	// flash.error(" Already Exist : "+organization.getOrganizationId());
	// boolean confirming = true;
	// render("Organizations/detail.html",organization, mode, confirming);
	// }
	// }

	public static void confirm(GnOrganization organization, String mode, boolean isNewRec, String status) {
		log.debug("confirm. organization: " + organization + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		try {
			if (organization == null)
				back(null, mode, isNewRec, status);
			// DUPLICATE ORGANIZATION ID VALIDATION
			// =============================================================================
			List<GnOrganization> organizations = generalService.listOrganizations();
			for (GnOrganization organizationInTable : organizations) {
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					if (organizationInTable.getOrganizationId().equals(organization.getOrganizationId())) {
						flash.error("Organization Id : ' " + organization.getOrganizationId() + " ' " + ExceptionConstants.DATA_DUPLICATE);
						boolean confirming = true;
						render("Organizations/detail.html", organization, mode, confirming, isNewRec, status);
					}
				}
			}
			// ==================================================================================================================
			generalService.saveOrganization(MenuConstants.GN_ORGANIZATION, organization, session.get(UIConstants.SESSION_USERNAME), "", isNewRec, session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Organization Id : ' " + organization.getOrganizationId() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			render("Organizations/detail.html", organization, mode, confirming, isNewRec, status);
		}
	}

	public static void back(String id, String mode, boolean isNewRec, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		GnOrganization organization = serializerService.deserialize(session.getId(), id, GnOrganization.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_ORGANIZATION));
		render("Organizations/detail.html", organization, mode, isNewRec, status);
	}

	public static void approval(String taskId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnOrganization organization = json.readValue(maintenanceLog.getNewData(), GnOrganization.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Organizations/approval.html", organization, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// @Check("approvals")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveOrganization(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("approvals")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveOrganization(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}