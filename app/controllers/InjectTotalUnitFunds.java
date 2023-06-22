package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import vo.FaUnitTransactionSearchParameters;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.FaUnitTransaction;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

public class InjectTotalUnitFunds extends Registry {
	private static Logger log = Logger.getLogger(InjectTotalUnitFunds.class);

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("transaction.injectTotalUnitFund")
	public static void list() {
		log.debug("list. ");

		List<FaUnitTransaction> faUnitTransactions = fundService.listFaUnitTransactions();
		// FaUnitTransaction id = new FaDailyNav();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
		render("InjectTotalUnitFunds/list.html", faUnitTransactions);
	}

	@Check("transaction.injectTotalUnitFund")
	public static void search(FaUnitTransactionSearchParameters params) {
		log.debug("search. params: " + params);

		if (!("").equals(params.faUnitTransactionSearchFundCode)) {
			List<FaUnitTransaction> faUnitTransactions = fundService.searchFaUnitTransaction(params.faUnitTransactionSearchTransactionDate, params.faUnitTransactionSearchFundCode);
			render("InjectTotalUnitFunds/grid.html", faUnitTransactions);
		}
		if (("").equals(params.faUnitTransactionSearchFundCode)) {
			log.debug("masuk sini dengan transactionDate = " + params.faUnitTransactionSearchTransactionDate);
			List<FaUnitTransaction> faUnitTransactions = fundService.searchFaUnitTransaction(params.faUnitTransactionSearchTransactionDate, null);
			log.debug("size faUnitTransactions = " + faUnitTransactions.size());
			render("InjectTotalUnitFunds/grid.html", faUnitTransactions);
		}
	}

