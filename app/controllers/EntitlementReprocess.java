package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

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

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import play.i18n.Messages;
import play.mvc.Before;
import vo.AnnouncementSearchParameter;

public class EntitlementReprocess extends MedallionController {
	private static Logger log = Logger.getLogger(EntitlementReprocess.class);

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

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void search(AnnouncementSearchParameter params) {
		log.debug("search. params: " + params);

		List<ScCorporateAnnouncement> allCorporateAnnouncements = securityService.searchAnnouncementForEntitlementReprocess(params.dateFrom, params.dateTo, params.fieldDate, params.actionCode, params.securityType, params.securityCode, UIHelper.withOperator(params.announcementNo, params.announcementNoOperator));

		List<ScCorporateAnnouncement> corporateAnnouncements = new ArrayList<ScCorporateAnnouncement>();

		Map<String, ScCorporateAnnouncement> mapAnnouncements = new HashMap<String, ScCorporateAnnouncement>();
		for (ScCorporateAnnouncement annnouncement : allCorporateAnnouncements) {

			if (mapAnnouncements.isEmpty()) {
				mapAnnouncements.put(annnouncement.getCorporateAnnouncementKey().toString(), annnouncement);
				corporateAnnouncements.add(annnouncement);
			}

			boolean exist = false;
			for (String key : mapAnnouncements.keySet()) {
				if (key.equals(annnouncement.getCorporateAnnouncementKey().toString())) {
					exist = true;
					break;
				}
			}

			if (!exist) {
				mapAnnouncements.put(annnouncement.getCorporateAnnouncementKey().toString(), annnouncement);
				corporateAnnouncements.add(annnouncement);
			}
		}
		render("EntitlementReprocess/grid.html", corporateAnnouncements);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void list() {
		log.debug("list. ");

		List<ScCorporateAnnouncement> entitlements = securityService.listCorporateAnnouncementByStatus(UIConstants.SIMIAN_BANK_ID, "O ");
		render(entitlements);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void view() {
		log.debug("view. ");
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		List<SelectItem> operators = UIHelper.yesNoOperators();
		ScCorporateAnnouncement corporateAnnouncement = new ScCorporateAnnouncement();
		corporateAnnouncement.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));

		renderTemplate("EntitlementReprocess/entitlement.html", mode, corporateAnnouncement, operators);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;

		ScCorporateAnnouncement corporateAnnouncement = securityService.getCorporateAnnouncementById(id);
		Set<ScEntitlement> entitlements = corporateAnnouncement.getEntitlement();
		ScEntitlement entitlement = new ScEntitlement();
		for (ScEntitlement entitle : entitlements) {

			if (entitle.getStatus().trim().equals(LookupConstants.__RECORD_STATUS_APPROVED)) {
				entitlement = entitle;
				break;
			}
		}

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

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ENTITLEMENT_REPROCESS));
		render("EntitlementReprocess/detail1.html", mode, corporateAnnouncement, currentDate, entitlement);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void save(String taskId, Long keyId, String mode, String from) {
		log.debug("save. taskId: " + taskId + " keyId: " + keyId + " mode: " + mode + " from: " + from);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void confirming(String taskId, Long keyId, String mode, String from) {
		log.debug("confirming. taskId: " + taskId + " keyId: " + keyId + " mode: " + mode + " from: " + from);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void confirm(String taskId, Long keyId, String mode, String from) throws MedallionException {
		log.debug("confirm. taskId: " + taskId + " keyId: " + keyId + " mode: " + mode + " from: " + from);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void back(Long keyId, String taskId, String mode, ScCorporateAnnouncement corporateAnnouncement, ScEntitlement entitlement, String from) {
		log.debug("back. keyId: " + keyId + " taskId: " + taskId + " mode: " + mode + " corporateAnnouncement: " + corporateAnnouncement + " entitlement: " + entitlement + " from: " + from);

		corporateAnnouncement = securityService.getCorporateAnnouncementById(keyId);
		entitlement = serializerService.deserialize(session.getId(), keyId, ScEntitlement.class);
		Set<ScEntitlementDetail> entitlementDetails = entitlement.getEntitlementDetails();

		if (from.equals(UIConstants.WORKFLOW_FROM)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
		} else {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
		}
		render("EntitlementReprocess/entitlement.html", taskId, mode, corporateAnnouncement, entitlement, entitlementDetails, from);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void clear(String mode) {
		log.debug("clear. mode: " + mode);

		ScCorporateAnnouncement corporateAnnouncement = new ScCorporateAnnouncement();
		corporateAnnouncement.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		render("EntitlementReprocess/master_detail.html", corporateAnnouncement, mode);
	}

	public static void approval(String taskId, Long keyId, ScEntitlement entitlement, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " entitlement: " + entitlement + " from: " + from);
	}

	public static void approve(String taskId, Long keyId) {
		log.debug("approve. taskId: " + taskId + " keyId: " + keyId);

		try {
			ScEntitlement entitlement = securityService.approveEntitlement(keyId, taskId, session.get("username"));

			renderJSON(Formatter.resultSuccess(Messages.get("entitlement.approved", entitlement.getEntitlementKey())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
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

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void populateEntitlement(ScEntitlement entitlement, Long id) {
		log.debug("populateEntitlement. entitlement: " + entitlement + " id: " + id);

		Set<ScEntitlementDetail> entitlementDetails = securityService.populateEntitlement(id, false);

		render("EntitlementReprocesss/tab_grid_entitlement_detail.html", entitlementDetails);
	}

	@Check("transaction.corporateActionEntitlementReprocess")
	public static void process(ScCorporateAnnouncement corporateAnnouncement, ScEntitlement entitlement) {
		log.debug("process. corporateAnnouncement: " + corporateAnnouncement + " entitlement: " + entitlement);

		Date currentDate = taService.getCurrentBusinessDate();
		List<String> logs = new ArrayList<String>();
		try {
			//populate sc_entitlement_detail
			securityService.populateEntitlementDetailTmp(corporateAnnouncement, entitlement);
			//reprocess entitlement
			logs = transactionService.reprocess(corporateAnnouncement, entitlement, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY), true);
		} catch (Exception e) {
			logs.add(e.getMessage());
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ENTITLEMENT_REPROCESS));
		render("EntitlementReprocess/detail1.html", logs, corporateAnnouncement, currentDate, entitlement);
	}
}