package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CsChargeItem;
import com.simian.medallion.model.CsChargeProfile;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.vo.SelectItem;

//@Check("administration.chargeProfile")
public class ChargeProfiles extends MedallionController {
	private static Logger log = Logger.getLogger(ChargeProfiles.class);

	@Before(unless = { "list", "save", "confirm" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.chargeProfile")
	public static void list() {
		log.debug("list. ");

		List<CsChargeProfile> profiles = generalService.listChargeProfiles(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_PROFILE));
		render(profiles);
	}

	@Check("administration.chargeProfile")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsChargeProfile profile = null;
		// Set<ChargeItem> items = profile.getChargeItems();
		String detailItems = null;
		if (id != null) {
			profile = generalService.getChargeProfile(id);
			if (profile != null) {
				try {
					JsonHelper json = new JsonHelper().getChargeProfileSerializer();
					detailItems = json.serialize(profile.getChargeItems());
				} catch (JsonGenerationException ex) {
					log.debug("json.serialize");
				} catch (JsonMappingException ex) {
					log.debug("json.serialize");
				} catch (IOException ex) {
					log.debug("json.serialize");
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_PROFILE));
		render("ChargeProfiles/detail.html", profile, mode, detailItems);

	}

	@Check("administration.chargeProfile")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsChargeProfile profile = new CsChargeProfile();
		profile.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		Set<CsChargeItem> items = profile.getChargeItems();
		String detailItems = null;
		try {
			detailItems = json.writeValueAsString(profile.getChargeItems());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_PROFILE));
		render("ChargeProfiles/detail.html", profile, mode, detailItems, items);
	}

	@Check("administration.chargeProfile")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsChargeProfile profile = generalService.getChargeProfile(id);
		String status = profile.getRecordStatus();
		String detailItems = null;
		try {
			JsonHelper json = new JsonHelper().getChargeProfileSerializer();
			detailItems = json.serialize(profile.getChargeItems());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_PROFILE));
		render("ChargeProfiles/detail.html", profile, detailItems, mode, status);
	}

	@Check("administration.chargeProfile")
	public static void save(CsChargeProfile profile, CsChargeItem[] items, String mode, String status) {
		log.debug("save. profile: " + profile + " items: " + items + " mode: " + mode + " status: " + status);

		if (profile != null) {
			if (items != null) {
				// logger.debug("item size = " +items.size());
				for (CsChargeItem item : items) {
					if (item != null) {
						log.debug("ITEM KEY >>> " + item.getChargeItemKey());
						// item.getChargeMaster().setChargeCode(item.getChargeMaster().getChargeCode().toUpperCase());
						log.debug("security class = " + item.getSecurityClass());
						profile.getChargeItems().add(item);
					}
				}
			}

			log.debug("isACtive >> " + profile.getIsActive());
			String detailItems = null;
			try {
				if (items == null) {
					// items = new ArrayList<CsChargeItem>();
					// items = json.writeValueAsString(new
					// ArrayList<CsChargeItem>());
				}
				detailItems = json.writeValueAsString(items);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			if (validation.errorsMap().values().contains(items) == false) {
				validation.clear();
			}
			validation.required("Code is", profile.getName());
			validation.required("Description is", profile.getDescription());
			if (validation.hasErrors()) {
				log.debug("ERROR");
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_PROFILE));
				render("ChargeProfiles/detail.html", profile, items, mode, detailItems, status);
			} else {

				Long id = profile.getChargeProfileKey();
				serializerService.serialize(session.getId(), profile.getChargeProfileKey(), profile);
				confirming(id, mode, status);
			}
		} else {
			/*
			 * validation.clear();
			 * flash.error(ExceptionConstants.PARAMETER_NULL, profile);
			 */
			entry();
		}
	}

	@Check("administration.chargeProfile")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		boolean confirming = true;
		CsChargeProfile profile = serializerService.deserialize(session.getId(), id, CsChargeProfile.class);
		Set<CsChargeItem> items = new HashSet<CsChargeItem>(profile.getChargeItems());
		if (items != null) {
			for (CsChargeItem item : items) {
				log.debug("ITEM KEY CONFIRMING >>> " + item.getChargeItemKey());
			}
		}
		String detailItems = null;
		try {
			detailItems = json.writeValueAsString(profile.getChargeItems());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_PROFILE));
		render("ChargeProfiles/detail.html", profile, mode, detailItems, confirming, id, status);

	}

	@Check("administration.chargeProfile")
	public static void confirm(String param, CsChargeProfile profile, CsChargeItem[] items, String mode, String status) {
		log.debug("confirm. param: " + param + " profile: " + profile + " items: " + items + " mode: " + mode + " status: " + status);

		if (profile == null) {
			back(null, mode, null, null, status);
		}
		String detailItems = null;
		try {
			detailItems = json.writeValueAsString(items);
			detailItems.toString();
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}
		try {
			// logger.debug("SIZE ITEM = " +items.size());
			for (CsChargeItem item : items) {
				log.debug("COde SEC CLASS = " + item.getSecurityClass().getLookupCode());
				log.debug("ID SEC CLASS = " + item.getSecurityClass().getLookupId());
				if ((item.getSecurityClass().getLookupId().isEmpty()) || (item.getSecurityClass().getLookupCode().equals("*ALL"))) {
					log.debug("masuk sini 1 = ");
					item.setSecurityClass(null);
				}
				log.debug("security class = " + item.getSecurityClass());

				log.debug("Security Type = " + item.getSecurityType().getSecurityType());
				if ((item.getSecurityType().getSecurityType().equals("*ALL"))) {
					log.debug("masuk sini 2 = ");
					item.setSecurityType(null);
				}
				log.debug("security Type = " + item.getSecurityType());

				log.debug("Code Security = " + item.getSecurity().getSecurityId());
				log.debug("Key Security = " + item.getSecurity().getSecurityKey());
				if ((item.getSecurity().getSecurityKey() == null) || (item.getSecurity().getSecurityId().equals("*ALL"))) {
					log.debug("masuk sini 3 = ");
					item.setSecurity(null);
				}
				log.debug("security = " + item.getSecurity());

				log.debug("Code transaction = " + item.getTransactionTemplate().getTransactionCode());
				log.debug("Key transaction = " + item.getTransactionTemplate().getTransactionTemplateKey());
				if ((item.getTransactionTemplate().getTransactionTemplateKey() == null) || (item.getTransactionTemplate().getTransactionCode().equals("*ALL"))) {
					log.debug("masuk sini 4 = ");
					item.setTransactionTemplate(null);
				}
				log.debug("transaction template = " + item.getTransactionTemplate());
			}

			List<CsChargeProfile> profiles = generalService.listChargeProfiles(UIConstants.SIMIAN_BANK_ID);
			if (profiles.size() == 0) {
				profile.setIsDefault(true);
			}
			if (profile.getIsDefault() == null) {
				log.debug("is default false");
				profile.setIsDefault(false);
			} else {
				log.debug("is default true");
				profile.setIsDefault(true);
			}

			if (!(UIConstants.DISPLAY_MODE_ENTRY.equals(mode))) {
				if ((profile.getIsDefault() == true) && (profile.getIsActive() == false)) {
					List<SelectItem> operators = UIHelper.yesNoOperators();
					renderArgs.put("operators", operators);
					throw new MedallionException(ExceptionConstants.CHARGE_PROFILE_DEFAULT_INACTIVE);
				}
			}
			profile.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));

			if (items != null) {
				profile.setChargeItems(new HashSet<CsChargeItem>());
				for (CsChargeItem item : items) {
					profile.getChargeItems().add(item);
				}
			}

			// ==== VALIDATE DEFAULT VALUE ==== //
			if (!(UIConstants.DISPLAY_MODE_ENTRY.equals(mode))) {
				CsChargeProfile oldProfile = generalService.getChargeProfile(profile.getChargeProfileKey());
				if (oldProfile.getIsDefault() == true) {
					if (!(oldProfile.getIsDefault().equals(profile.getIsDefault()))) {
						List<SelectItem> operators = UIHelper.yesNoOperators();
						renderArgs.put("operators", operators);
						throw new MedallionException("as the Default profile, uncheck 'Default' is not allowed !");
					}
				}
			}
			validation.clear();
			generalService.saveChargeProfile(MenuConstants.CS_CHARGE_PROFILE, profile, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			log.error(e.getMessage(), e);
			validation.clear();
			flash.error("Profile Code : '" + profile.getName() + "' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_PROFILE));
			render("ChargeProfiles/detail.html", profile, detailItems, mode, confirming, e, status);
		}
	}

	@Check("administration.chargeProfile")
	public static void back(Long id, String mode, CsChargeProfile profile, List<CsChargeItem> items, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " profile: " + profile + " items: " + items + " status: " + status);

		boolean confirming = false;
		profile = serializerService.deserialize(session.getId(), id, CsChargeProfile.class);
		String detailItems = null;
		try {
			detailItems = json.writeValueAsString(profile.getChargeItems());
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}
		// flash.clear();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_PROFILE));
		renderTemplate("ChargeProfiles/detail.html", profile, mode, items, detailItems, confirming, status);
	}

	// @Check("workflow.approval")
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CsChargeProfile profile = json.readValue(maintenanceLog.getNewData(), CsChargeProfile.class);
			Set<CsChargeItem> items = profile.getChargeItems();
			String detailItems = null;
			try {
				detailItems = json.writeValueAsString(profile.getChargeItems());
			} catch (JsonGenerationException ex) {
				log.debug("json.serialize");
			} catch (JsonMappingException ex) {
				log.debug("json.serialize");
			} catch (IOException ex) {
				log.debug("json.serialize");
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ChargeProfiles/approval.html", profile, detailItems, items, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// @Check("workflow.approval")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveChargeProfile(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("workflow.approval")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveChargeProfile(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}