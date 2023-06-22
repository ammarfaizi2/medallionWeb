package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.InquiryCorporateAnnoncementSearchParameter;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.ScAnnouncementDetail;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScMaster;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CancelCorporateAnnouncements extends MedallionController {
	private static Logger log = Logger.getLogger(CancelCorporateAnnouncements.class);

	private final static String FROMCANCEL = "fromCancel";

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("operators", operators);
	}

	@Before(only = { "view", "save", "confirming", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> holdingBasedOn = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._HOLDING_BASED_ON);
		renderArgs.put("holdingBasedOn", holdingBasedOn);

		List<SelectItem> roundingType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ROUNDING_TYPE);
		renderArgs.put("roundingType", roundingType);

		renderArgs.put("targetTypeCash", LookupConstants.CA_TARGET_TYPE_CASH);
		renderArgs.put("targetTypeSame", LookupConstants.CA_TARGET_TYPE_SAME);
		renderArgs.put("targetTypeOther", LookupConstants.CA_TARGET_TYPE_OTHER);
		renderArgs.put("securityTypeCash", LookupConstants.__SECURITY_TYPE_CASH);
		//start end yusuf 6145 std dari 5974 rubah pencarian query untuk security id cash menjadi cash_MM
		//ScMaster security = securityService.getSecurity(LookupConstants.__SECURITY_TYPE_CASH, LookupConstants.__SECURITY_TYPE_CASH);
		ScMaster security = securityService.getSecurity(LookupConstants.__SECURITY_ID_CASH_MM, LookupConstants.__SECURITY_TYPE_CASH);
		//end yusuf
		renderArgs.put("securityKeyCash", security.getSecurityKey());

	}

	@Check("transaction.cancelcorporateannouncements")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		InquiryCorporateAnnoncementSearchParameter params = new InquiryCorporateAnnoncementSearchParameter();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ANNOUNCEMENT));
		render("CancelCorporateAnnouncements/list.html", params, mode);
	}

	public static void paging(Paging page, InquiryCorporateAnnoncementSearchParameter param) {
		log.debug("paging. page: " + page + " param: " + param);

		if (param.isQuery()) {
			// page.addParams("caed.recordStatus", page.NOTEQUAL ,
			// LookupConstants.__RECORD_STATUS_UPDATED);

			page.addParams("ca.announcementDate", page.GREATEQUAL, param.dateFrom);
			page.addParams("ca.announcementDate", page.LESSEQUAL, param.dateTo);
			if (param.actionCode != null && !param.actionCode.equals("")) {
				page.addParams("ca.actionTemplate.actionTemplateKey", page.EQUAL, Long.parseLong(param.actionCode));
			}

			page.addParams("ca.securityType.securityType", page.EQUAL, param.securityType);
			if (param.securityCode != null && !param.securityCode.equals("")) {
				page.addParams("ca.security.securityKey", page.EQUAL, Long.parseLong(param.securityCode));
			}

			page.addParams("ca.corporateAnnouncementCode", param.corporateNoOperator, UIHelper.withOperator(param.corporateSearchNo, param.corporateNoOperator));
			page.addParams(Helper.searchAll("(to_char(ca.announcementDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(ca.recordingDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "to_char(ca.distributionDate,'" + Helper.dateOracle(appProp.getDateFormat()) + "')||" + "ca.corporateAnnouncementCode||ca.description||ca.status)"), page.LIKE, UIHelper.withOperator(page.getsSearch().toUpperCase(), 1));

			page = securityService.pagingCancelCorporateAnnouncement(page);
		}
		renderJSON(page);
	}

	@Check("transaction.cancelcorporateannouncements")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(id);
		Set<ScAnnouncementDetail> announcements = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcements.iterator().next();
		String fromCancel = FROMCANCEL;
		Date entitleDate = securityService.getEntitlementDate(corporateAnnouncement.getCorporateAnnouncementKey());

		if (corporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED)) {
			corporateAnnouncement.setStatus(LookupConstants.TRX_STATUS_WAITING_ENTITLEMENT_APPROVE);
		} else {
			corporateAnnouncement.setCancelledDate(entitleDate);
		}

		String nameFile = null;
		if (corporateAnnouncement.getDocument() != null) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			corporateAnnouncement.setFlagAttachFile(true);
		}

		if (!corporateAnnouncement.getTaxable()) {
			if (corporateAnnouncement.getActionTemplate() != null) {
				corporateAnnouncement.getActionTemplate().setTaxObject(null);
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ANNOUNCEMENT));
		render("CorporateAnnouncements/corporateAnnouncement.html", corporateAnnouncement, announcementDetail, mode, fromCancel, nameFile, entitleDate);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	@Check("transaction.cancelcorporateannouncements")
	public static void save(String mode, ScCorporateAnnouncement corporateAnnouncement, ScAnnouncementDetail announcementDetail, File attachment) {
		log.debug("save. mode: " + mode + " corporateAnnouncement: " + corporateAnnouncement + " announcementDetail: " + announcementDetail + " attachment: " + attachment);

		if (corporateAnnouncement != null) {
			Date entitleDate = securityService.getEntitlementDate(corporateAnnouncement.getCorporateAnnouncementKey());
			Date currentBusinessDate = generalService.getCurrentBusinessDate(LookupConstants.SIMIAN_ORGANIZATION_ID);

			validation.required("corporateAnnouncement.corporateAnnouncementCode", corporateAnnouncement.getCorporateAnnouncementCode());
			validation.required("Remark Cancel is", corporateAnnouncement.getRemarkCancel());

			if (corporateAnnouncement.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_ENTITLED)) {
				if (corporateAnnouncement.getCancelledDate() == null) {
					validation.required("Cancel Date is", corporateAnnouncement.getCancelledDate());
				} else {
					if (corporateAnnouncement.getCancelledDate().compareTo(currentBusinessDate) > 0) {
						validation.addError("", ExceptionConstants.CANCELDATE_GREATER_APPLICATION_DATE);
					}

					if (corporateAnnouncement.getCancelledDate().compareTo(entitleDate) < 0) {
						validation.addError("", ExceptionConstants.CANCELDATE_LESS_ENTITLEMENT_DATE);
					}
				}
			}

			if (validation.hasErrors()) {
				String nameFile = null;
				if (corporateAnnouncement.getDocument() != null) {
					int dash = corporateAnnouncement.getDocument().indexOf("-");
					nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
					corporateAnnouncement.setFlagAttachFile(true);
				}
				String fromCancel = FROMCANCEL;
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ANNOUNCEMENT));
				render("CorporateAnnouncements/corporateAnnouncement.html", corporateAnnouncement, announcementDetail, mode, fromCancel, nameFile, entitleDate);
			} else {
				Set<ScAnnouncementDetail> announcementDetails = new HashSet<ScAnnouncementDetail>();
				if (announcementDetail != null) {
					announcementDetails.add(announcementDetail);
				}

				corporateAnnouncement.setAnnouncementDetails(announcementDetails);
				Long id = corporateAnnouncement.getCorporateAnnouncementKey();
				serializerService.serialize(session.getId(), id, corporateAnnouncement);
				confirming(id, mode);
			}
		} else {
			flash.error("argument.null", corporateAnnouncement);
		}
	}

	@Check("transaction.cancelcorporateannouncements")
	public static void confirming(Long id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		ScCorporateAnnouncement corporateAnnouncement = serializerService.deserialize(session.getId(), id, ScCorporateAnnouncement.class);
		Set<ScAnnouncementDetail> announcementDetails = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcementDetails.iterator().next();
		String nameFile = null;
		if (!corporateAnnouncement.getDocument().isEmpty()) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			corporateAnnouncement.setFlagAttachFile(true);
		}
		String fromCancel = FROMCANCEL;
		Date entitleDate = securityService.getEntitlementDate(corporateAnnouncement.getCorporateAnnouncementKey());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ANNOUNCEMENT));
		render("CorporateAnnouncements/corporateAnnouncement.html", mode, confirming, fromCancel, corporateAnnouncement, announcementDetail, nameFile, entitleDate);
	}

	@Check("transaction.cancelcorporateannouncements")
	public static void confirm(String mode, ScCorporateAnnouncement corporateAnnouncement, ScAnnouncementDetail announcementDetail) {
		log.debug("confirm. mode: " + mode + " corporateAnnouncement: " + corporateAnnouncement + " announcementDetail: " + announcementDetail);

		try {
			corporateAnnouncement = securityService.cancelAnnouncement(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ANNOUNCEMENT, corporateAnnouncement, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "success");
			result.put("message", Messages.get("announcement.cancelled.successful", corporateAnnouncement.getCorporateAnnouncementCode()));
			renderJSON(result);

		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex != null && ex.getParams() != null) {
				for (Object paramItem : ex.getParams()) {
					params.add(Messages.get(paramItem));
				}

				result.put("error", Messages.get("error." + Messages.get(ex.getErrorCode()), params.toArray()));
			} else {
				result.put("error", Messages.get("error." + Messages.get(ex.getErrorCode())));
			}
			renderJSON(result);

		}catch (Exception e) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error",e.getMessage());
			renderJSON(result);
		}
	}

	@Check("transaction.cancelcorporateannouncements")
	public static void back(Long id, String mode, ScCorporateAnnouncement corporateAnnouncement) {
		log.debug("back. id: " + id + " mode: " + mode + " corporateAnnouncement: " + corporateAnnouncement);

		corporateAnnouncement = serializerService.deserialize(session.getId(), id, ScCorporateAnnouncement.class);

		Set<ScAnnouncementDetail> announcements = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcements.iterator().next();
		String nameFile = null;
		if (!corporateAnnouncement.getDocument().isEmpty()) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			corporateAnnouncement.setFlagAttachFile(true);
		}
		String fromCancel = FROMCANCEL;
		Date entitleDate = securityService.getEntitlementDate(corporateAnnouncement.getCorporateAnnouncementKey());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ANNOUNCEMENT));
		render("CorporateAnnouncements/corporateAnnouncement.html", corporateAnnouncement, mode, announcementDetail, nameFile, fromCancel, entitleDate);
	}

	public static void approval(String taskId, Long keyId, String from, String operation, String processDefinition, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " processDefinition: " + processDefinition + " maintenanceLogKey: " + maintenanceLogKey);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(keyId);
		if (corporateAnnouncement.getTaxable() != null) {
			if (corporateAnnouncement.getTaxable() == false) {
				corporateAnnouncement.getActionTemplate().setTaxObject(null);
			}
		}
		Set<ScAnnouncementDetail> announcements = corporateAnnouncement.getAnnouncementDetails();
		ScAnnouncementDetail announcementDetail = announcements.iterator().next();
		if (corporateAnnouncement.getActionTemplate().getTargetType().getLookupId().equals(LookupConstants.CA_TARGET_TYPE_CASH)) {
			announcementDetail.setSecurityTypeTarget(new ScTypeMaster());
			announcementDetail.getSecurityTypeTarget().setSecurityType("CASH");
			announcementDetail.getSecurityTypeTarget().setDescription("CASH");
			announcementDetail.setSecurityTarget(new ScMaster());
			//start end yusuf 6145 std dari 5974, rubah securityID cash jadi cash_MM
			//announcementDetail.getSecurityTarget().setSecurityId("CASH");
			announcementDetail.getSecurityTarget().setSecurityId(LookupConstants.__SECURITY_ID_CASH_MM);
			//end yusuf
			announcementDetail.getSecurityTarget().setDescription("CASH");
		}
		corporateAnnouncement.setRemarkCorrection(null);
		String nameFile = null;
		if (corporateAnnouncement.getDocument() != null) {
			int dash = corporateAnnouncement.getDocument().indexOf("-");
			nameFile = corporateAnnouncement.getDocument().substring(dash + 1);
			corporateAnnouncement.setFlagAttachFile(true);
		}
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		String fromCancel = FROMCANCEL;
		boolean confirming = true;
		Date entitleDate = securityService.getEntitlementDate(corporateAnnouncement.getCorporateAnnouncementKey());
		render("CancelCorporateAnnouncements/approval.html", fromCancel, confirming, corporateAnnouncement, announcementDetail, taskId, mode, from, operation, maintenanceLogKey, keyId, nameFile, entitleDate);
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			String hasil = securityService.approveCancelAnnouncement(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, session.get(UIConstants.SESSION_USER_KEY));
			if (hasil.trim().equals(WorkflowConstants.APPROVE_SUCCESS)) {
				renderJSON(Formatter.resultSuccess("success"));
			} else {
				renderJSON(Formatter.resultError(Messages.get(hasil)));
			}
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveCancelAnnouncement(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, session.get(UIConstants.SESSION_USER_KEY));

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
