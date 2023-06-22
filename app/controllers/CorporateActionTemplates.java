package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.ScActionTemplate;
import com.simian.medallion.vo.SelectItem;

//@Check("administration.corporateActionTemplate")
public class CorporateActionTemplates extends MedallionController {
	private static Logger log = Logger.getLogger(CorporateActionTemplates.class);

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		renderArgs.put("targetTypeC", LookupConstants.CA_TARGET_TYPE_CASH);
		renderArgs.put("caScheduleCouponDate", LookupConstants.CA_SCHEDULE_COUPON_DATE);
		renderArgs.put("operators", UIHelper.sourceTargetOperator());
		renderArgs.put("typeOperators", UIHelper.actionTemplateTypeOperator());
		renderArgs.put("trueFalseOperators", UIHelper.yesNoOperators());

		List<SelectItem> target = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CA_TARGET_TYPE);
		renderArgs.put("target", target);

		List<SelectItem> lookupEntitlementDate = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CA_SCHEDULE);
		renderArgs.put("lookupEntitlementDate", lookupEntitlementDate);

		List<SelectItem> holdingType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CA_HOLDING_TYPE);
		renderArgs.put("holdingType", holdingType);

		List<SelectItem> layoutType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CA_LAYOUT_TYPE);
		renderArgs.put("layoutType", layoutType);

		List<SelectItem> merCaTypeOpts = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CA_TYPE);
		renderArgs.put("merCaTypeOpts", merCaTypeOpts);
		
		List<SelectItem> taxObject = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TAX_OBJECT);
		renderArgs.put("taxObject", taxObject);

	}

	@Check("administration.corporateActionTemplate")
	public static void list() {
		log.debug("list. ");

		List<ScActionTemplate> actionTemplates = securityService.listActionTemplate(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_CORPORATE_ACTION));
		render(actionTemplates);
	}

	@Check("administration.corporateActionTemplate")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			ScActionTemplate actionTemplate = securityService.getActionTemplate(id);
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_CORPORATE_ACTION));
			render("CorporateActionTemplates/detail.html", actionTemplate, mode);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	@Check("administration.corporateActionTemplate")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		ScActionTemplate actionTemplate = new ScActionTemplate();
		actionTemplate.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		actionTemplate.setAutoEntitlement(true);
		actionTemplate.setExecutionPrice(BigDecimal.ZERO);
		actionTemplate.setLookupEntitlementDate(new GnLookup(LookupConstants.CA_SCHEDULE_EX_DATE));
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_CORPORATE_ACTION));
		renderTemplate("CorporateActionTemplates/detail.html", mode, actionTemplate);
	}

	@Check("administration.corporateActionTemplate")
	public static void edit(Long id, String param) {
		log.debug("edit. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		ScActionTemplate actionTemplate = securityService.getActionTemplate(id);
		String status = actionTemplate.getRecordStatus();
		if (actionTemplate.getTaxObject() != null) {
			actionTemplate.setTaxApply(true);
		}
		if (actionTemplate.getActionTemplateLink() != null) {
			actionTemplate.setCekBoxLinkAnnouncement(true);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_CORPORATE_ACTION));
		render("CorporateActionTemplates/detail.html", mode, actionTemplate, status);
	}

	@Check("administration.corporateActionTemplate")
	public static void save(String mode, ScActionTemplate actionTemplate, String status) {
		log.debug("save. mode: " + mode + " actionTemplate: " + actionTemplate + " status: " + status);

		// validasi
		if (actionTemplate != null) {
			validation.required("Action Code is", actionTemplate.getActionCode());
			validation.required("Description is", actionTemplate.getDescription());
			validation.required("Security Type is", actionTemplate.getSecurityType().getSecurityType());
			validation.required("CA Target Type is", actionTemplate.getTargetType().getLookupId());
			validation.required("Holding Base On is", actionTemplate.getHoldingBase().getLookupId());
			validation.required("Holding Type is", actionTemplate.getHoldingType().getLookupId());
			validation.required("Execution Price is", actionTemplate.getExecutionPrice());
			validation.required("Report/Email Template Price is", actionTemplate.getReportTemplate().getLookupId());
			validation.required("Customer SpecificFund is", actionTemplate.getCustomerFund().getLookupId());
			if (actionTemplate.isTaxApply()) {
				validation.required("Tax Object is", actionTemplate.getTaxObject().getLookupId());
			}
			if (actionTemplate.isCekBoxLinkAnnouncement()) {
				validation.required("Link Announcement is", actionTemplate.getActionTemplateLink().getActionCode());
			}

			if ((actionTemplate.getActionCode() != null) && (actionTemplate.getDescription() != null) && (actionTemplate.getTargetType() != null)) {
				if ((actionTemplate.getSourceTransaction().getTransactionTemplateKey() == null) && (actionTemplate.getTargetTransaction().getTransactionTemplateKey() == null)) {
					validation.required("Transaction Code (Source) or (Target) is", actionTemplate.getSourceTransaction().getTransactionCode() != null ? actionTemplate.getTargetTransaction().getTransactionCode() : actionTemplate.getSourceTransaction().getTransactionCode());
				}
			}

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_CORPORATE_ACTION));
				render("CorporateActionTemplates/detail.html", actionTemplate, mode, status);
			} else {
				Long id = actionTemplate.getActionTemplateKey();
				String json = serializerService.serialize(session.getId(), actionTemplate.getActionTemplateKey(), actionTemplate);
				log.debug("serialized: " + json);
				serializerService.serialize(session.getId(), id, actionTemplate);
				confirming(id, mode, status);
			}
		} else {
			// flash.error("argument.null", actionTemplate);
			entry();
		}
	}

	@Check("administration.corporateActionTemplate")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		boolean confirming = true;
		ScActionTemplate actionTemplate = serializerService.deserialize(session.getId(), id, ScActionTemplate.class);
		// if (actionTemplateMaster.getTargetType()!=null){
		// actionTemplateMaster.setTargetType(generalService.getLookup(actionTemplateMaster.getTargetType().getLookupId()));
		// }
		setTargetType(actionTemplate);
		// logger.debug(">>> Action Template Detail size: " +
		// actionTemplateMaster.getActionTemplateDetails().size());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_CORPORATE_ACTION));
		render("CorporateActionTemplates/detail.html", mode, confirming, actionTemplate, status);

	}

	@Check("administration.corporateActionTemplate")
	private static void setData(ScActionTemplate actionTemplate) {
		log.debug("setData. actionTemplate: " + actionTemplate);

		if (actionTemplate != null) {
			// if (actionDetails != null) {
			// actionTemplateMaster.setActionTemplateDetails(new
			// HashSet<ActionTemplateDetail>(actionDetails));
			// }
		}
	}

	@Check("administration.corporateActionTemplate")
	public static void confirm(String mode, ScActionTemplate actionTemplate, String status) {
		log.debug("confirm. mode: " + mode + " actionTemplate: " + actionTemplate + " status: " + status);

		try {
			if (actionTemplate == null)
				back(null, mode, null, status);
			if (actionTemplate.getAutoEntitlement() == null) {
				actionTemplate.setAutoEntitlement(false);
			}
			securityService.saveActionTemplate(MenuConstants.SC_CORPORATE_ACTION, actionTemplate, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			flash.error("Action Code : ' " + actionTemplate.getActionCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_CORPORATE_ACTION));
			render("CorporateActionTemplates/detail.html", actionTemplate, mode, confirming, status);
		}
	}

	@Check("administration.corporateActionTemplate")
	public static void back(Long id, String mode, ScActionTemplate actionTemplate, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " actionTemplate: " + actionTemplate + " status: " + status);

		// List<ActionTemplateDetail> actionDetails) {
		actionTemplate = serializerService.deserialize(session.getId(), id, ScActionTemplate.class);
		setTargetType(actionTemplate);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_CORPORATE_ACTION));
		renderTemplate("CorporateActionTemplates/detail.html", actionTemplate, mode, status);
	}

	@Check("administration.corporateActionTemplate")
	private static void setTargetType(ScActionTemplate actionTemplate) {
		log.debug("setTargetType. actionTemplate: " + actionTemplate);

		if (actionTemplate.getIsCoupon() == true) {
			actionTemplate.setTargetType(generalService.getLookup("CA_TARGET_TYPE-C"));
		}
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			ScActionTemplate actionTemplate = json.readValue(maintenanceLog.getNewData(), ScActionTemplate.class);
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("CorporateActionTemplates/approval.html", actionTemplate, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			securityService.approveActionTemplate(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			securityService.approveActionTemplate(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}