	@Check("transaction.injectTotalUnitFund")
	public static void edit(Long id, String transactionDate, boolean isNewRec) {
		log.debug("edit. id: " + id + " transactionDate: " + transactionDate + " isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		// FaDailyNav faDailyNav = new FaDailyNav();
		isNewRec = false;
		// try {
		// FaDailyNavId id = new FaDailyNavId(fundKey,
		// parseDate(transactionDate, "MM/dd/yyyy"));
		// faDailyNav = fundService.getFaDailyNav(id);
		// } catch(Exception e) {
		// logger.error(e.getMessage(), e);
		// }
		log.debug("ID >>>> " + id);
		FaUnitTransaction faUnitTransaction = fundService.getFaUnitTransaction(id);
		String status = faUnitTransaction.getTransactionStatus() + " ";
		log.debug("from edit status = --" + status + "--");
		log.debug("FROM EDIT isNewRec = " + isNewRec);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
		render("InjectTotalUnitFunds/detail.html", faUnitTransaction, mode, isNewRec, status);
	}

	@Check("transaction.injectTotalUnitFund")
	public static void view(Long id, String transactionDate) {
		log.debug("view. id: " + id + " transactionDate: " + transactionDate);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		// FaDailyNav faDailyNav = new FaDailyNav();
		//
		// try {
		// FaDailyNavId id = new FaDailyNavId(fundKey,
		// parseDate(transactionDate, "MM/dd/yyyy"));
		// faDailyNav = fundService.getFaDailyNav(id);
		// logger.debug("before >>> faDailyNav.setPrevTotalUnit = " +
		// faDailyNav.getPrevTotalUnit() + " transactionDate = " +
		// transactionDate);
		// faDailyNav.setPrevTotalUnit(fundService.getFaDailyNavTotalUnit(faDailyNav.getId().getFundKey(),
		// transactionDate));
		// logger.debug("after >>> faDailyNav.setPrevTotalUnit = " +
		// faDailyNav.getPrevTotalUnit());
		// } catch(Exception e) {
		// logger.error(e.getMessage(), e);
		// }
		FaUnitTransaction faUnitTransaction = fundService.getFaUnitTransaction(id);
		faUnitTransaction.setPrevTotalUnit(fundService.getFaUnitTransactionTotalUnit(faUnitTransaction.getFaMaster().getFundKey(), transactionDate));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
		render("InjectTotalUnitFunds/detail.html", faUnitTransaction, mode);
	}

	@Check("transaction.injectTotalUnitFund")
	public static void entry(boolean isNewRec) {
		log.debug("entry. isNewRec: " + isNewRec);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaUnitTransaction faUnitTransaction = new FaUnitTransaction();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date applicationDate = generalService.getCurrentBusinessDate(UIConstants.SIMIAN_BANK_ID);
		String transactionDate = dateFormat.format(applicationDate);
		isNewRec = true;
		log.debug("FROM ENTRY isNewRec = " + isNewRec);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
		render("InjectTotalUnitFunds/detail.html", faUnitTransaction, mode, isNewRec, transactionDate);
	}

	@Check("transaction.injectTotalUnitFund")
	public static void save(FaUnitTransaction faUnitTransaction, String mode, boolean isNewRec, String status) {
		log.debug("save. faUnitTransaction: " + faUnitTransaction + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (faUnitTransaction != null) {
			validation.required("Fund Code is", faUnitTransaction.getFaMaster().getFundKey());
			validation.required("Date is", faUnitTransaction.getTransactionDate());
			validation.required("Total Unit is", faUnitTransaction.getTotalUnit());
			// logger.debug("[save] isNewRec >>> " + isNewRec);
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
				render("InjectTotalUnitFunds/detail.html", faUnitTransaction, mode, isNewRec, status);
			} else {
				// FaDailyNavId id = faDailyNav.getId();
				Long id = faUnitTransaction.getTransactionKey();
				serializerService.serialize(session.getId(), id, faUnitTransaction);
				log.debug("FROM SAVE isNewRec = " + isNewRec);
				confirming(id, mode, isNewRec, status);
			}
		} else {
			entry(isNewRec);
		}

	}

	@Check("transaction.injectTotalUnitFund")
	public static void confirming(Long id, String mode, boolean isNewRec, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		renderArgs.put("confirming", true);
		FaUnitTransaction faUnitTransaction = serializerService.deserialize(session.getId(), id, FaUnitTransaction.class);
		log.debug("FROM CONFIRMING isNewRec = " + isNewRec);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
		render("InjectTotalUnitFunds/detail.html", faUnitTransaction, mode, isNewRec, status);
	}

	@Check("transaction.injectTotalUnitFund")
	public static void confirm(FaUnitTransaction faUnitTransaction, String mode, boolean isNewRec, String status) {
		log.debug("confirm. faUnitTransaction: " + faUnitTransaction + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (faUnitTransaction == null)
			back(null, mode, isNewRec, status);

		try {
			Helper.showProperties(faUnitTransaction);
			log.debug("FROM CONFIRM isNewRec = " + isNewRec);
			// if (faDailyNav.getPrevTotalUnit().equals(BigDecimal.ZERO)){
			// faDailyNav.setPrevTotalUnit(null);
			// logger.debug("after prevTotalUnit = " +
			// faDailyNav.getPrevTotalUnit());
			// }
			if (faUnitTransaction.getUnitMovement().equals(BigDecimal.ZERO)) {
				faUnitTransaction.setUnitMovement(null);
				log.debug("after unitMovement = " + faUnitTransaction.getUnitMovement());
			}

			// DUPLICATE FUND CODE & DATE
			// =======================================================================================
			List<FaUnitTransaction> faUnitTransactions = fundService.listFaUnitTransactions();
			for (FaUnitTransaction faUnitTransactionInTable : faUnitTransactions) {
				if (UIConstants.DISPLAY_MODE_ENTRY.equals(mode)) {
					Long fundKeyTable = faUnitTransactionInTable.getFaMaster().getFundKey();
					Long fundKey = faUnitTransaction.getFaMaster().getFundKey();
					Date transactionDateTable = faUnitTransactionInTable.getTransactionDate();
					Date transactionDate = faUnitTransaction.getTransactionDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

					if ((fundKeyTable.equals(fundKey)) && ((dateFormat.format(transactionDateTable)).equals(dateFormat.format(transactionDate)))) {
						flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
						flash.error("Fund Code : ' " + faUnitTransaction.getFaMaster().getFundCode() + " ' " + ExceptionConstants.DATA_DUPLICATE);
						boolean confirming = true;
						render("InjectTotalUnitFunds/detail.html", faUnitTransaction, mode, confirming, isNewRec, status);
					}
				}
			}
			// ==================================================================================================================
			fundService.saveFaUnitTransaction(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND, faUnitTransaction, session.get(UIConstants.SESSION_USERNAME), "", isNewRec, session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			// flash.error(ex.getErrorCode(), ex.getParams());
			flash.error("Fund Code : ' " + faUnitTransaction.getFaMaster().getFundCode() + " ' " + Messages.get(e.getMessage()));
			renderArgs.put("confirming", true);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
			render("InjectTotalUnitFunds/detail.html", faUnitTransaction, mode, isNewRec, status);
		}
	}

	@Check("transaction.injectTotalUnitFund")
	public static void back(Long id, String mode, boolean isNewRec, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " isNewRec: " + isNewRec + " status: " + status);

		if (isNewRec == true) {
			mode = UIConstants.DISPLAY_MODE_ENTRY;
		} else {
			mode = UIConstants.DISPLAY_MODE_EDIT;
		}
		FaUnitTransaction faUnitTransaction = serializerService.deserialize(session.getId(), id, FaUnitTransaction.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_INJECT_TOTAL_UNIT_FUND));
		render("InjectTotalUnitFunds/detail.html", faUnitTransaction, mode, isNewRec, status);
	}

	public static void approval(String taskId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			FaUnitTransaction faUnitTransaction = json.readValue(maintenanceLog.getNewData(), FaUnitTransaction.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("InjectTotalUnitFunds/approval.html", faUnitTransaction, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaUnitTransaction(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.injectTotalUnitFund")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaUnitTransaction(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
