package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.i18n.Messages;
import play.mvc.Before;

import com.google.gson.JsonParseException;
import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.FaFeeDetail;
import com.simian.medallion.model.FaFeeMaster;
import com.simian.medallion.model.FaFeeTier;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.Paging;
import com.simian.medallion.model.helper.Helper;
import com.simian.medallion.service.TransactionHelper;
import com.simian.medallion.vo.SelectItem;

public class FeeAccruals extends MedallionController {
	protected static Logger log = Logger.getLogger(FeeAccruals.class);

	@Before(only = { "entry", "view", "edit", "confirming", "back", "approval" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> yearBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._YEAR_BASE);
		renderArgs.put("yearBase", yearBase);

		List<SelectItem> accrualBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._ACCRUAL_BASE);
		renderArgs.put("accrualBase", accrualBase);

		List<SelectItem> valueType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_VALUE);
		renderArgs.put("valueType", valueType);

		List<SelectItem> tieringType = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_CALCULATION);
		renderArgs.put("tieringType", tieringType);
		renderArgs.put("tierTypeSingleValue", LookupConstants.CHARGE_CALCULATION_SINGLE_VALUE);
		
		List<GnLookup> lstAccrualBaseOn = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._FA_CALCULATION_BASE);
		List<SelectItem> previousAccrual = new ArrayList<SelectItem>();
		for(GnLookup lookup : lstAccrualBaseOn){
			if(lookup.getLookupId().equals(LookupConstants.FA_CALCULATION_BASE_PREVIOUS_NAB)) previousAccrual.add(new SelectItem(LookupConstants.FA_CALCULATION_BASE_PREVIOUS_NAB, lookup.getLookupDescription()));
			if(lookup.getLookupId().equals(LookupConstants.FA_CALCULATION_BASE_TOTAL_UNIT)) previousAccrual.add(new SelectItem(LookupConstants.FA_CALCULATION_BASE_TOTAL_UNIT, lookup.getLookupDescription()));
			if(lookup.getLookupId().equals(LookupConstants.FA_CALCULATION_BASE_TOTAL_SPECIFIC_COA)) previousAccrual.add(new SelectItem(LookupConstants.FA_CALCULATION_BASE_TOTAL_SPECIFIC_COA, lookup.getLookupDescription()));
			
		}
		renderArgs.put("previousAccrual", previousAccrual);
	}

	@Check("administration.feeAccrual")
	public static void list() {
		log.debug("list. ");

		List<FaFeeMaster> faFeeMasters = fundService.listFeeAccrual();
		render(faFeeMasters);
	}

	@Check("administration.feeAccrual")
	public static void paging(Paging page) {
		log.debug("paging. page: " + page);

		page.addParams(Helper.searchAll("(ffm.feeKey||fm.fundCode||ffm.feeCode||ffm.description||ffm.recordStatus||ffm.isActive)"), Paging.LIKE, UIHelper.withOperator(page.getsSearch(), 1));

		page = fundService.pagingFeeAccuals(page);
		renderJSON(page);
	}

	@Check("administration.feeAccrual")
	public static void entry() {
		log.debug("entry. ");

		renderArgs.put("confirming", false);
		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		FaFeeMaster faFeeMaster = new FaFeeMaster();
		faFeeMaster.setIsActive(false);
		BigDecimal amount = new BigDecimal(0);
		String master = null;
		try {
			master = json.writeValueAsString(faFeeMaster);
		} catch (JsonGenerationException e) {
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FEE_ACCRUAL));
		render("FeeAccruals/detail.html", master, mode, amount);
	}

	@Check("administration.feeAccrual")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		FaFeeMaster faFeeMaster = null;
		String master = null;
		BigDecimal amount = new BigDecimal(0);
		if (id != null) {
			faFeeMaster = fundService.getFeeAccrual(id);
			if (faFeeMaster != null) {
				try {
					JsonHelper json = new JsonHelper().getFeeAccrualSerializer();
					master = json.serialize(faFeeMaster);
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.error(e.getMessage(), e);
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FEE_ACCRUAL));
		render("FeeAccruals/detail.html", master, mode, id, amount);
	}

	@Check("administration.feeAccrual")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		FaFeeMaster faFeeMaster = null;
		//faFeeMastr = new FaFeeMaster();
		String master = null;
		String status = null;
		BigDecimal amount = new BigDecimal(0);
		if (id != null) {
			faFeeMaster = fundService.getFeeAccrual(id);
			//faFeeMastr = fundService.getFeeAccrual(id);
			status = faFeeMaster.getRecordStatus();
			if (faFeeMaster != null) {
				try {
					JsonHelper json = new JsonHelper().getFeeAccrualSerializer();
					master = json.serialize(faFeeMaster);
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.error(e.getMessage(), e);
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FEE_ACCRUAL));
		render("FeeAccruals/detail.html", master, mode, id, amount, status, faFeeMaster);
	}

	@Check("administration.feeAccrual")
	public static void save(String body, String mode, Long id, BigDecimal amount, String status) {
		log.debug("save. body: " + body + " mode: " + mode + " id: " + id + " amount: " + amount + " status: " + status);

		try {
			log.debug("MODE in SAVE" + mode);
			FaFeeMaster feeMasters = json.readValue(body, FaFeeMaster.class);

			log.debug("STATUS >> " + feeMasters.getRecordStatus());
			if (feeMasters != null) {
				if (feeMasters.getIsActive() == true) {
					feeMasters.setIsActive(true);
				} else {
					feeMasters.setIsActive(false);
				}
				if (feeMasters.getPublishedNav() != null) {
					if (feeMasters.getPublishedNav() == true) {
						feeMasters.setPublishedNav(true);
					} else {
						feeMasters.setPublishedNav(false);
					}
				} else {
					feeMasters.setPublishedNav(false);
				}
				if (feeMasters.getFaFeeDetails() != null) {
					Set<FaFeeDetail> faFeeDetails = feeMasters.getFaFeeDetails();
					// for(int i=0; i<faFeeDetails.size(); i++){
					int i = 1;
					for (FaFeeDetail faFeeDetail : faFeeDetails) {
						if (feeMasters.getFeeKey() != null) {
							faFeeDetail.getId().setFeeKey(feeMasters.getFeeKey());
						}
						faFeeDetail.getId().setRowNumber(i++);

						if ((feeMasters.getFaCalculationBase().getLookupId().equals(LookupConstants.FA_CALCULATION_BASE_DAILY_AMOUNT)) || (feeMasters.getFaCalculationBase().getLookupId().equals(LookupConstants.FA_CALCULATION_BASE_TOTAL_AMOUNT))) {
							faFeeDetail.getTieringType().setLookupId(LookupConstants.CHARGE_CALCULATION_SINGLE_VALUE);
							faFeeDetail.getTieringType().setLookupDescription("SINGLE VALUE");
							faFeeDetail.getValueType().setLookupId(LookupConstants.CHARGE_VALUE_FIX_AMOUNT);
						}
						if (faFeeDetail.getFaFeeTiers() != null) {
							Set<FaFeeTier> faFeeTiers = faFeeDetail.getFaFeeTiers();
							// int j = 1;
							for (FaFeeTier faFeeTier : faFeeTiers) {
								if (feeMasters.getFeeKey() != null) {
									faFeeTier.getId().setFeeKey(feeMasters.getFeeKey());
								}
								faFeeTier.getId().setRowNumber(faFeeDetail.getId().getRowNumber());
								// faFeeTier.getId().setTierSequence(j);
								// j++;
								faFeeDetail.getFaFeeTiers().add(faFeeTier);
							}

						}
						feeMasters.getFaFeeDetails().add(faFeeDetail);
					}
					// }
				}
				// validation.required("Fund Code is",
				// feeMasters.getFaMaster().getFundCode());
				// if (validation.hasErrors()){
				// flash.error("Fund Code : '"+
				// faFeeMaster.getFaMaster().getFundCode()+"' with Fee Code : '"+faFeeMaster.getFeeCode()+"' "
				// + ex.getMessage());
				// String error = "error";
				// renderText(error);
				// }
				String tes = serializerService.serialize(session.getId(), id, feeMasters);
				log.debug("check : " + tes);
			}
		} catch (JsonParseException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.debug("json.serialize");
			log.error(e.getMessage(), e);
		}
	}

	@Check("administration.feeAccrual")
	public static void confirming(Long id, String mode, BigDecimal amount, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " amount: " + amount + " status: " + status);

		renderArgs.put("confirming", true);
		FaFeeMaster faFeeMaster = serializerService.deserialize(session.getId(), id, FaFeeMaster.class);
		String master = null;
		try {
			master = json.writeValueAsString(faFeeMaster);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FEE_ACCRUAL));
		render("FeeAccruals/detail.html", master, faFeeMaster, mode, id, amount, status);
	}

	@Check("administration.feeAccrual")
	public static void confirm(Long id, String mode, FaFeeMaster faFeeMaster, String status) {
		log.debug("confirm. id: " + id + " mode: " + mode + " faFeeMaster: " + faFeeMaster + " status: " + status);

		faFeeMaster = serializerService.deserialize(session.getId(), id, FaFeeMaster.class);

		for (FaFeeDetail detail : faFeeMaster.getFaFeeDetails()) {
			for (FaFeeTier faFeeTier : detail.getFaFeeTiers()) {
				log.debug("tierr a " + faFeeTier.getId().getFeeKey() + " : " + faFeeTier.getId().getRowNumber() + " : " + faFeeTier.getId().getTierSequence() + " : " + faFeeTier.getTierMaximumRange());
			}
		}

		if (faFeeMaster.getFeeKey() != null) {
			mode = UIConstants.DISPLAY_MODE_EDIT;
		} else {
			mode = UIConstants.DISPLAY_MODE_ENTRY;
		}
		log.debug("mode--------------->>>" + mode);

		log.debug("Size Confirm : " + faFeeMaster);
		if (faFeeMaster.getFaFeeDetails() != null) {
			for (FaFeeDetail feeDetail : faFeeMaster.getFaFeeDetails()) {
				if (feeDetail.getFaFeeTiers() != null) {
					feeDetail.setFaFeeTiers(new HashSet<FaFeeTier>(feeDetail.getFaFeeTiers()));
				}
			}
			faFeeMaster.setFaFeeDetails(new HashSet<FaFeeDetail>(faFeeMaster.getFaFeeDetails()));
		}
		if (faFeeMaster.getPaymentTransaction().getTransactionMasterKey() == null) {
			faFeeMaster.setPaymentTransaction(null);
		}

		List<FaFeeMaster> faFeeMasters = fundService.listFeeAccrual();

		for (FaFeeMaster feeMasterInTable : faFeeMasters) {

			if (!(UIConstants.DISPLAY_MODE_EDIT.equals(mode))) {
				if (faFeeMaster.getFeeKey() == null) {
					log.debug("validate DUPLICATE");
					if (feeMasterInTable.getFaMaster().getFundCode().equals(faFeeMaster.getFaMaster().getFundCode())) {
						if (feeMasterInTable.getFeeCode().equals(faFeeMaster.getFeeCode())) {

							flash.error("Fund Code : '" + faFeeMaster.getFaMaster().getFundCode() + "' with Fee Code : '" + faFeeMaster.getFeeCode() + "' " + ExceptionConstants.DATA_DUPLICATE);
							flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FEE_ACCRUAL));
							String error = "error";
							renderText(error);
						}
					}
				}
			}
		}
		if(faFeeMaster.getFaCoaMaster() == null || (faFeeMaster.getFaCoaMaster() != null && faFeeMaster.getFaCoaMaster().getCoaMasterKey() == null)){
			faFeeMaster.setFaCoaMaster(null);
		}
		try {
			fundService.saveFeeAccrual(MenuConstants.FA_FEE_ACCRUAL, faFeeMaster, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
		} catch (MedallionException ex) {
			log.debug("<<< ERROR >>>");
			flash.error("Fund Code : '" + faFeeMaster.getFaMaster().getFundCode() + "' with Fee Code : '" + faFeeMaster.getFeeCode() + "' " + Messages.get(ex.getMessage()));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FEE_ACCRUAL));
			String error = "error";
			renderText(error);
		}
	}

	@Check("administration.feeAccrual")
	public static void back(Long id, String mode, BigDecimal amount, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " amount: " + amount + " status: " + status);

		FaFeeMaster feeMaster = serializerService.deserialize(session.getId(), id, FaFeeMaster.class);
		status = feeMaster.getRecordStatus();
		log.debug("Status >>> " + status);
		String master = null;
		try {
			master = json.writeValueAsString(feeMaster);
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		log.debug("master back >>" + master);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.FA_FEE_ACCRUAL));
		render("FeeAccruals/detail.html", master, mode, id, amount, status);
	}

	// @Check("administration.feeAccrual")
	public static void calculateDay(Date startDate, Date endDate, int base) {
		log.debug("calculateDay. startDate: " + startDate + " endDate: " + endDate + " base: " + base);

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DATE, -1);
		int days = TransactionHelper.calculateAccruedDays(cal.getTime(), endDate, base);
		// days = days + 1;
		renderText(days);
	}

	// @Check("administration.feeAccrual")
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			boolean approval = true;
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			String master = null;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			FaFeeMaster faFeeMaster = json.readValue(maintenanceLog.getNewData(), FaFeeMaster.class);
			try {
				master = json.writeValueAsString(faFeeMaster);
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
			render("FeeAccruals/approval.html", master, faFeeMaster, mode, taskId, operation, maintenanceLogKey, from, approval);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// @Check("administration.feeAccrual")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFeeAccrual(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("administration.feeAccrual")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			fundService.approveFeeAccrual(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}
