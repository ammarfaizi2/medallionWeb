package controllers;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.SystemParamConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnCurrencyExchange;
import com.simian.medallion.model.GnCurrencyProfile;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

@With(Secure.class)
public class CurrencyProfiles extends MedallionController {
	private static Logger log = Logger.getLogger(CurrencyProfiles.class);

	private static JsonHelper jsonCurrencyExchangeDetail = new JsonHelper().getCurrencyExchangeSerializer();
	private static JsonHelper jsonCurrencyProfile = new JsonHelper().getCurrencyProfileSerializer();

	private static final GnLookup exchangeRateMethodOtherToBase = generalService.getLookup(LookupConstants.EXCHANGE_RATE_OTHER_TO_BASE);
	private static final GnLookup exchangeRateMethodBaseToOther = generalService.getLookup(LookupConstants.EXCHANGE_RATE_BASE_TO_OTHER);

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		renderArgs.put("inputothertobaseval", exchangeRateMethodOtherToBase.getLookupId());
		renderArgs.put("inputbasetootherval", exchangeRateMethodBaseToOther.getLookupId());
		renderArgs.put("inputothertobasedesc", exchangeRateMethodOtherToBase.getLookupDescription());
		renderArgs.put("inputbasetootherdesc", exchangeRateMethodBaseToOther.getLookupDescription());
	}

	@Check("administration.exchangeRate")
	public static void paging(Paging page){
		page.addParams(Helper.searchAll("(to_char(profile_date,'DD/MM/YYYY')||b.lookupCode||c.currencyCode)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
		page = generalService.pagingCurrencyProfiles(page);
		renderJSON(page);
	}
	
	@Check("administration.exchangeRate")
	public static void list() {
		log.debug("list. ");

		List<GnCurrencyProfile> currencyProfiles = generalService.listCurrencyProfiles();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_EXCHANGE_RATE));
		render(currencyProfiles);
	}

	@Check("administration.exchangeRate")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		GnCurrencyProfile currencyProfile = generalService.getCurrencyProfile(id);

		String currencyExchangeTabs = null;

		try {
			currencyExchangeTabs = jsonCurrencyExchangeDetail.serialize(currencyProfile.getCurrencyProfileDetails());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_EXCHANGE_RATE));
		render("CurrencyProfiles/detail.html", currencyProfile, currencyExchangeTabs, mode);

	}

	@Check("administration.exchangeRate")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnCurrencyProfile currencyProfile = new GnCurrencyProfile();

		// default value
		currencyProfile.setCurrencyProfile(generalService.getLookup(generalService.getValueParam(SystemParamConstants.PARAM_CURRRENCY_PROFILE)));
		currencyProfile.setInputMethod(generalService.getLookup(LookupConstants.EXCHANGE_RATE_OTHER_TO_BASE));

		String currencyExchangeTabs = null;
		try {
			currencyExchangeTabs = jsonCurrencyExchangeDetail.serialize(currencyProfile.getCurrencyProfileDetails());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_EXCHANGE_RATE));
		render("CurrencyProfiles/detail.html", currencyProfile, mode, currencyExchangeTabs);
	}

	@Check("administration.exchangeRate")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnCurrencyProfile currencyProfile = generalService.getCurrencyProfile(id);

		String currencyExchangeTabs = null;
		String status = currencyProfile.getRecordStatus();
		try {
			currencyExchangeTabs = jsonCurrencyExchangeDetail.serialize(currencyProfile.getCurrencyProfileDetails());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_EXCHANGE_RATE));
		render("CurrencyProfiles/detail.html", currencyProfile, mode, status, currencyExchangeTabs);
	}

	@Check("administration.exchangeRate")
	public static void save(GnCurrencyProfile currencyProfile, String mode, String status, GnCurrencyExchange[] currencyExchangeDetails) {
		log.debug("save. currencyProfile: " + currencyProfile + " mode: " + mode + " status: " + status + " currencyExchangeDetails: " + currencyExchangeDetails);

		if (currencyExchangeDetails != null) {
			for (GnCurrencyExchange gnCurrencyExchange : currencyExchangeDetails) {
				currencyProfile.getCurrencyProfileDetails().add(gnCurrencyExchange);
			}
		}

		// Validate here
		if (currencyProfile != null) {
			log.debug("Aktiv >> " + currencyProfile.getActive());
			if (validation.errorsMap().values().containsAll(currencyProfile.getCurrencyProfileDetails()) == false) {
				validation.clear();
			}

			validation.required("Date", currencyProfile.getProfileDate());
			validation.required("Currency", currencyProfile.getBaseCurrency().getCurrencyCode());
			validation.required("Exchange Rate", currencyProfile.getCurrencyProfile().getLookupCode());

			if ((currencyProfile.getCurrencyProfileDetails() == null) || (currencyProfile.getCurrencyProfileDetails().size() == 0)) {
				validation.required(currencyProfile.getCurrencyProfileDetails());
			}

			if (mode.equals(UIConstants.DISPLAY_MODE_ENTRY)) {
				for (GnCurrencyExchange gnCurrencyExchange : currencyProfile.getCurrencyProfileDetails()) {
					if (currencyProfile.getInputMethod().getLookupId().equalsIgnoreCase(exchangeRateMethodOtherToBase.getLookupId())) {
						if (currencyProfile.getBaseCurrency().getCurrencyCode().equalsIgnoreCase(gnCurrencyExchange.getSourceCurrency().getCurrencyCode())) {
							validation.addError(currencyProfile.getBaseCurrency().getCurrencyCode(), "Base Currency already exist in Other Currency!");
						}
					} else if (currencyProfile.getInputMethod().getLookupId().equalsIgnoreCase(exchangeRateMethodBaseToOther.getLookupId())) {
						if (currencyProfile.getBaseCurrency().getCurrencyCode().equalsIgnoreCase(gnCurrencyExchange.getTargetCurrency().getCurrencyCode())) {
							validation.addError(currencyProfile.getBaseCurrency().getCurrencyCode(), "Base Currency already exist in Other Currency!");
						}
					}
				}
			}

			if (validation.hasErrors()) {
				String currencyExchangeTabs = null;
				try {
					currencyExchangeTabs = jsonCurrencyExchangeDetail.serialize(currencyProfile.getCurrencyProfileDetails());
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_EXCHANGE_RATE));
				render("CurrencyProfiles/detail.html", currencyProfile, currencyExchangeDetails, currencyExchangeTabs, mode, status);
			} else {
				Long id = currencyProfile.getCurrencyProfileKey();
				serializerService.serialize(session.getId(), id, currencyProfile);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", currencyProfile);
			entry();
		}
	}

	@Check("administration.exchangeRate")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		GnCurrencyProfile currencyProfile = serializerService.deserialize(session.getId(), id, GnCurrencyProfile.class);
		String currencyExchangeTabs = null;
		try {
			currencyExchangeTabs = jsonCurrencyExchangeDetail.serialize(currencyProfile.getCurrencyProfileDetails());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_EXCHANGE_RATE));
		render("CurrencyProfiles/detail.html", currencyProfile, currencyExchangeTabs, mode, status);
	}

	@Check("administration.exchangeRate")
	public static void confirm(GnCurrencyProfile currencyProfile, GnCurrencyExchange[] currencyExchangeDetails, String mode, String status) {
		log.debug("confirm. currencyProfile: " + currencyProfile + " currencyExchangeDetails: " + currencyExchangeDetails + " mode: " + mode + " status: " + status);

		if (currencyProfile == null)
			back(null, mode, status);
		try {
			if (currencyExchangeDetails != null) {
				for (GnCurrencyExchange gnCurrencyExchange : currencyExchangeDetails) {
					if (currencyProfile.getInputMethod().getLookupId().equals(exchangeRateMethodOtherToBase.getLookupId())) {
						gnCurrencyExchange.setTargetCurrency(currencyProfile.getBaseCurrency());
					} else if (currencyProfile.getInputMethod().getLookupId().equals(exchangeRateMethodBaseToOther.getLookupId())) {
						gnCurrencyExchange.setSourceCurrency(currencyProfile.getBaseCurrency());
					}
					gnCurrencyExchange.setExchangeDate(currencyProfile.getProfileDate());
					currencyProfile.getCurrencyProfileDetails().add(gnCurrencyExchange);
				}
			}

			currencyProfile = generalService.saveExchangeCurrency(MenuConstants.GN_EXCHANGE_RATE, currencyProfile, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Date : ' " + new SimpleDateFormat(appProp.getDateFormat()).format(currencyProfile.getProfileDate()) + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			String currencyExchangeTabs = null;
			try {
				currencyExchangeTabs = jsonCurrencyExchangeDetail.serialize(currencyExchangeDetails);
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
			validation.clear();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_EXCHANGE_RATE));
			render("CurrencyProfiles/detail.html", currencyProfile, currencyExchangeTabs, mode, confirming, status);
		}
	}

	@Check("administration.exchangeRate")
	public static void back(String id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		GnCurrencyProfile currencyProfile = serializerService.deserialize(session.getId(), id, GnCurrencyProfile.class);
		String currencyExchangeTabs = null;
		try {
			currencyExchangeTabs = jsonCurrencyExchangeDetail.serialize(currencyProfile.getCurrencyProfileDetails());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_EXCHANGE_RATE));
		render("CurrencyProfiles/detail.html", currencyProfile, mode, status, currencyExchangeTabs);
	}

	public static void approval(String taskId, String keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnCurrencyProfile currencyProfile = jsonCurrencyProfile.deserialize(maintenanceLog.getNewData(), GnCurrencyProfile.class);
			String currencyExchangeTabs = null;

			try {
				currencyExchangeTabs = jsonCurrencyExchangeDetail.serialize(currencyProfile.getCurrencyProfileDetails());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("CurrencyProfiles/approval.html", currencyProfile, currencyExchangeTabs, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error("Exchange Rate Approval", e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveExchangeCurrency(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveExchangeCurrency(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
