package controllers;

import helpers.UIConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.FaDailyNav;
import com.simian.medallion.model.FaMaster;
import com.simian.medallion.vo.FaEndOfDayItem;

public class FaEndOfDay extends Registry {
	protected static Logger log = Logger.getLogger(FaEndOfDay.class);

	@Check("transaction.faEndOfday")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaEndOfDayItem eod = new FaEndOfDayItem();
		eod.setConfirm("no");
		// eod.setFromDate(taService.getCurrentBusinessDate());
		// eod.setToDate(eod.getFromDate());

		render("FaEndOfDay/list.html", eod, mode);
	}

	@Check("transaction.faEndOfday")
	public static String getFaDailyNav(Long fundKey, String fromDateYYYYMMDD, String toDateYYYYMMDD) throws Exception {
		log.debug("getFaDailyNav. fundKey: " + fundKey + " fromDateYYYYMMDD: " + fromDateYYYYMMDD + " toDateYYYYMMDD: " + toDateYYYYMMDD);

		Date fromDate = parseYYYYMMDD(fromDateYYYYMMDD);
		Date toDate = parseYYYYMMDD(toDateYYYYMMDD);
		log.debug("afromDate " + fromDate);
		log.debug("toDate " + toDate);

		String message = null;
		List<FaDailyNav> faDailyNavs = taService.getFaDailyNav(fromDate, toDate, fundKey);
		log.debug("elvcino " + faDailyNavs.size());
		if (faDailyNavs != null && !faDailyNavs.isEmpty()) {
			message = "Date [";
			for (FaDailyNav nav : faDailyNavs) {
				String postedDate = fmtMMDDYYYY(nav.getId().getNavDate());
				message += postedDate + ", ";
			}
			message = message.substring(0, message.length() - 1);
			message += "] has already posted, do you wanto to re-post ?";
		}
		return message;
	}

	// table FA_TRANSACTION (selain P) < from
	@Check("transaction.faEndOfday")
	public static String getPendingTrans(Long fundKey, String fromDateYYYYMMDD) throws Exception {
		log.debug("getPendingTrans. fundKey: " + fundKey + " fromDateYYYYMMDD: " + fromDateYYYYMMDD);

		Date fromDate = parseYYYYMMDD(fromDateYYYYMMDD);
		Long sum = taService.getPendingTransaction(fundKey, fromDate);
		String message = null;
		if (sum > 0) {
			message = "There is pending transaction before " + fmtMMDDYYYY(fromDate) + ", please check FA Pending transacton report. Do you want to continue ?";
		}
		return message;
	}

	@Check("transaction.faEndOfday")
	public static void getMaxNavDate(Long fundKey) {
		log.debug("getMaxNavDate. fundKey: " + fundKey);

		Date maxNavDate = taService.getFaProcessDate(fundKey);
		if (maxNavDate == null) {
			maxNavDate = taService.getCurrentBusinessDate();
		} else {
			maxNavDate = addDate(maxNavDate, 1);
		}
		renderJSON(fmtYYYYMMDD(maxNavDate));
	}

	@Check("transaction.faEndOfday")
	public static void process(FaEndOfDayItem eod) throws Exception {
		log.debug("process. eod: " + eod);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;

		Long fundKey = eod.getFaMaster().getFundKey();
		Date fromDate = eod.getFromDate();
		Date toDate = eod.getToDate();

		Date businessDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		FaMaster faMaster = fundService.getFaFundSetupForPick(eod.getFaMaster().getFundCode());
		Date maxNavDate = taService.getFaProcessDate(fundKey);

		String strFromDate = fmtYYYYMMDD(fromDate);
		log.debug("fromDate " + strFromDate);
		String strToDate = fmtYYYYMMDD(toDate);
		log.debug("toDate " + strToDate);
		String strBusinessDate = fmtYYYYMMDD(businessDate);
		log.debug("applicationDate " + strBusinessDate);
		String strFromYear = fmtYYYY(fromDate);
		log.debug("fromYear  " + strFromYear);
		String strToYear = fmtYYYY(toDate);
		log.debug("toYear  " + strToYear);
		String strFinancialYear = faMaster.getFinancialYear();
		log.debug("financialYear  " + strFinancialYear);
		String strBackDated = fmtYYYYMMDD(addDate(businessDate, -faMaster.getBackDatedAllowed()));
		log.debug("backDated " + strBackDated);
		log.debug("allow day backdated " + faMaster.getBackDatedAllowed());

		int fromDateDOY = fmtDOY(fromDate);
		int maxNavDOY = fmtDOY(maxNavDate);

		if (strFromDate.compareTo(strBusinessDate) > 0) {
			validation.addError("global", "fa.eod.fromgreterthenappdate");
		} else {
			if (strFromYear.compareTo(strFinancialYear) < 0) {
				validation.addError("global", "fa.eod.fromyeargreterthenappdate");
			} else if (strToDate.compareTo(strBusinessDate) > 0) {
				validation.addError("global", "fa.eod.togreterthenappdate");
			} else {
				if (strToYear.compareTo(strFinancialYear) < 0) {
					validation.addError("global", "fa.eod.toyeargreterthenappdate");
				} else if (strFromDate.compareTo(strToDate) > 0) {
					validation.addError("global", "fa.eod.fromgreaterthento");
				} else {
					if (strFromDate.compareTo(strBackDated) < 0) {
						validation.addError("global", "fa.eod.frombackdatedallow");
					} else if (maxNavDate != null && (fromDateDOY - maxNavDOY) > 1) {
						validation.addError("global", Messages.get("fa.eod.frompostingdate", fmtMMDDYYYY(addDate(maxNavDate, 1))));
					}
				}
			}
		}

		// logger.debug("strBusinessDate "+strBusinessDate);
		//
		// logger.debug("validation 1 "+strFromDate+"  "+strBusinessDate);
		// //harus lebih kecil
		// if ((strFromDate.compareTo(strBusinessDate) > 0) ||
		// (strToDate.compareTo(strBusinessDate) > 0)) {
		// validation.addError("global", "fa.eod.fromgreterthenappdate");
		// }
		//
		// if (maxNavDate != null && (fromDateDOY - maxNavDOY) > 1) {
		// validation.addError("global", Messages.get("fa.eod.frompostingdate",
		// fmtMMDDYYYY(addDate(maxNavDate, 1))));
		// }
		//
		// logger.debug("validation 2 "+strFromYear+"  "+strFinancialYear);
		// //financial yaer > dari posting date gax boleh
		// if (strFromYear.compareTo(strFinancialYear) < 0) {
		// validation.addError("global", "fa.eod.fromyeargreterthenappdate");
		// }
		//
		// logger.debug("validation 3 "+strFromDate+"  "+strToDate+" "+strBackDated);
		// if (strFromDate.compareTo(strBackDated) <= 0 ||
		// strToDate.compareTo(strBackDated) <= 0) {
		// validation.addError("global", "fa.eod.frombackdatedallow");
		// }
		//
		// logger.debug("Confirm "+eod.getConfirm());
		//
		List<String> logs = new ArrayList<String>();
		if (!validation.hasErrors()) {
			if ("yes".equals(eod.getConfirm())) {
				logs = taService.eodBodFa(fromDate, toDate, fundKey, session.get(UIConstants.SESSION_USERNAME));
				eod.setConfirm("no");
				eod.setConfirmFaDailyNav(null);
				eod.setConfirmPendingTrans(null);
			} else {
				eod.setConfirmFaDailyNav(getFaDailyNav(fundKey, strFromDate, strToDate));
				eod.setConfirmPendingTrans(getPendingTrans(fundKey, strFromDate));

				if (eod.getConfirmFaDailyNav() == null && eod.getConfirmPendingTrans() == null) {
					logs = taService.eodBodFa(fromDate, toDate, fundKey, session.get(UIConstants.SESSION_USERNAME));
					eod.setConfirm("no");
					eod.setConfirmFaDailyNav(null);
					eod.setConfirmPendingTrans(null);
				} else {
					eod.setConfirm("ask");
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_END_OF_DAY));
		render("FaEndOfDay/list.html", eod, logs, mode);
	}

	// public static void process(FaEndOfDayItem eod) throws Exception{
	// String mode = UIConstants.DISPLAY_MODE_ENTRY;
	//
	// Long fundKey = eod.getFaMaster().getFundKey();
	// Date fromDate = eod.getFromDate();
	// Date toDate = eod.getToDate();
	//
	// Date businessDate =
	// generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
	// FaMaster faMaster =
	// fundService.getFaFundSetupForPick(eod.getFaMaster().getFundCode());
	// Date maxNavDate = taService.getFaProcessDate(fundKey);
	//
	// String strFromDate = fmtYYYYMMDD(fromDate);
	// String strToDate = fmtYYYYMMDD(toDate);
	// String strBusinessDate = fmtYYYYMMDD(businessDate);
	// String strFromYear = fmtYYYY(fromDate);
	// String strFinancialYear = faMaster.getFinancialYear();
	// String strBackDated = fmtYYYYMMDD(addDate(businessDate,
	// -(faMaster.getBackDatedAllowed()+1)));
	//
	// int fromDateDOY = fmtDOY(fromDate);
	// int maxNavDOY = fmtDOY(maxNavDate);
	//
	// logger.debug("strBusinessDate "+strBusinessDate);
	//
	// logger.debug("validation 1 "+strFromDate+"  "+strBusinessDate); //harus
	// lebih kecil
	// if ((strFromDate.compareTo(strBusinessDate) > 0) ||
	// (strToDate.compareTo(strBusinessDate) > 0)) {
	// validation.addError("global", "fa.eod.fromgreterthenappdate");
	// }
	//
	// if (maxNavDate != null && (fromDateDOY - maxNavDOY) > 1) {
	// validation.addError("global", Messages.get("fa.eod.frompostingdate",
	// fmtMMDDYYYY(addDate(maxNavDate, 1))));
	// }
	//
	// logger.debug("validation 2 "+strFromYear+"  "+strFinancialYear);
	// //financial yaer > dari posting date gax boleh
	// if (strFromYear.compareTo(strFinancialYear) < 0) {
	// validation.addError("global", "fa.eod.fromyeargreterthenappdate");
	// }
	//
	// logger.debug("validation 3 "+strFromDate+"  "+strToDate+" "+strBackDated);
	// if (strFromDate.compareTo(strBackDated) <= 0 ||
	// strToDate.compareTo(strBackDated) <= 0) {
	// validation.addError("global", "fa.eod.frombackdatedallow");
	// }
	//
	// logger.debug("Confirm "+eod.getConfirm());
	//
	// List<String> logs = new ArrayList<String>();
	// if (!validation.hasErrors()) {
	// if ("yes".equals(eod.getConfirm())) {
	// logs = taService.eodBodFa(fromDate, toDate, fundKey,
	// session.get(UIConstants.SESSION_USERNAME));
	// eod.setConfirm("no");
	// eod.setConfirmFaDailyNav(null);
	// eod.setConfirmPendingTrans(null);
	// }else{
	// eod.setConfirmFaDailyNav(getFaDailyNav(fundKey, strFromDate, strToDate));
	// eod.setConfirmPendingTrans(getPendingTrans(fundKey, strFromDate));
	//
	// if (eod.getConfirmFaDailyNav() == null && eod.getConfirmPendingTrans() ==
	// null) {
	// logs = taService.eodBodFa(fromDate, toDate, fundKey,
	// session.get(UIConstants.SESSION_USERNAME));
	// }else{
	// eod.setConfirm("ask");
	// }
	// }
	// }
	//
	// render("FaEndOfDay/list.html", eod, logs, mode);
	// }

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit() {
		log.debug("edit. ");
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);
	}
}
