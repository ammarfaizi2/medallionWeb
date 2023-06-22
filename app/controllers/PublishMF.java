package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.Before;
import play.mvc.With;
import vo.PublishMFParam;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;

@With(Secure.class)
public class PublishMF extends MedallionController {
	public static Logger log = Logger.getLogger(PublishMF.class);

	public static Map<String, List<String>> postingLogMap = new HashMap<String, List<String>>();

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		renderArgs.put("operators", UIHelper.stringOperators());
	}

	@Check("transaction.publishMF")
	public static void list(PublishMFParam param) {
		log.debug("list. param: " + param);

		if (param == null)
			param = new PublishMFParam();

		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		param.setAppDate(appDate);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_PUBLISH_MF));
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.FA_PUBLISH_MF + param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));
		render("PublishMF/list.html", param);
	}

	@Check("transaction.publishMF")
	public static void table(Paging page, PublishMFParam param) {
		log.debug("table. page: " + page + " param: " + param);

		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		page.setDateFormat(appProp.getDateFormat());
		// page.addParams("gat.loginDate", page.EQUAL, param.getAppDate());

		page.addParams("a.IS_ACTIVE", page.EQUAL, "1");

		/*
		 * page.addParams(Helper.searchAll(
		 * "(a.fund_key||a.fund_code||a.fund_description||" +
		 * "to_char(c.nav_date,'"
		 * +Helper.dateOracle(appProp.getDateFormat())+"'))"), Paging.LIKE,
		 * UIHelper.withOperator(page.getsSearch(), 1));
		 */

		page.addParams(Helper.searchAll("(a.fund_key||a.fund_code||a.fund_description||b.posted_status||c.net_asset_value||c.total_unit||c.nav_per_unit||" + "to_char(b.last_posted,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(b.last_published,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(b.process_date,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(c.nav_date,'" + Helper.dateOracle(appProp.getDateFormat()) + "'))"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = fundService.listPublishMF(page, appDate);
		renderJSON(page);
	}

	@Check("transaction.publishMF")
	public static void process(PublishMFParam param) {
		log.debug("process. param: " + param);

		printParameter(param, true);
		String sessionUuidX = session.get(PROCESSMARK + MenuConstants.FA_PUBLISH_MF + param.getSessionTag());
		if (sessionUuidX == null) {
			session.put(PROCESSMARK + MenuConstants.FA_PUBLISH_MF + param.getSessionTag(), param.getProcessMark());
			sessionUuidX = session.get(PROCESSMARK + MenuConstants.FA_PUBLISH_MF + param.getSessionTag());
		}
		if (isDoubleSubmission(MenuConstants.FA_PUBLISH_MF + param.getSessionTag())) {
			list(param);
		}
		log.error("start publish " + session.get(UIConstants.SESSION_USER_KEY));
		param.populate();
		log.debug("Param AppDate " + param.getAppDate());
		log.debug("FundKeys size " + param.getFundKeys());
		for (int i = 0; i < param.getFundKeys().size(); i++) {
			log.error("Fund Key " + param.getFundKeys().get(i));
			log.error("PublishDate " + param.getPublishDates().get(i));
		}

		List<String> logs = new ArrayList<String>();
		try {
			String userId = session.get(UIConstants.SESSION_USER_KEY);
			if (param.getFundKeys().size() != param.getPublishDates().size()) {
				throw new Exception("Invalid Parameter size");
			}
			logs = fundService.publishMF(param.getFundKeys(), param.getPublishDates(), userId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			logs.add("Fail to publish MF " + e.getMessage());
		}
		log.error("end publish " + session.get(UIConstants.SESSION_USER_KEY));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_PUBLISH_MF));
		param.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.FA_PUBLISH_MF + param.getSessionTag());
		param.setProcessMark(params.get(PROCESSMARK));
		render("PublishMF/list.html", param, logs);
	}

	@Check("transaction.publishMF")
	public static void processAjax(PublishMFParam param) {
		log.debug("processAjax. param: " + param);

		printParameter(param, false);
		param.populate();

		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();
		param.setAppDate(appDate);

		String postingLogKey = Helper.formatYMD(new Date()) + "_" + param.getSessionTag();

		List<String> postingLogs = new ArrayList<String>();
		postingLogMap.put(postingLogKey, postingLogs);

		try {
			String userId = session.get(UIConstants.SESSION_USER_KEY);
			if (param.getFundKeys().size() != param.getPublishDates().size()) {
				throw new Exception("Invalid Parameter size");
			}
			postingLogs = fundService.publishMF(param.getFundKeys(), param.getPublishDates(), userId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			postingLogs.add("Fail to publish MF " + e.getMessage());
		}

		postingLogMap.put(postingLogKey, postingLogs);
	}

	@Check("transaction.publishMF")
	public static void processAjaxLog(PublishMFParam param) {
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

		String postingLogKey = strCurrentDate + "_" + param.getSessionTag();
		List<String> postingLogs = postingLogMap.get(postingLogKey);

		String status = (postingLogs == null) ? "G" : (postingLogs.isEmpty()) ? "W" : "F";
		if ("F".equals(status))
			postingLogMap.remove(postingLogKey);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("logs", postingLogs);
		result.put("status", status);
		renderJSON(result);
	}

	private static void printParameter(PublishMFParam param, boolean raiseError) {
		log.debug("printParameter. param: " + param + " raiseError: " + raiseError);

		log.error("=======================================================");

		if (param.getParameter() != null)
			for (String parameter : param.getParameter()) {
				log.error("Parameter : " + parameter);
			}

		if (param.getFundKeys() != null)
			for (Long fundKey : param.getFundKeys()) {
				log.error("FundKey : " + fundKey);
			}

		if (param.getPublishDates() != null)
			for (Date publishDate : param.getPublishDates()) {
				log.error("publishDate : " + publishDate);
			}

		log.error("AppDate : " + param.getAppDate());
		log.error("SessionTag : " + param.getSessionTag());
		log.error("ProcessMark : " + param.getProcessMark());
		log.error("=======================================================");
		if (raiseError)
			throw new NullPointerException("Sengaja Gue errorin");
	}

	@Check("transaction.publishMF")
	public static void entry(String mode) {
		log.debug("entry. mode: " + mode);

	}

	@Check("transaction.publishMF")
	public static void edit(String mode) {
		log.debug("edit. mode: " + mode);
	}

	@Check("transaction.publishMF")
	public static void view(String mode) {
		log.debug("view. mode: " + mode);
	}

}
