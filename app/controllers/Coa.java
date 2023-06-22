package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;
import helpers.serializers.FaCoaMasterListSerializer;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.FaCoaMaster;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.vo.SelectItem;

public class Coa extends MedallionController {
	private static Logger log = Logger.getLogger(Coa.class);

	@Before(only = { "entry", "view", "edit", "save", "confirming", "confirm", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> debitCredit = UIHelper.debitCreditOperators();
		renderArgs.put("debitCredit", debitCredit);

		List<SelectItem> category = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._COA_CATEGORY);
		renderArgs.put("category", category);

		List<SelectItem> subledger = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._COA_SUB_LEDGER);
		renderArgs.put("subledger", subledger);
	}

	public static void list1() {
		log.debug("list1. ");

		render();
	}

	@Check("administration.coa")
	public static void expandAll() {
		log.debug("expandAll. ");

		List<FaCoaMaster> faCoaMasters = fundService.listAllFaCoaMaster();
		String coaExpandAll = null;
		// for (FaCoaMaster coa : faCoaMasters) {
		// if (coa.getCoaParent() != null) {
		// logger.debug("[EXPAND ALL] list updated child = " +
		// coa.getCoaParent().getRecordStatus());
		// if
		// (LookupConstants.__RECORD_STATUS_UPDATED.equals(coa.getCoaParent().getRecordStatus().trim()))
		// {
		// logger.debug("[EXPAND ALL] list updated child = " +
		// coa.getRecordStatus());
		// if
		// (LookupConstants.__RECORD_STATUS_ACTIVE.equals(coa.getRecordStatus().trim()))
		// {
		// coa.setRecordStatus(LookupConstants.__RECORD_STATUS_UPDATED);
		// }
		// }
		// }
		// }
		Long i = 0L;
		for (FaCoaMaster fac : faCoaMasters) {
			i++;
			fac.setRowNumber(i);

		}
		try {
			coaExpandAll = json.writeValueAsString(faCoaMasters);
			log.debug("[EXPAND ALL] coaExpandAll = " + coaExpandAll);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		renderJSON(faCoaMasters, new FaCoaMasterListSerializer());
	}

	@Check("administration.coa")
	public static void expandAllActive() {
		log.debug("expandAllActive. ");

		List<FaCoaMaster> faCoaMasters = fundService.listAllActiveFaCoaMaster();
		String coaExpandAllActive = null;
		log.debug("[EXPAND ALL ACTIVE] coas size = " + faCoaMasters.size());
		// for (FaCoaMaster coa : faCoaMasters) {
		// if (coa.getCoaParent() != null) {
		// logger.debug("[EXPAND ALL ACTIVE] list updated child = " +
		// coa.getCoaParent().getRecordStatus());
		// if
		// (LookupConstants.__RECORD_STATUS_UPDATED.equals(coa.getCoaParent().getRecordStatus().trim()))
		// {
		// logger.debug("[EXPAND ALL ACTIVE] list updated child = " +
		// coa.getRecordStatus());
		// if
		// (LookupConstants.__RECORD_STATUS_ACTIVE.equals(coa.getRecordStatus().trim()))
		// {
		// coa.setRecordStatus(LookupConstants.__RECORD_STATUS_UPDATED);
		// }
		// }
		// }
		// }
		Long i = 0L;
		for (FaCoaMaster fac : faCoaMasters) {
			i++;
			fac.setRowNumber(i);

		}
		try {
			coaExpandAllActive = json.writeValueAsString(faCoaMasters);
			log.debug("[EXPAND ALL ACTIVE] coaExpandAllActive = " + coaExpandAllActive);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		renderJSON(faCoaMasters, new FaCoaMasterListSerializer());
	}

	@Check("administration.coa")
	public static void get(Long parent) {
		log.debug("get. parent: " + parent);

		List<FaCoaMaster> faCoaMasters = fundService.listFaCoaMaster(parent);
		Long i = 0L;
		for (FaCoaMaster fac : faCoaMasters) {
			i++;
			fac.setRowNumber(i);

		}
		renderJSON(faCoaMasters, new FaCoaMasterListSerializer());
	}

	@Check("administration.coa")
	public static void getActive(Long parent) {
		log.debug("getActive. parent: " + parent);

		List<FaCoaMaster> faCoaMasters = fundService.listFaCoaMasterActive(parent);

		Long i = 0L;
		for (FaCoaMaster fac : faCoaMasters) {
			i++;
			fac.setRowNumber(i);

		}
		renderJSON(faCoaMasters, new FaCoaMasterListSerializer());
	}

	@Check("administration.coa")
	public static void list() {
		log.debug("list. ");

		// List<FaCoaMaster> faCoaMasters = fundService.listFaCoaMaster();
		List<FaCoaMaster> faCoaMasters = fundService.listFaCoaMaster();
		log.debug("[LIST] size faCoaMaster = " + faCoaMasters.size());
		List<SelectItem> viewAs = UIHelper.viewAsOperators();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_COA_MASTER));
		render(faCoaMasters, viewAs);
	}

	@Check("administration.coa")
	public static void entry(Long parentId) {
		log.debug("entry. parentId: " + parentId);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		log.debug("[ENTRY] parentId >>>" + parentId);
		FaCoaMaster faCoaMaster = new FaCoaMaster();
		faCoaMaster.setNature(LookupConstants.__COA_DEBIT);
		faCoaMaster.setIsParent(false);
		// faCoaMaster.setIsActive(true);
		FaCoaMaster newParent = null;

		if (parentId != null) {
			newParent = fundService.getFaCoaMasterByParent(parentId);
			faCoaMaster.setCoaParent(newParent);
			faCoaMaster.setCategory(newParent.getCategory());
			faCoaMaster.setNature(newParent.getNature());
			faCoaMaster.setIsParent(true);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_COA_MASTER));
		render("Coa/detail.html", faCoaMaster, mode);

	}

	@Check("administration.coa")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaCoaMaster faCoaMaster = fundService.getFaCoaMaster(id);
		if (faCoaMaster.getCoaParent() != null) {
			faCoaMaster.setIsParent(true);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_COA_MASTER));
		render("Coa/detail.html", faCoaMaster, mode);
	}

	@Check("administration.coa")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaCoaMaster faCoaMaster = fundService.getFaCoaMaster(id);
		faCoaMaster.setIsParent(false);
		if (faCoaMaster.getCoaParent() != null) {
			// FaCoaMaster newParent =
			// fundService.getFaCoaMasterByParent(faCoaMaster.getCoaParent().getCoaMasterKey());
			// faCoaMaster.setCoaParent(newParent);
			faCoaMaster.setIsParent(true);
		}
		log.debug("[EDIT] COA coaClass = " + faCoaMaster.getCategory().getLookupId());
		String status = faCoaMaster.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_COA_MASTER));
		render("Coa/detail.html", mode, faCoaMaster, status);
	}

	@Check("administration.coa")
	public static void save(FaCoaMaster faCoaMaster, String mode, String status) {
		log.debug("save. faCoaMaster: " + faCoaMaster + " mode: " + mode + " status: " + status);

		if (faCoaMaster != null) {
			log.debug("[SAVE] COA Parent coaCode = " + faCoaMaster.getCoaParent().getCoaCode());
			log.debug("[SAVE] COA Parent coaClass = " + faCoaMaster.getCategory().getLookupId());
			if (faCoaMaster.getIsParent() != null) {
				if (faCoaMaster.getIsParent() == true) {
					validation.required("Parent is", faCoaMaster.getCoaParent().getCoaCode());
				}
			}
			validation.required("Account No is", faCoaMaster.getCoaCode());
			validation.required("Description is", faCoaMaster.getDescription());
			validation.required("Class is", faCoaMaster.getCategory().getLookupId());
			// validation.required("Currency is",
			// faCoaMaster.getCurrency().getCurrencyCode());
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_COA_MASTER));
				int flek = 1;
				render("Coa/detail.html", faCoaMaster, mode, status, flek);
			} else {
				Long id = faCoaMaster.getCoaMasterKey();
				serializerService.serialize(session.getId(), faCoaMaster.getCoaMasterKey(), faCoaMaster);
				confirming(id, mode, status);
			}
		} else {
			// flash.error(ExceptionConstants.PARAMETER_NULL, faCoaMaster);
			entry(null);
		}
	}

	@Check("administration.coa")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		renderArgs.put("confirming", true);
		FaCoaMaster faCoaMaster = serializerService.deserialize(session.getId(), id, FaCoaMaster.class);
		log.debug("[CONFIRMING] COA Parent coaCode = " + faCoaMaster.getCoaParent().getCoaCode());
		log.debug("[CONFIRMING] COA Parent coaClass = " + faCoaMaster.getCategory().getLookupId());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_COA_MASTER));
		render("Coa/detail.html", faCoaMaster, mode, status);
	}

	@Check("administration.coa")
	public static void confirm(FaCoaMaster faCoaMaster, String mode, Long maintenanceLogKey, String status) {
		log.debug("confirm. faCoaMaster: " + faCoaMaster + " mode: " + mode + " maintenanceLogKey: " + maintenanceLogKey + " status: " + status);

		try {
			if (faCoaMaster == null)
				back(null, mode, null, status);
			log.debug("[CONFIRM] category = " + faCoaMaster.getCategory().getLookupId());
			log.debug("[CONFIRM] coaParent = " + faCoaMaster.getCoaParent().getCoaMasterKey());
			if (faCoaMaster.getCoaParent().getCoaMasterKey() != null) {
				Boolean error = false;
				Long parentKey = faCoaMaster.getCoaParent().getCoaMasterKey();
				for (int i = 0; i < 4; i++) {
					FaCoaMaster coa = fundService.getFaCoaMaster(parentKey);
					if (LookupConstants.__RECORD_STATUS_UPDATED.equals(coa.getRecordStatus().trim())) {
						error = true;
					}
					if (coa.getCoaParent() != null) {
						parentKey = coa.getCoaParent().getCoaMasterKey();
					}
				}
				if (error == true) {
					flash.error("COA Parent : '" + faCoaMaster.getCoaParent().getCoaCode() + "' is being updated, can not save!");
					boolean confirming = true;
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_COA_MASTER));
					render("Coa/detail.html", faCoaMaster, mode, confirming, status);
				}
			}
			fundService.saveFaCoaMaster(MenuConstants.FA_COA_MASTER, faCoaMaster, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			// flash.error("Duplicate Error! Code : ' "+faCoaMaster.getCoaCode()+" ' Already Exist",
			// faCoaMaster.getCoaCode());
			flash.error("Account No : ' " + faCoaMaster.getCoaCode() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_COA_MASTER));
			render("Coa/detail.html", faCoaMaster, mode, confirming, status);
		}
	}

	@Check("administration.coa")
	public static void back(Long id, String mode, FaCoaMaster faCoaMaster, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " faCoaMaster: " + faCoaMaster + " status: " + status);

		faCoaMaster = serializerService.deserialize(session.getId(), id, FaCoaMaster.class);
		render("Coa/detail.html", faCoaMaster, mode, status);
	}

	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			FaCoaMaster faCoaMaster = json.readValue(maintenanceLog.getNewData(), FaCoaMaster.class);
			if (faCoaMaster.getCoaParent() != null) {
				faCoaMaster.setIsParent(true);
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("Coa/approval.html", faCoaMaster, mode, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFaCoaMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			fundService.approveFaCoaMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void checkLastChild(Long coaMasterKey) {
		log.debug("checkLastChild. coaMasterKey: " + coaMasterKey);
	}
}