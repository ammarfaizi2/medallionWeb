package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
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

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnAdjustmentDetail;
import com.simian.medallion.model.GnAdjustmentMaster;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.vo.SelectItem;

import extensions.StatusExtension;

@With(Secure.class)
public class AdjustmentMaintenance extends MedallionController {
	private static Logger log = Logger.getLogger(AdjustmentMaintenance.class);

	@Before(unless = { "list" })
	public static void setup1() {
		log.debug("setup1. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	// @Before(unless = {"list", "confirm", "save" })
	@Before(only = { "edit", "confirming", "confirm", "approval", "back" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operatorType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._OPERATOR_FILTER);
		List<SelectItem> removeOpt = new ArrayList<SelectItem>();
		for (SelectItem operType : operatorType) {
			if (operType.text.contains("INN-")) {
				removeOpt.add(operType);
			} else {
				operType.text = getSymbol(operType.text);
			}
		}
		operatorType.removeAll(removeOpt);
		renderArgs.put("operatorType", operatorType);

		List<SelectItem> currencyOpt = generalService.listCurrenciesAsSelectItem();
		renderArgs.put("currencyOpt", currencyOpt);
	}

	@Check("maintenance.adjustmentMaintenance")
	public static void list() {
		log.debug("list. ");

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ADJUSTMENT_MAINTENANCE));
		List<GnAdjustmentMaster> listAdjustment = generalService.getListAdjustmentMaster();
		for (GnAdjustmentMaster adj : listAdjustment)
			adj.setRecordStatus(StatusExtension.decodeStatus(adj.getRecordStatus()));
		log.debug("listAdjustment " + listAdjustment.size());
		render("AdjustmentMaintenance/list.html", listAdjustment);
	}

	@Check("maintenance.adjustmentMaintenance")
	public static void entry() {
		log.debug("entry. ");

		render("AdjustmentMaintenance/list.html");
	}

