package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnSystemParameter;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class ThirdParties extends MedallionController {
	private static Logger log = Logger.getLogger(ThirdParties.class);

	public static final String DEFAULT_BANK_CODE = "organization.bank";

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.thirdParty")
	public static void group() {
		log.debug("group. ");

		List<GnLookup> lookups = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._THIRD_PARTY);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY));
		render(lookups);
	}

	@Check("administration.thirdParty")
	public static void list(String group) {
		log.debug("list. group: " + group);

		List<GnThirdParty> thirdParties = generalService.listThirdPartiesByType(group);
		GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY));
		renderArgs.put("pagetype", null);
		render(thirdParties, group, lookup);
	}

	@Check("administration.thirdParty")
	public static void listBankInformation(String group) {
		log.debug("listBankInformation. group: " + group);

		List<GnThirdParty> thirdParties = generalService.listThirdPartiesByType(group);
		GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK_INFORMATION));
		renderArgs.put("pagetype", group);
		renderArgs.put("canGoBack", false);
		render("ThirdParties/list.html", thirdParties, group, lookup);
	}

	@Check("administration.thirdParty")
	public static void listRetailGroup(String group) {
		log.debug("listRetailGroup. group: " + group);

		List<GnThirdParty> thirdParties = generalService.listThirdPartiesByType(group);
		GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL_GROUP));
		renderArgs.put("pagetype", group);
		renderArgs.put("canGoBack", false);
		render("ThirdParties/list.html", thirdParties, group, lookup);
	}

	@Check("administration.thirdParty")
	public static void listCounterParty(String group) {
		log.debug("listCounterParty. group: " + group);

		List<GnThirdParty> thirdParties = generalService.listThirdPartiesByType(group);
		GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
		renderArgs.put("pagetype", group);
		renderArgs.put("canGoBack", false);
		render("ThirdParties/list.html", thirdParties, group, lookup);
	}

	@Check("administration.thirdParty")
	public static void view(Long id, String group, String pagetype) {
		log.debug("view. id: " + id + " group: " + group + " pagetype: " + pagetype);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			GnLookup lookup = generalService.getLookup(group);
			thirdParty.setIsParent(false);
			if (thirdParty.getParentValue() == null) {
				thirdParty.setIsParent(true);
			}

			renderArgs.put("pagetype", pagetype);
			checkPageType(pagetype);

			render("ThirdParties/detail.html", thirdParty, mode, group, lookup);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.thirdParty")
	public static void entry(String group, String pagetype) {
		log.debug("entry. group: " + group + " pagetype: " + pagetype);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnThirdParty thirdParty = new GnThirdParty();
		thirdParty.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		thirdParty.setThirdPartyType(new GnLookup(group));
		thirdParty.setIsActive(false);
		thirdParty.setIsParent(true);
		GnLookup lookup = generalService.getLookup(group);
		renderArgs.put("pagetype", pagetype);
		checkPageType(pagetype);
		render("ThirdParties/detail.html", thirdParty, group, mode, lookup);
	}

	@Check("administration.thirdParty")
	public static void edit(Long id, String group, String pagetype) {
		log.debug("edit. id: " + id + " group: " + group + " pagetype: " + pagetype);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnThirdParty thirdParty = generalService.getThirdParty(id);
		GnLookup lookup = generalService.getLookup(group);
		String status = thirdParty.getRecordStatus().trim();
		thirdParty.setIsParent(false);
		if (thirdParty.getParentValue() == null) {
			thirdParty.setIsParent(true);
		}
		renderArgs.put("pagetype", pagetype);
		checkPageType(pagetype);

		GnSystemParameter systemParameter = generalService.getSystemParameter(DEFAULT_BANK_CODE);
		if ((systemParameter.getValue().equalsIgnoreCase(thirdParty.getThirdPartyCode())) && ((!Helper.isNullOrEmpty(pagetype)) && ((pagetype.equalsIgnoreCase("THIRD_PARTY-BANK")) || (pagetype.equalsIgnoreCase("THIRD_PARTY-RETAIL"))))) {
			view(id, group, pagetype);
		}

		render("ThirdParties/detail.html", thirdParty, group, mode, lookup, status);
	}

	@Check("administration.thirdParty")
	public static void save(String group, String mode, GnThirdParty thirdParty, String status, String pagetype) {
		log.debug("save. group: " + group + " mode: " + mode + " thirdParty: " + thirdParty + " status: " + status + " pagetype: " + pagetype);

		GnLookup lookup = generalService.getLookup(group);
		// Validate here
		if (thirdParty.getIsParent() == null) {
			thirdParty.setIsParent(false);
		}

		if (thirdParty != null) {
			validation.required("Code is", thirdParty.getThirdPartyCode());
			validation.required("Name is", thirdParty.getThirdPartyName());
			validation.required("Country is", thirdParty.getAddress1Country().getLookupId());
			if (!(group.equals(LookupConstants.THIRD_PARTY_BANK))) {
				validation.required("Branch is", thirdParty.getThirdPartyBranch());
				validation.required("Bank Code is", thirdParty.getBankCode().getThirdPartyCode());
				validation.required("Account No is", thirdParty.getAccountNo());
				validation.required("Account Holder Name is", thirdParty.getAccountHolderName());
				validation.required("Currency is", thirdParty.getCurrency().getCurrencyCode());
			}
			if (group.equals(LookupConstants.THIRD_PARTY_SELLINGAGENT)) {
				if (thirdParty.getIsParent() != true && !("").equals(lookup.getLookupId())) {
					validation.required("Parent Value is", thirdParty.getParentValue().getThirdPartyCode());
				}

			}

			if (validation.hasErrors()) {
				renderArgs.put("pagetype", pagetype);
				checkPageType(pagetype);
				render("ThirdParties/detail.html", thirdParty, group, mode, lookup, status);
			} else {
				Long id = thirdParty.getThirdPartyKey();
				serializerService.serialize(session.getId(), id, thirdParty);
				confirming(id, group, mode, status, pagetype);
			}
		} else {
			flash.error("argument.null", thirdParty);
		}
	}

	@Check("administration.thirdParty")
	public static void confirming(Long id, String group, String mode, String status, String pagetype) {
		log.debug("confirming. id: " + id + " group: " + group + " mode: " + mode + " status: " + status + " pagetype: " + pagetype);

		GnLookup lookup = generalService.getLookup(group);
		renderArgs.put("confirming", true);
		GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
		renderArgs.put("pagetype", pagetype);
		checkPageType(pagetype);
		render("ThirdParties/detail.html", thirdParty, group, mode, lookup, status);
	}

	@Check("administration.thirdParty")
	public static void confirm(String group, String mode, GnThirdParty thirdParty, String status, String pagetype) {
		log.debug("confirm. group: " + group + " mode: " + mode + " thirdParty: " + thirdParty + " status: " + status + " pagetype: " + pagetype);

		try {
			String menuCode = MenuConstants.GN_THIRD_PARTY;
			if (pagetype.equals(LookupConstants.THIRD_PARTY_BANK)) {
				menuCode = MenuConstants.GN_THIRD_PARTY_BANK_INFORMATION;
			}
			if (pagetype.equalsIgnoreCase(LookupConstants.THIRD_PARTY_RETAIL)) {
				menuCode = MenuConstants.GN_THIRD_PARTY_RETAIL_GROUP;
			}
			if (pagetype.equalsIgnoreCase(LookupConstants.THIRD_PARTY_COUNTER_PART)) {
				menuCode = MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY;
			}
			generalService.saveThirdParty(menuCode, thirdParty, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			renderArgs.put("pagetype", pagetype);
			if (!Helper.isNullOrEmpty(pagetype)) {
				if (pagetype.equalsIgnoreCase(LookupConstants.THIRD_PARTY_BANK)) {
					listBankInformation(group);
				} else if (pagetype.equalsIgnoreCase(LookupConstants.THIRD_PARTY_RETAIL)) {
					listRetailGroup(group);
				} else if (pagetype.equalsIgnoreCase(LookupConstants.THIRD_PARTY_COUNTER_PART)) {
					listCounterParty(group);
				}
			} else {
				list(group);
			}
		} catch (MedallionException e) {
			GnLookup lookup = generalService.getLookup(group);
			flash.error("Third Party Code : ' " + thirdParty.getThirdPartyCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			renderArgs.put("pagetype", pagetype);
			checkPageType(pagetype);
			render("ThirdParties/detail.html", thirdParty, group, lookup, mode, confirming, status);
		}
	}

	@Check("administration.thirdParty")
	public static void back(String id, String group, String mode, String status, String pagetype) {
		log.debug("back. id: " + id + " group: " + group + " mode: " + mode + " status: " + status + " pagetype: " + pagetype);

		GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
		GnLookup lookup = generalService.getLookup(group);
		renderArgs.put("pagetype", pagetype);
		checkPageType(pagetype);
		render("ThirdParties/detail.html", thirdParty, group, mode, lookup, status);
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnThirdParty thirdParty = json.readValue(maintenanceLog.getNewData(), GnThirdParty.class);
			GnLookup lookup = generalService.getLookup(thirdParty.getThirdPartyType().getLookupId());
			thirdParty.setIsParent(false);
			if (thirdParty.getParentValue() == null) {
				thirdParty.setIsParent(true);
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ThirdParties/approval.html", thirdParty, mode, taskId, lookup, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		Map<String, Object> result = new HashMap<String, Object>();
		try {

			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnThirdParty newThirdParty = json.readValue(maintenanceLog.getNewData(), GnThirdParty.class);
			GnThirdParty thirdParty = generalService.getThirdParty(newThirdParty.getThirdPartyKey());
			/*
			 * if(thirdParty.getParentValue() != null) {
			 * logger.debug("Masuk Sini1"+
			 * thirdParty.getParentValue().getThirdPartyKey()); GnThirdParty
			 * thirdPartyParent =
			 * generalService.getThirdParty(thirdParty.getParentValue
			 * ().getThirdPartyKey()); String recordStatus="";
			 * if(thirdPartyParent.getRecordStatus() != null) { recordStatus =
			 * thirdPartyParent.getRecordStatus().trim(); }
			 * 
			 * if(LookupConstants.__RECORD_STATUS_UPDATED.equals(recordStatus)){
			 * logger.debug("Masuk Sini3"); result.put("status", "error"); }
			 * else { logger.debug("Masuk Sini4"+recordStatus);
			 * generalService.approveThirdParty
			 * (session.get(UIConstants.SESSION_USERNAME), taskId, operation,
			 * maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
			 * result.put("status", "success"); } } else {
			 * logger.debug("Masuk Sini2");
			 * generalService.approveThirdParty(session
			 * .get(UIConstants.SESSION_USERNAME), taskId, operation,
			 * maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
			 * result.put("status", "success"); } renderJSON(result);
			 */
			if (newThirdParty.getParentValue() != null) {
				if ((newThirdParty.getParentValue() != null) && (thirdParty.getParentValue() == null)) {
					boolean checkParent = generalService.isCheckThirdPartyParent(newThirdParty.getThirdPartyKey());
					if (checkParent) {
						result.put("isParent", true);
						result.put("status", "error");
					} else {
						GnThirdParty thirdPartyParent = generalService.getThirdParty(newThirdParty.getParentValue().getThirdPartyKey());
						if (thirdPartyParent.getParentValue() != null) {
							result.put("isParent", false);
							result.put("parent", newThirdParty.getParentValue().getThirdPartyCode());
							result.put("status", "error");
						} else {
							generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
							result.put("status", "success");
						}

					}
				} else {
					GnThirdParty thirdPartyParent = generalService.getThirdParty(newThirdParty.getParentValue().getThirdPartyKey());
					if (thirdPartyParent.getParentValue() != null) {
						result.put("isParent", false);
						result.put("parent", newThirdParty.getParentValue().getThirdPartyCode());
						result.put("status", "error");
					} else {
						generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
						result.put("status", "success");
					}
				}
			} else {
				generalService.approveThirdParty(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);
				result.put("status", "success");
			}
			renderJSON(result);
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

	public static void onCheckParent(Long parentValue) {
		log.debug("onCheckParent. parentValue: " + parentValue);

		boolean checkParent = generalService.isCheckThirdPartyParent(parentValue);
		renderText(checkParent);
	}

	private static void checkPageType(String pagetype) {
		if (!Helper.isNullOrEmpty(pagetype)) {
			if (pagetype.equalsIgnoreCase(LookupConstants.THIRD_PARTY_BANK)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_BANK_INFORMATION));
			} else if (pagetype.equalsIgnoreCase(LookupConstants.THIRD_PARTY_RETAIL)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL_GROUP));
			} else if (pagetype.equalsIgnoreCase(LookupConstants.THIRD_PARTY_COUNTER_PART)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_COUNTER_PARTY));
			}
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY));
		}
	}
}