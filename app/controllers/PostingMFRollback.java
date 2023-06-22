package controllers;

import helpers.Formatter;
import helpers.UIConstants;

import java.util.Date;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnPostingMfRollback;
import com.simian.medallion.model.helper.Helper;

@With(Secure.class)
public class PostingMFRollback extends MedallionController {
	public static Logger log = Logger.getLogger(PostingMFRollback.class);

	public static final String DISPATCH_ENTRY = "ENTRY";
	public static final String DISPATCH_CONFIRM = "CONFIRM";
	public static final String DISPATCH_APPROVAL = "APPROVAL";

	@Check("transaction.postingMFRollback")
	public static void list(GnPostingMfRollback param) {
		log.debug("list. param: " + param);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		param = new GnPostingMfRollback();
		param.setDispatch(DISPATCH_ENTRY);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_MF_ROLLBACK));
		render("PostingMFRollback/list.html", param, mode);
	}

	@Check("transaction.postingMFRollback")
	public static void fetch(GnPostingMfRollback param, String mode) {
		log.debug("fetch. param: " + param + " mode: " + mode);

		param = taService.initPostingMFRollback(param);
		param.setDispatch(DISPATCH_ENTRY);

		log.debug("mode " + mode);
		log.debug("ValidDate " + param.getCurrent().getValidDate());
		log.debug("Status " + param.getCurrent().getStatus());
		log.debug("NavPerUnit " + param.getCurrent().getNavPerUnit());
		log.debug("NetAsset " + param.getCurrent().getNetAssetValue());
		log.debug("TotalUnit " + param.getCurrent().getTotalUnit());

		log.debug("Substibce " + param.getSubscription());
		log.debug("Redemtion " + param.getRedemtion());
		log.debug("Switching " + param.getSwitching());
		log.debug("Dividend " + param.getDividend());
		log.debug("Payment " + param.getPayment());

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_MF_ROLLBACK));
		render("PostingMFRollback/list.html", param, mode);
	}

	@Check("transaction.postingMFRollback")
	public static void next(GnPostingMfRollback param, String mode) {
		log.debug("next. param: " + param + " mode: " + mode);

		if (param != null) {
			validation.required("Fund Code", param.getFund().getFundCode());
			validation.required("Rollback Date", param.getRollback().getValidDate());

			Boolean checkFundCodeFilled = ((param.getFund() != null) && (param.getFund().getFundKey() != null) && (!Helper.isNullOrEmpty(param.getFund().getFundCode())));

			if (checkFundCodeFilled) {
				param = taService.initPostingMFRollback(param);
				param.setDispatch(DISPATCH_CONFIRM);

				Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
				Date backedDate = generalService.minusWorkingDate(appDate, param.getFund().getBackDatedAllowed());

				log.debug("ApplicationDate " + Helper.formatDMY(appDate));
				log.debug("BackDate " + Helper.formatDMY(backedDate));

				Boolean checkCurrentStatus = ((param.getCurrent().getValidDate() != null) && (param.getCurrent().getStatus() == null || "".equals(param.getCurrent().getStatus().trim())));

				Boolean checkRollbackStatus = ((param.getRollback().getValidDate() != null) && (param.getRollback().getStatus() == null || "".equals(param.getRollback().getStatus().trim())));

				Boolean checkRollbackdateNotGreaterOrEqualPostingDate = ((param.getCurrent() != null && param.getRollback() != null && param.getRollback().getValidDate() != null && param.getCurrent().getValidDate() != null) && (Helper.isGreaterYMD(param.getRollback().getValidDate(), param.getCurrent().getValidDate())));

				Boolean isRollbackDateLessThanBackDate = (param.getRollback() != null && param.getRollback().getValidDate() != null && Helper.isLessYMD(param.getRollback().getValidDate(), backedDate));

				/*
				 * if (param.getCurrent() == null ||
				 * param.getCurrent().getStatus() == null ||
				 * "".equals(param.getCurrent().getStatus().trim())) {
				 * //validation.addError("GlobalError",
				 * "Current Position Data is not exist");
				 * validation.addError("",
				 * "Current Position Data is not exist"); }
				 * 
				 * if (param.getRollback() == null ||
				 * param.getRollback().getStatus() == null ||
				 * "".equals(param.getRollback().getStatus().trim())) {
				 * //validation.addError("GlobalError",
				 * "Rollback Position Data is not exist");
				 * validation.addError("",
				 * "Rollback Position Data is not exist"); }
				 * 
				 * if (param.getCurrent() != null && param.getRollback() != null
				 * && param.getRollback().getValidDate() != null &&
				 * param.getCurrent().getValidDate() != null) { if
				 * (Helper.isGreaterEqualYMD(param.getRollback().getValidDate(),
				 * param.getCurrent().getValidDate())) {
				 * //validation.addError("GlobalError",
				 * "Rollback To Date can not be greater or equal then current posting date"
				 * ); validation.addError("",
				 * "Rollback To Date can not be greater or equal then current posting date"
				 * ); } }
				 * 
				 * if (param.getFund() != null &&
				 * !"".equals(param.getFund().getFundCode()) &&
				 * param.getRollback().getValidDate() != null) {
				 * GnPostingMfRollback rollback =
				 * taService.getPostingMFRollback(param.getFund().getFundKey(),
				 * param.getRollback().getValidDate()); if (rollback != null) {
				 * //validation.addError("GlobalError",
				 * "Fund Code : "+param.getFund
				 * ().getFundCode()+" with RollbackDate : "
				 * +Helper.format(param.getRollback().getValidDate(),
				 * appProp.getDateFormat())+", is in rollback status");
				 * validation.addError("",
				 * "Fund Code : "+param.getFund().getFundCode
				 * ()+" with RollbackDate : "
				 * +Helper.format(param.getRollback().getValidDate(),
				 * appProp.getDateFormat())+", is in rollback status"); } }
				 * 
				 * if (param.getRollback().getValidDate() != null &&
				 * Helper.isLessYMD(param.getRollback().getValidDate(),
				 * backedDate)) { //validation.addError("GlobalError",
				 * "Rollback To Date can not be less then Allowed back dated ["
				 * +Helper.format(backedDate, appProp.getDateFormat())+"]");
				 * validation.addError("",
				 * "Rollback To Date can not be less then Allowed back dated ["
				 * +Helper.format(backedDate, appProp.getDateFormat())+"]"); }
				 */

				if ((checkCurrentStatus) || (checkRollbackStatus)) {
					if (checkCurrentStatus) {
						// validation.addError("GlobalError",
						// "Current Position Data is not exist");
						validation.addError("", "Current Position Data is not exist");
					}

					if (checkRollbackStatus) {
						// validation.addError("GlobalError",
						// "Rollback Position Data is not exist");
						validation.addError("", "Rollback Position Data is not exist");
					}
				} else {
					if ((checkRollbackdateNotGreaterOrEqualPostingDate) || (isRollbackDateLessThanBackDate)) {
						if (checkRollbackdateNotGreaterOrEqualPostingDate) {
							// validation.addError("GlobalError",
							// "Rollback To Date can not be greater or equal then current posting date");
							validation.addError("", "Rollback To Date can not be greater then current posting date");
						}

						if (isRollbackDateLessThanBackDate) {
							// validation.addError("GlobalError",
							// "Rollback To Date can not be less then Allowed back dated ["+Helper.format(backedDate,
							// appProp.getDateFormat())+"]");
							validation.addError("", "Rollback To Date can not be less then Allowed back dated [" + Helper.format(backedDate, appProp.getDateFormat()) + "]");
						}
					} else {
						if ((param.getFund() != null) && (!"".equals(param.getFund().getFundCode())) && (param.getRollback().getValidDate() != null)) {
							GnPostingMfRollback rollback = taService.getPostingMFRollback(param.getFund().getFundKey(), param.getRollback().getValidDate());
							if (rollback != null) {
								// validation.addError("GlobalError",
								// "Fund Code : "+param.getFund().getFundCode()+" with RollbackDate : "+Helper.format(param.getRollback().getValidDate(),
								// appProp.getDateFormat())+", is in rollback status");
								validation.addError("", "Fund Code : " + param.getFund().getFundCode() + " is in rollback processing");
							}
						}
					}
				}
			}

			if (validation.hasErrors()) {
				param.setDispatch(DISPATCH_ENTRY);
			}

			log.debug("ValidDate " + param.getCurrent().getValidDate());
			log.debug("Status " + param.getCurrent().getStatus());
			log.debug("NavPerUnit " + param.getCurrent().getNavPerUnit());
			log.debug("NetAsset " + param.getCurrent().getNetAssetValue());
			log.debug("TotalUnit " + param.getCurrent().getTotalUnit());

			log.debug("Substibce " + param.getSubscription());
			log.debug("Redemtion " + param.getRedemtion());
			log.debug("Switching " + param.getSwitching());
			log.debug("Dividend " + param.getDividend());
			log.debug("Payment " + param.getPayment());

			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_MF_ROLLBACK));
			render("PostingMFRollback/list.html", param, mode);
		} else {
			flash.error("argument.null", param);
		}
	}

	@Check("transaction.postingMFRollback")
	public static void confirm(GnPostingMfRollback param, String mode) {
		log.debug("confirm. param: " + param + " mode: " + mode);

		param = taService.initPostingMFRollback(param);
		param.setDispatch(DISPATCH_CONFIRM);

		try {
			String userName = session.get(UIConstants.SESSION_USERNAME);
			String userKey = session.get(UIConstants.SESSION_USER_KEY);
			String menu = MenuConstants.FA_POSTING_MF_ROLLBACK;

			param = taService.savePostingMFRollback(param, userName, userKey, menu);
		} catch (Exception e) {
			flash.error("Fail to save Posting MF Rollback for " + param.getFund().getFundCode() + ", " + e.getMessage());
			log.error(e.getMessage(), e);
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_MF_ROLLBACK));
		render("PostingMFRollback/list.html", param);
	}

	// @Check("transaction.postingMFRollback")
	public static void approval(String taskId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;

			GnMaintenanceLog gnMaintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);

			GnPostingMfRollback param = json.readValue(gnMaintenanceLog.getNewData(), GnPostingMfRollback.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}

			param.setDispatch(DISPATCH_APPROVAL);
			param = taService.initPostingMFRollback(param);

			render("PostingMFRollback/approval.html", param, taskId, maintenanceLogKey, mode, operation, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// @Check("transaction.postingMFRollback")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			String userName = session.get(UIConstants.SESSION_USERNAME);
			taService.approvePostingMFRollback(userName, taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("transaction.postingMFRollback")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			String userName = session.get(UIConstants.SESSION_USERNAME);
			taService.approvePostingMFRollback(userName, taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.postingMFRollback")
	public static void entry(String mode) {
		log.debug("entry. mode: " + mode);
	}

	@Check("transaction.postingMFRollback")
	public static void edit(String mode) {
		log.debug("edit. mode: " + mode);
	}

	@Check("transaction.postingMFRollback")
	public static void view(String mode) {
		log.debug("view. mode: " + mode);
	}
}