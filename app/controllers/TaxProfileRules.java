package controllers;

import helpers.Formatter;
import helpers.UIConstants;
import helpers.UIHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.With;

import com.simian.medallion.constant.ExceptionConstants;
import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.constant.WorkflowConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.helper.json.JsonHelper;
import com.simian.medallion.model.GnMaintenanceLog;
import com.simian.medallion.model.TxProfRuleInt;
import com.simian.medallion.model.TxProfRuleIntDetail;
import com.simian.medallion.model.TxProfRuleIntId;
import com.simian.medallion.model.TxProfile;
import com.simian.medallion.model.TxProfileRule;
import com.simian.medallion.model.TxProfileRuleId;
import com.simian.medallion.vo.SelectItem;
import com.simian.medallion.vo.TxProfRuleIntDetailGridItem;

@With(Secure.class)
public class TaxProfileRules extends MedallionController {
	private static Logger log = Logger.getLogger(TaxProfileRules.class);

	@Before(unless = { "list", "save", "confirm" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);
	}

	@Check("administration.taxProfiles")
	public static void list(String taxProfileCode) {
		log.debug("list. taxProfileCode: " + taxProfileCode);

		TxProfile taxProfile = generalService.getTaxProfile(taxProfileCode);
		List<TxProfileRule> taxProfileRules = generalService.listProfilRules(taxProfileCode);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
		render(taxProfileRules, taxProfile, taxProfileCode);
	}

	@Check("administration.taxProfiles")
	public static void view(String taxProfileCode, String securityType) {
		log.debug("view. taxProfileCode: " + taxProfileCode + " securityType: " + securityType);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		TxProfileRule txProfileRule = null;
		String txProfRuleGrid = null;
		String status = null;
		if (taxProfileCode != null) {
			txProfileRule = generalService.getTxProfileRule(taxProfileCode, securityType);

			status = txProfileRule.getRecordStatus();
			txProfRuleGrid = extractTxProfRuleIntDetailGridItemSerializedFromTxProfileRule(txProfileRule, txProfRuleGrid);
		}
		renderArgs.put("taxProfileCode", taxProfileCode);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
		render("TaxProfileRules/detail.html", txProfileRule, mode, txProfRuleGrid, status);

	}