	@Check("maintenance.adjustmentMaintenance")
	public static void edit(String id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;

		GnAdjustmentMaster adjustmentMaster = generalService.getAdjustmentMasterByCode(id);
		List<GnAdjustmentDetail> adjustmentDetails = generalService.getListAdjustmentDetailByCode(id);
		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(adjustmentDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		if (adjustmentMaster.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_NEW) || adjustmentMaster.getRecordStatus().trim().equals(LookupConstants.__RECORD_STATUS_UPDATED)) {
			mode = UIConstants.DISPLAY_MODE_VIEW;
		}
		log.debug("**********************************************************8trans = " + transactionDetails);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ADJUSTMENT_MAINTENANCE));
		render("AdjustmentMaintenance/edit.html", adjustmentMaster, transactionDetails, mode);
	}

	@Check("maintenance.adjustmentMaintenance")
	public static void save(GnAdjustmentMaster gnAdjustmentMaster, GnAdjustmentDetail[] gnAdjustmentDetails, String mode) {
		log.debug("save. gnAdjustmentMaster: " + gnAdjustmentMaster + " gnAdjustmentDetails: " + gnAdjustmentDetails + " mode: " + mode);

		if (gnAdjustmentMaster != null) {
			if (gnAdjustmentDetails != null) {
				for (GnAdjustmentDetail gnAdjustmentDetail : gnAdjustmentDetails) {
					gnAdjustmentMaster.getGnAdjustmentDetails().add(gnAdjustmentDetail);
				}
			}
			serializerService.serialize(session.getId(), gnAdjustmentMaster.getCode(), gnAdjustmentMaster);
			confirming(gnAdjustmentMaster.getCode(), mode);
		}
	}

	@Check("maintenance.adjustmentMaintenance")
	public static void confirming(String id, String mode) {
		log.debug("confirming. id: " + id + " mode: " + mode);

		boolean confirming = true;
		GnAdjustmentMaster adjustmentMaster = serializerService.deserialize(session.getId(), id, GnAdjustmentMaster.class);

		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(adjustmentMaster.getGnAdjustmentDetails());
			log.debug("**********************************************************8trans = " + transactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ADJUSTMENT_MAINTENANCE));
		render("AdjustmentMaintenance/edit.html", adjustmentMaster, transactionDetails, mode, confirming);
	}

	@Check("maintenance.adjustmentMaintenance")
	public static void confirm(GnAdjustmentMaster gnAdjustmentMaster, GnAdjustmentDetail[] gnAdjustmentDetails) {
		log.debug("confirm. gnAdjustmentMaster: " + gnAdjustmentMaster + " gnAdjustmentDetails: " + gnAdjustmentDetails);

		String mode = UIConstants.DISPLAY_MODE_EDIT;

		boolean confirming = true;
		if (gnAdjustmentMaster == null) {
			back(null, mode);
		}

		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(gnAdjustmentMaster.getGnAdjustmentDetails());
			log.debug(">>> transactionDetails=" + transactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		try {

			if (gnAdjustmentDetails != null) {
				for (GnAdjustmentDetail gnAdjustmentDetail : gnAdjustmentDetails) {
					if (gnAdjustmentDetail != null) {
						gnAdjustmentDetail.setGnAdjustmentMaster(gnAdjustmentMaster);
						gnAdjustmentMaster.getGnAdjustmentDetails().add(gnAdjustmentDetail);
					}
				}
			}

			GnAdjustmentMaster adjm = generalService.createAdjustmentMaintenance(MenuConstants.CS_ADJUSTMENT_MAINTENANCE, gnAdjustmentMaster, session.get(UIConstants.SESSION_USERNAME), session.get(UIConstants.SESSION_USER_KEY));
			log.debug("LENGTH DETAILNYA ADALAH = " + adjm.getGnAdjustmentDetails().size() + " --- " + adjm.getCode());
			if (adjm.getCode() != null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("status", "success");
				// result.put("message",
				// Messages.get("adjustment.maintenance.successful",
				// adjm.getCode()));
				renderJSON(result);
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ADJUSTMENT_MAINTENANCE));
				render("AdjustmentMaintenance/edit.html", gnAdjustmentMaster, transactionDetails, mode, confirming);
			}
		} catch (MedallionException ex) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			List<String> params = new ArrayList<String>();
			if (ex != null && ex.getParams() != null) {
				for (Object param : ex.getParams()) {
					params.add(Messages.get(param));
				}
				result.put("error", Messages.get("error." + ex.getErrorCode(), params.toArray()));
			} else {
				result.put("error", Messages.get(ex.getErrorCode()));
			}
			renderJSON(result);
		}catch (Exception e) {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("status", "error");
			result.put("error",e.getMessage());
			renderJSON(result);
		}

		render("AdjustmentMaintenance/list.html");
	}

	public static void approval(String taskId, String keyId, String from, String operation, Long maintenanceLogKey) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " from: " + from + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			JsonHelper jsonGnAdjustmentMaster = new JsonHelper().getGnAdjustmentMasterSerializer();
			JsonHelper jsonGnAdjustmentDetail = new JsonHelper().getGnAdjustmentDetailSerializer();
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);

			GnAdjustmentMaster adjustmentMaster = jsonGnAdjustmentMaster.deserialize(maintenanceLog.getNewData(), GnAdjustmentMaster.class);

			List<GnAdjustmentDetail> lstGnAdjustmentDetails = null;

			if ((adjustmentMaster.getGnAdjustmentDetails() != null) && (adjustmentMaster.getGnAdjustmentDetails().size() > 0)) {
				lstGnAdjustmentDetails = new ArrayList<GnAdjustmentDetail>(adjustmentMaster.getGnAdjustmentDetails());
				adjustmentMaster.getGnAdjustmentDetails().clear();

				for (GnAdjustmentDetail gnAdjustmentDetail : lstGnAdjustmentDetails) {
					gnAdjustmentDetail.setGnAdjustmentMaster(adjustmentMaster);
				}

				adjustmentMaster.getGnAdjustmentDetails().addAll(lstGnAdjustmentDetails);
			}

			String transactionDetails = null;
			try {
				transactionDetails = jsonGnAdjustmentDetail.serialize(adjustmentMaster.getGnAdjustmentDetails());
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ADJUSTMENT_MAINTENANCE));
			render("AdjustmentMaintenance/approval.html", adjustmentMaster, taskId, mode, transactionDetails, from, operation, maintenanceLogKey);
		} catch (Exception e) {
			log.error("Adjustment Maintenance Approval", e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			GnAdjustmentMaster gnAdjustmentMaster = generalService.approveAdjustmentMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess(Messages.get("adjustment.maintenance.approved", gnAdjustmentMaster.getCode())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void reject(String taskId, Long maintenanceLogKey, String operation, String code) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation + " code: " + code);

		try {
			GnAdjustmentMaster gnAdjustmentMaster = new GnAdjustmentMaster();
			gnAdjustmentMaster = generalService.approveAdjustmentMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess(Messages.get("adjustment.maintenance.rejected", gnAdjustmentMaster == null ? code : gnAdjustmentMaster.getCode())));
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	@Check("maintenance.adjustmentMaintenance")
	public static void view() {
		log.debug("view. ");

		render("AdjustmentMaintenance/list.html");
	}

	@Check("maintenance.adjustmentMaintenance")
	public static void back(String id, String mode) {
		log.debug("back. id: " + id + " mode: " + mode);

		GnAdjustmentMaster adjustmentMaster = serializerService.deserialize(session.getId(), id, GnAdjustmentMaster.class);
		log.debug("gnAdjustmentMaster.getCode " + adjustmentMaster.getCode());
		String transactionDetails = null;
		try {
			transactionDetails = json.writeValueAsString(adjustmentMaster.getGnAdjustmentDetails());
			log.debug(">>> transactionDetails=" + transactionDetails);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_ADJUSTMENT_MAINTENANCE));
		render("AdjustmentMaintenance/edit.html", adjustmentMaster, mode, transactionDetails);
	}

	private static String getSymbol(String text) {
		log.debug("getSymbol. text: " + text);

		String symbol = "";

		if (text.contains("EQ-")) {
			symbol = "=";
		} else if (text.contains("GT-")) {
			symbol = ">";
		} else if (text.contains("GTE-")) {
			symbol = ">=";
		} else if (text.contains("IN-")) {
			symbol = "<>";
		} else if (text.contains("LT-")) {
			symbol = "<";
		} else if (text.contains("LTE-")) {
			symbol = "<=";
		} else if (text.contains("INN-")) {
			symbol = "";
		}

		log.debug("symbol = " + symbol);
		return symbol;
	}
}
