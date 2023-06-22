package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

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
import vo.AnnouncementSearchParameter;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.ScAnnouncementDetail;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.model.ScEntitlement;
import com.simian.medallion.model.ScEntitlementDetail;
import com.simian.medallion.vo.SelectItem;

public class Entitlements extends MedallionController {
	private static Logger log = Logger.getLogger(Entitlements.class);

	static List<String> hitProcess = new ArrayList<String>();
	static List<Long> hitApprove = new ArrayList<Long>();
	
	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Before(unless = { "edit", "entry", "save", "back" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("transaction.corporateActionEntitlement")
	public static void search(AnnouncementSearchParameter params) {
		log.debug("search. params: " + params);

		List<ScCorporateAnnouncement> corporateAnnouncements = securityService.searchAnnouncementForEntitlements(params.dateFrom, params.dateTo, params.fieldDate, params.actionCode, params.securityType, params.securityCode, UIHelper.withOperator(params.announcementNo, params.announcementNoOperator));
		render("Entitlements/grid.html", corporateAnnouncements);
	}

	@Check("transaction.corporateActionEntitlement")
	public static void list() {
		log.debug("list. ");

		List<ScCorporateAnnouncement> entitlements = securityService.listCorporateAnnouncementByStatus(UIConstants.SIMIAN_BANK_ID, "O ");
		render(entitlements);
	}

	@Check("transaction.corporateActionEntitlement")
	public static void view() {
		log.debug("view. ");
	}

	@Check("transaction.corporateActionEntitlement")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		List<SelectItem> operators = UIHelper.yesNoOperators();
		ScCorporateAnnouncement corporateAnnouncement = new ScCorporateAnnouncement();
		corporateAnnouncement.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		renderTemplate("Entitlements/entitlement.html", mode, corporateAnnouncement, operators);
	}

	@Check("transaction.corporateActionEntitlement")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(id);

		if (corporateAnnouncement.getActionTemplate().getLookupEntitlementDate().getLookupId().equals(LookupConstants.CA_SCHEDULE_ANNOUNCEMENT_DATE)) {
			corporateAnnouncement.setEntitlementDate(corporateAnnouncement.getAnnouncementDate());
		}
		if (corporateAnnouncement.getActionTemplate().getLookupEntitlementDate().getLookupId().equals(LookupConstants.CA_SCHEDULE_CUM_DATE)) {
			corporateAnnouncement.setEntitlementDate(corporateAnnouncement.getCumDate());
		}
		if (corporateAnnouncement.getActionTemplate().getLookupEntitlementDate().getLookupId().equals(LookupConstants.CA_SCHEDULE_DISTRIBUTION_DATE)) {
			corporateAnnouncement.setEntitlementDate(corporateAnnouncement.getDistributionDate());
		}
		if (corporateAnnouncement.getActionTemplate().getLookupEntitlementDate().getLookupId().equals(LookupConstants.CA_SCHEDULE_EX_DATE)) {
			corporateAnnouncement.setEntitlementDate(corporateAnnouncement.getExDate());
		}
		if (corporateAnnouncement.getActionTemplate().getLookupEntitlementDate().getLookupId().equals(LookupConstants.CA_SCHEDULE_RECORDING_DATE)) {
			corporateAnnouncement.setEntitlementDate(corporateAnnouncement.getRecordingDate());
		}
		if (corporateAnnouncement.getActionTemplate().getLookupEntitlementDate().getLookupId().equals(LookupConstants.CA_SCHEDULE_COUPON_DATE)) {
			int couponNo = 0;
			for (ScAnnouncementDetail detail : corporateAnnouncement.getAnnouncementDetails()) {
				couponNo = detail.getCouponNo();
			}
			ScCouponSchedule coupon = securityService.getCouponScheduleFilter(corporateAnnouncement.getSecurity().getSecurityKey(), couponNo);
			corporateAnnouncement.setEntitlementDate(coupon.getPaymentDate());
		}

		Date currentDate = taService.getCurrentBusinessDate();

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ENTITLEMENT));
		render("Entitlements/detail1.html", mode, corporateAnnouncement, currentDate);
	}

	@Check("transaction.corporateActionEntitlement")
	public static void save(String taskId, Long keyId, String mode, String from) {
		log.debug("save. taskId: " + taskId + " keyId: " + keyId + " mode: " + mode + " from: " + from);

		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(keyId);
		Set<ScEntitlementDetail> entitlementDetails = securityService.populateEntitlement(keyId, false);
		ScEntitlement entitlement = new ScEntitlement();

		if (entitlementDetails != null) {
			if (validation.hasErrors()) {
				if (from.equals(UIConstants.WORKFLOW_FROM)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
				} else {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
				}
				render("Entitlements/entitlement.html", entitlement, corporateAnnouncement, mode, taskId, from);
			} else {

				serializerService.serialize(session.getId(), keyId, entitlement);
				log.debug("End " + new Date());
				confirming(taskId, keyId, mode, from);
			}
		} else {
			flash.error("argument.null", entitlement);
		}
	}

	@Check("transaction.corporateActionEntitlement")
	public static void confirming(String taskId, Long keyId, String mode, String from) {
		log.debug("confirming. taskId: " + taskId + " keyId: " + keyId + " mode: " + mode + " from: " + from);

		boolean confirming = true;
		log.debug("Deserialize Start " + new Date());
		ScEntitlement entitlement = serializerService.deserialize(session.getId(), keyId, ScEntitlement.class);
		log.debug("Deserialize End " + new Date());
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(keyId);
		Set<ScEntitlementDetail> entitlementDetails = securityService.populateEntitlement(keyId, false);
		log.debug("[confirming] entitlementDetails size = " + entitlementDetails.size());
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		render("Entitlements/entitlement.html", taskId, mode, confirming, corporateAnnouncement, entitlement, entitlementDetails, from);
	}

	@Check("transaction.corporateActionEntitlement")
	public static void confirm(String taskId, Long keyId, String mode, String from) throws MedallionException {
		log.debug("confirm. taskId: " + taskId + " keyId: " + keyId + " mode: " + mode + " from: " + from);

		boolean confirming = true;
		try {
			ScEntitlement entitlement = new ScEntitlement();
			ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(keyId);
			Set<ScEntitlementDetail> entitlementDetails = securityService.populateEntitlement(keyId, false);

			log.debug("[CONT] entitlement detail size = " + entitlementDetails.size());
			if (entitlementDetails != null) {
				entitlement.setEntitlementDetails(new HashSet<ScEntitlementDetail>(entitlementDetails));
			}
			entitlement.setCorporateAnnouncement(corporateAnnouncement);

			ScEntitlement newEntitlement = securityService.createEntitlement(MenuConstants.CS_ENTITLEMENT, entitlement, session.get("username"), taskId, session.get("userKey"));
			if (newEntitlement.getEntitlementKey() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				result.put("message", Messages.get("entitlement.confirmed.successful", newEntitlement.getEntitlementKey()));
				renderJSON(result);
			} else {
				render("Entitlements/detail_ent.html", taskId, newEntitlement, mode, confirming, entitlementDetails, from);
			}
		} catch (MedallionException e) {
			flash.error("Invalid Error !! ");
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Entitlements/detail_ent.html", taskId, mode, confirming, from);
		}
	}

	@Check("transaction.corporateActionEntitlement")
	public static void back(Long keyId, String taskId, String mode, ScCorporateAnnouncement corporateAnnouncement, ScEntitlement entitlement, String from) {
		log.debug("back. keyId: " + keyId + " taskId: " + taskId + " mode: " + mode + " corporateAnnouncement: " + corporateAnnouncement + " entitlement: " + entitlement + " from: " + from);

		//boolean confirming = false;

		corporateAnnouncement = securityService.getCorporateAnnouncementById(keyId);
		entitlement = serializerService.deserialize(session.getId(), keyId, ScEntitlement.class);
		Set<ScEntitlementDetail> entitlementDetails = entitlement.getEntitlementDetails();
		log.debug("[back] after entitlementDetails size = " + entitlementDetails.size());
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		render("Entitlements/entitlement.html", taskId, mode, corporateAnnouncement, entitlement, entitlementDetails, from);
	}

	@Check("transaction.corporateActionEntitlement")
	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		ScCorporateAnnouncement corporateAnnouncement = new ScCorporateAnnouncement();
		corporateAnnouncement.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		render("Entitlements/master_detail.html", corporateAnnouncement, mode);
	}

	public static void approval(String taskId, Long keyId, ScEntitlement entitlement, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " entitlement: " + entitlement + " from: " + from);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		entitlement = securityService.getEntitlement(keyId);
		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey());
		log.debug("[approval] corporateAnnouncementKey = " + entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey());
		Set<ScEntitlementDetail> entitlementDetails = securityService.populateEntitlement(entitlement.getCorporateAnnouncement().getCorporateAnnouncementKey(), false);
		if ("corporateaction-settlement".equals(from)) {
			entitlementDetails = entitlement.getEntitlementDetails();
			for (ScEntitlementDetail detail : entitlementDetails) {
				if (detail.getSourceHoldingRefs() != null) {
					detail.setTotalHolding(detail.getSourceHolding());
				} else {
					detail.setTotalHolding(detail.getTargetHolding());
				}
			}
		}
		log.debug("[approval] size entitlementDetails = " + entitlementDetails.size());
		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else if (from.equals(UIConstants.PARAM_CA_SETTLE)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CA_SETTLEMENT_ENTRY));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		render("Entitlements/approval.html", mode, taskId, corporateAnnouncement, entitlement, entitlementDetails, from, keyId);
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);
		Map<String, Object> result = new HashMap<String, Object>();
		if (hitApprove.contains(keyId)) {
			result.put("status", "error");
			result.put("error","error.Record has already been modified");
			renderJSON(result);
		} else {
			hitApprove.add(keyId);
			try {
				ScEntitlement entitlement = securityService.approveEntitlement(keyId, taskId, session.get("username"));

				//renderJSON(Formatter.resultSuccess(Messages.get("entitlement.approved", entitlement.getEntitlementKey())));
				result.put("status", "success");
				result.put("message", Messages.get("entitlement.approved", entitlement.getEntitlementKey()));
			} catch (MedallionException e) {
				//renderJSON(Formatter.resultError(e));
				log.error(e.getMessage(), e);
				result.put("error", "error." + e.getMessage());
				List<String> params = new ArrayList<String>();
				if(e.getParams()!= null){
					for (Object param : e.getParams()) {
						params.add(Messages.get(param));
					}
					result.put("error", Messages.get("error." + e.getErrorCode(), params.toArray()));
				}else{
					result.put("error", Messages.get("error." + e.getErrorCode()));
				}
			} catch (Exception e) {
				//renderJSON(Formatter.resultError(e));
				result.put("status", "error");
				result.put("error", "error." + e.getMessage());
			} finally {
				hitApprove.remove(keyId);
			}
			renderJSON(result);
		}
		
	}

	public static void reject(String taskId, Long keyId) {
		log.debug("reject. taskId: " + taskId + " keyId: " + keyId);

		try {
			ScEntitlement entitlement = securityService.rejectEntitlement(keyId, taskId, session.get("username"));

			renderJSON(Formatter.resultSuccess(Messages.get("entitlement.rejected", entitlement.getEntitlementKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("transaction.corporateActionEntitlement")
	public static void populateEntitlement(ScEntitlement entitlement, Long id) {
		log.debug("populateEntitlement. entitlement: " + entitlement + " id: " + id);
	}

	@Check("transaction.corporateActionEntitlement")
	public static void process(ScCorporateAnnouncement corporateAnnouncement) {
		log.debug("process. corporateAnnouncement: " + corporateAnnouncement);

		Date currentDate = taService.getCurrentBusinessDate();
		List<String> logs = new ArrayList<String>();

		if (hitProcess.contains(corporateAnnouncement.getCorporateAnnouncementKey().toString())) {
			logs.add("This announcement is in progress!");
		}else{
			hitProcess.add(corporateAnnouncement.getCorporateAnnouncementKey().toString());
			try {

				logs = transactionService.entitlements(corporateAnnouncement, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY), false);

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				hitProcess.remove(corporateAnnouncement.getCorporateAnnouncementKey().toString());
			}
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ENTITLEMENT));
		render("Entitlements/detail1.html", logs, corporateAnnouncement, currentDate);

	}
}