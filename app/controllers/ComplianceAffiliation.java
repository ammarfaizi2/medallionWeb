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

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CpAffiliation;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class ComplianceAffiliation extends MedallionController {
	private static Logger log = Logger.getLogger(ComplianceAffiliation.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("operators", UIHelper.yesNoOperators());

		List<SelectItem> tPartys = new ArrayList<SelectItem>();
		tPartys.add(new SelectItem("true", getThirdParty(LookupConstants.THIRD_PARTY_COUNTER_PART)));
		tPartys.add(new SelectItem("false", getThirdParty(LookupConstants.THIRD_PARTY_ISSUER)));
		renderArgs.put("tPartys", tPartys);

		renderArgs.put("CounterParty", getThirdParty(LookupConstants.THIRD_PARTY_COUNTER_PART));
		renderArgs.put("Issuer", getThirdParty(LookupConstants.THIRD_PARTY_ISSUER));

	}

	@Check("administration.complianceAffiliation")
	public static void list() {
		log.debug("list. ");

		List<CpAffiliation> affiliations = generalService.listAffiliation();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_AFFILIATION));
		render("ComplianceAffiliation/list.html", affiliations);
	}

	@Check("administration.complianceAffiliation")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CpAffiliation aff = new CpAffiliation();
		aff.setActive(new Boolean(false));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_AFFILIATION));
		render("ComplianceAffiliation/entry.html", aff, mode);
	}

	@Check("administration.complianceAffiliation")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CpAffiliation aff = populateKeys(generalService.getAffiliation(id));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_AFFILIATION));
		render("ComplianceAffiliation/entry.html", aff, mode);
	}

	@Check("administration.complianceAffiliation")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CpAffiliation aff = populateKeys(generalService.getAffiliation(id));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_AFFILIATION));
		render("ComplianceAffiliation/entry.html", aff, mode);
	}

	@Check("administration.complianceAffiliation")
	public static void save(CpAffiliation aff, String mode) {
		log.debug("save. aff: " + aff + " mode: " + mode);

		if (aff == null)
			entry();

		aff = populateIssuer(aff);

		validation.required("Fund Manager Code is", aff.getFundManager().getThirdPartyCode());

		if ("".equals(aff.getIssuerkeys().trim())) {
			validation.required("Issuer Code list is", "");
		}

		if (!validation.hasErrors() && mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
			CpAffiliation cpaff = generalService.getAffiliation(aff.getFundManager().getThirdPartyKey());
			if (cpaff != null)
				validation.addError("global", Messages.get("cp.affiliation.fund_code_exist", aff.getFundManager().getThirdPartyCode()));
		}

		if (validation.hasErrors()) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_AFFILIATION));
			render("ComplianceAffiliation/entry.html", aff, mode);
		} else {
			Long id = aff.getFundManagerCode();
			serializerService.serialize(session.getId(), id, aff);
			confirming(id, mode);
		}
	}

	@Check("administration.complianceAffiliation")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		CpAffiliation aff = serializerService.deserialize(session.getId(), id, CpAffiliation.class);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_AFFILIATION));
		render("ComplianceAffiliation/entry.html", aff, mode);
	}

	@Check("administration.complianceAffiliation")
	public static void confirm(CpAffiliation aff, String mode) {
		log.debug("confirm. aff: " + aff + " mode: " + mode);

		if (aff == null)
			back(null, mode);

		try {
			aff = populateIssuer(aff);
			generalService.saveAffiliation(MenuConstants.CP_AFFILIATION, aff, session.get("username"), session.get("userKey"));
			mode = UIConstants.DISPLAY_MODE_CONFIRM;

			list();
		} catch (MedallionException ex) {
			aff = populateIssuer(aff);

			flash.error("Fund Manager Code : ' " + aff.getFundManager().getThirdPartyCode() + " ' " + Messages.get(ex.getMessage()));
			renderArgs.put("confirming", true);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_AFFILIATION));
			render("ComplianceAffiliation/entry.html", aff, mode);
		}
	}

	@Check("administration.complianceAffiliation")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", false);
		if (id == null) {
			id = (long) 0;
		}

		CpAffiliation aff = serializerService.deserialize(session.getId(), id, CpAffiliation.class);
		aff = populateIssuer(aff);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_AFFILIATION));
		render("ComplianceAffiliation/entry.html", aff, mode);
	}

	public static CpAffiliation populateIssuer(CpAffiliation aff) {
		log.debug("populateIssuer. aff: " + aff);

		aff.setFundManagerCode(aff.getFundManager().getThirdPartyKey());

		String issuerkey = aff.getIssuerkeys();
		if (!"".equals(issuerkey.trim())) {
			String[] keys = issuerkey.split("\\-");
			aff.setIssuers(new ArrayList<GnThirdParty>());
			for (String key : keys) {
				aff.getIssuers().add(generalService.getThirdPartyNType(new Long(key)));
			}
		}
		return aff;
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CpAffiliation aff = json.readValue(maintenanceLog.getNewData(), CpAffiliation.class);
			log.debug("aff " + aff.getActive());
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ComplianceAffiliation/approval.html", aff, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveAffiliation(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

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
			generalService.approveAffiliation(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static CpAffiliation populateKeys(CpAffiliation aff) {
		log.debug("populateKeys. aff: " + aff);

		String keys = "";
		if (aff.getIssuers() != null) {
			for (GnThirdParty gn : aff.getIssuers()) {
				if ("".equals(keys)) {
					keys += gn.getThirdPartyKey();
				} else {
					keys += ("-" + gn.getThirdPartyKey());
				}
			}
		}
		aff.setIssuerkeys(keys);
		return aff;
	}

	private static String getThirdParty(String param) {
		log.debug("getThirdParty. param: " + param);

		if (param.equals("THIRD_PARTY-COUNTER_PART"))
			return "Counter Party";
		if (param.equals("THIRD_PARTY-ISSUER"))
			return "Issuer";
		return "";
	}
}