	@Check("administration.taxProfiles")
	public static void entry(String taxProfileCode) {
		log.debug("entry. taxProfileCode: " + taxProfileCode);

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		TxProfileRule txProfileRule = new TxProfileRule();
		TxProfile taxProfile = generalService.getTaxProfile(taxProfileCode);
		txProfileRule.setTxProfile(taxProfile);
		String txProfRuleGrid = null;
		TxProfRuleIntDetailGridItem interestGridItem = new TxProfRuleIntDetailGridItem();
		boolean isNewRec = true;
		try {
			txProfRuleGrid = json.writeValueAsString(interestGridItem);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
		render("TaxProfileRules/detail.html", mode, txProfileRule, txProfRuleGrid, isNewRec);
	}

	@Check("administration.taxProfiles")
	public static void edit(String taxProfileCode, String securityType) {
		log.debug("edit. taxProfileCode: " + taxProfileCode + " securityType: " + securityType);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		TxProfileRule txProfileRule = null;
		String txProfRuleGrid = null;
		String status = null;
		boolean isNewRec = false;
		if (taxProfileCode != null) {
			txProfileRule = generalService.getTxProfileRule(taxProfileCode, securityType);
			status = txProfileRule.getRecordStatus();
			txProfRuleGrid = extractTxProfRuleIntDetailGridItemSerializedFromTxProfileRule(txProfileRule, txProfRuleGrid);
		}
		renderArgs.put("oldSecurityType", txProfileRule.getSecurityType().getSecurityType());

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
		render("TaxProfileRules/detail.html", txProfileRule, mode, txProfRuleGrid, status, isNewRec);
	}

	private static String extractTxProfRuleIntDetailGridItemSerializedFromSerializer(TxProfileRule txProfileRule, String txProfRuleGrid) {
		log.debug("extractTxProfRuleIntDetailGridItemSerializedFromSerializer. txProfileRule: " + txProfileRule + " txProfRuleGrid: " + txProfRuleGrid);

		List<TxProfRuleIntDetailGridItem> lstInterestGridItem = new ArrayList<TxProfRuleIntDetailGridItem>();

		if (txProfileRule != null) {
			if ((txProfileRule.getLstTxProfRuleIntDetailGridItem() != null) && (txProfileRule.getLstTxProfRuleIntDetailGridItem().size() > 0)) {
				lstInterestGridItem.clear();
				lstInterestGridItem.addAll(txProfileRule.getLstTxProfRuleIntDetailGridItem());
			}

			// create json serialized TxProfRuleIntDetailGridItem
			try {
				txProfRuleGrid = json.writeValueAsString(lstInterestGridItem);

			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return txProfRuleGrid;
	}

	private static String extractTxProfRuleIntDetailGridItemSerializedFromTxProfileRule(TxProfileRule txProfileRule, String txProfRuleGrid) {
		log.debug("extractTxProfRuleIntDetailGridItemSerializedFromTxProfileRule. txProfileRule: " + txProfileRule + " txProfRuleGrid: " + txProfRuleGrid);

		List<TxProfRuleIntDetailGridItem> lstInterestGridItem = new ArrayList<TxProfRuleIntDetailGridItem>();
		if (txProfileRule != null) {
			if ((txProfileRule.getTxProfRuleInts() != null) && (txProfileRule.getTxProfRuleInts().size() > 0)) {
				for (TxProfRuleInt txProfRuleInt : txProfileRule.getTxProfRuleInts()) {
					if ((txProfRuleInt.getTxProfRuleIntDetails() != null) && (txProfRuleInt.getTxProfRuleIntDetails().size() > 0)) {
						TxProfRuleIntDetailGridItem tmpTxProfRuleIntDetailGridItem = new TxProfRuleIntDetailGridItem();
						tmpTxProfRuleIntDetailGridItem.setEffectiveDateFrom(new SimpleDateFormat(appProp.getDateFormat()).format(txProfRuleInt.getEffectiveDateFrom()));
						tmpTxProfRuleIntDetailGridItem.setEffectiveDateTo(new SimpleDateFormat(appProp.getDateFormat()).format(txProfRuleInt.getEffectiveDateTo()));

						// convert List of TxProfRuleIntDetail to
						// TxProfRuleIntDetailGridItem
						for (TxProfRuleIntDetail txProfRuleIntDetail : txProfRuleInt.getTxProfRuleIntDetails()) {
							if (txProfRuleIntDetail.getTaxObject() != null) {
								if (txProfRuleIntDetail.getTaxObject().getLookupId().trim().equals(LookupConstants.TAX_OBJECT_ACCRUED_INTEREST)) {
									tmpTxProfRuleIntDetailGridItem.setTaxObjectAccruedInterest(txProfRuleIntDetail);
								}

								if (txProfRuleIntDetail.getTaxObject().getLookupId().trim().equals(LookupConstants.TAX_OBJECT_CAPITAL_GAIN)) {
									tmpTxProfRuleIntDetailGridItem.setTaxObjectCapitalGain(txProfRuleIntDetail);
								}

								if (txProfRuleIntDetail.getTaxObject().getLookupId().trim().equals(LookupConstants.TAX_OBJECT_DISCOUNT)) {
									tmpTxProfRuleIntDetailGridItem.setTaxObjectDiscount(txProfRuleIntDetail);
								}

								if (txProfRuleIntDetail.getTaxObject().getLookupId().trim().equals(LookupConstants.TAX_OBJECT_DIVIDEND)) {
									tmpTxProfRuleIntDetailGridItem.setTaxObjectDividend(txProfRuleIntDetail);
								}
							}
						}
						lstInterestGridItem.add(tmpTxProfRuleIntDetailGridItem);
					}
				}
			}

			// create json serialized TxProfRuleIntDetailGridItem
			try {
				JsonHelper jsonRuleIntDetailGrid = new JsonHelper().getTxProfileRuleIntDetailGridSerializer();
				txProfRuleGrid = jsonRuleIntDetailGrid.serialize(lstInterestGridItem);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return txProfRuleGrid;
	}

	@Check("administration.taxProfiles")
	// public static void save(String body, String mode, String id, String
	// status, boolean isNewRec) {
	public static void save(TxProfileRule txProfileRule, String mode, String id, String status, String oldSecurityType, boolean isNewRec, TxProfRuleIntDetailGridItem[] profRuleGrid) {
		log.debug("save. txProfileRule: " + txProfileRule + " mode: " + mode + " id: " + id + " status: " + status + " oldSecurityType: " + oldSecurityType + " isNewRec: " + isNewRec + " profRuleGrid: " + profRuleGrid);

		if (txProfileRule != null) {
			List<TxProfRuleIntDetailGridItem> lstProfRuleIntDetailGrid = new ArrayList<TxProfRuleIntDetailGridItem>();
			if ((profRuleGrid != null) && (profRuleGrid.length > 0)) {
				for (TxProfRuleIntDetailGridItem txProfRuleIntDetailGridItem : profRuleGrid) {
					lstProfRuleIntDetailGrid.add(txProfRuleIntDetailGridItem);
				}
			}

			if (validation.errorsMap().values().containsAll(lstProfRuleIntDetailGrid) == false) {
				validation.clear();
			}

			validation.required("Security Type is", txProfileRule.getSecurityType().getSecurityType());
			if ((profRuleGrid == null) || (profRuleGrid.length == 0)) {
				validation.required("Tax Profile Rule Detail is", profRuleGrid);
			}

			String taxCode = txProfileRule.getTxProfile().getTaxProfileCode();
			String secType = txProfileRule.getSecurityType().getSecurityType();

			if (txProfileRule.getActive() == null) {
				txProfileRule.setActive(false);
			}

			txProfileRule.setId(new TxProfileRuleId(taxCode, secType));

			if ((profRuleGrid != null) && (profRuleGrid.length > 0)) {
				List<TxProfRuleIntDetailGridItem> aTxProfRuleIntDetail = new ArrayList<TxProfRuleIntDetailGridItem>();

				for (TxProfRuleIntDetailGridItem txProfRuleIntDetailGridItem : profRuleGrid) {
					aTxProfRuleIntDetail.add(txProfRuleIntDetailGridItem);
				}

				txProfileRule.getLstTxProfRuleIntDetailGridItem().clear();
				txProfileRule.getLstTxProfRuleIntDetailGridItem().addAll(aTxProfRuleIntDetail);
			}

			if (validation.hasErrors()) {
				String txProfRuleGrid = null;
				try {
					if ((profRuleGrid == null) || (profRuleGrid.length == 0)) {
						profRuleGrid = new TxProfRuleIntDetailGridItem[] {};
					}

					txProfRuleGrid = json.writeValueAsString(profRuleGrid);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
				render("TaxProfileRules/detail.html", txProfileRule, mode, txProfRuleGrid, oldSecurityType, status, isNewRec);
			} else {
				id = taxCode + secType;
				String data = serializerService.serialize(session.getId(), id, txProfileRule);
				log.debug("DATA SERIALIZE = " + data);
				confirming(id, mode, status, oldSecurityType, isNewRec);
			}

		} else {
			flash.error("argument.null", txProfileRule);
		}

	}

	@Check("administration.taxProfiles")
	public static void confirming(String id, String mode, String status, String oldSecurityType, boolean isNewRec) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status + " oldSecurityType: " + oldSecurityType + " isNewRec: " + isNewRec);

		renderArgs.put("confirming", true);
		TxProfileRule txProfileRule = serializerService.deserialize(session.getId(), id, TxProfileRule.class);

		String taxCode = txProfileRule.getTxProfile().getTaxProfileCode();
		String secType = txProfileRule.getSecurityType().getSecurityType();
		id = taxCode + secType;
		renderArgs.put("id", id);

		String txProfRuleGrid = null;
		txProfRuleGrid = extractTxProfRuleIntDetailGridItemSerializedFromSerializer(txProfileRule, txProfRuleGrid);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
		render("TaxProfileRules/detail.html", mode, txProfileRule, status, oldSecurityType, txProfRuleGrid, isNewRec);
	}

	@Check("administration.taxProfiles")
	public static void confirm(TxProfileRule txProfileRule, String mode, String id, String status, String oldSecurityType, boolean isNewRec, TxProfRuleIntDetailGridItem[] profRuleGrid) {
		log.debug("confirm. txProfileRule: " + txProfileRule + " mode: " + mode + " id: " + id + " status: " + status + " oldSecurityType: " + oldSecurityType + " isNewRec: " + isNewRec + " profRuleGrid: " + profRuleGrid);

		// txProfileRule = serializerService.deserialize(session.getId(), id,
		// TxProfileRule.class);

		/*
		 * if(txProfileRule.getTxProfRuleInts() != null){ for(TxProfRuleInt
		 * txProfRuleInt : txProfileRule.getTxProfRuleInts()){
		 * if(txProfRuleInt.getTxProfRuleIntDetails() != null){
		 * 
		 * txProfRuleInt.setTxProfRuleIntDetails(new
		 * HashSet<TxProfRuleIntDetail>
		 * (txProfRuleInt.getTxProfRuleIntDetails())); for (TxProfRuleIntDetail
		 * detail : txProfRuleInt.getTxProfRuleIntDetails()) {
		 * logger.debug("TAX MASTER KEY ON CONFIRM = "
		 * +detail.getTaxMaster().getTaxKey()); } } }
		 * txProfileRule.setTxProfRuleInts(new
		 * HashSet<TxProfRuleInt>(txProfileRule.getTxProfRuleInts())); }
		 */
		validation.clear();

		String taxCode = txProfileRule.getTxProfile().getTaxProfileCode();
		String secType = txProfileRule.getSecurityType().getSecurityType();

		String tieringType = "";
		Date effDateFrom = new Date();
		Date effDateTo = new Date();
		String txProfRuleGrid = null;

		if ((profRuleGrid != null) && (profRuleGrid.length > 0)) {
			List<TxProfRuleIntDetailGridItem> aTxProfRuleIntDetail = new ArrayList<TxProfRuleIntDetailGridItem>();

			for (TxProfRuleIntDetailGridItem txProfRuleIntDetailGridItem : profRuleGrid) {
				aTxProfRuleIntDetail.add(txProfRuleIntDetailGridItem);
			}

			txProfileRule.getTxProfRuleInts().clear();

			for (TxProfRuleIntDetailGridItem txProfRuleIntDetailGridItem : aTxProfRuleIntDetail) {
				TxProfRuleInt txProfRuleInt = new TxProfRuleInt();
				tieringType = LookupConstants.CHARGE_CALCULATION_SINGLE_VALUE;
				try {
					effDateFrom = new SimpleDateFormat(appProp.getDateFormat()).parse(txProfRuleIntDetailGridItem.getEffectiveDateFrom());
					effDateTo = new SimpleDateFormat(appProp.getDateFormat()).parse(txProfRuleIntDetailGridItem.getEffectiveDateTo());
				} catch (ParseException e) {
					log.error(e.getMessage(), e);
				}
				txProfRuleInt.setId(new TxProfRuleIntId(taxCode, secType, tieringType, effDateFrom, effDateTo));
				txProfRuleInt.setTxProfileRule(txProfileRule);
				txProfRuleInt.setTieringType(generalService.getLookup(tieringType));
				txProfRuleInt.setEffectiveDateFrom(effDateFrom);
				txProfRuleInt.setEffectiveDateTo(effDateTo);

				txProfRuleInt.getTxProfRuleIntDetails().clear();

				if ((txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest() != null) && (txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getTaxMaster() != null) && (txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getTaxMaster().getTaxKey() != null)) {
					String taxLookupId = LookupConstants.TAX_OBJECT_ACCRUED_INTEREST;
					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getId().setSecurityTypeCode(secType);
					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getId().setTaxKey(txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getTaxMaster().getTaxKey());
					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getId().setTieringTypeCode(tieringType);
					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getId().setEffectiveDateFromCode(effDateFrom);
					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getId().setEffectiveDateToCode(effDateTo);
					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getId().setTaxObjectLookupId(taxLookupId);

					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().setTxProfRuleInt(txProfRuleInt);

					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().setRate(txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().getTaxMaster().getTaxRate());

					txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest().setTaxObject(generalService.getLookup(taxLookupId));

					txProfRuleInt.getTxProfRuleIntDetails().add(txProfRuleIntDetailGridItem.getTaxObjectAccruedInterest());
				}

				if ((txProfRuleIntDetailGridItem.getTaxObjectCapitalGain() != null) && (txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getTaxMaster() != null) && (txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getTaxMaster().getTaxKey() != null)) {
					String taxLookupId = LookupConstants.TAX_OBJECT_CAPITAL_GAIN;
					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getId().setSecurityTypeCode(secType);
					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getId().setTaxKey(txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getTaxMaster().getTaxKey());
					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getId().setTieringTypeCode(tieringType);
					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getId().setEffectiveDateFromCode(effDateFrom);
					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getId().setEffectiveDateToCode(effDateTo);
					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getId().setTaxObjectLookupId(taxLookupId);

					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().setTxProfRuleInt(txProfRuleInt);

					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().setRate(txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().getTaxMaster().getTaxRate());

					txProfRuleIntDetailGridItem.getTaxObjectCapitalGain().setTaxObject(generalService.getLookup(taxLookupId));

					txProfRuleInt.getTxProfRuleIntDetails().add(txProfRuleIntDetailGridItem.getTaxObjectCapitalGain());
				}

				if ((txProfRuleIntDetailGridItem.getTaxObjectDiscount() != null) && (txProfRuleIntDetailGridItem.getTaxObjectDiscount().getTaxMaster() != null) && (txProfRuleIntDetailGridItem.getTaxObjectDiscount().getTaxMaster().getTaxKey() != null)) {
					String taxLookupId = LookupConstants.TAX_OBJECT_DISCOUNT;
					txProfRuleIntDetailGridItem.getTaxObjectDiscount().getId().setSecurityTypeCode(secType);
					txProfRuleIntDetailGridItem.getTaxObjectDiscount().getId().setTaxKey(txProfRuleIntDetailGridItem.getTaxObjectDiscount().getTaxMaster().getTaxKey());
					txProfRuleIntDetailGridItem.getTaxObjectDiscount().getId().setTieringTypeCode(tieringType);
					txProfRuleIntDetailGridItem.getTaxObjectDiscount().getId().setEffectiveDateFromCode(effDateFrom);
					txProfRuleIntDetailGridItem.getTaxObjectDiscount().getId().setEffectiveDateToCode(effDateTo);
					txProfRuleIntDetailGridItem.getTaxObjectDiscount().getId().setTaxObjectLookupId(taxLookupId);

					txProfRuleIntDetailGridItem.getTaxObjectDiscount().setTxProfRuleInt(txProfRuleInt);

					txProfRuleIntDetailGridItem.getTaxObjectDiscount().setRate(txProfRuleIntDetailGridItem.getTaxObjectDiscount().getTaxMaster().getTaxRate());

					txProfRuleIntDetailGridItem.getTaxObjectDiscount().setTaxObject(generalService.getLookup(taxLookupId));

					txProfRuleInt.getTxProfRuleIntDetails().add(txProfRuleIntDetailGridItem.getTaxObjectDiscount());
				}

				if ((txProfRuleIntDetailGridItem.getTaxObjectDividend() != null) && (txProfRuleIntDetailGridItem.getTaxObjectDividend().getTaxMaster() != null) && (txProfRuleIntDetailGridItem.getTaxObjectDividend().getTaxMaster().getTaxKey() != null)) {
					String taxLookupId = LookupConstants.TAX_OBJECT_DIVIDEND;
					txProfRuleIntDetailGridItem.getTaxObjectDividend().getId().setSecurityTypeCode(secType);
					txProfRuleIntDetailGridItem.getTaxObjectDividend().getId().setTaxKey(txProfRuleIntDetailGridItem.getTaxObjectDividend().getTaxMaster().getTaxKey());
					txProfRuleIntDetailGridItem.getTaxObjectDividend().getId().setTieringTypeCode(tieringType);
					txProfRuleIntDetailGridItem.getTaxObjectDividend().getId().setEffectiveDateFromCode(effDateFrom);
					txProfRuleIntDetailGridItem.getTaxObjectDividend().getId().setEffectiveDateToCode(effDateTo);
					txProfRuleIntDetailGridItem.getTaxObjectDividend().getId().setTaxObjectLookupId(taxLookupId);

					txProfRuleIntDetailGridItem.getTaxObjectDividend().setTxProfRuleInt(txProfRuleInt);

					txProfRuleIntDetailGridItem.getTaxObjectDividend().setRate(txProfRuleIntDetailGridItem.getTaxObjectDividend().getTaxMaster().getTaxRate());

					txProfRuleIntDetailGridItem.getTaxObjectDividend().setTaxObject(generalService.getLookup(taxLookupId));

					txProfRuleInt.getTxProfRuleIntDetails().add(txProfRuleIntDetailGridItem.getTaxObjectDividend());
				}

				txProfileRule.getTxProfRuleInts().add(txProfRuleInt);
			}

			txProfileRule.setOldSecurityType(oldSecurityType);

			try {
				JsonHelper jsonRuleIntDetailGrid = new JsonHelper().getTxProfileRuleIntDetailGridSerializer();
				txProfRuleGrid = jsonRuleIntDetailGrid.serialize(aTxProfRuleIntDetail);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}
		txProfileRule.getLstTxProfRuleIntDetailGridItem().clear();

		/*
		 * if(txProfileRule.getTxProfile().getTaxProfileCode() == null){
		 * txProfileRule.setTxProfile(null); }
		 * 
		 * if((txProfileRule.getId().getTaxProfileCode() != null &&
		 * !txProfileRule.getId().getTaxProfileCode().equals("")) &&
		 * (txProfileRule.getId().getSecurityTypeCode() != null &&
		 * !txProfileRule.getId().getSecurityTypeCode().equals(""))){ mode =
		 * UIConstants.DISPLAY_MODE_EDIT; isNewRec = false; }else{ mode =
		 * UIConstants.DISPLAY_MODE_ENTRY; isNewRec = true; }
		 */

		List<TxProfileRule> txProfileRules = generalService.listAllProfilRules();

		for (TxProfileRule txProfileRuleIntTable : txProfileRules) {

			if (!(UIConstants.DISPLAY_MODE_EDIT.equals(mode))) {

				if (txProfileRuleIntTable.getTxProfile().getTaxProfileCode().equals(txProfileRule.getTxProfile().getTaxProfileCode()) && txProfileRuleIntTable.getSecurityType().getSecurityType().equals(txProfileRule.getSecurityType().getSecurityType())) {

					flash.error("Security Type : '" + txProfileRuleIntTable.getSecurityType().getSecurityType() + "' " + Messages.get(ExceptionConstants.DATA_DUPLICATE));
					/*
					 * String error = "error"; renderText(error);
					 */
					flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
					render("TaxProfileRules/detail.html", mode, txProfileRule, status, oldSecurityType, txProfRuleGrid, isNewRec);
				}
			}
		}

		try {
			generalService.saveTxProfileRule(MenuConstants.TX_TAX_SETUP, txProfileRule, session.get(UIConstants.SESSION_USERNAME), "", isNewRec, session.get(UIConstants.SESSION_USER_KEY));
			list(taxCode);
		} catch (MedallionException me) {
			flash.error("Profil Code : '" + txProfileRule.getTxProfile().getTaxProfileCode() + "' " + Messages.get(me.getMessage()));
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
			/*
			 * String error = "error"; renderText(error);
			 */
			render("TaxProfileRules/detail.html", mode, txProfileRule, status, oldSecurityType, txProfRuleGrid, isNewRec);
		}

	}

	@Check("administration.taxProfiles")
	public static void back(String id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		TxProfileRule txProfileRule = serializerService.deserialize(session.getId(), id, TxProfileRule.class);

		if ((txProfileRule.getRecordStatus() != null) && (txProfileRule.getRecordStatus().trim().length() > 0)) {
			status = txProfileRule.getRecordStatus();
		}

		String txProfRuleGrid = null;

		txProfRuleGrid = extractTxProfRuleIntDetailGridItemSerializedFromSerializer(txProfileRule, txProfRuleGrid);

		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TX_TAX_SETUP));
		render("TaxProfileRules/detail.html", txProfRuleGrid, txProfileRule, mode, id, status);

	}

	public static void approval(String taskId, String keyId, String operation, Long maintenanceLogKey, String from) {
		log.debug("approval. taskId: " + taskId + " keyId: " + keyId + " operation: " + operation + " maintenanceLogKey: " + maintenanceLogKey + " from: " + from);

		try {
			String mode = UIConstants.DISPLAY_MODE_VIEW;
			String txProfRuleGrid = null;

			GnMaintenanceLog maintenanceLog = maintenanceService.getMaintenanceLog(maintenanceLogKey);

			TxProfileRule txProfileRule = json.readValue(maintenanceLog.getNewData(), TxProfileRule.class);

			String status = null;

			status = txProfileRule.getRecordStatus();
			txProfRuleGrid = extractTxProfRuleIntDetailGridItemSerializedFromTxProfileRule(txProfileRule, txProfRuleGrid);

			if (from.equals(UIConstants.WORKFLOW_FROM)) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_LIST_BATCH));
			} else {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.WF_SINGLE));
			}
			render("TaxProfileRules/approval.html", txProfileRule, txProfRuleGrid, mode, taskId, operation, maintenanceLogKey, from, status);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void approve(String taskId, Long maintenanceLogKey, String operation) {
		log.debug("approve. taskId: " + taskId + " maintenanceLogKey: " + maintenanceLogKey + " operation: " + operation);

		try {
			generalService.approveTxProfileRule(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_APPROVE);

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
			generalService.approveTxProfileRule(session.get(UIConstants.SESSION_USERNAME), taskId, operation, maintenanceLogKey, WorkflowConstants.PROCESS_TYPE_REJECT);

			renderJSON(Formatter.resultSuccess());
		} catch (MedallionException e) {
			renderJSON(Formatter.resultError(e));
		} catch (Exception e) {
			renderJSON(Formatter.resultError(e));
		}
	}
}