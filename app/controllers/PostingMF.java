package controllers;

import helpers.UIConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.With;
import vo.PostingMFParam;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.FaMaster;
import com.simian.medallion.model.GnPostingMfLog;
import com.simian.medallion.model.helper.Helper;

@With(Secure.class)
public class PostingMF extends MedallionController {
	public static Logger log = Logger.getLogger(PostingMF.class);

	public static Map<String, List<String>> postingLogMap = new HashMap<String, List<String>>();

	@Check("transaction.postingMF")
	public static void list(PostingMFParam param) {
		log.debug("list. param: " + param);

		if (param == null)
			param = new PostingMFParam();

		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		param.setFromDate(appDate);
		param.setToDate(appDate);
		param.setAppDate(Helper.format(appDate, appProp.getDateFormat()));
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.FA_POSTING_MF + param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));

		//String sessionUuidX = (String) session.get(PROCESSMARK + MenuConstants.FA_POSTING_MF + param.getSessionTag());

		render("PostingMF/list.html", param);
	}

	@Check("transaction.postingMF")
	public static void process(PostingMFParam param) {
		log.debug("process. param: " + param);

		String sessionUuidX = session.get(PROCESSMARK + MenuConstants.FA_POSTING_MF + param.getSessionTag());
		if (sessionUuidX == null) {
			session.put(PROCESSMARK + MenuConstants.FA_POSTING_MF + param.getSessionTag(), param.getProcessMark());
			sessionUuidX = session.get(PROCESSMARK + MenuConstants.FA_POSTING_MF + param.getSessionTag());
		}
		if (isDoubleSubmission(MenuConstants.FA_POSTING_MF + param.getSessionTag())) {
			list(param);
		}

		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		// FaMaster faMaster =
		// fundService.getFaMaster(Long.valueOf(param.getFundCodeKey()));
		FaMaster faMaster = fundService.getFaFundSetupForPick(param.getFundCodeKey());
		Date backedDate = generalService.minusWorkingDate(appDate, faMaster.getBackDatedAllowed());

		log.info("FundCode=" + param.getFundCode());
		log.info("FromDate=" + Helper.formatDMY(param.getFromDate()));
		log.info("ToDate=" + Helper.formatDMY(param.getToDate()));
		log.info("ApplicationDate=" + Helper.formatDMY(appDate));
		log.info("AllowedBackdated=" + faMaster.getBackDatedAllowed());
		log.info("Backdated=" + Helper.formatDMY(backedDate));

		validation.required("FundCode", param.getFundCode());
		validation.required("PeriodeFrom", param.getFromDate());
		validation.required("PeriodeTo", param.getToDate());

		if (param.getFromDate() != null && param.getToDate() != null) {
			if (Helper.isLessYMD(param.getToDate(), param.getFromDate())) {
				validation.addError("PeriodeTo", "fa.posting.tolessequalfrom");
			}

			if (Helper.isLessY(param.getFromDate(), Helper.toDate(faMaster.getFinancialYear())) || Helper.isLessY(param.getToDate(), Helper.toDate(faMaster.getFinancialYear()))) {
				validation.addError("PeriodeTo", Messages.get("fa.posting.financialyearlessthenperiode", faMaster.getFinancialYear()));
			}
		}

		if (param.getFromDate() != null) {
			if (Helper.isGreaterYMD(backedDate, param.getFromDate())) {
				String formated = Helper.formatDMY(backedDate);
				log.info("formated=" + formated);
				validation.addError("PeriodeTo", Messages.get("fa.posting.backdatedgreaterthenfromdate", formated));
			}
		}

		if (param.getToDate() != null) {
			if (Helper.isGreaterYMD(param.getToDate(), appDate)) {
				String formated = Helper.formatDMY(appDate);
				log.info("formated=" + formated);
				validation.addError("PeriodeTo", Messages.get("fa.posting.todategreaterthenappdate", formated));
			}
		}

		List<String> logs = new ArrayList<String>();
		log.debug("validation.hasErrors() " + validation.hasErrors());
		log.error("start process");
		if (!validation.hasErrors()) {
			try {
				String username = session.get(UIConstants.SESSION_USERNAME);
				log.error("start postingMF " + username + ", " + param.getFundCodeKey() + ", " + Helper.formatDMY(param.getFromDate()) + ", " + Helper.formatDMY(param.getToDate()));
				logs = fundService.postingMF(username, param.getFundCodeKey(), param.getFromDate(), param.getToDate());
				log.error("finish postingMF " + username + ", " + param.getFundCodeKey() + ", " + Helper.formatDMY(param.getFromDate()) + ", " + Helper.formatDMY(param.getToDate()));

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				logs.add("Fail execute Posting MF " + e.getMessage());
			}
		}
		log.error("end process");
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_POSTING_MF));
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.FA_POSTING_MF + param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));
		render("PostingMF/list.html", param, logs);
	}

	@Check("transaction.postingMF")
	public static void processAjax(PostingMFParam param) {
		log.debug("processAjax. param: " + param);

		String postingLogKey = Helper.formatYMD(new Date()) + "_" + param.getFundCode() + "_" + param.getSessionTag();

		List<String> postingLogs = new ArrayList<String>();
		postingLogMap.put(postingLogKey, postingLogs);

		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		FaMaster faMaster = fundService.getFaFundSetupForPick(param.getFundCodeKey());
		Date backedDate = generalService.minusWorkingDate(appDate, faMaster.getBackDatedAllowed());

		try {
			param.setFromDate(Helper.parse(param.getFromDateStr(), "dd/MM/yyyy"));
			param.setToDate(Helper.parse(param.getToDateStr(), "dd/MM/yyyy"));
		} catch (Exception e) {
		}

		log.info("FundCode=" + param.getFundCode());
		log.info("FundCodeKey=" + param.getFundCodeKey());
		log.info("FundCodeDesc=" + param.getFundCodeDesc());
		log.info("FromDate=" + Helper.formatDMY(param.getFromDate()));
		log.info("ToDate=" + Helper.formatDMY(param.getToDate()));
		log.info("FromDateStr=" + param.getFromDateStr());
		log.info("ToDateStr=" + param.getToDateStr());
		log.info("ApplicationDate=" + Helper.formatDMY(appDate));
		log.info("Param ApplicationDate=" + param.getAppDate());
		log.info("SessionTag=" + param.getSessionTag());
		log.info("ProcessMark=" + param.getProcessMark());
		log.info("AllowedBackdated=" + faMaster.getBackDatedAllowed());
		log.info("Backdated=" + Helper.formatDMY(backedDate));

		Map<String, String> validations = new HashMap<String, String>();
		if (Helper.isEmpty(param.getFundCode()))
			validations.put("fundCodeErr", "Required");
		if (Helper.isNull(param.getFromDate()))
			validations.put("fromDateErr", "Required");
		if (Helper.isNull(param.getToDate()))
			validations.put("toDateErr", "Required");

		if (param.getFromDate() != null && param.getToDate() != null) {
			if (Helper.isLessYMD(param.getToDate(), param.getFromDate())) {
				validations.put("toDateErr", Messages.get("fa.posting.tolessequalfrom"));
			}

			if (Helper.isLessY(param.getFromDate(), Helper.toDate(faMaster.getFinancialYear())) || Helper.isLessY(param.getToDate(), Helper.toDate(faMaster.getFinancialYear()))) {
				validations.put("toDateErr", Messages.get("fa.posting.financialyearlessthenperiode", faMaster.getFinancialYear()));
			}
		}

		if (param.getFromDate() != null) {
			if (Helper.isGreaterYMD(backedDate, param.getFromDate())) {
				String formated = Helper.formatDMY(backedDate);
				log.info("formated=" + formated);
				validations.put("toDateErr", Messages.get("fa.posting.backdatedgreaterthenfromdate", formated));
			}
		}

		if (param.getToDate() != null) {
			if (Helper.isGreaterYMD(param.getToDate(), appDate)) {
				String formated = Helper.formatDMY(appDate);
				log.info("formated=" + formated);
				validations.put("toDateErr", Messages.get("fa.posting.todategreaterthenappdate", formated));
			}
		}

		if (validations.isEmpty()) {
			try {
				String username = session.get(UIConstants.SESSION_USERNAME);
				log.error("start postingMF " + username + ", " + param.getFundCodeKey() + ", " + Helper.formatDMY(param.getFromDate()) + ", " + Helper.formatDMY(param.getToDate()));

				postingLogs = fundService.postingMF(username, param.getFundCodeKey(), param.getFromDate(), param.getToDate());
				postingLogMap.put(postingLogKey, postingLogs);

				log.error("finish postingMF " + username + ", " + param.getFundCodeKey() + ", " + Helper.formatDMY(param.getFromDate()) + ", " + Helper.formatDMY(param.getToDate()));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				if (e.getMessage().contains("org.hibernate"))
					postingLogs.add("Fail execute Posting MF, Someone using this Posting");
				else
					postingLogs.add("Fail execute Posting MF " + e.getMessage());
			}
		} else {
			postingLogMap.remove(postingLogKey);
		}
		log.error("end process");

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("errorSize", validations.size());
		result.put("validations", validations);
		renderJSON(result);
	}

	@Check("transaction.postingMF")
	public static void processAjaxLog(PostingMFParam param) {
		log.debug("processAjaxLog. param: " + param);

		// Status
		// W : Waiting process to finish
		// F : Process Finish
		// G : Gone stop process

		String strCurrentDate = Helper.formatYMD(new Date());
		List<String> postingLogExpiredKeys = new ArrayList<String>();
		// todo hapus semua yang bukan hari ini
		for (String expiredKey : postingLogMap.keySet()) {
			if (!expiredKey.startsWith(strCurrentDate)) {
				postingLogExpiredKeys.add(expiredKey);
			}
		}

		for (String expiredKey : postingLogExpiredKeys) {
			postingLogMap.remove(expiredKey);
		}

		String postingLogKey = strCurrentDate + "_" + param.getFundCode() + "_" + param.getSessionTag();
		List<String> postingLogs = postingLogMap.get(postingLogKey);

		String status = (postingLogs == null) ? "G" : (postingLogs.isEmpty()) ? "W" : "F";
		if ("F".equals(status))
			postingLogMap.remove(postingLogKey);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("logs", postingLogs);
		result.put("status", status);
		renderJSON(result);
	}

	@Check("transaction.postingMF")
	public static void entry(String mode) {
		log.debug("entry. mode: " + mode);

	}

	@Check("transaction.postingMF")
	public static void edit(String mode) {
		log.debug("edit. mode: " + mode);

	}

	@Check("transaction.postingMF")
	public static void view(String mode) {
		log.debug("view. mode: " + mode);

	}

	public static void getPostingMFLog(String fundKey) {
		log.debug("getPostingMFLog. fundKey: " + fundKey);

		String posting = "";
		try {
			Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
			GnPostingMfLog gnPosting = generalService.getGnPostingMFLog(Long.parseLong(fundKey), new Date());
			if (gnPosting != null) {
				if (gnPosting.getLastPosted() != null) {
					gnPosting.setLastPosted(Helper.add(gnPosting.getLastPosted(), 1));
					if (gnPosting.getLastPosted().compareTo(appDate) < 0) {
						posting = json.writeValueAsString(gnPosting);
					}
				}
			}
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		renderJSON(posting);
	}

}
