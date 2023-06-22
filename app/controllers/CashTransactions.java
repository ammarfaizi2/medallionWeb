package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
// @Check("transaction.cashTransaction")
public class CashTransactions extends MedallionController {
	private static Logger log = Logger.getLogger(CashTransactions.class);

	@Before(only = { "entry", "save", "confirming", "confirm", "back", "clear", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> paymentType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PAYMENT_TYPE);
		renderArgs.put("paymentType", paymentType);
	}

	public static void list() {
		log.debug("list. ");
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	@Check("transaction.cashTransaction")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsTransaction transaction = new CsTransaction();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_TRANSACTION));
		render("CashTransactions/entry.html", transaction, mode);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	@Check("transaction.cashTransaction")
	public static void save(CsTransaction transaction, String mode) {
		log.debug("save. transaction: " + transaction + " mode: " + mode);

		if (transaction != null) {
			validation.required("transaction.settlementDate", transaction.getSettlementDate());
			validation.required("transaction.account.accountNo", transaction.getAccount().getAccountNo());
			validation.required("transaction.settlementAccount.accountNo", transaction.getSettlementAccount().getAccountNo());
			validation.required("transaction.transactionTemplate.transactionCode", transaction.getTransactionTemplate().getTransactionCode());
			// validation.required("transaction.paymentType.lookupId",
			// transaction.getPaymentType().getLookupId());
			validation.required("transaction.amount", transaction.getAmount());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_TRANSACTION));
				render("CashTransactions/entry.html", transaction, mode);
			} else {
				Long id = transaction.getTransactionKey();
				serializerService.serialize(session.getId(), id, transaction);
				confirming(transaction.getTransactionKey(), mode);
			}
		} else {
			// flash.error("argument.null", transaction);
			entry();
		}
	}

	@Check("transaction.cashTransaction")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		try {
			renderArgs.put("confirming", true);
			CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_TRANSACTION));
			render("CashTransactions/entry.html", transaction, mode);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}

	}

	@Check("transaction.cashTransaction")
	public static void confirm(CsTransaction transaction, String id, String mode) {
		log.debug("confirm. transaction: " + transaction + " id: " + id + " mode: " + mode);

		try {
			boolean confirming = true;
			CsTransaction trx = transactionService.createCashTransaction(MenuConstants.BN_CASH_TRANSACTION, transaction, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			if (trx.getTransactionKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("transaction.confirmed.successful", trx.getTransactionNumber()));
				renderJSON(result);
				// renderText (trx.getTransactionNumber());
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_TRANSACTION));
				render("CashTransactions/detail.html", trx, mode, confirming);
			}
		} catch (MedallionException ex) {
			log.debug(">>>>ERROR <<<");
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex != null && ex.getParams() != null) {
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
			}
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_TRANSACTION));
			result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			renderJSON(result);
		} catch (Exception e) {
			log.debug(">>>>ERROR <<<");
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_TRANSACTION));
			result.put("error", Messages.get("error." + e.getMessage(), params.toArray()));
			renderJSON(result);
		}
	}

	@Check("transaction.cashTransaction")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", false);
		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.BN_CASH_TRANSACTION));
		render("CashTransactions/entry.html", transaction, mode);
	}

	@Check("transaction.cashTransaction")
	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		CsTransaction transaction = new CsTransaction();
		render("CashTransactions/detail.html", transaction, mode);
	}

	public static void approval(String taskId, Long keyId, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsTransaction transaction = accountService.getTransaction(keyId);

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			render("CashTransactions/approval.html", transaction, taskId, mode);
		} else {

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			render("CashTransactions/approval.html", transaction, taskId, mode);
		}
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			CsTransaction transaction = transactionService.approveTransaction(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.approved", transaction.getTransactionNumber())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			CsTransaction transaction = transactionService.rejectTransaction(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.rejected", transaction.getTransactionNumber())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
