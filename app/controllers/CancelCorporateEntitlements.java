package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;
import vo.InquiryCorporateAnnoncementSearchParameter;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.CsTransaction;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.ScAnnouncementDetail;
import com.simian.medallion.model.ScCorporateAnnouncement;
import com.simian.medallion.model.ScCouponSchedule;
import com.simian.medallion.model.ScEntitlement;
import com.simian.medallion.model.ScEntitlementDetail;
import com.simian.medallion.vo.SelectItem;

@With(Secure.class)
public class CancelCorporateEntitlements extends Registry {
	private static Logger log = Logger.getLogger(CancelCorporateEntitlements.class);

	private final static String SOURCE = "Source";
	private final static String TARGET = "Target";

	@Before(only = { "list" })
	public static void setupList() {
		log.debug("setupList. ");

		List<SelectItem> operators = UIHelper.stringOperators();
		renderArgs.put("stringOperators", operators);
	}

	@Check("transaction.cancelcorporateentitlements")
	public static void list() {
		log.debug("list. ");

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		InquiryCorporateAnnoncementSearchParameter params = new InquiryCorporateAnnoncementSearchParameter();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ENTITLEMENT));
		render("CancelCorporateEntitlements/list.html", params, mode);
	}

	public static void search(InquiryCorporateAnnoncementSearchParameter params) {
		log.debug("search. params: " + params);

		List<ScCorporateAnnouncement> corporateAnnouncements = securityService.searchCancelCAEntitlement(params.dateFrom, params.dateTo, params.actionCode, params.securityType, params.securityCode, UIHelper.withOperator(params.corporateSearchNo, params.corporateNoOperator));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ENTITLEMENT));
		render("CancelCorporateEntitlements/grid.html", corporateAnnouncements);
	}

	@Check("transaction.cancelcorporateentitlements")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;

		ScEntitlement entitlement = securityService.getEntitlementByAnnouncement(id);
		List<CsTransaction> listCancel = accountService.listCancelEntitlement(entitlement.getEntitlementKey().toString());
		List<CsTransaction> transLists = new ArrayList<CsTransaction>();
		BigDecimal sumQty = BigDecimal.ZERO;
		BigDecimal sumSettle = BigDecimal.ZERO;
		for (CsTransaction csTransaction : listCancel) {
			CsTransaction csTrans = accountService.getTransactionByEntitlement(csTransaction.getTransactionKey());
			sumQty = sumQty.add(csTrans.getQuantity());
			sumSettle = sumSettle.add(csTrans.getSettlementAmount());
			transLists.add(csTrans);
		}

		Long securityKey = new Long(0);
		Integer couponNo = new Integer(0);

		if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_ANNOUNCEMENT_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getAnnouncementDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_CUM_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getCumDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_EX_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getExDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_RECORDING_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getRecordingDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_COUPON_DATE)) {
			for (ScAnnouncementDetail scd : entitlement.getCorporateAnnouncement().getAnnouncementDetails()) {
				securityKey = scd.getCorporateAnnouncement().getSecurity().getSecurityKey();
				couponNo = scd.getCouponSchedule().getId().getCouponNo();
			}
			ScCouponSchedule coupon = securityService.getCouponScheduleFilter(securityKey, couponNo);
			entitlement.getCorporateAnnouncement().setAnnouncementDate(coupon.getPaymentDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_DISTRIBUTION_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getDistributionDate());
		}

		BigDecimal counTrans = new BigDecimal(transLists.size());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ENTITLEMENT));
		render("CancelCorporateEntitlements/detail.html", entitlement, mode, transLists, counTrans, sumQty, sumSettle);
	}

	@Check("transaction.cancelcorporateentitlements")
	public static void cancel(Long id, Long idTx) {
		log.debug("cancel. id: " + id + " idTx: " + idTx);

		String mode = UIConstants.DISPLAY_MODE_VIEW;

		ScEntitlement entitlement = securityService.getEntitlementByAnnouncement(id);
		ScEntitlementDetail trans = securityService.getEntitlementDetails(idTx);

		Long securityKey = new Long(0);
		Integer couponNo = new Integer(0);

		if (trans.getTargetHoldingRefs() != null && trans.getSourceHoldingRefs() != null) {
			trans.setTargetHoldingRefs(trans.getTargetHoldingRefs());
		} else if (trans.getTargetHoldingRefs() != null) {
			trans.setTargetHoldingRefs(trans.getTargetHoldingRefs());
		} else if (trans.getSourceHoldingRefs() != null) {
			trans.setTargetHoldingRefs(trans.getSourceHoldingRefs());
		}

		if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_ANNOUNCEMENT_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getAnnouncementDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_CUM_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getCumDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_EX_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getExDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_RECORDING_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getRecordingDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_COUPON_DATE)) {
			for (ScAnnouncementDetail scd : entitlement.getCorporateAnnouncement().getAnnouncementDetails()) {
				securityKey = scd.getCorporateAnnouncement().getSecurity().getSecurityKey();
				couponNo = scd.getCouponSchedule().getId().getCouponNo();
			}
			ScCouponSchedule coupon = securityService.getCouponScheduleFilter(securityKey, couponNo);
			entitlement.getCorporateAnnouncement().setAnnouncementDate(coupon.getPaymentDate());
		} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_DISTRIBUTION_DATE)) {
			entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getDistributionDate());
		}

		List<CsTransaction> csTrans = accountService.listTransactionByRef(trans.getEntitlementDetailKey().toString());
		for (CsTransaction csTransaction : csTrans) {
			if (csTransaction.getTransactionTemplate() != null) {
				if (trans.getEntitlement().getCorporateAnnouncement().getActionTemplate().getSourceTransaction() != null) {
					if (csTransaction.getTransactionTemplate().getTransactionTemplateKey().toString().equals(trans.getEntitlement().getCorporateAnnouncement().getActionTemplate().getSourceTransaction().getTransactionTemplateKey().toString())) {
						csTransaction.setViewClassification(SOURCE);
					}
				}
				if (trans.getEntitlement().getCorporateAnnouncement().getActionTemplate().getTargetTransaction() != null) {
					if (csTransaction.getTransactionTemplate().getTransactionTemplateKey().toString().equals(trans.getEntitlement().getCorporateAnnouncement().getActionTemplate().getTargetTransaction().getTransactionTemplateKey().toString())) {
						csTransaction.setViewClassification(TARGET);
					}
				}
			}
		}

		trans.setCancelDate(entitlement.getCorporateAnnouncement().getAnnouncementDate());

		String listTrans = null;
		try {
			listTrans = json.writeValueAsString(csTrans);
			listTrans = listTrans.replace("\'", "");
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ENTITLEMENT));
		render("CancelCorporateEntitlements/entry.html", entitlement, mode, trans, listTrans);
	}

	public static void entry() {
		log.debug("entry. ");
	}

	public static void edit(Long id) {
		log.debug("edit. id: " + id);
	}

	public static void save() {
		log.debug("save. ");
	}

	public static void confirming() {
		log.debug("confirming. ");
	}

	public static void confirm(ScEntitlementDetail trans, String mode, ScEntitlement entitlement) {
		log.debug("confirm. trans: " + trans + " mode: " + mode + " entitlement: " + entitlement);

		try {
			ScEntitlementDetail sce = securityService.cancelCAEntitlement(MenuConstants.CS_CANCEL_CORPORATE_ACTION_ENTITLEMENT, trans, entitlement, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "success");
			result.put("message", Messages.get("Entitlement has successfully cancelled with Entitlement No : " + sce.getEntitlement().getEntitlementKey()));
			renderJSON(result);
		} catch (MedallionException e) {
			log.error(e.getMessage(), e);
			log.debug("error confirm cancelCAEntitlement " + e.getMessage());
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");

			result.put("error", Messages.get("error." + Messages.get(e.getMessage())));
			renderJSON(result);
		}
	}

	public static void back() {
		log.debug("back. ");
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			ScEntitlementDetail trans = json.readValue(maintenanceLog.getNewData(), ScEntitlementDetail.class);
			// ScEntitlement entitlement =
			// securityService.getEntitlementByAnnouncement(trans.getEntitlement().getCorporateAnnouncement().getCorporateAnnouncementKey());
			ScEntitlement entitlement = securityService.getEntitlementByAnnouncementAndEntitlement(trans.getEntitlement().getCorporateAnnouncement().getCorporateAnnouncementKey(), trans.getEntitlement().getEntitlementKey());

			Long securityKey = new Long(0);
			Integer couponNo = new Integer(0);

			if (trans.getTargetHoldingRefs() != null && trans.getSourceHoldingRefs() != null) {
				trans.setTargetHoldingRefs(trans.getTargetHoldingRefs());
			} else if (trans.getTargetHoldingRefs() != null) {
				trans.setTargetHoldingRefs(trans.getTargetHoldingRefs());
			} else if (trans.getSourceHoldingRefs() != null) {
				trans.setTargetHoldingRefs(trans.getSourceHoldingRefs());
			}

			if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_ANNOUNCEMENT_DATE)) {
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getAnnouncementDate());
			} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_CUM_DATE)) {
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getCumDate());
			} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_EX_DATE)) {
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getExDate());
			} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_RECORDING_DATE)) {
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getRecordingDate());
			} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_COUPON_DATE)) {
				for (ScAnnouncementDetail scd : entitlement.getCorporateAnnouncement().getAnnouncementDetails()) {
					securityKey = entitlement.getCorporateAnnouncement().getSecurity().getSecurityKey();
					couponNo = scd.getCouponSchedule().getId().getCouponNo();
				}
				ScCouponSchedule coupon = securityService.getCouponScheduleFilter(securityKey, couponNo);
				entitlement.getCorporateAnnouncement().setAnnouncementDate(coupon.getPaymentDate());
			} else if (entitlement.getCorporateAnnouncement().getActionTemplate().getLookupEntitlementDate().getLookupId().trim().equals(LookupConstants.CA_SCHEDULE_DISTRIBUTION_DATE)) {
				entitlement.getCorporateAnnouncement().setAnnouncementDate(entitlement.getCorporateAnnouncement().getDistributionDate());
			}

			List<CsTransaction> csTrans = trans.getChildTrans();
			for (CsTransaction csTransaction : csTrans) {
				if (csTransaction.getTransactionTemplate() != null) {
					if (trans.getEntitlement().getCorporateAnnouncement().getActionTemplate().getSourceTransaction() != null) {
						if (csTransaction.getTransactionTemplate().getTransactionTemplateKey().toString().equals(trans.getEntitlement().getCorporateAnnouncement().getActionTemplate().getSourceTransaction().getTransactionTemplateKey().toString())) {
							csTransaction.setViewClassification(SOURCE);
						}
					}
					if (trans.getEntitlement().getCorporateAnnouncement().getActionTemplate().getTargetTransaction() != null) {
						if (csTransaction.getTransactionTemplate().getTransactionTemplateKey().toString().equals(trans.getEntitlement().getCorporateAnnouncement().getActionTemplate().getTargetTransaction().getTransactionTemplateKey().toString())) {
							csTransaction.setViewClassification(TARGET);
						}
					}
				}
			}

			String listTrans = null;
			try {
				listTrans = json.writeValueAsString(csTrans);
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("CancelCorporateEntitlements/approval.html", trans, entitlement, listTrans, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.debug("error approval Cancel CA Entitlement " + e.getMessage());
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			String hasil = securityService.approveCancelCAEntitlement(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE, session.get(UIConstants.SESSION_USER_KEY));
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
			securityService.approveCancelCAEntitlement(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT, session.get(UIConstants.SESSION_USER_KEY));

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
