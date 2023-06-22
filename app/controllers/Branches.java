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
import com.simian.medallion.model.GnBranch;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Branches extends MedallionController {
	private static Logger log = Logger.getLogger(Branches.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.branch")
	public static void list() {
		log.debug("list. ");

		List<GnBranch> branches = generalService.listBranches(UIConstants.SIMIAN_BANK_ID);
		// flash.put(UIConstants.BREADCRUMB,
		// applicationService.getMenuBreadcrumb(MenuConstants.GN_BRANCH));
		render(branches);
	}

	@Check("administration.branch")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		GnBranch branch = generalService.getBranch(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_BRANCH));
		render("Branches/detail.html", branch, mode);

	}

	@Check("administration.branch")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnBranch branch = new GnBranch();
		branch.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		branch.setIsActive(false);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_BRANCH));
		render("Branches/detail.html", branch, mode);
	}

	@Check("administration.branch")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		String record = null;
		GnBranch branch = generalService.getBranch(id);
		String status = branch.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_BRANCH));
		log.debug("BRANCH " + branch);
		log.debug("MODE " + mode);
		log.debug("STATUS " + status);

		render("Branches/detail.html", branch, mode, status);
	}

	@Check("administration.branch")
	public static void save(GnBranch branch, String mode, String status) {
		log.debug("save. branch: " + branch + " mode: " + mode + " status: " + status);

		if (branch != null) {

			validation.required("Branch No is", branch.getBranchNo());
			validation.required("Name is", branch.getName());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_BRANCH));
				render("Branches/detail.html", branch, mode, status);
			} else {
				Long id = branch.getBranchKey();
				String json = serializerService.serialize(session.getId(), branch.getBranchKey(), branch);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", branch);
			entry();
		}
	}

	@Check("administration.branch")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		GnBranch branch = serializerService.deserialize(session.getId(), id, GnBranch.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_BRANCH));
		render("Branches/detail.html", branch, mode, status);
	}

	@Check("administration.branch")
	public static void confirm(GnBranch branch, String mode, String status) {
		log.debug("confirm. branch: " + branch + " mode: " + mode + " status: " + status);

		try {
			if (branch == null)
				back(null, mode, status);
			generalService.saveBranch(MenuConstants.GN_BRANCH, branch, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Branch No : ' " + branch.getBranchNo() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_BRANCH));
			render("Branches/detail.html", branch, mode, confirming, status);
		}
	}

	@Check("administration.branch")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		GnBranch branch = serializerService.deserialize(session.getId(), id, GnBranch.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_BRANCH));
		render("Branches/detail.html", branch, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnBranch branch = json.readValue(maintenanceLog.getNewData(), GnBranch.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Branches/approval.html", branch, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveBranch(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveBranch(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
