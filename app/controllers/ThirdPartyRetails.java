package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.GnThirdParty;
import com.simian.medallion.model.GnThirdPartyAccount;
import com.simian.medallion.model.GnThirdPartyBank;
import com.simian.medallion.vo.SelectItem;

public class ThirdPartyRetails extends MedallionController {
	private static Logger log = Logger.getLogger(ThirdPartyRetails.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.thirdParty.retail")
	public static void list() {
		log.debug("list. ");

		List<GnThirdParty> thirdParties = generalService.listThirdPartiesByType(LookupConstants.THIRD_PARTY_RETAIL);
		// GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL));
		// renderArgs.put("pagetype", group);
		// renderArgs.put("canGoBack", false);
		render("ThirdPartyRetails/list.html", thirdParties);
	}

	@Check("administration.thirdParty.retail")
	public static void view(Long id, String group, String pagetype) {
		log.debug("view. id: " + id + " group: " + group + " pagetype: " + pagetype);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
			if (!thirdParty.getThirdPartyBanks().isEmpty()) {
				Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
				thirdPartyBank = tpBanks.iterator().next();
			}
			if (thirdParty.getThirdPartyAccounts() != null) {
				for (GnThirdPartyAccount gnThirdPartyAccount : thirdParty.getThirdPartyAccounts()) {
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) {
						if (gnThirdPartyAccount.getAccountCode() != null)
							gnThirdPartyAccount.setCbestFlag(true);
						thirdParty.setCbestAccount(gnThirdPartyAccount);
					}
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)) {
						if (gnThirdPartyAccount.getAccountCode() != null)
							gnThirdPartyAccount.setBiFlag(true);
						thirdParty.setBiAccount(gnThirdPartyAccount);
					}
				}
			}
			if (thirdParty.getAddress1() != null) {
				String[] address = thirdParty.getAddress1().split("\n");
				if (address.length == 1) {
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length == 2) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}

				if (address.length == 3) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
					thirdParty.setAddress3Ext(address[2].trim());
				}
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL));
			render("ThirdPartyRetails/detail.html", thirdParty, thirdPartyBank, mode);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.thirdParty.retail")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnThirdParty thirdParty = new GnThirdParty();
		thirdParty.setCbestAccount(new GnThirdPartyAccount());
		thirdParty.setBiAccount(new GnThirdPartyAccount());
		thirdParty.getCbestAccount().setCbestFlag(true);
		thirdParty.getBiAccount().setBiFlag(true);
		GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
		thirdParty.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		thirdParty.setIsActive(false);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL));
		render("ThirdPartyRetails/detail.html", thirdParty, thirdPartyBank, mode);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		try {
			String mode = UIConstants.DISPLAY_MODE_EDIT;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			String status = thirdParty.getRecordStatus();
			log.debug("third party = " + thirdParty.getThirdPartyBanks());
			GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
			if (!thirdParty.getThirdPartyBanks().isEmpty()) {
				Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
				thirdPartyBank = tpBanks.iterator().next();
			}
			if (thirdParty.getThirdPartyAccounts() != null) {
				for (GnThirdPartyAccount gnThirdPartyAccount : thirdParty.getThirdPartyAccounts()) {
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) {
						if (gnThirdPartyAccount.getAccountCode() != null)
							gnThirdPartyAccount.setCbestFlag(true);
						thirdParty.setCbestAccount(gnThirdPartyAccount);
					}
					if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)) {
						if (gnThirdPartyAccount.getAccountCode() != null)
							gnThirdPartyAccount.setBiFlag(true);
						thirdParty.setBiAccount(gnThirdPartyAccount);
					}
				}
			}
			if (thirdParty.getAddress1() != null) {
				String[] address = thirdParty.getAddress1().split("\n");
				log.debug("address = " + address.length);
				if (address.length == 1) {
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length == 2) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}

				if (address.length == 3) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
					thirdParty.setAddress3Ext(address[2].trim());
				}
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL));
			render("ThirdPartyRetails/detail.html", thirdParty, thirdPartyBank, mode, status);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	public static void save(GnThirdParty thirdParty, GnThirdPartyBank thirdPartyBank, String mode, String status) {
		log.debug("save. thirdParty: " + thirdParty + " thirdPartyBank: " + thirdPartyBank + " mode: " + mode + " status: " + status);

		if (thirdParty != null) {
			validation.required("Code is", thirdParty.getThirdPartyCode());
			validation.required("Name is", thirdParty.getThirdPartyName());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL));
				render("ThirdPartyRetails/detail.html", thirdParty, thirdPartyBank, mode);
			} else {
				Set<GnThirdPartyBank> tpBanks = new HashSet<GnThirdPartyBank>();
				if (tpBanks != null) {
					tpBanks.add(thirdPartyBank);
				}
				thirdParty.setThirdPartyBanks(tpBanks);
				Long id = thirdParty.getThirdPartyKey();
				serializerService.serialize(session.getId(), id, thirdParty);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", thirdParty);
			entry();
		}
	}

	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		try {
			renderArgs.put("confirming", true);
			GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
			Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
			GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL));
			render("ThirdPartyRetails/detail.html", thirdParty, thirdPartyBank, mode, status);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	public static void confirm(GnThirdParty thirdParty, GnThirdPartyBank thirdPartyBank, String mode, String status) {
		log.debug("confirm. thirdParty: " + thirdParty + " thirdPartyBank: " + thirdPartyBank + " mode: " + mode + " status: " + status);

		try {
			if (thirdParty == null)
				back(null, mode, status);
			Set<GnThirdPartyBank> tpBanks = new HashSet<GnThirdPartyBank>();
			List<GnThirdPartyAccount> tpAccounts = new ArrayList<GnThirdPartyAccount>();
			if (thirdPartyBank != null) {
				tpBanks.add(thirdPartyBank);
			}

			thirdParty.setThirdPartyBanks(tpBanks);
			thirdParty.setThirdPartyAccounts(new HashSet<GnThirdPartyAccount>());
			thirdParty.getThirdPartyAccounts().addAll(tpAccounts);
			thirdParty.setThirdPartyType(new GnLookup(LookupConstants.THIRD_PARTY_RETAIL));
			log.debug("Size of Accounts = " + thirdParty.getThirdPartyAccounts().size());
			generalService.saveThirdParty(MenuConstants.GN_THIRD_PARTY_RETAIL, thirdParty, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Third Party Code : ' " + thirdParty.getThirdPartyCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL));
			render("ThirdPartyRetails/detail.html", thirdParty, thirdPartyBank, mode, status, confirming);
		}
	}

	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
		Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
		GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_RETAIL));
		render("ThirdPartyRetails/detail.html", thirdParty, thirdPartyBank, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			boolean approvalMode = true;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnThirdParty thirdParty = json.readValue(maintenanceLog.getNewData(), GnThirdParty.class);
			GnLookup lookup = generalService.getLookup(thirdParty.getThirdPartyType().getLookupId());
			Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
			GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();

			for (GnThirdPartyAccount gnThirdPartyAccount : thirdParty.getThirdPartyAccounts()) {
				if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_KSEI_CBEST)) {
					if (gnThirdPartyAccount.getAccountCode() != null)
						gnThirdPartyAccount.setCbestFlag(true);
					thirdParty.setCbestAccount(gnThirdPartyAccount);
				}
				if (gnThirdPartyAccount.getAccountCategory().getLookupId().equals(LookupConstants.DEPOSITORY_CODE_BI_BISSSS)) {
					if (gnThirdPartyAccount.getAccountCode() != null)
						gnThirdPartyAccount.setBiFlag(true);
					thirdParty.setBiAccount(gnThirdPartyAccount);
				}
			}
			if (thirdParty.getAddress1() != null) {
				String[] address = thirdParty.getAddress1().split("\n");
				if (address.length == 1) {
					thirdParty.setAddress1Ext(address[0].trim());
				}
				if (address.length == 2) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
				}

				if (address.length == 3) {
					thirdParty.setAddress1Ext(address[0].trim());
					thirdParty.setAddress2Ext(address[1].trim());
					thirdParty.setAddress3Ext(address[2].trim());
				}
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ThirdPartyRetails/approval.html", thirdParty, thirdPartyBank, lookup, mode, taskId, operation, maintenanceLogKey, from, approvalMode);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
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