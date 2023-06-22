package controllers;

import helpers.UIConstants;
import helpers.UIHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import play.i18n.Messages;
import play.mvc.Before;

import com.simian.medallion.constant.LookupConstants;
import com.simian.medallion.constant.MenuConstants;
import com.simian.medallion.exception.MedallionException;
import com.simian.medallion.model.GnLookup;
import com.simian.medallion.model.ScTypeMaster;
import com.simian.medallion.model.TdTransactionTemplate;
import com.simian.medallion.vo.SelectItem;

//@Check("administration.depositTemplate")
public class DepositTemplates extends MedallionController {
	private static Logger log = Logger.getLogger(DepositTemplates.class);

	@Before(only = { "view", "edit", "save", "entry", "confirming", "confirm", "back" })
	public static void setup() {
		log.debug("setup. ");

		List<SelectItem> operators = UIHelper.yesNoOperators();
		renderArgs.put("operators", operators);

		List<SelectItem> categories = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._TRANSACTION_CATEGORY);
		renderArgs.put("categories", categories);

		GnLookup paymentFreqMonthly = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MONTHLY);
		GnLookup paymentFreqMaturity = generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY);

		List<SelectItem> usedBy = generalService.listLookupsForDropDownAsSelectItem(UIConstants.SIMIAN_BANK_ID, LookupConstants._USED_BY);
		renderArgs.put("usedBy", usedBy);
		renderArgs.put("noNewData", true);
		renderArgs.put("paymentFreqMonthlyId", paymentFreqMonthly.getLookupId());
		renderArgs.put("paymentFreqMonthlyDesc", paymentFreqMonthly.getLookupDescription());
		renderArgs.put("paymentFreqMaturityId", paymentFreqMaturity.getLookupId());
		renderArgs.put("paymentFreqMaturityDesc", paymentFreqMaturity.getLookupDescription());
	}

	@Check("administration.depositTemplate")
	public static void list() {
		log.debug("list. ");

		List<ScTypeMaster> securityTypes = securityService.listSecurityTypesDepositTemplate();
		for (ScTypeMaster scM : securityTypes) {
			List<TdTransactionTemplate> tmpl = depositoService.getLstTransactionTemplate(scM.getSecurityType());
			scM.setIsActive(tmpl.get(0).getActive());
		}
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
		render(securityTypes);
	}

	@Check("administration.depositTemplate")
	public static void view(String id) {
		log.debug("view. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_VIEW;
		ScTypeMaster secType = securityService.getSecurityType(id);
		// List<TdTransactionTemplate> lstTemplate =
		// accountService.listTdTransactionTemplate(secType.getSecurityType());
		List<TdTransactionTemplate> lstTemplate = new ArrayList<TdTransactionTemplate>();
		List<GnLookup> lstLookupTemplateTrans = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITO_TEMPLATE);

		//int i = 0;
		for (GnLookup lookupTemplate : lstLookupTemplateTrans) {
			TdTransactionTemplate tmpl = depositoService.getTdTransactionTemplate(lookupTemplate.getLookupId(), secType.getSecurityType());
			if (tmpl == null) {
				tmpl = new TdTransactionTemplate();

				tmpl.setDepositoTemplateKey(null);
				tmpl.setSecurityType(secType);
				tmpl.setDepositoTemplate(lookupTemplate);
				tmpl.setDepositoTemplateCode(lookupTemplate.getLookupDescription());
				tmpl.setActive(true);
				tmpl.setInterestFrequency(generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY));
			}
			//i++;
			//
			lstTemplate.add(tmpl);
		}

		secType.setIsActive(lstTemplate.get(0).getActive());
		secType.setInterestFrequency(lstTemplate.get(0).getInterestFrequency());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
		secType.setDepositoTrxTemplates(new ArrayList<TdTransactionTemplate>());
		secType.getDepositoTrxTemplates().addAll(lstTemplate);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
		render("DepositTemplates/detail.html", secType, mode);
	}

	@Check("administration.depositTemplate")
	public static void edit(String id) {
		log.debug("edit. id: " + id);

		String mode = UIConstants.DISPLAY_MODE_EDIT;
		ScTypeMaster secType = securityService.getSecurityType(id);
		// List<TdTransactionTemplate> lstTemplate =
		// accountService.listTdTransactionTemplate(secType.getSecurityType());
		List<TdTransactionTemplate> lstTemplate = new ArrayList<TdTransactionTemplate>();
		List<GnLookup> lstLookupTemplateTrans = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITO_TEMPLATE);

		int i = 0;
		boolean isRecordEmpty = true;
		for (GnLookup lookupTemplate : lstLookupTemplateTrans) {
			TdTransactionTemplate tmpl = depositoService.getTdTransactionTemplate(lookupTemplate.getLookupId(), secType.getSecurityType());
			if (tmpl == null) {
				tmpl = new TdTransactionTemplate();

				tmpl.setDepositoTemplateKey(null);
				tmpl.setSecurityType(secType);
				tmpl.setDepositoTemplateCode(lookupTemplate.getLookupDescription());
				tmpl.setActive(true);
				tmpl.setInterestFrequency(generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY));
			} else {
				isRecordEmpty = false;
			}
			tmpl.setDepositoTemplate(lookupTemplate);
			tmpl.setRowNumber(i);
			i++;
			lstTemplate.add(tmpl);
		}

		if (isRecordEmpty)
			mode = UIConstants.DISPLAY_MODE_ENTRY;

		secType.setIsActive(lstTemplate.get(0).getActive());
		secType.setInterestFrequency(lstTemplate.get(0).getInterestFrequency());
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
		secType.setDepositoTrxTemplates(new ArrayList<TdTransactionTemplate>());
		secType.getDepositoTrxTemplates().addAll(lstTemplate);
		render("DepositTemplates/detail.html", secType, mode);
	}

	@Check("administration.depositTemplate")
	public static void save(String mode, ScTypeMaster secType, String status) {
		log.debug("save. mode: " + mode + " secType: " + secType + " status: " + status);

		if (secType != null) {
			for (int i = 0; i < secType.getDepositoTrxTemplates().size(); i++) {
				validation.required("Security Type is", secType.getSecurityType());
				validation.required("Deposit code " + (i + 1) + " is", secType.getDepositoTrxTemplates().get(i).getDepositoTemplateCode());
				validation.required("Transaction template " + (i + 1) + " is", secType.getDepositoTrxTemplates().get(i).getTransactionTemplate().getTransactionCode());

			}
			if (validation.hasErrors()) {
				flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
				render("DepositTemplates/detail.html", secType, mode, status);
			} else {
				String id = secType.getSecurityType();

				serializerService.serialize(session.getId(), id, secType);
				confirming(id, mode, status);
			}
		} else {
			list();
		}
	}

	@Check("administration.depositTemplate")
	public static void entry() {
		log.debug("entry. ");

		String mode = UIConstants.DISPLAY_MODE_ENTRY;
		ScTypeMaster secType = new ScTypeMaster();
		List<TdTransactionTemplate> lstTemplate = new ArrayList<TdTransactionTemplate>();
		List<GnLookup> lstLookupTemplateTrans = generalService.listLookups(UIConstants.SIMIAN_BANK_ID, LookupConstants._DEPOSITO_TEMPLATE);

		int i = 0;
		for (GnLookup lookupTemplate : lstLookupTemplateTrans) {
			TdTransactionTemplate tmpl = new TdTransactionTemplate();

			tmpl.setDepositoTemplateKey(null);
			tmpl.setSecurityType(secType);
			tmpl.setDepositoTemplate(lookupTemplate);
			tmpl.setDepositoTemplateCode(lookupTemplate.getLookupDescription());
			tmpl.setActive(true);
			tmpl.setInterestFrequency(generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY));
			tmpl.setRowNumber(i);
			i++;
			lstTemplate.add(tmpl);
		}

		secType.setInterestFrequency(generalService.getLookup(LookupConstants.INTEREST_FREQUENCY_MATURITY));
		secType.setDepositoTrxTemplates(new ArrayList<TdTransactionTemplate>());
		secType.getDepositoTrxTemplates().addAll(lstTemplate);
		secType.setIsActive(true);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
		render("DepositTemplates/detail.html", secType, mode);
	}

	@Check("administration.depositTemplate")
	public static void confirming(String id, String mode, String status) {
		log.debug("confirming. id: " + id + " mode: " + mode + " status: " + status);

		boolean confirming = true;
		ScTypeMaster secType = serializerService.deserialize(session.getId(), id, ScTypeMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
		render("DepositTemplates/detail.html", secType, mode, confirming, status);

	}

	@Check("administration.depositTemplate")
	public static void confirm(String mode, ScTypeMaster secType, String status) {
		log.debug("confirm. mode: " + mode + " secType: " + secType + " status: " + status);

		try {
			if (secType == null)
				back(null, mode, status);

			for (int i = 0; i < secType.getDepositoTrxTemplates().size(); i++) {
				secType.getDepositoTrxTemplates().get(i).setActive(secType.getIsActive());
				secType.getDepositoTrxTemplates().get(i).setInterestFrequency(secType.getInterestFrequency());
				secType.getDepositoTrxTemplates().get(i).setSecurityType(secType);

				if (LookupConstants.DEPOSITO_TEMPLATE_PLACEMENT.equals(secType.getDepositoTrxTemplates().get(i).getDepositoTemplate().getLookupId())) {
					secType.getDepositoTrxTemplates().get(i).setDeposityType(null);
					secType.getDepositoTrxTemplates().get(i).setRollover(false);
				}

				if (LookupConstants.DEPOSITO_TEMPLATE_FULL_REDEEM.equals(secType.getDepositoTrxTemplates().get(i).getDepositoTemplate().getLookupId())) {
					secType.getDepositoTrxTemplates().get(i).setDeposityType(new GnLookup(LookupConstants.DEPOSITO_TYPE_FULL_REDEEM));
					secType.getDepositoTrxTemplates().get(i).setRollover(true);
				}

				if (LookupConstants.DEPOSITO_TEMPLATE_ROLLOVER_PRINCIPAL.equals(secType.getDepositoTrxTemplates().get(i).getDepositoTemplate().getLookupId())) {
					secType.getDepositoTrxTemplates().get(i).setDeposityType(new GnLookup(LookupConstants.DEPOSITO_TYPE_FULL_ARO));
					secType.getDepositoTrxTemplates().get(i).setRollover(true);
				}

				if (LookupConstants.DEPOSITO_TEMPLATE_ROLLOVER_PRINCIPAL_INTREST.equals(secType.getDepositoTrxTemplates().get(i).getDepositoTemplate().getLookupId())) {
					secType.getDepositoTrxTemplates().get(i).setDeposityType(new GnLookup(LookupConstants.DEPOSITO_TYPE_FULL_ARO_PLUS));
					secType.getDepositoTrxTemplates().get(i).setRollover(true);
				}

				if (LookupConstants.DEPOSITO_TEMPLATE_BREAK.equals(secType.getDepositoTrxTemplates().get(i).getDepositoTemplate().getLookupId())) {
					secType.getDepositoTrxTemplates().get(i).setDeposityType(new GnLookup(LookupConstants.DEPOSITO_TYPE_FULL_REDEEM));
					secType.getDepositoTrxTemplates().get(i).setRollover(false);
				}

				if (LookupConstants.DEPOSITO_TEMPLATE_INTEREST_PAYMENT.equals(secType.getDepositoTrxTemplates().get(i).getDepositoTemplate().getLookupId())) {
					secType.getDepositoTrxTemplates().get(i).setDeposityType(new GnLookup(LookupConstants.DEPOSITO_TYPE_FULL_REDEEM));
					secType.getDepositoTrxTemplates().get(i).setRollover(false);
				}
			}
			depositoService.saveTransactionTemplate(secType.getDepositoTrxTemplates());

			list();
			// =============================
		} catch (MedallionException e) {
			flash.error("Transaction Code : ' " + secType.getSecurityType() + " ' " + Messages.get(e.getMessage()));
			boolean confirming = true;
			flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
			render("DepositTemplates/detail.html", secType, mode, confirming, status);
		}
	}

	@Check("administration.depositTemplate")
	public static void back(String id, String mode, String status) {
		log.debug("back. id: " + id + " mode: " + mode + " status: " + status);

		ScTypeMaster secType = serializerService.deserialize(session.getId(), id, ScTypeMaster.class);
		flash.put(UIConstants.BREADCRUMB, applicationService.getMenuBreadcrumb(MenuConstants.TD_DEPOSITO_TEMPLATES));
		render("DepositTemplates/detail.html", secType, mode, status);
	}

}
