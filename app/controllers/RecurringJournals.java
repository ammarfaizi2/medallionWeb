package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.FaTransactionSearchParametrs;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.FaRecurring;
import com.simian.medallion.model.FaRecurringDetail;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class RecurringJournals extends MedallionController {
	private static Logger log = Logger.getLogger(RecurringJournals.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "entry", "save", "confirming", "confirm", "back", "edit", "approval", "view" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);
		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
		renderArgs.put("depositoryCodes", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITORY_CODE));
	}

	@Check("transaction.recurringJournal")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
		render("RecurringJournals/list.html");
	}

	@Check("transaction.recurringJournal")
	public static void search(FaTransactionSearchParametrs params) {
		log.debug("search. params: " + params);

		List<FaRecurring> faRecurrings = fundService.searchRecurringJournal(params.faTransactionSearchTransactionDateFrom, params.faTransactionSearchTransactionDateTo, params.faTransactionSearchFundKey, UIHelper.withOperator(params.transactionSearchNoOperator, params.TransactionNoOperator));
		for (FaRecurring faRecurring : faRecurrings) {
			BigDecimal totalAmount = fundService.totalAmountFaRecurring(faRecurring.getRecurringKey());
			faRecurring.setTotalAmount(totalAmount);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
		render("RecurringJournals/grid.html", faRecurrings);
	}

	@Check("transaction.recurringJournal")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaRecurring faRecurring = new FaRecurring();
		String recurringDetails = null;
		try {
			recurringDetails = json.writeValueAsString(faRecurring.getFaRecurringDetails());
			log.debug(">>> recurringDetails=" + recurringDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
		render("RecurringJournals/entry.html", faRecurring, mode, recurringDetails);
	}

	@Check("transaction.recurringJournal")
	public static void save(FaRecurring faRecurring, FaRecurringDetail[] faRecurringDetails, String mode) {
		log.debug("save. faRecurring: " + faRecurring + " faRecurringDetails: " + faRecurringDetails + " mode: " + mode);

		if (faRecurring != null) {
			String recurringDetails = null;
			try {
				if (faRecurringDetails == null) {
					faRecurring.setFaRecurringDetails(new HashSet<FaRecurringDetail>());
					recurringDetails = json.writeValueAsString(faRecurring.getFaRecurringDetails());
				} else {
					recurringDetails = json.writeValueAsString(faRecurringDetails);
				}

				log.debug(">>> recurringDetails=" + recurringDetails);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			if (faRecurringDetails != null) {
				for (FaRecurringDetail faRecurringDetail : faRecurringDetails) {
					if ((faRecurringDetail.getRecurringDetailKey() == null) || (faRecurringDetail.getRecurringDetailKey() == null)) {
						validation.clear();
					}
				}
			}

			validation.required("Fund Code is", faRecurring.getFaMaster().getFundCode());
			validation.required("Name is", faRecurring.getRecurringName());
			validation.required("Effective Date From is", faRecurring.getEffectiveDateFrom());
			validation.required("Effective Date To is", faRecurring.getEffectiveDateTo());

			if (faRecurring.getEffectiveDateFrom() != null && faRecurring.getEffectiveDateTo() != null) {
				if (faRecurring.getEffectiveDateFrom().after(faRecurring.getEffectiveDateTo())) {
					validation.addError("", "Effective Date From should be before than Effective Date To");
				}
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
				render("RecurringJournals/entry.html", faRecurring, faRecurringDetails, mode, recurringDetails);
			} else {
				if (faRecurringDetails != null) {
					for (FaRecurringDetail faRecurringDetail : faRecurringDetails) {
						if (faRecurringDetail != null) {
							faRecurring.getFaRecurringDetails().add(faRecurringDetail);
						}
					}
				}
				// Validate here
				serializerService.serialize(session.getId(), faRecurring.getRecurringKey(), faRecurring);
				confirming(faRecurring.getRecurringKey(), mode);
			}
		} else {
			// flash.error("argument.null", faRecurring);
			entry();
		}
	}

	@Check("transaction.recurringJournal")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		FaRecurring faRecurring = serializerService.deserialize(session.getId(), id, FaRecurring.class);

		String recurringDetails = null;
		try {
			recurringDetails = json.writeValueAsString(faRecurring.getFaRecurringDetails());
			log.debug(">>> recurringDetails=" + recurringDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
		render("RecurringJournals/entry.html", faRecurring, mode, confirming, recurringDetails);
	}

	@Check("transaction.recurringJournal")
	public static void confirm(FaRecurring faRecurring, FaRecurringDetail[] faRecurringDetails, String mode) {
		log.debug("confirm. faRecurring: " + faRecurring + " faRecurringDetails: " + faRecurringDetails + " mode: " + mode);

		boolean confirming = true;
		if (faRecurring == null) {
			back(null, mode);
		}

		ArrayList<FaRecurringDetail> currDetail = new ArrayList<FaRecurringDetail>();
		if (faRecurringDetails != null) {
			for (FaRecurringDetail fa_ : faRecurringDetails) {
				if (fa_ != null) {
					currDetail.add(fa_);
				}
			}
		}
		String recurringDetails = null;
		try {
			// recurringDetails = json.writeValueAsString(faRecurringDetails);
			recurringDetails = json.writeValueAsString(currDetail);
			log.debug(">>> recurringDetails=" + recurringDetails);
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}

		try {

			if (faRecurringDetails != null) {
				for (FaRecurringDetail faRecurringDetail : faRecurringDetails) {
					if (faRecurringDetail != null) {
						faRecurring.getFaRecurringDetails().add(faRecurringDetail);
					}
				}
			}
			log.debug("size " + faRecurring.getFaRecurringDetails().size());
			FaRecurring trx = fundService.createRecurringJournal(MenuConstants.FA_RECURRING_JOURNAL, faRecurring, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			if (trx.getRecurringKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				// result.put("transactionNo", trx.getTransactionKey());
				result.put("message", Messages.get("recurring.confirmed.successful", trx.getRecurringKey()));
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
				render("RecurringJournals/entry.html", trx, faRecurringDetails, recurringDetails, mode, confirming);
			}
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if ((ex.getParams() != null) && (ex.getParams().length > 0)) {
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			} else {
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			renderJSON(result);
		}catch (Exception e) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error",e.getMessage());
			renderJSON(result);
		}
	}

	@Check("transaction.recurringJournal")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		FaRecurring faRecurring = serializerService.deserialize(session.getId(), id, FaRecurring.class);
		log.debug("faTransaction.getTransactionKey " + faRecurring.getRecurringKey());
		String recurringDetails = null;
		try {
			recurringDetails = json.writeValueAsString(faRecurring.getFaRecurringDetails());
			log.debug(">>> recurringDetails=" + recurringDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
		render("RecurringJournals/entry.html", faRecurring, mode, recurringDetails);
	}

	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);
	}

	public static void cancel(String taskId) {
		log.debug("cancel. taskId: " + taskId);
	}

	@Check("transaction.recurringJournal")
	public static void edit(Long id, String taskId) {
		log.debug("edit. id: " + id + " taskId: " + taskId);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaRecurring faRecurring = fundService.getFaRecurring(id);
		List<FaRecurringDetail> faRecurringDetails = fundService.listFaRecurringDetail(id);
		String recurringDetails = null;
		try {
			recurringDetails = json.writeValueAsString(faRecurringDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
		render("RecurringJournals/entry.html", faRecurring, recurringDetails, taskId, mode);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			JsonHelper jsonFaRecurringDetail = new JsonHelper().getFaRecurringDetailSerializer();
			FaRecurring faRecurring = fundService.getFaRecurring(keyId);

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
			render("RecurringJournals/approval.html", faRecurring, taskId, mode, recurringDetails, from, operation, maintenanceLogKey);
		} catch (Exception e) {
			log.error("Fa Recurring Approval", e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			FaRecurring faRecurring = fundService.approveRecurringJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null);

			renderJSON(Formatter.resultSuccess(Messages.get("recurring.approved", faRecurring.getRecurringKey())));
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
				faRecurring = fundService.approveRecurringJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, correction);
			} else {
				faRecurring = fundService.approveRecurringJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction);
			}

			renderJSON(Formatter.resultSuccess(Messages.get("recurring.rejected", faRecurring.getRecurringKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.recurringJournal")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaRecurring faRecurring = fundService.getFaRecurring(id);
		List<FaRecurringDetail> faRecurringDetails = fundService.listFaRecurringDetail(id);
		String recurringDetails = null;
		try {
			recurringDetails = json.writeValueAsString(faRecurringDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_RECURRING_JOURNAL));
		render("RecurringJournals/detail.html", faRecurring, recurringDetails, mode);
	}
}