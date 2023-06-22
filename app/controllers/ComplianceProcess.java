package controllers;

import helpers.UIConstants;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.ComplianceProcessItem;

@With(Secure.class)
public class ComplianceProcess extends MedallionController {
	private static Logger log = Logger.getLogger(ComplianceProcess.class);

	@Check("transaction.complianceProcess")
	public static void list(ComplianceProcessItem cpItem) {
		log.debug("list. cpItem: " + cpItem);

		if (cpItem == null)
			cpItem = new ComplianceProcessItem();

		cpItem.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.CP_PROCESS + cpItem.getSessionTag());
		cpItem.setProcessMark(params.get(PROCESSMARK));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PROCESS));
		render("ComplianceProcess/list.html", cpItem);
	}

	@Check("transaction.complianceProcess")
	public static void process(ComplianceProcessItem cpItem) {
		log.debug("process. cpItem: " + cpItem);

		String sessionUuidX = session.get(PROCESSMARK + MenuConstants.CP_PROCESS + cpItem.getSessionTag());
		if (sessionUuidX == null) {
			session.put(PROCESSMARK + MenuConstants.CP_PROCESS + cpItem.getSessionTag(), cpItem.getProcessMark());
			sessionUuidX = session.get(PROCESSMARK + MenuConstants.CP_PROCESS + cpItem.getSessionTag());
		}
		if (isDoubleSubmission(MenuConstants.CP_PROCESS + cpItem.getSessionTag())) {
			list(null);
		}

		validation.required("Fund Code is", cpItem.getFund().getFundCode());
		validation.required("Date is", cpItem.getDate());

		List<String> logs = new ArrayList<String>();
		if (!validation.hasErrors()) {
			logs = generalService.processCompliance(cpItem.getFund().getFundKey(), cpItem.getDate());
		}
		cpItem.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.CP_PROCESS + cpItem.getSessionTag());
		cpItem.setProcessMark(params.get(PROCESSMARK));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PROCESS));
		render("ComplianceProcess/list.html", cpItem, logs);
	}

	@Check("transaction.complianceProcess")
	public static void check(Long fundKey, String yyyyMMdd) throws Exception {
		log.debug("check. fundKey: " + fundKey + " yyyyMMdd: " + yyyyMMdd);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(yyyyMMdd);
		BigDecimal val = generalService.checkProcessCompliance(fundKey, date);
		renderJSON(val);
	}

	@Check("transaction.complianceProcess")
	public static void entry() {
		log.debug("entry. ");
	}

	@Check("transaction.complianceProcess")
	public static void edit() {
		log.debug("edit. ");
	}

	@Check("transaction.complianceProcess")
	public static void view(Long id) {
		log.debug("view. id: " + id);
	}
}