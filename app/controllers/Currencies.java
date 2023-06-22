package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnCurrency;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class Currencies extends MedallionController {
	private static Logger log = Logger.getLogger(Currencies.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.currency")
	public static void list() {
		log.debug("list. ");

		List<GnCurrency> currencies = generalService.listCurrencies();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CURRENCY));
		render(currencies);
	}

	@Check("administration.currency")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnCurrency currency = new GnCurrency();
		boolean isNewRec = true;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CURRENCY));
		renderTemplate("Currencies/detail.html", currency, mode, isNewRec);
	}

	@Check("administration.currency")
	public static void edit(String id, boolean isNewRec) {
		log.debug("edit. id: " + id + " isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		isNewRec = false;
		GnCurrency currency = generalService.getCurrency(id);
		String status = currency.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CURRENCY));
		render("Currencies/detail.html", currency, mode, isNewRec, status);
	}

	@Check("administration.currency")
	public static void view(String id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		GnCurrency currency = generalService.getCurrency(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CURRENCY));
		render("Currencies/detail.html", currency, mode);
	}

	@Check("administration.currency")
	public static void save(String mode, GnCurrency currency, boolean isNewRec, String status) {
		log.debug("save. mode: " + mode + " currency: " + currency + " isNewRec: " + isNewRec + " status: " + status);

		if (currency != null) {
			validation.required("Currency Code is", currency.getCurrencyCode());
			validation.required("Description is", currency.getDescription());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CURRENCY));
				render("Currencies/detail.html", currency, mode, isNewRec, status);
			} else {
				String id = currency.getCurrencyCode();
				String json = serializerService.serialize(session.getId(), currency.getCurrencyCode(), currency);
				confirming(id, mode, isNewRec, status);
			}
		} else {
			// flash.error("argument.null", currency);
			entry();
		}
	}

	@Check("administration.currency")
	public static void confirming(String id, String mode, boolean isNewRec, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		renderArgs.put("confirming", true);
		GnCurrency currency = serializerService.deserialize(session.getId(), id, GnCurrency.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CURRENCY));
		render("Currencies/detail.html", currency, mode, isNewRec, status);
	}

	@Check("administration.currency")
	public static void confirm(GnCurrency currency, String mode, boolean isNewRec, String status) {
		log.debug("confirm. currency: " + currency + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		try {
			if (currency == null)
				back(null, mode, isNewRec, status);
			// DUPLICATE CURRENCY CODE VALIDATION
			// ===============================================================================
			List<GnCurrency> currencies = generalService.listCurrencies();
			for (GnCurrency curInTable : currencies) {
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					if (curInTable.getCurrencyCode().equals(currency.getCurrencyCode())) {
						flash.error("Currency Code : ' " + currency.getCurrencyCode() + " ' " + ExceptionConstants.DATA_DUPLICATE);
						boolean confirming = true;
						render("Currencies/detail.html", currency, mode, confirming, isNewRec, status);
					}
				}
			}
			// ==================================================================================================================
			generalService.saveCurrency(MenuConstants.GN_CURRENCY, currency, session.get(UIConstants.SESSION_USERNAME), "", isNewRec, session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Currency Code : ' " + currency.getCurrencyCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CURRENCY));
			render("Currencies/detail.html", currency, mode, confirming, isNewRec, status);
		}
	}

	@Check("administration.currency")
	public static void back(String id, String mode, boolean isNewRec, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		GnCurrency currency = serializerService.deserialize(session.getId(), id, GnCurrency.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_CURRENCY));
		render("Currencies/detail.html", currency, mode, isNewRec, status);
	}

	public static void approval(String taskId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnCurrency currency = json.readValue(maintenanceLog.getNewData(), GnCurrency.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Currencies/approval.html", currency, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveCurrency(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveCurrency(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// private static CurrencyDetailViewModel getViewModel(String mode) {
	// CurrencyDetailViewModel model = new CurrencyDetailViewModel();
	// model.mode = mode;
	// return model;
	// }
}
