package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;

import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.vo.SelectItem;

public class TimeDepositBreaks extends MedallionController {
	private static Logger log = Logger.getLogger(TimeDepositBreaks.class);

	public static void list() {
		log.debug("list. ");
	}

	@Before(unless = { "entry", "edit", "save", "back" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.periodOfInterestOperators();
		renderArgs.put("operators", operators);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsTransaction transaction = new CsTransaction();
		render("TimeDepositBreaks/timeDepositBreak.html", transaction, mode);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	public static void save(CsTransaction transaction, String mode) {
		log.debug("save. transaction: " + transaction + " mode: " + mode);

		if (transaction != null) {
			validation.required("transaction.settlementDate", transaction.getSettlementDate());
			validation.required("transaction.account.accountNo", transaction.getAccount().getAccountNo());

			if (validation.hasErrors()) {
				render("TimeDepositBreaks/timeDepositBreak.html", transaction, mode);
			} else {
				Long id = transaction.getTransactionKey();
				serializerService.serialize(session.getId(), id, transaction);
				confirming(transaction.getTransactionKey(), mode);
			}
		} else {
			flash.error("argument.null", transaction);
		}
	}

	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);

		render("TimeDepositBreaks/timeDepositBreak.html", transaction, mode);
	}

	public static void confirm(CsTransaction transaction, String mode) {
		log.debug("confirm. transaction: " + transaction + " mode: " + mode);

		boolean confirming = true;
		CsTransaction trx = accountService.saveTransaction(transaction);
		if (trx.getTransactionKey() != null) {
			renderText(trx.getTransactionNumber());
		} else {
			render("TimeDepositBreaks/detail.html", trx, mode, confirming);
		}
	}

	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", false);
		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
		renderTemplate("TimeDepositBreaks/timeDepositBreak.html", transaction, mode);
	}

	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		CsTransaction transaction = new CsTransaction();
		render("TimeDepositBreaks/detail.html", transaction, mode);
	}
}