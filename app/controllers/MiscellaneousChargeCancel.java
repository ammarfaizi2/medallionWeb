package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.CsTransactionCharge;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.i18n.Messages;
import play.mvc.Before;
import vo.MiscellaneousChargeSearchParameter;

public class MiscellaneousChargeCancel extends MedallionController {
	private static Logger log = Logger.getLogger(MiscellaneousChargeCancel.class);

	@Before
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> transOperators = UIHelper.stringOperators();
		renderArgs.put("transOperators", transOperators);

		/*
		 * String menuCancel = MenuConstants.CS_MISCELLANEOUS_CHARGE;
		 * renderArgs.put("menuCancel", menuCancel);
		 * 
		 * String menuEntry = MenuConstants.CS_MISCELLANEOUS_CANCEL;
		 * renderArgs.put("menuEntry", menuEntry);
		 */
	}

	@Check("transaction.miscellaneousCancel")
	public static void list(MiscellaneousChargeSearchParameter param) {
		log.debug("list. param: " + param);

		String menuCancel = MenuConstants.CS_MISCELLANEOUS_CANCEL;
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CANCEL));
		MiscellaneousChargeSearchParameter params = new MiscellaneousChargeSearchParameter();
		render("MiscellaneousChargeEntries/list.html", param, menuCancel, params);
	}

	@Check("transaction.miscellaneousCancel")
	public static void paging(Paging page, MiscellaneousChargeSearchParameter param) {
		log.debug("paging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			log.debug("Masuk paging cancel");
			// List<CsTransaction> csTransactions =
			// transactionService.listCancelTransaction();
			// logger.debug("Size csTransaction "+csTransactions.size());
			String transactionKeys = "true";

			/*
			 * for (CsTransaction csTransaction : csTransactions) { if
			 * (transactionKeys ==""){ transactionKeys =
			 * ""+csTransaction.getCancelTransaction().getTransactionKey();
			 * }else{ transactionKeys =
			 * transactionKeys+" , "+csTransaction.getCancelTransaction
			 * ().getTransactionKey(); } }
			 */

			log.debug("transaction key " + transactionKeys);

			page.addParams("ct.TRANSACTION_DATE", Paging.GREATEQUAL, param.miscellaneousFrom);
			page.addParams("ct.TRANSACTION_DATE", Paging.LESSEQUAL, param.miscellaneousTo);
			page.addParams("cm.CUSTOMER_KEY", Paging.EQUAL, param.customerCodeId);
			page.addParams("cc.CHARGE_KEY", Paging.EQUAL, param.chargeKey);
			page.addParams("ct.TRANSACTION_NUMBER", param.transactionNoOperator, UIHelper.withOperator(param.transactionId, param.transactionNoOperator));
			page.addParams("ct.TRANSACTION_STATUS", Paging.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
			page.addParams("ct.RECORD_STATUS", Paging.EQUAL, LookupConstants.__RECORD_STATUS_APPROVED);
			page.addParams("ctt.USED_BY", Paging.EQUAL, LookupConstants.USED_BY_MISC_CHARGES);
			page.addParams("ct.SOURCE", Paging.EQUAL, MenuConstants.CS_MISCELLANEOUS_CHARGE);
			page.addParams(Helper.searchAll("(cc.CHARGE_VALUE||to_char(ct.SETTLEMENT_DATE,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "ct.TRANSACTION_NUMBER||" + "ca.ACCOUNT_NO||ca.NAME||gc.DESCRIPTION)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));
			page = transactionService.searchCsTransactionPaging(page, transactionKeys);
		}
		renderJSON(page);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}

	@Check("transaction.miscellaneousCancel")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsTransaction transaction = transactionService.getTransaction(id);
		CsTransactionCharge charge = new CsTransactionCharge();
		if (!transaction.getTransactionCharges().isEmpty()) {
			Set<CsTransactionCharge> transChanges = transaction.getTransactionCharges();
			charge = transChanges.iterator().next();
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CANCEL));
		render("MiscellaneousChargeCancel/entry.html", transaction, charge, mode);
	}

	@Check("transaction.miscellaneousCancel")
	public static void save(CsTransaction transaction, CsTransactionCharge charge, String mode) {
		log.debug("save. transaction: " + transaction + " charge: " + charge + " mode: " + mode);

		//Date applicationDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);

		if (transaction != null) {
			validation.required("Cancel Date is", transaction.getCancelledDate());

			/*
			 * if (transaction.getCancelledDate() != null) { //
			 * logger.debug("[SAVE] what value = "
			 * +(transaction.getCancelledDate().getTime() <
			 * transaction.getTransactionDate().getTime())); if
			 * (transaction.getCancelledDate().getTime() <
			 * transaction.getTransactionDate().getTime()) {
			 * validation.addError("",
			 * "Cancel Date is less than Transaction Date"); }
			 * 
			 * if (transaction.getCancelledDate().getTime() >
			 * applicationDate.getTime()) { validation.addError("",
			 * "Cancel Date is greater than Application Date"); } }
			 */
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CANCEL));
				render("MiscellaneousChargeCancel/entry.html", transaction, charge, mode);
			} else {
				Set<CsTransactionCharge> transCharges = new HashSet<CsTransactionCharge>();
				if (charge != null) {
					transCharges.add(charge);
				}
				transaction.setTransactionCharges(transCharges);
				Long id = transaction.getTransactionKey();
				serializerService.serialize(session.getId(), id, transaction);
				confirming(transaction.getTransactionKey(), mode);
			}
		} else {
			flash.error("argument.null", transaction);
		}
	}

	@Check("transaction.miscellaneousCancel")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		try {
			renderArgs.put("confirming", true);
			CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
			Set<CsTransactionCharge> charges = transaction.getTransactionCharges();
			CsTransactionCharge charge = charges.iterator().next();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CANCEL));
			render("MiscellaneousChargeCancel/entry.html", transaction, charge, mode);

		} catch (Exception e) {
			log.debug("Masuk confirm error cancel");
			log.debug(e.getMessage(), e);
		}
	}

	public static void entry() {
		log.debug("entry. ");
	}

	@Check("transaction.miscellaneousCancel")
	public static void confirm(CsTransaction transaction, CsTransactionCharge charge, String mode) {
		log.debug("confirm. transaction: " + transaction + " charge: " + charge + " mode: " + mode);

		try {
			charge.setChargeCapitalized(true);
			Set<CsTransactionCharge> transCharges = new HashSet<CsTransactionCharge>();
			if (charge != null) {
				transCharges.add(charge);
			}
			transaction.setTransactionCharges(transCharges);
			log.debug("Charge value " + charge.getChargeValue());
			transaction.setChargeValueMisc(charge.getChargeValue());
			log.debug("Desc charge master " + charge.getChargeMaster().getDescription());
			transaction.setChargeNameMisc(charge.getChargeMaster().getDescription());
			// Date currentBusinessDate =
			// generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
			// transaction.setTransactionDate(currentBusinessDate);
			boolean confirming = true;
			// CsTransaction trx =
			// transactionService.createTransactionWithSerialized(menuCode,
			// transaction, session.get(UIConstants.SESSION_USERNAME), "",
			// session.get(UIConstants.SESSION_USER_KEY));

			CsTransaction trx = transactionService.createCancelMiscellaneousCharge(MenuConstants.CS_MISCELLANEOUS_CANCEL, transaction, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));

			// CsTransaction trx =
			// transactionService.createTransactionWithSerialized(MenuConstants.CS_MISCELLANEOUS_CHARGE,
			// transaction, session.get(UIConstants.SESSION_USERNAME),"",
			// session.get(UIConstants.SESSION_USER_KEY));

			if (trx.getTransactionKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("transaction.confirmed.successful", trx.getTransactionNumber()));
				renderJSON(result);
				// renderText (trx.getTransactionNumber());
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
				render("MiscellaneousChargeEntries/detail.html", trx, mode, confirming);
			}
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if(ex.getParams()!= null){
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			}else{
				result.put("error", Messages.get("error." + ex.getErrorCode()));
			}
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
			renderJSON(result);
		}catch (Exception e) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error",e.getMessage());
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
			renderJSON(result);
		}
	}

	@Check("transaction.miscellaneousCancel")
	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", false);
		CsTransaction transaction = serializerService.deserialize(session.getId(), id, CsTransaction.class);
		Set<CsTransactionCharge> charges = transaction.getTransactionCharges();
		CsTransactionCharge charge = charges.iterator().next();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_MISCELLANEOUS_CHARGE));
		renderTemplate("MiscellaneousChargeCancel/entry.html", transaction, charge, mode);

	}

	public static void clear() {
		log.debug("clear. ");
	}

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " processDefinition: " + processDefinition);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		boolean approval = true;
		CsTransaction transaction = accountService.getTransaction(keyId);
		transaction.setRemaksCancel(transaction.getRemaks());
		CsTransactionCharge charge = new CsTransactionCharge();
		Set<CsTransactionCharge> charges = transaction.getTransactionCharges();
		charge = charges.iterator().next();

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			render("MiscellaneousChargeCancel/approval.html", transaction, charge, taskId, mode, from, operation, approval);
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			render("MiscellaneousChargeCancel/approval.html", transaction, charge, taskId, mode, from, operation, approval);
		}
	}

	public static void approve(Long keyId, String taskId, String operation, String correction) {
		log.debug("approve. keyId: " + keyId + " taskId: " + taskId + " operation: " + operation + " correction: " + correction);

		try {
			CsTransaction transaction = transactionService.approveCancelMiscellaneousCharge(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.approved", transaction.getTransactionNumber())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(Long keyId, String taskId, String operation, String correction) {
		log.debug("reject. keyId: " + keyId + " taskId: " + taskId + " operation: " + operation + " correction: " + correction);

		try {
			CsTransaction transaction = transactionService.rejectMiscellaneousCharge(keyId, session.get(UIConstants.SESSION_USERNAME), taskId);

			renderJSON(Formatter.resultSuccess(Messages.get("transaction.rejected", transaction.getTransactionNumber())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}