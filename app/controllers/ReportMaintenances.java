package controllers;

import helpers.Formatter;
import helpers.UIConstants;

import java.util.List;

import org.apache.log4j.Logger;

import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnReportList;

@With(Secure.class)
public class ReportMaintenances extends MedallionController {
	private static Logger log = Logger.getLogger(ReportMaintenances.class);

	public static void list() {
		log.debug("list. ");

		List<GnReportList> reports = generalService.listReport();
		render(reports);
	}

	public static void view(Long id) {
		log.debug("view. id: " + id);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnReportList report = generalService.getReport(id);
			render("ReportMaintenances/detail.html", report, mode);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}

	}

	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnReportList report = new GnReportList();
		render("ReportMaintenances/detail.html", report, mode);
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnReportList report = generalService.getReport(id);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_REPORT_MAINTENANCE));
		render("ReportMaintenances/detail.html", report, mode);
	}

	public static void save(GnReportList report, String mode) {
		log.debug("save. report: " + report + " mode: " + mode);

		// Validate here
		if (report != null) {
			validation.required("report.sequenceNo", report.getSequenceNo());
			validation.required("report.reportGroup", report.getReportGroup());
			validation.required("report.reportName", report.getReportName());
			validation.required("report.reportUrl", report.getReportUrl());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_REPORT_MAINTENANCE));
				render("ReportMaintenances/detail.html", report, mode);
			} else {
				Long id = report.getReportKey();
				String json = serializerService.serialize(session.getId(), report.getReportKey(), report);
				confirming(id, mode);
			}
		} else {
			flash.error("argument.null", report);

		}
	}

	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		renderArgs.put("confirming", true);
		GnReportList report = serializerService.deserialize(session.getId(), id, GnReportList.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_REPORT_MAINTENANCE));
		render("ReportMaintenances/detail.html", report, mode);
	}

	public static void confirm(GnReportList report, String mode) {
		log.debug("confirm. report: " + report + " mode: " + mode);

		try {
			generalService.saveReport(report, session.get("username"), "");
			list();
		} catch (MedallionException e) {
			// flash.error("Duplicate Error! Code : ' "+taxMaster.getTaxCode()+" ' Already Exist",
			// taxMaster.getTaxCode());
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_REPORT_MAINTENANCE));
			render("ReportMaintenances/detail.html", report, mode, confirming);
		}
	}

	public static void back(Long id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		GnReportList report = serializerService.deserialize(session.getId(), id, GnReportList.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.GN_REPORT_MAINTENANCE));
		renderTemplate("ReportMaintenances/detail.html", report, mode);
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " group: " + group + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnReportList report = json.readValue(maintenanceLog.getNewData(), GnReportList.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ReportMaintenances/approval.html", report, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveReport(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveReport(session.get("username"), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

}
