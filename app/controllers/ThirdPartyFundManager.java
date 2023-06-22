package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class ThirdPartyFundManager extends MedallionController {
	private static Logger log = Logger.getLogger(ThirdPartyFundManager.class);

	public static final String DEFAULT_BANK_CODE = "organization.bank";
	private static String FIELDDELIMITER = "\n";

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

	}

	@Check("administration.thirdParty.fundManager")
	public static void group() {
		log.debug("group. ");

		List<GnLookup> lookups = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._THIRD_PARTY);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
		render(lookups);
	}

	@Check("administration.thirdParty.fundManager")
	public static void list(String group) {
		log.debug("list. group: " + group);

		List<GnThirdParty> thirdParties = generalService.listThirdPartiesByType(LookupConstants.THIRD_PARTY_FUND_MANAGER);
		GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
		render(thirdParties, group, lookup);
	}

	@Check("administration.thirdParty.fundManager")
	public static void entry(String group) {
		log.debug("entry. group: " + group);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnThirdParty thirdParty = new GnThirdParty();
		thirdParty.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		thirdParty.setThirdPartyType(new GnLookup(group));
		thirdParty.setIsActive(false);
		GnLookup lookup = generalService.getLookup(group);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
		render("ThirdPartyFundManager/detail.html", thirdParty, group, mode, lookup);
	}

	@Check("administration.thirdParty.fundManager")
	public static void edit(Long id, String group) {
		log.debug("edit. id: " + id + " group: " + group);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnThirdParty thirdParty = generalService.getThirdParty(id);
		GnLookup lookup = generalService.getLookup(group);
		String status = thirdParty.getRecordStatus().trim();

		String[] address1 = new String[] {};

		address1 = (!Helper.isNullOrEmpty(thirdParty.getAddress1())) ? thirdParty.getAddress1().split(FIELDDELIMITER) : new String[] {};
		if (address1.length >= 1) {
			thirdParty.setAddress1Ext(address1[0]);
		}
		if ((address1.length == 2) || (address1.length == 3)) {
			thirdParty.setAddress2Ext(address1[1]);
		}
		if (address1.length == 3) {
			thirdParty.setAddress3Ext(address1[2]);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
		render("ThirdPartyFundManager/detail.html", thirdParty, group, mode, lookup, status);

	}

	@Check("administration.thirdParty.fundManager")
	public static void view(Long id, String group) {
		log.debug("view. id: " + id + " group: " + group);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			GnLookup lookup = generalService.getLookup(group);

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
			render("ThirdPartyFundManager/detail.html", thirdParty, mode, group, lookup);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.thirdParty.fundManager")
	public static void save(String group, String mode, GnThirdParty thirdParty, String status) {
		log.debug("save. group: " + group + " mode: " + mode + " thirdParty: " + thirdParty + " status: " + status);

		GnLookup lookup = generalService.getLookup(group);
		if (thirdParty != null) {
			validation.required("Code is", thirdParty.getThirdPartyCode());
			validation.required("Name is", thirdParty.getThirdPartyName());
			validation.required("Address is", thirdParty.getAddress1Ext());
			validation.required("Phone is", thirdParty.getAddress1Phone1());
			validation.required("Fax Number is", thirdParty.getAddress1Phone3());
			validation.required("Email Address is", thirdParty.getEmail());

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
				render("ThirdPartyFundManager/detail.html", thirdParty, group, mode, lookup, status);
			} else {
				Long id = thirdParty.getThirdPartyKey();
				serializerService.serialize(session.getId(), id, thirdParty);
				confirming(id, group, mode, status);
			}
		} else {
			// flash.error("argument.null", thirdParty);
			entry(group);
		}
	}

	@Check("administration.thirdParty.fundManager")
	public static void back(String id, String group, String mode, String status) {
		log.debug("back. id: " + id + " group: " + group + " mode: " + mode + " status: " + status);

		GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
		GnLookup lookup = generalService.getLookup(group);

		String[] address1 = new String[] {};

		address1 = (!Helper.isNullOrEmpty(thirdParty.getAddress1())) ? thirdParty.getAddress1().split(FIELDDELIMITER) : new String[] {};
		if (address1.length >= 1) {
			thirdParty.setAddress1Ext(address1[0]);
		}
		if ((address1.length == 2) || (address1.length == 3)) {
			thirdParty.setAddress2Ext(address1[1]);
		}
		if (address1.length == 3) {
			thirdParty.setAddress3Ext(address1[2]);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
		render("ThirdPartyFundManager/detail.html", thirdParty, group, mode, lookup, status);
	}

	@Check("administration.thirdParty.fundManager")
	public static void confirming(Long id, String group, String mode, String status) {
		log.debug("confirming. id: " + id + " group: " + group + " mode: " + mode + " status: " + status);

		GnLookup lookup = generalService.getLookup(group);
		renderArgs.put("confirming", true);
		GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
		render("ThirdPartyFundManager/detail.html", thirdParty, group, mode, lookup, status);

	}

	@Check("administration.thirdParty.fundManager")
	public static void confirm(String group, String mode, GnThirdParty thirdParty, String status) {
		log.debug("confirm. group: " + group + " mode: " + mode + " thirdParty: " + thirdParty + " status: " + status);

		try {
			if (thirdParty == null)
				back(null, group, mode, status);
			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				GnThirdParty gnThirdPartyDB = generalService.getGnThirdPartyByCodeType(thirdParty.getThirdPartyCode(), thirdParty.getThirdPartyType().getLookupId());
				if (gnThirdPartyDB != null) {
					throw new MedallionException(ExceptionConstants.DATA_DUPLICATE);
				}
			}

			generalService.saveThirdParty(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER, thirdParty, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list(group);
		} catch (MedallionException e) {
			GnLookup lookup = generalService.getLookup(group);
			flash.error("Third Party Code : ' " + thirdParty.getThirdPartyCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_FUND_MANAGER));
			render("ThirdPartyFundManager/detail.html", thirdParty, group, lookup, mode, confirming, status);

		}
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnThirdParty thirdParty = json.readValue(maintenanceLog.getNewData(), GnThirdParty.class);
			GnLookup lookup = generalService.getLookup(thirdParty.getThirdPartyType().getLookupId());

			String[] address1 = new String[] {};

			address1 = (!Helper.isNullOrEmpty(thirdParty.getAddress1())) ? thirdParty.getAddress1().split(FIELDDELIMITER) : new String[] {};
			if (address1.length >= 1) {
				thirdParty.setAddress1Ext(address1[0]);
			}
			if ((address1.length == 2) || (address1.length == 3)) {
				thirdParty.setAddress2Ext(address1[1]);
			}
			if (address1.length == 3) {
				thirdParty.setAddress3Ext(address1[2]);
			}

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			log.debug("address -------->" + address1);
			render("ThirdPartyFundManager/approval.html", thirdParty, mode, taskId, lookup, operation, maintenanceLogKey, from);

		} catch (Exception e) {
			log.debug("Error ---> " + e);
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			//GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			//GnThirdParty newThirdParty = json.readValue(maintenanceLog.getNewData(), GnThirdParty.class);
			//GnThirdParty thirdParty = generalService.getThirdParty(newThirdParty.getThirdPartyKey());
			generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}