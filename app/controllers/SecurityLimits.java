package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CpSecurity;
import com.simian.medallion.model.CpSecurityDetail;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class SecurityLimits extends MedallionController {
	private static Logger log = Logger.getLogger(SecurityLimits.class);

	private static JsonHelper jsonSecurityLimitDetail = new JsonHelper().getSecurityLimitDetailSerializer();

	@Before(unless = { "list" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.complianceSecurityType")
	public static void list() {
		log.debug("list. ");

		List<CpSecurity> cpSecurities = generalService.listSecurityLimits();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_LIMIT));
		render(cpSecurities);
	}

	@Check("administration.complianceSecurityType")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CpSecurity cpSecurity = generalService.getSecurityLimit(id);
		String securityLimitTabs = null;
		Set<CpSecurityDetail> securityDetails = cpSecurity.getCpSecurityDetails();
		cpSecurity.setCpSecurityDetails(new HashSet<CpSecurityDetail>(0));

		for (CpSecurityDetail cpSecurityDetail : securityDetails) {
			cpSecurityDetail.setCpSecurity(null);
			cpSecurityDetail.getSecurityType().setSecurityClass(null);
			cpSecurity.getCpSecurityDetails().add(cpSecurityDetail);
		}
		try {
			securityLimitTabs = jsonSecurityLimitDetail.serialize(cpSecurity.getCpSecurityDetails());
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_LIMIT));
		render("SecurityLimits/detail.html", cpSecurity, securityLimitTabs, mode);

	}

	@Check("administration.complianceSecurityType")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CpSecurity cpSecurity = new CpSecurity();
		String securityLimitTabs = null;
		try {
			securityLimitTabs = jsonSecurityLimitDetail.serialize(cpSecurity.getCpSecurityDetails());
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_LIMIT));
		render("SecurityLimits/detail.html", cpSecurity, mode, securityLimitTabs);
	}

	@Check("administration.complianceSecurityType")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CpSecurity cpSecurity = generalService.getSecurityLimit(id);

		String securityLimitTabs = null;
		String status = cpSecurity.getRecordStatus();
		try {
			securityLimitTabs = jsonSecurityLimitDetail.serialize(cpSecurity.getCpSecurityDetails());
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_LIMIT));
		render("SecurityLimits/detail.html", cpSecurity, mode, status, securityLimitTabs);
	}

	@Check("administration.complianceSecurityType")
	public static void save(CpSecurity cpSecurity, String mode, String status, CpSecurityDetail[] securityLimitDetails) {
		log.debug("save. cpSecurity: " + cpSecurity + " mode: " + mode + " status: " + status + " securityLimitDetails: " + securityLimitDetails);

		if (cpSecurity == null)
			entry();
		List<CpSecurityDetail> lstSecurityLimitDetail = new ArrayList<CpSecurityDetail>();
		cpSecurity.setCpSecurityDetails(new HashSet<CpSecurityDetail>(0));

		if ((securityLimitDetails != null) && (securityLimitDetails.length > 0)) {
			for (CpSecurityDetail cpSecurityDetail : securityLimitDetails) {
				lstSecurityLimitDetail.add(cpSecurityDetail);

				cpSecurity.getCpSecurityDetails().add(cpSecurityDetail);
			}
		}

		// Validate here
		if (cpSecurity != null) {
			log.debug("Aktiv >> " + cpSecurity.getActive());
			if (validation.errorsMap().values().containsAll(lstSecurityLimitDetail) == false) {
				validation.clear();
			}

			validation.required("Rule Code", cpSecurity.getComplianceRule().getRuleCode());

			if ((cpSecurity.getCpSecurityDetails() == null) || (cpSecurity.getCpSecurityDetails().size() == 0)) {
				validation.required("Security Type List", cpSecurity.getCpSecurityDetails());
			}

			if (validation.hasErrors()) {
				String securityLimitTabs = null;
				try {
					securityLimitTabs = jsonSecurityLimitDetail.serialize(cpSecurity.getCpSecurityDetails());
				} catch (Exception e) {
					log.debug(e.getMessage(), e);
				}

				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_LIMIT));
				render("SecurityLimits/detail.html", cpSecurity, securityLimitDetails, securityLimitTabs, mode, status);
			} else {
				Long id = cpSecurity.getSecurityLimitKey();
				serializerService.serialize(session.getId(), id, cpSecurity);
				confirming(id, mode, status);
			}
		} else {
			flash.error("argument.null", cpSecurity);
		}
	}

	@Check("administration.complianceSecurityType")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		CpSecurity cpSecurity = serializerService.deserialize(session.getId(), id, CpSecurity.class);
		String securityLimitTabs = null;
		try {
			securityLimitTabs = jsonSecurityLimitDetail.serialize(cpSecurity.getCpSecurityDetails());
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_LIMIT));
		render("SecurityLimits/detail.html", cpSecurity, securityLimitTabs, mode, status);
	}

	@Check("administration.complianceSecurityType")
	public static void confirm(CpSecurity cpSecurity, CpSecurityDetail[] securityLimitDetails, String mode, String status) {
		log.debug("confirm. cpSecurity: " + cpSecurity + " securityLimitDetails: " + securityLimitDetails + " mode: " + mode + " status: " + status);

		if (cpSecurity == null)
			back(null, mode, status);
		try {
			cpSecurity.setCpSecurityDetails(new HashSet<CpSecurityDetail>(0));

			if ((securityLimitDetails != null) && (securityLimitDetails.length > 0)) {
				for (CpSecurityDetail cpSecurityDetail : securityLimitDetails) {
					cpSecurityDetail.setCpSecurity(cpSecurity);
					cpSecurity.getCpSecurityDetails().add(cpSecurityDetail);
				}
			}
			cpSecurity = generalService.saveSecurityLimit(MenuConstants.CP_SECURITY_LIMIT, cpSecurity, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Rule ID : ' " + cpSecurity.getComplianceRule().getRuleCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			String securityLimitTabs = null;
			try {
				securityLimitTabs = jsonSecurityLimitDetail.serialize(cpSecurity.getCpSecurityDetails());
			} catch (Exception ex) {
				log.debug(ex.getMessage(), ex);
			}
			validation.clear();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_LIMIT));
			render("SecurityLimits/detail.html", cpSecurity, securityLimitTabs, mode, confirming, status);
		}
	}

	@Check("administration.complianceSecurityType")
	public static void back(Long id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		CpSecurity cpSecurity = serializerService.deserialize(session.getId(), id, CpSecurity.class);
		String securityLimitTabs = null;
		try {
			securityLimitTabs = jsonSecurityLimitDetail.serialize(cpSecurity.getCpSecurityDetails());
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CP_SECURITY_LIMIT));
		render("SecurityLimits/detail.html", cpSecurity, mode, status, securityLimitTabs);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CpSecurity cpSecurity = json.readValue(maintenanceLog.getNewData(), CpSecurity.class);
			String securityLimitTabs = null;
			Set<CpSecurityDetail> securityDetails = cpSecurity.getCpSecurityDetails();
			cpSecurity.setCpSecurityDetails(new HashSet<CpSecurityDetail>(0));

			for (CpSecurityDetail cpSecurityDetail : securityDetails) {
				cpSecurityDetail.setCpSecurity(null);
				cpSecurityDetail.getSecurityType().setSecurityClass(null);
				cpSecurity.getCpSecurityDetails().add(cpSecurityDetail);
			}
			try {
				securityLimitTabs = jsonSecurityLimitDetail.serialize(cpSecurity.getCpSecurityDetails());
			} catch (Exception e) {
				log.debug(e.getMessage(), e);
			}

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("SecurityLimits/approval.html", cpSecurity, securityLimitTabs, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error("Approval Impacted Security Limit", e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveSecurityLimit(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveSecurityLimit(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}