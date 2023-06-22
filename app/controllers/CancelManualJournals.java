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
import play.mvc.With;
import vo.FaTransactionSearchParametrs;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.FaTransaction;
import com.simian.medallion.model.FaTransactionDetail;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CancelManualJournals extends MedallionController {
	private static Logger log = Logger.getLogger(CancelManualJournals.class);

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);

		List<SelectItem> journalTypeOperators = UIHelper.journalTypeOperators();
		renderArgs.put("journalTypeOperators", journalTypeOperators);
	}

	@Before(only = { "approval", "view" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);
		renderArgs.put("classifications", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION));
		renderArgs.put("depositoryCodes", generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITORY_CODE));
	}

	@Check("transaction.cancelManualJournal")
	public static void cancel(Long key) {
		log.debug("cancel. key: " + key);

		FaTransaction trx = fundService.createCancelManualJournal(MenuConstants.FA_CANCEL_MANUAL_JOURNAL, key, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
		Map<String, Object> result = new HashMap<String, Object>();
		SimpleDateFormat dateFormat = new SimpleDateFormat(UIConstants.DATE_FORMAT);

		result.put("status", "success");
		result.put("transactionNo", trx.getTransactionKey());
		result.put("transactionStatus", trx.getTransactionStatus().trim());
		result.put("transactionDate", dateFormat.format(trx.getTransactionDate()));
		renderJSON(result);
	}

	public static void approval(String taskId, Long keyId, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaTransaction faTransaction = fundService.getFaTransaction(keyId);
		if (faTransaction.getFaPostingRule() != null) {
			faTransaction.setName(faTransaction.getFaPostingRule().getDescription());
		}
		List<FaTransactionDetail> faTransactionDetails = fundService.listFaTransactionDetail(keyId);
		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(faTransactionDetails);
			log.debug(">>> transactionDetails=" + transactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		render("CancelManualJournals/approval.html", faTransaction, taskId, mode, transactionDetails, from);

	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			FaTransaction faTransaction = fundService.approveCancelManualJournal(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("Cancel Transaction No #" + faTransaction.getTransactionKey() + " is Approved")));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			FaTransaction faTransaction = fundService.rejectCancelManualJournal(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("Cancel Transaction No #" + faTransaction.getTransactionKey() + " is Rejected")));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.cancelManualJournal")
	public static void view(Long key) {
		log.debug("view. key: " + key);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaTransaction faTransaction = fundService.getFaTransaction(key);
		// journal type = system.
		if (faTransaction.getFaPostingRule() != null) {
			faTransaction.setName(faTransaction.getFaPostingRule().getDescription());
		}
		String transactionDetails = null;
		try {
			List<FaTransactionDetail> faTransactionDetails = fundService.listFaTransactionDetail(key);
			transactionDetails = json.writeValueAsString(faTransactionDetails);
			log.debug(">>> transactionDetails=" + transactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CANCEL_MANUAL_JOURNAL));
		render("CancelManualJournals/cancel.html", faTransaction, mode, transactionDetails);
	}

	@Check("transaction.cancelManualJournal")
	public static void list() {
		log.debug("list. ");

		List<FaTransaction> faTransactions = new ArrayList<FaTransaction>();
		FaTransaction id = new FaTransaction();
		FaTransactionSearchParametrs params = new FaTransactionSearchParametrs();
		id.setTransactionDate(generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_CANCEL_MANUAL_JOURNAL));
		render("CancelManualJournals/list.html", faTransactions, id, params);
	}

	@Check("transaction.cancelManualJournal")
	public static void pagingCancelFaTransaction(Paging page, FaTransactionSearchParametrs param) {
		log.debug("pagingCancelFaTransaction. page: " + page + " param: " + param);

		if (param.isQuery()) {
			page.addParams("trunc(ft.transactionDate)", page.GREATEQUAL, param.faTransactionSearchTransactionDateFrom);
			page.addParams("trunc(ft.transactionDate)", page.LESSEQUAL, param.faTransactionSearchTransactionDateTo);
			page.addParams("ft.faMaster.fundKey", page.EQUAL, Long.parseLong(param.faTransactionSearchFundKey));
			page.addParams("ft.transactionKey", param.TransactionNoOperator, UIHelper.withOperator(param.transactionSearchNoOperator, param.TransactionNoOperator));
			page.addParams("1", page.EQUAL, 1);

			if (LookupConstants.JOURNAL_SYSTEM.equals(param.journalTypeOperator)) {
				page.addParams(Helper.searchAll("(ft.transactionKey||ft.name)||(select sum(ftd.amount) from FaTransactionDetail ftd " + "where ftd.faTransaction.transactionKey = ft.transactionKey " + "and ftd.dorc = 'D') "), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

				page = fundService.pagingCancelFaTransactionSystem(page);

			} else {
				page.addParams(Helper.searchAll("(ft.transactionKey||ft.name)||(select sum(ftd.amount) from FaTransactionDetail ftd " + "where ftd.faTransaction.transactionKey = ft.transactionKey " + "and ftd.dorc = 'D') "), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

				page = fundService.pagingCancelFaTransaction(page);
			}

		}
		renderJSON(page);
	}

	@Check("transaction.cancelManualJournal")
	public static void search(FaTransactionSearchParametrs params) {
		log.debug("search. params: " + params);

		List<FaTransaction> faTransactions = fundService.searchCancelFaTransaction(params.faTransactionSearchTransactionDateFrom, params.faTransactionSearchTransactionDateTo, params.faTransactionSearchFundKey, UIHelper.withOperator(params.transactionSearchNoOperator, params.TransactionNoOperator));
		for (FaTransaction faTransaction : faTransactions) {
			BigDecimal totalAmount = fundService.totalAmountFaTransaction(faTransaction.getTransactionKey());
			faTransaction.setTotalAmount(totalAmount);
		}
		render("CancelManualJournals/grid.html", faTransactions);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit() {
		log.debug("edit. ");
	}
}
