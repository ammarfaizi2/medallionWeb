package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.FaTransaction;
import com.simian.medallion.model.FaTransactionDetail;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class ManualJournals extends MedallionController {
	private static Logger log = Logger.getLogger(ManualJournals.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(only = { "entry", "save", "confirming", "confirm", "back", "clear", "edit", "approval", "view" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);
		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
		renderArgs.put("depositoryCodes", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITORY_CODE));
	}

	public static void test(long key) {
		log.debug("test. key: " + key);

		CsTransaction transaction = accountService.getTransaction(key);
		render(transaction);
	}

	public static void testJSON(long key) {
		log.debug("testJSON. key: " + key);

		CsTransaction transaction = accountService.getTransaction(key);
		String data = serialize(transaction);
		renderJSON(data);
	}

	@Check("transaction.manualJournal")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
		render("ManualJournals/list.html");
	}

	@Check("transaction.manualJournal")
	public static void search(FaTransactionSearchParametrs params) {
		log.debug("search. params: " + params);

		List<FaTransaction> faTransactions = fundService.searchFaTransaction(params.faTransactionSearchTransactionDateFrom, params.faTransactionSearchTransactionDateTo, params.faTransactionSearchFundKey, UIHelper.withOperator(params.transactionSearchNoOperator, params.TransactionNoOperator));
		for (FaTransaction faTransaction : faTransactions) {
			BigDecimal totalAmount = fundService.totalAmountFaTransaction(faTransaction.getTransactionKey());
			faTransaction.setTotalAmount(totalAmount);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
		render("ManualJournals/grid.html", faTransactions);
	}

	@Check("transaction.manualJournal")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaTransaction faTransaction = new FaTransaction();
		faTransaction.setTransactionDate(generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate());
		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(faTransaction.getFaTransactionDetails());
			log.debug(">>> transactionDetails=" + transactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		// faTransaction.setTransactionDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
		render("ManualJournals/entry.html", faTransaction, mode, transactionDetails);
	}

	@Check("transaction.manualJournal")
	public static void save(FaTransaction faTransaction, FaTransactionDetail[] faTransactionDetails, String mode) {
		log.debug("save. faTransaction: " + faTransaction + " faTransactionDetails: " + faTransactionDetails + " mode: " + mode);

		if (faTransaction != null) {
			String transactionDetails = null;
			try {
				if (faTransactionDetails == null) {
					// faTransactionDetails = new
					// ArrayList<FaTransactionDetail>();
				}
				transactionDetails = json.writeValueAsString(faTransactionDetails);

			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			log.debug(">>> transactionDetails=" + transactionDetails);
			if (faTransactionDetails != null) {
				for (FaTransactionDetail faTransactionDetail : faTransactionDetails) {
					if ((faTransactionDetail.getFaTransaction().getTransactionKey() == null) || (faTransactionDetail.getFaTransaction() == null)) {
						validation.clear();
					}
				}
			}

			Map<String, String> validRes = fundService.validateJournalDate(faTransaction);
			if (!Helper.isNullOrEmpty(validRes.get("errorMsg"))) {
				log.debug("validRes " + validRes.get("errorMsg"));
				log.debug("validRes " + validRes.get("lastPostingDate"));

				validation.addError("", validRes.get("errorMsg") + " (" + validRes.get("lastPostingDate") + ")", "");
			}

			validation.required("Fund Code is", faTransaction.getFaMaster().getFundCode());
			validation.required("Name is", faTransaction.getName());
			validation.required("Journal Date is", faTransaction.getTransactionDate());
			if (validation.hasError("Fund Code is") || validation.hasError("Name is") || validation.hasError("Journal Date is") || validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
				render("ManualJournals/entry.html", faTransaction, faTransactionDetails, mode, transactionDetails);
			} else {
				if (faTransactionDetails != null) {
					for (FaTransactionDetail faTransactionDetail : faTransactionDetails) {
						if (faTransactionDetail != null) {
							faTransaction.getFaTransactionDetails().add(faTransactionDetail);
						}
					}
				}
				// Validate here
				serializerService.serialize(session.getId(), faTransaction.getTransactionKey(), faTransaction);
				confirming(faTransaction.getTransactionKey(), mode);
			}
		} else {
			// flash.error("argument.null", faTransaction);
			entry();

		}
	}

	@Check("transaction.manualJournal")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		FaTransaction faTransaction = serializerService.deserialize(session.getId(), id, FaTransaction.class);

		//List<FaTransactionDetail> faTransactionDetails = null;
		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(faTransaction.getFaTransactionDetails());
			log.debug(">>> transactionDetails=" + transactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
		render("ManualJournals/entry.html", faTransaction, mode, confirming, transactionDetails);
	}

	@Check("transaction.manualJournal")
	public static void confirm(FaTransaction faTransaction, FaTransactionDetail[] faTransactionDetails) {
		log.debug("confirm. faTransaction: " + faTransaction + " faTransactionDetails: " + faTransactionDetails);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		boolean confirming = true;
		if (faTransaction == null) {
			back(null, mode);
			// faTransactionDetails = new ArrayList<FaTransactionDetail>();
		}

		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(faTransactionDetails);
			log.debug(">>> transactionDetails=" + transactionDetails);
		} catch (JsonGenerationException ex) {
			log.debug("json.serialize");
		} catch (JsonMappingException ex) {
			log.debug("json.serialize");
		} catch (IOException ex) {
			log.debug("json.serialize");
		}

		try {

			if (faTransactionDetails != null) {
				for (FaTransactionDetail faTransactionDetail : faTransactionDetails) {
					if (faTransactionDetail != null) {
						faTransaction.getFaTransactionDetails().add(faTransactionDetail);
					}
				}
			}
			// FaTransaction trx = fundService.saveFaTransaction(faTransaction);

			faTransaction.setSource(MenuConstants.FA_MANUAL_JOURNAL);
			FaTransaction trx = fundService.createManualJournal(MenuConstants.FA_MANUAL_JOURNAL, faTransaction, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			log.debug("LENGTH DETAILNYA ADALAH = " + trx.getFaTransactionDetails().size() + " --- " + trx.getTransactionKey());
			if (trx.getTransactionKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				// result.put("transactionNo", trx.getTransactionKey());
				result.put("message", Messages.get("transaction.confirmed.successful", trx.getTransactionKey()));
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
				render("ManualJournals/entry.html", trx, faTransactionDetails, transactionDetails, mode, confirming);
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

	@Check("transaction.manualJournal")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		FaTransaction faTransaction = serializerService.deserialize(session.getId(), id, FaTransaction.class);
		log.debug("faTransaction.getTransactionKey " + faTransaction.getTransactionKey());
		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(faTransaction.getFaTransactionDetails());
			log.debug(">>> transactionDetails=" + transactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
		render("ManualJournals/entry.html", faTransaction, mode, transactionDetails);
	}

	@Check("transaction.manualJournal")
	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		CsTransaction transaction = new CsTransaction();
		transaction.setTransactionDate(new Date());
		transaction = accountService.getTransactionCharges(UIConstants.SIMIAN_BANK_ID, transaction);
		List<GnUdfMaster> udfs = generalService.listUdfMastersByCategory("TRANSACTION");
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
		render("Transactions/detail.html", transaction, mode, udfs);

	}

	public static void cancel(String taskId) {
		log.debug("cancel. taskId: " + taskId);
	}

	@Check("transaction.manualJournal")
	public static void edit(Long id, String taskId) {
		log.debug("edit. id: " + id + " taskId: " + taskId);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaTransaction faTransaction = fundService.getFaTransaction(id);
		List<FaTransactionDetail> faTransactionDetails = fundService.listFaTransactionDetail(id);
		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(faTransactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
		render("ManualJournals/entry.html", faTransaction, transactionDetails, taskId, mode);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			JsonHelper jsonFaTransactionDetail = new JsonHelper().getFaTransactionDetailSerializer();
			FaTransaction faTransaction = fundService.getFaTransaction(keyId);

			if ((faTransaction.getFaTransactionDetails() != null) && (faTransaction.getFaTransactionDetails().size() > 0)) {
				List<FaTransactionDetail> lstFaTransactionDetails = new ArrayList<FaTransactionDetail>(faTransaction.getFaTransactionDetails());
				faTransaction.getFaTransactionDetails().clear();

				for (FaTransactionDetail faTransactionDetail : lstFaTransactionDetails) {
					faTransactionDetail.setFaTransaction(faTransaction);
				}

				faTransaction.getFaTransactionDetails().addAll(lstFaTransactionDetails);
			}

			String transactionDetails = null;
			try {
				transactionDetails = jsonFaTransactionDetail.serialize(faTransaction.getFaTransactionDetails());
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
			render("ManualJournals/approval.html", faTransaction, taskId, mode, transactionDetails, from, operation, maintenanceLogKey);
		} catch (Exception e) {
			log.error("Fa Transaction Approval", e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation, String correction) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction);

		try {
			FaTransaction faTransaction = fundService.approveManualJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, null);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.approved", faTransaction.getTransactionKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String correction, Long fakey) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " correction: " + correction + " fakey: " + fakey);

		try {
			FaTransaction faTransaction = new FaTransaction();
			if (Helper.isNullOrEmpty(correction)) {
				faTransaction = fundService.approveManualJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, correction);
			} else {
				faTransaction = fundService.approveManualJournal(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, "", correction);
			}

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.rejected", faTransaction == null ? fakey : faTransaction.getTransactionKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.manualJournal")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaTransaction faTransaction = fundService.getFaTransaction(id);
		List<FaTransactionDetail> faTransactionDetails = fundService.listFaTransactionDetail(id);
		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(faTransactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_MANUAL_JOURNAL));
		render("ManualJournals/detail.html", faTransaction, transactionDetails, mode);
	}

}
