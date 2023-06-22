package controllers;

import helpers.UIConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.ComplianceProcessItem;

@With(Secure.class)
public class ComplianceProcessRg extends MedallionController {
	private static Logger log = Logger.getLogger(ComplianceProcessRg.class);

	@Check("transaction.complianceProcessrg")
	public static void list() {
		log.debug("list. ");

		Date date = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();

		ComplianceProcessItem item = new ComplianceProcessItem();
		item.setProcessDate(date);
		item.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.CP_PROCESS_RG + item.getSessionTag());
		item.setProcessMark(params.get(PROCESSMARK));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PROCESS_RG));
		render("ComplianceProcessProduct/list.html", item);
	}

	@Check("transaction.complianceProcessrg")
	public static void process(ComplianceProcessItem item) {
		log.debug("process. item: " + item);

		String sessionUuidX = session.get(PROCESSMARK + MenuConstants.CP_PROCESS_RG + item.getSessionTag());
		if (sessionUuidX == null) {
			session.put(PROCESSMARK + MenuConstants.CP_PROCESS_RG + item.getSessionTag(), item.getProcessMark());
			sessionUuidX = session.get(PROCESSMARK + MenuConstants.CP_PROCESS_RG + item.getSessionTag());
		}
		if (isDoubleSubmission(MenuConstants.CP_PROCESS_RG + item.getSessionTag())) {
			list();
		}
		validation.required("Fund Code is", item.getProduct().getProductCode());
		validation.required("Date is", item.getProcessDate());

		List<String> logs = new ArrayList<String>();
		if (!validation.hasErrors()) {
			logs = taService.processComplianceRg(item.getProduct().getProductCode(), item.getProcessDate());
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_PROCESS_RG));
		item.setSessionTag(Helper.getRandomText(10));
		markSubmission(MenuConstants.CP_PROCESS_RG + item.getSessionTag());
		item.setProcessMark(params.get(PROCESSMARK));
		render("ComplianceProcessProduct/list.html", item, logs);
	}

	public static void validate(String productCode, String date) throws Exception {
		log.debug("validate. productCode: " + productCode + " date: " + date);

		Date processDate = Helper.parseDate(date, appProp.getDateFormat());
		Date appDate = generalService.getApplicationDate(UIConstants.SIMIAN_BANK_ID).getCurrentBusinessDate();

		log.debug("processDate =" + processDate + ", appDate=" + appDate);
		Map<String, String> map = new HashMap<String, String>();
		if (Helper.isGreaterYMD(processDate, appDate)) {
			map.put("validateDate", "Date can not be greater then application Date");
		}

		if (map.isEmpty()) {
			try {
				// check posting eod sudah dilakukan blm kalo blm munculin error
				boolean alreadyEod = generalService.isAlreadyEod(productCode, processDate);
				if (!alreadyEod) {
					map.put("validateEod", "Please run posting first");
				}
			} catch (Exception e) {
				map.put("validateEod", e.getMessage());
			}
		}

		if (map.isEmpty()) {
			try {
				// cek apakah nav untuk fund tersebut sudah ada belum
				boolean navExist = generalService.isNavExist(productCode, processDate);
				if (!navExist) {
					map.put("validateNav", "NAV not available");
				}
			} catch (Exception e) {
				map.put("validateNav", e.getMessage());
			}
		}

		// BILA mapnya kosong cek apakah fund sudah di process
		if (map.isEmpty()) {
			try {
				boolean alredyCompliance = generalService.isAlreadyProcessed(productCode, processDate);
				if (alredyCompliance) {
					map.put("validateReprocess", "This fund already process, do you want to re-process ?");
				}
			} catch (Exception e) {
				map.put("validateReprocess", e.getMessage());
			}
		}

		renderJSON(map);
	}

	@Check("transaction.complianceProcessrg")
	public static void entry() {
		log.debug("entry. ");
	}

	@Check("transaction.complianceProcessrg")
	public static void edit() {
		log.debug("edit. ");
	}

	@Check("transaction.complianceProcessrg")
	public static void view(Long id) {
		log.debug("view. id: " + id);
	}
}