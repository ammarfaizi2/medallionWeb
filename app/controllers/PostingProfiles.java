package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.FaPostingProfile;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class PostingProfiles extends MedallionController {
	private static Logger log = Logger.getLogger(PostingProfiles.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		String recordStatusNew = LookupConstants.__RECORD_STATUS_NEW;
		renderArgs.put("recordStatusNew", recordStatusNew);

		String recordStatusUpdate = LookupConstants.__RECORD_STATUS_UPDATED;
		renderArgs.put("recordStatusUpdate", recordStatusUpdate);
	}

	@Check("administration.postingProfile")
	public static void list() {
		log.debug("list. ");

		List<FaPostingProfile> faPostingProfiles = fundService.listFaPostingProfile();
		render(faPostingProfiles);
	}

	@Check("administration.postingProfile")
	public static void paging(Paging page) {
		log.debug("paging. page: " + page);

		page.addParams("1", page.EQUAL, 1);
		page.addParams(Helper.searchAll("(pp.profileName||pp.profileCode)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = fundService.pagingFaPostingProfile(page);
		renderJSON(page);
	}

	@Check("administration.postingProfile")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaPostingProfile faPostingProfile = fundService.getFaPostingProfile(id);
		render("PostingProfiles/detail.html", faPostingProfile, mode);
	}

	@Check("administration.postingProfile")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaPostingProfile faPostingProfile = new FaPostingProfile();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		render("PostingProfiles/detail.html", faPostingProfile, mode);
	}

	@Check("administration.postingProfile")
	public static void edit(long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaPostingProfile faPostingProfile = fundService.getFaPostingProfile(id);
		String status = faPostingProfile.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		render("PostingProfiles/detail.html", faPostingProfile, mode, status);
	}

	@Check("administration.postingProfile")
	public static void save(String mode, FaPostingProfile faPostingProfile, String status) {
		log.debug("save. mode: " + mode + " faPostingProfile: " + faPostingProfile + " status: " + status);

		if (faPostingProfile != null) {
			validation.required("Code is", faPostingProfile.getProfileCode());
			validation.required("Name is", faPostingProfile.getProfileName());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
				render("PostingProfiles/detail.html", faPostingProfile, mode, status);
			} else {
				Long id = faPostingProfile.getPostingProfileKey();
				serializerService.serialize(session.getId(), faPostingProfile.getPostingProfileKey(), faPostingProfile);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", faPostingProfile);
			entry();
		}
	}

	@Check("administration.postingProfile")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		boolean confirming = true;
		FaPostingProfile faPostingProfile = serializerService.deserialize(session.getId(), id, FaPostingProfile.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		render("PostingProfiles/detail.html", faPostingProfile, confirming, mode, status);
		// } catch (Exception e){
		// logger.debug(e.getMessage(), e);
		// }
	}

	@Check("administration.postingProfile")
	public static void confirm(FaPostingProfile faPostingProfile, String mode, String status) {
		log.debug("confirm. faPostingProfile: " + faPostingProfile + " mode: " + mode + " status: " + status);

		try {
			if (faPostingProfile == null)
				back(null, mode, null, status);
			fundService.saveFaPostingProfile(MenuConstants.FA_POSTING_RULE, faPostingProfile, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Code : ' " + faPostingProfile.getProfileCode() + " ' " + e.getMessage());
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
			render("PostingProfiles/detail.html", faPostingProfile, mode, confirming, status);
		}
	}

	@Check("administration.postingProfile")
	public static void back(Long id, String mode, String param, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " param: " + param + " status: " + status);

		// try {
		FaPostingProfile faPostingProfile = serializerService.deserialize(session.getId(), id, FaPostingProfile.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_RULE));
		renderTemplate("PostingProfiles/detail.html", faPostingProfile, mode, status);
		// } catch (Exception e) {
		// logger.debug(e.getMessage(), e);
		// }
	}

	// @Check("administration.postingProfile")
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			FaPostingProfile faPostingProfile = json.readValue(maintenanceLog.getNewData(), FaPostingProfile.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("PostingProfiles/approval.html", faPostingProfile, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// @Check("administration.postingProfile")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaPostingProfile(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("administration.postingProfile")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaPostingProfile(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}