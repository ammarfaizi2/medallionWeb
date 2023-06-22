package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnUdfMaster;
import com.simian.medallion.vo.SelectItem;

public class UdfMaintenances extends MedallionController {
	private static Logger log = Logger.getLogger(UdfMaintenances.class);

	@Before(unless = { "edit entry save back view", "list" })
	public static void setup(GnUdfMaster udfMaster) {
		log.debug("setup. udfMaster: " + udfMaster);

		List<SelectItem> displayTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DISPLAY_TYPE);
		log.debug("displayTypes = " + displayTypes);
		renderArgs.put("displayTypes", displayTypes);

		List<SelectItem> dataTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._DATA_TYPE);
		renderArgs.put("dataTypes", dataTypes);

		List<SelectItem> customerTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._UDF_CUSTOMER_TYPE);
		renderArgs.put("customerTypes", customerTypes);

		List<SelectItem> lookupGroups = generalService.listLookupGroupsForDropDownAsSelectItem();
		renderArgs.put("lookupGroups", lookupGroups);

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.custodyUdf")
	public static void list(String group) {
		log.debug("list. group: " + group);

		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
			List<GnUdfMaster> udfMasters = generalService.listUdfMastersForCustomer();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_CUSTOMER));
			render(udfMasters, group);
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
			List<GnUdfMaster> udfMasters = generalService.listUdfMastersForTransaction();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_TRANSACTION));
			render(udfMasters, group);
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
			List<GnUdfMaster> udfMasters = generalService.listUdfMastersForSecurity();
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_UDF_SECURITY));
			render(udfMasters, group);
		}
	}

	@Check("administration.custodyUdf")
	public static void view(Long id, String group) {
		log.debug("view. id: " + id + " group: " + group);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnUdfMaster udfMaster = generalService.getUdfMaster(id);
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_CUSTOMER));
			}
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_TRANSACTION));
			}
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_UDF_SECURITY));
			}
			render("UdfMaintenances/detail.html", udfMaster, group, mode);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
		}
	}

	@Check("administration.custodyUdf")
	public static void entry(String group) {
		log.debug("entry. group: " + group);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		GnUdfMaster udfMaster = new GnUdfMaster();
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_CUSTOMER));
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_TRANSACTION));
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_UDF_SECURITY));
		}
		render("UdfMaintenances/detail.html", udfMaster, group, mode);
	}

	@Check("administration.custodyUdf")
	public static void edit(Long id, String group) {
		log.debug("edit. id: " + id + " group: " + group);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		GnUdfMaster udfMaster = generalService.getUdfMaster(id);
		String status = udfMaster.getRecordStatus().trim();
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_CUSTOMER));
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_TRANSACTION));
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_UDF_SECURITY));
		}
		render("UdfMaintenances/detail.html", udfMaster, group, mode, status);
	}

	@Check("administration.custodyUdf")
	public static void save(GnUdfMaster udfMaster, String group, String mode, String status) {
		log.debug("save. udfMaster: " + udfMaster + " group: " + group + " mode: " + mode + " status: " + status);

		if (udfMaster != null) {
			// print out ini jangan di hapus
			log.debug("udfMaster.getLength() length " + udfMaster.getLength());
			if (LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER.equals(udfMaster.getCategory().getLookupId())) {
				validation.required("Customer Type is", udfMaster.getCustomerType().getLookupId());
			}
			validation.required("Field Name is", udfMaster.getFieldName());
			validation.required("Display Type is", udfMaster.getDisplayType().getLookupId());
			if (udfMaster.getDisplayType().getLookupId().equals("DISPLAY_TYPE-1")) {
				validation.required("Data Type is", udfMaster.getDataType().getLookupId());
				validation.required("Length is", udfMaster.getLength());
			} else if (udfMaster.getDisplayType().getLookupId().equals("DISPLAY_TYPE-2")) {
				// validation.required("Data Type is",
				// udfMaster.getDataType().getLookupId());
				validation.required("Lookup Group is", udfMaster.getLookupGroup().getLookupGroup());
			} else if (udfMaster.getDisplayType().getLookupId().equals("DISPLAY_TYPE-3")) {
				validation.required("Data Type is", udfMaster.getDataType().getLookupId());
			} else {
				validation.required("Data Type is", udfMaster.getDataType().getLookupId());
				validation.required("Length is", udfMaster.getLength());
				validation.required("Lookup Group is", udfMaster.getLookupGroup().getLookupGroup());
			}
			validation.required("Label is", udfMaster.getLabel());
			validation.required("Sequence is", udfMaster.getSequence());
			if (validation.hasErrors()) {
				if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_CUSTOMER));
				}
				if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_TRANSACTION));
				}
				if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_UDF_SECURITY));
				}
				render("UdfMaintenances/detail.html", udfMaster, group, mode, status);
			} else {
				Long id = udfMaster.getUdfMasterKey();
				serializerService.serialize(session.getId(), id, udfMaster);
				confirming(id, group, mode, status);
			}
		} else {
			// flash.error("argument.null", udfMaster);
			entry(group);
		}
	}

	@Check("administration.custodyUdf")
	public static void confirming(Long id, String group, String mode, String status) {
		log.debug("confirming. id: " + id + " group: " + group + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		GnUdfMaster udfMaster = serializerService.deserialize(session.getId(), id, GnUdfMaster.class);
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_CUSTOMER));
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_TRANSACTION));
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_UDF_SECURITY));
		}
		render("UdfMaintenances/detail.html", udfMaster, group, mode, status);
	}

	@Check("administration.custodyUdf")
	public static void confirm(String group, String mode, GnUdfMaster udfMaster, String status) {
		log.debug("confirm. group: " + group + " mode: " + mode + " udfMaster: " + udfMaster + " status: " + status);

		try {
			String menu = new String();
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
				menu = MenuConstants.CS_UDF_CUSTOMER;
			}
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
				menu = MenuConstants.CS_UDF_TRANSACTION;
			}
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
				menu = MenuConstants.SC_UDF_SECURITY;
			}
			if (udfMaster == null)
				back(null, group, mode, status);
			generalService.saveUdfMaster(menu, udfMaster, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list(group);
		} catch (MedallionException e) {
			flash.error("Field Name : ' " + udfMaster.getFieldName() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_CUSTOMER));
			}
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_TRANSACTION));
			}
			if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_UDF_SECURITY));
			}
			render("UdfMaintenances/detail.html", udfMaster, group, mode, confirming, status);
		}
	}

	@Check("administration.custodyUdf")
	public static void back(Long id, String group, String mode, String status) {
		log.debug("back. id: " + id + " group: " + group + " mode: " + mode + " status: " + status);

		GnUdfMaster udfMaster = serializerService.deserialize(session.getId(), id, GnUdfMaster.class);
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_CUSTOMER)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_CUSTOMER));
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_TRANSACTION)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_UDF_TRANSACTION));
		}
		if (group.equals(LookupConstants.UDF_MASTER_CATEGORY_SECURITY)) {
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.SC_UDF_SECURITY));
		}
		render("UdfMaintenances/detail.html", udfMaster, group, mode, status);
	}

	public static void approval(String taskId, String group, Long keyId, String operation, Long maintenanceLogKey, String from) {
		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			GnUdfMaster udfMaster = json.readValue(maintenanceLog.getNewData(), GnUdfMaster.class);
			log.debug("from approval >>> group = " + udfMaster.getCategory().getLookupDescription());
			group = udfMaster.getCategory().getLookupId();
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("UdfMaintenances/approval.html", udfMaster, group, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveUdfMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveUdfMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
