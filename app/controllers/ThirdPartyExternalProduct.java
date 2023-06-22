package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

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
import com.simian.medallion.model.GnThirdPartyBank;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class ThirdPartyExternalProduct extends MedallionController {
	private static Logger log = Logger.getLogger(ThirdPartyExternalProduct.class);

	//private static String FIELDDELIMITER = "\n";

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		String recordStatusNew = LookupConstants.__RECORD_STATUS_NEW;
		renderArgs.put("recordStatusNew", recordStatusNew);

		String recordStatusUpdate = LookupConstants.__RECORD_STATUS_UPDATED;
		renderArgs.put("recordStatusUpdate", recordStatusUpdate);

		String fundManager = LookupConstants.THIRD_PARTY_FUND_MANAGER;
		renderArgs.put("fundManager", fundManager);

	}

	@Check("administration.thirdParty.externalProduct")
	public static void list(String group) {
		log.debug("list. group: " + group);

		GnLookup lookup = generalService.getLookup(group);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
		render("ThirdPartyExternalProduct/list.html", group, lookup);
	}

	public static void group() {
		log.debug("group. ");

		List<GnLookup> lookups = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._THIRD_PARTY);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
		render(lookups);
	}

	@Check("administration.thirdParty.externalProduct")
	public static void paging(Paging page) {
		log.debug("paging. page: " + page);

		// page.addParams("cs.invoice_no", page.LIKE,
		// UIHelper.withOperator(page.getsSearch(), 1));
		page.addParams("tp.third_party_type", page.EQUAL, LookupConstants.THIRD_PARTY_EXTERNAL_PRODUCT);
		page.addParams(Helper.searchAll("(tp.THIRD_PARTY_CODE||tp.THIRD_PARTY_NAME)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = generalService.pagingListThirdPartiesByType(page);
		log.debug("json ---> " + page);
		renderJSON(page);
	}

	@Check("administration.thirdParty.externalProduct")
	public static void entry(String group) {
		log.debug("entry. group: " + group);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnThirdParty thirdParty = new GnThirdParty();
		thirdParty.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		thirdParty.setThirdPartyType(new GnLookup(group));
		thirdParty.setIsActive(false);
		GnLookup lookup = generalService.getLookup(group);
		GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
		render("ThirdPartyExternalProduct/detail.html", thirdParty, thirdPartyBank, group, mode, lookup);
	}

	@Check("administration.thirdParty.externalProduct")
	public static void edit(Long id, String group) {
		log.debug("edit. id: " + id + " group: " + group);

		try {
			String mode = UIConstants.DISPLAY_MODE_EDIT;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			//String status = thirdParty.getRecordStatus();
			GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
			if (!thirdParty.getThirdPartyBanks().isEmpty()) {
				Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
				thirdPartyBank = tpBanks.iterator().next();
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
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
			render("ThirdPartyExternalProduct/detail.html", thirdParty, thirdPartyBank, group, mode);

		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.thirdParty.externalProduct")
	public static void view(Long id, String group) {
		log.debug("view. id: " + id + " group: " + group);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnThirdParty thirdParty = generalService.getThirdParty(id);
			GnThirdPartyBank thirdPartyBank = new GnThirdPartyBank();
			if (!thirdParty.getThirdPartyBanks().isEmpty()) {
				Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
				thirdPartyBank = tpBanks.iterator().next();
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
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
			render("ThirdPartyExternalProduct/detail.html", thirdParty, thirdPartyBank, mode);

		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.thirdParty.externalProduct")
	public static void save(String mode, GnThirdParty thirdParty, GnThirdPartyBank thirdPartyBank, String status) {
		log.debug("save. mode: " + mode + " thirdParty: " + thirdParty + " thirdPartyBank: " + thirdPartyBank + " status: " + status);

		if (thirdParty != null) {
			validation.required("Code is", thirdParty.getThirdPartyCode());
			validation.required("Name is", thirdParty.getThirdPartyName());
			validation.required("Currency is", thirdParty.getCurrency().getCurrencyCode());
			validation.required("Fund Manager is", thirdParty.getFundManager().getThirdPartyKey());
			validation.required("Custodian Bank is", thirdParty.getCustodianBank());
			if (thirdPartyBank != null) {
				validation.required("Bank Code is", thirdPartyBank.getBankCode().getThirdPartyKey());
				validation.required("Account No is", thirdPartyBank.getAccountNo());
				validation.required("Beneficiary Name is", thirdPartyBank.getAccountName());
				validation.required("Currency bank is", thirdPartyBank.getCurrency().getCurrencyCode());
				validation.required("Branch is", thirdPartyBank.getBranchCode());
				validation.required("BIC Code is", thirdPartyBank.getBicCode());
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
				render("ThirdPartyExternalProduct/detail.html", thirdParty, thirdPartyBank, mode);
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
			entry("THIRD_PARTY-EXTERNAL_PRODUCT");
		}
	}

	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		try {
			renderArgs.put("confirming", true);
			GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
			Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
			GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
			render("ThirdPartyExternalProduct/detail.html", thirdParty, thirdPartyBank, mode, status);

		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.thirdParty.externalProduct")
	public static void back(String id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		GnThirdParty thirdParty = serializerService.deserialize(session.getId(), id, GnThirdParty.class);
		Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
		GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
		render("ThirdPartyExternalProduct/detail.html", thirdParty, thirdPartyBank, mode, status);
	}

	@Check("administration.thirdParty.externalProduct")
	public static void confirm(GnThirdParty thirdParty, GnThirdPartyBank thirdPartyBank, String mode, String status, String group) {
		log.debug("confirm. thirdParty: " + thirdParty + " thirdPartyBank: " + thirdPartyBank + " mode: " + mode + " status: " + status + " group: " + group);

		try {
			if (thirdParty == null)
				back(null, mode, status);
			Set<GnThirdPartyBank> tpBanks = new HashSet<GnThirdPartyBank>();
			if (thirdPartyBank != null) {
				tpBanks.add(thirdPartyBank);
			}
			thirdParty.setThirdPartyBanks(tpBanks);
			thirdParty.setThirdPartyType(new GnLookup(LookupConstants.THIRD_PARTY_EXTERNAL_PRODUCT));
			generalService.saveThirdParty(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT, thirdParty, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list(group);
		} catch (MedallionException e) {
			//GnLookup lookup = generalService.getLookup(group);
			flash.error("Third Party Code : ' " + thirdParty.getThirdPartyCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_THIRD_PARTY_EXTERNAL_PRODUCT));
			render("ThirdPartyExternalProduct/detail.html", thirdParty, thirdPartyBank, mode, status, group, confirming);

		}
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			boolean approvalMode = true;
			log.debug("Maintenance log key " + maintenanceLogKey);

			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnThirdParty thirdParty = json.readValue(maintenanceLog.getNewData(), GnThirdParty.class);
			GnLookup lookup = generalService.getLookup(thirdParty.getThirdPartyType().getLookupId());
			Set<GnThirdPartyBank> tpBanks = thirdParty.getThirdPartyBanks();
			GnThirdPartyBank thirdPartyBank = tpBanks.iterator().next();

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

			render("ThirdPartyExternalProduct/approval.html", thirdParty, thirdPartyBank, lookup, mode, taskId, operation, maintenanceLogKey, from, approvalMode);
		} catch (Exception e) {
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
