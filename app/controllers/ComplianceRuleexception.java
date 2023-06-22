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
import com.simian.medallion.model.CpRuleexception;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnThirdParty;

@With(Secure.class)
public class ComplianceRuleexception extends MedallionController {
	private static Logger log = Logger.getLogger(ComplianceRuleexception.class);

	@Before
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("operators", UIHelper.yesNoOperators());
	}

	@Check("administration.complianceException")
	public static void list() {
		log.debug("list. ");

		List<CpRuleexception> exceptions = generalService.listRuleexceptions();
		render("ComplianceRuleexception/list.html", exceptions);
	}

	@Check("administration.complianceException")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CpRuleexception rule = new CpRuleexception();
		rule.setActive(new Boolean(false));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_RULEEXCEPTION));
		render("ComplianceRuleexception/entry.html", rule, mode);
	}

	@Check("administration.complianceException")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CpRuleexception rule = populateKeys(generalService.getRuleexception(id));

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_RULEEXCEPTION));
		render("ComplianceRuleexception/entry.html", rule, mode);
	}

	@Check("administration.complianceException")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CpRuleexception rule = populateKeys(generalService.getRuleexception(id));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_RULEEXCEPTION));
		render("ComplianceRuleexception/entry.html", rule, mode);
	}

	@Check("administration.complianceException")
	public static void save(CpRuleexception rule, String mode) {
		log.debug("save. rule: " + rule + " mode: " + mode);

		if (rule == null)
			entry();

		rule = populateIssuer(rule);

		validation.required("Rule Code is", rule.getRule().getRuleCode());

		if ("".equals(rule.getIssuerkeys().trim())) {
			validation.required("Issuer Code list is", "");
		}

		if (!validation.hasErrors() && mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			CpRuleexception cprule = generalService.getRuleexception(rule.getRule().getRuleId());
			if (cprule != null)
				validation.addError("global", Messages.get("cp.ruleexception.rule_id_exist", rule.getRule().getRuleCode()));
		}

		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_RULEEXCEPTION));
			render("ComplianceRuleexception/entry.html", rule, mode);
		} else {
			Long id = rule.getRuleId();
			serializerService.serialize(session.getId(), id, rule);
			confirming(id, mode);
		}
	}

	@Check("administration.complianceException")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		CpRuleexception rule = serializerService.deserialize(session.getId(), id, CpRuleexception.class);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_RULEEXCEPTION));
		render("ComplianceRuleexception/entry.html", rule, mode);
	}

	@Check("administration.complianceException")
	public static void confirm(CpRuleexception rule, String mode) {
		log.debug("confirm. rule: " + rule + " mode: " + mode);

		if (rule == null)
			back(null, mode);

		try {
			rule = populateIssuer(rule);
			generalService.saveRuleexception(MenuConstants.CP_RULEEXCEPTION, rule, session.get("username"), session.get("userKey"));
			mode = UIConstants.DISPLAY_MODE_CONFIRM;
			list();
		} catch (MedallionException ex) {
			rule = populateIssuer(rule);

			flash.error("Rule Code : ' " + rule.getRule().getRuleCode() + " ' " + Messages.get(ex.getMessage()));
			renderArgs.put("confirming", true);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_RULEEXCEPTION));
			render("ComplianceRuleexception/entry.html", rule, mode);
		}
	}

	@Check("administration.complianceException")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", false);
		if (id == null) {
			id = (long) 0;
		}

		CpRuleexception rule = serializerService.deserialize(session.getId(), id, CpRuleexception.class);
		rule = populateIssuer(rule);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_RULEEXCEPTION));
		render("ComplianceRuleexception/entry.html", rule, mode);
	}

	public static CpRuleexception populateIssuer(CpRuleexception rule) {
		log.debug("populateIssuer. rule: " + rule);

		rule.setRuleId(rule.getRule().getRuleId());

		String issuerkey = rule.getIssuerkeys();
		if (!"".equals(issuerkey.trim())) {
			String[] keys = issuerkey.split("\\-");
			rule.setIssuers(new ArrayList<GnThirdParty>());
			for (String key : keys) {
				rule.getIssuers().add(generalService.getThirdParty(new Long(key)));
			}
		}
		return rule;
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CpRuleexception rule = json.readValue(maintenanceLog.getNewData(), CpRuleexception.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ComplianceRuleexception/approval.html", rule, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveRuleexception(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

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
			generalService.approveRuleexception(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static CpRuleexception populateKeys(CpRuleexception rex) {
		log.debug("populateKeys. rex: " + rex);

		String keys = "";
		if (rex.getIssuers() != null) {
			for (GnThirdParty gn : rex.getIssuers()) {
				if ("".equals(keys)) {
					keys += gn.getThirdPartyKey();
				} else {
					keys += ("-" + gn.getThirdPartyKey());
				}
			}
		}
		rex.setIssuerkeys(keys);
		return rex;
	}
}
