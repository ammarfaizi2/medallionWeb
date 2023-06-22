package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.vo.SelectItem;

public class InHouseTransfers extends MedallionController {
	private static Logger log = Logger.getLogger(InHouseTransfers.class);

	@Before(only = { "entry", "save", "confirming", "back", "clear" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> depositoryCode = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITORY_CODE);
		renderArgs.put("depositoryCode", depositoryCode);

		List<SelectItem> classifications = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CLASSIFICATION);
		renderArgs.put("classifications", classifications);
	}

	public static void list() {
		log.debug("list. ");
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		// Transaction transaction = new Transaction();
		render("InHouseTransfers/InHouseTransfer.html", mode);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	public static void save(CsTransaction transaction, String mode) {
		log.debug("save. transaction: " + transaction + " mode: " + mode);

		if (transaction != null) {
			validation.required("transaction.transactionDate", transaction.getTransactionDate());
			validation.required("transaction.account.accountNo", transaction.getAccount().getAccountNo());
			validation.required("transaction.settlementAccount.accountNo", transaction.getSettlementAccount().getAccountNo());
			validation.required("transaction.transactionTemplate.transactionCode", transaction.getTransactionTemplate().getTransactionCode());
			validation.required("transaction.paymentType.lookupId", transaction.getPaymentType().getLookupCode());
			validation.required("transaction.amount", transaction.getAmount());
			if (validation.hasErrors()) {
				render("CashTransactions/cashTransaction.html", transaction, mode);
			} else {
				Long id = transaction.getTransactionKey();
				serializerService.serialize(session.getId(), id, transaction);
				confirming(id, mode);
			}
		} else {
			flash.error("argument.null", transaction);
		}
	}

	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
		render("CashTransactions/cashTransaction.html", transaction, mode);
	}

	public static void confirm(CsTransaction transaction, String id, String mode) {
		log.debug("confirm. transaction: " + transaction + " id: " + id + " mode: " + mode);
	}

	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", false);
		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
		render("CashTransactions/cashTransaction.html", transaction, mode);
	}

	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		CsTransaction transaction = new CsTransaction();
		render("InHouseTransfers/detail.html", transaction, mode);
	}
}
