package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.CsChargeMaster;
import com.simian.medallion.model.CsChargeTier;
import com.simian.medallion.model.CsChargeTierId;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.GnOrganization;
import com.simian.medallion.model.comparators.ChargeTierRangeComparator;
import com.simian.medallion.vo.SelectItem;

public class ChargeMasters extends MedallionController {
	private static Logger log = Logger.getLogger(ChargeMasters.class);

	@Before(only = { "view", "entry", "edit", "save", "confirming", "confirm", "back", "approval", "viewForChargeOverride" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> frequency = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._FREQUENCY);
		renderArgs.put("frequency", frequency);

		List<SelectItem> chargeCategory = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_CATEGORY);
		renderArgs.put("chargeCategory", chargeCategory);

		renderArgs.put("chargeCategoryEntitlement", LookupConstants.CHARGE_CATEGORY_ENTITLEMENT);
		renderArgs.put("chargeCategorySafekeepingFee", LookupConstants.CHARGE_CATEGORY_SAFEKEEPING_FEE);

		List<SelectItem> chargesCalculation = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_CALCULATION);
		renderArgs.put("chargesCalculation", chargesCalculation);

		List<SelectItem> chargesTypes = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_TYPE);
		renderArgs.put("chargesTypes", chargesTypes);

		List<SelectItem> chargesValues = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_VALUE);
		renderArgs.put("chargesValues", chargesValues);

		List<SelectItem> chargeFrequency = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._CHARGE_FREQUENCY);
		renderArgs.put("chargeFrequency", chargeFrequency);

		renderArgs.put("chargeFrequencyMonthly", LookupConstants.CHARGE_FREQUENCY_MONTHLY);

		List<SelectItem> chargeBaseCalculation = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._BASE_CALCULATION);
		renderArgs.put("chargeBaseCalculation", chargeBaseCalculation);

		List<SelectItem> methodSkfee = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._METHOD_SKFEE);
		renderArgs.put("methodSkfee", methodSkfee);

		List<SelectItem> priceUsed = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._PRICE_USED);
		renderArgs.put("priceUsed", priceUsed);

		List<SelectItem> invoiceCharge = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._INVOICE_CHARGE);
		renderArgs.put("invoiceCharge", invoiceCharge);

		List<SelectItem> yearBase = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._YEAR_BASE);
		renderArgs.put("yearBase", yearBase);

		renderArgs.put("noTaxValue", LookupConstants._TAX_NOT);
	}

	@Check("administration.chargeMaster")
	public static void list() {
		log.debug("list. ");

		List<CsChargeMaster> masters = generalService.listChargeMasters(UIConstants.SIMIAN_BANK_ID);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_MASTER));
		render(masters);
	}

	@Check("administration.chargeMaster")
	public static void view(Long id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsChargeMaster master = null;
		String detailTiers = null;
		List<Integer> periods = null;
		if (id != null) {
			master = generalService.getChargeMaster(id);
			master.fillPeriods(master.getPeriodArray());
			periods = master.getPeriods();

			if (master != null) {
				try {
					JsonHelper json = new JsonHelper().getChargeMasterSerializer();
					detailTiers = json.serialize(master.getChargeTiers());
				} catch (JsonGenerationException ex) {
					log.debug("json.serialize");
				} catch (JsonMappingException ex) {
					log.debug("json.serialize");
				} catch (IOException ex) {
					log.debug("json.serialize");
				}
			}
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_MASTER));
		render("ChargeMasters/detail.html", master, mode, detailTiers, periods);
	}

	@Check("administration.chargeMaster")
	public static void entry(String param) {
		log.debug("entry. param: " + param);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		CsChargeMaster master = new CsChargeMaster();
		Set<CsChargeTier> tiers = master.getChargeTiers();
		master.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
		master.setIsChecked(true);
		String detailTiers = null;
		try {
			detailTiers = json.writeValueAsString(master.getChargeTiers());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		Long keyHelp = null;
		if (master.getChargeKey() == null) {
			keyHelp = null;
		} else {
			keyHelp = master.getChargeKey();
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_MASTER));
		render("ChargeMasters/detail.html", master, tiers, keyHelp, mode, detailTiers, param);
	}

	@Check("administration.chargeMaster")
	public static void edit(Long id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		CsChargeMaster master = generalService.getChargeMaster(id);
		master.fillPeriods(master.getPeriodArray());
		List<Integer> periods = master.getPeriods();
		String detailTiers = null;
		if (id != null) {
			if (master != null) {
				try {
					JsonHelper json = new JsonHelper().getChargeMasterSerializer();
					detailTiers = json.serialize(master.getChargeTiers());
				} catch (JsonGenerationException e) {
					log.debug("json.serialize");
				} catch (JsonMappingException e) {
					log.debug("json.serialize");
				} catch (IOException e) {
					log.debug("json.serialize");
				}
			}
		}
		Long keyHelp = null;
		if (master.getChargeKey() == null) {
			keyHelp = null;
		} else {
			keyHelp = master.getChargeKey();
		}
		String status = master.getRecordStatus();
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_MASTER));
		render("ChargeMasters/detail.html", master, keyHelp, periods, detailTiers, mode, status);
	}

	@Check("administration.chargeMaster")
	public static void save(String mode, CsChargeMaster master, List<CsChargeTier> tiers, List<Integer> periods, String status) {
		log.debug("save. mode: " + mode + " master: " + master + " tiers: " + tiers + " periods: " + periods + " status: " + status);

		if (master != null) {
			String detailTiers = null;
			try {
				if (tiers == null) {
					tiers = new ArrayList<CsChargeTier>();
				}
				detailTiers = json.writeValueAsString(tiers);
				log.debug("data tier>>" + detailTiers + "<<size >>>>" + tiers.size());
			} catch (JsonGenerationException e) {
				log.debug("json.serialize");
			} catch (JsonMappingException e) {
				log.debug("json.serialize");
			} catch (IOException e) {
				log.debug("json.serialize");
			}
			// Validate
			validation.required("Charges Category is", master.getChargeCategory().getLookupId());
			validation.required("Charges Code is", master.getChargeCode());
			validation.required("Charges Group is", master.getInvoiceCharge().getLookupId());
			validation.required("Description is", master.getDescription());
			validation.required("Charges Type is", master.getChargeType().getLookupId());
			validation.required("Charges Frequency is", master.getChargeFrequency().getLookupId());
			validation.required("Currency is", master.getCurrency().getCurrencyCode());
			if (master.getTieringType() != null) {
				validation.required("Tiering Type is", master.getTieringType().getLookupId());
			}
			if (!LookupConstants.CHARGE_TYPE_MANUAL.equals(master.getChargeType().getLookupId())) {
				validation.required("Value Type is", master.getValueType().getLookupId());
				if ((!(LookupConstants.CHARGE_VALUE_FIX_AMOUNT.equals(master.getValueType().getLookupId()))) && (!(LookupConstants.CHARGE_TYPE_MANUAL.equals(master.getChargeType().getLookupId()))) && (!(LookupConstants.CHARGE_CALCULATION_PROGRESSIVE.equals(master.getTieringType().getLookupId())))) {
					validation.required("Base calculation is", master.getBaseCalculationField().getLookupId());
				}
				validation.required("Tier Comparison is", master.getTierComparisonField().getLookupId());
			}

			if (!(LookupConstants.CHARGE_TYPE_MANUAL.equals(master.getChargeType().getLookupId()))) {
				validation.required("Minimum Value is", master.getMinimumValue());
			}

			if (master.getIsChecked() == null || (master.getIsChecked() != null && master.getIsChecked() == false)) {
				master.setIsChecked(false);
				validation.required("Maximum Value is", master.getMaximumValue());
			}

			if (LookupConstants.CHARGE_CATEGORY_SAFEKEEPING_FEE.equals(master.getChargeCategory().getLookupId())) {
				validation.required("Method is", master.getMethodSkfee().getLookupId());
				if (master.getYearBase() != null) {
					validation.required("Year Base is", master.getYearBase().getLookupId());
				}
				// if
				// (LookupConstants.METHOD_SKFEE_MAX_HOLDING_BY_PERIOD.equals(master.getMethodSkfee().getLookupId())){
				// logger.debug(">>>>>>>>>>>>>>>");
				// validation.required("Period is", master.getPeriodArray());
				// }
			}
			log.debug("tax code : " + master.getTaxMaster().getTaxCode());
			validation.required("Tax Code is", master.getTaxMaster().getTaxCode());

			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_MASTER));
				render("ChargeMasters/detail.html", master, tiers, mode, detailTiers, status);
			} else {
				log.debug("SIZE PEriod" + master.getPeriods().size());
				if (periods != null) {
					log.debug("period Array" + master.getPeriods());
					Collections.sort(periods);
					master.setPeriods(periods);
					master.setPeriodArray(master.buildPeriodArray());
					log.debug("PeriodArray >>" + master.getPeriodArray());
				}

				if (!(tiers == null)) {
					// sort
					Collections.sort(tiers, new ChargeTierRangeComparator());
					for (int i = 0; i < tiers.size(); i++) {
						CsChargeTier tier = tiers.get(i);
						if (tier != null) {
							if (master.getChargeKey() == null) {
								tier.setId(new CsChargeTierId(0, i + 1));
							} else {
								tier.setId(new CsChargeTierId(master.getChargeKey(), i + 1));
							}
							tier.setTaxMaster(master.getTaxMaster());
							master.getChargeTiers().add(tier);
						}
					}
				}
				Long id = master.getChargeKey();
				serializerService.serialize(session.getId(), master.getChargeKey(), master);
				confirming(id, mode, status);
			}
		} else {
			/*
			 * validation.clear(); flash.error("argument.null", master);
			 */
			entry(null);
		}
	}

	@Check("administration.chargeMaster")
	public static void confirming(Long id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		boolean confirming = true;
		CsChargeMaster master = serializerService.deserialize(session.getId(), id, CsChargeMaster.class);
		// Set<ChargeTier> tiers = master.getChargeTiers();
		log.debug("active confirming >> " + master.getIsActive());
		List<Integer> periods = null;
		if (master.getPeriods() != null) {
			periods = master.getPeriods();
		} else {
			periods = null;
		}
		String detailTiers = null;
		try {
			detailTiers = json.writeValueAsString(master.getChargeTiers());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_MASTER));
		render("ChargeMasters/detail.html", master, detailTiers, confirming, mode, periods, id, status);
	}

	@Check("administration.chargeMaster")
	public static void confirm(CsChargeMaster master, List<CsChargeTier> tiers, List<Integer> periods, String mode, String status) {
		log.debug("confirm. master: " + master + " tiers: " + tiers + " periods: " + periods + " mode: " + mode + " status: " + status);

		if (master == null)
			back(null, mode, null, null, status);
		try {
			master.setOrganization(new GnOrganization(UIConstants.SIMIAN_BANK_ID));
			if (periods != null) {
				log.debug("set Period");
				master.setPeriods(periods);
				master.setPeriodArray(master.buildPeriodArray());
			}
			if (tiers != null) {
				master.setChargeTiers(new HashSet<CsChargeTier>(tiers));
			}
			if (master.getMaximumValue() != null) {
				master.setIsChecked(false);
			} else {
				master.setIsChecked(true);
			}
			for (CsChargeTier tier : master.getChargeTiers()) {
				tier.setTaxMaster(generalService.getTaxMaster(tier.getTaxMaster().getTaxKey()));
				master.getChargeTiers().add(tier);
			}

			master.setChargeCode(master.getChargeCode().toUpperCase());

			generalService.saveChargeMaster(MenuConstants.CS_CHARGE_MASTER, master, session.get(UIConstants.SESSION_USERNAME), "", session.get(UIConstants.SESSION_USER_KEY));
			list();
		} catch (MedallionException e) {
			if (tiers == null) {
				tiers = new ArrayList<CsChargeTier>();
			} else {

				for (int i = 0; i < tiers.size(); i++) {
					CsChargeTier tier = tiers.get(i);
					if (tier != null) {
						if (master.getChargeKey() != null) {
							tier.setId(new CsChargeTierId(master.getChargeKey(), i + 1));
						} else {
							tier.setId(new CsChargeTierId(0, i + 1));
						}
						master.getChargeTiers().add(tier);
					}
				}
			}
			Long id = master.getChargeKey();
			String detailTiers = null;

			try {
				detailTiers = json.writeValueAsString(tiers);
			} catch (JsonGenerationException ex) {
				log.debug("json.serialize");
			} catch (JsonMappingException ex) {
				log.debug("json.serialize");
			} catch (IOException ex) {
				log.debug("json.serialize");
			}
			log.error(e.getMessage(), e);
			validation.clear();
			flash.error("Charges Code : ' " + master.getChargeCode() + " ' " + e.getMessage());
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_MASTER));
			render("ChargeMasters/detail.html", master, detailTiers, periods, mode, confirming, id, status);
		}
	}

	@Check("administration.chargeMaster")
	public static void back(Long id, String mode, CsChargeMaster master, List<CsChargeTier> tiers, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " master: " + master + " tiers: " + tiers + " status: " + status);

		boolean confirming = false;
		master = serializerService.deserialize(session.getId(), id, CsChargeMaster.class);
		// List<Integer> periods = master.getPeriods();
		List<Integer> periods = null;
		if (master.getPeriods() != null) {
			// master.fillPeriods(master.getPeriodArray());
			periods = master.getPeriods();
		} else {
			periods = null;
		}
		// if (periods != null) {
		// master.fillPeriods(master.getPeriodArray());
		// }
		String detailTiers = null;
		try {
			detailTiers = json.writeValueAsString(master.getChargeTiers());
		} catch (JsonGenerationException e) {
			log.debug("json.serialize");
		} catch (JsonMappingException e) {
			log.debug("json.serialize");
		} catch (IOException e) {
			log.debug("json.serialize");
		}
		validation.clear();
		if (master.getChargeKey() == null) {
			mode = UIConstants.DISPLAY_MODE_EDIT;
		}
		Long keyHelp = null;
		if (master.getChargeKey() == null) {
			keyHelp = null;
		} else {
			keyHelp = master.getChargeKey();
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.CS_CHARGE_MASTER));
		renderTemplate("ChargeMasters/detail.html", master, mode, keyHelp, tiers, periods, detailTiers, confirming, id, status);
	}

	// @Check("workflow.approval")
	public static void approval(String taskId, Long keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);
			CsChargeMaster master = json.readValue(maintenanceLog.getNewData(), CsChargeMaster.class);
			List<Integer> periods = null;
			if (master.getPeriods() != null) {
				master.fillPeriods(master.getPeriodArray());
				periods = master.getPeriods();
			} else {
				periods = null;
			}
			String detailTiers = null;
			try {

				detailTiers = json.writeValueAsString(master.getChargeTiers());
			} catch (JsonGenerationException ex) {
				log.debug("json.serialize");
			} catch (JsonMappingException ex) {
				log.debug("json.serialize");
			} catch (IOException ex) {
				log.debug("json.serialize");
			}
			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("ChargeMasters/approval.html", master, mode, periods, detailTiers, taskId, operation, maintenanceLogKey, from);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// @Check("workflow.approval")
	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveChargeMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	// @Check("workflow.approval")
	public static void reject(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("reject. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveChargeMaster(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}

	public static void viewForChargeOverride(Long id, String param) {
		log.debug("viewForChargeOverride. id: " + id + " param: " + param);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		CsChargeMaster master = null;
		String detailTiers = null;
		List<Integer> periods = null;
		if (id != null) {
			master = generalService.getChargeMaster(id);
			periods = master.getPeriods();
			if (master != null) {
				try {
					JsonHelper json = new JsonHelper().getChargeMasterSerializer();
					detailTiers = json.serialize(master.getChargeTiers());
				} catch (JsonGenerationException ex) {
					log.debug("json.serialize");
				} catch (JsonMappingException ex) {
					log.debug("json.serialize");
				} catch (IOException ex) {
					log.debug("json.serialize");
				}
			}
		}

		render("ChargeMasters/formForOverride.html", master, detailTiers, mode, periods);
	}
}