package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import vo.FaRecurringSearchParameters;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.FaRecurring;
import com.simian.medallion.model.FaRecurringDetail;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class CancelRecurringJournals extends MedallionController {
	private static Logger log = Logger.getLogger(CancelRecurringJournals.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "approval", "view" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);
		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
		renderArgs.put("depositoryCodes", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITORY_CODE));
	}

	@Check("transaction.cancelRecurringJournal")
	public static void cancel(FaRecurring faRecurring, String mode) {
		log.debug("cancel. faRecurring: " + faRecurring + " mode: " + mode);

		String menu = MenuConstants.FA_CANCEL_RECURRING_JOURNAL;
		String username = session.get(UIConstants.SESSION_USERNAME);
		String userKey = session.get(UIConstants.SESSION_USER_KEY);

		FaRecurring trx = fundService.createCancelRecurringJournal(menu, username, userKey, faRecurring);

		Map<String, Object> result = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat(UIConstants.DATE_FORMAT);

		result.put("status", "success");
		result.put("recurringNo", trx.getRecurringKey());
		result.put("recurringStatus", trx.getStatus().trim());
		result.put("cancelDate", dateFormat.format(trx.getCancelledDate()));
		renderJSON(result);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			JsonHelper jsonFaRecurring = new JsonHelper().getFaRecurringSerializer();
			JsonHelper jsonFaRecurringDetail = new JsonHelper().getFaRecurringDetailSerializer();
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);

			FaRecurring faRecurring = jsonFaRecurring.deserialize(maintenanceLog.getNewData(), FaRecurring.class);

			if ((faRecurring.getFaRecurringDetails() != null) && (faRecurring.getFaRecurringDetails().size() > 0)) {
				List<FaRecurringDetail> lstFaRecurringDetails = new ArrayList<FaRecurringDetail>(faRecurring.getFaRecurringDetails());
				faRecurring.getFaRecurringDetails().clear();

				for (FaRecurringDetail faRecurringDetail : lstFaRecurringDetails) {
					faRecurringDetail.setFaRecurring(faRecurring);
				}

				faRecurring.getFaRecurringDetails().addAll(lstFaRecurringDetails);
			}

			String recurringDetails = null;
			try {
				recurringDetails = jsonFaRecurringDetail.serialize(faRecurring.getFaRecurringDetails());
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
			render("CancelRecurringJournals/approval.html", faRecurring, taskId, mode, recurringDetails, from, operation, maintenanceLogKey);
		} catch (Exception e) {
			log.error("Fa Cancel Recurring Approval", e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			FaRecurring faRecurring = fundService.approveCancelRecurringJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess(Messages.get("cancelRecurring.approved", faRecurring.getRecurringKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			FaRecurring faRecurring = new FaRecurring();
			if (Helper.isNullOrEmpty(correction)) {
				faRecurring = fundService.approveCancelRecurringJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);
			} else {
				faRecurring = fundService.approveCancelRecurringJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "");
			}

			renderJSON(Formatter.resultSuccess(Messages.get("cancelRecurring.rejected", faRecurring.getRecurringKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.cancelRecurringJournal")
	public static void view(Long key) {
		log.debug("view. key: " + key);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaRecurring faRecurring = fundService.getFaRecurring(key);
		String recurringDetails = null;
		try {
			List<FaRecurringDetail> faRecurringDetails = fundService.listFaRecurringDetail(key);
			recurringDetails = json.writeValueAsString(faRecurringDetails);
			log.debug(">>> recurringDetails=" + recurringDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CANCEL_RECURRING_JOURNAL));
		render("CancelRecurringJournals/cancel.html", faRecurring, mode, recurringDetails);
	}

	@Check("transaction.cancelRecurringJournal")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CANCEL_RECURRING_JOURNAL));
		render("CancelRecurringJournals/list.html");
	}

	public static void search(FaRecurringSearchParameters params) {
		log.debug("search. params: " + params);

		List<FaRecurring> faRecurrings = fundService.searchCancelFaRecurring(params.faRecurringSearchEffectiveDateFrom, params.faRecurringSearchEffectiveDateTo, params.faRecurringSearchFundKey, UIHelper.withOperator(params.recurringSearchNoOperator, params.recurringNoOperator));
		for (FaRecurring faRecurring : faRecurrings) {
			BigDecimal totalAmount = fundService.totalAmountFaRecurring(faRecurring.getRecurringKey());
			faRecurring.setTotalAmount(totalAmount);
		}

		render("CancelRecurringJournals/grid.html", faRecurrings);
	}

	@Check("transaction.cancelRecurringJournal")
	public static void entry() {
		log.debug("entry. ");
	}

	@Check("transaction.cancelRecurringJournal")
	public static void edit() {
		log.debug("edit. ");
	}
}