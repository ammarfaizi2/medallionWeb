package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CpComplianceProfile;
import com.simian.medallion.model.CpComplianceRule;
import com.simian.medallion.model.GnMaintenanceLog;

@With(Secure.class)
public class ComplianceRuleProfile extends MedallionController {
	private static Logger log = Logger.getLogger(ComplianceRuleProfile.class);

	@Before
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("operators", UIHelper.yesNoOperators());
	}

	@Check("administration.complianceRuleProfile")
	public static void list() {
		log.debug("list. ");

		List<CpComplianceProfile> profiles = generalService.listComplianceProfile();
		render("ComplianceRuleProfile/list.html", profiles);
	}

	@Check("administration.complianceRuleProfile")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CpComplianceProfile profile = new CpComplianceProfile();
		profile.setActive(new Boolean(false));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_PROFILE));
		render("ComplianceRuleProfile/entry.html", profile, mode);
	}

	@Check("administration.complianceRuleProfile")
	public static void view(String id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CpComplianceProfile profile = populateKeys(generalService.getComplianceProfile(id));

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_PROFILE));
		render("ComplianceRuleProfile/entry.html", profile, mode);
	}

	@Check("administration.complianceRuleProfile")
	public static void edit(String id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CpComplianceProfile profile = populateKeys(generalService.getComplianceProfile(id));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_PROFILE));
		render("ComplianceRuleProfile/entry.html", profile, mode);
	}

	@Check("administration.complianceRuleProfile")
	public static void save(CpComplianceProfile profile, String mode) {
		log.debug("save. profile: " + profile + " mode: " + mode);

		if (profile == null)
			entry();
		profile = populateRules(profile);

		validation.required("Compliance Rule Code is", profile.getComplianceProfCode());
		validation.required("Description is", profile.getDescription());
		if ("".equals(profile.getRulekeys().trim())) {
			validation.required("Master Rule Code list is", "");
		}

		if (!validation.hasErrors() && mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			CpComplianceProfile cpProfile = generalService.getComplianceProfile(profile.getComplianceProfCode());
			if (cpProfile != null)
				validation.addError("global", Messages.get("cp.ruleprofile.profile_code_exist", profile.getComplianceProfCode()));
		}

		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_PROFILE));
			render("ComplianceRuleProfile/entry.html", profile, mode);
		} else {
			String id = profile.getComplianceProfCode();
			log.debug("serialize id" + id + " session " + session.getId());
			serializerService.serialize(session.getId(), id, profile);
			confirming(id, mode);
		}
	}

	@Check("administration.complianceRuleProfile")
	public static void confirming(String id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		CpComplianceProfile profile = serializerService.deserialize(session.getId(), id, CpComplianceProfile.class);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_PROFILE));
		render("ComplianceRuleProfile/entry.html", profile, mode);
	}

	@Check("administration.complianceRuleProfile")
	public static void confirm(CpComplianceProfile profile, String mode) {
		log.debug("confirm. profile: " + profile + " mode: " + mode);

		if (profile == null)
			back(null, mode);

		log.debug("confirm profile=" + profile + ", mode=" + mode + " active " + profile.getActive());

		try {
			profile = populateRules(profile);
			generalService.saveComplianceProfile(MenuConstants.CP_COMPLIANCE_PROFILE, profile, session.get("username"), session.get("userKey"));
			mode = UIConstants.DISPLAY_MODE_CONFIRM;

			list();
		} catch (MedallionException ex) {
			profile = populateRules(profile);

			flash.error("Compliance Profile Code : ' " + profile.getComplianceProfCode() + " ' " + Messages.get(ex.getMessage()));
			renderArgs.put("confirming", true);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_PROFILE));
			render("ComplianceRuleProfile/entry.html", profile, mode);
		}
	}

	@Check("administration.complianceRuleProfile")
	public static void back(String id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", false);

		CpComplianceProfile profile = serializerService.deserialize(session.getId(), id, CpComplianceProfile.class);
		profile = populateRules(profile);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_COMPLIANCE_PROFILE));
		render("ComplianceRuleProfile/entry.html", profile, mode);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CpComplianceProfile profile = json.readValue(maintenanceLog.getNewData(), CpComplianceProfile.class);
			log.debug("profile " + profile.getActive());
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ComplianceRuleProfile/approval.html", profile, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveComplianceProfile(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveComplianceProfile(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static CpComplianceProfile populateRules(CpComplianceProfile profile) {
		log.debug("populateRules. profile: " + profile);

		String rulekey = profile.getRulekeys();

		if (!"".equals(rulekey.trim())) {
			String[] keys = rulekey.split("\\-");
			profile.setRules(new ArrayList<CpComplianceRule>());
			for (String key : keys) {
				profile.getRules().add(generalService.getComplianceRule(new Long(key)));
			}
		}
		return profile;
	}

	public static CpComplianceProfile populateKeys(CpComplianceProfile profile) {
		log.debug("populateKeys. profile: " + profile);

		String keys = "";
		if (profile.getRules() != null) {
			for (CpComplianceRule gn : profile.getRules()) {
				if ("".equals(keys)) {
					keys += gn.getRuleId();
				} else {
					keys += ("-" + gn.getRuleId());
				}
			}
		}
		profile.setRulekeys(keys);
		return profile;
	}
}